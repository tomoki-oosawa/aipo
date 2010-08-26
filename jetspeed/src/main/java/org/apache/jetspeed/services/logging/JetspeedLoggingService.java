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
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

// Servlet API
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

// Turbine classes
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.logging.LoggingService;
import org.apache.turbine.services.logging.LoggingConfig;
import org.apache.turbine.services.logging.Logger;
import org.apache.turbine.util.RunData;
import org.apache.turbine.Turbine;

/**
 * This service now only functions as an interim implementation of
 * Turbine's LoggingService. In order to facilitate the new Log4J logging
 * in Jetspeed, the default logging service behavior of Turbine must be overridden.
 * The JetspeedLoggingService now just reroutes to JetspeedLogFactoryService
 *
 *
 * @see org.apache.jetspeed.services.logging.JetspeedLogFactoryService
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:harald@ommang.com">Harald Ommang</a>
 * @version $Id: JetspeedLoggingService.java,v 1.4 2004/02/23 03:33:29 jford Exp $
 */
public class JetspeedLoggingService
extends TurbineBaseService
implements LoggingService
{
    /**
     * Static initialization. Facilitates configuration via JetspeedLogFactoryService
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedLoggingService.class.getName());
    
    /**
     * loggers repository
     */
    private HashMap loggers;

    /**
     * logger for methods without target
     */
    private Logger defaultLogger;

    /**
     * bootstrap and shutdown logger using context.log
     */
    private Logger simpleLogger;

    /**
     * context for resolving paths and servlet logging
     */
    private ServletContext context = null;

    /**
     * Resources for this Service
     */
    private ResourceService resources = null;

    public JetspeedLoggingService()
    {
        loggers = new HashMap();
        defaultLogger = null;
    }

    /**
     * Load all configured components and initialize them. This is
     * a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */ 
    public void init()
    throws InitializationException
    {
        ServletConfig conf = Turbine.getTurbineServletConfig();
        init(conf);
    }

    /**
     * Inits the service using servlet parameters to obtain path to the
     * configuration file. Change relatives paths.
     *
     * @param config The ServletConfiguration from Turbine
     *
     * @throws InitializationException Something went wrong when starting up.
     */
    public void init(ServletConfig config) 
    throws InitializationException
    {
        context = config.getServletContext();

        // Create bootstrap logger, for handling exceptions during service
        // initialization.
        defaultLogger = new BaseLogger(logger);

        simpleLogger = defaultLogger;

        internalInit();
        setInit(true);
    }

    /**
     * This gets the ResourceService associated to this Service
     */
    public ResourceService getResources()
    {
        if (resources == null)
        {
            // Get the properties for this Service
            resources = TurbineResources
                        .getResources(TurbineServices.SERVICE_PREFIX 
                                      + LoggingService.SERVICE_NAME);

            //add webappRoot manually - cos logging is a primary
            //service and so it is not yet defined
            String webappRoot = context.getRealPath("/");
            resources.setProperty(Turbine.WEBAPP_ROOT, webappRoot);
        }
        return (resources);
    }

    /**
     * This method initializes the service.
     */
    private void internalInit() throws InitializationException
    {
        ResourceService props = getResources();
        if (props == null)
        {
            throw new InitializationException("LoggingService failed to " 
                                              + "get access to the properties for this service.");
        }

        //looking for default logger name
        String defaultLoggerName = props.getString(LoggingConfig.DEFAULT);

        //checking whether default logger is properly configured
        if (defaultLoggerName == null)
        {
            throw new InitializationException("LoggingService can't find " 
                                              + "default logger name in the configuration file.");
        }

        // Create default logger
        loggers.put(defaultLoggerName, defaultLogger);

        //checking whether default logger is properly configured
        if (defaultLogger == null)
        {
            throw new InitializationException("LoggingService can't find " 
                                              + "default logger in working loggers.");
        }
    }

    /**
     * Shutdowns all loggers. After shutdown servlet logger is still available
     * using the servlet log method
     */
    public void shutdown()
    {
        if (!getInit())
        {
            return;
        }

        for ( Iterator iter = loggers.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                ((Logger) entry.getValue()).shutdown();
        }
        
        defaultLogger = simpleLogger;

        //we don't set init as false, because we can still log.
    }

    /**
     * This method returns default logger for Turbine System
     */
    public final Logger getLogger()
    {
        return defaultLogger;
    }

    /**
     * This method returns logger with given name.
     */
    public Logger getLogger(String logName)
    {
        Logger logger = (Logger) loggers.get(logName);
        if (logger == null)
        {
            logger = new BaseLogger(JetspeedLogFactoryService.getLogger(logName));
            if (logger == null)
            {
                return defaultLogger;
            }
            else
            {
                loggers.put(logName, logger);
            }
        }
        return logger;
    }

    /**
     * This method sets the log level of the default logger.
     */
    public void setLogLevel(int level)
    {
        defaultLogger.setLogLevel(level);
    }

    /**
     * This method sets the log level of the logger of given name.
     */
    public void setLogLevel(String logName, int level)
    {
        Logger logger = (Logger) loggers.get(logName);
        if (logger != null)
        {
            logger.setLogLevel(level);
        }
    }

    /**
     * This method sets format style of the default logger
     */
    public void setFormat(String format)
    {
        defaultLogger.setFormat(format);
    }

    /**
     * This method sets format style of the given logger.
     */
    public void setFormat(String logName, String format)
    {
        Logger logger = (Logger) loggers.get(logName);
        if (logger != null)
        {
            logger.setFormat(format);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message)
    {
        defaultLogger.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message, Throwable t)
    {
        defaultLogger.debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.debug(message, t);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.debug(message);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message, RunData data)
    {
        defaultLogger.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message, RunData data, Throwable t)
    {
        defaultLogger.debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.debug(message, data, t);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message, RunData data)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.debug(message, data);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public void info(String message)
    {
        defaultLogger.info(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public void info(String message, Throwable t)
    {
        defaultLogger.info(message, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.info(message);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.info(message, t);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public void info(String message, RunData data)
    {
        defaultLogger.info(message);
    }

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the default logger
     */
    public void info(String message, RunData data, Throwable t)
    {
        defaultLogger.info(message, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message, RunData data)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.info(message, data);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.info(message, data, t);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public void warn(String message)
    {
        defaultLogger.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public void warn(String message, Throwable t)
    {
        defaultLogger.warn(message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.warn(message);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.warn(message, t);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the default logger
     */
    public void warn(String message, RunData data)
    {
        defaultLogger.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public void warn(String message, RunData data, Throwable t)
    {
        defaultLogger.warn(message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message, RunData data)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.warn(message, data);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.warn(message, data, t);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message)
    {
        defaultLogger.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message, Throwable t)
    {
        defaultLogger.error(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.error(message);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.error(message, t);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message, RunData data)
    {
        defaultLogger.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message, RunData data, Throwable t)
    {
        defaultLogger.error(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message, RunData data)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.error(message, data);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = getLogger(logName);
        if (logger != null)
        {
            logger.error(message, data, t);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }
}
