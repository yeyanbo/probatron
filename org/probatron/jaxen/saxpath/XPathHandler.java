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
 * $Header: /TEST/probatron/org/probatron/jaxen/saxpath/XPathHandler.java,v 1.1 2009/02/11 08:52:58 GBDP\andrews Exp $
 * $Revision: 1.1 $
 * $Date: 2009/02/11 08:52:58 $
 *
 * ====================================================================
 *
 * Copyright 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
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
 * @version $Id: XPathHandler.java,v 1.1 2009/02/11 08:52:58 GBDP\andrews Exp $
 */




package org.probatron.jaxen.saxpath;


/** Interface for event-based XPath parsing.
 *
 *  <p>
 *  A {@link org.probatron.jaxen.saxpath.XPathReader} generates callbacks into
 *  an <code>XPathHandler</code> to allow for custom
 *  handling of the parse.
 *  </p>
 *
 *  <p>
 *  The callbacks very closely match the productions
 *  listed in the W3C XPath specification.  Gratuitous
 *  productions (e.g. Expr/startExpr()/endExpr()) are not
 *  included in this API.
 *  </p>
 *
 *  @author bob mcwhirter (bob@werken.com)
 */
public interface XPathHandler
{
    /** Receive notification of the start of an XPath expression parse.
     */
    void startXPath() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of an XPath expression parse.
     */
    void endXPath() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a path expression.
     */
    void startPathExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a path expression.
     */
    void endPathExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of an absolute location path expression.
     */
    void startAbsoluteLocationPath() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of an absolute location path expression.
     */
    void endAbsoluteLocationPath() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a relative location path expression.
     */
    void startRelativeLocationPath() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a relative location path expression.
     */
    void endRelativeLocationPath() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a name step.
     *
     *  @param axis the axis of this step
     *  @param prefix the namespace prefix for the name to test,
     *         or the empty string if no prefix is specified
     *  @param localName the local part of the name to test
     */
    void startNameStep(int axis,
                       String prefix,
                       String localName) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a NameStep
     */
    void endNameStep() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a text() step.
     *
     *  @param axis the axis of this step
     */
    void startTextNodeStep(int axis) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a text() step.
     */
    void endTextNodeStep() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a comment() step.
     *
     *  @param axis the axis of this step
     */
    void startCommentNodeStep(int axis) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a comment() step.
     */
    void endCommentNodeStep() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a node() step.
     *
     *  @param axis the axis of this step
     */
    void startAllNodeStep(int axis) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a node() step.
     */
    void endAllNodeStep() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a processing-instruction(...) step.
     *
     *  @param axis the axis of this step
     *  @param name the name of the processing-instruction, or
     *         the empty string if none is specified
     */
    void startProcessingInstructionNodeStep(int axis,
                                            String name) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a processing-instruction(...) step.
     */
    void endProcessingInstructionNodeStep() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a predicate.
     */
    void startPredicate() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a predicate.
     */
    void endPredicate() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a filter expression.
     */
    void startFilterExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a filter expression.
     */
    void endFilterExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of an 'or' expression.
     */
    void startOrExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of an 'or' expression.
     *
     *  @param create flag that indicates if this expression
     *         should truly be instantiated, or if it was just
     *         a pass-through, based upon the grammar productions
     */
    void endOrExpr(boolean create) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of an 'and' expression.
     */
    void startAndExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of an 'and' expression.
     *
     *  @param create flag that indicates if this expression
     *         should truly be instantiated, or if it was just
     *         a pass-through, based upon the grammar productions
     */
    void endAndExpr(boolean create) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of an equality ('=' or '!=') expression.
     */
    void startEqualityExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of an equality ('=' or '!=') expression.
     *
     *  @param equalityOperator the operator specific to this particular
     *         equality expression.  If null, this expression
     *         is only a pass-through, and should not actually
     *         be instantiated.
     */
    void endEqualityExpr(int equalityOperator) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a relational ('&lt;', '>', '&lt;=', or '>=') expression.
     */
    void startRelationalExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a relational ('&lt;', '>', '&lt;=', or '>=') expression.
     *
     *  @param relationalOperator the operator specific to this particular
     *         relational expression.  If NO_OP, this expression
     *         is only a pass-through, and should not actually
     *         be instantiated.
     */
    void endRelationalExpr(int relationalOperator) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of an additive ('+' or '-') expression.
     */
    void startAdditiveExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of an additive ('+' or '-') expression.
     *
     *  @param additiveOperator the operator specific to this particular
     *         additive expression.   If NO_OP, this expression
     *         is only a pass-through, and should not actually
     *         be instantiated.
     */
    void endAdditiveExpr(int additiveOperator) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a multiplicative ('*', 'div' or 'mod') expression.
     */
    void startMultiplicativeExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a multiplicative ('*', 'div' or 'mod') expression.
     *
     *  @param multiplicativeOperator the operator specific to this particular
     *         multiplicative expression.  If null, this expression
     *         is only a pass-through, and should not actually
     *         be instantiated.
     */
    void endMultiplicativeExpr(int multiplicativeOperator) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a unary ('+' or '-') expression.
     */
    void startUnaryExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a unary ('+' or '-') expression.
     *
     *  @param unaryOperator the operator specific to this particular
     *         unary expression. If NO_OP, this expression is only
     *         a pass-through, and should not actually be instantiated.
     *         If not {@link org.probatron.jaxen.saxpath.Operator#NO_OP}, it will 
     *         always be {@link org.probatron.jaxen.saxpath.Operator#NEGATIVE}.
     */
    void endUnaryExpr(int unaryOperator) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the start of a union ('|') expression.
     */
    void startUnionExpr() throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a union ('|') expression.
     *
     *  @param create flag that indicates if this expression
     *         should truly be instantiated, or if it was just
     *         a pass-through, based upon the grammar productions
     */
    void endUnionExpr(boolean create) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of a number expression.
     *
     *  @param number the number value
     */
    void number(int number) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of a number expression.
     *
     *  @param number the number value
     */
    void number(double number) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of a literal expression.
     *
     *  @param literal the string literal value
     */
    void literal(String literal) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of a variable-reference expression.
     *
     *  @param prefix the namespace prefix of the variable
     *  @param variableName the local name of the variable
     */
    void variableReference(String prefix,
                           String variableName) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of a function call.
     *
     *  @param prefix the namespace prefix of the function
     *  @param functionName the local name of the function
     */
    void startFunction(String prefix,
                       String functionName) throws org.probatron.jaxen.saxpath.SAXPathException;

    /** Receive notification of the end of a function call
     */
    void endFunction() throws org.probatron.jaxen.saxpath.SAXPathException;
}