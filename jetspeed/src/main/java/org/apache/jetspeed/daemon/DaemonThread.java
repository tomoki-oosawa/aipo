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

package org.apache.jetspeed.daemon;

import org.apache.jetspeed.services.daemonfactory.DaemonFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton </a>
 */
public class DaemonThread extends Thread {

  private Daemon daemon = null;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(DaemonThread.class.getName());

  /**
   */
  public DaemonThread(DaemonEntry entry) {

    super("DaemonThread:" + entry.getName());
    try {
      this.setDaemon(true);
      this.daemon = DaemonFactory.getDaemon(entry);
      this.setPriority(Thread.MIN_PRIORITY);
    } catch (DaemonException e) {
      // really screwed here.
      logger.error("Error instantiating DaemonThread", e);
    }
  }

  public DaemonThread() {
    super();
    this.setDaemon(true);
  }

  /**
   */
  public Daemon getDaemon() {
    return this.daemon;
  }

  /**
   */
  public void run() {
    // at the very minimum this daemon is processing...

    DaemonEntry de = this.getDaemon().getDaemonEntry();

    logger.info("DaemonThread: started processing daemon " + de.getName());

    if (de.onStartup()) {
      this.runDaemon(this.getDaemon());
    }

    while (true) {
      // move the seconds to miliseconds
      try {
        de = this.getDaemon().getDaemonEntry();
        synchronized (this) {
          this.wait(de.getInterval() * 1000);
        }

      } catch (InterruptedException e) {
        // this is a normal situation.
        // the DaemonFactory may want to stop this thread form
        // sleeping and call interrupt() on this thread.
        break;
      }
      if (this.isInterrupted()) {
        break;
      }

      this.runDaemon(this.getDaemon());
    }
  }

  /**
   */
  private void runDaemon(Daemon daemon) {

    daemon.setStatus(Daemon.STATUS_PROCESSING);

    logger.info("DaemonThread -> PROCESSING daemon -> "
        + daemon.getDaemonEntry().getName());

    try {
      daemon.run();
    } catch (Throwable t) {
      logger.error("Could not process Daemon: "
          + daemon.getDaemonEntry().getName(), t);
    }

    logger.info("DaemonThread -> *DONE* PROCESSING daemon -> "
        + daemon.getDaemonEntry().getName());

    daemon.setStatus(Daemon.STATUS_PROCESSED);

  }

}
