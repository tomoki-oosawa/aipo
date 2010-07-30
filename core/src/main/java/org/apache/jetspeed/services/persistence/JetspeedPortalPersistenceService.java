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

package org.apache.jetspeed.services.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Jetspeed classes
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.portal.JetspeedPortletInstance;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

// Turbine classes
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.RunData;

/**
 * Implementation of the Portal Persistence Service for storing and
 * retrieving portlet instances.
 *
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 * @version $Id: JetspeedPortalPersistenceService.java,v 1.5 2004/02/23 03:33:52 jford Exp $
 */
public class JetspeedPortalPersistenceService
    extends TurbineBaseService
    implements PortalPersistenceService
{    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortalPersistenceService.class.getName());    
    
    /**
     * Use this to verify the RunData object in use is going to be the Jetspeed
     * RunData object.  This way we know right off, whether or not things are going
     * to work.
     * 
     */
    public void init(RunData data) throws InitializationException
    {
        if (!(data instanceof JetspeedRunData))
        {
            logger.error(
                "[PortalPersistenceService] The current RunData implenetation does not implement the JetspeedRunData interface.");
            setInit(false);
            throw new InitializationException("The current RunData implenetation does not implement the JetspeedRunData interface.");
        }

        super.init(data);
    }    
    
    /**
     * @see PortalPersistenceService#getInstance(Portlet, Profile)
     */
    public PortletInstance getInstance(Portlet portlet, Profile profile)
    {
        return new JetspeedPortletInstance(portlet, profile);
    }

    /**
     * @see PortalPersistenceService#getInstance(Portlet, RunData)
     */
    public PortletInstance getInstance(Portlet portlet, RunData data)
    {
     	String attrKey = "portlet_instance:"+portlet.getID();
    	// optimize portlet instance retreival by saving it to the request
    	// this also guarantees the PortletInstance object is the same
    	// object for the entire life of the request
    	PortletInstance instance = (PortletInstance) data.getRequest().getAttribute(attrKey);
    	if(instance != null)
    	{
    		return instance;
    	}
    	else
    	{
         	instance=  new JetspeedPortletInstance(portlet, data);
         	data.getRequest().setAttribute(attrKey, instance);
         	return instance;
    	}

    }

    /**
     * @see PortalPersistenceService#store(PortletInstance)
     */
    public void store(PortletInstance pPortlet) throws PortalPersistenceException
    {
        Profile profile = pPortlet.getProfile();
        profile.setDocument(pPortlet.getDocument());
        
        if(!PsmlManager.store(profile))
        {
            throw new PortalPersistenceException("Unable to write portlet information.");
        }
    }

    /**
     * @see PortalPersistenceService#getInstances(Portlet, Profile)
     */
    public List getInstances( Profile profile) throws PortletException
    {
        PSMLDocument doc = profile.getDocument();
        Portlets portlets =  doc.getPortlets();
        ArrayList pList = new ArrayList();
       
       buildAllEntries(portlets, pList, profile);
        
        return pList;
    }

    /**
     * @see PortalPersistenceService#getInstances(Portlet, RunData)
     */
    public List getInstances( RunData data) throws PortletException
    {
        JetspeedRunData jData = (JetspeedRunData) data;
        return getInstances(jData.getProfile());
    }
    
    protected void buildAllEntries(Portlets portlets, ArrayList entries, Profile profile) throws PortletException
    {
        // First let's add all the Entries in the current level
        Iterator eItr = portlets.getEntriesIterator();
        while(eItr.hasNext())
        {
            Object obj =  eItr.next();
            Entry entry = (Entry)obj;
            Portlet portlet = PortletFactory.getPortlet(entry);
            entries.add(new JetspeedPortletInstance(portlet, profile));
        }
        
        //Now if there are child levels, drill down recursively
        if(portlets.getPortletsCount() > 0)
        {
            Iterator pItr = portlets.getPortletsIterator();
            while(pItr.hasNext())
            {
                Portlets childPortlets = (Portlets)pItr.next();
                buildAllEntries(childPortlets, entries, profile);
            }
            
            return;
        }
        else
        // bootstrap
        {
            return;
        }
        
        
    }

}