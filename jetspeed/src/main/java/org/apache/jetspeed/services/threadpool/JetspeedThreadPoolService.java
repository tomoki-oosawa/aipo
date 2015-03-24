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

// Java Stuff
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;

/**
 * This is a Service that provides a simple threadpool usable by all thread
 * intensive classes in order to optimize resources utilization screen: <br>
 * 
 * <p>
 * It uses 3 parameters for contolling resource usage:
 * <dl>
 * <dt>init.count</dt>
 * <dd>The number of threads to start at initizaliation</dd>
 * <dt>max.count</dt>
 * <dd>The maximum number of threads started by this service</dd>
 * <dt>minspare.count</dt>
 * <dd>The pool tries to keep lways this minimum number if threads available
 * </dd>
 * </dl>
 * </p>
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton </a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala </a>
 */
public class JetspeedThreadPoolService extends TurbineBaseService implements
    ThreadPoolService {
  /**
   * Static initialization of the logger for this class
   */
  protected static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(JetspeedThreadPoolService.class.getName());

  /**
   * The number of threads to create on initialization
   */
  private int initThreads = 50;

  /**
   * The maximum number of threads that should ever be created.
   */
  private int maxThreads = 100;

  /**
   * The minimum amount of threads that should always be available
   */
  private int minSpareThreads = 15;

  /**
   * The default priority to use when creating new threads.
   */
  public static final int DEFAULT_THREAD_PRIORITY = Thread.MIN_PRIORITY;

  /**
   * Stores threads that are available within the pool.
   */
  private Vector availableThreads = new Vector();

  /**
   * The thread group used for all created threads.
   */
  private ThreadGroup tg = new ThreadGroup("JetspeedThreadPoolService");

  /**
   * Create a new queue for adding Runnable objects to.
   */
  private Queue queue = new Queue();

  /**
   * Holds the total number of threads that have ever been processed.
   */
  private int count = 0;

  public static final String SERVICE_NAME = "ThreadPool";

  /**
   * Constructor.
   * 
   * @exception Exception,
   *                a generic exception.
   */
  public JetspeedThreadPoolService() throws Exception {
  }

  /**
   * Late init. Don't return control until early init says we're done.
   */
  public void init() {
    while (!getInit()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        logger.info("ThreadPool service: Waiting for init()...");
      }
    }
  }

  /**
   * Called during Turbine.init()
   * 
   * @param config
   *            A ServletConfig.
   */
  public synchronized void init(ServletConfig config) {
    if (getInit()) {
      // Already inited
      return;
    }

    try {
      logger.info("JetspeedThreadPoolService early init()....starting!");
      initThreadpool(config);
      setInit(true);
      logger.info("JetspeedThreadPoolService early init()....finished!");
    } catch (Exception e) {
      logger.error("Cannot initialize JetspeedThreadPoolService!", e);
    }

    // we don't call setInit(true) yet, because we want init() to be called also
  }

  /**
   * Processes the Runnable object with an available thread at default priority
   * 
   * @see #process( Runnable, int )
   * @param runnable
   *            the runnable code to process
   */
  public void process(Runnable runnable) {

    process(runnable, Thread.MIN_PRIORITY);

  }

  /**
   * Process a Runnable object by allocating a Thread for it at the given
   * priority
   * 
   * @param runnable
   *            the runnable code to process
   * @param priority
   *            the priority used be the thread that will run this runnable
   */
  public void process(Runnable runnable, int priority) {

    RunnableThread thread = this.getAvailableThread();

    if (thread == null) {

      this.getQueue().add(runnable);

    } else {

      try {
        synchronized (thread) {
          // get the default priority of this Thread
          int defaultPriority = thread.getPriority();
          if (defaultPriority != priority) {
            // setting priority triggers security checks,
            // so we do it only if needed.
            thread.setPriority(priority);
          }
          thread.setRunnable(runnable);
          thread.notify();
        }
      } catch (Throwable t) {
        logger.error("Throwable", t);
      }

    }

  }

  /**
   * Get the number of threads that have been created
   * 
   * @return the number of threads currently created by the pool
   */
  public int getThreadCount() {
    return this.tg.activeCount();
  }

  /**
   * Get the number of threads that are available.
   * 
   * @return the number of threads available in the pool
   */
  public int getAvailableThreadCount() {
    return this.availableThreads.size();
  }

  /**
   * Get the current length of the Runnable queue, waiting for processing
   * 
   * @return the length of the queue of waiting processes
   */
  public int getQueueLength() {
    return this.getQueue().size();
  }

  /**
   * Get the number of threads that have successfully been processed for logging
   * and debugging purposes.
   * 
   * @return the number of processes executed since initialization
   */
  public int getThreadProcessedCount() {
    return this.count;
  }

  /**
   * Get the queue used by the JetspeedThreadPoolService
   * 
   * @return the queue holding the waiting processes
   */
  Queue getQueue() {
    return this.queue;
  }

  /**
   * Place this thread back into the pool so that it can be used again
   * 
   * @param thread
   *            the thread to release back to the pool
   */
  void release(RunnableThread thread) {

    synchronized (this.availableThreads) {

      this.availableThreads.addElement(thread);

      ++this.count;

      /*
       * It is important to synchronize here because it is possible that between
       * the time we check the queue and we get this another thread might return
       * and fetch the queue to the end.
       */
      synchronized (this.getQueue()) {

        // now if there are any objects in the queue add one for processing to
        // the thread that you just freed up.
        if (this.getQueue().size() > 0) {

          Runnable r = this.getQueue().get();

          if (r != null) {
            this.process(r);
          } else {
            logger.info("JetspeedThreadPoolService: no Runnable found.");
          }

        }

      }

    }

  }

  /**
   * This method initialized the ThreadPool
   * 
   * @param config
   *            A ServletConfig.
   */
  private void initThreadpool(ServletConfig config) {
    // Properties props = getProperties();

    try {

      // get configuration parameters from Jetspeed Resources
      ResourceService serviceConf = ((TurbineServices) TurbineServices
          .getInstance()).getResources(JetspeedThreadPoolService.SERVICE_NAME);

      this.initThreads = serviceConf.getInt("init.count", 10);
      this.maxThreads = serviceConf.getInt("max.count", 50);
      this.minSpareThreads = serviceConf.getInt("minspare.count", 10);

      // this.initThreads = Integer.parseInt( props.getProperty(
      // "services.ThreadPool.init.count" ) );
      // this.maxThreads = Integer.parseInt( props.getProperty(
      // "services.ThreadPool.max.count" ) );
      // this.minSpareThreads = Integer.parseInt( props.getProperty(
      // "services.ThreadPool.minspare.count" ) );

    } catch (NumberFormatException e) {
      logger.error("Invalid number format in properties", e);
    }

    // create the number of threads needed for initialization
    createThreads(this.initThreads);

  }

  /**
   * Create "count" number of threads and make them available.
   * 
   * @param count
   *            the number of threads to create
   */
  private synchronized void createThreads(int count) {

    // if the amount of threads you are about to create would end up being
    // greater than maxThreads then just cap this off to the end point so that
    // you end up with exactly maxThreads
    if (this.getThreadCount() < this.maxThreads
        && this.getThreadCount() + count > this.maxThreads) {

      count = this.maxThreads - this.getThreadCount();

    } else if (this.getThreadCount() >= this.maxThreads) {

      return;
    }

    logger.info("JetspeedThreadPoolService:  creating " + count
        + " more thread(s) for a total of: " + (this.getThreadCount() + count));

    for (int i = 0; i < count; ++i) {

      // RunnableThread has a static numbering counter
      RunnableThread thread = new RunnableThread(this.tg);
      thread.setPriority(DEFAULT_THREAD_PRIORITY);

      thread.start(); // The thread calls release to add...
      // SGP this.availableThreads.addElement( thread );

    }

  }

  /**
   * Get a thread that is available from the pool or null if there are no more
   * threads left.
   * 
   * @return a thread from the pool or null if non available
   */
  private RunnableThread getAvailableThread() {

    synchronized (this.availableThreads) {

      // if the current number of available threads is less than minSpareThreads
      // then we need to create more

      if (this.getAvailableThreadCount() < this.minSpareThreads) {
        this.createThreads(this.minSpareThreads);
      }

      // now if there aren't any threads available then just return null.
      if (this.getAvailableThreadCount() == 0) {
        return null;
      }

      RunnableThread thread = null;

      // get the element to use
      int id = this.availableThreads.size() - 1;

      thread = (RunnableThread) this.availableThreads.elementAt(id);
      this.availableThreads.removeElementAt(id);

      return thread;
    }

  }

}

/**
 * Handles holding Runnables until they are ready to be processed. This is an
 * impl of a FIFO (First In First Out) Queue. This makes it possible to add
 * Runnable objects so that they get processed and they pass through the queue
 * in a predictable fashion.
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton </a>
 */

class Queue {

  /**
   * Holds Runnables that have been requested to process but there are no
   * threads available.
   */
  private Vector queue = new Vector();

  /**
   * Add a Runnable object into the queue.
   * 
   * @param runnable
   *            the process to add to the queue
   */
  public synchronized void add(Runnable runnable) {
    queue.insertElementAt(runnable, 0);
  }

  /**
   * Get a Runnable object from the queue, and then remove it. Return null if no
   * more Runnable objects exist.
   * 
   * @return the first Runnable stored in the queue or null if empty
   */
  public synchronized Runnable get() {

    if (this.queue.size() == 0) {
      JetspeedThreadPoolService.logger
          .info("JetspeedThreadPoolService->Queue: No more Runnables left in queue.  Returning null");
      return null;
    }

    int id = queue.size() - 1;
    Runnable runnable = (Runnable) queue.elementAt(id);
    this.queue.removeElementAt(id);

    return runnable;
  }

  /**
   * Return the size of the queue.
   * 
   * @return the size of the queue
   */
  public int size() {
    return this.queue.size();
  }

}
