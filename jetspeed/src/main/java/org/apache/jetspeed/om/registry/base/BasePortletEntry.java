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
import java.util.Map;
import java.util.Hashtable;
import java.util.HashMap;

import org.apache.jetspeed.om.registry.*;
import org.apache.jetspeed.services.Registry;

/**
 * Default bean like implementation of the PortletEntry interface
 * suitable for serialization with Castor
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BasePortletEntry.java,v 1.5 2004/02/23 03:08:26 jford Exp $
 */
public class BasePortletEntry extends BasePortletInfoEntry
   implements PortletEntry, java.io.Serializable
{

    private String parent;

    private ContentURL url = new BaseContentURL();

    protected Vector categories = new Vector();

    private boolean application;

    private String type = PortletEntry.TYPE_ABSTRACT;

    private boolean isRef = true;

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BasePortletEntry obj = (BasePortletEntry)object;

        if (application!=obj.isApplication())
        {
            return false;
        }

        if (parent!=null)
        {
            if (!parent.equals(obj.getParent()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getParent()!=null)
            {
                return false;
            }
        }

        if (type!=null)
        {
            if (!type.equals(obj.getType()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getType()!=null)
            {
                return false;
            }
        }

        if (url!=null)
        {
            if (!url.equals(obj.getContentURL()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getContentURL()!=null)
            {
                return false;
            }
        }

        Iterator i = categories.iterator();
        Iterator i2 = obj.getCategories().iterator();
        while(i.hasNext())
        {
            BaseCategory c1 = (BaseCategory)i.next();
            BaseCategory c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseCategory)i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return super.equals(object);
    }

    /** @return the URL associated with this portlet or null */
    public String getURL()
    {
        return this.url.getURL();
    }

    /**
     * Sets the URL for this PortletEntry
     * @param url the new PortletEntry URL
     */
    public void setURL( String url )
    {
        this.url.setURL(url);
    }

    public boolean isCachedOnURL()
    {
        return url.isCacheKey();
    }

    public void setCachedOnURL(boolean cache)
    {
        url.setCachedOnURL(cache);
    }

    public ContentURL getURLEntry()
    {
        return url;
    }

    /** @return the entry name from which this one is derived */
    public String getParent()
    {
        return this.parent;
    }

    /** @return the classname associated to this entry */
    public String getClassname()
    {
        if (isRef && (classname == null) )
        {
            return getParentEntry().getClassname();
        }

        return classname;
    }


    /**
     * Sets the ancestor for this PortletEntry.
     * @param parent the new ancestor entry name. This name should
     * be defined in the system registry
     */
    public void setParent( String parent )
    {
        this.parent = parent;
    }

    /** @return true is this entry is only accessible by the
      * portal administrators.
      */
    public boolean isAdmin()
    {
        if (getSecurity()!=null)
        {
            return "admin".equals(getSecurity().getRole());
        }

        return false;
    }

    /** @return true is the PortletEntry is marked as an application */
    public boolean isApplication()
    {
        return this.application;
    }

    /** Sets the application status of this portlet entry. If an entry
     *  is maked as application, the associated portlet will only be displayed
     *  in Maximized mode and can be retrieved specifically
     *
     *  @param application the new application status
     */
    public void setApplication( boolean application )
    {
        this.application = application;
    }

    /** @return the type of this entry */
    public String getType()
    {
        return this.type;
    }

    /** Sets the type of this entry. The type specifies whether it is
     *  abstract, instance or ref
     *
     *  @param type the new type for the PortletEntry
     */
    public void setType( String type )
    {
        this.isRef = PortletEntry.TYPE_REF.equals(type);
        this.type = type;
    }

    /** This method is used by the Castor persistence system to retrieve
     *  the application status
     *
     * @see PortletEntry#isApplication
     * @return the application status of this entry
     */
    public boolean getApplication()
    {
        return this.application;
    }

    public String getTitle()
    {
        String title = super.getTitle();
        if (title != null)
            return title;
        if (isRef)
        {
           return getParentEntry().getTitle();
        }
        return null;
    }

    public String getDescription()
    {
        String desc = super.getDescription();
        if (desc != null)
            return desc;

        if (isRef)
        {
            return getParentEntry().getDescription();
        }
        return null;
    }

    /** Looks up in the Registry the parent entry for this real entry */
    public PortletEntry getParentEntry()
    {
        PortletEntry parent = null;
        parent = (PortletEntry)Registry.getEntry( Registry.PORTLET, getParent() );
        if (parent == null)
        {
            parent = new BasePortletEntry();
            parent.setName(getParent());
            parent.setType(PortletEntry.TYPE_ABSTRACT);
        }
        return parent;
    }

    public MetaInfo getMetaInfo()
    {
        MetaInfo meta = super.getMetaInfo();
        if (meta == null)
        {
            return getParentEntry().getMetaInfo();
        }
        return meta;
    }

    /** @return an enumeration of this entry parameter names */
    public Iterator getParameterNames()
    {
        if (isRef)
        {
            Hashtable hash = new Hashtable();
            Iterator i = super.getParameterNames();
            while(i.hasNext())
            {
                hash.put(i.next(),"1");
            }
            i = getParentEntry().getParameterNames();
            while(i.hasNext())
            {
                hash.put(i.next(),"1");
            }

            return hash.keySet().iterator();
        }

        return super.getParameterNames();
    }

    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public Parameter getParameter( String name )
    {
        Parameter p = super.getParameter(name);
        if (isRef && p == null)
        {
            return getParentEntry().getParameter(name);
        }
        return p;
    }

    public CachedParameter getCachedParameter( String name )
    {
        Parameter p = getParameter(name);
        return (CachedParameter)p;
    }

    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getParameterMap()
    {
        Hashtable params = (Hashtable)super.getParameterMap();

        if (isRef)
        {
            Map map = getParentEntry().getParameterMap();
            map.putAll(params);
            return map;
        }

        return params;
    }

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listMediaTypes()
    {
        if (isRef)
        {
            Map types = new HashMap();

            Iterator i = super.listMediaTypes();
            while(i.hasNext())
            {
                types.put(i.next(),"1");
            }

            i = getParentEntry().listMediaTypes();
            while(i.hasNext())
            {
                types.put(i.next(),"1");
            }

            return types.keySet().iterator();
        }

        return super.listMediaTypes();
    }

    /**
     * Test if a given media type is supported by this entry.
     * The test is done by a case sensitive name comparison
     *
     * @param name the media type name to test for.
     * @return true is the media type is supported false otherwise
     */
    public boolean hasMediaType(String name)
    {
        if (isRef)
        {
            return super.hasMediaType(name) || getParentEntry().hasMediaType(name);
        }

        return super.hasMediaType(name);
    }

    /** @return the URL associated with this portlet or null */
    public BaseContentURL getContentURL()
    {
        return (BaseContentURL)this.url;
    }

    /**
     * Sets the URL for this PortletEntry
     * @param url the new PortletEntry URL
     */
    public void setContentURL( BaseContentURL url )
    {
        this.url = url;
    }

    /*
     * Categories
     */
    public Vector getCategories()
    {
        return this.categories;
    }

    public void setCategories(Vector v)
    {
        this.categories = v;
    }

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listCategories()
    {
        return new PortletIterator(this, "getCategories");
    }

    /**
     * Test if a given category exists for this entry
     *
     * @param name the category name
     * @return true is the category exists in the default group
     */
    public boolean hasCategory(String name)
    {
        return hasCategory(name, PortletEntry.DEFAULT_GROUP);
    }

    /**
     * Test if a given category exists for this entry, in the specified group of categories.
     *
     * @param name the category name
     * @param group the category group
     * @return true is the category exists in the specified group
     */
    public boolean hasCategory(String name, String group)
    {
        Iterator it = listCategories();
        while (it.hasNext())
        {
            Category cat = (Category)it.next();
            if (cat.getName().equals(name) && cat.getGroup().equals(group))
                return true;
        }
        return false;
    }


    /**
     * Add a new category to this portlet entry in the default group.
     *
     * @param name the category name
     */
    public void addCategory(String name)
    {
        addCategory(name, PortletEntry.DEFAULT_GROUP);
    }

    /**
     * Add a new category to this portlet entry.
     *
     * @param name the category name
     * @param group the category group name
     */
    public void addCategory(String name, String group)
    {
        if (!hasCategory(name, group))
        {
            Category cat = new BaseCategory();
            cat.setName(name);
            cat.setGroup(group);
            categories.add(cat);
        }
    }

    /**
     * Remove a category from this portlet entry in the default group.
     *
     * @param name the category name
     */
    public void removeCategory(String name)
    {
        removeCategory(name, PortletEntry.DEFAULT_GROUP);
    }

    /**
     * Remove a category from this portlet entry in the specified group.
     *
     * @param name the media type name to remove.
     * @param group the category group name
     */
    public void removeCategory(String name, String group)
    {
        for (int ix = 0; ix < categories.size(); ix++)
        {
            Category cat = (Category)categories.elementAt(ix);
            if (cat.getName().equals(name) && cat.getGroup().equals(group))
            {
                categories.remove(ix);
                return;
            }
        }
    }
}


