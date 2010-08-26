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

// Turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.Comparable;
import org.apache.turbine.util.QuickSort;

// Jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.modules.actions.portlets.JspPortletAction;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.webservices.finance.stockmarket.StockQuoteService;
import org.apache.jetspeed.webservices.finance.stockmarket.StockQuote;
import org.apache.jetspeed.util.PortletConfigState;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.StringUtils;

/**
 * This action sets up the template context for retrieving stock quotes.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JspStockQuoteAction.java,v 1.3 2004/02/23 02:56:58 jford Exp $ 
 */

public class JspStockQuoteAction extends JspPortletAction implements Comparable
{
    private static final String SYMBOLS = "symbols";
    private static final String COLUMNS = "columns";
    private static final String QUOTES = "quotes";
    private static final String SORT = "sort";
    private static final String SELECTED_COLUMNS = "selected-columns";
    private static final String[] ALL_COLUMNS = {"Symbol","Price","Change","Volume"};
    private String sort = null;

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JspStockQuoteAction.class.getName());     
    
    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext(Portlet portlet, RunData rundata)
    {

        // We always fetch the most current quotes so might as well call refresh from here
        this.doRefresh(rundata, portlet);
    }

    /**
     * Sort the quotes.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    public void doSort(RunData rundata, Portlet portlet)
    {
        // We always fetch the most current quotes so might as well call refresh from here
        this.doRefresh(rundata, portlet);
        logger.info("JspStockQuoteAction: sorting...");
    }

    /**
     * Refresh the portlet content.
     *
     * @param portlet The jsp-based portlet that is being built.
     * @param rundata The turbine rundata context for this request.
     */
    public void doRefresh(RunData rundata, Portlet portlet)
    {
        try
        {
            // Get reference to stock quote web service
            StockQuoteService service = (StockQuoteService) TurbineServices.getInstance().
                getService(StockQuoteService.SERVICE_NAME);

            // Retrieve portlet parameters
            String symbols = (String) PortletSessionState.getAttributeWithFallback(portlet, rundata, SYMBOLS);

            this.sort = (String) PortletSessionState.getAttributeWithFallback(portlet, rundata, SORT);
            if (this.sort != null)
            {
                PortletSessionState.setAttribute(portlet, rundata, SORT, sort);
                rundata.getRequest().setAttribute(SORT, sort);
            }

            String columns = PortletConfigState.getParameter(portlet, rundata, COLUMNS, 
                                                             StringUtils.arrayToString(ALL_COLUMNS, ","));
            String[] selectedColumnsArray = StringUtils.stringToArray(columns, ",");


            // Request stock quote(s) from the stock quote web service
            String[] symbolArray = StringUtils.stringToArray(symbols, ",");
            StockQuote[] quotes = service.fullQuotes(symbolArray);

            // Sort the entries
            if (this.sort != null)
            {
                QuickSort.quickSort(quotes, 0, quotes.length - 1, this);
                rundata.getRequest().setAttribute(SORT, this.sort);
            }

            // Place appropriate objects in jsp context
            rundata.getRequest().setAttribute(QUOTES, quotes);
            rundata.getRequest().setAttribute(COLUMNS, selectedColumnsArray);
            rundata.getRequest().setAttribute(SELECTED_COLUMNS, columns);

            logger.info("JspStockQuoteAction: refreshing...");
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    /**
     * Compare to another <code>StockQuote</code>.  Used by the
     * <code>QuickSort</code> class to determine sort order.
     * 
     * @param entry1 The first <code>StockQuoteEntry</code> object.
     * @param entry2 The second <code>StockQuoteEntry</code> object.
     * @return An <code>int</code> indicating the result of the comparison.
     */
    public int compare(Object entry1, Object entry2)
    {
        if (this.sort.equalsIgnoreCase("price"))
        {
            Float entrycol1 = new Float(((StockQuote) entry1).getPrice());
            Float entrycol2 = new Float(((StockQuote) entry2).getPrice());
            return entrycol1.compareTo(entrycol2);
        }
        else if (this.sort.equalsIgnoreCase("symbol"))
        {
            String entrycol1 = ((StockQuote) entry1).getSymbol();
            String entrycol2 = ((StockQuote) entry2).getSymbol();
            return entrycol1.compareTo(entrycol2);
        }
        else if (this.sort.equalsIgnoreCase("change"))
        {
            Double entrycol1 = new Double(((StockQuote) entry1).getChange());
            Double entrycol2 = new Double(((StockQuote) entry2).getChange());
            return entrycol1.compareTo(entrycol2);
        }
        else
        {
            Long entrycol1 = new Long(((StockQuote) entry1).getVolume());
            Long entrycol2 = new Long(((StockQuote) entry2).getVolume());
            return entrycol1.compareTo(entrycol2);
        }

    }

}

