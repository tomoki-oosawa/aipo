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

package org.apache.jetspeed.portal.portlets;

//standard java stuff
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

//JAXP support
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.ecs.ConcreteElement;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
//Element Construction Set
import org.apache.jetspeed.util.JetspeedClearElement;
//standard Jetspeed stuff
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.util.SimpleTransform;
import org.apache.jetspeed.xml.JetspeedXMLEntityResolver;
//turbine
import org.apache.turbine.util.RunData;
//XML stuff
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * Portlet which renders RDF Site Summary.
 * </p>
 * <p>
 * This portlet uses XML stylesheet for transforming the RSS content into
 * display markup depending on the MimeType requested by the user-agent
 * </p>
 * <p>
 * It accepts the following parameters :
 * <dl>
 * <dt>itemDisplayed</dt>
 * <dd>The number of items from the RSS file to display on screen. Default 15
 * for HTML, 5 for WML</dd>
 * <dt>showDescription</dt>
 * <dd>Should the portlet show the item descriptions. Must be true or false.
 * Default: true for HTML, false for WML</dd>
 * <dt>showTitle</dt>
 * <dd>Should the portlet show the channel description. Must be true or false.
 * Default: true for HTML, false for WML</dd>
 * <dt>stylesheet[.<mime>]</dt>
 * <dd>The stylesheet URL. If a mime-type is specified, the stylesheet is only
 * used for this mime-type</dd>
 * </dl>
 * 
 * @author <A HREF="mailto:raphael@apache.org">Rapha�l Luta</A>
 * @version $Id: NewRSSPortlet.java,v 1.22 2004/02/23 04:03:34 jford Exp $
 */
public class NewRSSPortlet extends FileWatchPortlet {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(NewRSSPortlet.class.getName());

  public final static String ERROR_NOT_VALID =
    "This does not appear to be an RSS document";

  public final static String INVALID_TYPE =
    "Unable to display for this browser";

  private Document document = null;

  private Hashtable stylesheets = null;

  private Hashtable params = null;

  /**
   * This method loads the init parameters and parse the document tied to this
   * portlet
   */
  @Override
  public void init() throws PortletException {

    // first make sure we propagate init
    super.init();

    DocumentBuilder parser = null;
    String url = null;

    // load stylesheets available
    stylesheets = new Hashtable();
    params = new Hashtable();
    Iterator i = this.getPortletConfig().getInitParameterNames();
    while (i.hasNext()) {
      String name = (String) i.next();
      String base = MimeType.HTML.toString();

      if (name.startsWith("stylesheet")) {
        int idx = -1;
        if ((idx = name.indexOf(".")) > -1) {
          base = name.substring(idx + 1, name.length());
        }
        stylesheets.put(base, getPortletConfig().getInitParameter(name));
      } else {
        params.put(name.toLowerCase(), getPortletConfig()
          .getInitParameter(name));
      }
    }

    // read content, clean it, parse it and cache the DOM
    try {
      final DocumentBuilderFactory docfactory =
        DocumentBuilderFactory.newInstance();
      // Have it non-validating
      docfactory.setValidating(false);
      parser = docfactory.newDocumentBuilder();
      parser.setEntityResolver(new JetspeedXMLEntityResolver());

      url = getPortletConfig().getURL();
      String content = JetspeedDiskCache.getInstance().getEntry(url).getData();
      CapabilityMap xmap =
        CapabilityMapFactory.getCapabilityMap(CapabilityMapFactory.AGENT_XML);
      setContent(new JetspeedClearElement(content), xmap);
      InputSource isrc = new InputSource(this.cleanse(content));
      isrc.setSystemId(url);
      isrc.setEncoding("UTF-8");
      this.document = parser.parse(isrc);
      this.setMetainfo(document);

    } catch (Throwable t) {

      String message = "RSSPortlet:  Couldn't parse out XML document -> " + url;

      logger.error(message, t);
      throw new PortletException(t.getMessage());
    }

  }

  /**
   * Parse out title and description
   *
   * @param document
   */
  private void setMetainfo(Document document) throws PortletException {
    // Determine title and description for this portlet
    String title = null;
    String description = null;

    // now find the channel node.
    Node channel = null;

    NodeList list = document.getElementsByTagName("channel");

    if (list.getLength() != 1) {
      throw new PortletException(ERROR_NOT_VALID);
    }

    channel = list.item(0);

    Node tn = getNode(channel, "title");

    if (tn == null) {
      throw new PortletException(ERROR_NOT_VALID);
    } else {
      Node fc = tn.getFirstChild();
      if (fc != null) {
        title = fc.getNodeValue();
      }
    }

    Node dn = getNode(channel, "description");

    if (dn != null) {
      Node fc = dn.getFirstChild();
      if (fc != null) {
        description = fc.getNodeValue();
      }
    }

    this.setTitle(title);
    this.setDescription(description);
  }

  /**
   * This methods outputs the content of the portlet for a given request.
   * 
   * @param data
   *          the RunData object for the request
   * @return the content to be displayed to the user-agent
   */
  @Override
  public ConcreteElement getContent(RunData data) {
    if (org.apache.jetspeed.util.PortletSessionState.getPortletConfigChanged(
      this,
      data)) {
      try {
        init();
      } catch (PortletException pe) {
        logger.error("Exception", pe);
      }
    }
    CapabilityMap map = ((JetspeedRunData) data).getCapability();
    String type = map.getPreferredType().toString();
    ConcreteElement content = new JetspeedClearElement(INVALID_TYPE);
    String stylesheet = (String) stylesheets.get(type);

    if (stylesheet != null) {
      content = getContent(data, map);
      if (content == null) {
        try {
          content =
            new JetspeedClearElement(SimpleTransform.transform(
              this.document,
              stylesheet,
              this.params));
          setContent(content, map);
        } catch (SAXException e) {
          logger.error("Exception", e);
          content = new JetspeedClearElement(e.getMessage());
        }
      }
    } else {
      if (map.getPreferredType().equals(MimeType.XML)) {
        return getContent(data, map);
      }
    }

    return content;
  }

  /**
   * This portlet supports has many types as those it has stylesheets defined
   * for in its parameters
   * 
   * @see Portlet#supportsType
   * @param mimeType
   *          the MIME type queried
   * @return true if the portlet knows how to display content for mimeType
   */
  @Override
  public boolean supportsType(MimeType mimeType) {

    Enumeration en = stylesheets.keys();
    while (en.hasMoreElements()) {
      String type = (String) en.nextElement();
      if (type.equals(mimeType.toString())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Utility method for traversing the document parsed DOM tree and retrieving a
   * Node by tagname
   * 
   * @param start
   *          the parent node for the search
   * @param name
   *          the tag name to be searched for
   * @return the first child node of start whose tagname is name
   */
  private final Node getNode(Node start, String name) {

    NodeList list = start.getChildNodes();

    for (int i = 0; i < list.getLength(); ++i) {

      Node node = list.item(i);

      if (node.getNodeName().equals(name)) {
        return node;
      }
    }
    return null;
  }

  /**
   * Given a URL to some content, clean the content to Xerces can handle it
   * better. Right now this involves:
   * <ul>
   * <li>
   * If the document doesn't begin with "<?xml version=" truncate the content
   * until this is the first line</li>
   * 
   * </ul>
   */
  private Reader cleanse(String content) throws IOException {

    String filtered = null;

    // specify the XML declaration to search for... this is just a subset
    // of the content but it will always exist.
    String XMLDECL = "<?xml version=";

    int start = content.indexOf(XMLDECL);

    if (start <= 0) {
      filtered = content;
    } else {
      filtered = content.substring(start, content.length());
    }

    return new StringReader(filtered);
  }

}
