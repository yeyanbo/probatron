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
 * Created on 19 Dec 2007
 */
package org.probatron;

import com.griffinbrown.xmltool.XMLToolException;

public interface Variable
{
    /**
     * Accesses the expression declared for the variable.
     * @return the declared expression
     */
    String getExpression();
    
    /**
     * Accesses the name of a variable.
     * @return
     */
    String getName();
    
    /**
     * Compiles the variable using the specified evaluator.
     * @return the compiled expression
     * @throws if the expression cannot be compiled
     */
    Object compile( QueryEvaluator evaluator ) throws XMLToolException;
}
