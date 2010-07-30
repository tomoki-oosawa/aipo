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
/*
 * GenericMVCContext.java
 *
 * Created on January 27, 2003, 8:47 PM
 */
package org.apache.jetspeed.portal.portlets;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.velocity.context.Context;

/**
 *
 * Context that holds all the data you want to transfer to your view.  You
 * populate this during your action handling.
 * <p>
 * This context also supports chaining of additional contexts.  The initial
 * context created by this object always has priority over an addtional 
 * chained objects.  In short matching keys within the initial context
 * will "hide" matching keys within the addtional context(s).
 * </p>
 * 
 * @author  tkuebler
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 * @version $Id: GenericMVCContext.java,v 1.3 2003/02/05 00:32:13 tkuebler Exp $
 * @stereotype thing
 * 
 */

/*
 * Note:
 *
 * create the generic context interface later
 * just use Velocity's for now
 *
 */
public class GenericMVCContext implements Context
{

    private HashMap data;
    private HashSet additionalContexts;

    /** Creates a new instance of GenericMVCContext */
    public GenericMVCContext()
    {
        data = new HashMap();
        additionalContexts = new HashSet();

    }

    /**
     * Adds an existing Collection of contexts into this one.  
     * Externally added contexts are maintained indvidually
     * and are not merged into the existing context.
     * Done to facilitate the context chanining.
     * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
     */
    public GenericMVCContext(Collection contexts)
    {
        this();
        additionalContexts.addAll(contexts);
    }
    
     /**
     * Adds an existing context into this one.  
     * Externally added contexts are maintained indvidually
     * and are not merged into the existing context
     * Done to facilitate the context chanining.
     * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
     */
    public GenericMVCContext(Context context)
    {
        this();
        additionalContexts.add(context);
    }
    
   
    public boolean containsKey(java.lang.Object key)
    {
        boolean found = data.containsKey(key);
        if (!found)
        {
            Iterator itr = additionalContexts.iterator();
            while (itr.hasNext() && !found)
            {
                found = ((Context) itr.next()).containsKey(key);
            }
        }

        return found;
    }

    public Object get(java.lang.String key)
    {
        Object value = data.get(key);

        // Proceed to search chained contexts
        if (value == null)
        {
            Iterator itr = additionalContexts.iterator();
            while (itr.hasNext() && value == null)
            {
                value = ((Context) itr.next()).get(key);
            }
        }

        return value;
    }

    public Object[] getKeys()
    {
        Set keySet = data.keySet();

        Iterator itr = additionalContexts.iterator();

        while (itr.hasNext())
        {
            Object[] keys = ((Context) itr.next()).getKeys();
            for (int i = 0; i < keys.length; i++)
            {
                keySet.add(keys[i]);
            }
        }

        // (Object[])java.lang.reflect.Array.newInstance((new Object()).getClass(),2);
        return data.keySet().toArray();
    }

    public Object put(java.lang.String key, java.lang.Object value)
    {

        return data.put(key, value);
    }

    public Object remove(java.lang.Object key)
    {
        Object obj = data.remove(key);
        if (obj == null)
        {
            Iterator itr = additionalContexts.iterator();
            while (itr.hasNext() && obj == null)
            {
                obj = ((Context) itr.next()).remove(key);
            }
        }

        return obj;
    }
    
     /**
     * Add an additional context to this one
     * @param Context context Additional Context object to add.
     * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
     */
    public void addContext(Context context)
    {
        additionalContexts.add(context);
    }
    
    /**
     * This Collection is "live" as it is the same Collection
     * that maintains this Context's chained contexts.  This
     * Collection DOES NOT include objects maintained in
     * the initial context.
     * @return a Collection all the chained contexts
     * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>-
     */
    public Collection getChainedContexts()
    {
        return additionalContexts;
    }

}
