/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.wiki;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * Wikiカテゴリのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class WikiCategoryFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiCategoryFormData.class.getName());

  /** カテゴリ名 */
  private ALStringField category_name;

  private Integer category_id;

  /** ログインユーザーのID * */
  private int user_id;

  private String aclPortletFeature;

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

    String categoryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (categoryid != null && Integer.valueOf(categoryid) != null) {
      category_id = Integer.valueOf(categoryid);
    }

    user_id = ALEipUtils.getUserId(rundata);
  }

  /**
   *
   *
   */
  @Override
  public void initField() {
    // カテゴリ名
    category_name = new ALStringField();
    category_name.setFieldName("カテゴリ名");
    category_name.setTrim(true);
  }

  /**
   * Wikiカテゴリの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // カテゴリ名必須項目
    category_name.setNotNull(true);
    // カテゴリ名文字数制限
    category_name.limitMaxLength(50);
  }

  /**
   * Wikiカテゴリのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery<EipTWikiCategory> query =
        Database.query(EipTWikiCategory.class);

      Expression exp =
        ExpressionFactory.matchExp(
          EipTWikiCategory.CATEGORY_NAME_PROPERTY,
          category_name.getValue());

      Expression exp2 =
        ExpressionFactory.matchExp(EipTWikiCategory.USER_ID_PROPERTY, Integer
          .valueOf(0));

      Expression exp3;
      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp4 =
          ExpressionFactory.noMatchDbExp(
            EipTWikiCategory.CATEGORY_ID_PK_COLUMN,
            category_id);
        EipTWikiCategory category = WikiUtils.getEipTWikiCategory(category_id);
        exp3 =
          ExpressionFactory.matchExp(
            EipTWikiCategory.USER_ID_PROPERTY,
            category.getUserId());
        // exp AND (exp4 AND (exp3 OR exp2))
        query.setQualifier(exp4.andExp(exp.andExp(exp3.orExp(exp2))));
      } else {
        exp3 =
          ExpressionFactory.matchExp(EipTWikiCategory.USER_ID_PROPERTY, Integer
            .valueOf(this.user_id));
        // exp1 AND ( exp2 OR exp3 )
        query.setQualifier(exp.andExp(exp2.orExp(exp3)));
      }

      if (query.fetchList().size() != 0) {
        msgList.add("カテゴリ名『 <span class='em'>"
          + category_name.toString()
          + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("wiki", ex);
      return false;
    }

    // カテゴリ名
    category_name.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * Wikiカテゴリをデータベースから読み出します。 <BR>
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
      EipTWikiCategory category =
        WikiUtils.getEipTWikiCategory(rundata, context);
      if (category == null) {
        return false;
      }
      // カテゴリ名
      category_name.setValue(category.getCategoryName());
    } catch (Exception e) {
      logger.error("WikiCategoryFormData.loadFormData", e);
      return false;
    }
    return true;
  }

  /**
   * Wikiカテゴリをデータベースに格納します。 <BR>
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
      EipTWikiCategory category = Database.create(EipTWikiCategory.class);
      category.setCategoryName(category_name.getValue());
      category.setTurbineUser(ALEipUtils.getTurbineUser(ALEipUtils
        .getUserId(rundata)));
      category.setUpdateUserId(ALEipUtils.getUserId(rundata));
      category.setCreateDate(Calendar.getInstance().getTime());
      category.setUpdateDate(Calendar.getInstance().getTime());
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_WIKI_CATEGORY,
        category_name.getValue());

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WikiCategoryFormData]", t);
      return false;
    }

    return true;
  }

  /**
   * データベースに格納されているWikiカテゴリを更新します。 <BR>
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
      EipTWikiCategory category =
        WikiUtils.getEipTWikiCategory(rundata, context);
      if (category == null) {
        return false;
      }
      // カテゴリ名
      category.setCategoryName(category_name.getValue());
      // 更新ユーザーID
      category.setUpdateUserId(ALEipUtils.getUserId(rundata));
      // 更新日
      category.setUpdateDate(Calendar.getInstance().getTime());
      // Wikiカテゴリを更新
      Database.commit();
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_WIKI_CATEGORY,
        category_name.getValue());

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WikiCategoryFormData]", t);
      return false;
    }
    return true;
  }

  /**
   * Wikiカテゴリを削除します。 <BR>
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
      EipTWikiCategory category =
        WikiUtils.getEipTWikiCategory(rundata, context);
      if (category == null) {
        return false;
      }

      if (category.getEipTWiki().size() > 0) {
        msgList.add("1つ以上のWikiを含むカテゴリを削除することはできません。");
        return false;
      }

      // entityIdを取得
      Integer entityId = category.getCategoryId();
      // カテゴリ名を取得
      String categoryName = category.getCategoryName();

      Database.delete(category);
      Database.commit();

      // ログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_WIKI_CATEGORY,
        categoryName);

      // 一覧表示画面のフィルタに設定されているカテゴリのセッション情報を削除
      String filtername =
        WikiSelectData.class.getName() + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WikiCategoryFormData]", t);
      return false;
    }
    return true;
  }

  /**
   * カテゴリ名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCategoryName() {
    return category_name;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}
