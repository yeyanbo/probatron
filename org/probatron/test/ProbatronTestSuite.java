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

import junit.framework.Test;
import junit.framework.TestSuite;

public class ProbatronTestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        
        //TODO: reinstate these suites as appropriate
        
//        suite.addTest( new TestSuite( EqualityTests.class ) );
//        suite.addTest( new TestSuite( ExtensionFunctionTests.class ) );
//        suite.addTest( new TestSuite( ExternalDocumentLocatorsTest.class ) );
//        suite.addTest( new TestSuite( LocatorTests.class ) );
//        suite.addTest( new TestSuite( StressTests.class ) );
//        suite.addTest( new TestSuite( SundryTests.class ) );
//        suite.addTest( new TestSuite( XIncludeTests.class ) );

        //some external test suites
//        suite.addTest( RelaxNGTestSuite.suite() );
        suite.addTest( SchematronTestSuite.suite() );
        suite.addTest( XPath1FunctionTestSuite.suite() );
        suite.addTest( XPathTestSuite.suite() );
        
        return suite;
    }
}


