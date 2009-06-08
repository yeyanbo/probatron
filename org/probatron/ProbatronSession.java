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

/*
 * Copyright (c) 2003 Griffin Brown Digital Publishing Ltd. All rights reserved.
 */
package org.probatron;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.griffinbrown.xmltool.Application;
import com.griffinbrown.xmltool.CustomClassLoader;
import com.griffinbrown.xmltool.SessionImpl;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * Class to represent a session of processing under Probatron.
 */
public class ProbatronSession extends SessionImpl
{
    private Application app;
    private boolean optimiseReports;
    private boolean emitRuleset;
    private CustomClassLoader customClassLoader = new CustomClassLoader();

    private static Logger logger = Logger.getLogger( ProbatronSession.class );


    /**
     * Constructor for normal use, for a default Probatron session.
     * @param app the owner application
     * @param inputSource the SAX input source to process
     * @throws XMLToolException
     */
    public ProbatronSession( Application app, InputSource inputSource ) throws XMLToolException
    {
        super( app, inputSource );
        this.app = app;
    }


    /**
     * Constructs a session using a pre-existing configuration.
     * @param inputSource the SAX input source to process
     * @throws XMLToolException
     */
    public ProbatronSession( InputSource inputSource ) throws XMLToolException
    {
        super( Probatron.getInstance(), inputSource );
        this.app = Probatron.getInstance();
    }


    /**
     * @see com.griffinbrown.xmltool.SessionImpl#setFeature(java.lang.String, java.lang.String)
     */

    public void setFeature( String feature, String value )
    {
        super.setFeature( feature, value );

        /*if( feature.equals( XMLProbe.BATCH_PROCESS ) && value.equals( "true" ) )
         System.err.println(XMLProbe.BATCH_PROCESS);*/

        if( feature.equals( Probatron.OPTIMISE_REPORTS ) && value.equals( "true" ) )
            this.optimiseReports = true;

        if( feature.equals( Probatron.EMIT_RULESET ) && value.equals( "true" ) )
            this.emitRuleset = true;
    }


    public boolean isTerminating()
    {
        return super.isTerminating();
    }


    /**
     * @see com.griffinbrown.xmltool.SessionImpl#preParse()
     */
    public void preParse()
    {
        super.preParse();

        //emit normalized ruleset?
        if( this.emitRuleset && this.getConfig() != null )
        {
            emitToPrintStream( this.getConfig().asNormalizedXml() );
            setEmitReportOnExit( false ); //else the report will be appended
            terminate( 0 );
        }
    }


    public void postParse()
    {
        super.postParse();
    }


    /**
     * Accesses the custom class loader for Probatron sessions.
     * @return a class-member instance of {@link CustomClassLoader}
     */
    public ClassLoader getCustomClassLoader()
    {
        return this.customClassLoader;
    }

}
