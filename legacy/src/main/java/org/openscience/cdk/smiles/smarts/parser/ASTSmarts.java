/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */
package org.openscience.cdk.smiles.smarts.parser;

/**
 * An AST node. It represents a sub structure expression in smarts. The
 * 'componentId' indicates whether the component level grouping should be
 * applied and which component this expression belongs.
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.keyword SMARTS AST
 */
@Deprecated
class ASTSmarts extends SimpleNode {

    /** Component level grouping. */
    private int componentId;

    /**
     * Creates a new instance.
     */
    public ASTSmarts(int id) {
        super(id);
    }

    /**
     * Creates a new instance.
     */
    public ASTSmarts(SMARTSParser p, int id) {
        super(p, id);
    }

    /**
     * Get the component id of this SMARTS expression. The default component is
     * '0' which means 'ungrouped'.
     *
     * @return the component id
     */
    public int componentId() {
        return componentId;
    }

    /**
     * Indicate that component-level grouping should applied and this expression
     * belongs to the specified component.
     *
     * @param id the component id
     */
    public void setComponentId(int id) {
        componentId = id;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.smiles.smarts.parser.SimpleNode#jjtAccept(org.openscience
     * .cdk.smiles.smarts.parser.SMARTSParserVisitor, java.lang.Object)
     */
    @Override
    public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
