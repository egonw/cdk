/*
 * Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package org.openscience.cdk.smsd.algorithm.vflib.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.smsd.algorithm.matchers.VFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.VFBondMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IEdge;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;

/**
 * Class for parsing and generating query graph.
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFQueryBuilder implements IQuery {

    private final List<INode>       nodesList;
    private final List<IEdge>       edgesList;
    private final Map<INode, IAtom> nodeBondMap;

    /**
     * Constructor for VF Query Builder
     */
    public VFQueryBuilder() {
        nodesList = new ArrayList<>();
        edgesList = new ArrayList<>();
        nodeBondMap = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IEdge> edges() {
        return Collections.unmodifiableList(edgesList);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<INode> nodes() {
        return Collections.unmodifiableList(nodesList);
    }

    /** {@inheritDoc} */
    @Override
    public INode getNode(int index) {
        return nodesList.get(index);
    }

    /**
     * Return a node for a given atom else return null
     * @param atom
     * @return Node in the graph for a given atom
     */
    public INode getNode(IAtom atom) {

        for (Map.Entry<INode, IAtom> v : nodeBondMap.entrySet()) {
            if (v.getValue().equals(atom)) {
                return v.getKey();
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public IEdge getEdge(int index) {
        return edgesList.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public IEdge getEdge(INode source, INode target) {
        if (source == target) {
            return null;
        }

        NodeBuilder sourceImpl = (NodeBuilder) source;

        for (IEdge edge : sourceImpl.getEdges()) {
            if (edge.getSource() == target || edge.getTarget() == target) {
                return edge;
            }
        }

        return null;
    }

    /**
     * Add and return a node for a query atom
     * @param matcher
     * @param atom
     * @return added Node
     */
    public INode addNode(VFAtomMatcher matcher, IAtom atom) {
        NodeBuilder node = new NodeBuilder(matcher);
        nodesList.add(node);
        nodeBondMap.put(node, atom);
        return node;
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getAtom(INode node) {
        return nodeBondMap.get(node);
    }

    /** {@inheritDoc} */
    @Override
    public int countNodes() {
        return nodesList.size();
    }

    /** {@inheritDoc} */
    @Override
    public int countEdges() {
        return edgesList.size();
    }

    /**
     * Construct and return an edge for a given query and target node
     * @param source
     * @param target
     * @param matcher
     * @return connected edges
     */
    public IEdge connect(INode source, INode target, VFBondMatcher matcher) {
        NodeBuilder sourceImpl = (NodeBuilder) source;
        NodeBuilder targetImpl = (NodeBuilder) target;
        EdgeBuilder edge = new EdgeBuilder(sourceImpl, targetImpl, matcher);

        sourceImpl.addNeighbor(targetImpl);
        targetImpl.addNeighbor(sourceImpl);

        sourceImpl.addEdge(edge);
        targetImpl.addEdge(edge);

        edgesList.add(edge);
        return edge;
    }
}
