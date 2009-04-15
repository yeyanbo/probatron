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

import com.griffinbrown.shail.util.ShailIterator;

public class SingleNodeIterator extends ShailIterator
{
    private int object;
    private boolean seen;


    /**
     * Creates a new single object iterator.
     * 
     * @param object the object to iterate over
     */
    public SingleNodeIterator( int object )
    {
        this.object = object;
        this.seen = false;
    }


    /**
     * Returns true if this iterator's element has not yet been seen; false if it has.
     * 
     * @return true if this iterator has another element; false if it doesn't
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return ! this.seen;
    }


    /**
     * Returns the single element in this iterator if it has not yet
     * been seen. 
     * 
     * @return the next element in this iterator
     * 
     * @throws NoSuchElementException if the element has already been seen
     * 
     * @see java.util.Iterator#next()
     */
    public int nextNode()
    {
        if( hasNext() )
        {
            this.seen = true;
            return this.object;
        }

        throw new NoSuchElementException();
    }


    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
