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
 * Created on 13 Dec 2007
 */
package org.probatron.test;

import org.probatron.ShailNavigator;

public class LocatorTests extends TestsBase
{
    public void testNodeLocators() throws Exception
    {
        String config = "test/rulesets/node-locators.xml";
        String src = "test/test-cases/multi-root-instance.xml";
        String dest = "test/dest/node-locators.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

    }
    
    public void testAttributeLocator() throws Exception
    {
        String config = "test/rulesets/attributeLocator.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/attributeLocator.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        //TODO: can accurate attribute locators be reported? 
        //Currently, they are the same as their parent element's start tag...
        //        assertXPathTrue( doc, "(//probe:line)[1] != (//probe:line)[2] and (//probe:column)[1] != (//probe:column)[2]" );
    }
    



    public void testDTDParseMessagesHaveXPathLocators() throws Exception
    {
        String config = "test/rulesets/validate.xml";
        String src = "test/test-cases/instance+dtd_INVALID.xml";
        String dest = "test/dest/testDTDParseMessagesHaveXPathLocators.xml";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 3, doc, "//probe:message" );

        //TODO: is it possible to get locators for attributes??
        //        assertValueOfXPath( "/foo[1]/@id", doc, "//probe:expression[preceding-sibling::probe:line = '7']" );
        assertValueOfXPath( "/foo[1]/bar[1]/lsdfjlaksjdf[1]", doc,
                "//probe:expression[preceding-sibling::probe:line = '9']" );
        assertValueOfXPath( "/foo[1]/bar[1]", doc,
                "//probe:expression[preceding-sibling::probe:line = '10']" );

    }


    public void testRELAXNGParseMessagesHaveXPathLocators() throws Exception
    {
        String config = "test/rulesets/relaxng-schema-compact-syntax.xml";
        String src = "test/test-cases/invalid-probe-report.xml";
        String dest = "test/dest/testRELAXNGParseMessagesHaveXPathLocators.xml";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 2, doc, "//probe:message" );

        assertValueOfXPath(
                "/*[local-name()='silcn' and namespace-uri()='http://silcn.org/200309'][1]"
                        + "/*[local-name()='report' and namespace-uri()='http://silcn.org/200309'][1]"
                        + "/*[local-name()='expression-language-declaration' and namespace-uri()='http://silcn.org/200309'][1]"
                        + "/foo[1]", doc,
                "//probe:expression[preceding-sibling::probe:line = '19']" );

        assertValueOfXPath(
                "/*[local-name()='silcn' and namespace-uri()='http://silcn.org/200309'][1]"
                        + "/*[local-name()='report' and namespace-uri()='http://xmlprobe.com/200312'][1]"
                        + "/*[local-name()='messages' and namespace-uri()='http://xmlprobe.com/200312'][1]",
                doc, "//probe:expression[preceding-sibling::probe:line = '15']" );
    }
    
    public void testLineColumnNos() throws Exception
    {
        String config = "test/rulesets/line-column-nos.xml";
        String src = "test/test-cases/line-column-nos.xml";
        String dest = "test/dest/line-column-nos.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc,
                "//silcn:expression[ . = '/' and ../probe:line = '1' and ../probe:column = '1' ]" );

        assertXPathTrue( doc,
                "//silcn:expression[ . = '/q:foo[1]' and ../probe:line = '1' and ../probe:column = '40' ]" );

        assertXPathTrue( doc,
                "//silcn:expression[ . = '/q:foo[1]/@c' and ../probe:line = '1' and ../probe:column = '40' ]" );

        assertXPathTrue( doc,
                "//silcn:expression[ . = '/q:foo[1]/@b' and ../probe:line = '1' and ../probe:column = '40' ]" );

        assertXPathTrue( doc,
                "//silcn:expression[ . = '/q:foo[1]/@a' and ../probe:line = '1' and ../probe:column = '40' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/text()[1]' and ../probe:line = '1' and ../probe:column = '40' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/processing-instruction()[1]' and ../probe:line = '2' and ../probe:column = '35' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/text()[2]' and ../probe:line = '2' and ../probe:column = '35' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/comment()[1]' and ../probe:line = '5' and ../probe:column = '15' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/bar[1]' and ../probe:line = '5' and ../probe:column = '21' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/text()[3]' and ../probe:line = '5' and ../probe:column = '21' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/blort[1]' and ../probe:line = '6' and ../probe:column = '20' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/q:foo[1]/text()[4]' and ../probe:line = '6' and ../probe:column = '20' ]" );

        assertXPathTrue( doc,
                "//silcn:expression[ . = '/comment()[1]' and ../probe:line = '17' and ../probe:column = '34' ]" );

    }


    //    public void testAttributeLocator2() throws Exception
    //    {
    //        XMLReader xr = XMLReaderFactory.createXMLReader();
    //        xr.setFeature( "http://xml.org/sax/features/validation", true );
    //
    //        xr.setErrorHandler( new ErrorHandler() {
    //            public void error( SAXParseException exception )
    //            {
    //                System.err.println( exception.getMessage() + " line "
    //                        + exception.getLineNumber() + " column " + exception.getColumnNumber() );
    //            }
    //
    //
    //            public void fatalError( SAXParseException exception )
    //            {
    //                System.err.println( exception.getMessage() + " line "
    //                        + exception.getLineNumber() + " column " + exception.getColumnNumber() );
    //            }
    //
    //
    //            public void warning( SAXParseException exception )
    //            {
    //                System.err.println( exception.getMessage() + " line "
    //                        + exception.getLineNumber() + " column " + exception.getColumnNumber() );
    //            }
    //        } );
    //
    //        xr.parse( "test/test-cases/instance+dtd_INVALID.xml" );
    //    }    
}
