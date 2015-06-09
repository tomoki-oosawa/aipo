/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.webmail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * 共有フォルダのフォルダフォームデータを管理するクラス <BR>
 * 
 */
public class WebMailFolderFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFolderFormData.class.getName());

  /** フォルダ名 */
  private ALStringField folder_name;

  /** アクセス権限フラグ */
  private ALNumberField access_flag;

  /** ログインユーザー */
  private ALEipUser login_user;

  /** メールアカウント */
  private EipMMailAccount mailAccount;

  private String folderId = null;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    super.init(action, rundata, context);

    int mailAccountId = 0;

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      try {
        // セッションからアカウントIDを取得する。
        mailAccountId =
          Integer.parseInt(ALEipUtils.getTemp(
            rundata,
            context,
            WebMailUtils.ACCOUNT_ID));
      } catch (Exception e) {
        logger.error("[WebMailFolderFormData]", e);
        return;
      }

      if (rundata.getParameters().containsKey(ALEipConstants.ENTITY_ID)) {
        String entityId =
          rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
        if (!"new".equals(entityId)) {
          ALEipUtils.setTemp(
            rundata,
            context,
            ALEipConstants.ENTITY_ID,
            entityId);
          folderId = entityId;
        }
      }
    }

    login_user = ALEipUtils.getALEipUser(rundata);

    // メールアカウントを取得する
    mailAccount =
      ALMailUtils.getMailAccount(
        (int) login_user.getUserId().getValue(),
        mailAccountId);
    if (mailAccount == null) {
      return;
    }

    if (folderId != null) {
      // 指定されたフォルダがアカウントのものかどうかチェックする
      EipTMailFolder folder =
        WebMailUtils.getEipTMailFolder(mailAccount, folderId);
      if (folder == null) {
        logger.error("[WebMail Folder] mail folder was not found.");
        return;
      }
    }

  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);

    if (res) {
      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        folderId = ALEipUtils.getTemp(rundata, context, WebMailUtils.FOLDER_ID);
      }
    }
    return res;
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // フォルダ名
    folder_name = new ALStringField();
    folder_name
      .setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_FOLDER_NAME"));
    folder_name.setTrim(true);
  }

  /**
   * フォルダの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // フォルダ名必須項目
    folder_name.setNotNull(true);
    // フォルダ名の文字数制限
    folder_name.limitMaxLength(128);
  }

  /**
   * フォルダのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // フォルダ名
    folder_name.validate(msgList);

    // 同じフォルダ名が無いかどうか確かめる
    if (existsFolderName(folder_name.getValue(), ALEipConstants.MODE_UPDATE
      .equals(getMode()))) {
      msgList.add("このフォルダ名と同じフォルダがすでに存在するため、登録できません。フォルダ名を変更してください。");
    }

    return (msgList.size() == 0);
  }

  /**
   * 同じアカウントに同じ名前のフォルダがあるかどうか調べます。
   * 
   * @return
   */
  private boolean existsFolderName(String fname, boolean is_update) {
    if (fname == null || "".equals(fname)) {
      return false;
    }

    try {
      SelectQuery<EipTMailFolder> query = Database.query(EipTMailFolder.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTMailFolder.FOLDER_NAME_PROPERTY, fname);
      if (is_update) {
        exp =
          exp.andExp(ExpressionFactory.noMatchDbExp(
            EipTMailFolder.FOLDER_ID_PK_COLUMN,
            folderId));
      }
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTMailFolder.EIP_MMAIL_ACCOUNT_PROPERTY,
          mailAccount);

      List<EipTMailFolder> list =
        query.setQualifier(exp.andExp(exp2)).fetchList();
      if (list != null && list.size() > 0) {
        return true;
      }
    } catch (Exception e) {
      return true;
    }
    return false;
  }

  /**
   * フォルダをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTMailFolder folder =
        WebMailUtils.getEipTMailFolder(mailAccount, folderId);
      if (folder == null) {
        return false;
      }

      // フォルダ名
      folder_name.setValue(folder.getFolderName());

    } catch (Exception ex) {
      logger.error("webmail", ex);
      return false;
    }
    return true;
  }

  /**
   * フォルダをデータベースとファイルシステムから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      String folderId =
        ALEipUtils.getParameter(rundata, context, ALEipConstants.ENTITY_ID);

      // デフォルトのフォルダは削除不可。
      if (StringUtils.isEmpty(folderId)
        || mailAccount.getDefaultFolderId() == Integer.parseInt(folderId)) {
        return false;
      }

      // 削除するフォルダオブジェクトモデルを取得する．
      EipTMailFolder folder =
        WebMailUtils.getEipTMailFolder(mailAccount, folderId);

      // 一緒に削除するメール
      List<EipTMail> folderMails = ALMailUtils.getEipTMails(folder);

      // 振り分け先として指定してあるフィルタは、振り分け先をデフォルトに変更
      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMailFilter.EIP_TMAIL_FOLDER_PROPERTY,
          folder);

      List<EipTMailFilter> filters = query.setQualifier(exp).fetchList();
      if (filters != null && filters.size() != 0) {
        EipTMailFolder defaultFolder =
          WebMailUtils.getEipTMailFolder(mailAccount, mailAccount
            .getDefaultFolderId()
            .toString());
        for (EipTMailFilter filter : filters) {
          filter.setEipTMailFolder(defaultFolder);
        }
      }

      // ローカルファイルに保存されているメールのパスのリスト
      List<String> mailPaths = new ArrayList<String>();
      if (folderMails != null && folderMails.size() > 0) {
        for (EipTMail mail : folderMails) {
          mailPaths.add(mail.getFilePath());
        }
      }

      // 一緒にメールを削除する
      String sql = "DELETE FROM eip_t_mail WHERE FOLDER_ID = #bind($folderId)";
      Database
        .sql(EipTMail.class, sql)
        .param("folderId", folder.getFolderId())
        .execute();

      // フォルダ情報を削除
      Database.delete(folder);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        folder.getFolderId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FOLDER,
        folder.getFolderName());

      // ローカルファイルに保存されているファイルのパスを取得する．
      String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
      int type_mail =
        (WebMailUtils.TAB_RECEIVE.equals(currentTab))
          ? ALFolder.TYPE_RECEIVE
          : ALFolder.TYPE_SEND;
      int userId = ALEipUtils.getUserId(rundata);

      String orgId = Database.getDomainName();

      int accountId = -1;
      accountId =
        Integer.parseInt(ALEipUtils.getTemp(
          rundata,
          context,
          WebMailUtils.ACCOUNT_ID));
      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      ALFolder alFolder =
        handler.getALFolder(type_mail, orgId, userId, Integer
          .valueOf(accountId));
      // ローカルファイルに保存されているファイルを削除する．
      if (mailPaths.size() > 0) {
        int size = mailPaths.size();
        for (int k = 0; k < size; k++) {
          ALStorageService.deleteFile(alFolder.getFullName()
            + ALStorageService.separator()
            + mailPaths.get(k));
        }
      }
      return true;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailFolderFormData.deleteFormData]", t);
      return false;
    }
  }

  /**
   * フォルダをデータベースとファイルシステムに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 新規オブジェクトモデル
      EipTMailFolder folder = Database.create(EipTMailFolder.class);

      // フォルダ名
      folder.setFolderName(folder_name.getValue());
      // 作成日
      folder.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      folder.setUpdateDate(Calendar.getInstance().getTime());
      // アカウントID
      folder.setEipMMailAccount(mailAccount);

      // フォルダを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        folder.getFolderId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FOLDER,
        folder_name.getValue());
      return true;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailFolderFormData]", t);
      return false;
    }
  }

  /**
   * データベースとファイルシステムに格納されているフォルダを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗O
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTMailFolder folder =
        WebMailUtils.getEipTMailFolder(mailAccount, folderId);
      if (folder == null) {
        return false;
      }

      // フォルダ名
      folder.setFolderName(folder_name.getValue());

      // 更新日
      // folder.setUpdateDate(Calendar.getInstance().getTime());

      // フォルダを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        folder.getFolderId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FOLDER,
        folder_name.getValue());
      return true;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailFolderFormData]", t);
      return false;
    }
  }

  /**
   * 選択中のメールアカウントを取得する． <BR>
   * 
   * @return
   */
  public EipMMailAccount getMailAccount() {
    return mailAccount;
  }

  /**
   * フォルダ名を取得する． <BR>
   * 
   * @return
   */
  public ALStringField getFolderName() {
    return folder_name;
  }

  public ALNumberField getAccessFlag() {
    return access_flag;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }
}
