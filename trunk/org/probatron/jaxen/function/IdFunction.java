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
 * $Header: /home/projects/jaxen/scm/jaxen/src/java/main/org/jaxen/function/IdFunction.java,v
 * 1.13 2006/02/05 21:47:41 elharo Exp $ $Revision: 1.1 $ $Date: 2009/02/11 08:52:57 $
 * 
 * ====================================================================
 * 
 * Copyright 2000-2002 bob mcwhirter & James Strachan. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *  * Neither the name of the Jaxen Project nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
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
 * @version $Id: IdFunction.java,v 1.1 2009/02/11 08:52:57 GBDP\andrews Exp $
 */

package org.probatron.jaxen.function;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.Navigator;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

/**
 * <p><b>4.1</b> <code><i>node-set</i> id(<i>object</i>)</code> </p>
 *  
 * <p>The <b>id</b> function returns a <code>List</code>
 * of all the elements in the context document that have an ID
 * matching one of a specified list of IDs. How an attribute is determined
 * to be of type ID depends on the navigator, but it normally requires that
 * the attribute be declared to have type ID in the DTD. 
 * </p>
 * 
 * <p>
 * There should be no more than one element in any document with a 
 * certain ID. However, if there are multiple such elements--i.e. if 
 * there are duplicate IDs--then this function selects only the first element 
 * in document order with the specified ID. 
 * </p>
 * 
 * @author Erwin Bolwidt (ejb @ klomp.org)
 * @author J\u00e9r\u00f4me N\u00e8gre (jerome.negre @ e-xmlmedia.fr)
 * 
 * @see <a href="http://www.w3.org/TR/xpath#function-id" target="_top">Section 4.1 of the XPath Specification</a>
 */
public class IdFunction implements Function
{

    /**
     * Create a new <code>IdFunction</code> object.
     */
    public IdFunction()
    {}


    /** 
     * Returns a list of the nodes with the specified IDs.
     *
     * @param context the context at the point in the
     *         expression when the function is called
     * @param args a list with exactly one item which is either a string
     *     a node-set
     * 
     * @return a <code>List</code> containing the first node in document 
     *     with each of the specified IDs; or 
     *     an empty list if there are no such nodes
     * 
     * @throws FunctionCallException if <code>args</code> has more or less than one item
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        if( args.size() == 1 )
        {
            return evaluate( context.getNodeSet(), args.get( 0 ), context.getNavigator() );
        }

        throw new FunctionCallException( "id() requires one argument" );
    }


    /** 
     * Returns a list of the nodes with the specified IDs.
     * 
     * @param contextNodes the context node-set. The first item in this list
     *     determines the document in which the search is performed.
     * @param arg the ID or IDs to search for
     * @param nav the navigator used to calculate string-values and search
     *     by ID
     * 
     * @return a <code>List</code> containing the first node in document 
     *     with each of the specified IDs; or 
     *     an empty list if there are no such nodes
     * 
     */
    public static List evaluate( List contextNodes, Object arg, Navigator nav )
    {
        if( contextNodes.size() == 0 )
            return Collections.EMPTY_LIST;

        ShailList nodes = new ShailList();

        int contextNode = ( ( ShailList )contextNodes ).getInt( 0 );

        if( arg instanceof ShailList )
        {
            ShailIterator iter = ( ( ShailList )arg ).shailListIterator();
            while( iter.hasNext() )
            {
                String id = StringFunction.evaluate( iter.nextNode(), nav );
                nodes.addAll( evaluate( contextNodes, id, nav ) );
            }
        }
        else
        {
            String ids = StringFunction.evaluate( arg, nav );
            StringTokenizer tok = new StringTokenizer( ids, " \t\n\r" );
            while( tok.hasMoreTokens() )
            {
                String id = tok.nextToken();
                int node = nav.getElementById( contextNode, id );
                if( node != - 1 )
                {
                    nodes.addInt( node );
                }
            }
        }

        return nodes;
    }

}
