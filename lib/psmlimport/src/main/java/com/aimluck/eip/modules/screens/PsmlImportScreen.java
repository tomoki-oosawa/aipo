package com.aimluck.eip.modules.screens;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.modules.actions.portlets.PsmlManagerAction;
import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
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
    JetspeedLogFactoryService.getLogger(PsmlManagerAction.class.getName());

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
        logger.error("PsmlManagerAction: Could not load the file "
          + f.getAbsolutePath(), e);
      } catch (MarshalException e) {
        logger.error("PsmlManagerAction: Could not unmarshal the file "
          + f.getAbsolutePath(), e);
      } catch (MappingException e) {
        logger.error("PsmlManagerAction: Could not unmarshal the file "
          + f.getAbsolutePath(), e);
      } catch (ValidationException e) {
        logger.error("PsmlManagerAction: document "
          + f.getAbsolutePath()
          + " is not valid", e);
      } catch (Exception e) {
        logger.error("PsmlManagerAction: Error while loading  "
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
          // ProfileLocator locator = this.mapFileToLocator(path);
          PSMLDocument doc = this.loadDocument(psml);

          //
          // create a new profile
          //
          if (doc != null) {
            Portlets portlets = doc.getPortlets();

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
    // TODO 自動生成されたメソッド・スタブ
    return null;
  }

}
