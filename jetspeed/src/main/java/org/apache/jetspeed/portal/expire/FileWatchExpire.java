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

package org.apache.jetspeed.portal.expire;

//java stuff
import java.io.IOException;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
Handles expiration mechanisms that expire when the file changes.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: FileWatchExpire.java,v 1.10 2004/02/23 03:24:40 jford Exp $
*/
public class FileWatchExpire extends BaseExpire 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(FileWatchExpire.class.getName());    
    
    FileWatcher fw = null;
    
    /**
    @see Expire#isExpired
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: FileWatchExpire.java,v 1.10 2004/02/23 03:24:40 jford Exp $
    */
    public boolean isExpired() {
        
        if ( this.fw != null ) {
            return this.fw.hasChanged();
        } else {
            return false;
        }
        
        
    }
    
    /**
    Set the url on which this depends.  It is required that you call this 
    method before you use it.
    */
    public void setURL( String url ) {
        try {
            this.fw = new FileWatcher( url, this.getPortlet().getName() );
        } catch ( IOException e ) {
            logger.error("Exception",  e);
        }
    }
    
    
}
