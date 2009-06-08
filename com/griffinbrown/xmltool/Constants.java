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
 * Created on 13-May-2003
 * 
 * To change the template for this generated file go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package com.griffinbrown.xmltool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.probatron.jaxen.xpath2.MatchesFunction;

/**
 * Utility class for constant values.
 */
public final class Constants
{
    private Constants()
    {}
    
    //character encodings...

    //...as Java expects them

    /**
     * Flag for UTF-8 encoding.
     */
    public static final String ENC_UTF8 = "UTF-8";

    /**
     * Flag for UTF-16 encoding.
     */
    public static final String ENC_UTF16 = "UTF-16";

    /**
     * Flag for US-ASCII encoding.
     */
    public static final String ENC_US_ASCII = "US-ASCII";

    private static final String[] _SUPPORTED_ENCODINGS = { ENC_US_ASCII, ENC_UTF16, ENC_UTF8 };

    //see Bloch, Effective Java, for the reasons behind this
    /**
     * Convenience list of supported (XML) encodings.
     */
    public static final List SUPPORTED_ENCODINGS = Collections.unmodifiableList( Arrays
            .asList( _SUPPORTED_ENCODINGS ) );

    /*//...as the configuration file has them
     static final String OPT_ENC_UTF8 = "utf8";
     static final String OPT_ENC_UTF16 = "utf16";
     static final String OPT_ENC_US_ASCII = "ascii";*/

    //error output flags
    //...as a short
    
    /**
     * Identifier for XML output format.
     */
    public static final short ERRORS_AS_XML = 0;
    
    /**
     * Identifier for HTML output format.
     */
    public static final short ERRORS_AS_HTML = 1;
    
    /**
     * Identifier for plain text output format.
     */
    public static final short ERRORS_AS_TEXT = 2;

    //error types
    
    /**
     * Identifier for XML errors of type fatal.
     */
    public static final String ERROR_TYPE_FATAL = "FATAL";
    
    /**
     * Identifier for XML errors of type error.
     */
    public static final String ERROR_TYPE_NON_FATAL = "error";
    
    /**
     * Identifier for XML errors of type warning.
     */
    public static final String ERROR_TYPE_WARNING = "warning";
    
    /**
     * Identifier for errors of type info.
     */
    public static final String ERROR_TYPE_INFO = "info";
    
    /**
     * Identifier for errors of type log.
     */
    public static final String ERROR_TYPE_LOG = "log";

    //...as the config file has them
    static final String OPT_XML = "xml";
    static final String OPT_HTML = "html";
    static final String OPT_TEXT = "text";

    //some Java identifiers
    static final String SYS_PROP_SAX_DRIVER = "org.xml.sax.driver";
    
    /**
     * Identifier for the default SAX driver.
     */
    public static final String DEFAULT_SAX_DRIVER = "org.apache.xerces.parsers.SAXParser";
    static final String SAX_XML_READER = "org.xml.sax.XMLReader";

    /**
     * The mode in which output appears: pretty-printed or minimal. The default is pretty-printed.
     * @see XmlConstruct
     */
    static final short DEFAULT_EMISSION_MODE = XmlConstruct.EMIT_PRETTY;
    static final short DEFAULT_ERROR_FORMAT = ERRORS_AS_TEXT;

    /**
     * The class for extensions.
     * @see Extension
     */
    public static final Class ADD_IN_CLASS = Extension.class;

    //SAX features and properties

    /**
     * Identifier for SAX validation feature.
     */
    public static final String SAX_FEATURE_VALIDATION = "http://xml.org/sax/features/validation";

    /**
     * Identifier for SAX validation namespace prefixes feature.
     */
    public static final String SAX_FEATURE_NS_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

    /**
     * Identifier for SAX namespaces feature.
     */
    public static final String SAX_FEATURE_NSS = "http://xml.org/sax/features/namespaces";

    /**
     * Identifier for SAX lexical handler property.
     */
    public static final String SAX_PROPERTY_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

    /**
     * Identifier for SAX declaration handler property.
     */
    public static final String SAX_PROPERTY_DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";

    /**
     * Identifier for Apache dynamic validation feature.
     */
    //Apache
    public static final String XERCES_DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";

    /**
     * Identifier for built-in QA XPath expression feature.
     */
    public static final String QA_XPATH_EXPR = "qa-xpath-expression";

    //common application features
    static final String FEATURE_ENCODING = "encoding";

    static final String FEATURE_ISSUE_WARNINGS = "issue-warnings";

    static final String FEATURE_WELL_FORMED = "parse-as-well-formed";

    static final String FEATURE_ERROR_FORMAT = "error-format";

    static final String FEATURE_SHOW_LOG = "show-activity-log";

    static final String FEATURE_USE_XINCLUDE = "use-xinclude";

    static final String FEATURE_STYLESHEET_LOCATION = "stylesheet-location";

    static final String FEATURE_XSLT_ENGINE = "xslt-engine";

    private static final String[] _CORE_FEATURES = { FEATURE_ENCODING, FEATURE_ISSUE_WARNINGS,
            FEATURE_WELL_FORMED, FEATURE_ERROR_FORMAT, FEATURE_SHOW_LOG, FEATURE_USE_XINCLUDE,
            FEATURE_STYLESHEET_LOCATION, FEATURE_XSLT_ENGINE };

    /**
     * A convenience list of core supported features.
     */
    public static final List CORE_FEATURES = Collections.unmodifiableList( Arrays
            .asList( _CORE_FEATURES ) );

    /**
     * Flag for debug mode feature.
     */
    public static final String DEBUG_MODE = "http://www.griffinbrown.com/features/debug-mode";
    
    /**
     * Flag for timing mode feature.
     */
    public static final String TIMING_MODE = "http://www.griffinbrown.com/features/timing-mode";
    
    /**
     * Flag for XPath expression optimization.
     */
    public static final String OPTIMIZE_XPATH_EXPRESSIONS = "optimize-xpath-expressions";

    /**
     * Flag for RELAX NG schema (XML syntax) location.
     */
    public static final String RELAXNG_XML_SYNTAX_SCHEMA_LOC = "relaxng-xml-syntax-schema-location";
    
    /**
     * Flag for RELAX NG schema (compact syntax) location.
     */
    public static final String RELAXNG_COMPACT_SYNTAX_SCHEMA_LOC = "relaxng-compact-syntax-schema-location";

    /**
     * Namespace URI for XPath 2.0 emulation functions.
     * @see MatchesFunction 
     */
    public static final String PROBATRON_XPATH_FUNCTION_NS = "http://www.probatron.org/xpath-functions";

}
