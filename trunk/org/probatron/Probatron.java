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

/**
 * Part of the com.griffinbrown.xmltool package
 * 
 * XML utility classes.
 * 
 * Developed by:
 * 
 * Griffin Brown Digital Publishing Ltd. (http://www.griffinbrown.com).
 * 
 * Please note this software uses the Xerces-J parser which is governed by the The Apache
 * Software License, Version 1.1, which appears below.
 * 
 * See http://xml.apache.org for further details of Apache software.
 * 
 * Internal revision information: @version $Id: Probatron.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 * 
 */

/**
 * This application uses Apache's Xerces XML parser which is covered by the Apache software
 * license (below).
 * 
 * The Apache Software License, Version 1.1
 * 
 * 
 * Copyright (c) 1999 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/)." Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Xerces" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", nor may "Apache" appear in
 * their name, without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally based on software copyright (c) 1999,
 * International Business Machines, Inc., http://www.ibm.com. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */

/**
 * Redistribution and use of this software and associated documentation ("Software"), with or
 * without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain copyright statements and notices.
 * Redistributions must also contain a copy of this document.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * 3. The name "DOM4J" must not be used to endorse or promote products derived from this
 * Software without prior written permission of MetaStuff, Ltd. For written permission, please
 * contact dom4j-info@metastuff.com.
 * 
 * 4. Products derived from this Software may not be called "DOM4J" nor may "DOM4J" appear in
 * their names without prior written permission of MetaStuff, Ltd. DOM4J is a registered
 * trademark of MetaStuff, Ltd.
 * 
 * 5. Due credit should be given to the DOM4J Project (http://dom4j.org/).
 * 
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Copyright 2001 (C) MetaStuff, Ltd. All Rights Reserved.
 */

/*
 * XMLProbe
 * 
 * Copyright (c) 2003 Griffin Brown Digital Publishing Ltd. All rights reserved.
 * 
 * @version $Id: Probatron.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 */
package org.probatron;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xerces.impl.Version;
import org.xml.sax.InputSource;

import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.xmltool.Application;
import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Class to represent XMLProbe's command-line interface.
 * For information on how to invoke XMLProbe programmatically, see 
 * <a href='doc-files/invoking-xmlprobe.htm'>this document</a>.
 */
public class Probatron implements Application
{
    private static final String CVS_REVISION_DATE = "$Date: 2009/01/13 10:38:22";

    //XMLProbe
    public static final String VERSION = "0.1a";
    public static final String PROBATRON_NS = "http://probatron.org/200901";
    public static final String FEATURES_PREFIX = "http://probatron.org/features/";
    private static final Probatron theInstance = new Probatron();
    private static HashMap props = new HashMap() {
        {
            //flags
            put( Constants.DEBUG_MODE, Boolean.FALSE );
            put( Constants.TIMING_MODE, Boolean.FALSE );
        }
    };
    private StringBuffer report = new StringBuffer();
    private StringBuffer qaMessages = new StringBuffer();
    private Session x;

    //features
    static final String BATCH_PROCESS = "batch-processing-mode";
    static final String OPTIMISE_REPORTS = "optimise-reports";
    public static final String EMIT_RULESET = "emit-normalized-ruleset";
    private static final Logger logger = Logger.getLogger( Probatron.class );
    private static boolean isLoggerInitialized;


    protected static void initLogger( String[] args )
    {
        String logLvl = "INFO";

        for( int i = 0; i < args.length; i++ )
        {
            if( args[ i ].equals( "-d" ) )
            {
                logLvl = "DEBUG";
                break;
            }
        }

        // set up log message format, etc.
        Properties p = new Properties();
        p.setProperty( "log4j.rootCategory", logLvl + ", A1" );
        p.setProperty( "log4j.appender.A1", "org.apache.log4j.ConsoleAppender" );
        p.setProperty( "log4j.appender.A1.target", "System.err" );
        p.setProperty( "log4j.appender.A1.layout", "org.apache.log4j.PatternLayout" );
        p.setProperty( "log4j.appender.A1.layout.ConversionPattern",
                "[%p][%d{DATE}]:%m [%C{1}][%t]%n" );

        PropertyConfigurator.configure( p );
        isLoggerInitialized = true;
    }


    protected static boolean isLoggerInitialized()
    {
        return isLoggerInitialized;
    }


    Probatron()
    {}


    /**
     * Default constructor.
     */
    Probatron( String[] argv )
    {
        String inputFile = null;

        showBanner();

        setDebug( argv );

        if( argv.length < 1 )
        {
            showHelp();
            terminate( - 1 );
        }

        if( argv.length > 1 )
        {
            inputFile = argv[ 1 ];
        }

        //create a new session
        try
        {
            this.x = new ProbatronSession( this, new InputSource( inputFile ) );
            x.setConfig( new SchematronConfiguration( argv[ 0 ], x ) );
            x.run();
            x.emitReport();
        }
        catch( XMLToolException e )
        {
            Utils.emitToError( e.getMessage() );
            terminate( - 1 );
        }
    }


    public void showBanner()
    {
        System.err
                .println( "Probatron version "
                        + VERSION
                        + " build #"
                        + buildId()
                        + '\n'
                        + "Copyright (c) Griffin Brown Digital Publishing Ltd 2009. All rights reserved." );
        System.err.println( "This version compiled with " + Version.getVersion() );
    }


    public void showHelp()
    {
        System.err
                .println( "\nusage: java org.probatron.Probatron [Schematron-schema] [XML-document]" );
    }


    public static void main( String[] argv )
    {
        initLogger( argv );
        new Probatron( argv );
    }


    /**
     * Provides last-ditch opportunity to output errors, perform housekeeping, etc.
     */
    private void onExit()
    {
    //do nothing
    }


    public final String getVersion()
    {
        return VERSION;
    }


    public static final String buildId()
    {
        String dateNorm = CVS_REVISION_DATE.replaceAll( "[^0-9]+", "" );

        return Integer.toHexString( Integer.parseInt( dateNorm.substring( 0, 8 ) ) ) + '-'
                + dateNorm.substring( 8 );
    }


    /**
     * @return the class which extensions to this application should extend 
     */
    public static Class extensionClass()
    {
        return Constants.ADD_IN_CLASS;
    }


    /**
     * @see com.griffinbrown.xmltool.Application#featuresPrefix()
     */
    public String featuresPrefix()
    {
        return FEATURES_PREFIX;
    }


    /**
     * Causes the application to exit.
     */
    public void terminate( int exitCode )
    {
        onExit();
        logger.debug( "XMLProbe is terminating..." );
        System.exit( exitCode );
    }


    /**
     * @see com.griffinbrown.xmltool.Application#name()
     */
    public String name()
    {
        return "XMLProbe";
    }


    /**
     * @see com.griffinbrown.xmltool.Application#namespacePrefix()
     */
    public String namespacePrefix()
    {
        return "probe";
    }


    /**
     * @see com.griffinbrown.xmltool.Application#namespaceUri()
     */
    public String namespaceUri()
    {
        return PROBATRON_NS;
    }


    /**
     * Accesses an immutable instance, for cases where a reference to the application
     * is required. 
     * @return an immutable XMLProbe instance
     */
    public static Probatron getInstance()
    {
        return theInstance;
    }


    /**
     * @see com.griffinbrown.xmltool.Application#getProperty(java.lang.String)
     */
    public Object getProperty( String name )
    {
        return props.get( name );
    }


    /**
     * @return Returns the qaMessages.
     */
    StringBuffer getQAMessages()
    {
        return qaMessages;
    }


    /**
     * @return Returns the report.
     */
    StringBuffer getReport()
    {
        return report;
    }


    private void setDebug( String[] args )
    {
        for( int i = 0; i < args.length; i++ )
        {
            if( args[ i ].equals( "-d" ) )
            {
                props.put( Constants.DEBUG_MODE, Boolean.TRUE );
                break;
            }
        }
    }
}