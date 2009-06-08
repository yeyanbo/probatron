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
 * @version $Id: AttDecl.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
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

import java.util.Iterator;

import org.xml.sax.helpers.LocatorImpl;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.XmlConstruct;

/**
 * Class to represent an attribute type declaration.
 *
 * For further details, see section 3.3 of the XML 1.0
 * (Second Edition).
 *
 * @author $Author: GBDP\andrews $
 * @version @version $Id: AttDecl.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 */

public class AttDecl extends XmlConstruct implements Comparable
{

	private ElemTypeDecl assocElemDecl;
	private String _name;
	private String _type;
	private String _valueDefault;
	private String _value;
	private Instance _inst;
	private LocatorImpl _loc;
	private boolean isBound;
	
	static final String TYPE_IMPLIED = "#IMPLIED";
	static final String TYPE_REQUIRED = "#REQUIRED";
	static final String TYPE_DEFAULTED = null;
	static final String TYPE_FIXED = "#FIXED";

	/**
	 * Constructor for normal use.
	 * @param inst the instance to which this declaration belongs
	 * @param etd the element type declaration for the element to which this attribute declaration belongs 
	 * @param name the name of the attribute
	 * @param type the type of the attribute
	 * @param valueDefault the default value of the attribute 
	 * @param value the value of the attribute
	 * @param loc a SAX locator for the attribute declaration
	 */
	public AttDecl(
		Instance inst,
		ElemTypeDecl etd,
		String name,
		String type,
		String valueDefault,
		String value,
		LocatorImpl loc)
	{

		super(inst);

		isBound = true;
		_inst = inst;
		assocElemDecl = etd;
		_name = new String(name);
		_type = new String(type);
		_loc = loc;
		if (valueDefault != null)
		{
			_valueDefault = new String(valueDefault);
		}
		if (value != null)
		{
			_value = new String(value);
		}

		Iterator atts = etd.attDecls();

		while (atts.hasNext())
		{
			AttDecl att = (AttDecl) atts.next();

			if (_name.equals(att.name()))
			{
				isBound = false;
				if (this._inst.session().issueWarnings())
				{
//					if (etd instanceof DocumentingEtd)
//					{}
//					else
//					{
						inst.addParseMessage(
							"Duplicate declaration of attribute \""
								+ _name
								+ "\" for element type \""
								+ etd.name()
								+ "\".",
							_loc,
							Constants.ERROR_TYPE_WARNING);
						break;
//					}
				}
			}

		}
	}

	public String asNormalizedXml(int emissionType)
	{
		String s = _name + " " + _type;

		if (_valueDefault != null)
		{
			s = s + " " + _valueDefault;
		}
		if (_value != null)
		{
			s = s + " \"" + _value + "\"";
		}

		return s;
	}

	/**
	 * Whether an this <code>AttDecl</code> has been declared previously in the DTD.
	 * @return whether an AttDecl has been declared previously in the DTD
	 * <p>Note that according to the Recommendation, the first declaration is 
	 * binding.</p>
	 */
	public boolean isBound()
	{
		return this.isBound;
	}

	/**
	 * Accesses the name of the attribute declared.
	 * @return the name of the attribute declared
	 */
	public String name()
	{
		return _name;
	}

	/**
	 * Accesses the type of the attribute declared.
	 * @return the type of the attribute declared
	 */
	public String type()
	{
		return _type;
	}

	/**
	 * Accesses the value of the attribute declared. 
	 * @return the allowed value(s) of the attribute declared 
	 */
	public String value()
	{
		return _value;
	}

	/**
	 * Accesses the default value of the attribute declared.
	 * @return the default value of the attribute declared
	 */
	public String valueDefault()
	{
		return _valueDefault;
	}

	/**
	 * Accesses the SAX locator for this declaration.
	 * @return the location of this <code>AttDecl</code>
	 */
	public LocatorImpl location()
	{
		return _loc;
	}
	
	/**
	 * The associated {@link ElemTypeDecl} for this <code>AttDecl</code>.
	 * @return the associated {@link ElemTypeDecl} for this <code>AttDecl</code>
	 */
	public ElemTypeDecl assocElemDecl()
	{
		return assocElemDecl;
	}
	
	/**
	 * Comparison method. At present, the comparison extends only to the 
	 * declaration's <tt>name</tt> attribute.
	 */
	public int compareTo( Object obj )
	{
		AttDecl da = (AttDecl)obj;
		return this._name.compareTo( da.name() );
	}	
}