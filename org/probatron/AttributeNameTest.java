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

package org.probatron;


/**
 * @author andrews
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AttributeNameTest extends NodeTest
{
    private String qName = null;
    private String namespacePrefix = null;
    private String namespaceURI = null;


    /**
     * Constructor.
     * @param namespacePrefix namespace prefix, or <code>null</code> if not present
     * @param qName qualified element type name
     */
    AttributeNameTest( int node, QueryEvaluator evaluator )
    {
        super( node );
        ShailNavigator nav = ShailNavigator.getInstance();
        this.qName = nav.getAttributeQName( node );
        //		this.nCName = evaluator.getPrefix( nav.getNamespaceURI( node ) );
    }


    /**
     * @return qualified name for this test.
     */
    public String getQName()
    {
        return this.qName;
    }


    /**
     * @return the <code>NCName</code> for this test.
     */
    public String getNCName()
    {
        return this.namespacePrefix;
    }


    /**
     * N.B. Attribute NameTests don't have a positional predicate.
     * 
     * @see NodeTest#getValue()
     * 
     */
    public String getValue()
    {
        if( this.namespacePrefix != null )
        {
            return "@" + this.namespacePrefix + ":" + this.qName;
        }
        else
        {
            if( namespaceURI == null ) //no prefix or uri: just return unprefixed name
                return "@" + qName;

            return "@*[local-name()='" + qName + "' and namespace-uri()='" + namespaceURI
                    + "']" + getPredicate();
        }
    }


    public void setPredicate( int predicate )
    {}

}
