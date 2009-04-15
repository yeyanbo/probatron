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
 * Created on 26 Nov 2008
 */
package org.probatron.test;

import org.probatron.AbsolutizedShailXPath;
import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.XPath;


public class XPath2XMLTest extends TestsBase
{
    public void test() throws Exception
    {
        XPath xpath = new AbsolutizedShailXPath( "//*" );
        xpath.evaluate( ShailNavigator.getInstance().getDocumentAsObject( "test/test-cases/dummy-instance.xml" ) );
        
//        xpath = new ShailXPath( "id('foo')" );
//        xpath = new ShailXPath( "id(//bar)" );
//        xpath = new ShailXPath( "foo | bar" );
    }
}
