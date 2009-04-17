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
 * Internal revision information: @version $Id: ContentHandler.java,v 1.1 2006/07/12 11:02:14
 * GBDP\andrews Exp $
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

import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.xmltool.dtd.ElemTypeDecl;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * The default content handler for all operations.
 */
public class ContentHandler implements ErrorHandler, org.xml.sax.ContentHandler, DTDHandler, LexicalHandler, DeclHandler//SAXEventReceiver
{
    Locator locator;
    private Extension[] extensions; //add-ins (Extension impls)
    private Instance instance = null;
    private boolean isInDtd;
    private boolean dtdStarted;


    public ContentHandler( Instance inst )
    {
        instance = inst;
        this.locator = null;
    }


    public void setDocumentLocator( Locator locator )
    {
        this.locator = locator;
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.setDocumentLocator( locator );
        }
    }


    public void characters( char[] ch, int start, int length )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.characters( ch, start, length );
        }
    }


    public void endDocument()
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.endDocument();
        }
    }


    public void endElement( String namespaceURI, String localName, String qName )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.endElement( namespaceURI, localName, qName );
        }

        //		this.eventHistory.add(
        //			new ParseEvent(ParseEvent.EV_END_ELEMENT, cloneLocator()));
    }


    public void endPrefixMapping( String prefix )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.endPrefixMapping( prefix );
        }

    }


    public void ignorableWhitespace( char[] ch, int start, int length )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.ignorableWhitespace( ch, start, length );
        }

    }


    public void processingInstruction( String target, String data )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.processingInstruction( target, data );
        }

    }


    public void skippedEntity( String name )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.skippedEntity( name );
        }
    }


    public void startDocument()
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.startDocument();
        }

    }


    public void startElement( String namespaceURI, String localName, String qName,
            Attributes atts )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.startElement( namespaceURI, localName, qName, atts );
        }

        /* KEEP THESE AT THE END OF THIS METHOD!*/
        //		this.eventHistory.add(
        //			new ParseEvent(ParseEvent.EV_START_ELEMENT, cloneLocator()));
        //		this.context.addElement(
        //			new ParseEvent(ParseEvent.EV_START_ELEMENT, cloneLocator()));
    }


    public void startPrefixMapping( String prefix, String uri )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.startPrefixMapping( prefix, uri );
        }
    }


    //////////////////////////////////////////
    //////// LEXICAL HANDLER METHODS /////////
    //////////////////////////////////////////

    public void comment( char[] ch, int start, int length )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.comment( ch, start, length );
        }
    }


    public void startCDATA()
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.startCDATA();
        }
    }


    public void endCDATA()
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.endCDATA();
        }
    }


    public void startDTD( String name, String pubId, String systemId )
    {
        dtdStarted = true;
        isInDtd = true;
        instance().doctype = name;
        instance().dtd().setPubId( pubId );
        instance().dtd().setSysId( systemId );

        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.startDTD( name, pubId, systemId );
        }
    }


    public void endDTD()
    {
        isInDtd = false;
        // if there hasn't been a startDTD event, set DTD to null
        if( ! this.dtdStarted )
        {
            instance().setDtd( null );
            //DEBUG System.err.println( "DTD=" +instance().dtd );
        }
        instance().dtd().forwardDecls();

        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.endDTD();
        }
    }


    public void startEntity( String name )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.startEntity( name );
        }
    }


    public void endEntity( String name )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];

            se.endEntity( name );
        }
    }


    //////////////////////////////////////////
    ///////// DeclHandler METHODS ////////////
    //////////////////////////////////////////
    public void attributeDecl( String eName, String aName, String type, String valueDefault,
            String value )
    {
        // see if the element's been declared
        ElemTypeDecl etd = instance.dtd().getElemTypeDeclByName( eName );

        // no, we'll need a 'forward declaration' (i.e., with no content model)
        if( etd == null )
        {
            etd = instance.dtd().addElemTypeDecl( eName, "" );
        }

        etd.addAttDecl( aName, type, valueDefault, value );

        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.attributeDecl( eName, aName, type, valueDefault, value );
        }
    }


    public void elementDecl( String name, String model )
    {
        // see if the element's placeholder been put in the DTD hashtable
        // cos of a previous ATTLIST

        ElemTypeDecl etd = instance.dtd().getElemTypeDeclByName( name );

        //System.err.println( "==>" + name );	 //DEBUG

        if( etd == null )
        {
            etd = instance.dtd().addElemTypeDecl( name, model );
        }
        else
        {
            // all that remains is to set the content model
            //System.err.println( "Setting content model for elem " + etd.name() + model );
            etd.setContentModel( model );
        }

        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.elementDecl( name, model );
        }
    }


    public void externalEntityDecl( String name, String publicId, String systemId )
    {
        if( ! name.startsWith( "%" ) ) // ignore parameter entities
        {
            instance.dtd().addExternalEntityDecl( name, publicId, systemId );
        }

        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.externalEntityDecl( name, publicId, systemId );
        }
    }


    public void internalEntityDecl( String name, String value )
    {
        if( ! name.startsWith( "%" ) ) // ignore parameter entities
        {
            instance.dtd().addEntityDecl( name, value );
        }

        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.internalEntityDecl( name, value );
        }
    }


    //////////////////////////////////////////
    ////////////DTD HANDLER METHODS///////////
    //////////////////////////////////////////
    public void notationDecl( String name, String publicId, String systemId )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.notationDecl( name, publicId, systemId );
        }
    }


    public void unparsedEntityDecl( String name, String publicId, String systemId,
            String notationName )
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.unparsedEntityDecl( name, publicId, systemId, notationName );
        }
    }


    //////////////////////////////////////////
    ////////  XMLTOOL-SPECIFIC METHODS  /////////
    //////////////////////////////////////////

    public void preParse()
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            try
            {
                se.preParse();
            }
            catch( XMLToolException exception )
            {
                Utils.reportFatalError( exception.getMessage() );
                SessionRegistry.getInstance().getCurrentSession().fatalError();
            }
        }
    }


    public void postParse()
    {
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.postParse();
        }
    }


    /**
     * @return The current instance.
     */
    Instance instance()
    {
        return this.instance;
    }


    /**
     * Add a handler add-in for this session.
     */
    public void registerExtensions( Extension[] exts )
    {
        this.extensions = exts;
    }


    /**
     * @return A persistent error locator object.
     */
    public LocatorImpl cloneLocator()
    {
        return locator == null ? null : new LocatorImpl( locator );
    }


    /**
     * @return Whether the event history contains an event of this type.
     
     boolean hasAncestorEventOfType(int type)
     {
     //System.err.println( "Context size=" + this.context.size() );
     for (int i = 0; i < this.context.size(); i++)
     {
     ParseEvent e = (ParseEvent) this.context.elementAt(i);
     if (e.type() == type)
     {
     return true;
     }
     }

     return false;
     }*/

    /**
     * Receive notification of a recoverable error.
     */
    public void error( SAXParseException e ) throws SAXException
    {
        ParseMessage m = instance().addParseMessage( e, Constants.ERROR_TYPE_NON_FATAL );
        Extension se = null;
        for( int i = 0; i < extensions.length; i++ )
        {
            se = extensions[ i ];
            se.parseMessage( m );
        }
    }


    /**
     * Receive notification of a non-recoverable error.
     */
    public void fatalError( SAXParseException e ) throws SAXException
    {
        instance().addParseMessage( e, Constants.ERROR_TYPE_FATAL );
    }


    /**
     * Receive notification of a warning.
     */
    public void warning( SAXParseException e ) throws SAXException
    {
        instance().addParseMessage( e, Constants.ERROR_TYPE_WARNING );
    }


    public Extension[] getRegisteredExtensions()
    {
        return this.extensions;
    }
}