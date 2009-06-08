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

package com.griffinbrown.xmltool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * A registry for application sessions.
 * 
 * @author andrews
 *
 * $Id$
 */
public class SessionRegistry
{
    private static final SessionRegistry INSTANCE = new SessionRegistry();
    private Logger logger = Logger.getLogger( SessionRegistry.class );
    private Map threads2sessions = Collections.synchronizedMap( new HashMap() );


    private SessionRegistry()
    {}

    /**
     * Accesses the registry instance.
     * @return the registry instance
     */
    public static SessionRegistry getInstance()
    {
        return INSTANCE;
    }

    /**
     * Registers a session.
     * @param session the session to register
     */
    public void register( Session session )
    {
        Thread t = Thread.currentThread();
        threads2sessions.put( t, session );
        if( logger.isDebugEnabled() )
            logger.debug( "mapped thread " + t + " to " + session );
    }

    /**
     * Accesses the current session.
     * @return the current session
     */
    public Session getCurrentSession()
    {
        return ( Session )threads2sessions.get( Thread.currentThread() );
    }
}
