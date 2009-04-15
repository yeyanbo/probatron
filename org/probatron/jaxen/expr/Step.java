/*
 * Copyright 2009 Griffin Brown Digital Publishing Ltd
 * All rights reserved.
 *
 * This file is part of Probatron.
 *
 * Probatron is free software: you can redistribute it and/or modify
 * it under the terms of the Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Probatron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero General Public License for more details.
 *
 * You should have received a copy of the Affero General Public License
 * along with Probatron.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This software contains, in modified form, source code originally created by 
 * the Jaxen Project.
 *
 * The copyright notice, conditions and disclaimer pertaining to that 
 * distribution are included below.
 *
 * Jaxen distributions are available from <http://jaxen.org/>.
 */  

/*
 * $Header: /TEST/probatron/org/probatron/jaxen/expr/Step.java,v 1.1 2009/02/11 08:52:56 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/02/11 08:52:56 $
 *
 * ====================================================================
 *
 * Copyright 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   * Neither the name of the Jaxen Project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * @version $Id: Step.java,v 1.1 2009/02/11 08:52:56 GBDP\andrews Exp $
 */

package org.probatron.jaxen.expr;

import java.util.Iterator;

import org.probatron.jaxen.Context;
import org.probatron.jaxen.ContextSupport;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.UnsupportedAxisException;

import com.griffinbrown.shail.util.ShailList;

/**
 * <p>Represents a location step in a LocationPath. The node-set selected by 
 * the location step is the node-set that results from generating an initial 
 * node-set from the axis and node-test, and then filtering that node-set by 
 * each of the predicates in turn.</p>
 * 
 * <p>
 * The initial node-set consists of the nodes having the relationship to the 
 * context node specified by the axis, and having the node type and expanded-name 
 * specified by the node test.</p>
 */
public interface Step extends Predicated
{

    /**
     * Performs the node-test part of evaluating the step for the given node
     * (which must be on the axis).
     * 
     * @return true if the node matches this step; false if it doesn't
     */    
    boolean matches(int node,
                    ContextSupport contextSupport) throws JaxenException;

    /**
     * Returns a <code>String</code> containing the XPath expression.
     * 
     * @return the text form of this step
     */
    String getText();

    /**
     * Simplifies the XPath step. In practice, this is usually a noop.
     * Jaxen does not currently perform any simplification.
     */
    void simplify();

    /**
     * Get an identifier for the current axis.
     * 
     * @return the axis identifier
     * @see org.probatron.jaxen.saxpath.Axis
     */
    public int getAxis();

    /**
     * Get an Iterator for the current axis starting in the given contextNode.
     * 
     * @param contextNode the node from which to follow this step
     * @param support the remaining context for the traversal
     * @return an iterator over the nodes along the axis
     * @throws UnsupportedAxisException if the navigator does not support this step's axis
     * 
     */
    Iterator axisIterator(int contextNode,
                          ContextSupport support) throws UnsupportedAxisException;
    

    /**
     * For each node in the given context calls matches() for every node on the
     * axis, then filters the result by each of the predicates.
     * 
     * @return a list of matching nodes
     */
    ShailList evaluate(Context context) throws JaxenException;

}

