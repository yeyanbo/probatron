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

public class Utils
{
    static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    static final String FILE_SEPARATOR = System.getProperty( "file.separator" );


    public static String encodeAsAsciiXml( String src )
    {

        //System.err.println( "Converting " + src );

        char[] ca = src.toCharArray();
        int nEnc = 8 * ca.length; // every char will need 8 chars for its XML numeric entity reference

        char[] cb = new char[ nEnc ];

        int j = 0;
        for( int i = 0; i < ca.length; i++ )
        {
            int c = ( int )ca[ i ];

            cb[ j ] = '&';
            cb[ j + 1 ] = '#';
            cb[ j + 2 ] = digits[ c / 10000 ];
            c = c - ( c / 10000 ) * 10000;
            cb[ j + 3 ] = digits[ c / 1000 ];
            c = c - ( c / 1000 ) * 1000;
            cb[ j + 4 ] = digits[ c / 100 ];
            c = c - ( c / 100 ) * 100;
            cb[ j + 5 ] = digits[ c / 10 ];
            c = c - ( c / 10 ) * 10;
            cb[ j + 6 ] = digits[ c ];
            cb[ j + 7 ] = ';';
            j += 8;

        }

        return new String( cb );
    }


    /**
     * Replaces any substrings matching <I>replace</I> with <I>with</I> in the string <I>str</I>.
     * Pasted from usenet (works okay).
     **/
    public static String replace( String str, String replace, String with )
    {
        StringBuffer sb = null;
        String temp = str;
        boolean found = false;
        int start = 0;
        int stop = 0;

        while( ( start = temp.indexOf( replace, stop ) ) != - 1 )
        {
            found = true;
            stop = start + replace.length();
            sb = new StringBuffer( temp.length() + with.length() - replace.length() );
            sb.append( temp.substring( 0, start ) );
            sb.append( with );
            sb.append( temp.substring( stop, temp.length() ) );
            temp = sb.toString();
            stop += with.length() - replace.length();
        }

        if( ! found )
        {
            return str;
        }
        else
        {
            return sb.toString();
        }
    }


    /**
     * Returns a nicely-formatted string from a a SAXParseException.
     * (i.e. where there is no access to the underlying Locator object)
     */
    public static String getLocationString( SAXParseException e )
    {
        StringBuffer str = new StringBuffer();

        String systemId = e.getSystemId();

        if( systemId != null )
        {
            int index = systemId.lastIndexOf( '/' );
            if( index != - 1 )
            {
                systemId = systemId.substring( index + 1 );
            }
            str.append( systemId );
        }
        str.append( ':' );
        str.append( e.getLineNumber() );
        str.append( ':' );
        str.append( e.getColumnNumber() );

        return str.toString();
    }


    /**
     * @return String of sys id <code>s</code> after last index of '/',
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
     * @return The string <code>s</code> with XML entities escaped.
     */
    public static String escape( String s )
    {
        if( s.indexOf( '<' ) != - 1 || s.indexOf( '>' ) != - 1 || s.indexOf( '&' ) != - 1 )
        {
            s = s.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
        }
        return s;
    }


    public static String quoteAttr( String s )
    {
        if( s.indexOf( '\'' ) != - 1 || s.indexOf( '"' ) != - 1 )
            return s.replaceAll( "'", "&apos;" ).replaceAll( "\"", "&quot;" );
        return s;
    }


    /**
     * Redirects a string to a given file using the specified encoding.
     */
    public static void writeToFile( String s, String fname, String enc ) throws IOException
    {

        PrintStream out = new PrintStream( new BufferedOutputStream( new FileOutputStream(
                fname ) ) );
        if( enc.equals( Constants.ENC_US_ASCII ) )
        {
            out.print( Utils.encodeOutput( s, Constants.ENC_US_ASCII, out ) );
        }
        // the other encodings
        else
        {
            out.print( Utils.encodeOutput( s, enc, out ) );
        }
        out.close();
    }


    /**
     * Utility method for encoding output strings.
     * @return A String in the specified encoding.
     * @param in String to be encoded
     * @param enc encoding to be used
     */
    public static String encodeOutput( String in, String enc, PrintStream out )
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
     * Emits a string to stdout.
     */
    public static void emitToStdout( String s, String enc ) throws UnsupportedEncodingException
    {
        System.out.print( Utils.encodeOutput( s, enc, System.out ) );
        //== print( Utils.encodeOutput( s, enc, System.out ) );
    }


    /**
     * Emits a string to specified PrintStream.
     * N.B. no flushing of the stream takes place.
     * @param s the string to emit
     * @param enc the encoding to use
     * @param out the PrintStream to use
     * 
     */
    public static void print( String s, String enc, PrintStream out )
            throws UnsupportedEncodingException
    {
        out.print( Utils.encodeOutput( s, enc, out ) );
    }


    /**
     * @return The suffix of an application feature.
     */
    public static String featureSuffix( String uri, String prefix )
    {
        if( uri.startsWith( prefix ) )
        {
            return uri.substring( prefix.length() );
        }
        return uri;
    }


    /**
     * Writes the bytes in <tt>ba</tt> to the file named <tt>fn</tt>, creating it if
     * necessary.
     * 
     * @param ba
     *                   the byte array to be written
     * @param fn
     *                   the filename of the file to be written to
     * @throws IOException
     */
    public static void writeBytesToFile( byte[] ba, String fn ) throws IOException
    {
        File f = new File( fn );
        f.createNewFile();

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
     * Writes the bytes in <tt>ba</tt> to the file <tt>f</tt>.
     * 
     * @param ba
     *                   the byte array to be written
     * @param f
     *                   the file to be written to
     * @throws IOException
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


    public static String toString( Document doc )
    {
        DOMImplementationRegistry registry = null;
        try
        {
            registry = DOMImplementationRegistry.newInstance();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        DOMImplementationLS impl = ( DOMImplementationLS )registry.getDOMImplementation( "LS" );
        LSSerializer writer = impl.createLSSerializer();
        return writer.writeToString( doc );
    }


    public static void serialize( Document doc )
    {
        String s = toString( doc );
        System.err.print( s );
    }


    public static void serialize( Document doc, File f ) throws IOException
    {
        DOMImplementationRegistry registry = null;
        try
        {
            registry = DOMImplementationRegistry.newInstance();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        DOMImplementationLS impl = ( DOMImplementationLS )registry.getDOMImplementation( "LS" );
        LSSerializer writer = impl.createLSSerializer();
        writer.writeToURI( doc, f.toURI().toString() );
    }


    //TODO: utility methods for Messages

    public static void reportFatalError( String s )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_FATAL, s ) );
    }


    public static void reportNonFatalError( String s )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_NON_FATAL, s ) );
    }


    public static void reportWarning( String s )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_WARNING, s ) );
    }


    public static void logMessage( String s, String associatedClass )
    {
        Session session = SessionRegistry.getInstance().getCurrentSession();
        session.addMessage( new SessionMessage( session.getApplication(),
                Constants.ERROR_TYPE_LOG, s, associatedClass ) );
    }


    /**
     * Gets an entry from a zip file.
     * @param url the zip file location
     * @param entry the entry to get
     * @return the entry as an <code>InputStream</code> on success, otherwise null
     */
    public static InputStream getZipEntryAsInputStream( ZipFile zip, String entry )
    {
        ZipEntry zEntry = null;
        InputStream is = null;
        try
        {
            zEntry = zip.getEntry( entry );
            is = zip.getInputStream( zEntry );
        }
        catch( IOException e )
        {
            System.err.println( "IOException getting zip entry " + entry + ": "
                    + e.getMessage() );
            closeZip( zip );
        }
        return is;
    }


    public static byte[] getZipEntryAsByteArray( ZipFile zip, String entry )
    {
        ZipEntry zEntry = zip.getEntry( entry );
        if( zEntry == null )
            return null;

        byte[] ba = null;
        BufferedInputStream bis = new BufferedInputStream(
                getZipEntryAsInputStream( zip, entry ) );

        int size = ( int )zEntry.getSize();
        ba = new byte[ size ];

        try
        {
            int i = 0;
            while( i != - 1 )
            {
                i = bis.read( ba, 0, size );
            }

            bis.close();
        }
        catch( IOException ioe )
        {
            System.err.println( "IOException reading zip entry: " + ioe.getMessage() );
        }

        return ba;
    }


    /**
     * Closes the specified zip file.
     * @param zip the zip file to close
     * @return true if no exception is thrown, otherwise false
     */
    public static boolean closeZip( ZipFile zip )
    {
        try
        {
            zip.close();
        }
        catch( IOException e )
        {
            System.err.println( "IOException closing zip: " + e.getMessage() );
            return false;
        }
        return true;
    }


    public static Result transform( String stylesheetURI, String src, String dest,
            String[] params )
    {
        Source xsl = new StreamSource( stylesheetURI );

        TransformerFactory factory = newTransformerFactory();
        if( factory == null )
            return null;

        Transformer stylesheet = compileStylesheet( factory, xsl );

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


    public static Result transform( String stylesheetURI, InputStream src, String dest,
            String[] params )
    {
        Source xsl = new StreamSource( stylesheetURI );

        TransformerFactory factory = newTransformerFactory();
        if( factory == null )
            return null;

        Transformer stylesheet = compileStylesheet( factory, xsl );

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


    public static Result transform( String stylesheetURI, InputStream src, String dest,
            List params )
    {
        Source xsl = new StreamSource( stylesheetURI );

        TransformerFactory factory = newTransformerFactory();
        if( factory == null )
            return null;

        Transformer stylesheet = compileStylesheet( factory, xsl );

        if( stylesheet != null )
        {
            Source input = new StreamSource( src );
            Result result = new StreamResult( dest );

            //set params here
            if( params != null )
            {
                if( params.size() % 2 != 0 )
                {
                    throw new RuntimeException(
                            "uneven number of Strings passed as stylesheet parameters" );
                }
                for( int i = 0; i < params.size() - 1; i += 2 )
                {
                    stylesheet.setParameter( ( String )params.get( i ), ( String )params
                            .get( i + 1 ) );
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


    public static Result transform( String stylesheetURI, InputStream src, OutputStream dest,
            String[] params )
    {
        Source xsl = new StreamSource( stylesheetURI );

        TransformerFactory factory = newTransformerFactory();
        if( factory == null )
            return null;

        Transformer stylesheet = compileStylesheet( factory, xsl );

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


    public static TransformerFactory newTransformerFactory()
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
     * Reads a file into memory
     * @param uri the URI of the file to read
     * @return the file as an <code>InputStream</code> on success, otherwise null
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
     * Convenience method to run a command using {@link java.lang.Runtime#exec(String)}.
     * 
     * This implementation captures the error stream of the sub-process and emits it to
     * standard error.
     * 
     * Note this implementation is best suited to processes which handle standard output
     * themselves. Attempts to use this method without suitable capture of standard output
     * may result in this method hanging. 
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