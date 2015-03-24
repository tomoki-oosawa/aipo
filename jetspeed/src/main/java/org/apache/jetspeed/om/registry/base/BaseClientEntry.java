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

import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.MimetypeMap;
import org.apache.jetspeed.om.registry.CapabilityMap;

/**
 * Simple implementation of the ClientRegistry interface.
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseClientEntry.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseClientEntry extends BaseRegistryEntry
        implements ClientEntry, java.io.Serializable
{
    private String useragentpattern = "";
    private String manufacturer = "";
    private String model = "";
    private String version = "";
    private MimetypeMap mimetypes = new BaseMimetypeMap();
    private CapabilityMap capabilities = new BaseCapabilityMap();

    public BaseClientEntry()
    {
    }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BaseClientEntry obj = (BaseClientEntry)object;

        if (useragentpattern!=null)
        {
            if (!useragentpattern.equals(obj.useragentpattern))
            {
                return false;
            }
        }
        else
        {
            if (obj.useragentpattern!=null)
            {
                return false;
            }
        }

        if (manufacturer!=null)
        {
            if (!manufacturer.equals(obj.manufacturer))
            {
                return false;
            }
        }
        else
        {
            if (obj.manufacturer!=null)
            {
                return false;
            }
        }

        if (model!=null)
        {
            if (!model.equals(obj.model))
            {
                return false;
            }
        }
        else
        {
            if (obj.model!=null)
            {
                return false;
            }
        }

        if (version!=null)
        {
            if (!version.equals(obj.version))
            {
                return false;
            }
        }
        else
        {
            if (obj.version!=null)
            {
                return false;
            }
        }

        if (!mimetypes.equals(obj.mimetypes))
        {
            return false;
        }

        if (!capabilities.equals(obj.capabilities))
        {
            return false;
        }

        return super.equals(object);
    }

    public String getUseragentpattern()
    {
        return useragentpattern;
    }

    public void setUseragentpattern(String useragentpattern)
    {
        this.useragentpattern = useragentpattern;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String name)
    {
        manufacturer = name;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String name)
    {
        model = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String name)
    {
        version = name;
    }

    public MimetypeMap getMimetypeMap()
    {
        return mimetypes;
    }

    public CapabilityMap getCapabilityMap()
    {
        return capabilities;
    }

    // castor related method definitions

    public BaseMimetypeMap getMimetypes()
    {
        return (BaseMimetypeMap)mimetypes;
    }

    public void setMimetypes(BaseMimetypeMap mimetypes)
    {
        this.mimetypes = mimetypes;
    }

    public BaseCapabilityMap getCapabilities()
    {
        return (BaseCapabilityMap)capabilities;
    }

    public void setCapabilities(BaseCapabilityMap capabilities)
    {
        this.capabilities = capabilities;
    }
}
