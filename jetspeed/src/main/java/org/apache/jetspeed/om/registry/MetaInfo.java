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

package org.apache.jetspeed.om.registry;

/**
 * Interface for storing meta-info on a registry entry
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: MetaInfo.java,v 1.2 2004/02/23 03:11:39 jford Exp $
 */
public interface MetaInfo
{

    /** @return the title for this entry */         
    public String getTitle();

    /** Sets the title for this entry
     * @param title the new title for this entry
     */                    
    public void setTitle( String title );

    /** @return the description for this entry */         
    public String getDescription();

    /** Sets the description for this entry
     * @param description the new description for this entry
     */                    
    public void setDescription( String description );

    /** @return the image link for this entry */         
    public String getImage();

    /** Sets the image URL attached to this entry
     * @param image the image URL to link to this entry
     */                    
    public void setImage( String image );
}
