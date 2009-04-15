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
package org.probatron.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI.MalformedURIException;
import org.probatron.QueryHandler;
import org.probatron.ShailNavigator;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.function.StringFunction;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.Constants;

/**
 * @author andrews
 *
 * Class to represent the XPath <tt>document()</tt> function.
 * Note that this enhanced function supports caching of documents and aliasing
 * of URIs. The class is a singleton, so that cached and aliased documents may be
 * accessed by multiple processing sessions.
 */
public class DocumentFunction implements Function
{
    private HashMap cache = null;
    private HashMap urlMap = null;
    private static DocumentFunction INSTANCE = new DocumentFunction();
    //    private CustomDocumentNavigator navigator = CustomDocumentNavigator.getSingleton();
    private EntityResolver resolver = null;
    private boolean debug;
    private String baseURI;
    private QueryHandler qaHandler;
    private static Logger logger = Logger.getLogger( DocumentFunction.class );
    private ShailNavigator navigator = ShailNavigator.getInstance();


    private DocumentFunction()
    {
        this.cache = new HashMap();
        this.urlMap = new HashMap();
    }


    public static DocumentFunction getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * Clears the internal document cache.
     */
    public static void clearCache()
    {
        INSTANCE.cache.clear();
    }


    public static DocumentFunction newInstance( QueryHandler qaHandler )
    {
        INSTANCE.qaHandler = qaHandler;
        //        INSTANCE.navigator.setXPathQAHandler( qaHandler );
        INSTANCE.debug = qaHandler.getSession().getApplication().getProperty(
                Constants.DEBUG_MODE ).equals( Boolean.TRUE );
        return INSTANCE;
    }


    public Object call( Context context, List args ) throws FunctionCallException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "document() called with context=" + context + ", args=" + args );

        int argCount = args.size();

        if( argCount == 1 )
        {
            Object arg = args.get( 0 );
            if( arg instanceof List )
            {
                return document( ( List )arg );
            }
            else
            {
                return document( StringFunction.evaluate( arg, navigator ) );
            }
        }

        else if( argCount == 2 ) //second node-set argument specifies base URI
        {
            Object arg1 = args.get( 0 );
            Object arg2 = args.get( 1 );

            if( ! ( arg2 instanceof List ) )
                throw new FunctionCallException(
                        "second argument to document() must be of type node-set" );

            if( arg1 instanceof List )
            {
                return document( ( List )arg1, ( ShailList )arg2 );
            }
            else
            {
                return document( StringFunction.evaluate( arg1, navigator ), ( ShailList )arg2 );
            }
        }
        else
        {
            throw new FunctionCallException( "document() requires 1 or 2 arguments" );
        }
        //return evaluate( url, navigator ); //if the second arg (node) has no associated location, fall back
    }


    /**
     * See XSLT 1.0 Recommendation (http://www.w3.org/TR/xslt):
     * "When the document function has exactly one argument and the argument 
     * is a node-set, then the result is the union, for each node in the 
     * argument node-set, of the result of calling the document function with 
     * the first argument being the string-value of the node, and the second 
     * argument being a node-set with the node as its only member."
     * @param nodeset
     * @return
     */
    private Object document( List nodeset ) throws FunctionCallException
    {
        List results = new ArrayList( nodeset.size() );
        Iterator iter = ( nodeset.iterator() );
        String url;
        while( iter.hasNext() )
        {
            url = StringFunction.evaluate( iter.next(), navigator );
            if( logger.isDebugEnabled() )
                logger.debug( "calling document(nodeset); url=" + url );
            results.add( evaluate( url ) );
        }
        return results;
    }


    /**
     * See XSLT 1.0 Recommendation (http://www.w3.org/TR/xslt):
     * "When the document function has two arguments and the first argument
     *  is a node-set, then the result is the union, for each node in the 
     *  argument node-set, of the result of calling the document function with
     *   the first argument being the string-value of the node, and with the 
     *   second argument being the second argument passed to the document function."
     *   
     * "The base URI (see [3.2 Base URI]) of the node in the second argument 
     * node-set that is first in document order is used as the base URI for 
     * resolving the relative URI into an absolute URI."  
     * @param nodeset
     * @param resolutionBase
     * @return
     */
    private Object document( List nodeset, ShailList resolutionBase )
            throws FunctionCallException
    {
        if( resolutionBase.isEmpty() )
            throw new FunctionCallException(
                    "second argument to document() must not be an empty node-set" );

        ShailList results = new ShailList( nodeset.size() );
        ShailIterator iter = ((ShailList)nodeset).shailListIterator();
        String url;

        //base URI is that of the 1st node in the 2nd argument
        Locator loc = ( Locator )qaHandler.getLocatorForNode( resolutionBase.getInt( 0 ) );

        while( iter.hasNext() )
        {
            url = StringFunction.evaluate( iter.nextNode(), navigator );

            if( logger.isDebugEnabled() )
                logger.debug( "calling document(nodeset,nodeset); url=" + url + "; baseURI="
                        + ( loc == null ? loc.toString() : loc.getSystemId() ) );

            if( loc != null && loc.getSystemId() != null )
            {
                ShailList doc = (ShailList)evaluate( url, loc.getSystemId() );
                results.addInt( doc.getInt( 0 ) );
            }
            else
            {
                ShailList doc = (ShailList)evaluate( url );
                results.addInt( doc.getInt( 0 ) );
            }
        }
        return results;
    }


    /**
     * See XSLT 1.0 Recommendation (http://www.w3.org/TR/xslt):
     * "When the first argument to the document function is not a node-set, 
     * the first argument is converted to a string as if by a call to the 
     * string function." 
     * @param url
     * @return
     */
    private Object document( String url ) throws FunctionCallException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "calling document(url) " + url );
        return evaluate( url );
    }


    /**
     * See XSLT 1.0 Recommendation (http://www.w3.org/TR/xslt):
     * "When the first argument to the document function is not a node-set, 
     * the first argument is converted to a string as if by a call to the 
     * string function."
     *   
     * "The base URI (see [3.2 Base URI]) of the node in the second argument 
     * node-set that is first in document order is used as the base URI for 
     * resolving the relative URI into an absolute URI." 
     * @param url
     * @param nodeset
     * @return
     */
    private Object document( String url, ShailList resolutionBase )
            throws FunctionCallException
    {
        //base URI is that of the 1st node in the 2nd argument
        Locator loc = ( Locator )qaHandler.getLocatorForNode( resolutionBase.getInt( 0 ) );

        if( logger.isDebugEnabled() )
            logger.debug( "calling document(url,nodeset); url=" + url );

        if( logger.isDebugEnabled() && loc != null )
            logger.debug( "calling document(url,nodeset); baseURI=" + loc.getSystemId() );

        if( loc != null && loc.getSystemId() != null )
        {
            return evaluate( url, loc.getSystemId() );
        }
        return evaluate( url );
    }


    public Object evaluate( String url ) throws FunctionCallException
    {
        return evaluate( url, this.baseURI );
    }


    public Object evaluate( String url, String base ) throws FunctionCallException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "getting document " + url + " with base URI " + base );

        //resolve the document URI relative to the default base URI (i.e. that of the *configuration* file)
        try
        {
            url = XMLEntityManager.expandSystemId( url, base, false ); //(true=strict resolution)
        }
        catch( MalformedURIException e )
        {
            throw new FunctionCallException( e );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "resolved URI=" + url );

        //remapped to another URL?
        if( urlMap.get( url ) != null )
        {
            url = ( String )urlMap.get( url ); //remapped!
            if( logger.isDebugEnabled() )
                logger.debug( "remapped url=" + url ); //DEBUG
        }

        //already cached?
        if( isCached( url ) )
        {
            if( logger.isDebugEnabled() )
                logger.debug( "***the document was cached***" );
            return cache.get( url );
        }

        //not cached: open the document
        if( logger.isDebugEnabled() )
            logger.debug( "using navigator " + navigator );
        
        Object o = navigator.getDocumentAsObject( url );

        if( logger.isDebugEnabled() )
            logger.debug( "caching " + url );

        cache.put( url, o ); //cache it!

        if( logger.isDebugEnabled() )
            logger.debug( "cache=" + cache );

        return o;
    }


    /**
     * @param url url (or other unique identifier) of file
     * @return whether a document corresponding to <tt>url</tt> has been cached
     */
    public boolean isCached( String url )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "cache=" + cache );
        return ( cache.get( url ) != null );
    }


    /**
     * Maps a url to a different url, for instance when parsing documents contained
     * in temporary files.
     * @param url url as the function will receive it
     * @param actualUrl the url where it should in fact retrieve the document from
     */
    public void mapUrl( String actualUrl, String alias )
    {
        if( logger.isDebugEnabled() )
            logger.debug( this + " mapping " + actualUrl + " to " + alias );
        urlMap.put( actualUrl, alias );
    }


    /**
     * 
     * @param alias
     * @return whether a document with the alias has been mapped 
     */
    public boolean isMapped( String alias )
    {
        return urlMap.get( alias ) != null;
    }


    /**
     * Accessor method for map of URLs to cached documents
     * @return
     */
    public HashMap getMap()
    {
        return urlMap;
    }


    /**
     * Note that this method has the side-effect of setting the EntityResolver for
     * the DocumentNavigator used.
     * @param er
     */
    public void setEntityResolver( EntityResolver er )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "setting DocumentFunction EntityResolver=" + er );

        resolver = er;

        navigator.setEntityResolver( er );

        if( logger.isDebugEnabled() )
            logger.debug( navigator.getClass().getName() + " EntityResolver=" + er );
    }


    public void setBaseURI( String uri )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "base URI set to:" + uri );
        this.baseURI = uri;
    }


    /**
     * Sets the XPathQAHandler associated with this function.
     * N.B. this is set by default in the constructor. This method
     * is provided to allow the handler to be REset.
     * @param qaHandler
     */
    public void setXPathQAHandler( QueryHandler qaHandler )
    {
        this.qaHandler = qaHandler;
    }


    /**
     * 
     * @param d
     * @return the URL for this document, if cached, otherwise <code>null</code>
     
    public String getURLForDocument( int doc )
    {
        logger.debug( "getting URL for document=" + doc );
        Object docObj = new ShailSingletonList( doc );
        if( this.cache.containsValue( docObj ) )
        {
            Set keys = ( Set )cache.keySet();
            Iterator iter = keys.iterator();
            while( iter.hasNext() )
            {
                String url = ( String )iter.next();
                if( cache.get( url ) == docObj )
                {
                    logger.debug( "returning url=" + url );
                    return url;
                }
            }
            logger.debug( "returning cache=" + cache.toString() );
            return cache.toString();
        }
        logger.debug( "returning null" );
        return null;
    }*/

    public void cache( Object url, Object doc )
    {
        logger.debug( "caching url=" + url + "; doc=" + doc );
        this.cache.put( url, doc );
        logger.debug( "cache=" + cache );
    }
}