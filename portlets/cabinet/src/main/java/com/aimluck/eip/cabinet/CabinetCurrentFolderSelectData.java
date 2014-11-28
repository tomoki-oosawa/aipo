package com.aimluck.eip.cabinet;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 現在いるフォルダを取得するためのクラスです。
 */
public class CabinetCurrentFolderSelectData extends CabinetFolderSelectData {
  @Override
  public void setSessionFolderId(RunData rundata, Context context) {
    if (rundata.getParameters().containsKey(CabinetUtils.KEY_CURRENT_FOLDER_ID)) {
      String fid =
        rundata.getParameters().getString(CabinetUtils.KEY_CURRENT_FOLDER_ID);
      ALEipUtils.setTemp(
        rundata,
        context,
        CabinetUtils.KEY_CURRENT_FOLDER_ID,
        fid);
    }
  }

  @Override
  public String getFolderIdFromSession(RunData rundata, Context context) {
    return ALEipUtils.getTemp(
      rundata,
      context,
      CabinetUtils.KEY_CURRENT_FOLDER_ID);
  }

}