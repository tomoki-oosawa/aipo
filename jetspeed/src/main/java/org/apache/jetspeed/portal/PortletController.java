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

import org.apache.jetspeed.util.MimeType;
import org.apache.turbine.util.RunData;
import org.apache.ecs.ConcreteElement;
import java.util.Map;
import java.io.Serializable;

/**
 * A PortletController is responsible for laying out and rendering the content of 
 * a PortletSet. 
 * <p>As such, it also provides 2 additionnal services :
 * <ul>
 *   <li>factory for the creating appropriate PortletSet;Constraints
 *   <li>customization facilities for handling the addition of a new portlet
 *       within the set and moving entries around within the set
 * </ul>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id: PortletController.java,v 1.54 2004/02/23 04:05:35 jford Exp $
 */
public interface PortletController extends Serializable
{

    /**
     * Initialize this PortletController. This method must be called after
     * the Config object has been set and whenever it is changed
     */
    public void init();

    /**
     * Sets the configuration object for this controller instance
     *
     * @param conf the controller configuration
     */
    public void setConfig(PortletControllerConfig conf);

    /**
     * Returns the configuration object for this object
     *
     * @return a configuration object
     */
    public PortletControllerConfig getConfig();

    /**
     * Sets the PotletSet to render by this controller
     *
     * @param portlets the portlet set to render
     */
    public void setPortlets(PortletSet portlets);

    /**
     * Returns the PortletSet to render by this controller
     *
     * @return the portlet set that should currently be rendered by this
     * controller
     */
    public PortletSet getPortlets();

    /**
     * Renders the content of the PortletSet for the current request
     * 
     * @param rundata the RunData for the request
     * @return the rendered content for this set or null if the content
     * was written directly on the writer available in RunData
     */
    public ConcreteElement getContent( RunData rundata );

    /**
     * Tests whether the controller supports outputting content for 
     * a specific mime type
     *
     * @param mimeType the requested mime type
     * @return true if the requested mime type is supported by this controller
     */
    public boolean supportsType( MimeType mimeType );
        
    /**
     * Creates a constraint object based on an original source.
     *
     * @param original the source for this constraint object
     * @return a new Constraints object appropriate for this controller
     */
    public PortletSet.Constraints getConstraints( Map original );
        
}
