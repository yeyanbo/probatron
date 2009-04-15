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

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;


public class StressTests extends TestsBase
{
    private static Logger logger = Logger.getLogger( StressTests.class );


    public void testStressTest() throws Exception
    {
        String config = "test/rulesets/stress-test.xml";
        String src = "test/test-cases/dsupps.xml";
        String dest = "test/dest/stress-test.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        logger.debug( "doc=" + doc );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertCountXPath( 1, doc, "//silcn:id/parent::*" );
        assertCountXPath( 1, doc, "//silcn:node/preceding-sibling::silcn:id" );
        assertCountXPath( 1, doc, "//silcn:id/following-sibling::silcn:node" );
        assertXPathTrue( doc, "count( //silcn:node[ ../silcn:id = 'count-all-elems' ] ) = 1" );

        assertXPathEquals( "40681", doc,
                "//silcn:node[ ../silcn:id = 'count-all-elems' ]/probe:text" );

    }
}
