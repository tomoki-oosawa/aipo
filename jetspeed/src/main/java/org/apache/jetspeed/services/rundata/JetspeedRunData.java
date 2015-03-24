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

package org.apache.jetspeed.services.rundata;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.turbine.services.rundata.TurbineRunData;
import org.apache.jetspeed.om.security.JetspeedUser;

/**
 * This interface extends the RunData interface with methods
 * specific to the needs of a Jetspeed like portal implementation.
 *
 * <note>Several of these properties may be put in the base RunData
 * interface in future releases of Turbine</note>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: JetspeedRunData.java,v 1.10 2004/02/23 03:36:10 jford Exp $
 */
public interface JetspeedRunData extends TurbineRunData
{

    public int NORMAL = 0;
    public int CUSTOMIZE = 1;
    public int MAXIMIZE = 2;

    /**
     * Returns the Jetspeed User (same as getUser without cast)
     *
     * @return the current user.
     */
    public JetspeedUser getJetspeedUser();

    /**
     * Returns the portlet id referenced in this request
     *
     * @return the portlet id referenced or null
     */
    public String getPortlet();

    /**
     * Returns the portlet id (PEID) referenced in this request
     *
     * @return the portlet id (PEID) referenced or null
     */
    public String getJs_peid();

    /**
     * Sets the portlet id referenced for this request
     *
     * @param id the portlet id referenced in this request
     */
    public void setPortlet(String id);

    /**
     * Sets the portlet id (PEID) referenced for this request
     *
     * @param id the portlet id (PEID) referenced in this request
     */
    public void setJs_peid(String peid);

    /**
     * Returns the portlet id which should be customized for this request
     *
     * @return the portlet id being customized or null
     */
    public Portlet getCustomized();

    /**
     * Sets the portlet id to customize
     *
     * @param id the portlet id to customize or null;
     */
    public void setCustomized(Portlet p);

    /**
    * Get the psml profile being customized.
    * @return the Profile being customized.
    */
    public Profile getCustomizedProfile();

    /**
    * Set the psml profile being customized.
    * @param profile The Profile being customized.
    */
    public void setCustomizedProfile(Profile profile);

    /**
    * Clean up from customization
    */
    public void cleanupFromCustomization();

    /**
     * Returns the portlet id which should be maximized for this request
     *
     * @return the portlet id being maximized or null
     */
    public int getMode();

    /**
     * Sets the portlet id to maximize
     *
     * @param id the portlet id to maximize or null;
     */
    public void setMode(int mode);

    /**
     * Sets the portlet id to maximize
     *
     * @param id the portlet id to maximize or null;
     */
    public void setMode(String mode);

    /**
     * Returns the template path as requested from the parameters
     */
    public String getRequestedTemplate();

    /**
     * Sets the template path as requested from the parameters
     */
    public void setRequestedTemplate(String id);

    /**
     * Returns the capability map for the user agent issuing this request
     *
     * @return a capability map objet
     */
    public CapabilityMap getCapability();

    /**
     * Gets the user portal profile for the current request
     *
     * @return a profile implementation for the current request
     */
    public Profile getProfile();

    /**
     * Sets the user portal profile for the current request
     *
     * @param profile a profile implementation for the current request
     */
    public void setProfile(Profile profile);

    /**
     * Get the user id for the current user.
     * This method is provided as an abstraction to the very implementation
     * specific method of retrieving user ids in Turbine.
     *
     * @return String The current user's id.
     */
    public String getUserId();

    /**
     * Access an identifier for the current request's PageSession.
     * A PageSession is a specific portal page being viewed in a specific
     * user session (and perhaps, but not yet [@todo] in a specific browser window).
     * @return the identifier for the current request's PageSession.
     */
    public String getPageSessionId();

    /**
     * Access the current request's UserSession state object.
     * @return the current request's UserSession state object (may be null).
     */
    public SessionState getUserSessionState();

    /**
     * Access the current request's PageSession state object.
     * @return the current request's PageSession state object (may be null).
     */
    public SessionState getPageSessionState();

    /**
     * Access the current request's PortletSession state object.
     * @param id The Portlet's unique id.
     * @return the current request's PortletSession state object. (may be null).
     */
    public SessionState getPortletSessionState(String id);

}

