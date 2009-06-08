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
 * Created on 17 Jul 2007
 */
package org.probatron;

import java.util.List;

import org.xml.sax.EntityResolver;

import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.Session;

/**
 * Document builder for the Shail document model. 
 */
public class ShailTreeBuilder extends BuilderImpl
{
    private Session session;
    private CachingEntityResolver resolver = null;
    //    private XPathLocationTracker xpathLocationTracker;

    private static final String FEATURE_REMAP_ENTITY = "remap-entity";

    /**
     * Constructor for normal use.
     * @param instance the instance the builder will construct a model of
     * @param session the session of processing the builder belongs to
     */
    public ShailTreeBuilder( Instance instance, Session session )
    {
        super( instance, session );
        this.session = session;
    }


    /**
     * Configure the behaviour of this client.
     * @param featureValuePairs a Vector of other attributes for this feature, in this
     * case typically a set of behaviours for this feature
     * @param uri URI unique to this feature
     */
    public void setFeature( String uri, List featureValuePairs )
    {
        super.setFeature( uri, featureValuePairs ); //DO NOT OMIT!!

        /*more features here*/

    }


    public void preParse()
    {
        super.preParse();

        if( this.resolver != null )
        {
            session.parser().setEntityResolver( resolver );
            //            resolver.setBaseURI( session.getConfig().getSystemId() );
        }
    }


    //    /**
    //     * @see com.griffinbrown.shail.Builder#endDocument()
    //     */
    //    public void endDocument()
    //    {
    //        super.endDocument();
    //
    //        xpathLocationTracker.endDocument();
    //    }
    //
    //
    //    /**
    //     * @see com.griffinbrown.shail.Builder#endElement(java.lang.String, java.lang.String, java.lang.String)
    //     */
    //    public void endElement( String uri, String localName, String name )
    //    {
    //        super.endElement( uri, localName, name );
    //
    //        xpathLocationTracker.endElement( uri, localName, name );
    //    }
    //
    //
    //    /**
    //     * @see com.griffinbrown.shail.Builder#startDocument()
    //     */
    //    public void startDocument()
    //    {
    //        super.startDocument();
    //
    //        xpathLocationTracker = new XPathLocationTracker();
    //        xpathLocationTracker.startDocument();
    //    }
    //
    //
    //    /**
    //     * @see com.griffinbrown.shail.Builder#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    //     */
    //    public void startElement( String uri, String localName, String qName, Attributes atts )
    //    {
    //        super.startElement( uri, localName, qName, atts );
    //
    //        xpathLocationTracker.startElement( uri, localName, qName, atts );
    //    }

    /////////////////////////////////////////////
    /* ACCESSOR METHODS FOR USE BY SUB-CLASSES */
    /////////////////////////////////////////////
    
    /**
     * Retrieves the SAX entity resolver used by this builder.
     */
    public EntityResolver getEntityResolver()
    {
        return this.resolver;
    }

}
