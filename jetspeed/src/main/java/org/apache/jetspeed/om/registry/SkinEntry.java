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
 * The SkinEntry defines the properties used for storing a Skin description in
 * the registry
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: SkinEntry.java,v 1.2 2004/02/23 03:11:39 jford Exp $
 */
public interface SkinEntry extends RegistryEntry
{

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
        
}