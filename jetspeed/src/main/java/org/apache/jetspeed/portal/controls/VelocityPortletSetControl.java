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

package org.apache.jetspeed.portal.controls;

// Turbine stuff
import org.apache.turbine.util.RunData;

// Jetspeed stuff
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletState;
import org.apache.jetspeed.portal.PanedPortletController;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.portal.PortletInstance;

// Velocity Stuff
import org.apache.velocity.context.Context;

// Java stuff
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Enumeration;

/**
 * A Velocity based portlet control designed for handling a PortletSet
 * child
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 *
 * @version $Id: VelocityPortletSetControl.java,v 1.15 2004/02/23 03:25:35 jford Exp $
 */
public class VelocityPortletSetControl extends VelocityPortletControl
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(VelocityPortletSetControl.class.getName());    
    
    /**
     * This method adds the control specific objects to the context
     *
     * @param rundata the RunData object for this request
     * @param context the Context used by the template
     */
    public void buildContext(RunData rundata, Context context)
    {
        if (getPortlet() instanceof PortletSet)
        {
            context.put("tabs", getTabs((PortletSet) getPortlet(), rundata, context));
        }
    }    

    /**
     * Populate a list of tabs that should be displayed by this control.
     * Each tab represents a child portlet.
     *
     * This method works best if the child of this control is a PortletSet
     * whose controller implements the PanedPortletController interface.
     * 
     * @param portlet the base portlet to explore for children
     * @
     */
    private Collection getTabs(PortletSet portlets, RunData rundata, Context context)
    {       
        TreeSet tabs = new TreeSet(new PortletTabComparator());
        PanedPortletController controller = null;

        // if portlet is a PortletSet, try to retrieve the Controller
        // we need a PanedPortletController to work properly.
        if (portlets.getController() instanceof PanedPortletController)
        {    
            controller = (PanedPortletController) portlets.getController();
        }

        int count = 0;
        for (Enumeration en = portlets.getPortlets(); en.hasMoreElements(); count++)
        {
            Portlet p = (Portlet) en.nextElement();
            PortalResource portalResource = new PortalResource(p);

            // Secure the tabs
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
            boolean hasView = JetspeedSecurity.checkPermission((JetspeedUser) jdata.getUser(),
                                                                portalResource, 
                                                                JetspeedSecurity.PERMISSION_VIEW);
            if (!hasView)
            {
                continue;
            }
            // skip any closed portlet
            if ((p instanceof PortletState) && (((PortletState) p).isClosed(rundata)))
            {
                continue;
            }            

            String mstate = p.getAttribute("_menustate", "open", rundata);
            if (mstate.equals("closed"))
            {
                continue;
            }

            PortletTab tab = new PortletTab();
            
            // Handle the portlet title
            String title = null;            
            PortletInstance pi = PersistenceManager.getInstance(p, rundata);
            if (pi != null)
            {
                title = pi.getTitle();
                if (title == null)
                {
                    title = (p.getTitle() != null) ? p.getTitle() : p.getName();
                }
            }
            tab.setTitle(title);

            tab.setPosition(p.getPortletConfig().getPosition());
            if (tabs.contains(tab))
            {
                PortletTab lastTab = (PortletTab) tabs.last();
                int nextPos = lastTab.getPosition() + 1;
                tab.setPosition(nextPos);            
            }        
                
            if (controller != null)
            {
                tab.setSelected(controller.isSelected(p, rundata));
                tab.setLink(controller.getPortletURI(p, rundata).toString());                
            }
                
            tab.setActions(buildActionList(rundata, p));
            tabs.add(tab);
        }

        return tabs;
    }
    
    /** Utilty class describing a Tab elemnt in the template Velocity Context
     */
    public class PortletTab
    {
        private String title = null;
        private boolean selected = false;
        private String link = null;
        private List actions = null;
        private int position = -1;
        
        public String getTitle()
        {
            return this.title;
        }
        
        public void setTitle(String title)
        {
            this.title = title;
        }
        
        public boolean isSelected()
        {
            return this.selected;
        }
        
        public void setSelected(boolean selected)
        {
            this.selected = selected;
        }
        
        public String getLink()
        {
            return this.link;
        }
        
        public void setLink(String link)
        {
            this.link = link;
        }
        
        public List getActions()
        {
            return (this.actions == null) ? new Vector() : this.actions;
        }
        
        public void setActions(List actions)
        {
            this.actions = actions;
        }

        public int getPosition()
        {
            return position;
        }
         
        public void setPosition(int pos)
        {
            position = pos;
        }   
    }

    /**
     * Used to correctly order tabs based on the position value
     * that is found each PortletTab's parent Portlet's PortletConfig object.
     */
    public class PortletTabComparator implements  Comparator
    {

        /**
         * @see Comparator#compare(Object, Object)
         */
        public int compare(Object o1, Object o2)
        {
            try
            {
                PortletTab pt1 = (PortletTab) o1;
                PortletTab pt2 = (PortletTab) o2;
                int pos1 = pt1.getPosition();
                int pos2 = pt2.getPosition();
          
                if (pos1 < pos2)
                {
                  return -1;
                }
                else if (pos1 > pos2)
                {
                  return 1;
                }
                else
                {
                  return 0;
                }                
            }
            catch (ClassCastException e)
            {
                logger.error( "Exception in compare", e );
                return 0;
            }
        }
    }

}