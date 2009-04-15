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
 * Internal revision information: @version $Id: Instance.java,v 1.1 2009/01/08 14:41:30
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI.MalformedURIException;
import org.probatron.QueryHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;

import com.griffinbrown.xmltool.dtd.Dtd;
import com.thaiopensource.validate.ValidationDriver;

/*
 * Class to represent an XML instance.
 */
public class Instance extends XmlConstruct
{
    private Dtd dtd;
    String sysId;
    String doctype;
    String pubId;
    private String resolvedUri;
    private StringBuffer normal = new StringBuffer();
    //	private boolean isSubsetInternal;
    private boolean isStandalone = false;
    boolean isExtIdOmissible;
    boolean isWellFormed;
    boolean hasDtd;
    private boolean isParsed;
    private ArrayList errs = new ArrayList();
    private int fatal = 0;
    private int non_fatal = 0;
    private int warning = 0;
    boolean areWarningsIssuable = true;
    private InputSource inputSource = null;
    private Session session = null;
    private static Logger logger = Logger.getLogger( Instance.class );
    private ValidationDriver validator;


    /**
     * Constructor for parsing a resource based on a URI.
     * Must be passed the systemId (system-specific format) of an entity containing
     * an XML document.
     */
    public Instance( String sysId, Session session )
    {
        super( null );
        this.sysId = sysId;

        this.dtd = new Dtd( this );
        this.session = session;

    }


    /**
     * Constructor for parsing a resource based on a URI, where a Session has
     * already been registered with the application.
     * Must be passed the systemId (system-specific format) of an entity containing
     * an XML document.
     */
    public Instance( String sysId )
    {
        super( null );
        this.sysId = sysId;

        this.dtd = new Dtd( this );
        this.session = SessionRegistry.getInstance().getCurrentSession();
    }


    /**
     * Constructor for parsing an <code>InputSource</code>.
     */
    public Instance( InputSource inputSource, Session session )
    {
        super( null );
        this.inputSource = inputSource;
        this.sysId = inputSource.getSystemId();
        this.dtd = new Dtd( this );
        this.session = session;
    }


    /**
     * This method is provided for clients which wish to process exceptions
     * arising from the parse in their own way.
     * 
     */
    public void parseExposingExceptions() throws SAXException, IOException
    {
        session.parser().parse( this.sysId );
    }


    public void parse()
    {
        if( validator == null ) //fragile
            validateAgainstDTD();
        else
            validateAgainstRelaxNGSchema();
    }


    /**
     * The usual method for parsing an <code>Instance</code>.
     *
     */
    void validateAgainstDTD()
    {
//        if( logger.isDebugEnabled() )
//            logger.debug( "validating..." );

        if( ! this.isParsed )
        {
            //parse an InputSource
            if( this.inputSource != null )
            {
                try
                {
                    session.parser().parse( this.inputSource );
                }

                catch( SAXException saxe )
                {
                    addParseMessage( "SAX exception: " + saxe.getMessage(), session
                            .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
                    session().fatalError();
                }
                catch( IOException ioex )
                {
                    if( session.getContentHandler().cloneLocator() != null )
                    {
                        addParseMessage( "IOException: " + ioex.getMessage(), session
                                .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
                    }
                    else
                    {
                        addParseMessage( "IOException: " + ioex.getMessage(), session
                                .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
                    }
                    session().fatalError();
                }
            }
            //parse a sys id
            else
            {
                try
                {
                    session.parser().parse( this.sysId );
                }

                catch( SAXException saxe )
                {
                    //addParseError( saxe.getMessage(), Swixsession.getContentHandler().cloneLocator(), ParseMessage.ERROR_TYPE_NON_FATAL );
                    addParseMessage( "SAX exception: " + saxe.getMessage(), session
                            .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
                    session().fatalError();
                }
                catch( IOException ioex )
                {
                    if( session.getContentHandler().cloneLocator() != null )
                    {
                        addParseMessage( "IOException: " + ioex.getMessage(), session
                                .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
                    }
                    else
                    {
                        addParseMessage( "IOException: " + ioex.getMessage(), session
                                .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
                    }
                    session().fatalError();
                }

                this.isParsed = true;

                if( dtd() != null )
                {
                    if( dtd().elemDeclCount() == 0 && dtd().notationDecls().hasNext() == false
                            && dtd().entityDecls().hasNext() == false
                            && dtd().unparsedEntityDecls().hasNext() == false )
                    {
                        this.dtd = null;
                        // there are no declarations, therefore no DTD!
                    }
                    else
                    {
                        dtd().resolveChildren();
                    }
                }
            }
        }

    }


    void validateAgainstRelaxNGSchema()
    {
        if( logger.isDebugEnabled() )
            logger.debug( "validating against RELAX NG schema" );

        try
        {
            validator.validate( validator.uriOrFileInputSource( sysId ) );
        }
        catch( SAXException e ) //SAXException, IOException
        {
            addParseMessage( "SAX exception parsing with XInclude: " + e.getMessage(), session
                    .getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
            session().fatalError();
        }
        catch( IOException e )
        {
            if( session.getContentHandler().cloneLocator() != null )
            {
                addParseMessage( "IOException parsing with XInclude: " + e.getMessage(),
                        session.getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
            }
            else
            {
                addParseMessage( "IOException parsing with XInclude: " + e.getMessage(),
                        session.getContentHandler().cloneLocator(), Constants.ERROR_TYPE_FATAL );
            }
            e.printStackTrace();
            session().fatalError();
        }
    }


    /**
     * Parses an instance using the XMLFilter specified.
     * @param filter
     */
    public void parse( XMLFilter filter )
    {
        if( ! this.isParsed )
        {
            //parse an InputSource
            if( this.inputSource != null )
            {
                try
                {
                    filter.parse( this.inputSource );
                }

                catch( java.net.ConnectException e )
                {
                    addParseMessage( e.getMessage(),
                            session.getContentHandler().cloneLocator(),
                            Constants.ERROR_TYPE_NON_FATAL );
                    e.getCause().printStackTrace();
                    session.terminate( - 1 );
                    //					e.printStackTrace();
                }
                catch( SAXException saxe )
                {
                    addParseMessage( "SAX exception parsing with XInclude: "
                            + saxe.getMessage(), session.getContentHandler().cloneLocator(),
                            Constants.ERROR_TYPE_FATAL );
                    session().fatalError();
                }
                catch( IOException ioex )
                {
                    if( session.getContentHandler().cloneLocator() != null )
                    {
                        addParseMessage( "IOException parsing with XInclude: "
                                + ioex.getMessage(),
                                session.getContentHandler().cloneLocator(),
                                Constants.ERROR_TYPE_FATAL );
                    }
                    else
                    {
                        addParseMessage( "IOException parsing with XInclude: "
                                + ioex.getMessage(),
                                session.getContentHandler().cloneLocator(),
                                Constants.ERROR_TYPE_FATAL );
                    }
                    ioex.printStackTrace();
                    session().fatalError();
                }

            }
            //parse a sys id
            else
            {
                try
                {
                    filter.parse( this.sysId );
                }

                catch( SAXException saxe )
                {
                    //addParseError( saxe.getMessage(), Swixsession.getContentHandler().cloneLocator(), ParseMessage.ERROR_TYPE_NON_FATAL );
                    addParseMessage( "SAX exception parsing with XInclude: "
                            + saxe.getMessage(), session.getContentHandler().cloneLocator(),
                            Constants.ERROR_TYPE_FATAL );
                    session().fatalError();
                }
                catch( IOException ioex )
                {
                    if( session.getContentHandler().cloneLocator() != null )
                    {
                        addParseMessage( "IOException parsing with XInclude: "
                                + ioex.getMessage(),
                                session.getContentHandler().cloneLocator(),
                                Constants.ERROR_TYPE_FATAL );
                    }
                    else
                    {
                        addParseMessage( "IOException parsing with XInclude: "
                                + ioex.getMessage(),
                                session.getContentHandler().cloneLocator(),
                                Constants.ERROR_TYPE_FATAL );
                    }
                    ioex.printStackTrace();
                    session().fatalError();
                }

                this.isParsed = true;

                if( dtd() != null )
                {
                    if( dtd().elemDeclCount() == 0 && dtd().notationDecls().hasNext() == false
                            && dtd().entityDecls().hasNext() == false
                            && dtd().unparsedEntityDecls().hasNext() == false )
                    {
                        this.dtd = null;
                        // there are no declarations, therefore no DTD!
                    }
                    else
                    {
                        dtd().resolveChildren();
                    }
                }
            }
        }
    }


    public String asNormalizedXml( int emissionType )
    {
        //parse();	//WHY DID WE NEED TO PARSE HERE??
        return this.normal.toString();
    }


    /**
     * @return The string of the normalized DTD as an internal subset.
     */
    public String normalizedIntDtd( Dtd d, int emissionType )
    {
        String s = getXmlDecl( XmlConstruct.XML_VERSION, session.getOutputEncoding(),
                this.isStandalone )
                + getDoctypeDecl( true );
        s += d.asNormalizedXml( emissionType ) + "]>";
        return s;
    }


    /**
     * @return The string of the normalized DTD as an external subset.
     */
    public String normalizedExtDtd( Dtd d, int emissionType )
    {
        String s = d.asNormalizedXml( emissionType );
        return s;
    }


    /**
     * @return The doctype declaration.
     * @param isSubsetInternal whether the declaration will appear before an internal subset.
     */
    public String getDoctypeDecl( boolean isSubsetInternal )
    {
        String dtdecl = "";
        if( dtd == null )
            return dtdecl;

        //if it's destined for an internal subset, '[' and don't close it; otherwise '>'
        if( isSubsetInternal )
        {
            dtdecl += "[";
        }
        else if( isExtIdOmissible & ! isSubsetInternal )
        {
            //omit external id
            dtdecl += ">";
        }
        else if( isExtIdOmissible & isSubsetInternal )
        {
            //leave the DOCTYPE open, ready for elemdecls
            return dtdecl;
        }
        else
        {
            dtdecl += getExternalId();
            dtdecl += ">";
        }

        return "<!DOCTYPE " + docType() + dtdecl;
    }


    /**
     * Accesses the <code>SYSTEM</code> and <code>PUBLIC</code> identifiers for 
     * this instance.
     * @return the external id info, if present, otherwise <code>null</code>
     */
    public String getExternalId()
    {
        //this.parse();	WHY DID WE NEED TO PARSE HERE???
        String extId = null;

        if( dtd() != null )
        {
            extId = "";
            // PUBLIC and SYSTEM
            if( this.dtd.getPubId() != null )
            {
                extId += " PUBLIC '" + dtd.getPubId() + "' '" + dtd.getSysId() + "'";
            }
            // SYSTEM only
            else if( this.dtd.getSysId() != null )
            {
                extId += " SYSTEM '" + dtd.getSysId() + "'";
            }
            // no ExternalId
            else
            {
                return null;
            }
        }
        return extId;
    }


    /**
     * @return the XML declaration for this instance
     */
    public String getXmlDecl( String version, String encoding, boolean standalone )
    {
        String textdecl = "<?xml version='1.0'" + " encoding='" + encoding + "'";
        if( standalone )
        {
            textdecl += " standalone='yes'";
        }
        textdecl += "?>";
        return textdecl;
    }


    /**
     * @return The document prolog: the XML declaration and the doctype declaration.
     * Note that because the XML and DOCTYPE decls do not have associated events, any
     * (allowed) comments, processing instructions or whitespace between the two will
     * appear <em>after</em> the prolog.

     public String getProlog()
     {
     String prolog = getXmlDecl( "1.0", Swix.outputEncoding, false ) + getDoctypeDecl();
     return prolog;
     }*/

    /**
     * @return The current containing instance. Overridden to return <code>null</code>, since
     * this is the outermost container.
     */
    public Instance containingInstance()
    {
        return null;
    }


    /**
     * @return The local name of the document element.
     */
    public String docType()
    {
        //		parse();	//WHY DID WE NEED TO PARSE HERE???
        return this.doctype;
    }


    /**
     * Accesses the system identifier of this instance.
     * @return the system identifier of this instance as it was passed to
     * the constructor
     */
    public String sysId()
    {
        return this.sysId;
    }


    /**
     * Returns the absolute URI of the instance, resolved against ???
     */
    public String getResolvedURI()
    {
        if( resolvedUri != null )
            return resolvedUri;

        resolvedUri = this.sysId;

        try
        {
            if( logger.isDebugEnabled() )
            {
                logger.debug( "resolving " + this.sysId + " against user.dir="
                        + System.getProperty( "user.dir" ) );
            }
            resolvedUri = XMLEntityManager.expandSystemId( this.sysId, null, false ); //(true=strict resolution); 2nd arg=null uses user.dir
        }
        catch( MalformedURIException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_FATAL, e.getMessage() ) );
        }
        return resolvedUri;
    }


    /**
     * @return This instance's governing DTD; <code>null</code> if there is none.
     */
    public Dtd dtd()
    {
        return this.dtd;
    }


    /**
     * Adds a new ParseError to this Instance.
     */
    public ParseMessage addParseMessage( String msg, Locator loc, String type )
    {
        ParseMessage pe = makeParseMessage( msg, loc, type );
        this.errs.add( pe );
        incrErrorCount( type );
        return pe;
    }


    /**
     * Adds a new ParseError to this Instance.
     */
    protected ParseMessage addParseMessage( SAXParseException spe, String type )
    {
        ParseMessage pe = makeParseMessage( spe, type );
        this.errs.add( pe );
        incrErrorCount( type );
        return pe;
    }


    /**
     * Factory method. Creates a new ParseMessage object.
     * @see ParseMessage
     */
    protected ParseMessage makeParseMessage( String msg, Locator loc, String type )
    {
        return new ParseMessage( session.getApplication(), msg, loc, type );
    }


    /**
     * Factory method. Creates a new ParseMessage object.
     * @see ParseMessage
     */
    protected ParseMessage makeParseMessage( SAXParseException spe, String type )
    {
        return new ParseMessage( session.getApplication(), spe, type );
    }


    /**
     * Accesses the parsing errors generated for this instance.
     * @return iterator over the parse errors
     */
    public List parseErrors()
    {
        return this.errs;
    }


    /**
     * Calculates the total number of parser messages associated with this instance. 
     * @return the total number of messages for this instance
     */
    public int getErrorCount()
    {
        return this.errs.size();
    }


    /**
     * Calculates the total number of fatal errors associated with this instance. 
     * @return the total number of fatal errors for this instance
     */
    public int getFatalErrorCount()
    {
        return this.fatal;
    }


    /**
     * Calculates the total number of non-fatal errors associated with this instance. 
     * @return the total number of non-fatal errors for this instance
     */
    public int getNonFatalErrorCount()
    {
        return this.non_fatal;
    }


    /**
     * Calculates the total number of warnings associated with this instance. 
     * @return the total number of warnings for this instance
     */
    public int getWarningCount()
    {
        return this.warning;
    }


    private void incrErrorCount( String type )
    {
        if( type.equals( Constants.ERROR_TYPE_FATAL ) )
        {
            fatal++;
        }
        if( type.equals( Constants.ERROR_TYPE_NON_FATAL ) )
        {
            non_fatal++;
        }
        if( type.equals( Constants.ERROR_TYPE_WARNING ) )
        {
            warning++;
        }

    }


    /**
     * Returns the parsing errors in format specified.
     * @return a string of parsing errors in the format specified
     */
    public String parseErrors( int format )
    {
        StringBuffer s = new StringBuffer();
        if( format == Constants.ERRORS_AS_XML || format == Constants.ERRORS_AS_HTML )
        {
            Iterator pes = this.parseErrors().iterator();
            while( pes.hasNext() )
            {
                ParseMessage pe = ( ParseMessage )pes.next();
                if( ! areWarningsIssuable && pe.getType() == Constants.ERROR_TYPE_WARNING )
                {
                }
                else
                {
                    s.append( pe.asXml() );
                }
            }
        }
        if( format == Constants.ERRORS_AS_TEXT )
        {
            Iterator pes = this.parseErrors().iterator();
            ParseMessage pe = null;
            while( pes.hasNext() )
            {
                pe = ( ParseMessage )pes.next();
                s.append( pe.asText() );
            }
        }
        return s.toString();
    }


    boolean isWellFormed()
    {
        return this.isWellFormed;
    }


    /**
     * Requests that the external id information for this instance be omitted on output.
     *
     */
    public void omitExternalId()
    {
        this.isExtIdOmissible = true;
    }


    /**
     * Accesses the associated <code>Session</code>.
     * @return the <code>Session</code> associated with this instance
     */
    public Session session()
    {
        return this.session;
    }


    /**
     * Sets the <code>Dtd</code> for this instance.
     * @param dtd the <code>Dtd</code>
     * @return the <code>Dtd</code> so set
     */
    Dtd setDtd( Dtd dtd )
    {
        this.dtd = dtd;
        return dtd;
    }


    public StringBuffer normalized()
    {
        return this.normal;
    }

    private QueryHandler qaHandler;


    public void setQueryHandler( QueryHandler qaHandler )
    {
        this.qaHandler = qaHandler;
    }


    void setRelaxNGValidator( ValidationDriver validator )
    {
        this.validator = validator;
        if( logger.isDebugEnabled() )
            logger.debug( "RELAX NG validator set: " + validator );
    }

}