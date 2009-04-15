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
package org.probatron;

import org.apache.log4j.Logger;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Session;

public abstract class ReportEmitter
{
    private Session session;
    private static Logger logger = Logger.getLogger( ReportEmitter.class );


    public ReportEmitter( Session session )
    {
        this.session = session;
    }

    /**
     * Requests that the validation report is emitted.
     * In this implementation, the report is emitted to the prevailing <code>PrintStream</code> for the session.
     * 
     * @throws Exception
     * @see {@link Session#setPrintStream(java.io.PrintStream)}
     */
    public void emitReport() throws Exception
    {
        short errorFormat = session.getErrorFormat();

        if( errorFormat == Constants.ERRORS_AS_XML )
        {
            xml();
        }
        else if( errorFormat == Constants.ERRORS_AS_TEXT )
        {
            text();
        }

        session.getPrintStream().flush(); //required when writing output *files*
    }

    /**
     * Requests that an XML validation report is emitted.
     */
    protected abstract void xml() throws Exception;

    /**
     * Requests that a plain text validation report is emitted.
     */
    protected abstract void text() throws Exception;


    


    
}
