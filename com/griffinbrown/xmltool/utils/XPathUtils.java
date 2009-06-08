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
 * Created on 15 Aug 2008
 */
package com.griffinbrown.xmltool.utils;

import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.XPath;

/**
 * Utilities for evaluating XPath expressions. 
 * @author andrews
 *
 * $Id$
 */
public class XPathUtils
{
    private XPathUtils()
    {}
    
    /**
     * Compiles an XPath expression using a specified factory.
     * Compilation errors are reported to standard error.
     * @param expr the expression to compile
     * @param xpathFactory the factory to use
     * @return the XPath expression as an object, or <code>null</code> if compilation fails
     */
    public static Object compileXPathExpression( String expr, XPathFactory xpathFactory )
    {
        XPath compiled = null;

        try
        {
            compiled = ( XPath )xpathFactory.createXPath( expr );
        }
        catch( Exception e )
        {
            System.err.println( "error compiling XPath expression: " + e.getMessage() );
            return null;
        }

        //        compiled.setNamespaceContext( this.namespaceContext );
        //        compiled.setFunctionContext( this.functionContext );
        //        compiled.setVariableContext( this.variableContext );

        return compiled;
    }

    /**
     * Evaluates a compiled XPath expression against a given context.
     * Evaluation errors are reported to standard error.
     * @param context the context for evaluation
     * @param compiledExpr the compiled expression
     * @return the result of the evaluation (one of the four XPath 1.0 return types), or <code>null</code> on failure
     */
    public static Object evaluateXPath( Object context, Object compiledExpr )
    {
        Object result = null;

        try
        {
            result = ( ( XPath )compiledExpr ).evaluate( context );
        }
        catch( JaxenException e )
        {
            System.err.println( "error evaluating XPath expression: " + e.getMessage() );
            return null;
        }
        return result;
    }
}
