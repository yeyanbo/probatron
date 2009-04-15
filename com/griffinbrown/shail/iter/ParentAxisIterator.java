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

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;

public class ParentAxisIterator extends ShailIterator
{
    int parent;
    boolean hasNext;
    private Model model;


    public ParentAxisIterator( int context, Model model )
    {
        this.model = model;

        //special case for namespace nodes: they're not part of the Shail stream!
        if( model.getType( context ) == Model.EV_NAMESPACE )
        {
            parent = model.getParent( context );
        }

        else
        {
            parent = model.getParent( context );
        }

        if( parent >= 0 )
        {
            hasNext = true;
        }
    }

    /**
     * Returns true if there is a parent node remaining; false otherwise.
     */
    public boolean hasNext()
    {
        return hasNext;
    }

    /**
     * Returns the parent node, if not already iterated past; otherwise <tt>-1</tt>.
     */
    public int nextNode()
    {
        if( parent < 0 || hasNext == false )
        {
            return - 1;
        }
        hasNext = false;
        return parent;
    }

}
