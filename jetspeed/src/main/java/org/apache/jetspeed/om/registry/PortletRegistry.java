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

package org.apache.jetspeed.om.registry;

import java.util.Iterator;

/**
    Represents a portlet registry.

    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: PortletRegistry.java,v 1.8 2004/02/23 03:11:39 jford Exp $
*/

public interface PortletRegistry extends Registry
{

    /*
     * List all portlets in this registry, sorted by category
     *
     * @return Iterator The result as an iterator.
     */
    public Iterator listByCategory();

    /*
     * Find portlets in this registry, looking up by category in the default category group.
     *
     * @param category The category and optional subcategories.
     * @return Iterator The result as an iterator.
     */
    public Iterator findPortletsByCategory(String category);

    /*
     * Find portlets in this registry, looking up by category and category group.
     *
     * @param group The group to search for categories in.
     * @param category The category and optional subcategories.
     * @return Iterator The result as an iterator.
     */
    public Iterator findPortletsByGroupCategory(String group, String category);

    /*
     * Returns the category key for a given Portlet Registry entry's category object.
     * This key is used to look up entries in the Portlet Registry by category.
     *
     * @param category The registry portlet entry category sub-object.
     * @return String The category key for looking up this entry by category.
     */
    public String getCategoryKey( Category category );

}


