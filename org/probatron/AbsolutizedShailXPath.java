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
 * Created on 27 Nov 2008
 */
package org.probatron;

import org.probatron.jaxen.AbsolutizingJaxenHandler;
import org.probatron.jaxen.JaxenException;
import org.probatron.jaxen.Navigator;
import org.probatron.jaxen.saxpath.SAXPathException;
import org.probatron.jaxen.saxpath.XPathReader;
import org.probatron.jaxen.saxpath.helpers.XPathReaderFactory;

import com.griffinbrown.shail.expr.ShailXPathFactory;

public class AbsolutizedShailXPath extends ShailXPath
{

    public AbsolutizedShailXPath( String xpathExpr, Navigator navigator ) throws JaxenException
    {
        super( xpathExpr, navigator );
    }

    public AbsolutizedShailXPath( String xpathExpr ) throws JaxenException
    {
        super( xpathExpr );
        
    }

    /**
     * @see org.probatron.ShailXPath#parseXPath(java.lang.String)
     */
    void parseXPath( String xpathExpr ) throws JaxenException
    {
        try
        {
            XPathReader reader = XPathReaderFactory.createReader();
            AbsolutizingJaxenHandler handler = new AbsolutizingJaxenHandler();
            handler.setXPathFactory( new ShailXPathFactory() );
            reader.setXPathHandler( handler );
            reader.parse( xpathExpr );
            setXPath( handler.getXPathExpr() );
        }
        catch( org.probatron.jaxen.saxpath.XPathSyntaxException e )
        {
            throw new org.probatron.jaxen.XPathSyntaxException( e );
        }
        catch( SAXPathException e )
        {
            throw new JaxenException( e );
        }
    }
}
