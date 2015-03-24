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

package org.apache.jetspeed.daemon.impl.util.feeddaemon;

//jetspeed stuff
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.threadpool.ThreadPool;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.om.registry.PortletEntry;


/**
Handles taking all known Portlets and then instantiatles them all so that 
everything is in memory.

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: EntryInstantiator.java,v 1.16 2004/02/23 02:47:27 jford Exp $
*/
public class EntryInstantiator {

    private PortletEntry[] entries = null;
    
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(EntryInstantiator.class.getName());
    
    /**
    Create a EntryInstantiator for processing.
    */
    public EntryInstantiator( PortletEntry[] entries ) {
        super();
        this.entries = entries;
    }
    
    public void process() {

        if ( logger.isInfoEnabled() )
        {
            logger.info( "BEGIN: EntryInstantiator:  Instantiating " + 
                  Registry.get(Registry.PORTLET).getEntryCount() + 
                  " Portlet(s) found in the PortletRegistry" );
        }
        
        for( int i = 0; i < entries.length; ++i ) {

            Instantiator inst = new Instantiator( i, entries[i] );
            ThreadPool.process( inst );

        }
        
        logger.info( "END: EntryInstantiator:  Instantiating all Portlets found in the PortletRegistry" );
        
    }
    
}




