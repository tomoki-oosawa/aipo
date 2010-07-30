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

package org.apache.jetspeed.modules.actions.portlets.browser;

import java.util.List;

import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * Browser Query Fetch Interface
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BrowserQuery.java,v 1.9 2004/02/23 02:51:19 jford Exp $
 *
*/
public interface BrowserQuery
{

    /**
     * This method returns the query to be executed to get the results which will
     * be opened in the browser.
     *
     */
    public String getQueryString(RunData rundata, Context context);

    /**
     * Filter the row programmatically on a query.
     * By returning true, instruct the database browser to filter the row.
     * By returning false, instruct the database browser to keep the row.
     * (Filtering means removing the row from the final result set).
     *
     * @param row The row being inspected for filtration.
     * @return True to filter the row, false to keep it.
     */
    public boolean filter(List row, RunData rundata);

    /*
     * Set a list of JDBC query parameters.
     * All members of this list must be java objects (not primitives)
     * Should be called from derived classes.
     *
     */
    public void setSQLParameters(List parameters);

    public List getSQLParameters();


}
