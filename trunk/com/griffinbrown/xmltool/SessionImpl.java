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
 * Internal revision information: @version $Id: SessionImpl.java,v 1.1 2006/07/12 11:02:14 GBDP\andrews
 * Exp $
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

package com.griffinbrown.xmltool;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI.MalformedURIException;
import org.probatron.InMemoryMessageHandler;
import org.probatron.ReportEmitter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.griffinbrown.jing.ext.ValidatePropertyExt;
import com.griffinbrown.schematron.NamespaceDeclaration;
import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.schematron.SchematronReportEmitter;
import com.griffinbrown.xmltool.utils.FilteredXMLReaderCreator;
import com.griffinbrown.xmltool.utils.Utils;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.rng.CompactSchemaReader;

/**
 * Represents a session of parsing under an XMLTool application.
 */
public class SessionImpl implements Session
{
    //default options
    private short errorFormat = Constants.ERRORS_AS_TEXT;
    private String outputEncoding = Constants.ENC_UTF8;
    private short emissionMode = Constants.DEFAULT_EMISSION_MODE;

    //housekeeping storage
    private ArrayList extensions = new ArrayList();
    private ArrayList sessionMessages = new ArrayList(); //parser and session msgs
    private ArrayList qaMessages = new ArrayList(); //QA msgs
    private ArrayList children;

    //flags
    private boolean showLog;
    private boolean areWarningsIssuable;
    private boolean isParserDefault = true;
    private boolean useXInclude;
    private boolean isTerminating;
    private boolean emitsReportOnExit = true;
    private boolean emitsReportAutomatically;
    private boolean applicationTerminatesWhenSessionTerminates = true;

    private Configuration config;
    private String inputFile;
    private Instance instance;
    private String outputFile;
    private String xmlFilter;
    private String stylesheetLoc;
    private String xsltEngine;
    private String relaxNGSchema;
    private InputSource inputSource;

    private XMLReader parser = null;
    private ContentHandler ch = null;
    private XMLFilter filter = null;
    private Session parentSession = null;
    private boolean hasParent = false;
    private String exprLang = null;
    private HashMap msgMap; //map of QA msg types to msgs
    private ArrayList nsDecls;

    //misc
    private Date start;
    private Application app;
    private String prefix;
    private MessageHandler messageHandler = new InMemoryMessageHandler(); //default
    private ReportEmitter reportEmitter; //default
    private PrintStream printStream = System.out; //default
    private static Logger logger = Logger.getLogger( SessionImpl.class );

    //RELAX NG stuff
    private ValidationDriver validator;
    private PropertyMapBuilder validatorProperties;
    private SchemaReader schemaReader;


    /**
     * The constructor for normal use, for default sessions.
     * @param config configuration file for this session, 
     * passed in at the command line.
     * @param inputSource InputSource of XML file to be processed with this configuration.
     * This may be passed to the application from the command line, though it may also be
     * an InputSource passed in when a Session is initialized internally.
     * If <tt>inputSource</tt> is <tt>null</tt>, the input defaults to that specified in
     * the configuration file.
     */
    public SessionImpl( Application app, InputSource inputSource )
    {
        this.app = app;
        this.prefix = app.namespacePrefix();
        this.inputSource = inputSource;
        this.reportEmitter = new SchematronReportEmitter( this );
    }


    /**
     * Constructor for a Session instantiated by a parent Session.
     * In this case, the child session is configured from scratch, and no settings
     * are inherited from the parent's configuration.
     * @param config sys id of the new configuration for this child session
     * @param parentSession session instantiating the child session
     
     public SessionImpl(Application app, SessionConfiguration config, InputSource inputSource, Session parentSession ) throws XMLToolException
     {
     this.app = app;
     this.prefix = app.namespacePrefix();
     this.parentSession = parentSession;

     configParser = new SessionConfigurationParser( this );
     this.config = config;

     //got a config file? parse it!
     if (config.getSystemId() != null)
     {
     config.parse( configParser );
     }

     //choose source of input file
     if ( inputSource == null || inputSource.getSystemId() == null )
     {
     //use the input specified in the configuration file
     this.inputFile = config.getInputFile();
     if( this.inputFile == null )
     {
     throw new XMLToolException(app.name()+":[FATAL]: expected exactly one input file");
     }
     //reassign the InputSource
     inputSource = new InputSource( this.inputFile );
     }
     else
     {
     this.inputFile = inputSource.getSystemId();
     }

     //checkFileExists(this.inputFile);

     //create the Instance for parsing
     instance = new Instance( inputSource, this );
     
     run();
     }*/

    /**
     * Constructor for a Session which uses a pre-existing configuration.
     * @param inputSource the instance to be parsed
     */
    public SessionImpl( InputSource inputSource )
    {
        setDefaultParser();
        this.instance = new Instance( inputSource, this );
        run();
    }


    /**
     * Constructor for a Session instantiated by a parent Session.
     * In this case, the basic settings of the parent session <strong>are</strong>
     * intended to be inherited, e.g. so that the encoding, error format etc. of
     * any output will be uniform.
     * (If all the parent session's settings were inherited (especially the
     * extension(s) causing a new session to be started using this constructor),
     * an infinite loop would probably result.)
     * @param inputSource an org.xml.sax.InputSource to be parsed
     * @param parentSession the parent Session whose configuration is (at
     * least partially) to be inherited by this session
     */
    public SessionImpl( InputSource inputSource, Session parentSession )
    {
        //inherit from parent session
        //        SessionRegistry.getInstance().register( this );
        parser = parentSession.parser();
        config = parentSession.getConfig();
        errorFormat = parentSession.getErrorFormat();
        outputEncoding = parentSession.getOutputEncoding();
        emissionMode = parentSession.getEmissionMode();
        this.parentSession = parentSession;
        //now the parse
        this.instance = new Instance( inputSource, this );
        this.app = parentSession.getApplication();

        /* TODO: REFACTOR!!
         * calling loadExtensions(), configureExtensions() or preParse() here
         * often will cause a java.lang.OutOfMemoryError, because of the recurring
         * configuration documented above.
         * Split the configuration from the file, so that it can be inherited
         * cleanly and without side-effects.*/

        run();
    }


    public void run()
    {
        SessionRegistry.getInstance().register( this );
        checkConfigNotNull();
        checkInputNotNull();
        createInstance();
        preParse();
        parse();
        postParse();
    }


    private void createInstance()
    {
        //create the Instance for parsing
        instance = new Instance( inputSource, this );
    }


    private void checkInputNotNull()
    {
        //choose source of input file
        if( inputSource == null || inputSource.getSystemId() == null )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_FATAL,
                    "expected exactly one input file" ) );
            fatalError();
        }
        else
        {
            this.inputFile = inputSource.getSystemId();
        }
    }


    private void checkConfigNotNull()
    {
        if( config == null )
        {
            fatalError();
        }
    }


    /**
     * Parse the XML instance.
     */
    public void parse()
    {
        instance.parse();
    }


    /**
     * @see Extension
     * This method is used to handle any additional steps required
     * by the client after the parse has finished, i.e. once parse()
     * has returned.
     */
    public void postParse()
    {
        //call client extensions, via content handler
        ch.postParse();

        //output messages
        //TODO: sort this out for use with ConcurrentSessionManager!
        if( emitsReportAutomatically )
            emitReport();
    }


    /**
     * @see Extension
     * This method is used to handle any additional steps required
     * by the client before the parse has begun, i.e. before parse()
     * is called.
     */
    public void preParse()
    {
        start = new Date(); //for benchmarking
        msgMap = new HashMap();
        nsDecls = new ArrayList();

        XMLReader xr = null;

        //set the parser class
        try
        {
            xr = setXMLReader();

            if( logger.isDebugEnabled() )
                logger.debug( "user-specified parser=" + xr );
        }
        catch( XMLToolException e )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_FATAL, e.getMessage() ) );
            fatalError();
        }

        if( xr == null ) //use default parser
        {
            setDefaultParser();
            setParserDefaults();
        }
        else
            setParserFeatures( config.getXMLReader().getFeatures() );

        if( config != null ) //app config
        {
            //load requested extensions, if any
            loadExtensions();
            //configure the parser, now that we have it
            setHandlers();
            configureExtensions();

            //if using RELAX NG, create a validator
            if( relaxNGSchema != null )
            {
                validator = createValidationDriver();
                instance.setRelaxNGValidator( validator );
            }

            //if using RELAX NG, load the schema
            if( validator != null )
            {

                if( logger.isDebugEnabled() )
                {
                    logger.debug( "schema validator error handler="
                            + ValidateProperty.ERROR_HANDLER.get( validatorProperties
                                    .toPropertyMap() ) );
                    logger.debug( "schema validator XMLReaderCreator="
                            + ValidateProperty.XML_READER_CREATOR.get( validatorProperties
                                    .toPropertyMap() ) );
                }
                loadRelaxNGSchema();
            }

        }
        else
        //InputSource
        {
            setHandlers();
        }

        ch.preParse();
    }


    /**
     * Loads requested extension classes for this session.
     */
    private void loadExtensions()
    {
        //loop over them
        Iterator iter = config.getExtensions().iterator();
        while( iter.hasNext() )
        {
            ExtensionConfiguration extConf = ( ExtensionConfiguration )iter.next();

            //The getConstructor method has one parameter:
            //an array of Class objects that correspond to the constructor's parameters.
            Class[] params = new Class[] { this.getInstance().getClass(),
                    this.getSessionClass() };
            try
            {
                extConf.initialize( params, this );
            }
            catch( XMLToolException e )
            {
                Utils.reportFatalError( e.getMessage() );
                fatalError();
            }

            if( logger.isDebugEnabled() )
                logger.debug( "loadExtensions(): extension " + extConf.getClassName()
                        + " loaded" );

            extensions.add( extConf );
        }
    }


    /**
     * Sets the parser class to be loaded.
     * @return the user-specified XMLReader or null if none is specified
     */
    private XMLReader setXMLReader() throws XMLToolException
    {
        //try to find an appropriate XMLReader class to instantiate
        ExtensionConfiguration xmlReaderConfig = config.getXMLReader();

        XMLReader xr = null;

        if( xmlReaderConfig != null )
        {
            if( logger.isDebugEnabled() )
                logger.debug( "setting XMLReader to " + xmlReaderConfig.getClassName() );

            try
            {
                xr = XMLReaderFactory.createXMLReader( xmlReaderConfig.getClassName() );
                isParserDefault = false;
            }
            catch( SAXException e )
            {
                throw new XMLToolException( e.getMessage() );
            }

            //report success
            if( xr != null )
            {
                this.parser = xr;
                addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG,
                        "Setting parser: " + xr.getClass().getName() ) );
            }
        }
        return xr;
    }


    /**
     * Configures the parser to use an XInclude filter.
     * This method should only be called after both XMLReader (the parser) and
     * default ContentHandler have been initialized.
     
     private void setXIncludeFilter()
     {
     includer = new XIncludeFilter();
     includer.setParent( parser );	//sets the reader for the XIncluder filter
     includer.setContentHandler( ch );
     includer.setErrorHandler( eh );
     try
     {
     includer.setProperty( Constants.SAX_PROPERTY_LEXICAL_HANDLER, ch );
     }
     catch ( SAXNotRecognizedException nre )
     {
     addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "SAX property not recognized: " +
     nre.getMessage() ) );
     }
     catch ( SAXNotSupportedException nse )
     {
     addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "SAX property not supported: " +
     nse.getMessage() ) );
     }
     }*/

    /**
     * Sets the XMLFilter to be used for this session's parse.
     
     private void setXMLFilter()
     {
     try
     {
     filter = (XMLFilter)Class.forName( xmlFilter ).newInstance();
     //parser = filter;
     }
     catch (ClassNotFoundException e)
     {
     addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "class not found: " + e.getMessage() ) );
     }
     catch (IllegalAccessException e)
     {
     addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "IllegalAccessException: " + e.getMessage() ) );
     }
     catch (InstantiationException e)
     {
     addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "InstantiationException: " + e.getMessage() ) );
     }
     }*/

    //	private void configureFilter()
    //	{
    //		filter.setParent( parser );
    //		filter.setContentHandler( ch );
    //		filter.setErrorHandler( eh );
    //	}
    /**
     * Sets the SAX handlers for the parse.
     */
    private void setHandlers()
    {
        //now we have the parser, configure it
        ch = new ContentHandler( instance );

        parser.setContentHandler( ch );
        parser.setDTDHandler( ch );
        parser.setErrorHandler( ch );

        try
        {
            parser.setProperty( Constants.SAX_PROPERTY_LEXICAL_HANDLER, ch );
            parser.setProperty( Constants.SAX_PROPERTY_DECLARATION_HANDLER, ch );
        }
        catch( SAXNotRecognizedException e1 )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL,
                    "SAX property not recognized" ) );
        }
        catch( SAXNotSupportedException e2 )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL,
                    "SAX property not supported" ) );
        }

        if( relaxNGSchema != null ) //we're using RELAX NG
        {
            validatorProperties = new PropertyMapBuilder();
            ValidateProperty.ERROR_HANDLER.put( validatorProperties, ch );
        }
    }


    /**
     * Configures the loaded parser with the features specified.
     * Note that some application-specific features are also set via this
     * method, because they are set using the parser class as a handle.
     */
    private void setParserFeatures( List features )
    {
        Iterator iter = features.iterator();
        while( iter.hasNext() )
        {
            Feature feature = ( Feature )iter.next();
            String featureName = feature.getName();
            Iterator attrIter = feature.getAttributes().iterator();
            while( attrIter.hasNext() )
            {
                FeatureValuePair pair = ( FeatureValuePair )attrIter.next();
                String value = pair.getValue();

                if( featureName.startsWith( app.featuresPrefix() ) ) //application-specific features
                {
                    setCoreFeature( Utils.featureSuffix( featureName, app.featuresPrefix() ),
                            value );
                }
                else
                    setParserFeature( featureName, value );
            }
        }
    }


    /**
     * Sets core application features.
     * @param s the feature name, stripped of its URI
     * @param value the feature value
     */
    private void setCoreFeature( String s, String value )
    {
        boolean valueAsBool = new Boolean( value ).booleanValue();

        //issue-warnings
        if( s.equals( Constants.FEATURE_ISSUE_WARNINGS ) )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG, "Setting feature: "
                    + s + ": " + value ) );
            areWarningsIssuable = valueAsBool;
        }
        //error-format
        else if( s.equals( Constants.FEATURE_ERROR_FORMAT ) )
        {
            if( value.equalsIgnoreCase( Constants.OPT_TEXT ) )
            {
                setErrorFormat( Constants.ERRORS_AS_TEXT );
                addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG,
                        "Setting feature: " + s + ": " + value ) );
            }
            else if( value.equalsIgnoreCase( Constants.OPT_XML ) )
            {
                setErrorFormat( Constants.ERRORS_AS_XML );
                addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG,
                        "Setting feature: " + s + ": " + value ) );
            }
            else if( value.equalsIgnoreCase( Constants.OPT_HTML ) )
            {
                errorFormat = Constants.ERRORS_AS_HTML;
                addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG,
                        "Setting feature: " + s + ": " + value ) );
            }
            else
            {
                addMessage( new SessionMessage( app, Constants.ERROR_TYPE_WARNING,
                        "error format '" + value + "' not supported" ) );
            }
            logger.debug( "error format for session set to " + this.errorFormat );
        }

        //encoding
        else if( s.equals( Constants.FEATURE_ENCODING ) )
        {
            if( value.equals( Constants.ENC_US_ASCII ) || value.equals( Constants.ENC_UTF16 )
                    || value.equals( Constants.ENC_UTF8 ) )
            {
                setOutputEncoding( value );
            }
            else
            {
                addMessage( new SessionMessage( app, Constants.ERROR_TYPE_WARNING,
                        "output encoding '" + value + "' not supported" ) );
            }
        }
        else if( s.equals( Constants.FEATURE_SHOW_LOG ) )
        {
            if( valueAsBool )
            {
                showLog = true;
            }
        }
        else if( s.equals( Constants.RELAXNG_COMPACT_SYNTAX_SCHEMA_LOC )
                || s.equals( Constants.RELAXNG_XML_SYNTAX_SCHEMA_LOC ) )
        {
            if( relaxNGSchema == null )
                relaxNGSchema = value;
            else
            {
                Utils.reportNonFatalError( "tried to set RELAX NG schema to: " + value
                        + "; it is already set to: " + relaxNGSchema );
                return; //do NOT allow it to be reset
            }

            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG, "Setting feature: "
                    + s + ": " + value ) );

            if( s.equals( Constants.RELAXNG_COMPACT_SYNTAX_SCHEMA_LOC ) )
                schemaReader = CompactSchemaReader.getInstance();
        }

        /*...other features go here...*/

        else
        {
            setFeature( s, value );
        }

    }


    private void setParserFeature( String name, String value )
    {
        try
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG, "Setting feature: "
                    + name + ": " + value ) );
            parser().setFeature( name, new Boolean( value ).booleanValue() );
        }
        catch( SAXNotRecognizedException e1 )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "SAX feature "
                    + name + " not recognized" ) );
        }
        catch( SAXNotSupportedException e2 )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, "SAX feature "
                    + name + " not supported" ) );
        }
    }


    /**
     * Creates a ValidationDriver for RELAX NG validation.
     * Note that the properties assigned to the validator must all be set BEFORE the validator
     * is constructed. 
     * @return
     */
    private ValidationDriver createValidationDriver()
    {
        PropertyMapBuilder instanceProperties = new PropertyMapBuilder();
        instanceProperties.put( ValidatePropertyExt.SAX_EVENT_RECEIVER, ch );
        instanceProperties.put( ValidateProperty.XML_READER_CREATOR, new FilteredXMLReaderCreator() );

        ValidationDriver vd = new ValidationDriver( validatorProperties.toPropertyMap(),
                instanceProperties.toPropertyMap(), schemaReader );

        if( logger.isDebugEnabled() )
            logger.debug( "RELAX NG validation driver created: " + vd );

        return vd;
    }


    /**
     * Loads a RELAX NG schema for validation.
     */
    private void loadRelaxNGSchema()
    {
        if( logger.isDebugEnabled() )
            logger.debug( "loading RELAX NG schema '" + relaxNGSchema + "'..." );

        //resolve relative URI of schema location AGAINST CONFIGURATION FILE
        String uri = null;
        if( ! ( config instanceof SchematronConfiguration ) )
        {
            try
            {
                uri = XMLEntityManager.expandSystemId( relaxNGSchema, config.getSystemId(),
                        false ); //(true=strict resolution); 2nd arg=base uri
                if( logger.isDebugEnabled() )
                {
                    logger.debug( "RELAX NG schema location " + relaxNGSchema
                            + " resolved against config uri=" + config.getSystemId()
                            + " resolved uri=" + uri );
                }
            }
            catch( MalformedURIException e )
            {
                addMessage( new SessionMessage( getApplication(), Constants.ERROR_TYPE_FATAL, e
                        .getMessage() ) );
            }
        }
        else
        {
            //for Schematron, schema loc is set as sys prop at cmd line, so resolve against cwd
            try
            {
                //appending a path separator to the cwd helps the resolver, else it goes up one dir first(!)
                uri = XMLEntityManager.expandSystemId( relaxNGSchema, System
                        .getProperty( "user.dir" )
                        + "/", false ); //(true=strict resolution); 2nd arg=base uri
                if( logger.isDebugEnabled() )
                {
                    logger.debug( "RELAX NG schema location " + relaxNGSchema
                            + " resolved against config uri=" + config.getSystemId()
                            + " resolved uri=" + uri );
                }
            }
            catch( MalformedURIException e )
            {
                addMessage( new SessionMessage( getApplication(), Constants.ERROR_TYPE_FATAL, e
                        .getMessage() ) );
            }
        }

        boolean isSchemaLoaded = false;
        try
        {
            isSchemaLoaded = validator.loadSchema( new InputSource( uri ) );
        }
        catch( IOException e )
        {
            addMessage( new SessionMessage( getApplication(), Constants.ERROR_TYPE_FATAL,
                    "error loading schema '" + relaxNGSchema + "': IOException: "
                            + e.getMessage() ) );
            fatalError();
        }
        catch( SAXException e )
        {
            addMessage( new SessionMessage( getApplication(), Constants.ERROR_TYPE_FATAL,
                    "error loading schema '" + relaxNGSchema + "': SAXException: "
                            + e.getMessage() ) );
            fatalError();
        }

        if( ! isSchemaLoaded ) //schema did not validate
        {
            addMessage( new SessionMessage( getApplication(), Constants.ERROR_TYPE_FATAL,
                    "error loading schema '" + relaxNGSchema + "': invalid schema" ) );
            fatalError();
        }

        if( logger.isDebugEnabled() )
            logger.debug( "loaded RELAX NG schema" );
    }


    public void setFeature( String feature, String value )
    {}


    public XMLReader parser()
    {
        return this.parser;
    }


    public Instance getInstance()
    {
        return this.instance;
    }


    /**
     * Sets the output file.
     * @param sysId sys id of the output file.
     */
    public void setOutputFile( String sysId )
    {
        if( sysId.equals( inputFile ) )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_FATAL,
                    "input and output sys ids are identical." ) );
            fatalError();
        }

        if( outputFile != null )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_FATAL,
                    "tried to set output file to '" + sysId + "'; output file already set to '"
                            + outputFile + "'" ) );
            fatalError();
        }

        outputFile = sysId;
        try
        {
            this.printStream = new PrintStream( new BufferedOutputStream( new FileOutputStream(
                    outputFile ) ) );
        }
        catch( FileNotFoundException e )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_FATAL,
                    "error setting output file: " + e.getMessage() ) );
        }
    }


    public void setOutputEncoding( String enc )
    {
        setEncoding( enc );
        addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG, "output encoding = "
                + enc ) );
    }


    /**
     * This method actually sets the encoding for this session's outputs.
     * @see #setOutputEncoding
     */
    private void setEncoding( String enc )
    {
        outputEncoding = enc;
    }


    public short getEmissionMode()
    {
        return this.emissionMode;
    }


    public void setEmissionMode( short mode )
    {
        this.emissionMode = mode;
    }


    /**
     * Sets the format in which an error report is emitted. 
     * The default is text.
     * 
     * @see Constants#ERRORS_AS_TEXT
     */
    public void setErrorFormat( short format )
    {
        this.errorFormat = format;
        this.messageHandler.setErrorFormat( format );
    }


    public short getErrorFormat()
    {
        return this.errorFormat;
    }


    /**
     * Sets the default behaviour for this session's parser.
     * Note that this may be overridden by subsequent configuration.
     */
    private void setParserDefaults()
    {
        /* DEFAULT FEATURES:
         * 1. validation
         * 2. namespace prefixes
         */
        setParserFeature( Constants.SAX_FEATURE_VALIDATION, "true" );
        //setParserFeature( SAX_FEATURE_NS_PREFIXES, "true" );
        //setParserFeature( SAX_FEATURE_NSS, "true" );
    }


    private void turnOffValidation()
    {
        setParserFeature( Constants.SAX_FEATURE_VALIDATION, "false" );
    }


    public void addOutputString( String s )
    {}


    /**
     * Configure the loaded extensions for this session.
     */
    private void configureExtensions()
    {
        Iterator exts = extensions.iterator();
        Extension[] ea = new Extension[ extensions.size() ];
        int idx = 0;
        while( exts.hasNext() )
        {
            ExtensionConfiguration extConf = ( ExtensionConfiguration )exts.next();

            this.addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG,
                    "Adding extension: " + extConf.getClassName() ) );

            Extension ext = ( Extension )extConf.getExtensionInstance();
            ea[ idx ] = ext;
            idx++;

            //configure loaded parser class
            if( parser().getClass().getName().equals( extConf.getClassName() ) )
            {
                setParserFeatures( extConf.getFeatures() );
            }
            //configure other loaded extensions
            else
            {
                Extension extension = ( Extension )extConf.getExtensionInstance();
                Iterator iter = extConf.getFeatures().iterator();
                while( iter.hasNext() )
                {
                    Feature feature = ( Feature )iter.next();
                    try
                    {
                        extension.setFeature( Utils.featureSuffix( feature.getName(), app
                                .featuresPrefix() ), feature.getAttributes() );
                    }
                    catch( XMLToolException e )
                    {
                        addMessage( new SessionMessage( app, Constants.ERROR_TYPE_NON_FATAL, e
                                .getMessage() ) );
                    }
                }
            }

        }
        //dispatch Extension array to the ContentHandler
        ch.registerExtensions( ea );
    }


    public Iterator getCustomConfigs()
    {
        //        return this.config.getCustomConfigs();
        return null;
    }


    public boolean issueWarnings()
    {
        return this.areWarningsIssuable;
    }


    /**
     * In the absence of any specified parser, sets the default parser to be used
     * in this session.
     */
    private void setDefaultParser()
    {
        addMessage( new SessionMessage( app, Constants.ERROR_TYPE_INFO,
                "default parser will be used" ) );
        addMessage( new SessionMessage( app, Constants.ERROR_TYPE_LOG,
                "Setting default parser: " + Constants.DEFAULT_SAX_DRIVER ) );

        XMLReader xr = null;
        try
        {
            xr = XMLReaderFactory.createXMLReader( Constants.DEFAULT_SAX_DRIVER );
        }
        catch( SAXException e )
        {
            addMessage( new SessionMessage( app, Constants.ERROR_TYPE_FATAL, e.getMessage() ) );
            fatalError();
        }
        this.parser = xr;
    }


    public String getOutputEncoding()
    {
        return this.outputEncoding;
    }


    public String getInputFile()
    {
        return this.inputFile;
    }


    public Configuration getConfig()
    {
        return config;
    }


    public Date getStart()
    {
        return start;
    }


    /**
     * Adds a message to the report for this session.
     * 
     * In this implementation for SILCN reports, user-defined ("QA") messages and messages generated by
     * the application are handled separately: QA messages are sent to the message handler for this 
     * session. The rest are stored within this session. Both types of message are recombined when the 
     * report is assembled and emitted.  
     */
    public void addMessage( Message msg )
    {
        if( msg.getXPathLocator() != null ) //test for QA msgs
        {
            addQAMessage( msg );
        }
        else
        {
            sessionMessages.add( msg );
        }
    }


    private Message addQAMessage( Message msg )
    {
        this.messageHandler.handle( msg );
        return msg;
    }


    public void emitReport()
    {
        try
        {
            reportEmitter.emitReport();
        }
        catch( Exception e )
        {
            System.err.println( "error emitting report: " + e.getMessage() );
        }
    }


    //TODO: support user-defined XSLT
    /*
     private Source getStylesheet( String uri )
     {
     if( uri.equals( Constants.DEFAULT_XSL_STYLESHEET ) )
     {
     return getDefaultStylesheet();
     }
     else
     {
     return null;
     }

     }*/

    public ReportEmitter getReportEmitter()
    {
        return this.reportEmitter;
    }


    public String parserMessages()
    {
        return instance == null ? "" : instance.parseErrors( errorFormat );
    }


    /**
     * <p>Aborts this session.</p>
     * <p>This is the preferred method of termination, since
     * {@link com.griffinbrown.xmltool.Session#onExit()} is called,
     * allowing any messages to be output before the application exits.</p>
     */
    public void terminate( int exitCode )
    {
        isTerminating = true;
        onExit();
        if( applicationTerminatesWhenSessionTerminates )
            app.terminate( exitCode );
        else
            throw new SessionTerminationException( "session " + this
                    + " terminated with exit code " + exitCode );
    }


    public void onExit()
    {
        if( emitsReportOnExit )
        {
            emitReport();
        } //this should ALWAYS(?) emit messages, else
        //some fatal errors pass by silently
    }


    public boolean isLogShown()
    {
        return this.showLog;
    }


    public List getMessages()
    {
        return this.sessionMessages;
    }


    public void fatalError()
    {
        terminate( - 1 );
    }


    /**
     * Adds a child session to the current session.
     */
    public void addChild( Session child )
    {
        if( this.children == null )
            this.children = new ArrayList();
        this.children.add( child );
    }


    /**
     * @see com.griffinbrown.xmltool.Session#getChildren()
     */
    public List getChildren()
    {
        return this.children;
    }


    public Session getSession()
    {
        return this;
    }


    /**
     * Returns a Class object suitable for the purposes of dynamic class loading.
     * For dynamic class loading, we need a Class object, not an instance. This
     * method is provided to give this information to the class loading mechanism.
     * @return Class object for this Session
     */
    public Class getSessionClass()
    {
        return this.getClass();
    }


    /**
     *
     * @return the XInclude-compliant filter used for this session, or <code>null</code>
     * if none exists
     
     public XMLFilter getXIncludeFilter()
     {
     return this.includer;
     }*/

    /**
     * Whether this session uses an XInclude-compliant filter to read its input
     * @return whether this Session uses XInclude
     */
    public boolean usesXInclude()
    {
        return useXInclude;
    }


    /**
     * @param lang the expression language to use for this session
     */
    public void setExpressionLanguage( String lang )
    {
        this.exprLang = lang;
    }


    /**
     * @return the expression language defined for use by a SILCN configuration
     * of this session
     * @see <a href='http://www.silcn.org'>http://www.silcn.org</a>
     */
    public String getExpressionLanguage()
    {
        return this.exprLang;
    }


    public void addNamespaceDeclaration( NamespaceDeclaration nsd )
    {
        this.nsDecls.add( nsd );
    }


    public void emitToPrintStream( String s )
    {
        try
        {
            Utils.print( s, this.outputEncoding, this.printStream );
            //                        this.printStream.flush();
        }
        catch( UnsupportedEncodingException e )
        {
            Utils.emitToError( "UnsupportedEncodingException: " + e.getMessage() );
        }
    }


    /**
     * @see com.griffinbrown.xmltool.Session#getApplication()
     */
    public Application getApplication()
    {
        return app;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#getFeature(java.lang.String)
     */
    public String getFeature( String name )
    {
        return null;
    }


    public void reset()
    {
        app = null;
        areWarningsIssuable = false;
        ch = null;
        children = null;
        config = null;
        emissionMode = 0;
        errorFormat = 0;
        exprLang = null;
        extensions = null;
        filter = null;
        inputFile = null;
        instance = null;
        isParserDefault = false;
        isTerminating = false;
        sessionMessages = null;
        msgMap = null;
        nsDecls = null;
        outputEncoding = null;
        outputFile = null;
        parentSession = null;
        parser = null;
        prefix = null;
        showLog = false;
        start = null;
        useXInclude = false;
        xmlFilter = null;
    }


    /**
     * @return number of messages related specifically to QA (i.e. not generated by the parser 
     * or for this session) 
     */
    protected int getQAMessageCount()
    {
        int n = 0;
        for( Iterator iter = msgMap.values().iterator(); iter.hasNext(); )
        {
            ArrayList element = ( ArrayList )iter.next();
            n += element.size();
        }
        return n;
    }


    /**
     * @return number of SILCN 1.0 matched-set elements to be generated for this 
     * session
     */
    protected int getMatchedSetCount()
    {
        return this.msgMap.keySet().size();
    }


    /**
     * @see com.griffinbrown.xmltool.Session#hasParentSession()
     */
    public boolean hasParentSession()
    {
        return this.hasParent;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#setParent(boolean)
     */
    public void setParent( boolean b )
    {
        hasParent = b;
    }


    /**
     * Allows derived classes to set the Instance, e.g. for batch processing, 
     * where the Instance may not be known until the configuration file has been 
     * processed. 
     * @param instance
     */
    protected void setInstance( Instance instance )
    {
        this.instance = instance;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#setMessageHandler(com.griffinbrown.xmltool.MessageHandler)
     */
    public void setMessageHandler( MessageHandler mh )
    {
        messageHandler = mh;
        messageHandler.setErrorFormat( getErrorFormat() );
    }


    /**
     * @see com.griffinbrown.xmltool.Session#getMessageHandler()
     */
    public MessageHandler getMessageHandler()
    {
        return this.messageHandler;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#isTerminating()
     */
    public boolean isTerminating()
    {
        return isTerminating;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#getPrintStream()
     */
    public OutputStream getPrintStream()
    {
        return printStream;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#setPrintStream(java.io.PrintStream)
     */
    public void setPrintStream( PrintStream out )
    {
        printStream = out;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#setEmitReportOnExit(boolean)
     */
    public void setEmitReportOnExit( boolean b )
    {
        this.emitsReportOnExit = b;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#setEmitReportAutomatically(boolean)
     */
    public void setEmitReportAutomatically( boolean b )
    {
        emitsReportAutomatically = b;
    }


    /**
     * @see com.griffinbrown.xmltool.Session#getContentHandler()
     */
    public ContentHandler getContentHandler()
    {
        return ch;
    }


    /**
     * Request that the application not terminate when the session is terminated.
     * In the case of XMLProbe, terminating the application generates a {@link System#exit(int)}) call,
     * thus halting the JVM. It is useful to avoid this e.g. when running a batch of Sessions, 
     * as in a series of JUnit tests.  
     * N.B. not an interface method
     * @param b whether the application should terminate when the session terminates
     */
    public void setTerminateApplicationOnSessionTermination( boolean b )
    {
        applicationTerminatesWhenSessionTerminates = b;
    }


    public boolean emitsReportOnExit()
    {
        return emitsReportOnExit;
    }


    public boolean emitsReportAutomatically()
    {
        return emitsReportAutomatically;
    }


    public void setConfig( Configuration config )
    {
        this.config = config;
    }


    /**
     * @param reportEmitter the reportEmitter to set
     */
    public void setReportEmitter( ReportEmitter reportEmitter )
    {
        this.reportEmitter = reportEmitter;
    }

}