/*
 * Created on 21 Aug 2008
 */
package com.griffinbrown.shail;

/**
 * An node on the XPath <code>namespace</code> axis.
 * 
 * @author andrews
 *
 * $Id$
 */
public class NamespaceNode
{
    private String uri;
    private String prefix;


    NamespaceNode( int parent, String prefix, String uri )
    {
        this.uri = uri;
        this.prefix = prefix;
    }


    NamespaceNode( int parent, int att, Model model )
    {
        String prefix = model.getPrefix( att );
        String uri = model.getNamespaceURI( att );
        
        if( prefix.equals( "xmlns" ) )
        {
            this.prefix = "";
        }
        else if( prefix.startsWith( "xmlns:" ) )
        {
            this.prefix = prefix.substring( 6 ); // the part after "xmlns:"
        }
        else
        { // workaround for Crimson bug; Crimson incorrectly reports the prefix as the node name
            this.prefix = prefix;
        }
        this.uri = uri;
    }


    String getURI()
    {
        return this.uri;
    }


    /**
     * @return the name
     */
    String getPrefix()
    {
        return prefix;
    }
}
