/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.io;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 */
class PCCompoundASNReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(PCCompoundASNReaderTest.class);

    @BeforeAll
    static void setup() throws Exception {
        setSimpleChemObjectReader(new PCCompoundASNReader(), "cid1.asn");
    }

    @Test
    void testAccepts() {
        PCCompoundASNReader reader = new PCCompoundASNReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    void testReading() throws Exception {
        String filename = "cid1.asn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        PCCompoundASNReader reader = new PCCompoundASNReader(ins);
        IChemFile cFile = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cFile);
        Assertions.assertEquals(1, containers.size());
        Assertions.assertTrue(containers.get(0) instanceof IAtomContainer);
        IAtomContainer molecule = containers.get(0);
        Assertions.assertNotNull(molecule);

        // check atom stuff
        Assertions.assertEquals(31, molecule.getAtomCount());
        Assertions.assertNotNull(molecule.getAtom(3));
        Assertions.assertEquals("O", molecule.getAtom(3).getSymbol());
        Assertions.assertNotNull(molecule.getAtom(4));
        Assertions.assertEquals("N", molecule.getAtom(4).getSymbol());

        // check bond stuff
        Assertions.assertEquals(30, molecule.getBondCount());
        Assertions.assertNotNull(molecule.getBond(3));
        Assertions.assertEquals(molecule.getAtom(2), molecule.getBond(3).getBegin());
        Assertions.assertEquals(molecule.getAtom(11), molecule.getBond(3).getEnd());

        // some extracted props
        Assertions.assertEquals("InChI=1/C9H17NO4/c1-7(11)14-8(5-9(12)13)6-10(2,3)4/h8H,5-6H2,1-4H3", molecule.getProperty(CDKConstants.INCHI));
        Assertions.assertEquals("CC(=O)OC(CC(=O)[O-])C[N+](C)(C)C", molecule.getProperty(CDKConstants.SMILES));
    }
}
