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

package org.probatron.jaxen;

/*
 @version $Id: ContextSupport.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $

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

import java.io.Serializable;

/** Supporting context information for resolving
 *  namespace prefixes, functions, and variables.
 * 
 *  <p>
 *  <strong>NOTE:</strong> This class is not typically used directly,
 *  but is exposed for writers of implementation-specific
 *  XPath packages.
 *  </p>
 *
 *  @see com.xmlprobe.jaxen.dom4j.Dom4jXPath XPath for dom4j
 *  @see com.xmlprobe.jaxen.jdom.JDOMXPath   XPath for JDOM
 *  @see com.xmlprobe.jaxen.dom.DOMXPath     XPath for W3C DOM
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version @version $Id: ContextSupport.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 */
public class ContextSupport implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4494082174713652559L;

    /** Function context. */
    private transient FunctionContext functionContext;
    
    /** Namespace context. */
    private NamespaceContext namespaceContext;

    /** Variable context. */
    private VariableContext variableContext;
    
    /** Model navigator. */
    private Navigator navigator;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------
    
    /** Construct an empty <code>ContextSupport</code>.
     */
    public ContextSupport()
    {
        // intentionally left blank
    }

    /** Create a new ContextSupport object.
     *
     *  @param namespaceContext the NamespaceContext
     *  @param functionContext the FunctionContext
     *  @param variableContext the VariableContext
     *  @param navigator the model navigator
     */
    public ContextSupport(NamespaceContext namespaceContext,
                          FunctionContext functionContext,
                          VariableContext variableContext,
                          Navigator navigator)
    {
        setNamespaceContext( namespaceContext );
        setFunctionContext( functionContext );
        setVariableContext( variableContext );

        this.navigator = navigator;
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /** Set the <code>NamespaceContext</code>.
     *
     *  @param namespaceContext the namespace context
     */
    public void setNamespaceContext(NamespaceContext namespaceContext)
    {
        this.namespaceContext = namespaceContext;
    }

    /** Retrieve the <code>NamespaceContext</code>.
     *
     *  @return the namespace context
     */
    public NamespaceContext getNamespaceContext()
    {
        return this.namespaceContext;
    }

    /** Set the <code>FunctionContext</code>.
     *
     *  @param functionContext the function context
     */
    public void setFunctionContext(FunctionContext functionContext)
    {
        this.functionContext  = functionContext;
    }

    /** Retrieve the <code>FunctionContext</code>.
     *
     *  @return the function context
     */
    public FunctionContext getFunctionContext()
    {
        return this.functionContext;
    }

    /** Set the <code>VariableContext</code>.
     *
     *  @param variableContext the variable context
     */
    public void setVariableContext(VariableContext variableContext)
    {
        this.variableContext  = variableContext;
    }

    /** Retrieve the <code>VariableContext</code>.
     *
     *  @return the variable context
     */
    public VariableContext getVariableContext()
    {
        return this.variableContext;
    }

    /** Retrieve the <code>Navigator</code>.
     *
     *  @return the navigator
     */
    public Navigator getNavigator()
    {
        return this.navigator;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Translate a namespace prefix to its URI.
     *
     *  @param prefix The prefix
     *
     *  @return the namespace URI mapped to the prefix
     */
    public String translateNamespacePrefixToUri(String prefix)
    {
        
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        NamespaceContext context = getNamespaceContext();

        if ( context != null )
        {
            return context.translateNamespacePrefixToUri( prefix );
        }

        return null;
    }

    /** Retrieve a variable value.
     *
     *  @param namespaceURI the function namespace URI
     *  @param prefix the function prefix
     *  @param localName the function name
     *
     *  @return the variable value.
     *
     *  @throws UnresolvableException if unable to locate a bound variable.
     */
    public Object getVariableValue( String namespaceURI,
                                    String prefix,
                                    String localName )
        throws UnresolvableException
    {
        VariableContext context = getVariableContext();

        if ( context != null )
        {
            return context.getVariableValue( namespaceURI, prefix, localName );
        }
        else
        {
            throw new UnresolvableException( "No variable context installed" );
        }
    }

    /** Retrieve a <code>Function</code>.
     *
     *  @param namespaceURI the function namespace URI
     *  @param prefix the function prefix
     *  @param localName the function name
     *
     *  @return the function object
     *
     *  @throws UnresolvableException if unable to locate a bound function
     */
    public Function getFunction( String namespaceURI,
                                 String prefix,
                                 String localName )
        throws UnresolvableException
    {
        FunctionContext context = getFunctionContext();

        if ( context != null )
        {
            return context.getFunction( namespaceURI, prefix, localName );
        }
        else
        {
            throw new UnresolvableException( "No function context installed" );
        }
    }
    
}
