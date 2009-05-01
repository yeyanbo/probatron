package com.griffinbrown.shail.util;

import java.util.Iterator;

/**
 * Base iterator for Shail document axes. 
 * @author andrews
 *
 * $Id$
 */
public abstract class ShailIterator implements Iterator
{
    /**
     * Whether there is further node in the list.
     */
    public abstract boolean hasNext();

    /**
     * Accesses the next Shail node in the list.
     * @return the next Shail node in the list
     */
    public abstract int nextNode();

    /**
     * This operation is not supported.
     */
    public Object next()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
