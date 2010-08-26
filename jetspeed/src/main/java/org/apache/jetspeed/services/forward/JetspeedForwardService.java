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

package org.apache.jetspeed.services.forward;

// java
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileReader;
import javax.servlet.ServletConfig;

// turbine, services
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.servlet.TurbineServlet;

// marshaling
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.InputSource;

// jetspeed
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;

// forwarding configuration
import org.apache.jetspeed.services.forward.configuration.ForwardsConfiguration;
import org.apache.jetspeed.services.forward.configuration.Forward;
import org.apache.jetspeed.services.forward.configuration.Page;
import org.apache.jetspeed.services.forward.configuration.Pane;
import org.apache.jetspeed.services.forward.configuration.Portlet;
import org.apache.jetspeed.services.forward.configuration.PortletForward;
import org.apache.jetspeed.services.forward.configuration.QueryParam;


/**
 * <P>This is the implementation of the Jetspeed Forward services.
 *    The interface defines methods for forwarding navigation to 
 *    other pages or panes in the portal. The Forward service
 *    provides an abstraction, by removing the hard-coding of
 *    portal resources in your actions. Instead, all forward targets
 *    are defined in a centralized configuration file. By using the 
 *    forward service, you use logical forward names in your java code.</P>
 *
 * @see org.apache.jetspeed.om.profile.Profile
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: JetspeedForwardService.java,v 1.7 2004/02/23 03:51:09 jford Exp $
 */

public class JetspeedForwardService extends TurbineBaseService
                                    implements ForwardService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedForwardService.class.getName());
    
    // configuration keys
    protected final static String CONFIG_MAPPING = "mapping";
    protected final static String CONFIG_DIRECTORY = "directory";

    // configuration parameters
    protected String mapping =                 // the forwards XML-Java mapping 
               "/WEB-INF/conf/forwards-mapping.xml";

    protected String directory =                // the location of forwards definitions
                      "/WEB-INF/conf/forwards/";

    /** the Castor mapping file name */
    protected Mapping mapper = null;

    /** the output format for pretty printing when saving registries */
    protected OutputFormat format = null;


    // Forward definitions
    protected Map forwards = new HashMap();
    
    // Portlet Forward definitions
    protected Map portletForwards = new TreeMap();


    protected final static String KEY_DELIMITER = ":";

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf) throws InitializationException
    {

        // already initialized
        if (getInit()) return;

        try
        {
            // get configuration parameters from Jetspeed Resources
            ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                         .getResources(ForwardService.SERVICE_NAME);

            this.mapping = serviceConf.getString(CONFIG_MAPPING, this.mapping);

            this.directory = serviceConf.getString(CONFIG_DIRECTORY, this.directory);

            this.mapping = TurbineServlet.getRealPath(this.mapping);
            this.directory = TurbineServlet.getRealPath(this.directory);

            loadForwards();

        }
        catch (Exception e)
        {
            logger.error("ForwardService: Failed to load ", e);
        }

        // initialization done
        setInit(true);

     }



    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
    }

    /**
     *  Forward to a specific forward by name.
     *  All parameters are resolved statically (via the forward definition)
     *
     * @param rundata The turbine rundata context for this request.     
     * @param forwardName Forward to this abstract forward name.
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forward(RunData rundata, String forwardName)
    {
        return forwardInternal(rundata, forwardName, null, null);
    }

    /**
     *  Forward to a specific forward by name.
     *  Parameters are resolved both statically and dynamically, with the 
     *  dynamic parameter overriding the static parameter definitions.
     *
     * @param rundata The turbine rundata context for this request.     
     * @param forwardName Forward to this abstract forward name.
     * @param parameters The dynamic Validation Parameters used in creating validation forwards
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forwardDynamic(RunData rundata, String forwardName, Map parameters)
    {
        return forwardInternal(rundata, forwardName, null, parameters);        
    }

    /**
     * Internal implementation of Forward used by both forwards and portlet forwards.
     *
     * @param rundata The turbine rundata context for this request.     
     * @param name Forward to this abstract forward name.
     * @param staticParams Map of static query parameters from PortletForward 
     *                     overriding the static Forwards query parameters
     * @param dynamicParams Map of dynamic query parameters overriding both
     *                     static PortletForward parameters and static Forwards query parameters     
     * @return DynamicURI the full link to the referenced page     
     */
    private DynamicURI forwardInternal(RunData rundata, 
                                   String  forwardName,
                                   Map staticParams,
                                   Map dynamicParams)
    {
        DynamicURI duri = null;
        Forward forward = null;

        try
        {
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            int rootType = JetspeedLink.DEFAULT;
            int elementType = JetspeedLink.DEFAULT;
            String rootValue = null;
            String pageName = null;
            String elementValue = null;
            String actionName = null;
            String templateName = null;
            String mediaType = null;
            String language = null;
            String country = null;


            forward = (Forward)this.forwards.get(forwardName);
            if (null != forward)
            {
                Pane pane = forward.getPane();
                if (null != pane)
                {
                    elementValue = pane.getId();
                    elementType = JetspeedLink.PANE_ID;
                    if (elementValue == null)
                    {
                        elementValue = pane.getName();
                        elementType = JetspeedLink.PANE_NAME;
                    }                    
                }
                else // can't have both portlet and pane
                {
                    Portlet portlet = forward.getPortlet();
                    if (null != portlet)
                    {
                        elementValue = portlet.getId();
                        elementType = JetspeedLink.PORTLET_ID;
                        if (elementValue == null)
                        {
                            elementValue = portlet.getName();
                            elementType = JetspeedLink.PORTLET_NAME;
                        }                    
                        actionName = portlet.getAction();
                    }
                }

                Page page = forward.getPage();
                if (null != page)
                {
                    pageName = page.getName();

                    String user = page.getUser();
                    if (user != null)
                    {
                        rootType = JetspeedLink.USER;
                        rootValue = user;
                    }
                    else 
                    {
                        String role = page.getRole();
                        if (role != null)
                        {
                            rootType = JetspeedLink.ROLE;
                            rootValue = role;
                        }
                        else
                        {
                            String group = page.getGroup();
                            if (group != null)
                            {
                                rootType = JetspeedLink.GROUP;
                                rootValue = group;
                            }
                            else
                            {
                                rootType = JetspeedLink.CURRENT;
                            }
                        }
                    }
                }

                duri = link.getLink(rootType, 
                             rootValue, 
                             pageName, 
                             elementType, 
                             elementValue, 
                             actionName,  
                             templateName, // not yet implemented
                             mediaType,    // not yet implemented 
                             language,     // not yet implemented
                             country);     // not yet implemented 

            }
            else
            {
                // forward not found, log it and return to home page
                // TODO: perhaps this could be configurable to go to a default error page
                logger.error("Forward not found, going to Home Page:" + forwardName);
                duri = link.getHomePage();
    
            }
    
            if (null == duri)
            {
                duri = link.getPage();
            }

            Map baseQueryParams = null;
            if (null != forward)
            {
                baseQueryParams = forward.getQueryParams();
            }
            setQueryParams(duri, baseQueryParams, staticParams, dynamicParams);

            rundata.setRedirectURI(duri.toString());
            JetspeedLinkFactory.putInstance(link);
        }
        catch (Throwable t)
        {
            logger.error("Exception in Forward",t);
        }
        return duri;
    }

    /**
     * Adds query parameters to the final URI.
     * Parameters are merged from the base forwards definition, with the
     * overlay parameters being overlaid over th base parameters
     *
     * @param duri The dynamic URI to have query parameters added to it
     * @param baseQueryParams The base query parameters from the forward definition
     * @param staticParams Map of static query parameters from PortletForward 
     *                     overriding the static Forwards query parameters
     * @param dynamicParams Map of dynamic query parameters overriding both
     *                     static PortletForward parameters and static Forwards query parameters     
     * @return DynamicURI The new URI including query parameters
     */
    private DynamicURI setQueryParams(DynamicURI duri, 
                                      Map baseQueryParams, 
                                      Map staticParams,
                                      Map dynamicParams)
    {
        if (baseQueryParams == null && staticParams == null && dynamicParams == null)
        {
            return duri;
        }

        Iterator it = null;

        // First add the base params
        if (baseQueryParams != null)
        {
            it = baseQueryParams.values().iterator();
            while (it.hasNext())
            {
                QueryParam qparam = (QueryParam)it.next();
                if (   (null == staticParams || !staticParams.containsKey(qparam.getName())) 
                    && (null == dynamicParams || !dynamicParams.containsKey(qparam.getName())))
                {
                    duri.addQueryData(qparam.getName(), qparam.getValue());
                }
            }            
        }

        // Then add the static params
        if (staticParams != null)
        {
            it = staticParams.values().iterator();
            while (it.hasNext())
            {
                QueryParam qparam = (QueryParam)it.next();
                if (null == dynamicParams || !dynamicParams.containsKey(qparam.getName()))
                {               
                    duri.addQueryData(qparam.getName(), qparam.getValue());
                }
            }            
        }
        
        // Then add the dynamic params
        if (dynamicParams != null)
        {
            it = dynamicParams.entrySet().iterator();
            while (it.hasNext())
            {
                Entry entry = (Entry)it.next();
                duri.addQueryData((String)entry.getKey(), entry.getValue());
            }            
        }

        return duri;
    }

    
    private void dumpMap(String mapName, Map map)
    {
        System.out.println("----------- MAP: " + mapName);
        Iterator it = map.values().iterator();
        while (it.hasNext())
        {
            QueryParam qparam = (QueryParam)it.next();
            System.out.println("name = " + qparam.getName() + ", value = " + qparam.getValue());
        }
    }

    /**
     *  For the given portlet and given action, forward to the target
     *  defined in the forward configuration for the portlet + action.
     *  All parameters are resolved statically (via the forward definition)     
     *
     * @param portlet The name of the portlet for which we are forwarding.
     * @param target A logical target name. Portlets can have 1 or more targets.
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forward(RunData rundata, String portlet, String target)
    {
        return forwardDynamic(rundata, portlet, target, null);
    }

    /**
     *  For the given portlet and given action, forward to the target
     *  defined in the forward configuration for the portlet + action.
     *  Parameters are resolved both statically and dynamically, with the 
     *  dynamic parameter overriding the static parameter definitions.     
     *
     * @param portlet The name of the portlet for which we are forwarding.
     * @param target A logical target name. Portlets can have 1 or more targets.
     * @param parameters The dynamic Validation Parameters used in creating validation forwards     
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forwardDynamic(RunData rundata, 
                                 String portlet, 
                                 String target,
                                 Map parameters)
    {
        try
        {
            Map staticParams = null;
            String forwardName = "";
            String key = makePortletForwardKey(portlet, target);
            PortletForward pf = (PortletForward)this.portletForwards.get(key);        
            if (null != pf)
            {
                staticParams = pf.getQueryParams();
                Forward forward = (Forward)this.forwards.get(pf.getForward());
                if (null != forward)
                {
                    forwardName = forward.getName();
                }
            }
            return forwardInternal(rundata, forwardName, staticParams, parameters);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        return new DynamicURI();

    }

    /**
     * Get a collection of all forwards in the system.
     *
     * @return Collection of all forward definitions
     */
    public Collection getForwards()
    {
        return this.forwards.values();
    }

    /**
     * Get a collection of all portlet forwards in the system.
     *
     * @return Collection of all portlet forward definitions
     */
    public Collection getPortletForwards()
    {
        return this.portletForwards.values();
    }

    /**
     * Lookup a single forward definition by forward name
     *
     * @param  forwardName The name of the Forward to find
     * @return Forward The found forward definition or null if not found
     */
    public Forward getForward(String forwardName)
    {
        return (Forward)this.forwards.get(forwardName);
    }

    /**
     * Lookup a single portlet forward definition by portlet name + target name
     *
     * @param  portlet The name of the portlet in the Portlet Forward to find
     * @param  target The name of the target in the Portlet Forward to find     
     * @return Forward The found portlet forward definition or null if not found
     */
    public PortletForward getPortletForward(String portlet, String target)
    {
        return (PortletForward)this.portletForwards.get(makePortletForwardKey(portlet, target));
    }

    /**
     * Load all forward configuration files from forwards directory.
     *
     * 
     */
    protected void loadForwards()
        throws InitializationException
    {
        // create the serializer output format
        this.format = new OutputFormat();
        this.format.setIndenting(true);
        this.format.setIndent(4);

        File map = new File(this.mapping);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                this.mapper = new Mapping();
                InputSource is = new InputSource(new FileReader(map));
                is.setSystemId(this.mapping);
                this.mapper.loadMapping(is);
            }
            catch (Exception e)
            {
                String msg = "ForwardService: Error in castor mapping creation";
                logger.error(msg, e);
                throw new InitializationException(msg, e);
            }
        }
        else
        {
            String msg = "ForwardService: Mapping not found or not a file or unreadable: " + this.mapping;
            logger.error(msg);
            throw new InitializationException(msg);
        }


        try
        {
        
            File directory = new File(this.directory); 
            File[] files = directory.listFiles();
            for (int ix=0; ix < files.length; ix++)
            {
                if (files[ix].isDirectory())
                {
                    continue;
                }

                loadForwardConfiguration(files[ix]);
            }

        }
        catch (Exception e)
        {
            String msg = "ForwardService: Fatal error loading Forward configurations";
            logger.error(msg, e);
            throw new InitializationException(msg, e);
        }

    }

    protected String makePortletForwardKey(String portlet, String target)
    {
        StringBuffer key = new StringBuffer(portlet);
        key.append(KEY_DELIMITER);
        key.append(target);
        return key.toString();
    }

    /**
     * Load and unmarshal a Forward Configuration from a file.
     *
     * @param file the absolute file path storing this fragment
     */
    protected void loadForwardConfiguration(File file)
    {
        try
        {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbfactory.newDocumentBuilder();

            Document doc = builder.parse(file);

            Unmarshaller unmarshaller = new Unmarshaller(this.mapper);
            ForwardsConfiguration configuration = 
                (ForwardsConfiguration) unmarshaller.unmarshal((Node) doc);

            Iterator it = configuration.getForwards().iterator();
            while (it.hasNext())
            {
                Forward forward = (Forward)it.next();
                if (this.forwards.containsKey(forward.getName()))
                {
                    logger.error("ForwardService: already contains Forward key: " + forward.getName());
                }
                else
                {
                    this.forwards.put(forward.getName(), forward);
                }
                
                resyncParamMap(forward.getQueryParams());

            }

            it = configuration.getPortletForwards().iterator();
            while (it.hasNext())
            {
                PortletForward pf = (PortletForward)it.next();
                String key = makePortletForwardKey(pf.getPortlet(), pf.getTarget());
                if (this.portletForwards.containsKey(key))
                {
                    logger.error("ForwardService: already contains portletForward key: " + key);
                }
                else
                {
                    this.portletForwards.put(key, pf);
                    resyncParamMap(pf.getQueryParams());
                }
            }


        }
        catch (Throwable t)
        {
            logger.error("ForwardService: Could not unmarshal: " + file, t);
        }

    }

    private void resyncParamMap(Map map)
    {
        // Castor doesn't set the keys properly for maps
        // get the base query params        
        ArrayList list = new ArrayList(map.size());
        Iterator it = map.values().iterator();
        while (it.hasNext())
        {
            QueryParam qp = (QueryParam)it.next();
            list.add(qp);
        }                    
        map.clear();
        it = list.iterator();
        while (it.hasNext())
        {
            QueryParam qp = (QueryParam)it.next();
            map.put(qp.getName(), qp);
        }

    }
}

