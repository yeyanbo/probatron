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
 * Created on 31-Aug-2005
 */
package org.probatron.functions;

import org.probatron.QueryEvaluator;
import org.probatron.QueryHandler;
import org.probatron.jaxen.xpath2.MatchesFunction;

import com.griffinbrown.xmltool.Constants;
import com.griffinbrown.xmltool.Instance;
import com.griffinbrown.xmltool.Session;

/**
 * @author andrews
 *
 * @version $Id: CoreLibrary.java,v 1.2 2009/02/11 08:52:53 GBDP\andrews Exp $
 */
public class CoreLibrary
{

    private CoreLibrary()
    {}


    /**
     *
     * Initialize any XPath functions specified as built-in by this class.
     *
     */
    public static void initBuiltIns( QueryHandler handler )
    {
        Session session = handler.getSession();
        Instance instance = session.getInstance();

        Object asAbsoluteURI = AsAbsoluteURIFunction.getInstance( handler, session.getConfig()
                .getSystemId() );

        Object charToInt = new CharToIntFunction();
        Object docFunc = DocumentFunction.newInstance( handler );

        Object fileExistsFunc = FileExistsFunction.getInstance( handler, session.getConfig()
                .getSystemId() );

        Object functionAvailable = null;

        Object inCdataSect = new IsInCdataFunction();
        Object inNodeSet = InNodeSetFunction.newInstance( handler );
        Object isDeclaredEmpty = null;

        Object match = new RegExpFunction( handler );
        Object matches = new MatchesFunction();

        Object mixedCont = null;

        Object tokenizeFunc = new StringTokenizeFunction();

        QueryEvaluator evaluator = handler.getEvaluator();

        evaluator.registerFunction( "allows-pcdata", mixedCont );
        evaluator.registerFunction( "as-absolute-uri", asAbsoluteURI );
        evaluator.registerFunction( "char-to-int", charToInt );

        evaluator.registerFunction( "document", docFunc );
        evaluator.registerFunction( "file-exists", fileExistsFunc );

        evaluator.registerFunction( "function-available", functionAvailable );

        evaluator.registerFunction( "in-cdata-section", inCdataSect );
        evaluator.registerFunction( "in-nodeset", inNodeSet );
        evaluator.registerFunction( "is-declared-empty", isDeclaredEmpty );

        evaluator.registerFunction( "match-regexp", match );
        evaluator.registerFunction( Constants.PROBATRON_XPATH_FUNCTION_NS, "matches", matches );

        evaluator.registerFunction( "tokenize", tokenizeFunc );

    }

}