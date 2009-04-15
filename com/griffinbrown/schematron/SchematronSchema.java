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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.probatron.QueryHandler;

import com.griffinbrown.xmltool.Session;
import com.griffinbrown.xmltool.SessionMessage;
import com.griffinbrown.xmltool.XMLToolException;

/**
 * Represents a Schematron schema.
 * @author andrews
 *
 * @version $Id: SchematronSchema.java,v 1.2 2009/02/11 08:52:52 GBDP\andrews Exp $
 */
public class SchematronSchema
{
    private String id;
    private String version;
    private String queryBinding;
    private String title;
    private List namespaceDecls;
    private List variables;
    private List globalVariables;
    private List phases;
    private List patterns;
    private HashMap phaseMap;
    private HashMap patternMap;
    private HashMap abstractRuleMap;
    private HashMap abstractPatternMap;
    private Phase activePhase;
    private Phase defaultPhase;
    private QueryHandler queryHandler;

    private static Logger logger = Logger.getLogger( SchematronSchema.class );


    /**
     * Constructor for general use.
     */
    public SchematronSchema()
    {
        namespaceDecls = new ArrayList();
        variables = new ArrayList();
        globalVariables = new ArrayList();
        phases = new ArrayList();
        patterns = new ArrayList();
        phaseMap = new HashMap();
        patternMap = new HashMap();
        abstractRuleMap = new HashMap();
        abstractPatternMap = new HashMap();
    }


    /**
     * Accesses the id of this schema.
     * @return the id declared by this schema
     */
    public String getId()
    {
        return id;
    }


    /**
     * Configures the default phase for this schema.
     * @param defaultPhase the defaultPhase to set
     */
    public void setDefaultPhase( String id ) throws XMLToolException
    {
        this.defaultPhase = getPhaseById( id );
        if( this.defaultPhase == null )
            throw new XMLToolException( "no such default phase '" + id + "' found" );
    }


    /**
     * Accesses the default phase for this schema.
     * A value of <code>null</code> signifies that no default phase is set.
     * 
     * @return the default phase, if one is configured, otherwise null
     */
    public Phase getDefaultPhase()
    {
        return defaultPhase;
    }


    /**
     * Accesses the version declared by this schema.
     * @return the schemaVersion
     */
    public String getVersion()
    {
        return version;
    }


    /**
     * Accesses the query binding for this schema.
     * @return the queryBinding
     */
    public String getQueryBinding()
    {
        return queryBinding;
    }


    /**
     * Accesses the title declared by this schema.
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * Accesses namespace declarations declared by this schema.
     * @return the namespaceDecls
     */
    public List getNamespaceDecls()
    {
        return namespaceDecls;
    }


    /**
     * Accesses variables belonging to the schema's active phase.
     * If all phases are active (the default), all variables declared in the 
     * schema are returned.
     *  
     * @return a List of active {@link GlobalXPathVariable}s 
     */
    public List getVariables()
    {
        return variables;
    }


    /**
     * Accesses all phases declared by this schema, regardless of whether active 
     * @return a List of all declared {@link Phase}s
     */
    public List getPhases()
    {
        return phases;
    }


    /**
     * Sets the active phase to the phase with the id specified.
     * 
     * Resetting the active phase has the side-effect of resetting which variables are in scope.
     * In this implementation, resetting the active phase to the phase previously set as active 
     * has no effect.
     * If the phase with the id specified cannot be found, all phases become active (the default
     * setting). 
     * 
     * @param id the id of the phase to set as active
     */
    public void setActivePhase( String id )
    {
        if( id == null )
        {
            throw new IllegalArgumentException( "argument cannot be null" );
        }

        if( activePhase == null ) //hasn't been set
        {
            if( id.equals( Phase.ALL ) )
            {
                activePhase = Phase.PHASE_ALL;
            }
            else if( id.equals( Phase.DEFAULT ) )
            {
                if( defaultPhase != null )
                    activePhase = defaultPhase;
                else
                    activePhase = Phase.PHASE_ALL; //fallback
            }
            else
            {
                activePhase = getPhaseById( id );
                if( activePhase == null )
                    activePhase = Phase.PHASE_ALL;
            }
            setInScopeVariables();
        }
        else if( activePhase.getId().equals( id ) )
        {
            //do nothing
            return;
        }
    }


    /**
     * Accesses the active phase for this schema.
     * If no active phase is set, this method returns {@link Phase#PHASE_ALL}.
     * 
     * @return the active phase, if one is configured, otherwise {@link Phase#PHASE_ALL}
     */
    public Phase getActivePhase()
    {
        return activePhase;
    }


    /**
     * Accesses all patterns declared by this schema, regardless of whether part of an active phase.
     * @return all {@link Pattern}s declared by this schema
     */
    public List getPatterns()
    {
        return patterns;
    }


    void addPatterns( List patterns )
    {
        this.patterns.addAll( patterns );

        //hash against id if present
        Iterator iter = patterns.iterator();
        while( iter.hasNext() )
        {
            Pattern pattern = ( Pattern )iter.next();
            patternMap.put( pattern.getId(), pattern );
        }
    }


    void addNamespaceDecls( List decls )
    {
        namespaceDecls.addAll( decls );
    }


    void setVersion( String version )
    {
        this.version = version;
    }


    void addGlobalVariables( List vars )
    {
        this.globalVariables.addAll( vars );
    }


    /*void addVariables( List variables )
    {
        this.variables.addAll( variables );
    }*/

    void addPhases( List phases )
    {
        this.phases.addAll( phases );

        //hash them against their ids
        Iterator iter = phases.iterator();
        while( iter.hasNext() )
        {
            Phase phase = ( Phase )iter.next();
            phaseMap.put( phase.getId(), phase );
        }
    }


    void addAbstractRule( AbstractRule rule )
    {
        this.abstractRuleMap.put( rule.getId(), rule );
    }


    void addAbstractPattern( AbstractPattern pattern )
    {
        this.abstractPatternMap.put( pattern.getId(), pattern );
    }


    Phase getPhaseById( String id )
    {
        return ( Phase )phaseMap.get( id );
    }


    Pattern getPatternById( String id )
    {
        return ( Pattern )patternMap.get( id );
    }


    AbstractRule getAbstractRuleById( String id )
    {
        return ( AbstractRule )abstractRuleMap.get( id );
    }


    AbstractPattern getAbstractPatternById( String id )
    {
        return ( AbstractPattern )abstractPatternMap.get( id );
    }


    private void setInScopeVariables()
    {
        variables = globalVariables;

        if( activePhase == Phase.PHASE_ALL )
        {
            //add vars for ALL phases
            Iterator iter = phases.iterator();
            while( iter.hasNext() )
            {
                Phase phase = ( Phase )iter.next();
                variables.addAll( phase.getVariables() );
            }

            //and for all patterns and rules
            variables.addAll( getPatternVariables() );
        }
        else
        {
            //add just those for the active phase
            // - within phases
            variables.addAll( activePhase.getVariables() );

            // - within patterns and rules
            variables.addAll( getPatternVariables() );
        }
    }


    /**
     * 
     * @return list of variables in scope for any active patterns; could be an empty list 
     */
    private List getPatternVariables()
    {
        List results = new ArrayList();

        Iterator iter = getActivePatterns().iterator();
        while( iter.hasNext() )
        {
            Pattern pattern = ( Pattern )iter.next();
            results.addAll( pattern.getVariables() );
        }

        return results;
    }


    /**
     * 
     * @return list of variables in scope for any active patterns; could be an empty list 
     */
    private List getRuleVariables( Pattern pattern )
    {
        List results = new ArrayList();

        Iterator iter = pattern.getRules().iterator();
        while( iter.hasNext() )
        {
            Rule rule = ( Rule )iter.next();
            results.addAll( rule.getVariables() );
        }

        return results;
    }


    /**
     * Accesses the active patterns for this schema.
     * @return a List of active {@link Pattern}s for this schema
     */
    public List getActivePatterns()
    {
        if( activePhase == Phase.PHASE_ALL )
        {
            List results = new ArrayList();
            Iterator iter = patterns.iterator();
            while( iter.hasNext() )
            {
                Pattern pattern = ( Pattern )iter.next();
                results.add( pattern );
            }
            logger.debug( "active patterns=" + results );
            return results;
        }

        return activePhase.getActivePatterns();
    }

    /**
     * Accesses the active rules for this schema.
     * @return a List of active rules for this schema
     */
    public List getActiveRules()
    {
        List rules = new ArrayList();
        List patterns = getActivePatterns();

        Iterator it = patterns.iterator();
        while( it.hasNext() )
        {
            Pattern pattern = ( Pattern )it.next();
            rules.addAll( pattern.getRules() );
        }
        return rules;
    }


    /**
     * @param queryBinding the queryBinding to set
     */
    void setQueryBinding( String queryBinding )
    {
        this.queryBinding = queryBinding;
    }


    /**
     * @param title the title to set
     */
    void setTitle( String title )
    {
        this.title = title;
    }


    /**
     * Sets the handler to use when evaluating assertions 
     * @param queryHandler the queryHandler to set
     */
    public void setQueryHandler( QueryHandler queryHandler )
    {
        this.queryHandler = queryHandler;
    }


    /**
     * Accesses the handler to use when evaluating assertions 
     * @return the queryHandler
     */
    public QueryHandler getQueryHandler()
    {
        return queryHandler;
    }


    void resolveAbstractRules() throws XMLToolException
    {
        List patterns = getActivePatterns();
        logger.debug( "schema.resolveAbstractRules() patterns=" + patterns );
        Iterator patternsIterator = patterns.iterator();
        while( patternsIterator.hasNext() )
        {
            Pattern pattern = ( Pattern )patternsIterator.next();
            pattern.resolveAbstractRules();
        }
    }


    void resolveAbstractPatterns()
    {
        List patterns = getActivePatterns();
        Iterator patternsIterator = patterns.iterator();
        while( patternsIterator.hasNext() )
        {
            Pattern pattern = ( Pattern )patternsIterator.next();
            try
            {
                if( logger.isDebugEnabled() )
                    logger.debug( "resolving abstract pattern " + pattern );
                pattern.resolveAbstractPatterns();
            }
            catch( XMLToolException e )
            {
                Session session = queryHandler.getSession();
                session.addMessage( new SessionMessage( session.getApplication(),
                        com.griffinbrown.xmltool.Constants.ERROR_TYPE_FATAL,
                        "error resolving abstract pattern: " + e.getMessage() ) );
                session.fatalError();
            }
        }
    }

}
