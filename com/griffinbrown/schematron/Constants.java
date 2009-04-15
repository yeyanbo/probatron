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
 * Created on 17 Dec 2007
 */
package com.griffinbrown.schematron;

import java.io.ByteArrayInputStream;

import org.xml.sax.InputSource;

public class Constants
{
    private Constants()
    {}
    
    public static final String SCHEMATRON_NAMESPACE = "http://purl.oclc.org/dsdl/schematron";
    public static final String SVRL_NAMESPACE = "http://purl.oclc.org/dsdl/svrl";
    
    /**
     * Command-line property to specify the format in which errors should appear.
     */
    public static final String PROP_ERROR_FORMAT = "error-format";
    
    static final String PROP_RELAX_NG_SCHEMA_LOCATION = "relaxng-schema-location";
    
    /**
     * Flag to set the error output format to XML.
     */
    public static final String ERROR_FORMAT_XML = "xml";
    
    /**
     * Flag to set the error output format to plain text.
     */
    public static final String ERROR_FORMAT_TEXT = "text";
    

    private static final byte[] SCHEMATRON_SCHEMA = ( "# (c) International Organization for Standardization 2005.\n"
            + "# Permission to copy in any form is granted for use with conforming\n"
            + "# SGML systems and applications as defined in ISO 8879,\n"
            + "# provided this notice is included in all copies.\n"
            + "default namespace sch = \"http://purl.oclc.org/dsdl/schematron\"\n"
            + "namespace local = \"\"\n"
            + "start = schema\n"
            + "# Element declarations\n"
            + "schema = element schema {\n"
            + "attribute id { xsd:ID }?,\n"
            + "rich,\n"
            + "attribute schemaVersion { non-empty-string }?,\n"
            + "attribute defaultPhase { xsd:IDREF }?,\n"
            + "attribute queryBinding { non-empty-string }?,\n"
            + "(foreign\n"
            + "& inclusion*\n"
            + "& (title?,\n"
            + "ns*,\n"
            + "p*,\n"
            + "let*,\n"
            + "phase*,\n"
            + "pattern+,\n"
            + "p*,\n"
            + "diagnostics?))\n"
            + "}\n"
            + "active = element active {\n"
            + "attribute pattern { xsd:IDREF },\n"
            + "(foreign & (text | dir | emph | span)*)\n"
            + "}\n"
            + "assert = element assert {\n"
            + "attribute test { exprValue },\n"
            + "attribute flag { flagValue }?,\n"
            + "attribute id { xsd:ID }?,\n"
            + "attribute diagnostics { xsd:IDREFS }?,\n"
            + "rich,\n"
            + "linkable,\n"
            + "(foreign & (text | name | value-of | emph | dir | span)*)\n"
            + "}\n"
            + "diagnostic = element diagnostic {\n"
            + "attribute id { xsd:ID },\n"
            + "rich,\n"
            + "(foreign & (text | value-of | emph | dir | span)*)\n"
            + "}\n"
            + "diagnostics = element diagnostics {\n"
            + "foreign & inclusion* & diagnostic*\n"
            + "}\n"
            + "dir = element dir {\n"
            + "attribute value { \"ltr\" | \"rtl\" }?,\n"
            + "(foreign & text)\n"
            + "}\n"
            + "emph = element emph { text }\n"
            + "extends = element extends {\n"
            + "attribute rule { xsd:IDREF },\n"
            + "foreign-empty\n"
            + "}\n"
            + "let = element let {\n"
            + "attribute name { nameValue },\n"
            + "attribute value { string }\n"
            + "}\n"
            + "name = element name {\n"
            + "attribute path { pathValue }?,\n"
            + "foreign-empty\n"
            + "}\n"
            + "ns = element ns {\n"
            + "attribute uri { uriValue },\n"
            + "attribute prefix { nameValue },\n"
            + "foreign-empty\n"
            + "}\n"
            + "p = element p {\n"
            + "attribute id { xsd:ID }?,\n"
            + "attribute class { classValue }?,\n"
            + "attribute icon { uriValue }?,\n"
            + "(foreign & (text | dir | emph | span)*)\n"
            + "}\n"
            + "param = element param {\n"
            + "attribute name { nameValue },\n"
            + "attribute value { non-empty-string }\n"
            + "}\n"
            + "pattern = element pattern {\n"
            + "rich,\n"
            + "(foreign & inclusion* &\n"
            + "( (attribute abstract { \"true\" }, attribute id { xsd:ID },\n"
            + "title?, (p*, let*, rule*))\n"
            + "| (attribute abstract { \"false\" }?, attribute id { xsd:ID }?,\n"
            + "title?, (p*, let*, rule*))\n"
            + "| (attribute abstract { \"false\" }?, attribute is-a { xsd:IDREF },\n"
            + "attribute id { xsd:ID }?, title?, (p*, param*))\n"
            + ")\n"
            + ")\n"
            + "}\n"
            + "phase = element phase {\n"
            + "attribute id { xsd:ID },\n"
            + "rich,\n"
            + "(foreign & inclusion* & (p*, let*, active*))\n"
            + "}\n"
            + "report = element report {\n"
            + "attribute test { exprValue },\n"
            + "attribute flag { flagValue }?,\n"
            + "attribute id { xsd:ID }?,\n"
            + "attribute diagnostics { xsd:IDREFS }?,\n"
            + "rich,\n"
            + "linkable,\n"
            + "(foreign & (text | name | value-of | emph | dir | span)*)\n"
            + "}\n"
            + "rule = element rule {\n"
            + "attribute flag { flagValue }?,\n"
            + "rich,\n"
            + "linkable,\n"
            + "(foreign & inclusion*\n"
            + "& ((attribute abstract { \"true\" },\n"
            + "attribute id { xsd:ID }, let*, (assert | report | extends)+)\n"
            + "| (attribute context { pathValue },\n"
            + "attribute id { xsd:ID }?,\n"
            + "attribute abstract { \"false\" }?,\n"
            + "let*, (assert | report | extends)+)))\n"
            + "}\n"
            + "span = element span {\n"
            + "attribute class { classValue },\n"
            + "(foreign & text)\n"
            + "}\n"
            + "title = element title {\n"
            + "(text | dir)*\n"
            + "}\n"
            + "value-of = element value-of {\n"
            + "attribute select { pathValue },\n"
            + "foreign-empty\n"
            + "}\n"
            + "# common declarations\n"
            + "inclusion = element include {\n"
            + "attribute href { uriValue }\n"
            + "}\n"
            + "rich =\n"
            + "attribute icon { uriValue }?,\n"
            + "attribute see { uriValue }?,\n"
            + "attribute fpi { fpiValue }?,\n"
            + "attribute xml:lang { langValue }?,\n"
            + "attribute xml:space { \"preserve\" | \"default\" }?\n"
            + "linkable =\n"
            + "attribute role { roleValue }?,\n"
            + "attribute subject { pathValue }?\n"
            + "foreign =\n"
            + "foreign-attributes, foreign-element*\n"
            + "foreign-empty =\n"
            + "foreign-attributes\n"
            + "foreign-attributes =\n"
            + "attribute * - (local:* | xml:*) { text }*\n"
            + "foreign-element = element * - sch:* {\n"
            + "(attribute * { text }\n"
            + "| foreign-element\n"
            + "| schema\n"
            + "| text)*\n"
            + "}\n"
            + "# Data types\n"
            + "uriValue = xsd:anyURI\n"
            + "pathValue = string\n"
            + "exprValue = string\n"
            + "fpiValue = string\n"
            + "langValue = xsd:language\n"
            + "roleValue = string\n"
            + "flagValue = string\n"
            + "nameValue = string # In the default query language binding, xsd:NCNAME\n"
            + "classValue = string\n" + "non-empty-string = xsd:token { minLength = \"1\" }" )
            .getBytes();


    /**
     * Convenience method for accessing the RELAXNG schema for Schematron schemas
     * (as defined in ISO/IEC 19757-3 Annex A).
     * 
     * @return the RELAXNG schema for Schematron schemas 
     */
    public static InputSource getSchematronSchema()
    {
        return new InputSource( new ByteArrayInputStream( SCHEMATRON_SCHEMA ) );
    }
}
