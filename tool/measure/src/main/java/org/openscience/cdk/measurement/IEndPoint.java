/* Copyright (C) 2013-2015  Egon Willighagen <egonw@users.sf.net>
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

import java.net.URI;

/**
 * Chemical, physical, or biological phenomenon that is measured in an assay
 * experiment. Examples include temperature, cell survival, and zeta potential.
 *
 * @author egonw
 */
public interface IEndPoint {

	/**
	 * End points can have a parent, reflecting some hierarchy.
	 *
	 * @return the parent {@link IEndPoint}
	 */
	public IEndPoint getParent();

	/**
	 * Get a string representation of name of this endpoint.
	 *
	 * @return a {@link String} with the name of this endpoint.
	 */
	public String getLabel();

	/**
	 * Get the ontology identifier for this endpoint.
	 *
	 * @return a {@link URI} representing the ontology identifier
	 */
	public URI getURI();
	
}
