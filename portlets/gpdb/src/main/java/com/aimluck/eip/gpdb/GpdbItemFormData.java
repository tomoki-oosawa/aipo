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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
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
public class GpdbItemFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbItemFormData.class.getName());

  /** 項目定義名 */
  private ALStringField gpdb_item_name;

  /** タイトルフラグ */
  private ALStringField title_flg;

  /** 必須フラグ */
  private ALStringField required_flg;

  /** 入力形式 */
  private ALStringField type;

  /** 区分ID */
  private ALNumberField gpdb_kubun_id;

  /** 一覧画面表示フラグ */
  private ALStringField list_flg;

  /** 詳細画面表示フラグ */
  private ALStringField detail_flg;

  /** 表示サイズ（横） */
  private ALNumberField size_col;

  /** 表示サイズ（縦） */
  private ALNumberField size_row;

  /** 表示行数 */
  private ALNumberField line;

  /** デフォルトソートフラグ */
  private ALStringField default_sort_flg;

  /** ソート順 */
  private ALStringField asc_desc;

  /** 項目定義ID */
  private String gpdbItemId;

  /** WebデータベースID */
  private String gpdbId;

  /** Webデータベースオブジェクト */
  private EipTGpdb gpdb;

  /** ユーザーID */
  private int userId;

  /** 全区分の一覧 */
  private List<GpdbKubunResultData> allGpdbKubun;

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
    allGpdbKubun = GpdbUtils.getGpdbKubunAllList(); // 全区分

    // WebデータベースID
    gpdbId = rundata.getParameters().getString("gpdb_id");

    // Webデータベースオブジェクト
    gpdb = GpdbUtils.getEipTGpdb(gpdbId);
  }

  /**
   * 各フィールドを初期化します。
   */
  @Override
  public void initField() {
    // 項目定義名
    gpdb_item_name = new ALStringField();
    gpdb_item_name.setFieldName(ALLocalizationUtils.getl10n("GPDB_ITEM_NAME"));
    gpdb_item_name.setTrim(true);
    // タイトルフラグ
    title_flg = new ALStringField();
    title_flg.setFieldName(ALLocalizationUtils.getl10n("GPDB_TITLE"));
    title_flg.setTrim(false);
    title_flg.setValue(GpdbUtils.FLG_OFF);
    // 必須フラグ
    required_flg = new ALStringField();
    required_flg.setFieldName(ALLocalizationUtils.getl10n("GPDB_ESSENTIAL"));
    required_flg.setTrim(false);
    required_flg.setValue(GpdbUtils.FLG_OFF);
    // 入力形式
    type = new ALStringField();
    type.setFieldName(ALLocalizationUtils.getl10n("GPDB_INPUT_FORM"));
    type.setTrim(false);
    type.setNotNull(true);
    // 区分ID
    gpdb_kubun_id = new ALNumberField();
    gpdb_kubun_id.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_SELECTION_ITEMS"));
    gpdb_kubun_id.setNotNull(true);
    // 一覧画面表示フラグ
    list_flg = new ALStringField();
    list_flg.setFieldName(ALLocalizationUtils.getl10n("GPDB_DISPLAY_LIST"));
    list_flg.setTrim(false);
    list_flg.setValue(GpdbUtils.FLG_OFF);
    // 詳細画面表示フラグ
    detail_flg = new ALStringField();
    detail_flg.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_DISPLAY_DETAIL_SCREEN1"));
    detail_flg.setTrim(false);
    detail_flg.setValue(GpdbUtils.FLG_ON);
    // 表示サイズ（横）
    size_col = new ALNumberField();
    size_col.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_DISPLAY_SIZE_WIDTH"));
    size_col.setNotNull(true);
    size_col.limitValue(1, 999);
    // 表示サイズ（縦）
    size_row = new ALNumberField();
    size_row.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_DISPLAY_SIZE_HEIGHT"));
    size_row.setNotNull(true);
    size_row.limitValue(1, 999);
    // 表示行数
    line = new ALNumberField();
    line.setFieldName(ALLocalizationUtils.getl10n("GPDB_NUMBER_OF_LINES"));
    line.setNotNull(true);
    line.limitValue(1, 99);
    // デフォルトソートフラグ
    default_sort_flg = new ALStringField();
    default_sort_flg.setFieldName(ALLocalizationUtils.getl10n("GPDB_SORT"));
    default_sort_flg.setTrim(false);
    default_sort_flg.setValue(GpdbUtils.FLG_OFF);
    // ソート順
    asc_desc = new ALStringField();
    asc_desc.setFieldName(ALLocalizationUtils.getl10n("GPDB_SORT_ORDER"));
    asc_desc.setTrim(false);
  }

  /**
   * 項目定義の各フィールドに対する制約条件を設定します。
   */
  @Override
  protected void setValidator() {
    // Webデータベース名必須項目
    gpdb_item_name.setNotNull(true);
    // Webデータベース名の文字数制限
    gpdb_item_name.limitMaxLength(50);
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
      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        gpdbItemId =
          ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * 項目定義のフォームに入力されたデータの妥当性検証を行います。
   * 
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {

      // 項目名
      gpdb_item_name.validate(msgList);

      if (GpdbUtils.DISP_FIELD_SIZE_COL.contains(type.getValue())) {
        size_col.validate(msgList); // 表示サイズ（横）
      }

      if (GpdbUtils.DISP_FIELD_SIZE_ROW.contains(type.getValue())) {
        size_row.validate(msgList); // 表示サイズ（縦）
      }

      if (GpdbUtils.DISP_FIELD_SELECT_ITEM.contains(type.getValue())) {
        // 選択式項目の表示

        // 区分ID選択
        gpdb_kubun_id.validate(msgList); // 区分ID
      }

      if (GpdbUtils.DISP_FIELD_LINE.contains(type.getValue())) {
        line.validate(msgList); // 表示行数
      }

      if (GpdbUtils.FLG_ON.equals(title_flg.getValue())) {
        if (GpdbUtils.DISP_FIELD_REQUIRED.contains(type.getValue())
          && GpdbUtils.FLG_OFF.equals(required_flg.getValue())) {
          msgList.add("タイトルにする場合、必須にしてください");
        }
        if (GpdbUtils.FLG_OFF.equals(list_flg.getValue())) {
          msgList.add("タイトルにする場合、一覧画面表示してください");
        }
        if (GpdbUtils.FLG_OFF.equals(detail_flg.getValue())) {
          msgList.add("タイトルにする場合、詳細画面表示してください");
        }
      }

      // レコードオブジェクトモデルを取得
      int recordCount = GpdbUtils.getRecordCount(gpdbId);

      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        // 登録

        if (recordCount > 0) {
          // レコードが存在する場合

          if (GpdbUtils.FLG_ON.equals(title_flg.getValue())) {
            msgList.add("既にデータが存在するためタイトル項目は変更できません。");
          }
        }

      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        // 更新
        EipTGpdbItem gpdbItem = GpdbUtils.getEipTGpdbItem(gpdbItemId);
        if (gpdbItem == null) {
          msgList.add("更新対象のデータがありません");
          return false;
        }

        if (GpdbUtils.FLG_ON.equals(gpdbItem.getTitleFlg())
          && GpdbUtils.FLG_OFF.equals(title_flg.getValue())) {
          msgList.add("タイトルは解除できません。他の項目でタイトル指定してください");
        }

        if (recordCount > 0) {
          // レコードが存在する場合

          if (!gpdbItem.getTitleFlg().equals(title_flg.getValue())) {
            msgList.add("既にデータが存在するためタイトル項目は変更できません。");
          }

          if (!gpdbItem.getType().equals(type.getValue())) {
            msgList.add("既にデータが存在するため入力形式は変更できません。");
          }

          if (GpdbUtils.DISP_FIELD_SELECT_ITEM.contains(type.getValue())
            && gpdbItem.getGpdbKubunId() != gpdb_kubun_id.getValue()) {
            msgList.add("既にデータが存在するため選択項目は変更できません。");
          }
        }
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    return msgList.isEmpty();
  }

  /**
   * 項目定義をデータベースから読み出します。
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
      // 項目定義オブジェクトモデルを取得
      EipTGpdbItem gpdbItem = GpdbUtils.getEipTGpdbItem(rundata, context);
      if (gpdbItem == null) {
        return false;
      }

      // 項目名
      gpdb_item_name.setValue(gpdbItem.getGpdbItemName());
      // タイトルフラグ
      title_flg.setValue(gpdbItem.getTitleFlg());
      // 入力形式
      type.setValue(gpdbItem.getType());
      // 区分ID
      gpdb_kubun_id.setValue(gpdbItem.getGpdb().getGpdbId());
      // 一覧表示フラグ
      list_flg.setValue(gpdbItem.getListFlg());
      // 詳細表示フラグ
      detail_flg.setValue(gpdbItem.getDetailFlg());
      // デフォルトソートフラグ
      default_sort_flg.setValue(gpdbItem.getDefaultSortFlg());
      // ソート順
      asc_desc.setValue(gpdbItem.getAscDesc());

      if (GpdbUtils.DISP_FIELD_SIZE_COL.contains(type.getValue())) {
        size_col.setValue(gpdbItem.getSizeCol()); // 表示サイズ（横）
      }

      if (GpdbUtils.DISP_FIELD_SIZE_ROW.contains(type.getValue())) {
        size_row.setValue(gpdbItem.getSizeRow()); // 表示サイズ（縦）
      }

      if (GpdbUtils.DISP_FIELD_SELECT_ITEM.contains(type.getValue())) {
        // 選択式の場合

        // 区分ID
        gpdb_kubun_id.setValue(gpdbItem.getGpdbKubunId());
      }

      if (GpdbUtils.DISP_FIELD_LINE.contains(type.getValue())) {
        // 行数
        line.setValue(gpdbItem.getLine());
      }

      if (GpdbUtils.DISP_FIELD_REQUIRED.contains(type.getValue())) {
        // 必須フラグ
        required_flg.setValue(gpdbItem.getRequiredFlg());
      }

      if (ALEipConstants.MODE_EDIT_FORM.equals(getMode())) {
        // WebデータベースID
        gpdbId = rundata.getParameters().getString("gpdb_id");
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 項目定義をデータベースに格納します。
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

      // 表示順を決定
      SelectQuery<EipTGpdbItem> query = Database.query(EipTGpdbItem.class);
      query.setQualifier(ExpressionFactory.matchExp(
        EipTGpdbItem.GPDB_PROPERTY,
        gpdbId));
      query.orderDesending(EipTGpdbItem.ORDER_NO_PROPERTY);
      EipTGpdbItem maxOrderItem = query.fetchSingle();

      int orderNo = 1;
      if (maxOrderItem != null) {
        orderNo = maxOrderItem.getOrderNo() + 1;
      }

      // -----------------------
      // 項目定義の登録
      // -----------------------

      // 新規オブジェクトモデル
      EipTGpdbItem gpdbItem = Database.create(EipTGpdbItem.class);
      // 表示順
      gpdbItem.setOrderNo(orderNo);
      // 作成者
      gpdbItem.setTurbineUser(tuser);
      // 作成日
      gpdbItem.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      gpdbItem.setUpdateDate(Calendar.getInstance().getTime());

      // 登録／更新値設定
      setInputForm(rundata, gpdbItem, gpdbId);

      // レコードオブジェクトモデルを取得
      List<EipTGpdbRecord> gpdbRecordList =
        GpdbUtils.getEipTGpdbRecordTitle(gpdbId);

      if (gpdbRecordList != null && !gpdbRecordList.isEmpty()) {
        // レコードが存在する場合、追加項目のデータを登録する

        Set<Integer> recordNoSet = new HashSet<Integer>();

        for (EipTGpdbRecord record : gpdbRecordList) {
          Integer no = record.getRecordNo();
          if (recordNoSet.contains(no)) {
            continue;
          }
          recordNoSet.add(no);

          // 新規オブジェクトモデル
          EipTGpdbRecord gpdbRecord = Database.create(EipTGpdbRecord.class);
          // Webデータベースオブジェクト
          gpdbRecord.setGpdb(gpdb);
          // Webデータベース項目定義
          gpdbRecord.setGpdbItem(gpdbItem);
          // レコードNo
          gpdbRecord.setRecordNo(no);
          // 作成者
          gpdbRecord.setTurbineUser(tuser);
          // 更新者
          gpdbRecord.setUpdateUserId(record.getUpdateUserId());
          // 作成日
          gpdbRecord.setCreateDate(Calendar.getInstance().getTime());
          // 更新日
          gpdbRecord.setUpdateDate(Calendar.getInstance().getTime());

          String type = gpdbItem.getType();
          if (GpdbUtils.ITEM_TYPE_SEQ.equals(type)) {
            // 自動採番
            gpdbRecord.setValue("" + no);

          } else if (GpdbUtils.ITEM_TYPE_CREATE_USER.equals(type)) {
            // 登録者
            gpdbRecord.setValue("" + record.getTurbineUser().getUserId());

          } else if (GpdbUtils.ITEM_TYPE_UPDATE_USER.equals(type)) {
            // 更新者
            gpdbRecord.setValue("" + record.getUpdateUserId());

          } else if (GpdbUtils.ITEM_TYPE_CREATE_DATE.equals(type)) {
            // 作成日
            ALDateTimeField t = new ALDateTimeField();
            t.setValue(record.getCreateDate());
            gpdbRecord.setValue(t.toStringDateTime());

          } else if (GpdbUtils.ITEM_TYPE_UPDATE_DATE.equals(type)) {
            // 更新日時
            ALDateTimeField t = new ALDateTimeField();
            t.setValue(record.getUpdateDate());
            gpdbRecord.setValue(t.toStringDateTime());
          }
        }
      }

      // 項目定義を登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        gpdbItem.getGpdbItemId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 「"
          + gpdb.getGpdbName()
          + "」 項目定義 「"
          + gpdbItem.getGpdbItemName()
          + "」 追加");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 項目定義を更新します。
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
      EipTGpdbItem gpdbItem = GpdbUtils.getEipTGpdbItem(rundata, context);
      if (gpdbItem == null) {
        return false;
      }

      // 更新日
      gpdbItem.setUpdateDate(Calendar.getInstance().getTime());

      // 登録／更新値設定
      setInputForm(rundata, gpdbItem, gpdbId);

      // Webデータベースを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        gpdbItem.getGpdbItemId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 「"
          + gpdb.getGpdbName()
          + "」 項目定義 「"
          + gpdbItem.getGpdbItemName()
          + "」 更新");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 登録／更新値を設定する
   * 
   * @param rundata
   *          RunData
   * @param gpdbItem
   *          項目定義オブジェクト
   * @param gpdbId
   *          WebデータベースID
   */
  private void setInputForm(RunData rundata, EipTGpdbItem gpdbItem,
      String gpdbId) {

    if (GpdbUtils.FLG_ON.equals(title_flg.getValue())) {
      // タイトル項目にした場合、現在タイトルのものをOFFにする
      GpdbUtils.gpdbItemTitleOff(gpdb);
    }

    // Webデータベース
    gpdbItem.setGpdb(gpdb);

    if (GpdbUtils.FLG_ON.equals(title_flg.getValue())) {
      // タイトル項目にした場合、現在タイトルのものをOFFにする
      GpdbUtils.gpdbItemTitleOff(gpdb);
    }

    if (GpdbUtils.FLG_ON.equals(default_sort_flg.getValue())) {
      // デフォルトソート項目にした場合、現在デフォルトソートのものをOFFにする
      GpdbUtils.gpdbItemDefaultSortOff(gpdb);
    }

    // 項目定義名
    gpdbItem.setGpdbItemName(gpdb_item_name.getValue());
    // 項目形式
    gpdbItem.setType(type.getValue());
    // タイトルフラグ
    gpdbItem.setTitleFlg(title_flg.getValue());
    // 一覧画面表示フラグ
    gpdbItem.setListFlg(list_flg.getValue());
    // 詳細画面表示フラグ
    gpdbItem.setDetailFlg(detail_flg.getValue());
    // デフォルトソートフラグ
    gpdbItem.setDefaultSortFlg(default_sort_flg.getValue());

    // 更新値
    Integer sizeCol = null; // 表示サイズ（横）
    Integer sizeRow = null; // 表示サイズ（縦）
    Integer line = null; // 表示行数
    Integer kubunId = null; // 区分マスタID
    String requiredFlg = GpdbUtils.FLG_OFF; // 必須フラグ
    String ascDesc = asc_desc.getValue(); // ソート順

    if (GpdbUtils.DISP_FIELD_SIZE_COL.contains(type.getValue())) {
      sizeCol = (int) size_col.getValue(); // 表示サイズ（横）
    }

    if (GpdbUtils.DISP_FIELD_SIZE_ROW.contains(type.getValue())) {
      sizeRow = (int) size_row.getValue(); // 表示サイズ（縦）
    }

    if (GpdbUtils.DISP_FIELD_SELECT_ITEM.contains(type.getValue())) {
      // 選択式の場合

      // 区分
      kubunId = (int) gpdb_kubun_id.getValue();
    }

    if (GpdbUtils.DISP_FIELD_LINE.contains(type.getValue())) {
      line = (int) this.line.getValue();
    }
    if (GpdbUtils.DISP_FIELD_REQUIRED.contains(type.getValue())) {
      requiredFlg = required_flg.getValue();
    }

    if (GpdbUtils.FLG_OFF.equals(default_sort_flg.getValue())) {
      ascDesc = "";
    }

    gpdbItem.setSizeCol(sizeCol); // 表示サイズ（横）
    gpdbItem.setSizeRow(sizeRow); // 表示サイズ（縦）
    gpdbItem.setLine(line); // 表示行数
    gpdbItem.setGpdbKubunId(kubunId); // 区分ID
    gpdbItem.setRequiredFlg(requiredFlg); // 必須フラグ
    gpdbItem.setAscDesc(ascDesc); // 昇順降順
  }

  /**
   * 項目定義をデータベースから削除します。
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
      EipTGpdbItem gpdbItem = GpdbUtils.getEipTGpdbItem(rundata, context);
      if (gpdbItem == null) {
        return false;
      }

      if (GpdbUtils.FLG_ON.equals(gpdbItem.getTitleFlg())) {
        msgList.add(GpdbUtils.ERRMSG_TITLE_ITEM_DELETE);
        return false;
      }

      // レコード情報を削除
      GpdbUtils.removeGpdbRecord(rundata, gpdbItem);

      // 項目定義を削除
      Database.delete(gpdbItem);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        gpdbItem.getGpdbItemId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 「"
          + gpdb.getGpdbName()
          + "」 項目定義 「"
          + gpdbItem.getGpdbItemName()
          + "」 削除");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 項目名を取得します。
   * 
   * @return 項目名
   */
  public ALStringField getGpdbItemName() {
    return gpdb_item_name;
  }

  /**
   * タイトルフラグを取得します。
   * 
   * @return タイトルフラグ
   */
  public ALStringField getTitleFlg() {
    return title_flg;
  }

  /**
   * 必須フラグを取得します。
   * 
   * @return 必須フラグ
   */
  public ALStringField getRequiredFlg() {
    return required_flg;
  }

  /**
   * 入力形式を取得します。
   * 
   * @return 入力形式
   */
  public ALStringField getType() {
    return type;
  }

  /**
   * 区分IDを取得します。
   * 
   * @return 区分ID
   */
  public ALNumberField getGpdbKubunId() {
    return gpdb_kubun_id;
  }

  /**
   * 一覧画面表示フラグを取得します。
   * 
   * @return 一覧画面表示フラグ
   */
  public ALStringField getListFlg() {
    return list_flg;
  }

  /**
   * 詳細画面表示フラグを取得します。
   * 
   * @return 詳細画面表示フラグ
   */
  public ALStringField getDetailFlg() {
    return detail_flg;
  }

  /**
   * 表示サイズ（横）を取得します。
   * 
   * @return 表示サイズ（横）
   */
  public ALNumberField getSizeCol() {
    return size_col;
  }

  /**
   * 表示サイズ（縦）を取得します。
   * 
   * @return 表示サイズ（縦）
   */
  public ALNumberField getSizeRow() {
    return size_row;
  }

  /**
   * 表示行数を取得します。
   * 
   * @return 表示行数
   */
  public ALNumberField getLine() {
    return line;
  }

  /**
   * デフォルトソートフラグを取得します。
   * 
   * @return デフォルトソートフラグ
   */
  public ALStringField getDefaultSortFlg() {
    return default_sort_flg;
  }

  /**
   * ソート順を取得します。
   * 
   * @return ソート順
   */
  public ALStringField getAscDesc() {
    return asc_desc;
  }

  /**
   * 全入力形式マップを取得します。
   * 
   * @return 全入力形式マップ
   */
  public Map<String, String> getGpdbTypeMap() {
    return GpdbUtils.ITEM_TYPE;
  }

  /**
   * 全区分のリストを返す
   * 
   * @return 全区分のリスト
   */
  public List<GpdbKubunResultData> getAllGpdbKubun() {
    return allGpdbKubun;
  }

  /**
   * WebデータベースIDを返す
   * 
   * @return WebデータベースID
   */
  public String getGpdbId() {
    return gpdbId;
  }

  /**
   * 項目定義の入力項目が出力対象かを判定する
   * 
   * @param field
   *          入力項目フィールド名
   * @param type
   *          入力形式
   * @return 出力する：TRUE 出力しない：FALSE
   */
  public boolean displayField(String field, String type) {
    return GpdbUtils.dipslayField(field, type);
  }

  /**
   * Webデータベースを取得します。
   * 
   * @return Webデータベース
   */
  public EipTGpdb getGpdb() {
    return gpdb;
  }

}
