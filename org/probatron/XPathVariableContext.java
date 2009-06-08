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
 * Created on 31 Jul 2008
 */
package org.probatron;

import org.apache.log4j.Logger;
import org.probatron.jaxen.SimpleVariableContext;
import org.probatron.jaxen.UnresolvableException;
import org.probatron.jaxen.VariableContext;

import com.griffinbrown.xmltool.XMLToolException;

/**
 * Manages XPath variables in force for a session of XPath evaluation.   
 * @author andrews
 *
 * $Id$
 */
public class XPathVariableContext implements VariableContext
{
    private SimpleVariableContext globals;
    private SimpleVariableContext locals;

    private static Logger logger = Logger.getLogger( XPathVariableContext.class );

    /**
     * Constructor for normal use.
     */
    public XPathVariableContext()
    {
        globals = new SimpleVariableContext();
    }


    SimpleVariableContext getLocalVariableContext()
    {
        if( locals == null )
        {
            locals = new SimpleVariableContext();
        }
        return locals;
    }


    void clearLocals()
    {
        locals = null;
    }


    /**
     * @see org.probatron.jaxen.VariableContext#getVariableValue(java.lang.String, java.lang.String, java.lang.String)
     */
    public Object getVariableValue( String namespaceURI, String prefix, String localName )
            throws UnresolvableException
    {
        if( locals == null ) //no locals in scope
            return globals.getVariableValue( namespaceURI, prefix, localName );
        else
        {
            try
            {
                return locals.getVariableValue( namespaceURI, prefix, localName );
            }
            catch( UnresolvableException e )
            {
                return globals.getVariableValue( namespaceURI, prefix, localName );
            }
        }
    }


    /**
     * @see org.probatron.jaxen.SimpleVariableContext#setVariableValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    void setGlobalVariableValue( String namespaceURI, String localName, Object value )
            throws XMLToolException
    {
        try
        {
            globals.getVariableValue( namespaceURI, null, localName );
        }
        catch( UnresolvableException e )
        {
            globals.setVariableValue( namespaceURI, localName, value );
            return;
        }

        //got a dupe
        throw new XMLToolException( "duplicate variable name: " + localName );
    }


    void setLocalVariableValue( String namespaceURI, String localName, Object value )
            throws XMLToolException
    {
        locals = getLocalVariableContext();

        //locals must not have the same name as a global
        try
        {
            globals.getVariableValue( namespaceURI, null, localName );
        }
        catch( UnresolvableException e )
        {
            locals.setVariableValue( namespaceURI, localName, value );
            return;
        }

        //got a dupe
        throw new XMLToolException( "duplicate variable name: " + localName );

    }


    /**
     * An internal variable is a global variable without any check for duplication.
     * @param namespaceURI
     * @param localName
     * @param value
     */
    void setInternalVariableValue( String namespaceURI, String localName, Object value )
    {
        globals.setVariableValue( namespaceURI, localName, value );
    }
    
    void setLocalVariableContext( SimpleVariableContext locals )
    {
        this.locals = locals;
    }
    
    SimpleVariableContext getGlobalVariableContext()
    {
        return this.globals;
    }

}
