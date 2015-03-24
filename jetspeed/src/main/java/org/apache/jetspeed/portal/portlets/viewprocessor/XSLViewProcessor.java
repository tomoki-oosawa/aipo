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
package org.apache.jetspeed.portal.portlets.viewprocessor;

//standard java stuff
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.io.IOException;

//JAXP support
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// Jetspeed api
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.GenericMVCContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.util.SimpleTransform;
import org.apache.jetspeed.xml.JetspeedXMLEntityResolver;

// Element Construction Set
import org.apache.ecs.ConcreteElement;

// Turbine api
import org.apache.turbine.util.RunData;

// XML stuff
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Simple ViewProcessor which does a basic XSLT transform with the stylesheet parameter
 * and the given URL.
 * 
 * @author tkuebler@cisco.com
 * @version $Id: $
 * @since 1.4b4
 */

public class XSLViewProcessor implements ViewProcessor
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(XSLViewProcessor.class.getName());
    
    private static final String XMLDECL = "<?xml version=";
    public static final String ERROR_NOT_VALID = "This does not appear to be an XML document";
    public static final String INVALID_TYPE = "Unable to display for this browser";
    protected Document document = null;
    protected Hashtable stylesheets = null;
    private Hashtable params = null;

    /**
     * This method loads the init parameters and
     * parse the document tied to this portlet
     * 
     * @param portlet
     * @exception PortletException
     */
    public void init(Portlet portlet)
    throws PortletException
    {

        DocumentBuilder parser = null;
        String url = null;

        // load stylesheets available
        stylesheets = new Hashtable();
        params = new Hashtable();

        Iterator i = portlet.getPortletConfig().getInitParameterNames();

        while (i.hasNext())
        {

            String name = (String) i.next();
            String base = MimeType.HTML.toString();

            if (name.startsWith("stylesheet"))
            {

                int idx = -1;

                if ((idx = name.indexOf(".")) > -1)
                {
                    base = name.substring(idx + 1, name.length());
                }

                stylesheets.put(base, portlet.getPortletConfig().getInitParameter(name));
            }
            else
            {
                params.put(name.toLowerCase(), portlet.getPortletConfig().getInitParameter(name));
            }
        }

        // read content, clean it, parse it and cache the DOM
        try
        {

            final DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();

            //Have it non-validating
            docfactory.setValidating(false);
            parser = docfactory.newDocumentBuilder();
            parser.setEntityResolver(new JetspeedXMLEntityResolver());
            url = portlet.getPortletConfig().getURL();

            String content = JetspeedDiskCache.getInstance().getEntry(url).getData();
            CapabilityMap xmap = CapabilityMapFactory.getCapabilityMap(CapabilityMapFactory.AGENT_XML);

            // no cache yet // portlet.setContent( new JetspeedClearElement(content), xmap );
            InputSource isrc = new InputSource(this.cleanse(content));
            isrc.setSystemId(url);
            isrc.setEncoding("UTF-8");
            this.document = parser.parse(isrc);
        }
        catch (Throwable t)
        {

            String message = "XSLViewProcessor:  Couldn't parse out XML document -> " + url;
            logger.error(message, t);
            throw new PortletException(t.getMessage());
        }

    }

    /**
     * This methods outputs the content of the portlet for a given
     * request.
     * 
     * @param context
     * @return the content to be displayed to the user-agent
     */
    public Object processView(GenericMVCContext context)
    {

        try
        {
            init((Portlet) context.get("portlet"));
        }
        catch (PortletException pe)
        {
            logger.error("XSLViewProcessor - error: " + pe.getMessage(), pe);
        }

        RunData data = (RunData) context.get("data");
        CapabilityMap map = ((JetspeedRunData) data).getCapability();
        String type = map.getPreferredType().toString();
        ConcreteElement content = new JetspeedClearElement(INVALID_TYPE);
        String stylesheet = (String) stylesheets.get(type);

        if (stylesheet != null)
        {

            try
            {
                content = new JetspeedClearElement(SimpleTransform.transform(this.document, stylesheet, this.params));

                // no caching yet // setContent( content, map );
            }
            catch (SAXException e)
            {
                logger.error("SAXException", e);
                content = new JetspeedClearElement(e.getMessage());
            }
        }
        else
        {
            content = new JetspeedClearElement("stylesheet not defined");
        }

        return content;
    }

    /**
     * This portlet supports has many types as those
     * it has stylesheets defined for in its parameters
     * 
     * @param mimeType the MIME type queried
     * @return true if the portlet knows how to display
     *         content for mimeType
     * @see Portlet#supportsType
     */
    public boolean supportsType(MimeType mimeType)
    {

        Enumeration en = stylesheets.keys();

        while (en.hasMoreElements())
        {

            String type = (String) en.nextElement();

            if (type.equals(mimeType.toString()))
            {

                return true;
            }
        }

        return false;
    }

    /**
     * Utility method for traversing the document parsed
     * DOM tree and retrieving a Node by tagname
     * 
     * @param start  the parent node for the search
     * @param name   the tag name to be searched for
     * @return the first child node of start whose tagname
     *         is name
     */
    protected Node getNode(Node start, String name)
    {

        NodeList list = start.getChildNodes();

        for (int i = 0; i < list.getLength(); ++i)
        {

            Node node = list.item(i);

            if (node.getNodeName().equals(name))
            {

                return node;
            }
        }

        return null;
    }

    /**
     *  Given a URL to some content, clean the content to Xerces can handle it
     *  better.  Right now this involves:
     * <ul>
     *     <li>
     *         If the document doesn't begin with "&lt;?xml version=" truncate the
     *         content until this is the first line
     *     </li>
     * 
     * </ul>
     * 
     * @param content
     * @return 
     * @exception IOException
     */
    protected Reader cleanse(String content)
    throws IOException
    {

        String filtered = null;
        int start = content.indexOf(XMLDECL);

        if (start <= 0)
        {
            filtered = content;
        }
        else
        {
            filtered = content.substring(start, content.length());
        }

        return new StringReader(filtered);
    }
}
