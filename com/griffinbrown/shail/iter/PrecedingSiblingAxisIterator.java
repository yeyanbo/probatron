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
 * Created on 23 Aug 2007
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;

public class PrecedingSiblingAxisIterator extends ShailIterator
{
    private int[] precedingSiblings;
    private int pos;
    private Model model;

    private static Logger logger = Logger.getLogger( PrecedingSiblingAxisIterator.class );


    public PrecedingSiblingAxisIterator( int context, Model model )
    {
        this.model = model;
        precedingSiblings = model.getPrecedingSiblings( context );
//        logger.debug( "preceding siblings=" + debug( precedingSiblings ) );
        pos = 0;
    }

    /**
     * Returns the next preceding sibling node.
     * 
     * @return the next preceding sibling node
     * 
     * @throws NoSuchElementException if no preceding sibling nodes remain
     * 
     * @see java.util.Iterator#next()
     */
    public int nextNode()
    {
        if( pos >= precedingSiblings.length )
        {
            throw new NoSuchElementException();
        }
        int pn = precedingSiblings[ pos ];

        pos++;
        return pn;
    }

    /**
     * Returns true if there are any preceding sibling nodes remaining; 
     * false otherwise.
     * 
     * @return true if any preceding sibling nodes remain
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return pos < precedingSiblings.length;
    }


    private String debug( int[] nodes )
    {
        String s = "[";
        for( int i = 0; i < nodes.length; i++ )
        {
            s += model.toString( nodes[ i ] );
            if( i < nodes.length - 1 )
                s += ",";
        }
        s += "]";
        return s;
    }
}
