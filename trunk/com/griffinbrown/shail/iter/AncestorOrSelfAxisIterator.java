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
 * Created on 28 Sep 2007
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;

public class AncestorOrSelfAxisIterator extends ShailIterator
{
    int[] ancestors;
    int pos = 0;


    public AncestorOrSelfAxisIterator( int contextNode, Model model )
    {
        ancestors = model.getAncestors( contextNode, true );

    }

    /**
     * Returns true if there are any ancestor or self nodes remaining, otherwise false.
     */
    public boolean hasNext()
    {
        return pos < ancestors.length;
    }

    /**
     * Return the next ancestor or self node.
     */
    public int nextNode()
    {
        if( pos >= ancestors.length )
        {
            throw new NoSuchElementException();
        }
        int pn = ancestors[ pos ];
        pos++;
        return pn;
    }

}
