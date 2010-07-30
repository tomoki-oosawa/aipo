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

package org.apache.jetspeed.portal.portlets;

import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.JetspeedClearElement;

import org.apache.ecs.ConcreteElement;

import org.apache.turbine.util.RunData;

/**
    Aggregate Portlet aggregates the content of other portlets.

    This portlet is a test for an alternate aggregation algorithm (from getSet)

    @author <A HREF="mailto:taylor@apache.org">David Sean Taylor</A>
    @version $Id: AggregatePortlet.java,v 1.8 2004/02/23 04:03:34 jford Exp $
*/

public class AggregatePortlet extends AbstractPortlet
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(AggregatePortlet.class.getName());
    
    /**
    Returns an HTML representation of this portlet.  Usually a Portlet would
    initialized itself within init() and then when getContent is called it
    would return its presentation.
    */
    public ConcreteElement getContent(RunData rundata)
    {
        String key = ((JetspeedRunData)rundata).getProfile().getId()
                    + "." + this.getID();

        String path = (String)rundata.getUser().getTemp(key);
        if (path == null)
        {
            path = this.getPortletConfig().getInitParameter("path");
        }

        if (null == path)
        {
            return new JetspeedClearElement("Path parameter not set");
        }
        ProfileLocator locator = Profiler.createLocator();
        locator.createFromPath(path);
        String id = locator.getId();

        try
        {
            Profile profile = Profiler.getProfile(locator);
            PSMLDocument doc = profile.getDocument();

            if (doc == null)
            {
                return null;
            }
            Portlets portlets = doc.getPortlets();
            PortletSet ps = PortalToolkit.getSet(portlets);
            return ps.getContent(rundata);
        }
        catch (Exception e)
        {
            logger.error("Exception",  e);
            return new JetspeedClearElement("Error in aggregation portlet: " + e.toString());
        }
    }



}
