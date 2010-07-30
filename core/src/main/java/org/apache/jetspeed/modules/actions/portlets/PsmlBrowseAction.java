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

//Jetspeed
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.portal.portlets.browser.DatabaseBrowserIterator;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.util.PortletConfigState;
import org.apache.jetspeed.util.PortletSessionState;

// Turbine stuff
import org.apache.turbine.util.RunData;

// Velocity Stuff
import org.apache.velocity.context.Context;

//Java
import java.util.ArrayList;
import java.util.Iterator;

// regexp stuff
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;

/**
 * This action enables to browse any of the psml info, for displaying
 * available entries and information on these entries
 *
 * @author <a href="mailto:david@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlBrowseAction.java,v 1.14 2004/02/23 02:56:58 jford Exp $
 */
public class PsmlBrowseAction extends VelocityPortletAction
{

    protected static final String PSML_REFRESH_FLAG = "psmlRefreshFlag";
    protected static final String TRUE = "true";
    protected static final String FALSE = "false";
    protected static final String PROFILE_ITERATOR = "profileIterator";
    protected static final String PAGE_SIZE = "page-size";
    protected static final String CUSTOMIZE_TEMPLATE = "customize-template";
    private static final String PEID = "js_peid";

    /** name of the parameter that holds the filter value */
    public static final String FILTER_VALUE = "filter_value";

    /** name of the parameter that holds the regexp flag */
    public static final String FILTER_REGEXP = "filter_regexp";

    /** name of the parameter that holds the filter type */
    public static final String FILTER_TYPE = "filter_type";

    /** value of the filter type parameter for searching by username */
    public static final String FILTER_TYPE_USER = "filter_type_user";

    /** value of the filter type parameter for searching by role */
    public static final String FILTER_TYPE_ROLE = "filter_type_role";

    /** value of the filter type parameter for searching by group */
    public static final String FILTER_TYPE_GROUP = "filter_type_group";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PsmlBrowseAction.class.getName());     
    
    /**
     * Subclasses should override this method if they wish to
     * provide their own customization behavior.
     * Default is to use Portal base customizer action
     */
    protected void buildConfigureContext(VelocityPortlet portlet,
                                         Context context,
                                         RunData rundata)
    {
        try
        {
            super.buildConfigureContext(portlet, context, rundata);
        }
        catch (Exception ex)
        {
            logger.error("Exception", ex);
        }
        context.put(PAGE_SIZE, PortletConfigState.getParameter(portlet, rundata, PAGE_SIZE, "20"));
        setTemplate(rundata, PortletConfigState.getParameter(portlet, rundata, CUSTOMIZE_TEMPLATE, null));
    }
    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    protected void buildNormalContext(VelocityPortlet portlet,
                                      Context context,
                                      RunData rundata)
    {
        int start = rundata.getParameters().getInt("start", 0);

        if (start < 0)
        {
            start = 0;
        }

        String pageSize = PortletConfigState.getParameter(portlet, rundata, PAGE_SIZE, "20");
        int size = Integer.parseInt(pageSize);

        int next = start + size + 1;
        int prev = start - size - 1;

        //System.out.println("start="+start+" size="+size+" next="+next+" prev="+prev);

        //check to see if resultset has changed due to PsmlUpdateAction
        //if so reconstruct the iterator and reset the flag
        
        boolean refreshFlag = (rundata.getUser().getTemp(PSML_REFRESH_FLAG, FALSE)).equals(TRUE);
        rundata.getUser().setTemp(PSML_REFRESH_FLAG, FALSE);
        
        //Get the iterator
        DatabaseBrowserIterator windowIterator =
            (DatabaseBrowserIterator) PortletSessionState.getAttribute(portlet, rundata, PROFILE_ITERATOR);
        if ((windowIterator == null) || refreshFlag)
        {
            int index = 0;
            QueryLocator ql = new QueryLocator(QueryLocator.QUERY_ALL);
            ArrayList entries = new ArrayList();
            Iterator i = Profiler.query(ql);

            // Is filtering requested?
            String filterValue = rundata.getParameters().getString(FILTER_VALUE);
            if (filterValue != null && !filterValue.trim().equalsIgnoreCase(""))
            {
                String filterType = rundata.getParameters().getString(FILTER_TYPE, FILTER_TYPE_USER);
                boolean useRE = rundata.getParameters().getBoolean(FILTER_REGEXP);
                RE r = null;
                RECompiler rc = null;
                if (useRE)
                {
                    try 
                    {
                        rc = new RECompiler();
                        r = new RE();
                        r.setProgram(rc.compile(filterValue));
                    }
                    catch (org.apache.regexp.RESyntaxException rex)
                    {
                        logger.warn("PsmlBrowseAction: error processing regular expression [" + filterValue + "]: " + 
                                 rex.toString());
                    }
                }
                try 
                {
                    while (i.hasNext())
                    {
                        Profile profile = (Profile) i.next();
                        String compareValue = null;
                        if (filterType.equals(FILTER_TYPE_USER))
                        {
                            compareValue = profile.getUserName();
                        }
                        else if (filterType.equals(FILTER_TYPE_ROLE))
                        {
                            compareValue = profile.getRoleName();                        }
                        else if (filterType.equals(FILTER_TYPE_GROUP))
                        {
                            compareValue = profile.getGroupName();                        
                        }

                        if (compareValue != null)
                        {
                            if (useRE && r.match(compareValue))
                            {
                                entries.add(profile);
                            } 
                            else if (compareValue.startsWith(filterValue))
                            {
                                entries.add(profile);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exception", e);
                }
            } 
            else
            {
                while (i.hasNext())
                {
                    Profile profile = (Profile) i.next();
                    //System.out.println("profile["+index+"]="+profile.getPath());
                    entries.add(profile);
                    index++;
                }
            }

            ArrayList entryType = new ArrayList();
            entryType.add("Profile");
            windowIterator = new DatabaseBrowserIterator(entries, entryType, entryType, size);
            PortletSessionState.setAttribute(portlet, rundata, PROFILE_ITERATOR, windowIterator);
        }
        else
        {
            windowIterator.setTop(start);
        }
        

        if (windowIterator != null)
        {
            context.put("psml", windowIterator);
            if (start > 0)
            {
                context.put("prev", String.valueOf(prev + 1));
            }
            if (next <= windowIterator.getResultSetSize())
            {
                context.put("next", String.valueOf(next - 1));
            }

        }
        else
        {
            logger.error("No Psml entries Found");
        }

    }

    /**
     * This method is called when the user configures any of the parameters.
     * @param data The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doUpdate(RunData rundata, Context context)
    {
        String pageSize = null;

        VelocityPortlet portlet = (VelocityPortlet) context.get("portlet");
        if (portlet != null)
        {
            String peid = portlet.getID();
            if ((peid != null)
                && peid.equals(rundata.getParameters().getString(PEID)))
            {
                pageSize = rundata.getParameters().getString(PAGE_SIZE);
            }
            if (pageSize != null)
            {
                PortletConfigState.setInstanceParameter(portlet, rundata, PAGE_SIZE, pageSize);
                PortletSessionState.clearAttribute(portlet, rundata, PROFILE_ITERATOR);
            }
        }

        buildNormalContext(portlet, context, rundata);
    }

    /**
     * This method is to refresh psml from disk or database.
     * @param data The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doRefresh(RunData rundata, Context context)
    {
        VelocityPortlet portlet = (VelocityPortlet) context.get("portlet");
        PortletSessionState.clearAttribute(portlet, rundata, PROFILE_ITERATOR);
        rundata.getParameters().remove(FILTER_VALUE);
        buildNormalContext(portlet, context, rundata);
    }

    /**
     * This method is to enter filtering mode.
     * @param data The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doFilter(RunData rundata, Context context)
    {
        VelocityPortlet portlet = (VelocityPortlet) context.get("portlet");
        PortletSessionState.clearAttribute(portlet, rundata, PROFILE_ITERATOR);
        buildNormalContext(portlet, context, rundata);
    }

}
