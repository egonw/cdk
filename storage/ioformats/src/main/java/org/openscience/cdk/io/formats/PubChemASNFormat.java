/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.formats;

import org.openscience.cdk.tools.DataFeatures;

/**
 */
public class PubChemASNFormat extends SimpleChemFormatMatcher implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    public PubChemASNFormat() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new PubChemASNFormat();
        return myself;
    }

    /** {@inheritDoc} */
    @Override
    public String getFormatName() {
        return "PubChem Compound ASN";
    }

    /** {@inheritDoc} */
    @Override
    public String getMIMEType() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNameExtensions() {
        return new String[]{"asn"};
    }

    /** {@inheritDoc} */
    @Override
    public String getReaderClassName() {
        return "org.openscience.cdk.io.PCCompoundASNReader";
    }

    /** {@inheritDoc} */
    @Override
    public String getWriterClassName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isXMLBased() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getSupportedDataFeatures() {
        return DataFeatures.NONE;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequiredDataFeatures() {
        return DataFeatures.NONE;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 1 && line.startsWith("PC-Compound")) return true;
        return false;
    }
}
