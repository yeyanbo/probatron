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
 * Created on 2 Nov 2007
 */
package com.griffinbrown.xmltool;

/**
 * An exception signalling the termination of a processing session.
 * An exception of this type may be used to notify clients that the session
 * is ending without terminating the virtual machine.
 * 
 * @author andrews
 *
 * $Id$
 */
public class SessionTerminationException extends RuntimeException
{

    public SessionTerminationException()
    {
    // TODO Auto-generated constructor stub
    }


    public SessionTerminationException( String arg0 )
    {
        super( arg0 );
        // TODO Auto-generated constructor stub
    }


    public SessionTerminationException( Throwable arg0 )
    {
        super( arg0 );
        // TODO Auto-generated constructor stub
    }


    public SessionTerminationException( String arg0, Throwable arg1 )
    {
        super( arg0, arg1 );
        // TODO Auto-generated constructor stub
    }

}
