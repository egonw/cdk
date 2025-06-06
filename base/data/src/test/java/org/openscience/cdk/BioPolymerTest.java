/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.test.interfaces.AbstractBioPolymerTest;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of the BioPolymer class.
 *
 *
 * @see org.openscience.cdk.BioPolymer
 */
class BioPolymerTest extends AbstractBioPolymerTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(BioPolymer::new);
    }

    @Test
    void testBioPolymer() {
        IBioPolymer oBioPolymer = new BioPolymer();
        Assertions.assertNotNull(oBioPolymer);
        Assertions.assertEquals(oBioPolymer.getMonomerCount(), 0);

        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IMonomer oMono3 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono3.setMonomerName("GLYA16");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom4 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom5 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");

        oBioPolymer.addAtom(oAtom1);
        oBioPolymer.addAtom(oAtom2, oStrand1);
        oBioPolymer.addAtom(oAtom3, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom4, oMono2, oStrand2);
        oBioPolymer.addAtom(oAtom5, oMono3, oStrand2);
        Assertions.assertNotNull(oBioPolymer.getAtom(0));
        Assertions.assertNotNull(oBioPolymer.getAtom(1));
        Assertions.assertNotNull(oBioPolymer.getAtom(2));
        Assertions.assertNotNull(oBioPolymer.getAtom(3));
        Assertions.assertNotNull(oBioPolymer.getAtom(4));
        Assertions.assertEquals(oAtom1, oBioPolymer.getAtom(0));
        Assertions.assertEquals(oAtom2, oBioPolymer.getAtom(1));
        Assertions.assertEquals(oAtom3, oBioPolymer.getAtom(2));
        Assertions.assertEquals(oAtom4, oBioPolymer.getAtom(3));
        Assertions.assertEquals(oAtom5, oBioPolymer.getAtom(4));

        Assertions.assertNull(oBioPolymer.getMonomer("0815", "A"));
        Assertions.assertNull(oBioPolymer.getMonomer("0815", "B"));
        Assertions.assertNull(oBioPolymer.getMonomer("0815", ""));
        Assertions.assertNull(oBioPolymer.getStrand(""));
        Assertions.assertNotNull(oBioPolymer.getMonomer("TRP279", "A"));
        Assertions.assertEquals(oMono1, oBioPolymer.getMonomer("TRP279", "A"));
        Assertions.assertEquals(oBioPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
        Assertions.assertNotNull(oBioPolymer.getMonomer("HOH", "B"));
        Assertions.assertEquals(oMono2, oBioPolymer.getMonomer("HOH", "B"));
        Assertions.assertEquals(oBioPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
        Assertions.assertEquals(oBioPolymer.getStrand("B").getAtomCount(), 2);
        Assertions.assertEquals(oBioPolymer.getStrand("B").getMonomerCount(), 2);
        Assertions.assertNull(oBioPolymer.getStrand("C"));
        Assertions.assertNotNull(oBioPolymer.getStrand("B"));
    }

}
