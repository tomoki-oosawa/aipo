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

package org.apache.jetspeed.services.urlmanager;

// Java classes
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.TurbineBaseService;

/**
 * <p>
 * This implementation of the URLManagerService is backed by a simple map
 * persisted on disk in a properties file
 * </p>
 * Added: Support for proxies. <br>
 * Example: (Set in <code>JetspeedResources.properties</code>)<br>
 * <code>services.URLManager.proxy.http.host=myproxy.mydomain</code><br>
 * <code>services.URLManager.proxy.http.port=81</code><br>
 * 
 * @see URLManagerService
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
 * @version $Id: JetspeedURLManagerService.java,v 1.16 2004/02/23 03:30:47 jford
 *          Exp $
 */
public class JetspeedURLManagerService extends TurbineBaseService implements
    URLManagerService {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(JetspeedURLManagerService.class.getName());

  /**
   * Map used to store all URL Information.
   */
  private Map urls = new HashMap();

  /**
   * Path to the properties file used for persisting the data
   */
  private String path = null;

  /**
   * Hashtable to store proxy configuration in
   */
  private final Hashtable proxies = new Hashtable();

  /**
   * Late init. Don't return control until early init says we're done.
   */
  @Override
  public void init() {
    while (!getInit()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        logger.info("URLManager service: Waiting for init()...");
      }
    }

  }

  /**
   * Called during Turbine.init()
   * 
   * @param config
   *          A ServletConfig.
   */
  @Override
  public synchronized void init(ServletConfig config) {
    // We have already been initialized...
    if (getInit()) {
      return;
    }

    try {
      logger.info("JetspeedURLManagerService early init()....starting!");

      // Proxy Settings are stored as
      // 'services.URLManager.proxy.<protocol>.port' and as
      // 'services.URLManager.proxy.<protocol>.port' in
      // JetspeedResource.properties.
      // Get a list of settings and store them in the hashtable
      String prefix = "services." + URLManagerService.SERVICE_NAME + ".proxy.";
      Iterator resourceKeys = JetspeedResources.getKeys(prefix);

      String key, hashKey;
      Object hashValue = null;
      while (resourceKeys.hasNext()) {
        key = (String) resourceKeys.next();
        hashKey = key.substring(prefix.length()).toLowerCase();
        if (hashKey.endsWith(".host")) {
          hashValue = JetspeedResources.getString(key);
          proxies.put(hashKey, hashValue);
        } else if (hashKey.endsWith(".port")) {
          hashValue = new Integer(JetspeedResources.getInt(key));
          proxies.put(hashKey, hashValue);
        }
      }

      path =
        JetspeedResources.getString("services."
          + URLManagerService.SERVICE_NAME
          + ".url");

      if (path == null) {
        String tempdir = new String("WEB-INF/conf/datasources.properties");
        String ps = System.getProperty("file.separator");

        try {
          ServletContext sc = config.getServletContext();
          tempdir =
            sc.getAttribute("javax.servlet.context.tempdir").toString()
              + ps
              + "jetspeed"
              + ps
              + "conf"
              + ps
              + "datasources.properties";
          logger
            .debug("URLMangler: will create file in servlet temp directory "
              + tempdir);
        } catch (Exception e) {
          logger
            .debug("URLMangler: problems creating file in servlet temp directory "
              + " falling back to WEB-INF/conf : "
              + e);
        }
        path = tempdir;
      } else {
        logger.debug("URLMangler: will create file in user configured " + path);
        path = config.getServletContext().getRealPath(path);
        // should test for writability here and fallback to servlet tmp
        // directory on failure
      }

      load();
      logger.info("JetspeedURLManagerService early init()....finished!");
    } catch (Throwable t) {
      logger.error("Cannot initialize JetspeedURLManagerService!", t);
    }
    setInit(true);

  }

  /**
   * Called during Turbine destroy(). Persist the Manager state to disk
   */
  @Override
  public void shutdown() {
    save();
  }

  /**
   * Registers a new URL record. If the url is already registered in the system,
   * doesn't modify the current record.
   * 
   * @param url
   *          the url to register
   */
  public void register(String url) {
    if (url != null) {
      URLInfo info = getInfo(url);
      if (info == null) {
        register(new URLInfo(url, URLManagerService.STATUS_OK));
      }
    }
  }

  /**
   * Registers a new URL record. If the url is already registered in the system,
   * updates the status of this URL info record
   * 
   * @param url
   *          the url to register
   * @param status
   *          the status of this url
   */
  public void register(String url, int status) {
    if (url != null) {
      URLInfo info = getInfo(url);
      if (info == null) {
        register(new URLInfo(url, status));
      } else {
        info.setStatus(status);
      }
    }
  }

  /**
   * Registers a new URL record. If the url is already registered in the system,
   * updates both the status and the message of this URL info record
   * 
   * @param url
   *          the url to register
   * @param status
   *          the status of this url
   * @param message
   *          a descriptive message of the status
   */
  public void register(String url, int status, String message) {
    if (url != null) {
      URLInfo info = getInfo(url);
      if (info == null) {
        register(new URLInfo(url, status, message));
      } else {
        info.setStatus(status);
        info.setMessage(message);
      }
    }
  }

  /**
   * Register or replace an URL record. All records are keyed to the imutable
   * URL of URLInfo.
   * 
   * @param info
   *          the info record to store
   */
  public void register(URLInfo info) {
    if (info != null) {
      synchronized (urls) {
        if (getInfo(info.getURL()) == null) {
          urls.put(info.getURL().intern(), info);
        }
      }
    }
  }

  /**
   * Unregister an URL from the repository
   * 
   * @param url
   *          the url to remove
   */
  public void unregister(String url) {
    if (url != null) {
      synchronized (urls) {
        urls.remove(url.intern());
      }
    }
  }

  /**
   * Get the information record stored in the database about an URL.
   * 
   * @param url
   *          the url whose record is sought
   * @return the description record found in the repository or null.
   */
  public URLInfo getInfo(String url) {
    URLInfo info = null;

    if (url != null) {
      synchronized (urls) {
        info = (URLInfo) urls.get(url.intern());
      }
    }

    return info;
  }

  /**
   * Test whether the URL is currently believed to be OK by this repository.
   * 
   * @param url
   *          the url to be tested
   * @return false is the url is known by this repository and has a status
   *         indicating an error, true otherwise.
   */
  public boolean isOK(String url) {
    URLInfo info = getInfo(url);

    // we don't know this URL, play it safe and say it's good
    if (info == null) {
      return true;
    }

    return (info.getStatus() == URLManagerService.STATUS_OK);
  }

  /**
   * List of the current known URLs in the repository
   * 
   * @return a List of URL strings known to this repository
   */
  public List list() {
    synchronized (urls) {
      return new Vector(urls.keySet());
    }
  }

  /**
   * List of the current known URLs in the repository which have the given
   * status.
   * 
   * @param status
   *          the status to be retrieved. May be
   *          {@link URLManagerService#STATUS_ANY} to indicate any status
   * @return a List of URL strings known to this repository with this status
   */
  public List list(int status) {
    Vector result = new Vector();

    synchronized (urls) {
      Iterator i = urls.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry entry = (Map.Entry) i.next();
        URLInfo info = (URLInfo) entry.getValue();
        if ((info.getStatus() & status) != 0) {
          result.addElement(entry.getKey());
        }
      }
    }

    return result;
  }

  /**
   * Load the persisted state of the repository from disk
   */
  private synchronized void load() {

    Map store = new HashMap();
    // Configuration config = null;

    logger.info("Restoring the URLs from disk: " + path);

    try {
      /*-
        config = new Configuration( path );

        int count = 1;
        String url = null;

        while ( ( url = ( config
                          .getString("entry."+count+".url") ) ) != null ) {
            //Intern the url to ensure we can use "==" to compare
            //and synchronize on it
            url = url.intern();
            int status = config.getInteger("entry."+count+".status", URLManagerService.STATUS_OK );
            if( store.get( url ) == null )
                store.put( url, new URLInfo( url, status ) );
            count++;
        }

        logger.info( "URLManager loaded " + count + " urls" );                    
       */
    } catch (Exception e) {
      logger.error("Could not restore URLManager state", e);
      return;
    } finally {
      // set the loaded store as the new store
      this.urls = store;
    }

  }

  /**
   * Persist the state of the repository on disk in a properties file
   */
  private synchronized void save() {
    PrintWriter pw = null;

    try {

      File propfile = new File(path); // FileWriter doesn't always do this
      propfile.getParentFile().mkdirs();
      propfile.createNewFile();

      pw = new PrintWriter(new BufferedWriter(new FileWriter(propfile)));
      synchronized (urls) {
        Iterator i = urls.values().iterator();
        int entryNum = 1;
        while (i.hasNext()) {
          URLInfo info = (URLInfo) i.next();
          pw.print("entry.");
          pw.print(entryNum);
          pw.print(".url=");
          writeEscaped(pw, info.getURL());
          pw.println("");
          pw.print("entry.");
          pw.print(entryNum);
          pw.print(".status=");
          pw.print(info.getStatus());
          pw.println("");
          entryNum++;
        }
      }
    } catch (Throwable t) {
      logger.error("Impossible to save URLManager state to " + path, t);
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  /**
   * Return the port of a proxy
   * 
   * @param protocol
   *          The protocol that the proxy supports, e.g. 'http'
   * @return The port number (1-65535), or -1 if no port was specified (= use
   *         default)
   */
  public int getProxyPort(String protocol) {
    Integer proxyPort =
      (Integer) proxies.get((protocol + ".port").toLowerCase());

    if (proxyPort != null) {
      return proxyPort.intValue();
    } else {
      return -1;
    }
  }

  /**
   * Return a proxy's hostname
   * 
   * @param protocol
   *          The protocol that the proxy supports, e.g. 'http'
   * @return The hostname of the proxy, or <code>null</code> if no proxy is
   *         specified for this protocol
   */
  public String getProxyHost(String protocol) {
    String proxyHost = (String) proxies.get((protocol + ".host").toLowerCase());

    return proxyHost;
  }

  /**
   * <p>
   * Escape values when saving. Appends a String to a StringBuffer, escaping
   * commas.
   * </p>
   * <p>
   * We assume that commas are unescaped.
   * </p>
   * 
   * @param sink
   *          a StringBuffer to write output
   * @param element
   *          a value to be written
   */
  protected void writeEscaped(PrintWriter sink, String element) {
    int upTo = element.indexOf(",");
    if (upTo == -1) {
      sink.print(element);
      return;
    }
    sink.print(element.substring(0, upTo));
    sink.print("\\,");
    writeEscaped(sink, element.substring(upTo + 1, element.length()));
    return;
  }
}
