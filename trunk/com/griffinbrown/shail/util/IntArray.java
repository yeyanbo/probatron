/*
 * Created on 29-Apr-2004
 * 
 */
package com.griffinbrown.shail.util;

import java.util.EmptyStackException;

import org.apache.log4j.Logger;

/**
 *A class for storing an array of Java int primitive datatypes.
 *
 *Objects of this class dynamically allocate storage for themselves in 'pages', as specifiable
 *in one of the constructors.
 */
public class IntArray
{

    private final static int DEFAULT_CAPACITY = 2; //default size for each mem block
    private int numItems; // how many ints contained
    private int[] items; // yer actual data
    private int capacityDelta; // capacity when object is fresh
    private int capacity; // how many slots in total object has

    private static Logger logger = Logger.getLogger( IntArray.class );


    /**
     * Constructs an IntArray with default capacity (65536 items).
     */
    public IntArray()
    {
        capacityDelta = DEFAULT_CAPACITY;
        init();
    }


    /**
     * Constucts an IntArray with a capacity delta as specified. The array will allocate a new memory
     * block for storage when multiples of this number of items are exceeded. 
     * @param capacity the initial capacity, and the amount to increase capacity by every time multiples of this are exceeded.
     */
    public IntArray( int capacity )
    {
        this.capacityDelta = capacity;
        init();
    }


    private void init()
    {
        items = new int[ capacityDelta ];
        numItems = 0;
        capacity = capacityDelta;
    }


    //	public static void main(String[] args)
    //	{
    //		IntArray a = new IntArray(1);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //
    //		System.out.println("should be 10: " + a.numItems());
    //		System.out.print("should be 10 9 10 9 10 9 10 9 10 9 : ");
    //		for (int i = 0; i < a.numItems(); i++)
    //		{
    //			System.out.print(a.itemAt(i));
    //		}
    //		a.empty();
    //		System.out.println("\nshould be 0: " + a.numItems());
    //
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //		a.appendItem(10);
    //		a.appendItem(9);
    //
    //		System.out.println("should be 10: " + a.numItems());
    //		System.out.print("should be 10 9 10 9 10 9 10 9 10 9 : ");
    //
    //		for (int i = 0; i < a.numItems(); i++)
    //		{
    //			System.out.print(a.itemAt(i));
    //		}
    //		
    //		a.empty();
    //		
    //		int[]c = new int[2];
    //		
    //		c[0] = 10;
    //		c[1] = 9;
    //		
    //		a.appendMulti( c );
    //		a.appendMulti( c );
    //		a.appendMulti( c );
    //		a.appendMulti( c );
    //		a.appendMulti( c );
    //		
    //		System.out.println("should be 10: " + a.numItems());
    //		System.out.print("should be 10 9 10 9 10 9 10 9 10 9 : ");
    //		
    //		for (int i = 0; i < a.numItems(); i++)
    //				{
    //					System.out.print(a.itemAt(i));
    //				}
    //		
    //		
    //
    //	}

    private void addCapacity()
    {
        //        capacity += capacityDelta;

        capacity += ( capacity == 0 ? DEFAULT_CAPACITY : capacity );

        int[] newItems = new int[ capacity ];
        System.arraycopy( items, 0, newItems, 0, numItems );
        this.items = null;
        this.items = newItems;
    }


    /**
     * Returns this object to its newly-constructed state (i.e., empty and with initial memory allocation).
     */
    public void empty()
    {
        //		assert (capacityDelta > 0);
        //        items = new int[ capacityDelta ];

        items = new int[ 0 ];
        numItems = 0;
        capacity = 0;
    }


    /**
     * @return the number of items stored in this array.
     */
    public int numItems()
    {
        return this.numItems;
    }


    /**
     * Appends an item to this array.
     * @param n the item to append.
     */
    public void appendItem( int n )
    {
        if( numItems >= capacity )
        {
            addCapacity();
        }
        items[ numItems ] = n;
        numItems++;

    }


    /**
     * Prepends an item to this array.
     * @param n the item to prepend.
     */
    public void prependItem( int n )
    {
        numItems++;
        if( numItems >= capacity )
        {
            addCapacity();
        }

        //copy old array 0..n to 1..n in new array
        int[] copy = new int[ capacity ];
        System.arraycopy( this.items, 0, copy, 1, this.numItems - 1 );

        //prepend the new item
        items[ 0 ] = n;
    }


    /**
     * Appends the items contained in the array <tt>a</tt> to this array.
     * @param a the items to append.
     */
    public void appendMulti( int[] a )
    {
        while( numItems + a.length > capacity )
        {
            addCapacity();
        }

        System.arraycopy( a, 0, this.items, numItems, a.length );

        numItems += a.length;

        //DEBUG
        /* if( (numItems / 12) / 1000 > 0 && (numItems/12) % 1000 == 0 )
         {
         System.err.println("nodes created="+numItems/12);
         //			if( (numItems/12) / 10000 > 0 && (numItems/12) % 10000 == 0 )
         //				System.err.println( (numItems/12)+","+ Runtime.getRuntime().freeMemory()/1024);//+"kb free");		
         }//*/
    }


    public int pop()
    {
        if( numItems > 0 )
        {
            numItems--;
            return this.items[ numItems ];
        }
        throw new EmptyStackException();
    }


    public int peek()
    {
        if( numItems > 0 )
        {
            return this.items[ numItems - 1 ];
        }
        throw new EmptyStackException();
    }


    /**
     * Modifies a previously added value.
     * @param pos the position at which to make the modification.
     * @param n the new value.
     */
    public void modify( int pos, int n )
    {
        if( pos < 0 || pos >= numItems )
        {
            throw new ArrayIndexOutOfBoundsException();
        }
        items[ pos ] = n;
    }


    /**
     * Retrieves an item from the array.
     * @param pos the position of the item to retrieve. 
     * @return the item at that position.
     */
    public int itemAt( int pos )
    {
        if( pos < 0 || pos >= numItems )
        {
            throw new ArrayIndexOutOfBoundsException( "wanted " + pos + "; items=" + numItems );
        }
        return items[ pos ];
    }


    public String toString()
    {
        StringBuffer s = new StringBuffer( "[" );
        for( int i = 0; i < numItems; i++ )
        {
            if( i < numItems - 1 )
            {
                s.append( items[ i ] + "," );
            }
            else
            {
                s.append( items[ i ] );
            }
        }
        s.append( ']' );
        return s.toString();
    }


    public int[] toIntArray()
    {
        int[] result = new int[ numItems ];
        System.arraycopy( this.items, 0, result, 0, numItems );
        return result;
    }


    public boolean isEmpty()
    {
        return numItems == 0;
    }


    public IntArray subList( int start )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "subList(" + start + "); numItems=" + numItems );

        if( start < 0 || start > numItems )
        {
            throw new ArrayIndexOutOfBoundsException();
        }

        int len = numItems - start;
        int[] newList = new int[ len ];
        System.arraycopy( this.items, start, newList, 0, len );
        this.items = newList;
        this.numItems = len;

        return this;
    }
}
