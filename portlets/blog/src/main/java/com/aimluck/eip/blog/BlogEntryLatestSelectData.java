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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import javax.imageio.ImageIO;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
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
 * ブログエントリー検索データを管理するクラスです。 <BR>
 *
 */
public class BlogEntryLatestSelectData extends ALAbstractSelectData implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(BlogEntryLatestSelectData.class.getName());

  /** エントリーの総数 */
  private int entrySum;

  private List photoList;

  private int uid;

  /** 新着コメントがついたエントリー ID */
  private int newEntryId;

  /** ユーザーがコメントした記事の一覧 */
  private List commentHistoryList;

  /** コメントした記事が一覧に表示される日数 */
  private final int DELETE_DATE = 7;

  private DataContext dataContext;

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

    uid = ALEipUtils.getUserId(rundata);

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    super.init(action, rundata, context);
  }

  private void loadPhotos() throws Exception {
    photoList = new ArrayList();

    // String[] ext = { ".jpg", ".jpeg", ".JPG", ".JPEG" };
    String[] ext = ImageIO.getWriterFormatNames();

    SelectQuery<EipTBlogFile> query = new SelectQuery<EipTBlogFile>(EipTBlogFile.class);
    Expression exp01 = ExpressionFactory.likeExp(EipTBlogFile.TITLE_PROPERTY,
        "%" + ext[0]);
    query.setQualifier(exp01);
    for (int i = 1; i < ext.length; i++) {
      Expression exp02 = ExpressionFactory.likeExp(EipTBlogFile.TITLE_PROPERTY,
          "%" + ext[i]);
      query.orQualifier(exp02);
    }

    query.orderDesending(EipTBlogFile.UPDATE_DATE_PROPERTY);
    query.limit(5);
    List list = query.fetchList();
    if (list != null && list.size() > 0) {
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTBlogFile record = (EipTBlogFile) list.get(i);
        BlogFileResultData file = new BlogFileResultData();
        file.initField();
        file.setFileId(record.getFileId().longValue());
        file.setOwnerId(record.getOwnerId().longValue());
        file.setEntryId(record.getEipTBlogEntry().getEntryId().longValue());
        file.setEntryTitle(record.getEipTBlogEntry().getTitle());
        photoList.add(file);
      }
    }
  }

  private void loadCommentHistoryList(RunData rundata) throws Exception {
    commentHistoryList = new ArrayList();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日（EE）");
    Integer thisUserId = Integer.valueOf(uid);
    Object beforeEntryId = null;

    SelectQuery comment_query = new SelectQuery(EipTBlogComment.class);
    // ユーザーがコメントした記事のリストをEntryId順に作成
    Expression exp1 = ExpressionFactory.matchExp(
        EipTBlogComment.OWNER_ID_PROPERTY, thisUserId);
    comment_query.setQualifier(exp1);
    Expression exp2 = ExpressionFactory.greaterExp(
        EipTBlogComment.UPDATE_DATE_PROPERTY,
        reduceDate(Calendar.getInstance().getTime(), DELETE_DATE));
    comment_query.andQualifier(exp2);
    comment_query.orderAscending("eipTBlogEntry");
    List aList = comment_query.fetchList();

    // リストからcommentHistoryListを作成する
    int size = aList.size();
    for (int i = 0; i < size; i++) {
      EipTBlogComment record = (EipTBlogComment) aList.get(i);
      EipTBlogEntry entry = record.getEipTBlogEntry();
      if (entry.getOwnerId().equals(thisUserId))
        continue;
      if (entry.getEntryId().equals(beforeEntryId))
        continue;
      else
        beforeEntryId = entry.getEntryId();
      BlogEntryResultData rd = new BlogEntryResultData();
      rd.initField();
      rd.setEntryId(entry.getEntryId().longValue());
      rd.setOwnerId(entry.getOwnerId().longValue());
      rd.setOwnerName(BlogUtils.getUserFullName(entry.getOwnerId().intValue()));
      rd.setTitle(ALCommonUtils.compressString(entry.getTitle(), getStrLength()));
      rd.setTitleDate(sdf.format(record.getUpdateDate()));

      SelectQuery<EipTBlogComment> cquery = new SelectQuery<EipTBlogComment>(EipTBlogComment.class).select(EipTBlogComment.COMMENT_ID_PK_COLUMN);
      Expression cexp = ExpressionFactory.matchDbExp(
          EipTBlogComment.EIP_TBLOG_ENTRY_PROPERTY + "."
              + EipTBlogEntry.ENTRY_ID_PK_COLUMN, entry.getEntryId());
      cquery.setQualifier(cexp);
      List<EipTBlogComment> list = cquery.fetchList();
      if (list != null && list.size() > 0) {
        rd.setCommentsNum(list.size());
      }
      commentHistoryList.add(rd);
    }
    // コメント日時の新しい順に並び替え
    Collections.sort(commentHistoryList, getDateComparator());
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractListData#selectData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  public List selectList(RunData rundata, Context context) {
    try {
      loadPhotos();
      loadCommentHistoryList(rundata);

      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      query.orderDesending(EipTBlogEntry.CREATE_DATE_PROPERTY);
      List list = query.fetchList();
      // エントリーの総数をセットする．
      entrySum = list.size();
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipTBlogEntry.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      EipTBlogEntry record = (EipTBlogEntry) obj;

      BlogEntryResultData rd = new BlogEntryResultData();
      rd.initField();
      rd.setEntryId(record.getEntryId().longValue());
      rd.setOwnerId(record.getOwnerId().longValue());
      rd.setOwnerName(BlogUtils.getUserFullName(record.getOwnerId().intValue()));
      rd.setTitle(ALCommonUtils.compressString(record.getTitle(),
          getStrLength()));
      rd.setBlogId(record.getEipTBlog().getBlogId().intValue());
      rd.setThemaId(record.getEipTBlogThema().getThemaId().intValue());
      rd.setThemaName(record.getEipTBlogThema().getThemaName());
      rd.setAllowComments("T".equals(record.getAllowComments()));

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日（EE）");
      rd.setTitleDate(sdf.format(record.getCreateDate()));

      List list = record.getEipTBlogComments();
      if (list != null && list.size() > 0) {
        rd.setCommentsNum(list.size());
      }

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
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
  public Object selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj) {
    return null;
  }

  public List getPhotoList() {
    return photoList;
  }

  public int getLoginUid() {
    return uid;
  }

  /**
   * エントリーの総数を返す． <BR>
   *
   * @return
   */
  public int getEntrySum() {
    return entrySum;
  }

  public int getNewEntryId() {
    return newEntryId;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("update", EipTBlogFile.UPDATE_DATE_PROPERTY);
    return map;
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
   * ユーザーがコメントした記事の一覧を返す。
   */
  public List getCommentHistoryList() {
    return commentHistoryList;
  }

  /**
   * TitleDateの新しい順に並び替える。
   *
   * @param type
   * @param name
   * @return
   */
  public static Comparator getDateComparator() {
    Comparator com = null;
    com = new Comparator() {
      public int compare(Object obj0, Object obj1) {
        String date0 = ((BlogEntryResultData) obj0).getTitleDate().toString();
        String date1 = ((BlogEntryResultData) obj1).getTitleDate().toString();
        if (date0.compareTo(date1) < 0) {
          return 1;
        } else if (date0.equals(date1)) {
          return 0;
        } else {
          return -1;
        }
      }
    };
    return com;
  }

  /**
   * 引数dateの日時からday日前の日時を返します。
   *
   * @param date
   * @param day
   */
  public Date reduceDate(Date date, int day) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_MONTH, -day);
    return cal.getTime();
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER;
  }
}
