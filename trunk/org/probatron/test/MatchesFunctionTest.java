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
 * Created on 2 Dec 2008
 */
package org.probatron.test;

import org.probatron.ShailNavigator;


public class MatchesFunctionTest extends TestsBase
{
    
    
    /**
     * @see org.probatron.test.TestsBase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        System.clearProperty( "error-format" );
        getNamespaceContext().addNamespace( "svrl", "http://purl.oclc.org/dsdl/svrl" );
    }

    public void testMatchesWrongArity() throws Exception
    {
        String config = "test/rulesets/matches-wrong-arity.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/matches-wrong-arity.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "error evaluating XPath expression: fn:matches() expects two arguments";
        assertFileContains( dest, toTestFor );
    }
    
    public void testMatchesWrongArity2() throws Exception
    {
        String config = "test/rulesets/matches-wrong-arity2.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/matches-wrong-arity2.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "error evaluating XPath expression: fn:matches() expects two arguments [flags argument not supported]";
        assertFileContains( dest, toTestFor );
    }


    public void testMatchesBadSecondArg() throws Exception
    {
        String config = "test/rulesets/matches-bad-first-arg.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/matches-bad-first-arg.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "fn:matches() second argument must be of type string";
        assertFileContains( dest, toTestFor );
    }


    public void testMatchesBadRegexp() throws Exception
    {
        String config = "test/rulesets/matches-bad-regexp.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/matches-bad-regexp.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "error evaluating XPath expression: Error compiling regular expression: Unclosed group near index 1";
        assertFileContains( dest, toTestFor );
    }
    
    public void testMatchesNoMatchFound() throws Exception
    {
        System.setProperty( "error-format", "xml" );
        
        String config = "test/rulesets/matches-no-match-found.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/matches-no-match-found.out";

        runXMLProbe( config, src, dest );
        
        int doc = ShailNavigator.getInstance().getDocument( dest );
        
        assertCountXPath( 1, doc, "//svrl:fired-rule[ @context = '//*' ]" );
        assertCountXPath( 0, doc, "//svrl:failed-assert" );
    }
    
    public void testMatchesMatchFound() throws Exception
    {
        System.setProperty( "error-format", "xml" );
        
        String config = "test/rulesets/matches-match-found.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/matches-match-found.out";

        runXMLProbe( config, src, dest );
        
        int doc = ShailNavigator.getInstance().getDocument( dest );
        
        assertCountXPath( 1, doc, "//svrl:fired-rule[ @context = '//*' ]" );
        assertCountXPath( 1, doc, "//svrl:failed-assert" );
    }

    //    public void testMatchesVariable() throws Exception
    //    {
    //        String config = "test/rulesets/oup.xml";
    //        String src = "test/test-cases/oup.xml";
    //        String dest = "test/dest/testGlobalRegexpVariable.out";
    //
    //        runXMLProbe( config, src, dest );
    //
    //        int doc = ShailNavigator.getInstance().getDocument( dest );
    //
    //        assertCountXPath( 0, doc, "//probe:message" );
    //        assertCountXPath( 2, doc, "//silcn:node" );
    //
    //        assertXPathEquals( "@doi='10.1093/...' does not match product id DOI 'med-'", doc,
    //                "(//silcn:node)[../silcn:id='test']/probe:text" );
    //        assertXPathEquals( "@doi='10.1093/...'; 10.1093/...", doc,
    //                "(//silcn:node)[../silcn:id='test2']/probe:text" );
    //    }

}
