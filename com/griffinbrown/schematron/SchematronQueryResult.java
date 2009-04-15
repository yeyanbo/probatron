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

/*
 * Created on 30 Sep 2008
 */
package com.griffinbrown.schematron;

import org.apache.log4j.Logger;
import org.probatron.QueryEvaluator;
import org.probatron.QueryHandler;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.XPathLocatorImpl;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.NameFunction;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XMLToolException;
import com.griffinbrown.xmltool.XPathLocator;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Represents the result of evaluating a query against an XML document as part of validation using Schematron.
 * @author andrews
 *
 * @version $Id: SchematronQueryResult.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class SchematronQueryResult
{
    private int context;
    private SchematronQuery query;
    private XPathLocator xpathLocator;
    private LocatorImpl locator;
    private String name;
    private String message;
    
    private static final String ELEMENT_NAME = "name";
    private static final String ELEMENT_VALUE_OF = "value-of";
    
    private static Logger logger = Logger.getLogger( SchematronQueryResult.class );


    SchematronQueryResult( int context, SchematronQuery query, QueryEvaluator evaluator ) throws JaxenException
    {
        this.context = context;
        QueryHandler handler = query.getRule().getPattern().getSchema().getQueryHandler();
        locator = ( LocatorImpl )handler.getLocatorForNode( context );
        this.query = query;
        xpathLocator = new XPathLocatorImpl( context, evaluator );
        name = query.getClass().getName().endsWith( "Assert" ) ? "failed-assert"
                : "successful-report";
        message = getMessage();
    }

    /**
     * Requests a (post-validation) report for the associated query.  
     * @param format the format of the report required (e.g. plain text, XML)  
     * @return the string of the report
     * @throws JaxenException
     */
    public String report( short format ) throws JaxenException
    {
        if( format == com.griffinbrown.xmltool.Constants.ERRORS_AS_XML )
            return query.report( locator, xpathLocator, getMessage() );
        else if( format == com.griffinbrown.xmltool.Constants.ERRORS_AS_TEXT )
            return asText();
        return null;
    }


    private String asText() throws JaxenException
    {
        StringBuffer s = new StringBuffer( getMessage() );
        s.append( ":" );
        if( xpathLocator != null )
            s.append( xpathLocator.toString() );
        s.append( "line " + locator.getLineNumber() );
        s.append( ", column " + locator.getColumnNumber() );
        return s.toString();
    }


    String getMessage() throws JaxenException
    {
        return message == null ? processElemDescendants( query.getMessageNode() ) : message;
    }


    /**
     * Adds the descendants of an element to the message XML.
     * @param node
     * @param contextNode
     * @return
     */
    String processElemDescendants( int node ) throws JaxenException
    {
        StringBuffer s = new StringBuffer();
        ShailXPath xpath = new ShailXPath( "node()" );
        ShailList nodes = ( ShailList )xpath.evaluate( node ); //child nodes
        ShailIterator iter = nodes.shailListIterator();
        ShailNavigator nav = ShailNavigator.getInstance();

        while( iter.hasNext() )
        {
            int n = iter.nextNode();

            if( nav.isElement( n ) )
            {
                s.append( processElement( n ) );
            }
            else if( nav.isText( n ) )
            {
                s.append( processText( n ) );
            }
            //TODO: other node types here? 
        }
        return s.toString();
    }


    private String processElement( int node ) throws JaxenException
    {
        StringBuffer s = new StringBuffer();
        ShailNavigator nav = ShailNavigator.getInstance();
        String nodeName = nav.getElementName( node );

        if( nodeName.equals( ELEMENT_NAME )
                && nav.getNamespaceURI( node ).equals( Constants.SCHEMATRON_NAMESPACE ) )
        {
            s.append( elementName( node, context ) );
        }
        else if( nodeName.equals( ELEMENT_VALUE_OF )
                && nav.getNamespaceURI( node ).equals( Constants.SCHEMATRON_NAMESPACE ) )
        {
            s.append( elementValueOf( node, context ) );
        }
        else
        {
            s.append( "<" + nodeName );
            ShailXPath xpath = new ShailXPath( "@*" );
            ShailList atts = ( ShailList )xpath.evaluate( node );
            if( ! atts.isEmpty() )
            {
                ShailIterator iter = atts.shailListIterator();
                while( iter.hasNext() )
                {
                    int att = iter.nextNode();
                    s.append( " "
                            + nav.getAttributeName( att )
                            + "='"
                            + Utils.quoteAttr( Utils
                                    .escape( nav.getAttributeStringValue( att ) ) ) + "'" );
                }
            }
            String ns = nav.getNamespaceURI( node );
            if( ns != null )
                s.append( " xmlns='" + ns + "'" );
            s.append( ">" );
            s.append( processElemDescendants( node ) );
            s.append( "</" + nodeName + ">" );
        }

        return s.toString();
    }


    /**
     * Evaluates this node as an XPath expression against the context node.  
     * @param node
     * @param contextNode
     * @return the result of evaluating this node as an XPath expression against
     * the context node
     */
    private String elementName( int node, int contextNode ) throws JaxenException
    {
        QueryHandler queryHandler = query.getRule().getPattern().getSchema().getQueryHandler();
        QueryEvaluator evaluator = queryHandler.getEvaluator();

        ShailXPath xpath = new ShailXPath( "string(@path)" );
        String path = ( String )xpath.evaluate( node );
        String o = null;
        if( "".equals( path.trim() ) ) //no @path, so just treat as name() 
        {
            try
            {
                o = NameFunction.evaluate( contextNode, ShailNavigator.getInstance() );
            }
            catch( FunctionCallException e )
            {
                queryHandler.getSession().addMessage(
                        new SessionMessage( queryHandler.getSession().getApplication(),
                                com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                                "error in element name: " + e.getMessage() ) );
                return "";
            }
        }
        else
        //wrap in name() - taking lead from Schematron text impl (NOT in spec)
        {
            o = evaluateXPath( "name(" + path + ")", contextNode );
        }

        return Utils.escape( o );
    }


    /**
     * Evaluates an XPath against a context node.
     * @param xpath
     * @param context
     * @return
     */
    private String evaluateXPath( String xpath, int context )
    {
        QueryHandler queryHandler = query.getRule().getPattern().getSchema().getQueryHandler();
        QueryEvaluator evaluator = queryHandler.getEvaluator();

        String o = null;
        //evaluate *its* XPath expression on the result node
        try
        {
            o = evaluator.evaluateAsString( context, evaluator.compile( xpath ) );
        }
        catch( XMLToolException e )
        {
            queryHandler.getSession().addMessage(
                    new SessionMessage( queryHandler.getSession().getApplication(),
                            com.griffinbrown.xmltool.Constants.ERROR_TYPE_NON_FATAL,
                            "error evaluating XPath expression '" + xpath + "': "
                                    + e.getMessage() ) );
            return "";
        }
        return o;
    }


    private String elementValueOf( int node, int contextNode ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "string(@select)" );
        String select = ( String )xpath.evaluate( node );
        if( ! "".equals( select.trim() ) )
        {
            String o = evaluateXPath( select, contextNode );
            if( o != null )
                return Utils.escape( o );
        }
        return "";
    }


    private String processText( int node )
    {
        return Utils.escape( ShailNavigator.getInstance().getTextStringValue( node ) );
    }

}
