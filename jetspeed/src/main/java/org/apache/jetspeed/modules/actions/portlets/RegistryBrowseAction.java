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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action enables to browse any of the system registries for displaying
 * available entries and information on these entries
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $ID$
 */
public class RegistryBrowseAction extends GenericMVCAction
{
    public static final String PREFIX = "RegistryBrowseAction:";
    
    public static final String REFRESH = "refresh";
    public static final String FILTER_FIELDS = "filter_fields";
    public static final String FILTER_VALUES = "filter_values";
    
    public static final String START = "start";
    public static final String RESULTS = "results";
    public static final String FILTERED_RESULTS = "filtered_results";

    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected void buildNormalContext( Portlet portlet, 
                                       Context context,
                                       RunData rundata )
    {
        String regName = portlet.getPortletConfig()
                                .getInitParameter("registry",Registry.PORTLET);
        
        Boolean refresh = (Boolean)PortletSessionState.getAttribute(rundata, PREFIX + regName + ":" + REFRESH, Boolean.FALSE);
        
        if(refresh.equals(Boolean.TRUE))
        {
            PortletSessionState.clearAttribute(portlet, rundata, START);
            PortletSessionState.clearAttribute(portlet, rundata, RESULTS);
            PortletSessionState.clearAttribute(portlet, rundata, FILTERED_RESULTS);
            PortletSessionState.clearAttribute(rundata, PREFIX + regName + ":" + REFRESH);
        }
        
        int start = getStart(rundata, portlet);
        if (start < 0) start = 0;

        String pageSize = portlet.getPortletConfig()
                                 .getInitParameter("page-size","20");

        int size = Integer.parseInt(pageSize);

        int prev = start-size;
        
        if(prev < 0)
        {
            prev = 0;
        }
        
        String[] filterFields = (String[]) PortletSessionState.getAttribute(portlet, rundata, FILTER_FIELDS);
        String[] filterValues = (String[]) PortletSessionState.getAttribute(portlet, rundata, FILTER_VALUES);
        
                                
        List regEntries = (List)PortletSessionState.getAttribute(portlet, rundata, RESULTS);
        List filteredEntries = (List)PortletSessionState.getAttribute(portlet, rundata, FILTERED_RESULTS);
        if(regEntries == null)
        {
            Iterator i = Registry.get(regName).listEntryNames();
            regEntries = new ArrayList();
    
            while(i.hasNext())
            {
                String name = (String)i.next();
                
                RegistryEntry regEntry = Registry.getEntry(regName,name);
        
                if ( (regEntry!=null) && (!regEntry.isHidden()) )
                {
                    regEntries.add(regEntry);
                }
            }

            Collections.sort(regEntries,
                new Comparator() {
                    public int compare(Object o1, Object o2)
                    {
                        String t1 = ((RegistryEntry) o1).getName().toLowerCase();
                        String t2 = ((RegistryEntry) o2).getName().toLowerCase();
                                      
                        return t1.compareTo(t2);
                    }
                });
            
            PortletSessionState.setAttribute(portlet, rundata, RESULTS, regEntries);
            
            filteredEntries = filter(regEntries, filterFields, filterValues);
            PortletSessionState.setAttribute(portlet, rundata, FILTERED_RESULTS, filteredEntries);
        }
        
        if(filterFields != null && filterValues != null && filterFields.length == filterValues.length)
        {
            for(int i=0; i<filterFields.length; i++)
            {
                String field = filterFields[i];
                String value = filterValues[i];
                
                context.put(field + "_filter_value", value);
            }
        }
        
        int end = start+size;
        if(end> filteredEntries.size())
        {
            end = filteredEntries.size();
        }
        List pageEntries = filteredEntries.subList(start, end);

        context.put("registry", pageEntries);
        context.put("filtered_entries", filteredEntries);
        if (start > 0)
        {
            context.put("prev",String.valueOf(prev));
        }
        if (end < filteredEntries.size())
        {
            context.put("next",String.valueOf(end));
        }
    }
    
    /**
     * @param rundata The turbine rundata context for this request.
     * @param portlet The portlet
     * @return The value of the start variable
     */
    private int getStart(RunData rundata, Portlet portlet)
    {
        int start = 0;
        Integer startInteger = rundata.getParameters().getInteger(START, -1);
        
        if(startInteger.intValue() == -1) {
            startInteger = (Integer) PortletSessionState.getAttribute(portlet, rundata, START);
            if(startInteger != null) {
                start = startInteger.intValue();
            }
        } else {
            PortletSessionState.setAttribute(portlet, rundata, START, startInteger);
            start = startInteger.intValue();
        }
        
        return start;
    }
    
    /**
     * Adds a filter over the available portlets list based on category
     * 
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doFilter(RunData rundata, Context context) throws Exception
    {
        String[] filterFields = rundata.getParameters().getStrings("filter_field");
        String[] filterValues = new String[filterFields.length];
        for(int i=0; i<filterFields.length; i++)
        {
            String filterField = filterFields[i];
            String filterValue = rundata.getParameters().getString(filterField + ":filter_value");
            filterValues[i] = filterValue;
        }
        
        String regName = getPortlet(context).getPortletConfig()
                                        .getInitParameter("registry",Registry.PORTLET);
        
        PortletSessionState.setAttribute(getPortlet(context), rundata, FILTER_FIELDS, filterFields);
        PortletSessionState.setAttribute(getPortlet(context), rundata, FILTER_VALUES, filterValues);
        PortletSessionState.setAttribute(rundata, PREFIX + regName + ":" + REFRESH, Boolean.TRUE);
    }
    
    
    /**
     * Method that filters the registry entries.  This should be overridden in 
     * child classes to determine what filters each browser will support.  By
     * default, this implemenation does no filtering.
     * 
     * @param entries The list of registry entries to filter.
     * @param fields The array of filter names
     * @param values The array of filter values.  This should be in a 1:1 ratio with the fitler names.
     * @return The list of filtered portlets.
     */
    protected List filter(List entries, String[] fields, String[] values) {
       return entries;
    }
}
