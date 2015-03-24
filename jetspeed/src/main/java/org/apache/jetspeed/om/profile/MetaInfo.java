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
 
package org.apache.jetspeed.om.profile;

import java.io.Serializable;
/**
 * Interface describing meta info for an entry. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: MetaInfo.java,v 1.4 2004/02/23 03:05:01 jford Exp $
 */
public interface MetaInfo extends Serializable, Cloneable
{
    
    /** @return the parameter's title */
    public String getTitle();

    /** Sets the descsription of this parameter.value.
     * 
     * @param description the new title
     */
    public void setTitle(String title);

    /** @return the parameter's description */
    public String getDescription();

    /** Sets the descsription of this parameter.value.
     * 
     * @param description the new description
     */
    public void setDescription(String description);

    /** @return the image name for this parameter */
    public String getImage();

    /** Sets the image name of this parameter.
     * 
     * @param image the new parameter value
     */
    public void setImage(String image);

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}