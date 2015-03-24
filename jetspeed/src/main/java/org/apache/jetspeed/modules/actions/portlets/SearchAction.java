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
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.services.search.Search;
import org.apache.jetspeed.services.search.ParsedObject;
import org.apache.jetspeed.services.search.SearchResults;

/**
 * Search Actions built here
 *
 * @author <a href="mailto:taylor@apache.org">David Sean taylor</a>
 * @version $Id: SearchAction.java,v 1.3 2004/02/23 02:56:58 jford Exp $
 */

public class SearchAction extends GenericMVCAction
{
    public final static String SEARCH_STRING = "search";
    public final static String SEARCH_RESULTS = "search_results";    
    public final static String SEARCH_RESULTSIZE = "search_resultsize";    
    
    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    public void buildNormalContext(Portlet portlet, 
                                   Context context, 
                                   RunData rundata)
    throws Exception
    {     
        List results = (List)rundata.getRequest().getAttribute(SEARCH_RESULTS);           
        if (null == results)
        {
            results = (List)PortletSessionState.getAttribute(rundata, SEARCH_RESULTS);
            //System.out.println("session results = " + results); 
            
        }
        else
        {
            //System.out.println("qp results = " + results);
            PortletSessionState.setAttribute(rundata, SEARCH_RESULTS, results);
        }
        
        if (null != results)
        {
            context.put(SEARCH_RESULTS, results);
        }
        
        String searchString = (String)rundata.getParameters().getString(SEARCH_STRING);
        if (searchString == null || searchString.trim().length() == 0)
        {
            searchString = (String)PortletSessionState.getAttribute(rundata, SEARCH_STRING);
        }
        else
        {
            PortletSessionState.setAttribute(rundata, SEARCH_STRING, searchString);
        }
        
        context.put(SEARCH_STRING, searchString);
        
        if (results != null)
        {
            //System.out.println("size = " + results.size());
            context.put(SEARCH_RESULTSIZE, new Integer(results.size()));
        }
    }

    public void doSearch(RunData rundata, Context context)
    {
        // get posted new target
        String searchString = (String)rundata.getParameters().getString(SEARCH_STRING);
        
        if (searchString == null || searchString.trim().length() == 0)
        {
            return;
        }
        
        //
        // execute the query
        //
        ParsedObject result = null;
        SearchResults results  = Search.search(searchString);
        //System.out.println("Query hits = " + results.size());
        rundata.getRequest().setAttribute(SEARCH_RESULTS, results.getResults());                
    }

}

