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
import java.util.Vector;

import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.lang.IllegalStateException;

import org.apache.jetspeed.services.Registry;


/**
 * PortletIterator - seamless iterator over nested vectors of portlet collections
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletIterator.java,v 1.3 2004/02/23 03:08:26 jford Exp $
 */
public class PortletIterator implements Iterator
{
    protected BasePortletEntry entry;
    protected String method;
    protected Vector vector ;
    protected int index = 0;    

    public PortletIterator(BasePortletEntry entry, String method)
    {
        this.entry = entry;
        this.method = method;
        this.vector = getVector();
    }

    public boolean hasNext()
    {
        int size = vector.size();

        if (size == 0)
            return false;

        if (index >= size)
        {
            entry = getParentEntry(entry);
            if (entry  == null)
                return false;
            vector = getVector();

            if (vector == null)
            {
                return false;
            }
            index = 0;
            if (vector.size() == 0)
                return false;
        }
        return true;
    }

    public void remove() throws IllegalStateException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException("The remove() method is not supported");
    }

    protected BasePortletEntry getParentEntry(BasePortletEntry entry)
    {
        String parentName = entry.getParent();
        if (parentName == null || parentName.equals(""))
            return null;

        BasePortletEntry parent = null;
        parent = (BasePortletEntry)Registry.getEntry( Registry.PORTLET, entry.getParent() );        
        return parent;
    }

    public Object next() throws NoSuchElementException
    {
        Object o = vector.elementAt(index);
        index++;
        return o;
    }

    protected Vector getVector()
    {
        try
        {
            this.vector = (Vector)this.entry.getClass().getMethod(this.method, null).invoke(this.entry, null);
        }
        catch (Exception e)
        {
            this.vector = null;
        }

        return this.vector;
    }
}

