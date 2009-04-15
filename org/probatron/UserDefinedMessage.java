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
 * Copyright (c) 2003 Griffin Brown Digital Publishing Ltd. All rights reserved.
 */
package org.probatron;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.xmltool.Application;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XPathLocator;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Class to represent a message defined by the user.
 * This is a sub-class to accommodate well-formed XML included in user-defined messages.
 */
public class UserDefinedMessage extends SessionMessage
{
    public UserDefinedMessage( Application app, String type, String message, LocatorImpl loc,
            XPathLocator xpl )
    {
        super( app, type, message, loc, xpl );
    }


    public UserDefinedMessage( Application app, String type, String message, LocatorImpl loc )
    {
        super( app, type, message, loc );
    }
    
    public String asXml()
    {
      //SILCN impl
        StringBuffer buf = new StringBuffer();

        XPathLocator xpl = getXPathLocator();
        if( xpl != null )
        {
            buf.append( "<silcn:node>\n<silcn:expression>" );
            buf.append( xpl.toString() );
            buf.append( "</silcn:expression>\n" );
        }
        else
        {
            buf.append( "<message>\n" );
        }

        String type = getType();
        if( type != null && xpl == null ) //those with an XPath locator will have <silcn:id>s
        {
            buf.append( "<type>" ).append( type ).append( "</type>\n" );
        }
        
        Locator locator = getLocator();
        if( locator != null )
        {
            //note we include the sysId, as this is likely to differ from the
            //that of the main input file...
            buf.append( "<systemId>" );
            if( locator.getSystemId() != null )
            {
                //hack: an initial '?' means the URI's absolute
                buf.append( locator.getSystemId().startsWith( "?" ) ? locator.getSystemId()
                        .substring( 1 ) : Utils.getMinimalSysId( locator.getSystemId() ) );
            }
            buf.append( "</systemId>\n<line>" ).append( locator.getLineNumber() ).append(
                    "</line>\n<column>" ).append( locator.getColumnNumber() ).append(
                    "</column>\n" );
        }

        String message = getString();
        //DO NOT ESCAPE MESSAGE, IN CASE WELL-FORMED XML IS PASSED THROUGH!
        if( message != null )
        {
            buf.append( "<text>" )
               .append( message ).append( "</text>\n" );
        }

        if( xpl != null )
        {
            buf.append( "</silcn:node>\n" );
        }
        else
        {
            buf.append( "</message>" );
        }

        return buf.toString();
    }
}
