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

import java.util.List;

import org.probatron.ShailNavigator;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

/**
 * @author andrews
 *
 * Class to represent in-built XPath extension function.
 * This function tests whether a node is contained by a CDATA section. In practice, 
 * this means whether a text node (as XPath sees it) is in fact a CDATA section. 
 */
public class IsInCdataFunction implements Function
{

	/**
	 * Default constructor. 
	 */
	public IsInCdataFunction()
	{
		super();
	}

	/**
	 * @see org.probatron.jaxen.Function#call(org.probatron.jaxen.Context, java.util.List)
	 */
	public Object call( Context context, List args ) throws FunctionCallException
	{
		if (args.size() == 1)
		{
			return evaluate( args.get(0) );
		}

		throw new FunctionCallException( "in-cdata-section() takes 1 argument." );
	}
	
	/**
	 * 
	 * @param arg the (text) node to be evaluated
	 * @param nav
	 * @return a list of nodes of type org.w3c.dom.Node.CDATA_SECTION_NODE, or 
	 * an empty list if none are found.
	 */
	private Object evaluate( Object arg )
	{
		ShailList results = new ShailList();
		
		if( ! ( arg instanceof ShailList ) )
		{
			return results;
		}
		
		
		ShailIterator iter = (ShailIterator)((ShailList)arg).iterator();
		
		while( iter.hasNext() )
		{
			int node = iter.nextNode();
			ShailNavigator nav = ShailNavigator.getInstance();
			if( nav.containsCDATA( node ) )
			{
				results.addInt( node );
			}			
		}
		return results;
	}

}
