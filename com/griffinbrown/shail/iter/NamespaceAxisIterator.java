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
 * Created on 21 Aug 2008
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;

public class NamespaceAxisIterator extends ShailIterator
{
    private int[] namespaces;
    private int pos = 0;


    public NamespaceAxisIterator( int context, Model model )
    {
        namespaces = model.getNamespaces( context );
    }


    /**
     * Returns true if there are any namespace nodes remaining; false otherwise.
     */
    public boolean hasNext()
    {
        return pos < namespaces.length;
    }


    /**
     * Returns the next namespace node.
     * 
     * @throws NoSuchElementException if no namespace nodes remain
     */
    public int nextNode()
    {
        if( pos >= namespaces.length )
        {
            throw new NoSuchElementException();
        }
        int pn = namespaces[ pos ];

        pos++;
        return pn;
    }

}
