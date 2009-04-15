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

import org.probatron.ShailNavigator;

/**
 * NOTE - due to a limitation of JING(?), the JVM must have a minimum stack size of approx. 512kB,
 * else a StackOverflowError results.
 * 
 * @author andrews
 *
 * @version $Id: RelaxNGTests.java,v 1.1 2008/11/11 10:43:39 GBDP\andrews Exp $
 */
public class RelaxNGTests extends TestsBase
{
    public void testRelaxNGSchemaNotFound() throws Exception
    {
        String config = "test/rulesets/relaxng-schema-not-found.xml";
        String src = "test/test-cases/relaxng-schema-not-found.xml";
        String dest = "test/dest/relaxng-schema-not-found.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//probe:message) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and probe:text = \"error loading schema 'file:///e:/work/wiley/wng/WileyNG.rng~': IOException: e:\\work\\wiley\\wng\\WileyNG.rng~ (The system cannot find the file specified)\" ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testRelaxNGSchemaNotWellFormed() throws Exception
    {
        String config = "test/rulesets/relaxng-schema-not-well-formed.xml";
        String src = "test/test-cases/relaxng-schema-not-well-formed.xml";
        String dest = "test/dest/relaxng-schema-not-well-formed.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//probe:message) = 2" );

        assertXPathTrue( doc, "count(//probe:message[probe:type = 'FATAL']) = 2" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and not( probe:line ) and starts-with( probe:text, \"error loading schema '../test-cases/relaxng-schema-not-well-formed.rng': SAXException:\" ) ]" );

        assertXPathTrue( doc, "//probe:message[ probe:type = 'FATAL' and probe:line ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testRelaxNGSchemaValidationError() throws Exception
    {
        String config = "test/rulesets/relaxng-schema-validation-error.xml";
        String src = "test/test-cases/relaxng-schema-validation-error.xml";
        String dest = "test/dest/relaxng-schema-validation-error.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//probe:message) = 2" );

        assertXPathTrue( doc, "count(//probe:message[probe:type='FATAL']) = 1" );

        assertXPathTrue( doc, "count(//probe:message[probe:type='error']) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and not( probe:line ) and probe:text = \"error loading schema '../test-cases/relaxng-schema-validation-error.rng': invalid schema\" ]" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' and probe:line and probe:text = 'missing \"start\" element' ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testRelaxNGSchemaCompactSyntax() throws Exception
    {
        String config = "test/rulesets/relaxng-schema-compact-syntax.xml";
        String src = "test/test-cases/parser-feature-bad-encoding.out";
        String dest = "test/dest/relaxng-schema-compact-syntax.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testRelaxNGSchemaXmlSyntax() throws Exception
    {
        String config = "test/rulesets/relaxng-schema-xml-syntax.xml";
        String src = "test/test-cases/parser-feature-bad-encoding.out";
        String dest = "test/dest/relaxng-schema-xml-syntax.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testRelaxNGSchemaURIResolution() throws Exception
    {
        String config = "test/rulesets/wileyng-rules-deployable.xml";
        String src = "test/test-cases/sample-article-1.xml";
        String dest = "test/dest/testRelaxNGSchemaURIResolution.xml";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

    }
}
