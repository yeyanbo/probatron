/*
 * Created on 31 Jul 2007
 */
package com.griffinbrown.shail;

import com.griffinbrown.shail.util.ShailIterator;

public final class EmptyShailIterator extends ShailIterator
{

    public boolean hasNext()
    {
        return false;
    }


    public int nextNode()
    {
        throw new UnsupportedOperationException();
    }

}
