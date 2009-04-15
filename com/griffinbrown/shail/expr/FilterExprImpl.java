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
 * $Header: /TEST/probatron/com/griffinbrown/shail/expr/FilterExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/01/08 14:41:27 $
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
 * @version $Id: FilterExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 */



package com.griffinbrown.shail.expr;

import java.util.List;

import org.probatron.jaxen.Context;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.expr.FilterExpr;
import org.probatron.jaxen.expr.Predicate;
import org.probatron.jaxen.expr.PredicateSet;
import org.probatron.jaxen.expr.Predicated;


import com.griffinbrown.shail.util.ShailList;


public class FilterExprImpl extends ExprImpl implements FilterExpr, Predicated
{
    /**
     * 
     */
    private static final long serialVersionUID = -549640659288005735L;
    private Expr expr;
    private org.probatron.jaxen.expr.PredicateSet predicates;

    public FilterExprImpl(PredicateSet predicateSet)
    {
        this.predicates = predicateSet;
    }

    public FilterExprImpl(Expr expr, PredicateSet predicateSet)
    {
        this.expr       = expr;
        this.predicates = predicateSet;
    }

    public void addPredicate(Predicate predicate)
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

    public Expr getExpr()
    {
        return this.expr;
    }

    public String toString()
    {
        return "[(FilterExprImpl): expr: " + expr + " predicates: " + predicates + " ]";
    }

    public String getText()
    {
        String text = "";
        if ( this.expr != null )
        {
            text = this.expr.getText();
        }
        text += predicates.getText();
        return text;
    }

    public Expr simplify()
    {
        this.predicates.simplify();

        if ( this.expr != null ) 
        {
            this.expr = this.expr.simplify();
        }

        if ( this.predicates.getPredicates().size() == 0 )
        {
            return getExpr();
        }

        return this;
    }

    /** Returns true if the current filter matches at least one of the context nodes
     */
    public boolean asBoolean(Context context) throws JaxenException 
    {
        Object results = null;
        if ( expr != null ) 
        {
            results = expr.evaluate( context );
        }
        else
        {
            ShailList nodeSet = context.getNodeSet();
            ShailList list = new ShailList(nodeSet.size());
            list.addAll( nodeSet );
            results = list;
        }
        
        if ( results instanceof Boolean ) 
        {
            Boolean b = (Boolean) results;
            return b.booleanValue();
        }
        if ( results instanceof List )
        {
            return getPredicateSet().evaluateAsBoolean( 
                (List) results, context.getContextSupport() 
            );
        }
        
        return false;
    }
    
    public Object evaluate(Context context) throws JaxenException
    {
        Object results = getExpr().evaluate( context );
        
        if ( results instanceof ShailList )
        {
            ShailList newresults = getPredicateSet().evaluatePredicates( (ShailList)results,
                                    context.getContextSupport() );
        results = newresults;
        }

        return results;
    }

}
