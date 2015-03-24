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

package org.apache.jetspeed.om.registry.base;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.registry.MetaInfo;
import org.apache.jetspeed.om.registry.Security;
import org.apache.jetspeed.om.registry.RegistryEntry;

/**
 * Base simple bean-like implementation of the RegistryEntry interface
 * suitable for Castor XML serialization.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseRegistryEntry.java,v 1.10 2004/02/23 03:08:26 jford Exp $
 */
public class BaseRegistryEntry implements RegistryEntry, java.io.Serializable
{
    protected long id = 0;

    protected String name;

    protected MetaInfo metaInfo = null;

    protected Security security = null;

    protected boolean hidden;

    protected int _hidden;

    /** Holds value of property securityRef. */
    protected SecurityReference securityRef = null;

    public BaseRegistryEntry()
    {}

    public BaseRegistryEntry(long id,
                             String name,
                             int _hidden,
                             String title,
                             String description,
                             String image,
                             String role)
    {
        this.id = id;
        this.name = name;
        this._hidden = _hidden;
        this.hidden = (_hidden == 1);
        BaseMetaInfo meta = new BaseMetaInfo(title, description, image);
        this.setMetaInfo(meta);
        BaseSecurity security = new BaseSecurity(role);
        this.setSecurity(security);
    }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object entry)
    {
        if (entry==null)
        {
            return false;
        }

        BaseRegistryEntry e = (BaseRegistryEntry)entry;

        if (e.getId()!=getId())
        {
            return false;
        }

        if (e.isHidden()!=isHidden())
        {
            return false;
        }

        if (name!=null)
        {
            if (!e.name.equals(name))
            {
                return false;
            }
        }
        else
        {
            if (e.name!=null)
            {
                return false;
            }
        }

        if (metaInfo != null)
        {
            if (!metaInfo.equals(e.metaInfo))
            {
                return false;
            }
        }
        else
        {
            if (e.metaInfo!=null)
            {
                return false;
            }
        }

        if (security!=null)
        {
            if (!security.equals(e.security))
            {
                return false;
            }
        }
        else
        {
            if (e.security!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @see RegistryEntry#getName */
    public long getId()
    {
        return this.id;
    }

    /** @see RegistryEntry#getName */
    public String getName()
    {
        return this.name;
    }

    /** @see RegistryEntry#setName */
    public void setName( String name )
    {
        this.name = name;
    }

    /** @see RegistryEntry#getTitle */
    public String getTitle()
    {
        if (this.metaInfo != null)
        {
            String title = this.metaInfo.getTitle();
            if (null != title)
            {
                return title;
            }
        }

        return null;
    }

    /** @see RegistryEntry#setTitle */
    public void setTitle(String title)
    {
        if (this.metaInfo == null)
        {
            this.metaInfo = new BaseMetaInfo();
        }

        this.metaInfo.setTitle(title);
    }

    /** @see RegistryEntry#getDescription */
    public String getDescription()
    {
        if (this.metaInfo != null)
        {
            String desc = this.metaInfo.getDescription();
            if (null != desc)
                return desc;
        }

        return null;
    }

    /** @see RegistryEntry#setDescription */
    public void setDescription(String description)
    {
        if (this.metaInfo == null)
        {
            this.metaInfo = new BaseMetaInfo();
        }

        this.metaInfo.setDescription(description);
        this.description = description;
    }

    /** @see RegistryEntry#getSecurity */
    public Security getSecurity()
    {
        return this.security;
    }

    /** @see RegistryEntry#setSecurity */
    public void setSecurity( Security security )
    {
        this.security = security;
        this.role = this.security.getRole();
    }

    /** @see RegistryEntry#isHidden */
    public boolean isHidden()
    {
        return this.hidden;
    }

    /** @see RegistryEntry#setHidden */
    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
        this._hidden = (hidden) ? 1 : 0;
    }

    // Castor serialization methods

    /** Required by Castor 0.8.11 XML serialization for retrieving the visibility
      * status
      */
    public boolean getHidden()
    {
        return this.hidden;
    }

    /** Required by Castor 0.8.11 XML serialization for retrieving the security
      * object
      */
    public BaseSecurity getBaseSecurity()
    {
        return (BaseSecurity)this.security;
    }

    /** Required by Castor 0.8.11 XML serialization for setting the security
      * status
      */
    public void setBaseSecurity( BaseSecurity security )
    {
        this.security = security;
        this.role = this.security.getRole();
    }

    public MetaInfo getMetaInfo()
    {
        return this.metaInfo;
    }

    /** Required by Castor 0.8.11 XML serialization for setting the entry
      * metainfo
      */
    public void setMetaInfo( MetaInfo metaInfo )
    {
        this.metaInfo = metaInfo;
        this.title = metaInfo.getTitle();
        this.description = metaInfo.getDescription();
        this.image = metaInfo.getImage();
    }

    /** Required by Castor 0.8.11 XML serialization for retrieving the metainfo
      */
    public BaseMetaInfo getBaseMetaInfo()
    {
        return (BaseMetaInfo)this.metaInfo;
    }

    /** Required by Castor 0.8.11 XML serialization for setting the entry
      * metainfo
      */
    public void setBaseMetaInfo( BaseMetaInfo metaInfo )
    {
        this.metaInfo = metaInfo;
        this.title = metaInfo.getTitle();
        this.description = metaInfo.getDescription();
        this.image = metaInfo.getImage();
    }

    /** Getter for property securityId.
     * @return Value of property securityId.
     */
    public SecurityReference getSecurityRef()
    {
        return securityRef;
    }

    /** Setter for property securityId.
     * @param securityId New value of property securityId.
     */
    public void setSecurityRef(SecurityReference securityRef)
    {
        this.securityRef = securityRef;
    }

    // OJB - can't seem to get embedded objects to work without this hack
    String title;
    String description;
    String image;
    String role;

}
