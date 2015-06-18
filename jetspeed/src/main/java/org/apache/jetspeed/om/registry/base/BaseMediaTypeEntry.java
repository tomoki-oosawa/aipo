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

import org.apache.jetspeed.om.registry.*;

/**
 * Default bean like implementation of MediaTypeEntry interface
 * suitable for serializing with Castor
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseMediaTypeEntry.java,v 1.6 2004/02/23 03:08:26 jford Exp $
 */
public class BaseMediaTypeEntry extends BaseRegistryEntry
    implements MediaTypeEntry
{

    protected String mimeType;
    protected String characterSet;
    private CapabilityMap capabilities = new BaseCapabilityMap();

    public BaseMediaTypeEntry()
    {}

    public BaseMediaTypeEntry(long id,
                              String name,
                              int _hidden,
                              String mimeType,
                              String title,
                              String description,
                              String image,
                               String role)
    {
        super(id, name, _hidden, title, description, image, role);

        this.mimeType = mimeType;
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

        BaseMediaTypeEntry obj = (BaseMediaTypeEntry)object;

        if (mimeType!=null)
        {
            if (!mimeType.equals(obj.mimeType))
            {
                return false;
            }
        }
        else
        {
            if (obj.mimeType!=null)
            {
                return false;
            }
        }

        if (characterSet!=null)
        {
            if (!characterSet.equals(obj.characterSet))
            {
                return false;
            }
        }
        else
        {
            if (obj.characterSet!=null)
            {
                return false;
            }
        }

        if (!capabilities.equals(obj.capabilities))
        {
            return false;
        }

        return super.equals(object);
    }

    /** @return the mime type associated with this MediaType */
    public String getMimeType()
    {
        return this.mimeType;
    }

    /** Sets the MimeType associated with this MediaType
     *  @param mimeType the MIME type to associate
     */
    public void setMimeType( String mimeType )
    {
        this.mimeType = mimeType;
    }

    /** @return the character set associated with this MediaType */
    public String getCharacterSet()
    {
        return this.characterSet;
    }

    /** Sets the character set associated with this MediaType */
    public void setCharacterSet( String charSet)
    {
        this.characterSet = charSet;
    }

    public CapabilityMap getCapabilityMap()
    {
        return capabilities;
    }

    // castor related method definitions

    public BaseCapabilityMap getCapabilities()
    {
        return (BaseCapabilityMap)capabilities;
    }

    public void setCapabilities(BaseCapabilityMap capabilities)
    {
        this.capabilities = capabilities;
    }
}
