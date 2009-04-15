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
 * Created on 1 Nov 2007
 */
package org.probatron.test;

import org.apache.log4j.Logger;
import org.probatron.ShailNavigator;


public class SundryTests extends TestsBase
{
    private static Logger logger = Logger.getLogger( SundryTests.class );


    public void testForEach() throws Exception
    {
        String config = "test/rulesets/for-each.xml";
        String src = "test/test-cases/multi-root-instance.xml";
        String dest = "test/dest/for-each.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertCountXPath( 1, doc, "//silcn:expression" );

        assertXPathTrue( doc, "not(//probe:message)" );

        assertXPathTrue( doc, "//silcn:node[position()=1 and probe:text='one']" );

    }





    public void testNlmRules() throws Exception
    {
        String config = "test/rulesets/nlm-online-publishing-rules.xml";
        String src = "E:\\nlm\\Publishing-2_2-dtd-June\\sampleBlue-UGLY.xml";
        String dest = "test/dest/nlm-online-publishing-rules.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message[ probe:type = 'FATAL' ] )" );

        assertXPathTrue( doc, "not( //probe:message[ probe:type = 'error' ] )" );

    }


    public void testCrossrefRules() throws Exception
    {
        String config = "test/rulesets/crossref-303-rules.xml";
        String src = "test/test-cases/book303.xml";
        String dest = "test/dest/crossref-303-rules.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message[ probe:type = 'FATAL' ] )" );

        assertXPathTrue( doc, "not( //probe:message[ probe:type = 'error' ] )" );

    }


    public void testDuplicateAttributeInInstance() throws Exception
    {
        String config = "test/rulesets/duplicate-atts.xml";
        String src = "test/test-cases/duplicate-atts.xml";
        String dest = "test/dest/duplicate-atts.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( ., 'Attribute \"bar\" was already specified for element \"foo\".' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    /*
     * EXPERIMENTAL FEATURE!
     public void testOptimisedXpathExprr() throws Exception
    {
        String config = "test/rulesets/optimised-xpath-exprr.xml";
        String src = "test/test-cases/multi-root-instance.xml";
        String dest = "test/dest/optimised-xpath-exprr.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

    }*/


    public void testPredicateLast() throws Exception
    {
        String config = "test/rulesets/predicate-last.xml";
        String src = "test/test-cases/multi-root-instance.xml";
        String dest = "test/dest/predicate-last.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'last' ]" );

        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'count-elems' ]" );

        assertCountXPath( 1, doc, "//silcn:node[ ../silcn:id = 'atts' ]" );

        assertXPathTrue(
                doc,
                "//silcn:node[ ../silcn:id = 'last' and probe:line = '6' and probe:column= '15' and probe:text = 'number' ]" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'count-elems' and probe:text = '5' ]" );

    }


    public void testEntityRemappingToNotFound() throws Exception
    {
        String config = "test/rulesets/entity-resolution.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/entity-resolution.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count(//probe:message) = 1" );

    }


    public void testEntityRemapping2() throws Exception
    {
        String config = "test/rulesets/entity-resolution2.xml";
        String src = "test/test-cases/instance+dtd2.xml";
        String dest = "test/dest/entity-resolution2.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertValueOfXPath(
                "IOException: E:\\eclipse\\workspace\\probe-1.5\\test\\test-cases\\lalalal (The system cannot find the file specified)",
                doc, "//probe:message[ probe:type = 'FATAL']/probe:text" );

    }


    /**
     * Tests that re-mapping an entity which is not encountered during the parse has no effect. 
     * @throws Exception
     */
    public void testEntityRemapping3() throws Exception
    {
        String config = "test/rulesets/entity-resolution3.xml";
        String src = "test/test-cases/instance+dtd2.xml";
        String dest = "test/dest/entity-resolution3.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertValueOfXPath(
                "IOException: E:\\eclipse\\workspace\\probe-1.5\\test\\test-cases\\non-existent.dtd (The system cannot find the file specified)",
                doc, "//probe:message[ probe:type = 'FATAL']/probe:text" );

    }


    public void testPatternBasedEntityRemapping()
    {
    //TODO
    }


    public void testCharacterEntityRefs() throws Exception
    {
        String config = "test/rulesets/character-entity-refs.xml";
        String src = "test/test-cases/character-entity-refs.xml";
        String dest = "test/dest/character-entity-refs.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:node) = 2" );

        assertXPathTrue( doc, "count(//silcn:node[ ../silcn:id = 'contains' ]) = 1" );

        assertXPathTrue( doc, "count(//silcn:node[ ../silcn:id = 'not-contains' ]) = 1" );

    }


    public void testVariableReturningDocumentRoot() throws Exception
    {
        String config = "test/rulesets/variableReturnsDocumentRoot.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/variableReturnsDocumentRoot.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
    }


    public void testTextChildOfAttribute() throws Exception
    {
        String config = "test/rulesets/textChildOfAttribute.xml";
        String src = "test/test-cases/oup2.xml";
        String dest = "test/dest/textChildOfAttribute.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 2, doc, "//silcn:node[../silcn:id='TEST']" );
        assertCountXPath( 0, doc, "//silcn:node[../silcn:id='TEST-2']" );
    }


    public void testCountFollowingSiblings() throws Exception
    {
        String config = "test/rulesets/testCount.xml";
        String src = "test/test-cases/oup3.xml";
        String dest = "test/dest/testCount.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 0, doc, "//silcn:node[../silcn:id='PMS-QA-1-0110']" );
        assertCountXPath( 0, doc, "//silcn:node[../silcn:id='PMS-QA-1-0105']" );
        assertCountXPath( 1, doc, "//silcn:node[../silcn:id='preceding' and probe:text='329']" );
    }
    
    public void testCDATADoesntAffectTextNodeCount() throws Exception
    {
        String config = "test/rulesets/testCDATADoesntAffectTextNodeCount.xml";
        String src = "test/test-cases/cdata2.xml";
        String dest = "test/dest/testCDATADoesntAffectTextNodeCount.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertXPathEquals( "1", doc, "//silcn:node[../silcn:id='test' ]/probe:text" );
    }  
    
    public void testTextNodeContainsCDATA() throws Exception
    {
        String config = "test/rulesets/testTextNodeContainsCDATA.xml";
        String src = "test/test-cases/cdata2.xml";
        String dest = "test/dest/testTextNodeContainsCDATA.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 2, doc, "//silcn:node" );
        assertXPathEquals( "foo bar blort", doc, "//silcn:node[../silcn:id='test' ]/probe:text" );
        assertXPathEquals( "wibble", doc, "//silcn:node[../silcn:id='test2' ]/probe:text" );
    }
    
    public void testRootElementHasNoPrecedingSibling() throws Exception
    {
        String config = "test/rulesets/testRootElementHasNoPrecedingSibling.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/testRootElementHasNoPrecedingSibling.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        assertXPathEquals( "0", doc, "//silcn:node[../silcn:id='test' ]/probe:text" );
    }    

}
