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

package org.apache.jetspeed.daemon;

/**
@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: DaemonEntry.java,v 1.4 2004/02/23 02:48:57 jford Exp $
*/
public class DaemonEntry {
    
    private String  name;
    private long    interval = 0;
    private String  classname;
    private boolean onstartup = false;
    private boolean hasprocessed = false;
    private int     status = Daemon.STATUS_NOT_PROCESSED;

    public DaemonEntry( String name,
                        long interval,
                        String classname,
                        boolean onstartup ) {
        this.name = name;
        this.interval = interval;
        this.classname = classname;
        this.onstartup = onstartup;

    }
    
    /**
    The "name" of this Daemon.  This is the handle used within Jetspeed
    for representation.
    */
    public String getName() { return this.name; }

    /**
    The amount of time in seconds that this daemon should wait before
    processing again.
    */
    public long getInterval() { return this.interval; }

    /**
    The classname of this daemon
    */
    public String getClassname() { return this.classname; }

    /**
    Determine if this daemon should run on system startup.
    */
    public boolean onStartup() { return this.onstartup; }

    /**
    Return true if this daemon has processed at least once.

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: DaemonEntry.java,v 1.4 2004/02/23 02:48:57 jford Exp $
    */
    public boolean hasProcessed() { return this.hasprocessed; }

    public int getStatus() {
        return this.status;
    }
    
}
