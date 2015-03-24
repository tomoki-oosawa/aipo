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
 * Contract for implementing a specific parsed object.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: ParsedObject.java,v 1.4 2004/02/23 03:48:47 jford Exp $
 */
public interface ParsedObject
{

    public static final String FIELDNAME_KEY = "fieldname.key";
    public static final String FIELDNAME_KEY_DEFAULT = "Key";
    public static final String FIELDNAME_TYPE = "fieldname.type";
    public static final String FIELDNAME_TYPE_DEFAULT = "Type";
    public static final String FIELDNAME_CONTENT = "fieldname.content";
    public static final String FIELDNAME_CONTENT_DEFAULT = "Content";
    public static final String FIELDNAME_DESCRIPTION = "fieldname.description";
    public static final String FIELDNAME_DESCRIPTION_DEFAULT = "Description";
    public static final String FIELDNAME_TITLE = "fieldname.title";
    public static final String FIELDNAME_TITLE_DEFAULT = "Title";
    public static final String FIELDNAME_LANGUAGE = "fieldname.language";
    public static final String FIELDNAME_LANGUAGE_DEFAULT = "Language";
    public static final String FIELDNAME_FIELDS = "fieldname.fields";
    public static final String FIELDNAME_FIELDS_DEFAULT = "Fields";
    public static final String FIELDNAME_KEYWORDS = "fieldname.keywords";
    public static final String FIELDNAME_KEYWORDS_DEFAULT = "Keywords";
    public static final String FIELDNAME_URL = "fieldname.url";
    public static final String FIELDNAME_URL_DEFAULT = "URL";
    public static final String FIELDNAME_SCORE = "fieldname.score";
    public static final String FIELDNAME_SCORE_DEFAULT = "Score";
    public static final String FIELDNAME_CLASSNAME = "fieldname.className";
    public static final String FIELDNAME_CLASSNAME_DEFAULT = "ClassName";

    // Known object types
    public static final String OBJECT_TYPE_URL = "url";
    public static final String OBJECT_TYPE_PORTLET = "portlet";
    public static final String OBJECT_TYPE_PDF = "pdf";

    /**
     * Returns parsed object key (cannot be null)
     * 
     * @return 
     */
    public String getKey();

    /**
     * Sets parsed object key (cannot be null)
     * 
     * @param type
     */
    public void setKey(String key);

    /**
     * Returns parsed object type (cannot be null)
     * 
     * @return 
     */
    public String getType();

    /**
     * Sets parsed object type (cannot be null)
     * 
     * @param type
     */
    public void setType(String type);

    /**
     * Returns parsed object content (cannot be null)
     * 
     * @return 
     */
    public String getContent();

    /**
     * Sets parsed object content (cannot be null)
     * 
     * @param content
     */
    public void setContent(String content);

    /**
     * Returns parsed object description (cannot be null)
     * 
     * @return 
     */
    public String getDescription();

    /**
     * Sets parsed object description (cannot be null)
     * 
     * @param description
     */
    public void setDescription(String description);

    /**
     * @deprecated
     * 
     * Returns parsed object keywords
     * 
     * @return 
     */
    public String[] getKeywords();

    /**
     * @deprecated
     * 
     * Sets parsed object keywords
     * 
     * @param keywords
     */
    public void setKeywords(String[] keywords);

    /**
     * Returns parsed object title (cannot be null)
     * 
     * @return 
     */
    public String getTitle();

    /**
     * Sets parsed object title (cannot be null)
     * 
     * @param title
     */
    public void setTitle(String title);

    /**
     * Returns parsed object language  (cannot be null)
     * 
     * @return 
     */
    public String getLanguage();

    /**
     * Sets parsed object language (cannot be null)
     * 
     * @param language
     */
    public void setLanguage(String language);

    /**
     * @deprecated
     * 
     * Returns parsed object searchable fields
     * 
     * @return 
     */
    public Map getFields();

    /**
     * @deprecated
     * 
     * Sets parsed object searchable fields
     * 
     * @param fields
     */
    public void setFields(Map fields);
    
    /**
     * @return
     */
    public MultiMap getMultiFields();
    
    /**
     * @param multiFields
     */
    public void setMultiFields(MultiMap multiFields);
    
    /**
     * @return
     */
    public MultiMap getMultiKeywords();
    
    /**
     * @param multiKeywords
     */
    public void setMultiKeywords(MultiMap multiKeywords);

    /**
     * Returns parsed object URL
     * 
     * @return 
     */
    public URL getURL();

    /**
     * Sets parsed object URL
     * 
     * @param url
     */
    public void setURL(URL url);

    /**
     * Getter for property score.
     * 
     * @return Value of property score.
     */
    public float getScore();
    
    /**
     * Setter for property score.
     * 
     * @param score  New value of property score.
     */
    public void setScore(float score);
    
    /**
     * Getter for property className.
     * 
     * @return Value of property className.
     */
    public String getClassName();
    
    /**
     * Setter for property className.
     * 
     * @param className  New value of property className.
     */
    public void setClassName(String className);

}

