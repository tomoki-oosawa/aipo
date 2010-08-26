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
package org.apache.jetspeed.modules.actions.portlets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.registry.MediaTypeRegistry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action extends the RegistryBrowseAction to provide filtering for portlets
 * and add extra information into the context 
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id: PortletBrowseAction.java,v 1.2 2004/02/23 02:56:58 jford Exp $
 */
public class PortletBrowseAction extends RegistryBrowseAction
{
    /**
    * Subclasses must override this method to provide default behavior
    * for the portlet action
    */
    /**
    * Build the normal state content for this portlet.
    *
    * @param portlet The velocity-based portlet that is being built.
    * @param context The velocity context for this request.
    * @param rundata The turbine rundata context for this request.
    */
    protected void buildNormalContext(
        Portlet portlet,
        Context context,
        RunData rundata)
    {
        super.buildNormalContext(portlet, context, rundata);

        List portlets =
            (List) PortletSessionState.getAttribute(portlet, rundata, RESULTS);

        List categories = PortletFilter.buildCategoryList(portlets);
        context.put("categories", categories);

        MediaTypeRegistry mediaTypeReg =
            (MediaTypeRegistry) Registry.get(Registry.MEDIA_TYPE);

        ArrayList collection = new ArrayList();
        Iterator iter = mediaTypeReg.listEntryNames();
        while (iter.hasNext())
        {
            collection.add(iter.next());
        }

        context.put("media_types", collection);

        context.put("parents", PortletFilter.buildParentList(portlets));
    }

    /**
     * Filter portlets by using the PortletFilter helper class
     * 
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryBrowseAction#filter(java.util.List, java.lang.String[], java.lang.String[])
     */
    protected List filter(List entries, String[] fields, String[] values)
    {
        return PortletFilter.filterPortlets(entries, fields, values);
    }
}
