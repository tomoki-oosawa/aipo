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
 
package org.apache.jetspeed.modules.actions.portlets;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;

// Turbine stuff
import org.apache.turbine.util.RunData;

// Velocity Stuff
import org.apache.velocity.context.Context;

import org.apache.jetspeed.services.forward.ForwardService;
import org.apache.jetspeed.services.forward.configuration.Forward;
import org.apache.jetspeed.services.forward.configuration.PortletForward;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.jetspeed.util.PortletConfigState;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.HtmlItem;

/**
 * Demo of Forward stuff
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ForwardDemoAction.java,v 1.6 2004/02/23 02:56:58 jford Exp $ 
 */
public class ForwardDemoAction extends VelocityPortletAction
{
    private static final String PARAM_NEXT = "fda_next";
    private static final String PARAM_TARGET = "fda_target";

    private static final String VAR_FORWARDS = "fda_forwards";
    private static final String VAR_TARGETS = "fda_targets";

    private static final String PORTLET_NAME = "ForwardDemo"; // this is fu'd up

    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected void buildNormalContext( VelocityPortlet portlet, 
                                       Context context,
                                       RunData rundata )
    {
        String next = (String)PortletSessionState.getAttribute(rundata, PARAM_NEXT);
        if (null == next)
        {
            next = (String)PortletConfigState.getParameter(portlet, rundata, PARAM_NEXT, "NOT_SET");

            PortletSessionState.setAttribute(rundata, PARAM_NEXT, next);
        }
        String target = (String)PortletSessionState.getAttribute(rundata, PARAM_TARGET);
        if (null == target)
        {
            target = (String)PortletConfigState.getParameter(portlet, rundata, PARAM_TARGET, "NOT_SET");

            PortletSessionState.setAttribute(rundata, PARAM_TARGET, target);
        }

        List forwards = (List)PortletSessionState.getAttribute(rundata, VAR_FORWARDS);
        if (null == forwards)
        {
            forwards = getAllForwards(next);
            PortletSessionState.setAttribute(rundata, VAR_FORWARDS, forwards);
        }
        
        List portletForwards = (List)PortletSessionState.getAttribute(rundata, VAR_TARGETS);
        if (null == portletForwards)
        {
            portletForwards = getPortletForwards(target);
            PortletSessionState.setAttribute(rundata, VAR_TARGETS, portletForwards);
        }

        context.put(VAR_FORWARDS, forwards);
        context.put(PARAM_NEXT, next);
        context.put(VAR_TARGETS, portletForwards);
        context.put(PARAM_TARGET, target);

    }

    public void doUpdate(RunData rundata, Context context)
    {
        // get posted new target
        String next = (String)rundata.getParameters().getString(PARAM_NEXT);
        
        if (next!=null)
        {
            PortletSessionState.setAttribute( rundata, PARAM_NEXT, next);

            List forwards = (List)PortletSessionState.getAttribute(rundata, VAR_FORWARDS);
            if (forwards != null)
            {
                Iterator it = forwards.iterator();

                while (it.hasNext())
                {
                    HtmlItem item = (HtmlItem)it.next();

                    if (item.getName().equals(next))
                    {
                        item.setSelected(true);
                    }
                    else
                    {
                        item.setSelected(false);
                    }
                }
            }

            ForwardService forward = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
            forward.forward(rundata, next);
        }
    }

    private List getAllForwards(String next)
    {
        ForwardService fs = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
        List vList = new java.util.LinkedList();
        Iterator it = fs.getForwards().iterator();
        int index = 1;
        while (it.hasNext())
        {
            Forward forward = (Forward)it.next();
            boolean selected = forward.getName().equals(next);
            vList.add(new HtmlItem(index, forward.getName(), selected));
            index++;
        }

        return vList;
    }


    public void doTarget(RunData rundata, Context context)
    {
        // get posted new target
        String target = (String)rundata.getParameters().getString(PARAM_TARGET);
        if (target!=null)
        {
            PortletSessionState.setAttribute( rundata, PARAM_TARGET, target);

            List forwards = (List)PortletSessionState.getAttribute(rundata, VAR_TARGETS);
            if (forwards != null)
            {
                Iterator it = forwards.iterator();

                while (it.hasNext())
                {
                    HtmlItem item = (HtmlItem)it.next();

                    if (item.getName().equals(target))
                    {
                        item.setSelected(true);
                    }
                    else
                    {
                        item.setSelected(false);
                    }
                }
            }

            ForwardService fs = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
            fs.forward(rundata, PORTLET_NAME, target);
        }
    }

    private List getPortletForwards(String target)
    {
        ForwardService fs = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
        List vList = new java.util.LinkedList();
        Iterator it = fs.getPortletForwards().iterator();
        int index = 1;
        while (it.hasNext())
        {
            PortletForward forward = (PortletForward)it.next();
            boolean selected = forward.getTarget().equals(target);
            vList.add(new HtmlItem(index, forward.getTarget(), selected));
            index++;
        }

        return vList;
    }

    public void doDynamic(RunData rundata, Context context)
    {
        Map map = new HashMap();
        map.put("dynamic", "33");
        ForwardService fs = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
        fs.forwardDynamic(rundata, "ApacheGroupNews", map);
    }

    public void doDynamic2(RunData rundata, Context context)
    {
        Map map = new HashMap();
        map.put("dynamic", "44");
        map.put("msgok", "no");
        map.put("msg", "3");

        ForwardService fs = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
        fs.forwardDynamic(rundata, PORTLET_NAME, "Success", map);
    }

}

