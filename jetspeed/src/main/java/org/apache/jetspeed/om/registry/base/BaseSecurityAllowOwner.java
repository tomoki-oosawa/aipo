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
import org.apache.jetspeed.om.registry.base.BaseSecurityAllow;

/**
 * Interface for manipulatin the Security Allow on the registry entries
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: BaseSecurityAllowOwner.java,v 1.2 2004/02/23 03:08:26 jford Exp $
 */
public class BaseSecurityAllowOwner extends BaseSecurityAllow
{
    /** Creates new BaseSecurityAllow */
    public BaseSecurityAllowOwner()
    {
        super(true);
    }
}
