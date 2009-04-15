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
 * Created on 31 Jul 2007
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import org.probatron.jaxen.JaxenRuntimeException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.UnsupportedAxisException;


import com.griffinbrown.shail.EmptyShailIterator;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.shail.util.ShailIterator;

public class FollowingAxisIterator extends ShailIterator
{
    private int contextNode;

    private Navigator navigator;

    private ShailIterator siblings;

    private ShailIterator currentSibling;


    /**
     * Create a new <code>following</code> axis iterator.
     * 
     * @param contextNode the node to start from
     * @param navigator the object model specific navigator
     */
    public FollowingAxisIterator( int contextNode, Navigator navigator )
            throws UnsupportedAxisException
    {
        this.contextNode = contextNode;
        this.navigator = navigator;
        this.siblings = ( ShailIterator )navigator
                .getFollowingSiblingAxisIterator( contextNode );
        this.currentSibling = new EmptyShailIterator();
    }


    private boolean goForward()
    {
        while( ! siblings.hasNext() )
        {
            if( ! goUp() )
            {
                return false;
            }
        }

        int nextSibling = siblings.nextNode();

        this.currentSibling = new DescendantOrSelfAxisIterator( nextSibling, ModelRegistry.getModelForNode( nextSibling ) );

        return true;
    }


    private boolean goUp()
    {
        if( contextNode == - 1 || navigator.isDocument( contextNode ) )
        {
            return false;
        }

        try
        {
            contextNode = navigator.getParentNode( contextNode );

            if( contextNode != - 1 && ! navigator.isDocument( contextNode ) )
            {
                siblings = ( ShailIterator )navigator
                        .getFollowingSiblingAxisIterator( contextNode );
                return true;
            }
            else
            {
                return false;
            }
        }
        catch( UnsupportedAxisException e )
        {
            throw new JaxenRuntimeException( e );
        }
    }


    /**
     * Returns true if there are any following nodes remaining; 
     * false otherwise.
     * 
     * @return true if any following nodes remain
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        while( ! currentSibling.hasNext() )
        {
            if( ! goForward() )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Returns the next following node.
     * 
     * @return the next following node
     * 
     * @throws NoSuchElementException if no following nodes remain
     * 
     * @see java.util.Iterator#next()
     */
    public int nextNode() throws NoSuchElementException
    {
        if( ! hasNext() )
        {
            throw new NoSuchElementException();
        }

        return currentSibling.nextNode();
    }


    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    public void remove() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
