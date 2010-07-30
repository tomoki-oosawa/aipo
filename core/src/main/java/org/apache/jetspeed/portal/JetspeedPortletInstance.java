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

// Java imports
import java.util.ArrayList;
import java.util.Iterator;


// ECS imports
import org.apache.ecs.ConcreteElement;

// Jetspeed imports
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

//Turbine imports
import org.apache.turbine.util.RunData;

/**
 * Default Implementation of Jetspeed Portlet Instance.
 * It implements the methods for getting, setting and removing attributes from 
 * portlet instance persistence storage.
 *
 * In a Jetspeed 1.4x PSML profile, the default XML format for an instance and attribute is:
 *
 * <entry>
 *    <parameter name="someName" value="someValue"/>
 *
 * Since this modifies PSMLDocuments, it will make a clone once modification starts.
 *
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: JetspeedPortletInstance.java,v 1.9 2004/02/23 04:05:35 jford Exp $
 */
public class JetspeedPortletInstance implements PortletInstance
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortletInstance.class.getName());    
    
    /** The Profile (and PSMLDocument) that we started with, considered RO */
    private Profile m_profile = null;

    /** The Profile (and PSMLDocument) that we cloned to edit. */
    private Profile m_clonedProfile = null;

    /** The rundata - WARNING: it is VERY DANGEROUS to store rundata like this.
        Rundata objects are re-used, and are only good within one request.
        This means that a PortletInstance CANNOTA BE STORED BEYOND A SINGLE REQUEST! */
    private JetspeedRunData m_rundata = null;

    /** 
     * keeping an instance of the actual Portlet around helps us with
     * getting information that is not directly available from the PSML profile
     */
    private Portlet m_portlet = null;
    
    /**
     * Constructs a PortletInstance from a Portlet and a RunData.
     *
     * @param Portlet The parent portlet of this instance.
     * @param RunData Jetspeed Run Data
     */
    public JetspeedPortletInstance(Portlet portlet, RunData data)
    {
        this(portlet, ((JetspeedRunData) data).getProfile());
        m_rundata = (JetspeedRunData)data;
        
        // if we are in an edit already, use that as the clone
        m_clonedProfile = ((JetspeedRunData)data).getCustomizedProfile();
    }

   /**
    * Constructs a PortletInstance from a Portlet and a Profile
    *
    * @param Portlet The parent portlet of this instance.
    * @param Profile The profile containing the instance.
    */
    public JetspeedPortletInstance(Portlet portlet, Profile profile)
    {
        m_portlet = portlet;
        m_profile = profile;
    }    
 
     /**
     * Protect the constructor so it can only be instantitated by
     * by the PortalPersistenceService
     */
    protected JetspeedPortletInstance()
    {
        // does nothin'
    }

    /**
     * @see PortletInstance#getAttribute()
     */
    public String getAttribute(String name, String dftValue)
    {
        Entry entry = getEntry();

        if (entry == null)
        {
            return dftValue;
        }
        Parameter attr = entry.getParameter(name);
        if (attr != null)
        {
            return attr.getValue();
        }
        else
        {
            return dftValue;
        }
    }

    /**
     * @see PortletInstance#getAttribute()
     */
    public String getAttribute(String name)
    {
        Entry entry = getEntry();

        if (entry == null)
        {
            return "";
        }
        Parameter attr = entry.getParameter(name);
        if (attr != null)
        {
            return attr.getValue();
        }
        else
        {
            return "";
        }
    }

    /**
     * @see PortletInstance#setAttribute(String, String)
     */
    public void setAttribute(String name, String value)
    {
        // make sure we are updating and using the clone now
        setupForUpdate();
        
        Entry entry = getEntry();

        Parameter attr = entry.getParameter(name);
         
         // Setting a attribute to null should just remove it.
        if(value == null)
        {
            removeAttribute(name);
        }
        else if (attr != null)
        {
            attr.setValue(value);
        }
        // If an attribute does not exist, then add it.
        else
        {
            PsmlParameter newAttr = new PsmlParameter();
            newAttr.setName(name);
            newAttr.setValue(value);
            entry.addParameter(newAttr);
        }
    }

    /**
     * @see PortletInstance#removeAttribute(String)
     */
    public void removeAttribute(String name)
    {
        // make sure we are updating and using the clone now
        setupForUpdate();
        
        Entry entry = getEntry();

        // I am assuming that we only allow one parameter per name
        Iterator params = entry.getParameterIterator();
        int index = -1;
        int count = 0;
        while (params.hasNext())
        {
            Parameter param = (Parameter) params.next();
            if (param.getName().equalsIgnoreCase(name))
            {
                index = count;
                break;
            }
            count++;
        }

        // We have to wait until we are outside the loop to remove
        // or else we throw a ConcurrentModificationException		
        if (index != -1)
        {
            entry.removeParameter(index);           
        }
    }

    /**
     * @see PortletInstance#removeAllAttributes()
     */
    public void removeAllAttributes()
    {
        // make sure we are updating and using the clone now
        setupForUpdate();
        
        Entry entry = getEntry();

        entry.removeAllParameter();
    }

    /**
     * @see PortletInstance#getAttributes()
     */
    public Iterator getAttributes()
    {
        Entry entry = getEntry();

        return entry.getParameterIterator();
    }

    /**
     * @see PortletInstance#getAttributeNames()
     */
    public Iterator getAttributeNames()
    {
        Iterator itr = getAttributes();
        ArrayList list = new ArrayList();
        while(itr.hasNext());
        {
            Parameter param = (Parameter) itr.next();
            list.add(param.getName());
        }
        
        return list.iterator();
    }

    /**
     * @see PortletInstance#getDocument()
     */
    public PSMLDocument getDocument()
    {
        return getProfile().getDocument();
    }

    /**
     * @see PortletInstance#getProfile()
     */
    public Profile getProfile()
    {
        // use the clone if we have made it
        if (m_clonedProfile != null) return m_clonedProfile;

        return m_profile;
    }

    /**
     * @see PortletInstance#getPortlet()
     */
    public Entry getEntry()
    {
        return getDocument().getEntryById(m_portlet.getID());
    }

    /**
     * @see PortletInstance#getDescription()
     */
    public String getDescription()
    {
        Entry entry = getEntry();

        String description = null;
        if (entry != null)
        {
            MetaInfo metaInfo = entry.getMetaInfo();
            if (metaInfo != null)
            {
                description = metaInfo.getDescription();
            }
        }
        return m_portlet.getDescription(description);
    }

    /**
     * @see PortletInstance#getName()
     */
    public String getName()
    {
        return getEntry().getParent();
    }

    /**
     * @see PortletInstance#getTitle()
     */
    public String getTitle()
    {
        Entry entry = getEntry();

        String title = null;
        if (entry != null)
        {
            MetaInfo metaInfo = entry.getMetaInfo();
            if (metaInfo != null)
            {
                title = metaInfo.getTitle();
            }
        }
        return m_portlet.getTitle(title);
    }

    /**
     * @see PortletInstance#getImage()
     */
    public String getImage()
    {
        Entry entry = getEntry();

        String image = null;
        if (entry != null)
        {
            MetaInfo metaInfo = entry.getMetaInfo();
            if (metaInfo != null)
            {
                return image = metaInfo.getImage();
            }
        }
        return m_portlet.getImage(image);
    }

    /**
     * @see PortletInstance#getPortlet()
     */
    public Portlet getPortlet()
    {
        return m_portlet;
    }
    
    /**
     * @see PortletInstance#getContent()
     */
    public ConcreteElement getContent()
    {
      return m_portlet.getContent(m_rundata);
    }
    
    /**
     * @see PortletInstance#isShowTitleBar()
     */
    public boolean isShowTitleBar()
    {
      return m_portlet.isShowTitleBar(m_rundata);
    }

    /**
    * Setup for making a change to the Entry
    * We must not change the PSMLDocument that we started with, instead we make a clone
    * and start using it.
    */
    private void setupForUpdate()
    {
        if (m_clonedProfile != null) return;
        
        try
        {
            m_clonedProfile = (Profile) m_profile.clone();
        }
        catch (CloneNotSupportedException e)
        {
            logger.warn("JetspeedPortletInstance: cannot clone Profile!: " + e);
        }
    }
}

