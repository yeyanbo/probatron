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
 * Created on 22-Dec-2005
 */
package com.griffinbrown.xmltool.utils;

/**
 * Factory for XPath expressions.
 * @author andrews
 *
 * $Id$
 */
public interface XPathFactory
{
    /**
     * Creates an XPath object.
     * @param expression the XPath expression
     * @return the expression as an object
     * @throws Exception if syntax or other general problems occur in creating the XPath object
     */
    Object createXPath( String expression ) throws Exception;
}
