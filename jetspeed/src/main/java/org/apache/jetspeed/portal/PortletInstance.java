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

package org.apache.jetspeed.portal;

import java.util.Iterator;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.portal.Portlet;

/**
 * This interface provides an easy, object-oriented approach to modifing 
 * a portlet instance's persistent attributes.
 * It provides methods for getting, setting and removing attributes from 
 * portlet instance persistence storage. 
 *
 * In a Jetspeed 1.4x PSML profile, the default XML format for an instance and attribute is:
 *
 * <entry>
 *    <parameter name="someName" value="someValue"/>
 *
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a> 
 * @version $Id: PortletInstance.java,v 1.3 2004/02/23 04:05:35 jford Exp $
 */
public interface PortletInstance
{
    /**
     * Retrieves an attributes value associated with a named object from a
     * portlet instance's persistence storage. 
     *
     * @param name The name of the attribute
     * @param defaultValue The default value if the attribute is not found.
     * @return String The attribute value, or the defaultValue if not found.
     */
    String getAttribute(String name, String defaultValue);
    
    /**
     * Retrieves an attributes value associated with a named object from a
     * portlet instance's persistence storage. 
     *
     * @param name The name of the attribute
     * @return String The attribute value, or the empty string if not found.
     */
    String getAttribute(String name);


    /**
     * Sets a portlet instance attribute into persistence storage.
     *
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    void setAttribute(String name, String value);
    
    /**
     * Removes a portlet instance attribute from persistence storage.
     *
     * @param name The name of the attribute.
     */
    void removeAttribute(String name);
    
    /**
     * Removes all portlet instance attributes from persistence storage.
     *
     */
    void removeAllAttributes();
    
    /**
     * Retrieves a list of all of the attributes of this portlet instance
     *  as <code>org.apache.jetspeed.om.profile.Parameter</code> objects.
     *
     * @return java.util.Iterator
     */
    Iterator getAttributes();
    
    /**
     * Retrieves a list of all attributes names for all the attributes
     * contained within this portlet instance.
     *
     * @return java.util.Iterator
     */
    Iterator getAttributeNames();
    
    /**
     * Returns the PSMLDocument that contains this portlet instance.
     *
     * @return org.apache.jetspeed.om.profile.PSMLDocument
     * 
     */
    PSMLDocument getDocument();
    
    /**
     * Returns the Profile instance containing this portlet instance.  
     *
     * @return org.apache.jetspeed.om.profile.Profile
     */
    Profile getProfile();
    
    /**
     * Returns the PSML OM instance associated with this instance.
     *
     * @return org.apache.jetspeed.om.profile.Entry
     */
    Entry getEntry();    
    
     /**
      * Returns the parent portlet of this PortletInstance.
      *
      * @return org.apache.jetspeed.portal.Portlet 
      */
    Portlet getPortlet();    
    
    /**
     * Retrieves the name of the parent portlet as it is defined within the portlet registry.
     *
     * @return String The name of the portlet.
     */
    String getName();
    
    /**
     * Retrieves the title (if it is defined) of the parent portlet as it is defined 
     * within the portlet registry.
     *
     * @return String The title of the portlet.
     */
    String getTitle();
    
    /**
     * Retrieves the description (if it is defined) of the parent portlet as it 
     * is defined within the portlet registry.
     * 
     * @return String The description of the portlet.
     */
    String getDescription();   
    

}
