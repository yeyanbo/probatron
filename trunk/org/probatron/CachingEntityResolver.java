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
 * Copyright (c) 2004 Griffin Brown Digital Publishing Ltd.
 * All rights reserved.
 */
package org.probatron;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class to represent an EntityResolver which caches any InputSources it returns.
 *
 * @author andrews
 */
public class CachingEntityResolver implements EntityResolver
{
    private HashMap map = new HashMap(); //expected-sys-id:actual-sys-id
    private HashMap inputSources = new HashMap(); //expected-sys-id:InputSource
    private boolean debug;
//    private static CachingEntityResolver INSTANCE = new CachingEntityResolver();
    private static Logger logger = Logger.getLogger( CachingEntityResolver.class );


    public static CachingEntityResolver getInstance( boolean debug )
    {
//        INSTANCE.debug = debug;
//        if( debug )
//            if( logger.isDebugEnabled() )
//                logger.debug( "returning entity resolver singleton " + INSTANCE );
//        return INSTANCE;
        return new CachingEntityResolver();
    }


    private CachingEntityResolver()
    {}


    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity( String publicId, String systemId ) throws SAXException,
            IOException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "retrieving entity mapping for PUBLIC=" + publicId + ", SYSTEM="
                    + systemId );

        if( map.containsKey( systemId ) )
        {
            String actualSysId = ( String )map.get( systemId );
            if( logger.isDebugEnabled() )
                logger.debug( "found key " + actualSysId );

            if( ! inputSources.containsKey( systemId ) )
            {
                if( logger.isDebugEnabled() )
                    logger.debug( "adding InputSource for sys id " + systemId );
                inputSources.put( systemId, new InputSource( actualSysId ) );
            }
            if( logger.isDebugEnabled() )
                logger.debug( "returning " + ( InputSource )inputSources.get( systemId )
                        + " for sysId " + systemId );
            return ( InputSource )inputSources.get( systemId );
        }
        else if( map.containsKey( publicId ) )
        {
            String actualPubId = ( String )map.get( publicId );
            if( logger.isDebugEnabled() )
                logger.debug( "found key " + actualPubId );

            if( ! inputSources.containsKey( publicId ) )
            {
                if( logger.isDebugEnabled() )
                    logger.debug( "adding InputSource for pub id " + publicId );
                inputSources.put( publicId, new InputSource( actualPubId ) );
            }
            return ( InputSource )inputSources.get( systemId );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "***ENTITY NOT MAPPED***" );

        return null;
    }


    public void addMapping( String from, String to )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "adding mapping: " + from + " -> " + to );
        map.put( from, to );
    }
}
