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
 @version $Id: IterableAttributeAxis.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $

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

import java.util.Iterator;

import org.probatron.jaxen.ContextSupport;
import org.probatron.jaxen.NamedAccessNavigator;
import org.probatron.jaxen.UnsupportedAxisException;


/**
 * Provide access to the XPath attribute axis.
 * This axis does not include namespace declarations such as 
 * <code>xmlns</code> and <code>xmlns:<i>prefix</i></code>.
 * It does include attributes defaulted from the DTD.
 * 
 * @author Bob McWhirter
 * @author James Strachan
 * @author Stephen Colebourne
 */
public class IterableAttributeAxis extends IterableAxis {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor.
     * 
     * @param value the axis value
     */
    public IterableAttributeAxis(int value) {
        super(value);
    }

    /**
     * Gets an iterator for the attribute axis.
     * 
     * @param contextNode  the current context node to work from
     * @param support  the additional context information
     */
    public Iterator iterator(int contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getAttributeAxisIterator(contextNode);
    }

    /**
     * Gets the iterator for the attribute axis that supports named access.
     * 
     * @param contextNode  the current context node to work from
     * @param support  the additional context information
     * @param localName  the local name of the attributes to return
     * @param namespacePrefix  the prefix of the namespace of the attributes to return
     * @param namespaceURI  the uri of the namespace of the attributes to return
     */
    public Iterator namedAccessIterator(
        Object contextNode,
        ContextSupport support,
        String localName,
        String namespacePrefix,
        String namespaceURI)
            throws UnsupportedAxisException {
                
        NamedAccessNavigator nav = (NamedAccessNavigator) support.getNavigator();
        return nav.getAttributeAxisIterator(contextNode, localName, namespacePrefix, namespaceURI);
    }

    /**
     * Does this axis support named access?
     * 
     * @param support  the additional context information
     * @return true if named access is supported. If not iterator() will be used.
     */
    public boolean supportsNamedAccess(ContextSupport support) {
        return (support.getNavigator() instanceof NamedAccessNavigator);
    }

}
