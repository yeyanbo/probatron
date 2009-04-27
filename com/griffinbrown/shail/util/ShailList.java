package com.griffinbrown.shail.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ShailList implements List
{
    private IntArray list;

    public static final ShailList EMPTY_LIST = new ShailList( 0 );

//    private static Logger logger = Logger.getLogger( ShailList.class );


    public ShailList()
    {
        this.list = new IntArray();
    }


    public boolean equals( Object o )
    {
        return ( o instanceof ShailList ) && o.hashCode() == this.hashCode();
    }


    public int hashCode()
    {
        return toString().hashCode();   //FIXME: for very large lists, this will be expensive
        //FIXME: do we also need to sort the list before toString(), to get round reverse axes?
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
     * Converts the singleton list passed in to a ShailList.
     * For internal use, where a ShailSingletonList contains a single <strong>node</strong>,
     * rather than a String, Boolean, Number or node-set.
     * Typically, this will occur e.g. when a call to document() returns the root node.
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


    public void addInt( int i )
    {
        this.list.appendItem( i );
    }


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


    public final void add( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException();
    }


    public boolean addAll( Collection c )
    {
        if( ! ( c instanceof ShailList ) )
            throw new UnsupportedOperationException();

        this.list.appendMulti( ( ( ShailList )c ).toIntArray() );
        return true;
    }


    public boolean addAll( int arg0, Collection arg1 )
    {
        throw new UnsupportedOperationException();
    }


    public void addFirst( int i )
    {
        this.list.prependItem( i );
    }


    /**
     * In this implementation, the list is only emptied <strong>if it is not already empty</strong>.
     */
    public void clear()
    {
        if( ! isEmpty() )
            this.list.empty();
    }


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


    public boolean containsAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public Object get( int i )
    {
        throw new UnsupportedOperationException();
    }


    public int getInt( int i )
    {
        return list.itemAt( i );
    }


    public int indexOf( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


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


    public int lastIndexOf( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public ListIterator listIterator()
    {
        throw new UnsupportedOperationException();
    }


    public ListIterator listIterator( int arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public Object remove( int arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public boolean remove( Object arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public boolean removeAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public boolean retainAll( Collection arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public Object set( int arg0, Object arg1 )
    {
        throw new UnsupportedOperationException();
    }


    public int size()
    {
        return this.list.numItems();
    }


    public List subList( int arg0, int arg1 )
    {
        throw new UnsupportedOperationException();
    }


    public Object[] toArray()
    {
        //        throw new UnsupportedOperationException();
        Integer[] result = new Integer[ this.list.numItems() ];
        for( int i = 0; i < this.list.numItems(); i++ )
        {
            //            IntegerCounter.increment();
            result[ i ] = new Integer( this.list.itemAt( i ) );
        }
        return result;
    }


    public Object[] toArray( Object[] arg0 )
    {
        throw new UnsupportedOperationException();
    }


    public int[] toIntArray()
    {
        return this.list.toIntArray();
    }


    public void sort() //prolly better implemented as a method of IntArray, for efficiency
    {
        int[] a = this.list.toIntArray();
        Arrays.sort( a );
        this.list.empty();
        this.list.appendMulti( a );
    }


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


    public ShailListIterator shailListIterator()
    {
        ShailListIterator iter = new ShailListIterator();
        iter.setList( list );
        return iter;
    }


    public ShailListIterator shailListIterator( int i )
    {
        ShailListIterator iter = new ShailListIterator( i );
        //        IntArray subList = list.subList( i );
        //        logger.debug( "shailListIterator(" + i+") subList="+subList );
        //        iter.setList( subList );
        iter.setList( list );
        return iter;
    }

}