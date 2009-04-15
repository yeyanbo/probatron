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
 * Created on 19 Apr 2007
 */
package com.griffinbrown.shail.iter;

import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.util.ShailIterator;

public class AttributeIterator extends ShailIterator
{
    int[] atts;
    int pos;
    int lastAttribute = - 1;
    private Model model;

    private static Logger logger = Logger.getLogger( AttributeIterator.class );


    /**
     * Constructor.
     *
     * @param parent the parent element for the attributes.
     */
    public AttributeIterator( int parent, Model model )
    {
        this.model = model;
        //        logger.debug( "ATTR ITERATOR FOR PARENT " + parent );
        this.atts = model.getAttributes( parent );
        this.pos = 0;

        //DEBUG
        //        debug( parent );

        //        exclude namespace decls
        //        for( int i = this.atts.length - 1; i >= 0; i-- )
        //        {
        //            int node = atts[ i ];
        //            if( ! "http://www.w3.org/2000/xmlns/".equals( model.getNamespaceURI( node ) ) )
        //            {
        //                this.lastAttribute = i;
        //                break;
        //            }
        //        }
    }

    /**
     * Returns the next attribute node.
     */
    public int nextNode()
    {
        if( pos >= atts.length )
        {
            throw new NoSuchElementException();
        }

        //        logger.debug( "att NS="+model.getNamespaceURI( pos ) );
        //
        //        if( "http://www.w3.org/2000/xmlns/".equals( model.getNamespaceURI( pos ) ) )
        //        {
        //            // XPath doesn't consider namespace declarations to be attributes 
        //            // so skip it and go to the next one
        //            
        //            System.err.println("++++++SKIPPING @"+model.getName( pos ));
        //            
        //            pos++;
        //            if( hasNext() )
        //                    next();
        //        }

        int pn = atts[ pos ];

        pos++;
        return pn;
    }

    /**
     * Returns true if there are any attribute nodes remaining, otherwise false.
     */
    public boolean hasNext()
    {
        if( atts == null )
            return false;
        return pos < atts.length;
        //        return pos <= lastAttribute;
    }

}