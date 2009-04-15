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

import org.apache.log4j.Logger;
import org.probatron.jaxen.Navigator;


/**
 * <p>Represents the <code>NodeTest</code> part of an XPath <code>StepPattern</code>
 * in the specific context of an <code>XPathLocator</code>.
 * This can be:</p>
 * <ul>
 * <li>a <code>ElementNameTest</code>, e.g. an element name</li>
 * <li>a <code>NodeType</code>: one of <code>comment()</code>, <code>text()</code>,
 * <code>processing-instruction()</code> or <code>node()</code></li>
 * <li>a named processing instruction, e.g. <code>processing-instruction('pi-target').</code></li>
 * </ul>
 * 
 * @see com.griffinbrown.xmltool.XPathLocator 
 * 
 * @author andrews
 * @version $Revision: 1.1 $
 * 
 * @version $Id: NodeTest.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 * 
 */

public abstract class NodeTest
{
    private int predicate = 1; //note that the special value -1 means the node has no similar siblings
    private static Logger logger = Logger.getLogger( NodeTest.class );


    NodeTest( int node )
    {}


    /**
     * Factory method for creating NodeTests of the correct type, according to
     * the type of DOM node passed in. 
     * Note that for custom XPath evaluations (for instance using extension
     * functions) it may be necessary to allow further types of nodes to be 
     * instantiated (e.g. CDATA or EntityReference nodes). 
     * 
     * @param node
     * @return an instance of a class implementing the 
     * com.griffinbrown.xmltool.qa.NodeTest interface, or <code>null</code> if 
     * the node type is not one of:
     * <ul>
     * <li>org.w3c.dom.Node.ATTRIBUTE_NODE</li>
     * <li>org.w3c.dom.Node.COMMENT_NODE</li>
     * <li>org.w3c.dom.Node.ELEMENT_NODE</li>
     * <li>org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE</li>
     * <li>org.w3c.dom.Node.TEXT_NODE</li>
     * </ul> 
     */
    public static NodeTest newInstance( int node, QueryEvaluator evaluator )
    {
        Navigator nf = ShailNavigator.getInstance();

        if( nf.isAttribute( node ) )
        {
            return new AttributeNameTest( node, evaluator );
        }
        else if( nf.isComment( node ) )
        {
            return new Comment( node );
        }
        else if( nf.isDocument( node ) )
        {
            return new DocumentRoot( node );
        }
        else if( nf.isElement( node ) )
        {
            return new ElementNameTest( node, evaluator );
        }
        else if( nf.isProcessingInstruction( node ) )
        {
            return new ProcessingInstruction( node );
        }
        else if( nf.isText( node ) )    //TODO: CDATA!! || nf.isCDATASection( node ) ) //CDATA too because XPath has no conception of CDATA sections
        {
            return new Text( node );
        }

        return null;
    }


    /**
     * @return the positional predicate for this <code>NodeTest</code>.
     */
    public String getPredicate()
    {
        return ( this.predicate != - 1 ) ? ( "[" + this.predicate + "]" ) : "";
    }


    /**
     * @return a string representation of this <code>NodeTest</code>, suitable 
     * for inclusion in an <code>XPathLocator</code> string.
     */
    public abstract String getValue();


    public void setPredicate( int predicate )
    {
        this.predicate = predicate;
    }

}
