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
 * Created on 13 Nov 2007
 */
package com.griffinbrown.xmltool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.Probatron;


public class ExtensionConfiguration
{
    private List features;
    private String className;
    private Class extClass = null;
    private Constructor constructor;
    private Class[] interfs;
    private Object extension;
    private static Logger logger = Logger.getLogger( ExtensionConfiguration.class );


    public ExtensionConfiguration( String className ) throws ClassNotFoundException
    {
        features = new ArrayList();
        this.className = className;

        this.extClass = Loader.getClass( this.className );

        this.interfs = this.extClass.getInterfaces();
    }


    public Feature addFeature( String featureName )
    {
        Feature feature = new Feature( featureName );
        features.add( feature );
        if( logger.isDebugEnabled() )
            logger.debug( "added feature " + featureName );
        return feature;
    }


    public List getFeatures()
    {
        return features;
    }


    public void setFeature( String featureName, String value )
    {
        Feature f = addFeature( featureName );
        f.addAttribute( featureName, value );
    }


//    public void setFeature( String featureName, List values )
//    {
//        Feature f = addFeature( featureName );
//        Iterator iter = values.iterator();
//        while( iter.hasNext() )
//        {
//            Node node = ( Node )iter.next();
//            f.addAttribute( node.getName(), node.getStringValue(), node );
//            if( logger.isDebugEnabled() )
//                logger.debug( "added " + node.getName() + "=" + node.getStringValue() );
//        }
//    }


    /**
     * @return whether the class implements interface <tt>interf</tt>.
     */
    boolean isImpl( String interf )
    {
        //test for direct implementation
        for( int i = 0; i < interfs.length; i++ )
        {
            //DEBUG System.err.println("This class implements: " + interfs[i]);
            if( interfs[ i ].getName().equals( interf ) )
            {
                return true;
            }
        }
        return false;
    }


    /**
     * @return whether this class's superclass implements interface <tt>interf</tt>.
     */
    boolean isSuperImpl( String interf )
    { //test for impl in superclass
        Class sc = extClass.getSuperclass();
        //DEBUG System.err.println( "This class has superclass: " + sc );
        Class[] superintfs = sc.getInterfaces();

        for( int j = 0; j < superintfs.length; j++ )
        {
            if( superintfs[ j ].getName().equals( interf ) )
            {
                return true;
            }
        }
        return false;
    }


    //    /**
    //     * @return An Object constructed <b>without<b> arguments.
    //     */
    //    Object initialize()
    //    {
    //        //DEBUG System.err.println( "Initializing '" + classname() + "' without params" );
    //
    //        Object extension = null;
    //
    //        try
    //        {
    //            extension = ( Object )Class.forName( classname() ).newInstance();
    //        }
    //        catch( ClassNotFoundException cnfe )
    //        {
    //            session.addMessage( new SessionMessage( session.getApplication(),
    //                    Constants.ERROR_TYPE_FATAL, "add-in class '" + classname + "' not found" ) );
    //            session.fatalError();
    //        }
    //        catch( InstantiationException ie )
    //        {
    //            session.addMessage( new SessionMessage( session.getApplication(),
    //                    Constants.ERROR_TYPE_FATAL, "the underlying constructor for class  '"
    //                            + classname + "' represents an abstract class or interface" ) );
    //            session.fatalError();
    //        }
    //        catch( IllegalAccessException iae )
    //        {
    //            session.addMessage( new SessionMessage( session.getApplication(),
    //                    Constants.ERROR_TYPE_FATAL, "the underlying constructor for add-in class '"
    //                            + classname + "' is inaccessible" ) );
    //            session.fatalError();
    //        }
    //        catch( IllegalArgumentException iarge )
    //        {
    //            session.addMessage( new SessionMessage( session.getApplication(),
    //                    Constants.ERROR_TYPE_FATAL, "class '" + classname + "': "
    //                            + iarge.getMessage() ) );
    //            session.fatalError();
    //        }
    //
    //        this.extension = extension;
    //        return extension;
    //    }

    /**
     * @return An Object constructed <b>with<b> parameters.
     */
    Object initialize( Class[] params, Session session ) throws XMLToolException
    {
        Object extension = null;

        //Create a Constructor object by invoking getConstructor on the Class object
        try
        {
            constructor = extClass.getConstructor( params );
        }
        catch( NoSuchMethodException nsme )
        {
            session.addMessage( new SessionMessage( session.getApplication(),
                    Constants.ERROR_TYPE_NON_FATAL, "NoSuchMethodException: "
                            + nsme.getMessage() ) );
        }

        if( constructor == null )
        {
            throw new XMLToolException( "null constructor returned by subclass of "
                    + Probatron.extensionClass() + " '" + className
                    + "': ensure constructor has public access" );
        }

        //Create the object by invoking newInstance on the Constructor object.
        //The newInstance method has one parameter: an Object array whose elements
        //are the argument values being passed to the constructor.
        try
        {
            Object[] args = new Object[] { session.getInstance(), session };
            extension = constructor.newInstance( args );
        }
        catch( InstantiationException ie )
        {
            throw new XMLToolException( "the underlying constructor for class  '" + className
                    + "' represents an abstract class or interface" );
        }
        catch( IllegalAccessException iae )
        {
            throw new XMLToolException( "the underlying constructor for add-in class '"
                    + className + "' is inaccessible. " + iae.getMessage() );
        }
        catch( IllegalArgumentException iarge )
        {
            throw new XMLToolException( "class '" + this.className + "': " + iarge.getMessage() );
        }
        catch( InvocationTargetException ite )
        {
            throw new XMLToolException( ite + ": class " + this.className + ": "
                    + ite.getMessage() + "; cause: " + ite.getCause() );
        }

        this.extension = extension;
        return extension;
    }


    /**
     * @return The classname for this extension.
     */
    public String getClassName()
    {
        return this.className;
    }


    /**
     * @return This extension as an instantiated <tt>Object</tt>.
     */
    Object getExtensionInstance()
    {
        return this.extension;
    }


    /**
     * @return The Class object for this extension.
     */
    Class extensionClass()
    {
        return this.extClass;
    }


    public static Class loadClass( String classname ) throws ClassNotFoundException
    {
        Class c = null;
        //Create a Class object for the object you want to create
        try
        {
            c = Loader.getClass( classname );
        }
        catch( ClassNotFoundException cnfe )
        {
            throw cnfe;
        }
        return c;
    }


    /**
     * @return whether this extension class extends the application's extension class.
     */
    public boolean extendz( Class applicationExtensionClass )
    {
        Class superClass = this.extClass.getSuperclass();

        while( superClass != null )
        {
            if( superClass != null && superClass.equals( applicationExtensionClass ) )
            {
                return true;
            }
            superClass = superClass.getSuperclass();
        }

        return false;
    }


//    public String asNormalizedXml()
//    {
//        return "\n\n<probe:addIn>\n<probe:name>" + this.className
//                + "</probe:name>\n<probe:config>\n" + featuresAsNormalizedXml()
//                + "\n</probe:config>\n</probe:addIn>";
//    }
//
//
//    private String featuresAsNormalizedXml()
//    {
//        Iterator it = features.iterator();
//        StringBuffer buf = new StringBuffer();
//        while( it.hasNext() )
//        {
//            Feature f = ( Feature )it.next();
//            buf.append( f.asNormalizedXml() );
//        }
//        return buf.toString();
//    }


    public boolean isXMLReader()
    {
        return this.extendz( this.extClass );
    }

}
