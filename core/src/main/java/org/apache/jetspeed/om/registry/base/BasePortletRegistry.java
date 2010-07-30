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

package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.PortletRegistry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.InvalidEntryException;
import org.apache.jetspeed.om.registry.Category;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Provides base functionality within a Portlet Registry.
 *
 * <p>To avoid loops, a RegistryService implementation using this class
 * nees to call the addLocalEntry/removeLocalEntry methods to modify
 * the in memory state of this Registry</p>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BasePortletRegistry.java,v 1.7 2004/02/23 03:08:26 jford Exp $
 */
public class BasePortletRegistry extends BaseRegistry implements PortletRegistry
{

    private Map catMap = new TreeMap();

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BasePortletRegistry.class.getName());    
    
    /**
    @see Registry#setEntry
    */
    public void setEntry( RegistryEntry entry ) throws InvalidEntryException
    {
		// Delegate to the RegistryService to ensure correct handling of
		// persistence if using file fragments

		try
		{
			Registry.addEntry(Registry.PORTLET, entry);
		}
		catch (RegistryException e)
		{
			logger.error("Exception", e);
		}
    }

    /**
    @see Registry#addEntry
    */
    public void addEntry( RegistryEntry entry ) throws InvalidEntryException
    {
		// Delegate to the RegistryService to ensure correct handling of
		// persistence if using file fragments

		try
		{
			Registry.addEntry(Registry.PORTLET, entry);
		}
		catch (RegistryException e)
		{
			logger.error("Exception", e);
		}
    }

    /**
    @see Registry#removeEntry
    */
    public void removeEntry( String name )
    {
		// Delegate to the RegistryService to ensure correct handling of
		// persistence if using file fragments

		Registry.removeEntry(Registry.PORTLET, name);
    }

    /**
    @see Registry#removeEntry
    */
    public void removeEntry( RegistryEntry entry )
    {
		// Delegate to the RegistryService to ensure correct handling of
		// persistence if using file fragments

		if (entry != null)
		{
			Registry.removeEntry(Registry.PORTLET, entry.getName());
		}
    }

    protected void setPortletEntry(PortletEntry entry) throws InvalidEntryException
    {
        synchronized (catMap)
        {
            int count = 0;
            Iterator it = ((PortletEntry)entry).listCategories();
            while (it.hasNext())
            {
                Category category = (Category)it.next();
                String key = getCategoryKey(category);
                HashMap bucket = (HashMap)this.catMap.get(key);
                if (null == bucket)
                {
                    bucket = new HashMap();
                    bucket.put(entry.getName(), entry);
                    this.catMap.put(key, bucket);
                }
                else
                {
                    bucket.put(entry.getName(), entry);
                }
                count++;
            }
            /*
            TODO: this feature could be optional
            */
            if (0 == count)
            {
                StringBuffer key = new StringBuffer(128);
                key.append(PortletEntry.DEFAULT_GROUP);
                key.append(".");
                if (entry.getType().equals(PortletEntry.TYPE_ABSTRACT))
                    key.append(PortletEntry.DEFAULT_CATEGORY_ABSTRACT);
                else
                    key.append(PortletEntry.DEFAULT_CATEGORY_REF);

                HashMap bucket = (HashMap)this.catMap.get(key.toString());
                if (null == bucket)
                {
                    bucket = new HashMap();
                    bucket.put(entry.getName(), entry);
                    this.catMap.put(key.toString(), bucket);
                }
                else
                {
                    bucket.put(entry.getName(), entry);
                }
            }

        }
    }


    public String getCategoryKey(Category category)
    {
        if (category == null)
        {
            return PortletEntry.DEFAULT_GROUP;
		}

        String categoryName = category.getName();

        if ((categoryName == null) || categoryName.equals(""))
        {
            return category.getGroup();
		}

        return category.getGroup() + "." + categoryName;
    }


    /*
     * Find portlets in this registry, looking up by category in the default category group.
     *
     * @param category The category and optional subcategories.
     * @return Iterator The result as an iterator.
     */
    public Iterator findPortletsByCategory(String category)
    {
    	String key;

        if ((category == null) || category.equals(""))
        {
        	key = PortletEntry.DEFAULT_GROUP;
	    }
        else
        {
            key = PortletEntry.DEFAULT_GROUP + "." + category;
		}

        CategoryIterator iterator = new CategoryIterator((SortedMap)catMap, key);

        return iterator;
    }

    /*
     * Find portlets in this registry, looking up by category and category group.
     *
     * @param group The group to search for categories in.
     * @param category The category and optional subcategories.
     * @return Iterator The result as an iterator.
     */
    public Iterator findPortletsByGroupCategory(String group, String category)
    {
        if ((group == null) || group.equals(""))
        {
            group = PortletEntry.DEFAULT_GROUP;
		}

        String key = group + "." + category;

        CategoryIterator iterator = new CategoryIterator((SortedMap)catMap, key);

        return iterator;
    }

    /*
     * List all portlets in this registry, sorted by category
     *
     * @return Iterator The result as an iterator.
     */
    public Iterator listByCategory()
    {
        CategoryIterator iterator = new CategoryIterator((SortedMap)catMap, null);
        return iterator;
    }

    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry()
    {
		return new BasePortletEntry();
	}

    /**
    @see Registry#setEntry
    */
    public void setLocalEntry( RegistryEntry entry ) throws InvalidEntryException
    {
        super.setLocalEntry(entry);
        setPortletEntry((PortletEntry)entry);
    }

    /**
    @see Registry#addEntry
    */
    public void addLocalEntry( RegistryEntry entry ) throws InvalidEntryException
    {
        super.addLocalEntry(entry);
        setPortletEntry((PortletEntry)entry);
    }

    /**
    @see Registry#removeEntry
    */
    public void removeLocalEntry( String name )
    {
        if (name == null)
        {
            return;
		}

        RegistryEntry entry = (RegistryEntry)this.entries.get( name ) ;

        if (entry == null)
        {
            return;
		}

        removeLocalEntry(entry);
    }

    /**
    @see Registry#removeEntry
    */
    public void removeLocalEntry( RegistryEntry entry )
    {
        synchronized(catMap)
        {
            int count = 0;
            Iterator it = ((PortletEntry)entry).listCategories();
            while (it.hasNext())
            {
                Category category = (Category)it.next();
                HashMap map = (HashMap)catMap.get(getCategoryKey(category));
                if (map != null)
                {
                    map.remove(entry.getName());
                    if (0 == map.size())
                    {
                        catMap.remove(getCategoryKey(category));
                    }
                }
            }
        }
        super.removeLocalEntry(entry);
    }

}