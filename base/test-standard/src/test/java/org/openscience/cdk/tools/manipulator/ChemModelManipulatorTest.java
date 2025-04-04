/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 */
class ChemModelManipulatorTest extends CDKTestCase {

    private final static ILoggingTool logger      = LoggingToolFactory
                                                          .createLoggingTool(ChemModelManipulatorTest.class);

    private IAtomContainer                    molecule1   = null;
    private IAtomContainer                    molecule2   = null;
    private IAtom                             atomInMol1  = null;
    private IBond                             bondInMol1  = null;
    private IAtom                             atomInMol2  = null;
    private IAtomContainerSet                 moleculeSet = null;
    private IReaction                         reaction    = null;
    private IReactionSet                      reactionSet = null;
    private IChemModel                        chemModel   = null;

    ChemModelManipulatorTest() {
        super();
    }

    @BeforeEach
    void setUp() {
        molecule1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomInMol1 = new Atom("Cl");
        atomInMol1.setCharge(-1.0);
        atomInMol1.setFormalCharge(-1);
        atomInMol1.setImplicitHydrogenCount(1);
        molecule1.addAtom(atomInMol1);
        molecule1.addAtom(new Atom("Cl"));
        bondInMol1 = new Bond(atomInMol1, molecule1.getAtom(1));
        molecule1.addBond(bondInMol1);
        molecule2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomInMol2 = new Atom("O");
        atomInMol2.setImplicitHydrogenCount(2);
        molecule2.addAtom(atomInMol2);
        moleculeSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        moleculeSet.addAtomContainer(molecule1);
        moleculeSet.addAtomContainer(molecule2);
        reaction = new Reaction();
        reaction.addReactant(molecule1);
        reaction.addProduct(molecule2);
        reactionSet = new ReactionSet();
        reactionSet.addReaction(reaction);
        chemModel = new ChemModel();
        chemModel.setMoleculeSet(moleculeSet);
        chemModel.setReactionSet(reactionSet);
    }

    @Test
    void testGetAllAtomContainers_IChemModel() throws Exception {
        String filename = "a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);

        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemModel chemFile = (ChemModel) reader.read((ChemObject) new ChemModel());
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemModelManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
    }

    @Test
    void testGetAllAtomContainers_IChemModel_WithReactions() throws Exception {
        String filename = "0024.stg02.rxn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);

        MDLRXNV2000Reader reader = new MDLRXNV2000Reader(ins, Mode.STRICT);
        ChemModel chemFile = (ChemModel) reader.read((ChemObject) new ChemModel());
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemModelManipulator.getAllAtomContainers(chemFile);

        Assertions.assertEquals(2, containersList.size());
    }

    @Test
    void testNewChemModel_IAtomContainer() {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("C"));
        IChemModel model = ChemModelManipulator.newChemModel(ac);
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(ac.getAtomCount(), mol.getAtomCount());
    }

    @Test
    void testGetAtomCount_IChemModel() {
        int count = ChemModelManipulator.getAtomCount(chemModel);
        Assertions.assertEquals(6, count);
    }

    @Test
    void testGetBondCount_IChemModel() {
        int count = ChemModelManipulator.getBondCount(chemModel);
        Assertions.assertEquals(2, count);
    }

    @Test
    void testRemoveElectronContainer_IChemModel_IElectronContainer() {
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol1.addAtom(new Atom("Cl"));
        mol1.addAtom(new Atom("Cl"));
        IBond bond1 = new Bond(mol1.getAtom(0), mol1.getAtom(1));
        mol1.addBond(bond1);
        IAtomContainer mol2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol2.addAtom(new Atom("I"));
        mol2.addAtom(new Atom("I"));
        IBond bond2 = new Bond(mol2.getAtom(0), mol2.getAtom(1));
        mol2.addBond(bond2);
        IAtomContainerSet molSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        molSet.addAtomContainer(mol1);
        IReaction r = new Reaction();
        r.addProduct(mol2);
        IReactionSet rSet = new ReactionSet();
        rSet.addReaction(r);
        IChemModel model = new ChemModel();
        model.setMoleculeSet(molSet);
        model.setReactionSet(rSet);
        IBond otherBond = new Bond();
        Assertions.assertEquals(2, ChemModelManipulator.getBondCount(model));
        ChemModelManipulator.removeElectronContainer(model, otherBond);
        Assertions.assertEquals(2, ChemModelManipulator.getBondCount(model));
        ChemModelManipulator.removeElectronContainer(model, bond1);
        Assertions.assertEquals(1, ChemModelManipulator.getBondCount(model));
        ChemModelManipulator.removeElectronContainer(model, bond2);
        Assertions.assertEquals(0, ChemModelManipulator.getBondCount(model));
    }

    @Test
    void testRemoveAtomAndConnectedElectronContainers_IChemModel_IAtom() {
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom("Cl");
        mol1.addAtom(atom1);
        mol1.addAtom(new Atom("Cl"));
        IBond bond1 = new Bond(mol1.getAtom(0), mol1.getAtom(1));
        mol1.addBond(bond1);
        IAtomContainer mol2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom2 = new Atom("I");
        mol2.addAtom(atom2);
        mol2.addAtom(new Atom("I"));
        IBond bond2 = new Bond(mol2.getAtom(0), mol2.getAtom(1));
        mol2.addBond(bond2);
        IAtomContainerSet molSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        molSet.addAtomContainer(mol1);
        IReaction r = new Reaction();
        r.addProduct(mol2);
        IReactionSet rSet = new ReactionSet();
        rSet.addReaction(r);
        IChemModel model = new ChemModel();
        model.setMoleculeSet(molSet);
        model.setReactionSet(rSet);
        IAtom otherAtom = new Atom("Cl");
        Assertions.assertEquals(2, ChemModelManipulator.getBondCount(model));
        Assertions.assertEquals(4, ChemModelManipulator.getAtomCount(model));
        ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, otherAtom);
        Assertions.assertEquals(2, ChemModelManipulator.getBondCount(model));
        Assertions.assertEquals(4, ChemModelManipulator.getAtomCount(model));
        ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atom1);
        Assertions.assertEquals(1, ChemModelManipulator.getBondCount(model));
        Assertions.assertEquals(3, ChemModelManipulator.getAtomCount(model));
        ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atom2);
        Assertions.assertEquals(0, ChemModelManipulator.getBondCount(model));
        Assertions.assertEquals(2, ChemModelManipulator.getAtomCount(model));
    }

    @Test
    void testSetAtomProperties_IChemModel_Object_Object() {
        String key = "key";
        String value = "value";
        ChemModelManipulator.setAtomProperties(chemModel, key, value);
        Assertions.assertEquals(value, atomInMol1.getProperty(key));
        Assertions.assertEquals(value, atomInMol2.getProperty(key));
    }

    @Test
    void testGetRelevantAtomContainer_IChemModel_IAtom() {
        IAtomContainer ac1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInMol1);
        Assertions.assertEquals(molecule1, ac1);
        IAtomContainer ac2 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInMol2);
        Assertions.assertEquals(molecule2, ac2);
    }

    @Test
    void testGetRelevantAtomContainer_IChemModel_IBond() {
        IAtomContainer ac1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, bondInMol1);
        Assertions.assertEquals(molecule1, ac1);
    }

    @Test
    void testGetAllChemObjects_IChemModel() {
        List<IChemObject> list = ChemModelManipulator.getAllChemObjects(chemModel);
        Assertions.assertEquals(5, list.size());
        //int atomCount = 0; // not traversed
        //int bondCount = 0; // not traversed
        int molCount = 0;
        int molSetCount = 0;
        int reactionCount = 0;
        int reactionSetCount = 0;
        for (Object o : list) {
            //if (o instanceof IAtom) ++atomCount;
            //if (o instanceof IBond) ++bondCount;
            if (o instanceof IAtomContainer)
                ++molCount;
            else if (o instanceof IAtomContainerSet)
                ++molSetCount;
            else if (o instanceof IReaction)
                ++reactionCount;
            else if (o instanceof IReactionSet)
                ++reactionSetCount;
            else
                Assertions.fail("Unexpected Object of type " + o.getClass());
        }
        //Assert.assertEquals(3, atomCount);
        //Assert.assertEquals(1, bondCount);
        Assertions.assertEquals(2, molCount);
        Assertions.assertEquals(1, molSetCount);
        Assertions.assertEquals(1, reactionCount);
        Assertions.assertEquals(1, reactionSetCount);
    }

    @Test
    void testCreateNewMolecule_IChemModel() {
        IChemModel model = new ChemModel();
        IAtomContainer ac = ChemModelManipulator.createNewMolecule(model);
        Assertions.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());
        Assertions.assertEquals(ac, model.getMoleculeSet().getAtomContainer(0));
    }

    @Test
    void testGetRelevantReaction_IChemModel_IAtom() {
        IReaction r = ChemModelManipulator.getRelevantReaction(chemModel, atomInMol1);
        Assertions.assertNotNull(r);
        Assertions.assertEquals(reaction, r);
    }

    @Test
    void testGetAllIDs_IChemModel() {
        Assertions.assertEquals(0, ChemModelManipulator.getAllIDs(chemModel).size());
        IDCreator.createIDs(chemModel);
        List<String> allIDs = ChemModelManipulator.getAllIDs(chemModel);
        Assertions.assertEquals(16, ChemModelManipulator.getAllIDs(chemModel).size());
        Set<String> uniq = new HashSet<>(allIDs);
        Assertions.assertEquals(10, uniq.size());
    }

    /**
     * @cdk.bug 3530861
     */
    @Test
    void testGetRelevantAtomContainer_NonExistentAtom() {
        IChemModel model = new org.openscience.cdk.silent.ChemModel();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ChemModelManipulator.getRelevantAtomContainer(model, new org.openscience.cdk.silent.Atom());
        });
    }

}
