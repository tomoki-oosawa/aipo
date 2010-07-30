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

package org.apache.jetspeed.services.idgenerator;

/**
 * This service handles the generation of unique identifiers
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: IdGeneratorService.java,v 1.2 2004/02/23 03:28:57 jford Exp $
 */

// Turbine
import org.apache.turbine.services.Service;

public interface IdGeneratorService extends Service
{

    public String SERVICE_NAME = "IdGenerator";

    /**
     * Generate a Unique PEID
     *
     * @return Unique PEID
     */    
    public String getNextPeid();
    
}

