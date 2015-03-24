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

package org.apache.jetspeed.om.profile.psml;

import org.apache.jetspeed.om.profile.*;

/**
 * Bean like implementation of a meta-info repository
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PsmlMetaInfo.java,v 1.4 2004/02/23 03:02:54 jford Exp $
 */
public class PsmlMetaInfo implements MetaInfo
{     
    private String title = null;
     
    private String description = null;
         
    private String image = null;

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
    
    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();
        return cloned;

    }   // clone
}
