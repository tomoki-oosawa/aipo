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
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * 共有フォルダのフォルダフォームデータを管理するクラス <BR>
 * 
 */
public class WebMailFilterFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFilterFormData.class.getName());

  /** フィルタ名 */
  private ALStringField filter_name;

  /** フィルタ文字列 */
  private ALStringField filter_string;

  /** フィルタ種別 */
  private ALStringField filter_type;

  /** 振り分け先フォルダID */
  private ALNumberField dst_folder_id;

  /** ログインユーザー */
  private ALEipUser login_user;

  /** フィルタID */
  private String filterId;

  /** フィルタと紐付くメールアカウント */
  private EipMMailAccount mailAccount;

  /** フィルタと紐付くメールアカウント名 */
  private ALStringField mailAccountName;

  /** アカウントに紐付くフォルダリスト */
  private List<WebMailFolderResultData> folderList;

  private Map<String, String> typeList;

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
        // パラメータにアカウントIDがあった場合
        if (rundata.getParameters().containsKey(WebMailUtils.ACCOUNT_ID)) {
          mailAccountId =
            Integer.parseInt(rundata.getParameters().get(
              WebMailUtils.ACCOUNT_ID));
        } else {
          // 無い場合はセッションからアカウントIDを取得する。
          mailAccountId =
            Integer.parseInt(ALEipUtils.getTemp(
              rundata,
              context,
              WebMailUtils.ACCOUNT_ID));
        }

        // フィルタIDを取得
        filterId =
          ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      } catch (Exception e) {
        return;
      }
    }

    login_user = ALEipUtils.getALEipUser(rundata);

    // メールアカウントを取得する
    mailAccount =
      ALMailUtils.getMailAccount(
        (int) login_user.getUserId().getValue(),
        mailAccountId);

    mailAccountName.setValue(mailAccount.getAccountName());

    // フィルタタイプ一覧を取得する
    typeList = ALMailUtils.getMailFilterTypeMap();
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
    // フォルダ一覧
    loadMailFolderList(rundata, context);

    boolean res = super.setFormData(rundata, context, msgList);

    return res;
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // フィルタ名
    filter_name = new ALStringField();
    filter_name
      .setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_FILTER_NAME"));
    filter_name.setTrim(true);

    // フィルタ文字列
    filter_string = new ALStringField();
    filter_string.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_DIVIDE_CONDITION"));
    filter_string.setTrim(true);

    // フィルタ種別
    filter_type = new ALStringField();
    filter_type.setTrim(true);

    // 振り分け先フォルダ
    dst_folder_id = new ALNumberField();
    dst_folder_id.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_DIVIDE_FOLDER"));

    // フォルダ一覧
    folderList = new ArrayList<WebMailFolderResultData>();

    mailAccountName = new ALStringField();
  }

  /**
   * フォルダの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // フィルタ名必須項目
    filter_name.setNotNull(true);
    // フィルタ名の文字数制限
    filter_name.limitMaxLength(128);

    // フィルタ文字列必須項目
    filter_string.setNotNull(true);
    // フィルタ文字列の文字数制限
    filter_string.limitMaxLength(128);
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
    // フィルタ名
    filter_name.validate(msgList);

    // フィルタ文字列
    filter_string.validate(msgList);

    // 振り分け先フォルダ
    dst_folder_id.validate(msgList);

    // 同じフィルタ名が無いかどうか確かめる
    if (existsFilterName(filter_name.getValue(), ALEipConstants.MODE_UPDATE
      .equals(getMode()))) {
      msgList.add("このフィルタ名と同じフィルタがすでに存在するため、登録できません。フィルタ名を変更してください。");
    }

    return (msgList.size() == 0);
  }

  /**
   * 同じアカウントに同じ名前のフィルタがあるかどうか調べます。
   * 
   * @return
   */
  private boolean existsFilterName(String fname, boolean is_update) {
    if (fname == null || "".equals(fname)) {
      return false;
    }

    try {
      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTMailFilter.FILTER_NAME_PROPERTY, fname);
      if (is_update) {
        exp =
          exp.andExp(ExpressionFactory.noMatchDbExp(
            EipTMailFilter.FILTER_ID_PK_COLUMN,
            filterId));
      }
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
          mailAccount);

      List<EipTMailFilter> list =
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
   * フィルタをデータベースから読み出します。 <BR>
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
      // フィルタIDを取得
      String filterId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      // オブジェクトモデルを取得
      EipTMailFilter filter =
        WebMailUtils.getEipTMailFilter(mailAccount, filterId);
      if (filter == null) {
        return false;
      }

      // フィルタ名
      filter_name.setValue(filter.getFilterName());

      // フィルタ文字列
      filter_string.setValue(filter.getFilterString());

      // フィルタ種別
      filter_type.setValue(filter.getFilterType());

      // 宛先フォルダ
      dst_folder_id.setValue(filter.getEipTMailFolder().getFolderId());

      // フォルダ一覧
      loadMailFolderList(rundata, context);
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return false;
    }
    return true;
  }

  /**
   * フィルタをデータベースから削除します。 <BR>
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
      // フィルタIDを取得
      String filterId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      // 削除するフィルタオブジェクトモデルを取得する．
      EipTMailFilter filter =
        WebMailUtils.getEipTMailFilter(mailAccount, filterId);

      // ソート番号を取得
      int sortOrder = filter.getSortOrder();

      // ソート番号のずれをなおす
      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);

      Expression exp =
        ExpressionFactory.matchExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
          filter.getEipMMailAccount());
      Expression exp2 =
        ExpressionFactory.greaterOrEqualExp(
          EipTMailFilter.SORT_ORDER_PROPERTY,
          sortOrder + 1);

      List<EipTMailFilter> filters =
        query.setQualifier(exp.andExp(exp2)).fetchList();
      for (EipTMailFilter correct_filter : filters) {
        correct_filter.setSortOrder(correct_filter.getSortOrder() - 1);
      }

      // フィルタ情報を削除、オーダー番号を更新
      Database.delete(filter);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        filter.getFilterId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FILTER,
        filter.getFilterName());
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailFilterFormData]", t);
      return false;
    }
    return true;
  }

  /**
   * フィルタをデータベースに格納します。 <BR>
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
      // 当該アカウントに所属する振り分け先フォルダを取得する
      EipTMailFolder mailFolder =
        WebMailUtils.getEipTMailFolder(mailAccount, String
          .valueOf(dst_folder_id.getValue()));

      // 新規オブジェクトモデル
      EipTMailFilter filter = Database.create(EipTMailFilter.class);

      // フィルタ名
      filter.setFilterName(filter_name.getValue());

      // フィルタ文字列
      filter.setFilterString(filter_string.getValue());

      // フィルタタイプ
      filter.setFilterType(filter_type.getValue());

      // 振り分け先フォルダ
      filter.setEipTMailFolder(mailFolder);

      // アカウントID
      filter.setEipMMailAccount(mailAccount);

      // ソート番号
      filter
        .setSortOrder(WebMailUtils.getMailFilterLastSortOrder(mailAccount) + 1);

      // フィルタを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        filter.getFilterId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FILTER,
        filter_name.getValue());

      return true;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailFilterFormData]", t);
      return false;
    }
  }

  /**
   * データベースに格納されているフィルタを更新します。 <BR>
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
      // フィルタIDを取得
      String filterId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      // オブジェクトモデルを取得
      EipTMailFilter filter =
        WebMailUtils.getEipTMailFilter(mailAccount, filterId);
      if (filter == null) {
        return false;
      }

      // 当該アカウントに所属する振り分け先フォルダを取得する
      EipTMailFolder mailFolder =
        WebMailUtils.getEipTMailFolder(mailAccount, String
          .valueOf(dst_folder_id.getValue()));

      // フィルタ名
      filter.setFilterName(filter_name.getValue());

      // フィルタ文字列
      filter.setFilterString(filter_string.getValue());

      // フィルタ種別
      filter.setFilterType(filter_type.getValue());

      // 振り分け先フォルダ
      filter.setEipTMailFolder(mailFolder);

      // フィルタを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        filter.getFilterId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FILTER,
        filter_name.getValue());
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailFilterFormData]", t);
      return false;
    }
    return true;
  }

  /**
   * 
   * フォルダ一覧をロードします。
   * 
   * @param rundata
   * @param context
   */
  public void loadMailFolderList(RunData rundata, Context context) {
    if (mailAccount == null) {
      return;
    }
    try {
      // フォルダ一覧を取得する
      List<EipTMailFolder> mailFolders =
        ALMailUtils.getEipTMailFolderAll(mailAccount);
      for (EipTMailFolder folder : mailFolders) {
        WebMailFolderResultData rd = new WebMailFolderResultData();
        rd.initField();
        rd.setFolderId(folder.getFolderId());
        rd.setFolderName(folder.getFolderName());
        folderList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("webmail", ex);
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
   * 対象のメールアカウント名を取得する． <BR>
   * 
   * @return
   */
  public ALStringField getMailAccountName() {
    return mailAccountName;
  }

  /**
   * フィルタ名を取得する． <BR>
   * 
   * @return
   */
  public ALStringField getFilterName() {
    return filter_name;
  }

  /**
   * フィルタ文字列を取得する． <BR>
   * 
   * @return
   */
  public ALStringField getFilterString() {
    return filter_string;
  }

  /**
   * フィルタ種別を取得する． <BR>
   * 
   * @return
   */
  public String getFilterType() {
    return filter_type.getValue();
  }

  /**
   * 振り分け先フォルダIDを取得する． <BR>
   * 
   * @return
   */
  public ALNumberField getDstFolderId() {
    return dst_folder_id;
  }

  /**
   * フォルダ一覧を取得する． <BR>
   * 
   * @return
   */
  public List<WebMailFolderResultData> getFolderList() {
    return folderList;
  }

  /**
   * フィルタタイプ一覧を取得する． <BR>
   * 
   * @return
   */
  public Map<String, String> getTypeList() {
    return typeList;
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
