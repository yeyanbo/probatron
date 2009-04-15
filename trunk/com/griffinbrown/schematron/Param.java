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
 * Created on 18 Dec 2007
 */
package com.griffinbrown.schematron;


/**
 * Represents a parameter declared in a Schematron schema.
 * @author andrews
 *
 * @version $Id: Param.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class Param extends org.probatron.XPathVariable
{
    private String name;
    private String value;

    Param( String name, String value )
    {
        super( name, value );
        this.name = name;
        this.value = value;
    }

    /**
     * Accesses the name of the parameter.
     * @return the name declared for this parameter in its <code>name</code> attribute
     */
    public String getName()
    {
        return name;
    }

    /**
     * Accesses the value of the parameter.
     * @return the value declared for this parameter in its <code>value</code> attribute
     */
    public String getValue()
    {
        return value;
    }


    public String toString()
    {
        return "<" + getClass().getName() + " name=" + getName() + " value=" + getValue() + ">";
    }


    /**
     * <p>The parameter as normalized XML.</p>
     * 
     * <p>In this implementation, the normalized value is defined as an XML representation 
     * of the parameter using Schematron syntax, e.g.</p>
     * 
     * <p><code>&lt;param name="foo" value="bar"/&gt;</code></p>
     * 
     * @see org.probatron.XPathVariable#asNormalizedXml()
     */
    public String asNormalizedXml()
    {
        return "<param name='" + name + "' value='" + value + "'/>";
    }
    
    
}
