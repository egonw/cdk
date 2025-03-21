/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.HeterolyticCleavagePBReaction;
import org.openscience.cdk.reaction.type.HyperconjugationReaction;
import org.openscience.cdk.reaction.type.PiBondingMovementReaction;
import org.openscience.cdk.reaction.type.RearrangementAnionReaction;
import org.openscience.cdk.reaction.type.RearrangementCationReaction;
import org.openscience.cdk.reaction.type.RearrangementLonePairReaction;
import org.openscience.cdk.reaction.type.RearrangementRadicalReaction;
import org.openscience.cdk.reaction.type.SharingLonePairReaction;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;

/**
* TestSuite that runs all tests.
*
*/
public class StructureResonanceGeneratorTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private final LonePairElectronChecker                 lpcheck = new LonePairElectronChecker();

    /**
     * Constructor of the StructureResonanceGeneratorTest.
     */
    StructureResonanceGeneratorTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testStructureResonanceGenerator() {

        Assertions.assertNotNull(new StructureResonanceGenerator());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testStructureResonanceGenerator_boolean() {

        Assertions.assertNotNull(new StructureResonanceGenerator(true));
    }

    /**
     * A unit test suite for JUnit
     *
     *
     */
    @Test
    void testGetReactions() {

        Assertions.assertNotNull(new StructureResonanceGenerator().getReactions());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testSetDefaultReactions() {
        StructureResonanceGenerator sRG = new StructureResonanceGenerator();

        List<IReactionProcess> reactionList = sRG.getReactions();
        Assertions.assertNotNull(reactionList);

        Assertions.assertEquals(6, reactionList.size());

        SharingLonePairReaction slReaction = (SharingLonePairReaction) reactionList.get(0);
        Assertions.assertEquals(1, slReaction.getParameterList().size());
        List<IParameterReact> objects = slReaction.getParameterList();
        for (IParameterReact object : objects) {
            if (object instanceof SetReactionCenter) Assertions.assertFalse(object.isSetParameter());
        }

        PiBondingMovementReaction pBReaction = (PiBondingMovementReaction) reactionList.get(1);
        Assertions.assertEquals(1, pBReaction.getParameterList().size());
        objects = pBReaction.getParameterList();
        for (IParameterReact object : objects) {
            if (object instanceof SetReactionCenter) Assertions.assertFalse(object.isSetParameter());
        }

        RearrangementAnionReaction raReaction = (RearrangementAnionReaction) reactionList.get(2);
        Assertions.assertEquals(1, raReaction.getParameterList().size());
        objects = raReaction.getParameterList();
        for (IParameterReact object : objects) {
            if (object instanceof SetReactionCenter) Assertions.assertFalse(object.isSetParameter());
        }

        RearrangementCationReaction rcReaction = (RearrangementCationReaction) reactionList.get(3);
        Assertions.assertEquals(1, rcReaction.getParameterList().size());
        objects = rcReaction.getParameterList();
        for (IParameterReact object : objects) {
            if (object instanceof SetReactionCenter) Assertions.assertFalse(object.isSetParameter());
        }

        RearrangementLonePairReaction lnReaction = (RearrangementLonePairReaction) reactionList.get(4);
        Assertions.assertEquals(1, lnReaction.getParameterList().size());
        objects = lnReaction.getParameterList();
        for (IParameterReact object : objects) {
            if (object instanceof SetReactionCenter) Assertions.assertFalse(object.isSetParameter());
        }

        RearrangementRadicalReaction rrReaction = (RearrangementRadicalReaction) reactionList.get(5);
        Assertions.assertEquals(1, rrReaction.getParameterList().size());
        objects = rrReaction.getParameterList();
        for (IParameterReact object : objects) {
            if (object instanceof SetReactionCenter) Assertions.assertFalse(object.isSetParameter());
        }

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     * @throws Exception Thrown to indicate the failure of this unit test
     */
    @Test
    void testSetReactions_List() throws Exception {

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        List<IReactionProcess> reactionList = sRG.getReactions();
        Assertions.assertNotNull(reactionList);

        Assertions.assertEquals(6, reactionList.size());

        // put only one reaction more.
        List<IReactionProcess> newReactionList = new ArrayList<>();

        IReactionProcess reaction = new HyperconjugationReaction();
        newReactionList.add(reaction);

        sRG.setReactions(newReactionList);

        Assertions.assertEquals(1, sRG.getReactions().size());

    }

    //
    //    /**
    //	 * <p>A unit test suite for JUnit: Resonance - CC(=[O*+])C=O</p>
    //	 * <p>CC(=[O*+])C=O <=> C[C+]([O*])C=O <=> CC([O*])=CO <=> CC(=O)[C*][O+] <=> CC(=O)C=[O*+]</p>
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testGetAllStructures_IAtomContainer() throws Exception {
    //		IAtomContainer molecule = (new SmilesParser(DefaultChemObjectBuilder.getInstance())).parseSmiles("CC(=O)C=O");
    //        addExplicitHydrogens(molecule);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
    //        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    //        lpcheck.saturate(molecule);
    //
    //        IAtom atom =  molecule.getAtom(2);
    //        molecule.addSingleElectron(new SingleElectron(atom));
    //        atom.setFormalCharge(1);
    //        List<ILonePair> selectron = molecule.getConnectedLonePairsList(atom);
    //		molecule.removeLonePair(selectron.get(0));
    //        SmilesGenerator sg = new SmilesGenerator();
    //		System.out.println("> "+sg.createSMILES(molecule));
    //		makeSureAtomTypesAreRecognized(molecule);
    //
    //		StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,true,true,false,false,-1);
    //		IAtomContainerSet setOfMolecules = gRI.getAllStructures(molecule);
    //		for(int i = 0; i < setOfMolecules.getAtomContainerCount(); i++)
    //			System.out.println("> "+sg.createSMILES((IAtomContainer) setOfMolecules.getAtomContainer(i)));
    //
    //
    //		Iterator<IAtomContainer> containers = setOfMolecules.atomContainers();
    //		SmilesGenerator smiGen = new SmilesGenerator();
    //		while (containers.hasNext()) {
    //			System.out.println(smiGen.createSMILES(new AtomContainer(containers.next())));
    //		}
    //		Assert.assertEquals(8,setOfMolecules.getAtomContainerCount());
    //
    //		/*1*/
    //        IAtomContainer molecule1 = (new SmilesParser(DefaultChemObjectBuilder.getInstance())).parseSmiles("C[C+](O)C=O");
    //        for(int i = 0; i < 4; i++)
    //			molecule1.addAtom(new Atom("H"));
    //		molecule1.addBond(0, 5, IBond.Order.SINGLE);
    //	    molecule1.addBond(0, 6, IBond.Order.SINGLE);
    //	    molecule1.addBond(0, 7, IBond.Order.SINGLE);
    //	    molecule1.addBond(3, 8, IBond.Order.SINGLE);
    //        lpcheck.saturate(molecule1);
    //        IAtom atom1 =  molecule1.getAtom(2);
    //        molecule1.addSingleElectron(new SingleElectron(atom1));
    //        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
    //		Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
    //
    ////		/*2*/
    ////		Molecule molecule2 = (new SmilesParser()).parseSmiles("CC(O)=CO");
    ////		for(int i = 0; i < 4; i++)
    ////			molecule2.addAtom(new Atom("H"));
    ////		molecule2.addBond(0, 5, IBond.Order.SINGLE);
    ////	    molecule2.addBond(0, 6, IBond.Order.SINGLE);
    ////	    molecule2.addBond(0, 7, IBond.Order.SINGLE);
    ////	    molecule2.addBond(3, 8, IBond.Order.SINGLE);
    ////        lpcheck.newSaturate(molecule2);
    ////		IAtom atom2a =  molecule2.getAtom(2);
    ////		molecule2.addElectronContainer(new SingleElectron(atom2a));
    ////
    ////		IAtom atom2b =  molecule2.getAtom(4);
    ////		atom2b.setHydrogenCount(0);
    ////		atom2b.setFormalCharge(1);
    ////
    ////		qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
    ////		Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(3),qAC));
    //	}
    //
    //	private void makeSureAtomTypesAreRecognized(IAtomContainer molecule)
    //            throws CDKException {
    //	    Iterator<IAtom> atoms = molecule.atoms();
    //		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getNewBuilder());
    //		while (atoms.hasNext()) {
    //			IAtom nextAtom = atoms.next();
    //			Assert.assertNotNull(
    //				"Missing atom type for: " + nextAtom,
    //				matcher.findMatchingAtomType(molecule, nextAtom)
    //			);
    //		}
    //    }
    //	/**
    //	 * A unit test suite for JUnit: Resonance CC(=[O*+])C=O <=> CC(=O)C=[O*+]
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testGetStructures_IAtomContainer() throws Exception {
    //		IAtomContainer molecule = (new SmilesParser(DefaultChemObjectBuilder.getInstance())).parseSmiles("CC(=O)C=O");
    //        addExplicitHydrogens(molecule);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
    //        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    //        lpcheck.saturate(molecule);
    //
    //        IAtom atom =  molecule.getAtom(2);
    //        molecule.addSingleElectron(new SingleElectron(atom));
    //        atom.setFormalCharge(1);
    //        List<ILonePair> selectron = molecule.getConnectedLonePairsList(atom);
    //		molecule.removeLonePair(selectron.get(selectron.size()-1));
    //		makeSureAtomTypesAreRecognized(molecule);
    //
    //		StructureResonanceGenerator gRI = new StructureResonanceGenerator();
    //		IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);
    //
    //		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());
    //
    //		IAtomContainer molecule1 = (new SmilesParser(DefaultChemObjectBuilder.getInstance())).parseSmiles("CC(=O)C=O");
    //		addExplicitHydrogens(molecule1);
    //		lpcheck.saturate(molecule1);
    //		IAtom atom1 =  molecule1.getAtom(4);
    //		molecule1.addSingleElectron(new SingleElectron(atom1));
    //		selectron = molecule1.getConnectedLonePairsList(atom1);
    //		molecule1.removeLonePair((ILonePair)selectron.get(0));
    //		atom1.setFormalCharge(1);
    //
    //
    //		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
    //		Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
    //
    //	}
    //	/**
    //	 * A unit test suite for JUnit: Resonance CCC(=[O*+])C(C)=O <=> CCC(=O)C(C)=[O*+]
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testGetStructures2() throws Exception {
    //		IAtomContainer molecule = (new SmilesParser(DefaultChemObjectBuilder.getInstance())).parseSmiles("CCC(=O)C(C)=O");
    //        addExplicitHydrogens(molecule);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
    //        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    //        lpcheck.saturate(molecule);
    //
    //        IAtom atom =  molecule.getAtom(3);
    //        molecule.addSingleElectron(new SingleElectron(atom));
    //        atom.setFormalCharge(1);
    //        List<ILonePair> selectron = molecule.getConnectedLonePairsList(atom);
    //		molecule.removeLonePair(selectron.get(0));
    //		makeSureAtomTypesAreRecognized(molecule);
    //
    //		StructureResonanceGenerator gRI = new StructureResonanceGenerator();
    //		IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);
    //
    //		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());
    //
    //		IAtomContainer molecule1 = (new SmilesParser(DefaultChemObjectBuilder.getInstance())).parseSmiles("CCC(=O)C(C)=O");
    //		addExplicitHydrogens(molecule1);
    //		lpcheck.saturate(molecule1);
    //
    //        IAtom atom1 =  molecule1.getAtom(6);
    //        molecule1.addSingleElectron(new SingleElectron(atom1));
    //        atom1.setFormalCharge(1);
    //        selectron = molecule.getConnectedLonePairsList(atom);
    //		molecule.removeLonePair((ILonePair)selectron.get(0));
    //
    //		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
    //		Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
    //
    //	}
    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+] +
     * C-C=C-[C+]-C-[C+]-C=C + C-[C+]-C=C-C-[C+]-C=C
     *
     *
     */
    @Test
    void testGetStructures_IAtomContainer() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(new Atom("C"));
        molecule.getAtom(3).setFormalCharge(+1);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        molecule.getAtom(7).setFormalCharge(+1);
        addExplicitHydrogens(molecule);

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = sRG.getStructures(molecule);

        Assertions.assertEquals(4, setOfMolecules.getAtomContainerCount());

    }

    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+]
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testFlagActiveCenter1() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(new Atom("C"));
        molecule.getAtom(3).setFormalCharge(+1);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        molecule.getAtom(7).setFormalCharge(+1);
        addExplicitHydrogens(molecule);

        molecule.getAtom(1).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getBond(2).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getAtom(3).setFlag(IChemObject.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        for (IReactionProcess reaction : sRG.getReactions()) {
            reaction.setParameterList(paramList);
        }

        IAtomContainerSet setOfMolecules = sRG.getStructures(molecule);

        Assertions.assertEquals(2, setOfMolecules.getAtomContainerCount());

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(new Atom("C"));
        molecule2.addAtom(new Atom("C"));
        molecule2.getAtom(1).setFormalCharge(+1);
        molecule2.addBond(0, 1, IBond.Order.SINGLE);
        molecule2.addAtom(new Atom("C"));
        molecule2.addBond(1, 2, IBond.Order.SINGLE);
        molecule2.addAtom(new Atom("C"));
        molecule2.addBond(2, 3, IBond.Order.DOUBLE);
        molecule2.addAtom(new Atom("C"));
        molecule2.addBond(3, 4, IBond.Order.SINGLE);
        molecule2.addAtom(new Atom("C"));
        molecule2.addBond(4, 5, IBond.Order.SINGLE);
        molecule2.addAtom(new Atom("C"));
        molecule2.addBond(5, 6, IBond.Order.DOUBLE);
        molecule2.addAtom(new Atom("C"));
        molecule2.addBond(6, 7, IBond.Order.SINGLE);
        molecule2.getAtom(7).setFormalCharge(+1);
        addExplicitHydrogens(molecule2);

        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1), qAC));
    }

    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C-] <=> C=C-[C-]-C
     *
     *
     */
    @Test
    void testtestGetStructures2() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.getAtom(3).setFormalCharge(-1);
        molecule.addLonePair(new LonePair(molecule.getAtom(3)));
        addExplicitHydrogens(molecule);

        StructureResonanceGenerator gR = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = gR.getStructures(molecule);

        Assertions.assertEquals(2, setOfMolecules.getAtomContainerCount());

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(0, 1, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(1, 2, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(2, 3, IBond.Order.SINGLE);
        molecule2.getAtom(2).setFormalCharge(-1);
        molecule2.addLonePair(new LonePair(molecule2.getAtom(2)));
        addExplicitHydrogens(molecule2);

        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1), qAC));
    }

    /**
     * A unit test suite for JUnit: Resonance Formic acid  C(=O)O <=> [C+](-[O-])O <=> C([O-])=[O+]
     *
     *  @cdk.inchi InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H
     *
     *
     */
    @Test
    void testFormicAcid() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gR = new StructureResonanceGenerator();
        List<IReactionProcess> reactionList = gR.getReactions();
        reactionList.add(new HeterolyticCleavagePBReaction());
        gR.setReactions(reactionList);
        IAtomContainerSet setOfMolecules = gR.getStructures(molecule);

        Assertions.assertEquals(3, setOfMolecules.getAtomContainerCount());

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule2.getAtom(1).setFormalCharge(-1);
        molecule2.addBond(0, 1, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule2.getAtom(2).setFormalCharge(1);
        molecule2.addBond(0, 2, IBond.Order.DOUBLE);
        addExplicitHydrogens(molecule2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
        lpcheck.saturate(molecule2);

        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1), qAC));

    }

    /**
     * A unit test suite for JUnit: Resonance Formic acid  F-C=C <=> [F+]=C-[C-]
     *
     *  @cdk.inchi InChI=1/C2H3F/c1-2-3/h2H,1H2
     *
     *
     */
    @Test
    void testFluoroethene() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gR = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = gR.getStructures(molecule);

        Assertions.assertEquals(2, setOfMolecules.getAtomContainerCount());

        IAtomContainer molecule1 = builder.newInstance(IAtomContainer.class);
        molecule1.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(0, 1, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(1, 2, IBond.Order.SINGLE);
        molecule1.getAtom(0).setFormalCharge(+1); // workaround for bug #1875949
        molecule1.getAtom(2).setFormalCharge(-1);
        addExplicitHydrogens(molecule1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule1);
        lpcheck.saturate(molecule1);

        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1), qAC));
    }

    /**
     * A unit test suite for JUnit: Resonance Fluorobenzene  Fc1ccccc1 <=> ...
     *
     * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testFluorobenzene() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(5, setOfMolecules.getAtomContainerCount());

        IAtomContainer molecule1 = builder.newInstance(IAtomContainer.class);
        molecule1.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule1.getAtom(0).setFormalCharge(1);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(0, 1, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.getAtom(2).setFormalCharge(-1);
        molecule1.addBond(1, 2, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(2, 3, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(3, 4, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(4, 5, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(5, 6, IBond.Order.DOUBLE);
        molecule1.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule1);
        lpcheck.saturate(molecule1);

        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(2), qAC));

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule2.getAtom(0).setFormalCharge(1);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(0, 1, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(1, 2, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(2, 3, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(3, 4, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.getAtom(4).setFormalCharge(-1);
        molecule2.addBond(4, 5, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(5, 6, IBond.Order.DOUBLE);
        molecule2.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
        lpcheck.saturate(molecule2);

        IAtomContainer product2 = setOfMolecules.getAtomContainer(4);
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(product2, qAC));

    }

    /**
     * A unit test suite for JUnit: Resonance Fluorobenzene  Fc1ccccc1 <=> ...
     *
     * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testFluorobenzeneContainer() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainer container = gRI.getContainer(molecule, molecule.getAtom(0));

        Assertions.assertEquals(7, container.getAtomCount());

    }

    /**
     * A unit test suite for JUnit: Resonance Fluorobenzene  Fc1ccccc1 <=> ...
     *
     * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testFluorobenzene_symm() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator(true);
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(3, setOfMolecules.getAtomContainerCount());

        IAtomContainer molecule1 = builder.newInstance(IAtomContainer.class);
        molecule1.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule1.getAtom(0).setFormalCharge(1);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(0, 1, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.getAtom(2).setFormalCharge(-1);
        molecule1.addBond(1, 2, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(2, 3, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(3, 4, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(4, 5, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(5, 6, IBond.Order.DOUBLE);
        molecule1.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule1);
        lpcheck.saturate(molecule1);

        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(setOfMolecules.getAtomContainer(1), qAC));

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule2.getAtom(0).setFormalCharge(1);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(0, 1, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(1, 2, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(2, 3, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(3, 4, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.getAtom(4).setFormalCharge(-1);
        molecule2.addBond(4, 5, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(5, 6, IBond.Order.DOUBLE);
        molecule2.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
        lpcheck.saturate(molecule2);

        IAtomContainer product2 = setOfMolecules.getAtomContainer(2);
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(product2, qAC));

    }

    /**
     * A unit test suite for JUnit: Resonance   n1ccccc1 <=> ...
     *
     * @cdk.inchi InChI=1/C6H7N/c7-6-4-2-1-3-5-6/h1-5H,7H2
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testAniline() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(5, setOfMolecules.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit: Resonance   n1ccccc1 <=> ...
     *
     * @cdk.inchi InChI=1/C6H7N/c7-6-4-2-1-3-5-6/h1-5H,7H2
     *
     *
     */
    @Test
    void testAniline_Symm() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator(true);
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(3, setOfMolecules.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit.
     * ClC([H])=C([H])[C+]([H])[H] => [H]C([H])=C([H])[C+](Cl)[H] +
     * Cl=C([H])[C-]([H])[C+]([H])[H] + Cl=C([H])C([H])=C([H])[H]
     *
     * @throws Exception
     */
    @Test
    @Tag("SlowTest")
    void testAllyl() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "Cl")); // to remove symmetry :)
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.getAtom(0).setFormalCharge(+1);
        addExplicitHydrogens(molecule);
        Assertions.assertEquals(8, molecule.getAtomCount());
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet resonanceStructures = gRI.getStructures(molecule);

        Assertions.assertEquals(4, resonanceStructures.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @throws Exception
     */
    @Test
    @Tag("SlowTest")
    void testAllylRadical() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.getAtom(0).setFormalCharge(1);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C")); // to remove symmetry :)
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        molecule.getAtom(0).setFormalCharge(0);
        molecule.addSingleElectron(new SingleElectron(molecule.getAtom(0)));
        Assertions.assertEquals(11, molecule.getAtomCount());

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet resonanceStructures = gRI.getStructures(molecule);
        Assertions.assertEquals(2, resonanceStructures.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit.
     * [H]C([H])=C([H])[O-] => O=C([H])[C-]([H])[H]
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2-3H,1H2/p-1/fC2H3O/h3h/q-1
     *
     * @throws Exception
     */
    @Test
    void testEthenolate() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.getAtom(0).setFormalCharge(-1);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
        Assertions.assertEquals(6, molecule.getAtomCount());

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet resonanceStructures = gRI.getStructures(molecule);

        Assertions.assertEquals(2, resonanceStructures.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit.
     * [H]N([H])C1=C([H])C([H])=C([H])C([H])=C1C([H])([H])[H] =>
     *  + [H]C=1C([H])=C(C(=[N+]([H])[H])[C-]([H])C=1([H]))C([H])([H])[H]
     *  + [H]C1=C([H])[C-]([H])C([H])=C(C1=[N+]([H])[H])C([H])([H])[H]
     *  + [H]C=1C([H])=C([H])[C-](C(C=1([H]))=[N+]([H])[H])C([H])([H])[H]
     *
     * @cdk.inchi InChI=1/C7H9N/c1-6-4-2-3-5-7(6)8/h2-5H,8H2,1H3
     *
     * @throws Exception
     */
    @Test
    void test2Methylaniline() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(5, 0, IBond.Order.DOUBLE);
        molecule.addBond(0, 6, IBond.Order.SINGLE);
        molecule.addBond(1, 7, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        Assertions.assertEquals(17, molecule.getAtomCount());

        StructureResonanceGenerator gRI = new StructureResonanceGenerator(true);
        IAtomContainerSet resonanceStructures = gRI.getStructures(molecule);

        Assertions.assertEquals(4, resonanceStructures.getAtomContainerCount());
    }

    /**
     *
     * A unit test suite for JUnit.
     *
     * @cdk.inchi InChI=1/C8H10/c1-7-5-3-4-6-8(7)2/h3-6H,1-2H3
     *
     *
     *
     * @throws    Exception
     */
    @Test
    void test12DimethylBenzene() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(5, 0, IBond.Order.DOUBLE);
        molecule.addBond(0, 6, IBond.Order.SINGLE);
        molecule.addBond(1, 7, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        LonePairElectronChecker lpChecker = new LonePairElectronChecker();
        lpChecker.saturate(molecule);

        Assertions.assertEquals(18, molecule.getAtomCount());

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        // put only one reaction more.
        List<IReactionProcess> newReactionList = new ArrayList<>();
        IReactionProcess reaction = new PiBondingMovementReaction();
        newReactionList.add(reaction);

        gRI.setReactions(newReactionList);

        IAtomContainerSet resonanceStructures = gRI.getStructures(molecule);

        Assertions.assertEquals(2, resonanceStructures.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit: Resonance Fluorobenzene  Fc1ccccc1 <=> ...
     *
     * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     *
     *
     */
    @Test
    void testPreservingAromaticity() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        boolean isAromatic = Aromaticity.cdkLegacy().apply(molecule);
        Assertions.assertTrue(isAromatic, "Molecule is expected to be marked aromatic!");

        Assertions.assertTrue(molecule.getBond(1).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(molecule.getBond(2).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(molecule.getBond(3).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(molecule.getBond(4).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(molecule.getBond(5).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(molecule.getBond(6).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");

        StructureResonanceGenerator gRI = new StructureResonanceGenerator(false);
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(5, setOfMolecules.getAtomContainerCount());

        IAtomContainer prod1 = setOfMolecules.getAtomContainer(1);
        Assertions.assertTrue(prod1.getBond(1).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod1.getBond(2).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod1.getBond(3).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod1.getBond(4).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod1.getBond(5).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod1.getBond(6).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        IAtomContainer prod2 = setOfMolecules.getAtomContainer(2);
        Assertions.assertTrue(prod2.getBond(1).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod2.getBond(2).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod2.getBond(3).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod2.getBond(4).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod2.getBond(5).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod2.getBond(6).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        IAtomContainer prod3 = setOfMolecules.getAtomContainer(3);
        Assertions.assertTrue(prod3.getBond(1).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod3.getBond(2).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod3.getBond(3).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod3.getBond(4).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod3.getBond(5).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");
        Assertions.assertTrue(prod3.getBond(6).getFlag(IChemObject.AROMATIC), "Bond is expected to be marked aromatic!");

    }

    @Test
    void testCyclobutadiene() throws Exception {
        // anti-aromatic
        IAtomContainer molecule = TestMoleculeFactory.makeCyclobutadiene();
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(2, setOfMolecules.getAtomContainerCount());

    }

    /**
     * A unit test for JUnit
     *
     * @cdk.bug      1728830
     */
    @Test
    @Tag("SlowTest")
    void testBenzene() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

        Assertions.assertEquals(2, setOfMolecules.getAtomContainerCount());
    }

    /**
     * A unit test suite for JUnit.
     * [H]C([H])=C([H])[O-] => OCC
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2-3H,1H2/p-1/fC2H3O/h3h/q-1
     *
     * @throws Exception
     */
    @Test
    @Tag("SlowTest")
    void testGetContainers_IAtomContainer() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.getAtom(0).setFormalCharge(-1);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
        Assertions.assertEquals(6, molecule.getAtomCount());

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet containers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, containers.getAtomContainerCount());
        Assertions.assertEquals(3, containers.getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(2, containers.getAtomContainer(0).getBondCount());
    }

    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+]
     *
     *
     */
    @Test
    void testGetContainers2Groups() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.getBond(0).setID("bond_0");
        molecule.addAtom(new Atom("C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.getBond(1).setID("bond_1");
        molecule.addAtom(new Atom("C"));
        molecule.getAtom(3).setFormalCharge(+1);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.getBond(2).setID("bond_2");
        molecule.addAtom(new Atom("C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.getBond(3).setID("bond_3");
        molecule.addAtom(new Atom("C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.getBond(4).setID("bond_4");
        molecule.addAtom(new Atom("C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.getBond(5).setID("bond_5");
        molecule.addAtom(new Atom("C"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        molecule.getBond(6).setID("bond_6");
        molecule.getAtom(7).setFormalCharge(+1);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = sRG.getContainers(molecule);

        Assertions.assertEquals(2, setOfContainers.getAtomContainerCount());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(3, setOfContainers.getAtomContainer(i).getAtomCount());
            Assertions.assertEquals(2, setOfContainers.getAtomContainer(i).getBondCount());

        }
    }

    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+]
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testGetContainer_IAtomContainer_IAtom() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom atom1 = builder.newInstance(IAtom.class, "C");
        atom1.setID("atom1");
        molecule.addAtom(atom1);
        IAtom atom2 = builder.newInstance(IAtom.class, "C");
        atom2.setID("atom2");
        molecule.addAtom(atom2);
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        IAtom atom3 = builder.newInstance(IAtom.class, "C");
        atom3.setID("atom3");
        molecule.addAtom(atom3);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        IAtom atom4 = builder.newInstance(IAtom.class, "C");
        atom4.setID("atom4");
        molecule.addAtom(atom4);
        atom4.setFormalCharge(+1);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addAtom(new Atom("C"));
        molecule.getAtom(7).setFormalCharge(+1);
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        IAtomContainer container = sRG.getContainer(molecule, atom4);

        Assertions.assertEquals(3, container.getAtomCount());
        Assertions.assertEquals(2, container.getBondCount());
        Assertions.assertTrue(container.contains(atom4));

    }

    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+]
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testGetContainer_IAtomContainer_IBond() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom atom1 = builder.newInstance(IAtom.class, "C");
        atom1.setID("atom1");
        molecule.addAtom(atom1);
        IAtom atom2 = builder.newInstance(IAtom.class, "C");
        atom2.setID("atom2");
        molecule.addAtom(atom2);
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        IAtom atom3 = builder.newInstance(IAtom.class, "C");
        atom3.setID("atom3");
        molecule.addAtom(atom3);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        IAtom atom4 = builder.newInstance(IAtom.class, "C");
        atom4.setID("atom4");
        molecule.addAtom(atom4);
        atom4.setFormalCharge(+1);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addAtom(new Atom("C"));
        molecule.getAtom(7).setFormalCharge(+1);
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        IAtomContainer container = sRG.getContainer(molecule, molecule.getBond(1));

        Assertions.assertEquals(3, container.getAtomCount());
        Assertions.assertEquals(2, container.getBondCount());
        Assertions.assertTrue(container.contains(molecule.getBond(1)));

    }

    /**
     * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+]
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testGetID() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom atom1 = builder.newInstance(IAtom.class, "C");
        atom1.setID("atom1");
        molecule.addAtom(atom1);
        IAtom atom2 = builder.newInstance(IAtom.class, "C");
        atom2.setID("atom2");
        molecule.addAtom(atom2);
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        IAtom atom3 = builder.newInstance(IAtom.class, "C");
        atom3.setID("atom3");
        molecule.addAtom(atom3);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        IAtom atom4 = builder.newInstance(IAtom.class, "C");
        atom4.setID("atom4");
        molecule.addAtom(atom4);
        atom4.setFormalCharge(+1);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        IAtom atom5 = builder.newInstance(IAtom.class, "C");
        atom5.setID("atom5");
        molecule.addAtom(atom5);
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        IAtom atom6 = builder.newInstance(IAtom.class, "C");
        atom6.setID("atom6");
        molecule.addAtom(atom6);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        IAtom atom7 = builder.newInstance(IAtom.class, "C");
        atom7.setID("atom7");
        molecule.addAtom(atom7);
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        IAtom atom8 = builder.newInstance(IAtom.class, "C");
        atom8.setID("atom8");
        molecule.addAtom(atom8);
        atom8.setFormalCharge(+1);
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator sRG = new StructureResonanceGenerator();
        IAtomContainer container = sRG.getContainer(molecule, atom4);

        Assertions.assertNotNull(atom2.getID());
        Assertions.assertNotNull(atom3.getID());
        Assertions.assertNotNull(atom4.getID());
        Assertions.assertEquals(atom2.getID(), container.getAtom(0).getID());
        Assertions.assertEquals(atom3.getID(), container.getAtom(1).getID());
        Assertions.assertEquals(atom4.getID(), container.getAtom(2).getID());

    }

    /**
     * A unit test suite for JUnit: Resonance 1-fluoro-2-methylbenzene  Fc1ccccc1C <=> Fc1ccccc1
     *
     * @cdk.inchi  InChI=1/C7H7F/c1-6-4-2-3-5-7(6)8/h2-5H,1H3
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testGetContainersFluoromethylbenzene() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());

        IAtomContainer container = setOfContainers.getAtomContainer(0);

        Assertions.assertEquals(15, molecule.getAtomCount());
        Assertions.assertEquals(7, container.getAtomCount());

        Assertions.assertEquals(15, molecule.getBondCount());
        Assertions.assertEquals(7, container.getBondCount());

    }

    /**
     * A unit test suite for JUnit: Resonance 1-fluoro-benzene  Fc1ccccc1C <=> Fc1ccccc1
     *
     * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testGetContainersFluorobenzene() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());

        IAtomContainer container = setOfContainers.getAtomContainer(0);

        Assertions.assertEquals(12, molecule.getAtomCount());
        Assertions.assertEquals(7, container.getAtomCount());

        Assertions.assertEquals(12, molecule.getBondCount());
        Assertions.assertEquals(7, container.getBondCount());

    }

    /**
     * A unit test suite for JUnit: Resonance Formic acid  C-C(C)=C <=> [Cl+]=C(C)-[C-]
     *
     *  @cdk.inchi InChI=1/C3H5Cl/c1-3(2)4/h1H2,2H3
     *
     *
     */
    @Test

    @Tag("SlowTest")
    void test1Propene2chloro() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "Cl"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());
        Assertions.assertEquals(3, setOfContainers.getAtomContainer(0).getAtomCount());
    }

    /**
     * A unit test suite for JUnit: COC1=CC=C(C=C1)Br
     *
     *  @cdk.inchi InChI=1/C7H7BrO/c1-9-7-4-2-6(8)3-5-7/h2-5H,1H3
     *
     *
     */
    @Test
    @Tag("SlowTest")
    void testBenzene1bromo4methoxy() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());
        Assertions.assertEquals(8, setOfContainers.getAtomContainer(0).getAtomCount());
    }

    /**
     * A unit test suite for JUnit: COC1=CC=C(C=C1)Br
     *
     *  @cdk.inchi InChI=1/C7H7BrO/c1-9-7-4-2-6(8)3-5-7/h2-5H,1H3
     *
     *
     */
    @Test
    void testBenzene1bromo4methoxy_with() throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "Br"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.SINGLE);
        molecule.addBond(6, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(7, 8, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());
        Assertions.assertEquals(8, setOfContainers.getAtomContainer(0).getAtomCount());
    }

    /**
     * A unit test suite for JUnit
     *
     */
    @Test
    void testGetMaximalStructures() throws Exception {
        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        Assertions.assertEquals(50, gRI.getMaximalStructures());

    }

    /**
     * A unit test suite for JUnit
     *
     */
    @Test
    @Tag("SlowTest")
    void testSetMaximalStructures_int() throws Exception {
        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        Assertions.assertEquals(50, gRI.getMaximalStructures());
        gRI.setMaximalStructures(1);
        Assertions.assertEquals(1, gRI.getMaximalStructures());

    }

    /**
     * A unit test suite for JUnit: c1ccccc1CN
     *
     *  @cdk.inchi InChI=1/C7H9N/c8-6-7-4-2-1-3-5-7/h1-5H,6,8H2
     *
     *
     */
    @Test
    void testBenzylamine() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addBond(0, 7, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());
        Assertions.assertEquals(6, setOfContainers.getAtomContainer(0).getAtomCount());
    }

    /**
     * A unit test suite for JUnit: c1ccccc1CN
     *
     *  @cdk.inchi InChI=1/C7H9N/c8-6-7-4-2-1-3-5-7/h1-5H,6,8H2
     *  @cdk.bug 2014515
     *
     *
     */
    @Test
    void testBenzylamine_Aromatic() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addBond(0, 7, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(molecule));

        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertNotNull(setOfContainers);
        Assertions.assertEquals(1, setOfContainers.getAtomContainerCount());
        Assertions.assertEquals(6, setOfContainers.getAtomContainer(0).getAtomCount());
    }

    /**
     * A unit test suite for JUnit: c1ccccc1CN
     *
     *  @cdk.inchi InChI=1/C7H9N/c8-6-7-4-2-1-3-5-7/h1-5H,6,8H2
     *
     *
     */
    @Test
    void testBenzylamine_Aromatic_lookingSymmetry() throws Exception {

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addBond(0, 7, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(molecule));

        StructureResonanceGenerator gRI = new StructureResonanceGenerator(true);
        IAtomContainerSet setOfContainers = gRI.getContainers(molecule);

        Assertions.assertNull(setOfContainers);
    }
}
