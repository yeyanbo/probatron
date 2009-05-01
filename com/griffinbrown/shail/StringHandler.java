/*
 * Created on 12 Sep 2007
 */
package com.griffinbrown.shail;

import java.io.OutputStream;

/**
 * Interface to handle strings created when a Shail document is built.
 * @author andrews
 *
 * $Id$
 */
public interface StringHandler
{
    /**
     * Reports a string to the handler. 
     * @param string the string so reported
     */
    void addString( String string );
    
    /**
     * Accesses the string associated with node <code>node</code>. 
     * @param node the node whose string is required
     * @return string of the node required 
     */
    String getString( int node );
    
    /**
     * Accesses the total length of the strings passed to the handler.
     * @return total length of strings handled
     */
    int getTotalStringLength();
    
    /**
     * Sets the stream parse events should be written to. 
     * @param eventStream
     */
    void setEventStream( OutputStream eventStream );
    
    /**
     * Notifies the hander that the document has ended.
     */
    void endDocument();
    
    /**
     * Sets the document builder for this handler.
     * @param builder the builder to be set
     */
    void setBuilder( Builder builder );
}
