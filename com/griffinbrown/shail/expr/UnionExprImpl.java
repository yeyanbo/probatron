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
 * $Header: /TEST/probatron/com/griffinbrown/shail/expr/UnionExprImpl.java,v 1.1 2009/01/08 14:41:28 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/01/08 14:41:28 $
 *
 * ====================================================================
 *
 * Copyright 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
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
 * @version $Id: UnionExprImpl.java,v 1.1 2009/01/08 14:41:28 GBDP\andrews Exp $
 */

package com.griffinbrown.shail.expr;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.XPathSyntaxException;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.expr.UnionExpr;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

public class UnionExprImpl extends BinaryExprImpl implements UnionExpr
{
    /**
     * 
     */
    private static final long serialVersionUID = 7629142718276852707L;

    private static Logger logger = Logger.getLogger( UnionExprImpl.class );


    public UnionExprImpl( Expr lhs, Expr rhs )
    {
        super( lhs, rhs );
    }


    public String getOperator()
    {
        return "|";
    }


    public String toString()
    {
        return "[(UnionExprImpl): " + getLHS() + ", " + getRHS() + "]";
    }


    public Object evaluate( Context context ) throws JaxenException
    {
//        logger.debug( "***evaluate() " + getClass().getName() );

        ShailList results = new ShailList();

        try
        {
            ShailList lhsResults = ( ShailList )getLHS().evaluate( context );
            ShailList rhsResults = ( ShailList )getRHS().evaluate( context );

            IdentitySet unique = new IdentitySet();

            results.addAll( lhsResults );
//            unique.addAll( lhsResults );
            
            ShailIterator lhsIter = (ShailIterator)lhsResults.iterator();
            while( lhsIter.hasNext() )
            {
                int i = lhsIter.nextNode();
                unique.add( i );
            }

            ShailIterator rhsIter = (ShailIterator)rhsResults.iterator();

            while( rhsIter.hasNext() )
            {
                int i = rhsIter.nextNode();

                if( ! unique.contains( i ) )
                {
                    results.addInt( i );
                    unique.add( i );
                }
            }

            //            Collections.sort(results, new NodeComparator(context.getNavigator()));
            results.sort();

//            logger.debug( "returning " + results );
            return results;
        }
        catch( ClassCastException e )
        {
            throw new XPathSyntaxException( this.getText(), context.getPosition(),
                    "Unions are only allowed over node-sets" );
        }
    }

}
