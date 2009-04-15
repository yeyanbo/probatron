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
 * Created on 16 Oct 2008
 */
package org.probatron.test;

import org.probatron.ShailNavigator;

public class EqualityTests extends TestsBase
{
    public void testEqualsTwoNodesets() throws Exception
    {
        String config = "test/rulesets/equalsTwoNodesets.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testEqualsTwoNodesets.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue(
                doc,
                "//silcn:node[ silcn:expression = '/a[1]' and probe:text = 'the nodesets of elements b and c are equal' ]" );
    }


    //tests a 'foo != bar'-type expression, where both foo and bar are node-sets
    public void testNotEqualsTwoNodesets() throws Exception
    {
        String config = "test/rulesets/oup2.xml";
        String src = "test/test-cases/oup4.xml";
        String dest = "test/dest/testNotEqualsTwoNodesets.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node[../silcn:id='test']" );
    }


    public void testEqualsStringNodeset() throws Exception
    {
        String config = "test/rulesets/equalsStringNodeset.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testEqualsStringNodeset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//b = 'blort'\" ]" );
    }


    public void testNotEqualsStringNodeset() throws Exception
    {
        String config = "test/rulesets/notEqualsStringNodeset.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testNotEqualsStringNodeset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//b != 'phooey'\" ]" );
    }


    public void testEqualsNumberNodeset() throws Exception
    {
        String config = "test/rulesets/equalsNumberNodeset.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testEqualsNumberNodeset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//c = 12\" ]" );
    }


    public void testNotEqualsNumberNodeset() throws Exception
    {
        String config = "test/rulesets/notEqualsNumberNodeset.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testNotEqualsNumberNodeset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//c != 256\" ]" );
    }


    public void testEqualsBooleanNodeset() throws Exception
    {
        //this tests that true is equal to a non-empty nodeset
        String config = "test/rulesets/equalsBooleanNodeset.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testEqualsBooleanNodeset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//c = true()\" ]" );

    }


    public void testEqualsBooleanNodeset2() throws Exception
    {
        //this tests that false() is equal to an empty nodeset
        String config = "test/rulesets/equalsBooleanNodeset2.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testEqualsBooleanNodeset2.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//d = false()\" ]" );
    }


    public void testNotEqualsBooleanNodeset() throws Exception
    {
        String config = "test/rulesets/notEqualsBooleanNodeset.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testNotEqualsBooleanNodeset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//d != true()\" ]" );
    }


    public void testNotEqualsBooleanNodeset2() throws Exception
    {
        String config = "test/rulesets/notEqualsBooleanNodeset2.xml";
        String src = "test/test-cases/equals.xml";
        String dest = "test/dest/testNotEqualsBooleanNodeset2.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathTrue( doc,
                "//silcn:node[ silcn:expression = \"/a[1]\" and probe:text = \"//c != false()\" ]" );
    }

}
