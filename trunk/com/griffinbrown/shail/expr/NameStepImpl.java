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
 * @version $Id: NameStepImpl.java,v 1.1 2009/01/08 14:41:27 GBDP\andrews Exp $
 * 
 * Copyright 2003 The Werken Company. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * Neither the name of the Jaxen Project nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
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
 */
package com.griffinbrown.shail.expr;

import org.apache.log4j.Logger;
import org.probatron.jaxen.Context;
import org.probatron.jaxen.ContextSupport;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.UnresolvableException;
import org.probatron.jaxen.expr.NameStep;
import org.probatron.jaxen.expr.PredicateSet;
import org.probatron.jaxen.expr.iter.IterableAxis;
import org.probatron.jaxen.saxpath.Axis;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

/**
 * Expression object that represents any flavor
 * of name-test steps within an XPath.
 * <p>
 * This includes simple steps, such as "foo",
 * non-default-axis steps, such as "following-sibling::foo"
 * or "@foo", and namespace-aware steps, such
 * as "foo:bar".
 *
 * @author bob mcwhirter (bob@werken.com)
 * @author Stephen Colebourne
 *
 */
public class NameStepImpl extends StepImpl implements NameStep
{

    /**
     *
     */
    private static final long serialVersionUID = 428414912247718390L;

    private static int clearingTime;

    /**
     * Our prefix, bound through the current Context.
     * The empty-string ("") if no prefix was specified.
     * Decidedly NOT-NULL, due to SAXPath constraints.
     * This is the 'foo' in 'foo:bar'.
     */
    private String prefix;

    /**
     * Our local-name.
     * This is the 'bar' in 'foo:bar'.
     */
    private String localName;

    /** Quick flag denoting if the local name was '*' */
    private boolean matchesAnyName;

    /** Quick flag denoting if we have a namespace prefix **/
    private boolean hasPrefix;

    private static Logger logger = Logger.getLogger( NameStepImpl.class );


    /**
     * Constructor.
     *
     * @param axis  the axis to work through
     * @param prefix  the name prefix
     * @param localName  the local name
     * @param predicateSet  the set of predicates
     */
    public NameStepImpl( IterableAxis axis, String prefix, String localName,
            PredicateSet predicateSet )
    {
        super( axis, predicateSet );

        this.prefix = prefix;
        this.localName = localName;
        this.matchesAnyName = "*".equals( localName );
        this.hasPrefix = ( this.prefix != null && this.prefix.length() > 0 );
    }


    /**
     * Gets the namespace prefix.
     *
     * @return the prefix
     */
    public String getPrefix()
    {
        return this.prefix;
    }


    /**
     * Gets the local name.
     *
     * @return the local name
     */
    public String getLocalName()
    {
        return this.localName;
    }


    /**
     * Does this step match any name? (i.e. Is it '*'?)
     *
     * @return true if it matches any name
     */
    public boolean isMatchesAnyName()
    {
        return matchesAnyName;
    }


    /**
     * Gets the step as a fully defined XPath.
     *
     * @return the full XPath for this step
     */
    public String getText()
    {
        StringBuffer buf = new StringBuffer( 64 );
        buf.append( getAxisName() ).append( "::" );
        if( getPrefix() != null && getPrefix().length() > 0 )
        {
            buf.append( getPrefix() ).append( ':' );
        }
        return buf.append( getLocalName() ).append( super.getText() ).toString();
    }


    /**
     * Evaluate the context node set to find the new node set.
     * <p>
     * This method overrides the version in <code>Step</code>Impl for performance.
     */
    public ShailList evaluate( Context context ) throws JaxenException
    {
        //        logger.debug( "***evaluate <"+localName+"> "+getClass().getName() );

        ShailList contextNodeSet = context.getNodeSet();
        int contextSize = contextNodeSet.size();
        // optimize for context size 0
        if( contextSize == 0 )
        {
            return ShailList.EMPTY_LIST;
        }
        ContextSupport support = context.getContextSupport();
        IterableAxis iterableAxis = getIterableAxis();
        boolean namedAccess = ( ! matchesAnyName && iterableAxis.supportsNamedAccess( support ) );

        // optimize for context size 1 (common case, avoids lots of object creation)
        //                if( contextSize == 1 )
        //                {
        //                    Object contextNode = contextNodeSet.get( 0 );
        //                    if( namedAccess )
        //                    {
        //                        // get the iterator over the nodes and check it
        //                        String uri = null;
        //                        if( hasPrefix )
        //                        {
        //                            uri = support.translateNamespacePrefixToUri( prefix );
        //                            if( uri == null )
        //                            {
        //                                throw new UnresolvableException(
        //                                        "XPath expression uses unbound namespace prefix " + prefix );
        //                            }
        //                        }
        //                        Iterator axisNodeIter = iterableAxis.namedAccessIterator( contextNode, support,
        //                                localName, prefix, uri );
        //                        if( axisNodeIter == null || axisNodeIter.hasNext() == false )
        //                        {
        //                            return ShailList.EMPTY_LIST;
        //                        }
        //        
        //                        // convert iterator to list for predicate test
        //                        // no need to filter as named access guarantees this
        //                        List newNodeSet = new ShailList();
        //                        while( axisNodeIter.hasNext() )
        //                        {
        //                            newNodeSet.add( axisNodeIter.next() );
        //                        }
        //        
        //                        // evaluate the predicates
        //                        return getPredicateSet().evaluatePredicates( newNodeSet, support );
        //        
        //                    }
        //                }
        //            else
        //            {
        //                // get the iterator over the nodes and check it
        //                Iterator axisNodeIter = iterableAxis.iterator( contextNode, support );
        //                if( axisNodeIter == null || axisNodeIter.hasNext() == false )
        //                {
        //                    return Collections.EMPTY_LIST;
        //                }
        //
        //                // run through iterator, filtering using matches()
        //                // adding to list for predicate test
        //                List newNodeSet = new ArrayList( contextSize );
        //                while( axisNodeIter.hasNext() )
        //                {
        //                    Object eachAxisNode = axisNodeIter.next();
        //                    if( matches( eachAxisNode, support ) )
        //                    {
        //                        System.err.println("adding "+eachAxisNode);
        //                        newNodeSet.add( eachAxisNode );
        //                    }
        //                }
        //
        //                // evaluate the predicates
        //                return getPredicateSet().evaluatePredicates( newNodeSet, support );
        //            }
        //        }

        // full case
        IdentitySet unique = new IdentitySet();
        //        List interimSet = new ShailList( contextSize );
        ShailList interimSet = new ShailList();
        //        List newNodeSet = new ShailList( contextSize );
        ShailList newNodeSet = new ShailList();

        if( namedAccess )
        {
//            logger.debug( "***named access***" );

            String uri = null;
            if( hasPrefix )
            {
                uri = support.translateNamespacePrefixToUri( prefix );
                if( uri == null )
                {
                    throw new UnresolvableException(
                            "XPath expression uses unbound namespace prefix " + prefix );
                }
            }
            for( int i = 0; i < contextSize; ++i )
            {
                int eachContextNode = contextNodeSet.getInt( i );

                ShailIterator axisNodeIter = (ShailIterator)iterableAxis.namedAccessIterator( eachContextNode,
                        support, localName, prefix, uri );
                if( axisNodeIter == null || axisNodeIter.hasNext() == false )
                {
                    continue;
                }

                // ensure only one of each node in the result
                while( axisNodeIter.hasNext() )
                {
                    int eachAxisNode = axisNodeIter.nextNode();
                    if( ! unique.contains( eachAxisNode ) )
                    {
                        unique.add( eachAxisNode );
                        interimSet.addInt( eachAxisNode );
                    }
                }

                // evaluate the predicates
                newNodeSet.addAll( getPredicateSet().evaluatePredicates( interimSet, support ) );
                interimSet.clear();
            }

        }
        else
        {
//          logger.debug( "***UNnamed access***" );
            
            for( int i = 0; i < contextSize; ++i )
            {
                int eachContextNode = contextNodeSet.getInt( i );

                ShailIterator axisNodeIter = (ShailIterator)axisIterator( eachContextNode, support );
                if( axisNodeIter == null || axisNodeIter.hasNext() == false )
                {
                    continue;
                }

                /* See jaxen-106. Might be able to optimize this by doing
                 * specific matching for individual axes. For instance on namespace axis
                 * we should only get namespace nodes and on attribute axes we only get
                 * attribute nodes. Self and parent axes have single members.
                 * Children, descendant, ancestor, and sibling axes never
                 * see any attributes or namespaces
                 */

                

                // ensure only unique matching nodes in the result
                while( axisNodeIter.hasNext() )
                {
                    int eachAxisNode = axisNodeIter.nextNode();

                    if( matches( eachAxisNode, support ) )
                    {
                        if( ! unique.contains( eachAxisNode ) )
                        {
                            unique.add( eachAxisNode );
                            interimSet.addInt( eachAxisNode );
                        }
                    }
                }

                // evaluate the predicates
                PredicateSet predicates = getPredicateSet();
                ShailList foo = predicates.evaluatePredicates( interimSet, support );
//                System.err.println("adding predicates: interimSet=" + newNodeSet.size() + " preds="+foo.size());
                newNodeSet.addAll( foo );
                interimSet.clear();
            }
        }

        return newNodeSet;
    }


    /**
     * Checks whether the node matches this step.
     *
     * @param node  the node to check
     * @param contextSupport  the context support
     * @return true if matches
     * @throws JaxenException
     */
    public boolean matches( int node, ContextSupport contextSupport ) throws JaxenException
    {

        Navigator nav = contextSupport.getNavigator();
        String myUri = null;
        String nodeName = null;
        String nodeUri = null;

        if( nav.isElement( node ) )
        {
            //            logger.debug( "matches(): element node" );
            nodeName = nav.getElementName( node );
            nodeUri = nav.getElementNamespaceUri( node );
        }
        else if( nav.isText( node ) )
        {
            return false;
        }
        else if( nav.isAttribute( node ) )
        {
            if( getAxis() != Axis.ATTRIBUTE )
            {
                return false;
            }
            nodeName = nav.getAttributeName( node );
            nodeUri = nav.getAttributeNamespaceUri( node );

        }
        else if( nav.isDocument( node ) )
        {
            return false;
        }
        else if( nav.isNamespace( node ) )
        {
            if( getAxis() != Axis.NAMESPACE )
            {
                // Only works for namespace::*
                return false;
            }
            nodeName = nav.getNamespacePrefix( node );
        }
        else
        {
            return false;
        }

        if( hasPrefix )
        {
            myUri = contextSupport.translateNamespacePrefixToUri( this.prefix );
            if( myUri == null )
            {
                throw new UnresolvableException( "Cannot resolve namespace prefix '"
                        + this.prefix + "'" );
            }
        }
        else if( matchesAnyName )
        {
            return true;
        }

        // If we map to a non-empty namespace and the node does not
        // or vice-versa, fail-fast.
        if( hasNamespace( myUri ) != hasNamespace( nodeUri ) )
        {
            return false;
        }

        // To fail-fast, we check the equality of
        // local-names first.  Shorter strings compare
        // quicker.
        if( matchesAnyName || nodeName.equals( getLocalName() ) )
        {
            return matchesNamespaceURIs( myUri, nodeUri );
        }

        return false;
    }


    /**
     * Checks whether the URI represents a namespace.
     *
     * @param uri  the URI to check
     * @return true if non-null and non-empty
     */
    private boolean hasNamespace( String uri )
    {
        return ( uri != null && uri.length() > 0 );
    }


    /**
     * Compares two namespace URIs, handling null.
     *
     * @param uri1  the first URI
     * @param uri2  the second URI
     * @return true if equal, where null==""
     */
    protected boolean matchesNamespaceURIs( String uri1, String uri2 )
    {
        if( uri1 == uri2 )
        {
            return true;
        }
        if( uri1 == null )
        {
            return ( uri2.length() == 0 );
        }
        if( uri2 == null )
        {
            return ( uri1.length() == 0 );
        }
        return uri1.equals( uri2 );
    }


    /**
     * Returns a full information debugging string.
     *
     * @return a debugging string
     */
    public String toString()
    {
        String prefix = getPrefix();
        String qName = "".equals( prefix ) ? getLocalName() : getPrefix() + ":"
                + getLocalName();
        return "[(NameStepImpl): " + qName + "]";
    }

}
