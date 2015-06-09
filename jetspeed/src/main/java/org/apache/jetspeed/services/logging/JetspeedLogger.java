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

// Log4J classes
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The implementation of loggers for Jetspeed.
 *
 * This class acts as a wrapper so that the underlying logging implementation
 * is hidden fromthe rest of Jetspeed
 * The current implementation uses Log4J.
 *
 * @author <a href="mailto:harald@ommang.com">Harald Ommang</a>
 */
public class JetspeedLogger
{
 
    /**
     * The Log4J logger that is wrapped
     */        
    private Logger logger;

    /**
     * Constructor. Initialises this class with a given logger.
     * If the logger is null, one is given named from this class.
     *
     * @param logger The logger to wrap
     */    
    public JetspeedLogger(Logger logger)
    {
        if(logger != null)
        {
            this.logger = logger;
        } else
        {
            this.logger = LogManager.getLogger(JetspeedLogger.class.getName());
        }
    }

    /**
     * Checks if the current logger is enabled for debug logging.
     *
     * @return true if debug is enabled
     */
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    /**
     * Checks if the current logger is enabled for error logging.
     *
     * @return true if error is enabled
     */    
    public boolean isErrorEnabled()
    {
        return logger.isEnabledFor(Level.ERROR);
    }

    /**
     * Checks if the current logger is enabled for fatal logging.
     *
     * @return true if fatal is enabled
     */        
    public boolean isFatalEnabled()
    {
        return logger.isEnabledFor(Level.FATAL);
    }

    /**
     * Checks if the current logger is enabled for info logging.
     *
     * @return true if info is enabled
     */        
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    /**
     * Checks if the current logger is enabled for trace logging.
     * Wtih log4J, this is the same as debug.
     *
     * @return true if trace is enabled
     */        
    public boolean isTraceEnabled()
    {
        return logger.isDebugEnabled();
    }

    /**
     * Checks if the current logger is enabled for warning logging.
     *
     * @return true if warning is enabled
     */
    public boolean isWarnEnabled()
    {
        return logger.isEnabledFor(Level.WARN);
    }

    /**
     * Logs the given object if debug is enabled
     *
     * @param obj Object to log
     */    
    public void debug(Object obj)
    {
        logger.debug(obj);
    }

    /**
     * Logs the given object and throwable if debug is enabled
     *
     * @param obj Object to log
     * @param throwable The underlying implementation may log stack trace for this
     */        
    public void debug(Object obj, Throwable throwable)
    {
        logger.debug(obj, throwable);
    }

    /**
     * Logs the given object if error is enabled
     *
     * @param obj Object to log
     */        
    public void error(Object obj)
    {
        logger.error(obj);
    }

    /**
     * Logs the given object and throwable if error is enabled
     *
     * @param obj Object to log
     * @param throwable The underlying implementation may log stack trace for this
     */            
    public void error(Object obj, Throwable throwable)
    {
        logger.error(obj, throwable);
    }

    /**
     * Logs the given object if fatal is enabled
     *
     * @param obj Object to log
     */        
    public void fatal(Object obj)
    {
        logger.fatal(obj);
    }

    /**
     * Logs the given object and throwable if fatal is enabled
     *
     * @param obj Object to log
     * @param throwable The underlying implementation may log stack trace for this
     */            
    public void fatal(Object obj, Throwable throwable)
    {
        logger.fatal(obj, throwable);
    }

    /**
     * Logs the given object if info is enabled
     *
     * @param obj Object to log
     */        
    public void info(Object obj)
    {
        logger.info(obj);
    }

    /**
     * Logs the given object and throwable if info is enabled
     *
     * @param obj Object to log
     * @param throwable The underlying implementation may log stack trace for this
     */            
    public void info(Object obj, Throwable throwable)
    {
        logger.info(obj, throwable);
    }

    /**
     * Logs the given object if trace is enabled
     * With Log4J, this is the same as debug
     *
     * @param obj Object to log
     */        
    public void trace(Object obj)
    {
        logger.debug(obj);
    }

    /**
     * Logs the given object and throwable if trace is enabled
     * With Log4J, this is the same as debug
     *
     * @param obj Object to log
     * @param throwable The underlying implementation may log stack trace for this
     */            
    public void trace(Object obj, Throwable throwable)
    {
        logger.debug(obj, throwable);
    }

    /**
     * Logs the given object if warning is enabled
     *
     * @param obj Object to log
     */        
    public void warn(Object obj)
    {
        logger.warn(obj);
    }

    /**
     * Logs the given object and throwable if warning is enabled
     *
     * @param obj Object to log
     * @param throwable The underlying implementation may log stack trace for this
     */            
    public void warn(Object obj, Throwable throwable)
    {
        logger.warn(obj, throwable);
    }
} // class JetspeedLogger

