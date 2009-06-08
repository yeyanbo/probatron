/*
 * Copyright 2009 Griffin Brown Digital Publishing Ltd All rights reserved.
 * 
 * This file is part of Probatron.
 * 
 * Probatron is free software: you can redistribute it and/or modify it under the terms of the
 * Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * Probatron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the Affero General Public License for more details.
 * 
 * You should have received a copy of the Affero General Public License along with Probatron. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package com.griffinbrown.xmltool;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.xmltool.utils.Utils;

/**
 * Represents messages shown to the user relating to a {@link Session} and its configuration,
 * and to user-defined error messages.
 * 
 * 
 * @author andrews
 * 
 * @version $Revision: 1.2 $
 * 
 * @version $Id: SessionMessage.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 */
public class SessionMessage implements Message, Comparable
{
    private String type;
    private String message;
    private String relatedAddIn;
    private SAXParseException spe = null;
    private SAXException se = null;
    private LocatorImpl locator = null;
    private boolean isVerbatim;
    private Application app = null;
    private XPathLocator xpl;


    /**
     * Default constructor.
     * @param app the application thes session belongs to
     * @param type the message type
     * @param message the message
     */
    public SessionMessage( Application app, String type, String message )
    {
        this.app = app;
        this.type = type;
        this.message = message;
    }


    /**
     * Constructor providing a reference to the <code>Extension</code> where
     * this message originates.
     * @param app the application thes session belongs to
     * @param type the message type
     * @param message the message
     * @param relatedAddIn the plug-in the message relates to
     */
    public SessionMessage( Application app, String type, String message, String relatedAddIn )
    {
        this.app = app;
        this.type = type;
        this.message = message;
        this.relatedAddIn = relatedAddIn;
    }


    /**
     * Constructor for providing location details from an internal SAX parse.
     * @param app the application thes session belongs to
     * @param type the message type
     * @param se the exception relating to this message
     */
    public SessionMessage( Application app, String type, SAXException se )
    {
        this.app = app;
        this.type = type;
        this.message = se.getMessage();
        this.se = se;
    }


    /**
     * Constructor for providing location details from an internal SAX parse.
     * @param app the application thes session belongs to
     * @param type the message type
     * @param spe the exception relating to this message
     */
    public SessionMessage( Application app, String type, SAXParseException spe )
    {
        this.app = app;
        this.type = type;
        this.message = spe.getMessage();
        this.spe = spe;
    }


    /**
     * Constructor providing a simple string message and SAX locator.
     * @param app the application thes session belongs to
     * @param type message type
     * @param message string of this message
     * @param loc SAX locator for this message
     */
    public SessionMessage( Application app, String type, String message, LocatorImpl loc )
    {
        this.app = app;
        this.type = type;
        this.message = message;
        this.locator = loc;
    }

    /**
     * Constructor providing a simple string message, SAX locator and XPath locator.
     * @param app the application thes session belongs to
     * @param type message type
     * @param message string of this message
     * @param loc SAX locator for this message
     * @param xpl XPath locator for this message
     */
    public SessionMessage( Application app, String type, String message, LocatorImpl loc,
            XPathLocator xpl )
    {
        this.app = app;
        this.type = type;
        this.message = message;
        this.locator = loc;
        this.xpl = xpl;
    }


    /**
     * Message as verbatim XML string.
     *
     */
    public SessionMessage( String s )
    {
        this.message = s;
        isVerbatim = true; //could probably do with own class
        this.type = Constants.ERROR_TYPE_INFO; //hard-coded type for verbatim msgs
    }


    public String asText()
    {
        StringBuffer s = new StringBuffer( app.name() + ':' );
        if( isVerbatim )
        {
            return s.append( message ).toString();
        }

        if( spe != null )
        {
            s.append( spe.getSystemId() + ":" + spe.getLineNumber() + ":"
                    + spe.getColumnNumber() + ":" );
        }

        if( locator != null )
        {
            s.append( Utils.getMinimalSysId( locator.getSystemId() ) + ":"
                    + locator.getLineNumber() + ":" + locator.getColumnNumber() + ":" );
        }

        /*XPathLocator info goes here*/

        s.append( '[' + type + ']' );

        if( relatedAddIn != null )
        {
            s.append( "[" + relatedAddIn + "]" );
        }

        s.append( ":" + message );

        return s.toString();
    }


    public String asXml()
    {
        //SILCN impl
        StringBuffer buf = new StringBuffer();

        if( this.xpl != null )
        {
            buf.append( "<silcn:node>\n<silcn:expression>" );
            buf.append( this.xpl.toString() );
            buf.append( "</silcn:expression>\n" );
        }
        else
        {
            buf.append( "<message>\n" );
        }

        if( type != null && this.xpl == null ) //those with an XPath locator will have <silcn:id>s
        {
            buf.append( "<type>" ).append( type ).append( "</type>\n" );
        }

        if( relatedAddIn != null )
        {
            buf.append( "<relatedAddIn>" ).append( relatedAddIn ).append( "</relatedAddIn>\n" );
        }

        if( spe != null )
        {
            //note we include the sysId, as this is likely to differ from the
            //that of the main input file...
            buf.append( "<systemId>" ).append( spe.getSystemId() ).append(
                    "</systemId>\n<line>" ).append( spe.getLineNumber() ).append(
                    "</line>\n<column>" ).append( spe.getColumnNumber() )
                    .append( "</column>\n" );
        }

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

        if( message != null )
        {
            buf.append( "<text>" ).append( Utils.escape( message ) ).append( "</text>\n" );
        }

        if( this.xpl != null )
        {
            buf.append( "</silcn:node>\n" );
        }
        else
        {
            buf.append( "</message>" );
        }

        return buf.toString();

    }


    /**
     * Comparison method.
     */
    public int compareTo( Object o )
    {
        Message comp = ( Message )o;

        int ln = getLineNumber() - comp.getLineNumber();
        if( ln == 0 ) //line nos equal
        {
            int col = getColumnNumber() - comp.getColumnNumber();
            if( col == 0 ) //col nos equal
            {
                int sev = this.type.compareTo( comp.getType() );
                if( sev == 0 )
                {
                    return this.message.compareTo( comp.getString() );
                }
                return sev;
            }
            return col;
        }
        return ln;
    }


    public Locator getLocator()
    {
        return this.locator;
    }


    public XPathLocator getXPathLocator()
    {
        return this.xpl;
    }


    public String getString()
    {
        return this.message;
    }


    public String getType()
    {
        return this.type;
    }


    public int getColumnNumber()
    {
        if( this.locator != null )
        {
            return locator.getColumnNumber();
        }
        if( this.spe != null )
        {
            return spe.getColumnNumber();
        }
        return - 1;
    }


    public int getLineNumber()
    {
        if( this.locator != null )
        {
            return locator.getLineNumber();
        }
        if( this.spe != null )
        {
            return spe.getLineNumber();
        }
        return - 1;
    }


    public String getSystemId()
    {
        if( this.locator != null )
        {
            return locator.getSystemId();
        }
        if( this.spe != null )
        {
            return spe.getSystemId();
        }
        return null;
    }

}