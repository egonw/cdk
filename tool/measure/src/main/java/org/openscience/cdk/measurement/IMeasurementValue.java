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
 * Interface to represent measurement values that are defined as a single value
 * and an optional error (on the same scale as the measurement and not a percentage).
 *
 * @author egonw
 * @cdk.githash
 */
public interface IMeasurementValue extends IErrorlessMeasurementValue {

	/**
	 * Sets the value for this measurement. The error is optional.
	 * The error must be an absolute value, not a percentage.
	 *
	 * @param value the measured value
	 * @param error the error on the measured value as absolute number
	 */
    public void setValue(double value, double error);

    /**
     * Returns the measure value.
     */
    public double getValue();

    /**
     * Returns the error on the measurement.
     */
    public double getError();

}
