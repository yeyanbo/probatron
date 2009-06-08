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
 * Created on 23-Aug-2005
 */
package org.probatron;

/**
 * A document root as part of an XPath locator.
 *
 * @version $Id: DocumentRoot.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 */
public class DocumentRoot extends NodeTest
{
    DocumentRoot( int node )
    {
        super( node );
    }
    
    /**
     * @return the empty string
     */
    public String getValue()
    {
        return "";
    }

    /**
     * Does nothing. A document has only one root.
     */
    public void setPredicate( int predicate )
    {}
}
