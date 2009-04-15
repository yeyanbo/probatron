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
 * Created on 18 Aug 2008
 */
package org.probatron.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is adapted from the Jaxen 1.1 test suite classes for XPath 1.0 functions.
 * @author andrews
 *
 * @version $Id: XPath1FunctionTestSuite.java,v 1.1 2008/11/11 10:43:40 GBDP\andrews Exp $
 */
public class XPath1FunctionTestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        
        suite.addTest( new TestSuite( BooleanTest.class ) );
        suite.addTest( new TestSuite( CeilingTest.class ) );
        suite.addTest( new TestSuite( ContainsTest.class ) );
        suite.addTest( new TestSuite( CountTest.class ) );
        suite.addTest( new TestSuite( FalseTest.class ) );
        suite.addTest( new TestSuite( FloorTest.class ) );
        suite.addTest( new TestSuite( IdTest.class ) );
        suite.addTest( new TestSuite( LangTest.class ) );
        suite.addTest( new TestSuite( LastTest.class ) );
        suite.addTest( new TestSuite( LocalNameTest.class ) );
        suite.addTest( new TestSuite( NamespaceURITest.class ) );
        suite.addTest( new TestSuite( NameTest.class ) );
        suite.addTest( new TestSuite( NormalizeSpaceTest.class ) );
        suite.addTest( new TestSuite( NotTest.class ) );        
        suite.addTest( new TestSuite( NumberTest.class ) );
        suite.addTest( new TestSuite( PositionTest.class ) );
        suite.addTest( new TestSuite( RoundTest.class ) );
        suite.addTest( new TestSuite( StartsWithTest.class ) );
        suite.addTest( new TestSuite( StringLengthTest.class ) );
        suite.addTest( new TestSuite( StringTest.class ) );
        suite.addTest( new TestSuite( SubstringAfterTest.class ) );
        suite.addTest( new TestSuite( SubstringBeforeTest.class ) );
        suite.addTest( new TestSuite( SubstringTest.class ) );
        suite.addTest( new TestSuite( SumTest.class ) );
        suite.addTest( new TestSuite( TranslateTest.class ) );
        suite.addTest( new TestSuite( TrueTest.class ) );

        return suite;
    }
}
