/*
 * Created on 13 Jan 2009
 */
package com.griffinbrown.shail;

import java.util.HashMap;

public interface Builder
{
    public byte[] getEvents();
    
    public HashMap getIdMap();
    
    public StringHandler getStringHandler();
}
