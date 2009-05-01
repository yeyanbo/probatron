package com.griffinbrown.shail;

/**
 * A wrapper for a string stored in a hash table.
 * @author andrews
 *
 * $Id$
 */
public final class StringToken implements Comparable
{
    private String s;
    private int n;


    public int hashCode()
    {
        return s.hashCode();
    }


    public StringToken( String s, int n )
    {
        this.s = s;
        this.n = n;
    }


    public int compareTo( Object o )
    {

        return s.compareTo( ( ( StringToken )o ).s );
    }


    public String getString()
    {
        return s;
    }


    public int getTokenId()
    {
        return n;
    }


    public boolean equals( Object o )
    {
        return s.equals( ( ( StringToken )o ).s );
    }

}
