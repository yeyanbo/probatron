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

/**
 * Part of the com.griffinbrown.xmltool package
 * 
 * XML utility classes.
 * 
 * Developed by:
 * 
 * Griffin Brown Digital Publishing Ltd. (http://www.griffinbrown.com).
 * 
 * Please note this software uses the Xerces-J parser which is governed by the The Apache
 * Software License, Version 1.1, which appears below.
 * 
 * See http://xml.apache.org for further details of Apache software.
 * 
 * Internal revision information: @version $Id: ParseMessage.java,v 1.1 2006/07/12 11:02:14 GBDP\andrews
 * Exp $
 * 
 */

/**
 * This application uses Apache's Xerces XML parser which is covered by the Apache software
 * license (below).
 * 
 * The Apache Software License, Version 1.1
 * 
 * 
 * Copyright (c) 1999 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/)." Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Xerces" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", nor may "Apache" appear in
 * their name, without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally based on software copyright (c) 1999,
 * International Business Machines, Inc., http://www.ibm.com. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */
package com.griffinbrown.xmltool;

import org.apache.log4j.Logger;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import com.griffinbrown.xmltool.utils.Utils;

/**
 * Class to represent parser messages.
 *
 * @author $Author: GBDP\andrews $
 * @version @version $Id: ParseMessage.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 */

public class ParseMessage implements Message, Comparable
{
    private String _msg;
    private int _lno;
    private int _cno;
    private String _sysId;
    private String _pubId;
    private Locator _loc;
    private SAXParseException _spe;
    private String _type;
    private Application app = null;
    private XPathLocator xpl;

    private static Logger logger = Logger.getLogger( ParseMessage.class );


    /**
     * Constructor for customized validation errors.
     */
    public ParseMessage( Application app, String msg, Locator loc, String type )
    {
        this.app = app;
        _msg = new String( msg );
        _loc = loc;
        _type = type;

        if( loc != null )
        {
            _lno = loc.getLineNumber();
            _cno = loc.getColumnNumber();
            _sysId = loc.getSystemId();
            _pubId = loc.getPublicId();
        }

        //        logger.debug("parser message="+msg);
    }


    /**
     * Constructor for wrapping a validation error generated
     * by the parser (the default).
     */
    public ParseMessage( Application app, SAXParseException spe, String type )
    {
        this.app = app;
        _spe = spe;
        _loc = new Locator() {
            public String getPublicId()
            {
                return _spe.getPublicId();
            }


            public String getSystemId()
            {
                return _spe.getSystemId();
            }


            public int getLineNumber()
            {
                return _spe.getLineNumber();
            }


            public int getColumnNumber()
            {
                return _spe.getColumnNumber();
            }
        };
        _type = type;
        _lno = spe.getLineNumber();
        _cno = spe.getColumnNumber();
        _sysId = spe.getSystemId();
        _pubId = spe.getPublicId();
        _msg = spe.getMessage();

        if( logger.isDebugEnabled() )
            logger.debug( "parser message=" + _msg );
    }


    /**
     * @return The error message.
     */
    public String getString()
    {
        return this._msg;
    }


    public String getType()
    {
        return this._type;
    }


    public Locator getLocator()
    {
        return this._loc;
    }


    /**
     * Accesses the system identifier of the SAX locator associated with this message.
     * @return the system id of the instance containing the error
     * if present; otherwise <code>null</code>
     */
    public String getLocatorSystemId()
    {
        return this._sysId;
    }


    /**
     * Accesses the public identifier of the SAX locator associated with this message.
     * @return the public id of the instance containing the error
     * if present; otherwise <code>null</code>
     */
    public String getLocatorPublicId()
    {
        return this._pubId;
    }


    /**
     * @return the line number of the current error
     */
    public int getLineNumber()
    {
        return this._lno;
    }


    /**
     * @return the column number of the current error
     */
    public int getColumnNumber()
    {
        return this._cno;
    }


    public String getSystemId()
    {
        return this._sysId;
    }


    /**
     * Accesses the local part of the system identifier for this message.
     * @return the local part of the system id
     * @see #getSystemId()
     */
    public String getLocalSysId()
    {
        if( getLocatorSystemId() != null )
        {
            int index = getLocatorSystemId().lastIndexOf( '/' );
            if( index != - 1 )
            {
                return getLocatorSystemId().substring( index + 1 );
            }
        }
        return null;
    }


    /**
     * @return The parse error as XML.
     */
    public String asXml()
    {
        //		String prefix = app.namespacePrefix();
        //		StringBuffer s = new StringBuffer( "<"+ prefix + ":message>\n<" );
        //		s.append( prefix + ":type>" + this._type + "</"+ prefix + ":type>\n<" );
        //		s.append( prefix + ":systemId>" + getLocalSysId() + "</"+ prefix + ":systemId>\n<" );
        //		s.append( prefix + ":line>" + this._lno + "</"+ prefix + ":line>\n<" );
        //		s.append( prefix + ":column>" + this._cno + "</"+ prefix + ":column>\n<" );
        //		s.append( prefix + ":text>" + Utils.escape( _msg ) +
        //			"</"+ prefix + ":text>\n</"+ prefix + ":message>\n" );

        StringBuffer s = new StringBuffer( "<message>\n<type>" + this._type
                + "</type>\n<systemId>" + getLocalSysId() + "</systemId>\n<line>" + this._lno
                + "</line>\n<column>" + this._cno + "</column>\n" );

        if( this.xpl != null )
        {
            s.append( "<expression>" + this.xpl.toString() + "</expression>\n" );
        }

        s.append( "<text>" + Utils.escape( _msg ) + "</text>\n</message>\n" );

        return s.toString();
    }


    /**
     * @return The parse message as plain text.
     */
    public String asText()
    {
        StringBuffer s = new StringBuffer( app.name() + ':' );

        s.append( getLocalSysId() + ":" + getLineNumber() + ":" + getColumnNumber() + ":" );
        s.append( '[' + this._type + ']' );
        s.append( ":" + this._msg );

        return s.toString();
    }


    /**
     * Comparison method.
     */
    public int compareTo( Object o )
    {
        /* Sort order is:
         * 1. by line
         * 2. by column
         * 3. by type (~=severity)
         * 4. by message string
         */
        Message comp = ( Message )o;

        int ln = getLineNumber() - comp.getLineNumber();
        if( ln == 0 ) //line nos equal
        {
            int col = getColumnNumber() - comp.getColumnNumber();
            if( col == 0 ) //col nos equal
            {
                int sev = this._type.compareTo( comp.getType() );
                if( sev == 0 )
                {
                    return this._msg.compareTo( comp.getString() );
                }
                return sev;
            }
            return col;
        }
        return ln;
    }


    public XPathLocator getXPathLocator()
    {
        return null;
    }


    ///////NON-INTERFACE METHODS////////////
    
    /**
     * Sets the XPath locator for this message.
     */
    public void setXPathLocator( XPathLocator xpl )
    {
        this.xpl = xpl;
    }

}