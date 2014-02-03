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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * Wikiカテゴリ検索データを管理するクラスです。 <BR>
 * 
 */
public class WikiCategorySelectData extends
    ALAbstractSelectData<EipTWikiCategory, EipTWikiCategory> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiCategorySelectData.class.getName());

  /** カテゴリの総数 */
  private int categorySum;

  private ArrayList<WikiCategoryResultData> categoryList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "category_name");
      logger.debug("[WikiCategorySelectData] Init Parameter. : "
        + "category_name");
    }

    ALEipUtils.getUserId(rundata);

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTWikiCategory> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipTWikiCategory> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTWikiCategory> list = query.getResultList();
      // 件数をセットする．
      categorySum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("wiki", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTWikiCategory> getSelectQuery(RunData rundata,
      Context context) {
    return Database.query(EipTWikiCategory.class).where(
      Operations.eq(EipTWikiCategory.USER_ID_PROPERTY, ALEipUtils
        .getUserId(rundata)));
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTWikiCategory selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    return WikiUtils.getEipTWikiCategory(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTWikiCategory record) {
    WikiCategoryResultData rd = new WikiCategoryResultData();
    rd.initField();
    rd.setCategoryId(record.getCategoryId().longValue());
    rd.setCategoryName(ALCommonUtils.compressString(
      record.getCategoryName(),
      getStrLength()));

    try {
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
    } catch (ALDBErrorException ex) {
      logger.error("wiki", ex);
    }
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTWikiCategory record) {
    WikiCategoryResultData rd = new WikiCategoryResultData();
    rd.initField();
    rd.setCategoryId(record.getCategoryId().longValue());
    rd.setCategoryName(record.getCategoryName());
    try {
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
      rd.setUpdateUserName(ALEipUtils
        .getALEipUser(record.getUpdateUserId())
        .getAliasName()
        .getValue());
    } catch (ALDBErrorException ex) {
      logger.error("wiki", ex);
    }
    return rd;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("category_name", EipTWikiCategory.CATEGORY_NAME_PROPERTY);
    map.putValue("user_name", EipTWikiCategory.TURBINE_USER_PROPERTY
      + "."
      + TurbineUser.LAST_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata) {
    categoryList = WikiUtils.getCategoryList(rundata);
  }

  public int getCategorySum() {
    return categorySum;
  }

  /**
   * 現在選択されているタブを取得します。 <BR>
   * 
   * @return
   */
  public String getCurrentTab() {
    return "category";
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 
   * @return
   */
  public List<WikiCategoryResultData> getCategoryList() {
    return categoryList;
  }
}
