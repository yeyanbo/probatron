/*
 * Copyright 2009 Griffin Brown Digital Publishing Ltd All rights reserved.
 * 
 * This file is part of Probatron.
 * 
 * Probatron is free software: you can redistribute it and/or modify it under the terms of the
 * Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * Probatron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the Affero General Public License for more details.
 * 
 * You should have received a copy of the Affero General Public License along with Probatron. If
 * not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Created on 13 Jul 2007
 */
package org.probatron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.probatron.functions.CoreLibrary;
import org.probatron.functions.DocumentFunction;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.schematron.NamespaceDeclaration;
import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.FeatureValuePair;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.ParseMessage;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.XPathLocator;
import com.griffinbrown.xmltool.utils.Utils;
import com.griffinbrown.xmltool.utils.XPathFactory;

/**
 * A handler for the evaluation of XPath queries under Probatron.
 * 
 * @author andrews
 *
 * $Id$
 */
public class QAHandler extends ShailTreeBuilder implements QueryHandler
{
    private XPathFactory xpathFactory = new ShailXPathFactory();
    private Session session;
    private QueryEvaluator evaluator;
    private ArrayList parseMessages;

    //flags
    private boolean useXPathLocators;
    private boolean timeXPathEvals;
    boolean namespacesLoaded;
    boolean queriesLoaded;
    private boolean variablesLoaded;
    private boolean includePhysicalLocators = true;

    private static final String TIME_XPATH_EVAL = "time-xpath-evaluation";
    private static final String XPATH_EXT_FUNC = "xpath-extension-function";
    private static final String XPATH_LOCATORS = "use-xpath-locators";
    private static final String ELEMENT_MESSAGE = "message";
    private static final String ELEMENT_TYPE = "type";
    private static final String ELEMENT_FOR_EACH = "for-each";
    static final String MAX_REPORTS = "report-maximum";


    /**
     * Constructor for normal use.
     * @param instance the instance to process
     * @param session the owner session
     * @throws XMLToolException
     */
    public QAHandler( Instance instance, ProbatronSession session ) throws XMLToolException
    {
        super( instance, session );

        this.session = session;
        this.evaluator = new ShailXPathEvaluator();

        CoreLibrary.initBuiltIns( this ); //install built-in functions

        session.setExpressionLanguage( "XPath" ); //set the session's expr lang
        instance.setQueryHandler( this );

        if( logger.isDebugEnabled() )
            logger.debug( "init'd: " + this );

        session.setMessageHandler( new OnDiskMessageHandler() );
        parseMessages = new ArrayList();
    }


    public void addLocatorMap( HashMap map )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#setFeature(java.lang.String, List)
     */
    public void setFeature( String uri, List featureValuePairs )
    {
        super.setFeature( uri, featureValuePairs ); //DO NOT OMIT!!

        //load extension functions
        if( uri.equals( XPATH_EXT_FUNC ) )
        {
            String alias = null;
            String className = null;

            Iterator iter = featureValuePairs.iterator();
            while( iter.hasNext() )
            {
                FeatureValuePair fvp = ( FeatureValuePair )iter.next();
                String name = fvp.getName();
                String value = fvp.getValue();

                if( name.equals( "value" ) )
                {
                    className = value;
                }
                if( name.equals( "alias" ) )
                {
                    alias = value;
                }

                if( alias != null )
                {
                    alias = alias.replace( " ", "" ); //remove whitespace

                    if( ! alias.equals( "" ) )
                    {
                        logger.debug( "registering function " + value + " with alias " + alias );
                        registerFunction( loadFunction( className ), alias );
                    }
                    else
                        registerFunction( loadFunction( className ), value );
                }
                else
                    registerFunction( loadFunction( className ), value );
            }
        }

        else if( uri.equals( XPATH_LOCATORS ) )
        {
            Iterator iter = featureValuePairs.iterator();
            while( iter.hasNext() )
            {
                FeatureValuePair fvp = ( FeatureValuePair )iter.next();
                String name = fvp.getName();
                String value = fvp.getValue();

                if( name.equals( "value" ) && value.equals( "true" ) )
                    this.useXPathLocators = true;
                Utils.logMessage( uri + "=" + value, this.getClass().getName() );
            }
        }

        else if( uri.equals( TIME_XPATH_EVAL ) )
        {
            Iterator iter = featureValuePairs.iterator();
            while( iter.hasNext() )
            {
                FeatureValuePair fvp = ( FeatureValuePair )iter.next();
                String name = fvp.getName();
                String value = fvp.getValue();

                if( name.equals( "value" ) && value.equals( "true" ) )
                    this.timeXPathEvals = true;
                this.session
                        .addMessage( new SessionMessage( session.getApplication(),
                                Constants.ERROR_TYPE_LOG, uri + "=" + value, this.getClass()
                                        .getName() ) );
            }
        }

        else if( uri.equals( Constants.OPTIMIZE_XPATH_EXPRESSIONS ) )
        {
            Iterator iter = featureValuePairs.iterator();
            while( iter.hasNext() )
            {
                FeatureValuePair fvp = ( FeatureValuePair )iter.next();
                String name = fvp.getName();
                String value = fvp.getValue();

                if( name.equals( "value" ) && value.equals( "true" ) )
                {
                    evaluator.getProperties().setProperty(
                            Constants.OPTIMIZE_XPATH_EXPRESSIONS, value );
                    Utils.logMessage( uri + "=" + value, this.getClass().getName() );
                }
            }
        }

        else if( uri.equals( BuilderImpl.FEATURE_INCLUDE_PHYSICAL_LOCATORS ) )
        {
            Iterator iter = featureValuePairs.iterator();
            while( iter.hasNext() )
            {
                FeatureValuePair fvp = ( FeatureValuePair )iter.next();
                String name = fvp.getName();
                String value = fvp.getValue();

                if( name.equals( "value" ) && value.equals( "false" ) )
                    includePhysicalLocators = false;
                Utils.logMessage( uri + "=" + value, this.getClass().getName() );
            }
        }

    }


    public void evaluateQueries( int doc )
    {}


    void evaluateGlobalVariables( int doc )
    {
        Iterator iter = getEvaluator().getVariables().iterator();
        XPathVariable var = null;
        Object result = null;
        long t = 0L;
        while( iter.hasNext() )
        {
            var = ( XPathVariable )iter.next();
            if( logger.isDebugEnabled() )
                logger.debug( "evaluating global variable $" + var.getName() + ": "
                        + var.getExpression() );
            t = System.currentTimeMillis();
            try
            {
                result = var.evaluate( doc, getEvaluator() );
            }
            catch( XMLToolException e )
            {
                session.addMessage( new SessionMessage( session.getApplication(),
                        Constants.ERROR_TYPE_FATAL, e.getMessage() ) );
                session.fatalError();
            }
            if( logger.isDebugEnabled() )
                logger.debug( "variable evaluated in " + ( System.currentTimeMillis() - t )
                        + "ms; result=" + result.toString() );
        }
    }


    public QueryEvaluator getEvaluator()
    {
        return evaluator;
    }


    public Object getLocatorForNode( int o )
    {
        Model model = ModelRegistry.getModelForNode( o );

        //        logger.debug( "node=" + model.toString( o ) + " doc=" + model.getSystemId() + " line="
        //                + model.getLineNumber( o ) + " col=" + model.getColumnNumber( o ) );

        LocatorImpl loc = new LocatorImpl();
        loc.setSystemId( model.getSystemId() );

        if( includePhysicalLocators ) //new for Shail
        {
            loc.setColumnNumber( model.getColumnNumber( o ) );
            loc.setLineNumber( model.getLineNumber( o ) );
        }

        return loc;
    }


    public Session getSession()
    {
        return session;
    }


    public void setEvaluator( QueryEvaluator eval )
    {
        this.evaluator = eval;
    }


    /**
     * @see com.griffinbrown.xmltool.Extension#preParse()
     */
    public void preParse()
    {
        super.preParse();

        //TODO: set entity resolver to be used with document()
        try
        {
            DocumentFunction df = ( DocumentFunction )evaluator.getFunction( null, null,
                    "document" );
            df.setEntityResolver( getEntityResolver() );
            df.setBaseURI( session.getConfig().getSystemId() );
            //              System.err.println("***entity resolver set for document():"+customEntityResolver);
        }
        catch( XMLToolException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_FATAL, "unresolvable function 'document' "
                            + e.getMessage() ) );
        }

        evaluator.setDocument( getModel() );

        //
        //N.B. these lines were previously in setFeature(), but all features now need to be set BEFORE loading queries,
        //as part of XPath optimization!!! (AS, 20060825)
        //
        //load these only ONCE; do NSS *first*, so they're available to the queries
        if( ! namespacesLoaded )
            namespacesLoaded = loadNamespaces();
        if( ! queriesLoaded )
            queriesLoaded = loadQueries();
        if( ! variablesLoaded )
            variablesLoaded = loadVariables();
    }


    public void postParse()
    {
        super.postParse();

        if( logger.isDebugEnabled() )
        {
            logger.debug( "Shail document for evaluation=" + getModel() );
            logger.debug( getModel().report() );
        }

        //register the model
        //        ModelRegistry.register( getModel() );

        //TODO: add the instance passed in for QA to the document function cache
        //        DocumentFunction.getInstance().cache( instance.getResolvedURI(), this.getDocument() );

        if( logger.isDebugEnabled() )
            logger.debug( "evaluating XPath queries" );
        evaluateQueries( getModel().getRoot() );

        session.getMessageHandler().stop(); //required, if using OnDiskMessageHandler
    }


    /**
     * Loads the class for a user-specified XPath extension function.
     * @param classname the class to be loaded
     * @return the <code>Function</code> object, if successful
     */
    private Object loadFunction( String classname )
    {
        Object func = null;
        try
        {
            func = Class.forName( classname, true,
                    ( ( ProbatronSession )session ).getCustomClassLoader() ).newInstance();
        }
        catch( ClassNotFoundException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_FATAL,
                    "ClassNotFoundException loading XPath extension function: "
                            + e.getMessage() + "; cause: " + e.getCause() ) );
            session.fatalError();
        }
        catch( IllegalAccessException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_FATAL,
                    "IllegalAccessException loading XPath extension function: "
                            + e.getMessage() ) );
            session.fatalError();
        }
        catch( InstantiationException e )
        {
            session
                    .addMessage( new SessionMessage( session.getApplication(),
                            Constants.ERROR_TYPE_FATAL,
                            "InstantiationException loading XPath extension function:"
                                    + e.getMessage() ) );
            session.fatalError();
        }
        return func;
    }


    /**
     * Registers the XPath extension function <code>func</code> with the current
     * <code>FunctionContext</code>.
     *
     * @param func the extension function to be registered
     * @param name the name by which the function will be called
     */
    private void registerFunction( Object func, String name )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "registering function: " + name );
        Object f = null;

        try
        {
            //register user-specific extension functions
            this.evaluator.registerFunction( null, name, func );
            f = this.evaluator.getFunction( null, null, name );
        }
        catch( XMLToolException e )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_NON_FATAL, "Unresolvable XPath extension function: "
                            + e.getMessage() ) );
        }
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_LOG, "Loaded XPath extension function: " + name, this
                        .getClass().getName() ) );

        if( logger.isDebugEnabled() )
            logger.debug( "registered function=" + f );
    }


    /**
     * Loads the XPathQueries to be evaluated.
     * Certain sub-classes may need to invoke this method to ensure  
     * @return true on success
     */
    boolean loadQueries()
    {
        return false;
    }


    /**
     * Loads the XPathVariables to be evaluated.
     * @return true on success
     */
    private boolean loadVariables()
    {
        if( ! namespacesLoaded )
            loadNamespaces(); //else vars will have no NSS!

        List list = session.getConfig().getVariables();
        Variable var = null;
        for( Iterator iter = list.iterator(); iter.hasNext(); )
        {
            var = ( Variable )iter.next();
            this.evaluator.addVariable( var );
            try
            {
                var.compile( getEvaluator() );
            }
            catch( XMLToolException e )
            {
                session.addMessage( new SessionMessage( session.getApplication(),
                        Constants.ERROR_TYPE_FATAL, e.getMessage() ) );
                session.fatalError();
            }
        }
        return true;
    }


    /**
     * Add any namespace declarations to the namespace map.
     * @return true on success
     */
    boolean loadNamespaces()
    {
        Iterator iter = session.getConfig().getNamespaceDecls().iterator();

        String prefix = null, ns = null;

        while( iter.hasNext() )
        {
            NamespaceDeclaration nsd = ( NamespaceDeclaration )iter.next();
            prefix = nsd.getPrefix();
            ns = nsd.getUri();
            this.evaluator.addNamespace( prefix, ns );
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_LOG, "Namespace '" + ns + "' declared for prefix '"
                            + prefix + "'", this.getClass().getName() ) );
        }

        return true;
    }


    /**
     * @see org.probatron.BuilderImpl#parseMessage(com.griffinbrown.xmltool.ParseMessage)
     */
    public void parseMessage( ParseMessage m )
    {
        super.parseMessage( m );

        parseMessages.add( m );
    }


    private void setParseMessageXPathLocators( int node )
    {
        if( ! parseMessages.isEmpty() && ( node != - 1 ) ) //only add locators for nodes in the parsed instance (i.e. NOT the schema)
        {
            Model model = ModelRegistry.getModelForNode( node );
            Iterator iter = parseMessages.iterator();
            while( iter.hasNext() )
            {
                ParseMessage m = ( ParseMessage )iter.next();
                XPathLocator xpl = new XPathLocatorImpl( node, evaluator );
                m.setXPathLocator( xpl );
            }
        }
        parseMessages.clear();
    }


    /**
     * @see org.probatron.BuilderImpl#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement( String uri, String localName, String name )
    {
        super.endElement( uri, localName, name );

        setParseMessageXPathLocators( getCurrentNode() );
    }


    /**
     * @see org.probatron.BuilderImpl#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement( String uri, String localName, String name, Attributes atts )
    {
        super.startElement( uri, localName, name, atts );

        setParseMessageXPathLocators( getCurrentNode() );
    }

    /* (non-Javadoc)
     * @see com.griffinbrown.shail.Builder#endDocument()
     */
    //    public void endDocument()
    //    {
    //        // TODO Auto-generated method stub
    //        super.endDocument();
    //        getModel().debugEvents();
    //    }
}
