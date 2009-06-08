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
 * $ Id Extension.java $
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

package com.griffinbrown.xmltool;





import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * <p>Class to represent an application extension.</p>
 * <p>This abstract class contains methods to mirror all those available to SAX2 handlers and extended handlers.</p>
 * <p>All client extensions must be based on this class.</p>
 */
public abstract class Extension
{

	/**
	 * Create a new Extension, passing it the current instance being parsed.
	 */
	public Extension( Instance instance, Session session )
	{}
	
	/**
	 * Default constructor.
	 */
	public Extension()
	{}

	/* SAX2 ContentHandler methods*/

	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void characters(char[] ch, int start, int length);
		/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void endDocument() ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void endElement(String namespaceURI, String localName, String qName) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void endPrefixMapping(String prefix) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void ignorableWhitespace(char[] ch, int start, int length) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void processingInstruction(String target, String data) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void setDocumentLocator(Locator locator) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void skippedEntity(String name) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void startDocument() ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void startElement(String namespaceURI, String localName, String qName, Attributes atts) ;
	/**
	 * See org.xml.sax.ContentHandler
	 */
	public abstract void startPrefixMapping(String prefix, String uri)  ;

	/**
	 * This method is called by the application parser.
	 * It is used to configure the client extension via an XML configuration file.
	 * @param uri unique identifier for the feature to be set
	 * @param featureValuePairs <tt>Vector</tt> of any custom nodes to be used in configuring this feature
	 */
	public abstract void setFeature( String uri, List featureValuePairs ) throws XMLToolException;


	/* SAX2 ErrorHandler methods */

	/**
	 * See org.xml.sax.ErrorHandler
	 */
	public abstract void error( SAXParseException exception );
	/**
	 * See org.xml.sax.ErrorHandler
	 */
	public abstract void fatalError(SAXParseException exception);
	/**
	 * See org.xml.sax.ErrorHandler
	 */
	public abstract void warning(SAXParseException exception);


	/* SAX2 DTDHandler methods */

	/**
	 * See org.xml.sax.DTDHandler
	 */
	public abstract void notationDecl(String name, String publicId, String systemId);
	/**
	 * See org.xml.sax.DTDHandler
	 */
	public abstract void unparsedEntityDecl(String name, String publicId, String systemId, String notationName);


	/* SAX2 EntityResolver methods */

	/**
	 * See org.xml.sax.EntityResolver
	 */
	public abstract InputSource resolveEntity(String publicId, String systemId);


	/* SAX2 DeclHandler methods */

	/**
	 * See org.xml.sax.ext.DeclHandler
	 */
	public abstract void attributeDecl(String eName, String aName, String type, String valueDefault, String value);
	/**
	 * See org.xml.sax.ext.DeclHandler
	 */
	public abstract void elementDecl(String name, String model) ;
	/**
	 * See org.xml.sax.ext.DeclHandler
	 */
	public abstract void externalEntityDecl(String name, String publicId, String systemId);
	/**
	 * See org.xml.sax.ext.DeclHandler
	 */
	public abstract void internalEntityDecl(String name, String value);


	/* SAX2 LexicalHandler methods */

	/**
	 * See org.xml.sax.ext.LexicalHandler
	 */
	public abstract void comment(char[] ch, int start, int length);
	/**
	 * See org.xml.sax.ext.LexicalHandler
	 */
	public abstract void endCDATA();
	/**
	 * See org.xml.sax.ext.LexicalHandler
	 */
	public abstract void endDTD() ;
	/**
	 * See org.xml.sax.ext.LexicalHandler
	 */
	public abstract void endEntity(java.lang.String name);
	/**
	 * See org.xml.sax.ext.LexicalHandler
	 */
	public abstract void startCDATA() ;
	/**
	 * See {@link org.xml.sax.ext.LexicalHandler}
	 */
	public abstract void startDTD(java.lang.String name, java.lang.String publicId, java.lang.String systemId);
	/**
	 * See org.xml.sax.ext.LexicalHandler
	 */
	public abstract void startEntity(java.lang.String name);

	/* XMLTool-specific methods */

	/**
	 * Post-parse behaviour.
	 */
	public abstract void postParse();

	/**
	 * Pre-parse behaviour.
	 */
	public abstract void preParse() throws XMLToolException;
	
	/**
	 * Returns the Session to which this Extension belongs.
	 */
	public abstract Session getSession();
    
	/**
	 * Notifies this extension of a parse-related message.
	 * @param m the parse-related message
	 */
    public abstract void parseMessage( ParseMessage m ); 

}
