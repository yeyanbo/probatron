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
 * $Header: /TEST/probatron/org/probatron/jaxen/expr/PredicateSet.java,v 1.1 2009/02/11 08:52:56 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/02/11 08:52:56 $
 *
 * ====================================================================
 *
 * Copyright 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   * Neither the name of the Jaxen Project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Jaxen Project and was originally
 * created by bob mcwhirter <bob@werken.com> and
 * James Strachan <jstrachan@apache.org>.  For more information on the
 * Jaxen Project, please see <http://www.jaxen.org/>.
 *
 * @version $Id: PredicateSet.java,v 1.1 2009/02/11 08:52:56 GBDP\andrews Exp $
 */

package org.probatron.jaxen.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.probatron.jaxen.Context;
import org.probatron.jaxen.ContextSupport;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.BooleanFunction;

import com.griffinbrown.shail.util.ShailList;

/**
 * <p>
 * Represents the collection of predicates that follow the node-test in a
 * location path. 
 * </p>
 * 
 * <p>
 * There is no rule that the same predicate may not 
 * appear twice in an XPath expression, nor does this class enforce any such rule.
 * This is implemented more as a list than a set. However, adding the swme predicate 
 * twice should have no effect on the final result other than slowing it down.
 * </p>
 */
public class PredicateSet implements Serializable
{

    private static final long serialVersionUID = - 7166491740228977853L;

    private List predicates;


    /**
     * Create a new empty predicate set.
     */
    public PredicateSet()
    {
        this.predicates = Collections.EMPTY_LIST;
    }


    /**
     * Add a predicate to the set.
     * 
     * @param predicate the predicate to be inserted
     */
    public void addPredicate( Predicate predicate )
    {
        if( this.predicates == Collections.EMPTY_LIST )
        {
            this.predicates = new ArrayList();
        }

        this.predicates.add( predicate );
    }


    /**
     * Returns the list containing the predicates.
     * This list is live, not a copy.
     * 
     * @return a live list of predicates
     */
    public List getPredicates()
    {
        return this.predicates;
    }


    /**
     * Simplify each of the predicates in the list.
     */
    public void simplify()
    {
        Iterator predIter = this.predicates.iterator();
        Predicate eachPred = null;

        while( predIter.hasNext() )
        {
            eachPred = ( Predicate )predIter.next();
            eachPred.simplify();
        }
    }


    /**
     * Returns the XPath string containing each of the predicates.
     * 
     * @return the XPath string containing each of the predicates
     */
    public String getText()
    {
        StringBuffer buf = new StringBuffer();

        Iterator predIter = this.predicates.iterator();
        Predicate eachPred = null;

        while( predIter.hasNext() )
        {
            eachPred = ( Predicate )predIter.next();
            buf.append( eachPred.getText() );
        }

        return buf.toString();
    }


    /**
     * <p>Returns true if any of the supplied nodes satisfy 
     * all the predicates in the set. Returns false if none of the supplied
     * nodes matches all the predicates in the set. Returns false if the 
     * node-set is empty.</p>
     * 
     * @param contextNodeSet the nodes to test against these predicates
     * @param support ????
     * @return true if any node in the contextNodeSet matches all the predicates
     * @throws JaxenException
     */
    public boolean evaluateAsBoolean( List contextNodeSet, ContextSupport support )
            throws JaxenException
    {
        return anyMatchingNode( contextNodeSet, support );
    }


    private boolean anyMatchingNode( List contextNodeSet, ContextSupport support )
            throws JaxenException
    {
        // Easy way out (necessary)
        if( predicates.size() == 0 )
        {
            return false;
        }
        Iterator predIter = predicates.iterator();

        // initial list to filter
        List nodes2Filter = contextNodeSet;
        // apply all predicates
        while( predIter.hasNext() )
        {
            final int nodes2FilterSize = nodes2Filter.size();
            // Set up a dummy context with a list to hold each node
            Context predContext = new Context( support );
            ShailList tempList = new ShailList( 1 );
            predContext.setNodeSet( tempList );
            // loop through the current nodes to filter and add to the
            // filtered nodes list if the predicate succeeds
            for( int i = 0; i < nodes2FilterSize; ++i )
            {
                Object contextNode = nodes2Filter.get( i );
                tempList.clear();
                tempList.add( contextNode );
                predContext.setNodeSet( tempList );
                // ????
                predContext.setPosition( i + 1 );
                predContext.setSize( nodes2FilterSize );
                Object predResult = ( ( Predicate )predIter.next() ).evaluate( predContext );
                if( predResult instanceof Number )
                {
                    // Here we assume nodes are in forward or reverse order
                    // as appropriate for axis
                    int proximity = ( ( Number )predResult ).intValue();
                    if( proximity == ( i + 1 ) )
                    {
                        return true;
                    }
                }
                else
                {
                    Boolean includes = BooleanFunction.evaluate( predResult, predContext
                            .getNavigator() );
                    if( includes.booleanValue() )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * <p>Returns all of the supplied nodes that satisfy 
     * all the predicates in the set. </p>
     * 
     * @param contextNodeSet the nodes to test against these predicates
     * @param support ????
     * @return all the nodes that match each of the predicates
     * @throws JaxenException
     */
    public ShailList evaluatePredicates( ShailList contextNodeSet, ContextSupport support )
            throws JaxenException
    {
        // Easy way out (necessary)
        if( predicates.size() == 0 )
        {
            return contextNodeSet;
        }
        Iterator predIter = predicates.iterator();

        // initial list to filter
        ShailList nodes2Filter = contextNodeSet;
        // apply all predicates
        while( predIter.hasNext() )
        {
            nodes2Filter = applyPredicate( ( Predicate )predIter.next(), nodes2Filter, support );
        }

        return nodes2Filter;
    }


    public ShailList applyPredicate( Predicate predicate, ShailList nodes2Filter, ContextSupport support )
            throws JaxenException
    {
        final int nodes2FilterSize = nodes2Filter.size();
        ShailList filteredNodes = new ShailList( nodes2FilterSize );
        // Set up a dummy context with a list to hold each node
        Context predContext = new Context( support );
        ShailList tempList = new ShailList( 1 );
        predContext.setNodeSet( tempList );
        // loop through the current nodes to filter and add to the
        // filtered nodes list if the predicate succeeds
        for( int i = 0; i < nodes2FilterSize; ++i )
        {
            int contextNode = nodes2Filter.getInt( i );
            tempList.clear();
            tempList.addInt( contextNode );
            predContext.setNodeSet( tempList );
            // ????
            predContext.setPosition( i + 1 );
            predContext.setSize( nodes2FilterSize );
            Object predResult = predicate.evaluate( predContext );
            if( predResult instanceof Number )
            {
                // Here we assume nodes are in forward or reverse order
                // as appropriate for axis
                int proximity = ( ( Number )predResult ).intValue();
                if( proximity == ( i + 1 ) )
                {
                    filteredNodes.addInt( contextNode );
                }
            }
            else
            {
                Boolean includes = BooleanFunction.evaluate( predResult, predContext
                        .getNavigator() );
                if( includes.booleanValue() )
                {
                    filteredNodes.addInt( contextNode );
                }
            }
        }
        return filteredNodes;
    }

}
