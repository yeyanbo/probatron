/*
 * Created on 14 Sep 2007
 */
package com.griffinbrown.shail;

class DedupingCharArrayStringHandler extends CharArrayStringHandler
{
    public void addString( String s )
    {
        int tokid = chars.numItems();

        if( tokid == 0 ) //first offset
            eventStream.write( 0 );

        if( stringMap.containsKey( s ) )
        {
            tokid = ( ( Integer )stringMap.get( s ) ).intValue();
        }
        else
        {
            stringMap.put( s, new Integer( tokid ) );
            chars.appendMulti( s.toCharArray() );
            chars.appendItem( ( char )0 );
        }

        while( tokid != 0 )
        {
            eventStream.write( tokid & 127 );
            tokid >>= 7;
        }
    }
    
    public String getString( int node )
    {
        int start = consumeNumber( node + 1 );
        return getString( node, start );
    }
    
    private String getString( int node, int start )
    {
        for( int i = start; i < strings.length; i++ )
        {
            if( strings[ i ] == 0 ) //got NULL char
            {
                return new String( strings, start, i - start );
            }
        }
        return null;
    }
}
