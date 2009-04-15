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
 * Created on 13 Dec 2007
 */
package org.probatron.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.probatron.ProbatronSession;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.functions.DocumentFunction;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.SimpleNamespaceContext;
import org.probatron.jaxen.function.BooleanFunction;
import org.probatron.jaxen.function.StringFunction;
import org.xml.sax.InputSource;

import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.SessionTerminationException;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.utils.Utils;

public class TestsBase extends TestCase
{
    private SimpleNamespaceContext namespaceContext;
    private static Logger logger = Logger.getLogger( TestsBase.class );
    private String homeDir = System.getProperty( "user.dir" );
    private PrintStream errStream = System.err;


    /**
     * (Note this has been modified to construct ShailXPaths)
     * @param expectedSize
     * @param context
     * @param xpathStr
     * @return
     * @throws JaxenException
     */
    protected int assertCountXPath( int expectedSize, int context, String xpathStr )
            throws JaxenException
    {
        logger.debug( "  Select :: " + xpathStr );
        ShailXPath xpath = new ShailXPath( xpathStr );
        xpath.setNamespaceContext( namespaceContext );
        ShailList results = ( ShailList )xpath.selectNodes( context );
        logger.debug( "results=" + results );
        //        logger.debug( "RETURN TYPE="+results.getClass() );
        logger.debug( "    Expected Size :: " + expectedSize );
        logger.debug( "    Result Size   :: " + results.size() );
        if( expectedSize != results.size() )
        {
            logger.debug( "      ## FAILED" );
            logger.debug( "      ## xpath: " + xpath + " = " + xpath.debug() );
            ShailIterator resultIter = ( ShailIterator )results.iterator();
            while( resultIter.hasNext() )
            {
                logger.debug( "      --> " + resultIter.nextNode() );
            }
        }
        assertEquals( xpathStr, expectedSize, results.size() );
        if( expectedSize > 0 )
        {
            return results.getInt( 0 );
        }
        return - 1;
    }


    protected void assertValueOfXPath( String expected, int context, String xpathStr )
            throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( xpathStr );
        xpath.setNamespaceContext( namespaceContext );
        ShailList list = new ShailList( 1 );
        list.addInt( context );
        Object node = xpath.evaluate( list );

        String result = StringFunction.evaluate( node, ShailNavigator.getInstance() );
        logger.debug( "  Select :: " + xpathStr );
        logger.debug( "    Expected :: " + expected );
        logger.debug( "    Result   :: " + result );
        if( ! expected.equals( result ) )
        {
            logger.debug( "      ## FAILED" );
            logger.debug( "      ## xpath: " + xpath + " = " + xpath.debug() );
        }
        assertEquals( xpathStr, expected, result );
    }


    protected void assertXPathTrue( int context, String xpathStr ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( xpathStr );
        xpath.setNamespaceContext( namespaceContext );
        ShailList list = new ShailList( 1 );
        list.addInt( context );
        Object node = xpath.evaluate( list );

        Boolean result = BooleanFunction.evaluate( node, ShailNavigator.getInstance() );
        logger.debug( "  Select :: " + xpathStr );
        logger.debug( "    Result   :: " + result );
        if( ! Boolean.TRUE.equals( result ) )
        {
            logger.debug( "      ## FAILED" );
            logger.debug( "      ## xpath: " + xpath + " = " + xpath.debug() );
        }
        assertEquals( Boolean.TRUE, result );
    }


    protected void assertXPathEquals( String expected, int context, String xpathStr )
            throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( xpathStr );
        xpath.setNamespaceContext( namespaceContext );
        ShailList list = new ShailList( 1 );
        list.addInt( context );
        Object node = xpath.evaluate( list );

        String result = StringFunction.evaluate( node, ShailNavigator.getInstance() );
        logger.debug( "  Select :: " + xpathStr );
        logger.debug( "    Result   :: " + result );
        if( ! result.equals( expected ) )
        {
            logger.debug( "      ## FAILED" );
            logger.debug( "      ## xpath: " + xpath + " = " + xpath.debug() );
        }
        assertEquals( expected, result );
    }


    /**
     * Asserts that the file filename begins with the string toTestFor.
     * @param filename
     * @param toTestFor
     * @throws Exception
     */
    protected void assertFileStartsWith( String filename, String toTestFor ) throws Exception
    {
        InputStream in = Utils.readFromFile( filename );
        int i;
        StringBuffer buf = new StringBuffer();
        while( buf.length() < toTestFor.length() )
        {
            i = in.read(); //read a char
            if( i == - 1 )
                break; //no more input
            buf.append( ( char )i ); //add char to buffer
        }
        in.close();

        assertEquals( toTestFor, buf.toString() );
    }


    /**
     * Asserts that the file filename contains the substring toTestFor.
     * @param filename
     * @param toTestFor
     * @throws Exception
     */
    protected void assertFileContains( String filename, String toTestFor ) throws Exception
    {
        InputStream in = Utils.readFromFile( filename );
        int i;
        StringBuffer buf = new StringBuffer();
        while( true )
        {
            i = in.read(); //read a char
            if( i == - 1 )
                break; //no more input
            buf.append( ( char )i ); //add char to buffer
        }
        in.close();

        assertTrue( "string '" + toTestFor + "' not found", buf.toString().contains( toTestFor ) );
    }


    /**
     * Asserts that the file filename equals the string toTestFor.
     * @param filename
     * @param toTestFor
     * @throws Exception
     */
    protected void assertFileEquals( String filename, String toTestFor ) throws Exception
    {
        InputStream in = Utils.readFromFile( filename );
        int i;
        StringBuffer buf = new StringBuffer();
        while( true )
        {
            i = in.read(); //read a char
            if( i == - 1 )
                break; //no more input
            buf.append( ( char )i ); //add char to buffer
        }
        in.close();

        assertTrue( "expected:\n " + toTestFor + "\ngot:\n " + buf.toString(), buf.toString()
                .equals( toTestFor ) );
    }


    protected ProbatronSession createXMLProbeSession( String config, String src, String dest )
            throws XMLToolException, SessionTerminationException
    {
        ProbatronSession x = new ProbatronSession( new InputSource( src ) );
        x.setOutputFile( dest );
        x.setTerminateApplicationOnSessionTermination( false );
        x.setConfig( new SchematronConfiguration( config, x ) );

        return x;
    }


    protected void runXMLProbe( String config, String src, String dest )
            throws XMLToolException
    {
        ProbatronSession x = null;

        try
        {
            x = createXMLProbeSession( config, src, dest );
        }
        catch( SessionTerminationException e )
        {
            logger.debug( "***session terminated: " + e.getMessage() + "***" );
        }

        if( x != null )
            runXMLProbe( x );
    }


    /**
     * Run an XMLProbe session, additionally piping standard error output to file. 
     * @param config
     * @param src
     * @param dest
     * @param errs file to which standard error is piped
     * @throws XMLToolException
     * @throws FileNotFoundException
     */
    protected void runXMLProbe( String config, String src, String dest, String errs )
            throws XMLToolException, FileNotFoundException
    {
        ProbatronSession x = null;

        try
        {
            x = createXMLProbeSession( config, src, dest );
        }
        catch( SessionTerminationException e )
        {
            logger.debug( "***session terminated: " + e.getMessage() + "***" );
        }

        setErrorStream( errs );

        if( x != null )
            runXMLProbe( x );
    }


    protected void setErrorStream( String file ) throws FileNotFoundException
    {
        this.errStream = System.err;
        
        //output standard error to file named errs
        PrintStream stderr = new PrintStream( new BufferedOutputStream( new FileOutputStream(
                file ) ) );

        System.setErr( stderr );
    }


    protected void runXMLProbe( ProbatronSession x )
    {
        try
        {
            x.run();
        }
        catch( SessionTerminationException e )
        {
            logger.debug( "***session terminated: " + e.getMessage() + "***" );
            return;
        }
        x.emitReport();
    }


    protected void runXMLProbe( ProbatronSession x, String errs ) throws FileNotFoundException
    {
        setErrorStream( errs );

        try
        {
            x.run();
        }
        catch( SessionTerminationException e )
        {
            logger.debug( "***session terminated: " + e.getMessage() + "***" );
            return;
        }

        x.emitReport();

    }


    protected void setUp() throws Exception
    {
        System.clearProperty( "error-format" );
        System.clearProperty( "active-phase" );

        ModelRegistry.reset();
        DocumentFunction.clearCache(); //avoids crosstalk between runs
        initLogger();

        namespaceContext = new SimpleNamespaceContext();
        namespaceContext.addNamespace( "probe", "http://xmlprobe.com/200312" );
        namespaceContext.addNamespace( "silcn", "http://silcn.org/200309" );

        File f = new File( "err.log" );
        if( f.exists() )
        {
            boolean logDeleted = f.delete();
            if( ! logDeleted )
                throw new RuntimeException( "couldn't delete error log!" );
        }
    }


    private void initLogger()
    {
        String logLvl = "DEBUG";
        String debugProp = System.getProperty( "debug" );
        if( debugProp == null || ! debugProp.equals( "true" ) )
            logLvl = "INFO";

        // set up log message format, etc.
        Properties p = new Properties();
        p.setProperty( "log4j.rootCategory", logLvl + ", A1" );
        p.setProperty( "log4j.appender.A1", "org.apache.log4j.ConsoleAppender" );
        p.setProperty( "log4j.appender.A1.target", "System.out" );
        p.setProperty( "log4j.appender.A1.layout", "org.apache.log4j.PatternLayout" );
        p.setProperty( "log4j.appender.A1.layout.ConversionPattern",
                "[%p][%d{DATE}]:%m [%C{1}][%t]%n" );

        PropertyConfigurator.configure( p );
    }


    protected void tearDown() throws Exception
    {
        //reset standard error
        System.setErr( this.errStream );
    }


    protected SimpleNamespaceContext getNamespaceContext()
    {
        return this.namespaceContext;
    }


    protected int validateWithJing( String args )
    {
        String command = "cmd.exe /C java com.thaiopensource.relaxng.util.Driver " + args;
        logger.debug( "executing " + command );
        int n = Utils.execute( command );
        logger.debug( "returning " + n );
        return n;
    }
}
