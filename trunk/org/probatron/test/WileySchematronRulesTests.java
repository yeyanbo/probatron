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
 * Created on 30 Sep 2008
 */
package org.probatron.test;

import org.probatron.ShailNavigator;

public class WileySchematronRulesTests extends TestsBase
{
    private int doc;


    protected void setUp() throws Exception
    {
        super.setUp();
        System.setProperty( "error-format", "xml" );
        System.clearProperty( "relaxng-schema-location" );
        getNamespaceContext().addNamespace( "svrl", "http://purl.oclc.org/dsdl/svrl" ); //the SVRL namespace
    }


    private void checkReportLocationAndMessage( String location, String message )
            throws Exception
    {
        assertXPathTrue( doc, "//svrl:successful-report[svrl:text='" + message
                + "' and @location='" + location + "']" );
    }


    private void checkAssertLocationAndMessage( String location, String message )
            throws Exception
    {
        assertXPathTrue( doc, "//svrl:failed-assert[svrl:text='" + message
                + "' and @location='" + location + "']" );
    }


    public void testAssertAFails() throws Exception
    {
        String config = "rule-a.sch";
        String src = "test/a-fails.xml";
        String dest = "dest/a-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/wng:issueMeta[1]/wng:numberingGroup[1]",
                "issueMeta/numberingGroup/number@volume and number@issue must exist" );

    }


    public void testAssertASucceeds() throws Exception
    {
        String config = "rule-a.sch";
        String src = "test/a-succeeds.xml";
        String dest = "dest/a-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertBFails() throws Exception
    {
        String config = "rule-b.sch";
        String src = "test/b-fails.xml";
        String dest = "dest/b-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 2" );

        checkAssertLocationAndMessage( "/foo[1]/wng:issueMeta[1]/wng:copyrightInfo[1]",
                "copyrightYear must exist" );
        checkAssertLocationAndMessage( "/foo[1]/wng:unitMeta[1]/wng:copyrightInfo[1]",
                "copyrightYear must exist" );
    }


    public void testAssertBSucceeds() throws Exception
    {
        String config = "rule-b.sch";
        String src = "test/b-succeeds.xml";
        String dest = "dest/b-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertCFails() throws Exception
    {
        String config = "rule-c.sch";
        String src = "test/c-fails.xml";
        String dest = "dest/c-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 2" );

        checkAssertLocationAndMessage( "/foo[1]/wng:foo[1]",
                "must have format yyyy-mm-dd, yyyy-mm, or yyyy" );
        checkAssertLocationAndMessage( "/foo[1]/wng:coverDate[1]",
                "must have format yyyy-mm-dd, yyyy-mm, or yyyy" );
    }


    public void testAssertCSucceeds() throws Exception
    {
        String config = "rule-c.sch";
        String src = "test/c-succeeds.xml";
        String dest = "dest/c-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertEFails() throws Exception
    {
        String config = "rule-e.sch";
        String src = "test/e-fails.xml";
        String dest = "dest/e-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:successful-report) = 2" );

        String msg = "It should point to the id on the enclosing displayedItem or figure, and not the graphic etc.";
        checkReportLocationAndMessage( "/foo[1]/wng:xref[1]", msg );
        checkReportLocationAndMessage( "/foo[1]/wng:xref[2]", msg );
    }


    public void testAssertESucceeds() throws Exception
    {
        String config = "rule-e.sch";
        String src = "test/e-succeeds.xml";
        String dest = "dest/e-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:successful-report) = 0" );
    }


    public void testAssertFGFails() throws Exception
    {
        String config = "rule-fg.sch";
        String src = "test/fg-fails.xml";
        String dest = "dest/fg-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 3" );

        String msg1 = "Only occurrence of <label> in <listItem> must be as first element.";
        assertXPathTrue( doc, "count( //svrl:failed-assert[svrl:text='" + msg1
                + "' and @location='/foo[1]/wng:listItem[1]/wng:label[2]'] ) = 1" );

        String msg2 = "Only occurrence of <label> in everything else it can occur in must be as first element.";
        checkAssertLocationAndMessage( "/foo[1]/wng:cahuna[1]/wng:label[2]", msg2 );
        checkAssertLocationAndMessage( "/foo[1]/wng:listItem[1]/wng:label[2]", msg2 );
    }


    public void testAssertSucceeds() throws Exception
    {
        String config = "rule-fg.sch";
        String src = "test/fg-succeeds.xml";
        String dest = "dest/fg-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertHIFails() throws Exception
    {
        String config = "rule-hi.sch";
        String src = "test/hi-fails.xml";
        String dest = "dest/hi-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 2" );

        String msg1 = "Exactly one occurrence of journalTitle in a <citation type=\"journal\">";
        String msg2 = "At most one occurrence of articleTitle, vol, issue, firstPage, pubYear in a <citation type=\"journal\">";

        checkAssertLocationAndMessage( "/foo[1]/wng:citation[1]", msg1 );
        checkAssertLocationAndMessage( "/foo[1]/wng:citation[1]", msg2 );
    }


    public void testAssertHISucceeds() throws Exception
    {
        String config = "rule-hi.sch";
        String src = "test/hi-succeeds.xml";
        String dest = "dest/hi-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertJFails() throws Exception
    {
        String config = "rule-j.sch";
        String src = "test/j-fails.xml";
        String dest = "dest/j-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        String msg1 = "journalTitle shall not occur except in <citation type=\"journal\">";

        checkAssertLocationAndMessage( "/foo[1]/wng:journalTitle[1]", msg1 );
    }


    public void testAssertJSucceeds() throws Exception
    {
        String config = "rule-j.sch";
        String src = "test/j-succeeds.xml";
        String dest = "dest/j-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertKFails() throws Exception
    {
        String config = "rule-k.sch";
        String src = "test/k-fails.xml";
        String dest = "dest/k-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 2" );

        String msg1 = "At most one occurrence of bookTitle in a <citation type=\"book\">";
        String msg2 = "At most one occurrence of bookSeriesTitle in a <citation type=\"book\">";

        checkAssertLocationAndMessage( "/foo[1]/wng:citation[1]", msg1 );
        checkAssertLocationAndMessage( "/foo[1]/wng:citation[1]", msg2 );
    }


    public void testAssertKSucceeds() throws Exception
    {
        String config = "rule-k.sch";
        String src = "test/k-succeeds.xml";
        String dest = "dest/k-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertLFails() throws Exception
    {
        String config = "rule-l.sch";
        String src = "test/l-fails.xml";
        String dest = "dest/l-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 2" );

        String msg1 = "If there is more than one <abstract>, then @type is present on all of them.";

        checkAssertLocationAndMessage( "/foo[1]/wng:abstract[1]", msg1 );

        checkAssertLocationAndMessage( "/foo[1]/wng:abstract[2]", msg1 );
    }


    public void testAssertLSucceeds() throws Exception
    {
        String config = "rule-l.sch";
        String src = "test/l-succeeds.xml";
        String dest = "dest/l-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertMFails() throws Exception
    {
        String config = "rule-m.sch";
        String src = "test/m-fails.xml";
        String dest = "dest/m-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        String msg1 = "Within <titleGroup>, all <title>s must have @type";

        checkAssertLocationAndMessage( "/foo[1]/wng:titleGroup[1]/wng:title[1]", msg1 );

    }


    public void testAssertMSucceeds() throws Exception
    {
        String config = "rule-m.sch";
        String src = "test/m-succeeds.xml";
        String dest = "dest/m-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertNFails() throws Exception
    {
        String config = "rule-n.sch";
        String src = "test/n-fails.xml";
        String dest = "dest/n-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        String msg1 = "If <mediaResource> has no content then its @href must be specified.";

        checkAssertLocationAndMessage( "/foo[1]/wng:mediaResource[1]", msg1 );

    }


    public void testAssertNSucceeds() throws Exception
    {
        String config = "rule-n.sch";
        String src = "test/n-succeeds.xml";
        String dest = "dest/n-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertOFails() throws Exception
    {
        String config = "rule-o.sch";
        String src = "test/o-fails.xml";
        String dest = "dest/o-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:successful-report) = 1" );

        String msg1 = "<bibliography> shall not be an immediately following sibling of <bibliography> (use bibSection instead).";

        checkReportLocationAndMessage( "/foo[1]/wng:bibliography[1]", msg1 );

    }


    public void testAssertOSucceeds() throws Exception
    {
        String config = "rule-o.sch";
        String src = "test/o-succeeds.xml";
        String dest = "dest/o-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:successful-report) = 0" );
    }


    public void testAssertPFails() throws Exception
    {
        String config = "rule-p.sch";
        String src = "test/p-fails.xml";
        String dest = "dest/p-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 6" );

        assertXPathTrue(
                doc,
                "//svrl:failed-assert[svrl:text=\"wng:title/@type='focusSection' to occur only in issueMeta\" and @location='/foo[1]/wng:title[1]']" );
        assertXPathTrue(
                doc,
                "//svrl:failed-assert[svrl:text=\"wng:title/@type='supplementTitle' to occur only in issueMeta\" and @location='/foo[1]/wng:title[2]']" );
        assertXPathTrue(
                doc,
                "//svrl:failed-assert[svrl:text=\"wng:title/@type='supplementSeriesTitle' to occur only in issueMeta\" and @location='/foo[1]/wng:title[3]']" );
        assertXPathTrue(
                doc,
                "//svrl:failed-assert[svrl:text=\"wng:title/@type='supplementSubTitle' to occur only in issueMeta\" and @location='/foo[1]/wng:title[4]']" );
        assertXPathTrue(
                doc,
                "//svrl:failed-assert[svrl:text=\"wng:numbering/@type='supplement' to occur only in issueMeta\" and @location='/foo[1]/wng:numbering[1]']" );
        assertXPathTrue(
                doc,
                "//svrl:failed-assert[svrl:text=\"wng:numbering/@type='supplementSeriesVolume' to occur only in issueMeta\" and @location='/foo[1]/wng:numbering[2]']" );

    }


    public void testAssertPSucceeds() throws Exception
    {
        String config = "rule-p.sch";
        String src = "test/p-succeeds.xml";
        String dest = "dest/p-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertQFails() throws Exception
    {
        String config = "rule-q.sch";
        String src = "test/q-fails.xml";
        String dest = "dest/q-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:infoAsset[1]/wng:accessionId[1]",
                "In infoAsset, accessionId must be empty." );
    }


    public void testAssertQSucceeds() throws Exception
    {
        String config = "rule-q.sch";
        String src = "test/q-succeeds.xml";
        String dest = "dest/q-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertRFails() throws Exception
    {
        String config = "rule-r.sch";
        String src = "test/r-fails.xml";
        String dest = "dest/r-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:citation[1]",
                "citation/@type=\"selfOriginalLanguage\" shall only occur in selfCitationGroup" );
    }


    public void testAssertRSucceeds() throws Exception
    {
        String config = "rule-r.sch";
        String src = "test/r-succeeds.xml";
        String dest = "dest/r-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertSTFails() throws Exception
    {
        String config = "rule-st.sch";
        String src = "test/st-fails.xml";
        String dest = "dest/st-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );
        assertXPathTrue( doc, "count(//svrl:successful-report) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:issn[1]",
                "All ISSNs should be valid (i.e. checksum is correct)." );

        checkReportLocationAndMessage( "/foo[1]/wng:issn[2]",
                "Content in the body should not contain the keyboard hyphen character." );
    }


    public void testAssertSTSucceeds() throws Exception
    {
        String config = "rule-st.sch";
        String src = "test/st-succeeds.xml";
        String dest = "dest/st-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertUFails() throws Exception
    {
        String config = "rule-u.sch";
        String src = "test/u-fails.xml";
        String dest = "dest/u-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:unitMeta[1]",
                "wng:unitMeta \"type\" attribute must contain allowed value." );

    }


    public void testAssertUSucceeds() throws Exception
    {
        String config = "rule-u.sch";
        String src = "test/u-succeeds.xml";
        String dest = "dest/u-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertVFails() throws Exception
    {
        String config = "rule-v.sch";
        String src = "test/v-fails.xml";
        String dest = "dest/v-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:copyrightInfo[1]",
                "wng:copyrightInfo \"ownership\" attribute must contain allowed value." );

    }


    public void testAssertVSucceeds() throws Exception
    {
        String config = "rule-v.sch";
        String src = "test/v-succeeds.xml";
        String dest = "dest/v-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }


    public void testAssertWFails() throws Exception
    {
        String config = "rule-w.sch";
        String src = "test/w-fails.xml";
        String dest = "dest/w-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:creator[1]",
                "wng:creator \"creatorRole\" attribute must contain allowed value." );

    }


    public void testAssertWSucceeds() throws Exception
    {
        String config = "rule-w.sch";
        String src = "test/w-succeeds.xml";
        String dest = "dest/w-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }

    public void testAssertXYFails() throws Exception
    {
        String config = "rule-xy.sch";
        String src = "test/xy-fails.xml";
        String dest = "dest/xy-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 2" );

        checkAssertLocationAndMessage( "/foo[1]/wng:doi[2]",
        "wng:doi \"level\" attribute must contain allowed value." );
        
        checkAssertLocationAndMessage( "/foo[1]/wng:doi[3]",
                "wng:doi \"registered\" attribute must contain allowed value." );

    }


    public void testAssertXYSucceeds() throws Exception
    {
        String config = "rule-xy.sch";
        String src = "test/xy-succeeds.xml";
        String dest = "dest/xy-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }

    public void testAssertZFails() throws Exception
    {
        String config = "rule-z.sch";
        String src = "test/z-fails.xml";
        String dest = "dest/z-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/foo[1]/wng:xref[1]",
        "wng:xref \"style\" attribute must contain allowed value." );

    }


    public void testAssertZSucceeds() throws Exception
    {
        String config = "rule-z.sch";
        String src = "test/z-succeeds.xml";
        String dest = "dest/z-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }
    
    public void testAssertAAFails() throws Exception
    {
        String config = "rule-aa.sch";
        String src = "test/aa-fails.xml";
        String dest = "dest/aa-fails.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );

        checkAssertLocationAndMessage( "/wng:foo[1]",
        "wng:foo \"countryCode\" attribute must contain allowed value." );

    }

    public void testAssertAASucceeds() throws Exception
    {
        String config = "rule-aa.sch";
        String src = "test/aa-succeeds.xml";
        String dest = "dest/aa-succeeds.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert) = 0" );
    }
    
//    public void testAssertAllFail() throws Exception
//    {
//        String config = "rules.sch";
//        String src = "test/all-fail.xml";
//        String dest = "dest/all-fail.svrl";
//        
//        System.setProperty( "relaxng-schema-location", "schemas/wileyml3g.rng" ); //N.B. resolved against cwd
//
//        runXMLProbe( config, src, dest );
//
//        ShailNavigator nav = ShailNavigator.getInstance();
//        doc = nav.getDocument( dest );
//
//        assertXPathTrue( doc, "count(//svrl:failed-assert) = 1" );
//
//        checkAssertLocationAndMessage( "/wng:foo[1]",
//        "wng:foo \"countryCode\" attribute must contain allowed value." );
//
//    }

    public void testAssertAllSucceed() throws Exception
    {
        String config = "rules.sch";
        String src = "test/all-succeed.xml";
        String dest = "dest/all-succeed.svrl";

        runXMLProbe( config, src, dest );

        ShailNavigator nav = ShailNavigator.getInstance();
        int doc = nav.getDocument( dest );

        assertXPathTrue( doc, "count(//svrl:failed-assert|//svrl:successful-report) = 0" );
    }    
}
