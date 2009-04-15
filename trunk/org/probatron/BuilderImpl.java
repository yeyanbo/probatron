/*
 * Copyright 2009 Griffin Brown Digital Publishing Ltd
 * All rights reserved.
 *
 * This file is part of Probatron.
 *
 * Probatron is free software: you can redistribute it and/or modify
 * it under the terms of the Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Probatron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero General Public License for more details.
 *
 * You should have received a copy of the Affero General Public License
 * along with Probatron.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.probatron;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import com.griffinbrown.shail.Builder;
import com.griffinbrown.shail.HashingStringHandler;
import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.shail.StringHandler;
import com.griffinbrown.shail.util.IntArray;
import com.griffinbrown.xmltool.Extension;
import com.griffinbrown.xmltool.FeatureValuePair;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.ParseMessage;
import com.griffinbrown.xmltool.Session;

public class BuilderImpl extends Extension implements Builder
{
    private ByteArrayOutputStream eventStream;
    private ByteArrayOutputStream shelf;
    private Model model;
    private StringBuffer text;
    private boolean inDTD;
    private boolean textNodeContainsCDATA;
    private byte[] events;

    //logger
    public static final Logger logger = Logger.getLogger( BuilderImpl.class );

    private Session session;
    private ParseEventForwarder parseEventForwarder;
    private StringHandler stringHandler;
    private XMLReader xmlReader;
    private long start;

    private IntArray ancestors;
    private IntArray siblings;
    private byte prevEvent;
    private HashMap idMap;

    //locator-specific stuff
    private Locator locator;
    private int lastLineNumber;
    private int lastColumnNumber;
    private int lastLineNumberIndex;
    private int lastColumnNumberIndex;

    protected static final String FEATURE_INCLUDE_PHYSICAL_LOCATORS = "include-physical-locators";
    private static final String XML_NS = "http://www.w3.org/XML/1998/namespace";
    private static final String XML_PREFIX = "xmlns:xml";
    private boolean includePhysicalLocators = true;
    private Locator textLoc;
    private int currentNode = - 1;


    /**
     * Constructor for use within XMLProbe.
     * @param instance
     * @param session
     */
    public BuilderImpl( Instance instance, Session session )
    {
        super( instance, session );
        this.session = session;
        model = new Model( instance.getResolvedURI() );
        model.setBuilder( this );
        ModelRegistry.register( model );
        idMap = new HashMap( 512 );
    }


    /**
     * Constructor for general use.
     * N.B. <strong>Not</strong> intended for use within XMLProbe (use @see {@link BuilderImpl#Builder(Instance, Session)} instead.)
     * Constructs a Shail builder with default options.
     *  
     * @throws SAXException
     */
    public BuilderImpl() throws SAXException
    {
        model = new Model();
        model.setBuilder( this );
        ModelRegistry.register( model );
        parseEventForwarder = new ParseEventForwarder( this );
        idMap = new HashMap( 512 );

        //create and configure the parser
        xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler( getParseEventForwarder() );
        xmlReader.setProperty( "http://xml.org/sax/properties/lexical-handler",
                getParseEventForwarder() );
        xmlReader.setFeature( "http://xml.org/sax/features/namespace-prefixes", true );
        xmlReader.setFeature( "http://xml.org/sax/features/namespaces", true );
    }


    protected Model getModel()
    {
        return this.model;
    }


    protected void flushContentBuffer()
    {
        if( this.text.length() > 0 )
        {
            setFollowingSibling( Model.EV_TEXT );

            int context = eventStream.size() + shelf.size();

            shelve( Model.EV_TEXT, text.toString() );
            if( includePhysicalLocators )
            {
                addTextNodeLocatorEvents();
                this.textLoc = null;
            }

            if( textNodeContainsCDATA )
            {
                //add flag to stream
                shelve( Model.EV_CDATA_SECTION );
                textNodeContainsCDATA = false; //reset flag
            }

            setParent( ancestors.peek() );

            setPreviousSibling( context );

            prevEvent = Model.EV_TEXT;

            this.text = new StringBuffer();
        }
    }


    protected final void resetContentBuffer()
    {
        this.text = new StringBuffer();
    }


    private void resetLocators()
    {
        this.lastColumnNumber = - 1;
        this.lastLineNumber = - 1;
        this.lastColumnNumberIndex = - 1;
        this.lastLineNumberIndex = - 1;
    }


    protected final void reset()
    {
        resetContentBuffer();
        resetLocators();
        siblings = new IntArray();
        ancestors = new IntArray();
        this.eventStream = new ByteArrayOutputStream();
        this.shelf = new ByteArrayOutputStream();

        setStringHandler( new HashingStringHandler() );
        stringHandler.setEventStream( shelf );

        siblings.appendItem( - 1 ); //for prev sib of first node (to none)
        int context = eventStream.size();
        shelve( Model.EV_DOCUMENT, "" ); //the name of the root

        //XML namespace - EXPERIMENTAL!
        shelve( Model.EV_NAMESPACE_DECL_ATTRIBUTE );
        stringHandler.addString( XML_PREFIX );
        shelve( Model.EV_ATTRIBUTE_CHARDATA );
        stringHandler.addString( XML_NS );
        //ENDS

        ancestors.appendItem( context ); //set first parent node

        if( includePhysicalLocators )
            addLocatorEvents();

        currentNode = context;
        prevEvent = Model.EV_DOCUMENT;
    }


    public void characters( char[] ch, int start, int length )
    {
        this.text.append( ch, start, length );
    }


    public void endElement( String uri, String localName, String qName )
    {
        flushContentBuffer();
        if( includePhysicalLocators )
        {
            addLocatorEvents();
            setTextLocator();
        }

        //pop siblings until current parent is reached
        int parent = ancestors.peek();
        int sib = siblings.peek();
        while( true )
        {
            sib = siblings.peek();
            if( sib > parent )
            {
                siblings.pop();
            }
            else
                break;
        }

        currentNode = ancestors.pop();

        setFollowingSibling( Model.EV_END_ELEMENT );
        shelve( Model.EV_END_ELEMENT );
        prevEvent = Model.EV_END_ELEMENT;
    }


    public void endPrefixMapping( String arg0 )
    {}


    public void ignorableWhitespace( char[] arg0, int arg1, int arg2 )
    {}


    public void processingInstruction( String target, String data )
    {
        this.flushContentBuffer();

        if( ! inDTD )
        {
            setFollowingSibling( Model.EV_PROCESSING_INSTRUCTION );
            int context = eventStream.size() + shelf.size();
            shelve( Model.EV_PROCESSING_INSTRUCTION, target );
            shelve( Model.EV_PI_CHARDATA, data );
            setParent( ancestors.peek() );
            setPreviousSibling( context );

            if( includePhysicalLocators )
            {
                addLocatorEvents();
                setTextLocator();
            }

            prevEvent = Model.EV_PROCESSING_INSTRUCTION;
        }
    }


    public void setDocumentLocator( Locator locator )
    {
        this.locator = locator;
    }


    public void skippedEntity( String arg0 )
    {}


    public void startDocument()
    {
        reset();
    }


    public void startElement( java.lang.String uri, java.lang.String localName,
            java.lang.String qName, Attributes atts )
    {
        this.flushContentBuffer();

        setFollowingSibling( Model.EV_ELEMENT );
        int thisElement = eventStream.size() + shelf.size(); //this element node

        shelve( Model.EV_ELEMENT, qName );

        int parent = ancestors.peek();
        ancestors.appendItem( thisElement );

        setParent( parent );
        setPreviousSibling( thisElement );
        prevEvent = Model.EV_ELEMENT;

        //namespace URI
        shelve( Model.EV_NAMESPACE );
        stringHandler.addString( uri );

        if( includePhysicalLocators )
        {
            addLocatorEvents();
            setTextLocator();
        }

        for( int i = 0; i < atts.getLength(); i++ )
        {
            String attName = atts.getQName( i );

            //*****CHECK THIS!*****
            //namespace declaration atts (those starting xmlns) 
            //are added, so that namespace nodes may be constructed as needed by Jaxen
            if( attName.equals( "xmlns" ) || attName.startsWith( "xmlns:" ) )
            {
                //name
                shelve( Model.EV_NAMESPACE_DECL_ATTRIBUTE );
                stringHandler.addString( attName );

                //value
                shelve( Model.EV_ATTRIBUTE_CHARDATA );
                stringHandler.addString( atts.getValue( i ) );
            }
            else
            {
                shelve( Model.EV_ATTRIBUTE );
                stringHandler.addString( attName );

                shelve( Model.EV_ATTRIBUTE_CHARDATA );
                String attValue = atts.getValue( i );
                stringHandler.addString( attValue );

                shelve( Model.EV_NAMESPACE );
                String nsURI = atts.getURI( i );
                stringHandler.addString( nsURI );
            }

            //id atts
            if( atts.getType( i ).equals( "ID" ) )
            {
                idMap.put( atts.getValue( i ), new Integer( thisElement ) );
            }

            if( includePhysicalLocators )
                addLocatorEvents();

            setParent( thisElement );
        }

        currentNode = thisElement;

    }


    public void startPrefixMapping( String arg0, String arg1 )
    {}


    public void endDocument()
    {
        this.flushContentBuffer();
        if( includePhysicalLocators )
            addLocatorEvents();
        shelve( Model.EV_DOCUMENT_END );
        unshelve();
        stringHandler.endDocument();
        this.events = eventStream.toByteArray();
        eventStream.reset();
    }


    public byte[] getEvents()
    {
        return this.events;
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment( char[] ch, int start, int length )
    {
        this.flushContentBuffer();

        if( ! inDTD )
        {
            setFollowingSibling( Model.EV_COMMENT );
            int context = eventStream.size() + shelf.size();
            shelve( Model.EV_COMMENT, new String( ch, start, length ) );
            setParent( ancestors.peek() );
            setPreviousSibling( context );
            if( includePhysicalLocators )
            {
                addLocatorEvents();
                setTextLocator();
            }
            prevEvent = Model.EV_COMMENT;
        }
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA()
    {}


    /**
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD()
    {
        inDTD = false;
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity( String arg0 )
    {}


    /**
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA()
    {
        textNodeContainsCDATA = true;
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD( String name, String publicId, String systemId )
    {
        inDTD = true;
        model.setDoctypeSystemId( systemId );
        model.setDoctypePublicId( publicId );
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity( String arg0 )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void attributeDecl( String name, String name2, String type, String valueDefault,
            String value )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl( String name, String model )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#error(org.xml.sax.SAXParseException)
     */
    public void error( SAXParseException exception )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void externalEntityDecl( String name, String publicId, String systemId )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError( SAXParseException exception )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#getSession()
     */
    public Session getSession()
    {
        return session;
    }


    /**
     * @see com.griffinbrown.xmltool.Extension#internalEntityDecl(java.lang.String, java.lang.String)
     */
    public void internalEntityDecl( String name, String value )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#notationDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void notationDecl( String name, String publicId, String systemId )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#postParse()
     */
    public void postParse()
    {
        //                        model.debugEvents();
        this.model.setParseTime( System.currentTimeMillis() - start );
    }


    /**
     * @see com.griffinbrown.xmltool.Extension#preParse()
     */
    public void preParse()
    {
        // TODO: make the Builder re-usable(?) -- see TreeBuilder#preParse()   
        start = System.currentTimeMillis();
    }


    /**
     * Returns a document by parsing the URL passed in.
     * Note that this is for Shail-specific use and is not called 
     * when a Builder is used under XMLProbe.   
     * @param url the URL to parse
     * @return
     * @throws SAXException
     * @throws IOException
     */
    public Model parse( String url ) throws SAXException, IOException
    {
        this.xmlReader.parse( url );
        model.setSystemId( url );

        //                if( logger.isDebugEnabled() )
        //                    model.debugEvents();

        return this.model;
    }


    /**
     * @see com.griffinbrown.xmltool.Extension#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity( String publicId, String systemId )
    {
        return null;
    }


    /**
     * @see com.griffinbrown.xmltool.Extension#setFeature(java.lang.String, List)
     */
    public void setFeature( String uri, List featureValuePairs )
    {
        if( uri.equals( FEATURE_INCLUDE_PHYSICAL_LOCATORS ) )
        {
            Iterator iter = featureValuePairs.iterator();
            while( iter.hasNext() )
            {
                FeatureValuePair fvp = ( FeatureValuePair )iter.next();
                String name = fvp.getName();
                String value = fvp.getValue();

                if( name.equals( "value" ) && value.equals( "false" ) )
                    includePhysicalLocators = false;
            }
        }
    }


    /**
     * @see com.griffinbrown.xmltool.Extension#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void unparsedEntityDecl( String name, String publicId, String systemId,
            String notationName )
    {}


    /**
     * @see com.griffinbrown.xmltool.Extension#warning(org.xml.sax.SAXParseException)
     */
    public void warning( SAXParseException exception )
    {}


    /**
     * @return the parseEventForwarder
     */
    public ParseEventForwarder getParseEventForwarder()
    {
        return parseEventForwarder;
    }


    public void setStringHandler( StringHandler sh )
    {
        this.stringHandler = sh;
        stringHandler.setBuilder( this );
    }


    public StringHandler getStringHandler()
    {
        return this.stringHandler;
    }


    /**
     * Stores the offset of an event from the current event.
     * 
     * Performance gains are marginal in terms of memory use and execution time, 
     * except that writing node indexes instead usually means writing more events 
     * and therefore slower parse time.
     */
    private void addOffsetEvent( byte event, int context )
    {
        int offset = eventStream.size() + shelf.size() - context;
        shelve( event, offset );
    }


    private void writeIntToEventStream( int i )
    {
        while( i != 0 )
        {
            shelve( ( byte )( i & 127 ) );
            i >>= 7;
        }
    }


    private void setParent( int i )
    {
        addOffsetEvent( Model.EV_PARENT, i );
    }


    private int getPreviousSibling()
    {
        if( prevEvent == Model.EV_ELEMENT ) //start element
        {
            return - 1; //none
        }
        else
        {
            return siblings.peek();
        }
    }


    private void setPreviousSibling( int context )
    {
        int prev = getPreviousSibling();

        //        System.err.println("setting prev sib to "+prev);

        if( prev == - 1 )
            shelve( Model.EV_NO_PREVIOUS_SIBLING );
        else
            addOffsetEvent( Model.EV_PREVIOUS_SIBLING, prev );

        siblings.appendItem( context );
    }


    private void addLocatorEvents()
    {
        int col = locator.getColumnNumber();

        if( col == lastColumnNumber )
        {
            int offset = eventStream.size() + shelf.size() - lastColumnNumberIndex;
            shelve( Model.EV_COLUMN_NUMBER_OFFSET, offset );
        }
        else
        {
            lastColumnNumberIndex = eventStream.size() + shelf.size();
            shelve( Model.EV_COLUMN_NUMBER, col );
            lastColumnNumber = col;
        }

        int line = locator.getLineNumber();

        if( line == lastLineNumber )
        {
            int offset = eventStream.size() + shelf.size() - lastLineNumberIndex;
            shelve( Model.EV_LINE_NUMBER_OFFSET, offset );
        }
        else
        {
            lastLineNumberIndex = eventStream.size() + shelf.size();
            shelve( Model.EV_LINE_NUMBER, line );
            lastLineNumber = line;
        }
    }


    private void addTextNodeLocatorEvents()
    {
        int col = textLoc.getColumnNumber();

        if( col == lastColumnNumber )
        {
            int offset = eventStream.size() + shelf.size() - lastColumnNumberIndex;
            shelve( Model.EV_COLUMN_NUMBER_OFFSET, offset );
        }
        else
        {
            lastColumnNumberIndex = eventStream.size() + shelf.size();
            shelve( Model.EV_COLUMN_NUMBER, col );
            lastColumnNumber = col;
        }

        int line = textLoc.getLineNumber();

        if( line == lastLineNumber )
        {
            int offset = eventStream.size() + shelf.size() - lastLineNumberIndex;
            shelve( Model.EV_LINE_NUMBER_OFFSET, offset );
        }
        else
        {
            lastLineNumberIndex = eventStream.size() + shelf.size();
            shelve( Model.EV_LINE_NUMBER, line );
            lastLineNumber = line;
        }
    }


    private void shelve( byte b )
    {
        shelf.write( b );
    }


    private void shelve( byte b, int i )
    {
        shelve( b );
        writeIntToEventStream( i );
    }


    private void shelve( byte b, String s )
    {
        shelve( b );
        stringHandler.addString( s );
    }


    /**
     * Place all shelved events on the main event stream and reset the shelf.
     * This method should only be called once we are sure that following sibling
     * events have been reconciled for all shelved nodes. 
     */
    private void unshelve()
    {
        try
        {
            eventStream.write( shelf.toByteArray() );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        shelf.reset();
    }


    private void setFollowingSibling( byte event )
    {
        //1. text, comm, pi, st_elem
        if( event == Model.EV_TEXT || event == Model.EV_COMMENT
                || event == Model.EV_PROCESSING_INSTRUCTION || event == Model.EV_ELEMENT )
        {
            //preceded by end_elem
            if( prevEvent == Model.EV_END_ELEMENT )
            {
                eventStream.write( Model.EV_ELEM_NEXT_SIBLING );
                int offset = 3;
                while( offset != 0 )
                {
                    eventStream.write( offset & 127 );
                    offset >>= 7;
                }
                unshelve();
            }
            //preceded by: text, comm, pi
            else if( prevEvent == Model.EV_TEXT || prevEvent == Model.EV_COMMENT
                    || prevEvent == Model.EV_PROCESSING_INSTRUCTION )
            {
                unshelve();
                eventStream.write( Model.EV_NEXT_SIBLING );
                int offset = 2;
                while( offset != 0 )
                {
                    eventStream.write( offset & 127 );
                    offset >>= 7;
                }
            }
        }
        //2. end_elem
        else if( event == Model.EV_END_ELEMENT )
        {
            //preceded by: text, comm, pi
            if( prevEvent == Model.EV_TEXT || prevEvent == Model.EV_COMMENT
                    || prevEvent == Model.EV_PROCESSING_INSTRUCTION )
            {
                unshelve();
                eventStream.write( Model.EV_NO_NEXT_SIBLING );
            }
            //preceded by st_elem
            else if( prevEvent == Model.EV_ELEMENT )
            {
                unshelve();
            }
        }
        //3. st_elem preceded by st_elem
        else if( event == Model.EV_ELEMENT && prevEvent == Model.EV_ELEMENT )
        {
            unshelve();
        }
        //4. end_elem preceded by end_elem
        else if( event == Model.EV_END_ELEMENT && prevEvent == Model.EV_END_ELEMENT )
        {
            unshelve();
            eventStream.write( Model.EV_ELEM_NO_NEXT_SIBLING );
        }
        else
            unshelve();

    }


    /**
     * @see com.griffinbrown.xmltool.Extension#parseMessage(com.griffinbrown.xmltool.ParseMessage)
     */
    public void parseMessage( ParseMessage m )
    {}


    /**
     * 
     * @return the current node <strong>plus any offset for the root node</strong>
     */
    public int getCurrentNode()
    {
        return currentNode + model.getRoot();
    }


    public HashMap getIdMap()
    {
        return this.idMap;
    }


    protected void setTextLocator()
    {
        textLoc = new LocatorImpl( locator ); //keep a copy of it
    }


    protected boolean isInDTD()
    {
        return inDTD;
    }

}

class ParseEventForwarder implements ContentHandler, LexicalHandler
{
    private BuilderImpl receiver;


    ParseEventForwarder()
    {}


    ParseEventForwarder( BuilderImpl receiver )
    {
        this.receiver = receiver;
    }


    void setEventReceiver( BuilderImpl receiver )
    {
        this.receiver = receiver;
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment( char[] ch, int start, int length ) throws SAXException
    {
        receiver.comment( ch, start, length );
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException
    {
        receiver.endCDATA();
    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD() throws SAXException
    {
        receiver.endDTD();

    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity( String arg0 ) throws SAXException
    {
        receiver.endEntity( arg0 );

    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA() throws SAXException
    {
        receiver.startCDATA();

    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD( String arg0, String arg1, String arg2 ) throws SAXException
    {
        receiver.startDTD( arg0, arg1, arg2 );

    }


    /**
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity( String arg0 ) throws SAXException
    {
        receiver.startEntity( arg0 );

    }


    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters( char[] arg0, int arg1, int arg2 ) throws SAXException
    {
        receiver.characters( arg0, arg1, arg2 );
    }


    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
        receiver.endDocument();
    }


    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement( String arg0, String arg1, String arg2 ) throws SAXException
    {
        receiver.endElement( arg0, arg1, arg2 );

    }


    /**
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping( String arg0 ) throws SAXException
    {
        receiver.endPrefixMapping( arg0 );

    }


    /**
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace( char[] arg0, int arg1, int arg2 ) throws SAXException
    {
        receiver.ignorableWhitespace( arg0, arg1, arg2 );

    }


    /**
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction( String arg0, String arg1 ) throws SAXException
    {
        receiver.processingInstruction( arg0, arg1 );

    }


    /**
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator( Locator arg0 )
    {
        receiver.setDocumentLocator( arg0 );

    }


    /**
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity( String arg0 ) throws SAXException
    {
        receiver.skippedEntity( arg0 );

    }


    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException
    {
        receiver.startDocument();
    }


    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement( String arg0, String arg1, String arg2, Attributes arg3 )
            throws SAXException
    {
        receiver.startElement( arg0, arg1, arg2, arg3 );

    }


    /**
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping( String arg0, String arg1 ) throws SAXException
    {
        receiver.startPrefixMapping( arg0, arg1 );

    }
}