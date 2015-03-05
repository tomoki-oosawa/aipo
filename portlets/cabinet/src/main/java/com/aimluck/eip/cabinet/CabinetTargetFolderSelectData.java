package com.aimluck.eip.cabinet;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 操作対象フォルダを取得するためのクラスです。
 */
public class CabinetTargetFolderSelectData extends CabinetFolderSelectData {
  @Override
  public void setSessionFolderId(RunData rundata, Context context) {
    if (rundata.getParameters().containsKey(CabinetUtils.KEY_TARGET_FOLDER_ID)) {
      ALEipUtils.setTemp(
        rundata,
        context,
        CabinetUtils.KEY_TARGET_FOLDER_ID,
        rundata.getParameters().getString(CabinetUtils.KEY_TARGET_FOLDER_ID));
    }
  }

  @Override
  public String getFolderIdFromSession(RunData rundata, Context context) {
    return ALEipUtils.getTemp(
      rundata,
      context,
      CabinetUtils.KEY_TARGET_FOLDER_ID);
  }

  /**
   * 操作対象フォルダの詳細データを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTCabinetFolder selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    return CabinetUtils.getEipTCabinetTargetFolder(rundata, context);
  }

}