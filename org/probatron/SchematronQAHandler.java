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
 * Created on 16 Jan 2008
 */
package org.probatron;

import java.util.Iterator;
import java.util.List;

import com.griffinbrown.schematron.Pattern;
import com.griffinbrown.schematron.SchematronReportEmitter;
import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.schematron.SchematronSchema;
import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * A handler for the evaluation of Schematron queries under Probatron. 
 * 
 * @author andrews
 *
 * $Id$
 */
public class SchematronQAHandler extends QAHandler
{

    /**
     * Constructor for normal use.
     * @param instance the instance to process
     * @param session the Probatron processing session
     * @throws XMLToolException
     */
    public SchematronQAHandler( Instance instance, ProbatronSession session )
            throws XMLToolException
    {
        super( instance, session );
        configure();
        session.setReportEmitter( new SchematronReportEmitter( session ) );
        ((ShailXPathEvaluator)getEvaluator()).setVariableContext( new SchematronXPathVariableContext() );
    }

    private void configure()
    {
        setErrorFormat();
//        setLogShown();
    }

    private void setLogShown()
    {
//        Session session = getSession();        
//        String value = System
//                .getProperty( com.griffinbrown.schematron.Constants.PROP_SHOW_LOG );
//        
//        if( value != null )
//        {
//            if( value.equals("true"))
//                session.set
//        }
    }
    
    private void setErrorFormat()
    {
        Session session = getSession();        
        String value = System
                .getProperty( com.griffinbrown.schematron.Constants.PROP_ERROR_FORMAT );
        
        if( value != null )
        {
            if( value.equalsIgnoreCase( com.griffinbrown.schematron.Constants.ERROR_FORMAT_XML ) )
                session.setErrorFormat( Constants.ERRORS_AS_XML );
            else if( value
                    .equalsIgnoreCase( com.griffinbrown.schematron.Constants.ERROR_FORMAT_TEXT ) )
                session.setErrorFormat( Constants.ERRORS_AS_TEXT );
            //TODO: other error formats
        }
    }


    /**
     * @see org.probatron.QAHandler#loadQueries()
     */
    boolean loadQueries()
    {
        if( ! queriesLoaded )
        {
            Session session = getSession();
            QueryEvaluator evaluator = getEvaluator();

            if( logger.isDebugEnabled() )
                logger.debug( "loading queries..." );

            if( ! namespacesLoaded )
                loadNamespaces(); //else queries will have no NSS!

            SchematronConfiguration config = ( SchematronConfiguration )session.getConfig();

            //set this as the query handler for schema queries
            SchematronSchema schema = config.getSchema();
            schema.setQueryHandler( this );

            //QA using XPath expressions
            List list = config.getQueries();
            Query query = null;
            for( Iterator iter = list.iterator(); iter.hasNext(); )
            {
                query = ( Query )iter.next();
                evaluator.addQuery( query );
            }
        }
        return true;
    }


    /**
     * @see org.probatron.QAHandler#evaluateQueries(int)
     */
    public void evaluateQueries( int doc )
    {
        Session session = getSession();
        SchematronSchema schema = ( ( SchematronConfiguration )session.getConfig() )
                .getSchema();

        boolean time = session.getApplication().getProperty( Constants.TIMING_MODE ).equals(
                Boolean.TRUE );

        //1. evaluate global variables
        evaluateGlobalVariables( doc );

        //2. evaluate active patterns
        List patterns = schema.getActivePatterns();
        Iterator iter = patterns.iterator();

        if( logger.isDebugEnabled() )
            logger.debug( "active rules in schema=" + patterns.size() );

        while( iter.hasNext() )
        {
            Pattern pattern = ( Pattern )iter.next();
            if( logger.isDebugEnabled() )
                logger.debug( ">>>processing pattern " + pattern );

            try
            {
                pattern.evaluate();
            }
            catch( XMLToolException e )
            {
                session.addMessage( new SessionMessage( session.getApplication(),
                        Constants.ERROR_TYPE_FATAL, e.getMessage() ) );
                session.fatalError();
            }
        }

//        List results = Collections.synchronizedList( new ArrayList() );
    }
}
