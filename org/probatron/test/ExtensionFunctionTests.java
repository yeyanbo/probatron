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
 * Created on 11 Dec 2007
 */
package org.probatron.test;

import org.probatron.ShailNavigator;

public class ExtensionFunctionTests extends TestsBase
{
    public void testAllowsPcData() throws Exception
    {
        String config = "test/rulesets/allows-pcdata.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/allows-pcdata.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:expression) = 3" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/foo[1]' and ../probe:line = '4' and ../probe:column = '17' and ../probe:text = 'pcdata not allowed' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/foo[1]/bar[1]' and ../probe:line = '5' and ../probe:column = '7' and ../probe:text = 'pcdata allowed' ]" );

        assertXPathTrue(
                doc,
                "//silcn:expression[ . = '/foo[1]/bar[1]/blort[1]' and ../probe:line = '6' and ../probe:column = '11' and ../probe:text = 'pcdata not allowed' ]" );

    }


    public void testAllowsPcDataNoDtd() throws Exception
    {
        String config = "test/rulesets/allows-pcdata-no-dtd.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/allows-pcdata-no-dtd.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testAsAbsoluteUriWrongArity() throws Exception
    {
        String config = "test/rulesets/as-absolute-uri-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/as-absolute-uri-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'as-absolute-uri() requires one or two arguments' ) ]" );

    }


    public void testAsAbsoluteUriWrongArgType() throws Exception
    {
        String config = "test/rulesets/as-absolute-uri-wrong-arg-type.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/as-absolute-uri-wrong-arg-type.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to as-absolute-uri() must be of type node-set' ) ]" );

    }


    public void testAsAbsoluteUriEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/as-absolute-uri-empty-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/as-absolute-uri-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to as-absolute-uri() must not be an empty node-set' ) ]" );

    }


    public void testCharToIntWrongArity() throws Exception
    {
        String config = "test/rulesets/char-to-int-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/char-to-int-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'char-to-int() requires one argument' ) ]" );

    }


    public void testCharToIntWrongArgType() throws Exception
    {
        String config = "test/rulesets/char-to-int-wrong-arg-type.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/char-to-int-wrong-arg-type.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'char-to-int() argument must be of type string' ) ]" );

    }


    public void testCharToIntBadStringLength() throws Exception
    {
        String config = "test/rulesets/char-to-int-bad-string-length.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/char-to-int-bad-string-length.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'char-to-int() string argument must be one character long' ) ]" );

    }


    public void testCharToInt() throws Exception
    {
        String config = "test/rulesets/char-to-int.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/char-to-int.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'test2' and probe:text = '52428' ]" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'test1' and probe:text = '97' ]" );

    }


    public void testCheckCellSpanningWrongArity() throws Exception
    {
        String config = "test/rulesets/check-cell-spanning-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/check-cell-spanning-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'check-cell-spanning() takes 1 argument' ) ]" );

    }


    public void testCheckCellSpanningBadCols() throws Exception
    {
        String config = "test/rulesets/check-cell-spanning-bad-cols.xml";
        String src = "test/test-cases/check-cell-spanning-bad-cols.xml";
        String dest = "test/dest/check-cell-spanning-bad-cols.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' and contains( probe:text, 'Value of cols attribute is not an integer' ) ]" );

    }


    public void testCheckCellSpanningTooManyCellsInRow() throws Exception
    {
        String config = "test/rulesets/check-cell-spanning-too-many-cells-in-row.xml";
        String src = "test/test-cases/check-cell-spanning-too-many-cells-in-row.xml";
        String dest = "test/dest/check-cell-spanning-too-many-cells-in-row.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue( doc,
                "//probe:message[ probe:type = 'error' and probe:text = 'table row has too many cells!' ]" );

    }


    public void testCheckCellCollision() throws Exception
    {
        String config = "test/rulesets/check-cell-spanning-overlap-or-back-span.xml";
        String src = "test/test-cases/check-cell-collision.xml";
        String dest = "test/dest/check-cell-spanning-collision.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' and "
                        + "probe:text = 'table cell may encroach into area reserved by spanning operation' ]" );

    }


    public void testCombiningCharWrongArity() throws Exception
    {
        String config = "test/rulesets/combining-char-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/combining-char-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'contains-combining-character() takes 1 argument' ) ]" );

    }


    public void testCombiningChar() throws Exception
    {
        String config = "test/rulesets/combining-char.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/combining-char.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:node) = 1" );

        assertXPathTrue( doc, "//silcn:node[ probe:text = 'true' ]" );

    }


    public void testDocumentWrongArity() throws Exception
    {
        String config = "test/rulesets/document-function.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/document-function.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'document() requires 1 or 2 arguments' ) ]" );

    }


    public void testDocumentWrongArgType() throws Exception
    {
        String config = "test/rulesets/document-function-wrong-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/document-function-wrong-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to document() must be of type node-set' ) ]" );

    }


    public void testDocumentBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/document-function-bad-nodeset-arg.xml";
        String src = "test/test-cases/document-function.xml";
        String dest = "test/dest/document-function-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to document() must not be an empty node-set' ) ]" );

    }


    public void testDocumentFileNotFound() throws Exception
    {
        String config = "test/rulesets/document-function-file-not-found.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/document-function-file-not-found.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, '(The system cannot find the file specified)' ) ]" );

    }


    public void testDocumentMultipleDocs() throws Exception
    {
        String config = "test/rulesets/document-function-multiple-docs.xml";
        String src = "test/test-cases/document-function.xml";
        String dest = "test/dest/document-function-multiple-docs.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count( //silcn:node ) = 3" );

    }


    public void testDocumentDtdPublicIdWrongArity() throws Exception
    {
        String config = "test/rulesets/dtd-public-id-wrong-arity.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-public-id-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-public-identifier() takes 1 argument' ) ]" );

    }


    public void testDocumentDtdPublicIdBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/dtd-public-id-bad-nodeset-arg.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-public-id-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-public-identifier() argument must be a node' ) ]" );

    }


    public void testDocumentDtdPublicIdEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/dtd-public-id-empty-nodeset-arg.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-public-id-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-public-identifier() argument must not be an empty nodeset' ) ]" );

    }


    public void testDocumentDtdPublicIdNoDtd() throws Exception
    {
        String config = "test/rulesets/dtd-public-id-no-dtd.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/dtd-public-id-no-dtd.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:node) = 1" );

        assertXPathTrue( doc, "string(//probe:text) = ''" );

    }


    public void testDocumentDtdPublicId() throws Exception
    {
        String config = "test/rulesets/dtd-public-id.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-public-id.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:node) = 2" );

        assertXPathTrue( doc, "count( //probe:text[ . = 'foo.dtd'] ) = 2" );

    }


    public void testDocumentDtdSystemIdWrongArity() throws Exception
    {
        String config = "test/rulesets/dtd-system-id-wrong-arity.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-system-id-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-system-identifier() takes 1 argument' ) ]" );

    }


    public void testDocumentDtdSystemIdBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/dtd-system-id-bad-nodeset-arg.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-system-id-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-system-identifier() argument must be a node' ) ]" );

    }


    public void testDocumentDtdSystemIdEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/dtd-system-id-empty-nodeset-arg.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-system-id-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //silcn:node )" );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-system-identifier() argument must not be an empty nodeset' ) ]" );

    }


    public void testDocumentDtdSystemIdNoDtd() throws Exception
    {
        String config = "test/rulesets/dtd-system-id-no-dtd.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/dtd-system-id-no-dtd.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:node) = 1" );

        assertXPathTrue( doc, "string(//probe:text) = ''" );

    }


    public void testDocumentDtdSystemId() throws Exception
    {
        String config = "test/rulesets/dtd-system-id.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/dtd-system-id.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "count(//silcn:node) = 2" );

        assertXPathTrue( doc, "count( //probe:text[ . = 'foo.dtd'] ) = 2" );

    }


    public void testFileExistsCaseSensitiveWrongArity() throws Exception
    {
        String config = "test/rulesets/file-exists-case-sensitive-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-case-sensitive-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'file-exists-case-sensitive() requires one or two arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileExistsCaseSensitiveBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/file-exists-case-sensitive-bad-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-case-sensitive-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to file-exists-case-sensitive() must be of type node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileExistsCaseSensitiveEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/file-exists-case-sensitive-empty-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-case-sensitive-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to file-exists-case-sensitive() must not be an empty node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileExistsCaseSensitive() throws Exception
    {
        String config = "test/rulesets/file-exists-case-sensitive.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-case-sensitive.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "//silcn:id = 'a'" );

        assertXPathTrue( doc, "//silcn:id = 'b'" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testFileExistsWrongArity() throws Exception
    {
        String config = "test/rulesets/file-exists-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'file-exists() requires one or two arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileExistsBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/file-exists-bad-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to file-exists() must be of type node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileExistsEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/file-exists-empty-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to file-exists() must not be an empty node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileExists() throws Exception
    {
        String config = "test/rulesets/file-exists-case-sensitive.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-exists.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testFileSystemAsXmlWrongArity() throws Exception
    {
        String config = "test/rulesets/file-system-as-xml-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-system-as-xml-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'file-system-as-xml() takes 1 or 2 arguments.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileSystemAsXmlBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/file-system-as-xml-bad-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-system-as-xml-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'file-system-as-xml() 2nd argument must be of type node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileSystemAsXmlEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/file-system-as-xml-empty-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-system-as-xml-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'second argument to file-system-as-xml() must not be an empty node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testFileSystemAsXml() throws Exception
    {
        String config = "test/rulesets/file-system-as-xml.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/file-system-as-xml.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 1" );

        assertXPathTrue( doc, "not( //probe:message )" );

        assertXPathTrue( doc, "//silcn:node/probe:text != '0'" );

    }


    public void testUrlMimeTypeWrongArity() throws Exception
    {
        String config = "test/rulesets/url-mime-type-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/url-mime-type-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'url-mime-type() expects one or two arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testUrlMimeTypeBadArg() throws Exception
    {
        String config = "test/rulesets/url-mime-type-bad-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/url-mime-type-bad-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'url-mime-type() first argument must be of type nodeset or type string' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testUrlMimeTypeBadNodesetArg() throws Exception
    {
        String config = "test/rulesets/url-mime-type-bad-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/url-mime-type-bad-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'url-mime-type() first argument must be of type nodeset or type string' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testUrlMimeTypeEmptyNodesetArg() throws Exception
    {
        String config = "test/rulesets/url-mime-type-empty-nodeset-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/url-mime-type-empty-nodeset-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'url-mime-type() second argument must not be an empty nodeset' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testUrlMimeTypeBadUrl() throws Exception
    {
        String config = "test/rulesets/url-mime-type-bad-url.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/url-mime-type-bad-url.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue( doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'error parsing URL:' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testUrlMimeType() throws Exception
    {
        String config = "test/rulesets/url-mime-type.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/url-mime-type.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:id ) = 15" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'gif' ]/probe:text = 'image/gif'" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'tiff' ]/probe:text = 'image/tiff'" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'jpeg' ]/probe:text = 'image/jpeg'" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'png' ]/probe:text = 'image/png'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'xml-utf8' ]/probe:text = 'application/xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'xml-ascii' ]/probe:text = 'application/xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'xml-utf16LE' ]/probe:text = 'application/xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'xml-utf16LE-no-BOM' ]/probe:text = 'application/xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'xml-utf16BE' ]/probe:text = 'application/xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'xml-utf16BE-no-BOM' ]/probe:text = 'application/xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'pdf' ]/probe:text = 'application/pdf'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'postscript' ]/probe:text = 'application/postscript'" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'html' ]/probe:text = 'text/html'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'zip' ]/probe:text = 'application/zip'" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testGoverningDtdHash() throws Exception
    {
        String config = "test/rulesets/governing-dtd-hash.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/governing-dtd-hash.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 1" );

        assertXPathTrue( doc, "//silcn:node/probe:text != '0'" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testGoverningDtdHashNoDtd() throws Exception
    {
        String config = "test/rulesets/governing-dtd-hash.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/governing-dtd-hash-no-dtd.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 1" );

        assertXPathTrue( doc, "//silcn:node/probe:text = '0'" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testGoverningDtdHashWrongArity() throws Exception
    {
        String config = "test/rulesets/governing-dtd-hash-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/governing-dtd-hash-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'governing-dtd-hash() takes no arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testInNodesetWrongArity() throws Exception
    {
        String config = "test/rulesets/in-nodeset-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'in-nodeset() takes 2 arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testInNodesetBadArg() throws Exception
    {
        String config = "test/rulesets/in-nodeset-bad-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-bad-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'arguments to in-nodeset() must be either (string or nodeset, nodeset) or (string or nodeset, string)' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testInNodesetEmptyNodesetArg1() throws Exception
    {
        String config = "test/rulesets/in-nodeset-empty-nodeset-arg-1.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-empty-nodeset-arg-1.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );

        assertCountXPath( 1, doc, "//silcn:node" );
    }


    public void testInNodesetStringNode() throws Exception
    {
        String config = "test/rulesets/in-nodeset-string-node.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-string-node.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertCountXPath( 2, doc, "//silcn:node" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testInNodesetNodeNode() throws Exception
    {
        String config = "test/rulesets/in-nodeset-node-node.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-node-node.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testInNodesetStringString() throws Exception
    {
        String config = "test/rulesets/in-nodeset-string-string.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-string-string.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testInNodesetNodeString() throws Exception
    {
        String config = "test/rulesets/in-nodeset-node-string.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/in-nodeset-node-string.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testIsDeclaredEmptyWrongArity() throws Exception
    {
        String config = "test/rulesets/is-declared-empty-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-declared-empty-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'is-declared-empty() takes 1 argument' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testIsDeclaredEmpty() throws Exception
    {
        String config = "test/rulesets/is-declared-empty.xml";
        String src = "test/test-cases/instance+dtd.xml";
        String dest = "test/dest/is-declared-empty.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "count( //silcn:id[.='empty'] ) = 1" );

        assertXPathTrue( doc, "count( //silcn:id[.='not-empty'] ) = 1" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testIsInCdataWrongArity() throws Exception
    {
        String config = "test/rulesets/is-in-cdata-wrong-arity.xml";
        String src = "test/test-cases/cdata.xml";
        String dest = "test/dest/is-in-cdata-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'in-cdata-section() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    //    public void testIsInCdata() throws Exception
    //    {
    //        String config = "test/rulesets/is-in-cdata.xml";
    //        String src = "test/test-cases/cdata.xml";
    //        String dest = "test/dest/is-in-cdata.out";
    //
    //        runXMLProbe( config, src, dest );
    //
    //        ShailNavigator nav = ShailNavigator.getInstance();
    //        int doc = nav.getDocument( dest );
    //
    //        assertXPathTrue( doc, "count( //silcn:node ) = 2" );
    //
    //        assertXPathTrue( doc, "not( //probe:message )" );
    //
    //    }
    //
    //
    public void testIsValidIsoDateWrongArity() throws Exception
    {
        String config = "test/rulesets/is-valid-iso-date-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-iso-date-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'is-valid-ISO8601-date() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testIsValidIsoDate() throws Exception
    {
        String config = "test/rulesets/is-valid-iso-date.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-iso-date.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testIsValidIssnWrongArity() throws Exception
    {
        String config = "test/rulesets/is-valid-issn-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-issn-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'is-valid-issn() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testIsValidIssn() throws Exception
    {
        String config = "test/rulesets/is-valid-issn.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-issn.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 3" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testMinimalSystemIdWrongArity() throws Exception
    {
        String config = "test/rulesets/minimal-system-id-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/minimal-system-id-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'minimal-system-id() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMinimalSystemId() throws Exception
    {
        String config = "test/rulesets/minimal-system-id.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/minimal-system-id.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 4" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'empty-string' ]/probe:text = ''" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'empty-nodeset' ]/probe:text = ''" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'win-path' ]/probe:text = 'foo.xml'" );

        assertXPathTrue( doc,
                "//silcn:node[ ../silcn:id = 'unix-path' ]/probe:text = 'bar.xml'" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testNodeSetWrongArity() throws Exception
    {
        String config = "test/rulesets/node-set-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/node-set-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'node-set() expects 2 arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testNodeSetBadFirstArg() throws Exception
    {
        String config = "test/rulesets/node-set-bad-first-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/node-set-bad-first-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'node-set() first argument must be of type node-set' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testNodeSetBadSecondArg() throws Exception
    {
        String config = "test/rulesets/node-set-bad-second-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/node-set-bad-second-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'node-set() second argument must be of type string' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testNodeSetBadXpath() throws Exception
    {
        String config = "test/rulesets/node-set-bad-xpath.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/node-set-bad-xpath.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, \"error compiling XPath expression '(' dynamically in node-set() function\" ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testNodeSet() throws Exception
    {
        String config = "test/rulesets/node-set.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/node-set.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 4, doc, "//silcn:node" );

        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'node-set' ]/probe:text = ''" );
        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'string' ]/probe:text = 'foo'" );
        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'number' ]/probe:text = '1'" );
        assertXPathTrue( doc, "//silcn:node[ ../silcn:id = 'boolean' ]/probe:text = 'true'" );

    }


    public void testMatchRegexpWrongArity() throws Exception
    {
        String config = "test/rulesets/match-regexp-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/match-regexp-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'match-regexp() takes 2 or 3 arguments' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMatchRegexpBadFirstArg() throws Exception
    {
        String config = "test/rulesets/match-regexp-bad-first-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/match-regexp-bad-first-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'match-regexp() first argument must be of type string' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMatchRegexpBadThirdArg() throws Exception
    {
        String config = "test/rulesets/match-regexp-bad-third-arg.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/match-regexp-bad-third-arg.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'match-regexp() third argument must be of type number' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMatchRegexpBadRegexp2Args() throws Exception
    {
        String config = "test/rulesets/match-regexp-bad-regexp-2-args.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/match-regexp-bad-regexp-2-args.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'Error compiling regular expression:' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMatchRegexpBadRegexp3Args() throws Exception
    {
        String config = "test/rulesets/match-regexp-bad-regexp-3-args.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/match-regexp-bad-regexp-3-args.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'Error compiling regular expression:' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMatchRegexpThirdArgIoob() throws Exception
    {
        String config = "test/rulesets/match-regexp-third-arg-ioob.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/match-regexp-third-arg-ioob.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'Error compiling regular expression:' ) and contains( probe:text, 'error evaluating XPath expression: Error compiling regular expression: No group 4250' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testMatchRegexpVariable() throws Exception
    {
        String config = "test/rulesets/oup.xml";
        String src = "test/test-cases/oup.xml";
        String dest = "test/dest/testGlobalRegexpVariable.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 2, doc, "//silcn:node" );

        assertXPathEquals( "@doi='10.1093/...' does not match product id DOI 'med-'", doc,
                "(//silcn:node)[../silcn:id='test']/probe:text" );
        assertXPathEquals( "@doi='10.1093/...'; 10.1093/...", doc,
                "(//silcn:node)[../silcn:id='test2']/probe:text" );
    }


    public void testSystemIdWrongArity() throws Exception
    {
        String config = "test/rulesets/system-id-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/system-id-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'system-id() takes no arguments.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testSystemId() throws Exception
    {
        String config = "test/rulesets/system-id.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/system-id.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 1" );

        assertXPathEquals( "test/test-cases/dummy-instance.xml", doc,
                "//silcn:node[ ../silcn:id = 'system-id' ]/probe:text" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testIsValidIsbn13WrongArity() throws Exception
    {
        String config = "test/rulesets/is-valid-isbn-13-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-isbn-13-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'is-valid-isbn-13() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testIsValidIsbn13() throws Exception
    {
        String config = "test/rulesets/is-valid-isbn-13.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-isbn-13.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testIsValidIsbnWrongArity() throws Exception
    {
        String config = "test/rulesets/is-valid-isbn-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-isbn-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'is-valid-isbn() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testIsValidIsbn() throws Exception
    {
        String config = "test/rulesets/is-valid-isbn.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-isbn.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testIsValidUriWrongArity() throws Exception
    {
        String config = "test/rulesets/is-valid-uri-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-uri-wrong-arity.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //probe:message ) = 1" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'FATAL' and contains( probe:text, 'validate-uri() takes 1 argument.' ) ]" );

        assertXPathTrue( doc, "not( //silcn:node )" );

    }


    public void testIsValidUri() throws Exception
    {
        String config = "test/rulesets/is-valid-uri.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/is-valid-uri.out";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count( //silcn:node ) = 2" );

        assertXPathTrue( doc, "count( //silcn:node[../silcn:id='invalid'] ) = 1" );

        assertXPathTrue( doc, "count( //silcn:node[../silcn:id='valid'] ) = 1" );

        assertXPathTrue( doc, "not( //probe:message )" );

    }


    public void testExtFuncRomanNumeralToDecimalTooFewArgs() throws Exception
    {
        String config = "test/rulesets/roman-numeral-to-decimal-wrong-arity.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/roman-numeral-to-decimal-wrong-arity.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 1, doc, "//probe:message" );
        assertXPathTrue(
                doc,
                "//probe:message/probe:text[ ends-with( ., 'roman-numeral-to-decimal() takes one or two arguments' ) ]" );
    }


    public void testExtFuncRomanNumeralToDecimalTooManyArgs() throws Exception
    {
        String config = "test/rulesets/roman-numeral-to-decimal-wrong-arity2.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/roman-numeral-to-decimal-wrong-arity2.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 1, doc, "//probe:message" );
        assertXPathTrue(
                doc,
                "//probe:message/probe:text[ ends-with( ., 'roman-numeral-to-decimal() takes one or two arguments' ) ]" );
    }


    public void testExtFuncRomanNumeralToDecimalWrongArg1Type() throws Exception
    {
        String config = "test/rulesets/roman-numeral-to-decimal-wrong-arg-type.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/roman-numeral-to-decimal-wrong-arg-type.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 2, doc, "//probe:message" );
        assertXPathTrue(
                doc,
                "//probe:message[1]/probe:text[ ends-with( ., "
                        + "'first argument to roman-numeral-to-decimal() must be of type string or node-set' ) ]" );

        assertXPathTrue(
                doc,
                "//probe:message[2]/probe:text[ ends-with( ., "
                        + "'first argument to roman-numeral-to-decimal() must be of type string or node-set' ) ]" );
    }


    public void testExtFuncRomanNumeralToDecimalWrongArg2Type() throws Exception
    {
        String config = "test/rulesets/roman-numeral-to-decimal-wrong-arg-type2.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/roman-numeral-to-decimal-wrong-arg-type2.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 2, doc, "//probe:message" );
        assertXPathTrue( doc, "//probe:message[1]/probe:text[ ends-with( ., "
                + "'second argument to roman-numeral-to-decimal() must be of type boolean' ) ]" );

        assertXPathTrue(
                doc,
                "//probe:message[2]/probe:text[ ends-with( ., "
                        + "'argument to roman-numeral-to-decimal() must be of type string or node-set' ) ]" );
    }


    public void testExtFuncRomanNumeralToDecimal() throws Exception
    {
        String config = "test/rulesets/roman-numeral-to-decimal.xml";
        String src = "test/test-cases/dummy-instance.xml";
        String dest = "test/dest/roman-numeral-to-decimal.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        //FOLLOWING ONLY RELEVANT IF SECOND ARG IS GIVEN AND true()
        /*assertCountXPath( 10, doc, "//probe:message" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ ends-with( probe:text, "
                        + "\"error parsing roman numeral: no more chars expected at position 4: 'iiii'\" ) ]" );

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ ends-with( probe:text, "
                        + "\"error parsing roman numeral: no more chars expected at position 3: 'ivi'\" ) ]" );
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ ends-with( probe:text, "
                        + "\"error parsing roman numeral: illegal whitespace char at position 2: 'v '\" ) ]" );
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ ends-with( probe:text, "
                        + "\"error parsing roman numeral: illegal whitespace char at position 2: 'x '\" ) ]" );        
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ ends-with( probe:text, "
                        + "\"error parsing roman numeral: no more chars expected at position 5: 'viiiq'\" ) ]" ); 
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ ends-with( probe:text, "
                        + "\"error parsing roman numeral: unrecognised char at position 2: 'xq'\" ) ]" );         
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ contains( probe:text, "
                        + "\"error parsing roman numeral: no more chars expected at position 2: 'vv'\" ) ]" );
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ contains( probe:text, "
                        + "\"error parsing roman numeral: no more chars expected at position 3: 'viv'\" ) ]" );  
        
        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ contains( probe:text, "
                        + "\"error parsing roman numeral: no more chars expected at position 4: 'xxxx'\" ) ]" );          

        assertXPathTrue(
                doc,
                "//probe:message[ probe:type = 'error' ]"
                        + "[ contains( probe:text, "
                        + "\"error parsing roman numeral: invalid x char at position 2: 'vx'\" ) ]" );  */

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-1' ]/probe:text = '1'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-2' ]/probe:text = '2'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-3' ]/probe:text = '3'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-4' ]/probe:text = '4'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-5' ]/probe:text = '5'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-6' ]/probe:text = '6'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-7' ]/probe:text = '7'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-8' ]/probe:text = '8'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-9' ]/probe:text = '9'" );

        assertValueOfXPath( "10", doc, "//silcn:node[../silcn:id = 'test-10' ]/probe:text" );

        assertValueOfXPath( "14", doc, "//silcn:node[../silcn:id = 'test-14' ]/probe:text" );

        assertValueOfXPath( "16", doc, "//silcn:node[../silcn:id = 'test-16' ]/probe:text" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-20' ]/probe:text = '20'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-24' ]/probe:text = '24'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-25' ]/probe:text = '25'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-26' ]/probe:text = '26'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-28' ]/probe:text = '28'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-29' ]/probe:text = '29'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-30' ]/probe:text = '30'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-34' ]/probe:text = '34'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-40' ]/probe:text = '40'" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-50' ]/probe:text = '50'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-60' ]/probe:text = '60'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-70' ]/probe:text = '70'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-80' ]/probe:text = '80'" );
        assertValueOfXPath( "90", doc, "//silcn:node[../silcn:id = 'test-90' ]/probe:text" );
        assertValueOfXPath( "99", doc, "//silcn:node[../silcn:id = 'test-99' ]/probe:text" );

        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-49' ]/probe:text = '49'" );
        assertValueOfXPath( "88", doc, "//silcn:node[../silcn:id = 'test-88' ]/probe:text" );
        assertValueOfXPath( "69", doc, "//silcn:node[../silcn:id = 'test-69' ]/probe:text" );

        //Unicode chars
        assertValueOfXPath( "1", doc, "//silcn:node[../silcn:id = 'test-1-uni' ]/probe:text" );
        assertValueOfXPath( "2", doc, "//silcn:node[../silcn:id = 'test-2-uni' ]/probe:text" );
        assertValueOfXPath( "3", doc, "//silcn:node[../silcn:id = 'test-3-uni' ]/probe:text" );
        assertValueOfXPath( "4", doc, "//silcn:node[../silcn:id = 'test-4-uni' ]/probe:text" );
        assertValueOfXPath( "5", doc, "//silcn:node[../silcn:id = 'test-5-uni' ]/probe:text" );
        assertValueOfXPath( "6", doc, "//silcn:node[../silcn:id = 'test-6-uni' ]/probe:text" );
        assertValueOfXPath( "7", doc, "//silcn:node[../silcn:id = 'test-7-uni' ]/probe:text" );
        assertValueOfXPath( "8", doc, "//silcn:node[../silcn:id = 'test-8-uni' ]/probe:text" );
        assertValueOfXPath( "9", doc, "//silcn:node[../silcn:id = 'test-9-uni' ]/probe:text" );
        assertValueOfXPath( "10", doc, "//silcn:node[../silcn:id = 'test-10-uni' ]/probe:text" );
        assertValueOfXPath( "11", doc, "//silcn:node[../silcn:id = 'test-11-uni' ]/probe:text" );
        assertValueOfXPath( "12", doc, "//silcn:node[../silcn:id = 'test-12-uni' ]/probe:text" );
        assertValueOfXPath( "14", doc, "//silcn:node[../silcn:id = 'test-14-uni' ]/probe:text" );
        assertValueOfXPath( "50", doc, "//silcn:node[../silcn:id = 'test-50-uni' ]/probe:text" );
        assertValueOfXPath( "60", doc, "//silcn:node[../silcn:id = 'test-60-uni' ]/probe:text" );
        assertValueOfXPath( "70", doc, "//silcn:node[../silcn:id = 'test-70-uni' ]/probe:text" );
        assertValueOfXPath( "80", doc, "//silcn:node[../silcn:id = 'test-80-uni' ]/probe:text" );
        assertValueOfXPath( "90", doc, "//silcn:node[../silcn:id = 'test-90-uni' ]/probe:text" );

        assertXPathTrue( doc,
                "//silcn:node[../silcn:id = 'test-interpolated-whitespace' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-bad-4' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-bad-6' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-bad-7' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-4-stop' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-bad-char' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-vv' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id = 'test-viv' ]/probe:text = 'NaN'" );
        assertXPathTrue( doc,
                "//silcn:node[../silcn:id = 'test-leading-whitespace' ]/probe:text = '5'" );
        assertXPathTrue( doc,
                "//silcn:node[../silcn:id = 'test-trailing-whitespace' ]/probe:text = '5'" );

    }


    public void testIdFunction() throws Exception
    {
        String config = "test/rulesets/testIdFunction.xml";
        String src = "test/test-cases/ids.xml";
        String dest = "test/dest/testIdFunction.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 7, doc, "//silcn:node" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-1']/probe:text = 'foo'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-2']/probe:text = 'bar'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-3']/probe:text = '2'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-nodeset']/probe:text = '2'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-empty-nodeset']/probe:text = '0'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-number']/probe:text = '0'" );
        assertXPathTrue( doc, "//silcn:node[../silcn:id='test-boolean']/probe:text = '0'" );
    }


    public void testIdFunction2() throws Exception
    {
        String config = "test/rulesets/testIdFunction2.xml";
        String src = "test/test-cases/ids2.xml";
        String dest = "test/dest/testIdFunction2.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 2, doc, "//silcn:node" );
        
        assertXPathEquals( "ID=\"acprof-ISBN-boxedMatter-nn\"", doc, "//silcn:node[../silcn:id='test-1']/probe:text" );
        assertXPathEquals( "a1", doc, "//silcn:node[../silcn:id='test-2']/probe:text" );
    }
    
    public void testTokenizeWrongArity() throws Exception
    {
        String config = "test/rulesets/tokenize-wrong-arity.xml";
        String src = "test/test-cases/country-codes.xml";
        String dest = "test/dest/testTokenizeWrongArity.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 1, doc, "//probe:message" );
        assertCountXPath( 0, doc, "//silcn:node" );
        
        assertXPathEquals( "error evaluating XPath expression [ID='test'] at " +
        		"/silcn:silcn/silcn:selection/silcn:set-criterion/silcn:expression: " +
        		"'//foo[ tokenize( @country ) != document('country-codes.xml')//allowed ]' " +
        		"error evaluating XPath expression: tokenize() requires 1 argument", doc, "//probe:message/probe:text" );
    }   
    
    public void testTokenizeWrongArg() throws Exception
    {
        String config = "test/rulesets/tokenize-wrong-arg.xml";
        String src = "test/test-cases/country-codes.xml";
        String dest = "test/dest/testTokenizeWrongArg.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 1, doc, "//probe:message" );
        assertCountXPath( 0, doc, "//silcn:node" );
        
        assertXPathEquals( "error evaluating XPath expression [ID='test'] at " +
                "/silcn:silcn/silcn:selection/silcn:set-criterion/silcn:expression: " +
                "'//foo[ tokenize( @country, @country ) != document('country-codes.xml')//allowed ]' " +
                "error evaluating XPath expression: first argument to tokenize() must be of type string", doc, "//probe:message/probe:text" );
    }
    
    public void testTokenizeWrongArg2() throws Exception
    {
        String config = "test/rulesets/tokenize-wrong-arg2.xml";
        String src = "test/test-cases/country-codes.xml";
        String dest = "test/dest/testTokenizeWrongArg2.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 1, doc, "//probe:message" );
        assertCountXPath( 0, doc, "//silcn:node" );
        
        assertXPathEquals( "error evaluating XPath expression [ID='test'] at " +
                "/silcn:silcn/silcn:selection/silcn:set-criterion/silcn:expression: " +
                "'//foo[ tokenize( string(@country), @country ) != document('country-codes.xml')//allowed ]' " +
                "error evaluating XPath expression: second argument to tokenize() must be of type string", doc, "//probe:message/probe:text" );
    } 
    
    public void testTokenizeFails() throws Exception
    {
        String config = "test/rulesets/tokenize-fails.xml";
        String src = "test/test-cases/tokenize-fails.xml";
        String dest = "test/dest/testTokenizeFails.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 1, doc, "//silcn:node" );
        
        assertXPathEquals( "tokenized value GB|FR|DE|US|FOO not allowed", doc, "//silcn:node/probe:text" );
    }
    
    public void testTokenizeSucceeds() throws Exception
    {
        String config = "test/rulesets/tokenize.xml";
        String src = "test/test-cases/tokenize.xml";
        String dest = "test/dest/testTokenizeSucceeds.out";

        runXMLProbe( config, src, dest );

        int doc = ShailNavigator.getInstance().getDocument( dest );

        assertCountXPath( 0, doc, "//probe:message" );
        assertCountXPath( 0, doc, "//silcn:node" );
    }    
}
