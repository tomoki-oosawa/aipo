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

import java.util.Collection;

/**
 * Contract for implementing a search service.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean taylor</a>
 * @version $Id: SearchService.java,v 1.3 2004/02/23 03:48:47 jford Exp $
 */
public interface SearchService
{
    /**
     * Service name
     */
    public static final String SERVICE_NAME = "Search";

    /**
     * Add index entry
     * 
     * @param o
     * @return 
     */
    public boolean add(Object o);

    /**
     * Add collection of index entries
     * 
     * @param c
     * @return 
     */
    public boolean add(Collection c);

    /**
     * Remove index entry
     * 
     * @param o
     * @return 
     */
    public boolean remove(Object o);

    /**
     * Remove collection of index entries
     * 
     * @param c
     * @return 
     */
    public boolean remove(Collection c);

    /**
     * Update index entry
     * 
     * @param o
     * @return 
     */
    public boolean update(Object o);

    /**
     * Update index entries
     * 
     * @param c
     * @return 
     */
    public boolean update(Collection c);

    /**
     * Search the index
     * 
     * @param search
     * @return 
     */
    public SearchResults search(String search);
}
