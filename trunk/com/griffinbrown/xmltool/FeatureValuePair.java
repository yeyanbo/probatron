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

package com.griffinbrown.xmltool;



/**
 * Represents a feature-value pair for configuration purposes.
 */
public class FeatureValuePair
{
    private String name = null;
    private String value = null;

    /**
     * Constructor for normal use.
     * @param name the name of the feature
     * @param value the value associated with the feature
     */
    public FeatureValuePair( String name, String value )
    {
        this.name = name;
        this.value = value;
    }


    /**
     * Accesses the name of the feature.
     * @return the name of the feature
     */
    public String getName()
    {
        return name;
    }


    /**
     * Accesses the value of the feature.
     * @return the value of the feature
     */
    public String getValue()
    {
        return value;
    }

}
