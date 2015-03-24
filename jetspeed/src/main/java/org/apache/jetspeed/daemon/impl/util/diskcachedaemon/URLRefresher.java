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

package org.apache.jetspeed.daemon.impl.util.diskcachedaemon;

// jetspeed stuff
import java.util.Enumeration;

import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.urlmanager.URLFetcher;

/**
 * <p>
 * URLRefresher that checks if a URL is updated remotely. If it is then it will
 * pull the new URL down and try to reinstantiate all Portlets that depend on
 * this URL.
 * </p>
 * 
 * @author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
 */
public class URLRefresher implements Runnable {

  private String url = null;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(URLRefresher.class.getName());

  /**
   * default constructor
   */
  public URLRefresher(String url) {

    this.url = url;

  }

  /**
   * Used within the ThreadPool... IE Runnable interface.
   */
  public void run() {

    try {

      // only update this if the URL on which it is based is newer
      // than the one on disk.
      if (URLFetcher.refresh(url)) {
        // now make sure that the entry that depends on this HREF
        // is updated in the PortletFactory.

        // create a Runnable for updating this Portlet in the cache.

        Enumeration enu = Registry.get(Registry.PORTLET).getEntries();

        while (enu.hasMoreElements()) {
          PortletEntry entry = (PortletEntry) enu.nextElement();

          if ((entry.getURL() != null) && entry.getURL().equals(url)) {
            try {
              PortletRefresher pr = new PortletRefresher(entry);

              // now put this Instantiator in the ThreadPool so that
              // it can execute.
              // SGP Trying to intersperse CPU intensive tasks with
              // network intensive tasks
              pr.run();
              // ThreadPool.process( pr );
            } catch (Throwable t) {
              // FIXME: Put it in the bad URL list
              logger.info("DiskCacheDaemon: Error refreshing: " + url);
            }
          }
        }
      }
    } catch (Throwable t) {
      logger.error("Error refreshing URL", t);
    }
  }
}
