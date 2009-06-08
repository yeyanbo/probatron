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
 * Created on 08-Jul-2005
 */
package com.griffinbrown.xmltool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * A class loader for use with classes stored in a JAR (internal use only).
 *  
 * @author andrews
 *
 * @version @version $Id: CustomClassLoader.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */

public class CustomClassLoader extends ClassLoader
{
    /**
     * Default constructor.
     */
    public CustomClassLoader()
    {}
    
    private static Logger logger = Logger.getLogger( Loader.class );
    
    /**
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected Class findClass( String name ) throws ClassNotFoundException
    {
        if(logger.isDebugEnabled())
            logger.debug( "class requested to load="+name );        
        
        
        byte[] b = null;
        try
        {
            b = loadClassData( name );
        }
        catch( IOException e )
        {
            throw new ClassNotFoundException( e.getMessage() );
        }
        if( b == null )
            throw new ClassNotFoundException( "class " + name + " not found" );
        return defineClass( name, b, 0, b.length );
    }


    /**
     * Scans all files with a .jar extension in the same directory as the standalone JAR
     * for a class with a matching name. Note that the name of the class has to be
     * normalized to the format JARs use.
     * @param name the name of the class requested for loading
     * @return the class data, or null if it cannot be found
     */
    private byte[] loadClassData( String name ) throws IOException
    {
        String normName = name.replace( '.', '/' ) + ".class"; //classname as it should appear in a JAR

        //see http://java.sun.com/j2se/1.4.2/docs/tooldocs/findingclasses.html:
        //"The java launcher puts the user class path string in the java.class.path system property. 
        //The possible sources of this value are:
        // [...] 
        // The JAR archive specified by the -jar option, which overrides all other values. 
        // If this option is used, all user classes come from the specified archive."
        String classPath = System.getProperty( "java.class.path" ); //this is therefore the path to the standalone JAR invoked

        //get the directory in which the standalone JAR resides
        File installDir = new File( classPath ).getParentFile();
        if( installDir == null )
            installDir = new File( System.getProperty( "user.dir" ) ); //the cwd

        File[] files = installDir.listFiles();
        File f;
        JarFile jar = null;
        JarEntry entry = null;

        if( files == null )
            throw new IOException( "class data for '" + name + "' not found in classpath '"
                    + installDir
                    + "'; ensure that the application is invoked as a standalone JAR" );

        for( int i = 0; i < files.length; i++ )
        {
            f = files[ i ];

            if( f.isFile() && f.getName().endsWith( ".jar" ) ) //purports to be a JAR
            {
                try
                //open the JAR
                {
                    jar = new JarFile( f );
                }
                catch( IOException e )
                {
                    throw e;
                }

                //retrieve the class data
                entry = jar.getJarEntry( normName );

                if( entry != null )
                {
                    return byteArrayFromJarEntry( jar, entry );
                }

                try
                //close the JAR
                {
                    jar.close();
                }
                catch( IOException e )
                {
                    throw e;
                }
            }
        }

        return null; //not found
    }


    /**
     * @param jar JAR where this entry resides
     * @param entry entry whose byte array is required
     * @return entry as byte array, otherwise 
     */
    private byte[] byteArrayFromJarEntry( JarFile jar, JarEntry entry )
    {
        BufferedInputStream bis = null;
        int size = ( int )entry.getSize();

        if( size == - 1 )
            return null;

        try
        {
            bis = new BufferedInputStream( jar.getInputStream( entry ) );
        }
        catch( IOException ioe )
        {
            ioe.printStackTrace();
        }

        byte[] ba = new byte[ size ];
        try
        {
            int j = 0;
            while( j != - 1 )
            {
                j = bis.read( ba, 0, size );
            }

            bis.close();
        }
        catch( IOException ioe )
        {
            ioe.printStackTrace();
        }
        return ba;
    }

}