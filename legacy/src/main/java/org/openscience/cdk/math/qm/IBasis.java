/* Copyright (C) 2001-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.math.qm;

import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;

/**
 * A basis set must implement this class.
 *
 * @author Stephan Michels &lt;stephan@vern.chem.tu-berlin.de&gt;
 * @cdk.created 2001-07-02
 */
public interface IBasis {

    /**
     * Gets the number of base vectors
     */
    int getSize();

    /**
     * Gets the dimension of the volume, which describes the base.
     */
    double getMinX();

    /**
     * Gets the dimension of the volume, which describes the base.
     */
    double getMaxX();

    /**
     * Gets the dimension of the volume, which describes the base.
     */
    double getMinY();

    /**
     * Gets the dimension of the volume, which describes the base.
     */
    double getMaxY();

    /**
     * Gets the dimension of the volume, which describes the base.
     */
    double getMinZ();

    /**
     * Gets the dimension of the volume, which describes the base.
     */
    double getMaxZ();

    /**
     * Calculates the function value an (x,y,z).
     * @param index The number of the base
     */
    double getValue(int index, double x, double y, double z);

    /**
     * Calculates the function values.
     * @param index The number of the base
     */
    Vector getValues(int index, Matrix x);

    /**
     * Calculate the overlap integral S = &lt;phi_i|phi_j&gt;.
     *
     * @param i Index of the first base
     * @param j Index of the second base
     */
    double calcS(int i, int j);

    /**
     * Calculates the impulse J = -&lt;d/dr chi_i | d/dr chi_j&gt;.
     *
     * @param i Index of the first base
     * @param j Index of the second base
     */
    double calcJ(int i, int j);

    /**
     * Calculates the potential V = &lt;chi_i | 1/r | chi_j&gt;.
     *
     * @param i Index of the first base
     * @param j Index of the second base
     */
    double calcV(int i, int j);

    /**
     * Calculates a two eletron fout center integral
     * I = &lt;chi_i chi_j | 1/r12 | chi_k chi_l &gt;.
     *
     * @param i Index of the first base
     * @param j Index of the second base
     * @param k Index of the third base
     * @param l Index of the fourth base
     */
    double calcI(int i, int j, int k, int l);
}
