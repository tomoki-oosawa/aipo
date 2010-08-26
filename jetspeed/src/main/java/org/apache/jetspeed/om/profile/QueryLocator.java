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

package org.apache.jetspeed.om.profile;

public class QueryLocator extends BaseProfileLocator
{

    public static final int  QUERY_USER = 1;
    public static final int QUERY_ROLE = 2;
    public static final int QUERY_GROUP = 4;
    public static final int QUERY_ANON = 8;
    public static final int QUERY_ALL = 15;

    private int qm = QUERY_USER;
    private String query ;

    public QueryLocator( int qm )
    {
        super();
        this.qm = qm;
    }

    public int getQueryMode()
    {
        return qm;
    }

    public void setQueryMode( int qm)
    {
        this.qm = qm;
    }

    public String getQueryString()
    {
        return query;
    }

    public void setQueryString(String query)
    {
        this.query = query;
    }


}
