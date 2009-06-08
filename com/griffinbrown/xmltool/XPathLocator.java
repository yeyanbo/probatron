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

/**
 * Represents a node locator, expressed in XPath 1.0 syntax.
 * 
 * 
 * @author andrews
 * @version $Revision: 1.2 $
 * 
 * @version $Id: XPathLocator.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 * 
 */
public interface XPathLocator
{
	/**
	 * Whether this locator is absolute. 
	 * 
	 * @return whether the XPath locator is absolute, that is, specified with 
	 * the document root as its context. If this is <code>false</code>, then the XPath
	 * locator is relative, i.e. dependent on the context in which it occurs.
	 */
	boolean isAbsolute();
			
}