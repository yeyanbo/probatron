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
 * /home/projects/jaxen/scm/jaxen/src/java/main/org/jaxen/function/LocalNameFunction.java,v 1.14
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
 * @version $Id: LocalNameFunction.java,v 1.1 2009/02/11 08:52:57 GBDP\andrews Exp $
 */

package org.probatron.jaxen.function;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.Navigator;

import com.griffinbrown.shail.util.ShailList;

/**
 *   <p><b>4.1</b> <code><i>string</i> local-name(<i>node-set?</i>)</code></p> 
 *  
 * 
 * <blockquote src="http://www.w3.org/TR/xpath">
 * The <b>local-name</b> function returns the local part of the 
 * expanded-name of the node in the argument node-set that is first in document order. 
 * If the argument node-set is empty or the first node has no expanded-name, an 
 * empty string is returned. If the argument is omitted, it defaults to a node-set with the context node as its only member.
 *
 * </blockquote>
 * 
 * @author bob mcwhirter (bob @ werken.com)
 * @see <a href="http://www.w3.org/TR/xpath#function-local-name" target="_top">Section 4.1 of the XPath Specification</a>
 */
public class LocalNameFunction implements Function
{

    /**
     * Create a new <code>LocalNameFunction</code> object.
     */
    public LocalNameFunction()
    {}

    private static Logger logger = Logger.getLogger( LocalNameFunction.class );


    /**
     * Returns the local-name of the specified node or the context node if 
     * no arguments are provided.
     * 
     * @param context the context at the point in the
     *         expression where the function is called
     * @param args a <code>List</code> containing zero or one items
     * 
     * @return a <code>String</code> containing the local-name
     * 
     * @throws FunctionCallException if <code>args</code> has more than one item
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        if( args.size() == 0 )
        {
            return evaluate( context.getNodeSet(), context.getNavigator() );
        }

        if( args.size() == 1 )
        {
            return evaluate( args, context.getNavigator() );
        }

        throw new FunctionCallException( "local-name() requires zero or one argument." );
    }


    /**
     * Returns the local-name of <code>list.get(0)</code>
     * 
     * @param list a list of nodes
     * @param nav the <code>Navigator</code> used to retrieve the local name
     * 
     * @return the local-name of <code>list.get(0)</code>
     * 
     * @throws FunctionCallException if <code>list.get(0)</code> is not a node
     */
    public static String evaluate( List list, Navigator nav ) throws FunctionCallException
    {
        if( ! list.isEmpty() )
        {
            if( list instanceof ArrayList )
            {
                if( list.get( 0 ) instanceof ShailList )    //means it's a nodeset
                    return evaluate( ( List )list.get( 0 ), nav );
                else    //means it's an atomic value
                    throw new FunctionCallException(
                            "The argument to the local-name function must be a node-set" );
            }
            else
            {
                int first = ( ( ShailList )list ).getInt( 0 );

                if( nav.isElement( first ) )
                {
                    return nav.getElementName( first );
                }
                else if( nav.isAttribute( first ) )
                {
                    return nav.getAttributeName( first );
                }
                else if( nav.isProcessingInstruction( first ) )
                {
                    return nav.getProcessingInstructionTarget( first );
                }
                else if( nav.isNamespace( first ) )
                {
                    return nav.getNamespacePrefix( first );
                }
                else if( nav.isDocument( first ) )
                {
                    return "";
                }
                else if( nav.isComment( first ) )
                {
                    return "";
                }
                else if( nav.isText( first ) )
                {
                    return "";
                }
                else
                {
                    throw new FunctionCallException(
                            "The argument to the local-name function must be a node-set" );
                }
            }
        }
        
        return "";
    }
}
