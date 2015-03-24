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

/**
 * Interface describing a cached parameter for a registry entry. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: CachedParameter.java,v 1.2 2004/02/23 03:11:39 jford Exp $
 */
public interface CachedParameter extends Parameter
{

    /**
     * Determines whether to use the parameter name as part of the unique id to the portlet cache.
     * This can be used to control the lifetime of the portlet. 
     * The name is used in combination with the URL and parameter values for this portlet
     * to uniquely identify to portlet. 
     * This value can be set in the portlet registry.
     *
     * @return cached set to true if want this portlet to be cached based on the parameter name
     */
    public boolean isCachedOnName();

    /**
     * Determines whether to use the parameter value as part of the unique id to the portlet cache.
     * This can be used to control the lifetime of the portlet. 
     * The value is used in combination with the URL and parameter names for this portlet
     * to uniquely identify to portlet. 
     * This value can be set in the portlet registry.
     *
     * @return cached set to true if want this portlet to be cached based on the parameter value
     */
    public boolean isCachedOnValue();

    /**
     * Determines whether to use the parameter name as part of the unique id to the portlet cache.
     * This can be used to control the lifetime of the portlet. 
     * The name is used in combination with the URL and parameter values for this portlet
     * to uniquely identify to portlet. 
     * This value can be set in the portlet registry.
     *
     * @return cached Set to true if want this portlet to be cached based on the parameter name
     */
    public void setCachedOnName(boolean cache);

    /**
     * Determines whether to use the parameter value as part of the unique id to the portlet cache.
     * This can be used to control the lifetime of the portlet. 
     * The value is used in combination with the URL and parameter names for this portlet
     * to uniquely identify to portlet. 
     * This value can be set in the portlet registry.
     *
     *
     * @return cached Set to true if want this portlet to be cached based on the parameter value
     */
    public void setCachedOnValue(boolean cache);

}