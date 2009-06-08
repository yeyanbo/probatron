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

package com.griffinbrown.xmltool.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXParseException;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.SessionRegistry;

/**
 * General utility methods.
 * @author andrews
 *
 * $Id$
 */
public class Utils
{
    private Utils()
    {}

    static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    static final String FILE_SEPARATOR = System.getProperty( "file.separator" );


    /**
     * Gets a minimal version of a system identifier.
     * This usually means the filename plus extension.
     * @return string of sys id <code>s</code> after last index of '/',
     * otherwise <code>s</code>.
     */
    public static String getMinimalSysId( String s )
    {
        if( s != null )
        {
            int index = s.lastIndexOf( '/' );
            if( index != - 1 )
            {
                s = s.substring( index + 1 );
            }
            else
            {
                index = s.lastIndexOf( '\\' );
                if( index != - 1 )
                {
                    s = s.substring( index + 1 );
                }
            }
        }
        return s;
    }


    /**
     * Escapes XML general entities in a string.
     * @return the string <code>s</code> with XML entities escaped
     */
    public static String escape( String s )
    {
        if( s.indexOf( '<' ) != - 1 || s.indexOf( '>' ) != - 1 || s.indexOf( '&' ) != - 1 )
        {
            s = s.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
        }
        return s;
    }


    /**
     * <p>Escapes quotation marks and apostrophes with built-in entities.</p>
     * <p>U+0027 (apostrophe) is replaced with <code>&amp;apos;</code>.</p>
     * <p>U+0022 (quotation mark) is replaced with <code>&amp;quot;</code>.</p>
     * @param s the string to escape
     * @return the string with quotation marks and apostrophes escaped 
     */
    public static String quoteAttr( String s )
    {
        if( s.indexOf( '\'' ) != - 1 || s.indexOf( '"' ) != - 1 )
            return s.replaceAll( "'", "&apos;" ).replaceAll( "\"", "&quot;" );
        return s;
    }


    /**
     * Utility method for encoding output strings.
     * @return a string in the specified encoding
     * @param in string to be encoded
     * @param enc encoding to be used
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    private static String encodeOutput( String in, String enc, PrintStream out )
            throws UnsupportedEncodingException
    {
        String es = "";
        if( enc.equals( Constants.ENC_US_ASCII ) )
        {
            //            es = new String( UnicodeFormatter.encodeAsAsciiXml( in ) );
            es = new String( UnicodeFormatter.encodeAsAsciiString( in ) );
        }
        else
        {
            try
            {
                byte[] array = in.getBytes( enc );
                //System.out.write( array, 0, array.length );	*DOESN'T WORK*!
                for( int i = 0; i < array.length; i++ )
                {
                    out.write( array[ i ] );
                }
                out.flush();
            }
            catch( UnsupportedEncodingException e )
            {
                throw e;
            }
        }
        return es;
    }


    /**
     * Emits a string to stderr.
     */
    public static void emitToError( String s )
    {
        System.err.println( s );
    }


    /**
     * Emits a string to the specified PrintStream.
     * N.B. no flushing of the stream takes place.
     * @param s the string to emit
     * @param enc the encoding to use
     * @param out the PrintStream to use
     * @throws UnsupportedEncodingException if the requested encoding is unsupported
     */
    public static void print( String s, String enc, PrintStream out )
            throws UnsupportedEncodingException
    {
        out.print( Utils.encodeOutput( s, enc, out ) );
    }


    /**
     * Writes the bytes in <tt>ba</tt> to the file <tt>f</tt>.
     * 
     * @param ba
     *                   the byte array to be written
     * @param f
     *                   the file to be written to
     * @throws IOException if the output stream cannot be written to or closed
     */
    public static void writeBytesToFile( byte[] ba, File f ) throws IOException
    {
        try
        {
            FileOutputStream fos = new FileOutputStream( f );
            fos.write( ba );
            fos.close();
        }
        catch( FileNotFoundException e )
        {
            throw new RuntimeException( "File not found when writing", e );
            // should never happen
        }

    }


    /**
     * Adds a fatal error message to the application's report.
     * @param s the message to add
     */
    public static void reportFatalError( String s )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_FATAL, s ) );
    }


    /**
     * Adds a non-fatal error message to the application's report.
     * @param s the message to add
     */
    public static void reportNonFatalError( String s )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_NON_FATAL, s ) );
    }


    /**
     * Adds a warning message to the application's report.
     * @param s the message to add
     */
    public static void reportWarning( String s )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_WARNING, s ) );
    }


    /**
     * Adds a message to the application log.
     * @param s the message
     * @param associatedClass the plug-in class associated with the message
     */
    public static void logMessage( String s, String associatedClass )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_LOG, s, associatedClass ) );
    }


    /**
     * Executes a TrAX transform. Exceptions are simply emitted to standard error.
     * @param errorListener the error listener for the transform
     * @param stylesheetURI the stylesheet
     * @param src location of the source document
     * @param dest location of the destination document
     * @param params parameters passed to transformation engine
     * @return the result of the transformation if successful, otherwise <code>null</code>
     */
    public static Result transform( ErrorListener errorListener, String stylesheetURI,
            String src, String dest, String[] params )
    {
        Source xsl = new StreamSource( stylesheetURI );

        TransformerFactory factory = newTransformerFactory();
        if( factory == null )
            return null;

        Transformer stylesheet = compileStylesheet( factory, xsl );
        stylesheet.setErrorListener( errorListener );

        if( stylesheet != null )
        {
            Source input = new StreamSource( src );
            Result result = new StreamResult( dest );

            //set params here
            if( params != null )
            {
                if( params.length % 2 != 0 )
                {
                    throw new RuntimeException(
                            "uneven number of Strings passed as stylesheet parameters" );
                }
                for( int i = 0; i < params.length - 1; i += 2 )
                {
                    stylesheet.setParameter( params[ i ], params[ i + 1 ] );
                }
            }

            try
            {
                stylesheet.transform( input, result );
            }
            catch( TransformerException e )
            {
                System.err.println( "Internal error running transform:\n"
                        + e.getMessageAndLocation() );
                return null;
            }

            return result;
        }
        return null;
    }


    /**
     * Compiles an XSL stylesheet.
     * Compilation errors are emitted to standard error.
     * @param factory transformer factory to compile with
     * @param xsl the stylesheet
     * @return the compiled stylesheet, or <code>null</code> if compilation failed
     */
    public static Transformer compileStylesheet( TransformerFactory factory, Source xsl )
    {
        Transformer stylesheet = null;
        try
        {
            stylesheet = factory.newTransformer( xsl );
        }
        catch( TransformerConfigurationException e )
        {
            System.err.println( "Transformation error: " + e.getMessageAndLocation() );
        }
        return stylesheet;
    }


    private static TransformerFactory newTransformerFactory()
    {
        TransformerFactory factory = null;
        try
        {
            factory = TransformerFactory.newInstance();
        }
        catch( TransformerFactoryConfigurationError e )
        {
            System.err.println( "Error configuring TransformerFactory:\n\n" + e.getMessage() );
        }
        return factory;
    }


    /**
     * Reads a file into memory.
     * @param uri the URI of the file to read
     * @return the file as an <code>InputStream</code> on success, otherwise <code>null</code>
     */
    public static InputStream readFromFile( String uri )
    {
        BufferedInputStream bis = null;
        try
        {
            bis = new BufferedInputStream( new FileInputStream( uri ) );
        }
        catch( FileNotFoundException e )
        {
            System.err.println( "FileNotFound exception: " + e.getMessage() );
        }
        return bis;
    }


    /**
     * <p>Creates a temporary file.</p>
     * 
     * <p>The underlying method here is {@link File#createTempFile(String, String)}. IOExceptions in creating the file
     * are emitted to standard error. The user is responsible for file cleanup.</p> 
     * @param prefix the file prefix
     * @param suffix the file suffix
     * @return a new temporary file, or <code>null</code> if one cannot be created
     */
    public static File tempFile( String prefix, String suffix )
    {
        File f = null;
        try
        {
            f = File.createTempFile( prefix, suffix );
        }
        catch( IOException e )
        {
            System.err.println( "IOException creating temp file: " + e.getMessage() );
        }
        return f;
    }


    /**
     * <p>Convenience method to run a command using {@link java.lang.Runtime#exec(String)}.</p>
     * 
     * <p>This implementation captures the error stream of the sub-process and emits it to
     * standard error.</p>
     * 
     * <p>Note this implementation is best suited to processes which handle standard output
     * themselves. Attempts to use this method without suitable capture of standard output
     * may result in this method hanging.</p> 
     * 
     * @param command string of command to execute
     * @return exit code of the sub-process
     */
    public static int execute( String command )
    {
        Process p = null;
        InputStream err = null;

        try
        {
            p = Runtime.getRuntime().exec( command );
            err = p.getErrorStream();
        }
        catch( Exception e )
        {
            System.err.println( e.getMessage() );
            return - 1;
        }

        if( err != null )
        {
            int i = 0;
            while( true )
            {
                try
                {
                    i = err.read();
                    if( i == - 1 )
                        break;
                    System.err.write( i );
                }
                catch( IOException e )
                {
                    System.err.println( e.getMessage() );
                    return - 1;
                }
                finally
                {
                    System.err.flush();
                }
            }
        }

        return p.exitValue();
    }

    //    public static boolean addEntryToZip( InputStream is, String uri )
    //    {
    //        FileOutputStream dest = null;
    //        try
    //        {
    //            dest = new FileOutputStream( uri );
    //        }
    //        catch( FileNotFoundException e )
    //        {
    //            System.err.println( "FileNotFoundException: " + e.getMessage() );
    //        }
    //
    //        ZipOutputStream zos = new ZipOutputStream( new BufferedOutputStream( dest ) );
    //        zos.setMethod( ZipOutputStream.DEFLATED );
    //        
    //
    //        return false;
    //    }
}