package com.griffinbrown.shail.util;

import java.util.Iterator;

public abstract class ShailIterator implements Iterator
{
    public abstract boolean hasNext();


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
