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
 * Handles the evaluation of queries against XML documents.
 */
public interface QueryHandler
{
	/**
	 * Retrieves the SAX locator for a given node.
	 * @param node the node whose locator is required 
	 * @return locator for the node
	 */
	public abstract Object getLocatorForNode(int node);
	
	/**
	 * Evaluates any queries registered with the query evaluator and requests them 
	 * to report their results.
	 * @param doc the document against which the queries should be evaluated
	 * @see #getEvaluator()
	 */
	public void evaluateQueries(int doc);
	
	/**
	 * Accesses the application session in which this handler is active. 
	 * @return an application session
	 */
	public Session getSession();
	
	/**
	 * Accesses the evaluator for queries registered with a session of processing. 
	 * @return the registered query evaluator
	 */
	QueryEvaluator getEvaluator();
	
	/**
	 * Sets the evaluator for queries registered with a session of processing.
	 * @param evaluator the query evaluator
	 */
	void setEvaluator( QueryEvaluator evaluator );
    
	/**
	 * Adds a hashmap of nodes to their locators.  
	 * @param map a mapping of nodes to locators 
	 */
    void addLocatorMap( HashMap map );
}