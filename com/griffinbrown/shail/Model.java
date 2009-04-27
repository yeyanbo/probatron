package com.griffinbrown.shail;

import java.util.HashMap;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.griffinbrown.shail.util.IntArray;

public class Model
{
//    int tokCount;

    private int cursor = 0;
    private Stack elementContextStack = new Stack();
    private Builder builder;

    public final static byte EV_ELEM_NEXT_SIBLING = ( byte )0x97;
    public final static byte EV_ELEM_NO_NEXT_SIBLING = ( byte )0x96;

    public final static byte EV_LINE_NUMBER_OFFSET = ( byte )0x95;
    public final static byte EV_COLUMN_NUMBER_OFFSET = ( byte )0x94;
    public final static byte EV_COLUMN_NUMBER = ( byte )0x93;
    public final static byte EV_LINE_NUMBER = ( byte )0x92;

    public final static byte EV_NO_PREVIOUS_SIBLING = ( byte )0x91;
    public final static byte EV_PREVIOUS_SIBLING = ( byte )0x90;
    public final static byte EV_PARENT = ( byte )0x8F;
    public final static byte EV_NAMESPACE_DECL_ATTRIBUTE = ( byte )0x8E;
    public final static byte EV_NEXT_SIBLING = ( byte )0x8D;
    public final static byte EV_NO_NEXT_SIBLING = ( byte )0x8C;
    public final static byte EV_NAMESPACE = ( byte )0x8B;
    public final static byte EV_PI_CHARDATA = ( byte )0x8A;
    public final static byte EV_PROCESSING_INSTRUCTION = ( byte )0x89;
    public final static byte EV_TEXT = ( byte )0x88;
    public final static byte EV_DOCUMENT_END = ( byte )0x87;
    public final static byte EV_DOCUMENT = ( byte )0x86;
    public final static byte EV_CDATA_SECTION = ( byte )0x85;
    public final static byte EV_COMMENT = ( byte )0x84;
    public final static byte EV_END_ELEMENT = ( byte )0x83;
    public final static byte EV_ATTRIBUTE_CHARDATA = ( byte )0x82;
    public final static byte EV_ATTRIBUTE = ( byte )0x81;
    public final static byte EV_ELEMENT = ( byte )0x80;

    private static final Logger logger = Logger.getLogger( Model.class );

    private String url;
    int rootIndex;
    private long parseTime;
    private String doctypeSystemId;
    private String doctypePublicId;

    public static long follSibTime;


    public Model()
    {}


    public Model( String url )
    {
        this.url = url;
    }


    public byte getType( int i )
    {
        return builder.getEvents()[ i - rootIndex ];
    }


    /**
     * 
     * @return the local name of the node
     */
    public String getLocalName( int i )
    {
        if( ! ( getType( i ) == Model.EV_ATTRIBUTE || getType( i ) == Model.EV_ELEMENT ) )
            return null;

        String qName = getName( i ); //if this is null, all bets are off
        int colon = qName.lastIndexOf( ':' );
        if( colon > 0 )
        {
            return qName.substring( colon + 1 );
        }

        return qName;
    }


    public int getRoot()
    {
        return rootIndex;
    }


    public String report()
    {
        int eventCount = ( builder.getEvents() != null ? builder.getEvents().length : 0 );
        String s = "=== " + super.toString() + " ===\nNumber of events: " + eventCount;

        s += "\nURL: " + url;
        s += "\nEvent table: " + eventCount / ( long )1024 + " KB";
//        s += "\nDistinct strings: " + tokCount;
        s += "\nString combined length: " + builder.getStringHandler().getTotalStringLength();
        s += "\nParse time: " + this.parseTime + "ms";

        // s += "\nTOTAL: " + consumed / ( long )1024 + " KB";

        return s;
    }


    private int consumeNumber()
    {
        int tokid = 0;

        int j = 0;
        while( true )
        //        while( cursor < builder.getEvents().length )  //SAFER
        {
            tokid |= ( builder.getEvents()[ cursor ] << ( 7 * j ) );

            //            if( cursor+1 < builder.getEvents().length &&  //SAFER
            if( ( builder.getEvents()[ cursor + 1 ] & 0x80 ) != 0x00 )
            {
                break;
            }
            else
            {
                j++;
                cursor++;
            }

        }

        return tokid;

    }


    void pushElementContext( String en )
    {
        elementContextStack.push( en );

    }


    String popElementContext()
    {
        return ( String )elementContextStack.pop();

    }


    String peekElementContext()
    {
        return ( String )elementContextStack.peek();

    }


    public int[] getChildren( int node )
    {
        int context = node - rootIndex;

        byte b = builder.getEvents()[ context ];
        //only elements and document root have children
        if( b != Model.EV_ELEMENT && b != Model.EV_DOCUMENT )
        {
            return new int[ 0 ];
        }

        IntArray a = new IntArray();
        int depth = 0;

        int i = context + 1;
        for( ;; )
        {
            b = builder.getEvents()[ i ];

            if( b == Model.EV_END_ELEMENT )
            {
                if( depth == 0 )
                    break;
                depth--;
            }
            else if( depth == 0
                    && ( b == Model.EV_ELEMENT || b == Model.EV_TEXT || b == Model.EV_COMMENT || b == Model.EV_PROCESSING_INSTRUCTION ) ) //TODO: other types to add here!!
            {
                a.appendItem( i + rootIndex );
                depth += ( b == Model.EV_ELEMENT ? 1 : 0 );
            }
            else if( b == Model.EV_ELEMENT )
            {
                depth++;
            }
            else if( b == Model.EV_DOCUMENT_END )
            {
                break;
            }

            i++;

        }

        return a.toIntArray();

    }


    public int[] getDescendants( int context, boolean includeSelf )
    {
        IntArray descendants = new IntArray();

        if( includeSelf )
            descendants.appendItem( context );

        int[] children = getChildren( context );

        while( children.length > 0 )
        {
            descendants.appendMulti( children );
            children = addChildren( children );
        }

        return descendants.toIntArray();
    }


    public int[] getAncestors( int context, boolean includeSelf )
    {
        IntArray ancestors = new IntArray();

        if( includeSelf )
            ancestors.appendItem( context );

        int parent = getParent( context );

        while( parent != - 1 )
        {
            ancestors.appendItem( parent );
            parent = getParent( parent );
        }

        return ancestors.toIntArray();
    }


    /**
     * Given a list of nodes, returns a list of children of those nodes.
     * @param list
     * @return
     */
    private int[] addChildren( int[] list )
    {
        IntArray children = new IntArray();

        for( int i = 0; i < list.length; i++ )
        {
            children.appendMulti( getChildren( list[ i ] ) );
        }

        return children.toIntArray();
    }


    /**
     * Retrieves the first preceding EV_ELEMENT not balanced by an EV_END_ELEMENT event.
     * @param context
     * @return the int representing the index of the parent node, otherwise -1
     
    public int getParent( int context )
    {
        int i = context - rootIndex - 1; //start at previous event
        int depth = 0;
        byte b;
        while( i >= rootIndex )
        {
            b = builder.getEvents()[ i ];
            if( b == Model.EV_DOCUMENT || ( b == Model.EV_ELEMENT && depth == 0 ) )
            {
                return i;
            }
            else if( b == Model.EV_END_ELEMENT )
            {
                depth--;
            }
            else if( b == Model.EV_ELEMENT )
            {
                depth++;
            }
            i--;
        }
        return - 1;
    }*/

    /**
     * This version of the algorithm searches for parent events whose
     * following event(s) store the index into the event stream of the parent node.
    public int getParent( int node )
    {
        int context = node - rootIndex;

        if( ( byte )builder.getEvents()[ context ] == Model.EV_DOCUMENT )
            return - 1; //root has no parent

        return consumeNumber( nextEventOfType( Model.EV_PARENT, context + 1 ) + 1 );
    }*/

    /**
     * This version of the algorithm searches for parent events whose
     * following event(s) store the offset of the parent node from the context node.
     */
    public int getParent( int node )
    {
        int context = node - rootIndex;

        if( ( byte )builder.getEvents()[ context ] == Model.EV_DOCUMENT )
            return - 1; //root has no parent

        int parentEvent = nextEventOfType( Model.EV_PARENT, context + 1 );

        return parentEvent - consumeNumber( parentEvent + 1 ) + rootIndex;
    }


    private int consumeNumber( int index )
    {
        int tokid = 0;

        int j = 0;
        while( true )
        //        while( index < builder.getEvents().length )   SAFER
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
        return tokid;
    }


    public int[] getPreceding( int context )
    {
        int i = context - rootIndex - 1; //start at previous event

        IntArray result = new IntArray();
        byte b;
        int depth = 0;
        while( i > 0 )
        {
            b = builder.getEvents()[ i ];

            if( b == Model.EV_END_ELEMENT )
            {
                depth--;
            }
            else if( b == Model.EV_ELEMENT )
            {
                if( depth < 0 )
                {
                    result.appendItem( i + rootIndex );
                    depth++;
                }
            }

            if( b == Model.EV_COMMENT || b == Model.EV_PROCESSING_INSTRUCTION
                    || b == Model.EV_TEXT )
            {
                result.appendItem( i + rootIndex );
            }

            i--;
        }

        return result.toIntArray();
    }


    public int[] getPrecedingSiblings( int node )
    {
        //        logger.debug( "getPrecedingSiblings context="+node );

        IntArray prevSibs = new IntArray();

        int prev = getPrecedingSibling( node - rootIndex );

        while( prev != - 1 )
        {
            prevSibs.appendItem( prev + rootIndex ); //N.B. the node returned must have the rootIndex added to it!!
            prev = getPrecedingSibling( prev );
        }

        return prevSibs.toIntArray();
    }


    private int getPrecedingSibling( int context )
    {
        int prevSibEvent = nextPreviousSiblingEvent( context + 1 );
        if( prevSibEvent == - 1 )
            return - 1;

        int offset = consumeNumber( prevSibEvent + 1 );

        return ( offset == - 1 ? offset : prevSibEvent - offset );
    }


    public int getSimilarPrecedingSiblingCount( int node )
    {
        int count = 1;
        String name = getName( node );
        byte type = getType( node );

        int[] siblings = getPrecedingSiblings( node );
        for( int i = 0; i < siblings.length; i++ )
        {
            if( nodeIsSimilar( siblings[ i ], name, type ) )
                count++;
        }

        //omit [1] predicate when no siblings of same type are present
        if( count == 1 && ! hasSimilarFollowingSibling( node ) )
            count = - 1;

        return count;
    }


    private boolean hasSimilarFollowingSibling( int node )
    {
        return true;
        //        String name = getName( node  );
        //        byte type = getType( node );
        //        int sib = getFollowingSibling( node );
        //        while( sib != - 1 )
        //        {
        //            if( nodeIsSimilar( sib, name, type ) )
        //                return true;
        //            sib = getFollowingSibling( sib );
        //        }
        //        return false;
    }


    public int[] getFollowingSiblings( int node )
    {
        //        logger.debug( "getting foll sibs for " + node );
        IntArray follSibs = new IntArray();

        int foll = getFollowingSibling( node - rootIndex );
        //        logger.debug( "foll sib=" + foll );
        while( foll != - 1 )
        {
            follSibs.appendItem( foll + rootIndex );
            foll = getFollowingSibling( foll );
        }

        return follSibs.toIntArray();
    }


    private int getFollowingSibling( int node )
    {
        //        long start = System.currentTimeMillis();
        int context = node; // - rootIndex;

        int nextSibEvent = nextFollowingSiblingEvent( context + 1,
                ( getType( node + rootIndex ) == EV_ELEMENT ) );

        if( nextSibEvent == - 1 )
        {
            //            follSibTime += ( System.currentTimeMillis() - start );
            return nextSibEvent;
        }

        int offset = consumeNumber( nextSibEvent + 1 );

        //        follSibTime += ( System.currentTimeMillis() - start );

        return nextSibEvent + offset;
    }


    private boolean nodeIsSimilar( int node, String name, byte type )
    {
        if( getType( node ) == type )
        {
            //unnamed node
            if( type == EV_COMMENT || type == EV_TEXT )
                return true;
            //named node
            String name2 = getName( node );
            if( ( type == EV_ELEMENT || type == EV_PROCESSING_INSTRUCTION )
                    && name2.equals( name ) ) //named node
            {
                return true;
            }
        }
        return false;
    }


    /**
     * 
     * @param i the node whose string should be returned
     * @return the string of the node passed in
     */
    public String getStringValue( int i )
    {
        assert ( getType( i ) & Model.EV_ELEMENT ) > 0 : "PseudoNode does not correspond to node event";

        int index = i - rootIndex;

        switch( getType( i ) ){
        case Model.EV_ELEMENT:
            return null;

        case Model.EV_PROCESSING_INSTRUCTION:
            int nextPICharEvent = nextEventOfType( Model.EV_PI_CHARDATA, index );
            return builder.getStringHandler().getString( nextPICharEvent );

        case Model.EV_ATTRIBUTE:
            int nextAttrEvent = nextEventOfType( Model.EV_ATTRIBUTE_CHARDATA, index );
            return builder.getStringHandler().getString( nextAttrEvent );

        case Model.EV_TEXT:
            return builder.getStringHandler().getString( index );

        case Model.EV_COMMENT:
            return builder.getStringHandler().getString( index );

            //          TODO: other node types!!            

        default:
            return null;
        }
    }


    /**
     * Retrieves attributes for the element at position <code>context</code>.
     * @return
     */
    public int[] getAttributes( int context )
    {
        context -= rootIndex;
        if( builder.getEvents()[ context ] != Model.EV_ELEMENT )
        {
            //possible Jaxen optimisation here?
            //nodes of ALL types (not just elements) are checked for attributes(!)
            //            return new int[0];    //make this null?? -- see also toIntArray() below
            return null;
        }

        IntArray list = new IntArray();
        byte b;
        for( int i = context + 1; i < builder.getEvents().length; i++ ) //begin at NEXT event
        {
            b = builder.getEvents()[ i ];
            if( b == Model.EV_ATTRIBUTE )
            {
                list.appendItem( i + rootIndex );
            }
            //attr events have ended
            else if( b == Model.EV_COMMENT || b == Model.EV_ELEMENT
                    || b == Model.EV_END_ELEMENT || b == Model.EV_PROCESSING_INSTRUCTION
                    || b == Model.EV_TEXT )
            {
                break;
            }
        }
        return list.toIntArray();
    }


    int[] getNamespaceDeclAtts( int node )
    {
        int context = node - rootIndex;

        if( builder.getEvents()[ context ] != Model.EV_ELEMENT )
        {
            return new int[ 0 ];
        }

        IntArray list = new IntArray();
        byte b;
        for( int i = context + 1; i < builder.getEvents().length; i++ ) //begin at NEXT event
        {
            b = builder.getEvents()[ i ];
            if( b == Model.EV_NAMESPACE_DECL_ATTRIBUTE )
            {
                list.appendItem( i );
            }
            //attr events have ended
            else if( b == Model.EV_COMMENT || b == Model.EV_ELEMENT
                    || b == Model.EV_END_ELEMENT || b == Model.EV_PROCESSING_INSTRUCTION
                    || b == Model.EV_TEXT )
            {
                break;
            }
        }
        return list.toIntArray();
    }


    /**
     * N.B. In this impl, for <strong>named</strong> nodes (i.e. elements, attributes, PIs), the name
     * is presumed to occur as the very next event.  
     * @return the QName of the node
     */
    public String getName( int i )
    {
        int index = i - rootIndex;
        //        assert ( (byte)builder.getEvents()[ index ] & Model.EV_ELEMENT ) > 0 : "PseudoNode does not correspond to node event";

        return builder.getStringHandler().getString( index );
    }


    /**
     * Returns the prefix (if any) for the node at index <code>index</code>
     * @param index the index of the node whose prefix is requested 
     * @return the prefix of the node at index <code>index</code>, or null if none exists
     */
    public String getPrefix( int index )
    {
        byte type = builder.getEvents()[ index ];
        String qName;
        if( type == Model.EV_ATTRIBUTE || type == Model.EV_ELEMENT )
        {
            qName = builder.getStringHandler().getString( index );
            int i = qName.lastIndexOf( ':' );
            if( i > 0 )
            {
                return qName.substring( 0, i );
            }
        }
        else if( type == Model.EV_NAMESPACE_DECL_ATTRIBUTE )
        {
            qName = builder.getStringHandler().getString( index );
            int i = qName.lastIndexOf( ':' );
            if( i > 0 )
            {
                return qName.substring( i + 1 );
            }
            return ""; //TODO: check this!
        }
        return null;
    }


    public String getNamespaceURI( int node )
    {
        int index = node - rootIndex;
        byte type = builder.getEvents()[ index ];
        assert ( type & Model.EV_ELEMENT ) > 0 : "PseudoNode does not correspond to node event";

        if( type == Model.EV_ATTRIBUTE || type == Model.EV_ELEMENT )
        {
            int ns = nextEventOfType( Model.EV_NAMESPACE, index );
            return builder.getStringHandler().getString( ns ).equals( "" ) ? null : builder
                    .getStringHandler().getString( ns );
        }
        else if( type == Model.EV_NAMESPACE_DECL_ATTRIBUTE )
        {
            int nsda = nextEventOfType( Model.EV_ATTRIBUTE_CHARDATA, index );
            return builder.getStringHandler().getString( nsda ).equals( "" ) ? null : builder
                    .getStringHandler().getString( nsda );
        }

        return null;
    }


    private int nextEventOfType( byte type, int start )
    {
        for( int i = start; i < builder.getEvents().length; i++ )
        {
            if( builder.getEvents()[ i ] == type )
            {
                return i;
            }
        }
        return - 1;
    }


    private int nextLineEvent( int start )
    {
        byte event;
        for( int i = start; i < builder.getEvents().length; i++ )
        {
            event = builder.getEvents()[ i ];
            if( event == EV_LINE_NUMBER || event == EV_LINE_NUMBER_OFFSET )
            {
                return i;
            }
        }
        return - 1;
    }


    private int nextColumnEvent( int start )
    {
        byte event;
        for( int i = start; i < builder.getEvents().length; i++ )
        {
            event = builder.getEvents()[ i ];
            if( event == EV_COLUMN_NUMBER || event == EV_COLUMN_NUMBER_OFFSET )
            {
                return i;
            }
        }
        return - 1;
    }


    private int nextPreviousSiblingEvent( int start )
    {
        //        logger.debug( "nextPreviousSiblingEvent(" + start + ")" );
        byte event;
        for( int i = start; i < builder.getEvents().length; i++ )
        {
            event = builder.getEvents()[ i ];
            if( event == EV_NO_PREVIOUS_SIBLING )
            {
                //                logger.debug( "NONE! returning -1" );
                return - 1;
            }
            else if( event == EV_PREVIOUS_SIBLING )
            {
                //                logger.debug( "returning " + i );
                return i;
            }
        }
        //        logger.debug( "returning -1" );
        return - 1;
    }


    private int nextFollowingSiblingEvent( int start, boolean isNodeElement )
    {
        byte event;

        //because foll sib events only appear at the END of elements, 
        //we need to eliminate those belonging to their children
        if( isNodeElement )
        {
            int depth = 0;

            for( int i = start; i < builder.getEvents().length; i++ )
            {
                event = builder.getEvents()[ i ];
                if( event == EV_ELEM_NO_NEXT_SIBLING )
                {
                    if( depth == 0 )
                    {
                        return - 1;
                    }
                    depth--;
                }
                else if( event == EV_ELEM_NEXT_SIBLING )
                {
                    if( depth == 0 )
                    {
                        return i;
                    }
                    depth--;
                }
                else if( event == EV_ELEMENT )
                    depth++;
            }
        }
        else
        {
            for( int i = start; i < builder.getEvents().length; i++ )
            {
                event = builder.getEvents()[ i ];
                if( event == EV_NO_NEXT_SIBLING )
                {
                    return - 1;
                }
                else if( event == EV_NEXT_SIBLING )
                {
                    return i;
                }
            }
        }
        return - 1;
    }


    /**
     * 
     * @return the string of the serialized model
     
    public String serialise()
    {
        StringBuffer sb = new StringBuffer();
        cursor = 0;

        // byte MODE_CHARVAL = 0x01;
        byte MODE_ETAGO = 0x02;

        int mode = 0;
        byte b = 0;
        try
        {
            while( cursor < builder.getEvents().length )
            {

                b = builder.getEvents()[ cursor ];
                switch( b ){
                case Model.EV_ELEMENT:
                    if( ( mode & MODE_ETAGO ) != 0 )
                    {
                        sb.append( ">" );
                        mode &= ~ MODE_ETAGO;

                    }
                    sb.append( "<" );
                    mode |= MODE_ETAGO;
                    cursor++;
    //                    int tokid = consumeNumber();

                    //                    String elementName = tokens[ tokid ];
                    String elementName = builder.getStringHandler().getString( cursor );
                    pushElementContext( elementName );
                    sb.append( elementName );
                    logger.debug( elementName );
                    cursor++;
                    break;
                case Model.EV_TEXT:

                    if( ( mode & MODE_ETAGO ) != 0 )
                    {
                        sb.append( ">" );
                        mode &= ~ MODE_ETAGO;

                    }
                    cursor++;
    //                    tokid = consumeNumber();
                    sb.append( builder.getStringHandler().getString( cursor ) );
                    cursor++;
                    break;
                case Model.EV_END_ELEMENT:
                    if( ( mode & MODE_ETAGO ) != 0 )
                    {
                        sb.append( "/>" );
                        mode &= ~ MODE_ETAGO;
                    }
                    else
                    {
                        sb.append( "</" + peekElementContext() + ">" );
                    }
                    popElementContext();
                    cursor++;
                    break;

                case Model.EV_ATTRIBUTE:
                    sb.append( " " );
                    cursor++;
    //                    tokid = consumeNumber();
                    sb.append( builder.getStringHandler().getString( cursor ) );
                    cursor += 2;
    //                    tokid = consumeNumber();
                    //                    sb.append( "='" + tokens[ tokid ] + "'" );
                    sb.append( "='" + builder.getStringHandler().getString( cursor ) + "'" );
                    cursor++;
                    break;

                case Model.EV_COMMENT:
                    sb.append( "<!--" );
                    cursor++;
    //                    tokid = consumeNumber();
                    //                    sb.append( tokens[ tokid ] );
                    sb.append( builder.getStringHandler().getString( cursor ) );
                    cursor++;
                    sb.append( "-->" );
                    break;

                case Model.EV_PROCESSING_INSTRUCTION:
                    sb.append( "<?" );
                    cursor++;
    //                    tokid = consumeNumber();
                    //                    sb.append( tokens[ tokid ] );
                    sb.append( builder.getStringHandler().getString( cursor ) );
                    sb.append( " " );
                    cursor++;
                    break;

                case Model.EV_PI_CHARDATA:
                    cursor++;
    //                    tokid = consumeNumber();
                    //                    sb.append( tokens[ tokid ] );
                    sb.append( builder.getStringHandler().getString( cursor ) );
                    sb.append( "?>" );
                    cursor++;
                    break;

                case Model.EV_NAMESPACE:
                    cursor++;
    //                    tokid = consumeNumber();
                    //                    String ns = tokens[ tokid ];
                    String ns = builder.getStringHandler().getString( cursor );
                    if( ! ns.equals( "" ) )
                        sb.append( " xmlns='" + ns + "'" );
                    cursor++;
                    break;

                default:
                    cursor++;
                }
            }

        }
        catch( IndexOutOfBoundsException iob )
        {
            sb.append( "*** ERROR: IndexOutOfBoundsException: " + iob.getMessage() );
            iob.printStackTrace();

        }

        return sb.toString();

    }*/

    public String toString( int index )
    {
        String augText = null;
        byte eventType = getType( index );
        String type = getTypeAsString( eventType );

        switch( eventType ){
        case Model.EV_ELEMENT:
            type = "elem";
            augText = getName( index );
            if( getNamespaceURI( index ) != null )
                augText += " ns=" + getNamespaceURI( index );
            break;
        case Model.EV_DOCUMENT:
            type = "doc";
            augText += " url=" + this.url;
            break;
        case Model.EV_ATTRIBUTE:
            type = "att";
            augText = getName( index );
            if( getNamespaceURI( index ) != null )
                augText += " ns=" + getNamespaceURI( index );
            break;
        case Model.EV_TEXT:
            type = "text";
            augText = " value='" + getStringValue( index ) + "'";
            break;
        case Model.EV_COMMENT:
            type = "comm";
            break;
        case Model.EV_PROCESSING_INSTRUCTION:
            type = "pi";
            augText = getName( index ) + " data=" + getStringValue( index );
            break;
        case Model.EV_NAMESPACE:
            type = "ns";
            break;
        default:
            break;
        }

        String s = "<" + type + " idx=" + index;
        if( eventType == Model.EV_TEXT )
            s += augText;
        else if( augText != null )
            s += " name=" + augText;
        s += " hash=" + index + ">"; //" model=" + this + ">";

        return s;
    }


    /**
     * Sets the index at which the root of this document is located.
     * 
     * In fact, the implementation of events is still 0-based, but this is provided for cases
     * where more than one document is built using the same navigator. Under these circumstances,
     * the ModelRegistry is used to discover to which Model (document) a node belongs, since 
     * nodes are now int, and therefore cannot know of what document they are a part. 
     * @param i
     */
    void setRootIndex( int i )
    {
        this.rootIndex = i;
    }


    /**
     * Returns the system id of the document represented.
     * @return sys id of the document
     */
    public String getSystemId()
    {
        return url;
    }


    public final void debugEvents()
    {
        StringBuffer s = new StringBuffer( this + " sys id=" + getSystemId() + " events=[" );

        cursor = 0;
        while( cursor < builder.getEvents().length )
        {
            s.append( cursor + " " );
            byte b = builder.getEvents()[ cursor ];

            switch( b ){
            case EV_DOCUMENT:
                s.append( "EV_DOCUMENT" );

                cursor++;
                break;
            case EV_DOCUMENT_END:
                s.append( "EV_END_DOCUMENT" );

                cursor++;
                break;
            case EV_ATTRIBUTE:
                s.append( "EV_ATTRIBUTE" );

                cursor++;
                break;
            case EV_ATTRIBUTE_CHARDATA:
                s.append( "EV_ATTRIBUTE_CHARDATA" );

                cursor++;
                break;
            case EV_ELEMENT:
                s.append( "EV_ELEMENT" );

                cursor++;
                break;
            case EV_END_ELEMENT:
                s.append( "EV_END_ELEMENT" );

                cursor++;
                break;
            case EV_NAMESPACE:
                s.append( "EV_NAMESPACE" );

                cursor++;
                break;
            case EV_TEXT:
                s.append( "EV_TEXT" );

                cursor++;
                break;
            case EV_COMMENT:
                s.append( "EV_COMMENT" );

                cursor++;
                break;
            case EV_NAMESPACE_DECL_ATTRIBUTE:
                s.append( "EV_NAMESPACE_DECL_ATTRIBUTE" );

                cursor++;
                break;
            case EV_PI_CHARDATA:
                s.append( "EV_PI_CHARDATA" );

                cursor++;
                break;
            case EV_PROCESSING_INSTRUCTION:
                s.append( "EV_PROCESSING_INSTRUCTION" );

                cursor++;
                break;
            case EV_PARENT:
                s.append( "EV_PARENT" );
                cursor++;
                break;
            case EV_PREVIOUS_SIBLING:
                s.append( "EV_PREVIOUS_SIBLING" );
                cursor++;
                break;
            case EV_NO_PREVIOUS_SIBLING:
                s.append( "EV_NO_PREVIOUS_SIBLING" );
                cursor++;
                break;
            case EV_NEXT_SIBLING:
                s.append( "EV_NEXT_SIBLING" );
                cursor++;
                break;
            case EV_NO_NEXT_SIBLING:
                s.append( "EV_NO_NEXT_SIBLING" );
                cursor++;
                break;
            case EV_COLUMN_NUMBER:
                s.append( "EV_COLUMN_NUMBER" );
                cursor++;
                break;
            case EV_COLUMN_NUMBER_OFFSET:
                s.append( "EV_COLUMN_NUMBER_OFFSET" );
                cursor++;
                break;
            case EV_LINE_NUMBER:
                s.append( "EV_LINE_NUMBER" );
                cursor++;
                break;
            case EV_LINE_NUMBER_OFFSET:
                s.append( "EV_LINE_NUMBER_OFFSET" );
                cursor++;
                break;
            case EV_ELEM_NEXT_SIBLING:
                s.append( "EV_ELEM_NEXT_SIBLING" );
                cursor++;
                break;
            case EV_ELEM_NO_NEXT_SIBLING:
                s.append( "EV_ELEM_NO_NEXT_SIBLING" );
                cursor++;
                break;
            default:
                if( ( b & 127 ) > 0 ) //non-node event
                {
                    byte lastEvent = builder.getEvents()[ cursor - 1 ];

                    int start = consumeNumber();

                    if( lastEvent == Model.EV_PARENT || lastEvent == Model.EV_PREVIOUS_SIBLING
                            || lastEvent == EV_COLUMN_NUMBER_OFFSET
                            || lastEvent == EV_LINE_NUMBER_OFFSET
                            || lastEvent == EV_NEXT_SIBLING
                            || lastEvent == EV_ELEM_NEXT_SIBLING
                            || lastEvent == EV_ELEM_NO_NEXT_SIBLING )
                    {
                        s.append( "offset=" + consumeNumber( cursor ) );
                    }
                    else if( lastEvent == EV_LINE_NUMBER || lastEvent == EV_COLUMN_NUMBER )
                    {
                        s.append( "value=" + consumeNumber( cursor ) );
                    }
                    else
                    {
                        //                        s.append( "string [idx=" + start + "]='" + consumeNumber( cursor - 1 ) );
                        s.append( "string [idx=" + start + "]='"
                                + builder.getStringHandler().getString( cursor - 1 ) + "'" );
                    }
                    cursor++;
                    break;
                }
                else
                {
                    cursor++;
                    continue;
                }

            }

            if( cursor < builder.getEvents().length )
                s.append( ",\n" );
        }
        s.append( "]" );
        logger.debug( s );
        cursor = 0;
    }


    private String getTypeAsString( byte b )
    {
        switch( b ){
        case EV_ATTRIBUTE:
            return "EV_ATTRIBUTE";
        case EV_ATTRIBUTE_CHARDATA:
            return "EV_ATTRIBUTE_CHARDATA";
        case EV_COMMENT:
            return "EV_COMMENT";
        case EV_DOCUMENT:
            return "EV_DOCUMENT";
        case EV_DOCUMENT_END:
            return "EV_DOCUMENT_END";
        case EV_ELEMENT:
            return "EV_ELEMENT";
        case EV_END_ELEMENT:
            return "EV_END_ELEMENT";
        case EV_NAMESPACE:
            return "EV_NAMESPACE";
        case EV_NAMESPACE_DECL_ATTRIBUTE:
            return "EV_NAMESPACE_DECL_ATTRIBUTE";
        case EV_PI_CHARDATA:
            return "EV_PI_CHARDATA";
        case EV_PROCESSING_INSTRUCTION:
            return "EV_PROCESSING_INSTRUCTION";
        case EV_TEXT:
            return "EV_TEXT";

            //remainder only useful for debugging - these are event types, not node types
        case EV_COLUMN_NUMBER:
            return "EV_COLUMN_NUMBER";
        case EV_COLUMN_NUMBER_OFFSET:
            return "EV_COLUMN_NUMBER_OFFSET";
        case EV_ELEM_NEXT_SIBLING:
            return "EV_ELEM_NEXT_SIBLING";
        case EV_ELEM_NO_NEXT_SIBLING:
            return "EV_ELEM_NO_NEXT_SIBLING";
        case EV_LINE_NUMBER:
            return "EV_LINE_NUMBER";
        case EV_LINE_NUMBER_OFFSET:
            return "EV_LINE_NUMBER_OFFSET";
        case EV_NEXT_SIBLING:
            return "EV_NEXT_SIBLING";
        case EV_NO_NEXT_SIBLING:
            return "EV_NO_NEXT_SIBLING";
        case EV_NO_PREVIOUS_SIBLING:
            return "EV_NO_PREVIOUS_SIBLING";
        case EV_PARENT:
            return "EV_PARENT";

        default:
            break;
        }

        return null;
    }


    public void setBuilder( Builder builder )
    {
        this.builder = builder;
    }


    Builder getBuilder()
    {
        return this.builder;
    }


    public void setParseTime( long t )
    {
        this.parseTime = t;
    }


    public int getLineNumber( int i )
    {
        int context = i - rootIndex;

        int lineEvent = nextLineEvent( context + 1 );

        if( lineEvent == - 1 )
            return lineEvent;

        byte b = builder.getEvents()[ lineEvent ];
        int actualOrOffset = consumeNumber( lineEvent + 1 );

        return ( b == EV_LINE_NUMBER ) ? actualOrOffset : consumeNumber( lineEvent
                - actualOrOffset + 1 );
    }


    public int getColumnNumber( int i )
    {
        int context = i - rootIndex;

        int event = nextColumnEvent( context + 1 );

        if( event == - 1 )
            return event;

        byte b = builder.getEvents()[ event ];
        int actualOrOffset = consumeNumber( event + 1 ); //depending on the type of event, this is either the actual col no, or an offset indicating the previous col no event

        return ( b == EV_COLUMN_NUMBER ) ? actualOrOffset : consumeNumber( event
                - actualOrOffset + 1 );
    }


    public void setSystemId( String url )
    {
        this.url = url;
    }


    public String toString()
    {
        return "<" + getClass().getName() + " root=" + rootIndex + " sysId=" + url + " events="
                + ( builder.getEvents() == null ? 0 : builder.getEvents().length )
                + " hashcode=" + hashCode() + ">";
    }


    public String getDoctypeSystemId()
    {
        return this.doctypeSystemId;
    }


    public String getDoctypePublicId()
    {
        return this.doctypePublicId;
    }


    //TODO: implement xml:id
    public int getElementById( String id )
    {
        if( builder.getIdMap().containsKey( id ) )
            return ( ( Integer )builder.getIdMap().get( id ) ).intValue();

        return - 1;
    }


    public void setDoctypeSystemId( String systemId )
    {
        this.doctypeSystemId = systemId;
    }


    public void setDoctypePublicId( String publicId )
    {
        this.doctypePublicId = publicId;
    }


    /**
     * Accesses XPath namespace nodes in scope for the element passed in. 
     * @param context element whose namespace nodes are required
     * @return namespace nodes in scope for the element
     */
    public int[] getNamespaces( int context )
    {
        /* XPath 1.0, s5.4:
         * "...an element will have a namespace node:
         * 
         * - for every attribute on the element whose name starts with xmlns:;
         * - for every attribute on an ancestor element whose name starts xmlns: unless the element
         *   itself or a nearer ancestor redeclares the prefix;
         * - for an xmlns attribute, if the element or some ancestor has an xmlns attribute, and 
         *   the value of the xmlns attribute for the nearest such element is non-empty"
         */

        IntArray namespaces = new IntArray();
        int[] ancestorOrSelf = getAncestors( context, true );

        HashMap prefixes = new HashMap();

        for( int i = 0; i < ancestorOrSelf.length; i++ )
        {
            int elem = ancestorOrSelf[ i ];
            logger.debug( "elem=" + toString( elem ) );
            int[] atts = getNamespaceDeclAtts( elem );

            for( int j = 0; j < atts.length; j++ )
            {
                int att = atts[ j ];
                String prefix = getPrefix( att );
                logger.debug( "att prefix=" + prefix );
                if( ! prefixes.containsKey( prefix ) )
                {
                    prefixes.put( prefix, null );
                    namespaces.appendItem( att );
                }
            }
        }

        //xml prefix must be present too
        namespaces.appendItem( getXMLNamespace() );

        logger.debug( "returning nss=" + namespaces );

        return namespaces.toIntArray();
    }


    /**
     * Hacky: gets the XML namespace and URI, events for which are appended
     * to the document root.
     * 
     * TODO: the parent for this node!!
     *  
     * @return the XML namespace node
     */
    private int getXMLNamespace()
    {
        byte b;

        for( int i = 0; i < builder.getEvents().length; i++ ) //always start at 0 (the document event)
        {
            b = builder.getEvents()[ i ];
            if( b == Model.EV_NAMESPACE_DECL_ATTRIBUTE )
            {
                return i;
            }
        }
        throw new RuntimeException( "XML namespace not found" ); //sanity check
    }


    public boolean containsCDATASection( int node )
    {
        if( getType( node ) != Model.EV_TEXT )
            return false;
        
        int context = node - rootIndex;
        int i = context + 1;
        byte b;
        for( ;; )
        {
            b = builder.getEvents()[ i ];

            if( b == Model.EV_CDATA_SECTION )
                return true;
            else if( b == Model.EV_END_ELEMENT || b == Model.EV_ELEMENT
                    || b == Model.EV_COMMENT || b == Model.EV_PROCESSING_INSTRUCTION )
                return false;

            i++;

        }
    }
}
