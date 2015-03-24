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

package org.apache.jetspeed.services.portletfactory;

//jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.turbine.services.Service;
 
/**
 * This service handles the creation of Portlet objects
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PortletFactoryService.java,v 1.4 2004/02/23 03:36:42 jford Exp $
 */
public interface PortletFactoryService extends Service
{

    /** The default control to use when none is specified */
    public String SERVICE_NAME = "PortletFactory";
            
    /**
     * Given a PSML Entry return an instanciated Portlet.
     *
     * @param entry a PSML Entry describing a portlet
     * @return an instanciated portlet corresponding to this entry
     */
    public Portlet getPortlet( Entry entry ) throws PortletException;

    /**
     * Given a Portlet registry entry name, instanciate it
     *
     * @param name the name of a portlet in the registry
     * @return an instanciated portlet corresponding to this entry
     */
    public Portlet getPortlet( String name, String id ) throws PortletException;
}

