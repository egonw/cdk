/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

/**
 *  Class defining a molecular formula object. It maintains
 *   a list of list IMoleculaFormula.<p>
 *
 *  Examples:
 * <ul>
 *   <li><code>[C5H5]-</code></li>
 *   <li><code>C6H6</code></li>
 *   <li><code><sup>12</sup>C<sub>5</sub><sup>13</sup>CH<sub>6</sub></code></li>
 * </ul>
 *
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
public interface IMolecularFormulaSet extends ICDKObject {

    /**
     *  Adds all molecularFormulas in the MolecularFormulaSet to this chemObject.
     *
     * @param  formulaSet  The MolecularFormulaSet
     */
    void add(IMolecularFormulaSet formulaSet);

    /**
     * Adds an IMolecularFormula to this chemObject.
     *
     * @param  formula  The IMolecularFormula to be added to this chemObject
     */
    void addMolecularFormula(IMolecularFormula formula);

    /**
     *
     * Returns the IMolecularFormula at position <code>number</code> in the
     * chemObject.
     *
     * @param  position  The position of the MolecularFormula to be returned.
     * @return           The IMolecularFormula at position <code>number</code> .
     */
    IMolecularFormula getMolecularFormula(int position);

    /**
     * Returns the array of IMolecularFormula of this chemObject.
     *
     * @return    The array of IMolecularFormulas of this chemObject
     *
     * @see #addMolecularFormula(IMolecularFormula)
     */
    Iterable<IMolecularFormula> molecularFormulas();

    /**
     * Returns the number of MolecularFormulas in this IMolecularFormulaSet.
     *
     * @return     The number of MolecularFormulas in this IMolecularFormulaSet
     */
    int size();

    /**
     *  True, if the IMolecularFormulaSet contains the given IMolecularFormula object.
     *
     * @param  formula  The IMolecularFormula this IMolecularFormulaSet is searched for
     * @return          True, if the IMolecularFormulaSet contains the given formula object
     */
    boolean contains(IMolecularFormula formula);

    /**
     *  Removes the given IMolecularFormula from the IMolecularFormulaSet.
     *
     * @param formula  The IMolecularFormula to be removed
     */
    void removeMolecularFormula(IMolecularFormula formula);

    /**
     *  Removes the IMolecularFormula at the given position from the IMolecularFormulaSet.
     *
     * @param  position  The position of the IMolecularFormula to be removed.
     */
    void removeMolecularFormula(int position);

    /**
     * Removes all IMolecularFormula of this IMolecularFormulaSet.
     */
    void removeAllMolecularFormulas();

    /**
     * Clones this IMolecularFormulaSet object and its content.
     *
     * @return    The cloned object
     */
    Object clone() throws CloneNotSupportedException;

}
