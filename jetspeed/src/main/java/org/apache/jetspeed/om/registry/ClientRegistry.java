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

package org.apache.jetspeed.om.registry;

/**
 * <P>
 * The <CODE>ClientRegistry</CODE> interface allow to query the system
 * Registry to find client implementations whose "User-agent" identification
 * matches a specific regular expression as defined in a <code>ClientEntry</code>
 * </P>
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: ClientRegistry.java,v 1.2 2004/02/23 03:11:39 jford Exp $
 */
public interface ClientRegistry extends Registry
{

    /**
     * Returns the client which matches the given useragent string.
     *
     * @param useragent     the useragent to match
     * @return the found client or null if the user-agent does not match any
     *  defined client
     */
    public ClientEntry findEntry(String useragent);

}
