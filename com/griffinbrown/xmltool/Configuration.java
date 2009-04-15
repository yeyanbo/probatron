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
 * Created on 11 Nov 2007
 */
package com.griffinbrown.xmltool;

import java.util.List;

/**
 * A configuration for a validation session. 
 * 
 * @author andrews
 * @see Session
 * @see Session#setConfig(Configuration)
 * 
 *
 * @version $Id: Configuration.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public interface Configuration
{
    //    Session getSession();

    /**
     * Sets the system id of the file to be validated.
     * @param uri the system id of the input file
     */
    void setInputFile( String uri );

    /**
     * Accesses the system id of the file to be validated. 
     * @return the system id of the input file
     */
    String getInputFile();

    /**
     * Adds an output target to the configuration.
     * @param uri the destination URI of the output
     */
    void addOutput( String uri );


//    void parse() throws XMLToolException;


    /**
     * Adds an extension to the configuration.
     * @param ec the configuration to add
     * @throws XMLToolException if the extension cannot be added
     */
    void addExtension( ExtensionConfiguration ec ) throws XMLToolException;

    /**
     * Whether the configuration has any extensions. 
     * @return whether the configuration has any extensions
     */
    boolean hasExtensions();

    /**
     * Accesses the extensions added to this configuration.
     * @return a List of extensions
     */
    List getExtensions();

    /**
     * Adds a namespace declaration to the configuration.
     * @param prefix the namespace prefix 
     * @param uri the namespace URI
     */
    void addNamespaceDecl( String prefix, String uri );

    /**
     * Accesses the namespaces declared for this configuration.
     * @return a List of namespace declarations
     */
    List getNamespaceDecls();

    /**
     * Accesses the system id of the configuration file. 
     * @return the system id of the configuration
     */
    String getSystemId();

    /**
     * Requests a normalized XML representation of the configuration.
     * @return the string of the normalized XML
     */
    String asNormalizedXml();

    /**
     * Adds a variable declaration to the configuration.
     * @param name the variable name
     * @param value the variable value, as an expression
     */
    void addVariable( String name, String value );
    
    /**
     * Adds a variable whose value has been evaluated to the configuration.
     * @param variable the variable value to add
     */
    void addCompiledVariable( Object variable );

    /**
     * Accesses variables declared for this configuration.
     * @return a List of variables
     */
    List getVariables();
    
    /**
     * Sets the session to which the configuration applies.
     * @param session the session to configure
     */
    void setSession( Session session );
    
    /**
     * Accesses the session for which the configuration is in force.
     * @return the session
     */
    Session getSession();
    
    /**
     * Accesses the XMLReader the configuration specifies.
     * @return the XMLReader specified 
     */
    ExtensionConfiguration getXMLReader();
    
    /**
     * Sets the expression language for this configuration.
     * @param exprLang the expression language to be used
     */
    void setExpressionLanguage( String exprLang );
    
    /**
     * Accesses the expression language for the configuration.
     * @return the string of the expression language, or <code>null</code> if none has been declared
     */
    String getExpressionLanguage();
    
    /**
     * Accesses the queries in force for this validation configuration.
     * @return a List of queries
     */
    List getQueries();
}
