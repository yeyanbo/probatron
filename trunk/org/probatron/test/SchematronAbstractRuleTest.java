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
 * Created on 24 Jan 2008
 */
package org.probatron.test;

import java.util.List;

import org.probatron.ProbatronSession;
import org.probatron.ShailNavigator;

import com.griffinbrown.schematron.Pattern;
import com.griffinbrown.schematron.Rule;
import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.xmltool.Constants;

public class SchematronAbstractRuleTest extends TestsBase
{
    protected void setUp() throws Exception
    {
        super.setUp();
        //        System.setProperty( "debug", "true" );
        getNamespaceContext().addNamespace( "svrl", "http://purl.oclc.org/dsdl/svrl" ); //the SVRL namespace
    }


    public void testAbstractRuleResolution() throws Exception
    {
        String config = "test/rulesets/schema36.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractRuleResolution.out";

        runXMLProbe( config, src, dest );

        String toTestFor = ":/line 1, column 1\ngotta have a :/line 1, column 1\nblah:/line 1, column 1\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testAbstractRuleResolutionSVRL() throws Exception
    {
        String config = "test/rulesets/schema36.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractRuleResolution.svrl";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );

        runXMLProbe( x );

        int doc = ShailNavigator.getInstance().getDocument( dest );
        assertXPathTrue( doc, "count(//svrl:failed-assert )=3" );
        assertXPathTrue( doc, "count(//svrl:fired-rule[@id='c'] )=1" );
    }


    public void testNonAbstractRuleCantExtendItself() throws Exception
    {
        String config = "test/rulesets/schema37.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNonAbstractRuleCantExtendItself.out";

        runXMLProbe( config, src, dest );

        String toTestFor = " rule attribute value 'c' does not match the id attribute of an abstract rule";

        assertFileContains( dest, toTestFor );
    }


    public void testAbstractRuleCantExtendItself() throws Exception
    {
        String config = "test/rulesets/schema38.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractRuleCantExtendItself.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "[error]:error resolving abstract rule for rule [id='c']";
        String toTestForToo = "abstract rules must not extend themselves";

        assertFileContains( dest, toTestFor );
        assertFileContains( dest, toTestForToo );
    }


    public void testAbstractRuleCantExtendItself2() throws Exception
    {
        String config = "test/rulesets/schema39.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractRuleCantExtendItself2.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "[error]:error resolving abstract rule for rule ";
        String toTestForToo = "abstract rules must not extend themselves: rule id='b'";

        assertFileContains( dest, toTestFor );
        assertFileContains( dest, toTestForToo );
    }


    public void testAbstractRuleDuplicatedExtendsInstructions() throws Exception
    {
        String config = "test/rulesets/schema40.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractRuleDuplicateExtendsInstructions.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "[error]:error resolving abstract rule for rule [id='c']";
        String toTestForToo = "found multiple extends instructions for abstract rule [id='a']";

        assertFileContains( dest, toTestFor );
        assertFileContains( dest, toTestForToo );
    }


    public void testAbstractRuleDuplicatedExtendsInstructions2() throws Exception
    {
        String config = "test/rulesets/schema41.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractRuleDuplicateExtendsInstructions2.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "[error]:error resolving abstract rule for rule [id='c']";
        String toTestForToo = "found multiple extends instructions for abstract rule [id='a']";


        assertFileContains( dest, toTestFor );
        assertFileContains( dest, toTestForToo );
    }


    public void testResolvedRuleVariablesAndAssertionCount() throws Exception
    {
        String config = "test/rulesets/schema42.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testResolvedRulesVariables.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        List patterns = ( ( SchematronConfiguration )x.getConfig() ).getSchema()
                .getActivePatterns();
        Pattern p = ( Pattern )patterns.get( 0 );

        assertEquals( 1, patterns.size() );

        List rules = p.getRules();

        assertEquals( 1, rules.size() );

        Rule rule = ( Rule )rules.get( 0 );

        runXMLProbe( x );

        assertEquals( 4, rule.getAssertions().size() );
        assertEquals( 1, rule.getVariables().size() );

        String toTestFor = ":/line 1, column 1\ngotta have a :/line 1, column 1\nblah:/line 1, column 1\n";
        assertFileEquals( dest, toTestFor );
    }


    public void testResolvedRuleDuplicateVariable() throws Exception
    {
        String config = "test/rulesets/schema43.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testResolvedRuleDuplicateVariable.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        List patterns = ( ( SchematronConfiguration )x.getConfig() ).getSchema()
                .getActivePatterns();
        Pattern p = ( Pattern )patterns.get( 0 );

        assertEquals( 1, patterns.size() );

        List rules = p.getRules();

        assertEquals( 1, rules.size() );

        Rule rule = ( Rule )rules.get( 0 );
        assertEquals( rule.getId(), "d" );

        runXMLProbe( x );

        assertEquals( 5, rule.getAssertions().size() );
        assertEquals( 2, rule.getVariables().size() ); //one of these is a DUPLICATE!        

        String toTestFor = "XMLProbe:[FATAL]:duplicate variable name: ruleVar1\n";

        assertFileEquals( dest, toTestFor );
    }

}
