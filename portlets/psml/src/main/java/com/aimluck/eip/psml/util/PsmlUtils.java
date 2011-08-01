/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.psml.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 */
public class PsmlUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PsmlUtils.class.getName());

  public static String TEMPLATE_NAME = "template";

  public static String DEFAULT_PAGE_NAME = "default.psml";

  public static String ADMIN_PORTRET_ID = "101";

  public static String ATTRIBUTE_ID = "id";

  public static String ELEMENT_ENTRY = "entry";

  public static String ELEMENT_PARAMETER = "parameter";

  public static String ParsePsml(String psml) throws Exception {
    try {
      Document dom = loadXMLFrom(psml);
      NodeList portlets = dom.getElementsByTagName("portlets");
      for (int i = 0; i < portlets.getLength(); i++) {
        Node node = portlets.item(i);
        if (node
          .getAttributes()
          .getNamedItem(PsmlUtils.ATTRIBUTE_ID)
          .getNodeValue()
          .equals(PsmlUtils.ADMIN_PORTRET_ID)) {// 管理者部分のポートレット除去
          Node parent = node.getParentNode();
          parent.removeChild(node);
        } else {
          node.getAttributes().removeNamedItem(PsmlUtils.ATTRIBUTE_ID);// idの除去
        }
      }
      NodeList entry = dom.getElementsByTagName(PsmlUtils.ELEMENT_ENTRY);
      for (int i = 0; i < entry.getLength(); i++) {
        Node node = entry.item(i);
        node.getAttributes().removeNamedItem(PsmlUtils.ATTRIBUTE_ID);// idの除去
      }
      NodeList params = dom.getElementsByTagName(PsmlUtils.ELEMENT_PARAMETER);
      while (0 != params.getLength()) {
        Node node = params.item(0);
        Node parent = node.getParentNode();
        parent.removeChild(node);
      }
      StringWriter sw = new StringWriter();
      TransformerFactory tfactory = TransformerFactory.newInstance();
      Transformer transformer = tfactory.newTransformer();
      transformer.transform(new DOMSource(dom), new StreamResult(sw));
      return sw.toString();
    } catch (Exception ex) {
      throw ex;
    }
  }

  public static org.w3c.dom.Document loadXMLFrom(java.io.InputStream is)
      throws org.xml.sax.SAXException, java.io.IOException {
    javax.xml.parsers.DocumentBuilderFactory factory =
      javax.xml.parsers.DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    javax.xml.parsers.DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (javax.xml.parsers.ParserConfigurationException ex) {
    }

    org.w3c.dom.Document doc = builder.parse(is);
    is.close();
    return doc;
  }

  public static org.w3c.dom.Document loadXMLFrom(String xml)
      throws org.xml.sax.SAXException, java.io.IOException {
    org.w3c.dom.Document doc;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
      StringReader strReader = new StringReader(xml);
      doc = builder.parse(new InputSource(strReader));
      return doc;
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static File getTemplateHtmlDefaultPsmlFile(RunData runData) {
    String path = getTemplateHtmlDefaultPsmlPath(runData);
    File psml = new File(path);
    if (!psml.exists()) {
      return null;
    }

    return psml;
  }

  public static String getTemplateHtmlDefaultPsmlPath(RunData runData) {
    // get configuration parameters from Jetspeed Resources
    String root = JetspeedResources.getString("services.PsmlManager.root");
    ServletConfig conf = runData.getServletConfig();
    String rootPath = conf.getServletContext().getRealPath(root);

    StringBuffer buffer = new StringBuffer();
    buffer.append(rootPath).append(File.separator);
    buffer.append("user").append(File.separator);
    buffer.append(TEMPLATE_NAME).append(File.separator);
    buffer.append("html").append(File.separator);
    buffer.append(DEFAULT_PAGE_NAME);
    return buffer.toString();
  }

  public static PSMLDocument loadDocument(File file, Mapping map) {
    PSMLDocument doc = null;

    if (file != null) {
      // load the document and add it to the watcher
      // we'll assume the name is the the location of the file

      doc = new BasePSMLDocument();

      // now that we have a file reference, try to load the serialized PSML
      Portlets portlets = null;
      try {
        doc.setName(file.getCanonicalPath());

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbfactory.newDocumentBuilder();

        Document d = builder.parse(file);

        Unmarshaller unmarshaller = new Unmarshaller(map);
        portlets = (Portlets) unmarshaller.unmarshal(d);

        doc.setPortlets(portlets);

      } catch (IOException e) {
        logger.error("PSMLManager: Could not load the file "
          + file.getAbsolutePath(), e);
        doc = null;
      } catch (MarshalException e) {
        logger.error("PSMLManager: Could not unmarshal the file "
          + file.getAbsolutePath(), e);
        doc = null;
      } catch (MappingException e) {
        logger.error("PSMLManager: Could not unmarshal the file "
          + file.getAbsolutePath(), e);
        doc = null;
      } catch (ValidationException e) {
        logger.error("PSMLManager: document "
          + file.getAbsolutePath()
          + " is not valid", e);
        doc = null;
      } catch (ParserConfigurationException e) {
        logger.error("PSMLManager: Could not load the file "
          + file.getAbsolutePath(), e);
        doc = null;
      } catch (SAXException e) {
        logger.error("PSMLManager: Could not load the file "
          + file.getAbsolutePath(), e);
        doc = null;
      }
    }

    return doc;

  }

  public static Mapping getMapping(RunData runData) {

    ServletConfig conf = runData.getServletConfig();

    String mapFilePath =
      conf.getServletContext().getRealPath(
        JetspeedResources.getString("services.PsmlManager.mapping"));

    File map = new File(mapFilePath);

    if (!map.exists()) {
      return null;
    }

    Mapping mapping = null;

    try {
      mapping = new Mapping();
      InputSource is = new InputSource(new FileReader(map));
      is.setSystemId(map.getCanonicalPath());
      mapping.loadMapping(is);
    } catch (Exception e) {
      logger.error("PSMLManager: Error in psml mapping creation", e);
    }

    return mapping;
  }

}
