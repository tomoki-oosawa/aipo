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

import static com.aimluck.eip.util.ALLocalizationUtils.*;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * Wikiのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class WikiFormData extends ALAbstractFormData {

  /** wiki名 */
  private ALStringField name;

  /** カテゴリ ID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** メモ */
  private ALStringField note;

  /** */
  private boolean is_new_category;

  private EipTWikiCategory category;

  /** カテゴリ一覧 */
  private List<WikiCategoryResultData> categoryList;

  private int uid;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiFormData.class.getName());

  /** ACL用の変数 * */
  private final String aclPortletFeature = "";

  private String entityId = null;

  private String mode = null;

  /**
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    is_new_category = rundata.getParameters().getBoolean("is_new_category");
    uid = ALEipUtils.getUserId(rundata);
    entityId = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    mode = rundata.getParameters().getString(ALEipConstants.MODE, "");
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata, Context context) {
    categoryList = WikiUtils.loadCategoryList(rundata);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   */
  @Override
  public void initField() {
    name = new ALStringField();
    name.setFieldName(getl10n("WIKI_TITLE"));
    name.setTrim(true);

    // カテゴリID
    category_id = new ALNumberField();
    category_id.setFieldName(getl10n("WIKI_CATEGORY"));
    // カテゴリ
    category_name = new ALStringField();
    category_name.setFieldName(getl10n("WIKI_CATEGORY_NAME"));
    // メモ
    note = new ALStringField();
    note.setFieldName(getl10n("WIKI_NOTE"));
    note.setTrim(false);

  }

  /**
   * Wikiの各フィールドに対する制約条件を設定します。 <BR>
   */
  @Override
  protected void setValidator() {
    // wiki名必須項目
    name.setNotNull(true);
    // wiki名の文字数制限
    name.limitMaxLength(50);
    // メモ必須項目
    note.setNotNull(true);
    // メモの文字数制限
    note.limitMaxLength(10000);
    if (is_new_category) {
      // カテゴリ名必須項目
      category_name.setNotNull(true);
      // カテゴリ名文字数制限
      category_name.limitMaxLength(50);
    }
  }

  /**
   * Wikiのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // wiki名
    name.validate(msgList);
    // メモ
    note.validate(msgList);
    if (is_new_category) {
      // カテゴリ名
      category_name.validate(msgList);
    }

    boolean duplication = false;
    if (ALEipConstants.MODE_UPDATE.equals(mode)
      && !StringUtils.isEmpty(entityId)
      && StringUtils.isNumeric(entityId)) {
      duplication =
        WikiUtils.isTitleDuplicate(name.getValue(), Integer.parseInt(entityId));
    } else {
      duplication = WikiUtils.isTitleDuplicate(name.getValue());
    }
    if (duplication) {
      msgList.add(getl10n("WIKI_DUPLICATE_TITLE"));
    }

    return (msgList.size() == 0);
  }

  /**
   * Wikiをデータベースから読み出します。 <BR>
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
      EipTWiki wiki = WikiUtils.getEipTWiki(rundata, context);

      // wiki名
      name.setValue(wiki.getWikiName());
      // カテゴリID
      category_id.setValue(wiki.getCategoryId());
      // 内容
      note.setValue(wiki.getNote());

    } catch (Exception e) {
      logger.error("WikiFormData.loadFormData", e);
      return false;
    }
    return true;
  }

  /**
   * Wikiをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return true;
  }

  /**
   * Wikiをデータベースに格納します。 <BR>
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
      if (is_new_category) {
        // カテゴリの登録処理
        if (!insertCategoryData(rundata, context, msgList)) {
          return false;
        }
      } else {
        category =
          Database.get(EipTWikiCategory.class, Integer
            .valueOf((int) category_id.getValue()));
      }

      // 新規オブジェクトモデル
      EipTWiki wiki = Database.create(EipTWiki.class);
      // トピック名
      wiki.setWikiName(name.getValue());
      // カテゴリID
      wiki.setEipTWikiCategory(category);
      // メモ
      wiki.setNote(note.getValue());
      // 作成者
      TurbineUser turbineUser = ALEipUtils.getTurbineUser(Integer.valueOf(uid));
      wiki.setCreateUser(turbineUser);
      // 更新者
      wiki.setUpdateUser(turbineUser);
      // 作成日
      wiki.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      wiki.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit();

    } catch (Exception e) {
      logger.error("WikiFormData.insertFormData", e);
      return false;
    }
    return true;
  }

  /**
   * トピックカテゴリをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  private boolean insertCategoryData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      TurbineUser tuser = Database.get(TurbineUser.class, Integer.valueOf(uid));

      // 新規オブジェクトモデル
      category = Database.create(EipTWikiCategory.class);
      // カテゴリ名
      category.setCategoryName(category_name.getValue());
      // ユーザーID
      category.setTurbineUser(tuser);
      // 更新ユーザーID
      category.setUpdateUserId(Integer.valueOf(uid));
      // 作成日
      category.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      category.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit();

    } catch (Exception e) {
      Database.rollback();
      logger.error("insertCategoryData", e);
      msgList.add("エラーが発生しました。");
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているWikiを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      if (is_new_category) {
        // カテゴリの登録処理
        if (!insertCategoryData(rundata, context, msgList)) {
          return false;
        }
      } else {
        category =
          Database.get(EipTWikiCategory.class, Integer
            .valueOf((int) category_id.getValue()));
      }

      // 新規オブジェクトモデル
      EipTWiki wiki = WikiUtils.getEipTWiki(rundata, context);
      // トピック名
      wiki.setWikiName(name.getValue());
      // カテゴリID
      wiki.setEipTWikiCategory(category);
      // メモ
      wiki.setNote(note.getValue());
      TurbineUser turbineUser = ALEipUtils.getTurbineUser(Integer.valueOf(uid));
      // 更新者
      wiki.setUpdateUser(turbineUser);
      // 作成日
      wiki.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      wiki.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit();

    } catch (Exception e) {
      logger.error("WikiFormData.updateFormData", e);
      return false;
    }
    return true;
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

  /**
   * カテゴリIDを取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
  }

  /**
   * カテゴリ名を取得します。
   * 
   * @return
   */
  public ALStringField getCategoryName() {
    return category_name;
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
   * トピック名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  public List<WikiCategoryResultData> getCategoryList() {
    return categoryList;
  }

}
