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
 * Created on 8 Jan 2008
 */
package com.griffinbrown.schematron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.QueryEvaluator;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.ShailXPathEvaluator;
import org.probatron.XPathLocatorImpl;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.BooleanFunction;
import org.probatron.jaxen.function.StringFunction;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.XPathLocator;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Represents a Schematron <code>assert</code> assertion.
 * @author andrews
 *
 * @version $Id: Assert.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class Assert extends SchematronQuery
{
    private List context; //rule evaluation context
    private String id;
    private String expression;
    private static Logger logger = Logger.getLogger( Assert.class );
    private int node;
    private Rule rule;
    private int contextNode;
    private QueryEvaluator evaluator;


    Assert( int node, Rule rule ) throws JaxenException
    {
        this.rule = rule;
        this.node = node;

        ShailXPath xpath = new ShailXPath( "@id" );
        ShailList idNode = ( ShailList )xpath.evaluate( node );
        if( ! idNode.isEmpty() )
            id = StringFunction.evaluate( idNode, ShailNavigator.getInstance() );

        expression = setExpression( node );

        if( logger.isDebugEnabled() )
            logger.debug( this );
    }


    String setExpression( int node ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "@test" );
        ShailList test = ( ShailList )xpath.evaluate( node );
        if( ! test.isEmpty() )
            return StringFunction.evaluate( test, ShailNavigator.getInstance() );
        return null;
    }


    public String getExpression()
    {
        return expression;
    }


    public String getId()
    {
        return id;
    }


    public String toString()
    {
        return "<" + getClass().getName() + " id=" + getId() + " expr=" + getExpression()
                + " context=" + context + ">";
    }


    public Rule getRule()
    {
        return this.rule;
    }


    private void setVariableContext( int contextNode ) throws XMLToolException
    {
        //set local variable context
        Iterator variablesIterator = rule.getVariables().iterator();
        while( variablesIterator.hasNext() )
        {
            //compile and evaluate them
            LocalXPathVariable var = ( LocalXPathVariable )variablesIterator.next();
            var.setContext( contextNode );
            //                var.compile( evaluator );

            if( logger.isDebugEnabled() )
                logger.debug( "evaluating local variable " + var + " with context="
                        + contextNode );

            var.evaluate( contextNode, evaluator );
        }
    }


    private void setParams( int contextNode ) throws XMLToolException
    {
        //include any params for abstract patterns
        //TODO: ensure these evaluate as expected!
        List params = rule.getPattern().getParams();
        Iterator paramIterator = params.iterator();
        while( paramIterator.hasNext() )
        {
            Param param = ( Param )paramIterator.next();
            param.evaluate( contextNode, evaluator );
        }
    }


    private Object evaluate( int contextNode ) throws XMLToolException
    {
        try
        {
            return evaluator.evaluate( contextNode, getExpression() );
        }
        catch( XMLToolException e )
        {
            throw new XMLToolException( "error evaluating XPath expression [ID='" + getId()
                    + "'] at " + new XPathLocatorImpl( node, getRule().getPattern().getSchema().getQueryHandler().getEvaluator() ) + ": '" + getExpression() + "' "
                    + e.getMessage() );
        }
    }


    /**
     * Evaluate this assertion.
     * 
     * In this implementation, a non-empty list is returned if any assertions fail.
     * This list will contain an object encapsulating this information for each node failing the assertion. 
     * 
     *  @return a list of failed assertions
     */
    public Object evaluate() throws XMLToolException
    {
        evaluator = rule.getPattern().getSchema().getQueryHandler().getEvaluator();
        List results = new ArrayList();
        ShailIterator contextIterator = ( ( ShailList )context ).shailListIterator();
        while( contextIterator.hasNext() )
        {
            int contextNode = contextIterator.nextNode();
            this.contextNode = contextNode;

            setVariableContext( contextNode );
            setParams( contextNode );

            //evaluate expression
            if( logger.isDebugEnabled() )
                logger.debug( "evaluating " + this + " with context=" + contextNode );

            Object result = evaluate( contextNode );

            if( logger.isDebugEnabled() )
                logger.debug( "evaluated to " + result );

            //assemble failed assertions
            boolean passed = BooleanFunction.evaluate( result, ShailNavigator.getInstance() )
                    .booleanValue();
            if( ! passed )
            {
                try
                {
                    results.add( new SchematronQueryResult( contextNode, this, evaluator ) );
                }
                catch( JaxenException e )
                {
                    throw new XMLToolException( e );
                }
            }

            ( ( ShailXPathEvaluator )evaluator ).clearLocalVariables();

        }
        return results;
    }


    /**
     * Sets the rule this assertion belongs to.
     * Used when resolving abstract rules, since their "owner" rule changes to a concrete one.
     * 
     * @param rule the rule to set
     */
    void setRule( Rule rule )
    {
        this.rule = rule;
    }


    /**
     * @see com.griffinbrown.schematron.SchematronQuery#setEvaluationContext(java.util.List)
     */
    public void setEvaluationContext( List context )
    {
        this.context = context;
    }


    int getMessageNode()
    {
        return this.node;
    }


    String report( LocatorImpl locator, XPathLocator xpathLocator, String message )
    {
        /*failed-assert = element failed-assert {
        attlist.assert-and-report,
        diagnostic-reference*,
        human-text}
        */

        StringBuffer s = new StringBuffer( "<failed-assert" );

        if( getId() != null )
            s.append( " id='" + getId() + "'" );

        if( xpathLocator != null )
            s.append( " location='" + Utils.quoteAttr( Utils.escape( xpathLocator.toString() ) )
                    + "'" );

        s.append( " test='" + Utils.quoteAttr( Utils.escape( getExpression() ) ) + "'>" );

        s.append( "<probe:line>" + locator.getLineNumber() + "</probe:line>" );
        s.append( "<probe:column>" + locator.getColumnNumber() + "</probe:column>" );

        //TODO: diagnostic-reference*

        s.append( "<text>" + message + "</text>" );

        s.append( "</failed-assert>\n" );

        return s.toString();
    }
}
