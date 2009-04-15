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
 * $Header: /TEST/xmlprobe-dev/shail-dev/src/com/griffinbrown/shail/expr/StepImpl.java,v 1.1
 * 2007/07/23 12:27:30 GBDP\andrews Exp $ $Revision: 1.1 $ $Date: 2009/01/08 14:41:28 $
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
 * @version $Id: StepImpl.java,v 1.1 2009/01/08 14:41:28 GBDP\andrews Exp $
 */
package com.griffinbrown.shail.expr;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.ContextSupport;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.UnsupportedAxisException;
import org.probatron.jaxen.expr.Predicate;
import org.probatron.jaxen.expr.PredicateSet;
import org.probatron.jaxen.expr.Step;
import org.probatron.jaxen.expr.iter.IterableAxis;
import org.probatron.jaxen.saxpath.Axis;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

public abstract class StepImpl implements Step
{
    private IterableAxis axis;
    private PredicateSet predicates;

    private static final Logger logger = Logger.getLogger( StepImpl.class );


    public StepImpl( IterableAxis axis, PredicateSet predicates )
    {
        this.axis = axis;
        this.predicates = predicates;
//        logger.debug( "StepImpl(" + axis + "," + predicates + ")" );
    }


    public void addPredicate( Predicate predicate )
    {
        this.predicates.addPredicate( predicate );
    }


    public List getPredicates()
    {
        return this.predicates.getPredicates();
    }


    public PredicateSet getPredicateSet()
    {
        return this.predicates;
    }


    public int getAxis()
    {
        return this.axis.value();
    }


    public IterableAxis getIterableAxis()
    {
        return this.axis;
    }


    public String getAxisName()
    {
        return Axis.lookup( getAxis() );
    }


    public String getText()
    {
        return this.predicates.getText();
    }


    public String toString()
    {
        return getIterableAxis() + " " + super.toString();
    }


    public void simplify()
    {
        this.predicates.simplify();
    }


    public Iterator axisIterator( int contextNode, ContextSupport support )
            throws UnsupportedAxisException
    {
        //        logger.debug( "axis="+getIterableAxis() );
        return getIterableAxis().iterator( contextNode, support );
    }


    public ShailList evaluate( final Context context ) throws JaxenException
    {
//        logger.debug( "***evaluate " + getClass().getName() );

        final ShailList contextNodeSet = context.getNodeSet();
        final IdentitySet unique = new IdentitySet();
        final int contextSize = contextNodeSet.size();

        // ???? try linked lists instead?
        // ???? initial size for these?
        final ShailList interimSet = new ShailList();
        final ShailList newNodeSet = new ShailList();
        final ContextSupport support = context.getContextSupport();

        // ???? use iterator instead
        for( int i = 0; i < contextSize; ++i )
        {
            int eachContextNode = contextNodeSet.getInt( i );
//            logger.debug( "eachContextNode=" + eachContextNode );

            /* See jaxen-106. Might be able to optimize this by doing
             * specific matching for individual axes. For instance on namespace axis
             * we should only get namespace nodes and on attribute axes we only get 
             * attribute nodes. Self and parent axes have single members.
             * Children, descendant, ancestor, and sibling axes never 
             * see any attributes or namespaces
             */
            ShailIterator axisNodeIter = ( ShailIterator )axis.iterator( eachContextNode,
                    support );
            while( axisNodeIter.hasNext() )
            {
                int eachAxisNode = axisNodeIter.nextNode();
                //                logger.debug( "axisNode=" + eachAxisNode );
                if( ! unique.contains( eachAxisNode ) )
                {
                    if( matches( eachAxisNode, support ) )
                    {
                        unique.add( eachAxisNode );
                        interimSet.addInt( eachAxisNode );
                    }
                }
            }
            ShailList foo = getPredicateSet().evaluatePredicates( interimSet, support );
            newNodeSet.addAll( foo );
            interimSet.clear();
        }

        return newNodeSet;
    }

}
