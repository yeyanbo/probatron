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
 * An element name as part of an XPath locator.
 * 
 * @version $Revision: 1.1 $
 * 
 * @version $Id: ElementNameTest.java,v 1.1 2009/02/11 08:52:55 GBDP\andrews Exp $
 * 
 */
public class ElementNameTest extends NodeTest
{
    private String qName = null;
    private String namespaceURI = null;
    private String namespacePrefix = null; //declared by the user in the SILCN 1.0 instance


    /**
     * Constructor.
     * @param nCName namespace prefix, or <code>null</code> if not present
     * @param qName qualified element type name
     */
    ElementNameTest( int node, QueryEvaluator evaluator )
    {
        super( node );
        //        N.B. "For nodes of any type other than ELEMENT_NODE and ATTRIBUTE_NODE and nodes 
        //        created with a DOM Level 1 method, such as createElement from the Document 
        //        interface, this is always null."

        ShailNavigator nav = ShailNavigator.getInstance();

        this.qName = nav.getElementName( node ) != null ? nav.getElementName( node ) : nav
                .getElementQName( node );
        this.namespaceURI = nav.getNamespaceURI( node );

        if( namespaceURI != null && evaluator != null )
            this.namespacePrefix = evaluator.getPrefix( namespaceURI );

    }


    /**
     * Accesses the qualified name for this element
     * @return qualified name for this test
     */
    public String getQName()
    {
        return this.qName;
    }


    /**
     * Accesses the NCName for this element. 
     * @return the <code>NCName</code> for this test
     */
    public String getNCName()
    {
        //		return this.nCName;
        //this will return the namespace prefix *as declared in the SILCN instance*
        //		return ((QAHandler)query.getOwner()).getPrefixForUri( this.namespaceURI );
        return this.namespacePrefix;
    }

    /**
     * Accesses the namespace URI for this element.
     * @return
     */
    public String getNamespaceURI()
    {
        return this.namespaceURI;
    }


    /**
     * @see NodeTest#getValue()
     */
    public String getValue()
    {
        if( namespacePrefix != null )
        {
            return getNCName() + ":" + qName + getPredicate();
        }

        if( namespacePrefix == null ) //hasn't been registered by the user
        {
            if( namespaceURI == null ) //no prefix or uri: just return unprefixed name
                return qName + getPredicate();

            return "*[local-name()='" + qName + "' and namespace-uri()='" + namespaceURI + "']"
                    + getPredicate();
        }
        else
            return namespacePrefix + ":" + qName + getPredicate();
    }

}