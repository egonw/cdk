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
package org.openscience.cdk.qsar.result;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class NaNResultTest extends CDKTestCase {

    public NaNResultTest() {
        super();
    }

    @Test
    public void NaNResult_int() {
    	NaNResult result = new NaNResult(5);
        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.length());
    }

    @Test
    public void NaNArrayResult() {
    	NaNResult result = new NaNResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length());
    }

    @Test
    public void testToString() {
    	NaNResult result = new NaNResult();
        Assert.assertNotNull(result);
        Assert.assertEquals("NaN", result.toString());
    }

    @Test
    public void testToString_Sized() {
    	NaNResult result = new NaNResult(3);
        Assert.assertNotNull(result);
        Assert.assertEquals("NaN,NaN,NaN", result.toString());
    }

}
