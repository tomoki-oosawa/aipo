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

// Java APIs
import java.util.HashMap;
import java.util.Iterator;

// Jetspeed APIs
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.search.ParsedObject;
import org.apache.jetspeed.om.registry.Category;

/**
 * This object handler deals with portlet registry entries
 * 
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: PortletEntryToDocHandler.java,v 1.5 2004/02/23 03:47:46 jford Exp $
 */
public class PortletEntryToDocHandler extends RegistryEntryToDocHandler
{
    private static final String PARENT = "parent";
    private static final String TYPE = "type";
    
    {
        fields.add(PARENT);
        fields.add(TYPE);
    }
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletEntryToDocHandler.class.getName());
    
    /**
     * Parses portlet entry object
     * 
     * @param o
     * @return 
     */
    public ParsedObject parseObject(Object o)
    {
        ParsedObject result = super.parseObject(o);
        
        if ((o instanceof PortletEntry) == false)
        {
            logger.error("PortletEntryToDocHandler: invalid object type: " + o);
            return null;
        }

        PortletEntry portletEntry = (PortletEntry) o;
        
        HashMap fields = new HashMap();
        fields.put(PARENT, portletEntry.getParent());
        fields.put(TYPE, portletEntry.getType());
        
        result.setFields(fields);

        StringBuffer content = new StringBuffer();
        String title = portletEntry.getTitle();
        content.append(title == null ? portletEntry.getName() : title);
        content.append(" ");
        content.append(portletEntry.getDescription());
        content.append(" ");
        Iterator it = portletEntry.listCategories();
        while (it.hasNext())
        {
            Category cat = (Category) it.next();
            content.append(cat.getName());
            content.append(" ");
        }

        result.setContent(content.toString());

        result.setType(ParsedObject.OBJECT_TYPE_PORTLET);

        // TODO: index the url for portlets defining one. A good candidate would be HTML, Webpage
        // and IFrame portlets.
        
        return result;
    }
}
