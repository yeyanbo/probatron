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
 * $Header: /TEST/probatron/com/griffinbrown/shail/expr/PathExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/01/08 14:41:27 $
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
 * @version $Id: PathExprImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 */

package com.griffinbrown.shail.expr;


import org.probatron.jaxen.Context;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.expr.LocationPath;
import org.probatron.jaxen.expr.PathExpr;


class PathExprImpl extends ExprImpl implements PathExpr
{

    private static final long serialVersionUID = - 6593934674727004281L;
    private Expr filterExpr;
    private LocationPath locationPath;


    PathExprImpl( Expr filterExpr, LocationPath locationPath )
    {
        this.filterExpr = filterExpr;
        this.locationPath = locationPath;
    }


    public Expr getFilterExpr()
    {
        return this.filterExpr;
    }


    public void setFilterExpr( Expr filterExpr )
    {
        this.filterExpr = filterExpr;
    }


    public LocationPath getLocationPath()
    {
        return this.locationPath;
    }


    public String toString()
    {
        if( getLocationPath() != null )
        {
            return "[(PathExprImpl): " + getFilterExpr() + ", " + getLocationPath() + "]";
        }

        return "[(PathExprImpl): " + getFilterExpr() + "]";
    }


    public String getText()
    {
        StringBuffer buf = new StringBuffer();

        if( getFilterExpr() != null )
        {
            buf.append( getFilterExpr().getText() );
        }

        if( getLocationPath() != null )
        {
            if( ! getLocationPath().getSteps().isEmpty() )
                buf.append( "/" );
            buf.append( getLocationPath().getText() );
        }

        return buf.toString();
    }


    public Expr simplify()
    {
        if( getFilterExpr() != null )
        {
            setFilterExpr( getFilterExpr().simplify() );
        }

        if( getLocationPath() != null )
        {
            getLocationPath().simplify();
        }

        if( getFilterExpr() == null && getLocationPath() == null )
        {
            return null;
        }

        if( getLocationPath() == null )
        {
            return getFilterExpr();
        }

        if( getFilterExpr() == null )
        {
            return getLocationPath();
        }

        return this;
    }


    public Object evaluate( Context context ) throws JaxenException
    {
        Object results = null;
        Context pathContext = null;
        if( getFilterExpr() != null )
        {
            results = getFilterExpr().evaluate( context );
            pathContext = new Context( context.getContextSupport() );
            pathContext.setNodeSet( convertToList( results ) );
        }
        if( getLocationPath() != null )
        {
            return getLocationPath().evaluate( pathContext );
        }
        return results;
    }

}
