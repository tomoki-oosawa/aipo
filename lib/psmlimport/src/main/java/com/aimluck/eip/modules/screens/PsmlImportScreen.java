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

package com.aimluck.eip.modules.screens;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.RunData;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import com.aimluck.eip.common.ALEipConstants;

public class PsmlImportScreen extends RawScreen {

  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(PsmlImportScreen.class.getName());

  /**
   * This method recursively collect all .psml documents starting at the given
   * root
   * 
   * @param v
   *          Vector to put the file into
   * @param root
   *          Root directory for import
   */
  private void collectPsml(Vector v, String root) {

    File dir = new File(root);
    File[] files = dir.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory()) {
        collectPsml(v, files[i].getPath());
      } else if (files[i].isFile() && files[i].getPath().endsWith(".psml")) {
        v.add(files[i]);
      }
    }
  }

  /**
   * Loads psml mapping file
   * 
   * @exception Exception
   */
  private Mapping loadMapping() throws Exception {
    // get configuration parameters from Jetspeed Resources
    ResourceService serviceConf =
      ((TurbineServices) TurbineServices.getInstance())
        .getResources(PsmlManagerService.SERVICE_NAME);

    // test the mapping file and create the mapping object
    Mapping mapping = null;
    String mapFile =
      serviceConf.getString(
        "mapping",
        "${webappRoot}/WEB-INF/conf/psml-mapping.xml");
    mapFile = TurbineServlet.getRealPath(mapFile);
    if (mapFile != null) {
      File map = new File(mapFile);
      if (logger.isDebugEnabled()) {
        logger.debug("Loading psml mapping file " + mapFile);
      }
      if (map.exists() && map.isFile() && map.canRead()) {
        try {
          mapping = new Mapping();
          InputSource is = new InputSource(new FileReader(map));
          is.setSystemId(mapFile);
          mapping.loadMapping(is);
        } catch (Exception e) {
          logger.error("Error in psml mapping creation", e);
          throw new Exception("Error in mapping");
        }
      } else {
        throw new Exception(
          "PSML Mapping not found or not a file or unreadable: " + mapFile);
      }
    }

    return mapping;
  }

  /**
   * Load a PSMLDOcument from disk
   * 
   * @param fileOrUrl
   *          a String representing either an absolute URL or an absolute
   *          filepath
   */
  private PSMLDocument loadDocument(String fileOrUrl) {
    PSMLDocument doc = null;

    if (fileOrUrl != null) {
      // we'll assume the name is the the location of the file
      File f = null;

      f = new File(fileOrUrl);

      if (!f.exists()) {
        return null;
      }

      doc = new BasePSMLDocument();
      doc.setName(fileOrUrl);

      // now that we have a file reference, try to load the serialized PSML
      Portlets portlets = null;
      FileReader reader = null;
      try {
        reader = new FileReader(f);
        Unmarshaller unmarshaller = new Unmarshaller(this.loadMapping());
        portlets = (Portlets) unmarshaller.unmarshal(reader);
        doc.setPortlets(portlets);
      } catch (IOException e) {
        logger.error("PsmlImportScreen: Could not load the file "
          + f.getAbsolutePath(), e);
      } catch (MarshalException e) {
        logger.error("PsmlImportScreen: Could not unmarshal the file "
          + f.getAbsolutePath(), e);
      } catch (MappingException e) {
        logger.error("PsmlImportScreen: Could not unmarshal the file "
          + f.getAbsolutePath(), e);
      } catch (ValidationException e) {
        logger.error("PsmlImportScreen: document "
          + f.getAbsolutePath()
          + " is not valid", e);
      } catch (Exception e) {
        logger.error("PsmlImportScreen: Error while loading  "
          + f.getAbsolutePath(), e);
      } finally {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
    }

    return doc;
  }

  private ProfileLocator mapFileToLocator(String path) throws Exception {
    if (logger.isDebugEnabled()) {
      logger
        .debug("PsmlUpdateAction.createFromPath: processing path = " + path);
    }
    ProfileLocator result = Profiler.createLocator();

    // Tokenize the file path into elements
    StringTokenizer tok = new StringTokenizer(path, File.separator);

    // Load path elements into a vector for random access
    Vector tokens = new Vector();
    while (tok.hasMoreTokens()) {
      tokens.add(tok.nextToken());
    }

    // Assume that 1st element is the profile type (user|role|group) and 2nd is
    // the name
    if (tokens.size() > 1) {
      String type = (String) tokens.elementAt(0);
      String name = (String) tokens.elementAt(1);
      if (type.equals(Profiler.PARAM_USER)) {
        result.setUser(JetspeedSecurity.getUser(name));
      } else if (type.equals(Profiler.PARAM_GROUP)) {
        result.setGroup(JetspeedSecurity.getGroup(name));
      } else if (type.equals(Profiler.PARAM_ROLE)) {
        result.setRole(JetspeedSecurity.getRole(name));
      }
    }

    // Assume that the last element is the page name
    if (tokens.size() > 0) {
      result.setName((String) tokens.lastElement());
    }

    // Based on the number of path elements set the other profile attributes
    switch (tokens.size()) {
      case 3: // user|role|group/name/page.psml
        break;
      case 4: // user|role|group/name/media-type/page.psml
        result.setMediaType((String) tokens.elementAt(2));
        break;
      case 5: // user|role|group/name/media-type/language/page.psml
        result.setMediaType((String) tokens.elementAt(2));
        result.setLanguage((String) tokens.elementAt(3));
        break;
      case 6: // user|role|group/name/media-type/language/country/page.psml
        result.setMediaType((String) tokens.elementAt(2));
        result.setLanguage((String) tokens.elementAt(3));
        result.setCountry((String) tokens.elementAt(4));
        break;
      default:
        throw new Exception("Path must contain 3 to 6 elements: ["
          + path
          + "], and the size was: "
          + tokens.size());
    }

    return result;
  }

  /**
   * File Import All Action for Psml.
   * 
   * @param rundata
   *          The turbine rundata context for this request.
   * @param context
   *          The velocity context for this request.
   * @exception Exception
   */
  private boolean doImportall(RunData rundata) throws Exception {
    String copyFrom = null;
    PsmlManagerService exporterService = null;
    PsmlManagerService importerService = null;

    exporterService =
      (PsmlManagerService) TurbineServices.getInstance().getService(
        "PsmlImportManager");
    importerService = PsmlManager.getService();

    try {
      copyFrom =
        rundata.getParameters().getString("CopyFrom", "/WEB-INF/psml/");
      copyFrom = TurbineServlet.getRealPath(copyFrom);

      //
      // Collect all .psml files from the root specified
      //
      Vector files = new Vector();
      this.collectPsml(files, copyFrom);

      //
      // Process each file
      //
      for (Iterator it = files.iterator(); it.hasNext();) {
        // If error occurs processing one entry, continue on with the others
        String path = null;
        try {
          String psml = ((File) it.next()).getPath();
          path = psml.substring(copyFrom.length() + 1);
          ProfileLocator locator = mapFileToLocator(path);
          PSMLDocument doc = loadDocument(psml);

          //
          // create a new profile
          //
          if (doc != null) {
            // Portlets portlets = doc.getPortlets();
            Profile profile = Profiler.getProfile(locator);

            exporterService.createDocument(profile);

            // org.apache.jetspeed.util.PortletUtils.regenerateIds(clonedPortlets);
            // Profiler.createProfile(locator, clonedPortlets);
          } else {
            throw new Exception("Failed to load PSML document ["
              + psml
              + "] from disk");
          }
        } catch (Exception ouch) {
          logger.error("Exception", ouch);
          rundata.addMessage("ERROR importing file ["
            + path
            + "]: "
            + ouch.toString()
            + "<br>");
        }
      }
    } catch (Exception e) {
      // log the error msg
      logger.error("Exception", e);

      return false;
    }
    return true;
  }

  @Override
  protected void doOutput(RunData rundata) throws Exception {
    // TODO 自動生成されたメソッド・スタブ

    ServletOutputStream out = null;
    HttpServletResponse response = rundata.getResponse();

    response
      .setContentType("text/html; " + ALEipConstants.DEF_CONTENT_ENCODING);
    StringBuffer result = new StringBuffer();
    String message = "";

    boolean ran = doImportall(rundata);

    result
      .append("<!doctype html public \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    result.append("<html lang=\"ja\">");
    result.append("<head>");
    result
      .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
    result.append("<title lang=\"ja\">PSML Import</title>");
    result.append("</head>");
    result.append("<body>");
    result.append(message);
    result.append("</body>");
    result.append("</html>");

    out = response.getOutputStream();
    out.print(result.toString());
    out.flush();
    out.close();
  }

  @Override
  protected String getContentType(RunData rundata) {
    return null;
  }

}
