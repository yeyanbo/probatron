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
package org.probatron;

import java.io.IOException;
import java.util.HashMap;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author andrews
 *
 * Class to represent
 */
public class CustomEntityResolver implements EntityResolver
{
	private HashMap map = new HashMap();
	private boolean debug;

	public CustomEntityResolver( boolean debug )
	{
		this.debug = debug;
		debug("created entity resolver "+this);
	}

	private CustomEntityResolver()
	{}

	/**
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId)
		throws SAXException, IOException
	{
		debug( "retrieving entity mapping for PUBLIC=" + publicId + ", SYSTEM=" + systemId );

		if( map.containsKey( systemId ) )
		{
			debug( "found key "+(String)map.get( systemId ) );
			return new InputSource( (String)map.get( systemId ) );
		}
		else if( map.containsKey( publicId ) )
		{
			debug( "found key "+(String)map.get( publicId ) );
			return new InputSource( (String)map.get( publicId ) );
		}

		debug("***ENTITY NOT MAPPED***");

		return null;
	}

	public void addMapping( String from, String to )
	{
		debug("adding mapping: "+from+" -> "+to);
		map.put( from, to );
	}

	private void debug( String s )
	{
		if( debug )
			System.err.println( "[debug]:" + s );
	}

}
