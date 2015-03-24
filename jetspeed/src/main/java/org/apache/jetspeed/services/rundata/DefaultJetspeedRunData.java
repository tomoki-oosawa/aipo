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

// Java classes
import java.util.Stack;

// Jetspeed classes
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.services.statemanager.StateManagerService;
import org.apache.turbine.services.rundata.DefaultTurbineRunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.security.AccessControlList;

/**
 * This interface extends the RunData interface with methods
 * specific to the needs of a Jetspeed like portal implementation.
 *
 * <note>Several of these properties may be put in the base RunData
 * interface in future releases of Turbine</note>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: DefaultJetspeedRunData.java,v 1.20 2004/02/23 03:36:10 jford Exp $
 */
public class DefaultJetspeedRunData extends DefaultTurbineRunData
    implements JetspeedRunData
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DefaultJetspeedRunData.class.getName());
    
    private Profile profile = null;
    private CapabilityMap map = null;
    private String peid = null;
    private String pid = null;
    private int mode = NORMAL;
    private String template = null;

    /**
     * Returns the portlet id referenced in this request
     *
     * @return the portlet id referenced or null
     */
    public String getPortlet()
    {
      return pid;
    }

    /**
     * Sets the portlet id referenced for this request
     *
     * @param id the portlet id referenced in this request
     */
    public void setPortlet(String id)
    {
        this.pid = id;
    }

    /**
     * Returns the portlet id which should be customized for this request
     *
     * @return the portlet id being customized or null
     */
    public Portlet getCustomized()
    {
        // customization state info is in the page's session state
        SessionState customizationState = getPageSessionState();
        Stack stack = (Stack)customizationState.getAttribute("customize-stack");

        Portlet p = null;

        if ((stack!=null)&&(!stack.empty()))
        {
            p = (Portlet)stack.peek();
        }

        /**
         * Save the title of this currently selected portlet
         * --------------------------------------------------------------------------
         * last modified: 11/06/01
         * Andreas Kempf, Siemens ICM S CP PE, Munich
         */
        if ((p != null) && (stack.size() > 1))
          customizationState.setAttribute ("customize-paneName", (String)p.getTitle());
        else
          customizationState.setAttribute ("customize-paneName", "*");

        return (Portlet)p;
    }

    /**
     * Sets the portlet id to customize
     *
     * @param id the portlet id to customize or null;
     */
    public void setCustomized(Portlet p)
    {
        // customization state info is in the page's session state
        SessionState customizationState = getPageSessionState();
        Stack stack = (Stack)customizationState.getAttribute("customize-stack");
        if (stack == null)
        {
            stack = new Stack();
            customizationState.setAttribute("customize-stack", stack);
        }

        if (p==null)
        {
            if (!stack.empty()) stack.pop();

            customizationState.setAttribute ("customize-paneName", "*");
        }
        else
        {
          if (stack.size () > 0)
          {
            Portlet last = (Portlet)stack.peek();


            if ((last!=null) && (p.getName().equals(last.getName())) && (p.getTitle().equals(last.getTitle())))
            {
                //System.out.println ("Portlet already used!!!");
            }
            else
              stack.push(p);
          }
          else
            stack.push(p);


          /**
           * Save the title of this currently selected portlet
           * --------------------------------------------------------------------------
           * last modified: 11/06/01
           * Andreas Kempf, Siemens ICM S CP PE, Munich
           */

           customizationState.setAttribute ("customize-paneName", (String)p.getTitle());
        }
    }

    /**
    * Get the psml profile being customized.
    * @return the Profile being customized.
    */
    public Profile getCustomizedProfile()
    {
        // customization state info is in the page's session state
        SessionState customizationState = getPageSessionState();

        return (Profile) customizationState.getAttribute("customize-profile");

    }   // getCustomizedProfile

    /**
    * Set the psml profile being customized.
    * @param profile The Profile being customized.
    */
    public void setCustomizedProfile(Profile profile)
    {
        // customization state info is in the page's session state
        SessionState customizationState = getPageSessionState();

        customizationState.setAttribute("customize-profile", profile);

    }   // setCustomizedProfile

    /**
    * Clean up from customization
    */
    public void cleanupFromCustomization()
    {
        // get the customization state for this page
        SessionState customizationState = getPageSessionState();

        customizationState.removeAttribute("customize-stack");
        customizationState.removeAttribute("customize-paneName");
        customizationState.removeAttribute("customize-profile");
        //customizationState.removeAttribute("customize-type");
        customizationState.removeAttribute("customize-columns");
        customizationState.removeAttribute("customize-mode");
        customizationState.removeAttribute("customize-parameters");

       setMode("default");

    }   // cleanupFromCustomization

    /**
     * Returns the portlet id which should be maximized for this request
     *
     * @return the portlet id being maximized or null
     */
    public int getMode()
    {
        return this.mode;
    }

    /**
     * Sets the portlet id to maximize
     *
     * @param id the portlet id to maximize or null;
     */
    public void setMode(int mode)
    {
        this.mode=mode;
    }

    /**
     * Sets the portlet id to maximize
     *
     * @param id the portlet id to maximize or null;
     */
    public void setMode(String mode)
    {
        if ("customize".equals(mode))
        {
            setMode(CUSTOMIZE);
        }
        else if ("maximize".equals(mode))
        {
            setMode(MAXIMIZE);
        }
        else
        {
            setMode(NORMAL);
            setCustomized(null);
        }
    }

    /**
     * Returns the template path as requested from the parameters
     */
    public String getRequestedTemplate()
    {
        return this.template;
    }

    /**
     * Sets the template path as requested from the parameters
     */
    public void setRequestedTemplate(String id)
    {
        this.template=id;
    }

    /**
     * Returns the capability map for the user agent issuing this request
     *
     * @return a capability map objet
     */
    public CapabilityMap getCapability()
    {
        if (map == null)
        {
            map = CapabilityMapFactory.getCapabilityMap(this);
        }

        return map;
    }

    /**
     * Sets the user portal profile for the current request
     *
     * @param profile a profile implementation for the current request
     */
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }

    /**
     * Gets the user portal profile for the current request
     *
     * @return a profile implementation for the current request
     */
    public Profile getProfile()
    {
        return this.profile;
    }

    /** Clears the state of this object for recycling... */
    public void dispose()
    {
        mode=0;
        map = null;
        peid = null;
        pid = null;
        profile = null;
        template = null;

        super.dispose();
    }

    /**
     * Returns the portlet id (PEID) referenced in this request
     *
     * @return the portlet id (PEID) referenced or null
     */
    public String getJs_peid()
    {
        return peid;
    }

    /**
     * Sets the portlet id (PEID) referenced for this request
     *
     * @param id the portlet id (PEID) referenced in this request
     */
    public void setJs_peid(String peid)
    {
        this.peid = peid;
    }

    /**
     * Get the user id for the current user.
     * This method is provided as an abstraction to the very implementation
     * specific method of retrieving user ids in Turbine.
     *
     * @return int The current user's id.
     */
    public String getUserId()
    {
        JetspeedUser user = getJetspeedUser();
        if (user == null)
        {
            return "";
        }
        return user.getUserId();
    }

    /**
     * Access an identifier for the current request's PageSession.
     * A PageSession is a specific portal page being viewed in a specific
     * user session (and perhaps, but not yet [@todo] in a specific browser window).
     * @return the identifier for the current request's PageSession.
     */
    public String getPageSessionId()
    {
        // form based on the session and page's profile's id
        // session
        String sessionId = "?";
        if (getSession() != null)
        {
            sessionId = getSession().getId();
        }
        else
        {
            logger.warn("DefaultJetspeedRunData.getPageSessionId: no session");
        }

        // profile
        String profileId = "?";
        if (getProfile() != null)
        {
            profileId = getProfile().getId();
        }
        else
        {
            logger.warn("DefaultJetspeedRunData.getPageSessionId: no profile");
        }

        return sessionId + profileId;

    }   // getPageSessionId

    /**
     * Access the current request's UserSession state object.
     * @return the current request's UserSession state object (may be null).
     */
    public SessionState getUserSessionState()
    {
        // get the StateManagerService
        StateManagerService service = (StateManagerService)TurbineServices
                .getInstance().getService(StateManagerService.SERVICE_NAME);

        // handle no service
        if (service == null) return null;

        return service.getSessionState(getSession().getId());

    }   // getUserSessionState

    /**
     * Access the current request's PageSession state object.
     * @return the current request's PageSession state object (may be null).
     */
    public SessionState getPageSessionState()
    {
        // get the StateManagerService
        StateManagerService service = (StateManagerService)TurbineServices
                .getInstance().getService(StateManagerService.SERVICE_NAME);

        // handle no service
        if (service == null) return null;

        return service.getSessionState(getPageSessionId());

    }   // getPageSessionState

    /**
     * Access the current request's PortletSession state object.
     * @param id The Portlet's unique id.
     * @return the current request's PortletSession state object. (may be null).
     */
    public SessionState getPortletSessionState(String id)
    {
        // get the StateManagerService
        StateManagerService service = (StateManagerService)TurbineServices
                .getInstance().getService(StateManagerService.SERVICE_NAME);

        // handle no service
        if (service == null) return null;

        // PageSession key
        String pageInstanceId = getPageSessionId();

        return service.getSessionState(pageInstanceId + id);

    }   // getPortletSessionState

    /**
     * Returns the Jetspeed User (same as getUser without cast)
     *
     * @return the current user.
     */
    public JetspeedUser getJetspeedUser()
    {
        return (JetspeedUser)getUser();
    }

    /**
     * Function is deprecated by required to compile with Turbine
     *
     * @deprecated
     */
    public AccessControlList getACL()
    {
        return null;
    }
}
