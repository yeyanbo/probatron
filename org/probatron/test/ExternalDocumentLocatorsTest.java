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

public class ExternalDocumentLocatorsTest extends TestsBase
{
    public void testExternalDocumentLocators() throws Exception
    {
        String config = "test/rulesets/external-document-locators.xml";
        String src = "test/test-cases/multi-root-instance.xml";
        String dest = "test/dest/external-document-locators.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'atts' ]" );
        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'foo' ]" );

        //external document locators should not be -1!!
        assertXPathTrue(
                doc,
                "//silcn:node[ ../silcn:id = 'atts' and silcn:expression = '/' and probe:column != '-1' and probe:line != '-1' ]" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'foo' and silcn:expression = '/processing-instruction()[1]' "
                        + "and probe:column = '20' and probe:line = '3' ]" );
    }
    
    public void testExternalDocumentLocators2() throws Exception
    {
        String config = "test/rulesets/external-document-locators2.xml";
        String src = "test/test-cases/multi-root-instance.xml";
        String dest = "test/dest/external-document-locators2.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'atts' ]" );
        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'foo' ]" );

        //external document locators should not be -1!!
        assertXPathTrue(
                doc,
                "//silcn:node[ ../silcn:id = 'atts' and silcn:expression = '/' and probe:column != '-1' and probe:line != '-1' ]" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'foo' and silcn:expression = '/comment()[1]' "
                        + "and probe:column = '50' and probe:line = '3' ]" );
    }    
}
