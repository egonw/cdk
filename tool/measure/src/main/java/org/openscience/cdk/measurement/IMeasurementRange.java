/* Copyright (C) 2011-2015  Egon Willighagen <egonw@users.sf.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.measurement;

/**
 * Interface to represent measurement values that are defined as a range.
 *
 * @author egonw
 * @cdk.githash
 */
public interface IMeasurementRange extends IMeasurement {

	/**
	 * Sets the range of values for this measurement.
	 *
	 * @param minimum The minimum value specifying the lower value of the range.
	 * @param maximum The maximum value specifying the upper value of the range.
	 */
    public IMeasurement setValues(Double minimum, Double maximum);

    /**
     * Returns the lower value of the value range.
     *
     * @return a double
     */
	public Double getMinimumValue();

    /**
     * Returns the upper value of the value range.
     *
     * @return a double
     */
	public Double getMaximumValue();

}
