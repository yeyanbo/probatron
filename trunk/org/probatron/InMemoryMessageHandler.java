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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Message;
import com.griffinbrown.xmltool.MessageHandler;

/**
 * Class to represent a message handler which manipulates messages in memory.
 * 
 * @author andrews
 */
public class InMemoryMessageHandler implements MessageHandler
{
    private HashMap msgMap;
    private short errorFormat = Constants.ERRORS_AS_XML; //default


    /**
     * Constructs a default handler with an internal map capacity of 4096.
     *
     */
    public InMemoryMessageHandler()
    {
        msgMap = new HashMap( 4096 );
    }


    public void handle( Message msg )
    {
        //hash them by ID (type)
        if( msgMap.containsKey( msg.getType() ) )
        {
            ( ( ArrayList )msgMap.get( msg.getType() ) ).add( msg );
        }
        else
        {
            ArrayList list = new ArrayList();
            list.add( msg );
            msgMap.put( msg.getType(), list );
        }
    }


    /**
     * @see com.griffinbrown.xmltool.MessageHandler#getMessages(short)
     * In this implementation, returns a String of QA messages grouped by ID, 
     * wrapped in SILCN &lt;matched-set> tags. 
     */
    public String getMessages( short format )
    {
        StringBuffer s = new StringBuffer();

        //1. message objects
        if( format == Constants.ERRORS_AS_XML || format == Constants.ERRORS_AS_HTML )
        {
            Object type = null;

            if( msgMap != null )
            {
                Iterator i = msgMap.keySet().iterator();

                while( i.hasNext() ) //for each message ID
                {
                    type = i.next();

                    s.append( "<silcn:matched-set>\n<silcn:id>" ).append( ( String )type )
                            .append( "</silcn:id>\n" );

                    //String impl
                    Iterator j = ( ( ArrayList )msgMap.get( type ) ).iterator();

                    while( j.hasNext() )
                    {
                        s.append( ( ( Message )j.next() ).asXml() );
                    }

                    s.append( "</silcn:matched-set>\n" );
                }
            }
        }
        else if( format == Constants.ERRORS_AS_TEXT )
        {
            Collection lists = msgMap.values();
            Iterator iter = lists.iterator();
            while( iter.hasNext() )
            {
                ArrayList list = ( ArrayList )iter.next();
                Iterator iter2 = list.iterator();
                while( iter2.hasNext() )
                {
                    Message m = ( Message )iter2.next();
                    s.append( m.asText() + "\n" );
                }
            }

        }
        return s.toString();
    }


    /**
     * @see com.griffinbrown.xmltool.MessageHandler#stop()
     */
    public void stop()
    {
    // TODO Auto-generated method stub

    }


    /**
     * @see com.griffinbrown.xmltool.MessageHandler#setErrorFormat(short)
     */
    public void setErrorFormat( short format )
    {
        errorFormat = format;
    }

}
