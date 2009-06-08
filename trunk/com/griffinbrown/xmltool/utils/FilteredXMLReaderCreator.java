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
 * Created on 11 Nov 2008
 */
package com.griffinbrown.xmltool.utils;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.griffinbrown.xmltool.Constants;
import com.thaiopensource.xml.sax.XMLReaderCreator;

/**
 * Instantiates an XML parser for use under Jing.
 * @see com.thaiopensource.xml.sax.XMLReaderCreator
 */
public class FilteredXMLReaderCreator implements XMLReaderCreator
{
    XMLReader reader;


    /**
     * Default constructor.
     */
    public FilteredXMLReaderCreator()
    {
    //        System.err.println(this);
    }

    /**
     * Constructor for use with an existing <code>XMLReader</code>.
     * @param reader the existing reader
     */
    public FilteredXMLReaderCreator( XMLReader reader )
    {
        this.reader = reader;
    }


    /**
     * <p>Creates an XMLReader by loading the class specified by the system property
     * <code>org.xml.sax.driver</code>.</p>
     * <p>The value of {@link Constants#DEFAULT_SAX_DRIVER} is used if <code>org.xml.sax.driver</code> is not specified.</p>
     * <p>The resulting reader will have the features:</p>
     * <p><code>http://xml.org/sax/features/namespaces</code> and<br/>
     *  <code>http://apache.org/xml/features/xinclude</code></p>
     * <p>set to true and </p>
     * <p><code>http://apache.org/xml/features/xinclude/fixup-base-uris</code></p>
     * set to false. Any exceptions thrown in setting these features are reported and the reader is returned.</p> 
     * 
     * @return a XML parser (XInclude and namespace aware, if compatible) 
     */
    public XMLReader createXMLReader() throws SAXException
    {

        if( reader == null )
        {
            //substitute a default class to load, if none is specified
            if( System.getProperty( "org.xml.sax.driver" ) == null )
            {
                System.setProperty( "org.xml.sax.driver",
                        com.griffinbrown.xmltool.Constants.DEFAULT_SAX_DRIVER );
            }

            try
            {
                reader = XMLReaderFactory.createXMLReader();

                //                System.err.println("XML reader="+xr);

                reader.setFeature( "http://apache.org/xml/features/xinclude", true );
                //this feature disables the inclusion of xml:base, which causes schema validation errors if the att is not declared:
                reader.setFeature( "http://apache.org/xml/features/xinclude/fixup-base-uris",
                        false );
                reader.setFeature( "http://xml.org/sax/features/namespaces", true );
//                reader.setFeature( "http://xml.org/sax/features/namespace-prefixes", true );
                //                
                //                System.err.println( "XInclude aware=" + xr.getFeature( "http://apache.org/xml/features/xinclude" ) );
            }
            catch( SAXException e )
            {
                System.err.println( e.getMessage() );
            }
        }

        return reader;

    }

}
