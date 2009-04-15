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
 * $Header: /TEST/xmlprobe-dev/shail-dev/src/com/griffinbrown/shail/expr/TruthExprImpl.java,v
 * 1.1 2007/07/23 12:27:31 GBDP\andrews Exp $ $Revision: 1.1 $ $Date: 2009/01/08 14:41:28 $
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
 * @version $Id: TruthExprImpl.java,v 1.1 2009/01/08 14:41:28 GBDP\andrews Exp $
 */

package com.griffinbrown.shail.expr;

import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.function.BooleanFunction;
import org.probatron.jaxen.function.NumberFunction;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailSingletonList;

abstract class TruthExprImpl extends BinaryExprImpl
{
    private static Logger logger = Logger.getLogger( TruthExprImpl.class );


    TruthExprImpl( Expr lhs, Expr rhs )
    {
        super( lhs, rhs );
    }


    public String toString()
    {
        return "[(TruthExprImpl): " + getLHS() + ", " + getRHS() + "]";
    }


    protected boolean bothAreSets( Object lhs, Object rhs )
    {
        return ( lhs instanceof List && rhs instanceof List );
    }


    protected boolean eitherIsSet( Object lhs, Object rhs )
    {
        return ( lhs instanceof List || rhs instanceof List );
    }


    protected boolean isSet( Object obj )
    {
        return ( obj instanceof List );
    }


    protected boolean setIsEmpty( List set )
    {
        return ( set == null || set.size() == 0 );
    }


    protected boolean eitherIsBoolean( Object lhs, Object rhs )
    {
        return ( lhs instanceof Boolean || rhs instanceof Boolean );
    }


    protected boolean bothAreBoolean( Object lhs, Object rhs )
    {
        return ( lhs instanceof Boolean && rhs instanceof Boolean );
    }


    protected boolean eitherIsNumber( Object lhs, Object rhs )
    {
        return ( lhs instanceof Number || rhs instanceof Number );
    }


    protected Boolean evaluateSetSet( List lhsSet, List rhsSet, Navigator nav )
    {
        //both sets are empty
        if( setIsEmpty( lhsSet ) || setIsEmpty( rhsSet ) )
        {
            return Boolean.FALSE;
        }

        ShailIterator lhsIterator = null;
        ShailIterator rhsIterator = null;

        //both sets are singleton lists
        if( ( lhsSet instanceof ShailSingletonList ) && ( rhsSet instanceof ShailSingletonList ) )
        {
            if( evaluateObjectObject( lhsSet.get( 0 ), rhsSet.get( 0 ), nav ) )
            {
                return Boolean.TRUE;
            }
        }

        //lhs is singleton list
        else if( ( lhsSet instanceof ShailSingletonList )
                && ! ( rhsSet instanceof ShailSingletonList ) )
        {
//            logger.debug( "lhs is singleton list:" + lhsSet + "; rhs is not:" + rhsSet );
            Object lhs = lhsSet.get( 0 );
            for( rhsIterator = ( ShailIterator )rhsSet.iterator(); rhsIterator.hasNext(); )
            {
                int rhs = rhsIterator.nextNode();

                if( evaluateObjectInt( lhs, rhs, nav ) )
                {
                    return Boolean.TRUE;
                }
            }
        }

        //rhs is singleton list
        else if( ! ( lhsSet instanceof ShailSingletonList )
                && ( rhsSet instanceof ShailSingletonList ) )
        {
            Object rhs = rhsSet.get( 0 );
            for( lhsIterator = ( ShailIterator )lhsSet.iterator(); lhsIterator.hasNext(); )
            {
                int lhs = lhsIterator.nextNode();

                if( evaluateIntObject( lhs, rhs, nav ) )
                {
                    return Boolean.TRUE;
                }
            }
        }

        //neither is singleton list
        else
        {
            for( lhsIterator = ( ShailIterator )lhsSet.iterator(); lhsIterator.hasNext(); )
            {
                int lhs = lhsIterator.nextNode();

                for( rhsIterator = ( ShailIterator )rhsSet.iterator(); rhsIterator.hasNext(); )
                {
                    int rhs = rhsIterator.nextNode();

                    //                logger.debug("rhs="+rhs);

                    //                if( evaluateIntInt( lhs, rhs, nav ) )
                    if( lhs == rhs ) //FIXME!
                    {
                        return Boolean.TRUE;
                    }
                }
            }
        }

        return Boolean.FALSE;
    }


    protected boolean evaluateIntObject( int i, Object obj, Navigator nav )
    {
//        logger.debug( "int=" + i + " obj=" + obj + " class=" + obj.getClass().getName() );

        //        if( eitherIsBoolean( lhs, rhs ) )
        if( obj instanceof Boolean )
        {
            return evaluateObjectObject( new Boolean( i != - 1 ), BooleanFunction.evaluate(
                    obj, nav ) );
        }
        //        else if( eitherIsNumber( lhs, rhs ) )
        else if( obj instanceof Number )
        {
//            logger.debug( "object " + obj + " is Number:" + obj.getClass().getName() );

            Object int2num = NumberFunction.evaluate( i, nav );
            Object obj2num = NumberFunction.evaluate( obj, nav );

//            logger.debug( "returning evaluateObjectObject(" + int2num + " "
//                    + int2num.getClass() + ", " + obj2num + " " + obj2num.getClass() + ")" );

            return evaluateObjectObject( int2num, obj2num );
        }
        else
        {
//            logger.debug( "object " + obj + " neither Boolean nor Number:"
//                    + obj.getClass().getName() );
            return evaluateObjectObject( StringFunction.evaluate( i, nav ), StringFunction
                    .evaluate( obj, nav ) );
        }
    }
    
    protected boolean evaluateObjectInt( Object lhs, int rhs, Navigator nav )
    {
        //        if( eitherIsBoolean( lhs, rhs ) )
        if( lhs instanceof Boolean )
        {
            return evaluateObjectObject( BooleanFunction.evaluate(
                    lhs, nav ), new Boolean( rhs != - 1 ) );
        }
        //        else if( eitherIsNumber( lhs, rhs ) )
        else if( lhs instanceof Number )
        {
            return evaluateObjectObject( NumberFunction.evaluate( lhs, nav ), NumberFunction
                    .evaluate( rhs, nav ) );
        }
        else
        {
            return evaluateObjectObject( StringFunction.evaluate( lhs, nav ), StringFunction
                    .evaluate( rhs, nav ) );
        }
    }      


    protected boolean evaluateObjectObject( Object lhs, Object rhs, Navigator nav )
    {
        if( eitherIsBoolean( lhs, rhs ) )
        {
            return evaluateObjectObject( BooleanFunction.evaluate( lhs, nav ), BooleanFunction
                    .evaluate( rhs, nav ) );
        }
        else if( eitherIsNumber( lhs, rhs ) )
        {
            return evaluateObjectObject( NumberFunction.evaluate( lhs, nav ), NumberFunction
                    .evaluate( rhs, nav ) );
        }
        else
        {
            return evaluateObjectObject( StringFunction.evaluate( lhs, nav ), StringFunction
                    .evaluate( rhs, nav ) );
        }
    }


    protected abstract boolean evaluateObjectObject( Object lhs, Object rhs );

}
