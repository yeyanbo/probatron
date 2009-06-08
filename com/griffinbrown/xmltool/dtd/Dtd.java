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
 * Internal revision information: @version $Id: Dtd.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
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

package com.griffinbrown.xmltool.dtd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.SessionRegistry;
import com.griffinbrown.xmltool.XmlConstruct;

/**
 * Class to represent an XML DTD.
 * 
 * The class represents a DTD's overall semantics ... in other words, <B>what</B> the DTD means
 * and not <B>how</B> it has been authored.
 * 
 * @author $Author: GBDP\andrews $
 * 
 * @version @version $Id: Dtd.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 */
public class Dtd extends XmlConstruct
{
    private ArrayList etds = new ArrayList();
    private ArrayList forDecls = new ArrayList();
    private ArrayList ents = new ArrayList();
    private ArrayList extEnts = new ArrayList();
    private ArrayList notats = new ArrayList();
    private ArrayList unpeds = new ArrayList();
    private Hashtable dict = new Hashtable();
    private boolean hasResolvedChildren;

    String sysId;
    String pubId;


    /**
     * The constructor must be passed an Instance object.
     * @see Instance
     */
    public Dtd( Instance inst )
    {
        super( inst );
    }

    /**
     * Returns the hashcode of the normalized string of this DTD.
     */
    public int hashCode()
    {
        return this.asNormalizedXml( 0 ).hashCode();
    }


    /**
     * Adds a new element type declaration to this DTD.
     * @param name the name of the element
     * @param cm a representation of its content model
     */
    public ElemTypeDecl addElemTypeDecl( String name, String cm )
    {
        ElemTypeDecl etd = makeElemTypeDecl( name, cm );
        this.etds.add( etd );
        this.dict.put( name, etd );
        return etd;
    }


    /**
     * Adds a new notation declaration to this DTD.
     * @param name the name of the notation
     * @param pubId the public identifier for the notation
     * @param systemId the system identifier for the notation
     */
    public NotationDecl addNotationDecl( String name, String pubId, String systemId )
    {
        NotationDecl n = makeNotationDecl( name, pubId, systemId );
        this.notats.add( n );
        return n;
    }


    /**
     * Adds a new unparsed entity declaration to this DTD.
     * @param name the name of the entity
     * @param value the value of the entity 
     * 
     */
    public UnparsedEntityDecl addUnparsedEntityDecl( String name, String pubId,
            String systemId, String notationName )
    {
        UnparsedEntityDecl u = makeUnparsedEntityDecl( name, pubId, systemId, notationName );
        this.unpeds.add( u );
        return u;
    }


    /**
     * Adds a new general entity declaration to this DTD.
     * @param name the name of the entity
     * @param value the value of the entity 
     */
    public EntityDecl addEntityDecl( String name, String value )
    {
        EntityDecl e = makeEntityDecl( name, value );
        this.ents.add( e );
        return e;
    }


    /**
     * Adds an external entity declaration.
     * @param name the name of the entity
     * @param pubId the public identifier for the entity
     * @param systemId the system identifier for the entity
     */
    public ExternalEntityDecl addExternalEntityDecl( String name, String pubId, String systemId )
    {
        ExternalEntityDecl e = makeExternalEntityDecl( name, pubId, systemId );
        this.extEnts.add( e );
        return e;
    }


    /**
     * Factory method. Creates a new ElemTypeDecl object.
     * @see ElemTypeDecl
     */
    protected ElemTypeDecl makeElemTypeDecl( String name, String cm )
    {
        return new ElemTypeDecl( this.containingInstance(), name, cm, SessionRegistry
                .getInstance().getCurrentSession().getContentHandler().cloneLocator() );
    }


    /**
     * Factory method. Creates a new NotationDecl object.
     * @see ElemTypeDecl
     */
    protected NotationDecl makeNotationDecl( String name, String pubId, String systemId )
    {
        return new NotationDecl( this.containingInstance(), name, pubId, systemId );
    }


    /**
     * Factory method. Creates a new UnparsedEntityDecl object.
     * @see ElemTypeDecl
     */
    protected UnparsedEntityDecl makeUnparsedEntityDecl( String name, String pubId,
            String systemId, String notationName )
    {
        return new UnparsedEntityDecl( this.containingInstance(), name, pubId, systemId,
                notationName );
    }


    /**
     * Factory method. Creates a new EntityDecl object.
     * @see ElemTypeDecl
     */
    protected EntityDecl makeEntityDecl( String name, String value )
    {
        return new EntityDecl( this.containingInstance(), name, value );
    }


    /**
     * Factory method. Creates a new ExternalEntityDecl object.
     * @see ElemTypeDecl
     */
    protected ExternalEntityDecl makeExternalEntityDecl( String name, String pubId,
            String systemId )
    {
        return new ExternalEntityDecl( this.containingInstance(), name, pubId, systemId );
    }


    /**
     * Accesses a specfic element type declaration. 
     * @param name the local name of the element whose type declaration is being requested
     * @return the ElemTypeDecl object representing the element declared; <code>null</code> if not found
     * @see ElemTypeDecl
     */
    public ElemTypeDecl getElemTypeDeclByName( String name )
    {
        return ( ElemTypeDecl )this.dict.get( name );
    }


    /**
     * Accesses the element type declarations for this DTD. 
     * @return an Iterator of the ElemTypeDecls
     */
    public Iterator elemDecls()
    {
        return this.etds.iterator();
    }


    /**
     * @return ArrayList of ElemTypeDecls
     */
    private ArrayList etds()
    {
        return this.etds;
    }


    /**
     * @return an Iterator of forward declarations
     */
    private Iterator forDecls()
    {
        return this.forDecls.iterator();
    }


    /**
     * Accesses the notation declarations for this DTD.
     * @return an Iterator of the NOTATIONS declared by this DTD.
     */
    public Iterator notationDecls()
    {
        return this.notats.iterator();
    }


    /**
     * Accesses unparsed entity declarations for this DTD.
     * @return an Iterator of the unparsed entities declared by this DTD.
     */
    public Iterator unparsedEntityDecls()
    {
        return this.unpeds.iterator();
    }


    /**
     * Accesses the entity declarations for this DTD.
     * @return an Iterator of the general entities declared by this DTD.
     */
    public Iterator entityDecls()
    {
        return this.ents.iterator();
    }


    /**
     * @return an Iterator of the general entities declared by this DTD.
     */
    private Iterator externalEntityDecls()
    {
        return this.extEnts.iterator();
    }


    /**
     * Accesses the number of element type declarations in this DTD. 
     * @return the number of elements declared by this DTD.
     */
    public int elemDeclCount()
    {
        return this.etds.size();
    }


    /**
    * @return this DTD as normalised XML.
    */
    public String asNormalizedXml( int emissionType )
    {
        // TODO: cache the string value for this, and mark the object
        // as dirty every time a mutative method is called - this will
        // knock-on to improvement in hashcode() method.

        //TODO: override Dtd for SwiX purposes, to reinstate this feature
        boolean singleAttlists = true; //this.containingInstance().session().getSingleAttlists();

        StringBuffer buf = new StringBuffer();
        Collections.sort( this.etds );

        // First all the Element type declarations
        Iterator e = this.elemDecls();
        while( e.hasNext() )
        {
            // Add the element declaration ...
            ElemTypeDecl etd = ( ElemTypeDecl )e.next();
            etd.sortAttDeclsByType();
            etd.sortAttDecls();
            buf.append( etd.asNormalizedXml( emissionType ) );

            // ... and any associated Attribute declarations
            Iterator atts = etd.attDecls();

            if( atts.hasNext() )
            {
                /*if (emissionType == EMIT_PRETTY)
                {
                	s.append("\n");
                }*/
                buf.append( "<!ATTLIST " + etd.name() );
                buf.append( singleAttlists && emissionType == EMIT_PRETTY ? "\n          "
                        : " " );

                while( atts.hasNext() )
                {
                    AttDecl a = ( AttDecl )atts.next();
                    if( a.isBound() )
                    {
                        buf.append( a.asNormalizedXml( emissionType ) );
                        if( atts.hasNext() )
                        {
                            //use single ATTLISTs
                            if( singleAttlists )
                            {
                                if( emissionType == EMIT_PRETTY )
                                {
                                    buf.append( "\n          " );
                                }
                                else
                                {
                                    buf.append( " " );
                                }
                            }
                            else
                            {
                                buf.append( ">" );
                                if( emissionType == EMIT_PRETTY )
                                {
                                    buf.append( "\n" );
                                }
                                buf.append( "<!ATTLIST " + a.assocElemDecl().name() + " " );
                            }

                        }
                    }
                }
                buf.append( ">" );
                if( emissionType == EMIT_PRETTY )
                {
                    buf.append( "\n" );
                }
            }
            if( emissionType == EMIT_PRETTY )
            {
                buf.append( "\n" );
            }

        }

        // Forward declarations (attributes with no associated element)
        e = this.forDecls();
        while( e.hasNext() )
        {
            ElemTypeDecl etd = ( ElemTypeDecl )e.next();
            Iterator loneAtts = etd.attDecls();
            if( loneAtts.hasNext() && emissionType == EMIT_PRETTY )
            {
                buf.append( "\n" );
            }
            while( loneAtts.hasNext() )
            {
                AttDecl a = ( AttDecl )loneAtts.next();
                buf.append( a.asNormalizedXml( emissionType ) );
                if( emissionType == EMIT_PRETTY )
                {
                    buf.append( "\n" );
                }
            }
        }

        //sort entity decls
        Collections.sort( this.ents );
        e = this.entityDecls();
        if( e.hasNext() && emissionType == XmlConstruct.EMIT_PRETTY )
        {
            buf.append( "\n\n\n" ); // space it out a bit if we're emitted nicely
        }

        // we don't want to emit duplicated entity decls, so this Hashtable
        // keeps toll
        Hashtable done = new Hashtable();

        while( e.hasNext() )
        {
            EntityDecl etd = ( EntityDecl )e.next();

            if( done.get( etd._name ) == null )
            {
                buf.append( etd.asNormalizedXml( emissionType ) );
                done.put( etd._name, etd._name );
            }
        }

        // External Entity declarations
        e = this.externalEntityDecls();
        while( e.hasNext() )
        {
            ExternalEntityDecl xd = ( ExternalEntityDecl )e.next();
            buf.append( xd.asNormalizedXml( emissionType ) );
        }

        // Notation declarations
        e = this.notationDecls();
        while( e.hasNext() )
        {
            NotationDecl nd = ( NotationDecl )e.next();
            buf.append( nd.asNormalizedXml( emissionType ) );
        }

        // Unparsed entity declarations
        e = this.unparsedEntityDecls();
        while( e.hasNext() )
        {
            UnparsedEntityDecl ued = ( UnparsedEntityDecl )e.next();
            buf.append( ued.asNormalizedXml( emissionType ) );
        }

        return buf.toString();
    }

    /**
     * Resolves element declarations to identify elements referenced but not declared.
     * Internal use only. 
     */
    public void resolveChildren()
    {
        //System.err.println( "Resolving children..." );
        if( hasResolvedChildren )
        {
            return;
        }

        // Resolve the children
        Iterator en = this.elemDecls();
        while( en.hasNext() )
        {
            ElemTypeDecl e = ( ElemTypeDecl )en.next();
            //System.err.println( "Resolving element: " + e.name() );
            String cm = e.contentModel();

            // sanity check
            if( cm.length() == 0 )
            {
                throw new RuntimeException( "Empty content model!" );
            }

            String[] sa = elemTokens( cm, false );
            for( int i = 0; i < sa.length; i++ )
            {
                String name = sa[ i ];
                // skip reserved words
                if( ! ( name.equals( "#PCDATA" ) || name.equals( "ANY" ) || name
                        .equals( "EMPTY" ) ) )
                {
                    // See if there's been an element declared with the name
                    ElemTypeDecl child = getElemTypeDeclByName( name );

                    // Uh oh -- ref'd but not declared
                    if( child == null )
                    {
                        if( this.containingInstance().session().issueWarnings() )
                        {
                            this
                                    .containingInstance()
                                    .addParseMessage(
                                            "Element \""
                                                    + name
                                                    + "\" is referenced in content model for element \""
                                                    + e.name()
                                                    + "\" but is not itself declared.",
                                            e.location(), Constants.ERROR_TYPE_WARNING );
                        }

                    }
                    else
                    // found
                    {
                        if( child.contentModel().length() == 0 )
                        {
                            throw new RuntimeException( "Empty content model!" );
                        }
                        e._children.add( child );
                        //System.err.println( name + " is a child of " + e.name() );
                    }
                }
            }

        }

        //System.err.println( "Children resolved!" );
        hasResolvedChildren = true;
    }


    private String[] elemTokens( String contentModel, boolean includeTokens )
    {
        // find all the types in the content model
        StringTokenizer st = new StringTokenizer( contentModel, "|,()?*+ ", includeTokens );
        String[] sa = new String[ st.countTokens() ];

        int i = 0;
        while( st.hasMoreElements() )
        {
            String s = st.nextToken();
            sa[ i ] = s;
            i++;
        }

        return sa;
    }


    /**
     * Handles 'forward declarations',
     * i.e. element type declarations created in
     * anticipation of a content model that never materialized, and
     * issues a warning to this effect, if required.
     * @see section 3.3 of the XML Recommendation 1.0
     */
    public void forwardDecls()
    {
        ArrayList realEtds = new ArrayList();
        Iterator e = this.elemDecls();
        while( e.hasNext() )
        {
            ElemTypeDecl etd = ( ElemTypeDecl )e.next();

            // Test for duplicate attdecls
            //Enumeration allAtts = etd.attDecls();
            //System.err.println( "Examining: " + etd.name() );
            //Vector attNames = new Vector();
            /*while( allAtts.hasMoreElements() )
            {
            	AttDecl a = ( AttDecl )allAtts.nextElement();
            	if( attNames.indexOf( a.name() ) != -1 )
            	{
            		//System.err.println( "Attribute " + a.name() + " already declared!" );
            		if( Swix.areWarningsIssuable )
            		{
            			this.containingInstance().addParseError( "Duplicate declaration of attribute \"" + a.name() + "\" for element type \"" + etd.name() + "\".", a.location(), ParseMessage.ERROR_TYPE_WARNING );
            		}
            	}
            
            	//System.err.println( an );
            }*/

            if( etd.contentModel().equals( "" ) )
            {
                // fordecls are stored separately, and reported
                forDecls.add( etd );
                if( this.containingInstance().session().issueWarnings() )
                {
                    // create a new ParseMessage for each lone attribute
                    Iterator loneAtts = etd.attDecls();
                    while( loneAtts.hasNext() )
                    {
                        AttDecl a = ( AttDecl )loneAtts.next();
                        this.containingInstance().addParseMessage(
                                "Attribute \"" + a.name()
                                        + "\" declared for undeclared element \"" + etd.name()
                                        + "\".", a.location(), Constants.ERROR_TYPE_WARNING );
                    }
                }
            }
            else
            {
                realEtds.add( etd );
                // add element decl to vector of bona fide decls
                //System.err.println( "Approving " + etd.name() );
            }
        }

        this.etds = null;
        this.etds = realEtds;

        // fix-up the hashtable
        Hashtable ht = new Hashtable();
        e = this.elemDecls();
        while( e.hasNext() )
        {
            ElemTypeDecl etd = ( ElemTypeDecl )e.next();
            ht.put( etd.name(), etd );
        }

        this.dict = ht;

    }


    /**
     * Accesses the public identifier of this DTD. 
     * @return the PUBLIC ID of this DTD, or null if there is none.
     */
    public String getPubId()
    {
        return this.pubId;
    }


    /**
     * Accesses the system identifier of this DTD.
     * @return the SYSTEM id of this DTD, as it was specified in the Instance under this DTD's control.
     */
    public String getSysId()
    {
        return this.sysId;
    }


    /**
     * @return number of entity declarations (of all types) for this DTD.
     */
    private int entityDeclCount()
    {
        return ents.size() + extEnts.size() + notats.size() + unpeds.size();
    }

    /**
     * Sets the public identifier for this DTD.
     * @param pubId the public identifier
     */
    public void setPubId( String pubId )
    {
        this.pubId = pubId;
    }

    /**
     * Sets the system identifer for this DTD.
     * @param sysId the system identifer
     */
    public void setSysId( String sysId )
    {
        this.sysId = sysId;
    }
}
