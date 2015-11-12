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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;

import javax.imageio.ImageIO;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractMultiFilterSelectData;
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
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログエントリー検索データを管理するクラスです。 <BR>
 * 
 */
public class BlogEntryLatestSelectData extends
    ALAbstractMultiFilterSelectData<EipTBlogEntry, EipTBlogEntry> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogEntryLatestSelectData.class.getName());

  /** テーマ一覧 */
  private List<BlogThemaResultData> themaList;

  /** エントリーの総数 */
  private int entrySum;

  private List<BlogFileResultData> photoList;

  private int uid;

  /** 新着コメントがついたエントリー ID */
  private int newEntryId;

  /** ユーザーがコメントした記事の一覧 */
  private List<BlogEntryResultData> commentHistoryList;

  /** コメントした記事が一覧に表示される日数 */
  private final int DELETE_DATE = 7;

  /** 記事コメント記入履歴の最大数 */
  private final int MAX_COMMENT_HISTORY_COUNT = 20;

  private final List<Integer> users = new ArrayList<Integer>();

  /** 部署一覧 */
  private List<ALEipGroup> postList;

  /** テーマ　ID */
  private String themaId = "";

  /** テーマの初期値を取得する */
  private String filterType = "";

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

    uid = ALEipUtils.getUserId(rundata);

    // My グループの一覧を取得する．
    postList = ALEipUtils.getMyGroups(rundata);

    // テーマの初期値を取得する
    try {
      filterType = rundata.getParameters().getString("filtertype", "");
      if (filterType.equals("thema")) {
        String themaId = rundata.getParameters().getString("filter", "");
        if (!themaId.equals("")) {
          this.themaId = themaId;
        }
      }
    } catch (Exception ex) {
      logger.error("blog", ex);
    }
    super.init(action, rundata, context);
  }

  private void loadPhotos() throws Exception {
    photoList = new ArrayList<BlogFileResultData>();

    // String[] ext = { ".jpg", ".jpeg", ".JPG", ".JPEG" };
    String[] ext = ImageIO.getWriterFormatNames();

    SelectQuery<EipTBlogFile> query = Database.query(EipTBlogFile.class);
    Expression exp01 =
      ExpressionFactory.likeExp(EipTBlogFile.TITLE_PROPERTY, "%" + ext[0]);
    query.setQualifier(exp01);
    for (int i = 1; i < ext.length; i++) {
      Expression exp02 =
        ExpressionFactory.likeExp(EipTBlogFile.TITLE_PROPERTY, "%" + ext[i]);
      query.orQualifier(exp02);
    }

    query.orderDesending(EipTBlogFile.UPDATE_DATE_PROPERTY);
    query.limit(5);
    List<EipTBlogFile> list = query.fetchList();
    if (list != null && list.size() > 0) {
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTBlogFile record = list.get(i);
        BlogFileResultData file = new BlogFileResultData();
        file.initField();
        file.setFileId(record.getFileId().longValue());
        file.setOwnerId(record.getOwnerId().longValue());
        file.setEntryId(record.getEipTBlogEntry().getEntryId().longValue());
        photoList.add(file);
      }
    }
  }

  private void loadCommentHistoryList(RunData rundata) throws Exception {
    commentHistoryList = new ArrayList<BlogEntryResultData>();
    Integer thisUserId = Integer.valueOf(uid);
    Object beforeEntryId = null;

    SelectQuery<EipTBlogComment> comment_query =
      Database.query(EipTBlogComment.class);
    // ユーザーがコメントした記事のリストをEntryId順に作成
    Expression exp1 =
      ExpressionFactory.matchExp(EipTBlogComment.OWNER_ID_PROPERTY, thisUserId);
    comment_query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.greaterExp(
        EipTBlogComment.UPDATE_DATE_PROPERTY,
        reduceDate(Calendar.getInstance().getTime(), DELETE_DATE));
    comment_query.andQualifier(exp2);
    comment_query.orderAscending("eipTBlogEntry");
    List<EipTBlogComment> aList = comment_query.fetchList();

    // リストからcommentHistoryListを作成する
    for (EipTBlogComment record : aList) {
      try {
        EipTBlogEntry entry = record.getEipTBlogEntry();
        if (entry != null) {
          if (entry.getOwnerId().equals(thisUserId)) {
            continue;
          }
          if (entry.getEntryId().equals(beforeEntryId)) {
            continue;
          } else {
            beforeEntryId = entry.getEntryId();
          }
          BlogEntryResultData rd = new BlogEntryResultData();
          rd.initField();
          rd.setEntryId(entry.getEntryId().longValue());
          rd.setOwnerId(entry.getOwnerId().longValue());
          rd.setTitle(ALCommonUtils.compressString(
            entry.getTitle(),
            getStrLength()));
          rd.setTitleDate(record.getUpdateDate());

          SelectQuery<EipTBlogComment> cquery =
            Database.query(EipTBlogComment.class).select(
              EipTBlogComment.COMMENT_ID_PK_COLUMN);
          Expression cexp =
            ExpressionFactory.matchDbExp(
              EipTBlogComment.EIP_TBLOG_ENTRY_PROPERTY
                + "."
                + EipTBlogEntry.ENTRY_ID_PK_COLUMN,
              entry.getEntryId());
          cquery.setQualifier(cexp);
          List<EipTBlogComment> list = cquery.fetchList();
          if (list != null && list.size() > 0) {
            rd.setCommentsNum(list.size());
          }
          rd.setThemaId(entry.getEipTBlogThema().getThemaId().intValue());
          rd.setThemaName(entry.getEipTBlogThema().getThemaName());
          commentHistoryList.add(rd);

          if (!users.contains(entry.getOwnerId())) {
            users.add(entry.getOwnerId());
          }
        }
      } catch (Exception e) {
        logger.warn("[loadCommentHistoryList]", e);
      }
    }
    // コメント日時の新しい順に並び替え
    Collections.sort(commentHistoryList, getDateComparator());
    // コメント記入履歴数制限をかける
    if (commentHistoryList.size() > MAX_COMMENT_HISTORY_COUNT) {
      commentHistoryList.subList(
        MAX_COMMENT_HISTORY_COUNT,
        commentHistoryList.size()).clear();
    }
  }

  /**
   * @param rundata
   * @param context
   */
  public void loadThemaList(RunData rundata, Context context) {
    // テーマ一覧
    themaList = BlogUtils.getThemaList(rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTBlogEntry> selectList(RunData rundata, Context context) {
    try {
      loadPhotos();
      loadCommentHistoryList(rundata);

      SelectQuery<EipTBlogEntry> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      query.orderDesending(EipTBlogEntry.CREATE_DATE_PROPERTY);
      ResultList<EipTBlogEntry> list = query.getResultList();
      // エントリーの総数をセットする．
      entrySum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("blog", ex);
      return null;
    }
  }

  /**
   * パラメータをマップに変換します。
   * 
   * @param key
   * @param val
   */
  @Override
  protected void parseFilterMap(String key, String val) {
    super.parseFilterMap(key, val);

    Set<String> unUse = new HashSet<String>();

    for (Entry<String, List<String>> pair : current_filterMap.entrySet()) {
      if (pair.getValue().contains("0")) {
        unUse.add(pair.getKey());
      }
    }
    for (String unusekey : unUse) {
      current_filterMap.remove(unusekey);
    }
  }

  @Override
  protected SelectQuery<EipTBlogEntry> buildSelectQueryForFilter(
      SelectQuery<EipTBlogEntry> query, RunData rundata, Context context) {

    super.buildSelectQueryForFilter(query, rundata, context);

    if (current_filterMap.containsKey("post")) {
      // 部署を含んでいる場合デフォルトとは別にフィルタを用意

      List<String> postIds = current_filterMap.get("post");

      boolean existPost = false;
      for (int i = 0; i < postList.size(); i++) {
        String pid = postList.get(i).getName().toString();
        if (pid.equals(postIds.get(0).toString())) {
          existPost = true;
          break;
        }
      }
      Map<Integer, ALEipPost> map = ALEipManager.getInstance().getPostMap();
      for (Map.Entry<Integer, ALEipPost> item : map.entrySet()) {
        String pid = item.getValue().getGroupName().toString();
        if (pid.equals(postIds.get(0).toString())) {
          existPost = true;
          break;
        }
      }

      if (existPost) {
        HashSet<Integer> userIds = new HashSet<Integer>();
        for (String post : postIds) {
          List<Integer> userId = ALEipUtils.getUserIds(post);
          userIds.addAll(userId);
        }
        if (userIds.isEmpty()) {
          userIds.add(-1);
        }
        Expression exp =
          ExpressionFactory.inExp(EipTBlogEntry.OWNER_ID_PROPERTY, userIds);
        query.andQualifier(exp);
      } else {
        current_filterMap.remove("post");
      }
    }

    String search = ALEipUtils.getTemp(rundata, context, LIST_SEARCH_STR);

    if (search != null && !"".equals(search)) {
      current_search = search;
      Expression ex1 =
        ExpressionFactory.likeExp(EipTBlogEntry.NOTE_PROPERTY, "%"
          + search
          + "%");
      Expression ex2 =
        ExpressionFactory.likeExp(EipTBlogEntry.TITLE_PROPERTY, "%"
          + search
          + "%");
      SelectQuery<EipTBlogEntry> q = Database.query(EipTBlogEntry.class);
      q.andQualifier(ex1.orExp(ex2));
      List<EipTBlogEntry> queryList = q.fetchList();
      List<Integer> resultid = new ArrayList<Integer>();
      for (EipTBlogEntry item : queryList) {
        /*
         * if (item.getParentId() != 0 &&
         * !resultid.contains(item.getParentId())) {
         * resultid.add(item.getParentId()); } else if
         * (!resultid.contains(item.getTopicId())) {
         * resultid.add(item.getTopicId()); }
         */
      }
      if (resultid.size() == 0) {
        // 検索結果がないことを示すために-1を代入
        resultid.add(-1);
      }
      Expression ex =
        ExpressionFactory.inDbExp(EipTBlogEntry.ENTRY_ID_PK_COLUMN, resultid);
      query.andQualifier(ex);
    }
    return query;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTBlogEntry> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTBlogEntry> query = Database.query(EipTBlogEntry.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTBlogEntry record) {
    try {
      BlogEntryResultData rd = new BlogEntryResultData();
      rd.initField();
      rd.setEntryId(record.getEntryId().longValue());
      rd.setOwnerId(record.getOwnerId().longValue());
      rd.setTitle(ALCommonUtils.compressString(
        record.getTitle(),
        getStrLength()));
      rd.setNote(record.getNote().replaceAll("\\r\\n", " ").replaceAll(
        "\\n",
        " ").replaceAll("\\r", " "));
      rd.setBlogId(record.getEipTBlog().getBlogId().intValue());

      if (record.getEipTBlogThema() != null) {
        rd.setThemaId(record.getEipTBlogThema().getThemaId().intValue());
        rd.setThemaName(record.getEipTBlogThema().getThemaName());
      }

      rd.setAllowComments("T".equals(record.getAllowComments()));
      rd.setTitleDate(record.getCreateDate());

      List<?> list = record.getEipTBlogComments();
      if (list != null && list.size() > 0) {
        rd.setCommentsNum(list.size());
      }

      if (!users.contains(record.getOwnerId())) {
        users.add(record.getOwnerId());
      }

      return rd;
    } catch (Exception ex) {
      logger.error("blog", ex);
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
  public EipTBlogEntry selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTBlogEntry obj) {
    return null;
  }

  public List<BlogFileResultData> getPhotoList() {
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

  /**
   * @return themaList
   */
  public List<BlogThemaResultData> getThemaList() {
    return themaList;
  }

  public int getNewEntryId() {
    return newEntryId;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("thema", EipTBlogThema.THEMA_ID_PK_COLUMN);
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
  public List<BlogEntryResultData> getCommentHistoryList() {
    return commentHistoryList;
  }

  /**
   * TitleDateの新しい順に並び替える。
   * 
   * @param type
   * @param name
   * @return
   */
  public static Comparator<BlogEntryResultData> getDateComparator() {
    Comparator<BlogEntryResultData> com = null;
    com = new Comparator<BlogEntryResultData>() {
      @Override
      public int compare(BlogEntryResultData obj0, BlogEntryResultData obj1) {
        Date date0 = (obj0).getTitleDate().getValue();
        Date date1 = (obj1).getTitleDate().getValue();
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
   * 日付文字列をjava.util.Date型へ変換します。
   * 
   * @param str
   *          変換対象の文字列
   * @return 変換後のjava.util.Dateオブジェクト
   */
  public static Date toDate(String str) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日(EE)");
      // parseメソッドでDate型に変換します。
      Date date = sdf.parse(str);
      return date;
    } catch (Exception ex) {
      logger.error("blog", ex);
      return null;
    }
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
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER;
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

  /**
   * 部署一覧を取得します
   * 
   * @return postList
   */
  public List<ALEipGroup> getPostList() {
    return postList;
  }

  /**
   * 部署の一覧を取得する．
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public void setFiltersPSML(VelocityPortlet portlet, Context context,
      RunData rundata) {
    ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12f-filters"));

    ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12g-filtertypes"));
  }

  public String getThemaId() {
    return themaId;
  }
}
