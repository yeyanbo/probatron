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

package com.griffinbrown.xmltool;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.probatron.ReportEmitter;
import org.xml.sax.XMLReader;

import com.griffinbrown.schematron.NamespaceDeclaration;

/**
 * A generic XMLTool processing session.
 */
public interface Session extends Runnable
{
    /**
    * Accesses the URI of the input file for this session
    * @return the uri of the input file for this session, as a string.
    */
    String getInputFile();


    /**
     * Notifies this session that a fatal error has occurred.
     */
    void fatalError();


    /**
     * Adds a message to this session's report.
     * @param m the <code>Message</code> to add
     */
    void addMessage( Message m );


    /**
     * Adds a report of a namespace declaration to this session.
     * @param nsd the <code>NamespaceDeclaration</code> object to add
     */
    void addNamespaceDeclaration( NamespaceDeclaration nsd );


    /**
     * Add a String to the list of session outputs. (In the current implementation, only the
     * <strong>first</strong> output string added will be processed and
     * appear at stdout.)
     */
    void addOutputString( String s );


    /**
     * Whether parser warnings should be included in the error report
     * for this session.
     */
    boolean issueWarnings();


    /**
     * Emits messages generated for this session.
     */
    void emitReport();


    /**
     * Emit a string to the designated print stream. 
     * @param s string to emit
     */
    void emitToPrintStream( String s );


    /**
     * Accesses the configuration for this session.
     * @return <code>SessionConfiguration</code> for this session
     */
    Configuration getConfig();


    /**
     * Accesses non-standard configuration information.
     * Typically, this method need not be called, since custom configuration
     * nodes are parsed internally and access provided to them via the client interface.
     * @return Iterator of DOM Nodes for further processing. The nodes derive from elements etc.
     * specified for custom configuration. Correct handling of the nodes is the responsibility
     * of the user; this method is provided merely as a convenient access to them.
     */
    Iterator getCustomConfigs();


    /**
     * Accessor method for the specified emission mode.
     * @return the current specified emission mode
     */
    short getEmissionMode();


    /**
     * Accesses the error format specified for outputs.
     * @return the error output format.
     */
    short getErrorFormat();


    /**
     * Accesses the <code>Instance</code> object created during a session.
     * @return the XML instance scheduled for processing
     */
    Instance getInstance();


    /**
     * Accesses the messages stored for this session.
     * @return an <code>Iterator</code> over any <code>Message</code>s stored
     */
    List getMessages();


    /**
     * @return <code>Date</code> object representing the time parsing in this
     * session commenced, for reporting purposes.
     */
    Date getStart();


    /**
     * @return whether the activity log for this session is output
     */
    boolean isLogShown();


    /**
     * Provides a last-ditch opportunity for any other processing (clean-up,
     * messages etc) before this session is deliberately terminated.
     */
    void onExit();


    /**
     * Accesses the parser registered for this session.
     * @return the parser registered for this session
     */
    XMLReader parser();


    /**
     * Specifies the emission mode. Note that this is normally
     * set via the XML configuration file and affects normalized output
     * only.
     */
    void setEmissionMode( short mode );


    /**
     * Sets the output format for any messages output.
     */
    void setErrorFormat( short format );


    /**
     * Sets the encoding for XML outputs.
     */
    void setOutputEncoding( String enc );


    /**
     * Sets the sys id for the output.
     * @param sysId sys id of the output
     */
    void setOutputFile( String sysId );


    /**
     * Aborts this session.
     */
    void terminate( int exitCode );


    /**
     * Returns the current session object.
     * @return this session
     */
    Session getSession();


    /**
     * Accesses the encoding specified for output from this session.
     * @return output encoding for this session.
     */
    String getOutputEncoding();


    /**
     * Adds a child session to the current session.
     */
    void addChild( Session child );


    /**
     * Accesses the child sessions of the current session.
     * @return List of child sessions
     */
    List getChildren();


    /**
     * Accesses the application responsible for creating the current session.
     * @return the creating application
     */
    Application getApplication();


    /**
     * Accesses the value associated with a given application feature.
     * @param name name of the feature
     * @return the value associated with this feature
     */
    String getFeature( String name );


    /**
     * Starts processing under this session.
     */
    void run();


    void setParent( boolean b );


    boolean hasParentSession();


    void setMessageHandler( MessageHandler mh );


    MessageHandler getMessageHandler();


    ContentHandler getContentHandler();


    boolean isTerminating();


    void setPrintStream( PrintStream out );


    OutputStream getPrintStream();


    /**
     * Sets the policy for report emission on abnormal termination.
     * @param b whether the report should be emitted
     */
    void setEmitReportOnExit( boolean b );


    /**
     * Sets the policy for report emission 
     * @param b
     */
    void setEmitReportAutomatically( boolean b );


    void setConfig( Configuration config );


    ReportEmitter getReportEmitter();


    void setReportEmitter( ReportEmitter emitter );

}
