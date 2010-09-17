/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

package com.aimluck.eip.webmail;

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * ウェブメールのフォルダを管理するためのクラスです。 <br />
 */
public class WebMailFolderSelectData extends
    ALAbstractSelectData<EipTMailFolder, EipTMailFolder> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFolderSelectData.class.getName());

  /** フォルダID */
  String folder_id = null;

  /** メールアカウント */
  private EipMMailAccount mail_account;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    int mailAccountId = 0;

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // ENTITY ID
      if (rundata.getParameters().containsKey(WebMailUtils.FOLDER_ID)) {
        folder_id = rundata.getParameters().get(WebMailUtils.FOLDER_ID);
      }

      // メールアカウントID
      mailAccountId =
        Integer.parseInt(ALEipUtils.getTemp(
          rundata,
          context,
          WebMailUtils.ACCOUNT_ID));
    }

    ALEipUser login_user = ALEipUtils.getALEipUser(rundata);

    // 現在操作中のメールアカウントを取得する
    mail_account =
      ALMailUtils.getMailAccount(
        "",
        (int) login_user.getUserId().getValue(),
        mailAccountId);
    if (mail_account == null) {
      logger.error("[WebMail Folder] mail account was not found.");
      return;
    }

    // フォルダIDが設定されていない場合や、対象のフォルダが見つからない場合はエラーにする
    EipTMailFolder folder =
      WebMailUtils.getEipTMailFolder(mail_account, folder_id);
    if (folder == null) {
      logger.error("[WebMail Folder] mail folder was not found.");
      return;
    }

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipTMailFolder> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTMailFolder selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    EipTMailFolder folder = WebMailUtils.getEipTMailFolder(rundata, context);

    return folder;
  }

  /**
   * フォルダのデータを取得します。
   * 
   * 
   */
  @Override
  protected Object getResultData(EipTMailFolder obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * フォルダの詳細データを取得します。
   * 
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTMailFolder record)
      throws ALPageNotFoundException, ALDBErrorException {

    try {
      folder_id = record.getFolderId().toString();

      WebMailFolderResultData rd = new WebMailFolderResultData();
      rd.initField();
      rd.setFolderId(record.getFolderId().longValue());
      rd.setFolderName(record.getFolderName());
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));

      // デフォルトのフォルダは編集・削除不可
      if (mail_account.getDefaultFolderId().equals(record.getFolderId())) {
        rd.setCanUpdate(false);
      } else {
        rd.setCanUpdate(true);
      }

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    return map;
  }

  public String getFolderId() {
    return folder_id;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_CABINET_FOLDER;
  }
}
