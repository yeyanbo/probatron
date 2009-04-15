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
 * Created on 23 Nov 2006
 */
package org.probatron.functions;

import java.util.List;

import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.function.StringLengthFunction;



public class CharToIntFunction implements Function
{
    private static final Double ERROR_VALUE = new Double( - 1 );
    private static final Double STRING_LENGTH = new Double( 1 );


    public Object call( Context context, List args ) throws FunctionCallException
    {
        Object result = ERROR_VALUE;

        if( args.size() != 1 )
            throw new FunctionCallException( "char-to-int() requires one argument" );

        Object arg = args.get( 0 );

        if( ! ( arg instanceof String ) )
            throw new FunctionCallException( "char-to-int() argument must be of type string" );

        String s = ( String )arg;

        if( ! StringLengthFunction.evaluate( s, context.getNavigator() ).equals( STRING_LENGTH ) )
            throw new FunctionCallException(
                    "char-to-int() string argument must be one character long" );

        //TODO: surrogate pair support
        //        if( XMLChar.isHighSurrogate(s.charAt( 0 )))
        //        {
        //            System.err.println("HIGH SURROGATE="+(int)s.charAt( 0 ));
        //            System.err.println(  );
        //        }

        char c = s.charAt( 0 );
        result = new Double( ( int )c );

        return result;
    }

}
