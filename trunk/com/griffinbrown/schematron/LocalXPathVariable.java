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
 * Created on 22 Jan 2008
 */
package com.griffinbrown.schematron;

import org.probatron.QueryEvaluator;
import org.probatron.XPathVariable;

import com.griffinbrown.xmltool.XMLToolException;

/**
 * Represents an XPath variable in a Schematron schema which is local in scope.
 * 
 * @author andrews
 * 
 * @version $Id: LocalXPathVariable.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class LocalXPathVariable extends XPathVariable
{
    private int context;


    /**
     * Constructs an XPath variable in no namespace.
     * @param name the name of the variable
     * @param expr the variable expression
     */
    public LocalXPathVariable( String name, String expr )
    {
        super( name, expr );
    }


    /**
     * Constructs an XPath variable in a specified namespace. 
     * @param namespaceURI the URI associated with the variable's namespace
     * @param name the name of the variable
     * @param expr the variable expression
     */
    public LocalXPathVariable( String namespaceURI, String name, String expr )
    {
        super( namespaceURI, name, expr );
    }


    /**
     * Sets the context in which this variable is evaluated.
     * See s5.4.5: 
     * "If the let element is the child of a rule element, the variable is calculated and
     * scoped to the current rule and context. Otherwise, the variable is calculated with 
     * the context of the instance document root."
     * @param context the context in which this variable is evaluated
     */
    public void setContext( int context )
    {
        this.context = context;
    }


    public Object evaluate( int context, QueryEvaluator evaluator ) throws XMLToolException
    {
        Object result = super.evaluate( context, evaluator );
        evaluator.setLocalVariableValue( getNamespaceURI(), getName(), result );
        return result;
    }


    public String asNormalizedXml()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
