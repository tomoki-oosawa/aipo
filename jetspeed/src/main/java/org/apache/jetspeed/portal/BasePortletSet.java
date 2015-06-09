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

package org.apache.jetspeed.portal;

// standard java stuff
import java.util.Vector;
import java.util.Enumeration;

// Jetspeed stuff
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.persistence.PortalPersistenceException;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.util.MetaData;
import org.apache.jetspeed.util.MimeType;

// turbine stuff
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.RunData;

// ECS stuff
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

/**
 * The PortletSet is basically a wrapper around an array of portlets. It provides
 * runtime context for a set of portlets.
 * A portlet can get its current set by calling via its PortletConfig
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: BasePortletSet.java,v 1.35 2004/03/29 21:38:42 taylor Exp $
 */
public class BasePortletSet implements PortletSet, Portlet, PortletState
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BasePortletSet.class.getName());    
    
    /**
    Is this set sorted
    */
    private boolean dirty = false;

    /**
    The PortletConfig of this set
    */
    private PortletConfig pc = null;

    /**
    Provide a name for this set
    */
    private String name = null;

    /**
    Storage for the portlets assigned to this set
    */
    private Vector portlets = null;

    /**
    Controller which will layout the set
    */
    private PortletController controller = null;
    
    /**
    The time this portlet was created.
    */
    private long creationTime;
    
    /** 
    The name of the portlet displaying info
    */
    private String info;

    /** 
    The portletset id
    */
    private String id = null;

    /**
    Builds a new empty set for storing portlets
    */
    public BasePortletSet()
    {
        portlets = new Vector();
        try
        {
            init();
        }
        catch (PortletException e)
        {
            logger.error("Exception", e);
        }
    }

    /**
    Builds a new empty set for storing portlets with a default controller
    */
    public BasePortletSet(PortletController controller)
    {
            portlets = new Vector();
            setController(controller);
    }

    /**
    */
    public void init() throws PortletException
    {            
        if (getPortletConfig() == null)
        {
            setPortletConfig(new BasePortletConfig());
        }
    }

    // Set manipulation methods

    /**
    Returns the number of portlets currently stored in this set
    */
    public int size()
    {
        return portlets.size();
    }

    /**
    Returns the portlet set as an array.
    */
    public Portlet[] toArray()
    {
        sortPortletSet();
        Portlet[] p = new Portlet[portlets.size()];
        portlets.copyInto(p);

        return p;
    }

    /**
    Returns the Portlet at position pos
    */
    public Portlet getPortletAt(int pos)
    {
        sortPortletSet();
        return (Portlet) portlets.elementAt(pos);
    }

    /**
    Returns the Portlet with the given id
    */
    public Portlet getPortletByID(String id)
    {
        if (portlets == null)
        {
            return null;
        }

        Portlet portlet = null;
        for (int ix = 0; ix < portlets.size(); ix++)
        {
            portlet = (Portlet) portlets.elementAt(ix);
            if (portlet.getID().equals(id))
            {
                return portlet;
            }
        }
        return null;
    }

    /**
    Returns the Portlet with the given name
    */
    public Portlet getPortletByName(String name)
    {
        if (portlets == null)
        {
            return null;
        }

        Portlet portlet = null;
        for (int ix = 0; ix < portlets.size(); ix++)
        {
            portlet = (Portlet) portlets.elementAt(ix);
            if (portlet.getName().equals(name))
            {
                return portlet;
            }
        }
        return null;
    }

    /**
    Returns the portlet set as an Enumeration
    */
    public Enumeration getPortlets()
    {
        sortPortletSet();
        return portlets.elements();
    }


    // set content manipulation methods

    /**
    Add a portlet to this set.It updates its config to modify the current set
    */
    public void addPortlet(Portlet portlet)
    {
        addPortlet(portlet, null, -1);
    }

    /**
    Add a portlet to this set.It updates its config to modify the current set
    */
    public void addPortlet(Portlet portlet, int position)
    {
        addPortlet(portlet, null, position);
    }

    /**
    Add a portlet to this set.It updates its config to modify the current set
    */
    public void addPortlet(Portlet portlet, PortletSet.Constraints constraints)
    {
        addPortlet(portlet, constraints, -1);
    }

    /**
    Add a portlet to this set.It updates its config to modify the current set
    */
    public void addPortlet(Portlet portlet, PortletSet.Constraints constraints, int position)
    {
        synchronized (portlets)
        {
            portlets.addElement(portlet);
            PortletConfig pc = portlet.getPortletConfig();
            if (pc != null)
            {
                pc.setPortletSet(this);
                if (constraints != null)
                {
                    pc.setConstraints(constraints);
                }
                if (position >= 0)
                {
                    pc.setPosition(position);
                    if (position < (portlets.size() - 1))
                    {
                        this.dirty = true;
                    }
                }
            }
        }
    }

    // set properties setters/getters

    /**
    Return the current controller for this set
    */
    public PortletController getController()
    {
        return this.controller;
    }

    /**
    Set the controller for this set
    */
    public synchronized void setController(PortletController controller)
    {
        this.controller = controller;
        controller.setPortlets(this);
    }


    // portlet interface implementation

    /**
    */
    public ConcreteElement getContent(RunData rundata)
    {
        ConcreteElement content = null; 
        PortletController controller = getController();
        PortalResource portalResource = new PortalResource(this);

        try
        {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
            portalResource.setOwner(jsLink.getUserName());
            JetspeedLinkFactory.putInstance(jsLink);
        }
        catch (Exception e)
        {
            logger.warn(e.toString(), e);
            portalResource.setOwner(null);
        }

        if (!JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),  
                  portalResource, JetspeedSecurity.PERMISSION_VIEW))
        {   
            if ( logger.isDebugEnabled() )
            {
                logger.debug("Unauthorized access by user \"" + rundata.getUser().getUserName() + "\"");
            }
            // Clear any portlets that exist in this set
            if (this.portlets != null)
            {
                this.portlets.clear();
            }
            return new StringElement(Localization.getString(rundata, "SECURITY_NO_ACCESS"));
        }
        else
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("User \"" + rundata.getUser().getUserName() + "\" is authorized to portlet set " + getID());
            }
        }
            
        if (controller == null)
        {
            Portlet p = getPortletAt(0);
    
            if (p != null)
            {
                content = p.getContent(rundata);
            }
        }
        else
        {
            content = controller.getContent(rundata);
        }

        if (content == null)
        {
            content = new ConcreteElement();
        }
        
        return content;
    }

    /**
    */
    public String getName()
    {
        if (name == null)
        {
            return this.getClass().getName();
        }

        return name;
    }

    /**
    */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
    */
    public PortletConfig getPortletConfig()
    {
        return this.pc;
    }

    /**
    */
    public void setPortletConfig(PortletConfig pc)
    {
        this.pc = pc;
    }

    /**
    */
    public String getDescription()
    {
        if (getPortletConfig() != null)
        {
            if (getPortletConfig().getMetainfo() != null)
            {
                return getPortletConfig().getMetainfo().getDescription();
            }
        }

        return null;
    }

    /**
     * Getter for property description.
     * @return Name of portlet description.
     */
    public String getDescription(String instanceDescription)
    {
      if (instanceDescription != null)
      {
          return instanceDescription;
      }
      return getDescription();
    }

    /**
    */
    public void setDescription(String description)
    {
        PortletConfig pc = getPortletConfig();
        if (pc == null)
        {
            pc = new BasePortletConfig();
            setPortletConfig(pc);
        }

        MetaData meta = pc.getMetainfo();
        if (meta == null)
        {
            meta = new MetaData();
            pc.setMetainfo(meta);
        }

        meta.setDescription(description);
    }

    /**
     * Setter for property image.
     */
    public void setImage(String instanceImage)
    {
    }    
     
    /**
    */
    public String getImage()
    {
        if (getPortletConfig() != null)
        {
            if (getPortletConfig().getMetainfo() != null)
            {
                return getPortletConfig().getMetainfo().getImage();
            }
        }

        return null;
    }
    
    /**
     * Getter for property image.
     * @return Name of portlet image, icon.  The name is expected to be in the form of a URL.
     */
    public String getImage(String instanceImage)
    {
      if (instanceImage != null)
      {
          return instanceImage;
      }
      return getImage();
    }

    /**
    */
    public String getTitle()
    {
        if (getPortletConfig() != null)
        {
            if (getPortletConfig().getMetainfo() != null)
            {
                return getPortletConfig().getMetainfo().getTitle();
            }
        }

        return null;
    }
    

    /** Get a title for this instance of the portlet.  This method is called
     * from the context variable portlet_instance and from PortletInstance
     *
     * If you wish to append to the title, then you code should look like
     *    getTitle( String instanceTitle)
     *    {
     *      return super.getTitle( instanceTitle) + " - Appened title text";
     *    }
     *
     * @param instanceTitle Title from PSML
     */
    public String getTitle(String instanceTitle)
    {
        if (instanceTitle != null)
        {
            return instanceTitle;
        }
        return getTitle();
    }

    /**
    */
    public void setTitle(String title)
    {

        PortletConfig pc = getPortletConfig();
        if (pc == null)
        {
            pc = new BasePortletConfig();
            setPortletConfig(pc);
        }

        MetaData meta = pc.getMetainfo();
        if (meta == null)
        {
            meta = new MetaData();
            pc.setMetainfo(meta);
        }

        meta.setTitle(title);
    }

    /**
    */
    public boolean getAllowEdit(RunData rundata)
    {
        return true;
    }

    /**
     */
     public boolean getAllowView(RunData rundata)
     {
         return true;
     }
    
    /**
    */
    public boolean getAllowMaximize(RunData rundata)
    {
        return false;
    }

    // private util methods

    /**
     * Sort the portlets according to Layout position
     */
    public final void sortPortletSet()    
    {
        if (!this.dirty)
        {
            return;
        }
        
        synchronized (portlets)
        {
            for (int i = 0; i < portlets.size(); i++)
            {
                
                Portlet p = (Portlet) portlets.elementAt(i);
                int pos = p.getPortletConfig().getPosition();
                if ((pos >= 0) && (pos != i) && pos < portlets.size())
                {
                    portlets.removeElementAt(i);
                    portlets.insertElementAt(p, pos);
                }
            }
                    
            this.dirty = false;   
        }        
    }

    /**
    @see Portlet#getCreationTime()
    */
    public long getCreationTime()
    {
        return this.creationTime;
    }
    
    /**
    @see Portlet#setCreationTime
    */
    public void setCreationTime(long creationTime)
    {
        this.creationTime = creationTime;
    }
    
    /**
     Method retruns true if at least one of the portlets of the portletset
     fits the requested MimeTyp. Otherwise it retruns false. 
    */
    public boolean supportsType(MimeType mimeType)
    {
        Enumeration portlets = this.getPortlets();
        while (portlets.hasMoreElements())
        {
            Portlet p = (Portlet) portlets.nextElement();
            if (p.supportsType(mimeType))
            {
                return true;
            }
        }

        return false;
    }

    // PortletState Interface implementation
    
    /**
     * Implements the default close behavior: any authenticated user may
     * remove a portlet from his page
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowClose(RunData rundata)
    {
        return false;
    }

    /**
     * Returns true if this portlet is currently closed
     */
    public boolean isClosed(RunData data)
    {
        return false;
    }

    /**
     * Toggles the portlet state between closed and normal
     *
     * @param minimized the new portlet state
     * @param data the RunData for this request
     */
    public void setClosed(boolean close, RunData data)
    {
        // empty
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * get information on a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowInfo(RunData rundata)
    {
        return false;
    }

    /**
     * Implements the default print friendly format behavior: not
     * available for the portlet set
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowPrintFriendly(RunData rundata)
    {
        return false;
    }

    /**
     * Implements the default customize behavior: any authenticated user may
     * customize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowCustomize(RunData rundata)
    {
        PortalResource portalResource = new PortalResource(this);
        try
        {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
            portalResource.setOwner(jsLink.getUserName());
            JetspeedLinkFactory.putInstance(jsLink);
        }
        catch (Exception e)
        {
            logger.warn(e.toString(), e);
            portalResource.setOwner(null);
        }
        JetspeedRunData jdata = (JetspeedRunData) rundata;
        return JetspeedSecurity.checkPermission((JetspeedUser) jdata.getUser(),
                                                portalResource,
                                                JetspeedSecurity.PERMISSION_CUSTOMIZE);
    }

    /**
     * Implements the default maximize behavior: any authenticated user may
     * maximize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMaximize(RunData rundata)
    {
        return false;
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * minimize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMinimize(RunData rundata)
    {
        return false;
    }

    /**
     * Returns true if this portlet is currently minimized
     */
    public boolean isMinimized(RunData rundata)
    {
        return false;
    }

    /**
    Change the portlet visibility state ( minimized <-> normal )

    @param minimize True if the portlet change to minimized
    @param rundata A RunData object
    */
    public void setMinimized(boolean minimize, RunData rundata)
    {
        // empty
    }

    //Portlet interface

   /**
     * Retrieve a portlet attribute from persistent storage
     *
     * @param attrName The attribute to retrieve
     * @param attrDefValue The value if the attr doesn't exists
     * @param rundata The RunData object for the current request
     * @return The attribute value
     */
    public String getAttribute(String attrName, String attrDefValue, RunData rundata)
    {
        // return attrDefValue;
        
        String attrValue = null ;

        PortletInstance instance = PersistenceManager.getInstance(this, rundata);
        attrValue = instance.getAttribute(attrName, attrDefValue);

        return attrValue;
        
    }

    /**
     * Stores a portlet attribute in persistent storage
     *
     * @param attrName The attribute to retrieve
     * @paarm attrValue The value to store
     * @param rundata The RunData object for the current request
     */
    public void setAttribute(String attrName, String attrValue, RunData rundata)
    {
        try
        {
            PortletInstance instance = PersistenceManager.getInstance(this, rundata);
            instance.setAttribute(attrName, attrValue);
            PersistenceManager.store(instance);
        }
        catch (PortalPersistenceException e)
        {
            logger.error("Exception while setting attribute " + attrName + " for portlet " + getName(), e);
        }
    }

    /**
     * Gets the portlet instance associated with this portlet.
     *
     * @return PortletInstance
     */
    public PortletInstance getInstance(RunData rundata)
    {
       return PersistenceManager.getInstance(this, rundata);
    }


    public String getID()
    {
        return id;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    /**
    * @return true if the portlet does its own customization
    */
    public boolean providesCustomization()
    {
        return false;
    }
    
    /** Returns TRUE if the title bar in should be displayed. The title bar includes
     * the portlet title and action buttons.  This
     *
     * @param rundata The RunData object for the current request
     */
    public boolean isShowTitleBar(RunData rundata)
    {
        return true;
    }
}
