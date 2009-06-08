/*
 * Copyright 2009 Griffin Brown Digital Publishing Ltd All rights reserved.
 * 
 * This file is part of Probatron.
 * 
 * Probatron is free software: you can redistribute it and/or modify it under the terms of the
 * Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * Probatron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the Affero General Public License for more details.
 * 
 * You should have received a copy of the Affero General Public License along with Probatron. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.probatron;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.UnsupportedAxisException;
import org.probatron.jaxen.XPath;
import org.probatron.jaxen.pattern.Pattern;
import org.probatron.jaxen.saxpath.SAXPathException;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;

import com.griffinbrown.shail.EmptyShailIterator;
import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.shail.iter.AncestorAxisIterator;
import com.griffinbrown.shail.iter.AncestorOrSelfAxisIterator;
import com.griffinbrown.shail.iter.AttributeIterator;
import com.griffinbrown.shail.iter.ChildAxisIterator;
import com.griffinbrown.shail.iter.DescendantAxisIterator;
import com.griffinbrown.shail.iter.DescendantOrSelfAxisIterator;
import com.griffinbrown.shail.iter.FollowingAxisIterator;
import com.griffinbrown.shail.iter.FollowingSiblingAxisIterator;
import com.griffinbrown.shail.iter.NamespaceAxisIterator;
import com.griffinbrown.shail.iter.ParentAxisIterator;
import com.griffinbrown.shail.iter.PrecedingAxisIterator;
import com.griffinbrown.shail.iter.PrecedingSiblingAxisIterator;
import com.griffinbrown.shail.iter.SelfAxisIterator;
import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailSingletonList;

/**
 * Document navigator for the Shail document model.
 */
public class ShailNavigator implements Navigator
{
    private static Logger logger = Logger.getLogger( ShailNavigator.class );
    private Model model;
    private static final ShailNavigator INSTANCE = new ShailNavigator();
    private EntityResolver entityResolver;
    private Object evaluator;


    //    private ModelRegistry modelRegistry;

    private ShailNavigator()
    {
    //        this.modelRegistry = new ModelRegistry(); //new ModelRegistry();
    //        modelRegistry = ModelRegistry.getInstance();
    }


    /**
     * Gets the singleton instance of this navigator.
     * @return the <code>ShailNavigator</code> singleton
     */
    public static ShailNavigator getInstance()
    {
        return INSTANCE;
    }


    public boolean isAttribute( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_ATTRIBUTE;
    }


    public boolean isComment( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_COMMENT;
    }


    public boolean isDocument( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_DOCUMENT;
    }


    public boolean isElement( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_ELEMENT;
    }


    public boolean isNamespace( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_NAMESPACE_DECL_ATTRIBUTE;
    }


    public boolean isProcessingInstruction( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_PROCESSING_INSTRUCTION;
    }


    public boolean isText( int o )
    {
        return getModel( o ).getType( o ) == Model.EV_TEXT;
    }


    public Iterator getAncestorAxisIterator( int o ) throws UnsupportedAxisException
    {
        return new AncestorAxisIterator( o, getModel( o ) );
    }


    public Iterator getAncestorOrSelfAxisIterator( int o ) throws UnsupportedAxisException
    {
        return new AncestorOrSelfAxisIterator( o, getModel( o ) );
    }


    public Iterator getAttributeAxisIterator( int o ) throws UnsupportedAxisException
    {
        if( isElement( o ) )
        {
            return new AttributeIterator( o, getModel( o ) );
        }
        else
        {
            return new EmptyShailIterator();
        }
    }


    public String getAttributeName( int o )
    {
        return isAttribute( o ) ? getModel( o ).getLocalName( o ) : null;
    }


    public String getAttributeNamespaceUri( int o )
    {
        if( ! isAttribute( o ) )
            return null;
        return getModel( o ).getNamespaceURI( o );
    }


    public String getAttributeQName( int o )
    {
        return isAttribute( o ) ? getModel( o ).getName( o ) : null;
    }


    public String getAttributeStringValue( int o )
    {
        return isAttribute( o ) ? getModel( o ).getStringValue( o ) : null;
    }


    public Iterator getChildAxisIterator( int o ) throws UnsupportedAxisException
    {
        return new ChildAxisIterator( o, getModel( o ) );
    }


    public String getCommentStringValue( int o )
    {
        return getModel( o ).getStringValue( o );
    }


    public Iterator getDescendantAxisIterator( int o ) throws UnsupportedAxisException
    {
        return new DescendantAxisIterator( o, getModel( o ) );
    }


    public Iterator getDescendantOrSelfAxisIterator( int o ) throws UnsupportedAxisException
    {
        return new DescendantOrSelfAxisIterator( o, getModel( o ) );
    }


    /**
     * Main method for obtaining a Shail document which can then be queried using
     * XPath.
     */
    public int getDocument( String url ) throws FunctionCallException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "getting document: " + url );

        //        logger.debug( "document " + url + " has Model " + model );

        //TODO: check this!! see LocatorHashingDocumentBuilder#parse()

        try
        {
            BuilderImpl builder = new BuilderImpl();
            model = builder.parse( url );
        }
        catch( Exception e )
        {
            throw new FunctionCallException( e );
        }

        if( model != null )
        {
            ModelRegistry.register( model ); //this must be done AFTER the parse
            return model.getRoot();
        }

        throw new FunctionCallException( "null document" );
    }


    /**
     * Accesses the document at <tt>url</tt> as an object. 
     * @param url the location of the document
     * @return a <code>ShailSingletonList</code> containing the root node 
     * @throws FunctionCallException if the document cannot be retrieved
     */
    public Object getDocumentAsObject( String url ) throws FunctionCallException
    {
        int doc = getDocument( url );

        //        model.debugEvents();

        return new ShailSingletonList( doc );
    }


    public int getDocumentNode( int o )
    {
        //        logger.debug( "getting document node for " + o + " " + modelRegistry + " "
        //                + modelRegistry.debug() );

        if( o == getModel( o ).getRoot() )
        {
            return o;
        }
        else
        {
            return getModel( o ).getRoot();
        }
    }


    public int getElementById( int node, String id )
    {
        return getModel( node ).getElementById( id );
    }


    // Retrieve the name of the given element node.
    public String getElementName( int o )
    {
        return getModel( o ).getLocalName( o );
    }


    public String getElementNamespaceUri( int o )
    {
        if( ! ( isElement( o ) ) )
            return null;

        //                if( logger.isDebugEnabled() )
        //                    logger.debug( "getElementNamespaceUri(): "
        //                            + getModel(o).getNamespaceURI( o ) );

        return getModel( o ).getNamespaceURI( o );
    }


    public String getElementQName( int o )
    {
        return getModel( o ).getName( o ); //TODO: proper impl
    }


    public String getElementStringValue( int o )
    {
        if( ! isElement( o ) )
            return null;
        return getStringValue( o, new StringBuffer() ).toString();
    }


    //TODO: make more efficient(?), by using the Shail stream
    private StringBuffer getStringValue( int node, StringBuffer buffer )
    {
        if( isText( node ) )
        {
            buffer.append( getModel( node ).getStringValue( node ) );
        }
        else
        {
            int[] children = getModel( node ).getChildren( node );
            int length = children.length;
            for( int i = 0; i < length; i++ )
            {
                getStringValue( children[ i ], buffer );
            }
        }
        return buffer;
    }


    public Iterator getFollowingAxisIterator( int context ) throws UnsupportedAxisException
    {
        return new FollowingAxisIterator( context, this );
    }


    public Iterator getFollowingSiblingAxisIterator( int o ) throws UnsupportedAxisException
    {
        return new FollowingSiblingAxisIterator( o, this );
    }


    public Iterator getNamespaceAxisIterator( int context ) throws UnsupportedAxisException
    {
        // Only elements have namespace nodes
        if( isElement( context ) )
        {
            return new NamespaceAxisIterator( context, getModel( context ) );
        }
        else
            return new EmptyShailIterator();
    }


    public String getNamespacePrefix( int object )
    {
        if( isNamespace( object ) )
        {
            return getModel( object ).getPrefix( object );
        }

        else
        {
            return getModel( object ).getPrefix( object );
        }
    }

    /**
     * Retrieves the namespace URI for the node passed in.
     * @param node the node whose namespace URI is required
     * @return namespace URI for the node, or <code>null</code> if none exists 
     */
    public String getNamespaceURI( int node )
    {
        return getModel( node ).getNamespaceURI( node );
    }


    public String getNamespaceStringValue( int object )
    {
        if( isNamespace( object ) )
            return getModel( object ).getNamespaceURI( object );
        else
            return null;
    }


    /**
     * Returns the node type.
     * Note that for namespace nodes in this implementation, the same value as the DOM Level 
     * 3 XPathNamespace type is returned. Namespace nodes are created only when required by 
     * Jaxen, so doing this satisfies the engine's, rather than the navigator's, needs.
     */
    public short getNodeType( int o )
    {
        //        logger.debug( "getNodeType(): " + o.getClass() );
        switch( ( getModel( o ).getType( o ) ) ){
        case Model.EV_ATTRIBUTE:
            return Node.ATTRIBUTE_NODE;
        case Model.EV_DOCUMENT:
            return Node.DOCUMENT_NODE;
        case Model.EV_ELEMENT:
            return Node.ELEMENT_NODE;
        case Model.EV_TEXT:
            return Node.TEXT_NODE;
        case Model.EV_NAMESPACE:
            return Pattern.NAMESPACE_NODE;
        default:
            break;
        }
        return - 1;
    }


    public Iterator getParentAxisIterator( int context ) throws UnsupportedAxisException
    {
        return new ParentAxisIterator( context, getModel( context ) );
    }


    public int getParentNode( int contextNode ) throws UnsupportedAxisException
    {
        ShailIterator iter = ( ShailIterator )getParentAxisIterator( contextNode );
        if( iter != null && iter.hasNext() )
        {
            return iter.nextNode();
        }
        return - 1;
    }


    public Iterator getPrecedingAxisIterator( int context ) throws UnsupportedAxisException
    {
        //                return new PrecedingAxisIterator( context, this );
        return new PrecedingAxisIterator( context, getModel( context ) );
    }


    public Iterator getPrecedingSiblingAxisIterator( int context )
            throws UnsupportedAxisException
    {
        return new PrecedingSiblingAxisIterator( context, getModel( context ) );
    }


    public String getProcessingInstructionData( int o )
    {
        String s = getModel( o ).getStringValue( o );
        return s;
    }


    public String getProcessingInstructionTarget( int o )
    {
        return getModel( o ).getName( o );
    }


    public Iterator getSelfAxisIterator( int context ) throws UnsupportedAxisException
    {
        return new SelfAxisIterator( context );
    }


    public String getTextStringValue( int o )
    {
        return isText( o ) ? getModel( o ).getStringValue( o ) : null;
    }


    public XPath parseXPath( String xpath ) throws SAXPathException
    {
        return new ShailXPath( xpath );
    }


    public String translateNamespacePrefixToUri( String s, int i )
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Retrieves the Model of which this object is part.
     * @param o
     * @return
     */
    private Model getModel( int o )
    {
        //        logger.debug( "getting model for "+o+" model="+modelRegistry.getModelForNode( o ));
        return ModelRegistry.getModelForNode( o );
    }

    /**
     * Sets the SAX entity resolver to use in retrieving documents.
     * @param entityResolver the entity resolver to be used
     */
    public void setEntityResolver( EntityResolver entityResolver )
    {
        this.entityResolver = entityResolver;
    }


    /**
     * Whether the node passed in contains <tt>CDATA</tt>.
     * @param node node to query
     * @return whether the node contains <tt>CDATA</tt>
     */
    public boolean containsCDATA( int node )
    {
        return getModel( node ).containsCDATASection( node );
    }
}
