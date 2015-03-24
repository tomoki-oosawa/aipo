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

package org.apache.jetspeed.portal.controls;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

// Jetspeed imports
import org.apache.jetspeed.om.registry.PortletControlEntry;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletState;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletControlConfig;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.portal.security.portlets.PortletWrapper;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.services.resources.JetspeedResources;

// Turbine imports
import org.apache.turbine.util.RunData;

// ECS imports
import org.apache.ecs.ElementContainer;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

/**
 * The PortletControl acts both as a decoration around a Portlet or PortletSet
 * and also as the window manager for the enclosed Portlet(Set).
 * As such it controls the operation that may be performed on this portlet
 * and whether the portlet content should be displayed or not.
 * PortletControl also delegates all Portlet and PortletSet methods to its
 * inner object and can thus be transparently cascaded or substituted to
 * a simple portlet wherever in a PSML object tree.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id: AbstractPortletControl.java,v 1.22 2004/03/29 21:16:27 taylor Exp $
 */
public abstract class AbstractPortletControl extends AbstractPortlet
    implements PortletControl
{

    public static final String  COLOR               = "#CCCCCC";
    public static final String  BACKGROUND_COLOR    = "#FFFFFF";
    public static final String  TITLE_COLOR         = "#DDDDDD";
    public static final String  WIDTH               = "100%";

    private Portlet                 portlet = null;
    private PortletControlConfig    conf = null;

    // PortletControl specifc interface
    
    /**
     * @return the PortletControl specific configuration
     */
    public PortletControlConfig getConfig()
    {
        return this.conf;
    }

    /**
     * Sets the PortletControl specific configuration
     * @param conf the new PortletControl configuration
     */
    public void setConfig(PortletControlConfig conf)
    {
        this.conf=conf;
    }

    /**
     * Sets the portlet to be managed by this control
     * 
     * @param portlet the new portlet to be managed by the control
     */
    public void setPortlet(Portlet portlet)
    {
        this.portlet=portlet;
    }

    /**
     * Retrieves the portlet managed by this control
     * @return the portlet object managed or null
     */
    public Portlet getPortlet()
    {
        return this.portlet;
    }

    /**
     * Initializes the control and associates it with a portlet
     *
     * @param portlet the portlet to be managed by this control
     */
    public void init( Portlet portlet )
    {
        this.setPortlet( portlet );
    }

    /**
     * Returns the color to use for displaying the portlet text
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getColor()
    {
        return getPortlet().getPortletConfig().getPortletSkin().getTextColor();
    }

    /**
     * Sets the color to use for displaying the portlet text
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @param color the text color value in HTML format (#RRGGBB)
     */
    public void setColor(String color)
    {
        getPortlet().getPortletConfig().getPortletSkin().setTextColor( color );
    }

    /**
     * Returns the color to use for displaying the portlet background
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getBackgroundColor()
    {
        return getPortlet().getPortletConfig().getPortletSkin().getBackgroundColor();
    }

    /**
     * Sets the color to use for displaying the portlet background
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @param backgroundColor the background color value in HTML format (#RRGGBB)
     */
    public void setBackgroundColor(String backgroundColor)
    {
        getPortlet().getPortletConfig().getPortletSkin().setBackgroundColor( backgroundColor );
    }

    /**
     * Returns the color to use for displaying the portlet title
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getTitleColor()
    {
        return getPortlet().getPortletConfig().getPortletSkin().getTitleBackgroundColor();
    }

    /**
     * Sets the color to use for displaying the portlet title
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    public void setTitleColor(String titleColor)
    {
        getPortlet().getPortletConfig().getPortletSkin().setTitleBackgroundColor( titleColor );
    }

    /**
     * Returns the width of the managed portlet relative to the size of
     * portlet control.
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @return the size value. May be expressed in percentages (eg, "80%")
     */
    public String getWidth() {
        return getPortlet().getPortletConfig().getSkin( "width", WIDTH );
    }

    /**
     * Sets the width of the managed portlet relative to the size of
     * portlet control.
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @param width the width of portlet. Must be a positive, non nul integer
     */
    public void setWidth(int width)
    {
        getPortlet().getPortletConfig().setSkin( "width", String.valueOf(width) );
    }

    /**
     * Sets the width of the managed portlet relative to the size of
     * portlet control.
     * 
     * This method is deprecated and is only useful for ECS
     * based Control implementation
     * @deprecated
     * @param width the width of portlet. Must be parseable as a positive, non 
     * nul integer or a percentage
     */
    public void setWidth(String width)
    {
        getPortlet().getPortletConfig().setSkin( "width", width );
    }


    // Portlet interface implementation, delegates everything 
    // to the managed portlet

    /**
     * @return the inner portlet config object
     */
    public PortletConfig getPortletConfig()
    {
        if (getPortlet()==null) return null;
        return getPortlet().getPortletConfig();
    }

    /**
     * Updates the managed portlet config object
     *
     * @param portletConfig the new portet config object
     */
    public void setPortletConfig(PortletConfig portletConfig)
    {
        if (getPortlet()==null) return;
        getPortlet().setPortletConfig(portletConfig);
    }

    /**
     * @return the inner portlet name
     */
    public String getName()
    {   
        if (getPortlet()==null) return null;
        return getPortlet().getName();
    }

    /**
     * Updates the managed portlet name
     *
     * @param name the new portet name
     */
    public void setName(String name)
    {   
        if (getPortlet()!=null) getPortlet().setName(name);
    }

    /**
     * @return the inner portlet title
     */
    public String getTitle()
    {
        if (getPortlet()==null) return null;
        return getPortlet().getTitle();
    }

    /**
     * Set the title for the inner portlet
     *
     * @param title the new portlet title
     */
    public void setTitle( String title )
    {   
        if (getPortlet()!=null) getPortlet().setTitle(title);
    }

    /**
     * @return the inner portlet description
     */
    public String getDescription()
    {
        if (getPortlet()==null) return null;
        return getPortlet().getDescription();
    }

    /**
     * Set the description for the inner portlet
     *
     * @param description the new portlet description
     */
    public void setDescription( String description )
    {   
        if (getPortlet()!=null) getPortlet().setDescription(description);
    }

    /**
     * Inits the inner portlet
     */
    public void init() throws PortletException
    {
        if (getPortlet()!=null) getPortlet().init();
    }

    /**
     * Returns the content of inner portlet
     *
     * @param rundata the RunData for the request
     */
    public ConcreteElement getContent(RunData rundata)
    {
        Portlet portlet = getPortlet(); 
        if (portlet == null)
        {
            return new ElementContainer();
        }
        
        // Check to see if the portlet allows view
        // If the current security context disallows view,
        // do not display the portlet OR the control decorator
        if (portlet instanceof PortletWrapper)
        {
            PortletWrapper wrapper = (PortletWrapper)portlet;
            if (!wrapper.getAllowView(rundata))
            {
                if (JetspeedResources.getBoolean("defaultportletcontrol.hide.decorator", true))
                {
                    return new StringElement("");
                }
            }
        }
        
        return portlet.getContent( rundata );
    }

    /**
    @see Portlet#supportsType
    */
    public boolean supportsType( MimeType mimeType )
    {
        boolean allowed = true;

        if (getPortlet()!=null)
        {
            allowed = getPortlet().supportsType( mimeType );
        }
        
        // the inner portlet does not support the type, return immediately...
        if (allowed)
        {
            // we now need to check that the control also supports the type...
            PortletControlEntry entry = 
                (PortletControlEntry)Registry.getEntry(Registry.PORTLET_CONTROL,
                                                   getConfig().getName() );
            String baseType = mimeType.toString();

            if (entry!=null)
            {
                Iterator i = entry.listMediaTypes();
    
                while(i.hasNext())
                {
                    String name = (String)i.next();
                    MediaTypeEntry media = (MediaTypeEntry)Registry.getEntry(Registry.MEDIA_TYPE, name);

                    if (media != null)
                    {
                        if (baseType.equals(media.getMimeType())) 
                        {
                            allowed = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return allowed;
    }

    // delegate the portletSet interface to the stored PortletSet
    // or emulate a 1-element portletSet

    /**
     * @return the size of the inner portletSet, 1 if the inner portlet
     * is not a set, or 0 if there's no inner portlet
     */
    public int size()
    {
        if (getPortlet() instanceof PortletSet)
            return ((PortletSet)getPortlet()).size();

        return ((getPortlet()==null)?0:1);
    }

    /**
     * @return an array of portlet contained within the inner PortletSet
     * or the inner portlet
     */
    public Portlet[] toArray()
    {
        if (getPortlet() instanceof PortletSet)
            return ((PortletSet)getPortlet()).toArray();

        Portlet[] p = new Portlet[1];
        p[0]=getPortlet();

        return p;
    }

    /**
     * Returns the Portlet at the given position if the control manages
     * a PortletSet, else the inner portlet if the queried position is 0
     *
     * @param pos the queried position. Must be strictly smaller than the
     * value of size()
     * @return the portlet object stored at this position
     */
    public Portlet getPortletAt(int pos)
    {
        if (getPortlet() instanceof PortletSet)
            return ((PortletSet)getPortlet()).getPortletAt(pos);

        if (pos==0)
            return getPortlet();
        else
            return null;
    }

    /**
     * Returns the elements contained within the inner PortletSet or the
     * inner portlet
     *
     * @return an enumeration of portlets
     */
    public Enumeration getPortlets()
    {
        if (getPortlet() instanceof PortletSet)
            return ((PortletSet)getPortlet()).getPortlets();

        Vector v = new Vector();
        if (getPortlet()!=null) v.addElement(getPortlet());

        return v.elements();
    }

    /**
     * Adds a portlet to the inner portletSet
     *
     * @param portlet the portlet to add
     */
    public synchronized void addPortlet(Portlet portlet)
    {
        if (getPortlet() instanceof PortletSet)
            ((PortletSet)getPortlet()).addPortlet(portlet);
    }

    /**
     * Adds a portlet to the inner portletSet at the explicit position
     *
     * @param portlet the portlet to add
     * @param position the position of the new portlet
     */
    public synchronized void addPortlet(Portlet portlet, int position)
    {
        if (getPortlet() instanceof PortletSet)
            ((PortletSet)getPortlet()).addPortlet(portlet,position);
    }

    /**
     * Adds a portlet to the inner portletSet within the given constraints
     *
     * @param portlet the portlet to add
     * @param constraints the portlet layout constraints
     */
    public synchronized void addPortlet(Portlet portlet, PortletSet.Constraints constraints)
    {
        if (getPortlet() instanceof PortletSet)
            ((PortletSet)getPortlet()).addPortlet(portlet,constraints);
    }

    /**
     * Adds a portlet to the inner portletSet within the given constraints at the
     * specific position
     *
     * @param portlet the portlet to add
     * @param constraint the portlet layout constraints
     * @param position the portlet required position
     */
    public synchronized void addPortlet(Portlet portlet, PortletSet.Constraints constraint, int position)
    {
        if (getPortlet() instanceof PortletSet)
            ((PortletSet)getPortlet()).addPortlet(portlet,constraint,position);
    }

    /**
     * @return the controller for the inner portletSet or null
     */
    public PortletController getController()
    {
        if (getPortlet() instanceof PortletSet)
            return ((PortletSet)getPortlet()).getController();

        return null;
    }

    /**
     * Sets the controller for the inner PortletSet. Has no effect if their
     * is no inner portletSet
     *
     * @parama controller the new controller for the inner portletSet
     */
    public synchronized void setController(PortletController controller)
    {
        if (getPortlet() instanceof PortletSet)
            ((PortletSet)getPortlet()).setController(controller);
    }

    // Delegate PortletState Interface
    
    /**
     * Implements the default close behavior: any authenticated user may
     * remove a portlet from his page
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowClose( RunData rundata )
    {
        Portlet p = getPortlet();

        if (p==null) return false;
        
        if ((p instanceof PortletSet)
            ||(JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                p,
                                                JetspeedSecurity.PERMISSION_CLOSE)))
        {
            if (p instanceof PortletState)
            {
                return ((PortletState)p).allowClose(rundata);
            }
        }
        
        return false;           
    }

    /**
     * Returns true if this portlet is currently closed
     */
    public boolean isClosed(RunData rundata)
    {
        Portlet p = getPortlet();
        
        if ( (p!=null) && (p instanceof PortletState) )
        {
            return ((PortletState)p).isClosed(rundata);
        }
        
        return false;           
    }

    /**
     * Toggles the portlet state between closed and normal
     *
     * @param minimized the new portlet state
     * @param data the RunData for this request
     */
    public void setClosed(boolean close, RunData rundata)
    {
        Portlet p = getPortlet();
        
        if ( (p!=null) && (p instanceof PortletState) )
        {
            ((PortletState)p).setClosed(close, rundata);
        }
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * get information on a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowInfo( RunData rundata )
    {
        Portlet p = getPortlet();
        
        if (p==null) return false;
        
        if ((p instanceof PortletSet)
            ||(JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                p,
                                                JetspeedSecurity.PERMISSION_INFO)))
        {
            if (p instanceof PortletState)
            {
                return ((PortletState)p).allowInfo(rundata);
            }
        }
        
        return false;
    }

    /**
     * Implements the default customize behavior: any authenticated user may
     * customize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowCustomize( RunData rundata )
    {
        Portlet p = getPortlet();

        if (p==null) return false;
        
        if ((p instanceof PortletSet)
            ||(JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                p,
                                                JetspeedSecurity.PERMISSION_CUSTOMIZE)))
        {
            if (p instanceof PortletState)
            {
                return ((PortletState)p).allowCustomize(rundata);
            }
        }
        
        return false;
    }

    /**
     * Implements the default maximize behavior: any authenticated user may
     * maximize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMaximize( RunData rundata )
    {
        Portlet p = getPortlet();

        if (p==null) return false;
        
        if ((p instanceof PortletSet)
            ||(JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                p,
                                                JetspeedSecurity.PERMISSION_MAXIMIZE)))
        {
            if (p instanceof PortletState)
            {
                return ((PortletState)p).allowMaximize(rundata);
            }
        }
        
        return false;
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * minimize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMinimize( RunData rundata )
    {
        Portlet p = getPortlet();

        if (p==null) return false;
        
        if ((p instanceof PortletSet)
            ||(JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                p,
                                                JetspeedSecurity.PERMISSION_MINIMIZE)))
        {
            if (p instanceof PortletState)
            {
                return ((PortletState)p).allowMinimize(rundata);
            }
        }
        
        return false;
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * display portlet in print friendly format
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowPrintFriendly( RunData rundata )
    {
        Portlet p = getPortlet();

        if (p==null) return false;
        
        if ((p instanceof PortletSet)
            ||(JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                p,
                                                JetspeedSecurity.PERMISSION_PRINT_FRIENDLY)))
        {
            if (p instanceof PortletState)
            {
                return ((PortletState)p).allowPrintFriendly(rundata);
            }
        }
        
        return false;
    }

    /**
     * Returns true if this portlet is currently minimized
     */
    public boolean isMinimized(RunData rundata)
    {
        Portlet p = getPortlet();
        
        if ( (p!=null) && (p instanceof PortletState) )
        {
            return ((PortletState)p).isMinimized(rundata);
        }
        
        return false;
    }

    /**
    Change the portlet visibility state ( minimized <-> normal )

    @param minimize True if the portlet change to minimized
    @param rundata A RunData object
    */
    public void setMinimized( boolean minimize, RunData rundata )
    {
        Portlet p = getPortlet();
        
        if ( (p!=null) && (p instanceof PortletState) )
        {
            ((PortletState)p).setMinimized( minimize, rundata);
        }
    }

    public String getID()
    {
        return this.portlet.getID();
    }

    public void setID(String id)
    {
        this.portlet.setID(id);
    }

    /**
    Returns the Portlet with the given id
    */
    public Portlet getPortletByID(String id)
    {
        Portlet p = getPortlet();
         if (null == p) 
            return null;

        if (p instanceof PortletSet)
        {
            Enumeration e = ((PortletSet)p).getPortlets();
            Portlet portlet = null;
            while ( e.hasMoreElements() )
            {
                portlet = (Portlet)e.nextElement();
                if (portlet.getID().equals(id))
                    return portlet;
            }
            return portlet;
       }
       return null;
    }

    /**
    Returns the Portlet with the given name
    */
    public Portlet getPortletByName(String name)
    {
        Portlet p = getPortlet();
         if (null == p) 
            return null;

        if (p instanceof PortletSet)
        {
            Enumeration e = ((PortletSet)p).getPortlets();
            Portlet portlet = null;
            while ( e.hasMoreElements() )
            {
                portlet = (Portlet)e.nextElement();
                if (portlet.getName().equals(name))
                    return portlet;
            }
            return portlet;
       }
       return null;
    }


}
