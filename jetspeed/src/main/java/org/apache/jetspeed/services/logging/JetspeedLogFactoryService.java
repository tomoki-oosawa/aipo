package org.apache.jetspeed.services.logging;

/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

// Java classes
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * The default implementation of the logging service in Jetspeed.
 * 
 * This service initializes the underlying logging implementation, and acts as a
 * factory for loggers. The current implementation uses Log4J.
 * 
 * @see org.apache.log4j.LogManager
 * @see org.apache.log4j.Logger
 * @author <a href="mailto:harald@ommang.com">Harald Ommang</a>
 */
public class JetspeedLogFactoryService extends TurbineBaseService {

  public String SERVICE_NAME = "JetspeedLogFactoryService";

  private static final String CONFIG_LOG4J_PROPERTIES = "log4j.properties";

  private static final String CONFIG_LOG4J_PROPERTIES_DEFAULT =
    "/WEB-INF/conf/log4j.properties";

  private static final String CONFIG_LOG4J_AND_WATCH =
    "log4j.configureAndWatch";

  private static final boolean CONFIG_LOG4J_AND_WATCH_DEFAULT = true;

  private static final String CONFIG_LOG4J_WATCHINTERVAL =
    "log4j.watchInterval";

  private static final long CONFIG_LOG4J_WATCHINTERVAL_DEFAULT = 60000L;

  private ServletContext context;

  private static LoggerFactory loggerFactory = null;

  /**
   * Flag to check for initilization. Needed to make time of init more robust.
   * Also, cannot access the init in parent class from static method
   */
  private static boolean initDone = false;

  /**
   * Default constructor
   */
  public JetspeedLogFactoryService() {
    context = null;
  }

  /**
   * Initializes the service by getting the servlet configuration from Turbine
   * 
   * @throws InitializationException
   *           Initialization failed
   */
  @Override
  public void init() throws InitializationException {
    ServletConfig conf = Turbine.getTurbineServletConfig();
    if (conf != null) {
      init(conf);
    }
  }

  /**
   * Initializes the service with the given configuration Initializes the
   * underlying logging implementation, Log4J
   * 
   * @param config
   *          The ServletConfiguration from Turbine
   * 
   * @throws InitializationException
   *           Initialization failed
   */
  @Override
  public void init(ServletConfig config) throws InitializationException {
    context = config.getServletContext();
    String log4jProperties =
      JetspeedResources.getString(
        CONFIG_LOG4J_PROPERTIES,
        CONFIG_LOG4J_PROPERTIES_DEFAULT);
    if (log4jProperties != null) {
      try {
        String fileName = Turbine.getRealPath(log4jProperties);
        boolean watch =
          JetspeedResources.getBoolean(
            CONFIG_LOG4J_AND_WATCH,
            CONFIG_LOG4J_AND_WATCH_DEFAULT);
        long watchInterval =
          JetspeedResources.getLong(
            CONFIG_LOG4J_WATCHINTERVAL,
            CONFIG_LOG4J_WATCHINTERVAL_DEFAULT);
        System.setProperty("webappRoot", context.getRealPath("/"));

        // Check to see if property or XML configuration is to be used.
        if (fileName.endsWith(".properties")) {
          if (watch) {
            // Configure with a property file and watch for changes
            PropertyConfigurator.configureAndWatch(fileName, watchInterval);
          } else {
            PropertyConfigurator.configure(fileName);
          }
        } else {
          if (watch) {
            // Configure with an XML file and watch for changes
            DOMConfigurator.configureAndWatch(fileName, watchInterval);
          } else {
            DOMConfigurator.configure(fileName);
          }
        }
        String factoryClassName =
          JetspeedResources.getString("log4j.loggerFactory", "");
        if (factoryClassName != null) {
          loggerFactory =
            (LoggerFactory) OptionConverter.instantiateByClassName(
              factoryClassName,
              LoggerFactory.class,
              loggerFactory);
        }
      } catch (Exception e) {
        throw new InitializationException("Failed to load "
          + log4jProperties
          + " - "
          + e.toString());
      }
    }
    setInit(true);
    initDone = true;
  } // init

  /**
   * The actual Factory method that gets the appropriate logger from Log4j and
   * wraps it in a JetspeedLogger
   */
  public static JetspeedLogger getLogger(String loggerName) {
    // This test needed to ensure correct init sequence between this and
    // services that log.
    if (!initDone) {
      synchronized (JetspeedLogFactoryService.class) {
        if (!initDone) {
          try {
            new JetspeedLogFactoryService().init();
          } catch (Exception e) {
            System.err.println("Init failed no logging available"
              + e.getMessage());
            e.printStackTrace();
          }
        }
      }
    }

    Logger newLog = null;
    if (loggerFactory == null) {
      newLog = LogManager.getLogger(loggerName);
    } else {
      newLog = LogManager.getLogger(loggerName, loggerFactory);
    }
    JetspeedLogger newLogger = new JetspeedLogger(newLog);
    return newLogger;
  }
} // class JetspeedLogFactoryService

