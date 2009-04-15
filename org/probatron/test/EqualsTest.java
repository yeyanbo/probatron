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
 * $Header: /TEST/xmlprobe-dev/probe/src/com/xmlprobe/test/EqualsTest.java,v 1.1 2008/11/11 10:43:39 GBDP\andrews Exp $ $Revision: 1.1 $ $Date: 2008/11/11 10:43:39 $
 * 
 * ====================================================================
 * 
 * Copyright 2000-2002 bob mcwhirter & James Strachan. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *  * Neither the name of the Jaxen Project nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
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
 * @version $Id: EqualsTest.java,v 1.1 2008/11/11 10:43:39 GBDP\andrews Exp $
 */

package org.probatron.test;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.JaxenException;
import org.xml.sax.SAXException;


/**
 * <p>
 *  Test for function context.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b9
 *
 */
public class EqualsTest extends TestCase
{

    public void testEqualityAgainstNonExistentNodes() throws JaxenException,
            ParserConfigurationException
    {

        ShailXPath xpath = new ShailXPath( "/a/b[c = ../d]" );

        ShailNavigator nav = ShailNavigator.getInstance();
        Object doc = nav.getDocumentAsObject( "test/test-cases/equals-test.xml" );

        List result = ( List )xpath.evaluate( doc );
        assertEquals( 0, result.size() );

    }


    public void testOlander() throws JaxenException, SAXException, IOException,
            ParserConfigurationException
    {

        ShailXPath xpath = new ShailXPath(
                "//BlockStatement//IfStatement[./Statement =  ./ancestor::BlockStatement/following-sibling::BlockStatement//IfStatement/Statement]" );

        ShailNavigator nav = ShailNavigator.getInstance();
        Object doc = nav.getDocumentAsObject( "test/test-cases/olander.xml" );

        List result = ( List )xpath.evaluate( doc );
        assertEquals( 1, result.size() );

    }
    
    /**
     * XPath s3.4:
     * "If both objects to be compared are node-sets, then the comparison will be true if and 
     * only if there is a node in the first node-set and a node in the second node-set such that 
     * the result of performing the comparison on the string-values of the two nodes is true."
     * @throws Exception
     */
    public void testNodesetEquality() throws Exception
    {
        ShailXPath xpath = new ShailXPath("//a = //b");
        
        ShailNavigator nav = ShailNavigator.getInstance();
        Object doc = nav.getDocumentAsObject( "test/test-cases/nodeset-equality.xml" );

        Boolean result = ( Boolean )xpath.evaluate( doc );
        assertEquals( true, result.booleanValue() );
    }
    
    public void testNodesetEquality2() throws Exception
    {
        ShailXPath xpath = new ShailXPath("//b = //c");
        
        ShailNavigator nav = ShailNavigator.getInstance();
        Object doc = nav.getDocumentAsObject( "test/test-cases/nodeset-equality.xml" );

        Boolean result = ( Boolean )xpath.evaluate( doc );
        assertEquals( false, result.booleanValue() );
    }    

}
