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
 * Created on 16 Jul 2007
 */
package org.probatron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionContext;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.NamespaceContext;
import org.probatron.jaxen.SimpleFunctionContext;
import org.probatron.jaxen.SimpleNamespaceContext;
import org.probatron.jaxen.UnresolvableException;
import org.probatron.jaxen.XPath;
import org.probatron.jaxen.XPathFunctionContext;

import com.griffinbrown.shail.util.ShailSingletonList;
import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.XMLToolException;

public class ShailXPathEvaluator implements QueryEvaluator
{
    private NamespaceContext namespaceContext;
    private FunctionContext functionContext;
    private XPathVariableContext variableContext;
    private ArrayList queries;
    private ArrayList variables;
    private HashMap namespaceMap; //namespace prefix:uri
    private HashMap prefixMap; //uri:namespace prefix
    private Object doc;
    private static Logger logger = Logger.getLogger( ShailXPathEvaluator.class );
    private HashMap xpathVars;
    private int xpathVarCount;
    private Properties properties;
    private static final Properties defaultProperties = new Properties() {
        {
            setProperty( Constants.OPTIMIZE_XPATH_EXPRESSIONS, "false" );
        }
    };


    public ShailXPathEvaluator()
    {
        this.namespaceContext = new SimpleNamespaceContext();
        this.functionContext = new XPathFunctionContext(); //uses the core built-in function library
        this.variableContext = new XPathVariableContext();
        this.queries = new ArrayList();
        this.variables = new ArrayList();
        this.namespaceMap = new HashMap();
        this.prefixMap = new HashMap();
        this.xpathVars = new HashMap();
        this.properties = new Properties( defaultProperties );

        if( logger.isDebugEnabled() )
            logger.debug( "CONSTRUCTOR: evaluator=" + this + "; hashcode=" + hashCode() );
    }


    /**
     * @see org.probatron.QueryEvaluator#registerFunction(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void registerFunction( String uri, String localName, Object function )
    {
        ( ( SimpleFunctionContext )this.functionContext ).registerFunction( uri, localName,
                ( Function )function );
    }


    /**
     * @see org.probatron.QueryEvaluator#registerFunction(java.lang.String, java.lang.Object)
     */
    public void registerFunction( String localName, Object function )
    {
        ( ( SimpleFunctionContext )this.functionContext ).registerFunction( null, localName,
                ( Function )function );
    }


    /**
     * @see org.probatron.QueryEvaluator#getFunction(java.lang.String, java.lang.String, java.lang.String)
     */
    public Object getFunction( String uri, String prefix, String localName )
            throws XMLToolException
    {
        Function f = null;
        try
        {
            f = this.functionContext.getFunction( uri, prefix, localName );
        }
        catch( UnresolvableException e )
        {
            throw new XMLToolException( e );
        }
        return f;
    }


    /**
     * @see org.probatron.QueryEvaluator#addNamespace(java.lang.String, java.lang.String)
     */
    public void addNamespace( String prefix, String uri )
    {
        ( ( SimpleNamespaceContext )this.namespaceContext ).addNamespace( prefix, uri );
        namespaceMap.put( prefix, uri );
        prefixMap.put( uri, prefix );
    }


    /**
     * @see org.probatron.QueryEvaluator#evaluate(int, java.lang.String)
     */
    public Object evaluate( int context, String expr ) throws XMLToolException
    {
        //        if( logger.isDebugEnabled() )
        //            logger.debug( "evaluating uncompiled expression '" + expr + "'" );
        return evaluate( context, compile( expr ) );

    }


    public Object compile( String expr ) throws XMLToolException
    {
        XPath compiled = null;

        try
        {
            compiled = new ShailXPath( expr );
        }
        catch( Exception e )
        {
            throw new XMLToolException( "error compiling XPath expression: " + e.getMessage() );
        }

        compiled.setNamespaceContext( this.namespaceContext );
        compiled.setFunctionContext( this.functionContext );
        compiled.setVariableContext( this.variableContext );

        return properties.getProperty( Constants.OPTIMIZE_XPATH_EXPRESSIONS ).equals( "true" ) ? optimize( compiled )
                : compiled;
    }


    /**
     * This does the same in essence as compile(), but without further optimizing the expression,
     * or setting the namespace, function or variable contexts again (=superfluous).
     * @param expr
     * @return
     * @throws XMLToolException
     */
    private XPath recompile( String expr ) throws XMLToolException
    {
        XPath foo = null;

        logger.debug( "recompiling " + expr );

        try
        {
            foo = new ShailXPath( expr );
        }
        catch( Exception e )
        {
            throw new XMLToolException( "error recompiling XPath expression: " + e.getMessage() );
        }

        logger.debug( "recompiled expression=" + foo );

        return foo;
    }


    public Object compileUnoptimized( String expr ) throws XMLToolException
    {
        return recompile( expr );
    }


    //THIS CAN ONLY BE REVISITED IF/WHEN THE JAXEN VISITOR CLASS IS REINSTATED!! 
    private XPath optimize( XPath xpath ) throws XMLToolException
    {
        logger.debug( "optimizing " + xpath );
        logger.debug( "root expr=" + ( ( ShailXPath )xpath ).getRootExpr().getClass() );

        //        //1. see what sub-expressions are optimizable
        //        XPathVisitor v = new XPathVisitor( logger );
        //        ( ( DOMXPath )xpath ).getRootExpr().accept( v );
        //        String unoptimized = v.getUnoptimized();
        //
        //        logger.debug( "optimizable=" + v.getOptimizableParts() );
        //
        //        //2. replace optimizable sub-expressions with variable names and
        //        //3. hash the sub-expression against the variable name
        //        Iterator iter = v.getOptimizableParts().iterator();
        //        String s, optimized = null;
        //        while( iter.hasNext() )
        //        {
        //            s = ( String )iter.next();
        //            logger.debug( "optimizable part="+s);
        //            String varName = "opt-" + xpathVarCount;
        //            optimized = Utils.replace( unoptimized, s, '$'+varName );
        //            logger.debug( "optimized=" + optimized );
        //            if( ! xpathVars.containsKey( s ) )
        //            {
        //                this.xpathVars.put( s, varName );
        //                XPathVariable var = new InternalXPathVariable( varName, s );
        //                var.compile( this ); 
        //                this.addVariable( var );
        //                xpathVarCount++;
        //            }
        //            logger.debug( "optim. vars=" + xpathVars );
        //        }
        //
        //        logger.debug( "*optimized*=" + optimized );
        //
        //        Visitor v2 = new XPath2XMLVisitor();
        //        ( ( DOMXPath )xpath ).getRootExpr().accept( v2 );
        //        
        //        
        //        //4. TODO: optmization should ONLY occur if a sub-expression appears more than once in the ruleset!
        //        
        //
        //        return recompile( optimized );

        return xpath;
    }


    public Object evaluate( int context, Object compiledExpr ) throws XMLToolException
    {
        Object result = null;

        //////////////////////////////////////////////////////////////////
        // THIS AVOIDS UnresolvableExceptions FOR VARS CREATED FROM DOCS
        // CREATED *IN MEMORY ONLY*
        //TODO: explore WHY!!!!
        //////////////////////////////////////////////////////////////////
        ( ( XPath )compiledExpr ).setVariableContext( this.variableContext );
        //////////////////////////////////////////////////////////////////

        try
        {
            result = ( ( ShailXPath )compiledExpr )
                    .evaluate( new ShailSingletonList( context ) );
        }
        catch( JaxenException e )
        {
            throw new XMLToolException( "error evaluating XPath expression: " + e.getMessage() );
        }
        return result;
    }


    public void addQuery( Query query )
    {
        this.queries.add( query );
    }


    public List getQueries()
    {
        return this.queries;
    }


    public List getVariables()
    {
        return this.variables;
    }


    public void addVariable( Variable variable )
    {
        this.variables.add( variable );
        if( logger.isDebugEnabled() )
            logger.debug( "variable " + variable + " [name=" + variable.getName()
                    + "] added to evaluator with hashcode=" + hashCode() );
    }


    public void setGlobalVariableValue( String uri, String localName, Object value ) throws XMLToolException
    {
        this.variableContext.setGlobalVariableValue( uri, localName, value );
    }
    
    public void setLocalVariableValue( String uri, String localName, Object value ) throws XMLToolException
    {
        this.variableContext.setLocalVariableValue( uri, localName, value );
    }
    
    void setInternalVariableValue( String uri, String localName, Object value )
    {
        this.variableContext.setInternalVariableValue( uri, localName, value );
    }


    public String evaluateAsString( int context, Object compiledExpr ) throws XMLToolException
    {       
        String result = null;
        try
        {
            result = ( ( ShailXPath )compiledExpr ).stringValueOf( new ShailSingletonList(
                    context ) );
        }
        catch( JaxenException e )
        {
            throw new XMLToolException( "error evaluating XPath expression as string: "
                    + e.getMessage() );
        }
        return result;
    }


    /**
     * Jaxen-specific method providing access to prevailing NamespaceContext.
     * @return
     */
    public NamespaceContext getNamespaceContext()
    {
        return this.namespaceContext;
    }


    public void clearLocalVariables()
    {
        this.variableContext.clearLocals();
    }



    /**
     * @see org.probatron.QueryEvaluator#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI( String prefix )
    {
        return ( String )this.namespaceMap.get( prefix );
    }


    /**
     * @see org.probatron.QueryEvaluator#getPrefix(java.lang.String)
     */
    public String getPrefix( String namespaceURI )
    {
        return ( String )this.prefixMap.get( namespaceURI );
    }


    /**
     * @see org.probatron.QueryEvaluator#setDocument(org.w3c.dom.Document)
     */
    public void setDocument( Object doc )
    {
        this.doc = doc;
    }


    /**
     * @see org.probatron.QueryEvaluator#getDocument()
     */
    public Object getDocument()
    {
        return doc;
    }


    /**
     * @see org.probatron.QueryEvaluator#getProperties()
     */
    public Properties getProperties()
    {
        return properties;
    }


    /**
     * @see org.probatron.QueryEvaluator#getVariableValue(java.lang.String, java.lang.String)
     */
    public Object getVariableValue( String namespaceURI, String localName )
            throws XMLToolException
    {
        Object v = null;
        try
        {
            v = this.variableContext.getVariableValue( namespaceURI, null, localName );
        }
        catch( UnresolvableException e )
        {
            throw new XMLToolException( e );
        }
        return v;
    }


    /**
     * @param variableContext the variableContext to set
     */
    void setVariableContext( XPathVariableContext variableContext )
    {
        this.variableContext = variableContext;
    }

    
    
}
