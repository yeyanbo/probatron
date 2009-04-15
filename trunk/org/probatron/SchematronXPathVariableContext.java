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
 * Created on 8 Oct 2008
 */
package org.probatron;

import org.probatron.jaxen.UnresolvableException;

import com.griffinbrown.xmltool.XMLToolException;

public class SchematronXPathVariableContext extends XPathVariableContext
{
    void setLocalVariableValue( String namespaceURI, String localName, Object value )
            throws XMLToolException
    {
        setLocalVariableContext( getLocalVariableContext() );

        //locals must not have the same name as a global
        try
        {
            getGlobalVariableContext().getVariableValue( namespaceURI, null, localName );
        }
        catch( UnresolvableException noGlobalNameClash )
        {
            try
            {
                getLocalVariableContext().getVariableValue( namespaceURI, null, localName );
            }
            catch( UnresolvableException noLocalNameClash )
            {
                getLocalVariableContext().setVariableValue( namespaceURI, localName, value );
                return;
            }
        }

        //got a dupe
        throw new XMLToolException( "duplicate variable name: " + localName );

    }
}
