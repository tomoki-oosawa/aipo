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

package org.apache.jetspeed.portal.portlets;

//jetspeed
import org.apache.jetspeed.portal.expire.Expire;
import org.apache.jetspeed.portal.expire.ExpireFactory;
import org.apache.jetspeed.portal.expire.FileWatchExpire;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.JetspeedException;


/**
Just like AbstractPortlet except that when its URL is modified on disk it 
automatically expires itself.

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: FileWatchPortlet.java,v 1.9 2004/02/23 04:03:34 jford Exp $
*/
public abstract class FileWatchPortlet extends AbstractInstancePortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(FileWatchPortlet.class.getName());    
    
    FileWatchExpire expire = null;
    
    /**
    Expire this Portlet if it's URL Changes on disk.
    */
    public Expire getExpire() {
        
        try {

            if ( this.expire == null ) {
            
                this.expire = (FileWatchExpire)ExpireFactory
                    .getExpire( this, ExpireFactory.FILE_WATCH_EXPIRE );
                
                this.expire.setURL( this.getPortletConfig().getURL() );
            }

            return this.expire;

        } catch ( JetspeedException e ) {
            logger.error("Exception",  e);
            return null;
        }

        
    }
    
}

