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
 @version $Id: IterableAxis.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $

 Copyright 2003 The Werken Company. All Rights Reserved.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  * Neither the name of the Jaxen Project nor the names of its
    contributors may be used to endorse or promote products derived 
    from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.probatron.jaxen.expr.iter;

import java.io.Serializable;
import java.util.Iterator;

import org.probatron.jaxen.ContextSupport;
import org.probatron.jaxen.UnsupportedAxisException;


/**
 * Provide access to the XPath axes.
 * 
 * @author Bob McWhirter
 * @author James Strachan
 * @author Stephen Colebourne
 */
public abstract class IterableAxis implements Serializable {
    
    /** The axis type */
    private int value;

    /**
     * Constructor.
     * 
     * @param axisValue
     */
    public IterableAxis(int axisValue) {
        this.value = axisValue;
    }

    /**
     * Gets the axis value.
     * 
     * @return the axis value
     */
    public int value() {
        return this.value;
    }

    /**
     * Gets the iterator for a specific XPath axis.
     * 
     * @param contextNode  the current context node to work from
     * @param support  the additional context information
     * @return an iterator for the axis 
     * @throws UnsupportedAxisException
     */
    public abstract Iterator iterator(int contextNode, ContextSupport support) throws UnsupportedAxisException;

    /**
     * Gets the iterator for a specific XPath axis that supports named access.
     *
     * @param contextNode  the current context node to work from
     * @param support  the additional context information
     * @param localName  the local name of the nodes to return
     * @param namespacePrefix  the prefix of the namespace of the nodes to return
     * @param namespaceURI  the URI of the namespace of the nodes to return
     */
    public Iterator namedAccessIterator(
        int contextNode,
        ContextSupport support,
        String localName,
        String namespacePrefix,
        String namespaceURI)
            throws UnsupportedAxisException {
                
        throw new UnsupportedOperationException("Named access unsupported");
    }

    /**
     * Does this axis support named access?
     * 
     * @param support  the additional context information
     * @return true if named access supported. If not iterator() will be used
     */
    public boolean supportsNamedAccess(ContextSupport support) {
        return false;
    }

}
