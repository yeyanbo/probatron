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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.XPathLocatorImpl;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.utils.FilteredXMLReaderCreator;
import com.griffinbrown.xmltool.utils.Utils;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.rng.CompactSchemaReader;

class SchematronConfigurationProcessor implements ErrorHandler
{
    private SchematronConfiguration config;
    private static Logger logger = Logger.getLogger( SchematronConfigurationProcessor.class );
    private Object doc;
    private Session session;
    private String appNS; //application namespace

    private static final String INCLUSION_STYLESHEET = "xsl/schematron-includer.xsl";
    private static final String INCLUSIONS_XPATH = "//*[local-name()='include' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']";
    private static final String DEFAULT_PHASE_XPATH = "string(/*/@defaultPhase)";
    private static final String IDS_XPATH = "//@id";
    private static final String VARIABLES_XPATH = "//*[local-name()='let' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "'"
            + "and not( parent::*[local-name()='rule' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "'])]";
    private static final String GLOBAL_VARIABLES_XPATH = "/*/*[local-name()='let' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']";
    private static final String PHASES_XPATH = "/*/*[local-name()='phase' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']";

    private static final String QUERY_BINDING_XPATH = "/*[local-name()='schema' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']/@queryBinding";

    private static final String BAD_ACTIVE_IDREF_XPATH = "//*[local-name()='active' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "']/@pattern"
            + "[ not( . = //*[local-name()='pattern' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']/@id ) ]";

    private static final String BAD_ABSTRACT_PATTERN_IDREF_XPATH = "//*[local-name()='pattern' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "']/@is-a"
            + "[ not( . = //*[local-name()='pattern' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "' and @abstract='true']/@id ) ]";

    private static final String BAD_RULE_EXTENDS_IDREF_XPATH = "//*[local-name()='extends' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "']/@rule"
            + "[ not( . = //*[local-name()='rule' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "' and @abstract='true']/@id ) ]";

    private static final String PARAM_VARIABLE_CLASH_XPATH = "//*[local-name()='let' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "']/@name"
            + "[ . = //*[local-name()='pattern' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "']/*[local-name()='param' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']/@name ]";

    private static final String SCHEMA_VERSION_XPATH = "/*[local-name()='schema' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']/@schemaVersion";

    private static final String SCHEMA_TITLE_XPATH = "/*[local-name()='schema' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE
            + "']/*[local-name()='title' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']";

    private static final String PATTERN_XPATH = "/*/*[local-name()='pattern' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']";

    private static final String NAMESPACE_DECLS_XPATH = "/*/*[local-name()='ns' and namespace-uri()='"
            + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']";

    private HashMap uriMap = new HashMap();
    private SchematronSchema schema;


    SchematronConfigurationProcessor( SchematronConfiguration config, Session session,
            Object doc ) throws XMLToolException, JaxenException
    {
        this.config = config;
        this.session = session;
        this.appNS = session.getApplication().namespaceUri();
        this.doc = doc;

        String uri = resolveInclusions( config.getSystemId() );
        validate( uri );

        if( ! uri.equals( config.getSystemId() ) ) //need to re-init the DOM doc after includes are processed
        {
            this.doc = getDocument( uri );
        }

        init();
    }


    private void init() throws XMLToolException, JaxenException
    {
        //see 6.2 of the ISO Schematron spec, "Minimal Syntax"
        checkQueryLanguageBinding();
        checkForIncludedInclusions();
        checkAdditionalConstraints();

        checkForDuplicateIDs();
        checkForDuplicateVariables();
    }


    private void checkForDuplicateIDs() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( IDS_XPATH );
        ShailList ids = ( ShailList )xpath.evaluate( doc );

        HashMap map = new HashMap();
        ShailIterator iter = ids.shailListIterator();

        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            String id = StringFunction.evaluate( node, ShailNavigator.getInstance() );
            if( map.containsKey( id ) )
                session.addMessage( new SessionMessage( session.getApplication(),
                        Constants.ERROR_TYPE_NON_FATAL,
                        "Schematron schema validation error at " + new XPathLocatorImpl( node )
                                + ": duplicate id '" + id + "'" ) );
            else
                map.put( id, null ); //TODO: change value to non-null?
        }
    }


    /**
     * This checks for duplicate global variables, that is <code>let</code> elements which do
     * not have a <code>rule</code> parent element.
     */
    private void checkForDuplicateVariables() throws XMLToolException, JaxenException
    {
        ShailXPath xpath = new ShailXPath( VARIABLES_XPATH );
        ShailList variables = ( ShailList )xpath.evaluate( doc );

        HashMap map = new HashMap();
        ShailIterator iter = variables.shailListIterator();

        ShailXPath nameXPath = new ShailXPath( "string(@name)" );

        while( iter.hasNext() )
        {
            int variable = iter.nextNode();
            String name = ( String )nameXPath.evaluate( variable );
            if( ! "".equals( name ) )
            {
                if( map.containsKey( name ) )
                    throw new XMLToolException( "Schematron schema validation error at "
                            + new XPathLocatorImpl( variable )
                            + ": duplicate global variable '" + name + "'" );
                else
                    map.put( name, null ); //TODO: change mapping to non-null?
            }

        }
    }


    /**
     * See spec, s6.4: "A Schematron implementation which does not support the query language
     * binding, specified in a schema with the queryBinding attribute, shall fail with an error."
     */
    private void checkQueryLanguageBinding() throws XMLToolException, JaxenException
    {
        ShailXPath xpath = new ShailXPath( QUERY_BINDING_XPATH );
        ShailList binding = ( ShailList )xpath.evaluate( doc );

        if( binding.isEmpty() )
            return; //it'll be the default, i.e. XPath 1.0

        String queryLanguage = StringFunction.evaluate( binding, ShailNavigator.getInstance() );

        if( ! queryLanguage.equalsIgnoreCase( "xpath" ) ) //only XPath 1.0 currently supported
        {
            throw new XMLToolException( "Schematron query language binding '" + queryLanguage
                    + "' not supported" );
        }
    }


    /**
     * Implements the Schematron schema for additional constraints; see Annex B of the spec.
     */
    private void checkAdditionalConstraints() throws JaxenException
    {
        //1. "The pattern attribute of the active element shall match the
        //id attribute of a pattern."

        ShailXPath xpath = new ShailXPath( BAD_ACTIVE_IDREF_XPATH );
        ShailList activeBadIdRef = ( ShailList )xpath.evaluate( doc );

        ShailIterator iter = activeBadIdRef.shailListIterator();
        while( iter.hasNext() )
        {
            int patternAtt = iter.nextNode();
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_NON_FATAL, "Schematron schema validation error at "
                            + new XPathLocatorImpl( patternAtt )
                            + ": pattern attribute value '"
                            + StringFunction
                                    .evaluate( patternAtt, ShailNavigator.getInstance() )
                            + "' does not match the id attribute of a pattern" ) );
        }

        //2. "The is-a attribute of a pattern element shall match
        //the id attribute of an abstract pattern."

        xpath = new ShailXPath( BAD_ABSTRACT_PATTERN_IDREF_XPATH );
        ShailList patternBadIsAIdRef = ( ShailList )xpath.evaluate( doc );

        iter = patternBadIsAIdRef.shailListIterator();
        while( iter.hasNext() )
        {
            int isAAtt = iter.nextNode();
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_NON_FATAL, "Schematron schema validation error at "
                            + new XPathLocatorImpl( isAAtt ) + ": is-a attribute value '"
                            + StringFunction.evaluate( isAAtt, ShailNavigator.getInstance() )
                            + "' does not match the id attribute of an abstract pattern" ) );
        }

        //3. "The rule attribute of an extends element shall match
        //the id attribute of an abstract rule."

        xpath = new ShailXPath( BAD_RULE_EXTENDS_IDREF_XPATH );
        ShailList extendsBadIdRef = ( ShailList )xpath.evaluate( doc );

        iter = extendsBadIdRef.shailListIterator();
        while( iter.hasNext() )
        {
            int ruleAtt = iter.nextNode();
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_NON_FATAL, "Schematron schema validation error at "
                            + new XPathLocatorImpl( ruleAtt ) + ": rule attribute value '"
                            + StringFunction.evaluate( ruleAtt, ShailNavigator.getInstance() )
                            + "' does not match the id attribute of an abstract rule" ) );
        }

        //4. "A variable name and an abstract pattern parameter should not
        //use the same name."

        /*
         * N.B. Rule 4 in Annex B contains a pattern which could not occur in a valid schema:
         * an abstract pattern with a param child.
         */

        xpath = new ShailXPath( PARAM_VARIABLE_CLASH_XPATH );
        ShailList duplicateVarNames = ( ShailList )xpath.evaluate( doc );

        iter = duplicateVarNames.shailListIterator();
        while( iter.hasNext() )
        {
            int nameAtt = iter.nextNode();
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_NON_FATAL, "Schematron schema validation error at "
                            + new XPathLocatorImpl( nameAtt ) + ": variable name '"
                            + StringFunction.evaluate( nameAtt, ShailNavigator.getInstance() )
                            + "' should not match the name of a pattern parameter" ) );
        }
    }


    private void checkForIncludedInclusions() throws XMLToolException, JaxenException
    {
        ShailList inclusions = getInclusions();
        if( ! inclusions.isEmpty() )
            throw new XMLToolException(
                    "Schematron schema error at "
                            + new XPathLocatorImpl( inclusions.getInt( 0 ) )
                            + ": included documents must not themselves contain Schematron inclusion instructions" );
    }


    private Object getDocument( String uri ) throws XMLToolException
    {
        try
        {
            return ShailNavigator.getInstance().getDocumentAsObject( uri );
        }
        catch( FunctionCallException e )
        {
            throw new XMLToolException( e );
        }

    }


    private ShailList getInclusions() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( INCLUSIONS_XPATH );
        return ( ShailList )xpath.evaluate( doc );
    }


    private boolean schemaContainsIncludes() throws JaxenException
    {
        List inclusions = getInclusions();
        return ! inclusions.isEmpty();
    }


    /**
     * 
     * @param uri
     * @return the URI of the result of transforming the schema to resolve inclusions, IF
     * the URI passed in contained inclusion instructions and the result of the transform was non-null;
     * otherwise the URI passed in 
     */
    private String resolveInclusions( String uri ) throws JaxenException
    {
        if( schemaContainsIncludes() )
        {
            Result result = null;

            File temp = Utils.tempFile( "probe", null );
            //            temp.deleteOnExit();

            if( logger.isDebugEnabled() )
                logger.debug( "Schematron inclusion result=" + temp );

            String includedUri = temp.toURI().toString();

            result = Utils.transform( new InclusionErrorListener(), INCLUSION_STYLESHEET, uri,
                    includedUri, null );

            if( result == null )
            {
                session
                        .addMessage( new SessionMessage( session.getApplication(),
                                com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                                "resolving Schematron inclusions resulted in a null document; ignoring includes" ) );
                return uri;
            }
            else
            {
                //hash temp file URI against original URI (so validation sys id is correct)
                uriMap.put( includedUri, new File( uri ).toURI().toString() );
                //logger.debug("hashed "+uriMap);
                return includedUri;
            }
        }

        return uri;
    }



    //TODO: make this an XMLProbe process in itself
    private void validate( String uri )
    {
        SchemaReader schemaReader = CompactSchemaReader.getInstance();
        PropertyMapBuilder schemaProps = new PropertyMapBuilder();
        PropertyMapBuilder instProps = new PropertyMapBuilder();
        ValidateProperty.ERROR_HANDLER.put( schemaProps, this );
        ValidateProperty.ERROR_HANDLER.put( instProps, this );

        ValidateProperty.XML_READER_CREATOR.put( instProps, new FilteredXMLReaderCreator() );

        ValidationDriver vd = new ValidationDriver( schemaProps.toPropertyMap(), instProps
                .toPropertyMap(), schemaReader );

        boolean loaded = false;
        try
        {
            loaded = vd
                    .loadSchema( com.griffinbrown.schematron.Constants.getSchematronSchema() );
        }
        catch( Exception e )
        {
            System.err.println( "error loading schema: " + e.getMessage() );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "Schematron schema loaded=" + loaded );

        boolean isSchemaValid = false;
        try
        {
            isSchemaValid = vd.validate( vd.uriOrFileInputSource( uri ) );
        }
        catch( Exception e ) //SAXException, IOException
        {
            System.err.println( "error validating instance against schema: " + e.getMessage() );
        }

        if( ! isSchemaValid ) //abort
        {
            session
                    .addMessage( new SessionMessage( session.getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL,
                            "Schematron schema is invalid: cannot proceed until schema errors are fixed" ) );
            session.fatalError();
        }
    }


    /**
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error( SAXParseException e ) throws SAXException
    {
        SAXParseException e2 = setExceptionSystemId( e );
        session.addMessage( new SessionMessage( session.getApplication(),
                com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL, e2 ) );
    }


    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError( SAXParseException e ) throws SAXException
    {
        SAXParseException e2 = setExceptionSystemId( e );
        session.addMessage( new SessionMessage( session.getApplication(),
                com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL, e2 ) );
    }


    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning( SAXParseException e ) throws SAXException
    {
        SAXParseException e2 = setExceptionSystemId( e );
        session.addMessage( new SessionMessage( session.getApplication(),
                com.griffinbrown.xmltool.Constants.ERROR_TYPE_WARNING, e2 ) );
    }


    /**
     * Since we may be parsing an instance whose sys id is a temp file 
     * (because inclusions have been resolved to that location),
     * we need to set the sys id on any parse errors for such files to that of the original instance passed in.
     * @param e
     * @return
     */
    private SAXParseException setExceptionSystemId( SAXParseException e )
    {
        String sysId = ( String )uriMap.get( e.getSystemId() );

        if( sysId == null )
            return e;
        else
        {
            SAXParseException foo = new SAXParseException( e.getMessage(), e.getPublicId(),
                    sysId, e.getLineNumber(), e.getColumnNumber() );

            return foo;
        }
    }


    SchematronSchema getSchema() throws JaxenException
    {
        if( schema == null )
        {
            schema = new SchematronSchema();

            schema.setTitle( getTitle() );
            schema.setQueryBinding( getQueryBinding() );

            List patterns = makePatterns();

            schema.addPatterns( patterns );
            schema.addNamespaceDecls( getNamespaceDecls() );
            schema.setVersion( getSchemaVersion() );
            schema.addPhases( makePhases() );
            schema.addGlobalVariables( makeGlobalVariables() );

            String defaultPhase = getDefaultPhaseId();

            if( useDefaultPhase() )
            {
                if( defaultPhase != null )
                {
                    try
                    {
                        schema.setDefaultPhase( getDefaultPhaseId() );
                    }
                    catch( XMLToolException e )
                    {
                        session.addMessage( new SessionMessage( session.getApplication(),
                                com.griffinbrown.xmltool.Constants.ERROR_TYPE_WARNING,
                                "active phase set to '#DEFAULT' but " + e.getMessage()
                                        + "; all patterns will be active" ) );
                    }
                }
                else
                    session.addMessage( new SessionMessage( session.getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_WARNING,
                            "active phase set to '#DEFAULT' but schema does not specify "
                                    + "a default phase; all patterns will be active" ) );
            }
            else if( ! useDefaultPhase() && defaultPhase != null )
            {
                try
                {
                    schema.setDefaultPhase( getDefaultPhaseId() );
                }
                catch( XMLToolException e )
                {
                    session.addMessage( new SessionMessage( session.getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                            "error setting default phase: " + e.getMessage()
                                    + "; all patterns will be active" ) );
                }
            }

            schema.setActivePhase( getActivePhase() );

            //resolve abstract patterns and rules
            if( logger.isDebugEnabled() )
                logger.debug( "+++resolving abstract patterns..." );

            //            schema.resolveAbstractPatterns();

            //            try
            //            {
            //                schema.resolveAbstractRules();
            //            }
            //            catch( XMLToolException e )
            //            {
            //                session.addMessage( new SessionMessage( session.getApplication(),
            //                        com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
            //                        "error resolving abstract rule" + e.getMessage() ) );
            //            }
        }

        return schema;
    }


    private boolean useDefaultPhase()
    {
        String activePhase = getActivePhaseSystemProperty();
        return activePhase != null && activePhase.equals( Phase.DEFAULT );
    }


    private String getDefaultPhaseId() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( DEFAULT_PHASE_XPATH );
        String defaultPhaseId = ( String )xpath.evaluate( doc );
        if( ! "".equals( defaultPhaseId ) )
            return defaultPhaseId;
        return null;
    }


    private List makePhases() throws JaxenException
    {
        List phases = new ArrayList();

        ShailXPath xpath = new ShailXPath( this.PHASES_XPATH );
        ShailList nodes = ( ShailList )xpath.evaluate( doc );

        ShailIterator iter = nodes.shailListIterator();

        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            phases.add( new Phase( node, schema ) );
        }

        return phases;
    }


    /**
     * See s5.4.5: "A declaration of a named variable. If the let element is the child 
     * of a rule element, the variable is calculated and scoped to the current rule 
     * and context. Otherwise, the variable is calculated with the context of the 
     * instance document root."
     * 
     * @return list of global variables (i.e. those to be evaluated with the document root
     * as context) declared as children of the root element
     */
    private List makeGlobalVariables() throws JaxenException
    {
        List results = new ArrayList();

        ShailXPath xpath = new ShailXPath( GLOBAL_VARIABLES_XPATH );
        ShailXPath nameXPath = new ShailXPath( "string(@name)" );
        ShailXPath valueXPath = new ShailXPath( "string(@value)" );
        ShailList variables = ( ShailList )xpath.evaluate( doc );

        ShailIterator iter = variables.shailListIterator();

        while( iter.hasNext() )
        {
            int var = iter.nextNode();
            String name = ( String )nameXPath.evaluate( var );
            String value = ( String )valueXPath.evaluate( var );
            results.add( new GlobalXPathVariable( name, value ) );
        }

        return results;
    }


    private String getSchemaVersion() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( SCHEMA_VERSION_XPATH );
        ShailList version = ( ShailList )xpath.evaluate( doc );

        return version.isEmpty() ? null : StringFunction.evaluate( version, ShailNavigator
                .getInstance() );
    }


    private String getTitle() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( SCHEMA_TITLE_XPATH );
        ShailList title = ( ShailList )xpath.evaluate( doc );

        return title.isEmpty() ? null : StringFunction.evaluate( title, ShailNavigator
                .getInstance() );
    }


    private String getQueryBinding() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( QUERY_BINDING_XPATH );
        ShailList queryBinding = ( ShailList )xpath.evaluate( doc );

        return queryBinding.isEmpty() ? null : StringFunction.evaluate( queryBinding,
                ShailNavigator.getInstance() );
    }


    private List makePatterns() throws JaxenException
    {
        List patterns = new ArrayList();

        ShailXPath xpath = new ShailXPath( PATTERN_XPATH );
        ShailXPath isAbstract = new ShailXPath( "string(@abstract)" );
        ShailXPath isA = new ShailXPath( "@is-a" );
        ShailIterator iter = ( ( ShailList )xpath.evaluate( doc ) ).shailListIterator();

        while( iter.hasNext() )
        {
            int node = iter.nextNode();

            if( ( ( String )isAbstract.evaluate( node ) ).equals( "true" ) )
            {
                schema.addAbstractPattern( new AbstractPattern( node, schema ) );
            }
            else if( ! ( ( ShailList )isA.evaluate( node ) ).isEmpty() )
            {
                patterns.add( new AbstractPatternInstance( node, schema ) );
            }
            else
                patterns.add( new PatternImpl( node, schema ) );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "patterns=" + patterns );

        return patterns;
    }


    private List getNamespaceDecls() throws JaxenException
    {
        List decls = new ArrayList();

        ShailXPath xpath = new ShailXPath( NAMESPACE_DECLS_XPATH );
        ShailXPath prefixXPath = new ShailXPath( "@prefix" );
        ShailXPath uriXPath = new ShailXPath( "@uri" );
        ShailIterator iter = ( ( ShailList )xpath.evaluate( doc ) ).shailListIterator();

        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            ShailList prefix = ( ShailList )prefixXPath.evaluate( node );
            ShailList uri = ( ShailList )uriXPath.evaluate( node );

            if( prefix != null && uri != null )
                decls.add( new NamespaceDeclaration( StringFunction.evaluate( prefix,
                        ShailNavigator.getInstance() ), StringFunction.evaluate( uri,
                        ShailNavigator.getInstance() ) ) );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "namespaces=" + decls );

        return decls;
    }

    class InclusionErrorListener implements ErrorListener
    {
        /**
         * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
         */
        public void error( TransformerException e ) throws TransformerException
        {
            session
                    .addMessage( new SessionMessage( session.getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                            "error resolving Schematron schema inclusion: "
                                    + e.getMessageAndLocation() ) );
        }


        /**
         * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
         */
        public void fatalError( TransformerException e ) throws TransformerException
        {
            session
                    .addMessage( new SessionMessage( session.getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL,
                            "error resolving Schematron schema inclusion: "
                                    + e.getMessageAndLocation() ) );
        }


        /**
         * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
         */
        public void warning( TransformerException e ) throws TransformerException
        {
            session
                    .addMessage( new SessionMessage( session.getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_WARNING,
                            "error resolving Schematron schema inclusion: "
                                    + e.getMessageAndLocation() ) );
        }
    }


    private String getActivePhaseSystemProperty()
    {
        return System.getProperty( "active-phase" );
    }


    private String getActivePhase() throws JaxenException
    {
        String activePhase = getActivePhaseSystemProperty();

        if( activePhase == null )
        {
            return Phase.DEFAULT;
        }

        else
        {
            if( logger.isDebugEnabled() )
                logger.debug( "setting active phase to: " + activePhase );

            //#ALL
            if( activePhase.equals( Phase.ALL ) )
            {
                return Phase.ALL;
            }
            //#DEFAULT
            else if( activePhase.equals( Phase.DEFAULT ) )
            {
                Phase defaultPhase = schema.getDefaultPhase();

                //no default phase found
                if( defaultPhase == null )
                {
                    return Phase.ALL;
                }
                else
                {
                    return getDefaultPhaseId(); //success
                }
            }
            else
            {
                Phase namedPhase = schema.getPhaseById( activePhase );
                if( namedPhase == null )
                {
                    session
                            .addMessage( new SessionMessage(
                                    session.getApplication(),
                                    com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                                    "active phase set to '"
                                            + activePhase
                                            + "' but no such phase found; all patterns will be active" ) );
                    return Phase.ALL;
                }
                else
                    schema.setActivePhase( activePhase ); //success
            }

        }
        return Phase.ALL;
    }

}
