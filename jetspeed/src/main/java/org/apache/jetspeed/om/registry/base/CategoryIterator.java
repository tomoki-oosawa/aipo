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

import java.util.Iterator;
import java.util.SortedMap;
import java.util.HashMap;

import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.lang.IllegalStateException;

import org.apache.jetspeed.om.registry.*;


/**
 * CategoryIterator - iterators over category treemap/hashmap allowing dups
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: CategoryIterator.java,v 1.3 2004/02/23 03:08:26 jford Exp $
 */
public class CategoryIterator implements Iterator
{
    protected SortedMap map = null;
    protected String key;
    protected Iterator mapIterator = null;
    protected Iterator bucketIterator = null;
    protected boolean iteratingMaps = true;
    protected HashMap bucket = null;
    protected PortletEntry portlet = null;
    protected boolean findall = false;
    protected String category = "";
    protected String group = "";

    public String getCategory()
    {
        return category;
    }

    public String getGroup()
    {
        return group;
    }

    public CategoryIterator(SortedMap map, String key)
    {
        this.map = map;
        this.key = key;
        findall = (this.key == null || this.key.equals(""));
        if (findall)
            this.map = map;
        else
            this.map = map.tailMap(key);
        this.mapIterator = this.map.entrySet().iterator();
        this.bucketIterator = null;
        this.bucket = null;
    }

    private CategoryIterator() 
    {}

    public boolean hasNext()
    {
        if (iteratingMaps)
        {      
            if (mapIterator.hasNext() == false)
                return false; 
            return filter();
        }

        if (bucketIterator.hasNext())
            return getPortletEntry();

        // reached end of bucket, try next map
        if (mapIterator.hasNext())
        {
            return filter();
        }
        return false; //reached end of maps
    }

    protected boolean filter()
    {
        java.util.Map.Entry entry = (java.util.Map.Entry)mapIterator.next();
        String entryKey = (String)entry.getKey();
        int pos = entryKey.indexOf('.');
        this.category = "";
        if (-1 == pos)
        {
            this.group = entryKey;
        }
        else
        {
            this.group = entryKey.substring(0, pos);
            int length = entryKey.length();
            if (length > pos + 1)
                this.category = entryKey.substring(pos + 1, length);
        }

        if (!findall && !entryKey.startsWith(this.key))
            return false; // end of criteria

        bucket = (HashMap)entry.getValue();

        bucketIterator = bucket.entrySet().iterator();
        iteratingMaps = false;
        if (bucketIterator.hasNext() == false)
            return false;
        return getPortletEntry();
    }


    protected boolean getPortletEntry()
    {
        java.util.Map.Entry entry = (java.util.Map.Entry)bucketIterator.next();
        if (null == entry)
            return false;

        this.portlet = (PortletEntry)entry.getValue();

        return true;
    }

    public void remove() throws IllegalStateException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException("The remove() method is not supported");
    }


    public Object next() throws NoSuchElementException
    {       
        return portlet;
    }

}
