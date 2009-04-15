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

package org.probatron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.xmltool.XPathLocator;

/**
 * @version $Revision: 1.1 $
 * 
 * @version $Id: XPathLocatorImpl.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 * 
 */
public class XPathLocatorImpl implements XPathLocator
{
    private ArrayList list; //storage for NameTests
    private int node;
    private QueryEvaluator evaluator; //handles user-specified namespaces
    
    private static Logger logger = Logger.getLogger( XPathLocatorImpl.class );


    /**
     * Constructs an XPathLocator for the node passed in.
     * @param node the node whose locator is requested
     * @param factory the NodeFactory which created the node passed in
     */
    public XPathLocatorImpl( int node, QueryEvaluator evaluator )
    {
        this.node = node;
        this.evaluator = evaluator;
        if( logger.isDebugEnabled() )
            logger.debug( "new XPathLocator for node " + node );
    }

    /**
     * Use this constructor when a QueryEvaluator is not available to 
     * provide information about registered namespaces.
     * @param node
     */
    public XPathLocatorImpl( int node )
    {
        this.node = node;
        evaluate();
    }


    /**
     * @see com.griffinbrown.xmltool.XPathLocator#isAbsolute()
     */
    public boolean isAbsolute()
    {
        return true;
    }


    /**
     * Converts a List of <code>NodeTest</code>s into a string representation of
     * an XPathLocator.
     * @param list NodeTests making up this XPath locator
     * @return string of this XPath locator
     */
    public static String toString( List list )
    {
        StringBuffer s = new StringBuffer();
        Iterator iter = list.iterator();

        while( iter.hasNext() )
        {
            NodeTest nt = ( NodeTest )iter.next();
            s.insert( 0, '/' + nt.getValue() );
        }
        return s.toString();
    }


    public String toString()
    {
        if( list == null )
            evaluate();
        return toString( this.list );
    }


    private void evaluate()
    {
        list = new ArrayList();
        //1. add NameTest for current node to list
        NodeTest nt = NodeTest.newInstance( node, evaluator );
        nt.setPredicate( getPredicate( node ) );
        list.add( nt );
        //2. recurse with parent of node.
        getAncestors( node );

    }


    /**
     * Traverses the ancestor axis until the document root is reached, 
     * storing the names of any ancestor elements.
     * 
     * @param node the node from which to begin the walk
     */
    private void getAncestors( int node )
    {
        int[] ancestors = ModelRegistry.getModelForNode( node ).getAncestors( node, false );

        int ancestor;
        for( int i = 0; i < ancestors.length; i++ )
        {
            ancestor = ancestors[ i ];
            if( ShailNavigator.getInstance().isDocument( ancestor ) )
                break;

            NodeTest nt = NodeTest.newInstance( ancestor, evaluator );

            if( ! ShailNavigator.getInstance().isAttribute( ancestor ) )
                nt.setPredicate( getPredicate( ancestor ) );

            list.add( nt );
        }

    }   


    private int getPredicate( int node )
    {
        return ModelRegistry.getModelForNode( node ).getSimilarPrecedingSiblingCount( node );
    }
}
