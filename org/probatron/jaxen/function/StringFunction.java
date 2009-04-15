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
 * $Header:
 * /home/projects/jaxen/scm/jaxen/src/java/main/org/jaxen/function/StringFunction.java,v 1.33
 * 2006/02/05 21:47:41 elharo Exp $ $Revision: 1.1 $ $Date: 2009/02/11 08:52:57 $
 * 
 * ====================================================================
 * 
 * Copyright 2000-2002 bob mcwhirter & James Strachan. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: * Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer. *
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Jaxen Project nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ==================================================================== This software consists
 * of voluntary contributions made by many individuals on behalf of the Jaxen Project and was
 * originally created by bob mcwhirter <bob@werken.com> and James Strachan
 * <jstrachan@apache.org>. For more information on the Jaxen Project, please see
 * <http://www.jaxen.org/>.
 * 
 * @version $Id: StringFunction.java,v 1.1 2009/02/11 08:52:57 GBDP\andrews Exp $
 */

package org.probatron.jaxen.function;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenRuntimeException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.UnsupportedAxisException;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

/**
 * <p>
 * <b>4.2</b> <code><i>string</i> string(<i>object</i>)</code>
 * </p>
 * 
 * 
 * <blockquote src="http://www.w3.org/TR/xpath">
 * <p>
 * The <b>string</b> function converts
 * an object to a string as follows:
 * </p>
 * 
 * <ul>
 * 
 * <li>
 * <p>
 * A node-set is converted to a string by returning the <a
 * href="http://www.w3.org/TR/xpath#dt-string-value" target="_top">string-value</a> of the node in the node-set
 * that is first in <a href="http://www.w3.org/TR/xpath#dt-document-order" target="_top">document order</a>. If
 * the node-set is empty, an empty string is returned.
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * A number is converted to a string as follows
 * </p>
 * 
 * <ul>
 * 
 * <li>
 * <p>
 * NaN is converted to the string <code>NaN</code>
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * positive zero is converted to the string <code>0</code>
 * </p>
 * </li>
 * 
 * <li>
 * 
 * <p>
 * negative zero is converted to the string <code>0</code>
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * positive infinity is converted to the string <code>Infinity</code>
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * negative infinity is converted to the string <code>-Infinity</code>
 * 
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * if the number is an integer, the number is represented in decimal
 * form as a <a href="http://www.w3.org/TR/xpath#NT-Number" target="_top">Number</a> with no decimal point and
 * no leading zeros, preceded by a minus sign (<code>-</code>) if
 * the number is negative
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * otherwise, the number is represented in decimal form as a Number including a decimal point with at least
 * one digit before the decimal point and at least one digit after the
 * decimal point, preceded by a minus sign (<code>-</code>) if the
 * number is negative; there must be no leading zeros before the decimal
 * point apart possibly from the one required digit immediately before
 * the decimal point; beyond the one required digit after the decimal
 * point there must be as many, but only as many, more digits as are
 * needed to uniquely distinguish the number from all other IEEE 754
 * numeric values.
 * </p>
 * 
 * </li>
 * 
 * </ul>
 * 
 * </li>
 * 
 * <li>
 * <p>
 * The boolean false value is converted to the string <code>false</code>.
 * The boolean true value is converted to the string <code>true</code>.
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * An object of a type other than the four basic types is converted to a
 * string in a way that is dependent on that type.
 * </p>
 * 
 * </li>
 * 
 * </ul>
 * 
 * <p>
 * If the argument is omitted, it defaults to a node-set with the
 * context node as its only member.
 * </p>
 * 
 * </blockquote>
 * 
 * @author bob mcwhirter (bob @ werken.com)
 * @see <a href="http://www.w3.org/TR/xpath#function-string"
 *      target="_top">Section 4.2 of the XPath Specification</a>
 */
public class StringFunction implements Function
{

    private static DecimalFormat format = ( DecimalFormat )NumberFormat
            .getInstance( Locale.ENGLISH );

    private static Logger logger = Logger.getLogger( StringFunction.class );

    static
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols( Locale.ENGLISH );
        symbols.setNaN( "NaN" );
        symbols.setInfinity( "Infinity" );
        format.setGroupingUsed( false );
        format.setMaximumFractionDigits( 32 );
        format.setDecimalFormatSymbols( symbols );
    }


    /**
     * Create a new <code>StringFunction</code> object.
     */
    public StringFunction()
    {}


    /**
     * Returns the string-value of <code>args.get(0)</code> 
     * or of the context node if <code>args</code> is empty.
     * 
     * @param context the context at the point in the
     *         expression where the function is called
     * @param args list with zero or one element
     * 
     * @return a <code>String</code> 
     * 
     * @throws FunctionCallException if <code>args</code> has more than one item
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        int size = args.size();

        if( size == 0 )
        {
            return evaluate( context.getNodeSet(), context.getNavigator() );
        }
        else if( size == 1 )
        {
            return evaluate( args.get( 0 ), context.getNavigator() );
        }

        throw new FunctionCallException( "string() takes at most argument." );
    }


    /**
     * Returns the XPath string-value of <code>obj</code>.
     * This operation is only defined if obj is a node, node-set,
     * <code>String</code>, <code>Number</code>, or 
     * <code>Boolean</code>. For other types this function
     * returns the empty string. 
     * 
     * @param obj the node, node-set, string, number, or boolean
     *      whose string-value is calculated
     * @param nav the navigator used to calculate the string-value
     * 
     * @return a <code>String</code>. May be empty but is never null.
     */
    public static String evaluate( Object obj, Navigator nav )
    {
        try
        {
            if( obj instanceof String )
            {
                return ( String )obj;
            }
            else if( obj instanceof Boolean )
            {
                return stringValue( ( ( Boolean )obj ).booleanValue() );
            }
            else if( obj instanceof Number )
            {
                return stringValue( ( ( Number )obj ).doubleValue() );
            }

            int node = - 1;

            if( obj == null )
                return "";

            if( obj instanceof List )
            {
                ShailList list = ( ShailList )obj;

                if( list.isEmpty() )
                    return "";
                else
                    node = list.getInt( 0 ); // do not recurse: only first list should unwrap               
            }

            if( nav != null )
            {
                // This stack of instanceof really suggests there's 
                // a failure to take advantage of polymorphism here

                if( nav.isElement( node ) )
                {
                    return nav.getElementStringValue( node );
                }
                else if( nav.isAttribute( node ) )
                {
                    return nav.getAttributeStringValue( node );
                }

                else if( nav.isDocument( node ) )
                {
                    ShailIterator childAxisIterator = ( ShailIterator )nav
                            .getChildAxisIterator( node );
                    while( childAxisIterator.hasNext() )
                    {
                        int descendant = childAxisIterator.nextNode();
                        if( nav.isElement( descendant ) )
                        {
                            return nav.getElementStringValue( descendant );
                        }
                    }
                }
                else if( nav.isProcessingInstruction( node ) )
                {
                    return nav.getProcessingInstructionData( node );
                }
                else if( nav.isComment( node ) )
                {
                    return nav.getCommentStringValue( node );
                }
                else if( nav.isText( node ) )
                {
                    return nav.getTextStringValue( node );
                }
                else if( nav.isNamespace( node ) )
                {
                    return nav.getNamespaceStringValue( node );
                }
            }

        }
        catch( UnsupportedAxisException e )
        {
            throw new JaxenRuntimeException( e );
        }

        return "";

    }


    public static String evaluate( int node, Navigator nav )
    {
        try
        {
            if( nav != null )
            {
                // This stack of instanceof really suggests there's 
                // a failure to take advantage of polymorphism here
                if( nav.isElement( node ) )
                {
                    return nav.getElementStringValue( node );
                }
                else if( nav.isAttribute( node ) )
                {
                    return nav.getAttributeStringValue( node );
                }

                else if( nav.isDocument( node ) )
                {
                    ShailIterator childAxisIterator = ( ShailIterator )nav
                            .getChildAxisIterator( node );
                    while( childAxisIterator.hasNext() )
                    {
                        int descendant = childAxisIterator.nextNode();
                        if( nav.isElement( descendant ) )
                        {
                            return nav.getElementStringValue( descendant );
                        }
                    }
                }
                else if( nav.isProcessingInstruction( node ) )
                {
                    return nav.getProcessingInstructionData( node );
                }
                else if( nav.isComment( node ) )
                {
                    return nav.getCommentStringValue( node );
                }
                else if( nav.isText( node ) )
                {
                    return nav.getTextStringValue( node );
                }
                else if( nav.isNamespace( node ) )
                {
                    return nav.getNamespaceStringValue( node );
                }
            }

        }
        catch( UnsupportedAxisException e )
        {
            throw new JaxenRuntimeException( e );
        }

        return "";

    }


    private static String stringValue( double value )
    {

        // DecimalFormat formats negative zero as "-0".
        // Therefore we need to test for zero explicitly here.
        if( value == 0 )
            return "0";

        // need to synchronize object for thread-safety
        String result = null;
        synchronized( format )
        {
            result = format.format( value );
        }
        return result;

    }


    private static String stringValue( boolean value )
    {
        return value ? "true" : "false";
    }

}
