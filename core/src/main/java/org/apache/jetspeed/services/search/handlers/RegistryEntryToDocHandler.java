/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.services.search.handlers;

import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.search.BaseParsedObject;
import org.apache.jetspeed.services.search.AbstractObjectHandler;
import org.apache.jetspeed.services.search.ParsedObject;


/**
 * This object handler deals with registry entries
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id: RegistryEntryToDocHandler.java,v 1.4 2004/02/23 03:47:46 jford Exp $
 */
public class RegistryEntryToDocHandler extends AbstractObjectHandler
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(RegistryEntryToDocHandler.class.getName());
    
    /**
     * @see org.apache.jetspeed.services.search.ObjectHandler#parseObject(java.lang.Object)
     * @param o
     * @return 
     */
    public ParsedObject parseObject(Object o)
    {
        ParsedObject result = new BaseParsedObject();

        if ((o instanceof RegistryEntry) == false)
        {
            logger.error("RegistryEntryToDocHandler: invalid object type: " + o);
            return null;
        }

        RegistryEntry regEntry = (RegistryEntry) o;
        String desc = regEntry.getDescription();
        result.setDescription(desc == null ? regEntry.getName() : desc);
        result.setKey(regEntry.getName());
        String title = regEntry.getTitle();
        result.setTitle(title == null ? regEntry.getName() : title);
        
        result.setClassName(o.getClass().getName());
        
        return result;
    }
}
