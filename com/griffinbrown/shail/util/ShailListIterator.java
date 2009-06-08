/*
 * Created on 17 Aug 2007
 */
package com.griffinbrown.shail.util;

/**
 * An iterator for {@link ShailList}s.
 * @author andrews
 *
 * $Id$
 */
public class ShailListIterator extends ShailIterator
{
    private IntArray list;
    private int cursor;


    void setList( IntArray list )
    {
        this.list = list;
    }


    public ShailListIterator()
    {
        cursor = 0;
    }


    public ShailListIterator( int i )
    {
        cursor = i;
    }

    public static ShailListIterator EMPTY_ITERATOR = new ShailListIterator() {

        public boolean hasNext()
        {
            return false;
        }


        public int nextNode()
        {
            throw new UnsupportedOperationException();
        }


        public boolean hasPrevious()
        {
            return false;
        }


        public int previous()
        {
            return - 1;
        }
    };


    public int nextNode()
    {
        if( cursor < list.numItems() )
        {
            int i = list.itemAt( cursor );
            cursor++;
            return i;
        }
        throw new IndexOutOfBoundsException();
    }


    public boolean hasNext()
    {
        return cursor < list.numItems();
    }


    /**
     * Unsupported in this implementation.
     */
    public void add( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public Object next()
    {
        throw new UnsupportedOperationException();
    }


    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
