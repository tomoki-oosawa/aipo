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

package org.apache.jetspeed.services.portletcache;

import javax.servlet.ServletConfig;

// Jetspeed
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
// Turbine
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;
import org.apache.turbine.services.cache.Refreshable;
import org.apache.turbine.services.cache.RefreshableCachedObject;

/**
 * <P>
 * This implementation of the PortletCache service is a simple adapter to the
 * Turbine GlobalCacheService
 * </p>
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: JetspeedPortletCacheService.java,v 1.10 2004/02/23 03:34:54
 *          jford Exp $
 */
public class JetspeedPortletCacheService extends TurbineBaseService implements
    PortletCacheService {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(JetspeedPortletCacheService.class.getName());

  private static int DefaultTimeToLiveMillis = (JetspeedResources.getInt(
    TurbineServices.SERVICE_PREFIX
      + PortletCacheService.SERVICE_NAME
      + ".TimeToLive.default",
    (30 * 60 * 1000))); // 30 minutes

  /**
   * Called during Turbine.init()
   *
   * @param config
   *          A ServletConfig.
   */
  @Override
  public void init(ServletConfig config) {
    try {
      logger.info("JetspeedPortletCacheService early init()....starting!");
      if (DefaultTimeToLiveMillis < 0) {
        logger
          .info("JetspeedPortletCacheService - By default refreshable objects will live for ever");
      } else {
        logger
          .info("JetspeedPortletCacheService - By default refreshable objects will be removed after "
            + DefaultTimeToLiveMillis
            + " Millis ( "
            + (DefaultTimeToLiveMillis / (1000 * 60))
            + " minutes "
            + ((DefaultTimeToLiveMillis % (1000 * 60)) / 1000.00)
            + " Seconds "
            + ")");
      }
      // no specific init required, relies on GlobalCacheService
      logger.info("JetspeedPortletCacheService early init()....finished!");
    } catch (Exception e) {
      logger.error("Cannot initialize JetspeedPortletCacheService!", e);
    }
    setInit(true);
  }

  /**
   * Add a Cacheable object to the cache.
   *
   * @param item
   *          the object to store in the Cache
   */
  @Override
  public void addCacheable(Cacheable item) {

    String handle = item.getHandle();

    if (handle.length() == 0) {
      throw new RuntimeException(
        "You must specify a handle for the item you want to cache.");
    }

    if (item.isCacheable()) {
      CachedObject cachedObject = null;
      Long expirationMillis = item.getExpirationMillis();
      if (expirationMillis != null) {
        if (System.currentTimeMillis() < expirationMillis.longValue()) {
          cachedObject.setExpires(expirationMillis.longValue()
            - cachedObject.getCreated());
        }
      }
      if (item instanceof Refreshable) {
        RefreshableCachedObject rco =
          new RefreshableCachedObject((Refreshable) item);
        if (item instanceof AbstractPortlet) {
          AbstractPortlet portlet = (AbstractPortlet) item;
          String tempString =
            portlet.getPortletConfig().getInitParameter(
              JetspeedResources.TIME_TO_LIVE);
          if (tempString != null) {
            rco.setTTL(Integer.parseInt(tempString));
            if (logger.isWarnEnabled()) {
              logger.warn("PortletCache: portlet "
                + item.getHandle()
                + " overrides default time to live with "
                + tempString);
            }
          } else {
            rco.setTTL(DefaultTimeToLiveMillis);
          }
        } else {
          rco.setTTL(DefaultTimeToLiveMillis);
        }
        cachedObject = rco;

      } else {
        cachedObject = new CachedObject(item);
      }
      item.setCachedObject(cachedObject);

      // Add object to cache
      GlobalCache.addObject(handle, cachedObject);
    }
  }

  /**
   * Removes an object from the cache based on its handle
   *
   * @see PortletCacheService#removeCacheable
   * @param handle
   *          the identifier of the object we wish to retrieve
   */
  @Override
  public void removeCacheable(String handle) {

    CachedObject obj = null;

    try {
      obj = GlobalCache.getObject(handle);
    } catch (ObjectExpiredException e) {
      // nothing to do if already expired
    }

    if (obj != null) {
      obj.setStale(true);
    }
  }

  /**
   * Retrieves a Cacheable object from the cache.
   *
   * @param handle
   *          the identifier of the object we wish to retrieve
   * @return the cacehd object or null if not found
   */
  @Override
  public Cacheable getCacheable(String handle) {

    CachedObject obj = null;

    try {
      obj = GlobalCache.getObject(handle);
    } catch (ObjectExpiredException e) {
      logger.info("cache miss, object expired: " + handle);
    }

    if (obj == null) {
      // Log.info( "cache miss: " + handle );
      return null;
    } /*
       * else { Log.info( "cache hit: " + handle ); }
       */

    return (Cacheable) obj.getContents();

  }

}
