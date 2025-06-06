/* Copyright (C) 2021  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.dict;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the DefaultHanlder class.
 *
 *
 * @see org.openscience.cdk.dict.DefaultHanlder
 */
class DictionaryTest extends CDKTestCase {

    @Test
    void testDefaultHanlder() {
        DictionaryDatabase db = new DictionaryDatabase();
        Dictionary dict = db.getDictionary("chemical"); // this dictionary needs the DefaultHanlder
        Entry entry = dict.getEntry("ionPair".toLowerCase());
        Assertions.assertNotNull(entry);
    }

}
