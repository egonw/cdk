/* Copyright (C) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
package org.openscience.cdk.io.program;

import java.io.StringWriter;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.test.io.ChemObjectWriterTest;
import org.openscience.cdk.io.listener.PropertiesListener;

/**
 */
class Mopac7WriterTest extends ChemObjectWriterTest {

    @BeforeAll
    static void setup() throws Exception {
        setChemObjectWriter(new Mopac7Writer());
    }

    @Test
    void testAccepts() throws Exception {
        Mopac7Writer reader = new Mopac7Writer();
        Assertions.assertTrue(reader.accepts(IAtomContainer.class));
    }

    @Test
    void testWrite() throws Exception {
        StringWriter strWriter = new StringWriter();
        Mopac7Writer writer = new Mopac7Writer(strWriter);

        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("Cr"));
        writer.write(mol);
        writer.close();

        Assertions.assertTrue(strWriter.toString().contains("PM3"));
        Assertions.assertTrue(strWriter.toString().contains("Cr "));
        Assertions.assertTrue(strWriter.toString().contains("Generated by"));
    }

    @Test
    void testWriteWithOptimizationTrue() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("Cr"));

        StringWriter strWriter = new StringWriter();
        Mopac7Writer writer = new Mopac7Writer(strWriter);
        Properties customizations = new Properties();
        customizations.setProperty("optimize", "true");
        writer.addChemObjectIOListener(new PropertiesListener(customizations));
        writer.write(mol);
        writer.close();
        Assertions.assertTrue(strWriter.toString().contains("PRECISE"));
    }

    @Test
    void testWriteWithOptimizationFalse() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("Cr"));

        StringWriter strWriter = new StringWriter();
        Mopac7Writer writer = new Mopac7Writer(strWriter);
        Properties customizations = new Properties();
        customizations.setProperty("Optimize", "false");
        writer.addChemObjectIOListener(new PropertiesListener(customizations));
        writer.write(mol);
        writer.close();
        Assertions.assertFalse(strWriter.toString().contains("PRECISE"));
    }

    @Test
    void testWriteWithCustomCommands() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("Cr"));

        StringWriter strWriter = new StringWriter();
        Mopac7Writer writer = new Mopac7Writer(strWriter);
        Properties customizations = new Properties();
        customizations.setProperty("Commands", "THIS IS NOT GOING TO WORK");
        writer.addChemObjectIOListener(new PropertiesListener(customizations));
        writer.write(mol);
        writer.close();
        Assertions.assertTrue(strWriter.toString().contains("THIS IS NOT GOING TO WORK"));
    }

    @Test
    void testChargedCompounds() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom aluminum = new Atom("Al");
        aluminum.setFormalCharge(+3);
        mol.addAtom(aluminum);

        StringWriter strWriter = new StringWriter();
        Mopac7Writer writer = new Mopac7Writer(strWriter);
        writer.write(mol);
        writer.close();
        Assertions.assertTrue(strWriter.toString().contains("CHARGE=3"));
    }

}
