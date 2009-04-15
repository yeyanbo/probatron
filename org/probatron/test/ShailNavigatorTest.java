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
 * Created on 30 Apr 2007
 */
package org.probatron.test;

import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.SimpleNamespaceContext;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.shail.util.ShailIterator;

public class ShailNavigatorTest extends XPathTestBase
{
    private Navigator navigator;


    public ShailNavigatorTest( String name )
    {
        super( name );

        //configure logger
        DOMConfigurator.configure( "log4j.xml" );
    }


    protected void setUp() throws Exception
    {
        super.setUp();

        //reset the model registry between runs, else crosstalk results
        ModelRegistry.reset();

        //        log( ShailNavigator.getInstance().getModelRegistry().debug() );
    }


    /**
     * @see org.probatron.test.XPathTestBase#getDocument(java.lang.String)
     */
    protected int getDocument( String url ) throws Exception
    {
        return getNavigator().getDocument( url );
    }


    /**
     * @see org.probatron.test.XPathTestBase#getNavigator()
     */
    protected Navigator getNavigator()
    {
        if( navigator == null )
            navigator = ShailNavigator.getInstance();

        return navigator;
    }


    public void testPreviousSiblingAxis() throws Exception
    {
        int doc = getDocument( "xml/text.xml" );

        ShailXPath xpath = new ShailXPath( "//*" );
        List nodes = xpath.selectNodes( doc );

        logger.debug( "instance contains " + nodes.size() + " elements" );

//        ModelRegistry.reset();
        
        doc = getDocument( "xml/load-extension-function3.out" );
        
        Model model = ModelRegistry.getModelForNode( doc );
//        model.debugEvents();
        
        SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
        namespaceContext.addNamespace( "probe", "http://xmlprobe.com/200312" );
        namespaceContext.addNamespace( "silcn", "http://silcn.org/200309" );
        
        //preceding-sibling
        xpath = new ShailXPath(
        "//silcn:node[ preceding-sibling::silcn:id = 'elem-content-only' ][ probe:msg = 'foo' ]" );
        
        xpath.setNamespaceContext( namespaceContext );        
        nodes = xpath.selectNodes( doc );

        assertEquals( 1, nodes.size() );
        
        //parent 
        xpath = new ShailXPath( "//silcn:node/parent::*" );
        xpath.setNamespaceContext( namespaceContext );        
        nodes = xpath.selectNodes( doc );
        assertEquals( 1, nodes.size() );
        
        //ancestor 
        xpath = new ShailXPath( "//silcn:node/ancestor::*" );
        xpath.setNamespaceContext( namespaceContext );        
        nodes = xpath.selectNodes( doc );
        assertEquals( 3, nodes.size() );        
    }


    public void testShailXPaths() throws Exception
    {
        int doc = getDocument( "xml/shail-instance.xml" );

        //        System.err.println( ShailNavigator.getInstance().getModelRegistry().getModelForNode( doc ).serialise() );

        ShailXPath xpath = new ShailXPath( "/", this.getNavigator() );

        List nodes = xpath.selectNodes( doc );

        ShailIterator it = ( ShailIterator )nodes.iterator();

        while( it.hasNext() )
        {
            int context = it.nextNode();

//            xpath.selectSingleNodeForContext( xpath.getContext( context ) );

            assertValueOfXPath( "\none\ntwo\nthree\n", context, "." );

            assertCountXPath( 10, context, "(//node())[following-sibling::node()]" );

            assertCountXPath( 1, context, "//one/following-sibling::text()" );
            assertCountXPath( 1, context,
                    "/*/number[1]/processing-instruction()/following-sibling::*" );

            assertCountXPath( 3, context, "/*/number[1]/following-sibling::text()" );
            assertCountXPath( 2, context, "/*/number[1]/following-sibling::*" );

            assertCountXPath( 6, context, "/*/number[1]/following-sibling::node()" );
            assertCountXPath( 1, context, "/*/number[1]/following-sibling::comment()" );

            assertCountXPath( 1, context, "/*/number[2]/following-sibling::comment()" );

            assertCountXPath( 0, context, "/*/comment()/following-sibling::*" );
            assertCountXPath( 2, context, "/*/number" );

            assertCountXPath( 0, context, "/*/number[2]/following-sibling::*" );

            assertCountXPath( 2, context, "/*/number[2]/following-sibling::node()" );
            assertCountXPath( 3, context, "/*/number[1]/following-sibling::text()" );
            assertCountXPath( 1, context, "/*/number[2]/following-sibling::text()" );

            assertCountXPath( 0, context, "/preceding-sibling::node()" );

            assertCountXPath( 1, context, "//one/preceding-sibling::processing-instruction()" );
            assertCountXPath( 0, context,
                    "//processing-instruction('foo')/preceding-sibling::*" );
            assertCountXPath( 1, context, "//one/preceding-sibling::node()" );
            assertCountXPath( 3, context, "//comment()[.='1']/preceding-sibling::node()" );

            assertCountXPath( 1, context, "/*/ancestor::node()" );

            assertValueOfXPath( "two", context,
                    "//*[local-name()='number' and namespace-uri()='lalala']" );

            assertCountXPath( 1, context, "/*" );

            assertCountXPath( 5, context, "//*" );
            assertCountXPath( 4, context, "//@*" );

            assertCountXPath( 1, context, "/" );
            assertCountXPath( 2, context, "//comment()" );

            assertValueOfXPath( "1", context, "(//comment())[1]" );
            assertValueOfXPath( "<number>four</number>", context, "(//comment())[2]" );

            assertCountXPath( 1, context, "//processing-instruction()" );
            assertCountXPath( 1, context, "//processing-instruction('foo')" );
            assertValueOfXPath( "bar", context, "//processing-instruction('foo')" );

            assertValueOfXPath( "bar", context, "/*/number/@foo" );

            //TODO: test string value of namespace node (should be the URI)

            assertValueOfXPath( "\none\ntwo\nthree\n", context, "/" );
            assertValueOfXPath( "15", context, "string-length(/)" );

        }

        //        doc = getDocument( "xml/testNamespaces.xml" );

        //        xsp = new ShailXPath( "count( /ancestor-or-self::* )" );
        //        xsp = new ShailXPath( "/*/*/parent::node()" );
        //
        //        Object result = xsp.evaluate( doc );
        //        System.err.println( xsp + " = " + result );
        //
        //        xsp = new ShailXPath( "/*/child::*" );
        //        result = xsp.evaluate( doc );
        //
        //        System.err.println( xsp + " = " + result );
        //
        //        
        //        result = xsp.evaluate( doc );
        //
        //        System.err.println( xsp + " = " + result );
        //
        //        xsp = new ShailXPath( "/*" );
        //        result = xsp.evaluate( doc );
        //
        //        System.err.println( xsp + " = " + result );
        //
        //        xsp = new ShailXPath( "/descendant::*" );
        //        result = xsp.evaluate( doc );
        //
        //        System.err.println( xsp + " = " + result );
        //
        //        xsp = new ShailXPath( "processing-instruction('foo')[.='bar']" );
    }

    /**
     * See http://www.w3.org/TR/xpath#data-model:
     * "The namespace nodes are defined to occur before the attribute nodes."
     *
     
    public void testNamespacesOccurBeforeAttributes() throws Exception
    {
        int doc = getDocument( "xml/testNamespaces.xml" );

        ShailXPath xpath = new ShailXPath(
                "(/Template/Application1/namespace::*|/Template/Application1/@*)",
                getNavigator() );
        List nodes = xpath.selectNodes( doc );

        ShailIterator iter = (ShailIterator)nodes.iterator();
        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            System.err.println( node );

            assertCountXPath( 5, node,
                    "(/Template/Application1/namespace::*|/Template/Application1/@*)" );

            assertCountXPath(
                    3,
                    node,
                    "(/Template/Application1/namespace::*|/Template/Application1/@*)"
                            + "[ position() <= 3 ][ name() = 'xml' or name() = 'xpl' or name() = 'xplt' ]" );
        }

    }*/

    //    public void testHugeDocuments() throws Exception
    //    {
    //        Integer doc = ( Integer )getDocument( "E:\\work\\php\\200607\\Clarke\\clarke14.xml" ); //21.5MB
    //        //        PseudoNode doc = ( PseudoNode )getDocument( "E:\\work\\php\\200607\\excipients\\excipients.xml" );  //6.75MB
    //        System.err.println( ( ( ShailNavigator )getNavigator() ).getModel(doc).toString() );
    //
    //        assertCountXPath( 1, doc, "/" );
    //        
    //        doc = ( Integer )getDocument( "E:\\work\\php\\200607\\DSupps\\dsupps.xml" );
    //        
    //        assertCountXPath( 40681, doc, "//*" );
    //        
    //        System.err.println( ( ( ShailNavigator )getNavigator() ).getModel(doc).toString() );
    //    }
    //    public void testShailSerialization() //can we do this??
    //    {}
}
