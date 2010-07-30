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

package org.apache.jetspeed.services.forward.configuration.impl;

import java.util.Map;
import java.util.HashMap;

import org.apache.jetspeed.services.forward.configuration.PortletForward;

/**
 * Portlet Forward implementation
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletForwardImpl.java,v 1.4 2004/02/23 03:50:10 jford Exp $
 */
public class PortletForwardImpl implements PortletForward, java.io.Serializable
{
    private String portlet;
    private String forward;
    private String target;
    private Map queryParams = new HashMap();


    public String getPortlet()
    {
        return this.portlet;
    }

    public void setPortlet(String portlet)
    {
        this.portlet = portlet;
    }

    public String getForward()
    {
        return this.forward;
    }

    public void setForward(String forward)
    {
        this.forward = forward;
    }

    public String getTarget()
    {
        return this.target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public Map getQueryParams()
    {
        return this.queryParams;
    }

    public void setQueryParams(Map queryParams)
    {
        this.queryParams = queryParams;
    }

}



