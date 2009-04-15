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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.probatron.ShailNavigator;
import org.probatron.ShailXPath;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.function.StringFunction;

import com.griffinbrown.shail.util.ShailIterator;
import com.griffinbrown.shail.util.ShailList;

/**
 * Represents a Schematron validation phase.
 * @author andrews
 *
 * @version $Id: Phase.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class Phase
{
    private String id;
    private List activePatterns = new ArrayList(); //a list of Patterns active in this phase
    private List activePatternIds; //the IDs of active Patterns; a list of strings
    private List variables = new ArrayList();
    private SchematronSchema schema;

    /**
     * Constant value to signal that all phases are active.
     * This Phase has a fixed id value of "#ALL".
     * Note that this object <strong>cannot</strong> be used to access patterns or 
     * variables active for all phases. Appropriate calls to the schema or configuration
     * in force should be made instead.
     * 
     * @see SchematronSchema#getActivePatterns()
     * @see SchematronSchema#getVariables()  
     * @see SchematronConfiguration#getQueries()
     * @see SchematronConfiguration#getVariables()
     */
    public static final Phase PHASE_ALL = new Phase() {
        public final String getId()
        {
            return Phase.ALL;
        }
    };
    static final String DEFAULT = "#DEFAULT";
    static final String ALL = "#ALL";


    private Phase()
    {}


    Phase( int node, SchematronSchema schema ) throws JaxenException
    {
        this.schema = schema;
        ShailXPath xpath = new ShailXPath( "string(@id)" );
        id = ( String )xpath.evaluate( node );
        activePatterns = setActivePatterns( node );

        createVariables( node );
    }

    /**
     * Accesses the patterns active for this phase.
     * @return a List of active patterns
     */
    public List getActivePatterns()
    {
        return activePatterns;
    }


    private void createVariables( int phase ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "*[local-name()='let' and namespace-uri()='"
                + Constants.SCHEMATRON_NAMESPACE + "']" );
        ShailList variables = ( ShailList )xpath.evaluate( phase );

        ShailIterator iter = variables.shailListIterator();

        ShailXPath nameXPath = new ShailXPath( "string(@name)" );
        ShailXPath valueXPath = new ShailXPath( "string(@value)" );

        while( iter.hasNext() )
        {
            int node = iter.nextNode();
            this.variables.add( new GlobalXPathVariable( ( String )nameXPath.evaluate( node ),
                    ( String )valueXPath.evaluate( node ) ) );
        }
    }

    /**
     * Accesses variables in scope for this phase.
     * @return a List of variables
     */
    public List getVariables()
    {
        return variables;
    }


    private List setActivePatterns( int node ) throws JaxenException
    {
        ShailXPath xpath = new ShailXPath( "*[local-name()='active' and namespace-uri()='"
                + com.griffinbrown.schematron.Constants.SCHEMATRON_NAMESPACE + "']"
                + "/@pattern" );
        ShailList activeIds = ( ShailList )xpath.evaluate( node );

        ShailIterator iter = activeIds.shailListIterator();
        List results = new ArrayList( activeIds.size() );
        while( iter.hasNext() )
        {
            String idRef = StringFunction.evaluate( iter.nextNode(), ShailNavigator
                    .getInstance() );
            Pattern pattern = ( schema.getPatternById( idRef ) == null ? schema
                    .getAbstractPatternById( idRef ) : null );
            //TODO: trap null here!
            results.add( pattern );
        }
        return results;
    }

    /**
     * The phase as normalized XML.
     * 
     * @return
     */
    public String asNormalizedXml()
    {
        StringBuffer s = new StringBuffer( "<phase id='" + getId() + ">\n" );

        Iterator iter = activePatternIds.iterator();
        while( iter.hasNext() )
        {
            String active = ( String )iter.next();
            s.append( "<active pattern='" + active + "'/>\n" );
        }

        s.append( "</phase>" );

        return s.toString();
    }


    /**
     * Accesses the ID of this phase.
     * @return the id of the phase
     */
    public String getId()
    {
        return id;
    }


    public String toString()
    {
        return "<" + getClass().getName() + " id=" + getId() + " variables="
                + getVariables().size() + " patterns=" + getActivePatterns() + ">";
    }

}
