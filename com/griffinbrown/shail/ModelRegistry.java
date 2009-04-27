/*
 * Created on 5 Jul 2007
 */
package com.griffinbrown.shail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class ModelRegistry
{
    private HashMap models;
    private static Logger logger = Logger.getLogger( ModelRegistry.class );
    private int nextRootIndex = 0;

    private static ModelRegistry REGISTRY = new ModelRegistry();


    private ModelRegistry()
    {
        models = new HashMap();
    }


    public static void register( Model model )
    {

        if( ! REGISTRY.models.containsValue( model ) )
        {
            //set the model's root index
            REGISTRY.nextRootIndex = nextRootIndex();
            model.setRootIndex( REGISTRY.nextRootIndex );
            REGISTRY.models.put( new Integer( model.getRoot() ), model );
        }

        if( logger.isDebugEnabled() )
            logger.debug( "registering model " + model );

        if( logger.isDebugEnabled() )
            logger.debug( "model registered=" + model.getSystemId() + " registry " + REGISTRY
                    + "=" + REGISTRY.models );
    }


    private static int nextRootIndex()
    {
        Collection registered = REGISTRY.models.values();
        Iterator iter = registered.iterator();
        int i = 0;

        while( iter.hasNext() )
        {
            Model m = ( Model )iter.next();
            byte[] events = m.getBuilder().getEvents();
            if( events != null )
                i += events.length;
        }

        return i;
    }


    public static Model getModelForNode( int o )
    {
        //logger.debug( "*****getting model for node " + o + "****** registry=" + REGISTRY
          //      + " models=" + REGISTRY.models );

        if( REGISTRY.models.size() == 1 ) //only one doc has been built by the navigator
        {
            return ( Model )REGISTRY.models.get( new Integer( 0 ) );
        }

        //        logger.debug( "model registry size=" + models.size() );

        Integer node = new Integer( o );

        Object[] keys = REGISTRY.models.keySet().toArray();
        Arrays.sort( keys );

        Object prev = keys[ 0 ];
        for( int i = 0; i < keys.length; i++ )
        {
            Object root = keys[ i ];
            switch( node.compareTo( root ) ){
            case 0: //==
                return ( Model )REGISTRY.models.get( root );
            case 1: //node > root
                //              set prev
                //                logger.debug("node > root:"+node+">"+root);
                prev = root;
                break;
            case - 1: //node < root                
                //              set prev
                Model m = ( Model )REGISTRY.models.get( prev );
                //                logger.debug( "node="+node+"; root="+root+" returning "+m +" (root="+m.getRoot()+")" );
                return m;

            default:
                //                logger.debug("BREAK!");
                break;
            }
        }

        return ( Model )REGISTRY.models.get( prev );
    }


    String debug()
    {
        return REGISTRY.models.toString();
    }



    /**
     * Restores the registry to its newly-constructed state.
     */
    public static void reset()
    {
        REGISTRY.models = new HashMap();
        REGISTRY.nextRootIndex = 0;
    }

}
