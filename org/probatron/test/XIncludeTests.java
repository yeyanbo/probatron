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

public class XIncludeTests extends TestsBase
{
    public void testXInclude() throws Exception
    {
        String config = "test/rulesets/xinclude.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/xinclude.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//silcn:node) = 1" );

    }
    
    public void testSILCNInstanceIsXIncluded() throws Exception
    {
        String config = "test/rulesets/testXIncludeRuleset.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/testXIncludeRuleset.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
    }
}
