package com.aimluck.eip.modules.screens;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.psmlmanager.PsmlImporter;
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipConstants;

public class PsmlExportScreen extends RawScreen {

  @Override
  protected void doOutput(RunData rundata) throws Exception {
    // TODO 自動生成されたメソッド・スタブ

    ServletOutputStream out = null;
    HttpServletResponse response = rundata.getResponse();
    response
      .setContentType("text/html; " + ALEipConstants.DEF_CONTENT_ENCODING);
    StringBuffer result = new StringBuffer();
    String message = "";

    PsmlManagerService exporterService = null;
    PsmlManagerService importerService = null;

    exporterService =
      (PsmlManagerService) TurbineServices.getInstance().getService(
        "PsmlImportManager");
    importerService = PsmlManager.getService();

    PsmlImporter importer = new PsmlImporter();
    importer.setCheck(false);
    boolean ran = importer.run(exporterService, importerService);

    result
      .append("<!doctype html public \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    result.append("<html lang=\"ja\">");
    result.append("<head>");
    result
      .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
    result.append("<title lang=\"ja\">PSML Export</title>");
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
