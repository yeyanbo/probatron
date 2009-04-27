/*
 * Created on 12 Sep 2007
 */
package com.griffinbrown.shail;

import java.io.OutputStream;

public interface StringHandler
{
    void addString( String string );
    String getString( int node );
    int getTotalStringLength();
    void setEventStream( OutputStream eventStream );
    void endDocument();
    void setBuilder( Builder builder );
}
