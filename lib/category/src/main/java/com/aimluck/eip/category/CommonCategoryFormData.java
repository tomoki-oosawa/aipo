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
package com.aimluck.eip.category;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有カテゴリのフォームデータを管理するクラスです。 <br />
 * 
 */
public class CommonCategoryFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CommonCategoryFormData.class.getName());

  /** カテゴリID */
  private Integer category_id;

  /** カテゴリ名 */
  private ALStringField name;

  /** メモ */
  private ALStringField note;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    try {
      // カテゴリID
      category_id =
        Integer.valueOf(ALEipUtils.getTemp(
          rundata,
          context,
          ALEipConstants.ENTITY_ID));
    } catch (Exception e) {
      category_id = null;
    }
  }

  /**
   *
   */
  @Override
  public void initField() {
    // 共有カテゴリ名
    name = new ALStringField();
    name.setFieldName("共有カテゴリ名");
    name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("メモ");
    note.setTrim(true);
  }

  /**
   * ToDoカテゴリの各フィールドに対する制約条件を設定します。 <BR>
   * 
   */
  @Override
  protected void setValidator() {
    // カテゴリ名必須項目
    name.setNotNull(true);
    // カテゴリ名文字数制限
    name.limitMaxLength(50);
    // メモ文字数制限
    note.limitMaxLength(1000);
  }

  /**
   * 共有カテゴリのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery<EipTCommonCategory> query =
        Database.query(EipTCommonCategory.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTCommonCategory.NAME_PROPERTY, name
          .getValue());
      query.setQualifier(exp);

      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp3 =
          ExpressionFactory.noMatchDbExp(
            EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN,
            category_id);
        query.andQualifier(exp3);
      }

      if (query.fetchList().size() != 0) {
        msgList.add("共有カテゴリ名『 <span class='em'>"
          + name.toString()
          + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("CommonCategoryFormData.validate", ex);
      return false;
    }

    // 共有カテゴリ名
    name.validate(msgList);
    // メモ
    note.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * 共有カテゴリをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTCommonCategory category =
        CommonCategoryUtils.getEipTCommonCategory(rundata, context);
      if (category == null) {
        return false;
      }

      // カテゴリ名
      name.setValue(category.getName());
      // メモ
      note.setValue(category.getNote());
    } catch (Exception ex) {
      logger.error("CommonCategoryFormData.loadFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 共有カテゴリをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      EipTCommonCategory category = Database.create(EipTCommonCategory.class);

      category.setName(name.getValue());
      category.setNote(note.getValue());
      category.setCreateUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      category.setUpdateUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      category.setCreateDate(Calendar.getInstance().getTime());
      category.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCommonCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_COMMON_CATEGORY,
        name.getValue());
    } catch (Throwable t) {
      Database.rollback();
      logger.error("CommonCategoryFormData.insertFormData", t);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されている共有カテゴリを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTCommonCategory category =
        CommonCategoryUtils.getEipTCommonCategory(rundata, context);
      if (category == null) {
        return false;
      }

      boolean authority_edit =
        CommonCategoryUtils
          .checkPermission(
            rundata,
            context,
            ALAccessControlConstants.VALUE_ACL_UPDATE,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER);

      // 他人が作成したカテゴリは権限がないと変更不可
      ALEipUser eipUser = ALEipUtils.getALEipUser(rundata);
      if (category.getCreateUserId().intValue() != eipUser
        .getUserId()
        .getValue()
        && !authority_edit) {
        return false;
      }

      // カテゴリ名
      category.setName(name.getValue());
      // メモ
      category.setNote(note.getValue());
      // ユーザーID
      category.setUpdateUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // 更新日
      category.setUpdateDate(Calendar.getInstance().getTime());

      // 共有カテゴリを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCommonCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_COMMON_CATEGORY,
        name.getValue());
    } catch (Throwable t) {
      Database.rollback();
      logger.error("CommonCategoryFormData.updateFormData", t);
      return false;
    }
    return true;
  }

  /**
   * 共有カテゴリを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTCommonCategory category =
        CommonCategoryUtils.getEipTCommonCategory(rundata, context);
      if (category == null) {
        return false;
      }

      boolean authority_delete =
        CommonCategoryUtils
          .checkPermission(
            rundata,
            context,
            ALAccessControlConstants.VALUE_ACL_DELETE,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER);

      // 他人が作成したカテゴリは権限がないと消せない
      ALEipUser eipUser = ALEipUtils.getALEipUser(rundata);
      if (category.getCreateUserId().intValue() != eipUser
        .getUserId()
        .getValue()
        && !authority_delete) {
        return false;
      }

      // 共有カテゴリ内の SchaduleMap は「その他」にカテゴリ変更する
      CommonCategoryUtils.setDefaultCommonCategoryToSchedule(category);

      // entityIdを取得
      int entityId = category.getCommonCategoryId();
      // カテゴリ名を取得
      String categoryName = category.getName();

      // 共有カテゴリを削除
      Database.delete(category);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_COMMON_CATEGORY,
        categoryName);
    } catch (Throwable t) {
      Database.rollback();
      logger.error("CommonCategoryFormData.deleteFormData", t);
      return false;
    }
    return true;
  }

  /**
   * 共有カテゴリIDを取得します。 <BR>
   * 
   * @return
   */
  public Integer getCategoryId() {
    return category_id;
  }

  /**
   * 共有カテゴリ名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY;
  }
}
