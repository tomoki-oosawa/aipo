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
import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.cayenne.om.portlet.EipTWikiFile;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.util.WikiFileUtils;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * Wikiのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class WikiFormData extends ALAbstractFormData {

  /** wiki名 */
  private ALStringField name;

  /** カテゴリ ID */
  private ALNumberField parentId;

  /** カテゴリ名 */
  private ALStringField parent_name;

  /** メモ */
  private ALStringField note;

  /** 親ページ判断 */
  private boolean is_child;

  /** */
  private boolean is_new_category;

  private EipTWiki category;

  /** カテゴリ一覧 */
  private List<WikiResultData> categoryList;

  private int uid;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiFormData.class.getName());

  /** ACL用の変数 * */
  private final String aclPortletFeature = "";

  private String entityId = null;

  private String mode = null;

  /** 添付ファイルリスト */
  private List<FileuploadLiteBean> fileuploadList =
    new ArrayList<FileuploadLiteBean>();

  /** 添付フォルダ名 */
  private String folderName = null;

  private EipTWiki parentWiki = null;

  private String postedParentId = null;

  private String destWikiName = "";

  /**
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    is_child = rundata.getParameters().getBoolean("is_child");
    uid = ALEipUtils.getUserId(rundata);
    entityId = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    mode = rundata.getParameters().getString(ALEipConstants.MODE, "");
    folderName = rundata.getParameters().getString("folderName");
    postedParentId = rundata.getParameters().getString("parentId", "0");
    if (!StringUtils.isNumeric(postedParentId)
      || Integer.valueOf(postedParentId).intValue() < 0) {
      postedParentId = "0";
    }
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
    parentId = new ALNumberField();
    parentId.setFieldName(getl10n("WIKI_CATEGORY"));
    // カテゴリ
    parent_name = new ALStringField();
    parent_name.setFieldName(getl10n("WIKI_CATEGORY_NAME"));
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
    if (is_child) {
      // カテゴリ名必須項目
      parent_name.setNotNull(true);
      // カテゴリ名文字数制限
      parent_name.limitMaxLength(50);
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
    if (is_child) {
      // カテゴリ名
      parent_name.validate(msgList);
    }

    boolean duplication = false;
    if (ALEipConstants.MODE_UPDATE.equals(mode)
      && !StringUtils.isEmpty(entityId)
      && StringUtils.isNumeric(entityId)) {
      duplication =
        WikiUtils.isTitleDuplicate(name.getValue(), Integer.parseInt(entityId));
    } else {
      duplication =
        WikiUtils.isTitleDuplicate(name.getValue(), Integer
          .parseInt(postedParentId));
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

      parentId.setValue(wiki.getParentId());
      // 内容
      note.setValue(wiki.getNote());

      // ファイル
      SelectQuery<EipTWikiFile> query = Database.query(EipTWikiFile.class);
      query.andQualifier(ExpressionFactory.matchExp(
        EipTWikiFile.WIKI_ID_PROPERTY,
        wiki.getWikiId()));
      List<EipTWikiFile> fileList = query.fetchList();
      for (EipTWikiFile file : fileList) {
        FileuploadLiteBean fbean = new FileuploadLiteBean();
        fbean.initField();
        fbean.setFileId(file.getFileId());
        fbean.setFileName(file.getFileName());
        fileuploadList.add(fbean);
      }

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
    try {
      // オブジェクトモデルを取得
      EipTWiki wiki = WikiUtils.getEipTWiki(rundata, context);
      if (wiki == null) {
        return false;
      }

      // entityIdの取得
      int entityId = wiki.getWikiId();

      SelectQuery<EipTWiki> query = Database.query(EipTWiki.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTWiki.PARENT_ID_PROPERTY, entityId);
      List<EipTWiki> fetchList = query.andQualifier(exp).fetchList();
      if (fetchList.size() > 0) {
        msgList.add(getl10n("WIKI_HAS_CHILDREN"));
        return false;
      }

      // タイトルの取得
      String wikiName = wiki.getWikiName();
      // 添付ファイルの削除
      WikiFileUtils.deleteFiles(wiki.getWikiId());

      // Wikiを削除
      Database.delete(wiki);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_WIKI,
        wikiName);

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WikiFormData]", t);
      return false;
    }
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

      // 新規オブジェクトモデル
      EipTWiki wiki = Database.create(EipTWiki.class);
      // トピック名
      wiki.setWikiName(name.getValue());
      // 親ID

      if (StringUtils.isNotEmpty(postedParentId)) {
        wiki.setParentId(Integer.valueOf(postedParentId));
      } else if (null == category) {
        wiki.setParentId(WikiUtils.PARENT_WIKI);
      } else {
        wiki.setParentId(category.getWikiId());
      }
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

      // ファイルをデータベースに登録する．
      if (!WikiFileUtils.insertFileDataDelegate(
        rundata,
        context,
        wiki,
        fileuploadList,
        folderName,
        msgList)) {
        return false;
      }

      Database.commit();

      // 添付ファイル保存先のフォルダを削除
      ALStorageService.deleteTmpFolder(uid, folderName);

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
      category = Database.create(EipTWiki.class);
      // 親ID
      category.setParentId(WikiUtils.PARENT_WIKI);
      // ユーザーID
      category.setCreateUser(tuser);
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

      // 新規オブジェクトモデル
      EipTWiki wiki = WikiUtils.getEipTWiki(rundata, context);
      // トピック名
      wiki.setWikiName(name.getValue());
      // メモ
      wiki.setNote(note.getValue());
      TurbineUser turbineUser = ALEipUtils.getTurbineUser(Integer.valueOf(uid));
      // 更新者
      wiki.setUpdateUser(turbineUser);
      // 作成日
      wiki.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      wiki.setUpdateDate(Calendar.getInstance().getTime());

      // ファイルをデータベースに登録する．
      if (!WikiFileUtils.insertFileDataDelegate(
        rundata,
        context,
        wiki,
        fileuploadList,
        folderName,
        msgList)) {
        return false;
      }

      Database.commit();

      // 添付ファイル保存先のフォルダを削除
      ALStorageService.deleteTmpFolder(uid, folderName);

    } catch (Exception e) {
      logger.error("WikiFormData.updateFormData", e);
      return false;
    }
    return true;
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

    try {
      fileuploadList = WikiFileUtils.getFileuploadList(rundata);
    } catch (Exception e) {
      logger.error("WikiFormData.setFormData", e);
    }

    return res;
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
   * 子ページかどうか判断します。 <BR>
   * 
   * @return
   */
  public boolean getChild() {
    return is_child;
  }

  /**
   * カテゴリ名を取得します。
   * 
   * @return
   */
  public ALStringField getParentName() {
    return parent_name;
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

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    return fileuploadList;
  }

  public String getFolderName() {
    return folderName;
  }

  public void setParentWiki(EipTWiki wiki) {
    this.parentWiki = wiki;
  }

  public String getParentIdString() {
    if (null != parentWiki
      && parentWiki.getParentId() != null
      && parentWiki.getParentId().intValue() != 0) {
      return String.valueOf(parentWiki.getParentId());
    } else {
      return "";
    }
  }

  public String getParentWikiIdString() {
    if (null != parentWiki) {
      return String.valueOf(parentWiki.getWikiId());
    } else {
      return "0";
    }
  }

  public String getParentWikiName() {
    if (null == parentWiki) {
      return "";
    }
    String name = parentWiki.getWikiName();
    if (StringUtils.isEmpty(name)) {
      return "";
    }
    return new ALStringField(name).toString();
  }

  public boolean isChildForm() {
    return null != parentWiki;
  }

  public void setDestWikiName(String name) {
    this.destWikiName = name;
  }

  public String getDestWikiName() {
    return new ALStringField(destWikiName).toString();
  }

  public String getParentId() {
    return parentId.toString();
  }
}
