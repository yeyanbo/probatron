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
 * Created on 18 Dec 2007
 */
package com.griffinbrown.schematron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Abstract representation of a Schematron pattern.
 * 
 * @author andrews
 *
 * @version $Id: Pattern.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public abstract class Pattern
{
    private String id;
    private String title;
    private List variables;
    private List rules;
    private SchematronSchema schema;
    private HashMap matchedContexts;

    private static Logger logger = Logger.getLogger( Pattern.class );


    //TODO: resolve abstract patterns
    Pattern( int node, SchematronSchema schema ) throws JaxenException
    {
        this.schema = schema;
        ShailXPath xpath = new ShailXPath( "@id" );
        ShailList idNode = ( ShailList )xpath.evaluate( node );
        if( ! idNode.isEmpty()) 
            id = StringFunction.evaluate( idNode, ShailNavigator.getInstance() );

        xpath = new ShailXPath( "*[local-name()='title' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        ShailList titleNode = ( ShailList )xpath.evaluate( node );
        if( ! titleNode.isEmpty() )
            title = StringFunction.evaluate( titleNode, ShailNavigator.getInstance() );

        rules = createRules( node );
        variables = createVariables( node );
        matchedContexts = new HashMap();
    }

    /**
     * Accesses the schema to which this pattern belongs. 
     * @return the schema this pattern belongs to
     */
    public SchematronSchema getSchema()
    {
        return this.schema;
    }


    private List createVariables( int pattern ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "*[local-name()='let' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        ShailList variables = ( ShailList )xpath.evaluate( pattern );

        List results = new ArrayList( variables.size() );
        ShailIterator iter = variables.shailListIterator();

        ShailXPath name = new ShailXPath( "string(@name)" );
        ShailXPath value = new ShailXPath( "string(@value)" );

        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            results.add( new GlobalXPathVariable( ( String )name.evaluate( node ),
                    ( String )value.evaluate( node ) ) );
        }
        return results;
    }


    private List createRules( int pattern ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "*[local-name()='rule' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        ShailList rules = ( ShailList )xpath.evaluate( pattern );

        List results = new ArrayList( rules.size() );
        ShailIterator iter = rules.shailListIterator();

        ShailXPath isAXPath = new ShailXPath( "@abstract[.='true']" );

        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            if( ((ShailList)isAXPath.evaluate( node )).isEmpty() )
                results.add( new Rule( node, this ) );
            else
                schema.addAbstractRule( new AbstractRule( node, this ) );
        }
        return results;
    }

    /**
     * Accesses the ID of this pattern.
     * @return the id of this pattern, or <code>null</code> if it has none
     */
    public String getId()
    {
        return id;
    }

    /**
     * Accesses the title of this pattern.
     * @return the title of this pattern, or <code>null</code> if it has none
     */
    public String getTitle()
    {
        return title;
    }


    public String toString()
    {
        return "<" + getClass().getName() + " id=" + getId() + " title=" + getTitle()
                + " rules=" + getRules() + " variables=" + getVariables() + ">";
    }


    /**
     * Accesses variables in scope for this pattern.
     * @return List of variables
     */
    public List getVariables()
    {
        return variables;
    }


    /**
     * Accesses the rules active for this pattern.
     * Note that the set of active rules is not known until abstract rules have been resolved. 
     * @return the rules
     */
    public List getRules()
    {
        return rules;
    }


    /**
     * Requests a (post-validation) XML report for this pattern.
     * 
     * Though declared as <tt>pattern</tt> in the Schematron namespace, active
     * patterns are reported in SVRL as <tt>active-pattern</tt> in the SVRL namespace.
     * @return
     */
    public String report()
    {
        StringBuffer s = new StringBuffer( "<active-pattern" );
        if( getId() != null )
            s.append( " id='" + getId() + "'" );

        if( getTitle() != null )
            s.append( " name='" + Utils.quoteAttr( Utils.escape( getTitle() ) ) + "'" );

        s.append( "/>" );

        //TODO: SVRL also includes @name and @role - where these from??

        return s.toString();
    }


    void resolveAbstractRules() throws XMLToolException
    {
        Iterator iter = getRules().iterator();
        while( iter.hasNext() )
        {
            Rule rule = ( Rule )iter.next();
            if( logger.isDebugEnabled() )
                logger.debug( "resolving abstract rules for concrete rule " + rule );
            try
            {
                rule.resolveAbstractRules();
            }
            catch( Exception e )
            {
                throw new XMLToolException( " for rule [id='"
                        + ( rule.getId() != null ? rule.getId() : "" ) + "'] at "
                        + rule.getXPathLocation() + ": " + e.getMessage() );
            }

        }
    }

    /**
     * Cues evaluation of this pattern.
     * 
     * @throws XMLToolException
     */
    public void evaluate() throws XMLToolException
    {
        List rules = getRules();
        Iterator iter = rules.iterator();
        boolean passed = true;
        while( iter.hasNext() )
        {
            Rule rule = ( Rule )iter.next();
            if( logger.isDebugEnabled() )
                logger.debug( "processing rule " + rule );
            rule.evaluate();
        }
    }


    /**
     * Whether the given nodeset has already been matched by this pattern.
     * N.B. it is not clear from the standard (because of unclear definitions of "information item",
     * "matches" and "context") whether this means that a node in the nodeset has already been 
     * matched, or whether the nodeset matched is identical to that matched by a previous rule. 
     * @param nodeset context matched by the current rule
     * @param expr context expression specified by the current rule 
     * @return
     */
    boolean hasAlreadyMatched( List nodeset, String expr )
    {
        //TODO: early bail-outs: test on nodeset size and expression used first!

        HashMap map = matchedContexts;

        if( map.containsKey( nodeset ) )
            return true;

        map.put( nodeset, expr );
        if( logger.isDebugEnabled() )
            logger.debug( map );
        return false;
    }


    abstract void resolveAbstractPatterns() throws XMLToolException;


    abstract List getParams();

}
