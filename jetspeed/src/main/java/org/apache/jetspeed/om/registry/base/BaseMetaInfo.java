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
 * Bean like implementation of the Metainfo interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.MetaInfo
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: BaseMetaInfo.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseMetaInfo implements MetaInfo, java.io.Serializable
{
    private String title;

    private String description;

    private String image;

    public BaseMetaInfo()
    {}

    public BaseMetaInfo(String title, String description, String image)
    {
        this.title = title;
        this.description = description;
        this.image = image;
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

        BaseMetaInfo obj = (BaseMetaInfo)object;

        if (title!=null)
        {
            if (!title.equals(obj.getTitle()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getTitle()!=null)
            {
                return false;
            }
        }

        if (description!=null)
        {
            if(!description.equals(obj.getDescription()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getDescription()!=null)
            {
                return false;
            }
        }

        if (image!=null)
        {
            if(!image.equals(obj.getImage()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getImage()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the title for this entry */
    public String getTitle()
    {
        return this.title;
    }

    /** Sets the title for this entry
     * @param title the new title for this entry
     */
    public void setTitle( String title )
    {
        this.title = title;
    }

    /** @return the description for this entry */
    public String getDescription()
    {
        return this.description;
    }

    /** Sets the description for this entry
     * @param description the new description for this entry
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /** @return the image link for this entry */
    public String getImage()
    {
        return this.image;
    }

    /** Sets the image URL attached to this entry
     * @param image the image URL to link to this entry
     */
    public void setImage( String image )
    {
        this.image = image;
    }

}
