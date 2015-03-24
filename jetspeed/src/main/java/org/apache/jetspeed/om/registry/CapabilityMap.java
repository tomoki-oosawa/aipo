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

import java.util.Iterator;

/**
 * <P>
 * The <CODE>CapabilityMap</CODE> interface represents a list that
 * stores capabilities a client is capable of. It is accessed
 * by the portlet container to get information about the client's
 * capabilities.
 * </P>
 * <P>
 * The names of the capabilities are matched by the class variable names
 * of the class <CODE>Capability</CODE>. To add a capability use the
 * class variable name and <B>not</B> the internally used string value.
 * </P>
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="raphael@apache.org">Rapha謖 Luta</a>
 * @see Capability
 * @version $Id: CapabilityMap.java,v 1.3 2004/02/23 03:11:39 jford Exp $
 */
public interface CapabilityMap
{

    /**
     * Returns an enumeration of all capabilities a client
     * is capabale of.
     *
     * @return an enumeration of all capabilities
     */
    public Iterator getCapabilities();

    /**
     * Adds the given capability
     *
     * @param name   the name of the new capability
     */
    public void addCapability(String name);

    /**
     * Removes the given capability
     *
     * @param name   the name of the capability to be removed
     */
    public void removeCapability(String name);

    /**
     * Checks if the argument capability is included in this map
     *
     * @param capabiltiy a capability descriptor
     * @return true if the capability is supported
     */
    public boolean contains(String capability);

    /**
     * Checks if the all the elements of argument capability map
     * are included in the current one
     *
     * @param map a CapabilityMap implementation to test
     * @return true is all the elements the argument map are included in the
     * current map.
     */
    public boolean containsAll(CapabilityMap map);

}
