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
import java.util.Map;

/**
 * The PortletInfoEntry defines all the common description properties
 * for all the portlet related entries.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: PortletInfoEntry.java,v 1.4 2004/02/23 03:11:39 jford Exp $
 */
public interface PortletInfoEntry extends RegistryEntry
{

    /** @return the classname associated to this entry */
    public String getClassname();

    /** Sets the classname for this entry. This classname is used for
     *  instanciating the associated element
     *
     *  @param classname the classname used for instanciating the component
     *  associated with this entry
     */
    public void setClassname( String classname );

    /** @return an enumeration of this entry parameter names */
    public Iterator getParameterNames();

    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getParameterMap();

    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public Parameter getParameter( String name );

    /** Adds a new parameter for this entry
     *  @param name the new parameter name
     *  @param value the new parameter value
     */
    public void addParameter( String name, String value );

    /** Adds a new parameter for this entry
     *  @param parameter the new parameter to add
     */
    public void addParameter( Parameter parameter );

    /** Removes all parameter values associated with the
     *  name
     *
     * @param name the parameter name to remove
     */
    public void removeParameter( String name );

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listMediaTypes();

    /**
     * Test if a given media type is supported by this entry.
     *
     * @param name the media type name to test for.
     * @return true is the media type is supported, false otherwise
     */
    public boolean hasMediaType(String name);

    /**
     * Add a new supported media type
     *
     * @param name the media type name to add.
     */
    public void addMediaType(String name);

    /**
     * Remove support for a given media type
     *
     * @param name the media type name to remove.
     */
    public void removeMediaType(String name);

    /** @return an enumeration of this entry tool names */
    public Iterator getToolNames();

    /** Returns a map of tool descriptors keyed on the tool names
     *  @return the tool descriptor map
     */
    public Map getToolMap();

    /** Search for a named tool and return the associated
     *  ToolDescriptor. The search is case sensitive.
     *
     *  @return the ToolDescriptor for a given name
     *  @param name the tool name to look for
     */
    public ToolDescriptor getTool( String name );

    /** Adds a new tool to this entry
     *  @param tool the new tool to add
     */
    public void addTool( ToolDescriptor tool );

    /** Removes the tool associated with the
     *  name
     *
     * @param name the name of the tool to remove
     */
    public void removeTool( String name );

}