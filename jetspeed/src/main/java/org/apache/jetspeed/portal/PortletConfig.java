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

package org.apache.jetspeed.portal;

//standard java stuff
import java.util.Map;

//jetspeed support
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.util.Config;
import org.apache.jetspeed.util.MetaData;

/**
Defines a configuration for Portlets.  A PortletConfig provides information
about the running environment of a given Portlet.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: PortletConfig.java,v 1.50 2004/02/23 04:05:35 jford Exp $
*/

public interface PortletConfig extends Config
{
    
    /**
    Init this PortletConfig providing the basic info.
    */
    public void init( String url, Map init_params );

    /**
    Returns the portlet current PortletSet
    */
    public PortletSet getPortletSet();

    /**
    Set the context (PortletSet) for this portlet
    */
    public void setPortletSet(PortletSet set);

    /**
    Returns the portlet current PortletSet
    */
    public Map getLayout();

    /**
    Set the context (PortletSet) for this portlet
    */
    public void setLayout(Map constraints);

    /**
     * Returns the current skin mapping.  This method is used for configuration.  Use
     * getPortletSkin() to find skin use by the Layout engine.
     *
     * @return Current skin mapping or null if no skin is defined in PSML.
     */
    public Map getSkin();

    /**
     * Set the context (PortletSet) for this portlet
     *
     * @deprecated use setPortletSkin instead
     */
    public void setSkin(Map skin);

    /**
    Returns the portlet current PortletSet
    */
    public int getPosition();

    /**
    Set the context (PortletSet) for this portlet
    */
    public void setPosition(int position);

    /**
    Returns this Portlet's Metainfo or null it none exists.  The Metainfo can
    be used to determine an optional title or description for this Portlet.
    */
    public MetaData getMetainfo();

    /**
    Set the metainfo for the Portlet
    */
    public void setMetainfo(MetaData metainfo);

    /**
    Portlets can have external configuration information other than just
    parameters.  A URL can define an external configuration file or HTML file
    that this Portlet can parse out.

    The main reason for using setURL/getURL is because the remote URL is cached
    within Jetspeed so future requests won't have any latency.
    */
    public String getURL();

    /**
    Used to define a Portlet's URL.
    */
    public void setURL(String url);

    /**
     * Determines whether to use the URL as part of the unique id to the portlet cache.
     * This can be used to control the lifetime of the portlet. 
     * The URL is used in combination with the parameter names and values for this portlet
     * to uniquely identify to portlet. Parameters may also be optionally included in the cache key.
     * This value can be set in the portlet registry.
     *
     * @return true if the URL is to be part of the cache key.
     */
    public boolean isCachedOnURL();

    /**
     * Determines whether to use the URL as part of the unique id to the portlet cache.
     * This can be used to control the lifetime of the portlet. 
     * The URL is used in combination with the parameter names and values for this portlet
     * to uniquely identify to portlet. Parameters may also be optionally included in the cache key.
     * This value can be set in the portlet registry.
     *
     * @return cached set to true if want this portlet to be cached based on the url
     */
    public void setCachedOnURL(boolean cached);


    /**
    Returns a parameter (or defaultValue) that was given to a Portlet.  This can be
    by a Portlet to obtain further information of itself.
    The parameter is returned even if it is defined in the context and not directly
    in the portlet config
    */
    public String getLayout(String name, String defaultValue);

    /**
    Returns a parameter (or defaultValue) that was given to a Portlet.  This can be
    by a Portlet to obtain further information of itself.
    The parameter is returned even if it is defined in the context and not directly
    in the portlet config
    */
    public String getSkin(String name, String defaultValue);

    /**
    Sets a skin parameter value in the local config
    */
    public void setSkin(String name, String value);

    /**
     * Retrieves the Skin object that should be used for this portlet. If 
     * the current portlet does not have a skin, then skin is retrieve from
     * the parent portlet set of the system default is now skins are defined
     * in the portlet set.
     *
     * getSkin() can be used for configuration.
     *
     * @return the Skin object that should be used.
     */
    public PortletSkin getPortletSkin();
    
    /**
     * Sets the PortletSkin to use for this Portlet
     *
     * @param skin the new skin to use
     */
    public void setPortletSkin(PortletSkin skin);
    
    /**
     * Retrieves the constraints associated with this portlet
     *
     * @return the Constraints object
     */
    public PortletSet.Constraints getConstraints();
    
    /**
     * Sets the layout constraints in the current portlet set
     *
     * @param constraints the constrints object associated with this portlet
     * in the current set
     */
    public void setConstraints(PortletSet.Constraints constraints);
    
    /**
    */
    public CapabilityMap getCapabilityMap();
    
    /**
    */
    public void setCapabilityMap( CapabilityMap cm );

    public void setPortletId(String portletId);
    public String getPortletId();
    public void setPageId(String pageId);
    public String getPageId();
    
    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);
    
}
