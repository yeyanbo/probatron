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
 * Created on 8 Aug 2008
 */
package org.probatron;

import com.griffinbrown.xmltool.XMLToolException;

public class GlobalXPathVariable extends XPathVariable
{
    public GlobalXPathVariable( String name, String expr )
    {
        super( name, expr );
    }


    public GlobalXPathVariable( String namespaceURI, String name, String expr )
    {
        super( namespaceURI, name, expr );
    }


    /**
     * @see org.probatron.XPathVariable#evaluate(int, org.probatron.QueryEvaluator)
     */
    public Object evaluate( int context, QueryEvaluator evaluator ) throws XMLToolException
    {
        Object result = super.evaluate( context, evaluator );
        evaluator.setGlobalVariableValue( getNamespaceURI(), getName(), result );
        return result;
    }

}
