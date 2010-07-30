/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.services.security;

/**
 * This is the basic group exception. 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: GroupException.java,v 1.3 2004/02/23 03:58:11 jford Exp $
 */

public class GroupException extends JetspeedSecurityException {

    /**
     * Constructs a GroupException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public GroupException() {
        super();
    }

    /**
     * Constructs a GroupException with the specified detail message.
     * A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.  
     */
    public GroupException(String msg) {
        super(msg);
    }

    /**
     * Construct a nested exception.
     *
     * @param msg The detail message.
     * @param nested the exception or error that caused this exception 
     *               to be thrown.
     */
    public GroupException( String msg, Throwable nested )
    {
        super(msg, nested);
    }

}


