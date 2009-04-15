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
 * This software contains, in modified form, source code originally created by 
 * the Jaxen Project.
 *
 * The copyright notice, conditions and disclaimer pertaining to that 
 * distribution are included below.
 *
 * Jaxen distributions are available from <http://jaxen.org/>.
 */  

/*
 * $Header: /TEST/probatron/org/probatron/jaxen/XPathFunctionContext.java,v 1.1 2009/02/11 08:52:56 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/02/11 08:52:56 $
 *
 * ====================================================================
 *
 * Copyright 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   * Neither the name of the Jaxen Project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * @version $Id: XPathFunctionContext.java,v 1.1 2009/02/11 08:52:56 GBDP\andrews Exp $
 */


package org.probatron.jaxen;

import org.probatron.jaxen.function.BooleanFunction;
import org.probatron.jaxen.function.CeilingFunction;
import org.probatron.jaxen.function.ConcatFunction;
import org.probatron.jaxen.function.ContainsFunction;
import org.probatron.jaxen.function.CountFunction;
import org.probatron.jaxen.function.FalseFunction;
import org.probatron.jaxen.function.FloorFunction;
import org.probatron.jaxen.function.IdFunction;
import org.probatron.jaxen.function.LangFunction;
import org.probatron.jaxen.function.LastFunction;
import org.probatron.jaxen.function.LocalNameFunction;
import org.probatron.jaxen.function.NameFunction;
import org.probatron.jaxen.function.NamespaceUriFunction;
import org.probatron.jaxen.function.NormalizeSpaceFunction;
import org.probatron.jaxen.function.NotFunction;
import org.probatron.jaxen.function.NumberFunction;
import org.probatron.jaxen.function.PositionFunction;
import org.probatron.jaxen.function.RoundFunction;
import org.probatron.jaxen.function.StartsWithFunction;
import org.probatron.jaxen.function.StringFunction;
import org.probatron.jaxen.function.StringLengthFunction;
import org.probatron.jaxen.function.SubstringAfterFunction;
import org.probatron.jaxen.function.SubstringBeforeFunction;
import org.probatron.jaxen.function.SubstringFunction;
import org.probatron.jaxen.function.SumFunction;
import org.probatron.jaxen.function.TranslateFunction;
import org.probatron.jaxen.function.TrueFunction;
import org.probatron.jaxen.function.ext.EndsWithFunction;
import org.probatron.jaxen.function.ext.EvaluateFunction;
import org.probatron.jaxen.function.ext.LowerFunction;
import org.probatron.jaxen.function.ext.UpperFunction;
import org.probatron.jaxen.function.xslt.DocumentFunction;


/** A <code>FunctionContext</code> implementing the core XPath
 *  function library, plus Jaxen extensions.
 *
 *  <p>
 *  The core XPath function library is provided through this
 *  implementation of <code>FunctionContext</code>.  Additionally,
 *  extension functions have been provided, as enumerated below.
 *  </p>
 *
 *  <p>
 *  This class is re-entrant and thread-safe.  If using the
 *  default instance, it is inadvisable to call 
 *  {@link #registerFunction(String, String, Function)}
 *  as that will extend the global function context, affecting other
 *  users. 
 *  </p>
 *
 *  <p>
 *  Extension functions:
 *  </p>
 *
 *  <ul>
 *     <li>evaluate(..)</li>
 *     <li>upper-case(..)</li>
 *     <li>lower-case(..)</li>
 *     <li>ends-with(..)</li>
 *  </ul>
 *
 *  @see FunctionContext
 *  @see org.probatron.jaxen.function
 *  @see org.probatron.jaxen.function.xslt
 *  @see org.probatron.jaxen.function.ext
 *
 *  @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 */
public class XPathFunctionContext extends SimpleFunctionContext
{
    private static XPathFunctionContext instance = new XPathFunctionContext();

    /** Retrieve the default function context
     *
     *  @return the default function context
     */
    public static FunctionContext getInstance()
    {
        return instance;
    }

    /** Create a new XPath function context.
     *  All core XPath and Jaxen extension functions are registered.
     */
    public XPathFunctionContext()
    {
        this(true);
    }

    /** Create a new XPath function context.
     *  All core XPath functions are registered.
     *  
     * @param includeExtensionFunctions if true extension functions are included;
     *     if false, they aren't
     */
    public XPathFunctionContext(boolean includeExtensionFunctions)
    {
        registerXPathFunctions();
        if (includeExtensionFunctions) {
            registerXSLTFunctions();
            registerExtensionFunctions();
        }
    }

    private void registerXPathFunctions() {

        registerFunction( null,  // namespace URI
                          "boolean",
                          new BooleanFunction() );

        registerFunction( null,  // namespace URI
                          "ceiling",
                          new CeilingFunction() );

        registerFunction( null,  // namespace URI
                          "concat",
                          new ConcatFunction() );

        registerFunction( null,  // namespace URI
                          "contains",
                          new ContainsFunction() );
        
        registerFunction( null,  // namespace URI
                          "count",
                          new CountFunction() );

        registerFunction( null,  // namespace URI
                          "false",
                          new FalseFunction() );

        registerFunction( null,  // namespace URI
                          "floor",
                          new FloorFunction() );

        registerFunction( null,  // namespace URI
                          "id",
                          new IdFunction() );

        registerFunction( null,  // namespace URI
                          "lang",
                          new LangFunction() );

        registerFunction( null,  // namespace URI
                          "last",
                          new LastFunction() );

        registerFunction( null,  // namespace URI
                          "local-name",
                          new LocalNameFunction() );

        registerFunction( null,  // namespace URI
                          "name",
                          new NameFunction() );

        registerFunction( null,  // namespace URI
                          "namespace-uri",
                          new NamespaceUriFunction() );

        registerFunction( null,  // namespace URI
                          "normalize-space",
                          new NormalizeSpaceFunction() );

        registerFunction( null,  // namespace URI
                          "not",
                          new NotFunction() );

        registerFunction( null,  // namespace URI
                          "number",
                          new NumberFunction() );

        registerFunction( null,  // namespace URI
                          "position",
                          new PositionFunction() );

        registerFunction( null,  // namespace URI
                          "round",
                          new RoundFunction() );

        registerFunction( null,  // namespace URI
                          "starts-with",
                          new StartsWithFunction() );

        registerFunction( null,  // namespace URI
                          "string",
                          new StringFunction() );

        registerFunction( null,  // namespace URI
                          "string-length",
                          new StringLengthFunction() );

        registerFunction( null,  // namespace URI
                          "substring-after",
                          new SubstringAfterFunction() );

        registerFunction( null,  // namespace URI
                          "substring-before",
                          new SubstringBeforeFunction() );

        registerFunction( null,  // namespace URI
                          "substring",
                          new SubstringFunction() );

        registerFunction( null,  // namespace URI
                          "sum",
                          new SumFunction() );

        registerFunction( null,  // namespace URI
                          "true",
                          new TrueFunction() );
        
        registerFunction( null,  // namespace URI
                          "translate",
                          new TranslateFunction() );
    }

    private void registerXSLTFunctions() {

        // extension functions defined in XSLT
        registerFunction( null,  // namespace URI
                          "document",
                          new DocumentFunction() );
    }

    private void registerExtensionFunctions() {
        // extension functions should go into a namespace, but which one?
        // for now, keep them in default namespace to not break any code

        registerFunction( null,  // namespace URI
                          "evaluate",
                          new EvaluateFunction() );
        
        registerFunction( null,  // namespace URI
                          "lower-case",
                          new LowerFunction() );
        
        registerFunction( null,  // namespace URI
                          "upper-case",
                          new UpperFunction() );
        
        registerFunction( null,  // namespace URI
                          "ends-with",
                          new EndsWithFunction() );
    }

    
}
