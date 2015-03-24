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
 * The <CODE>ClientEntry</CODE> interface represents one client inside
 * of the client registry. It is accessed by the portlet container
 * to get information about the clients.
 * </P>
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="raphael@apache.org">Rapha謖 Luta</a>
 */
public interface ClientEntry extends RegistryEntry
{
    /**
     * Returns the pattern parameter of this client. The pattern is used
     * to match a client to the user agent used to access the portal. If
     * the pattern matches the user agent string, this client is recognized
     * as the one the user is currently working with.
     *
     * @return the pattern of this client
     */
    public String getUseragentpattern();

    /**
     * Sets the pattern used to match the user agent.
     *
     * @param useragentpattern
     *               the new pattern
     */
    public void setUseragentpattern(String useragentpattern);

    /**
     * Returns the manufacturer of this client
     *
     * @return the manufacturer of this client
     */
    public String getManufacturer();

    /**
     * Sets the new manufacturer of this client
     *
     * @param name   the new manufacturer
     */
    public void setManufacturer(String name);

    /**
     * Returns the model of this client
     *
     * @return the model of this client
     */
    public String getModel();

    /**
     * Sets the new model of this client
     *
     * @param name   the new model
     */
    public void setModel(String name);

    /**
     * Returns the version of this client
     *
     * @return the version of this client
     */
    public String getVersion();

    /**
     * Sets the new version of this client
     *
     * @param name   the new version
     */
    public void setVersion(String name);

    /**
     * Returns all supported mimetypes as <CODE>MimeTypeMap</CODE>.
     * The <CODE>MimeTypeMap</CODE> contains all mimetypes in decreasing
     * order of importance.
     *
     * @return the MimeTypeMap
     * @see MimeTypeMap
     */
    public MimetypeMap getMimetypeMap();

    /**
     * Returns all supported capablities as <CODE>CapabilityMap</CODE>.
     * The <CODE>CapabilityMap</CODE> contains all capabilities in arbitrary
     * order.
     *
     * @return the CapabilityMap
     * @see CapabilityMap
     */
    public CapabilityMap getCapabilityMap();

}
