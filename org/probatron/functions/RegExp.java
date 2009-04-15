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

package org.probatron.functions;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.probatron.QueryHandler;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.SessionMessage;

/**
 * Static regexp convenience methods.
 * Note that the methods used by com.xmlprobe.functions.RegExpFunction now only
 * compile the regexp once. 
 * 
 * @author andrews
 * @version $Revision: 1.2 $
 * 
 * @version $Id: RegExp.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 * 
 */

public class RegExp
{
    private static HashMap regexps = new HashMap();
    private static final String GROUP_MARKER = "***";
    private static Logger logger = Logger.getLogger( RegExp.class );


    /**
     * @return whether <code>pattern</code> matches String <code>data</code>
     * EXACTLY.
     * Any errors are emitted to stderr. 
     */
    public static boolean matches( String pattern, String data )
    {
        try
        {
            Pattern p = Pattern.compile( pattern );
            Matcher m = p.matcher( data );
            return m.matches();
        }
        catch( PatternSyntaxException e )
        {
            System.err.println( "Error compiling regular expression: " + e.getMessage() );
            return false;
        }

    }


    /**
     * @return whether <code>pattern</code> matches String <code>data</code>
     * EXACTLY.
     * This version of the method adds any errors to the current Session, rather
     * than printing to stderr.
     */
    public static boolean matches( String pattern, String data, QueryHandler handler )
    {
        try
        {
            Pattern p = ( Pattern )regexps.get( pattern );

            if( p == null )
            {
                p = Pattern.compile( pattern );
                regexps.put( pattern, p );
            }

            Matcher m = p.matcher( data );
            return m.matches();
        }
        catch( PatternSyntaxException e )
        {
            handler.getSession().addMessage(
                    new SessionMessage( handler.getSession().getApplication(),
                            Constants.ERROR_TYPE_NON_FATAL,
                            "Error compiling regular expression: " + e.getMessage() ) );
            return false;
        }

    }


    /**
     * @return whether String <code>data</code> contains match(es) for <code>
     * pattern</code>.
     
    public static boolean find( String pattern, String data )
    {
        try
        {
            Pattern p = ( Pattern )regexps.get( pattern );
            if( p == null )
            {
                p = Pattern.compile( pattern );
                regexps.put( pattern, p );
            }

            Matcher m = p.matcher( data );
            return m.find();
        }
        catch( PatternSyntaxException e )
        {
            System.err.println( "Error compiling regular expression: " + e.getMessage() );
            return false;
        }

    }*/


    /**
     * @return whether String <code>data</code> contains match(es) for <code>
     * pattern</code>.
     * This version of the method adds any errors to the current Session, rather
     * than printing to stderr. 
     */
    public static boolean find( String pattern, String data )
            throws PatternSyntaxException
    {
        Pattern p = ( Pattern )regexps.get( pattern );
        if( p == null )
        {
            p = Pattern.compile( pattern );
            regexps.put( pattern, p );
        }

        Matcher m = p.matcher( data );

        return m.find();
    }


    //	public static Matcher match( String pattern, String data, XPathQAHandler handler )
    //	{
    //		try
    //		{
    //			Pattern p = (Pattern)regexps.get( pattern );
    //			
    //			if( p == null )
    //			{
    //				p = Pattern.compile( pattern );
    //				regexps.put( pattern, p );
    //			}
    //			
    //			Matcher m = p.matcher( data );
    //			return m;
    //		}
    //		catch( PatternSyntaxException e )
    //		{
    //			handler.getSession().addMessage( new SessionMessage( handler.getSession().getApplication(), 
    //				Constants.ERROR_TYPE_NON_FATAL, "Error compiling regular expression: " + e.getMessage() ) );
    //			return null;
    //		}		
    //	}

    public static Matcher match( String pattern, String data, QueryHandler handler )
    {
        try
        {
            Pattern p = ( Pattern )regexps.get( pattern );

            if( p == null )
            {
                p = Pattern.compile( pattern );
                regexps.put( pattern, p );
            }

            Matcher m = p.matcher( data );
            return m;
        }
        catch( PatternSyntaxException e )
        {
            handler.getSession().addMessage(
                    new SessionMessage( handler.getSession().getApplication(),
                            Constants.ERROR_TYPE_NON_FATAL,
                            "Error compiling regular expression: " + e.getMessage() ) );
            return null;
        }
    }


    /**
     * @return string of matching group, indexed by <code>group</code>.
     * This version of the method adds any errors to the current Session, rather
     * than printing to stderr. 
     * @param pattern the pattern to match
     * @param data the string to search in
     * @param group index of group to return
     * @param session the related Session object 
     */
    public static String find( String pattern, String data, int group, QueryHandler handler )
            throws PatternSyntaxException, IllegalStateException, IndexOutOfBoundsException
    {
        String groupPattern = pattern + GROUP_MARKER + group; //add a non-compiling suffix
        Pattern p = ( Pattern )regexps.get( groupPattern );

        if( p == null )
        {
            p = Pattern.compile( pattern );
            regexps.put( groupPattern, p );
        }

        Matcher m = p.matcher( data );
        if( ! m.find() )
            return null;

        if( logger.isDebugEnabled() )
        {
            logger.debug( "group idx=" + group );
            logger.debug( "group=" + m.group( group ) );
        }

        return group == 0 ? data : m.group( group );
    }


    /**
     * @return whether <code>pattern</code> matches String <code>data</code>,
     * with flags <code>flags</code>.
     */
    public static boolean matches( String pattern, int flags, String data )
    {
        try
        {
            Pattern p = Pattern.compile( pattern, flags );
            Matcher m = p.matcher( data );
            return m.matches();
        }
        catch( PatternSyntaxException e )
        {
            System.err.println( "Error compiling regular expression: " + e.getMessage() );
            return false;
        }

    }


    /**
     * @return whether <code>pattern</code> matches String <code>data</code>,
     * with flags <code>flags</code>.
     * This version of the method adds any errors to the current Session, rather
     * than printing to stderr. 
     */
    public static boolean matches( String pattern, int flags, String data, QueryHandler handler )
    {
        try
        {
            Pattern p = Pattern.compile( pattern, flags );
            Matcher m = p.matcher( data );
            return m.matches();
        }
        catch( PatternSyntaxException e )
        {
            handler.getSession().addMessage(
                    new SessionMessage( handler.getSession().getApplication(),
                            Constants.ERROR_TYPE_NON_FATAL,
                            "Error compiling regular expression: " + e.getMessage() ) );
            return false;
        }

    }
}
