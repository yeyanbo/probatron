/*
 * Created on 30 Aug 2007
 */
package com.griffinbrown.shail.util;

import java.util.EmptyStackException;

import org.apache.log4j.Logger;

/**
 * A resizable array of <code>char</code>s.
 * 
 * @author andrews
 * 
 * $Id$
 */
public class CharArray
{
    private final static int DEFAULT_CAPACITY = 2048; //default size for each mem block
    private int numItems; // how many ints contained
    private char[] items; // yer actual data
    private int capacityDelta; // capacity when object is fresh
    private int capacity; // how many slots in total object has

    private static Logger logger = Logger.getLogger( CharArray.class );


    /**
     * Constructs an CharArray with default capacity (2048 items).
     */
    public CharArray()
    {
        capacityDelta = DEFAULT_CAPACITY;
        init();
    }


    /**
     * Constucts an CharArray with a capacity delta as specified. The array will allocate a new memory
     * block for storage when multiples of this number of items are exceeded. 
     * @param capacity the initial capacity, and the amount to increase capacity by every time multiples of this are exceeded.
     */
    public CharArray( int capacity )
    {
        this.capacityDelta = capacity;
        init();
    }


    private void init()
    {
        items = new char[ capacityDelta ];
        numItems = 0;
        capacity = capacityDelta;
    }


    //  public static void main(String[] args)
    //  {
    //      CharArray a = new CharArray(1);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //
    //      System.out.println("should be 10: " + a.numItems());
    //      System.out.print("should be 10 9 10 9 10 9 10 9 10 9 : ");
    //      for (int i = 0; i < a.numItems(); i++)
    //      {
    //          System.out.print(a.itemAt(i));
    //      }
    //      a.empty();
    //      System.out.println("\nshould be 0: " + a.numItems());
    //
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //      a.appendItem(10);
    //      a.appendItem(9);
    //
    //      System.out.println("should be 10: " + a.numItems());
    //      System.out.print("should be 10 9 10 9 10 9 10 9 10 9 : ");
    //
    //      for (int i = 0; i < a.numItems(); i++)
    //      {
    //          System.out.print(a.itemAt(i));
    //      }
    //      
    //      a.empty();
    //      
    //      int[]c = new int[2];
    //      
    //      c[0] = 10;
    //      c[1] = 9;
    //      
    //      a.appendMulti( c );
    //      a.appendMulti( c );
    //      a.appendMulti( c );
    //      a.appendMulti( c );
    //      a.appendMulti( c );
    //      
    //      System.out.println("should be 10: " + a.numItems());
    //      System.out.print("should be 10 9 10 9 10 9 10 9 10 9 : ");
    //      
    //      for (int i = 0; i < a.numItems(); i++)
    //              {
    //                  System.out.print(a.itemAt(i));
    //              }
    //      
    //      
    //
    //  }

    private void addCapacity()
    {
        //        capacity += capacityDelta;

        capacity += ( capacity == 0 ? DEFAULT_CAPACITY : capacity );

        char[] newItems = new char[ capacity ];
        System.arraycopy( items, 0, newItems, 0, numItems );
        this.items = null;
        this.items = newItems;
    }


    /**
     * Returns this object to its newly-constructed state (i.e., empty and with initial memory allocation).
     */
    public void empty()
    {
        //      assert (capacityDelta > 0);
        //        items = new int[ capacityDelta ];

        items = new char[ 0 ];
        numItems = 0;
        capacity = 0;
    }


    /**
     * Accesses the number of items stored in the array.
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
    public void appendItem( char c )
    {
        if( numItems >= capacity )
        {
            addCapacity();
        }
        items[ numItems ] = c;
        numItems++;

    }


    /**
     * Prepends an item to this array.
     * @param n the item to prepend.
     */
    public void prependItem( char c )
    {
        numItems++;
        if( numItems >= capacity )
        {
            addCapacity();
        }

        //copy old array 0..n to 1..n in new array
        char[] copy = new char[ capacity ];
        System.arraycopy( this.items, 0, copy, 1, this.numItems - 1 );

        //prepend the new item
        items[ 0 ] = c;
    }


    /**
     * Appends the items contained in the array <tt>a</tt> to this array.
     * @param a the items to append.
     */
    public void appendMulti( char[] a )
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
         //         if( (numItems/12) / 10000 > 0 && (numItems/12) % 10000 == 0 )
         //             System.err.println( (numItems/12)+","+ Runtime.getRuntime().freeMemory()/1024);//+"kb free");       
         }//*/
    }


    /**
     * Accesses the last item in the array and removes it.
     * @return the last item in the array; if the array is empty an {@link EmptyStackException} is thrown
     */
    public int pop()
    {
        if( numItems > 0 )
        {
            numItems--;
            return this.items[ numItems ];
        }
        throw new EmptyStackException();
    }


    /**
     * Accesses the last item in the array.
     * @return the last item in the array; if the array is empty an {@link EmptyStackException} is thrown 
     */
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
    public void modify( int pos, char c )
    {
        if( pos < 0 || pos >= numItems )
        {
            throw new ArrayIndexOutOfBoundsException();
        }
        items[ pos ] = c;
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


    /**
     * Returns a snapshot of items in the array as a <code>char</code> array.
     * The underlying array is unaffected.
     * @return an array of items stored in the array
     */
    public char[] toCharArray()
    {
        char[] result = new char[ numItems ];
        System.arraycopy( this.items, 0, result, 0, numItems );
        return result;
    }


    /**
     * Whether the array is empty
     * @return <code>true</code> if the array is empty, otherwise <code>false</code>
     */
    public boolean isEmpty()
    {
        return numItems == 0;
    }


}
