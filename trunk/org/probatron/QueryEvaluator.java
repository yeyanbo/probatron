/*
 * Copyright 2009 Griffin Brown Digital Publishing Ltd All rights reserved.
 * 
 * This file is part of Probatron.
 * 
 * Probatron is free software: you can redistribute it and/or modify it under the terms of the
 * Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * Probatron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the Affero General Public License for more details.
 * 
 * You should have received a copy of the Affero General Public License along with Probatron. If
 * not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Created on 06-May-2005
 */
package org.probatron;

import java.util.List;
import java.util.Properties;

import com.griffinbrown.xmltool.XMLToolException;

/**
 * <p>High-level interface onto third-party libraries which provide evaluation of queries against 
 * XML documents.</p>
 *  
 * @author andrews
 * 
 * @version $Id: QueryEvaluator.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 */
public interface QueryEvaluator
{
    /**
     * Register a function in a namespace.
     * @param uri namespace URI associated with the function
     * @param localName local name of the function
     * @param function object representing the function to be registered
     */
    void registerFunction( String uri, String localName, Object function );


    /**
     * Register a function without a namespace.
     * @param localName local name of the function
     * @param function object representing the function to be registered
     */
    void registerFunction( String localName, Object function );


    /**
     * Accesses a function registered with the evaluator.
     * If the function was not registered with a particular namespace, use <code>null</code> for uri and prefix.
     * @param uri the namespace URI associated with the function
     * @param prefix the namespace prefix associated with the function
     * @param localName the local name of the function
     * @return an object  representing the function, or <code>null</code> if the function does not exist
     * @throws XMLToolException if the function cannot be retrieved
     */
    Object getFunction( String uri, String prefix, String localName ) throws XMLToolException;


    /**
     * Adds a namespace to the evaluator.
     * @param prefix the namespace prefix
     * @param uri the namespace URI
     */
    void addNamespace( String prefix, String uri );


    /**
     * Evaluates the query against the context node using the prevailing 
     * function, namespace and variable contexts. 
     * @param query the query to evaluate
     * @param context the context node
     * @return an object representing the results of the evaluation
     */
    Object evaluate( int context, String query ) throws XMLToolException;


    /**
     * Evaluates a compiled query against the context node using the prevailing 
     * function, namespace and variable contexts. 
     * @param context the context node
     * @param compiledQuery the compiled query
     * @return an object representing the results of the evaluation
     */
    Object evaluate( int context, Object compiledQuery ) throws XMLToolException;

    /**
     * Evaluates the string value of a compiled query against the context node 
     * using the prevailing function, namespace and variable contexts.
     * 
     *   @param context the context node
     *   @param compiledQuery the compiled query
     *   @return string value of the evaluated query, or <code>null</code> if the query cannot be 
     *   evaluated
     */
    String evaluateAsString( int context, Object compiledQuery ) throws XMLToolException;


    /**
     * Compiles the query passed in using the prevailing 
     * function, namespace and variable contexts. 
     * @param expr the query to compile
     * @return an object representing the compiled query
     * @throws XMLToolException if the query cannot be compiled
     */
    Object compile( String expr ) throws XMLToolException;


    /**
     * Adds a query to the evaluator.
     * @param query the query to add
     */
    void addQuery( Query query );


    /**
     * Adds a variable to the evaluator.
     * @param variable the variable to add
     */
    void addVariable( Variable variable );

    /**
     * Accesses the queries registered with this evaluator.
     * @return the registered queries  
     */
    List getQueries();

    /**
     * Accesses the variables registered with this evaluator.
     * @return the registered variables  
     */
    List getVariables();


    /**
     * Accesses the value of a variable registered with this evaluator.
     * @param namespaceURI URI of the variable  
     * @param name name of the variable
     * @return the value of the specified variable as evaluated
     * @throws XMLToolException if the specified variable cannot be resolved or has not been evaluated
     */
    Object getVariableValue( String namespaceURI, String name ) throws XMLToolException;


    /**
     * Directly sets the pre-calculated value of a global variable. 
     * @param uri namespace URI for the variable
     * @param localName local name of the variable
     * @param value object representing the value of the variable
     */
    void setGlobalVariableValue( String uri, String localName, Object value )
            throws XMLToolException;


    /**
     * Directly sets the pre-calculated value of a local variable. 
     * @param uri namespace URI for the variable
     * @param localName local name of the variable
     * @param value object representing the value of the variable
     */
    void setLocalVariableValue( String uri, String localName, Object value )
            throws XMLToolException;

    /**
     * Accesses a namespace URI associated with the prefix passed in.
     * @param prefix the namespace prefix
     * @return the namespace URI, or <code>null</code> if none exists for the prefix
     */
    String getNamespaceURI( String prefix );

    /**
     * Accesses a namespace prefix associated with the URI passed in.
     * @param namespaceURI the namespace URI
     * @return the namespace prefix, or <code>null</code> if none exists for the URI
     */
    String getPrefix( String namespaceURI );

    /**
     * Sets the document for evaluation.
     * @param doc the document for evaluation
     */
    void setDocument( Object doc );


    /**
     * Accesses the document against which queries are being evaluated. 
     * @return the document currently being evaluated
     */
    Object getDocument();

    /**
     * Accesses the properties for this evaluator.
     * @return properties configured for this evaluator
     */
    Properties getProperties();

}