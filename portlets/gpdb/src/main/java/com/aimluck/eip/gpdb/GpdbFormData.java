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
package com.aimluck.eip.gpdb;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbRecord;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class GpdbFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbFormData.class.getName());

  /** Webデータベース名 */
  private ALStringField gpdb_name;

  /** メール配信フラグ */
  private ALStringField mail_flg;

  /** WebデータベースID */
  private String gpdbId;

  /** ユーザーID */
  private int userId;

  /** フィルタ値のキー */
  private static final String RECORD_LIST_FILTER_STR = new StringBuffer()
    .append(GpdbRecordSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_FILTER)
    .toString();

  /**
   * 初期設定
   *
   * @param action
   *          ALAction
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);
  }

  /**
   * 各フィールドを初期化します。
   */
  @Override
  public void initField() {
    // Webデータベース名
    gpdb_name = new ALStringField();
    gpdb_name.setFieldName(ALLocalizationUtils.getl10n("GPDB_DATABASE_NAME"));
    gpdb_name.setTrim(true);
    // メール配信フラグ
    mail_flg = new ALStringField();
    mail_flg
      .setFieldName(ALLocalizationUtils.getl10n("GPDB_MAIL_NOTIFICATION"));
    mail_flg.setTrim(false);
    mail_flg.setValue(GpdbUtils.FLG_OFF);
  }

  /**
   * Webデータベースの各フィールドに対する制約条件を設定します。
   */
  @Override
  protected void setValidator() {
    // Webデータベース名必須項目
    gpdb_name.setNotNull(true);
    // Webデータベース名の文字数制限
    gpdb_name.limitMaxLength(50);
  }

  /**
   * データに値を設定します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    try {
      if (res) {
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          gpdbId =
            ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * Webデータベースのフォームに入力されたデータの妥当性検証を行います。
   *
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery<EipTGpdb> query = Database.query(EipTGpdb.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        // 登録
        query.setQualifier(ExpressionFactory.matchExp(
          EipTGpdb.GPDB_NAME_PROPERTY,
          gpdb_name.getValue()));

      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        // 更新
        query.setQualifier(ExpressionFactory.matchExp(
          EipTGpdb.GPDB_NAME_PROPERTY,
          gpdb_name.getValue()));
        query.andQualifier(ExpressionFactory.noMatchDbExp(
          EipTGpdb.GPDB_ID_PK_COLUMN,
          Integer.valueOf(gpdbId)));
      }

      if (!query.fetchList().isEmpty()) {
        msgList.add("Webデータベース名『 <span class='em'>"
          + gpdb_name.toString()
          + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    // Webデータベース名
    gpdb_name.validate(msgList);
    // メール配信フラグ
    mail_flg.validate(msgList);

    return msgList.isEmpty();
  }

  /**
   * Webデータベースをデータベースから読み出します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTGpdb gpdb = GpdbUtils.getEipTGpdb(rundata, context);
      if (gpdb == null) {
        return false;
      }
      // Webデータベース名
      gpdb_name.setValue(gpdb.getGpdbName());
      // メール配信フラグ
      mail_flg.setValue(gpdb.getMailFlg());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * Webデータベースをデータベースに格納します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      // ログインユーザーの取得
      TurbineUser tuser = Database.get(TurbineUser.class, userId);

      // 新規オブジェクトモデル
      EipTGpdb gpdb = Database.create(EipTGpdb.class);
      // Webデータベース名
      gpdb.setGpdbName(gpdb_name.getValue());
      // メール配信フラグ
      gpdb.setMailFlg(mail_flg.getValue());
      // 作成者
      gpdb.setTurbineUser(tuser);
      // 作成日
      gpdb.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      gpdb.setUpdateDate(Calendar.getInstance().getTime());

      // -----------------------
      // 項目定義の登録
      // -----------------------

      // 新規オブジェクトモデル
      EipTGpdbItem gpdbItem = Database.create(EipTGpdbItem.class);
      // Webデータベース
      gpdbItem.setGpdb(gpdb);
      // 項目定義名
      gpdbItem.setGpdbItemName(GpdbUtils.ITEM_NAME_DEFAULT);
      // 項目形式
      gpdbItem.setType(GpdbUtils.ITEM_TYPE_TEXT);
      // タイトルフラグ
      gpdbItem.setTitleFlg(GpdbUtils.FLG_ON);
      // 必須フラグ
      gpdbItem.setRequiredFlg(GpdbUtils.FLG_ON);
      // サイズ（横）
      gpdbItem.setSizeCol(GpdbUtils.SIZE_COL_DEFAULT);
      // 一覧画面表示フラグ
      gpdbItem.setListFlg(GpdbUtils.FLG_ON);
      // 詳細画面表示フラグ
      gpdbItem.setDetailFlg(GpdbUtils.FLG_ON);
      // デフォルトソートフラグ
      gpdbItem.setDefaultSortFlg(GpdbUtils.FLG_OFF);
      // 表示順
      gpdbItem.setOrderNo(1);
      // 作成者
      gpdbItem.setTurbineUser(tuser);
      // 作成日
      gpdbItem.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      gpdbItem.setUpdateDate(Calendar.getInstance().getTime());

      // Webデータベースを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        gpdb.getGpdbId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 「" + gpdb.getGpdbName() + "」 追加");

      // 新規データベース登録直後リダイレクト処理用
      ALEipUtils.setTemp(rundata, context, RECORD_LIST_FILTER_STR, gpdb
        .getGpdbId()
        .toString());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * Webデータベースを更新します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTGpdb gpdb = GpdbUtils.getEipTGpdb(rundata, context);
      if (gpdb == null) {
        return false;
      }

      // Webデータベース名
      gpdb.setGpdbName(gpdb_name.getValue());
      // メモ
      gpdb.setMailFlg(mail_flg.getValue());
      // 更新日
      gpdb.setUpdateDate(Calendar.getInstance().getTime());

      // Webデータベースを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        gpdb.getGpdbId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 「" + gpdb.getGpdbName() + "」 更新");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * Webデータベースをデータベースから削除します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTGpdb gpdb = GpdbUtils.getEipTGpdb(rundata, context);
      if (gpdb == null) {
        return false;
      }

      List<EipTGpdbRecord> listRecord =
        GpdbUtils.getEipTGpdbRecord("" + gpdb.getGpdbId());

      if (listRecord != null && !listRecord.isEmpty()) {
        msgList.add(GpdbUtils.ERRMSG_DB_DELETE);
        return false;
      }

      // 項目定義を削除
      GpdbUtils.removeGpdbItem(gpdb);

      // Webデータベースを削除
      Database.delete(gpdb);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        gpdb.getGpdbId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 「" + gpdb.getGpdbName() + "」 削除");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * Webデータベース名を取得します。
   *
   * @return Webデータベース名
   */
  public ALStringField getGpdbName() {
    return gpdb_name;
  }

  /**
   * メール配信フラグを取得します。
   *
   * @return メール配信フラグ
   */
  public ALStringField getMailFlg() {
    return mail_flg;
  }

}
