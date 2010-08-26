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

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.search.AbstractObjectHandler;
import org.apache.jetspeed.services.search.BaseParsedObject;
import org.apache.jetspeed.services.search.ParsedObject;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;

/**
 * This object handler deals with portlets
 * 
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: PortletToDocHandler.java,v 1.4 2004/02/23 03:47:46 jford Exp $
 */
public class PortletToDocHandler extends AbstractObjectHandler
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletToDocHandler.class.getName());
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.search.ObjectHandler#parseObject(java.lang.Object)
     */
    public ParsedObject parseObject(Object o)
    {
        ParsedObject result = new BaseParsedObject();

        if ((o instanceof Portlet) == false)
        {
            logger.error("PortletToDocHandler: invalid object type: " + o);
            return null;
        }

        Portlet portlet = (Portlet) o;
        
        
        JetspeedRunDataService rds = (JetspeedRunDataService) ((TurbineServices) TurbineServices.getInstance())
                                                                                  .getResources(RunDataService.SERVICE_NAME);
        result.setContent(portlet.getContent(rds.getCurrentRunData()).toString());
        result.setDescription(portlet.getDescription());
        result.setType(this.getClass().getName());
        //result.setFields();
        result.setKey(portlet.getName());
        //result.setKeywords();
        //result.setLanguage();
        result.setTitle(portlet.getTitle());
        //result.setURL();
        
        return result;
    }
}
