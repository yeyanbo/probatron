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
 * Created on 15 Jan 2008
 */
package com.griffinbrown.schematron;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.probatron.ReportEmitter;
import org.probatron.jaxen.JaxenException;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.Message;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.utils.Utils;

/*
 * Class to emit a report for an XML document validated against a Schematron schema.
 * 
 * @author andrews
 * 
 * @version $Id: SchematronReportEmitter.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class SchematronReportEmitter extends ReportEmitter
{
    private Session session;
    private SchematronConfiguration config;


    public SchematronReportEmitter( Session session )
    {
        super( session );
        this.session = session;
        this.config = ( SchematronConfiguration )session.getConfig();
    }


    /**
     * Requests that namespace declarations are emitted.
     */
    protected void emitNSDecls()
    {
        Iterator decls = session.getConfig().getNamespaceDecls().iterator();
        while( decls.hasNext() )
        {
            NamespaceDeclaration decl = ( NamespaceDeclaration )decls.next();
            session.emitToPrintStream( decl.asNormalizedXml() );
        }
    }


    /**
     * Requests that validation messages are emitted.
     */
    protected void emitQAMsgs()
    {
        session.emitToPrintStream( session.getMessageHandler().getMessages(
                session.getErrorFormat() ) );
    }


    /**
     * Emits messages for child sessions of this session.
     * Note that at present there is no scope in this implementation for child
     * sessions of child sessions.
     */
    private void emitChildSessionReport()
    {
        List children = session.getChildren();

        if( children != null )
        {
            for( Iterator iterator = children.iterator(); iterator.hasNext(); )
            {
                Session child = ( Session )iterator.next();
                child.emitReport();
            }
        }
    }


    /**
     * Emits parsing errors for this session in the chosen error format.
     * @see Session#setErrorFormat(short)
     */
    protected void emitParserMessages()
    {
        Instance instance = session.getInstance();

        if( instance != null )
        {
            session.emitToPrintStream( instance.parseErrors( session.getErrorFormat() ) );
        }
    }


    private String reportHeader()
    {
        StringBuffer s = new StringBuffer();

        s.append( "<report>\n<version>" );
        s.append( session.getApplication().getVersion() );
        s.append( "</version>\n" );
        s.append( "<timestamp>" );
        s.append( session.getStart() );
        s.append( "</timestamp>\n" );
        s.append( "<sessionId>" );
        s.append( session.hashCode() );
        s.append( "</sessionId>\n" );
        s.append( "<parserClass>" );
        s.append( session.parser() == null ? "" : session.parser().getClass().getName() );
        s.append( "</parserClass>\n" );
        s.append( "<documentUri>" );
        s.append( Utils.escape( session.getInputFile() ) );
        s.append( "</documentUri>\n" );
        s.append( "<configurationUri>" );
        s.append( ( session.getConfig() != null ? session.getConfig().getSystemId() : "" ) );
        s.append( "</configurationUri>\n" );

        return s.toString();
    }


    /**
     * Emits this session's activity log in the chosen error format.
     * @see Session#setErrorFormat(short)
     */
    protected void emitSessionMessages()
    {
        Iterator iter = session.getMessages().iterator();
        while( iter.hasNext() )
        {
            Message msg = ( Message )iter.next();
            if( session.isLogShown() || ! session.isLogShown()
                    && ! msg.getType().equals( Constants.ERROR_TYPE_LOG ) )
            {
                if( session.getErrorFormat() == Constants.ERRORS_AS_TEXT )
                {
                    session.emitToPrintStream( msg.asText() + '\n' );
                }
                else
                    session.emitToPrintStream( msg.asXml() );
            }
        }
    }


    /////////////////////////////////

    /**
     * This method is called when an XML report is emitted to standard output.
     * Parser and session messages are emitted to standard error instead.
     */
    private void emitParserAndSessionMessagesToError()
    {
        Instance instance = session.getInstance();

        if( instance != null )
        {
            Utils.emitToError( instance
                    .parseErrors( com.griffinbrown.xmltool.Constants.ERRORS_AS_TEXT ) );
        }

        Iterator iter = session.getMessages().iterator();
        while( iter.hasNext() )
        {
            Message msg = ( Message )iter.next();
            if( ! msg.getType().equals( Constants.ERROR_TYPE_LOG ) )
                Utils.emitToError( msg.asText() + '\n' );
        }
        System.err.flush();
    }


    /**
     * @see org.probatron.ReportEmitter#emitReport()
     */
    protected void xml() throws JaxenException
    {
        emitParserAndSessionMessagesToError();

        if( ! session.isTerminating() )
        {
            SchematronSchema schema = config.getSchema();
            StringBuffer header = new StringBuffer( "<schematron-output xmlns='"
                    + com.griffinbrown.schematron.Constants.SVRL_NAMESPACE + "'" );

            header.append( " xmlns:" + session.getApplication().namespacePrefix() + "='"
                    + session.getApplication().namespaceUri() + "'" );

            if( schema.getTitle() != null )
                header.append( " title='" + schema.getTitle() + "'" );
            if( schema.getId() != null )
                header.append( " id='" + schema.getId() + "'" );
            if( schema.getVersion() != null )
                header.append( " schemaVersion='" + config.getSchema().getVersion() + "'" );

            String phase = schema.getActivePhase().getId();
            if( phase != Phase.ALL )
                header.append( " phase='" + phase + "'" );

            header.append( ">\n" );
            session.emitToPrintStream( header.toString() );

            //namespace decls
            Iterator iter = schema.getNamespaceDecls().iterator();
            while( iter.hasNext() )
            {
                NamespaceDeclaration nsd = ( NamespaceDeclaration )iter.next();
                session.emitToPrintStream( nsd.asNormalizedXml() + "\n" );
            }

            //active patterns
            iter = schema.getActivePatterns().iterator();
            while( iter.hasNext() )
            {
                Pattern pattern = ( Pattern )iter.next();
                session.emitToPrintStream( pattern.report() + "\n" );

                //(fired-rule, (failed-assert | successful-report)*)+
                Iterator rulesIterator = pattern.getRules().iterator();
                while( rulesIterator.hasNext() )
                {
                    Rule rule = ( Rule )rulesIterator.next();
                    if( rule.hasFired() )
                    {
                        session.emitToPrintStream( rule.report() + "\n" );
                        emitFailedAssertionsForRule( rule,
                                com.griffinbrown.xmltool.Constants.ERRORS_AS_XML );
                    }
                }
            }

            session.emitToPrintStream( "</schematron-output>" );
        }
    }


    protected void text() throws JaxenException
    {
        Instance instance = session.getInstance();

        //merge parse errors with session messages
        if( instance != null )
        {
            List errs = instance.parseErrors();
            session.getMessages().addAll( errs );
        }
        Collections.sort( session.getMessages() );
        emitSessionMessages();

        //failed assertions
        if( config != null )
        {
            SchematronSchema schema = config.getSchema();
            Iterator iter = schema.getActivePatterns().iterator();
            while( iter.hasNext() )
            {
                Pattern pattern = ( Pattern )iter.next();
                Iterator rulesIterator = pattern.getRules().iterator();
                while( rulesIterator.hasNext() )
                {
                    Rule rule = ( Rule )rulesIterator.next();
                    if( rule.hasFired() )
                    {
                        emitFailedAssertionsForRule( rule,
                                com.griffinbrown.xmltool.Constants.ERRORS_AS_TEXT );
                    }
                }
            }
        }
    }


    private void emitFailedAssertionsForRule( Rule rule, short format ) throws JaxenException
    {
        Iterator assertionIterator = rule.getFailedAssertions().iterator();
        while( assertionIterator.hasNext() )
        {
            SchematronQueryResult failed = ( SchematronQueryResult )assertionIterator.next();
            session.emitToPrintStream( failed.report( format ) + "\n" );
        }
    }
}
