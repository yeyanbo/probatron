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
 * Created on 23 Jan 2008
 */
package org.probatron.test;

import org.apache.log4j.Logger;
import org.probatron.ProbatronSession;
import org.probatron.ShailNavigator;

import com.griffinbrown.xmltool.Constants;

public class SchematronQueryEvaluationTests extends TestsBase
{
    private static Logger logger = Logger.getLogger( SchematronQueryEvaluationTests.class );


    /**
     * @see org.probatron.test.TestsBase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        getNamespaceContext().addNamespace( "svrl", "http://purl.oclc.org/dsdl/svrl" ); //the SVRL namespace
    }


    public void testQueryEvaluation1() throws Exception
    {
        String config = "test/rulesets/schema33.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testQueryEvaluation1.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "wng:component should have a publisherInfo child:/wng:component[1]line 5, column 81\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testRuleContextBadExpression() throws Exception
    {
        String config = "test/rulesets/schema34.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testRuleContextBadExpression.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "error evaluating context expression ";
        String toTestForToo = ": error compiling XPath expression: ";

        assertFileContains( dest, toTestFor );
        assertFileContains( dest, toTestForToo );
    }


    public void testLocalVariableDoesntCompile() throws Exception
    {
        String config = "test/rulesets/schema35.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testLocalVariableDoesntCompile.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "error compiling XPath variable [name='ruleVar1']; "
                + "expression='': error compiling XPath expression: Unexpected ''";

        assertFileContains( dest, toTestFor );
    }


    /**
     * See s6.3 of Schematron standard: a rule should only fire if the context has NOT been 
     * matched by a lexically previous rule. 
     */
    public void testRuleContextAlreadyMatched() throws Exception
    {
        String config = "test/rulesets/schema49.sch";
        String src = "test/test-cases/sample-article-2.xml";
        String dest = "test/dest/testRuleContextAlreadyMatched.svrl";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 2, doc, "//svrl:successful-report" );
        assertCountXPath( 0, doc, "//svrl:failed-assert" );
        assertCountXPath(
                1,
                doc,
                "//svrl:successful-report[@location='/wng:component[1]/wng:header[1]/wng:issueMeta[1]/wng:numberingGroup[1]/wng:numbering[1]']" );
        assertCountXPath(
                1,
                doc,
                "//svrl:successful-report[@location='/wng:component[1]/wng:header[1]/wng:issueMeta[1]/wng:numberingGroup[1]/wng:numbering[2]']" );
    }


    public void testRuleContextNonNodeset() throws Exception
    {
        String config = "test/rulesets/schema67.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testRuleContextNonNodeset.svrl";
        String errs = "test/dest/testRuleContextNonNodeset.errs";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x, errs );
        
        assertFileContains( errs, "XMLProbe:[FATAL]:rule context expression must evaluate to a node-set: got true" );
    }


    public void testRuleContextNotAllowedType() throws Exception
    {
    //TODO????
    }


    public void testLocalVariableContextSetCorrectly() throws Exception
    {
    //TODO
    }


    public void testAbsolutePathInQueryUsesDocumentRootAsContext() throws Exception
    {
    //TODO
    }

}
