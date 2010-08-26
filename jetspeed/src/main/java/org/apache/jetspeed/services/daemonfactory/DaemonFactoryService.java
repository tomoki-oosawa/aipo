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

package org.apache.jetspeed.services.daemonfactory;

import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.daemon.DaemonContext;
import org.apache.jetspeed.daemon.DaemonException;
import org.apache.jetspeed.daemon.DaemonNotFoundException;
import org.apache.turbine.services.Service;

/**
@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: DaemonFactoryService.java,v 1.4 2004/02/23 03:29:24 jford Exp $
*/
public interface DaemonFactoryService extends Service {
    
    public String SERVICE_NAME = "DaemonFactory";
    
    /**
    <p>
    Starts any daemons that need processing.
    </p>
    
    <p>
    This should be called right after init() so that any daemons that need to be 
    started will be.  If you need to do any per-daemon initialization then do so 
    before calling start()
    </p>
    */
    public void start();    
    
    /**
    Allows a Daemon to define its Thread priority through a factory.  The Thread
    that this object should return should be an implementation of itself.
    */
    public Daemon getDaemon( DaemonEntry entry ) throws DaemonException;

    /**
    Get a daemon with the given classname.
    
    @see    #getDaemon( DaemonEntry entry )
    */
    public Daemon getDaemon( String classname ) throws DaemonException;

    /**
    */
    public DaemonContext getDaemonContext();

    /**
    Kicks of processing of a Daemon.  Does the same thing as getDaemon() but
    also creates a thread and runs the daemon.
    */
    public void process( DaemonEntry entry ) throws DaemonException;

    /**
    */
    public int getStatus(DaemonEntry entry);

    /**
    Get the last known result of the given DaemonEntry's processing
    */
    public int getResult(DaemonEntry entry);

    /**
    Get the last known message of the given DaemonEntry's processing
    */
    public String getMessage( DaemonEntry entry );
    
    /**
    Get the current known DaemonEntries within the DaemonFactory
    */
    public DaemonEntry[] getDaemonEntries();

    /**
    Given the name of a DaemonEntry... get it from the DaemonFactory 
    */
    public DaemonEntry getDaemonEntry(String name) 
        throws DaemonNotFoundException;
    
}
