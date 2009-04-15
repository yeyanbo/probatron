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
 * $Header: /TEST/probatron/com/griffinbrown/shail/expr/FunctionCallExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/01/08 14:41:27 $
 *
 * ====================================================================
 *
 * Copyright 2000-2004 bob mcwhirter & James Strachan.
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
 * @version $Id: FunctionCallExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 */

package com.griffinbrown.shail.expr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.expr.FunctionCallExpr;



public class FunctionCallExprImpl extends ExprImpl implements FunctionCallExpr
{
    /**
     * 
     */
    private static final long serialVersionUID = -4747789292572193708L;
    private String prefix;
    private String functionName;
    private List parameters;
    private static Logger logger = Logger.getLogger( FunctionCallExprImpl.class );

    public FunctionCallExprImpl(String prefix, String functionName)
    {
//        logger.debug("called function "+functionName);
        this.prefix = prefix;
        this.functionName = functionName;
        this.parameters = new ArrayList();
    }

    public void addParameter(Expr parameter)
    {
        this.parameters.add(parameter);
    }


    public List getParameters()
    {
        return this.parameters;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public String getFunctionName()
    {
        return this.functionName;
    }


    public String getText()
    {
        StringBuffer buf = new StringBuffer();
        String prefix = getPrefix();

        if (prefix != null &&
                prefix.length() > 0)
        {
            buf.append(prefix);
            buf.append(":");
        }

        buf.append(getFunctionName());
        buf.append("(");

        Iterator paramIter = getParameters().iterator();

        while (paramIter.hasNext()) {
            Expr eachParam = (Expr) paramIter.next();

            buf.append(eachParam.getText());

            if (paramIter.hasNext())
            {
                buf.append(", ");
            }
        }

        buf.append(")");

        return buf.toString();
    }

    public Expr simplify()
    {
        List paramExprs = getParameters();
        int paramSize = paramExprs.size();

        List newParams = new ArrayList(paramSize);

        for (int i = 0; i < paramSize; ++i)
        {
            Expr eachParam = (Expr) paramExprs.get(i);

            newParams.add(eachParam.simplify());
        }

        this.parameters = newParams;

        return this;
    }


    public String toString()
    {
        String prefix = getPrefix();

        if (prefix == null)
        {
            return "[(FunctionCallExprImpl): " + getFunctionName() + "(" + getParameters() + ") ]";
        }

        return "[(FunctionCallExprImpl): " + getPrefix() + ":" + getFunctionName() + "(" + getParameters() + ") ]";
    }

    public Object evaluate(Context context) throws JaxenException
    {
//        logger.debug("***evaluate() function="+functionName);
        String namespaceURI =
                context.translateNamespacePrefixToUri(getPrefix());

        Function func = context.getFunction(namespaceURI,
                getPrefix(),
                getFunctionName());
        List paramValues = evaluateParams(context);

        return func.call(context, paramValues);
    }

    public List evaluateParams(Context context) throws JaxenException
    {
//        logger.debug( "evaluating params for function "+functionName );
        List paramExprs = getParameters();
        int paramSize = paramExprs.size();

        List paramValues = new ArrayList(paramSize);

        for (int i = 0; i < paramSize; ++i)
        {
            Expr eachParam = (Expr) paramExprs.get(i);
            
//            logger.debug( "eachParam="+eachParam.getClass().getName() );

            Object eachValue = eachParam.evaluate(context);
            
//            logger.debug( functionName+" param["+i+"]="+eachValue );

            paramValues.add(eachValue);
        }
        return paramValues;
    }

}

