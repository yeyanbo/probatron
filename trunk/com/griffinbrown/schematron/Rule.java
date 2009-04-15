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
 * Created on 24 Jan 2008
 */
package com.griffinbrown.schematron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.QueryEvaluator;
import org.probatron.QueryHandler;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.XPathLocatorImpl;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Represents a Schematron rule.
 * @author andrews
 *
 * @version $Id: Rule.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class Rule
{
    private String id;
    private List variables = new ArrayList();
    private List assertions = new ArrayList();
    private HashMap extendedRuleMap = new HashMap();
    private Pattern pattern;
    private String contextExpr;
    private int node;
    private boolean fired;
    private List failedAssertions;

    private static Logger logger = Logger.getLogger( Rule.class );


    Rule( int node, Pattern pattern ) throws JaxenException
    {
        this.pattern = pattern;
        this.node = node;

        ShailList contextNode = ( ShailList )new ShailXPath( "@context" ).evaluate( node );
        if( ! contextNode.isEmpty() )
            contextExpr = StringFunction.evaluate( contextNode, ShailNavigator.getInstance() );

        ShailXPath xpath = new ShailXPath( "@id" );
        ShailList idNode = ( ShailList )xpath.evaluate( node );
        if( ! idNode.isEmpty() )
        {
            id = StringFunction.evaluate( idNode, ShailNavigator.getInstance() );
        }

        //assert
        xpath = new ShailXPath( "*[local-name()='assert' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        ShailList nodes = ( ShailList )xpath.evaluate( node );
        ShailIterator iter = nodes.shailListIterator();

        while( iter.hasNext() )
        {
            int n = iter.nextNode();
            assertions.add( new Assert( n, this ) );
        }

        //report
        xpath = new ShailXPath( "*[local-name()='report' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        nodes = ( ShailList )xpath.evaluate( node );
        iter = nodes.shailListIterator();

        while( iter.hasNext() )
        {
            int n = iter.nextNode();
            assertions.add( new Report( n, this ) );
        }

        //let
        xpath = new ShailXPath( "*[local-name()='let' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        nodes = ( ShailList )xpath.evaluate( node );
        iter = nodes.shailListIterator();

        ShailXPath nameXPath = new ShailXPath( "string(@name)" );
        ShailXPath valueXPath = new ShailXPath( "string(@value)" );

        while( iter.hasNext() )
        {
            int n = iter.nextNode();
            LocalXPathVariable v = new LocalXPathVariable( ( String )nameXPath.evaluate( n ),
                    ( String )valueXPath.evaluate( n ) );
            variables.add( v );
        }

        failedAssertions = new ArrayList();
    }


    /**
     * Accesses the context for this rule.
     * See Annex C: "The rule context is interpreted according to the Production 1 of XSLT. 
     * The rule context may be the root node, elements, attributes, comments and processing 
     * instructions."
     *  
     * @return
     */
    private List getEvaluationContext() throws XMLToolException
    {
        String absExpr = toAbsolutePath( contextExpr );

        QueryHandler queryHandler = pattern.getSchema().getQueryHandler();
        QueryEvaluator evaluator = queryHandler.getEvaluator();

        //evaluate context expression
        Object result = null;

        try
        {
            result = evaluator.evaluate( ( ( Model )evaluator.getDocument() ).getRoot(),
                    absExpr );
        }
        catch( XMLToolException e )
        {
            throw new XMLToolException( "error evaluating context expression at "
                    + new XPathLocatorImpl( node, getPattern().getSchema().getQueryHandler().getEvaluator() )
                    + "/@context='" + contextExpr + "': " + e.getMessage() );
        }

        if( ! ( result instanceof List ) )
            throw new XMLToolException(
                    "rule context expression must evaluate to a node-set: got " + result );

        //TODO: "...rule context may be elements, attributes, comments and processing instructions..."
        //what happens if the list contains e.g. text nodes? warn about this?

        return ( List )result;
    }


    /**
     * For the purposes of XMLProbe, make the evaluation context explicit by
     * prepending the descendant-or-self axis operator (//).
     * @param expr
     * @return
     */
    private String toAbsolutePath( String expr )
    {
        //TODO: beware of expressions beginning with '(' -> //(foo) or union expressions!!

        String trimmed = expr.trim();
        if( trimmed.startsWith( "/" ) )
            return expr;
        return "//" + expr;
    }

    /**
     * Accesses the ID of this rule.
     * @return the ID of the rule, or <code>null</code> if it has none
     */
    public String getId()
    {
        return id;
    }


    /**
     * Accesses the (locally-scoped) variables for this rule.
     * 
     * 
     * @return
     */
    public List getVariables()
    {
        //TODO: reset the context for variables each time a call to this method is made, for convenience(?)

        return variables;
    }


    /**
     * Adds variables declared in an abstract rule which this concrete rule extends.
     * "It is an error for a variable to be multiply defined in the
     * current schema, phase, pattern and rule."
     * Note that reporting of duplicate local variables is done at evaluation-time.
     * 
     * @param variables
     */
    void addVariables( List variables )
    {
        this.variables.addAll( variables );
    }


    /**
     * Accesses the assertions belonging to this rule.
     * Note that in this implementation abstract rules are not resolved until assertions are tested
     * at runtime, therefore the return value of this method cannot be guaranteed accurate until
     * such resolution has taken place.
     *  
     * @return assertions declared for this rule
     */
    public List getAssertions()
    {
        return assertions;
    }


    /**
     * Add a list of assertions belonging to an <strong>abstract</strong> rule to this rule.
     * @param assertions
     */
    void addAssertions( List assertions )
    {
        Iterator it = assertions.iterator();
        while( it.hasNext() )
        {
            Assert assertion = ( Assert )it.next();
            assertion.setRule( this );
            this.assertions.add( assertion );
        }
    }

    /**
     * Requests a (post-validation) XML report for this rule.
     * @return the string of the XML report
     */
    public String report()
    {
        //TODO: in the current XSLT SVRL implementation, each node triggering a rule generates a fired-rule element 

        StringBuffer s = new StringBuffer( "<fired-rule" );

        if( contextExpr != null )
            s.append( " context='" + Utils.quoteAttr( Utils.escape( contextExpr ) ) + "'" );

        if( getId() != null )
            s.append( " id='" + getId() + "'" );

        s.append( "/>" );

        return s.toString();
    }

    /**
     * The rule as normalized XML.
     * @return the rule as normalized XML
     */
    //FIXME
    public String asNormalizedXml()
    {
        StringBuffer s = new StringBuffer( "<rule" );

        Iterator iter = getVariables().iterator();
        while( iter.hasNext() )
        {
            GlobalXPathVariable var = ( GlobalXPathVariable )iter.next();
            s.append( "\n" + var.asNormalizedXml() );
        }

        iter = getAssertions().iterator();
        while( iter.hasNext() )
        {
            SchematronQuery q = ( SchematronQuery )iter.next();
            //            s.append( "\n" + q.asNormalizedXml() );
        }

        s.append( "\n</rule>" );
        return s.toString();
    }


    /**
     * Accesses the Pattern to which this Rule belongs.
     * @return the Pattern to which this rule belongs
     */
    public Pattern getPattern()
    {
        return this.pattern;
    }


    void resolveAbstractRules() throws XMLToolException, JaxenException
    {
        //extends
        ShailXPath xpath = new ShailXPath( "*[local-name()='extends' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']/@rule" );
        ShailList extended = ( ShailList )xpath.evaluate( node );

        ShailIterator iter = extended.shailListIterator();
        while( iter.hasNext() )
        {
            int n = iter.nextNode();
            String idRef = StringFunction.evaluate( n, ShailNavigator.getInstance() );
            AbstractRule abstractRule = pattern.getSchema().getAbstractRuleById( idRef );

            if( abstractRule != null )
            {
                //no repeated extend instructions allowed (also not in spec)
                if( extendedRuleMap.containsKey( idRef ) )
                {
                    throw new XMLToolException(
                            "found multiple extends instructions for abstract rule [id='"
                                    + idRef
                                    + "'] at "
                                    + new XPathLocatorImpl( n, getPattern().getSchema()
                                            .getQueryHandler().getEvaluator() ) );
                }

                extendedRuleMap.put( idRef, abstractRule );
                abstractRule.resolveAbstractRules( this );
            }
            //TODO: report unresolvable abstract rule? (not mentioned in spec)

        }
    }


    /**
     * Access the context expression.
     * 
     * @return the context expression
     */
    public String getContextExpression()
    {
        return contextExpr;
    }


    public String toString()
    {
        return "<" + getClass().getName() + " id=" + getId() + ">";
    }


    String getXPathLocation()
    {
        return new XPathLocatorImpl( node, getPattern().getSchema().getQueryHandler().getEvaluator() ).toString();
    }


    /**
     * @return the extendedRuleMap
     */
    HashMap getExtendedRuleMap()
    {
        return extendedRuleMap;
    }


    void setPattern( Pattern pattern )
    {
        this.pattern = pattern;
    }


    /**
     * Cues evaluation of this rule.
     * 
     * @return true if:
     * - an assertion passed OR 
     * - no node matched the rule context OR
     * - the pattern has already matched this context
     * and false if the assertion failed
     * @throws XMLToolException
     */
    public boolean evaluate() throws XMLToolException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "evaluating rule " + this );

        List context = getEvaluationContext();
        if( context.isEmpty() )
            return true; //shortcut

        //TODO: if-then-else. See s6.5: "A rule element acts as an if-then-else statement within each pattern." ???
        //and Note 10, s6.3: "if the context of an instance matches the rule, and that context has not 
        //been matched by a previous rule in the same pattern, then the particular assertion evaluates 
        //to true when evaluated with the particular context and instance." ???

        if( pattern.hasAlreadyMatched( context, contextExpr ) )
        {
            this.fired = false;
            return true;
        }

        //this rule fired
        this.fired = true;

        Iterator iter = assertions.iterator();

        while( iter.hasNext() )
        {
            SchematronQuery query = ( SchematronQuery )iter.next();
            query.setEvaluationContext( context );
            List failed = ( List )query.evaluate();
            failedAssertions.addAll( failed );
        }

        return true;
    }


    /**
     * Whether the rule fired during processing.
     * See s3.19:
     * "3.19 rule context
     * element or other information item used for assertion tests; a rule is said to fire 
     * when an information item matches the rule context"
     * @return whether the rule fired
     */
    boolean hasFired()
    {
        return fired;
    }


    /**
     * Accesses the failed assertions for this rule. 
     * 
     * @return a (possibly empty) list of failed assertions
     */
    public List getFailedAssertions()
    {
        return failedAssertions;
    }


    void addFailedAssertion( SchematronQuery assertion )
    {
        failedAssertions.add( assertion );
    }
}
