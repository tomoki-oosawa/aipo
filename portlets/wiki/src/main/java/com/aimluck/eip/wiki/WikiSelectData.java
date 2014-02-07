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

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.common.ALAbstractMultiFilterSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * Wiki検索データを管理するクラスです。 <BR>
 * 
 */
public class WikiSelectData extends
    ALAbstractMultiFilterSelectData<EipTWiki, EipTWiki> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiSelectData.class.getName());

  /** カテゴリ一覧 */
  private List<WikiCategoryResultData> categoryList;

  /** カテゴリの初期値を取得する */
  private String filterType = "";

  /** カテゴリ　ID */
  private String categoryId = "";

  /** カテゴリ名 */
  private String categoryName = "";

  /** ターゲット　 */
  private ALStringField target_keyword;

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

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
      // default sort
      String sortStr = "update_date";
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, sortStr);
      if ("update_date".equals(sortStr)) {
        ALEipUtils.setTemp(rundata, context, LIST_SORT_TYPE_STR, "desc");
      }
    }

    // カテゴリの初期値を取得する
    try {
      filterType = rundata.getParameters().getString("filtertype", "");
      if (filterType.equals("category")) {
        String categoryId = rundata.getParameters().getString("filter", "");
        if (!categoryId.equals("")) {
          this.categoryId = categoryId;
        } else {
          VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
          this.categoryId =
            portlet.getPortletConfig().getInitParameter("p3a-category");
        }
      }
    } catch (Exception e) {
      logger.error("WikiSelectData.init", e);
    }
    try {
      updateCategoryName();
    } catch (Exception e) {
      logger.error("WikiSelectData.init", e);
    }

    target_keyword = new ALStringField();
    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata) {
    categoryList = WikiUtils.getCategoryList(rundata);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALDBErrorException
   */
  @Override
  public ResultList<EipTWiki> selectList(RunData rundata, Context context) {
    try {
      target_keyword.setValue(WikiUtils.getTargetKeyword(rundata, context));

      SelectQuery<EipTWiki> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      ResultList<EipTWiki> list = query.getResultList();
      setPageParam(list.getTotalCount());
      return list;
    } catch (Exception e) {
      logger.error("WikiSelectData.selectList", e);
      return null;
    }
  }

  private SelectQuery<EipTWiki> getSelectQuery(RunData rundata, Context context) {
    SelectQuery<EipTWiki> query = Database.query(EipTWiki.class);
    if ((target_keyword != null) && (!target_keyword.getValue().equals(""))) {
      // 選択したキーワードを指定する．
      String keyword = "%" + target_keyword.getValue() + "%";

      Expression exp =
        ExpressionFactory.likeExp(EipTWiki.WIKI_NAME_PROPERTY, keyword);
      Expression exp2 =
        ExpressionFactory.likeExp(EipTWiki.NOTE_PROPERTY, keyword);
      query.andQualifier(exp.orExp(exp2));
    }
    return buildSelectQueryForFilter(query, rundata, context);
  }

  @Override
  protected SelectQuery<EipTWiki> buildSelectQueryForFilter(
      SelectQuery<EipTWiki> query, RunData rundata, Context context) {
    if (current_filterMap.containsKey("category")) {
      // カテゴリを含んでいる場合デフォルトとは別にフィルタを用意
      List<String> categoryIds = current_filterMap.get("category");
      categoryId = categoryIds.get(0).toString();
      List<WikiCategoryResultData> categoryList =
        WikiUtils.loadCategoryList(rundata);
      boolean existCategory = false;
      if (categoryList != null && categoryList.size() > 0) {
        for (WikiCategoryResultData category : categoryList) {
          if (categoryId.equals(category.getCategoryId().toString())) {
            existCategory = true;
            break;
          }
        }

      }
      if (!existCategory) {
        categoryId = "";
        current_filterMap.remove("category");
      }

      updateCategoryName();
    }

    super.buildSelectQueryForFilter(query, rundata, context);

    return query;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTWiki record) {
    try {
      WikiResultData rd = new WikiResultData();
      rd.initField();
      rd.setId(record.getWikiId().longValue());
      rd.setName(record.getWikiName());
      rd.setCategoryId(record.getCategoryId().longValue());
      rd.setUpdateUser(ALEipUtils
        .getALEipUser(record.getUpdateUserId())
        .getAliasName()
        .getValue());
      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());
      return rd;
    } catch (Exception e) {
      logger.error("WikiSelectData.getResultData", e);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALDBErrorException
   */
  @Override
  public EipTWiki selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipTWiki wiki = WikiUtils.getEipTWiki(rundata, context);
      return wiki;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTWiki record) {
    try {
      WikiResultData rd = new WikiResultData();
      rd.initField();

      // 登録ユーザ名の設定
      ALEipUser createdUser =
        ALEipUtils.getALEipUser(record.getCreateUserId().intValue());
      String createdUserName = createdUser.getAliasName().getValue();
      rd.setCreateUser(createdUserName);

      // 更新ユーザ名の設定
      String updatedUserName;
      if (record.getCreateUserId().equals(record.getUpdateUserId())) {
        updatedUserName = createdUserName;
      } else {
        ALEipUser updatedUser =
          ALEipUtils.getALEipUser(record.getUpdateUserId().intValue());
        updatedUserName = updatedUser.getAliasName().getValue();
      }
      rd.setUpdateUser(updatedUserName);
      rd.setId(record.getWikiId().longValue());
      rd.setName(record.getWikiName());
      rd.setCategoryId(record.getCategoryId().longValue());
      rd.setCategoryName(WikiUtils
        .getEipTWikiCategory(record.getCategoryId())
        .getCategoryName());
      rd.setNote(record.getNote());
      rd.setCreateUser(ALEipUtils
        .getALEipUser(record.getCreateUserId())
        .getAliasName()
        .getValue());
      rd.setUpdateUser(ALEipUtils
        .getALEipUser(record.getUpdateUserId())
        .getAliasName()
        .getValue());
      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());
      return rd;
    } catch (Exception e) {
      logger.error("WikiSelectData.getResultDataDetail", e);
      return null;
    }
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("category", EipTWikiCategory.CATEGORY_ID_PK_COLUMN);
    map.putValue("wiki_name", EipTWiki.WIKI_NAME_PROPERTY);
    map.putValue("update_user", EipTWiki.UPDATE_USER_ID_PROPERTY);
    map.putValue("update_date", EipTWiki.UPDATE_DATE_PROPERTY);
    return map;
  }

  public String getCategoryId() {
    return categoryId;
  }

  private void updateCategoryName() {
    categoryName = "";
    if (categoryList != null) {
      for (int i = 0; i < categoryList.size(); i++) {
        String cid = categoryList.get(i).getCategoryId().toString();
        if (cid != null && categoryId != null) {
          if (cid.equals(categoryId.toString())) {
            categoryName = categoryList.get(i).getCategoryName().toString();
            return;
          }
        }
      }
    }
  }

  public String getCategoryName() {
    return categoryName;
  }

  public List<WikiCategoryResultData> getCategoryList() {
    return categoryList;
  }

  /**
   * @return target_keyword
   */
  public ALStringField getTargetKeyword() {
    return target_keyword;
  }
}
