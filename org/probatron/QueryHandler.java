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
 * Copyright (c) 2004 Griffin Brown Digital Publishing Ltd.
 * All rights reserved.
 */
package org.probatron;


import java.util.HashMap;

import com.griffinbrown.xmltool.Session;

/**
 * Interface to represent quality assurance using XPath. 
 */
public interface QueryHandler
{
	/**
	 * @see com.xmlprobe.QAHandler_12#getLocatorForNode(java.lang.Object)
	 */
	public abstract Object getLocatorForNode(int o);
	
	/**
	 * Evaluates any XPathQueries registered with the handler and requests them
	 * to report their results.
	 * @param doc
	 */
	public void evaluateQueries(int doc);
	
	public boolean useXPathLocators();
	
	public boolean isTimingEval();
	
	/**
	 * Whether the handler produces many DOM documents from one instance, e.g.
	 * when processing very large instances.
	 * This is mainly significant when generating XPath locators - references to
	 * a single root element are no longer meaningful.  
	 */
	public boolean handlesMultipleDocs();
	
	public Session getSession();
	
	QueryEvaluator getEvaluator();
	
	void setEvaluator( QueryEvaluator eval );
    
    void addLocatorMap( HashMap map );
}