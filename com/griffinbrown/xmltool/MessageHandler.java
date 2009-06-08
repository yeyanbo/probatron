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
package com.griffinbrown.xmltool;

/**
 * Represents a handler for user-defined (and dynamically-evaluated) error 
 * messages.  
 * 
 * @author andrews
 */
public interface MessageHandler
{
    /**
     * Handles Message objects created during a Session.
     * @param m the Message to handle
     */
    public void handle( Message m );

    /**
     * Gets the messages handled. 
     * @param format the output format to use for the string representation of the messages 
     * @return string of the handled messages
     */
    public String getMessages( short format );

    /**
     * Receives notification that message handling has ceased.
     */
    public void stop();

    /**
     * Sets the output format for handled messages.
     * @param format output format for handled messages
     * @see Constants#ERRORS_AS_TEXT
     * @see Constants#ERRORS_AS_XML
     * @see Constants#ERRORS_AS_HTML
     */
    public void setErrorFormat( short format );
}
