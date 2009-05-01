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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.Variable;
import org.probatron.XPathLocatorImpl;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * Represents an instance of a abstract pattern in a Schematron schema.
 * Objects of this class model, in XPath terms, nodes in the set <tt>pattern/@is-a</tt>
 * where the value of this node matches a value in the node-set <tt>pattern[ @abstract='true' ]/@id</tt>.
 * @author andrews
 *
 * @version $Id: AbstractPatternInstance.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class AbstractPatternInstance extends AbstractPattern
{
    private List params;
    private String abstractPatternRef;
    private SchematronSchema schema;
    private int node;
    private List variables = new ArrayList();
    private List rules = new ArrayList();
    private static Logger logger = Logger.getLogger( AbstractPattern.class );


    AbstractPatternInstance( int node, SchematronSchema schema ) throws JaxenException
    {
        super( node, schema );
        params = new ArrayList();
        this.schema = schema;
        this.node = node;

        ShailXPath xpath = new ShailXPath( "*[local-name()='param' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        ShailXPath nameXPath = new ShailXPath( "string(@name)" );
        ShailXPath valueXPath = new ShailXPath( "string(@value)" );
        ShailXPath isAXPath = new ShailXPath( "@is-a" );
        ShailList nodes = ( ShailList )xpath.evaluate( node );

        ShailIterator iter = nodes.shailListIterator();
        while( iter.hasNext() )
        {
            int param = iter.nextNode();
            params.add( new Param( ( String )nameXPath.evaluate( param ), ( String )valueXPath
                    .evaluate( param ) ) );
        }

        ShailList patRef = (ShailList)isAXPath.evaluate( node );
        if( ! patRef.isEmpty() )
            abstractPatternRef = StringFunction.evaluate( patRef, ShailNavigator.getInstance() );

    }


    /**
     * "Resolve all abstract patterns by replacing parameter references with actual parameter values in all enclosed
     *  attributes that contain queries." (s6.2)
     *  
     *  @throws XMLToolException if the referenced abstract pattern (of which this purports to be 
     *  an instance) cannot be found
     */
    void resolveAbstractPatterns() throws XMLToolException
    {
        if( abstractPatternRef != null )
        {
            if( logger.isDebugEnabled() )
                logger.debug( "resolving abstract pattern @is-a=" + abstractPatternRef );

            AbstractPattern pattern = ( AbstractPattern )schema
                    .getAbstractPatternById( abstractPatternRef );

            if( pattern != null )
            {
                //compile params as if they were variables
                Iterator it = params.iterator();
                while( it.hasNext() )
                {
                    Param param = ( Param )it.next();
                    param.compile( schema.getQueryHandler().getEvaluator() );
                }
                //acquire variables and assertions from AbstractPattern
                addRules( pattern.getRules() );
                variables.addAll( pattern.getVariables() );
                //add any variables to the global variables for the schema
                schema.addGlobalVariables( variables );
            }
            else
                throw new XMLToolException( "abstract pattern is-a='" + abstractPatternRef
                        + "' referenced at " + new XPathLocatorImpl( node, schema.getQueryHandler().getEvaluator() ) + " not found" );
        }

        logger.debug( "abstract pattern resolved: " + this );

    }


    private void addRules( List rules )
    {
        Iterator it = rules.iterator();
        while( it.hasNext() )
        {
            Rule rule = ( Rule )it.next();
            rule.setPattern( this );
            this.rules.add( rule );
        }
    }


    /**
     * @see com.griffinbrown.schematron.Pattern#getRules()
     */
    public List getRules()
    {
        return rules;
    }


    /**
     * @see com.griffinbrown.schematron.Pattern#getVariables()
     */
    public List getVariables()
    {
        return variables;
    }


    public String toString()
    {
        StringBuffer s = new StringBuffer( "<" + getClass().getName() + " id=" + getId()
                + " title=" + getTitle() + " rules=[" );

        for( int i = 0; i < getRules().size(); i++ )
        {
            s.append( ( ( Rule )getRules().get( i ) ).getId() );
            if( i < getRules().size() - 1 )
                s.append( ", " );
        }
        s.append( "] variables=[" );

        for( int i = 0; i < getVariables().size(); i++ )
        {
            s.append( ( ( Variable )getVariables().get( i ) ).getName() );
            if( i < getVariables().size() - 1 )
                s.append( ", " );
        }
        s.append( "] params=[" );

        for( int i = 0; i < getParams().size(); i++ )
        {
            s.append( ( ( Param )getParams().get( i ) ).getName() );
            if( i < getParams().size() - 1 )
                s.append( ", " );
        }
        s.append( "]>" );

        return s.toString();
    }


    /**
     * @see com.griffinbrown.schematron.AbstractPattern#getParams()
     */
    List getParams()
    {
        return this.params;
    }

}
