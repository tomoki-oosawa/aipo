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

package org.apache.jetspeed.services.portletstats;

// turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;

// jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// javax stuff
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

// java stuff
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple implementation of the PortletStatsService. This implementation
 * uses <A HREF="http://httpd.apache.org/docs/logs.html">Apache Common Log Format (CLF)</A> as its default log format.
 * This format uses the following pattern string: "%h %l %u %t \"%r\" %>s %b",
 * where:
 * <UL>
 * <LI><B>%h</B> - remote host</LI>
 * <LI><B>%l</B> - remote log name</LI>
 * <LI><B>%u</B> - remote user</LI>
 * <LI><B>%t</B> - time in common log time format</LI>
 * <LI><B>%r</B> - first line of request</LI>
 * <LI><B>%s</B> - status (either 200 or 401)</LI>
 * <LI><B>%b</B> - bytes sent (always "-" for no bytes sent). Optionally, portlet load time may be logged (see logLoadTime property)</LI>
 * </UL>
 * <P>
 * Here's an example log entry:
 * <P>
 * <CODE>127.0.0.1 - turbine [26/Aug/2002:11:44:40 -0500] "GET /jetspeed/DatabaseBrowserTest HTTP/1.1" 200 -</CODE>
 * <P>
 * TODO:
 * <UL>
 * <LI>Statistics cache (by portlet and by user)</LI>
 * <LI>Portlet exclusion</LI>
 * <LI>Configurable format pattern</LI>
 * </UL>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedPortletStatsService.java,v 1.6 2004/04/06 21:34:01 morciuch Exp $
 */
public class JetspeedPortletStatsService extends TurbineBaseService
    implements PortletStatsService
{
    /**
     * Static initialization of the logger for this class
     */    
    protected static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortletStatsService.class.getName());
    
    /**
     * The default log format pattern string to use with the following elements:
     * <OL START="0">
     * <LI>remote address</LI>
     * <LI>always "-"</LI>
     * <LI>user name</LI>
     * <LI>timestamp</LI>
     * <LI>request method</LI>
     * <LI>context</LI>     
     * <LI>portlet name</LI>
     * <LI>request protocol</LI>
     * <LI>status code</LI>
     * <LI>always "-" unless logLoadTime is true</LI>
     * </OL>
     */
    protected static final String defaultLogFormat = "{0} {1} {2} [{3}] \"{4} {5}/{6} {7}\" {8} {9}";

    /**
     * Logging enabled flag. If TRUE, the logging will occur. To improve performance,
     * the application should use isEnabled() method before calling logAccess().
     */
    private boolean enabled = false;

    /**
     * Date format to use in the log entry. Should conform to standard
     * format used by the SimpleDateFormat class.
     */
    protected String dateFormat = null;

    /** Date formatter */
    protected SimpleDateFormat formatter = null;

    /** Log portlet load time instead of bytes sent (which is always zero) */
    protected boolean logLoadTime = false;

    /**
     * This is the early initialization method called by the 
     * Turbine <code>Service</code> framework
     */
    public void init( ServletConfig conf ) throws InitializationException
    {

        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(PortletStatsService.SERVICE_NAME);

        this.enabled = serviceConf.getBoolean("enabled");
        this.dateFormat = serviceConf.getString("dateFormat", "dd/MM/yyyy:hh:mm:ss z");
        this.formatter = new SimpleDateFormat(this.dateFormat);
        this.logLoadTime = serviceConf.getBoolean("logLoadTime", false);
        
        setInit(true);

    }
            
    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#isEnabled
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#setEnabled
     */
    public boolean setEnabled(boolean state)
    {
        boolean oldState = this.enabled;
        this.enabled = state;

        return oldState;
    }

    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#logAccess
     */
    public void logAccess(RunData data, Portlet portlet, String statusCode, long time)
    {
        
        if (!this.isEnabled())
        {
            return;
        }
        
        try 
        {
            logger.info(this.getLogMessage(data, portlet, statusCode, time));
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    /**
     * Formats log message
     * 
     * @param data
     * @param portlet
     * @param statusCode
     * @param time
     * @return Formatted message
     * @exception Exception
     */
    protected String getLogMessage(RunData data, Portlet portlet, String statusCode, long time) 
    throws Exception
    {        
 
        HttpServletRequest req = data.getRequest();
        Object[] args = {
            req.getRemoteAddr(),
            "-",
            data.getUser().getUserName(),
            this.formatter.format(new Date()),
            req.getMethod(),
            req.getContextPath(),
            portlet.getName(),
            req.getProtocol(),
            statusCode,
            this.logLoadTime == true ? String.valueOf(time) : "-"
        }; 

        return MessageFormat.format(defaultLogFormat, args).toString();

    }

    /**
     * Formats log message using default load time
     * 
     * @param data
     * @param portlet
     * @param statusCode
     */
    public void logAccess(RunData data, Portlet portlet, String statusCode) 
    {
        logAccess(data, portlet, statusCode, 0);
    }
    
}

