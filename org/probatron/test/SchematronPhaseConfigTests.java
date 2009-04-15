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
 * Created on 15 Jan 2008
 */
package org.probatron.test;

import org.probatron.ProbatronSession;

import com.griffinbrown.schematron.Phase;
import com.griffinbrown.schematron.SchematronConfiguration;
import com.griffinbrown.schematron.SchematronSchema;

/**
 * Provides test cases for the following phase configurations:
 * 
 * A. system property "active-phase" given
 * 1. named phase found
 * 2. named phase not found
 * 3. phase #ALL given
 * 4. phase #DEFAULT and schema has no defaultPhase attribute
 * 5. phase #DEFAULT and defaultPhase not found
 * 6. phase #DEFAULT and defaultPhase found
 * 
 * B. system property "active-phase" NOT given
 * 1. schema has defaultPhase attribute
 * 2. schema has NO defaultPhase attribute
 * 
 * @author andrews
 *
 * @version $Id: SchematronPhaseConfigTests.java,v 1.1 2008/11/11 10:43:39 GBDP\andrews Exp $
 */
public class SchematronPhaseConfigTests extends TestsBase
{
    /*
     * @see com.xmlprobe.test.TestsBase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }


    /**
     * @see org.probatron.test.TestsBase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        System.clearProperty( "active-phase" );
    }


    /**
     * See case A1 above.
     * @throws Exception
     */
    public void testSchemaNamedActivePhaseFound() throws Exception
    {
        System.setProperty( "active-phase", "foo" );

        String config = "test/rulesets/schema29.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema29.out";

        runXMLProbe( config, src, dest );

        assertFileEquals( dest, "" );
    }


    /**
     * See case A2 above.
     * @throws Exception
     */
    public void testSchemaNamedActivePhaseNotFound() throws Exception
    {
        System.setProperty( "active-phase", "tttttttttttt" );

        String config = "test/rulesets/schema28.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema28.out";

        runXMLProbe( config, src, dest );

        assertFileContains( dest,
                "XMLProbe:[error]:active phase set to 'tttttttttttt' but no such"
                        + " phase found; all patterns will be active" );
    }


    /**
     * See case A3 above.
     * @throws Exception
     */
    public void testSchemaActivePhaseALL() throws Exception
    {
        System.setProperty( "active-phase", "#ALL" );

        String config = "test/rulesets/schema25.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema25.out";

        runXMLProbe( config, src, dest );

        assertFileEquals( dest, "" );
    }


    /**
     * See case A4 above.
     * @throws Exception
     */
    public void testSchemaActivePhaseDefaultNotSpecified() throws Exception
    {
        System.setProperty( "active-phase", "#DEFAULT" );

        String config = "test/rulesets/schema26.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema26.out";

        runXMLProbe( config, src, dest );

        assertFileContains( dest,
                "XMLProbe:[warning]:active phase set to '#DEFAULT' but schema does "
                        + "not specify a default phase; all patterns will be active" );
    }


    /**
     * See case A5 above.
     * @throws Exception
     */
    public void testSchemaActivePhaseDefaultNotFound() throws Exception
    {
        System.setProperty( "active-phase", "#DEFAULT" );

        String config = "test/rulesets/schema27.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema27.out";

        runXMLProbe( config, src, dest );

        assertFileContains( dest, "XMLProbe:[warning]:active phase set to '#DEFAULT' but no such"
                + " default phase 'alskdj' found; all patterns will be active" );
    }


    /**
     * See case A6 above.
     * @throws Exception
     */
    public void testSchemaActiveDefaultPhaseFound() throws Exception
    {
        System.setProperty( "active-phase", "#DEFAULT" );

        String config = "test/rulesets/schema30.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema30.out";

        runXMLProbe( config, src, dest );

        assertFileEquals( dest, "" );
    }


    /**
     * See case B1 above.
     * @throws Exception
     */
    public void testSchemaNoActivePhaseSysPropButDefaultPhasePresent() throws Exception
    {
        System.clearProperty( "active-phase" );

        String config = "test/rulesets/schema30.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testSchemaNoActivePhaseSysPropButDefaultPhasePresent.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        
        SchematronSchema schema = ((SchematronConfiguration)x.getConfig()).getSchema();
        
        assertEquals( schema.getActivePhase(), schema.getDefaultPhase() );

        runXMLProbe( x );

        assertFileEquals( dest, "" );
    }
    
    /**
     * See case B2 above.
     * @throws Exception
     */
    public void testSchemaNoActivePhaseSysPropAndNoDefaultPhase() throws Exception
    {
        System.clearProperty( "active-phase" );

        String config = "test/rulesets/schema25.sch";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/schema25.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );
        
        SchematronSchema schema = ((SchematronConfiguration)x.getConfig()).getSchema();
        
        assertNull( schema.getDefaultPhase() );
        assertNotNull( schema.getActivePhase() );
        assertEquals( schema.getActivePhase(), Phase.PHASE_ALL );
        assertEquals( 2, schema.getActivePatterns().size() );

        runXMLProbe( x );

        assertFileEquals( dest, "" );
    }    
}
