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
 * Created on 18 Dec 2007
 */
package com.griffinbrown.schematron;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.probatron.jaxen.JaxenException;

import com.griffinbrown.xmltool.XMLToolException;

/**
 * Represents a Schematron pattern declared as abstract (in XPath terms, <code>pattern/@abstract='true'</code>).
 * @author andrews
 * 
 * @version $Id: AbstractPattern.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class AbstractPattern extends Pattern
{
    //TODO: is this correct???
    void resolveAbstractRules() throws XMLToolException
    {}

    private HashMap paramRefMap = new HashMap();
    
    
    AbstractPattern( int node, SchematronSchema schema ) throws JaxenException
    {
        super( node, schema );        
    }

    /**
     * @see com.griffinbrown.schematron.Pattern#resolveAbstractPatterns()
     */
    void resolveAbstractPatterns() throws XMLToolException
    {}

    /**
     * @see com.griffinbrown.schematron.Pattern#getParams()
     */
    List getParams()
    {
        return Collections.EMPTY_LIST;
    }
    
    
    
}
