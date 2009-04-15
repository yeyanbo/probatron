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

/** This is a W3C interface we include here so that NamespaceNode can compile in both
 *  Java 1.4 and 1.5. It's owned by the W3C, and available under their usual 
 *  extremely liberal license so this shoudldn't bother anyone. (XPath itself
 *  is under the same license after all.)
 */

package org.w3c.dom;

public interface UserDataHandler {
    // OperationType
    public static final short NODE_CLONED               = 1;
    public static final short NODE_IMPORTED             = 2;
    public static final short NODE_DELETED              = 3;
    public static final short NODE_RENAMED              = 4;
    public static final short NODE_ADOPTED              = 5;

    public void handle(short operation, 
                       String key, 
                       Object data, 
                       Node src, 
                       Node dst);

}