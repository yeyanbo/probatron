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
 * Created on 10 Nov 2008
 */
package org.probatron.functions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;

import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Class to represent a string tokenizing extension function.
 * 
 * <p>The function takes two string arguments. The first is the string to be tokenized. 
 * The second is the delimiter by which to tokenize the string. If the second argument 
 * is the empty string, the delimiter defaults to the whitespace characters: space, 
 * line feed, carriage return, tab.</p>
 * 
 * <p>The function returns a nodeset of the tokens derived from the string. The 
 * delimiters are not included. The nodeset returned contains 0 or 
 * more &lt;token> elements, each containing the string of a token, e.g. &lt;token>GB&lt;/token>. 
 * The elements appear in the order in which their string value appears in the input 
 * string.</p>
 * 
 * @author andrews
 *
 * @version $Id: StringTokenizeFunction.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 */
public class StringTokenizeFunction implements Function
{

    public Object call( Context context, List args ) throws FunctionCallException
    {
        if( args.size() != 2 )
            throw new FunctionCallException( "tokenize() requires 1 argument" );

        if( ! ( args.get( 0 ) instanceof String ) )
            throw new FunctionCallException(
                    "first argument to tokenize() must be of type string" );

        if( ! ( args.get( 1 ) instanceof String ) )
            throw new FunctionCallException(
                    "second argument to tokenize() must be of type string" );

        return evaluate( ( String )args.get( 0 ), ( String )args.get( 1 ) );
    }


    private Object evaluate( String string, String delim ) throws FunctionCallException
    {
        StringTokenizer tokenizer = new StringTokenizer( string, delim );

        //create temp file for tokens
        File temp = Utils.tempFile( "probe", ".tmp" );
        temp.deleteOnExit();
        
        //write tokens out
        StringBuffer s = new StringBuffer( "<tokens>" );

        while( tokenizer.hasMoreTokens() )
        {
            s.append( "<token>" );
            s.append( tokenizer.nextToken() );
            s.append( "</token>" );
        }
        s.append( "</tokens>" );

        try
        {
            Utils.writeBytesToFile( s.toString().getBytes(), temp );
        }
        catch( IOException e )
        {
            throw new FunctionCallException(
                    "IOException creating temporary tree in tokenize(): " + e.getMessage() );
        }

        //read tokens back in
        Object doc = ShailNavigator.getInstance().getDocumentAsObject( temp.getAbsolutePath() );
        ShailList tokens;
        
        try
        {
            ShailXPath xpath = new ShailXPath( "//token" );
            tokens = (ShailList)xpath.evaluate( doc );
        }
        catch( JaxenException e )
        {
            throw new FunctionCallException( "error compiling XPath in tokenize(): "
                    + e.getMessage() );
        }

        return tokens;
    }
}
