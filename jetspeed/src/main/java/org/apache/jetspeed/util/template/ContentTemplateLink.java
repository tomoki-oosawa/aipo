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

package org.apache.jetspeed.util.template;

import java.lang.reflect.Method;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * A customized version of the DynamicURI for linking to non-servlet
 * webapp resources.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id: ContentTemplateLink.java,v 1.7 2004/02/23 03:20:45 jford Exp $
 */
public class ContentTemplateLink
    extends DynamicURI
    implements ApplicationTool
{
        
    /** the servlet 2.2+ webapp context */
    private String contextPath;

    /** the webapp relative URI to find */
    private String pathToContent;

    /** decide whether we should output full external URIs or simple
    absolute URIs */
    private boolean useExternalForm = false;
    
    /** Empty Constructor for introspection */
    public ContentTemplateLink ()
    {
    }

    /** Constructor */
    public ContentTemplateLink (RunData data)
    {
        super(data);
        initForceSecure();
        initContextPath(data);
    }

    /**
     * This will initialise a ContentTemplateLink object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data)
    {
        super.init((RunData)data);
        initForceSecure();
        initContextPath(data);
    }

    /** Inits the contextPath for this object 
     *
     * @param data the RunData to use 
     */
    protected void initContextPath(Object data)
    {
        try
        {
            Class runDataClass = RunData.class;
            Method meth = runDataClass.getDeclaredMethod("getContextPath", null);
            contextPath = (String)meth.invoke(data, null);
        }
        catch (Exception e)
        {
            /*
             * Ignore a NoSuchMethodException because it means we are
             * using Servlet API 2.0.  Make sure scriptName is not
             * null.
             */
            contextPath = "";
        }
    }

    /**
     * Inits the force secure setting.
     */
    protected void initForceSecure()
    {
        // check if we need to force to a secure (https) link
        if (JetspeedResources.getBoolean("force.ssl", false))
        {
            setSecure();
        }
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Specify the link should be expressed in external form (ie 
     * with protocol, server name and server port)
     * @return a self reference for easy link construction in templates
     */
    public ContentTemplateLink getExternal() {
        this.useExternalForm = true;
        return this;
    }
    
    /**
     * Specify the link should be expressed in absolute form (ie 
     * only a URI and not a full URL)
     * @return a self reference for easy link construction in templates
     */
    public ContentTemplateLink getAbsolute() {
        this.useExternalForm = false;
        return this;
    }
    
    /**
     * Specify the webapp resource to link to.
     *
     * @param pathToContent the path to resource, assumed to be relative to the
     * web application context
     * @return a self reference for easy link construction in templates
     */
    public ContentTemplateLink setURI(String pathToContent)
    {
        this.pathToContent = pathToContent;
        return this;
    }

    /**
     * Returns the URI. After rendering the URI, it clears the 
     * pathInfo and QueryString portions of the DynamicURI.
     *
     * @return A String with the URI in either external or absolute form
     */
    public String toString()
    {
        
        StringBuffer sb = new StringBuffer();

        // we want external URL form so include protocol and server name
        if (useExternalForm)
        {
	    String scheme = getServerScheme();
            sb.append ( getServerScheme() ); //http
            sb.append ("://");
            sb.append (getServerName()); //www.foo.com
	    int port = getServerPort();
	    if( ( "http".equals( scheme ) && port != 80 ) ||
		( "https".equals( scheme ) && port != 443 ) ) { //only for non-default ports, to preserve session tracking.
		sb.append (":");
		sb.append ( port ); //port webserver running on (8080 for TDK)
	    }
        }
        //the context for tomcat adds a / so no need to add another
        sb.append (contextPath); //the tomcat context
        sb.append ("/");
        if (pathToContent!=null) sb.append (pathToContent);

        // This was added to allow multilple $link variables in one
        // template.
        removePathInfo();
        removeQueryData();
        this.pathToContent=null;
        
        return (sb.toString());
    }

}
