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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.ConcreteElement;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.jetspeed.util.PortletConfigState;
import org.apache.jetspeed.util.StringUtils;
import org.apache.jetspeed.util.URIEncoder;
import org.apache.turbine.util.RunData;

/**
 * RedirectPortlet can be used for menu options that redirect directly 
 * to a URL outside of the portal.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: RedirectPortlet.java,v 1.5 2004/03/22 22:26:58 taylor Exp $
 */
public class RedirectPortlet extends AbstractInstancePortlet
{           
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(RedirectPortlet.class.getName());      
    
    public ConcreteElement getContent(RunData rundata)
    {
        String menuLevel = this.getPortletConfig().getInitParameter("menuLevel");        
        int menus = 1;        
        try 
        {
            if (menuLevel != null)
            {
                menus = Integer.parseInt(menuLevel);
            }
        }
        catch (Exception e)
        {
            logger.error("failed to parse menu level from Redirect Portlet");
        }
        
        // clear the pane ids of all parents so we don't get lock out the portal on return by recursive redirection
        PortletSet set = this.getPortletConfig().getPortletSet();
        int count = 0;
        while (set != null && count <= menus)
        {
            clearState(rundata, set);
            set = set.getPortletConfig().getPortletSet();
            count++;
        }
                     
        String url = this.getPortletConfig().getInitParameter("url");
        url = createDynamicUrl((JetspeedRunData)rundata, url);
        // rundata.setRedirectURI(url);
        HttpServletRequest request = rundata.getRequest();
        HttpServletResponse response = rundata.getResponse();
        try
        {
            String script = "<script> setTimeout( \"location.href='" + url + "'\", 1) </script>";
            response.getWriter().write(script);
            //response.sendRedirect(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String message = "Failed to redirect to " + url;
            logger.error(message, e);
            return new JetspeedClearElement(message);
        }
        return new JetspeedClearElement(url);        
    }
    
    private void clearState(RunData rundata, PortletSet set)
    {
        SessionState state = ((JetspeedRunData)rundata).getPortletSessionState(set.getID());
        state.setAttribute(JetspeedResources.PATH_PANEID_KEY, null);
    }
    
    private String createDynamicUrl(JetspeedRunData rundata, String url)
    {
        String parameterNames = PortletConfigState.getParameter(this, rundata, "parameterNames", null);
        String sessionAttributeNames = PortletConfigState.getParameter(this, rundata, "sessionAttributeNames", null);
        if (parameterNames == null || sessionAttributeNames == null)
        {
            return url;
        }
        
        String[] names = StringUtils.stringToArray(parameterNames, ",");
        String[] attribNames = StringUtils.stringToArray(sessionAttributeNames, ",");
        
        if (names == null || attribNames == null)
        {
            return url;
        }
        if (names.length == 0 || attribNames.length == 0)
        {
            return url;
        }
        int count = (names.length > attribNames.length) ? names.length : attribNames.length;
        StringBuffer dynamic = new StringBuffer(url);
        int appended = 0;
        for(int ix=0; ix < count; ix++)
        {
                String attribute = lookup(rundata, attribNames[ix]);
            //String attribute = (String)rundata.getSession().getAttribute(attribNames[ix]);
            if (attribute == null)
            {
                continue;
            }
            if (appended == 0)
            {
                dynamic.append("?");                
            }
            else
            {
                dynamic.append("&");
            }
            appended++;
            dynamic.append(URIEncoder.encode(names[ix]));
            dynamic.append("=");
            dynamic.append(URIEncoder.encode(attribute));
        }
        return dynamic.toString();
    }
    
    /**
     * First we look in the request for it, if not found look in the session.
     * If it contains a '.' & an object is found in the one of the scopes, then
     * we try and call the getter method for the field defined after the '.'.
     * 
     * @param rundata
     * @param attributeName
     * @return String The value
     */
    private String lookup(JetspeedRunData rundata,String attributeName)
    {
        String value = null;
        
        Object o = lookupAttribute(rundata, attributeName);
        
        //now see if there is a property defined in the attributeName
        if(o == null)
        {
            int index = attributeName.lastIndexOf(".");
            if(index > 0)
            {
                String name = attributeName.substring(0, index);
                String property = attributeName.substring(index + 1);
    
                o = lookupAttribute(rundata, name, property);
                
                if (o instanceof String)
                {
                    value = (String)o;
                }
                
            } //end if attributeName contains period
        }
        else if (o instanceof String)
        {
            value = (String) o;
        }
        return value;
    }
    
    private Object lookupAttribute(JetspeedRunData rundata, String name)
    {
        Object o = rundata.getRequest().getAttribute(name);
        if(o == null)
        {
            o = rundata.getSession().getAttribute(name);
        }
    
        return o;        
    }
    
    private Object lookupAttribute(JetspeedRunData rundata, String name, String property)
    {
        Object o = lookupAttribute(rundata, name);
        Object returnObject = null;                
        if(o != null)
        {
            //invoke the getter method for the property
            String getterName = "get" + property.substring(0,1).toUpperCase() + 
                                 property.substring(1);                    
            try 
            {
                Method getter = o.getClass().getMethod(getterName,null);
                        
                returnObject = getter.invoke(o,null);
            }
            catch(Exception e)
            {
                o = null;
            }
        } //end if o not null

        return returnObject;
    }
}
