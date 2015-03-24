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

package org.apache.jetspeed.services.threadpool;

// turbine stuff
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;

/**
 * A thread that is used to process Runnables. This thread will wait until it is
 * notified by another thread that it needs processing. However it will only
 * process if getRunnable != null.
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 */
public class RunnableThread extends Thread {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(RunnableThread.class.getName());

  private boolean running = false;

  private static int next_id = 0;

  /**
   * Increment a counter so that we can identify threads easily.
   */
  private static synchronized int getNextId() {
    return ++next_id;
  };

  private int id = 0;

  /**
   * The runnable that you want to process
   */
  private Runnable runnable = null;

  public RunnableThread() {
    super();
    this.setDaemon(true);
  }

  /**
   * Creates a new Thread in the specified threadgroup
   * 
   * @param tg
   *            the Threadgroup which will contain the new Thread
   */
  public RunnableThread(ThreadGroup tg) {
    super(tg, "Provisory");
    this.id = getNextId();
    super.setName("RunnableThread:" + this.id);
    this.setPriority(Thread.MIN_PRIORITY);
    this.setDaemon(true);

  }

  /**
   * Creates a new Thread in the specified threadgroup and assigns it an id.
   * 
   * @param tg
   *            the Threadgroup which will contain the new Thread
   * @param id
   *            an identifier for the new Thread
   */
  public RunnableThread(ThreadGroup tg, int id) {

    super(tg, "RunnableThread:" + id);
    this.setPriority(Thread.MIN_PRIORITY);
    this.setDaemon(true);
    this.id = id;
  }

  /**
   * Processes the Runnable object assigned to it, whenever one is available
   */
  public void run() {

    /*
     * FIXME: move to a static class variable to allow for pool shutdown
     */
    boolean poolrunning = true;
    while (poolrunning) {

      // On creation, we are idle.
      // So, add ourselves to the Pool.
      // Next times we come here, we are just finished
      // one run...
      this.setRunning(false);

      this.setRunnable(null);

      synchronized (this) {
        if (this.getPriority() != JetspeedThreadPoolService.DEFAULT_THREAD_PRIORITY) {
          // give the thread back it's default priority.
          this.setPriority(JetspeedThreadPoolService.DEFAULT_THREAD_PRIORITY);
        }

        // SGP I don't think it is needed. The scheduler will do its job
        // and the thread will be released sooner. Later, it will wait
        // until the Pool reuses it. Correct me if I'm wrong
        // but please comment the reasons, as I don't get it :)

        // yield this thread so that other threads can now execute
        // if necessary.
        // this.yield();

        // ok... add this thread back into the thread pool
        ((JetspeedThreadPoolService) TurbineServices.getInstance().getService(
            ThreadPoolService.SERVICE_NAME)).release(this);

        // if the runnable == null wait because it has been not been
        // directly assigned a task..
        if (this.getRunnable() == null) {

          try {
            this.wait();
          } catch (InterruptedException e) {
            // this is a normal situation.
            // the DaemonFactory may want to stop this thread form
            // sleeping and call interrupt() on this thread.
          } catch (Throwable t) {
            logger.error("Throwable", t);
            // continue;
          }

        }

      }

      if (this.getRunnable() != null) {
        this.setRunning(true);

        try {

          this.getRunnable().run();

        } catch (Throwable t) {
          logger.error("A problem occured while trying to run your thread", t);
        }

      }

    }

  }

  // misc getters/setters

  /**
   * Set the Runnable process to execute
   * 
   * @param runnable
   *            the Object to execute
   */
  public void setRunnable(Runnable runnable) {
    this.runnable = runnable;
  }

  /**
   * Get the Runnable process executing
   * 
   * @return the Object executed by this thread
   */
  public Runnable getRunnable() {
    return this.runnable;
  }

  /**
   * Test whether the thread is currently executing a process
   * 
   * @return the status of this thread. If true, the thread is currently
   *         executing a Runnable process, if false it's waiting for a new
   *         process
   */
  private boolean isRunning() {
    return this.running;
  }

  /**
   * Set the running status of this thread.
   * 
   * @return the status of this thread
   */
  private void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * Get the numeric identifier of this thread
   * 
   * @return the identifier of the thread
   */
  public long getId() {
    return this.id;
  }

}
