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

import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.probatron.QueryHandler;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailList;

/**
 * The Jaxen XPath engine requires custom XPath functions to be implemented
 * using its own (small) interface. 
 * 
 * @author andrews
 * @version $Revision: 1.2 $
 * 
 * @version $Id: RegExpFunction.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 * 
 */
public class RegExpFunction implements Function
{
    private QueryHandler handler;


    public RegExpFunction( QueryHandler handler )
    {
        this.handler = handler;
    }


    /**
     * @see org.probatron.jaxen.Function#call(Context, List)
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        if( args.size() < 2 || args.size() > 3 )
            throw new FunctionCallException( "match-regexp() takes 2 or 3 arguments." );

        if( ! ( args.get( 0 ) instanceof String ) )
        {
            throw new FunctionCallException(
                    "match-regexp() first argument must be of type string" );
        }

        if( args.size() == 2 )
        {
            return evaluate( args.get( 0 ), args.get( 1 ), context );
        }
        else
        //3 args
        {
            if( ! ( args.get( 2 ) instanceof Number ) )
                throw new FunctionCallException(
                        "match-regexp() third argument must be of type number" );
            return evaluate( args.get( 0 ), args.get( 1 ), args.get( 2 ), context
                    .getNavigator() );
        }
    }


    /**
     * Note that this method uses RegExp#find, i.e. looks for partial matches,
     * rather than a complete match between Pattern and String. For a complete
     * match, use RegExp#matches.
     * @see RegExp#find
     * @see RegExp#matches
     */
    private Object evaluate( Object pattern, Object input, Context context )
            throws FunctionCallException
    {
        String m = StringFunction.evaluate( input, context.getNavigator() );

        boolean found;
        try
        {
            found = RegExp.find( ( String )pattern, m );
        }
        catch( PatternSyntaxException e )
        {
            throw new FunctionCallException( "Error compiling regular expression: "
                    + e.getMessage() );
        }

        if( found )
            return input;
        return ShailList.EMPTY_LIST;
    }


    /**
     * @param strArg string of the regexp
     * @param matchArg node to search in
     * @param groupArg index of the group to capture
     * @param nav
     * @return string of the indexed matching group
     */
    private Object evaluate( Object strArg, Object matchArg, Object groupArg, Navigator nav )
            throws FunctionCallException
    {
        String s = StringFunction.evaluate( matchArg, nav );
        String match;

        try
        {
            match = RegExp.find( ( String )strArg, s, ( ( Double )groupArg ).intValue(),
                    handler );
        }
        catch( PatternSyntaxException e )
        {
            throw new FunctionCallException( "Error compiling regular expression: "
                    + e.getMessage() );
        }
        catch( IllegalStateException ise )
        {
            throw new FunctionCallException( "Error compiling regular expression: "
                    + ise.getMessage() );
        }
        catch( IndexOutOfBoundsException ioob )
        {
            throw new FunctionCallException( "Error compiling regular expression: "
                    + ioob.getMessage() );
        }

        if( match == null )
            return ShailList.EMPTY_LIST;
        return match;
    }

}
