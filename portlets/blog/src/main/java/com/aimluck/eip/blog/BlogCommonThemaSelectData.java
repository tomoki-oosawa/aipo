/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.blog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログテーマ検索データを管理するクラスです。 <BR>
 * 
 */
public class BlogCommonThemaSelectData extends ALAbstractSelectData implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(BlogCommonThemaSelectData.class.getName());

  private DataContext dataContext;

  private int loginuser_id = 0;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    loginuser_id = ALEipUtils.getUserId(rundata);

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List selectList(RunData rundata, Context context) {
    try {
      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      @SuppressWarnings("unchecked")
      List aList = dataContext.performQuery(query);
      List list = buildPaginatedList(aList);

      if (list != null && list.size() > 0) {
        EipTBlogThema[] themas = new EipTBlogThema[list.size()];
        themas = (EipTBlogThema[]) list.toArray(themas);

        Comparator comp = getCommonThemaComparator();
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
      logger.error("Exception", ex);
      return null;
    }
  }

  public static Comparator getCommonThemaComparator() {
    Comparator com = null;

    // テーマの昇順
    com = new Comparator() {
      public int compare(Object obj0, Object obj1) {
        int ret = 0;
        try {
          int themaSize0 = ((EipTBlogThema) obj0).getEipTBlogEntrys().size();
          int themaSize1 = ((EipTBlogThema) obj1).getEipTBlogEntrys().size();
          ret = themaSize1 - themaSize0;
        } catch (Exception e) {
          ret = -1;
        }
        return ret;
      }
    };

    return com;
  }

  public static Comparator getCommonEntryComparator() {
    Comparator com = null;

    // テーマの昇順
    com = new Comparator() {
      public int compare(Object obj0, Object obj1) {
        int ret = 0;
        try {
          Date createDate0 = ((EipTBlogEntry) obj0).getCreateDate();
          Date createDate1 = ((EipTBlogEntry) obj1).getCreateDate();
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
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipTBlogThema.class);
    query.addPrefetch(EipTBlogThema.EIP_TBLOG_ENTRYS_PROPERTY);
    return query;
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return BlogUtils.getEipTBlogThema(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      EipTBlogThema record = (EipTBlogThema) obj;
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
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj) {
    try {
      EipTBlogThema record = (EipTBlogThema) obj;
      BlogThemaResultData rd = new BlogThemaResultData();
      rd.initField();
      rd.setThemaId(record.getThemaId().longValue());
      rd.setThemaName(record.getThemaName());
      rd.setDescription(record.getDescription());

      List entryList = new ArrayList();
      EipTBlogEntry entry = null;
      BlogEntryResultData entryrd = null;
      List list = record.getEipTBlogEntrys();

      EipTBlogEntry[] entrys = new EipTBlogEntry[list.size()];
      entrys = (EipTBlogEntry[]) list.toArray(entrys);

      Comparator comp = getCommonEntryComparator();
      if (comp != null) {
        Arrays.sort(entrys, comp);
      }

      int size = entrys.length;
      for (int i = 0; i < size; i++) {
        entry = (EipTBlogEntry) entrys[i];
        entryrd = new BlogEntryResultData();
        entryrd.initField();
        entryrd.setEntryId(entry.getEntryId().longValue());
        entryrd.setTitle(ALCommonUtils.compressString(entry.getTitle(),
            getStrLength()));
        entryrd.setNote(BlogUtils.compressString(entry.getNote(), 100));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日（EE）");
        entryrd.setTitleDate(sdf.format(entry.getCreateDate()));

        entryrd.setOwnerId(entry.getOwnerId().intValue());
        entryrd.setOwnerName(BlogUtils.getUserFullName(entry.getOwnerId()
            .intValue()));

        List comments = entry.getEipTBlogComments();
        if (comments != null) {
          entryrd.setCommentsNum(comments.size());
        }

        entryList.add(entryrd);
      }
      if (entryList.size() > 0) {
        rd.setEntryList(entryList);
      }

      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
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
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_THEME;
  }
}
