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
 * Created on 4 Jun 2007
 */
package com.griffinbrown.shail.expr;


import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.expr.BinaryExpr;
import org.probatron.jaxen.expr.Expr;
import org.probatron.jaxen.expr.FilterExpr;
import org.probatron.jaxen.expr.FunctionCallExpr;
import org.probatron.jaxen.expr.LiteralExpr;
import org.probatron.jaxen.expr.LocationPath;
import org.probatron.jaxen.expr.NumberExpr;
import org.probatron.jaxen.expr.PathExpr;
import org.probatron.jaxen.expr.Predicate;
import org.probatron.jaxen.expr.PredicateSet;
import org.probatron.jaxen.expr.Step;
import org.probatron.jaxen.expr.UnionExpr;
import org.probatron.jaxen.expr.VariableReferenceExpr;
import org.probatron.jaxen.expr.XPathExpr;
import org.probatron.jaxen.expr.XPathFactory;
import org.probatron.jaxen.expr.iter.IterableAncestorAxis;
import org.probatron.jaxen.expr.iter.IterableAncestorOrSelfAxis;
import org.probatron.jaxen.expr.iter.IterableAttributeAxis;
import org.probatron.jaxen.expr.iter.IterableAxis;
import org.probatron.jaxen.expr.iter.IterableChildAxis;
import org.probatron.jaxen.expr.iter.IterableDescendantAxis;
import org.probatron.jaxen.expr.iter.IterableDescendantOrSelfAxis;
import org.probatron.jaxen.expr.iter.IterableFollowingAxis;
import org.probatron.jaxen.expr.iter.IterableFollowingSiblingAxis;
import org.probatron.jaxen.expr.iter.IterableNamespaceAxis;
import org.probatron.jaxen.expr.iter.IterableParentAxis;
import org.probatron.jaxen.expr.iter.IterablePrecedingAxis;
import org.probatron.jaxen.expr.iter.IterablePrecedingSiblingAxis;
import org.probatron.jaxen.expr.iter.IterableSelfAxis;
import org.probatron.jaxen.saxpath.Axis;
import org.probatron.jaxen.saxpath.Operator;


public class ShailXPathFactory implements XPathFactory
{
    public XPathExpr createXPath( Expr rootExpr ) throws JaxenException
    {
        return new XPathExprImpl( rootExpr );
    }

    public PathExpr createPathExpr( FilterExpr filterExpr,
                                    LocationPath locationPath ) throws JaxenException
    {
        return new PathExprImpl( filterExpr,
                                    locationPath );
    }

    public LocationPath createRelativeLocationPath() throws JaxenException
    {
        return new RelativeLocationPathImpl();
    }

    public LocationPath createAbsoluteLocationPath() throws JaxenException
    {
        return new AbsoluteLocationPathImpl();
    }

    public BinaryExpr createOrExpr( Expr lhs,
                                    Expr rhs ) throws JaxenException
    {
        return new OrExprImpl( lhs,
                                  rhs );
    }

    public BinaryExpr createAndExpr( Expr lhs,
                                     Expr rhs ) throws JaxenException
    {
        return new AndExprImpl( lhs,
                                   rhs );
    }

    public BinaryExpr createEqualityExpr( Expr lhs,
                                          Expr rhs,
                                          int equalityOperator ) throws JaxenException
    {
        switch( equalityOperator )
        {
            case Operator.EQUALS:
                {
                    return new EqualsExprImpl( lhs,
                                                  rhs );
                }
            case Operator.NOT_EQUALS:
                {
                    return new NotEqualsExprImpl( lhs,
                                                     rhs );
                }
        }
        throw new JaxenException( "Unhandled operator in createEqualityExpr(): " + equalityOperator );
    }

    public BinaryExpr createRelationalExpr( Expr lhs,
                                            Expr rhs,
                                            int relationalOperator ) throws JaxenException
    {
        switch( relationalOperator )
        {
            case Operator.LESS_THAN:
                {
                    return new LessThanExprImpl( lhs,
                                                    rhs );
                }
            case Operator.GREATER_THAN:
                {
                    return new GreaterThanExprImpl( lhs,
                                                       rhs );
                }
            case Operator.LESS_THAN_EQUALS:
                {
                    return new LessThanEqualExprImpl( lhs,
                                                         rhs );
                }
            case Operator.GREATER_THAN_EQUALS:
                {
                    return new GreaterThanEqualExprImpl( lhs,
                                                            rhs );
                }
        }
        throw new JaxenException( "Unhandled operator in createRelationalExpr(): " + relationalOperator );
    }

    public BinaryExpr createAdditiveExpr( Expr lhs,
                                          Expr rhs,
                                          int additiveOperator ) throws JaxenException
    {
        switch( additiveOperator )
        {
            case Operator.ADD:
                {
                    return new PlusExprImpl( lhs,
                                                rhs );
                }
            case Operator.SUBTRACT:
                {
                    return new MinusExprImpl( lhs,
                                                 rhs );
                }
        }
        throw new JaxenException( "Unhandled operator in createAdditiveExpr(): " + additiveOperator );
    }

    public BinaryExpr createMultiplicativeExpr( Expr lhs,
                                                Expr rhs,
                                                int multiplicativeOperator ) throws JaxenException
    {
        switch( multiplicativeOperator )
        {
            case Operator.MULTIPLY:
                {
                    return new MultiplyExprImpl( lhs,
                                                    rhs );
                }
            case Operator.DIV:
                {
                    return new DivExprImpl( lhs,
                                               rhs );
                }
            case Operator.MOD:
                {
                    return new ModExprImpl( lhs,
                                               rhs );
                }
        }
        throw new JaxenException( "Unhandled operator in createMultiplicativeExpr(): " + multiplicativeOperator );
    }

    public Expr createUnaryExpr( Expr expr,
                                 int unaryOperator ) throws JaxenException
    {
        switch( unaryOperator )
        {
            case Operator.NEGATIVE:
                {
                    return new UnaryExprImpl( expr );
                }
        }
        return expr;
    }

    public UnionExpr createUnionExpr( Expr lhs,
                                      Expr rhs ) throws JaxenException
    {
        return new UnionExprImpl( lhs,
                                     rhs );
    }

    public FilterExpr createFilterExpr( Expr expr ) throws JaxenException
    {
        return new FilterExprImpl( expr, createPredicateSet() );
    }

    public FunctionCallExpr createFunctionCallExpr( String prefix,
                                                    String functionName ) throws JaxenException
    {
        return new FunctionCallExprImpl( prefix,
                                            functionName );
    }

    public NumberExpr createNumberExpr( int number ) throws JaxenException
    {
        return new NumberExprImpl( new Double( number ) );
    }

    public NumberExpr createNumberExpr( double number ) throws JaxenException
    {
        return new NumberExprImpl( new Double( number ) );
    }

    public LiteralExpr createLiteralExpr( String literal ) throws JaxenException
    {
        return new LiteralExprImpl( literal );
    }

    public VariableReferenceExpr createVariableReferenceExpr( String prefix,
                                                              String variable ) throws JaxenException
    {
        return new VariableReferenceExprImpl( prefix,
                                                 variable );
    }

    public Step createNameStep( int axis,
                                String prefix,
                                String localName ) throws JaxenException
    {
        IterableAxis iter = getIterableAxis( axis );
        return new NameStepImpl( iter,
                                    prefix,
                                    localName,
                                    createPredicateSet() );
    }

    public Step createTextNodeStep( int axis ) throws JaxenException
    {
        IterableAxis iter = getIterableAxis( axis );
        return new TextNodeStepImpl( iter, createPredicateSet() );
    }

    public Step createCommentNodeStep( int axis ) throws JaxenException
    {
        IterableAxis iter = getIterableAxis( axis );
        return new CommentNodeStepImpl( iter, createPredicateSet() );
    }

    public Step createAllNodeStep( int axis ) throws JaxenException
    {
        IterableAxis iter = getIterableAxis( axis );
        return new AllNodeStepImpl( iter, createPredicateSet() );
    }

    public Step createProcessingInstructionNodeStep( int axis,
                                                     String piName ) throws JaxenException
    {
        IterableAxis iter = getIterableAxis( axis );
        return new ProcessingInstructionNodeStepImpl( iter,
                                                         piName,
                                                         createPredicateSet() );
    }

    public Predicate createPredicate( Expr predicateExpr ) throws JaxenException
    {
        return new PredicateImpl( predicateExpr );
    }

    protected IterableAxis getIterableAxis( int axis ) throws JaxenException
    {

        switch( axis )
        {
            case Axis.CHILD:
                 return new IterableChildAxis( axis );
            case Axis.DESCENDANT:
                 return  new IterableDescendantAxis( axis );
            case Axis.PARENT:
                return new IterableParentAxis( axis );
            case Axis.FOLLOWING_SIBLING:
                return  new IterableFollowingSiblingAxis( axis );
            case Axis.PRECEDING_SIBLING:
                return new IterablePrecedingSiblingAxis( axis );
            case Axis.FOLLOWING:
                return new IterableFollowingAxis( axis );
            case Axis.PRECEDING:
                return new IterablePrecedingAxis( axis );
            case Axis.ATTRIBUTE:
                return new IterableAttributeAxis( axis );
            case Axis.NAMESPACE:
                return new IterableNamespaceAxis( axis );
            case Axis.SELF:
                return new IterableSelfAxis( axis );
            case Axis.DESCENDANT_OR_SELF:
                return new IterableDescendantOrSelfAxis( axis );
            case Axis.ANCESTOR_OR_SELF:
                return new IterableAncestorOrSelfAxis( axis );
            case Axis.ANCESTOR:
                return new IterableAncestorAxis( axis );
            default:
                throw new JaxenException("Unrecognized axis code: " + axis);
        }

    }

    public PredicateSet createPredicateSet() throws JaxenException
    {
        return new PredicateSetExt();
    }
    

}
