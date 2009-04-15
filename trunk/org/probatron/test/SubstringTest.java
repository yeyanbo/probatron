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
 * $Header: /TEST/xmlprobe-dev/probe/src/com/xmlprobe/test/SubstringTest.java,v 1.1 2008/11/11 10:43:40 GBDP\andrews Exp $
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
 * @version $Id: SubstringTest.java,v 1.1 2008/11/11 10:43:40 GBDP\andrews Exp $
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
public class SubstringTest extends TestCase {

    private Object doc;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
       super.setUp();
       doc = ShailNavigator.getInstance().getDocumentAsObject( "test/test-cases/dummy-instance.xml" ); 
    }


    public SubstringTest(String name) {
        super(name);
    }

    public void testSubstringOfNumber() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring(1234, 3)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("34", result);
    }    
  
    public void testSubstringOfNumber2() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring(1234, 2, 3)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("234", result);
    }    
    
    // Unusual tests from XPath spec
    
    public void testUnusualSubstring1() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 1.5, 2.6)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("234", result);
    }    


    public void testUnusualSubstring2() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 0, 3)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("12", result);
    }    


    public void testUnusualSubstring3() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 0 div 0, 3)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
    }    

    public void testUnusualSubstring4() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 1, 0 div 0)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
    }    

    public void testUnusualSubstring5() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', -42, 1 div 0)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("12345", result);
    }    

    public void testUnusualSubstring6() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', -1 div 0, 1 div 0)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
    }    

  
    public void testSubstringOfNaN() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring(0 div 0, 2)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("aN", result);
    }    
 
  
    public void testSubstringOfEmptyString() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('', 2)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
    }    
 
  
    public void testSubstringWithNegativeLength() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 2, -3)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
    }    
 
  
    public void testSubstringWithExcessiveLength() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 2, 32)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("2345", result);
    }    
 
  
    public void testSubstringWithNegativeLength2() throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "substring('12345', 2, -1)" );
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
    }    
 
  
    public void testSubstringFunctionRequiresAtLeastTwoArguments() 
      throws JaxenException {
        
        ShailXPath xpath = new ShailXPath("substring('a')");
        
        try {
            xpath.selectNodes(doc);
            fail("Allowed substring function with one argument");
        }
        catch (FunctionCallException ex) {
            assertNotNull(ex.getMessage());
        }
        
    }    

    public void testNegativeStartNoLength() 
      throws JaxenException {
        
        ShailXPath xpath = new ShailXPath("substring('Hello', -50)");
        String result = (String) xpath.evaluate( doc );
        assertEquals("Hello", result);
        
    }    

    public void testNegativeStartWithLength() 
      throws JaxenException {
        
        ShailXPath xpath = new ShailXPath("substring('Hello', -50, 20)");
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
        
    }    

    public void testSubstringFunctionRequiresAtMostThreeArguments() 
      throws JaxenException {
        
        ShailXPath xpath = new ShailXPath("substring('a', 1, 1, 4)");
        
        try {
            xpath.selectNodes(doc);
            fail("Allowed substring function with four arguments");
        }
        catch (FunctionCallException ex) {
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testStringLengthCountsUnicodeCharactersNotJavaChars() 
      throws JaxenException {
   
        ShailXPath xpath = new ShailXPath("substring('A\uD834\uDD00', 1, 2)");
        String result = (String) xpath.evaluate( doc );
        assertEquals("A\uD834\uDD00", result);
        
    }
    
    
    public void testStringLengthIndexesUnicodeCharactersNotJavaChars() 
      throws JaxenException {
   
        ShailXPath xpath = new ShailXPath("substring('A\uD834\uDD00', 3, 1)");
        String result = (String) xpath.evaluate( doc );
        assertEquals("", result);
        
    }
    
    
    public void testStringLengthIndexesAndCountsUnicodeCharactersNotJavaChars() 
      throws JaxenException {
   
        ShailXPath xpath = new ShailXPath("substring('A\uD834\uDD00123', 3, 2)");
        String result = (String) xpath.evaluate( doc );
        assertEquals("12", result);
        
    }
    
    
}
