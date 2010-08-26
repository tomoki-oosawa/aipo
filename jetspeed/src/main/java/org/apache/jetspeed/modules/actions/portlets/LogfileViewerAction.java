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
package org.apache.jetspeed.modules.actions.portlets;

// Java classes
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;

// Log4J classes
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggerRepository;

// Turbine classes
import org.apache.turbine.util.RunData;

// Velocity classes
import org.apache.velocity.context.Context;

// Jetspeed classes
import org.apache.jetspeed.portal.Portlet;

/**
 * This class is the action class for a portlet that lets you view the Log4J 
 * logfiles defined in your Jetspeed installation.<br/>
 * the portlet iterates through the Log4J appender defined that are of type 
 * FileAppender or its subclasses, and lists the filenames in a listbox.<br/>
 *
 * The portlet puts the following in the context:<br/>
 * <code>appenders</code> - a HashMap with the appenders found<br/>
 * <code>files</code> - a HashMap with the filenames without path<br/>
 * <code>logfile</code> - the content of the file indicated by <code>selectedfile</code><br/>
 * 
 * 
 * @author <a href="mailto:harald@ommang.com">Harald Ommang</a>
 * @version $Id: LogfileViewerAction.java,v 1.3 2004/02/23 02:56:58 jford Exp $
 */
public class LogfileViewerAction extends GenericMVCAction
{
    /**
     * Static initialization of the logger for this class
     */    
     private static final Logger logger = LogManager.getLogger(LogfileViewerAction.class.getName());

     private static HashMap appenders = null;

    /** Creates a new instance of LogFileViewerAction */
    public LogfileViewerAction() 
    {
    }
    
    /** 
     * Lists the current logfiles
     * @param portlet The current portlet
     * @param context the current portlet context
     * @paran rundata the Turbine rundata
     *
     */
    protected void buildNormalContext(Portlet portlet, Context context, RunData rundata) throws Exception 
    {
        String tempName;
        LoggerRepository repos = logger.getLoggerRepository();
        Enumeration loggerEnum = repos.getCurrentLoggers();
        HashMap files = new HashMap();
        HashMap fileNames = new HashMap();
        appenders = new HashMap();
        
        while ( loggerEnum.hasMoreElements() )
        {
            Logger appLogger = (Logger) loggerEnum.nextElement();
            Enumeration appenderEnum = appLogger.getAllAppenders();
            String name;

            while ( appenderEnum.hasMoreElements() )
            {
                Appender appender = (Appender) appenderEnum.nextElement();
                if (appender instanceof FileAppender)
                {
                    name = appender.getName();
                    tempName = ((FileAppender)appender).getFile();
                    tempName = tempName.substring(tempName.lastIndexOf(System.getProperty("file.separator")) + 1);
                    if (name == null)
                    {
                        name = tempName;
                        appender.setName(name);
                    }
 
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("AppenderName " + name);
                    }
                    appenders.put(name, appender);
                    files.put(name, tempName);
                }
            }
        }        
        context.put("appenders", appenders.values());
        context.put("files", files);
    }
    
    /** 
     * If a file is selected, it's contents is put in "logfile"
     * @paran rundata the Turbine rundata
     * @param context the current portlet context
     *
     */    
    public void doUpdate(RunData data, Context context)
    {
        try 
        {        
            String fileName = data.getParameters().getString("selectedfile");
            logger.debug("selectedfile: " + fileName);
            if (fileName != null)
            {
                String content = readFile(fileName);
                context.put("logfile", content);
            }
            else
            {
                context.put("logfile", null);
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception in viewing logfile: ", ex);
        }
    }

    /** 
     * Reads the contents of a file and returns in \n separated lines.
     * @paran filename Name of file to read
     *
     */        
    private String readFile (String filename) 
    {
        StringBuffer buf = new StringBuffer("");
        try 
        {
            String line;
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) 
            {
                buf.append(line + "\n");
            }
            in.close();
        } 
        catch (IOException ioe) 
        {
            logger.error("Error reading file " + filename, ioe);
        }
        return buf.toString();
    } // readFile
    
} // class LogfileViewerAction
