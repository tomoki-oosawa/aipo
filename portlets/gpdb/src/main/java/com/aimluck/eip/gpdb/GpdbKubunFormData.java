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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubun;
import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubunValue;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
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
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class GpdbKubunFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbKubunFormData.class.getName());

  /** 区分ID */
  private ALStringField gpdb_kubun_id;

  /** 区分名 */
  private ALStringField gpdb_kubun_name;

  /** 区分値 */
  private ALStringField gpdb_kubun_value;

  /** 区分新規入力フラグ */
  private boolean is_new_kubun;

  /** 区分値ID */
  private String gpdbKubunValueId;

  /** 区分データ（登録時に使用） */
  private EipMGpdbKubun gpdbKubun;

  /** セレクトボックス用区分リスト */
  private List<EipMGpdbKubun> gpdb_kubun_list;

  /** 検索フィルタ名 */
  static final String LIST_FILTER_STR = new StringBuffer().append(
    GpdbKubunSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_FILTER).toString();

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
    is_new_kubun = rundata.getParameters().getBoolean("is_new_kubun");

    gpdb_kubun_list = new ArrayList<EipMGpdbKubun>();

    gpdbKubunValueId =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
  }

  /**
   * 各フィールドを初期化します。
   */
  @Override
  public void initField() {
    // 区分ID
    gpdb_kubun_id = new ALStringField();
    gpdb_kubun_id.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_SELECTION_ITEMS"));
    // 区分名
    gpdb_kubun_name = new ALStringField();
    gpdb_kubun_name.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_SELECTION_ITEMS"));
    gpdb_kubun_name.setTrim(true);
    // 区分値
    gpdb_kubun_value = new ALStringField();
    gpdb_kubun_value.setFieldName(ALLocalizationUtils.getl10n("GPDB_VALUE"));
    gpdb_kubun_value.setTrim(true);
  }

  /**
   * 区分値の各フィールドに対する制約条件を設定します。
   */
  @Override
  protected void setValidator() {
    // 区分値必須項目
    gpdb_kubun_value.setNotNull(true);
    // 区分値の文字数制限
    gpdb_kubun_value.limitMaxLength(50);
    // 区分文字数制限
    gpdb_kubun_name.limitMaxLength(50);
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
        if (ALEipConstants.MODE_NEW_FORM.equals(getMode())) {
          gpdb_kubun_id.setValue(ALEipUtils.getTemp(
            rundata,
            context,
            LIST_FILTER_STR));
        }
        SelectQuery<EipMGpdbKubun> fquery = Database.query(EipMGpdbKubun.class);
        fquery.orderAscending(EipMGpdbKubun.GPDB_KUBUN_NAME_PROPERTY);
        gpdb_kubun_list = fquery.fetchList();
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * 区分値のフォームに入力されたデータの妥当性検証を行います。
   *
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {

      EipMGpdbKubunValue gpdbKubunValue =
        GpdbUtils.getEipMGpdbKubunValue(gpdbKubunValueId);

      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        // 更新

        int recordCount = GpdbUtils.getRecordCountKubunUsed(gpdbKubunValueId);
        if (recordCount > 0) {
          // Webデータベースレコードが登録されている場合

          if (is_new_kubun
            || !gpdbKubunValue.getGpdbKubunId().toString().equals(
              gpdb_kubun_id.getValue())) {
            // 別の区分が入力された場合
            msgList.add("登録されている値のため選択項目は変更できません。");
            return false;
          }
        }

        if (existsItemAndLastKubun(gpdbKubunValue.getGpdbKubunId())) {
          // 項目定義されている区分でかつ区分値が1件のみの場合
          msgList.add("項目定義されている選択項目のため、別の選択項目に変更できません。");
          return false;
        }
      }

      if (is_new_kubun) {
        // 区分入力

        // 区分名必須
        gpdb_kubun_name.setNotNull(true);
        if (gpdb_kubun_name.validate(msgList)) {
          // 入力ありの場合

          // 区分重複チェック
          SelectQuery<EipMGpdbKubun> query =
            Database.query(EipMGpdbKubun.class);
          query.setQualifier(ExpressionFactory.matchExp(
            EipMGpdbKubun.GPDB_KUBUN_NAME_PROPERTY,
            gpdb_kubun_name.getValue()));

          if (!query.fetchList().isEmpty()) {
            msgList.add("選択項目『 <span class='em'>"
              + gpdb_kubun_name.toString()
              + "</span> 』は既に登録されています。");
          }
        }

      } else {
        // 区分選択

        // 区分選択必須
        gpdb_kubun_id.setNotNull(true);
        if (gpdb_kubun_id.validate(msgList)) {
          // 選択値ありの場合

          // 区分値重複チェック
          SelectQuery<EipMGpdbKubunValue> query =
            Database.query(EipMGpdbKubunValue.class);

          query.setQualifier(ExpressionFactory.matchExp(
            EipMGpdbKubunValue.GPDB_KUBUN_VALUE_PROPERTY,
            gpdb_kubun_value.getValue()));

          query.andQualifier(ExpressionFactory.matchExp(
            EipMGpdbKubunValue.GPDB_KUBUN_PROPERTY,
            gpdb_kubun_id.getValue()));

          if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
            query.andQualifier(ExpressionFactory.noMatchDbExp(
              EipMGpdbKubunValue.GPDB_KUBUN_VALUE_ID_PK_COLUMN,
              Integer.valueOf(gpdbKubunValueId)));
          }

          if (!query.fetchList().isEmpty()) {
            msgList.add("値『 <span class='em'>"
              + gpdb_kubun_value.toString()
              + "</span> 』は既に登録されています。");
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    // 区分値
    gpdb_kubun_value.validate(msgList);

    return msgList.isEmpty();

  }

  /**
   * 区分値をデータベースから読み出します。
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
      EipMGpdbKubunValue value =
        GpdbUtils.getEipMGpdbKubunValue(rundata, context);
      if (value == null) {
        return false;
      }
      // 区分ID
      gpdb_kubun_id.setValue(value.getGpdbKubun().getGpdbKubunId().toString());
      // 区分値
      gpdb_kubun_value.setValue(value.getGpdbKubunValue());

      SelectQuery<EipMGpdbKubun> fquery = Database.query(EipMGpdbKubun.class);
      fquery.orderAscending(EipMGpdbKubun.GPDB_KUBUN_NAME_PROPERTY);
      gpdb_kubun_list = fquery.fetchList();

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 区分値をデータベースに格納します。
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

      int orderNo = 1;
      if (is_new_kubun) {
        // 選択項目の登録処理
        if (!insertKubunData(msgList)) {
          return false;
        }
      } else {
        gpdbKubun =
          GpdbUtils.getEipMGpdbKubun(Integer.valueOf(gpdb_kubun_id.getValue()));

        // 表示順を決定
        SelectQuery<EipMGpdbKubunValue> query =
          Database.query(EipMGpdbKubunValue.class);
        query.setQualifier(ExpressionFactory.matchExp(
          EipMGpdbKubunValue.GPDB_KUBUN_PROPERTY,
          gpdbKubun));
        query.orderDesending(EipTGpdbItem.ORDER_NO_PROPERTY);
        EipMGpdbKubunValue maxOrderKubunValue = query.fetchSingle();
        orderNo = maxOrderKubunValue.getOrderNo() + 1;
      }

      // 新規オブジェクトモデル
      EipMGpdbKubunValue value = Database.create(EipMGpdbKubunValue.class);
      // 区分マスタ
      value.setGpdbKubun(gpdbKubun);
      // 区分値
      value.setGpdbKubunValue(gpdb_kubun_value.getValue());
      // 表示順
      value.setOrderNo(orderNo);
      // 作成日
      value.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      value.setUpdateDate(Calendar.getInstance().getTime());

      // 区分を登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        value.getGpdbKubunValueId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 選択項目「"
          + value.getGpdbKubun().getGpdbKubunName()
          + "」 値 「"
          + value.getGpdbKubunValue()
          + "」 追加");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 区分マスタをデータベースに格納します。
   *
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  private boolean insertKubunData(List<String> msgList) {
    try {

      // 新規オブジェクトモデル
      gpdbKubun = Database.create(EipMGpdbKubun.class);

      // 区分名
      gpdbKubun.setGpdbKubunName(gpdb_kubun_name.getValue());
      // 作成日
      gpdbKubun.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      gpdbKubun.setUpdateDate(Calendar.getInstance().getTime());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      msgList.add("エラーが発生しました");
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されている区分を更新します。
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
      EipMGpdbKubunValue value =
        GpdbUtils.getEipMGpdbKubunValue(rundata, context);
      if (value == null) {
        return false;
      }

      if (is_new_kubun) {
        // 区分の登録処理
        if (!insertKubunData(msgList)) {
          return false;
        }
        // 表示順
        value.setOrderNo(1);

      } else {
        gpdbKubun =
          GpdbUtils.getEipMGpdbKubun(Integer.valueOf(gpdb_kubun_id.getValue()));
      }

      // 区分マスタ
      value.setGpdbKubun(gpdbKubun);
      // 区分値
      value.setGpdbKubunValue(gpdb_kubun_value.getValue());
      // 更新日
      value.setUpdateDate(Calendar.getInstance().getTime());

      // 区分値を更新
      Database.commit();

      // 区分値を持たない区分を削除
      if (GpdbUtils.removeGpdbKubunNoValue()) {
        Database.commit();
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        value.getGpdbKubunValueId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 選択項目「"
          + value.getGpdbKubun().getGpdbKubunName()
          + "」 値 「"
          + value.getGpdbKubunValue()
          + "」 更新");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 区分値をデータベースから削除します。
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

      // 区分値オブジェクトモデルを取得
      EipMGpdbKubunValue value =
        GpdbUtils.getEipMGpdbKubunValue(rundata, context);
      if (value == null) {
        return false;
      }

      int kubunCount =
        GpdbUtils.getRecordCountKubunUsed("" + value.getGpdbKubunValueId());
      if (kubunCount > 0) {
        // Webデータベースレコードが登録されている場合
        msgList.add("使用されている値のため削除できません。");
        return false;

      } else {

        if (existsItemAndLastKubun(value.getGpdbKubunId())) {
          // 項目定義されている区分でかつ区分値が1件のみの場合
          msgList.add("項目定義されている選択項目のため、全ての値は削除できません。");
          return false;
        }
      }

      // 区分マスタ
      gpdbKubun = GpdbUtils.getEipMGpdbKubun(value.getGpdbKubunId());

      // 区分値を削除
      Database.delete(value);
      Database.commit();

      // 区分値を持たない区分を削除
      if (GpdbUtils.removeGpdbKubunNoValue()) {
        Database.commit();
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        value.getGpdbKubunValueId(),
        ALEventlogConstants.PORTLET_TYPE_GPDB,
        "Webデータベース 選択項目「"
          + gpdbKubun.getGpdbKubunName()
          + "」 値 「"
          + value.getGpdbKubunValue()
          + "」 削除");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 項目定義されている区分が1つかをチェックする
   *
   * @param kubunId
   *          区分ID
   * @return 項目定義されている区分でかつ1件のみ区分値がある場合：TRUE
   */
  private boolean existsItemAndLastKubun(int kubunId) {
    SelectQuery<EipTGpdbItem> query = Database.query(EipTGpdbItem.class);
    query.setQualifier(ExpressionFactory.matchExp(
      EipTGpdbItem.GPDB_KUBUN_ID_PROPERTY,
      kubunId));

    if (query.fetchList().isEmpty()) {
      return false;
    }

    // 区分値リストを取得
    List<GpdbKubunValueResultData> valList =
      GpdbUtils.getGpdbKubunValueList("" + kubunId);
    return (valList.size() <= 1);
  }

  /**
   * 区分マスタIDを取得します。
   *
   * @return　区分マスタID
   */
  public ALStringField getGpdbKubunId() {
    return gpdb_kubun_id;
  }

  /**
   * 区分マスタ名を取得します。
   *
   * @return　区分マスタ名を
   */
  public ALStringField getGpdbKubunName() {
    return gpdb_kubun_name;
  }

  /**
   * 区分マスタ名を取得します。(Wbr挿入)
   *
   * @return　区分マスタ名を
   */
  public String getWbrGpdbKubunName() {
    return ALCommonUtils.replaceToAutoCRString(gpdb_kubun_name.toString());
  }

  /**
   * 区分値を取得します。
   *
   * @return　区分値
   */
  public ALStringField getGpdbKubunValue() {
    return gpdb_kubun_value;
  }

  /**
   * 区分値を取得します。(Wbr挿入)
   *
   * @return　区分値
   */
  public String getWbrGpdbKubunValue() {
    return ALCommonUtils.replaceToAutoCRString(gpdb_kubun_value.toString());
  }

  /**
   * 区分マスタ新規入力フラグを取得します。
   *
   * @return　新規入力フラグ
   */
  public boolean isNewKubun() {
    return is_new_kubun;
  }

  /**
   * セレクトボックス用区分マスタリストを取得します。
   *
   * @return セレクトボックス用区分マスタリスト
   */
  public List<EipMGpdbKubun> getGpdbKubunList() {
    return gpdb_kubun_list;
  }

}
