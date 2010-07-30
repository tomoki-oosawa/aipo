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

// Java APIs
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

// Turbine Modules
import org.apache.turbine.util.RunData;
import org.apache.jetspeed.services.search.Search;

// Jetspeed Stuff
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.portal.Portlet;


/**
 * This class is responsible for indexing the registry.
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: IndexPortletRegistry.java,v 1.6 2004/03/31 04:49:10 morciuch Exp $
 */
public class IndexPortletRegistry extends SecureGenericMVCAction
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(IndexPortletRegistry.class.getName());     
    
    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext(Portlet portlet, RunData rundata)
    {
        // Do nothing
    }

    /**
     * Continue event handler.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    public void doIndex(RunData rundata, Portlet portlet)
    {
        if (portlet == null)
        {
            return;
        }

        Collection c = new ArrayList();

        for (Iterator i = Registry.get(Registry.PORTLET).listEntryNames(); i.hasNext();)
        {
            PortletEntry entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, (String) i.next());
            if (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT) && !entry.isHidden())
            {
                c.add(entry);
                //System.out.println("Will index [" + entry.getTitle() + "]");
            }
        }

        try
        {
            // Delete all entries from index
            Search.remove(c);
        }
        catch (Throwable e)
        {
            logger.error("Throwable", e);
        }

        try
        {
            // Add all entries to index
            Search.add(c);
        }
        catch (Throwable e)
        {
            logger.error("Throwable", e);
        }
    }

}
