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

import java.util.Map;
import java.net.URL;

import org.apache.commons.collections.MultiMap;

/**
 * Base parsed object.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: BaseParsedObject.java,v 1.4 2004/02/23 03:48:47 jford Exp $
 */
public class BaseParsedObject implements ParsedObject
{

    private String key;
    private String type;
    private String title;
    private String description;
    private String content;
    private String language;
    private URL url;
    private String[] keywords;
    private MultiMap multiKeywords;
    private Map fields;
    private MultiMap multiFields;
    private float score;
    private String className;

    /**
     * Returns parsed object key
     * 
     * @return 
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Sets parsed object key
     * 
     * @param content
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Returns parsed object type
     * 
     * @return 
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Sets parsed object type
     * 
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns parsed object content
     * 
     * @return 
     */
    public String getContent()
    {
        return this.content;
    }

    /**
     * Sets parsed object content
     * 
     * @param content
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    /**
     * Returns parsed object description
     * 
     * @return 
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Sets parsed object description
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns parsed object keywords
     * 
     * @return 
     */
    public String[] getKeywords()
    {
        return this.keywords;
    }

    /**
     * Sets parsed object keywords
     * 
     * @param keywords
     */
    public void setKeywords(String[] keywords)
    {
        this.keywords = keywords;
    }

    /**
     * Returns parsed object title
     * 
     * @return 
     */
    public String getTitle() 
    {
        return this.title;
    }

    /**
     * Sets parsed object title
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns parsed object language
     * 
     * @return 
     */
    public String getLanguage()
    {
        return this.language;
    }

    /**
     * Sets parsed object language
     * 
     * @param language
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }

    /**
     * Returns parsed object searchable fields
     * 
     * @return 
     */
    public Map getFields()
    {
        return this.fields;
    }

    /**
     * Sets parsed object searchable fields
     * 
     * @param fields
     */
    public void setFields(Map fields)
    {
        this.fields = fields;
    }

    /**
     * Returns parsed object URL
     * 
     * @return 
     */
    public URL getURL()
    {
        return this.url;
    }

    /**
     * Sets parsed object URL
     * 
     * @param fields
     */
    public void setURL(URL url)
    {
        this.url = url;
    }

    /**
     * Getter for property score.
     * 
     * @return Value of property score.
     */
    public float getScore()
    {
        return this.score;
    }
    
    /**
     * Setter for property score.
     * 
     * @param score  New value of property score.
     */
    public void setScore(float score)
    {
        this.score = score;
    }

    /**
     * Getter for property className.
     * 
     * @return Value of property className.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Setter for property className.
     * 
     * @param score  New value of property className.
     */
    public void setClassName(String className)
    {
       this.className = className;        
    }

    /** 
     * @see org.apache.jetspeed.services.search.ParsedObject#getMultiFields()
     */
    public MultiMap getMultiFields()
    {
        return multiFields;
    }

    /** 
     * @see org.apache.jetspeed.services.search.ParsedObject#setMultiFields(org.apache.commons.collections.MultiMap)
     */
    public void setMultiFields(MultiMap multiFields)
    {
       this.multiFields = multiFields;
        
    }

    /** 
     * @see org.apache.jetspeed.services.search.ParsedObject#getMultiKeywords()
     */
    public MultiMap getMultiKeywords()
    {
        return multiKeywords;
    }

    /**
     * @see org.apache.jetspeed.services.search.ParsedObject#setMultiKeywords(org.apache.commons.collections.MultiMap)
     */
    public void setMultiKeywords(MultiMap multiKeywords)
    {
        this.multiKeywords = multiKeywords;        
    }

}

