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
 * Created on 23 Mar 2007
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.UnsupportedAxisException;

import com.griffinbrown.shail.EmptyShailIterator;
import com.griffinbrown.shail.util.ShailIterator;

public class FollowingSiblingAxisIterator extends ShailIterator
{
    private int contextNode;
    private Navigator navigator;
    private ShailIterator siblingIter;


    public FollowingSiblingAxisIterator( int contextNode, Navigator navigator )
            throws UnsupportedAxisException
    {
        this.contextNode = contextNode;
        this.navigator = navigator;
        init();

    }


    private void init() throws UnsupportedAxisException
    {
        int parent = this.navigator.getParentNode( this.contextNode );

        if( parent != - 1 )
        {
            siblingIter = ( ShailIterator )this.navigator.getChildAxisIterator( parent );

            while( siblingIter.hasNext() )
            {
                int eachChild = siblingIter.nextNode();
                if( eachChild == this.contextNode )
                    break;
            }
        }
        else
        {
            siblingIter = new EmptyShailIterator();
        }

    }


    /**
     * Returns true if there are any following siblings remain; false otherwise.
     *
     * @return true if any following siblings remain; false otherwise
     *
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return siblingIter.hasNext();
    }


    /**
     * Returns the next following sibling.
     *
     * @return the next following sibling
     *
     * @throws NoSuchElementException if no following siblings remain
     *
     * @see java.util.Iterator#next()
     */
    public int nextNode() throws NoSuchElementException
    {
        return siblingIter.nextNode();
    }

}
