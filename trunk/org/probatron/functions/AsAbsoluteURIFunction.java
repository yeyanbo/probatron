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
 * Created on 04-Jan-2006
 */
package org.probatron.functions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI.MalformedURIException;
import org.probatron.QueryHandler;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.function.StringFunction;
import org.xml.sax.Locator;

import com.griffinbrown.shail.util.ShailList;

public class AsAbsoluteURIFunction implements Function
{
    private Logger logger = Logger.getLogger(AsAbsoluteURIFunction.class);
    private static final AsAbsoluteURIFunction INSTANCE = new AsAbsoluteURIFunction();
    private String baseURI;
    private QueryHandler qaHandler;
    
    private AsAbsoluteURIFunction()
    {}
    
    public static AsAbsoluteURIFunction getInstance( QueryHandler qaHandler, String baseURI )
    {
        INSTANCE.baseURI = baseURI;
        INSTANCE.qaHandler = qaHandler;
        return INSTANCE;
    }
    
    /**
     * @see org.probatron.jaxen.Function#call(org.probatron.jaxen.Context, java.util.List)
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        String s = null;
        
        if( args.size() == 1 )
        {
            s = StringFunction.evaluate( args.get( 0 ), context.getNavigator() );
            if(logger.isDebugEnabled())
                logger.debug( "string arg=" + s + "; baseURI=" + this.baseURI );
            return evaluate( s );
        }

        else if( args.size() == 2 ) //second node-set argument specifies base URI
        {
            if( ! ( args.get( 1 ) instanceof List ) )
                throw new FunctionCallException(
                        "second argument to as-absolute-uri() must be of type node-set" );

            ShailList nodeset = ( ShailList )args.get( 1 );
            if( nodeset.isEmpty() )
                throw new FunctionCallException(
                        "second argument to as-absolute-uri() must not be an empty node-set" );
            else
            {
                s = StringFunction.evaluate( args.get( 0 ), context.getNavigator() );
                int node = nodeset.getInt( 0 );
                //resolve against base URI of this node's document
                Locator loc = ( Locator )qaHandler.getLocatorForNode( node );
                if( loc != null && loc.getSystemId() != null )
                {
                    logger.debug( "sys id of node passed as 2nd arg to as-absolute-uri()="
                            + loc.getSystemId() );
                }
                return evaluate( s, loc.getSystemId() );
            }
        }
        else
        {
            throw new FunctionCallException("as-absolute-uri() requires one or two arguments");
        }
    }
    
    private Object evaluate( String url ) throws FunctionCallException
    {
        return evaluate( url, this.baseURI );   //use the base URI derived from the configuration file
    }
    
    private Object evaluate( String s, String baseURI ) throws FunctionCallException
    {
        //resolve relative to base URI (in this impl, of *configuration* file)
        try
        {
            s = XMLEntityManager.expandSystemId( s, baseURI, false ); //(true=strict resolution)
        }
        catch( MalformedURIException e )
        {
            throw new FunctionCallException( e );
        }

        URI uri = null;
        try
        {
            uri = new URI( s );
        }
        catch( URISyntaxException e )
        {
            throw new FunctionCallException( e );
        }
        
        return uri.toString();        
    }
}
