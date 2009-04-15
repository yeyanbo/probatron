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
 * Created on 06-May-2005
 */
package org.probatron;

import java.util.List;
import java.util.Properties;

import com.griffinbrown.xmltool.XMLToolException;

/**
 * High-level interface onto third-party libraries which provide XPath evaluation.
 * 
 * @author andrews
 *
 * @version $Id: QueryEvaluator.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 */
public interface QueryEvaluator
{
    /**
     * Register a function in a namespace.
     * @param uri
     * @param localName
     * @param function
     */
    void registerFunction( String uri, String localName, Object function );


    /**
     * Register a function without a namespace.
     * @param localName
     * @param function
     */
    void registerFunction( String localName, Object function );


    /**
     * If the function was not registered with a particular namespace, use null for uri and prefix.
     * @param uri
     * @param prefix
     * @param localName
     * @return
     */
    Object getFunction( String uri, String prefix, String localName ) throws XMLToolException;


    void addNamespace( String prefix, String uri );


    


    /**
     * Evaluate the XPath expression against the context node using the prevailing 
     * function, namespace and variable contexts. 
     * @param expr
     */
    Object evaluate( int context, String expr ) throws XMLToolException;


    /**
     * Evaluate a compiled XPath expression against the context node using the prevailing 
     * function, namespace and variable contexts. 
     * @param expr
     */
    Object evaluate( int context, Object compiledExpr ) throws XMLToolException;


    String evaluateAsString( int context, Object compiledExpr ) throws XMLToolException;


    Object compile( String expr ) throws XMLToolException;
    
    Object compileUnoptimized( String expr ) throws XMLToolException;

    void addQuery( Query query );
    void addVariable( Variable variable );
    List getQueries();


    List getVariables();


    /**
     * 
     * @param namespaceURI URI of the variable  
     * @param name name of the variable
     * @return the value of the specified variable as evaluated
     * @throws XMLToolException if the specified variable cannot be resolved or has not been evaluated
     */
    Object getVariableValue( String namespaceURI, String name ) throws XMLToolException;


    /**
     * Directly set the pre-calculated value of a global variable. 
     * @param uri
     * @param localName
     * @param value
     */
    void setGlobalVariableValue( String uri, String localName, Object value ) throws XMLToolException;
    
    /**
     * Directly set the pre-calculated value of a local variable. 
     * @param uri
     * @param localName
     * @param value
     */
    void setLocalVariableValue( String uri, String localName, Object value ) throws XMLToolException;    


    /*N.B. AFTER JAVA 1.5, THIS INTERFACE CAN SIMPLY EXTEND 
     * JAVAX.XML.NAMESPACE.NAMESPACECONTEXT INSTEAD*/

    String getNamespaceURI( String prefix );


    String getPrefix( String namespaceURI );


    void setDocument( Object doc );


    /**
     * 
     * @return the document currently being evaluated
     */
    Object getDocument();


    Properties getProperties();

}