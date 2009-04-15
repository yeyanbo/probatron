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
 * Created on 8 Jan 2008
 */
package com.griffinbrown.schematron;

import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.shail.util.ShailList;
import com.griffinbrown.xmltool.XPathLocator;
import com.griffinbrown.xmltool.utils.Utils;

/**
 * Represents a Schematron <code>report</code> assertion.
 * @author andrews
 *
 * @version $Id: Report.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class Report extends Assert
{
    private int node;
    private String origExpr; //the UN-negated version of the @test value


    Report( int node, Rule rule ) throws JaxenException
    {
        super( node, rule );
        this.node = node;
    }


    /**
     * <tt>report</tt> elements should have their expression negated (see s6.2 of the
     * Schematron spec)
     * 
     * @see com.griffinbrown.schematron.Assert#setExpression(org.dom4j.Node)
     */
    String setExpression( int node ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "@test" );
        ShailList test = ( ShailList )xpath.evaluate( node );
        if( ! test.isEmpty() )
        {
            this.origExpr = StringFunction.evaluate( test, ShailNavigator.getInstance() );
            return "not(" + this.origExpr + ")";
        }
        return null;
    }


    String report( LocatorImpl locator, XPathLocator xpathLocator, String message )
    {
        /*successful-report = element successful-report {
        attlist.assert-and-report,
        diagnostic-reference*,
        human-text}
        */

        StringBuffer s = new StringBuffer( "<successful-report" );

        if( getId() != null )
            s.append( " id='" + getId() + "'" );

        if( xpathLocator != null )
            s.append( " location='" + Utils.quoteAttr( Utils.escape( xpathLocator.toString() ) )
                    + "'" );

        s.append( " test='" + Utils.quoteAttr( Utils.escape( origExpr ) ) + "'>" );

        s.append( "<probe:line>" + locator.getLineNumber() + "</probe:line>" );
        s.append( "<probe:column>" + locator.getColumnNumber() + "</probe:column>" );

        //TODO: diagnostic-reference*

        s.append( "<text>" + message + "</text>" );

        s.append( "</successful-report>\n" );

        return s.toString();
    }
}
