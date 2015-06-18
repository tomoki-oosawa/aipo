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

package org.apache.jetspeed.util;


import java.util.Hashtable;
import java.util.Locale;
import java.util.Enumeration;
import java.util.Map;
import org.apache.jetspeed.services.resources.JetspeedResources;
import java.io.Serializable;

/**
A class for storing MetaData about an object.

@author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
@version $Id: MetaData.java,v 1.11 2004/02/23 03:23:42 jford Exp $
*/
public class MetaData implements Serializable
{

    public static final String DEFAULT_TITLE = 
        JetspeedResources.getString(JetspeedResources.DEFAULT_TITLE_KEY);

    public static final String DEFAULT_DESCRIPTION = 
        JetspeedResources.getString(JetspeedResources.DEFAULT_DESCRIPTION_KEY);

    public static final String DEFAULT_IMAGE = 
        JetspeedResources.getString(JetspeedResources.DEFAULT_IMAGE_KEY);

    /**
    Hashtable to store all the properties
    */
    private Hashtable data = new Hashtable();

    /**
    Sets a title in the default locale
    */
    public void setTitle(String title)
    {
        setTitle(title, null);
    }

    /**
    Sets a title for the given locale
    */
    public void setTitle(String title, Locale locale)
    {
        setProperty("title", locale, title);
    }

    /**
    Returns the title for the default locale
    */
    public String getTitle()
    {
        return getTitle(null);
    }

    /**
    Returns the title for the given locale, if the title isn't defined
    in this locale or the locale, return the default title
    */
    public String getTitle(Locale locale)
    {
        String title = (String) getProperty("title", locale);

        if ((title == null) && (locale != null))
        {
            title = (String) getProperty("title", null);
        }

        if (title == null)
        {
            title = DEFAULT_TITLE;
        }

        return title;
    }

    /**
    Sets a descriptive image in the default locale
    */
    public void setImage(String image)
    {
        setImage(image, null);
    }

    /**
    Sets a descriptive image for the given locale
    */
    public void setImage(String image, Locale locale)
    {
        setProperty("image", locale, image);
    }

    /**
    Returns the descriptive image for the default locale
    */
    public String getImage()
    {
        return getImage(null);
    }

    /**
    Returns the descriptive image for the given locale, if the image isn't defined
    in this locale or the locale, return the default image
    */
    public String getImage(Locale locale)
    {
        String image = (String) getProperty("image", locale);

        if ((image == null) && (locale != null))
        {
            image = (String) getProperty("image", null);
        }

        if (image == null)
        {
            image = DEFAULT_IMAGE;
        }

        return image;
    }

    /**
    Sets a description in the default locale
    */
    public void setDescription(String description)
    {
        setDescription(description, null);
    }

    /**
    Sets a description for the given locale
    */
    public void setDescription(String description, Locale locale)
    {
        setProperty("description", locale, description);
    }

    /**
    Returns the description for the default locale
    */
    public String getDescription()
    {
        return getDescription(null);
    }

    /**
    Returns the title for the given locale, if the title isn't defined
    in this locale or the locale, return the default title
    */
    public String getDescription(Locale locale)
    {
        String desc = (String) getProperty("description", locale);

        if ((desc == null) && (locale != null))
        {
            desc = (String) getProperty ("description", null);
        }

        if (desc == null)
        {
            desc = DEFAULT_DESCRIPTION;
        }

        return desc;
    }

    /**
     Stores a property for later retrieval, uses the notation
     propertyName.localeName for locale distinction

    <p>For example, title.fr_FR will store the French title, while
    title.sp will keep the Spanich one. title will keep the value
    which can be used when defaulting because en entry is not present
    for the queried locale</p>
    */
    private void setProperty(String name, Locale locale, Object value)
    {
        if (name == null)
        {
            return;
        }

        if (locale != null)
        {
            name = name + "." + locale.toString();
        }

        if (value == null)
        {
            data.remove(name);
        }
        else
        {
            data.put(name, value);
        }
    }

    /**
    Retrieves a property by name for a given locale
    */
    private Object getProperty(String name, Locale locale)
    {
        if (name == null)
        {
            return null;
        }

        String extname = null;
        if (locale != null)
        {
            extname = name + "." + locale.toString();
        }

        Object obj = null;

        if (extname != null)
        {
            obj = data.get(extname);
        }
        if (obj == null)
        {
            obj = data.get(name);
        }

        return obj;
    }

    /**
    Retrieves a property by name for a given locale
    */
    private Map getProperties() 
    {
        return data;
    }

    /**
    Merges the properties defined in the param MetaData into the current one.
    If values are defined in both object for the same key,the one passed as 
    parameter updates the previous one
    */
    public void merge(MetaData meta)
    {
        Map map = meta.getProperties();
        Hashtable params = (Hashtable) map;
        Enumeration en = params.keys();

        while (en.hasMoreElements())
        {
            Object key = en.nextElement();
            String value = (String) params.get(key);
            
            if (value != null)
            {
                this.data.put(key, value);
            }
        }
    }
}
