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
 * Please note this software uses the Xerces-J parser which is governed by the The Apache
 * Software License, Version 1.1, which appears below.
 * 
 * See http://xml.apache.org for further details of Apache software.
 * 
 * Internal revision information: @version $Id: Loader.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 * 
 */

/**
 * This application uses Apache's Xerces XML parser which is covered by the Apache software
 * license (below).
 * 
 * The Apache Software License, Version 1.1
 * 
 * 
 * Copyright (c) 1999 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/)." Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Xerces" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", nor may "Apache" appear in
 * their name, without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally based on software copyright (c) 1999,
 * International Business Machines, Inc., http://www.ibm.com. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */

package com.griffinbrown.xmltool;

import org.apache.log4j.Logger;

/**
 * Utility class to facilitate dynamic class loading.
 */
class Loader
{
    private static Logger logger = Logger.getLogger( Loader.class );


    /**
     * We attempt to load classes using the class loader in action under the JVM.
     * If this is not available, use the <tt>Class.forname()</tt> technique.
     */
    public static Class getClass( String classname ) throws ClassNotFoundException
    {
        if( logger.isDebugEnabled() )
            logger.debug( "class requested to load=" + classname );

        //		ClassLoader loader = Thread.currentThread().getContextClassLoader();
        CustomClassLoader loader = new CustomClassLoader(); //use a custom loader

        if( loader != null )
        {
            try
            {
                return loader.loadClass( classname );
            }
            catch( ClassNotFoundException cnfe )
            {
                try
                {
                    return Class.forName( classname );
                }
                catch( ClassNotFoundException cnfe2 )
                {
                    throw cnfe2;
                }
            }
        }
        else
        {
            return Class.forName( classname );
        }
    }
}