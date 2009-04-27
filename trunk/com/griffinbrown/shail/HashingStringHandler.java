/*
 * Created on 12 Sep 2007
 */
package com.griffinbrown.shail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class HashingStringHandler implements StringHandler
{
    private HashMap stringMap;
    private int stringLen;
    private ByteArrayOutputStream eventStream;
    private String[] strings;
    private Builder builder;

    private static Logger logger = Logger.getLogger( HashingStringHandler.class );


    public HashingStringHandler()
    {
        stringMap = new HashMap();
    }


    public void addString( String s )
    {
        StringToken dummy = new StringToken( s, 0 );
        Integer tokenId = ( Integer )stringMap.get( dummy );

        int tokid;

        if( tokenId == null )
        {
            tokid = stringMap.size() + 1;
            StringToken token = new StringToken( s, tokid );
            stringMap.put( token, new Integer( token.getTokenId() ) );
            stringLen += s.length();
        }
        else
        {
            tokid = tokenId.intValue();
        }

        while( tokid != 0 )
        {
            //N.B. WAS: builder.shelf.write( tokid & 127 );
            eventStream.write( tokid & 127 );
            tokid >>= 7;
        }

        //        logger.debug( "added string; event stream="+eventStream.size() );
    }


    /**
     * @return string of the node passed in
     * @param node the node whose string should be returned
     */
    public String getString( int node )
    {
        int stringId = consumeNumber( node + 1 );
        return strings[ stringId ];
    }


    /**
     * @see com.griffinbrown.shail.StringHandler#getTotalStringLength()
     */
    public int getTotalStringLength()
    {
        return stringLen;
    }


    /**
     * @see com.griffinbrown.shail.StringHandler#setEventStream(java.io.OutputStream)
     */
    public void setEventStream( OutputStream eventStream )
    {
        this.eventStream = ( ByteArrayOutputStream )eventStream;
    }


    int consumeNumber( int index )
    {
        //        logger.debug( "consumeNumber("+index+")");

        int tokid = 0;

        int j = 0;
        while( true )
        {
            tokid |= ( builder.getEvents()[ index ] << ( 7 * j ) );

            if( ( builder.getEvents()[ index + 1 ] & 0x80 ) != 0x00 )
            {
                break;
            }
            else
            {
                j++;
                index++;
            }

        }

        //        logger.debug( "consumeNumber() = " + tokid );

        return tokid;

    }


    /**
     * @see com.griffinbrown.shail.StringHandler#endDocument()
     */
    public void endDocument()
    {
        Object[] keys = this.stringMap.keySet().toArray();
        strings = new String[ keys.length + 1 ];
        for( int i = 0; i < keys.length; i++ )
        {
            StringToken st = ( StringToken )keys[ i ];
            strings[ st.getTokenId() ] = st.getString();
            keys[ i ] = null;
            //             System.out.println( "{" + st.getString() + "},{" + st.getTokenId() + "}" );

        }
//        stringMap = null;
        stringMap.clear();  //using clear() instead of nulling the map out means it can be used for MultiRootQAHandler too
    }


    /**
     * Provides a snapshot of the strings so far assembled.
     */
    private void snapshot()
    {
        Object[] keys = this.stringMap.keySet().toArray();
        strings = new String[ keys.length + 1 ];
        for( int i = 0; i < keys.length; i++ )
        {
            StringToken st = ( StringToken )keys[ i ];
            strings[ st.getTokenId() ] = st.getString();
            keys[ i ] = null;
            //                         System.out.println( "{" + st.getString() + "},{" + st.getTokenId() + "}" );

        }
    }


    /**
     * @see com.griffinbrown.shail.StringHandler#setBuilder(com.griffinbrown.shail.Builder)
     */
    public void setBuilder( Builder builder )
    {
        this.builder = builder;
    }

}
