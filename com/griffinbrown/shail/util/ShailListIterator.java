/*
 * Created on 17 Aug 2007
 */
package com.griffinbrown.shail.util;

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


    public int previous()
    {
        if( hasPrevious() )
        {
            int i = list.itemAt( cursor - 1 );
            cursor--;
            return i;
        }
        throw new IndexOutOfBoundsException();
    }


    public boolean hasPrevious()
    {
        return ( cursor - 1 ) >= 0;
    }


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


    public void add( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public Object next()
    {
        throw new UnsupportedOperationException();
    }


    public int nextIndex()
    {
        throw new UnsupportedOperationException();
    }


    public int previousIndex()
    {
        throw new UnsupportedOperationException();
    }


    public void remove()
    {
        throw new UnsupportedOperationException();
    }


    public void set( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }

}
