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
 * Created on 17 Dec 2007
 */
package com.griffinbrown.schematron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.griffinbrown.xmltool.Configuration;
import com.griffinbrown.xmltool.ExtensionConfiguration;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * Represents a Schematron validator configuration.
 * @author andrews
 *
 * @version $Id: SchematronConfiguration.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class SchematronConfiguration implements Configuration
{
    private String uri;
    private Session session;
    private List extensions = new ArrayList();
    private SchematronSchema schema;
    private List queries = new ArrayList();
    private String expressionLanguage;
    private boolean abstractionsResolved;
    private ExtensionConfiguration parserConfig;

    private static Logger logger = Logger.getLogger( SchematronConfiguration.class );


    public SchematronConfiguration( String uri, Session session ) throws XMLToolException
    {
        this.uri = uri;
        this.session = session;
        session.setConfig( this );

        XMLReader xmlReader = null;
        try
        {
            xmlReader = XMLReaderFactory
                    .createXMLReader( com.griffinbrown.xmltool.Constants.DEFAULT_SAX_DRIVER );
        }
        catch( SAXException e ) //if this occurs, the default parser will be used
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                    "SAXException getting XMLReader: " + e.getMessage() ) );
        }

        //NEW IN 1.4: *ALWAYS* XINCLUDE (at least until user option turns it off)
        try
        {
            xmlReader.setFeature( "http://apache.org/xml/features/xinclude", true );
        }
        catch( Exception e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                    "exception configuring XMLReader: " + e.getMessage() ) );
        }
        
        Object doc = null;
        try
        {
            doc = ShailNavigator.getInstance().getDocumentAsObject( uri );
        }
        catch( FunctionCallException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL, "error parsing configuration file: "
                            + e.getMessage() ) );

            session.fatalError();
        }

        SchematronConfigurationProcessor scp = null;
        try
        {
            scp = new SchematronConfigurationProcessor( this, session, doc );
        }
        catch( Exception e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL, e.getMessage() ) );

            session.fatalError();
        }

        //        session.setErrorFormat( Constants.ERRORS_AS_XML );
        addDefaultExtension();

        try
        {
            this.schema = scp.getSchema();
        }
        catch( JaxenException e )
        {
            throw new XMLToolException( e );
        }
    }


    private void addDefaultExtension() throws XMLToolException
    {
        String className = "org.probatron.SchematronQAHandler";
        ExtensionConfiguration ec = null;
        try
        {
            ec = new ExtensionConfiguration( className );
        }
        catch( ClassNotFoundException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL, "add-in class '"
                            + className + "' not found" ) );
            session.fatalError();
        }
        addExtension( ec );
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#addCompiledVariable(java.lang.Object)
     */
    public void addCompiledVariable( Object variable )
    {}


    /**
     * @see com.griffinbrown.xmltool.Configuration#addExtension(com.griffinbrown.xmltool.ExtensionConfiguration)
     */
    public void addExtension( ExtensionConfiguration ec ) throws XMLToolException
    {
        extensions.add( ec );
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#addNamespaceDecl(java.lang.String, java.lang.String)
     */
    public void addNamespaceDecl( String prefix, String uri )
    {
    // TODO Auto-generated method stub

    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#addOutput(java.lang.String)
     */
    public void addOutput( String uri )
    {}


    /**
     * @see com.griffinbrown.xmltool.Configuration#addVariable(java.lang.String, java.lang.String)
     */
    public void addVariable( String name, String value )
    {}


    /**
     * @see com.griffinbrown.xmltool.Configuration#asNormalizedXml()
     */
    public String asNormalizedXml()
    {
        StringBuffer s = new StringBuffer( "<schema xmlns='" );
        s.append( com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "'" );

        if( schema.getId() != null )
            s.append( " id='" + schema.getId() + "'" );

        if( schema.getVersion() != null )
            s.append( " schemaVersion='" + schema.getVersion() + "'" );

        if( schema.getDefaultPhase() != null )
            s.append( " defaultPhase='" + schema.getDefaultPhase() + "'" );

        if( schema.getQueryBinding() != null )
            s.append( " queryBinding='" + schema.getQueryBinding() + "'" );

        s.append( ">\n" );

        //TODO: title

        //namespaces
        Iterator iter = schema.getNamespaceDecls().iterator();
        while( iter.hasNext() )
        {
            NamespaceDeclaration nsd = ( NamespaceDeclaration )iter.next();
            s.append( nsd.asNormalizedXml() );
        }

        //variables
        iter = schema.getVariables().iterator();
        while( iter.hasNext() )
        {
            GlobalXPathVariable v = ( GlobalXPathVariable )iter.next();
            s.append( v.asNormalizedXml() );
        }

        //phases
        iter = schema.getPhases().iterator();
        while( iter.hasNext() )
        {
            Phase phase = ( Phase )iter.next();
            s.append( phase.asNormalizedXml() );
        }

        //patterns
        iter = schema.getPatterns().iterator();
        while( iter.hasNext() )
        {
            Pattern p = ( Pattern )iter.next();
            s.append( p.report() );
        }

        s.append( "</schema>" );

        return s.toString();
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getExtensions()
     */
    public List getExtensions()
    {
        return extensions;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getInputFile()
     */
    public String getInputFile()
    {
        return this.uri;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getNamespaceDecls()
     */
    public List getNamespaceDecls()
    {
        return schema.getNamespaceDecls();
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getSession()
     */
    public Session getSession()
    {
        return this.session;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getSystemId()
     */
    public String getSystemId()
    {
        return uri;
    }


    /**
     * Accesses variables belonging to the schema's active phase.
     * @see SchematronSchema#getVariables()
     * @return a List of active {@link GlobalXPathVariable}s
     */
    public List getVariables()
    {
        return schema.getVariables();
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#hasExtensions()
     */
    public boolean hasExtensions()
    {
        return false;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#setInputFile(java.lang.String)
     */
    public void setInputFile( String uri )
    {
        this.uri = uri;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#setSession(com.griffinbrown.xmltool.Session)
     */
    public void setSession( Session session )
    {
        this.session = session;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getXMLReader()
     */
    public ExtensionConfiguration getXMLReader()
    {
        if( parserConfig != null )
            return parserConfig;

        try
        {
            parserConfig = new ExtensionConfiguration(
                    com.griffinbrown.xmltool.Constants.DEFAULT_SAX_DRIVER ); //TODO: allow user to select via sys props
        }
        catch( ClassNotFoundException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL, "add-in class '"
                            + com.griffinbrown.xmltool.Constants.DEFAULT_SAX_DRIVER
                            + "' not found" ) );
            return null;
        }

        configureXMLReader();

        return parserConfig;
    }


    private void configureXMLReader()
    {
        //RELAX NG validation
        String schemaLoc = System.getProperty( Constants.PROP_RELAX_NG_SCHEMA_LOCATION );
        if( schemaLoc != null )
        {
            if( schemaLoc.trim().toLowerCase().endsWith( ".rnc" ) ) //compact syntax
            {
                parserConfig.setFeature( org.probatron.Probatron.FEATURES_PREFIX
                        + com.griffinbrown.xmltool.Constants.RELAXNG_COMPACT_SYNTAX_SCHEMA_LOC,
                        schemaLoc );
            }
            else
            //assume XML syntax
            {
                parserConfig.setFeature( org.probatron.Probatron.FEATURES_PREFIX
                        + com.griffinbrown.xmltool.Constants.RELAXNG_XML_SYNTAX_SCHEMA_LOC,
                        schemaLoc );
            }
        }
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#setExpressionLanguage(java.lang.String)
     */
    public void setExpressionLanguage( String exprLang )
    {
        expressionLanguage = exprLang;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getExpressionLanguage()
     */
    public String getExpressionLanguage()
    {
        return expressionLanguage;
    }


    /**
     * @see com.griffinbrown.xmltool.Configuration#getQueries()
     * 
     * @return the queries active for this Schematron validator
     */
    public List getQueries()
    {
        if( ! abstractionsResolved )
        {
            abstractionsResolved = resolveAbstractions();
        }

        Phase activePhase = schema.getActivePhase();

        if( logger.isDebugEnabled() )
            logger.debug( "active phase=" + activePhase );

        List queries = new ArrayList();

        //whether to use only patterns from a nominated phase, or all
        List patterns = null;
        if( activePhase == Phase.PHASE_ALL )
        {
            patterns = schema.getPatterns();
        }
        else
        {
            patterns = activePhase.getActivePatterns();
        }

        Iterator iter = patterns.iterator();
        while( iter.hasNext() )
        {
            Pattern pattern = ( Pattern )iter.next();

            if( logger.isDebugEnabled() )
                logger.debug( "active pattern=" + pattern );

            Iterator rulesIterator = pattern.getRules().iterator();
            while( rulesIterator.hasNext() )
            {
                Rule rule = ( Rule )rulesIterator.next();
                queries.addAll( rule.getAssertions() );
            }
        }

        return queries;
    }


    private boolean resolveAbstractions()
    {
        //resolve abstract patterns and rules
        if( logger.isDebugEnabled() )
            logger.debug( "+++resolving abstract patterns..." );

        schema.resolveAbstractPatterns();

        try
        {
            schema.resolveAbstractRules();
        }
        catch( XMLToolException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                    "error resolving abstract rule" + e.getMessage() ) );
        }
        return true;
    }

    /**
     * Accesses the schema to be used in validation.
     * @return the validation schema 
     */
    public SchematronSchema getSchema()
    {
        return this.schema;
    }

}
