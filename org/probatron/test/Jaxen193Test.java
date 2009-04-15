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
 * Created on 20 Nov 2008
 */
package org.probatron.test;

import org.probatron.ShailXPath;

import junit.framework.TestCase;


public class Jaxen193Test extends TestCase
{
    private String expr = "not( child::* or child::text() or child::processing-instruction() )";
    
    public void testJaxen193() throws Exception
    {
        ShailXPath xpath = new ShailXPath( expr );
    }
}
