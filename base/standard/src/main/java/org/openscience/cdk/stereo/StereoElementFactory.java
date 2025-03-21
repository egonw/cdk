/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.interfaces.IBond.Stereo.DOWN;
import static org.openscience.cdk.interfaces.IBond.Stereo.DOWN_INVERTED;
import static org.openscience.cdk.interfaces.IBond.Stereo.UP;
import static org.openscience.cdk.interfaces.IBond.Stereo.UP_INVERTED;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Create stereo elements for a structure with 2D and 3D coordinates. The
 * factory does not verify whether atoms can or cannot support stereochemistry -
 * for this functionality use {@link Stereocenters}. The factory will not create
 * stereo elements if there is missing information (wedge/hatch bonds, undefined
 * coordinates) or the layout indicates unspecified configuration.
 *
 * Stereocenters specified with inverse down (hatch) bond style are created if
 * the configuration is unambiguous and the bond does not connect to another
 * stereocenter.
 *
 * <blockquote><pre>
 * IAtomContainer       container = ...;
 * StereoElementFactory stereo    = StereoElementFactory.using2DCoordinates()
 *                                                      .interpretProjections(Projection.Haworth);
 *
 * // set the elements replacing any existing elements
 * container.setStereoElements(stereo.createAll());
 *
 * // adding elements individually is also possible but existing elements are
 * // are not removed
 * for (IStereoElement element : stereo.createAll())
 *     container.addStereoElement(element); // bad, there may already be elements
 *
 * </pre></blockquote>
 *
 * @author John May
 * @see Stereocenters
 */
public abstract class StereoElementFactory {

    /** Native CDK structure representation. */
    protected final IAtomContainer container;

    /** Adjacency list graph representation. */
    protected final int[][] graph;

    /** A bond map for fast access to bond labels between two atom indices. */
    protected final EdgeToBondMap bondMap;


    protected final Set<Projection> projections = EnumSet.noneOf(Projection.class);

    protected boolean strict;

    protected final ILoggingTool logger
            = LoggingToolFactory.createLoggingTool(StereoElementFactory.class);

    /**
     * Verify if created stereochemistry are actually stereo-centres.
     */
    protected boolean check = false;

    /**
     * Internal constructor.
     *
     * @param container an atom container
     * @param graph     adjacency list representation
     * @param bondMap   lookup bonds by atom index
     */
    protected StereoElementFactory(IAtomContainer container, int[][] graph, EdgeToBondMap bondMap) {
        this.container = container;
        this.graph = graph;
        this.bondMap = bondMap;
    }

    private boolean visitSmallRing(int[] mark, int aidx, int prev, int depth,
                                   int max) {
        if (mark[aidx] == 2)
            return true;
        if (depth == max)
            return false;
        if (mark[aidx] == 1)
            return false;
        mark[aidx] = 1;
        for (int nbr : graph[aidx]) {
            if (nbr != prev && visitSmallRing(mark, nbr, aidx, depth + 1, max))
                return true;
        }
        mark[aidx] = 0;
        return false;
    }

    private boolean isInSmallRing(IBond bond, int max) {
        if (!bond.isInRing())
            return false;
        IAtom beg  = bond.getBegin();
        IAtom end  = bond.getEnd();
        int[] mark = new int[container.getAtomCount()];
        int   bidx = container.indexOf(beg);
        int   eidx = container.indexOf(end);
        mark[bidx] = 2;
        return visitSmallRing(mark,
                              eidx,
                              bidx,
                              1,
                              max);
    }

    private boolean isInSmallRing(IAtom atom, int max) {
        if (!atom.isInRing())
            return false;
        for (IBond bond : container.getConnectedBondsList(atom)) {
            if (isInSmallRing(bond, max))
                return true;
        }
        return false;
    }

    private IBond getOtherDb(IAtom a, IBond other) {
        IBond result = null;
        for (IBond bond : container.getConnectedBondsList(a)) {
            if (bond.equals(other))
                continue;
            if (bond.getOrder() != IBond.Order.DOUBLE)
                continue;
            if (result != null)
                return null;
            result = bond;
        }
        return result;
    }

    private static IAtom getShared(IBond a, IBond b) {
        if (b.contains(a.getBegin()))
            return a.getBegin();
        if (b.contains(a.getEnd()))
            return a.getEnd();
        return null;
    }

    private List<IBond> getCumulatedDbs(IBond endBond) {
        List<IBond> dbs = new ArrayList<>();
        dbs.add(endBond);
        IBond other = getOtherDb(endBond.getBegin(), endBond);
        if (other == null)
            other = getOtherDb(endBond.getEnd(), endBond);
        if (other == null)
            return null;
        while (other != null) {
            dbs.add(other);
            IAtom a = getShared(dbs.get(dbs.size() - 1), dbs.get(dbs.size() - 2));
            other = getOtherDb(other.getOther(a), other);
        }
        return dbs;
    }

    /**
     * Creates all stereo elements found by {@link Stereocenters} using the or
     * 2D/3D coordinates to specify the configuration (clockwise/anticlockwise).
     * Currently only {@link ITetrahedralChirality} and {@link
     * IDoubleBondStereochemistry} elements are created..
     *
     * @return a list of stereo elements
     */
    public List<IStereoElement> createAll() {

        Cycles.markRingAtomsAndBonds(container);
        Stereocenters centers = new Stereocenters(container, graph, bondMap);
        if (check) {
            centers.checkSymmetry();
        }

        List<IStereoElement> elements = new ArrayList<>();

        // projection recognition (note no action in constructors)
        FischerRecognition fischerRecon = new FischerRecognition(container, graph, bondMap, centers);
        CyclicCarbohydrateRecognition cycleRecon = new CyclicCarbohydrateRecognition(container, graph, bondMap, centers);

        elements.addAll(fischerRecon.recognise(projections));
        elements.addAll(cycleRecon.recognise(projections));

        for (int v = 0; v < graph.length; v++) {
            switch (centers.elementType(v)) {
                // elongated tetrahedrals
                case Bicoordinate:
                    for (int w : graph[v]) {
                        // end of an extended tetrahedral or cis/trans
                        if (centers.elementType(w) == Stereocenters.Type.Tricoordinate) {
                            List<IBond> dbs = getCumulatedDbs(container.getBond(container.getAtom(w),
                                                                                container.getAtom(v)));
                            if (dbs == null)
                                continue;
                            if (container.indexOf(dbs.get(0)) > container.indexOf(dbs.get(dbs.size() - 1)))
                                continue;
                            if ((dbs.size() & 0x1) == 0) {
                                IAtom focus = getShared(dbs.get(dbs.size() / 2),
                                                        dbs.get((dbs.size() / 2)-1));
                                // extended tetrahedral
                                IStereoElement element = createExtendedTetrahedral(container.indexOf(focus),
                                                                                   centers);
                                if (element != null) elements.add(element);
                            } else {
                                // extended cis-trans
                                IStereoElement element = createExtendedCisTrans(dbs,
                                                                                centers);
                                if (element != null) elements.add(element);
                            }
                            break;
                        }
                    }
                    break;
                // tetrahedrals
                case Tetracoordinate:
                    IStereoElement element = createTetrahedral(v, centers);
                    if (element != null) elements.add(element);
                    break;
                // aryl-aryl atropisomers
                case Tricoordinate:
                    for (int w : graph[v]) {
                        IBond bond = bondMap.get(v, w);
                        if (w > v &&
                            centers.elementType(w) == Stereocenters.Type.Tricoordinate &&
                            bond.getOrder() == IBond.Order.SINGLE &&
                            !isInSmallRing(bond, 6) &&
                            isInSmallRing(bond.getBegin(), 6) &&
                            isInSmallRing(bond.getEnd(), 6)) {
                            element = createAtropisomer(v, w, centers);
                            if (element != null)
                                elements.add(element);
                            break;
                        }
                    }
                    break;
            }
        }

        // always need to verify for db...
        centers.checkSymmetry();
        for (int v = 0; v < graph.length; v++) {
            switch (centers.elementType(v)) {
                // Cis/Trans double bonds
                case Tricoordinate:
                    if (!centers.isStereocenter(v))
                        continue;
                    for (int w : graph[v]) {
                        IBond bond = bondMap.get(v, w);
                        if (w > v && bond.getOrder() == IBond.Order.DOUBLE) {
                            if (centers.elementType(w) == Stereocenters.Type.Tricoordinate
                                && centers.isStereocenter(w) && !isInSmallRing(bond, 7)) {
                                IStereoElement element = createGeometric(v, w, centers);
                                if (element != null) elements.add(element);
                            }
                            break;
                        }
                    }
                    break;
            }
        }

        return elements;
    }

    /**
     * Create a tetrahedral element for the atom at index {@code v}. If a
     * tetrahedral element could not be created then null is returned. An
     * element can not be created if, one or more atoms was missing coordinates,
     * the atom has an unspecified (wavy) bond, the atom is no non-planar bonds
     * (i.e. up/down, wedge/hatch). The method does not check if tetrahedral
     * chirality is supported - for this functionality use {@link
     * Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (int v = 0; v < container.getAtomCount(); v++) {
     *     // ... verify v is a stereo atom ...
     *     ITetrahedralChirality element = factory.createTetrahedral(v);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param v atom index (vertex)
     * @return a new stereo element
     */
    abstract ITetrahedralChirality createTetrahedral(int v, Stereocenters stereocenters);

    /**
     * Create axial atropisomers.
     *
     * @param v first atom of single bond
     * @param w other atom of single bond
     * @param stereocenters stereo centres
     * @return new stereo element
     */
    abstract IStereoElement createAtropisomer(int v, int w, Stereocenters stereocenters);

    /**
     * Create a tetrahedral element for the atom. If a tetrahedral element could
     * not be created then null is returned. An element can not be created if,
     * one or more atoms was missing coordinates, the atom has an unspecified
     * (wavy) bond, the atom is no non-planar bonds (i.e. up/down, wedge/hatch).
     * The method does not check if tetrahedral chirality is supported - for
     * this functionality use {@link Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (IAtom atom : container.atoms()) {
     *     // ... verify atom is a stereo atom ...
     *     ITetrahedralChirality element = factory.createTetrahedral(atom);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param atom atom
     * @return a new stereo element
     */
    abstract ITetrahedralChirality createTetrahedral(IAtom atom, Stereocenters stereocenters);

    /**
     * Create a geometric element (double-bond stereochemistry) for the provided
     * atom indices. If the configuration could not be created a null element is
     * returned. There is no configuration is the coordinates do not indicate a
     * configuration, there were undefined coordinates or an unspecified bond
     * label. The method does not check if double bond stereo is supported - for
     * this functionality use {@link Stereocenters}.
     *
     * @param u an atom index
     * @param v an atom pi bonded 'v'
     * @return a new stereo element
     */
    abstract IDoubleBondStereochemistry createGeometric(int u, int v, Stereocenters stereocenters);

    /**
     * Create a geometric element (double-bond stereochemistry) for the provided
     * double bond. If the configuration could not be created a null element is
     * returned. There is no configuration is the coordinates do not indicate a
     * configuration, there were undefined coordinates or an unspecified bond
     * label. The method does not check if double bond stereo is supported - for
     * this functionality use {@link Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (IBond bond : container.bonds()) {
     *     if (bond.getOrder() != DOUBLE)
     *         continue;
     *     // ... verify bond is a stereo bond...
     *     IDoubleBondStereochemistry element = factory.createGeometric(bond);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param bond the bond to create a configuration for
     * @return a new stereo element
     */
    abstract IDoubleBondStereochemistry createGeometric(IBond bond, Stereocenters stereocenters);

    /**
     * Create an extended tetrahedral element for the atom at index {@code v}.
     * If an extended  tetrahedral element could not be created then null is
     * returned. An element can not be created if, one or more atoms was
     * missing coordinates, the atom has an unspecified (wavy) bond, the atom
     * is no non-planar bonds (i.e. up/down, wedge/hatch). The method does not
     * check if tetrahedral chirality is supported - for this functionality
     * use {@link * Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (int v = 0; v < container.getAtomCount(); v++) {
     *     // ... verify v is a stereo atom ...
     *     ExtendedTetrahedral element = factory.createExtendedTetrahedral(v);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param v atom index (vertex)
     * @return a new stereo element
     */
    abstract ExtendedTetrahedral createExtendedTetrahedral(int v, Stereocenters stereocenters);

    /**
     * Create an extended cis/trans bond (cumulated) given one end (see diagram
     * below). The stereo element geometry will only be created if there is an
     * odd number of cumulated double bonds. The double bond list ('bonds')
     * should be ordered consecutively from one end to the other.
     *
     * <pre>
     *  C               C
     *   \             /
     *    C = C = C = C
     *      ^   ^   ^
     *      ^---^---^----- bonds
     * </pre>
     *
     * @param bonds cumulated double bonds
     * @param centers discovered stereocentres
     * @return the extended cis/trans geometry if one could be created
     */
    abstract ExtendedCisTrans createExtendedCisTrans(List<IBond> bonds,
                                                     Stereocenters centers);

    /**
     * Indicate that stereochemistry drawn as a certain projection should be
     * interpreted.
     *
     * <pre>{@code
     * StereoElementFactory factory =
     *   StereoElementFactory.using2DCoordinates(container)
     *                       .interpretProjections(Projection.Fischer, Projection.Haworth);
     * }</pre>
     *
     * @param projections types of projection
     * @return self
     * @see org.openscience.cdk.stereo.Projection
     */
    public StereoElementFactory interpretProjections(Projection ... projections) {
        Collections.addAll(this.projections, projections);
        this.check = true;
        return this;
    }

    public StereoElementFactory checkSymmetry(boolean check) {
        this.check = check;
        return this;
    }

    /**
     * Enables stricter stereochemistry checking, specifically tetrahedral
     * centres may not be created from inverse down wedges (i.e. Daylight
     * style depictions). This also sets that all stereocentres are tested
     * for asymmetry.
     *
     * @return stereo element factory.
     */
    public StereoElementFactory withStrictMode()
    {
        this.check = true;
        this.strict = true;
        return this;
    }

    /**
     * Create a stereo element factory for creating stereo elements using 2D
     * coordinates and depiction labels (up/down, wedge/hatch).
     *
     * @param container the structure to create the factory for
     * @return the factory instance
     */
    public static StereoElementFactory using2DCoordinates(IAtomContainer container) {
        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(container);
        int[][] graph = GraphUtil.toAdjList(container, bondMap);
        return new StereoElementFactory2D(container, graph, bondMap);
    }

    /**
     * Create a stereo element factory for creating stereo elements using 3D
     * coordinates and depiction labels (up/down, wedge/hatch).
     *
     * @param container the structure to create the factory for
     * @return the factory instance
     */
    public static StereoElementFactory using3DCoordinates(IAtomContainer container) {
        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(container);
        int[][] graph = GraphUtil.toAdjList(container, bondMap);
        return new StereoElementFactory3D(container, graph, bondMap).checkSymmetry(true);
    }

    private static boolean hasUnspecifiedParity(IAtom atom) {
        return atom.getStereoParity() != null && atom.getStereoParity() == 3;
    }

    /** Create stereo-elements from 2D coordinates. */
    static final class StereoElementFactory2D extends StereoElementFactory {

        /**
         * Threshold at which the determinant is considered too small (unspecified
         * by coordinates).
         */
        private static final double THRESHOLD = 0.1;

        /**
         * Create a new stereo-element factory for the specified structure.
         *
         * @param container native CDK structure representation
         * @param graph     adjacency list representation
         * @param bondMap   fast bond lookup from atom indices
         */
        StereoElementFactory2D(IAtomContainer container, int[][] graph, EdgeToBondMap bondMap) {
            super(container, graph, bondMap);
        }

        /**{@inheritDoc} */
        @Override
        ITetrahedralChirality createTetrahedral(IAtom atom, Stereocenters stereocenters) {
            return createTetrahedral(container.indexOf(atom), stereocenters);
        }

        /**{@inheritDoc} */
        @Override
        IDoubleBondStereochemistry createGeometric(IBond bond, Stereocenters stereocenters) {
            return createGeometric(container.indexOf(bond.getBegin()), container.indexOf(bond.getEnd()),
                                   stereocenters);
        }

        /**{@inheritDoc} */
        @Override
        ITetrahedralChirality createTetrahedral(int v, Stereocenters stereocenters) {

            IAtom focus = container.getAtom(v);

            if (hasUnspecifiedParity(focus)) return null;

            if (check) {
                if (!stereocenters.isStereocenter(v))
                    return null;
            }

            IAtom[] neighbors = new IAtom[4];
            int[] elevation = new int[4];

            neighbors[3] = focus; // implicit neighbour if needed

            boolean nonplanar = false;
            int n = 0;

            List<IBond> bonds = new ArrayList<>();
            for (int w : graph[v]) {
                IBond bond = bondMap.get(v, w);
                bonds.add(bond);

                // wavy bond
                if (isUnspecified(bond)) return null;

                neighbors[n] = container.getAtom(w);
                elevation[n] = elevationOf(focus, bond);

                if (elevation[n] != 0)
                    nonplanar = true;

                n++;
            }

            // too few neighbors
            if (n < 3) return null;

            if (strict) {
                if (!verifyWedgePattern(focus, n, bonds))
                    return null;
            }

            // no up/down bonds present - check for inverted down/hatch
            if (!nonplanar && !strict) {
                int[] ws = graph[v];
                for (int i = 0; i < ws.length; i++) {
                    int w = ws[i];
                    IBond bond = bondMap.get(v, w);

                    // we have already previously checked whether 'v' is at the
                    // 'point' and so these must be inverse (fat-end of hatched
                    // wedge is a stereocenter) ala Daylight
                    if (bond.getStereo() == DOWN || bond.getStereo() == DOWN_INVERTED) {

                        // we stick to the 'point' end convention but can
                        // interpret if the bond isn't connected to another
                        // stereocenter - otherwise it's ambiguous!
                        if (stereocenters.stereocenterType(w) != Stereocenters.Stereocenter.Non) {
                            stereocenters.checkSymmetry();
                            if (stereocenters.isStereocenter(w)) {
                                logger.error("Ambiguous down wedge bond between atom indexes ", w, " and ", v);
                                return null;
                            }
                        }

                        logger.warn("Inverse wedge bond used for stereo at atom index ", v);
                        elevation[i] = -1;
                        nonplanar = true;
                    }
                    // stereo at the "fat-end" of the bold wedge?
                    else if (bond.getStereo() == UP || bond.getStereo() == UP_INVERTED) {
                        logger.warn("Ignoring inverted up wedge bond connected to atom idx=", w);
                        return null;
                    }
                }
            }

            // still no bonds to use
            if (!nonplanar) return null;

            int parity = parity(focus, neighbors, elevation);

            if (parity == 0) return null;

            Stereo winding = parity > 0 ? Stereo.ANTI_CLOCKWISE : Stereo.CLOCKWISE;

            return new TetrahedralChirality(focus, neighbors, winding);
        }

        private boolean isOkay(int a, int b) {
            return a == 0 || b == 0 || a == b;
        }

        // check some obvious stereo chemistry errors, see the InChI
        // technical manual "Definition of 2D drawing correctness"
        private boolean verifyWedgePattern(IAtom focus, int n, List<IBond> bonds) {
            bonds.sort(GeometryUtil.polarBondComparator(focus));
            if (n == 3) {
                double angle = getMaxSweep(focus, bonds);
                double delta = angle - Math.PI;
                double threshold = 0.01;
                // largest angle between 2 neighbours is > 180 => wedges should
                // alternate
                if (delta > threshold) {
                    int ref = 0;
                    for (IBond bond : bonds) {
                        int curr = elevationOf(focus, bond);
                        if (!isOkay(ref, curr)) {
                            logger.error("Invalid wedge pattern, up/down bonds should be mixed when there is an acute angle!");
                            return false;
                        } else
                            ref = curr;
                        ref = -ref;
                    }
                }
                // larges angle between 2 neighbours is < 180 => all wedges
                // should be the same
                else if (delta < -threshold) {
                    int ref = 0;
                    for (IBond bond : bonds) {
                        int curr = elevationOf(focus, bond);
                        if (!isOkay(ref, curr)) {
                            logger.error("Invalid wedge pattern, up/down bonds should be same when there is not an acute angle!");
                            return false;
                        } else
                            ref = curr;
                    }
                } else {
                    // 180-degrees, check where the wedge is
                    if (bonds.size() != 3)
                        throw new IllegalArgumentException("3 bonds only!");
                    Vector2d v1 = toUnitVector(focus, bonds.get(0).getOther(focus));
                    Vector2d v2 = toUnitVector(focus, bonds.get(1).getOther(focus));
                    Vector2d v3 = toUnitVector(focus, bonds.get(2).getOther(focus));
                    String ambiuousStereoMesg = "Ambiguous stereochemistry - 3 neighbours and two bonds are co-linear";
                    if (Math.abs(signedAngle(v1,v2) - Math.PI) < threshold) {
                        if (elevationOf(focus, bonds.get(0)) == 0 &&
                            elevationOf(focus, bonds.get(1)) == 0 &&
                            elevationOf(focus, bonds.get(2)) != 0) {
                            logger.error(ambiuousStereoMesg);
                            return false;
                        }
                    } else if (Math.abs(signedAngle(v2,v3) - Math.PI) < threshold) {
                        if (elevationOf(focus, bonds.get(0)) != 0 &&
                            elevationOf(focus, bonds.get(1)) == 0 &&
                            elevationOf(focus, bonds.get(2)) == 0) {
                            logger.error(ambiuousStereoMesg);
                            return false;
                        }
                    } else if (Math.abs(signedAngle(v3,v1) - Math.PI) < threshold) {
                        if (elevationOf(focus, bonds.get(0)) == 0 &&
                            elevationOf(focus, bonds.get(1)) != 0 &&
                            elevationOf(focus, bonds.get(2)) == 0) {
                            logger.error(ambiuousStereoMesg);
                            return false;
                        }
                    }
                }
            } else { // n == 4
                int ref = 0;
                for (IBond bond : bonds) {
                    int curr = elevationOf(focus, bond);
                    if (curr != 0) {
                        if (ref != 0 && ref != curr) {
                            logger.error("Badly drawn stereochemistry with 4 neighbours, up/down bonds should alternate!");
                            return false;
                        }
                        else {
                            ref = curr;
                        }
                    }
                    ref = -ref; // flip for next check
                }
            }
            return true;
        }

        private double signedAngle(Vector2d a, Vector2d b) {
            double angle = Math.atan2(a.x*b.y-a.y*b.x, a.x*b.x+a.y*b.y);
            return angle >= 0 ? (2*Math.PI)-angle : -angle;
        }

        private double max(double a, double b, double c) {
            return Math.max(a, Math.max(b, c));
        }

        private double getMaxSweep(IAtom atom, List<IBond> bonds) {
            if (bonds.size() != 3)
                throw new IllegalArgumentException("3 bonds only!");
            Vector2d v1 = toUnitVector(atom, bonds.get(0).getOther(atom));
            Vector2d v2 = toUnitVector(atom, bonds.get(1).getOther(atom));
            Vector2d v3 = toUnitVector(atom, bonds.get(2).getOther(atom));
            return max(signedAngle(v1, v2),
                       signedAngle(v2, v3),
                       signedAngle(v3, v1));
        }

        private static boolean isWedged(IBond bond) {
            switch (bond.getStereo()) {
                case UP:
                case DOWN:
                case UP_INVERTED:
                case DOWN_INVERTED:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        IStereoElement createAtropisomer(int u, int v,
                                         Stereocenters stereocenters) {
            IAtom end1 = container.getAtom(u);
            IAtom end2 = container.getAtom(v);

            if (hasUnspecifiedParity(end1) || hasUnspecifiedParity(end2))
                return null;

            if (graph[u].length != 3 || graph[v].length != 3)
                return null;

            // check degrees of connected atoms, we only create the
            // atropisomer if the rings are 3x ortho substituted
            // CC1=CC=CC(C)=C1-C1=C(C)C=CC=C1C yes (sum1=9,sum2=9)
            // CC1=CC=CC=C1-C1=C(C)C=CC=C1C yes    (sum1=8,sum2=9)
            // CC1=CC=CC(C)=C1-C1=CC=CC=C1 no      (sum1=7,sum2=9)
            // CC1=CC=CC=C1-C1=C(C)C=CC=C1 no      (sum1=8,sum2=8)
            int sum1 = graph[graph[u][0]].length +
                    graph[graph[u][1]].length +
                    graph[graph[u][2]].length;
            int sum2 = graph[graph[v][0]].length +
                       graph[graph[v][1]].length +
                       graph[graph[v][2]].length;
            if (sum1 > 9 || sum1 < 8)
                return null;
            if (sum2 > 9 || sum2 < 8)
                return null;
            if (sum1 + sum2 < 17)
                return null;

            IAtom[] carriers = new IAtom[4];
            int[]   elevation = new int[4];

            int n = 0;
            for (int w : graph[u]) {
                IBond bond = bondMap.get(u, w);
                if (w == v) continue;
                if (isUnspecified(bond)) return null;

                carriers[n] = container.getAtom(w);
                elevation[n] = elevationOf(end1, bond);

                for (int w2 : graph[w]) {
                    if (isHydrogen(container.getAtom(w2)))
                        sum1--;
                    else if (elevation[n] == 0 &&
                             isWedged(bondMap.get(w, w2))) {
                        elevation[n] = elevationOf(container.getAtom(w), bondMap.get(w, w2));
                    }
                }


                n++;
            }
            n = 2;
            for (int w : graph[v]) {
                IBond bond = bondMap.get(v, w);
                if (w == u) continue;
                if (isUnspecified(bond)) return null;

                carriers[n] = container.getAtom(w);
                elevation[n] = elevationOf(end2, bond);

                for (int w2 : graph[w]) {
                    if (isHydrogen(container.getAtom(w2)))
                        sum2--;
                    else if (elevation[n] == 0 &&
                             isWedged(bondMap.get(w, w2))) {
                        elevation[n] = elevationOf(container.getAtom(w), bondMap.get(w, w2));
                    }
                }

                n++;
            }

            if (n != 4)
                return null;

            // recheck now we have accounted for explicit hydrogens
            if (sum1 > 9 || sum1 < 8)
                return null;
            if (sum2 > 9 || sum2 < 8)
                return null;
            if (sum1 + sum2 < 17)
                return null;

            if (elevation[0] != 0 || elevation[1] != 0) {
                if (elevation[2] != 0 || elevation[3] != 0) return null;
            } else {
                if (elevation[2] == 0 && elevation[3] == 0) return null; // undefined configuration
            }

            IAtom tmp = end1.getBuilder().newAtom();
            tmp.setPoint2d(new Point2d((end1.getPoint2d().x + end2.getPoint2d().x)/2,
                                       (end2.getPoint2d().y + end2.getPoint2d().y)/2));
            int parity = parity(tmp, carriers, elevation);
            int cfg    = parity > 0 ? IStereoElement.LEFT : IStereoElement.RIGHT;

            return new Atropisomeric(container.getBond(end1, end2), carriers, cfg);
        }

        /**{@inheritDoc} */
        @Override
        IDoubleBondStereochemistry createGeometric(int u, int v, Stereocenters stereocenters) {

            if (hasUnspecifiedParity(container.getAtom(u)) ||
                hasUnspecifiedParity(container.getAtom(v))) return null;

            int[] us = graph[u];
            int[] vs = graph[v];

            if (us.length < 2 || us.length > 3 || vs.length < 2 || vs.length > 3) return null;

            // move pi bonded neighbors to back
            moveToBack(us, v);
            moveToBack(vs, u);

            IAtom[] vAtoms = new IAtom[]{container.getAtom(us[0]),
                                         container.getAtom(us.length > 2 ? us[1] : u),
                                         container.getAtom(v)};
            IAtom[] wAtoms = new IAtom[]{container.getAtom(vs[0]),
                                         container.getAtom(vs.length > 2 ? vs[1] : v),
                                         container.getAtom(u)};

            // are any substituents a wavy unspecified bond
            if (isUnspecified(bondMap.get(u, us[0])) || isUnspecified(bondMap.get(u, us[1]))
                    || isUnspecified(bondMap.get(v, vs[0])) || isUnspecified(bondMap.get(v, vs[1]))) return null;

            int parity = parity(vAtoms) * parity(wAtoms);
            Conformation conformation = parity > 0 ? Conformation.OPPOSITE : Conformation.TOGETHER;

            if (parity == 0) return null;

            IBond bond = bondMap.get(u, v);

            // crossed bond
            if (isUnspecified(bond)) return null;

            // put the bond in to v is the first neighbor
            bond.setAtoms(new IAtom[]{container.getAtom(u), container.getAtom(v)});

            return new DoubleBondStereochemistry(bond, new IBond[]{bondMap.get(u, us[0]), bondMap.get(v, vs[0])},
                    conformation);
        }

        /**{@inheritDoc} */
        @Override
        ExtendedTetrahedral createExtendedTetrahedral(int v, Stereocenters stereocenters) {

            IAtom focus = container.getAtom(v);

            if (hasUnspecifiedParity(focus)) return null;

            IAtom[] terminals = ExtendedTetrahedral.findTerminalAtoms(container, focus);

            int t0 = container.indexOf(terminals[0]);
            int t1 = container.indexOf(terminals[1]);

            if (stereocenters.isSymmetryChecked() &&
                (!stereocenters.isStereocenter(t0) ||
                 !stereocenters.isStereocenter(t1)))
                return null;

            IAtom[] neighbors = new IAtom[4];
            int[] elevation = new int[4];

            neighbors[1] = terminals[0];
            neighbors[3] = terminals[1];

            int n = 0;
            for (int w : graph[t0]) {
                IBond bond = bondMap.get(t0, w);
                if (w == v) continue;
                if (bond.getOrder() != IBond.Order.SINGLE) continue;
                if (isUnspecified(bond)) return null;
                if (n == 2) return null;
                neighbors[n] = container.getAtom(w);
                elevation[n] = elevationOf(terminals[0], bond);
                n++;
            }
            if (n == 0)
                return null;
            n = 2;
            for (int w : graph[t1]) {
                IBond bond = bondMap.get(t1, w);
                if (bond.getOrder() != IBond.Order.SINGLE) continue;
                if (isUnspecified(bond)) return null;
                if (n == 4) return null;
                neighbors[n] = container.getAtom(w);
                elevation[n] = elevationOf(terminals[1], bond);
                n++;
            }
            if (n == 2)
                return null;
            if (elevation[0] != 0 || elevation[1] != 0) {
                if (elevation[2] != 0 || elevation[3] != 0) return null;
            } else {
                if (elevation[2] == 0 && elevation[3] == 0) return null; // undefined configuration
            }

            int parity = parity(focus, neighbors, elevation);

            Stereo winding = parity > 0 ? Stereo.ANTI_CLOCKWISE : Stereo.CLOCKWISE;

            return new ExtendedTetrahedral(focus, neighbors, winding);
        }

        ExtendedCisTrans createExtendedCisTrans(List<IBond> dbs, Stereocenters stereocenters) {

            // only applies to odd-counts
            if ((dbs.size() & 0x1) == 0)
                return null;

            IBond   focus    = dbs.get(dbs.size()/2);
            IBond[] carriers = new IBond[2];
            int     config;

            IAtom begAtom = dbs.get(0).getOther(getShared(dbs.get(0), dbs.get(1)));
            IAtom endAtom = dbs.get(dbs.size()-1).getOther(getShared(dbs.get(dbs.size()-1), dbs.get(dbs.size()-2)));

            List<IBond> begBonds = container.getConnectedBondsList(begAtom);
            List<IBond> endBonds = container.getConnectedBondsList(endAtom);

            if (stereocenters.isSymmetryChecked() &&
                (!stereocenters.isStereocenter(container.indexOf(begAtom)) ||
                 !stereocenters.isStereocenter(container.indexOf(endAtom))))
                return null;

            if (begBonds.size() < 2 || endBonds.size() < 2)
                return null;

            begBonds.remove(dbs.get(0));
            endBonds.remove(dbs.get(dbs.size() - 1));

            IAtom[] ends = ExtendedCisTrans.findTerminalAtoms(container, focus);
            assert ends != null;
            if (ends[0].equals(begAtom)) {
                carriers[0] = begBonds.get(0);
                carriers[1] = endBonds.get(0);
            } else {
                carriers[1] = begBonds.get(0);
                carriers[0] = endBonds.get(0);
            }

            IAtom begNbr = begBonds.get(0).getOther(begAtom);
            IAtom endNbr = endBonds.get(0).getOther(endAtom);

            Vector2d begVec = new Vector2d(begNbr.getPoint2d().x - begAtom.getPoint2d().x,
                                           begNbr.getPoint2d().y - begAtom.getPoint2d().y);
            Vector2d endVec = new Vector2d(endNbr.getPoint2d().x - endAtom.getPoint2d().x,
                                           endNbr.getPoint2d().y - endAtom.getPoint2d().y);

            begVec.normalize();
            endVec.normalize();

            double dot = begVec.dot(endVec);
            if (dot < 0)
                config = IStereoElement.OPPOSITE;
            else
                config = IStereoElement.TOGETHER;

            return new ExtendedCisTrans(focus, carriers, config);
        }

        /**
         * Is the provided bond have an unspecified stereo label.
         *
         * @param bond a bond
         * @return the bond has unspecified stereochemistry
         */
        private boolean isUnspecified(IBond bond) {
            switch (bond.getStereo()) {
                case UP_OR_DOWN:
                case UP_OR_DOWN_INVERTED:
                case E_OR_Z:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Parity computation for one side of a double bond in a geometric center.
         *
         * @param atoms atoms around the double bonded atom, 0: substituent, 1:
         *              other substituent (or focus), 2: double bonded atom
         * @return the parity of the atoms
         */
        private int parity(IAtom[] atoms) {

            if (atoms.length != 3) throw new IllegalArgumentException("incorrect number of atoms");

            Point2d a = atoms[0].getPoint2d();
            Point2d b = atoms[1].getPoint2d();
            Point2d c = atoms[2].getPoint2d();

            if (a == null || b == null || c == null) return 0;

            double det = det(a.x, a.y, b.x, b.y, c.x, c.y);

            // unspecified by coordinates
            if (Math.abs(det) < THRESHOLD) return 0;

            return (int) Math.signum(det);
        }

        /**
         * Parity computation for 2D tetrahedral stereocenters.
         *
         * @param atoms      the atoms surrounding the central focus atom
         * @param elevations the elevations of each atom
         * @return the parity (winding)
         */
        private int parity(IAtom focus, IAtom[] atoms, int[] elevations) {

            if (atoms.length != 4) throw new IllegalArgumentException("incorrect number of atoms");

            Point2d[] coordinates = new Point2d[atoms.length];
            for (int i = 0; i < atoms.length; i++) {
                coordinates[i] = atoms[i].getPoint2d();
                if (coordinates[i] == null) return 0;
                coordinates[i] = toUnitVector(focus.getPoint2d(), atoms[i].getPoint2d());
            }

            double det = parity(coordinates, elevations);

            return (int) Math.signum(det);
        }

        /**
         * Obtain the unit vector between two points.
         *
         * @param from the base of the vector
         * @param to   the point of the vector
         * @return the unit vector
         */
        private Point2d toUnitVector(Point2d from, Point2d to) {
            if (from.equals(to))
                return new Point2d(0, 0);
            Vector2d v2d = new Vector2d(to.x - from.x, to.y - from.y);
            v2d.normalize();
            return new Point2d(v2d);
        }

        private Vector2d toUnitVector(IAtom from, IAtom to) {
            if (from.equals(to))
                return new Vector2d(0, 0);
            return new Vector2d(toUnitVector(from.getPoint2d(), to.getPoint2d()));
        }

        /**
         * Compute the signed volume of the tetrahedron from the planar points
         * and elevations.
         *
         * @param coordinates locations in the plane
         * @param elevations  elevations above/below the plane
         * @return the determinant (signed volume of tetrahedron)
         */
        private double parity(final Point2d[] coordinates, final int[] elevations) {
            double x1 = coordinates[0].x;
            double x2 = coordinates[1].x;
            double x3 = coordinates[2].x;
            double x4 = coordinates[3].x;

            double y1 = coordinates[0].y;
            double y2 = coordinates[1].y;
            double y3 = coordinates[2].y;
            double y4 = coordinates[3].y;

            return (elevations[0] * det(x2, y2, x3, y3, x4, y4)) - (elevations[1] * det(x1, y1, x3, y3, x4, y4))
                    + (elevations[2] * det(x1, y1, x2, y2, x4, y4)) - (elevations[3] * det(x1, y1, x2, y2, x3, y3));
        }

        /** 3x3 determinant helper for a constant third column */
        private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
            return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
        }

        /**
         * Utility find the specified value, {@code v}, in the array of values,
         * {@code vs} and moves it to the back.
         *
         * @param vs an array of values (containing v)
         * @param v  a value
         */
        private static void moveToBack(int[] vs, int v) {
            for (int i = 0; i < vs.length; i++) {
                if (vs[i] == v) {
                    System.arraycopy(vs, i + 1, vs, i + 1 - 1, vs.length - (i + 1));
                    vs[vs.length - 1] = v;
                    return;
                }
            }
        }

        /**
         * Obtain the elevation of an atom connected to the {@code focus} by the
         * specified {@code bond}.
         *
         * @param focus a focus of stereochemistry
         * @param bond  a bond connecting the focus to a substituent
         * @return the elevation of the connected atom, +1 above, -1 below, 0
         *         planar
         */
        private int elevationOf(IAtom focus, IBond bond) {
            switch (bond.getStereo()) {
                case UP:
                    return bond.getBegin().equals(focus) ? +1 : 0;
                case UP_INVERTED:
                    return bond.getEnd().equals(focus) ? +1 : 0;
                case DOWN:
                    return bond.getBegin().equals(focus) ? -1 : 0;
                case DOWN_INVERTED:
                    return bond.getEnd().equals(focus) ? -1 : 0;
            }
            return 0;
        }
    }

    /** Create stereo-elements from 3D coordinates. */
    private static final class StereoElementFactory3D extends StereoElementFactory {

        /**
         * Create a new stereo-element factory for the specified structure.
         *
         * @param container native CDK structure representation
         * @param graph     adjacency list representation
         * @param bondMap   fast bond lookup from atom indices
         */
        StereoElementFactory3D(IAtomContainer container, int[][] graph, EdgeToBondMap bondMap) {
            super(container, graph, bondMap);
        }

        /**{@inheritDoc} */
        @Override
        ITetrahedralChirality createTetrahedral(IAtom atom, Stereocenters stereocenters) {
            return createTetrahedral(container.indexOf(atom), stereocenters);
        }

        /**{@inheritDoc} */
        @Override
        IDoubleBondStereochemistry createGeometric(IBond bond, Stereocenters stereocenters) {
            return createGeometric(container.indexOf(bond.getBegin()), container.indexOf(bond.getEnd()),
                                   stereocenters);
        }

        /**{@inheritDoc} */
        @Override
        ITetrahedralChirality createTetrahedral(int v, Stereocenters stereocenters) {

            if (!stereocenters.isStereocenter(v)) return null;

            IAtom focus = container.getAtom(v);

            if (hasUnspecifiedParity(focus)) return null;

            IAtom[] neighbors = new IAtom[4];

            neighbors[3] = focus;

            int n = 0;

            for (int w : graph[v])
                neighbors[n++] = container.getAtom(w);

            // too few/many neighbors
            if (n < 3 || n > 4) return null;

            int parity = parity(neighbors);

            Stereo winding = parity > 0 ? Stereo.ANTI_CLOCKWISE : Stereo.CLOCKWISE;

            return new TetrahedralChirality(focus, neighbors, winding);
        }

        @Override
        IStereoElement createAtropisomer(int u, int v,
                                         Stereocenters stereocenters) {

            IAtom end1 = container.getAtom(u);
            IAtom end2 = container.getAtom(v);

            if (hasUnspecifiedParity(end1) || hasUnspecifiedParity(end2))
                return null;

            if (graph[u].length != 3 || graph[v].length != 3)
                return null;

            // check degrees of connected atoms, we only create the
            // atropisomer if the rings are 3x ortho substituted
            // CC1=CC=CC(C)=C1-C1=C(C)C=CC=C1C yes (sum1=9,sum2=9)
            // CC1=CC=CC=C1-C1=C(C)C=CC=C1C yes    (sum1=8,sum2=9)
            // CC1=CC=CC(C)=C1-C1=CC=CC=C1 no      (sum1=7,sum2=9)
            // CC1=CC=CC=C1-C1=C(C)C=CC=C1 no      (sum1=8,sum2=8)
            int sum1 = graph[graph[u][0]].length +
                       graph[graph[u][1]].length +
                       graph[graph[u][2]].length;
            int sum2 = graph[graph[v][0]].length +
                       graph[graph[v][1]].length +
                       graph[graph[v][2]].length;

            if (sum1 > 9 || sum1 < 8)
                return null;
            if (sum2 > 9 || sum2 < 8)
                return null;
            if (sum1 + sum2 < 17)
                return null;


            IAtom[] carriers = new IAtom[4];

            int n = 0;
            for (int w : graph[u]) {
                if (w == v) continue;

                carriers[n] = container.getAtom(w);

                for (int w2 : graph[w]) {
                    if (isHydrogen(container.getAtom(w2)))
                        sum1--;
                }

                n++;
            }
            n = 2;
            for (int w : graph[v]) {
                if (w == u) continue;

                carriers[n] = container.getAtom(w);

                for (int w2 : graph[w]) {
                    if (isHydrogen(container.getAtom(w2)))
                        sum2--;
                }

                n++;
            }

            if (n != 4)
                return null;

            // recheck now we have account for explicit hydrogens
            if (sum1 > 9 || sum1 < 8)
                return null;
            if (sum2 > 9 || sum2 < 8)
                return null;
            if (sum1 + sum2 < 17)
                return null;

            int parity = parity(carriers);
            int cfg    = parity > 0 ? IStereoElement.LEFT : IStereoElement.RIGHT;

            return new Atropisomeric(container.getBond(end1, end2), carriers, cfg);
        }

        /**{@inheritDoc} */
        @Override
        IDoubleBondStereochemistry createGeometric(int u, int v, Stereocenters stereocenters) {

            if (hasUnspecifiedParity(container.getAtom(u)) || hasUnspecifiedParity(container.getAtom(v))) return null;

            int[] us = graph[u];
            int[] vs = graph[v];

            int x = us[0] == v ? us[1] : us[0];
            int w = vs[0] == u ? vs[1] : vs[0];

            IAtom uAtom = container.getAtom(u);
            IAtom vAtom = container.getAtom(v);
            IAtom uSubstituentAtom = container.getAtom(x);
            IAtom vSubstituentAtom = container.getAtom(w);

            if (uAtom.getPoint3d() == null || vAtom.getPoint3d() == null || uSubstituentAtom.getPoint3d() == null
                    || vSubstituentAtom.getPoint3d() == null) return null;

            int parity = parity(uAtom.getPoint3d(), vAtom.getPoint3d(), uSubstituentAtom.getPoint3d(),
                    vSubstituentAtom.getPoint3d());

            Conformation conformation = parity > 0 ? Conformation.OPPOSITE : Conformation.TOGETHER;

            IBond bond = bondMap.get(u, v);
            bond.setAtoms(new IAtom[]{uAtom, vAtom});

            return new DoubleBondStereochemistry(bond, new IBond[]{bondMap.get(u, x), bondMap.get(v, w),}, conformation);
        }

        /**{@inheritDoc} */
        @Override
        ExtendedTetrahedral createExtendedTetrahedral(int v, Stereocenters stereocenters) {

            IAtom focus = container.getAtom(v);

            if (hasUnspecifiedParity(focus)) return null;

            if (container.getConnectedBondsCount(focus) != 2)
        	    return null;

            IAtom[] terminals = ExtendedTetrahedral.findTerminalAtoms(container, focus);
            IAtom[] neighbors = new IAtom[4];

            int t0 = container.indexOf(terminals[0]);
            int t1 = container.indexOf(terminals[1]);

            // check for kinked cumulated bond
            if (!isColinear(focus, terminals))
                return null;

            neighbors[1] = terminals[0];
            neighbors[3] = terminals[1];

            int n = 0;
            for (int w : graph[t0]) {
                if (bondMap.get(t0, w).getOrder() != IBond.Order.SINGLE)
                    continue;
                neighbors[n++] = container.getAtom(w);
            }
            if (n == 0)
                return null;
            n = 2;
            for (int w : graph[t1]) {
                if (bondMap.get(t1, w).getOrder() != IBond.Order.SINGLE)
                    continue;
                neighbors[n++] = container.getAtom(w);
            }
            if (n == 2)
                return null;

            int parity = parity(neighbors);

            Stereo winding = parity > 0 ? Stereo.ANTI_CLOCKWISE : Stereo.CLOCKWISE;

            return new ExtendedTetrahedral(focus, neighbors, winding);
        }

        @Override
        ExtendedCisTrans createExtendedCisTrans(List<IBond> dbs,
                                                Stereocenters centers) {

            // only applies to odd-counts
            if ((dbs.size() & 0x1) == 0)
                return null;

            IBond   focus    = dbs.get(dbs.size()/2);
            IBond[] carriers = new IBond[2];
            int     config;

            IAtom begAtom = dbs.get(0).getOther(getShared(dbs.get(0),
                                                          dbs.get(1)));
            IAtom endAtom = dbs.get(dbs.size()-1)
                               .getOther(getShared(dbs.get(dbs.size()-1),
                                                   dbs.get(dbs.size()-2)));

            List<IBond> begBonds = container.getConnectedBondsList(begAtom);
            List<IBond> endBonds = container.getConnectedBondsList(endAtom);

            if (begBonds.size() < 2 || endBonds.size() < 2)
                return null;

            begBonds.remove(dbs.get(0));
            endBonds.remove(dbs.get(dbs.size() - 1));

            IAtom[] ends = ExtendedCisTrans.findTerminalAtoms(container, focus);
            assert ends != null;
            if (ends[0].equals(begAtom)) {
                carriers[0] = begBonds.get(0);
                carriers[1] = endBonds.get(0);
            } else {
                carriers[1] = begBonds.get(0);
                carriers[0] = endBonds.get(0);
            }

            IAtom begNbr = begBonds.get(0).getOther(begAtom);
            IAtom endNbr = endBonds.get(0).getOther(endAtom);

            Vector3d begVec = new Vector3d(begNbr.getPoint3d().x - begAtom.getPoint3d().x,
                                           begNbr.getPoint3d().y - begAtom.getPoint3d().y,
                                           begNbr.getPoint3d().z - begAtom.getPoint3d().z);
            Vector3d endVec = new Vector3d(endNbr.getPoint3d().x - endAtom.getPoint3d().x,
                                           endNbr.getPoint3d().y - endAtom.getPoint3d().y,
                                           endNbr.getPoint3d().z - endAtom.getPoint3d().z);

            begVec.normalize();
            endVec.normalize();

            double dot = begVec.dot(endVec);
            if (dot < 0)
                config = IStereoElement.OPPOSITE;
            else
                config = IStereoElement.TOGETHER;

            return new ExtendedCisTrans(focus, carriers, config);
        }

        private boolean isColinear(IAtom focus, IAtom[] terminals) {
            Vector3d vec0 = new Vector3d(terminals[0].getPoint3d().x - focus.getPoint3d().x,
                                         terminals[0].getPoint3d().y - focus.getPoint3d().y,
                                         terminals[0].getPoint3d().z - focus.getPoint3d().z);
            Vector3d vec1 = new Vector3d(terminals[1].getPoint3d().x - focus.getPoint3d().x,
                                         terminals[1].getPoint3d().y - focus.getPoint3d().y,
                                         terminals[1].getPoint3d().z - focus.getPoint3d().z);
            vec0.normalize();
            vec1.normalize();
            return Math.abs(vec0.dot(vec1) + 1) < 0.05;
        }

        /** 3x3 determinant helper for a constant third column */
        private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
            return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
        }

        /**
         * Parity computation for one side of a double bond in a geometric center.
         * The method needs the 3D coordinates of the double bond atoms (first 2
         * arguments) and the coordinates of two substituents (one at each end).
         *
         * @param u an atom double bonded to v
         * @param v an atom double bonded to u
         * @param x an atom sigma bonded to u
         * @param w an atom sigma bonded to v
         * @return the parity of the atoms
         */
        private int parity(Point3d u, Point3d v, Point3d x, Point3d w) {

            // create three vectors, v->u, v->w and u->x
            double[] vu = toVector(v, u);
            double[] vw = toVector(v, w);
            double[] ux = toVector(u, x);

            // normal vector (to compare against), the normal vector (n) looks like:
            // x     n w
            //  \    |/
            //   u = v
            double[] normal = crossProduct(vu, crossProduct(vu, vw));

            // compare the dot products of v->w and u->x, if the signs are the same
            // they are both pointing the same direction. if a value is close to 0
            // then it is at pi/2 radians (i.e. unspecified) however 3D coordinates
            // are generally discrete and do not normally represent on unspecified
            // stereo configurations so we don't check this
            int parity = (int) Math.signum(dot(normal, vw)) * (int) Math.signum(dot(normal, ux));

            // invert sign, this then matches with Sp2 double bond parity
            return parity * -1;
        }

        /**
         * Parity computation for 3D tetrahedral stereocenters.
         *
         * @param atoms the atoms surrounding the central focus atom
         * @return the parity (winding)
         */
        private int parity(IAtom[] atoms) {

            if (atoms.length != 4) throw new IllegalArgumentException("incorrect number of atoms");

            Point3d[] coordinates = new Point3d[atoms.length];
            for (int i = 0; i < atoms.length; i++) {
                coordinates[i] = atoms[i].getPoint3d();
                if (coordinates[i] == null) return 0;
            }

            double x1 = coordinates[0].x;
            double x2 = coordinates[1].x;
            double x3 = coordinates[2].x;
            double x4 = coordinates[3].x;

            double y1 = coordinates[0].y;
            double y2 = coordinates[1].y;
            double y3 = coordinates[2].y;
            double y4 = coordinates[3].y;

            double z1 = coordinates[0].z;
            double z2 = coordinates[1].z;
            double z3 = coordinates[2].z;
            double z4 = coordinates[3].z;

            double det = (z1 * det(x2, y2, x3, y3, x4, y4)) - (z2 * det(x1, y1, x3, y3, x4, y4))
                    + (z3 * det(x1, y1, x2, y2, x4, y4)) - (z4 * det(x1, y1, x2, y2, x3, y3));

            return (int) Math.signum(det);
        }

        /**
         * Create a vector by specifying the source and destination coordinates.
         *
         * @param src  start point of the vector
         * @param dest end point of the vector
         * @return a new vector
         */
        private static double[] toVector(Point3d src, Point3d dest) {
            return new double[]{dest.x - src.x, dest.y - src.y, dest.z - src.z};
        }

        /**
         * Dot product of two 3D coordinates
         *
         * @param u either 3D coordinates
         * @param v other 3D coordinates
         * @return the dot-product
         */
        private static double dot(double[] u, double[] v) {
            return (u[0] * v[0]) + (u[1] * v[1]) + (u[2] * v[2]);
        }

        /**
         * Cross product of two 3D coordinates
         *
         * @param u either 3D coordinates
         * @param v other 3D coordinates
         * @return the cross-product
         */
        private static double[] crossProduct(double[] u, double[] v) {
            return new double[]{(u[1] * v[2]) - (v[1] * u[2]), (u[2] * v[0]) - (v[2] * u[0]),
                    (u[0] * v[1]) - (v[0] * u[1])};
        }
    }

    private static boolean isHydrogen(IAtom atom) {
        Integer elem = atom.getAtomicNumber();
        return elem != null && elem == 1;
    }
}
