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
 * This software contains, in modified form, source code originally created by 
 * the Jaxen Project.
 *
 * The copyright notice, conditions and disclaimer pertaining to that 
 * distribution are included below.
 *
 * Jaxen distributions are available from <http://jaxen.org/>.
 */  

/*
 * Created on 21 Nov 2008
 */
package org.probatron.jaxen.xpath2;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.probatron.functions.RegExp;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.xmltool.Constants;

/**
 * Class to represent an emulation of the XPath 2.0 function <code>fn:matches</code>.
 * 
 * <p>This implementation differs in two respects:</p>
 * <ol>
 * <li>it resides in the {@link Constants#PROBATRON_XPATH_FUNCTION_NS} namespace;
 * <li>it uses Java 1.4 (rather than W3C) regular expression syntax.</li>
 * </ol>
 * 
 *  
 * @author andrews
 *
 * @version $Id: MatchesFunction.java,v 1.2 2009/02/11 08:52:55 GBDP\andrews Exp $
 */
public class MatchesFunction implements Function
{
    static final String NAME = "matches";


    /**
     * @see org.probatron.jaxen.Function#call(org.probatron.jaxen.Context, java.util.List)
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        if( args.size() != 2 )
            throw new FunctionCallException( "fn:matches() expects two arguments"
                    + ( args.size() == 3 ? " [flags argument not supported]" : "" ) );

        if( ! ( args.get( 1 ) instanceof String ) )
        {
            throw new FunctionCallException(
                    "fn:matches() second argument must be of type string" );
        }

        return evaluate( args.get( 0 ), args.get( 1 ), context );
    }


    private Boolean evaluate( Object input, Object pattern, Context context )
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

        return Boolean.valueOf( found );
    }

}
