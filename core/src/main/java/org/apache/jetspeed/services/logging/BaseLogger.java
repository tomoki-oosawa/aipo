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

// Turbine classes
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.logging.Logger;
import org.apache.turbine.services.logging.LoggingConfig;

/**
 * Classes that implement the Logger interface allows loging.
 * There is set of standart printing methods (info, debug ...).
 * This is a wrapper for the commons Log object.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:harald@ommang.com">Harald Ommang</a>
 * @version $Id: BaseLogger.java,v 1.3 2004/02/23 03:33:29 jford Exp $
 */
public class BaseLogger implements Logger
{
    /**
     * Reference to commons logger
     */
    private JetspeedLogger log = null;

    /**
     * Current log level for logger
     */
    private int logLevel;

    /**
     * Name of the logger
     */
    private String name;

    /**
     * Parametrized constructor
     * 
     * @param log
     */
    public BaseLogger(JetspeedLogger log)
    {
        this.log = log;
    }

    /**
     * name of the logger
     * 
     * @return log name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Setings the name
     * 
     * @param logName
     */
    public void setName(String logName)
    {
        this.name = logName;
    }

    /**
     * Reference to logger
     * 
     * @return JetspeedLogger
     */
    public JetspeedLogger getLog()
    {
        return this.log;
    }

    /**
     * Sets reference to JetspeedLogger
     * 
     * @param log
     */
    public void setLog(JetspeedLogger log)
    {
        this.log = log;
    }

    /**
     * This method should be implemented by user.
     * It performs action that are need for deterimne whether
     * logger was well configured or has any output
     * 
     * @return true if logger is well configured
     */
    public boolean checkLogger()
    {

        return true;
    }


    /**
     * This method sets parameters for the logger implementation.
     * If the implementation cannot handle some type of destination should ignore
     * that output.
     *
     * @param LoggingConfig configuration object for logging
     */
    public void init(LoggingConfig loggingConfig)
    {
        // Nothing to do. Declared to satisfy the interface.
    }

    /**
     * Close all destinations
     */
    public void shutdown()
    {
        // nothing to do
    }

    /**
     * Sets log level for the logger
     * 
     * @param level
     */
    public void setLogLevel(int level)
    {
        this.logLevel = level;
    }

     /**
      * Checks if DEBUG statements are enabled.
      * 
      * @return true if debug is enabled
      */
     public boolean isDebugEnabled() 
     {

         return this.log.isDebugEnabled();
     }

     /**
      * Checks if INFO statements are enabled.
      * 
      * @return true if into is enabled
      */
     public boolean isInfoEnabled()
     {

         return this.log.isInfoEnabled();
     }

     /**
      * Checks if WARN statements are enabled.
      * 
      * @return true if warn is enabled
      */
     public boolean isWarnEnabled()
     {

         return this.log.isWarnEnabled();
     }


     /**
      * Checks if ERROR statements are enabled.
      * 
      * @return true if error is enabled
      */
     public boolean isErrorEnabled()
     {

         return this.log.isErrorEnabled();
     }

    /**
     * Sets format style for extracting data from RunData
     * 
     * @param format
     */
    public void setFormat(String format)
    {
        // nothing to do
    }

    /**
     * This is a log method with logLevel == DEBUG
     * 
     * @param message
     */
    public void debug(String message)
    {
        this.log.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG
     * 
     * @param message
     * @param t
     */
    public void debug(String message, Throwable t)
    {
        this.log.debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG
     * 
     * @param message
     * @param data
     */
    public void debug(String message, RunData data)
    {
        this.log.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG
     * 
     * @param message
     * @param data
     * @param t
     */
    public void debug(String message, RunData data, Throwable t)
    {
        this.log.debug(message, t);
    }

    /**
     * This is a log method with logLevel == INFO
     * 
     * @param message
     */
    public void info(String message)
    {
        this.log.info(message);
    }

    /**
     * This is a log method with logLevel == INFO
     * 
     * @param message
     * @param t
     */
    public void info(String message, Throwable t)
    {
        this.log.info(message, t);
    }

    /**
     * This is a log method with logLevel == INFO
     * 
     * @param message
     * @param data
     */
    public void info(String message, RunData data)
    {
        this.log.info(message);
    }

    /**
     * This is a log method with logLevel == INFO
     * 
     * @param message
     * @param data
     * @param t
     */
    public void info(String message, RunData data, Throwable t)
    {
        this.log.info(message, t);
    }

    /**
     * This is a log method with logLevel == WARN
     * 
     * @param message
     */
    public void warn(String message)
    {
        this.log.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN
     * 
     * @param message
     * @param t
     */
    public void warn(String message, Throwable t)
    {
        this.log.warn(message, t);
    }

    /**
     * This is a log method with logLevel == WARN
     * 
     * @param message
     * @param data
     */
    public void warn(String message, RunData data)
    {
        this.log.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN
     * 
     * @param message
     * @param data
     * @param t
     */
    public void warn(String message, RunData data, Throwable t)
    {
        this.log.warn(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR
     * 
     * @param message
     */
    public void error(String message)
    {
        this.log.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR
     * 
     * @param message
     * @param e
     */
    public void error(String message, Throwable e)
    {
        this.log.error(message, e);
    }

    /**
     * This is a log method with logLevel == ERROR
     * 
     * @param e
     */
    public void error(Throwable e)
    {
        this.log.error(e);
    }

    /**
     * This is a log method with logLevel == ERROR
     * 
     * @param message
     * @param data
     */
    public void error(String message, RunData data)
    {
        this.log.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR
     * 
     * @param message
     * @param data
     * @param e
     */
    public void error(String message, RunData data, Throwable e)
    {
        this.log.error(message, e);
    }
}
