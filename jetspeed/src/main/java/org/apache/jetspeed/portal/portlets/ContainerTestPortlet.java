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

package org.apache.jetspeed.portal.portlets;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.persistence.PortalPersistenceException;
import org.apache.jetspeed.portal.PortletInstance;

import org.apache.jetspeed.util.MimeType;

import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.ecs.ConcreteElement;

import org.apache.turbine.util.RunData;

/**
    Aggregate Portlet aggregates the content of other portlets.

    This portlet is a test for an alternate aggregation algorithm

    UNDER CONSTRUCTION

    @author <A HREF="mailto:taylor@apache.org">David Sean Taylor</A>
    @version $Id: ContainerTestPortlet.java,v 1.9 2004/03/29 21:38:42 taylor Exp $
*/

public class ContainerTestPortlet implements Portlet /* , PortletState, Cacheable, Refreshable */
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ContainerTestPortlet.class.getName());    
    
    private String image = null;

    private String name = "not set";

    private String title = "la title";

    private String description = "description";

    private String id = null;

    private String handle = "";

    private PortletConfig   pc = null;


   /**
    Holds instances of ConcreteElements (Portlet output/content)
    based on its current CapabilityMap.
    */
    // protected Hashtable content = new Hashtable();

    /**
    The time this portlet was created.
    */
    private long creationTime;

    /**
    Returns a name for this portlet.  This is used by PSML to identify a Portlet
    within the PortletRegistry
    */
    public String getName()
    {
        return name;
    }

    /**
    Sets the name on this Portlet.

    @see #getName()
    */
    public void setName(String name)
    {
        System.out.println("setting name = " + name);
        this.name = name;
    }

    /**
    <p>
    Allows a Portlet to define its title.  This can be used by a PortletControl
    for rendering its content.
    </p>

    <p>
    In order to define a default title you should not override this but should
    call setTitle() within your init() method
    </p>

    <p>
    This should return null if not specified.
    </p>
    */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * t a title for this instance of the portlet.  This method is called
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
            return instanceTitle;
        return getTitle();
    }


    /**
    Set the title for this Portlet
    */
    public void setTitle( String title )
    {
        this.title = title;
    }

    /**
    <p>
    Returns a description of this portlet.  This should describe what the
    capabilities of the portlet and how it can help the user.
    </p>

    <p>
    In order to define a default title you should not override (in the
    AbstractPortlet implementation) this but should call setDescription()
    within your init() method
    </p>

    <p>
    This should return null if not specified.
    </p>
    */
    public String getDescription()
    {
        return description;
    }

    /**
     * Provide a Description within PML if the user has specified one.
     *
     * @return a null entry if the user hasn't defined anything
     */
    public String getDescription(String instanceDescription)
    {
      if (instanceDescription != null)
          return instanceDescription;
      return getDescription();
    }

    /**
    Set the description for this Portlet
    */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * Getter for property image.
     * @return Name of portlet image, icon.  The name is expected to be in the form of a URL.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Getter for property image.
     * @return Name of portlet image, icon.  The name is expected to be in the form of a URL.
     */
    public String getImage(String instanceImage)
    {
      if (instanceImage != null)
          return instanceImage;
      return getImage();
    }

    public void setImage( String image )
    {
        this.image = image;
    }

    /**
    Returns an HTML representation of this portlet.  Usually a Portlet would
    initialized itself within init() and then when getContent is called it
    would return its presentation.
    */
    public ConcreteElement getContent(RunData rundata)
    {
        String key = ((JetspeedRunData)rundata).getProfile().getId()
                    + "." + this.getID();

        String path = (String)rundata.getUser().getTemp(key);
        if (path == null)
        {
            path = this.getPortletConfig().getInitParameter("path");
        }

        if (null == path)
        {
            return new JetspeedClearElement("Path parameter not set");
        }

        ProfileLocator locator = Profiler.createLocator();
        locator.createFromPath(path);
        String id = locator.getId();

        try
        {
            Profile profile = Profiler.getProfile(locator);
            PSMLDocument doc = profile.getDocument();
            if (doc == null)
            {
                return null;
            }
            Portlets portlets = doc.getPortlets();
            //PortletContainer.aggregate(portlets);
            return new JetspeedClearElement("XXX Under Construction :)");
        }
        catch (Exception e)
        {
            logger.error("Exception",  e);
            return new JetspeedClearElement("Error in aggregation portlet: " + e.toString());
        }
    }

    /**
    All initialization should be performed here.  If your Portlet wants to
    do any work it should be done here.  You are not guaranteed that any
    particular order of method call will happen just that init() will happen
    first. Therefore if you have to calculate things like a title, a
    description, etc it should happen here.
    */
    public void init() throws PortletException
    {
        String path = this.pc.getInitParameter("path");
    }


    /**
    Set's the configuration of this servlet.
    */
    public void setPortletConfig(PortletConfig pc)
    {
        this.pc = pc;
    }


    /**
    Get the config of this servlet.
    */
    public PortletConfig getPortletConfig()
    {
        return pc;
    }

    /**
    <p>Return true if this portlet is allowed to be edited in the rundata's context .</p>

    <p>Note:  PortletControl implementations should pay attention to this so
    that they don't allow this option if it returns false.</p>
    */
    public boolean getAllowEdit( RunData rundata )
    {
        return false;
    }

    /**
    <p>Return true if this portlets is allowed to be maximized.</p>

    <p>Note:  PortletControl implementations should pay attention to this so
    that they don't allow this option if it returns false.</p>
    */
    public boolean getAllowMaximize( RunData rundata )
    {
        return true;
    }

    /**
    Get the creation time for this Portlet
    */
    public long getCreationTime()
    {

        return this.creationTime;
    }

    /**
    Set the creation time for this Portlet
    */
    public void setCreationTime( long creationTime )
    {
        System.out.println("setting creating time");
        this.creationTime = creationTime;
    }

    /**
    Returns true portlet is able to output content for given mimetype
    */
    public boolean supportsType( MimeType mimeType )
    {
        return true;
    }

   /**
     * Retrieve a portlet attribute from persistent storage
     *
     * @param attrName The attribute to retrieve
     * @param attrDefValue The value if the attr doesn't exists
     * @param rundata The RunData object for the current request
     * @return The attribute value
     */
    public String getAttribute( String attrName, String attrDefValue, RunData rundata )
    {
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
    public void setAttribute( String attrName, String attrValue, RunData rundata )
    {
        try
        {
            PortletInstance instance = PersistenceManager.getInstance(this, rundata);
            instance.setAttribute(attrName, attrValue);
            PersistenceManager.store(instance);
        }
        catch (PortalPersistenceException e)
        {
            logger.error("Exception while setting attribute "+attrName+" for portlet "+getName(),e);
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




    /**
    Retrieve a unique portlet id
    */
    public String getID()
    {
        return "9";
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

    /**
     * Is the portled viewable.
     * @param rundata The RunData object for the current request
     * @return <CODE>true</CODE> Viewing is allow
     * <CODE>false</CODE> Viewing is NOT alowed
     * 
     * Override this method to control your own View behavior
     */
    public boolean getAllowView( RunData rundata )
    {
        return true;
    }
    

}
