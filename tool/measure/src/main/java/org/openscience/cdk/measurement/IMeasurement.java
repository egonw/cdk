/* Copyright (C) 2011-2013  Egon Willighagen <egonw@users.sf.net>
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

import java.util.List;

import com.github.jqudt.Unit;

/**
 * Interface for experimentally measured properties of chemical entities.
 * Each measurement is a combination of a string representation of the
 * value, the endpoint which was measured (e.g. temperature, or percentage
 * cell survival), and the unit of the measurement. For the latter, the
 * jQUDT library is used. Measurements were made under certain conditions
 * (e.g. room temperature, or a particular pH). These conditions are
 * encoded as a list of measurements.
 * 
 * <p>This interface, however, does not specify how the value is stored,
 * as there are two options commonly used: a single value (with or
 * without error) and a value range. For these purposes, two interfaces
 * extend this interface: {@link IMeasurementValue} and
 * {@link IMeasurementRange}. Class should implement either of those
 * two interfaces instead of this one.
 *
 * @cdk.githash
 */
public interface IMeasurement {

	/**
	 * Sets the endpoint of an measurement indicates what was measure.
	 *
	 * @param endPoint a user defined endpoint implemented as a {@link IEndPoint} class.
	 */
	public void setEndPoint(IEndPoint endPoint);

	/**
	 * Returns the endpoint for this measurement.
	 *
	 * @return the endpoint of this measurement.
	 */
	public IEndPoint getEndPoint();

	/**
	 * Sets the unit for this measurement. The unit is a class extending the
	 * {@link Unit} class from the jQUDT library.
	 *
	 * @param unit
	 */
	public void setUnit(Unit unit);

	/**
	 * Returns the unit of this measurement.
	 *
	 * @return
	 */
	public Unit getUnit();

	// FIXME: what was this about??
	public String getString();

	/**
	 * Return the list of conditions under which this measurement was made.
	 *
	 * @return an {@link List} of {@link IMeasurement}s.
	 */
	public List<IMeasurement> getConditions();

	/**
	 * Sets the list of conditions under which this measurement was made.
	 *
	 * @param conditions the {@link List} of {@link IMeasurement}s.
	 */
	public void setConditions(List<IMeasurement> conditions);

}
