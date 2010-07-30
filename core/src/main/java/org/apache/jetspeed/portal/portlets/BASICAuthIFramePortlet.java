/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portal.portlets;

import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import org.apache.turbine.util.RunData;
import org.apache.ecs.ConcreteElement;


/**
 * Same as IFramePortlet except that it allows to use basic authentication using current
 * user name and password (or whatever is provided in portlet preferences)
 * 
 * @author <a href="mailto:hoju@visi.com">Jacob Kjome</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: BASICAuthIFramePortlet.java,v 1.3 2004/02/23 04:03:34 jford Exp $
 */
public class BASICAuthIFramePortlet extends IFramePortlet
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BASICAuthIFramePortlet.class.getName());    
    
    private String origSource = null;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    /**
     * 
     * @param runData
     * @return 
     */
    public ConcreteElement getContent(RunData runData)
    {
        if (org.apache.jetspeed.util.PortletSessionState.getPortletConfigChanged(this, runData))
        {
            try {
                this.init();
            }
            catch (PortletException pe)
            {
                logger.error("Exception", pe);
            }
        }
        
        setSource(getSource(runData));

        return super.getContent(runData);
    }

    /**
     * 
     * @param runData
     * @return 
     */
    public String getSource(RunData runData)
    {
        String source = origSource;
        if (source == null || source.trim().length() == 0)
        {
            return null;
        }

        int schemeCount = 8;
        int index = source.indexOf("https://");
        if (index == -1)
        {
            schemeCount = 7;
            index = source.indexOf("http://");
        }

        if (index != -1)
        {
            String user = this.getPortletConfig().getInitParameter(USERNAME);
            if (user == null || user.trim().length() == 0)
            {
                user = runData.getUser().getUserName();
            }
            String pass = this.getPortletConfig().getInitParameter(PASSWORD);
            if (pass == null || pass.trim().length() == 0)
            {
                pass = runData.getUser().getPassword();
            }
            String beginStr = source.substring(0, schemeCount);
            String endStr   = source.substring(schemeCount);
            int size = user.length() + pass.length() + source.length() + 2;
            StringBuffer buff = new StringBuffer(size);
            source = buff.append(beginStr).append(user).append(":").append(pass).append("@").append(endStr).toString();
        }
        return source;
    }

    /**
     * 
     * @exception PortletException
     */
    public void init() throws PortletException 
    {
        super.init();
        origSource = getSource();
    }

}
