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
 * $Header: /TEST/xmlprobe-dev/probe/src/com/xmlprobe/test/IdTest.java,v 1.1 2008/11/11 10:43:39 GBDP\andrews Exp $ $Revision: 1.1 $ $Date: 2008/11/11 10:43:39 $
 * 
 * ====================================================================
 * 
 * Copyright 2005 Elliotte Rusty Harold All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: * Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer. *
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Jaxen Project nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ==================================================================== This software consists
 * of voluntary contributions made by many individuals on behalf of the Jaxen Project and was
 * originally created by bob mcwhirter <bob@werken.com> and James Strachan
 * <jstrachan@apache.org>. For more information on the Jaxen Project, please see
 * <http://www.jaxen.org/>.
 * 
 * @version $Id: IdTest.java,v 1.1 2008/11/11 10:43:39 GBDP\andrews Exp $
 */

package org.probatron.test;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.XPath;
import org.xml.sax.SAXException;

import com.griffinbrown.shail.Model;
import com.griffinbrown.shail.ModelRegistry;
import com.griffinbrown.shail.util.ShailList;

/**
 * @author Elliotte Rusty Harold
 *
 */
public class IdTest extends TestsBase
{

    private Object doc;
    private static Logger logger = Logger.getLogger( IdTest.class );


    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        doc = ShailNavigator.getInstance().getDocumentAsObject(
                "test/test-cases/dummy-instance.xml" );
    }


    public void testIDFunctionSelectsNothingInDocumentWithNoIds() throws JaxenException
    {

        XPath xpath = new ShailXPath( "id('p1')" );

        List result = xpath.selectNodes( doc );
        assertEquals( 0, result.size() );

    }


    public void testIDFunctionRequiresAtLeastOneArgument() throws JaxenException
    {

        try
        {
            XPath xpath = new ShailXPath( "id()" );
            xpath.selectNodes( doc );
            fail( "Allowed empty id() function" );
        }
        catch( FunctionCallException success )
        {
            assertNotNull( success.getMessage() );
        }

    }


    public void testIDFunctionRequiresAtMostOneArgument() throws JaxenException
    {

        try
        {
            XPath xpath = new ShailXPath( "id('p', 'q')" );
            xpath.selectNodes( doc );
            fail( "Allowed two-argument id() function" );
        }
        catch( FunctionCallException success )
        {
            assertNotNull( success.getMessage() );
        }

    }


    public void testFindElementById() throws JaxenException, SAXException, IOException
    {

        XPath xpath = new ShailXPath( "id('p1')" );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocumentAsObject( "test/test-cases/findElementById.xml" );

        ShailList result = ( ShailList )xpath.selectNodes( doc );
        assertEquals( 1, result.size() );

        int root = ( ( ShailList )doc ).getInt( 0 );

        int a = result.getInt( 0 );
        assertXPathEquals( "a", root, "name(root/*)" );

    }


    /* public void testFindElementByXMLId() 
      throws JaxenException, SAXException, IOException {
        
        XPath xpath = new ShailXPath("id('p1')");
        String text = "<root><a xml:id='p1'/></root>";
        StringReader reader = new StringReader(text);
        InputSource in = new InputSource(reader);
        Document doc = builder.parse(in);
        List result = xpath.selectNodes(doc);
        assertEquals(1, result.size());
        Element a = (Element) result.get(0);
        assertEquals("a", a.getNodeName());
        
    }    */

    
    //TODO: these two don't work because the rootIndex needs subtracting from the int representing the node
    //N.B. this is specific to evaluating XPaths against individual nodes 
    
    /*public void testFindMultipleElementsByMultipleIDs() throws JaxenException, SAXException,
            IOException
    {
        XPath xpath = new ShailXPath( "id(//id)" );
        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocumentAsObject( "test/test-cases/findMultipleElementsByMultipleIds.xml" );

        ShailList result = ( ShailList )xpath.selectNodes( doc );
        assertEquals( 2, result.size() );
        
        logger.debug( "result="+result );
       

        assertXPathTrue( result.getInt( 0 ), "name() = 'a'" );
        assertXPathTrue( result.getInt( 1 ), "name() = 'a'" );
    }


    public void testIdReturnsFirstElementWithMatchingId() throws JaxenException, SAXException,
            IOException
    {
        XPath xpath = new ShailXPath( "id('p1')" );

        doc = ShailNavigator.getInstance().getDocumentAsObject(
                "test/test-cases/IdReturnsFirstElementWithMatchingId.xml" );

        ShailList result = ( ShailList )xpath.selectNodes( doc );
        assertEquals( 1, result.size() );

        int a = result.getInt( 0 );
//        assertXPathEquals( "a", a, "name()" );
        assertXPathTrue( a, "name() = 'a'" );
    }*/
}