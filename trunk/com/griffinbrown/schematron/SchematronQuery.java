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

import java.util.List;

import org.probatron.Query;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.xmltool.XPathLocator;

/**
 * Represents a query evaluated against an XML document as part of validation using Schematron.
 * @author andrews
 *
 * @version $Id: SchematronQuery.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public abstract class SchematronQuery implements Query
{

    /**
     * Accesses the Rule to which this query belongs
     * @return the rule to which this query belongs
     */
    public abstract Rule getRule();

    /**
     * Sets the context against which the query is evaluated.
     * @param context the evaluation context (i.e. a node-set)
     */
    public abstract void setEvaluationContext( List context );

    
    abstract int getMessageNode();
    
    abstract String report( LocatorImpl locator, XPathLocator xpathLocator, String message );

}
