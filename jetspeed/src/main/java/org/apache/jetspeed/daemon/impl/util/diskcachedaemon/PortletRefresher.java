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

package org.apache.jetspeed.daemon.impl.util.diskcachedaemon;

//jetspeed stuff
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.urlmanager.URLManager;

//turbine stuff
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
<p>
PortletRefresher updates a Entry within the Portlet registry with content
that has been updating on disk.
</p>

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: PortletRefresher.java,v 1.17 2004/02/23 02:47:00 jford Exp $
*/
public class PortletRefresher implements Runnable {
   
    private PortletEntry entry = null;
   
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletRefresher.class.getName());
    
    /**
    Given an Entry and RunData, create a PortletRefresher
    */
    public PortletRefresher( PortletEntry entry ) {
        this.entry = entry;
    }
   
    /**
    */
    public void run() {
       
        /* Code to avoid putting it in cache if it not required... */
        try {
            if ( JetspeedResources.getBoolean( JetspeedResources.AUTOCREATE_PORTLETS_KEY ) ) {
       
                PortletFactory.getPortlet( this.entry.getName(), "0" );
 
            } 
            
        } catch ( Throwable t ) {
            logger.error("Error getting portlet", t);

            URLManager.register( this.entry.getURL(), URLManager.STATUS_BAD, t.toString() );
            Registry.removeEntry( Registry.PORTLET, this.entry.getName() );
           
        }
       
    }
    
}

