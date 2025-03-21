/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
public class MDLV3000Format extends SimpleChemFormatMatcher implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    public MDLV3000Format() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new MDLV3000Format();
        return myself;
    }

    /** {@inheritDoc} */
    @Override
    public String getFormatName() {
        return "MDL Mol/SDF V3000";
    }

    /** {@inheritDoc} */
    @Override
    public String getMIMEType() {
        return "chemical/x-mdl-molfile";
    }

    /** {@inheritDoc} */
    @Override
    public String getPreferredNameExtension() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNameExtensions() {
        return new String[0];
    }

    /** {@inheritDoc} */
    @Override
    public String getReaderClassName() {
        return "org.openscience.cdk.io.MDLV3000Reader";
    }

    /** {@inheritDoc} */
    @Override
    public String getWriterClassName() {
        return "org.openscience.cdk.io.MDLV3000Writer";
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(int lineNumber, String line) {
        return lineNumber == 4 && (line.contains("v3000") || line.contains("V3000"));
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
}
