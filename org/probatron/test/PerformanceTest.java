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
 * Created on 12 Dec 2008
 */
package org.probatron.test;

import org.apache.log4j.Logger;
import org.probatron.ProbatronSession;
import org.probatron.ShailNavigator;

public class PerformanceTest extends TestsBase
{
    private static Logger logger = Logger.getLogger( PerformanceTest.class );


    protected void setUp() throws Exception
    {
        System.setProperty( "debug", "false" );
        super.setUp();        
    }


    //    public void test() throws Exception
    //    {
    //        Object doc = ShailNavigator.getInstance().getDocumentAsObject( "test/perf/t97.xml" );
    //        //        Object doc = ShailNavigator.getInstance().getDocumentAsObject( "test/test-cases/dummy-instance.xml" );
    //
    //        String xr = "//xr";
    //
    //        ShailXPath xpath = new ShailXPath( xr );
    //
    //        //        xpath = new ShailXPath( "//xr/@xid[ . = $foo ]");
    //        //        
    //        //        
    //        //        SimpleVariableContext vc = new SimpleVariableContext();
    //        //        vc.setVariableValue( "foo", new ShailXPath("//e[not(sg/se1)]/@eid").evaluate( doc ) );
    //        //        xpath.setVariableContext( vc );
    //        //        
    //        //        long t = System.currentTimeMillis();
    //        //        ShailList xrs = (ShailList)xpath.selectNodes( doc );
    //        //        t = System.currentTimeMillis() - t;
    //        //        logger.debug( "//xr/@id evaluated in " + t + "ms");
    //
    //        xpath = new ShailXPath( "//*" );
    //        System.err.println( xpath.getRootExpr() );
    //        List results = ( List )xpath.evaluate( doc );
    //        assertEquals( results.size(), 15597 );
    //
    //        xpath = new ShailXPath( "//*[ name() = 'xqzzz' ]" );
    //        System.err.println( xpath.getRootExpr() );
    //        results = ( List )xpath.evaluate( doc );
    //        assertEquals( results.size(), 0 );
    //        
    //        xpath = new ShailXPath( "//*[ name() = 'e' ]" );
    //        System.err.println( xpath.getRootExpr() );
    //        results = ( List )xpath.evaluate( doc );
    //        assertEquals( results.size(), 349 );
    //        
    //        xpath = new ShailXPath( "//*[ name() = 'e' ]|//*[ name() != 'e' ]" );
    //        System.err.println( xpath.getRootExpr() );
    //        results = ( List )xpath.evaluate( doc );
    //        assertEquals( results.size(), 15597 );
    //        
    ////        xpath = new ShailXPath( "//text()" );
    ////        System.err.println( xpath.getRootExpr() );
    ////        results = ( List )xpath.evaluate( doc );
    ////        assertEquals( results.size(), 15528 );
    //        
    //        //TODO: more checks for cached elements! 
    //
    //    }

//    public void testFollowingSiblingAxis() throws Exception
//    {
//        Object doc = ShailNavigator.getInstance().getDocumentAsObject( "test/perf/t97.xml" );
//
//        String xr = "$text[ not( (following-sibling::*) or following-sibling::text()) ][ substring( ., string-length(..), 1 ) = ' ']";
//
//        ShailXPath xpath = new ShailXPath( xr );
//        SimpleVariableContext vc = new SimpleVariableContext();
//        vc.setVariableValue( "text", new ShailXPath( "//text()" ).evaluate( doc ) );
//        xpath.setVariableContext( vc );
//
//        logger.debug( "expr=" + xpath.getRootExpr() );
//
//        long t = System.currentTimeMillis();
//        List results = ( List )xpath.evaluate( doc );
//        logger.debug( "evaluated in " + ( System.currentTimeMillis() - t ) + "ms [expr="
//                + xpath + "]" );
//
//        assertEquals( 0, results.size() );
//    }
//
//
//    public void testPrecedingAxis() throws Exception
//    {
//        Object doc = ShailNavigator.getInstance().getDocumentAsObject( "test/perf/t97.xml" );
//
//        ShailXPath xpath = new ShailXPath( "(//*)[last()]" );
//        SimpleVariableContext vc = new SimpleVariableContext();
//        vc.setVariableValue( "last-elem", xpath.evaluate( doc ) );
//
//        xpath = new ShailXPath( "$last-elem/preceding::*" );
//        xpath.setVariableContext( vc );
//
//        long t = System.currentTimeMillis();
//        ShailList result = ( ShailList )xpath.evaluate( doc );
//        logger.debug( "evaluated in " + ( System.currentTimeMillis() - t ) + "ms [expr="
//                + xpath + "]" );
//        assertEquals( 15591, result.size() );
//    }
//
//
//    public void testPrecedingAxis2() throws Exception
//    {
//        Object doc = ShailNavigator.getInstance()
//                .getDocumentAsObject( "test/perf/oso_test.xml" );
//
//        ShailXPath xpath = new ShailXPath( "(//*)[last()]" );
//        SimpleVariableContext vc = new SimpleVariableContext();
//        vc.setVariableValue( "last-elem", xpath.evaluate( doc ) );
//
//        xpath = new ShailXPath( "$last-elem/preceding::processing-instruction()" );
//        xpath.setVariableContext( vc );
//
//        long t = System.currentTimeMillis();
//        ShailList result = ( ShailList )xpath.evaluate( doc );
//        logger.debug( "evaluated in " + ( System.currentTimeMillis() - t ) + "ms [expr="
//                + xpath + "]" );
//        assertEquals( 734, result.size() );
//    }
//
//
//    public void testPrecedingAxis3() throws Exception
//    {
//        Object doc = ShailNavigator.getInstance().getDocumentAsObject( "test/perf/flat.xml" );
//
//        ShailXPath xpath = new ShailXPath( "(//*)[last()]" );
//        SimpleVariableContext vc = new SimpleVariableContext();
//        vc.setVariableValue( "last-elem", xpath.evaluate( doc ) );
//
//        xpath = new ShailXPath( "$last-elem/preceding::*" );
//        xpath.setVariableContext( vc );
//
//        long t = System.currentTimeMillis();
//        ShailList result = ( ShailList )xpath.evaluate( doc );
//        logger.debug( "evaluated in " + ( System.currentTimeMillis() - t ) + "ms [expr="
//                + xpath + "]" );
//        assertEquals( 99998, result.size() );
//    }
//
//
//    public void testPrecedingSiblingAxis() throws Exception
//    {
//        Object doc = ShailNavigator.getInstance().getDocumentAsObject( "test/perf/flat.xml" );
//
//        ShailXPath xpath = new ShailXPath( "(//*)[last()]" );
//        SimpleVariableContext vc = new SimpleVariableContext();
//        vc.setVariableValue( "last-elem", xpath.evaluate( doc ) );
//
//        xpath = new ShailXPath( "$last-elem/preceding-sibling::*" );
//        xpath.setVariableContext( vc );
//
//        long t = System.currentTimeMillis();
//        ShailList result = ( ShailList )xpath.evaluate( doc );
//        logger.debug( "evaluated in " + ( System.currentTimeMillis() - t ) + "ms [expr="
//                + xpath + "]" );
//
//        assertEquals( 99998, result.size() );
//    }
    
    public void testStressTestSVRL() throws Exception
    {
        logger.debug( "starting..." );

        String config = "test/perf/test.sch";
        String src = "test/perf/ecma376.xml";
        String dest = "test/dest/testStressTest.out";

        ProbatronSession x = createXMLProbeSession( config, src, dest );

        System.setProperty( "error-format", "xml" );
        runXMLProbe( x );

        int doc = ShailNavigator.getInstance().getDocument( dest );
        assertCountXPath(
                5253,
                doc,
                "//*[ local-name() = 'successful-report' and namespace-uri() = 'http://purl.oclc.org/dsdl/svrl' ]" );

    }
}