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

package org.apache.jetspeed.services.search;

// Java Imports
import java.io.File;
import java.util.Collection;

// Turbine imports
import org.apache.turbine.services.TurbineServices;


/**
 * Static accessor for the SearchService
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: Search.java,v 1.5 2004/02/23 03:48:47 jford Exp $
 */
public abstract class Search
{

    /**
     * Utility method for accessing the service 
     * implementation
     *
     * @return a SearchServiceimplementation instance
     */
    protected static SearchService getService()
    {
        return (SearchService) TurbineServices
            .getInstance().getService(SearchService.SERVICE_NAME);
    }

    /**
     * Search the default index
     *
     * @param searchString to use
     * @return Hits
     */    
    public static SearchResults search(String searchString)
    {
        return getService().search(searchString);
    }

    /**
     * 
     * @param o
     * @return 
     */
    public static boolean remove(Object o)
    {
        return getService().remove(o);
    }

    /**
     * 
     * @param c
     * @return 
     */
    public static boolean remove(Collection c)
    {
        return getService().remove(c);
    }

    /**
     * 
     * @param o
     * @return 
     */
    public static boolean add(Object o)
    {
        return getService().add(o);
    }

    /**
     * 
     * @param c
     * @return 
     */
    public static boolean add(Collection c)
    {
        return getService().add(c);
    }
    
    /**
     * @param o
     * @return
     */
    public static boolean update(Object o)
    {
        return getService().update(o);
    }
    
    /**
     * @param c
     * @return
     */
    public static boolean update(Collection c)
    {
        return getService().update(c);
    }

    /**
     * 
     * @param path
     * @param extension
     * @return 
     */
    public boolean addDirectory(String path, String extension)
    {
        File directory = new File(path); 
        File[] files = directory.listFiles();
        for (int ix=0; ix < files.length; ix++)
        {
            if (files[ix].isDirectory())
            {
                continue;
            }

            // TODO: subdirectories
        }
        return true;                        
    }

}
