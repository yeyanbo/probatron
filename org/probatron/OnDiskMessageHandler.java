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
 * Copyright (c) 2004 Griffin Brown Digital Publishing Ltd. All rights reserved.
 */
package org.probatron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.xml.sax.Locator;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Message;
import com.griffinbrown.xmltool.MessageHandler;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionRegistry;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Class to represent a message handler which writes messages to disk before 
 * manipulating them further. 
 */
public class OnDiskMessageHandler implements MessageHandler
{
    private PrintWriter out;
    private File temp;
    private static Logger logger = Logger.getLogger( OnDiskMessageHandler.class );
    private static final char DELIIM_CHAR = '\u001E'; //U+001E GROUP SEPARATOR (not a valid XML char)
    private static final String DELIIM_STRING = new String( new char[] { DELIIM_CHAR } );

    private static final int NUM_TOKENS = 6;

    private short errorFormat = Constants.ERRORS_AS_XML; //default


    /**
     * Default constructor.
     */
    public OnDiskMessageHandler() throws XMLToolException
    {
        try
        {
            this.temp = File.createTempFile( "probe", null );
            this.temp.deleteOnExit();
        }
        catch( IOException e )
        {
            throw new XMLToolException(
                    "IOException creating temp file in OnDiskMessageHandler: " + e.getMessage() );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "temp file created: " + temp );

        try
        {
            this.out = fileOutputStream( this.temp );
        }
        catch( IOException e )
        {
            throw new XMLToolException(
                    "IOException creating FileOutputStream in OnDiskMessageHandler: "
                            + e.getMessage() );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "temp file OutputStream created: " + out );
    }


    public void handle( Message m )
    {
        if( errorFormat == Constants.ERRORS_AS_TEXT )
            handleTextMessage( m );
        else
            handleXMLMessage( m );
    }


    private void handleXMLMessage( Message m )
    {
        out.write( m.getType() );
        out.write( DELIIM_CHAR );
        out.write( m.getXPathLocator().toString() );
        out.write( DELIIM_CHAR );
        out.write( Integer.toString( m.getLineNumber() ) );
        out.write( DELIIM_CHAR );
        out.write( Integer.toString( m.getColumnNumber() ) );
        out.write( DELIIM_CHAR );
        out.write( Utils.getMinimalSysId( m.getSystemId() ) );
        out.write( DELIIM_CHAR );
        //        out.write( Utils.escape( m.getString() ) ); //TODO: pass through well-formed content
        out.write( m.getString() );
        out.write( DELIIM_CHAR );
    }


    private void handleTextMessage( Message m )
    {
        Locator locator = m.getLocator();

        if( locator != null )
        {
            out.write( Utils.getMinimalSysId( locator.getSystemId() ) + ":"
                    + locator.getLineNumber() + ":" + locator.getColumnNumber() + ":" );
        }

        out.write( '[' + m.getType() + ']' );

        out.write( ":" + m.getString() );
        out.write( "\n" );
    }


    /**
     * Return a PrintWriter for the File passed in.
     * @param f the File the PrintWriter should write to
     * @return PrintWriter for File f
     */
    private PrintWriter fileOutputStream( File f ) throws IOException
    {
        return new PrintWriter( new BufferedWriter( new FileWriter( f, true ) ) );
    }


    public void stop()
    {
        out.close(); //if we don't do this, File#deleteOnExit() doesn't work!
        if( logger.isDebugEnabled() )
            logger.debug( "message handler stopped" );
    }


    /**
     * Parses the assembled messages, emitting them to <code>System.out</code>.
     */
    public String getMessages( short errorFormat )
    {
        if( errorFormat == Constants.ERRORS_AS_TEXT )
            return messagesAsText();
        else
            return messagesAsXML();
    }


    private String messagesAsText()
    {
        long t = System.currentTimeMillis();
        Session session = SessionRegistry.getInstance().getCurrentSession();
        PrintStream out = ( PrintStream )session.getPrintStream();
        String encoding = session.getOutputEncoding();
        StringBuffer buf = new StringBuffer();
        BufferedReader in = streamFromTempFile();

        try
        {
            int i = 0;
            int delims = 0;
            while( true )
            {
                i = in.read(); //read a char
                if( i == - 1 )
                    break; //no more input

                buf.append( ( char )i );

                if( i == '\n' )
                {
                    Utils.print( "Probatron:" + buf.toString(), encoding, out );
                    buf = new StringBuffer();
                }
            }
            in.close();
        }
        catch( IOException ioe )
        {
            System.err.println( "IOException getting messages from OnDiskMessageHandler: "
                    + ioe.getMessage() );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "got messages successfully in " + ( System.currentTimeMillis() - t )
                    + "ms" );

        return buf.toString();
    }


    //this impl uses delimited text
    private String messagesAsXML()
    {
        long t = System.currentTimeMillis();
        Session session = SessionRegistry.getInstance().getCurrentSession();
        PrintStream out = ( PrintStream )session.getPrintStream();
        String encoding = session.getOutputEncoding();
        StringBuffer buf = new StringBuffer();
        BufferedReader in = streamFromTempFile();

        try
        {
            int i = 0;
            int delims = 0;
            while( true )
            {
                i = in.read(); //read a char
                if( i == - 1 )
                    break; //no more input
                buf.append( ( char )i ); //add char to buffer
                if( i == DELIIM_CHAR )
                    delims++; //increment the delimiter count

                if( delims == NUM_TOKENS ) //got a whole message!
                {
                    //emit message
                    //TODO: text format!!
                    Utils.print( toXml( buf.toString() ), encoding, out );
                    //reset
                    buf = new StringBuffer();
                    delims = 0;
                }
            }
            in.close();
        }
        catch( IOException ioe )
        {
            System.err.println( "IOException getting messages from OnDiskMessageHandler: "
                    + ioe.getMessage() );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "got messages successfully in " + ( System.currentTimeMillis() - t )
                    + "ms" );

        return buf.toString();
    }


    private BufferedReader streamFromTempFile()
    {
        BufferedReader in = null;
        try
        {
            in = new BufferedReader( new FileReader( this.temp ) );
        }
        catch( FileNotFoundException e )
        {
            System.err
                    .println( "FileNotFoundException getting messages from OnDiskMessageHandler: "
                            + e.getMessage() );
        }

        if( in == null )
            SessionRegistry.getInstance().getCurrentSession().fatalError();

        return in;
    }


    /**
     * Parse message and return XML string.
     * @param s
     * @return
     */
    private String toXml( String s )
    {
        StringTokenizer st = new StringTokenizer( s, DELIIM_STRING );
        StringBuffer buf = new StringBuffer();
        while( st.hasMoreTokens() )
        {
            buf.append( "<silcn:matched-set>\n<silcn:id>" );
            buf.append( st.nextToken() );
            buf.append( "</silcn:id>\n<silcn:node>\n" );

            buf.append( "<silcn:expression>" );
            buf.append( st.nextToken() );
            buf.append( "</silcn:expression>\n" );

            buf.append( "<line>" );
            buf.append( st.nextToken() );
            buf.append( "</line>\n" );

            buf.append( "<column>" );
            buf.append( st.nextToken() );
            buf.append( "</column>\n" );

            buf.append( "<systemId>" );
            buf.append( st.nextToken() );
            buf.append( "</systemId>\n" );

            buf.append( "<text>" );
            if( st.hasMoreTokens() )
                buf.append( st.nextToken() );
            buf.append( "</text>\n" );

            buf.append( "</silcn:node>\n</silcn:matched-set>\n" );
        }

        return buf.toString();

    }


    private String toText( String s )
    {
        //        StringTokenizer st = new StringTokenizer( s, DELIIM_STRING );
        //        StringBuffer buf = new StringBuffer();
        //        while( st.hasMoreTokens() )
        //        {
        //            buf.append( SessionRegistry.getInstance().getCurrentSession().getApplication()
        //                    + ":" );
        //            buf.append( st.nextToken() );
        //            buf.append( "</silcn:id>\n<silcn:node>\n" );
        //
        //            buf.append( "<silcn:expression>" );
        //            buf.append( st.nextToken() );
        //            buf.append( "</silcn:expression>\n" );
        //
        //            buf.append( "<line>" );
        //            buf.append( st.nextToken() );
        //            buf.append( "</line>\n" );
        //
        //            buf.append( "<col>" );
        //            buf.append( st.nextToken() );
        //            buf.append( "</col>\n" );
        //
        //            buf.append( "<sysId>" );
        //            buf.append( st.nextToken() );
        //            buf.append( "</sysId>\n" );
        //
        //            buf.append( "<msg>" );
        //            if( st.hasMoreTokens() )
        //                buf.append( st.nextToken() );
        //            buf.append( "</msg>\n" );
        //
        //            buf.append( "</silcn:node>\n</silcn:matched-set>\n" );
        //        }
        //
        //        return buf.toString();
        return s;

    }


    /**
     * @see com.griffinbrown.xmltool.MessageHandler#setErrorFormat(short)
     */
    public void setErrorFormat( short format )
    {
        errorFormat = format;
    }
}
