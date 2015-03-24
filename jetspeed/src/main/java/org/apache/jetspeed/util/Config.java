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

//standard java stuff
import java.util.Map;
import java.util.Iterator;

/**
 * Defines a standard object configuration
 * A Config provides the parameters passed in the current request as well
 * as init parameters.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: Config.java,v 1.10 2004/02/23 03:23:42 jford Exp $
 */
public interface Config
{

    /**
    Returns the name for this configuration
    */
    public String getName();

    /**
    Sets the name of this configuration
    */
    public void setName(String name);

    /**
    Used to define a Portlet's parameters.
    */
    public void setInitParameters( Map init_params );

    /**
    Used to override Portlet's parameters.
    */
    public void addInitParameters( Map init_params );

    /**
    Retrieves the PortletController parameters
    */
    public Map getInitParameters();

    /**
    Used to define a PortletController's parameter.if value is null, removes
    the key from the stored properties
    */
    public void setInitParameter(String name, Object value);

    /**
    Returns a parameter (or null) that was given the controller.
    */
    public String getInitParameter(String name);

    /**
    Returns a parameter (or defaultValue) that was given the controller.
    */
    public String getInitParameter(String name, String defaultValue);

    /**
    Returns the parameter names of this Config.
    */
    public Iterator getInitParameterNames();

}
