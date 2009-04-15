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
package com.griffinbrown.xmltool;

/**
 * Represents an application interface.
 */
public interface Application
{
    /**
     * Accesses the common prefix for application features.
     * @return the string of the prefix
     */
	String featuresPrefix();
	
	/**
	 * Accesses the name of the application.
	 * @return the name of the application
	 */
	String name();	
	
	/**
	 * Accesses the application version.
	 * @return the application version
	 */
	String getVersion();
	
	/**
	 * The preferred namespace prefix to be used in XML reports from this app.
	 * Note that this should not violate the XML concept of a <code>QName</code>,
	 * if well-formed output is required, and must not be <code>null</code>.
	 * @return namespace prefix associated with this application
	 */
	String namespacePrefix();
	
	/**
	 * The namespace URI associated with this application.
	 * @return URI associated with this application
	 */
	String namespaceUri();
	
	/**
	 * Requests the application to terminate with a specified exit code.
	 * @param exitCode the exit code
	 */
	void terminate( int exitCode );
	
	/**
	 * Cues the display of help information to the user, typically at the command line.
	 */
	void showHelp();
	
	/**
	 * Cues the display of the application banner, typically at the command line.
	 */
	void showBanner();
	
	/**
	 * Accesses the value of a specified property by its name.
	 * @param name the name of the property
	 * @return the value of the property, or <code>null</code> if no such property exists
	 */
	Object getProperty( String name );
	
}
