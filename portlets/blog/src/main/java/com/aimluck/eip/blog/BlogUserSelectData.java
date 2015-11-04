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
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログ画面のユーザー情報の検索データを管理するクラスです。 <BR>
 * 
 */
public class BlogUserSelectData extends
    ALAbstractSelectData<BlogUserResultData, BlogUserResultData> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogUserSelectData.class.getName());

  /** 表示対象の部署名 */
  private String target_group_name;

  private List<ALEipGroup> myGroupList = null;

  private int login_uid = -1;

  private List<Integer> latestBlogerIds = null;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    target_group_name = BlogUtils.getTargetGroupName(rundata, context);

    login_uid = ALEipUtils.getUserId(rundata);

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    int length = myGroups.size();
    for (int i = 0; i < length; i++) {
      myGroupList.add(myGroups.get(i));
    }

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
  public ResultList<BlogUserResultData> selectList(RunData rundata,
      Context context) {
    try {
      latestBlogerIds = getLatestBlogerIds();

      String groupname = null;
      if ((target_group_name != null)
        && (!target_group_name.equals(""))
        && (!target_group_name.equals("all"))) {
        groupname = target_group_name;
      } else {
        groupname = "LoginUser";
      }
      List<BlogUserResultData> list2 =
        BlogUtils.getBlogUserResultDataList(groupname);
      return new ResultList<BlogUserResultData>(list2, getCurrentPage(), list2
        .size(), list2.size());
    } catch (Exception ex) {
      logger.error("blog", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(BlogUserResultData rd) {

    if (latestBlogerIds != null
      && latestBlogerIds.contains(Integer.valueOf((int) rd
        .getUserId()
        .getValue()))) {
      rd.setNewlyCreateEntry(true);
    } else {
      rd.setNewlyCreateEntry(false);
    }
    return rd;
  }

  private List<Integer> getLatestBlogerIds() {
    try {
      // 表示開始日時
      ALDateTimeField field = new ALDateTimeField("yyyy-MM-dd");
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, -1);
      field.setValue(cal.getTime());

      SelectQuery<EipTBlogEntry> query = Database.query(EipTBlogEntry.class);
      Expression exp =
        ExpressionFactory.greaterOrEqualExp(
          EipTBlogEntry.CREATE_DATE_PROPERTY,
          field.getValue());
      query.setQualifier(exp);
      List<EipTBlogEntry> list = query.fetchList();
      if (list == null || list.size() <= 0) {
        return null;
      }

      List<Integer> blogerids = new ArrayList<Integer>();
      Integer id = null;
      EipTBlogEntry entry = null;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        entry = list.get(i);
        id = entry.getOwnerId();
        if (!blogerids.contains(id)) {
          blogerids.add(id);
        }
      }

      return blogerids;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public BlogUserResultData selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(BlogUserResultData obj) {
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    return map;
  }

  public String getTargetGroupName() {
    return target_group_name;
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
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public int getLoginUid() {
    return login_uid;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER;
  }
}
