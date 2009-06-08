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

/**
 * Part of the com.griffinbrown.xmltool package
 *
 * XML utility classes.
 *
 * Developed by:
 *
 * Griffin Brown Digital Publishing Ltd. (http://www.griffinbrown.com).
 *
 * Please note this software uses the Xerces-J parser which is governed
 * by the The Apache Software License, Version 1.1, which appears below.
 *
 * See http://xml.apache.org for further details of Apache software.
 *
 * Internal revision information:
 * @version $Id: ElemTypeDecl.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 *
 */

/**
 * This application uses Apache's Xerces XML parser which is covered by the
 * Apache software license (below).
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.griffinbrown.xmltool.dtd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.SessionRegistry;
import com.griffinbrown.xmltool.XmlConstruct;


/**
 * Class to represent an element type declaration.
 */
public class ElemTypeDecl extends XmlConstruct implements Comparable
{
	
	private String _name;
	private String _cm;
	private LocatorImpl _loc;
	private Vector _parents = new Vector();
	ArrayList _children = new ArrayList();
	private ArrayList _atts = new ArrayList();
	private HashMap categorizedAttDecls = new HashMap();	
	
	/**
	 * Constructor for normal use.
	 * @param inst the instance the declaration belongs to
	 * @param name the name of the element
	 * @param cm the element content model
	 * @param loc SAX locator for the declaration
	 */
	public ElemTypeDecl( Instance inst, String name, String cm, LocatorImpl loc )
	{
		super( inst );
		
		_name = new String( name );
		_cm = new String( cm );
		_loc = loc;
	
	}
	
	/**
	 * Adds an <code>AttDecl</code> to this declaration.
	 * @param name the name of the attribute to add
	 * @param type the type of the attribute to add
	 * @param valueDefault the default value of the attribute to add
	 * @param value the value of the attribute to add
	 * @return the <code>AttDecl</code> so created
	 */
	public AttDecl addAttDecl( String name, String type, String valueDefault, String value )
	{
		AttDecl ad = makeAttDecl( name, type, valueDefault, value );
		_atts.add( ad );
		return ad;
	}
	
	/**
	 * Factory method for <code>AttDecl</code>s.
	 * @return the <code>AttDecl</code> so created
	 */
	protected AttDecl makeAttDecl( String name, String type, String valueDefault, String value )
	{
		return new AttDecl(
			containingInstance(),
			this,
			name,
			type,
			valueDefault,
			value,
            SessionRegistry.getInstance().getCurrentSession().getContentHandler().cloneLocator());
	}
	
	/**
	 * Accesses the content model specified in this element declaration.
	 * @return the content model as a string
	 */
	public String contentModel()
	{
		return _cm;
	}
	
	/**
	 * Accesses the name of the element specified in this declaration.
	 * @return the name of the element as a string
	 */
	public String name()
	{
		return _name;
	}
	
	/**
	 * Accesses the SAX locator for this element declaration.
	 * @return the SAX locator for this element declaration
	 */
	public Locator location()
	{
		return _loc;
	}
	
	/**
	 * Sets the content model for this element declaration.
	 * @param cm the content model verbatim
	 */
	public void setContentModel( String cm )
	{
		_cm = new String( cm );
	}
	
	/**
	 * Calculates the number of attributes declared for this declaration's element.
	 * @return the number of attributes declared for this declaration's element
	 */
	public int numAttDecls()
	{
		return _atts.size();
	}
	
	/**
	 * Accesses the attribute declarations for this element.
	 * @return iterator over this element's <code>AttDecl</code>s
	 */
	public Iterator attDecls()
	{
		return _atts.iterator();
	}
	
	public String asNormalizedXml( int emissionType )
	{
		String s = "<!ELEMENT " + _name + " " + _cm + ">";
		if( emissionType == EMIT_PRETTY )
		{
			s += "\n";
		}
		return s;
	}
	
	Iterator childTypes()
	{
		containingInstance().dtd().resolveChildren();
		return _children.iterator();
	}
	
	/**
	 * Accesses the elements which can be parent of this declaration's element
	 * @return the elements which can be parents of this element
	 **/
	public Enumeration parentTypes()
	{
		Iterator en = containingInstance().dtd().elemDecls();
		while( en.hasNext() )
		{
			ElemTypeDecl e = ( ElemTypeDecl )en.next();
			Iterator ich = e.childTypes();
			while( ich.hasNext() )
			{
				ElemTypeDecl child = ( ElemTypeDecl )ich.next();
				if( child != null && child.name().equals( this.name() ) && this._parents.indexOf( e ) == -1 )
				{
					_parents.addElement( e );
				}
			}
		}
		
		return _parents.elements();
	}

	/**
	 * Comparison method.
	 * @return the result of comparing the strings of the element names
	 */
	public int compareTo( Object o )
	{
		ElemTypeDecl etd = (ElemTypeDecl)o;
		return this._name.compareTo( etd.name() );
	}

	/**
	 * Sorts the AttDecls for this declaration by type. 
	 */
	public void sortAttDeclsByType()
	{
		
		Iterator enumeration = attDecls();
		while (enumeration.hasNext())
		{
			AttDecl a = (AttDecl) enumeration.next();
			if (categorizedAttDecls.get(a.valueDefault()) == null)
			{
				categorizedAttDecls.put(a.valueDefault(), new ArrayList());
			}
			ArrayList l = (ArrayList) categorizedAttDecls.get(a.valueDefault());
			l.add(a);
		}
		
	}
	
	/**
	 * Sorts the categorized AttDecls for this declaration alphabetically by 
	 * <tt>name</tt>.
	 */
	public void sortAttDecls()
	{
		// TODO: this function needs to be re-written; was adding duplicate decls to the attribute list
		/*
		ArrayList sorted = new ArrayList();
		
		ArrayList list;
		
		list = (ArrayList)categorizedAttDecls.get( AttDecl.TYPE_REQUIRED );
		if( list != null )
		{
			Collections.sort( list );
			sorted.addAll( list );
		}
		
		list = (ArrayList)categorizedAttDecls.get( AttDecl.TYPE_IMPLIED );
		if( list != null )
		{
			Collections.sort( list );
			sorted.addAll( list );
		}
		
		list = (ArrayList)categorizedAttDecls.get( AttDecl.TYPE_DEFAULTED );
		if( list != null )
		{
			Collections.sort( list );
			sorted.addAll( list );
		}
		
		list = (ArrayList)categorizedAttDecls.get( AttDecl.TYPE_FIXED );
		if( list != null )
		{
			Collections.sort( list );
			sorted.addAll( list );		
		}

		this._atts = sorted;	//reset the list of AttDecls
		*/
		
        Collections.sort( this._atts );
        
	}
	
	/**
	 * Accesses the attribute declarations for this element, sorted by type.
	 * @return map of attribute types to attribute declarations
	 */
	public HashMap categorizedAttDecls()
	{
		return this.categorizedAttDecls;
	}
}
