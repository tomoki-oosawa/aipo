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

package org.apache.jetspeed.modules.actions.portlets.security;

// velocity
import org.apache.velocity.context.Context;
import org.apache.velocity.app.FieldMethodizer;

// turbine util
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;

// jetspeed services
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.om.security.JetspeedUser;

// jetspeed velocity
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

// regexp stuff
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;

/**
 * This action sets up the template context for browsing of users in the Turbine database.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserBrowserAction.java,v 1.14 2004/02/23 02:53:08 jford Exp $
 */
public class UserBrowserAction extends VelocityPortletAction
{
    /** name of the parameter to this portlet that tells us how many rows to show per page */
    public static final String NUMBER_PER_PAGE = "number-per-page";

    /** name of the parameter that holds the page number to display */
    public static final String DISPLAY_PAGE = "ubpage";

    /** name of the parameter that holds the filter value */
    public static final String FILTER_VALUE = "filter_value";

    /** name of the parameter that holds the regexp flag */
    public static final String FILTER_REGEXP = "filter_regexp";

    /** name of the parameter that holds the filter type */
    public static final String FILTER_TYPE = "filter_type";

    /** value of the filter type parameter for searching by username */
    public static final String FILTER_TYPE_USERNAME = "filter_type_username";

    /** value of the filter type parameter for searching by last name */
    public static final String FILTER_TYPE_LASTNAME = "filter_type_lastname";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(UserBrowserAction.class.getName());     
    
    /**
     * Build the maximized state content for this portlet. (Same as normal state).
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildMaximizedContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {
        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Build the configure state content for this portlet.
     * TODO: we could configure this portlet with configurable skins, etc..
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildConfigureContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {

        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext( VelocityPortlet portlet,
                                       Context context,
                                       RunData rundata )
    {
        try
        {
            //hack to make the static variables visible in template
            context.put("s_config", new FieldMethodizer( context.get("config") ) );

            // Currently, the getUsers(filter) is not implemented - need to do local filtering
            Iterator users = JetspeedSecurity.getUsers();

            List userList = new ArrayList();

            // Is filtering requested?
            String filterValue = rundata.getParameters().getString(FILTER_VALUE);
            if (filterValue != null)
            {
                String filterType = rundata.getParameters().getString(FILTER_TYPE, FILTER_TYPE_USERNAME);
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
                        logger.warn("UserBrowserAction: error processing regular expression [" + filterValue + "]: " + 
                                 rex.toString());
                    }
                }
                while (users.hasNext())
                {
                    JetspeedUser user = (JetspeedUser) users.next();
                    String compareValue = null;
                    if (filterType.equals(FILTER_TYPE_USERNAME))
                    {
                        compareValue = user.getUserName();
                    } 
                    else if (filterType.equals(FILTER_TYPE_LASTNAME))
                    {
                        compareValue = user.getLastName();
                    }

                    if (compareValue != null)
                    {
                        if (useRE && r.match(compareValue))
                        {
                                userList.add(user);
                        }
                        else if (compareValue.startsWith(filterValue))
                        {
                            userList.add(user);
                        }
                    }
                }
            } else {
                while (users.hasNext())
                {
                    userList.add(users.next());
                }
            }


            int currentPage = rundata.getParameters().getInt(DISPLAY_PAGE, 1);

            int numberPerPage;

            try
            {
                numberPerPage = Integer.parseInt(portlet.getPortletConfig().getInitParameter(NUMBER_PER_PAGE,"50"));
            }
            catch (NumberFormatException e)
            {
                numberPerPage = 50;
            }

            if (userList.size() > numberPerPage)
            {
                int numberOfPages = (int) ((userList.size() - 1 + numberPerPage) / numberPerPage);
                int from = (currentPage - 1) * numberPerPage;
                int to = Math.min(currentPage * numberPerPage,userList.size());
                context.put(SecurityConstants.CONTEXT_USERS, userList.subList(from, to));

                //now build a set of links to access each page (assumed we will show all links)
                StringBuffer pageLinks = new StringBuffer("page: ");
                for (int i = 1; i <= numberOfPages; i++)
                {
                    if (i == currentPage)
                    {
                        pageLinks.append("( " + i + " ) &nbsp;");
                    }
                    else
                    {
//						make sure the page navigation always points to 
//						the right pane
						Object jslink = context.get("jslink");
						if (jslink instanceof JetspeedLink) {
						 pageLinks.append("[ <a href=\"" + ((JetspeedLink)jslink).getPaneByName("UserBrowser").addQueryData(DISPLAY_PAGE, new Integer(i)).toString() + "\">" + i + "</a> ] &nbsp;");
						} else {
						 pageLinks.append("[ <a href=\"./portal/" + DISPLAY_PAGE + "/" + i + "\">" + i + "</a> ] &nbsp;");
						}
                    }
                }
                context.put("pagelinks", pageLinks);
            }
            else
            {
                context.put(SecurityConstants.CONTEXT_USERS, userList);
            }
            context.put(DISPLAY_PAGE,Integer.toString(currentPage));


        }
        catch (JetspeedSecurityException e)
        {
          // log the error msg
            logger.error("Exception", e);

            rundata.setMessage("Error in Jetspeed User Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

}