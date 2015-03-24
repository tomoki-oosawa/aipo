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

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// Turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;
// Velocity Stuff
import org.apache.velocity.context.Context;

//import org.apache.jetspeed.util.StringUtils;
import org.apache.jetspeed.webservices.finance.stockmarket.StockQuoteService;
import org.apache.jetspeed.webservices.finance.stockmarket.StockQuote;
import org.apache.jetspeed.services.resources.JetspeedResources;

import org.apache.jetspeed.util.PortletConfigState;
import org.apache.jetspeed.util.StringUtils;

/**
 * This action sets up the template context for retrieving stock quotes.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 */

public class StockQuoteAction extends VelocityPortletAction
{
    private static final String SYMBOLS = "symbols";
    private static final String COLUMNS = "columns";
    private static final String QUOTES = "quotes";
    private static final String[] ALL_COLUMNS = {"Symbol","Price","Change","Volume"};
    private static final String SELECTED_COLUMNS = "selected-columns";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(StockQuoteAction.class.getName());     
    
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

        setTemplate(rundata, "stock-quote-customize");


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
            // Get reference to stock quote web service
            StockQuoteService service = (StockQuoteService) TurbineServices.getInstance().
                getService(StockQuoteService.SERVICE_NAME);

            // Retrieve portlet parameters
            String symbols = PortletConfigState.getParameter(portlet, rundata, SYMBOLS, "IBM,MSFT,ORCL,SUNW");
            String columns = PortletConfigState.getParameter(portlet, rundata, COLUMNS, 
                                                             StringUtils.arrayToString(ALL_COLUMNS, ","));
            String[] selectedColumnsArray = StringUtils.stringToArray(columns, ",");

            // Request stock quote(s) from the stock quote web service
            String[] symbolArray = StringUtils.stringToArray(symbols, ",");
            StockQuote[] quotes = service.fullQuotes(symbolArray);

            // Place appropriate objects in Velocity context
            context.put(QUOTES, quotes);
            context.put(SELECTED_COLUMNS, selectedColumnsArray);
            context.put(COLUMNS, columns);
        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Exception", e);

            rundata.setMessage("Error in Jetspeed Stock Quotes: " + e.toString());
            rundata.setStackTrace(org.apache.turbine.util.StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }



}

