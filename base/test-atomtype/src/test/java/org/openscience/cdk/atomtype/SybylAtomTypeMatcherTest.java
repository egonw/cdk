/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import static org.hamcrest.CoreMatchers.is;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * This class tests the perception of Sybyl atom types, which uses
 * CDK atom type perception and mapping of CDK atom types to Sybyl
 * atom types.
 *
 */
class SybylAtomTypeMatcherTest extends AbstractSybylAtomTypeTest {

    private static final Map<String, Integer> testedAtomTypes = new HashMap<>();

    static {
        // do not complain about a few non-tested atom types
        // so, just mark them as tested
        testedAtomTypes.put("LP", 1);
        testedAtomTypes.put("Du", 1);
        testedAtomTypes.put("Du.C", 1);
        testedAtomTypes.put("Any", 1);
        testedAtomTypes.put("Hal", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("Hev", 1);
        testedAtomTypes.put("X", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("H.t3p", 1);
        testedAtomTypes.put("H.spc", 1);
        testedAtomTypes.put("O.t3p", 1);
        testedAtomTypes.put("O.spc", 1);
    }

    @Test
    void testGetInstance_IChemObjectBuilder() {
        IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());
        Assertions.assertNotNull(matcher);
    }

    @Test
    void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());
        Assertions.assertNotNull(matcher);
        IAtomContainer ethane = TestMoleculeFactory.makeAlkane(2);
        String[] expectedTypes = {"C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, ethane);
    }

    @Test
    void testFindMatchingAtomType_IAtomContainer() throws Exception {
        String filename = "atomtyping.mol2";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());

        // just check consistency; other methods do perception testing
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        IAtomType[] types = matcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            Assertions.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
        }
    }

    @Test
    void testAtomTyping() throws Exception {
        String filename = "atomtyping.mol2";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IAtomContainer molecule = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertNotNull(molecule);
        IAtomContainer reference = molecule.clone();

        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            Assertions.assertEquals(refAtoms.next()
                                            .getAtomTypeName(), atoms.next().getAtomTypeName(), "Perceived atom type does not match atom type in file");
        }
    }

    /**
     * Uses findMatchingAtomType(IAtomContainer, IAtom) type.
     */
    @Test
    void testBenzene() throws Exception {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomTypes(benzene);
        for (IAtomType type : types) {
            Assertions.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

    @Test
    void testAdenine() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAdenine();
        String[] expectedTypes = {"C.ar", "C.ar", "C.ar", "N.ar", "N.ar", "N.ar", "N.ar", "N.3", "C.ar", "C.ar"};
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < expectedTypes.length; i++) {
            assertAtomType(testedAtomTypes, "Incorrect perception for atom " + i, expectedTypes[i], types[i]);
        }
    }

    /**
     * Uses findMatchingAtomType(IAtomContainer) type.
     */
    @Test
    void testBenzene_AtomContainer() throws Exception {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomTypes(benzene);
        for (IAtomType type : types) {
            Assertions.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

    @Test
    void testAtomTyping4() throws Exception {
        String filename = "atomtyping4.mol2";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IAtomContainer molecule = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertNotNull(molecule);
        IAtomContainer reference = molecule.clone();

        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
            Assertions.assertEquals(refAtom.getAtomTypeName(), atoms.next().getAtomTypeName(), "Perceived atom type does not match atom type in file");
        }
    }

    /**
     * @cdk.bug 2445178
     */
    @Test
    void testNonExistingType() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom();
        mol.addAtom(atom);
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
        Assertions.assertNotNull(type);
        org.hamcrest.MatcherAssert.assertThat(type.getAtomTypeName(), is("X"));
    }

    @Test
    void testAtomTyping2() throws Exception {
        String filename = "atomtyping2.mol2";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IAtomContainer molecule = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertNotNull(molecule);
        IAtomContainer reference = molecule.clone();

        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
            Assertions.assertEquals(refAtom.getAtomTypeName(), atoms.next().getAtomTypeName(), "Perceived atom type does not match atom type in file");
        }
    }

    @Test
    void testAtomTyping3() throws Exception {
        String filename = "atomtyping3.mol2";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IAtomContainer molecule = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertNotNull(molecule);
        IAtomContainer reference = molecule.clone();

        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
            Assertions.assertEquals(refAtom.getAtomTypeName(), atoms.next().getAtomTypeName(), "Perceived atom type does not match atom type in file");
        }
    }

    private void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws Exception {
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            atom.setAtomTypeName(null);
            IAtomType matched = matcher.findMatchingAtomType(container, atom);
            if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
    }

    @Test
    @Override
    public void testForDuplicateDefinitions() {
        super.testForDuplicateDefinitions();
    }

    @Test
    void testDummy() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        String[] expectedTypes = {"X"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testEthene() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.2", "C.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testImine() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.2", "N.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testPropyne() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.TRIPLE);
        mol.addBond(2, 1, Order.SINGLE);

        String[] expectedTypes = {"C.1", "C.1", "C.3"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    } 
    
    @Test
    void testAllene() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(2, 1, IBond.Order.DOUBLE);

        String[] expectedTypes = {"C.2", "C.1", "C.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testHalogenatedMethane() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("I"));
        mol.addAtom(new Atom("Br"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.3", "F", "Cl", "I", "Br"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testMnF4() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Mn");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        mol.addAtom(atom);
        atom.setFormalCharge(-1);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+2);
        mol.addAtom(atom3);
        atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        atom4.setFormalCharge(-1);
        mol.addAtom(atom5);
        atom5.setFormalCharge(-1);

        String[] expectedTypes = {"F", "Mn", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testAmide() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"O.2", "C.2", "N.am"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testCarboxylicAcid() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"O.co2", "C.2", "O.co2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testCarboxylate() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("O");
        atom3.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);

        String[] expectedTypes = {"O.co2", "C.2", "O.co2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testMethylAmine() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"N.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testMethylNitro_Charged() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        mol.addAtom(atom);
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("O");
        mol.addAtom(atom3);
        atom3.setFormalCharge(-1);
        IAtom atom4 = new Atom("O");
        mol.addAtom(atom4);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(1, 3, IBond.Order.DOUBLE);

        String[] expectedTypes = {"C.3", "N.pl3", "O.3", "O.2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testAmmonia() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        IAtom atom5 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"H", "N.4", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testMethanol() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"O.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testDMSO() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"O.2", "S.O", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testDMSOO() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 2, Order.DOUBLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(2, 3, Order.SINGLE);
        mol.addBond(2, 4, Order.SINGLE);

        String[] expectedTypes = {"O.2", "O.2", "S.O2", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testCarbokation() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("C");
        atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"H", "C.cat", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testSilicon() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "Si");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a10);
        IAtom a11 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a11);
        IAtom a12 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a12);
        IAtom a13 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a13);
        IAtom a14 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a14);
        IAtom a15 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a15);
        IAtom a16 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a16);
        IAtom a17 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a17);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a2, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a4, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a5, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a5, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a5, a10, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a6, a11, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a6, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a6, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a7, a14, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = mol.getBuilder().newInstance(IBond.class, a7, a15, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = mol.getBuilder().newInstance(IBond.class, a7, a16, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = mol.getBuilder().newInstance(IBond.class, a1, a17, IBond.Order.SINGLE);
        mol.addBond(b16);

        String[] expectedTypes = {"Si", "O.3", "O.3", "O.3", "C.3", "C.3", "C.3", "H", "H", "H", "H", "H", "H", "H",
                "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testThioAmide() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"S.2", "C.2", "N.am"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testSalts() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom = new Atom("Na");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        String[] expectedTypes = new String[]{"Na"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atom = new Atom("K");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"K"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atom = new Atom("Ca");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Ca"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atom = new Atom("Mg");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Mg"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atom = new Atom("Cu");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Cu"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atom = new Atom("Al");
        atom.setFormalCharge(+3);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Al"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testH2S() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h2, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"S.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testFerrocene() throws Exception {
        IAtomContainer ferrocene = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.getAtom(4).setFormalCharge(-1);
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.getAtom(9).setFormalCharge(-1);
        ferrocene.addAtom(new Atom("Fe"));
        ferrocene.getAtom(10).setFormalCharge(+2);
        ferrocene.addBond(0, 1, Order.DOUBLE);
        ferrocene.addBond(1, 2, Order.SINGLE);
        ferrocene.addBond(2, 3, Order.DOUBLE);
        ferrocene.addBond(3, 4, Order.SINGLE);
        ferrocene.addBond(4, 0, Order.SINGLE);
        ferrocene.addBond(5, 6, Order.DOUBLE);
        ferrocene.addBond(6, 7, Order.SINGLE);
        ferrocene.addBond(7, 8, Order.DOUBLE);
        ferrocene.addBond(8, 9, Order.SINGLE);
        ferrocene.addBond(9, 5, Order.SINGLE);

        String[] expectedTypes = new String[]{"C.2", "C.2", "C.2", "C.2", "Any", "C.2", "C.2", "C.2", "C.2", "Any",
                "Fe"};
        assertAtomTypes(testedAtomTypes, expectedTypes, ferrocene);
    }

    @Test
    void testHCN() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.TRIPLE);

        String[] expectedTypes = {"N.1", "C.1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testAniline() throws Exception {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        IAtom nitrogen = benzene.getBuilder().newInstance(IAtom.class, "N");
        benzene.addAtom(nitrogen);
        benzene.addBond(benzene.getBuilder().newInstance(IBond.class, benzene.getAtom(0), nitrogen, IBond.Order.SINGLE));

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomTypes(benzene);
        for (int i = 0; i < 6; i++) {
            assertAtomType(testedAtomTypes, "Incorrect perception for atom " + i, "C.ar", types[i]);
        }
        assertAtomType(testedAtomTypes, "Incorrect perception for atom " + 6, "N.3", types[6]);
    }

    @Test
    void testLithiumMethanoxide() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("Li");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"O.3", "C.3", "Li"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testTinCompound() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Sn");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"C.3", "Sn", "C.3", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testZincChloride() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("Zn"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"Zn", "Cl", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/H2Se/h1H2
     */
    @Test
    void testH2Se() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Se");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, se, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, se, h2, IBond.Order.SINGLE);

        mol.addAtom(se);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"Se", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testPhosphate() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"O.2", "P.3", "O.3", "O.3", "O.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void test_Mo_4() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("Mo");
        mol.addAtom(a1);
        IAtom a2 = new Atom("C");
        mol.addAtom(a2);
        IAtom a3 = new Atom("C");
        mol.addAtom(a3);
        IAtom a4 = new Atom("C");
        mol.addAtom(a4);
        IAtom a5 = new Atom("C");
        mol.addAtom(a5);
        mol.addBond(new Bond(a1, a2, IBond.Order.DOUBLE));
        mol.addBond(new Bond(a1, a3, IBond.Order.DOUBLE));
        mol.addBond(new Bond(a1, a4, IBond.Order.SINGLE));
        mol.addBond(new Bond(a1, a5, IBond.Order.SINGLE));

        String[] expectedTypes = {"Mo", "C.2", "C.2", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testCrth() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // this is made up
        IAtom a1 = new Atom("Cr");
        mol.addAtom(a1);
        for (int i = 0; i < 4; i++) {
            IAtom atom = new Atom("O");
            mol.addAtom(atom);
            mol.addBond(new Bond(a1, atom, IBond.Order.SINGLE));
        }

        String[] expectedTypes = {"Cr.th", "O.3", "O.3", "O.3", "O.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testCroh() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // this is made up, and may be wrong; info on the web is sparse, and PubChem has no
        // octa-coordinate structure; lone pairs involved?
        IAtom a1 = new Atom("Cr");
        mol.addAtom(a1);
        for (int i = 0; i < 6; i++) {
            IAtom atom = new Atom("O");
            mol.addAtom(atom);
            mol.addBond(new Bond(a1, atom, IBond.Order.SINGLE));
        }

        String[] expectedTypes = {"Cr.oh", "O.3", "O.3", "O.3", "O.3", "O.3", "O.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testCooh() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // this is made up, and may be wrong; info on the web is sparse, and PubChem has no
        // octa-coordinate structure; lone pairs involved?
        IAtom a1 = new Atom("Co");
        mol.addAtom(a1);
        for (int i = 0; i < 6; i++) {
            IAtom atom = new Atom("O");
            mol.addAtom(atom);
            mol.addBond(new Bond(a1, atom, IBond.Order.SINGLE));
        }

        String[] expectedTypes = {"Co.oh", "O.3", "O.3", "O.3", "O.3", "O.3", "O.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @AfterAll
    static void testTestedAtomTypes() throws Exception {
        countTestedAtomTypes(testedAtomTypes, factory);
    }
}
