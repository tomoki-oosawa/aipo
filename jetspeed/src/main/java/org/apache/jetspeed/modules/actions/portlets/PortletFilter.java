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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.base.BaseCategory;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * An abstract class with helper methods for filtering Portlets.
 *
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id: PortletFilter.java,v 1.3 2004/02/23 02:56:58 jford Exp $
 */
public abstract class PortletFilter
{
    /**
    * Static initialization of the logger for this class
    */
    private static final JetspeedLogger logger =
        JetspeedLogFactoryService.getLogger(PortletFilter.class.getName());

    /**
     * Method that filters a list of portlets based on a give filter name/value.
     * 
     * @param portlets List of portlets to filter
     * @param field The name of the filter
     * @param value The value of the filter
     * @return List of portlets that met the filter criteria
     */
    public static List filterPortlets(
        List portlets,
        String field,
        String value)
    {
        String[] fields = { field };
        String[] values = { value };

        return filterPortlets(portlets, fields, values);
    }

    /**
     * Method that filters a list of portlets based on certain criteria.
     * 
     * @param portlets The list of portlets to filter
     * @param fields The list of fields
     * @param values The list of values.  This should be in a 1:1 ratio with the fields.
     * @return List of portlets that met the filter criteria
     */
    public static List filterPortlets(
        List portlets,
        String[] fields,
        String[] values)
    {
        List filteredPortlets = new ArrayList();

        Iterator portletIter = portlets.iterator();
        while (portletIter.hasNext())
        {
            PortletEntry entry = (PortletEntry) portletIter.next();
            if (isFilteredIn(entry, fields, values))
            {
                filteredPortlets.add(entry);
            }
        }

        return filteredPortlets;
    }

    /**
     * Method that checks a portlet entry to see if it matches the given
     * filter criteria.
     * 
     * @param entry The entry to filter
     * @param fields The list of fields.
     * @param values The list of values.  This should be in a 1:1 ratio with the fields.
     * @return
     */
    private static boolean isFilteredIn(
        PortletEntry entry,
        String[] fields,
        String[] values)
    {
        boolean result = true;

        if (fields != null && values != null && fields.length == values.length)
        {
            for (int i = 0; i < fields.length && result; i++)
            {
                String field = fields[i];
                String value = values[i];

                if (field == null
                    || value == null
                    || field.length() == 0
                    || value.length() == 0)
                {
                    //skip and add to list
                }
                else if (field.equals("category"))
                {
                    result = result && entry.hasCategory(value);
                }
                else if (field.equals("media_type"))
                {
                    result = result && entry.hasMediaType(value);
                }
                else if (field.equals("parent"))
                {
                    if (entry.getParent() != null)
                    {
                        result = result && entry.getParent().equals(value);
                    }
                    else
                    {
                        result = false;
                    }
                }
                else if (field.equals("type"))
                {
                    if (entry.getType() != null)
                    {
                        result = result && entry.getType().equals(value);
                    }
                    else
                    {
                        result = false;
                    }
                }
                /*
                else if(field.equals("permission"))
                {
                    result = JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(), 
                                                                     new PortalResource(entry), 
                                                                     value);
                }
                */
                else
                {
                    logger.warn("Invalid filter " + field + " attempted");
                }
            }
        }

        return result;
    }

    /**
     * Builds a list of all portlet categories
     * 
     * @param List portlets portlets to scan for categories
     * @return List of categories
     */
    public static List buildCategoryList(List portlets)
    {
        TreeMap catMap = new TreeMap();
        Iterator pItr = portlets.iterator();
        while (pItr.hasNext())
        {
            PortletEntry entry = (PortletEntry) pItr.next();

            Iterator cItr = entry.listCategories();
            while (cItr.hasNext())
            {
                BaseCategory cat = (BaseCategory) cItr.next();
                catMap.put(cat.getName(), cat);
            }
        }

        return new ArrayList(catMap.values());
    }

    /**
     * Method to return all portlets in the Portlet Registry
     * 
     * @return List of portlets
     */
    public static List getAllPortlets()
    {
        List regEntries = new ArrayList();

        Iterator iter = Registry.get(Registry.PORTLET).listEntryNames();
        while (iter.hasNext())
        {
            String entryName = (String) iter.next();
            regEntries.add(Registry.getEntry(Registry.PORTLET, entryName));
        }

        return regEntries;
    }

    /**
     * Method that returns a list of parents from the provided list of portlets.
     * 
     * @param portlets List of portlets to search for parents
     * @return List of portlets that are parents
     */
    public static List buildParentList(List portlets)
    {
        HashSet parentSet = new HashSet();

        Iterator portletIter = portlets.iterator();
        while (portletIter.hasNext())
        {
            PortletEntry regEntry = (PortletEntry) portletIter.next();

            String regType = regEntry.getType();
            if (regType.equalsIgnoreCase(PortletEntry.TYPE_ABSTRACT))
            {
                parentSet.add(regEntry);
            }
        }

        return new ArrayList(parentSet);
    }

}
