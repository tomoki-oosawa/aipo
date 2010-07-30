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

//standard java stuff
import java.util.Hashtable;
import java.util.Map;

//turbine support
import org.apache.turbine.services.servlet.TurbineServlet;

//jetspeed support
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.util.BaseConfig;
import org.apache.jetspeed.util.MetaData;

/**
 * Defines a configuration for Portlets.  A PortletConfig provides information
 * about the running environment of a given Portlet.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id: BasePortletConfig.java,v 1.11 2004/02/23 04:05:35 jford Exp $
 */
public class BasePortletConfig extends BaseConfig implements PortletConfig
{

    private String          url;
    private MetaData        metainfo;
    private PortletSet.Constraints  layoutConstraints = null;
    private int             layoutPosition = -1;
    private PortletSkin      skin;
    private transient PortletSet      currentSet;
    private CapabilityMap   cm = null;
    private boolean cachedOnURL = true;
    private String pageId;
    private String portletId;
    private SecurityReference securityRef = null;

    /**
    Init this PortletConfig providing the basic info.
    */
    public void init( String url, Map init_params )
    {
        if (init_params == null)
        {
            this.setInitParameters(new Hashtable());
        }
        else
        {
            this.setInitParameters( init_params );
        }

        this.setURL( url );
    }

    /**
    Returns the portlet current PortletSet
    */
    public PortletSet getPortletSet()
    {
        return this.currentSet;
    }

    /**
    Set the context (PortletSet) for this portlet
    */
    public void setPortletSet(PortletSet set)
    {
        this.currentSet=set;
    }

    /**
    Returns the portlet current PortletSet
     @deprecated use getConstraints instead
    */
    public Map getLayout()
    {
        return this.layoutConstraints;
    }

    /**
     Set the context (PortletSet) for this portlet
     @deprecated use setConstraints instead
    */
    public void setLayout(Map constraints)
    {
        //obsolete
    }

    /**
     * Returns the current skin mapping.  This method is used for configuration.  Use
     * getPortletSkin() to find skin use by the Layout engine.
     *
     * @return Current skin mapping or null if no skin is defined in PSML.
     */
    public Map getSkin()
    {
        return this.skin;
    }

    /**
     * Set the context (PortletSet) for this portlet
     *
     * @deprecated use setPortletSkin instead
     */
    public void setSkin(Map skin)
    {
        //obsolete
    }

    /**
    Returns the portlet current PortletSet
    */
    public int getPosition()
    {
        return this.layoutPosition;
    }

    /**
    Set the context (PortletSet) for this portlet
    */
    public void setPosition(int position)
    {
        this.layoutPosition=position;
    }

    /**
    Returns this Portlet's Metainfo or null it none exists.  The Metainfo can
    be used to determine an optional title or description for this Portlet.
    */
    public MetaData getMetainfo()
    {
        return this.metainfo;
    }

    /**
    Set the metainfo for the Portlet
    */
    public void setMetainfo(MetaData metainfo)
    {
        this.metainfo = metainfo;
    }

    /**
    Portlets can have external configuration information other than just
    parameters.  A URL can define an external configuration file or HTML file
    that this Portlet can parse out.

    The main reason for using setURL/getURL is because the remote URL is cached
    within Jetspeed so future requests won't have any latency.
    */
    public String getURL()
    {
        return this.url;
    }

    /**
    Used to define a Portlet's URL.
    */
    public void setURL(String url)
    {
        if ( url == null )
        {
            return;
        }

        //if the given URL doesn not include a protocol... ie http:// or ftp://
        //then resolve it relative to the current URL context
        if ( url.indexOf("://") < 0 )
        {
            this.url = TurbineServlet.getResource( url ).toString();
        }
        else
        {
            this.url = url;
        }
    }

    public boolean isCachedOnURL()
    {
        return cachedOnURL;
    }

    public void setCachedOnURL(boolean cached)
    {
        cachedOnURL = cached;
    }


    /**
    Returns a parameter (or defaultValue) that was given to a Portlet.  This can be
    by a Portlet to obtain further information of itself.
    The parameter is returned even if it is defined in the context and not directly
    in the portlet config
    */
    public String getLayout(String name, String defaultValue)
    {
        String value = null;

        if (name!=null && layoutConstraints != null)
        {
            value=(String)layoutConstraints.get(name.toLowerCase());
        }

        if (value==null) 
        {
            value=defaultValue;
        }

        return value;
    }

    /**
    Returns a parameter (or defaultValue) that was given to a Portlet.  This can be
    by a Portlet to obtain further information of itself.
    The parameter is returned even if it is defined in the context and not directly
    in the portlet config
    */
    public String getSkin(String name, String defaultValue)
    {
        String value = null;

        try
        {
            value=(String)skin.get(name.toLowerCase());
            if (value==null)
            {
                value=(String)currentSet.getPortletConfig().getSkin(name, defaultValue);
            }
            if (value==null) value=defaultValue;
        }
        catch (RuntimeException e)
        {
            value=defaultValue;
        }

        return value;
    }

    /**
    Sets a skin parameter value in the local config
    */
    public void setSkin(String name, String value)
    {
        if (name!=null)
        {
            if (getSkin()==null)
            {
                setPortletSkin(getPortletSkin());
            }
            
            if (value==null)
            {
                getSkin().remove(name);
            }
            else
            {
                getSkin().put(name,value);
            }
        }
    }

    /**
     * Retrieves the Skin object that should be used for this portlet. If 
     * the current portlet does not have a skin, then skin is retrieve from
     * the parent portlet set of the system default is now skins are defined
     * in the portlet set.
     *
     * getSkin() can be used for configuration.
     *
     * @return the Skin object that should be used.
     */
    public PortletSkin getPortletSkin()
    { 
        if ((this.skin==null)&&(getPortletSet()!=null))
        {
            return getPortletSet().getPortletConfig().getPortletSkin();
        }
            
        return this.skin;
    }
    
    /**
     * Sets the PortletSkin to use for this Portlet
     *
     * @param skin the new skin to use
     */
    public void setPortletSkin(PortletSkin skin)
    {
        this.skin = skin;
    }
    
    /**
     * Retrieves the constraints associated with this portlet
     *
     * @return the Constraints object
     */
    public PortletSet.Constraints getConstraints()
    { 
        return this.layoutConstraints;
    }
    
    /**
     * Sets the layout constraints in the current portlet set
     *
     * @param constraints the constrints object associated with this portlet
     * in the current set
     */
    public void setConstraints(PortletSet.Constraints constraints)
    {
        this.layoutConstraints = constraints;
    }
    
    /**
    */
    public CapabilityMap getCapabilityMap()
    {
        return this.cm;
    }
    
    /**
    */
    public void setCapabilityMap( CapabilityMap cm )
    {
        this.cm = cm;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }

    public String getPageId()
    {
        return this.pageId;
    }

    public void setPortletId(String portletId)
    {
        this.portletId = portletId;
    }

    public String getPortletId()
    {
        return this.portletId;
    }

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef()
    {
        return this.securityRef;
    }
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef)
    {
        this.securityRef = securityRef;
    }
    
}
