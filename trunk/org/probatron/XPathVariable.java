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
 * Created on Jan 27, 2005
 * 
 */
package org.probatron;

import org.apache.log4j.Logger;

import com.griffinbrown.xmltool.XMLToolException;

/**
 * Class to represent a variable as part of an XPath expression.
 */
public abstract class XPathVariable implements Variable
{
    private String namespaceURI;
    private String name;
    private String expr;
    private static Logger logger = Logger.getLogger( XPathVariable.class );

    static final String ELEMENT_VARIABLE = "variable";


    public XPathVariable( String name, String expr )
    {
        this.name = name;
        this.expr = expr;
    }


    public XPathVariable( String namespaceURI, String name, String expr )
    {
        this.namespaceURI = namespaceURI;
        this.name = name;
        this.expr = expr;
    }


    /**
     * Compiles the variable using the specified evaluator.
     * @return the compiled expression
     * @throws if the expression cannot be compiled
     */
    public Object compile( QueryEvaluator evaluator ) throws XMLToolException
    {
        Object compiled = null;
        try
        {
            compiled = evaluator.compile( expr );
        }
        catch( XMLToolException e )
        {
            throw new XMLToolException( "error compiling XPath variable [name='" + name
                    + "']; expression='" + expr + "': " + e.getMessage() );
        }

        return compiled;
    }


    /**
     * Evaluates the variable against the given context, using the specified evaluator.
     * This implementation also compiles the variable.
     * @param context
     * @param evaluator
     * @return
     * @throws XMLToolException
     */
    public Object evaluate( int context, QueryEvaluator evaluator ) throws XMLToolException
    {
        Object result = null;

        try
        {
            result = evaluator.evaluate( context, compile( evaluator ) );
        }
        catch( XMLToolException e )
        {
            throw new XMLToolException( "error evaluating XPath variable [name='" + name
                    + "']; expression='" + expr + "': " + e.getMessage() );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "variable $" + name + " evaluated to " + result + " against context="
                    + context );

        return result;
    }


    public String getName()
    {
        return this.name;
    }


    public String getNamespaceURI()
    {
        return this.namespaceURI;
    }


    public String getExpression()
    {
        return this.expr;
    }

    /**
     * Retrieves a representation of the variable as normalized XML.
     * @return 
     */
    public abstract String asNormalizedXml();


    public String toString()
    {
        return "<" + getClass().getName() + " name=" + name + " uri=" + namespaceURI + " expr="
                + expr + ">";
    }
}