/*
 * Created on 12 Sep 2007
 */
package com.griffinbrown.shail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.griffinbrown.shail.util.CharArray;

public class CharArrayStringHandler implements StringHandler
{
    protected ByteArrayOutputStream eventStream;
    protected char[] strings;
    protected CharArray chars = new CharArray(1000000);
    private Builder builder;

    protected HashMap stringMap = new HashMap();


    //    private static Logger logger = Logger.getLogger( CharArrayStringHandler.class );

    public void addString( String s )
    {
        int tokid = chars.numItems();

        if( tokid == 0 ) //first offset
            eventStream.write( 0 );

        if( ! s.equals( "" ) )
        {
            chars.appendMulti( s.toCharArray() );
        }

        while( tokid != 0 )
        {
            eventStream.write( tokid & 127 );
            tokid >>= 7;
            //            intEvents++;
        }
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
    
    public String getString( int node )
    {
        int offset = consumeNumber( node + 1 );
        return getString( node, offset );
    }


    /**
     * Previously we used an index into a String array -- now we have a char array with offsets recorded in the 
     * Shail stream.
     * @param start - index into the char array at which the String begins
     * @return
     */
    private String getString( int node, int start )
    {
        //        logger.debug( "getString(start=" + start + ")" );

        int end = stringEndPoint( node );
        int len = end - start;

        if( len < 0 )
            throw new RuntimeException( "negative string length: " + len + " start=" + start
                    + " end=" + end );

        //        logger.debug( "getting string [" + start + ":" + end + "]" );// chars="+new String(chars) );
        //        char[] result = new char[ len ];
        //        System.arraycopy( chars, start, result, 0, len );
        //
        //        String s = new String( result );

        //        logger.debug( "returning string '" + s + "'" );
        //        return s;

        return new String( strings, start, len );
    }


    private int stringEndPoint( int nodeIndex )
    {
        //        logger.debug( "stringEndPoint(nodeIndex=" + nodeIndex + ")" );
        int next = strings.length; //default  

        byte b;
        for( int i = nodeIndex + 1; i < builder.getEvents().length; i++ )
        {
            b = builder.getEvents()[ i ];
            if( b == Model.EV_ATTRIBUTE || b == Model.EV_ATTRIBUTE_CHARDATA
                    || b == Model.EV_COMMENT || b == Model.EV_ELEMENT
                    || b == Model.EV_NAMESPACE || b == Model.EV_NAMESPACE_DECL_ATTRIBUTE
                    || b == Model.EV_PI_CHARDATA || b == Model.EV_PROCESSING_INSTRUCTION
                    || b == Model.EV_TEXT )
            {
                next = consumeNumber( i + 1 );
                //                logger.debug( "returning "+next+" from event "+i+" next event type="+getTypeAsString( events[i+1] ) );
                return next;
            }

        }

        return next;
    }


    public int getTotalStringLength()
    {
        return strings.length;
    }


    /**
     * @see com.griffinbrown.shail.StringHandler#setEventStream(java.io.OutputStream)
     */
    public void setEventStream( OutputStream eventStream )
    {
        this.eventStream = ( ByteArrayOutputStream )eventStream;
    }


    /**
     * @see com.griffinbrown.shail.StringHandler#endDocument()
     */
    public void endDocument()
    {
        stringMap = null;
        strings = chars.toCharArray();
        chars = null;
    }


    public void setBuilder( Builder builder )
    {
        this.builder = builder;
    }

}
