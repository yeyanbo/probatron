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

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.XPathLocatorImpl;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * Represents an instance of an abstract rule in a Schematron schema.
 * 
 * @author andrews
 *
 * @version $Id: AbstractRule.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class AbstractRule extends Rule
{
    private int node;
    private Pattern pattern;
    private static Logger logger = Logger.getLogger( AbstractRule.class );

    /**
     * Default constructor.
     * @param node
     * @param pattern
     * @throws JaxenException
     */
    AbstractRule( int node, Pattern pattern ) throws JaxenException
    {
        super( node, pattern );
        this.node = node;
        this.pattern = pattern;
    }


    void resolveAbstractRules( Rule concreteRule ) throws XMLToolException, JaxenException
    {
        concreteRule.addAssertions( getAssertions() );
        concreteRule.addVariables( getVariables() );

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
                //abstract rules mustn't extend themselves (not mentioned in Schematron spec)
                if( idRef.equals( getId() ) )
                {
                    throw new XMLToolException(
                            "abstract rules must not extend themselves: rule id='"
                                    + idRef
                                    + "' referenced at "
                                    + new XPathLocatorImpl( n, getPattern().getSchema()
                                            .getQueryHandler().getEvaluator() ) );
                }

                //no repeated extend instructions allowed (also not in spec)
                if( concreteRule.getExtendedRuleMap().containsKey( idRef ) )
                {
                    throw new XMLToolException(
                            "found multiple extends instructions for abstract rule [id='"
                                    + idRef
                                    + "'] at "
                                    + new XPathLocatorImpl( n, getPattern().getSchema()
                                            .getQueryHandler().getEvaluator() ) );
                }

                concreteRule.getExtendedRuleMap().put( idRef, abstractRule );
                //                    logger.debug("added extended rule to map:"+concreteRule.getExtendedRuleMap());
                abstractRule.resolveAbstractRules( concreteRule );

            }
        }
    }

}
