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

package org.probatron;


/**
 * <p>Represents a processing instruction node test, as part of an <code>XPathLocator</code>.</p>
 * <p>Note that no distinction is currently made between unnamed PIs and named PIs
 * (i.e. those with a named target), since for location purposes the predicate
 * is sufficient. The implementation of explicit targets may improve 
 * performance in interpretative tools.</p>
 */
public class ProcessingInstruction extends NodeTest
{
	private static final String VALUE = "processing-instruction()";
	
	ProcessingInstruction( int node )
	{
		super( node );
	}
	
	public String getValue()
	{
		return VALUE + getPredicate();
	}	
}
