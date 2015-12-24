/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.blog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ブログテーマのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class BlogThemaFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogThemaFormData.class.getName());

  /** カテゴリ名 */
  private ALStringField thema_name;

  /** メモ */
  private ALStringField description;

  private int thema_id;

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
  }

  /**
   *
   *
   */
  @Override
  public void initField() {

    // カテゴリ名
    thema_name = new ALStringField();
    thema_name.setFieldName(ALLocalizationUtils.getl10n("BLOG_THEME_NAME"));
    thema_name.setTrim(true);
    // メモ
    description = new ALStringField();
    description.setFieldName(ALLocalizationUtils.getl10n("BLOG_MEMO"));
    description.setTrim(true);
  }

  /**
   * ブログカテゴリの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // カテゴリ名必須項目
    thema_name.setNotNull(true);
    // カテゴリ名文字数制限
    thema_name.limitMaxLength(50);
    // メモ文字数制限
    description.limitMaxLength(1000);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        thema_id =
          Integer.parseInt(ALEipUtils.getTemp(
            rundata,
            context,
            ALEipConstants.ENTITY_ID));
      }
    }
    return res;
  }

  /**
   * 
   * /** ブログカテゴリのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {

    try {
      SelectQuery<EipTBlogThema> query = Database.query(EipTBlogThema.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchExp(
            EipTBlogThema.THEMA_NAME_PROPERTY,
            thema_name.getValue());
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipTBlogThema.THEMA_NAME_PROPERTY,
            thema_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipTBlogThema.THEMA_ID_PK_COLUMN,
            Integer.valueOf(thema_id));
        query.andQualifier(exp2);
      }

      if (query.fetchList().size() != 0) {
        msgList.add("テーマ名『 <span class='em'>"
          + thema_name
          + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("blog", ex);
      return false;
    }

    // テーマ名
    thema_name.validate(msgList);
    // メモ
    description.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * ブログカテゴリをデータベースから読み出します。 <BR>
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
      EipTBlogThema thema = BlogUtils.getEipTBlogThema(rundata, context);
      if (thema == null) {
        return false;
      }
      // カテゴリ名
      thema_name.setValue(thema.getThemaName());
      // メモ
      description.setValue(thema.getDescription());
    } catch (Exception ex) {
      logger.error("blog", ex);
      return false;
    }
    return true;
  }

  /**
   * ブログカテゴリをデータベースに格納します。 <BR>
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

      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_INSERT);

      int uid = ALEipUtils.getUserId(rundata);
      // 新規オブジェクトモデル
      EipTBlogThema thema = Database.create(EipTBlogThema.class);
      // カテゴリ名
      thema.setThemaName(thema_name.getValue());
      // メモ
      thema.setDescription(description.getValue());
      // 作成ユーザーID
      thema.setCreateUserId(Integer.valueOf(uid));
      // 更新ユーザーID
      thema.setUpdateUserId(Integer.valueOf(uid));
      // 作成日
      thema.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      thema.setUpdateDate(Calendar.getInstance().getTime());
      // ブログカテゴリを登録
      Database.commit();

      thema_id = thema.getThemaId().intValue();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        thema_id,
        ALEventlogConstants.PORTLET_TYPE_BLOG_THEMA,
        thema_name.getValue());
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("blog", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているToDoカテゴリを更新します。 <BR>
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
      EipTBlogThema thema = BlogUtils.getEipTBlogThema(rundata, context);
      if (thema == null) {
        return false;
      }
      // カテゴリ名
      thema.setThemaName(thema_name.getValue());
      // メモ
      thema.setDescription(description.getValue());
      // 更新ユーザーID
      thema.setUpdateUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // 更新日
      thema.setUpdateDate(Calendar.getInstance().getTime());

      // ブログカテゴリを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        thema.getThemaId().intValue(),
        ALEventlogConstants.PORTLET_TYPE_BLOG_THEMA,
        thema_name.getValue());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("blog", ex);
      return false;
    }
    return true;
  }

  /**
   * ブログカテゴリを削除します。 <BR>
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
      EipTBlogThema thema = BlogUtils.getEipTBlogThema(rundata, context);
      if (thema == null || thema.getCreateUserId() == 0) {
        // テーマ「その他」は削除不可
        msgList.add("このテーマは削除できません。");
        return false;
      }
      // 記事を「その他」に移す
      List<String> values = new ArrayList<String>();
      values.add(thema.getThemaId().toString());
      SelectQuery<EipTBlogEntry> reqquery = Database.query(EipTBlogEntry.class);
      Expression reqexp1 =
        ExpressionFactory.inDbExp(EipTBlogEntry.EIP_TBLOG_THEMA_PROPERTY
          + "."
          + EipTBlogThema.THEMA_ID_PK_COLUMN, values);
      reqquery.setQualifier(reqexp1);
      List<EipTBlogEntry> requests = reqquery.fetchList();
      if (requests != null && requests.size() > 0) {
        EipTBlogEntry request = null;
        EipTBlogThema defaultThema =
          BlogUtils.getEipTBlogThema(Long.valueOf(1));
        int size = requests.size();
        for (int i = 0; i < size; i++) {
          request = requests.get(i);
          request.setEipTBlogThema(defaultThema);
        }
      }

      // entityIdを取得
      Integer entityId = thema.getThemaId();
      // カテゴリ名を取得
      // String themaName = thema.getThemaName();

      // ブログカテゴリを削除
      Database.delete(thema);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_BLOG_THEMA,
        thema_name.getValue());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("blog", ex);
      return false;
    }
    return true;
  }

  public int getThemaId() {
    return thema_id;
  }

  /**
   * テーマ名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getThemaName() {
    return thema_name;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getDescription() {
    return description;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_THEME;
  }
}
