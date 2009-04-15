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
 * Created on 15 Jan 2008
 */
package com.griffinbrown.schematron;

/*
 * Represents the declaration of a namespace in a Schematron schema.
 * 
 * @author andrews
 * 
 * @version $Id: NamespaceDeclaration.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class NamespaceDeclaration
{

    private String prefix;
    private String uri;


    /**
     * Default constructor.
     * @param prefix the prefix declared for this namespace
     * @param uri the URI declared for this namespace
     */
    public NamespaceDeclaration( String prefix, String uri )
    {
        //null args not allowed
        if( prefix == null || uri == null )
        {
            throw new IllegalArgumentException( "null argument not allowed for "
                    + this.getClass().getName() );
        }
        this.prefix = prefix;
        this.uri = uri;
    }


    /**
     * <p>The namespace declaration as normalized XML.</p>
     * 
     * <p>Though declared as element <tt>ns</tt> in the Schematron namespace,
     * these appear, when reported in SVRL, as <tt>ns-prefix-in-attribute-values</tt> in the SVRL namespace.</p>
     * 
     * @see org.silcn.NamespaceDeclaration#asNormalizedXml()
     */
    public String asNormalizedXml()
    {
        return "<ns-prefix-in-attribute-values prefix='" + getPrefix() + "' uri='" + getUri()
                + "'/>";
    }


    /**
     * Accesses the prefix declared for this namespace.
     * @return prefix for this namespace
     */
    public String getPrefix()
    {
        return prefix;
    }


    /**
     * Accesses the URI declared for this namespace. 
     * @return URI for this namespace
     */
    public String getUri()
    {
        return uri;
    }

}
