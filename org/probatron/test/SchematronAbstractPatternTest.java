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
 * Created on 29 Jan 2008
 */
package org.probatron.test;

import java.util.List;

import org.probatron.ProbatronSession;

import com.griffinbrown.schematron.Pattern;
import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.xmltool.Constants;

public class SchematronAbstractPatternTest extends TestsBase
{
    public SchematronAbstractPatternTest()
    {
    //        System.setProperty( "debug", "true" );
    }


    public void testAbstractPatternReferencedPatternNotFound() throws Exception
    {
        String config = "test/rulesets/schema44.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractPatternReferencedPatternNotFound.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        List patterns = ( ( SchematronConfiguration )x.getConfig() ).getSchema()
                .getActivePatterns();

        assertEquals( 1, patterns.size() );

        runXMLProbe( x );
        String toTestFor = "[FATAL]:error resolving abstract pattern: "
                + "abstract pattern is-a='abstractPattern' referenced at";
        assertFileContains( dest, toTestFor );
    }


    public void testAbstractPatternParamRefUnresolvableFunction() throws Exception
    {
        String config = "test/rulesets/schema45.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractPatternParamRefUnresolvableFunction.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        x.setErrorFormat( Constants.ERRORS_AS_XML );

        SchematronConfiguration configuration = ( SchematronConfiguration )x.getConfig();
        List patterns = configuration.getSchema().getActivePatterns();

        assertEquals( 1, patterns.size() );
        assertEquals( 0, ( ( Pattern )patterns.get( 0 ) ).getRules().size() ); //unresolved patterns & rules

        runXMLProbe( x );

        assertEquals( 1, ( ( Pattern )patterns.get( 0 ) ).getRules().size() ); //resolved
        assertEquals( 1, configuration.getQueries().size() );

        assertFileEquals( dest, "" ); //we get a FATAL error, therefore an empty XML report
    }


    public void testAbstractPatternParamRefNotFound() throws Exception
    {
        String config = "test/rulesets/schema46.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testAbstractPatternParamRefNotFound.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        SchematronConfiguration configuration = ( SchematronConfiguration )x.getConfig();
        List patterns = configuration.getSchema().getActivePatterns();

        assertEquals( 1, patterns.size() );
        assertEquals( 0, ( ( Pattern )patterns.get( 0 ) ).getRules().size() ); //unresolved patterns & rules

        runXMLProbe( x );

        assertEquals( 1, ( ( Pattern )patterns.get( 0 ) ).getRules().size() ); //resolved
        assertEquals( 1, configuration.getQueries().size() );

        String toTestFor = "'$bar/blah' error evaluating XPath expression: Variable bar";
        assertFileContains( dest, toTestFor );
    }
}
