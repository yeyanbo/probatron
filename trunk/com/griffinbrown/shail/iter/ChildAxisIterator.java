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

package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;

public class ChildAxisIterator extends ShailIterator
{
    private int[] children;
    private int pos = 0;
//    private Model model;

    private Logger logger = Logger.getLogger( ChildAxisIterator.class );


    public ChildAxisIterator( int pn, Model model )
    {
//        this.model = model;
        children = model.getChildren( pn );

//                debug( pn );
    }

    /**
     * Returns the next child node.
     */
    public int nextNode()
    {
        if( pos >= children.length )
        {
            throw new NoSuchElementException();
        }
        int pn = children[ pos ];

        pos++;
        return pn;
    }

    /**
     * Returns true if there are any child nodes remaining, otherwise false.
     */
    public boolean hasNext()
    {
        return pos < children.length;
    }


    private void debug( Integer parent )
    {
        logger.debug( parent + " has " + children.length + " children:" );
        for( int i = 0; i < children.length; i++ )
        {
            logger.debug( "\t\t" + new Integer( children[ i ] ) );
        }
    }

}
