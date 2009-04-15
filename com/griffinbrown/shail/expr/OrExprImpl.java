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
 * $Header: /TEST/probatron/com/griffinbrown/shail/expr/OrExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
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
 * @version $Id: OrExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 */



package com.griffinbrown.shail.expr;


import org.probatron.jaxen.Context;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.function.BooleanFunction;


class OrExprImpl extends LogicalExprImpl 
{
    /**
     * 
     */
    private static final long serialVersionUID = 4894552680753026730L;

    OrExprImpl(Expr lhs,
                         Expr rhs)
    {
        super( lhs,
               rhs );
    }

    public String getOperator()
    {
        return "or";
    }

    public String toString()
    {
        return "[(OrExprImpl): " + getLHS() + ", " + getRHS() + "]";
    }

    public Object evaluate(Context context) throws JaxenException
    {
        Navigator nav = context.getNavigator();
        Boolean lhsValue = BooleanFunction.evaluate( getLHS().evaluate( context ), nav );

        if ( lhsValue.booleanValue() )
        {
            return Boolean.TRUE;
        }

        // Short circuits are required in XPath. "The right operand is not 
        // evaluated if the left operand evaluates to true."
        Boolean rhsValue = BooleanFunction.evaluate( getRHS().evaluate( context ), nav );

        if ( rhsValue.booleanValue() )
        {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
    
    protected boolean evaluateObjectObject( Object o1, Object o2 )
    {
        return false;
    }    
    
}
