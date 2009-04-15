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
 * Copyright (c) 2003 Griffin Brown Digital Publishing Ltd.
 * All rights reserved.
 */
package org.probatron.functions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

/**
 * @author andrews
 *
 * Class to represent 
 */
public class FileExistsFunction implements Function
{
    private String baseURI;
    private QueryHandler qaHandler;
    private static FileExistsFunction INSTANCE = new FileExistsFunction(); 
    private static Logger logger = Logger.getLogger( FileExistsFunction.class );


    private FileExistsFunction()
    {}
    
    public static FileExistsFunction getInstance( QueryHandler qaHandler, String baseURI )
    {
        INSTANCE.qaHandler = qaHandler;
        INSTANCE.baseURI = baseURI;
        return INSTANCE;
    }
    
    /**
     * @see org.probatron.jaxen.Function#call(org.probatron.jaxen.Context, java.util.List)
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        String url = null;
        
        if( args.size() == 1 )
        {
            url = StringFunction.evaluate( args.get( 0 ), context.getNavigator() );
            logger.debug( "getting document " + url );
            return evaluate( url );
        }

        else if( args.size() == 2 ) //second node-set argument specifies base URI
        {
            if( ! ( args.get( 1 ) instanceof List ) )
                throw new FunctionCallException(
                        "second argument to file-exists() must be of type node-set" );

            ShailList nodeset = ( ShailList )args.get( 1 );
            if( nodeset.isEmpty() )
                throw new FunctionCallException(
                        "second argument to file-exists() must not be an empty node-set" );
            else
            {
                url = StringFunction.evaluate( args.get( 0 ), context.getNavigator() );
                int node = nodeset.getInt( 0 );
                //resolve against base URI of this node's document
                Locator loc = ( Locator )qaHandler.getLocatorForNode( node );
                if( loc != null && loc.getSystemId() != null )
                {
                    logger.debug( "sys id of node passed as 2nd arg to file-exists()="
                            + loc.getSystemId() );
                }
                return evaluate( url, loc.getSystemId() );
            }
        }
        else
        {
            throw new FunctionCallException("file-exists() requires one or two arguments");
        }
    }
    
    private Object evaluate( String url ) throws FunctionCallException
    {
        return evaluate( url, this.baseURI );	//use the base URI derived from the configuration file
    }
    
    protected Object evaluate( String url, String baseURI ) throws FunctionCallException
    {
        ArrayList result = new ArrayList();

        //resolve relative to base URI (in this impl, of *configuration* file)
        try
        {
            url = XMLEntityManager.expandSystemId( url, baseURI, false ); //(true=strict resolution)
        }
        catch( MalformedURIException e )
        {
            throw new FunctionCallException( e );
        }

        URI uri = null;
        try
        {
            uri = new URI( url );
        }
        catch( URISyntaxException e )
        {
            throw new FunctionCallException( e );
        }

        File f = null;
        try
        {
            f = new File( uri );
        }
        catch( IllegalArgumentException e )
        {
            throw new FunctionCallException( e );
        }

        logger.debug("looking for file: " + uri + "; path="+uri.getPath() );
        logger.debug("exists? "+f.exists());

        if( f.exists() )
        {
            result.add( url );
        }

        return result;        
    }

}