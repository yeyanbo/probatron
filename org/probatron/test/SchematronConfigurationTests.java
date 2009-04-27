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
 * Created on 17 Dec 2007
 */
package org.probatron.test;

import java.util.Iterator;
import java.util.List;

import org.probatron.Query;
import org.probatron.QueryEvaluator;
import org.probatron.SchematronQAHandler;
import org.probatron.ShailNavigator;
import org.probatron.Variable;
import org.probatron.ProbatronSession;

import com.griffinbrown.schematron.NamespaceDeclaration;
import com.griffinbrown.schematron.Phase;
import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.schematron.SchematronSchema;
import com.griffinbrown.xmltool.Configuration;

public class SchematronConfigurationTests extends TestsBase
{

    /*
     * @see com.xmlprobe.test.TestsBase#setUp()
     */
    protected void setUp() throws Exception
    {
        //        System.setProperty( "debug", "true" );
        super.setUp();
        System.clearProperty( "relaxng-schema-location" );
    }


    public void testInvalid() throws Exception
    {
        String config = "test/rulesets/schema1.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/invalid-schematron-schema.out";

        runXMLProbe( config, src, dest );

        String startsWith = "Probatron:[FATAL]:Schematron schema is invalid: cannot proceed until schema errors are fixed";

        String toTestFor = "Probatron:file:/E:/eclipse/workspace/probatron/test"
                + "/rulesets/schema1.sch:3:10:[error]:unfinished element";

        assertFileStartsWith( dest, startsWith );
        assertFileContains( dest, toTestFor );
    }


    public void testInclusionSucceeds() throws Exception
    {
        String config = "test/rulesets/schema2.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema2.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileEquals( dest, toTestFor );
    }

    //TODO: either set the transformer explicitly, or abandon XSLT in favour of SAX 
    public void testInclusionSAXException() throws Exception
    {
        String config = "test/rulesets/schema3.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema3.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "[FATAL]:error resolving Schematron schema inclusion: "
                + "Error reported by XML parser; SystemID: file:/E:/eclipse/workspace/probatron/test/test-cases/; "
                + "Line#: 1; Column#: 1\n"
                + "Probatron:[warning]:error resolving Schematron schema inclusion: org.xml.sax.SAXParseException: Content is not allowed in prolog.";

        assertFileContains( dest, toTestFor );
    }


    public void testInclusionIOException() throws Exception
    {
        String config = "test/rulesets/schema4.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema4.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[warning]:error resolving Schematron schema inclusion: "
                + "Failure reading file:/E:/eclipse/workspace/probatron/test/test-cases/!!.xml";

        assertFileContains( dest, toTestFor );
    }


    public void testIncludedInclusion() throws Exception
    {
        String config = "test/rulesets/schema5.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema5.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:Schematron schema error at "
                + "/*[local-name()='schema' and namespace-uri()="
                + "'http://purl.oclc.org/dsdl/schematron'][1]"
                + "/*[local-name()='include' and namespace-uri()"
                + "='http://purl.oclc.org/dsdl/schematron'][1]"
                + ": included documents must not themselves contain Schematron inclusion instructions\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testIncludedInclusion2() throws Exception
    {
        String config = "test/rulesets/schema7.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema7.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:Schematron schema error at "
                + "/*[local-name()='schema' and namespace-uri()="
                + "'http://purl.oclc.org/dsdl/schematron'][1]"
                + "/*[local-name()='include' and namespace-uri()="
                + "'http://purl.oclc.org/dsdl/schematron'][1]"
                + ": "
                + "included documents must not themselves contain Schematron inclusion instructions";

        assertFileContains( dest, toTestFor );
    }


    public void testInvalidAfterInclusionsResolved() throws Exception
    {
        String config = "test/rulesets/schema6.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema6.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:file:/E:/eclipse/workspace/probatron/test/rulesets/schema6.sch:3:12:[error]:element \"active\" from namespace "
                + "\"http://purl.oclc.org/dsdl/schematron\" not allowed in this context";

        assertFileContains( dest, toTestFor );
    }


    public void testUnsupportedQueryBinding() throws Exception
    {
        String config = "test/rulesets/schema8.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema8.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:Schematron query language binding 'asdfasdf' not supported\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testSupportedQueryBindingXSLT() throws Exception
    {
        String config = "test/rulesets/schema9.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema9.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:Schematron query language binding 'XSLT' not supported\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testSupportedQueryBindingXSLT2() throws Exception
    {
        String config = "test/rulesets/schema10.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema10.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:Schematron query language binding 'XSLT2' not supported\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testSupportedQueryBindingXPath() throws Exception
    {
        String config = "test/rulesets/schema11.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema11.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileEquals( dest, toTestFor );
    }


    public void testSupportedQueryBindingXPath2() throws Exception
    {
        String config = "test/rulesets/schema12.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema12.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:Schematron query language binding 'xpath2' not supported\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testActiveIdRefNotAPatternId() throws Exception
    {
        String config = "test/rulesets/schema13.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema13.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[error]:Schematron schema validation error at "
                + "/*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/"
                + "dsdl/schematron'][1]/*[local-name()='phase' and namespace-uri()='"
                + "http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='active' "
                + "and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][2]/@pattern: "
                + "pattern attribute value 'non-existent-pattern-id' does not match the id attribute of a pattern\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testActiveIdRefIsAPatternId() throws Exception
    {
        String config = "test/rulesets/schema14.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema14.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileEquals( dest, toTestFor );
    }


    public void testPatternIsAIdRefNotAPatternId() throws Exception
    {
        String config = "test/rulesets/schema15.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema15.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[error]:Schematron schema validation error at "
                + "/*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/"
                + "dsdl/schematron'][1]/*[local-name()='pattern' and namespace-uri()='"
                + "http://purl.oclc.org/dsdl/schematron'][4]/@is-a: "
                + "is-a attribute value 'a1dddddddddd' does not match the id attribute of an abstract pattern\n";

        assertFileContains( dest, toTestFor );

        toTestFor = "Probatron:[FATAL]:error resolving abstract pattern: abstract pattern "
                + "is-a='a1dddddddddd' referenced at /*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='pattern' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][4] not found\n";

        assertFileStartsWith( dest, toTestFor );
    }


    public void testPatternIsAIdRefIsAPatternId() throws Exception
    {
        String config = "test/rulesets/schema16.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema16.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileEquals( dest, toTestFor );
    }


    public void testExtendsRuleBadIdRef() throws Exception
    {
        String config = "test/rulesets/schema17.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema17.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[error]:Schematron schema validation error at "
                + "/*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='pattern' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='rule' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='extends' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/@rule: "
                + "rule attribute value 'non-existent-rule' does not match the id attribute of an abstract rule\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testExtendsRuleGoodIdRef() throws Exception
    {
        String config = "test/rulesets/schema18.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema18.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileEquals( dest, toTestFor );
    }


    public void testVariableNameMatchesParamName() throws Exception
    {
        String config = "test/rulesets/schema19.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema19.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[error]:Schematron schema validation error at "
                + "/*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='let' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/@name: variable name 'foo' "
                + "should not match the name of a pattern parameter\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testVariableNameParamNameOK() throws Exception
    {
        String config = "test/rulesets/schema20.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema20.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileEquals( dest, toTestFor );
    }


    public void testDuplicateIds() throws Exception
    {
        String config = "test/rulesets/schema22.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema22.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "duplicate id 'asdfaf'";

        assertFileContains( dest, toTestFor );

        toTestFor = "duplicate id 'phase1'";

        assertFileContains( dest, toTestFor );
    }


    public void testAssertionCount() throws Exception
    {
        String config = "test/rulesets/schema23.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema23.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        List queries = x.getConfig().getQueries();

        assertEquals( "wrong number of assertions", 2, queries.size() );

        assertTrue( queries.get( 0 ) instanceof Query );

        String toTestFor = "";

        assertFileContains( dest, toTestFor );
    }


    public void testActiveAndDefaultPhaseReservedNames() throws Exception
    {
        //#DEFAULT and #ALL are reserved phase names and shall not be used in a Schematron schema

        String config = "test/rulesets/schema24.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema24.out";

        runXMLProbe( config, src, dest );

        assertFileContains( dest, "1:86:[error]:bad value for attribute \"defaultPhase\"" );
        assertFileContains( dest, "9:24:[error]:bad value for attribute \"id\"" );
        assertFileContains(
                dest,
                "Probatron:file:/E:/eclipse/workspace/probatron/test/rulesets/schema24.sch:13:20:[error]:bad value for attribute \"id\"" );
    }


    public void testPhaseVariablesCorrectlyScoped() throws Exception
    {
        //variables can occur: 
        // - as children of the root element
        // - within phases
        // - within patterns
        // - within rules
        //Unless they are in a rule, they are *global*.

        String config = "test/rulesets/schema31.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema31.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        SchematronSchema schema = ( ( SchematronConfiguration )x.getConfig() ).getSchema();
        Phase defaultPhase = schema.getDefaultPhase();
        assertNotNull( "null default phase", defaultPhase );
        assertEquals( "foo", defaultPhase.getId() );

        assertEquals( defaultPhase, schema.getActivePhase() ); //the default phase IS the active phase

        runXMLProbe( x );
        assertFileEquals( dest, "" );

        Iterator iter = x.getConfig().getVariables().iterator();
        while( iter.hasNext() )
        {
            Variable var = ( Variable )iter.next();
        }

        assertEquals( 5, x.getConfig().getVariables().size() );
    }


    public void testPhaseVariablesCorrectlyScoped2() throws Exception
    {
        String config = "test/rulesets/schema32.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema32.out";

        //set active phase to "#ALL"
        System.setProperty( "active-phase", "#ALL" );

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        SchematronSchema schema = ( ( SchematronConfiguration )x.getConfig() ).getSchema();
        assertSame( schema.getActivePhase(), Phase.PHASE_ALL );
        runXMLProbe( x );
        assertFileEquals( dest, "" );
        assertEquals( 6, x.getConfig().getVariables().size() );
    }


    public void testGlobalVariableValues() throws Exception
    {
        String config = "test/rulesets/schema31.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSchemaGlobalVariableValues.out";

        //set active phase to "#ALL"
        System.setProperty( "active-phase", "#ALL" );

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        SchematronSchema schema = ( ( SchematronConfiguration )x.getConfig() ).getSchema();
        assertSame( schema.getActivePhase(), Phase.PHASE_ALL );
        runXMLProbe( x );
        assertFileEquals( dest, "" );

        QueryEvaluator evaluator = ( ( SchematronQAHandler )x.getContentHandler()
                .getRegisteredExtensions()[ 0 ] ).getEvaluator();

        Object var = evaluator.getVariableValue( null, "globalVar1" );
        assertNotNull( var );
        if( ! ( var instanceof List ) )
            throw new Exception( "variable not a nodelist!" );

        if( ( ( List )var ).isEmpty() )
            throw new Exception( "variable is an empty nodelist!" );

        var = evaluator.getVariableValue( null, "globalVar2" );

        assertNotNull( var );
        if( ! ( var instanceof List ) )
            throw new Exception( "variable not a nodelist!" );

        assertEquals( 9, ( ( List )var ).size() );

        var = evaluator.getVariableValue( null, "phaseVar1" );
        assertEquals( 14, ( ( Double )var ).intValue() );

        var = evaluator.getVariableValue( null, "phaseVar2" );
        assertEquals( 15, ( ( Double )var ).intValue() );

        var = evaluator.getVariableValue( null, "patternVar1" );
        assertEquals( 15, ( ( Double )var ).intValue() );

        var = evaluator.getVariableValue( null, "patternVar2" );
        assertEquals( 16, ( ( Double )var ).intValue() );

    }


    public void testDuplicateGlobalVariables() throws Exception
    {
        String config = "test/rulesets/dupedvars.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testDuplicateVariables.out";

        runXMLProbe( config, src, dest );

        assertFileContains( dest, "Probatron:[FATAL]:Schematron schema validation error at "
                + "/*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/dsdl/"
                + "schematron'][1]/*[local-name()='let' and namespace-uri()='http://purl.oclc."
                + "org/dsdl/schematron'][2]: duplicate global variable 'globalVar1'" );
    }


    public void testInvalidSchemaDoesntCauseNPE() throws Exception
    {
        String config = "test/rulesets/schema1.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testInvalidSchemaDoesntCauseNPE.out";

        runXMLProbe( config, src, dest );

        assertFileContains( dest, "Probatron:file:/E:/eclipse/workspace/probatron/test"
                + "/rulesets/schema1.sch:3:10:[error]:unfinished element\n" );
    }


    public void testNoConcreteRules() throws Exception
    {
        String config = "test/rulesets/no-concrete-rules.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testNoConcreteRules.out";

        runXMLProbe( config, src, dest );

        String toTestFor = ""; //TODO

        assertFileEquals( dest, toTestFor );
    }


    public void testSystemPropertySetsErrorFormatToXml() throws Exception
    {
        String config = "test/rulesets/schema50.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSystemPropertySetsErrorFormatToXml.out";

        System.setProperty( "error-format", "xml" );
        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertXPathTrue( doc, "/" );
    }


    public void testSystemPropertySetsErrorFormatToXmlCaseInsignificant() throws Exception
    {
        String config = "test/rulesets/schema50.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSystemPropertySetsErrorFormatToXmlCaseInsignificant.out";

        System.setProperty( "error-format", "xML" );
        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );
        assertXPathTrue( doc, "/" );
    }


    public void testSystemPropertySetsErrorFormatToText() throws Exception
    {
        String config = "test/rulesets/schema50.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSystemPropertySetsErrorFormatToText.out";

        System.setProperty( "error-format", "text" );
        runXMLProbe( config, src, dest );

        String toTestFor = ""; //TODO

        assertFileEquals( dest, toTestFor );
    }


    public void testSystemPropertySetsErrorFormatToTextCaseInsignificant() throws Exception
    {
        String config = "test/rulesets/schema50.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSystemPropertySetsErrorFormatToTextCaseInsignificant.out";

        System.setProperty( "error-format", "tEXt" );
        runXMLProbe( config, src, dest );

        String toTestFor = ""; //TODO

        assertFileEquals( dest, toTestFor );
    }


    public void testNamespacesLoadCorrectly() throws Exception
    {
        String config = "test/rulesets/schema54.sch";
        String src = "test/test-cases/sample-article-2.xml";
        String dest = "test/dest/testNamespacesLoadCorrectly.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        runXMLProbe( x );

        Configuration c = x.getConfig();
        List namespaces = c.getNamespaceDecls();
        assertEquals( 1, namespaces.size() );
        NamespaceDeclaration nsd = ( ( NamespaceDeclaration )namespaces.get( 0 ) );
        assertEquals( nsd.getPrefix(), "wng" );
        assertEquals( nsd.getUri(), "http://www.wiley.com/namespaces/2007/wileyng" );
    }


    public void testSystemPropertySetsRelaxNGValidation() throws Exception
    {
        String config = "test/rulesets/wiley3g-rules.sch";
        String src = "test/test-cases/wileyml3g.xml";
        String dest = "test/dest/testSystemPropertySetsRelaxNGValidation.out";

        System.setProperty( "relaxng-schema-location", "test/schemas/wileyml3g.rng" ); //N.B. resolved against cwd
        runXMLProbe( config, src, dest );

        String toTestFor = ""; //TODO

        assertFileEquals( dest, toTestFor );
    }


    public void testSystemPropertySetsRelaxNGValidationCompactSyntax() throws Exception
    {
        String config = "test/rulesets/wiley3g-rules.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/testSystemPropertySetsRelaxNGValidationCompactSyntax.out";

        System.setProperty( "relaxng-schema-location", "test/schemas/svrl.rnc" ); //N.B. resolved against cwd
        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:dummy-instance.xml:1:7:[error]:unknown element \"foo\"\n"; //TODO

        assertFileEquals( dest, toTestFor );
    }


    public void testSystemPropertySetsRelaxNGValidationAndErrorReported() throws Exception
    {
        String config = "test/rulesets/wiley3g-rules.sch";
        String src = "test/test-cases/wileyml3g-invalid.xml";
        String dest = "test/dest/testSystemPropertySetsRelaxNGValidationAndErrorReported.out";

        System.setProperty( "relaxng-schema-location", "test/schemas/wileyml3g.rng" ); //N.B. resolved against cwd
        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:wileyml3g-invalid.xml:16:15:[error]:unknown element"
                + " \"foo\" from namespace \"http://www.wiley.com/namespaces/wiley\"\n";

        assertFileEquals( dest, toTestFor );
    }


    public void testLocalVariableCannotBeReferencedFromRuleInSeparatePattern() throws Exception
    {
        String config = "test/rulesets/localVariableCrosstalk.sch";
        String src = "test/test-cases/localVariableCrosstalk.xml";
        String dest = "test/dest/testLocalVariableCannotBeReferencedFromRuleInSeparatePattern.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "";

        assertFileStartsWith(
                dest,
                "Probatron:[FATAL]:error evaluating XPath expression [ID='u'] at "
                        + "/*[local-name()='schema' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='pattern' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][2]/*[local-name()='rule' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]/*[local-name()='assert' and namespace-uri()='http://purl.oclc.org/dsdl/schematron'][1]: "
                        + "'$current' error evaluating XPath expression: Variable current" );
    }


    public void testDuplicateLocalVariables() throws Exception
    {
        String config = "test/rulesets/dupedLocalVars.sch";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/testDuplicateLocalVariables.out";

        runXMLProbe( config, src, dest );

        String toTestFor = "Probatron:[FATAL]:duplicate variable name: ruleVar1\n";

        assertFileEquals( dest, toTestFor );
    }
}
