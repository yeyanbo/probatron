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
 * Created on 27 Aug 2008
 */
package org.probatron.test;

import org.apache.log4j.Logger;
import org.probatron.ProbatronSession;
import org.probatron.ShailNavigator;

import com.griffinbrown.xmltool.Constants;

public class SchematronReportTests extends TestsBase
{
    private static Logger logger = Logger.getLogger( SchematronReportTests.class );


    /**
     * @see org.probatron.test.TestsBase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        getNamespaceContext().addNamespace( "svrl", "http://purl.oclc.org/dsdl/svrl" ); //the SVRL namespace
    }


    public void testPhaseAll() throws Exception
    {
        String config = "test/rulesets/schema47.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/schema47.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        //validate SVRL output using Jing
        String validationResult = dest + ".validation";
        String errs = "test/dest/err.log";
        int retVal = validateWithJing( "-c schema/svrl.rnc " + dest + " 1>" + validationResult
                + " 2>" + errs );

        assertFileEquals( errs, "" );
        //N.B. the SVRL schema requires at least one fired-rule element, but this may not happen!!
        assertFileStartsWith(
                validationResult,
                "E:\\eclipse\\workspace\\probatron\\test\\dest\\schema47.out:8:18: error: required elements missing" );
    }


    public void testDefaultPhase() throws Exception
    {
        System.setProperty( "active-phase", "#DEFAULT" );

        String config = "test/rulesets/schema30.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/testDefaultPhase.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        //validate SVRL output using Jing
        String validationResult = dest + ".validation";
        String errs = "test/dest/err.log";
        int retVal = validateWithJing( "-c schema/svrl.rnc " + dest + " 1>" + validationResult
                + " 2>" + errs );

        assertFileEquals( errs, "" );
        //N.B. the SVRL schema requires at least one fired-rule element, but this may not happen!! 
        assertFileEquals(
                validationResult,
                "E:\\eclipse\\workspace\\probatron\\test\\dest\\testDefaultPhase.out:5:21: error: unfinished element\r\n" );
    }


    public void testFiredRulesReported() throws Exception
    {
        String config = "test/rulesets/schema48.sch";
        String src = "test/test-cases/sample-article-2.xml";
        String dest = "test/dest/testFiredRulesReported.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        //validate SVRL output using Jing
        String validationResult = dest + ".validation";
        String errs = "test/dest/testFiredRulesReported.err";
        int retVal = validateWithJing( "-c schema/svrl.rnc " + dest + " 1>" + validationResult
                + " 2>" + errs );

        assertFileEquals( errs, "" );
        //TODO: SVRL schema is broke
        //        assertFileEquals( validationResult, "" );
    }


    public void testFatalErrorReported() throws Exception
    {
        String config = "test/rulesets/schema48.sch";
        String src = "test/test-cases/sample-article-2-fatal.xml";
        String dest = "test/dest/testFatalErrorReported.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );

        //validate SVRL output using Jing
        String validationResult = dest + ".validation";
        String errs = "test/dest/testFatalErrorReported.err";
        int retVal = validateWithJing( "-c schema/svrl.rnc " + dest + " 1>" + validationResult
                + " 2>" + errs );

        assertFileEquals( errs, "" );
        //TODO: SVRL schema is broke
        //        assertFileEquals( validationResult, "" );
    }


    /**
     * This test uses the schema available at http://www.schematron.com/validators/universalTests.sch
     */
    public void testSchematronUniversalTests() throws Exception
    {
        String config = "test/rulesets/universalTests.sch";
        String src = "test/test-cases/sample-article-2.xml";
        String dest = "test/dest/universalTests.svrl";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 2, doc, "//svrl:failed-assert" );
        assertCountXPath( 1, doc, "//svrl:failed-assert[ starts-with( svrl:text, 'U7:' ) ]" );
        assertCountXPath( 1, doc, "//svrl:failed-assert[ starts-with( svrl:text, 'U9:' ) ]" );
        assertCountXPath( 4, doc, "//svrl:fired-rule" );

        //validate SVRL output using Jing
        String validationResult = dest + ".validation";
        String errs = "test/dest/err.log";
        int retVal = validateWithJing( "-c schema/svrl.rnc " + dest + " 1>" + validationResult
                + " 2>" + errs );

        assertFileEquals( errs, "" );
        //TODO: SVRL schema is broke
        //        assertFileEquals( validationResult, "" );
    }


    public void testSchematronUniversalTestsTextFormat() throws Exception
    {
        String config = "test/rulesets/universalTests.sch";
        String src = "test/test-cases/sample-article-2.xml";
        String dest = "test/dest/universalTests.txt";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        runXMLProbe( x );

        assertFileEquals(
                dest,
                "U7: This assertion should always fail.:/line 1, column 1\n"
                        + "U8: This report should always succeeed.:/line 1, column 1\n"
                        + "U9: This assertion should always fail.:/*[local-name()='component' "
                        + "and namespace-uri()='http://www.wiley.com/namespaces/wileyng'][1]line 5, column 76\n"
                        + "U10: This report should always succeeed.:/*[local-name()='component' "
                        + "and namespace-uri()='http://www.wiley.com/namespaces/wileyng'][1]line 5, column 76\n" );
    }


    public void testNonFatalErrorReported() throws Exception
    {
        String config = "test/rulesets/schema48.sch";
        String src = "test/test-cases/sample-article-2.xml";
        String dest = "test/dest/testNonFatalErrorReported.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        //validate SVRL output using Jing
        String validationResult = dest + ".validation";
        String errs = "test/dest/err.log";
        int retVal = validateWithJing( "-c schema/svrl.rnc " + dest + " 1>" + validationResult
                + " 2>" + errs );

        //TODO: SVRL schema is broke
        assertFileEquals( errs, "" );
        //        assertFileEquals( validationResult, "" );
    }


    public void testNameReportedInAssert() throws Exception
    {
        String config = "test/rulesets/schema51.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNameReportedInAssert.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='wng:component should have a publisherInfo child']" );
    }


    public void testNameReportedInReport() throws Exception
    {
        String config = "test/rulesets/schema52.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNameReportedInReport.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath(
                1,
                doc,
                "//svrl:successful-report[@test=\"foo\"][svrl:text=\"wng:publisherInfo shouldn't have a foo child\"]" );
    }


    public void testNamePathReportedInAssert() throws Exception
    {
        String config = "test/rulesets/schema53.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNamePathReportedInAssert.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a wng:productMeta']" );
    }


    public void testNamePathReportedInReport() throws Exception
    {
        String config = "test/rulesets/schema55.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNamePathReportedInReport.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a wng:productMeta']" );
    }


    public void testNamePathInAssertInvalidXPath() throws Exception
    {
        String config = "test/rulesets/schema54.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNamePathInAssertInvalidXPath.out";
        String errs = "test/dest/testNamePathInAssertInvalidXPath.err";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x, errs );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a ']" );

        assertFileContains( errs, "XMLProbe:[error]:error evaluating XPath expression 'name(\\\\)': error compiling XPath expression: Unexpected '\\\\)'" );
    }


    public void testNamePathInReportInvalidXPath() throws Exception
    {
        String config = "test/rulesets/schema56.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNamePathInReportInvalidXPath.out";
        String errs = "test/dest/testNamePathInReportInvalidXPath.err";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x, errs );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a ']" );

        assertFileContains( errs, "XMLProbe:[error]:error evaluating XPath expression 'name(\\\\)': error compiling XPath expression: Unexpected '\\\\)'" );
    }
    
    public void testValueOfReportedInAssert() throws Exception
    {
        String config = "test/rulesets/schema58.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfReportedInAssert.out";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a wng:publisherInfo']" );
    }
    
    public void testValueOfReportedInReport() throws Exception
    {
        String config = "test/rulesets/schema57.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfReportedInReport.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a wng:publisherInfo']" );
    }    
    
    public void testValueOfReportedInAssertInvalidXPath() throws Exception
    {
        String config = "test/rulesets/schema59.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfReportedInAssertInvalidXPath.out";
        String errs = "test/dest/testValueOfReportedInAssertInvalidXPath.err";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x, errs );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a ']" );

        assertFileContains( errs, "XMLProbe:[error]:error evaluating XPath expression '\\\\': error compiling XPath expression: Unexpected '\\\\'" );
    }
    
    public void testValueOfReportedInReportInvalidXPath() throws Exception
    {
        String config = "test/rulesets/schema60.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfReportedInReportInvalidXPath.out";
        String errs = "test/dest/testValueOfReportedInAssertInvalidXPath.err";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x, errs );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a ']" );

        assertFileContains( errs, "XMLProbe:[error]:error evaluating XPath expression '\\\\': error compiling XPath expression: Unexpected '\\\\'" );
    } 
    
    public void testValueOfVariableReportedInAssert() throws Exception
    {
        String config = "test/rulesets/schema61.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfVariableReportedInAssert.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a 16']" );
    }
    
    public void testValueOfVariableReportedInReport() throws Exception
    {
        String config = "test/rulesets/schema62.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfVariableReportedInReport.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a 16']" );
    }
    
    public void testValueOfVariableNoCrosstalkInReport() throws Exception
    {
        String config = "test/rulesets/schema63.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfVariableNoCrosstalkInReport.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
        "//svrl:successful-report[svrl:text='root element found 999']" );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a ']" );

        //TODO: test for error message on stderr about invalid <name/> XPath here
    }
    
    public void testValueOfVariableNoCrosstalkInAssert() throws Exception
    {
        String config = "test/rulesets/schema64.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfVariableNoCrosstalkInAssert.out";
        String errs = "test/dest/testValueOfVariableNoCrosstalkInAssert.err";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x, errs );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
        "//svrl:successful-report[svrl:text='root element found 999']" );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a ']" );

        assertFileContains( errs, "XMLProbe:[error]:error evaluating XPath expression '$ruleVar1': error evaluating XPath expression as string: Variable ruleVar1" );
    }
    
    public void testValueOfVariableReportedInAssertDynamicValue() throws Exception
    {
        String config = "test/rulesets/schema65.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfVariableReportedInAssertDynamicValue.out";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
        "//svrl:successful-report[svrl:text='root element found 999']" );
        assertCountXPath( 1, doc,
                "//svrl:failed-assert[svrl:text='I am a foo and I live in a wng:publisherInfo']" );
    }
    
    public void testValueOfVariableReportedInReportDynamicValue() throws Exception
    {
        String config = "test/rulesets/schema66.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testValueOfVariableReportedInReportDynamicValue.out";
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertCountXPath( 1, doc,
        "//svrl:successful-report[svrl:text='root element found 999']" );
        assertCountXPath( 1, doc,
                "//svrl:successful-report[svrl:text='I am a foo and I live in a wng:publisherInfo']" );
    }
    
    public void testSchemaTitleAppearsInReport() throws Exception
    {
        String config = "test/rulesets/schema68.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSchemaTitleAppearsInReport.out";
        
        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );
        runXMLProbe( x );
        
        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );        
        
        assertXPathEquals( "Schema title", doc, "/svrl:schematron-output/@title" );
    }
    
    

    //TODO: check all namespaces used are also declared in SVRL instance as xmlns:*   

    //TODO: schema/title
    //TODO: schematron-output/@schemaVersion
}
