package com.griffinbrown.shail.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/*
 * A container for Shail nodes.
 * 
 * @author andrews
 * 
 * $Id$
 */
public class ShailList implements List
{
    private IntArray list;

    public static final ShailList EMPTY_LIST = new ShailList( 0 );


    //    private static Logger logger = Logger.getLogger( ShailList.class );

    /**
     * Creates a list with default initial capacity.
     */
    public ShailList()
    {
        this.list = new IntArray();
    }


    /**
     * @return <code>true</code> if the object for comparison is an instance of {@link ShailList}
     * and has the same hashcode
     */
    public boolean equals( Object o )
    {
        return ( o instanceof ShailList ) && o.hashCode() == this.hashCode();
    }


    /**
     * @return the hashcode of the string returned by {@link #toString()}
     */
    public int hashCode()
    {
        return toString().hashCode(); //FIXME: for very large lists, this will be expensive
    }


    /**
     * Creates a list with a specified initial capacity.
     * @param capacity initial capacity of list
     */
    public ShailList( int capacity )
    {
        this.list = new IntArray( capacity );
    }


    /**
     * Converts the singleton list passed in to a {@link ShailList}.
     * For internal use, where a {@link ShailSingletonList} contains a single <strong>node</strong>,
     * rather than a String, Boolean, Number or node-set.
     * Typically, this will occur e.g. when a call to <code>document()</code> returns the root node.
     */
    public ShailList( ShailSingletonList list )
    {
        this.list = new IntArray();
        int i = list.getInt( 0 );
        this.list.appendItem( i );
    }


    /**
     * Creates a list from an existing list.
     * @param list the existing list to create the new list from
     */
    public ShailList( ShailList list )
    {
        this.list = list.list;
        //            logger.debug( "Collection list "+c.getClass().getName()+"="+(( ShailList )c ).list + "; items="+(( ShailList )c ).list.numItems() );
    }


    /**
     * Appends an item to the list. 
     * @param i the item to append
     */
    public void addInt( int i )
    {
        this.list.appendItem( i );
    }


    /**
     * Unsupported in this implementation.
     */
    public final boolean add( Object o )
    {
        throw new UnsupportedOperationException();
        //        if( ! ( o instanceof Integer ) )
        //        {
        //            throw new UnsupportedOperationException(
        //                    "can't add non-Integer object to ShailList: " + o.getClass().getName() );
        //        }
        //        else
        //        {
        //            this.list.appendItem( ( ( Integer )o ).intValue() );
        //            return true;
        //        }
    }


    /**
     * Unsupported in this implementation.
     */
    public final void add( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * @throws UnsupportedOperationException if the argument passed in is not an instance of {@link ShailList}
     */
    public boolean addAll( Collection c )
    {
        if( ! ( c instanceof ShailList ) )
            throw new UnsupportedOperationException();

        this.list.appendMulti( ( ( ShailList )c ).toIntArray() );
        return true;
    }


    /**
     * Unsupported in this implementation.
     */
    public boolean addAll( int arg0, Collection arg1 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Prepends an item to the list.
     * @param i the item to prepend
     */
    public void addFirst( int i )
    {
        this.list.prependItem( i );
    }


    /**
     * Empties the list of entries.
     * In this implementation, the list is only emptied <strong>if it is not already empty</strong>.
     */
    public void clear()
    {
        if( ! isEmpty() )
            this.list.empty();
    }


    /**
     * Unsupported in this implementation.
     */
    public boolean contains( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Whether this list contains a specified integer.
     * @param i
     * @return
     */
    public boolean containsInt( int i )
    {
        int[] sorted = list.toIntArray();
        Arrays.sort( sorted );
        return Arrays.binarySearch( sorted, i ) >= 0;
    }


    /**
     * Unsupported in this implementation.
     */
    public boolean containsAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Unsupported in this implementation.
     */
    public Object get( int i )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Accesses the item at index <code>i</code> in the list.
     * @param i index of the item to retrieve
     * @return the item at index <code>i</code>, an {@link ArrayIndexOutOfBoundsException} is thrown if no such index exists
     */
    public int getInt( int i )
    {
        return list.itemAt( i );
    }


    /**
     * Unsupported in this implementation.
     */
    public int indexOf( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Whether the list is empty.
     * @return whether the list is empty
     */
    public boolean isEmpty()
    {
        return this.list.numItems() == 0;
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer( "[" );
        for( int i = 0; i < list.numItems(); i++ )
        {
            sb.append( list.itemAt( i ) );
            if( i < list.numItems() - 1 )
                sb.append( ',' );
        }
        sb.append( ']' );
        //        sb.append( " #hash="+hashCode()+"#");
        return sb.toString();
    }


    /**
     * @return a {@link ShailIterator} for the list
     */
    public Iterator iterator()
    {
        return new ShailIterator() {

            private int cursor = 0;


            public int nextNode()
            {
                if( cursor < list.numItems() )
                {
                    int i = list.itemAt( cursor );
                    cursor++;
                    return i;
                }
                return - 1;
            }


            public boolean hasNext()
            {
                return cursor < list.numItems();
            }
        };

    }


    /**
     * Unsupported in this implementation.
     */
    public int lastIndexOf( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Unsupported in this implementation.
     */
    public ListIterator listIterator()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Unsupported in this implementation.
     */
    public ListIterator listIterator( int arg0 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */
    public Object remove( int arg0 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */
    public boolean remove( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */
    public boolean removeAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */
    public boolean retainAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */
    public Object set( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the number of items in the list
     */
    public int size()
    {
        return this.list.numItems();
    }

    /**
     * Unsupported in this implementation.
     */
    public List subList( int arg0, int arg1 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */
    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported in this implementation.
     */    
    public Object[] toArray( Object[] arg0 )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Accesses the items in this list as an <code>int</code> array.
     * The underlying array is unaffected.
     * @return the items in the list as an array
     */
    public int[] toIntArray()
    {
        return this.list.toIntArray();
    }

    /**
     * Sorts the items in the list.
     * This implementation uses {@link Arrays}{@link #sort()}.
     */
    public void sort() //prolly better implemented as a method of IntArray, for efficiency
    {
        int[] a = this.list.toIntArray();
        Arrays.sort( a );
        this.list.empty();
        this.list.appendMulti( a );
    }

    /**
     * Reverses the items in the list.
     */
    public void reverse() //prolly better implemented as a method of IntArray, for efficiency
    {
        int[] a = new int[ list.numItems() ];
        int j = 0;
        for( int i = list.numItems() - 1; i >= 0; i-- )
        {
            a[ j ] = list.itemAt( i );
            j++;
        }
    }

    /**
     * Gets a type-specific iterator for this list.
     * @return an iterator for this list
     */
    public ShailListIterator shailListIterator()
    {
        ShailListIterator iter = new ShailListIterator();
        iter.setList( list );
        return iter;
    }

}