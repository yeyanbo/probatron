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
 * Created on 09-Jun-2003
 * 
 * To change the template for this generated file go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package org.probatron.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.QueryEvaluator;
import org.probatron.QueryHandler;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.Function;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * This (built-in) XPath extension function tests whether the string value of a 
 * node in one nodeset is present in another nodeset.
 * 
 */

public class InNodeSetFunction implements Function
{
    private static Logger logger = Logger.getLogger( InNodeSetFunction.class );
    private HashMap lookUpCache;
    private QueryHandler handler;
    private static final InNodeSetFunction INSTANCE = new InNodeSetFunction();
    private Context context;


    private InNodeSetFunction()
    {
        this.lookUpCache = new HashMap();
    }


    public static InNodeSetFunction getInstance()
    {
        return INSTANCE;
    }


    public static InNodeSetFunction newInstance( QueryHandler handler )
    {
        INSTANCE.handler = handler;
        return INSTANCE;
    }


    /**
     * @see org.probatron.jaxen.Function#call(org.probatron.jaxen.Context, java.util.List)
     */
    public Object call( Context context, List args ) throws FunctionCallException
    {
        this.context = context;

        if( args.size() != 2 )
        {
            throw new FunctionCallException( "in-nodeset() takes 2 arguments" );
        }

        Object nodeToCheck = args.get( 0 );
        Object nodeset = args.get( 1 );
        Navigator nav = context.getNavigator();

        //		System.err.println(nodeToCheck + " " + nodeset);	//DEBUG

        if( nodeToCheck instanceof List && nodeset instanceof List )
        {
//            if( ( ( List )nodeToCheck ).isEmpty() )
//                throw new FunctionCallException(
//                        "first argument to in-nodeset() must not be an empty node-set" );
//            else if( ( ( List )nodeset ).isEmpty() )
//                throw new FunctionCallException(
//                        "second argument to in-nodeset() must not be an empty node-set" );

            if( logger.isDebugEnabled() )
                logger.debug( "in-nodeset(nodeset,nodeset) nodeToCheck=" + nodeToCheck
                        + " nodeset=" + nodeset );
            return evaluate( ( List )nodeToCheck, ( List )nodeset, nav );
        }
        else if( nodeToCheck instanceof String && nodeset instanceof List )
        {
//            if( ((List)nodeset).isEmpty() )
//                throw new FunctionCallException(
//                "second argument to in-nodeset() must not be an empty node-set" );                
            
            return evaluate( ( String )nodeToCheck, ( List )nodeset, nav );
        }
        else if( ( nodeToCheck instanceof String || nodeToCheck instanceof List )
                && nodeset instanceof String )
        {
//            if( nodeToCheck instanceof List && ( ( List )nodeToCheck ).isEmpty() )
//                throw new FunctionCallException(
//                        "first argument to in-nodeset() must not be an empty node-set" );            
            
            return evaluate( nodeToCheck, ( String )nodeset, nav );
        }
        //(list, string)?

        //report bad argument types here
        throw new FunctionCallException(
                "arguments to in-nodeset() must be either (string or nodeset, nodeset) or (string or nodeset, string)" );
    }


    /**
     * 
     * @param nodeToCheck list of nodes to check
     * @param nodeset constrained values to check against
     * @param nav navigator for this context
     * @return a list: empty if no matches, otherwise non-empty
     */
    protected Object evaluate( List nodeToCheck, List nodeset, Navigator nav )
    {
        ArrayList results = new ArrayList();

        if( nodeset.isEmpty() )
            return results;

        List sorted = getSortedLookUp( nodeset, nav );

        //the actual search
        ShailIterator iter = ( ( ShailList )nodeToCheck ).shailListIterator();
        String s;
        int idx;
        while( iter.hasNext() )
        {
            s = StringFunction.evaluate( iter.nextNode(), nav );

            if( logger.isDebugEnabled() )
                logger.debug( ">>>looking for:" + s );

            idx = Collections.binarySearch( sorted, s );
            if( idx > - 1 )
            {
                if( logger.isDebugEnabled() )
                    logger.debug( "***FOUND!***" );
                results.add( sorted.get( idx ) );
                logger.debug( "returning " + results );
                return results;
            }
        }

        return results;
    }


    /**
     * 
     * @param nodeset
     * @param nav
     * @return the <strong>modified</strong> list, each member having been evaluated using string()
     */
    private static List toListOfStrings( List nodeset, Navigator nav )
    {
        ArrayList strings = new ArrayList( nodeset.size() );
        ShailIterator iter = ( ( ShailList )nodeset ).shailListIterator();
        String s;
        while( iter.hasNext() )
        {
            s = StringFunction.evaluate( iter.nextNode(), nav );
            strings.add( s );
        }
        return strings;
    }


    /**
     * @param nodeset
     * @return the nodeset passed, sorted
     */
    private List getSortedLookUp( List nodeset, Navigator nav )
    {
        //sort and cache
        if( ! lookUpCache.containsKey( nodeset ) )
        {
            List sorted = toListOfStrings( nodeset, nav );

            if( logger.isDebugEnabled() )
                logger.debug( "sorting..." + sorted );

            Collections.sort( sorted );

            if( logger.isDebugEnabled() )
                logger.debug( "caching..." + sorted );

            lookUpCache.put( nodeset, sorted );
            return sorted;
        }
        else
        {
            if( logger.isDebugEnabled() )
                logger.debug( "cached! " + nodeset.hashCode() );
        }
        return ( List )lookUpCache.get( nodeset );
    }


    /**
     * 
     * @param nodeToCheck string value of node to check
     * @param nodeset constrained values to check against
     * @param nav navigator for this context
     * @return a list: empty if no matches, otherwise non-empty
     */
    protected Object evaluate( String nodeToCheck, List nodeset, Navigator nav )
    {
        ArrayList results = new ArrayList();

        if( logger.isDebugEnabled() )
            logger.debug( "nodes in nodeset=" + nodeset.size() );

        String val;

        if( logger.isDebugEnabled() )
            logger.debug( "string to check for=" + nodeToCheck ); //DEBUG

        if( logger.isDebugEnabled() )
            logger.debug( "allowed string count=" + nodeset.size() ); //DEBUG

        ShailIterator iter = ( ( ShailList )nodeset ).shailListIterator();
        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            val = StringFunction.evaluate( node, nav );

            if( logger.isDebugEnabled() )
                logger.debug( "allowed string=" + val ); //DEBUG

            if( val.equals( nodeToCheck ) )
            {
                results.add( nodeToCheck );
                break;
            }
        }

        return results;
    }


    /**
     * Evaluates the node to check for against a nodeset compiled from an XPath expression.
     * Note that in this impl, the same evaluation context is used as that in force when this
     * function itself was called, so that variables, functions and namespaces are all in scope
     * when evaluating the XPath expression dynamically. 
     * @param nodeToCheck 
     * @param expr an XPath expression
     * @param nav navigator for this context
     * @return a list: empty if no matches, otherwise non-empty
     */
    protected Object evaluate( Object nodeToCheck, String expr, Navigator nav )
            throws FunctionCallException
    {
        ArrayList results = new ArrayList();
        if( logger.isDebugEnabled() )
            logger.debug( "XPath expr=" + expr ); //DEBUG

        Object xpath = null;
        QueryEvaluator evaluator = handler.getEvaluator();

        //construct XPath from expr arg
        try
        {
            xpath = evaluator.compile( expr );
        }
        catch( XMLToolException e )
        {
            throw new FunctionCallException( e );
        }
        
        Object o = null;

        try
        {
            o = evaluator.evaluate( context.getNodeSet().getInt( 0 ), xpath );  //TODO: check evaluation context is correct here!            
        }
        catch( XMLToolException e )
        {
            throw new FunctionCallException( e );
        }

        if( ! ( o instanceof List ) )
            return results;

        if( logger.isDebugEnabled() )
            logger.debug( "XPath evaluated to:" + o ); //DEBUG

        if( nodeToCheck instanceof String )
        {
            return evaluate( ( String )nodeToCheck, ( List )o, nav ); //(string, list)
        }

        if( nodeToCheck instanceof List )
            return evaluate( ( List )nodeToCheck, ( List )o, nav ); //(list, list)

        return results;
    }

}
