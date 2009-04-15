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
 * Created on 8 Jan 2008
 */
package com.griffinbrown.schematron;

/**
 * Represents an XPath variable in a Schematron schema which is global in scope.
 * @author andrews
 *
 * @version $Id: GlobalXPathVariable.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class GlobalXPathVariable extends org.probatron.GlobalXPathVariable
{
    private Object context;


    /**
     * Constructs an XPath variable in no namespace.
     * @param name the name of the variable
     * @param expr the variable expression
     */
    public GlobalXPathVariable( String name, String expr )
    {
        super( name, expr );
    }


    /**
     * Constructs an XPath variable in a specified namespace. 
     * @param namespaceURI the URI associated with the variable's namespace
     * @param name the name of the variable
     * @param expr the variable expression
     */
    public GlobalXPathVariable( String namespaceURI, String name, String expr )
    {
        super( namespaceURI, name, expr );
    }


    /**
     * <p>The variable as normalized XML.</p>
     * <p>In this implementation, the value returned shall be an XML representation
     * of the variable in Schematron syntax, e.g.</p>
     * 
     * <p><code>&lt;let name="foo" value="bar"/&gt;</code></p>
     * 
     * @see org.probatron.XPathVariable#asNormalizedXml()
     */
    public String asNormalizedXml()
    {
        return "<let name='" + getName() + " value='" + getExpression() + "/>";
    }

}
