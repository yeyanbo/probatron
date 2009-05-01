/*
 * Created on 15 Jun 2007
 */
package com.griffinbrown.shail.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ShailSingletonList extends ShailList
{

    /**
     * @see com.griffinbrown.shail.ShailList#iterator()
     */
    public Iterator iterator()
    {
        return new ShailIterator() {

            private boolean done;


            public Object next()
            {
                if( ! done )
                {
                    done = true;
                    return singleton;
                }
                return null;
            }


            public boolean hasNext()
            {
                return ! done;
            }


            public int nextNode()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    private Object singleton;


    public ShailSingletonList( Object o )
    {
        singleton = o;
    }


    public ShailSingletonList( int node )
    {
        addInt( node );
    }


    /** 
     * The singleton list always contains one item.
     * 
     * @return 1
     */
    public int size()
    {
        return 1;
    }


    /**
     * Returns the single element in the list.
     * 
     * @return the only element in the list
     * 
     * @throws IndexOutOfBoundsException if index is not 0
     * 
     */
    public Object get( int index )
    {
        if( index == 0 )
        {
            return singleton;
        }
        throw new IndexOutOfBoundsException( index + " != 0" );
    }


    public String toString()
    {
        return "[" + singleton + "]";
    }


    public boolean isEmpty()
    {
        return false;
    }


    /**
     * @see com.griffinbrown.shail.ShailList#addAll(java.util.Collection)
     */
    public boolean addAll( Collection c )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#addAll(int, java.util.Collection)
     */
    public boolean addAll( int arg0, Collection arg1 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#clear()
     */
    public void clear()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#contains(java.lang.Object)
     */
    public boolean contains( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#containsAll(java.util.Collection)
     */
    public boolean containsAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#indexOf(java.lang.Object)
     */
    public int indexOf( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#listIterator()
     */
    public ListIterator listIterator()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#listIterator(int)
     */
    public ListIterator listIterator( int arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#remove(int)
     */
    public Object remove( int arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#remove(java.lang.Object)
     */
    public boolean remove( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#removeAll(java.util.Collection)
     */
    public boolean removeAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#retainAll(java.util.Collection)
     */
    public boolean retainAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#reverse()
     */
    public void reverse()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#set(int, java.lang.Object)
     */
    public Object set( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#sort()
     */
    public void sort()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#subList(int, int)
     */
    public List subList( int arg0, int arg1 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#toArray()
     */
    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#toArray(java.lang.Object[])
     */
    public Object[] toArray( Object[] arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see com.griffinbrown.shail.ShailList#toIntArray()
     */
    public int[] toIntArray()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see java.lang.Object#clone()
     */
    protected Object clone() throws CloneNotSupportedException
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        throw new UnsupportedOperationException();
    }
}
