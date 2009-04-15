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
 * $Header:
 * /home/projects/jaxen/scm/jaxen/src/java/main/org/jaxen/saxpath/base/XPathLexer.java,v 1.17
 * 2006/02/05 21:47:42 elharo Exp $ $Revision: 1.2 $ $Date: 2009/02/11 08:52:54 $
 * 
 * ====================================================================
 * 
 * Copyright 2000-2002 bob mcwhirter & James Strachan. All rights reserved.
 * 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: * Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer. *
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Jaxen Project nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ==================================================================== This software consists
 * of voluntary contributions made by many individuals on behalf of the Jaxen Project and was
 * originally created by bob mcwhirter <bob@werken.com> and James Strachan
 * <jstrachan@apache.org>. For more information on the Jaxen Project, please see
 * <http://www.jaxen.org/>.
 * 
 * @version $Id: XPathLexer.java,v 1.2 2009/02/11 08:52:54 GBDP\andrews Exp $
 */

/*
 * +++++++++++++++++N.B. amended by Andrew Sales, 20071019:
 * 
 * - added method multiplyOrAnyName() (called by star())
 * - replaced TokenTypes.STAR with type for MULTIPLY and ANY_NAME
 *  
 */

package org.probatron.jaxen.saxpath.base;

class XPathLexer
{
    private String xpath;
    private int currentPosition;
    private int endPosition;

    private Token previousToken;


    XPathLexer( String xpath )
    {
        setXPath( xpath );
    }


    private void setXPath( String xpath )
    {
        this.xpath = xpath;
        this.currentPosition = 0;
        this.endPosition = xpath.length();
    }


    String getXPath()
    {
        return this.xpath;
    }


    Token nextToken()
    {
        Token token = null;

        do
        {
            token = null;

            switch( LA( 1 ) ){
            case '$': {
                token = dollar();
                break;
            }

            case '"':
            case '\'': {
                token = literal();
                break;
            }

            case '/': {
                token = slashes();
                break;
            }

            case ',': {
                token = comma();
                break;
            }

            case '(': {
                token = leftParen();
                break;
            }

            case ')': {
                token = rightParen();
                break;
            }

            case '[': {
                token = leftBracket();
                break;
            }

            case ']': {
                token = rightBracket();
                break;
            }

            case '+': {
                token = plus();
                break;
            }

            case '-': {
                token = minus();
                break;
            }

            case '<':
            case '>': {
                token = relationalOperator();
                break;
            }

            case '=': {
                token = equals();
                break;
            }

            case '!': {
                if( LA( 2 ) == '=' )
                {
                    token = notEquals();
                }
                break;
            }

            case '|': {
                token = pipe();
                break;
            }

            case '@': {
                token = at();
                break;
            }

            case ':': {
                if( LA( 2 ) == ':' )
                {
                    token = doubleColon();
                }
                else
                {
                    token = colon();
                }
                break;
            }

            case '*': {
                token = star();
                break;
            }

            case '.': {
                switch( LA( 2 ) ){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    token = number();
                    break;
                }
                default: {
                    token = dots();
                    break;
                }
                }
                break;
            }

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                token = number();
                break;
            }

            case ' ':
            case '\t':
            case '\n':
            case '\r': {
                token = whitespace();
                break;
            }

            default: {
                if( isIdentifierStartChar( LA( 1 ) ) )
                {
//                    System.err.println("isIdentifierStartChar("+LA(1)+") = true");
                    token = identifierOrOperatorName();
//                    System.err.println("TOKEN="+token);
                }
            }
            }

            if( token == null )
            {
                if( ! hasMoreChars() )
                {
                    token = new Token( TokenTypes.EOF, getXPath(), currentPosition(),
                            endPosition() );
                }
                else
                {
//                    System.err.println( "nextToken() ERROR! at pos "+currentPosition );
                    token = new Token( TokenTypes.ERROR, getXPath(), currentPosition(),
                            endPosition() );
                }
            }

        }
        while( token.getTokenType() == TokenTypes.SKIP );

        setPreviousToken( token );

        return token;
    }


    private Token identifierOrOperatorName()
    {
//    System.err.println("identifierOrOperatorName() previous token="+previousToken);
        Token token = null;

        if( previousToken != null )
        {
            // For some reason, section 3.7, Lexical structure,
            // doesn't seem to feel like it needs to mention the
            // SLASH, DOUBLE_SLASH, and COLON tokens for the test
            // if an NCName is an operator or not.
            //
            // According to section 3.7, "/foo" should be considered
            // as a SLASH following by an OperatorName being 'foo'.
            // Which is just simply, clearly, wrong, in my mind.
            //
            //     -bob

            switch( previousToken.getTokenType() ){
            case TokenTypes.AT:
            case TokenTypes.DOUBLE_COLON:
            case TokenTypes.LEFT_PAREN:
            case TokenTypes.LEFT_BRACKET:
            case TokenTypes.AND:
            case TokenTypes.OR:
            case TokenTypes.MOD:
            case TokenTypes.DIV:
            case TokenTypes.COLON:
            case TokenTypes.SLASH:
            case TokenTypes.DOUBLE_SLASH:
            case TokenTypes.PIPE:
            case TokenTypes.DOLLAR:
            case TokenTypes.PLUS:
            case TokenTypes.MINUS:
//            case TokenTypes.STAR:
            case TokenTypes.MULTIPLY:
            case TokenTypes.COMMA:
            case TokenTypes.LESS_THAN_SIGN:
            case TokenTypes.GREATER_THAN_SIGN:
            case TokenTypes.LESS_THAN_OR_EQUALS_SIGN:
            case TokenTypes.GREATER_THAN_OR_EQUALS_SIGN:
            case TokenTypes.EQUALS:
            case TokenTypes.NOT_EQUALS: {
                token = identifier();
//                System.err.println( "identifierOrOperatorName(): token=" + token + " previous token=" + previousToken );
                break;
            }
            default: {
                token = operatorName();
//                System.err.println("identifierOrOperatorName(): operatorName()="+token+" previous token="+previousToken);
                break;
            }
            }
        }
        else
        {
            token = identifier();
        }

        return token;
    }


    private Token identifier()
    {
        Token token = null;

        int start = currentPosition();

        while( hasMoreChars() )
        {
            if( isIdentifierChar( LA( 1 ) ) )
            {
                consume();
            }
            else
            {
                break;
            }
        }

        token = new Token( TokenTypes.IDENTIFIER, getXPath(), start, currentPosition() );

        return token;
    }


    private Token operatorName()
    {
        Token token = null;

        switch( LA( 1 ) ){
        case 'a': {
            token = and();
            break;
        }

        case 'o': {
            token = or();
            break;
        }

        case 'm': {
            token = mod();
            break;
        }

        case 'd': {
            token = div();
            break;
        }
        }

        return token;
    }


    private Token mod()
    {
        Token token = null;

        if( ( LA( 1 ) == 'm' ) && ( LA( 2 ) == 'o' ) && ( LA( 3 ) == 'd' ) )
        {
            token = new Token( TokenTypes.MOD, getXPath(), currentPosition(),
                    currentPosition() + 3 );

            consume();
            consume();
            consume();
        }

        return token;
    }


    private Token div()
    {
        Token token = null;

        if( ( LA( 1 ) == 'd' ) && ( LA( 2 ) == 'i' ) && ( LA( 3 ) == 'v' ) )
        {
            token = new Token( TokenTypes.DIV, getXPath(), currentPosition(),
                    currentPosition() + 3 );

            consume();
            consume();
            consume();
        }

        return token;
    }


    private Token and()
    {
        Token token = null;

        if( ( LA( 1 ) == 'a' ) && ( LA( 2 ) == 'n' ) && ( LA( 3 ) == 'd' ) )
        {
            token = new Token( TokenTypes.AND, getXPath(), currentPosition(),
                    currentPosition() + 3 );

            consume();
            consume();
            consume();
        }

        return token;
    }


    private Token or()
    {
        Token token = null;

        if( ( LA( 1 ) == 'o' ) && ( LA( 2 ) == 'r' ) )
        {
            token = new Token( TokenTypes.OR, getXPath(), currentPosition(),
                    currentPosition() + 2 );

            consume();
            consume();
        }

        return token;
    }


    private Token number()
    {
        int start = currentPosition();
        boolean periodAllowed = true;

        loop: while( true )
        {
            switch( LA( 1 ) ){
            case '.':
                if( periodAllowed )
                {
                    periodAllowed = false;
                    consume();
                }
                else
                {
                    break loop;
                }
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                consume();
                break;
            default:
                break loop;
            }
        }

        return new Token( TokenTypes.DOUBLE, getXPath(), start, currentPosition() );
    }


    private Token whitespace()
    {
        consume();

        loop: while( hasMoreChars() )
        {
            switch( LA( 1 ) ){
            case ' ':
            case '\t':
            case '\n':
            case '\r': {
                consume();
                break;
            }

            default: {
                break loop;
            }
            }
        }

        return new Token( TokenTypes.SKIP, getXPath(), 0, 0 );
    }


    private Token comma()
    {
        Token token = new Token( TokenTypes.COMMA, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token equals()
    {
        Token token = new Token( TokenTypes.EQUALS, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token minus()
    {
        Token token = new Token( TokenTypes.MINUS, getXPath(), currentPosition(),
                currentPosition() + 1 );
        consume();

        return token;
    }


    private Token plus()
    {
        Token token = new Token( TokenTypes.PLUS, getXPath(), currentPosition(),
                currentPosition() + 1 );
        consume();

        return token;
    }


    private Token dollar()
    {
        Token token = new Token( TokenTypes.DOLLAR, getXPath(), currentPosition(),
                currentPosition() + 1 );
        consume();

        return token;
    }


    private Token pipe()
    {
        Token token = new Token( TokenTypes.PIPE, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token at()
    {
        Token token = new Token( TokenTypes.AT, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token colon()
    {
        Token token = new Token( TokenTypes.COLON, getXPath(), currentPosition(),
                currentPosition() + 1 );
        consume();

        return token;
    }


    private Token doubleColon()
    {
        Token token = new Token( TokenTypes.DOUBLE_COLON, getXPath(), currentPosition(),
                currentPosition() + 2 );

        consume();
        consume();

        return token;
    }


    private Token notEquals()
    {
        Token token = new Token( TokenTypes.NOT_EQUALS, getXPath(), currentPosition(),
                currentPosition() + 2 );

        consume();
        consume();

        return token;
    }


    private Token relationalOperator()
    {
        Token token = null;

        switch( LA( 1 ) ){
        case '<': {
            if( LA( 2 ) == '=' )
            {
                token = new Token( TokenTypes.LESS_THAN_OR_EQUALS_SIGN, getXPath(),
                        currentPosition(), currentPosition() + 2 );
                consume();
            }
            else
            {
                token = new Token( TokenTypes.LESS_THAN_SIGN, getXPath(), currentPosition(),
                        currentPosition() + 1 );
            }

            consume();
            break;
        }
        case '>': {
            if( LA( 2 ) == '=' )
            {
                token = new Token( TokenTypes.GREATER_THAN_OR_EQUALS_SIGN, getXPath(),
                        currentPosition(), currentPosition() + 2 );
                consume();
            }
            else
            {
                token = new Token( TokenTypes.GREATER_THAN_SIGN, getXPath(), currentPosition(),
                        currentPosition() + 1 );
            }

            consume();
            break;
        }
        }

        return token;

    }


    private Token star()
    {
//        Token token = new Token( TokenTypes.STAR, getXPath(), currentPosition(),
//                currentPosition() + 1 );
                
        Token token = multiplyOrAnyName();   //EXPERIMENTAL!!
//        System.err.println( "star(): " + token );
        
        consume();

        return token;
    }

    private Token multiplyOrAnyName()
    {
        /*"If there is a preceding token and the preceding token is not one of 
         * @, ::, (, [, , or an Operator, then a * must be recognized as a 
         * MultiplyOperator and an NCName must be recognized as an OperatorName."
         * (XPath s3.7)
         */
        
        if( previousToken != null )
        {
            int type = previousToken.getTokenType();
            if( //@, ::, (, [, ,
                type != TokenTypes.AT && 
                type != TokenTypes.DOUBLE_COLON &&
                type != TokenTypes.LEFT_PAREN &&
                type != TokenTypes.LEFT_BRACKET &&
                type != TokenTypes.COMMA &&
                //Operators
                /*
                 * [32]     Operator       ::=      OperatorName    
            | MultiplyOperator  
            | '/' | '//' | '|' | '+' | '-' | '=' | '!=' | '<' | '<=' | '>' | '>='   
                 */
                //OperatorName
                /*
                 * [33]     OperatorName       ::=      'and' | 'or' | 'mod' | 'div'
                 */
                type != TokenTypes.AND && 
                type != TokenTypes.OR && 
                type != TokenTypes.MOD && 
                type != TokenTypes.DIV &&
                //MultiplyOperator
                type != TokenTypes.MULTIPLY &&
                //'/' | '//' | '|' | '+' | '-' | '=' | '!=' | '<' | '<=' | '>' | '>='
                type != TokenTypes.SLASH &&    //'/'
                type != TokenTypes.DOUBLE_SLASH && //'//'
                type != TokenTypes.PIPE && //'|'
                type != TokenTypes.PLUS && //'+'
                type != TokenTypes.MINUS && //'-'
                type != TokenTypes.EQUALS && //'='
                type != TokenTypes.NOT_EQUALS && //'!='
                type != TokenTypes.LESS_THAN_SIGN && //'<'
                type != TokenTypes.LESS_THAN_OR_EQUALS_SIGN && //'<='
                type != TokenTypes.GREATER_THAN_SIGN && //'>'
                type != TokenTypes.GREATER_THAN_OR_EQUALS_SIGN //'>='
                && type != TokenTypes.COLON     //':' (omitted from spec!)
                )
            {
                return new Token( TokenTypes.MULTIPLY, getXPath(), currentPosition(),
                        currentPosition() + 1 );
            }
        }
        return new Token( TokenTypes.ANY_NAME, getXPath(), currentPosition(),
                    currentPosition() + 1 );
    }

    private Token literal()
    {
        Token token = null;

        char match = LA( 1 );

        consume();

        int start = currentPosition();

        while( ( token == null ) && hasMoreChars() )
        {
            if( LA( 1 ) == match )
            {
                token = new Token( TokenTypes.LITERAL, getXPath(), start, currentPosition() );
            }
            consume();
        }

        return token;
    }


    private Token dots()
    {
        Token token = null;

        switch( LA( 2 ) ){
        case '.': {
            token = new Token( TokenTypes.DOT_DOT, getXPath(), currentPosition(),
                    currentPosition() + 2 );
            consume();
            consume();
            break;
        }
        default: {
            token = new Token( TokenTypes.DOT, getXPath(), currentPosition(),
                    currentPosition() + 1 );
            consume();
            break;
        }
        }

        return token;
    }


    private Token leftBracket()
    {
        Token token = new Token( TokenTypes.LEFT_BRACKET, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token rightBracket()
    {
        Token token = new Token( TokenTypes.RIGHT_BRACKET, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token leftParen()
    {
        Token token = new Token( TokenTypes.LEFT_PAREN, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token rightParen()
    {
        Token token = new Token( TokenTypes.RIGHT_PAREN, getXPath(), currentPosition(),
                currentPosition() + 1 );

        consume();

        return token;
    }


    private Token slashes()
    {
        Token token = null;

        switch( LA( 2 ) ){
        case '/': {
            token = new Token( TokenTypes.DOUBLE_SLASH, getXPath(), currentPosition(),
                    currentPosition() + 2 );
            consume();
            consume();
            break;
        }
        default: {
            token = new Token( TokenTypes.SLASH, getXPath(), currentPosition(),
                    currentPosition() + 1 );
            consume();
        }
        }

        return token;
    }


    private char LA( int i )
    {
        if( currentPosition + ( i - 1 ) >= endPosition() )
        {
            return ( char ) - 1;
        }

        return getXPath().charAt( currentPosition() + ( i - 1 ) );
    }


    private void consume()
    {
        ++this.currentPosition;
    }


    private int currentPosition()
    {
        return this.currentPosition;
    }


    private int endPosition()
    {
        return this.endPosition;
    }


    private void setPreviousToken( Token previousToken )
    {
        this.previousToken = previousToken;
//        System.err.println("previous token set to "+this.previousToken);
    }


    private boolean hasMoreChars()
    {
        return currentPosition() < endPosition();
    }


    private boolean isIdentifierChar( char c )
    {
        return Verifier.isXMLNCNameCharacter( c );
    }


    private boolean isIdentifierStartChar( char c )
    {
        return Verifier.isXMLNCNameStartCharacter( c );
    }

}
