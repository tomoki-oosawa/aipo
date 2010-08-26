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
A simple interface to create Daemons within Jetspeed.  These are basically 
threads that get work done to facilitate content serving.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: Daemon.java,v 1.9 2004/02/23 02:48:57 jford Exp $
*/
public interface Daemon extends Runnable {

    public final static int STATUS_UNKNOWN          = 0;
    public final static int STATUS_NOT_PROCESSED    = 1;
    public final static int STATUS_PROCESSED        = 2;
    public final static int STATUS_PROCESSING       = 3;
    public final static int STATUS_NOT_STARTED      = 4;
    public final static int STATUS_STARTED          = 5;

    /**
    The result for this daemon is not yet known.
    */
    public final static int RESULT_UNKNOWN          = 0;

    /**
    A daemon has processed and it was successful
    */
    public final static int RESULT_SUCCESS          = 1;

    /**
    A daemon has processed but it has failed
    */
    public final static int RESULT_FAILED           = 2;

    /**
    A daemon is processing so its result is not yet known
    */
    public final static int RESULT_PROCESSING       = 3;
    
    /**
    Initialize this daemon providing configuration data.
    */
    public void init(DaemonConfig config, DaemonEntry entry);

    /**
    Require that Daemons have a getter for the DaemonConfig
    */
    public DaemonConfig getDaemonConfig();

    /**
    Report on the status of this daemon.  Should be of STATUS_NOT_PROCESSED, 
    STATUS_PROCESSED, or STATUS_PROCESSING 
    */
    public int getStatus();

    /**
    Force the status on this Daemon
    */
    public void setStatus(int status);
    
    /**
    Get the entry for this daemon.
    */
    public DaemonEntry getDaemonEntry();

    /**
    <p>
    Get the result of this daemons processing.  All Daemon implementations 
    are responsible for defining this.
    </p>
    
    <p>
    The default for this should be RESULT_UNKNOWN.  This usually means that
    this daemon has never been called for processing.  
    </p>
    */
    public int getResult();

    /**
    Force the result of this Daemon
    */
    public void setResult( int result );

    /**
    Provided so that a Daemon can provide a message to users.  Null if it has
    nothing to give to the user.
    */
    public String getMessage();
}

