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
 * $Header: /TEST/xmlprobe-dev/probe/src/com/xmlprobe/test/StartsWithTest.java,v 1.1 2008/11/11 10:43:40 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2008/11/11 10:43:40 $
 *
 * ====================================================================
 *
 * Copyright 2005 Elliotte Rusty Harold.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   * Neither the name of the Jaxen Project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * @version $Id: StartsWithTest.java,v 1.1 2008/11/11 10:43:40 GBDP\andrews Exp $
 */

package org.probatron.test;

import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.FunctionCallException;
import org.probatron.jaxen.JaxenException;

import junit.framework.TestCase;


/**
 * @author Elliotte Rusty Harold
 *
 */
public class StartsWithTest extends TestCase {

    private Object doc;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
       super.setUp();
       doc = ShailNavigator.getInstance().getDocumentAsObject( "test/test-cases/dummy-instance.xml" ); 
    }


    public StartsWithTest(String name) {
        super(name);
    }

    public void testStartsWithNumber() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with(33, '3')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.TRUE, result);
    }    
  
    public void testStartsWithString() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with('test', 't')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.TRUE, result);
    }    
  
    public void testStartsWithString3() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with('superlative', 'superlative')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.TRUE, result);
    }    
  
    public void testStartsWithNumber2() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with(43, '3')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.FALSE, result);
    }    
  
    public void testStartsWithString2() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with('1234567890', '1234567a')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.FALSE, result);
    }    
  
  
    public void testEmptyStringStartsWithNonEmptyString() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with('', 'a')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.FALSE, result);
    }
  
    public void testEmptyStringStartsWithEmptyString() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with('', '')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.TRUE, result);
    }
  
    public void testStartsWithEmptyString() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "starts-with('a', '')" );
        Boolean result = (Boolean) xpath.evaluate( doc );
        assertEquals(Boolean.TRUE, result);
    }    
  
    public void testStartsWithFunctionRequiresAtLeastTwoArguments() 
      throws JaxenException {
        
        ShailXPath xpath = new ShailXPath("starts-with('a')");
        
        try {
            xpath.selectNodes(doc);
            fail("Allowed starts-with function with one argument");
        }
        catch (FunctionCallException ex) {
            assertNotNull(ex.getMessage());
        }
        
    }    

    public void testStartsWithFunctionRequiresAtMostTwoArguments() 
      throws JaxenException {
        
        ShailXPath xpath = new ShailXPath("starts-with('a', 'a', 'a')");
        
        try {
            xpath.selectNodes(doc);
            fail("Allowed starts-with function with three arguments");
        }
        catch (FunctionCallException ex) {
            assertNotNull(ex.getMessage());
        }
        
    }    

}
