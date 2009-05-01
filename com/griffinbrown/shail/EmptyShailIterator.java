/*
 * Created on 31 Jul 2007
 */
package com.griffinbrown.shail;

import com.griffinbrown.shail.util.ShailIterator;

/**
 * An iterator for empty lists.
 * @author andrews
 *
 * $Id$
 */
public final class EmptyShailIterator extends ShailIterator
{
    /**
     * @return false
     */
    public boolean hasNext()
    {
        return false;
    }

    /**
     * @return this implementation always throws an {@link UnsupportedOperationException}
     */
    public int nextNode()
    {
        throw new UnsupportedOperationException();
    }

}
