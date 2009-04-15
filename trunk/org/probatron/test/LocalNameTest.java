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

package org.probatron.test;


import java.util.List;

import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.XPath;

import junit.framework.TestCase;

import com.griffinbrown.shail.util.ShailList;

/**
 * @author Elliotte Rusty Harold
 *
 */
public class LocalNameTest extends TestCase {

    private Object doc;


    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        doc = ShailNavigator.getInstance().getDocumentAsObject( "test/test-cases/testName.xml" );
    }


    public LocalNameTest(String name) {
        super(name);
    }

    public void testLocalNameOfNumber() throws JaxenException
    {
        try
        {
            XPath xpath = new ShailXPath( "local-name(3)" );
            xpath.selectNodes( doc );
            fail("local-name of non-node-set");
        }
        catch (FunctionCallException e) 
        {
           assertEquals("The argument to the local-name function must be a node-set", e.getMessage());
        }
    }    

    public void testLocalNameWithTwoArguments() throws JaxenException
    {
        try
        {
            XPath xpath = new ShailXPath( "local-name(/*, //*)" );
            xpath.selectNodes( doc );
            fail("local-name with two arguments");
        }
        catch (FunctionCallException e) 
        {
           assertEquals("local-name() requires zero or one argument.", e.getMessage());
        }
    }    

    public void testLocalNameAllowsNoArguments() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name()" );
        ShailList root = (ShailList)new ShailXPath( "/*" ).evaluate( doc );
        String result = (String) xpath.evaluate( root );
        assertEquals("foo", result);
    }    

    public void testLocalNameOfCommentIsEmptyString() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name(/*/comment())" );
        String result = (String) xpath.evaluate(doc);
        assertEquals("", result);
    }    

    public void testLocalNameOfEmptyNodeSetIsEmptyString() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name(/aaa)" );
        String result = (String) xpath.evaluate(doc);
        assertEquals("", result);
    }    

    public void testLocalNameOfProcessingInstructionIsTarget() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name(/*/processing-instruction())" );
        String result = (String) xpath.evaluate(doc);
        assertEquals("target", result);
    }    

    public void testLocalNameOfAttribute() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name(/*/@*)" );
        String result = (String) xpath.evaluate(doc);
        assertEquals("name", result);
    }    

    public void testLocalNameOfTextIsEmptyString() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name(/*/text())" );
        String result = (String) xpath.evaluate(doc);
        assertEquals("", result);
    }    

    public void testLocalNameOfNamespaceIsPrefix() throws JaxenException
    {
        XPath xpath = new ShailXPath( "local-name(/*/namespace::node())" );
        String result = (String) xpath.evaluate(doc);
        assertEquals("xml", result);
    }    

    public void testLocalNameNoArguments()
    {
        try
        {
            XPath xpath = new ShailXPath( "local-name()" );
            List results = xpath.selectNodes( doc );
            assertEquals("", results.get(0));
       }
       catch (Exception e)
        {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }    

}
