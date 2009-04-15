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
 * Created on 03-Jun-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.griffinbrown.xmltool;

import org.xml.sax.Locator;


/**
 * Represents a message to be shown to the user.
 */
public interface Message
{
	/**
	 * @return the string of this <code>Message</code> as XML
	 */
	String asXml();
	
	/**
	 * @return the string of this <code>Message</code> as plain text
	 */
	String asText();
	
	/**
	 * @return the string of this <code>Message</code>'s type
	 */
	String getType();
	
	/**
	 * @return the string associated with this <code>Message</code>
	 */
	String getString();
	
	/**
	 * @return the <code>Locator</code> associated with this <code>Message</code>
	 */
	Locator getLocator();
	
	/**
	 * @return the line number associated with this <code>Message</code>
	 */
	int getLineNumber();
	
	/**
	 * @return the column number associated with this <code>Message</code>
	 */
	int getColumnNumber();
	
	/**
	 * @return the system id associated with this <code>Message</code>, or null
	 * if none has been assigned
	 */
	String getSystemId();
	
	/**
	 * @return the <code>XPathLocator</code> associated with this <code>Message</code>
	 */
	XPathLocator getXPathLocator();
	 
	
	
	
}
