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
 * Created on 28 Mar 2007
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import com.griffinbrown.shail.util.ShailIterator;

public class SelfAxisIterator extends ShailIterator
{
    private boolean seen;
    private int contextNode;
    
    public SelfAxisIterator( int pn )
    {
        this.seen = false;
        this.contextNode = pn;
    }

    /**
     * Returns true if the self node remains; 
     * false otherwise.
     * 
     * @return true if the self node remains
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return ! this.seen;
    }


    /**
     * Returns the self node, if not already iterated past.
     * 
     * @return the self node, if not already iterated past
     * 
     * @throws NoSuchElementException if no self node remains
     * 
     * @see java.util.Iterator#next()
     */
    public int nextNode()
    {
        if( hasNext() )
        {
            this.seen = true;
            return contextNode;
        }
        throw new NoSuchElementException();
    }

}
