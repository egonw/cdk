/* Copyright (C) 2015  The Chemistry Development Kit (CDK) project
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

/**
 * Descriptor result reflecting one or more NaN values.
 *
 * @cdk.githash
 */
public class NaNResult implements IDescriptorResult {

	private static final long serialVersionUID = -1390259129707159229L;

	private int size;

	/**
	 * Creates a single NaN value.
	 */
	public NaNResult() {
        this(1);
    }

	/**
	 * Creates a single NaN value.
	 *
	 * @param size the number of NaN values.
	 */
    public NaNResult(int size) {
		if (size < 1) throw new IllegalAccessError("Size must be one or larger.");
        this.size = size;
    }

    /** {@inheritDoc} */ @Override
    public int length() {
        return this.size;
    }

    /** {@inheritDoc} */ @Override
    public String toString() {
    	if (size == 1) return "NaN";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length(); i++) {
            buf.append("NaN");
            if (i + 1 < length()) buf.append(',');
        }
        return buf.toString();
    }

}
