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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログテーマ検索データを管理するクラスです。 <BR>
 * 
 */
public class BlogCommonThemaSelectData extends
    ALAbstractSelectData<EipTBlogThema, EipTBlogThema> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogCommonThemaSelectData.class.getName());

  private int loginuser_id = 0;

  private final List<Integer> users = new ArrayList<Integer>();

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    loginuser_id = ALEipUtils.getUserId(rundata);

    // アクセス権限チェック 他人の記事一覧表示
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    boolean hasAuthority =
      aclhandler.hasAuthority(
        loginuser_id,
        ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    if (!hasAuthority) {
      ALEipUtils.redirectPermissionError(rundata);
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
  protected ResultList<EipTBlogThema> selectList(RunData rundata,
      Context context) {
    try {
      SelectQuery<EipTBlogThema> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTBlogThema> list = query.getResultList();

      if (list != null && list.size() > 0) {
        EipTBlogThema[] themas = new EipTBlogThema[list.size()];
        themas = list.toArray(themas);

        Comparator<EipTBlogThema> comp = getCommonThemaComparator();
        if (comp != null) {
          Arrays.sort(themas, comp);
        }

        list.clear();

        int size = themas.length;
        for (int i = 0; i < size; i++) {
          list.add(themas[i]);
        }
      }
      return list;
    } catch (Exception ex) {
      logger.error("BlogCommonThemaSelectData.selectList", ex);
      return null;
    }
  }

  public static Comparator<EipTBlogThema> getCommonThemaComparator() {
    Comparator<EipTBlogThema> com = null;

    // テーマの昇順
    com = new Comparator<EipTBlogThema>() {
      @Override
      public int compare(EipTBlogThema obj0, EipTBlogThema obj1) {
        int ret = 0;
        try {
          int themaSize0 = (obj0).getEipTBlogEntrys().size();
          int themaSize1 = (obj1).getEipTBlogEntrys().size();
          ret = themaSize1 - themaSize0;
        } catch (Exception e) {
          ret = -1;
        }
        return ret;
      }
    };

    return com;
  }

  public static Comparator<EipTBlogEntry> getCommonEntryComparator() {
    Comparator<EipTBlogEntry> com = null;

    // テーマの昇順
    com = new Comparator<EipTBlogEntry>() {
      @Override
      public int compare(EipTBlogEntry obj0, EipTBlogEntry obj1) {
        int ret = 0;
        try {
          Date createDate0 = (obj0).getCreateDate();
          Date createDate1 = (obj1).getCreateDate();
          if (createDate1.after(createDate0)) {
            ret = 1;
          } else if (createDate1.before(createDate0)) {
            ret = -1;
          } else {
            ret = 0;
          }
        } catch (Exception e) {
          ret = -1;
        }
        return ret;
      }
    };

    return com;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTBlogThema> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTBlogThema> query = Database.query(EipTBlogThema.class);
    query.prefetch(EipTBlogThema.EIP_TBLOG_ENTRYS_PROPERTY);
    return query;
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTBlogThema selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return BlogUtils.getEipTBlogThema(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTBlogThema record) {
    try {
      BlogThemaResultData rd = new BlogThemaResultData();
      rd.initField();
      rd.setThemaId(record.getThemaId().longValue());
      rd.setThemaName(record.getThemaName());
      rd.setDescription(record.getDescription());
      rd.setEntryNum(record.getEipTBlogEntrys().size());
      return rd;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTBlogThema record) {
    String string;
    try {
      BlogThemaResultData rd = new BlogThemaResultData();
      rd.initField();
      rd.setThemaId(record.getThemaId().longValue());
      // prevent from layout to destroy
      int displayMax = 9;
      if (record.getThemaName().length() > displayMax) {
        string =
          record.getThemaName().substring(0, (displayMax - 3)).concat("・・・");
        rd.setThemaName(string);
      } else {
        rd.setThemaName(record.getThemaName());
      }
      rd.setDescription(record.getDescription());

      List<BlogEntryResultData> entryList =
        new ArrayList<BlogEntryResultData>();
      EipTBlogEntry entry = null;
      BlogEntryResultData entryrd = null;
      List<?> list = record.getEipTBlogEntrys();

      EipTBlogEntry[] entrys = new EipTBlogEntry[list.size()];
      entrys = list.toArray(entrys);

      Comparator<EipTBlogEntry> comp = getCommonEntryComparator();
      if (comp != null) {
        Arrays.sort(entrys, comp);
      }

      int size = entrys.length;
      for (int i = 0; i < size; i++) {
        entry = entrys[i];
        entryrd = new BlogEntryResultData();
        entryrd.initField();
        entryrd.setEntryId(entry.getEntryId().longValue());
        entryrd.setTitle(ALCommonUtils.compressString(
          entry.getTitle(),
          getStrLength()));
        entryrd.setNote(entry.getNote().replaceAll("\\r\\n", " ").replaceAll(
          "\\n",
          " ").replaceAll("\\r", " "));
        entryrd.setTitleDate(entry.getCreateDate());

        entryrd.setOwnerId(entry.getOwnerId().intValue());

        List<?> comments = entry.getEipTBlogComments();
        if (comments != null) {
          entryrd.setCommentsNum(comments.size());
        }

        if (!users.contains(entry.getOwnerId())) {
          users.add(entry.getOwnerId());
        }

        entryList.add(entryrd);
      }
      if (entryList.size() > 0) {
        rd.setEntryList(entryList);
      }

      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));

      loadAggregateUsers();

      return rd;
    } catch (RuntimeException e) {
      // RuntimeException
      logger.error("BlogCommonThemaSelectData.getResultDataDetail", e);
      return null;
    } catch (Exception e) {
      logger.error("BlogCommonThemaSelectData.getResultDataDetail", e);
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
    map.putValue("thema_name", EipTBlogThema.THEMA_NAME_PROPERTY);
    return map;
  }

  public int getLoginUserId() {
    return loginuser_id;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
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

  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    boolean result = super.doViewList(action, rundata, context);
    loadAggregateUsers();
    return result;
  }

  protected void loadAggregateUsers() {
    ALEipManager.getInstance().getUsers(users);
  }
}
