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

package com.aimluck.eip.blog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.blog.util.calendar.CalendarElement;
import com.aimluck.eip.blog.util.calendar.Day;
import com.aimluck.eip.blog.util.calendar.Month;
import com.aimluck.eip.blog.util.calendar.MonthCalendar;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFootmarkMap;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
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
public class BlogEntrySelectData extends
    ALAbstractSelectData<EipTBlogEntry, EipTBlogEntry> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogEntrySelectData.class.getName());

  /** カテゴリ一覧 */
  private List<BlogThemaResultData> themaList;

  /** エントリーの総数 */
  private int entrySum;

  private List<BlogCommentResultData> commentList;

  private List<BlogFootmarkResultData> footmarkList;

  private int uid;

  /** 月カレンダ */
  private Month month;

  /** <code>viewStart</code> 表示開始日時 */
  private ALDateTimeField viewStart;

  /** <code>viewEndCrt</code> 表示終了日時 (Criteria) */
  private ALDateTimeField viewEndCrt;

  /** <code>viewMonth</code> 現在の月 */
  private ALDateTimeField viewMonth;

  /** <code>prevMonth</code> 前の月 */
  private ALDateTimeField prevMonth;

  /** <code>nextMonth</code> 次の月 */
  private ALDateTimeField nextMonth;

  private String viewDay;

  private int view_uid;

  private String view_uname;

  private boolean has_photo;

  private String userAccountURI;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    uid = ALEipUtils.getUserId(rundata);

    ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (rundata.getParameters().containsKey(ALEipConstants.ENTITY_ID)) {
      ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, rundata
        .getParameters()
        .get(ALEipConstants.ENTITY_ID));
    }

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // スケジュールの表示開始日時
      // e.g. 2004-3-14
      if (rundata.getParameters().containsKey("view_month")) {
        String v_month = rundata.getParameters().getString("view_month");
        if ("none".equals(v_month)) {
          ALEipUtils.removeTemp(rundata, context, "view_month");
        } else {
          ALEipUtils.setTemp(rundata, context, "view_month", v_month);
          ALEipUtils.removeTemp(rundata, context, "view_day");
        }
      }

      if (rundata.getParameters().containsKey("view_day")) {
        ALEipUtils.setTemp(rundata, context, "view_day", rundata
          .getParameters()
          .getString("view_day"));
      }

    }

    // POST/GET から yyyy-MM の形式で受け渡される。
    // 現在の月
    viewMonth = new ALDateTimeField("yyyy-MM");
    // 前の月
    prevMonth = new ALDateTimeField("yyyy-MM");
    // 次の月
    nextMonth = new ALDateTimeField("yyyy-MM");

    // 表示開始日時
    viewStart = new ALDateTimeField("yyyy-MM-dd");
    // 表示終了日時 (Criteria)
    viewEndCrt = new ALDateTimeField("yyyy-MM-dd");

    // 現在の月
    String tmpViewMonth = ALEipUtils.getTemp(rundata, context, "view_month");
    if (tmpViewMonth == null || tmpViewMonth.equals("")) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DATE, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      viewMonth.setValue(cal.getTime());
    } else {
      viewMonth.setValue(tmpViewMonth);
      if (!viewMonth.validate(new ArrayList<String>())) {
        ALEipUtils.removeTemp(rundata, context, "view_month");
        throw new ALPageNotFoundException();
      }
    }

    viewDay = ALEipUtils.getTemp(rundata, context, "view_day");

    // 月表示
    // 表示開始日時
    Calendar cal = Calendar.getInstance();
    cal.setTime(viewMonth.getValue());
    cal.set(Calendar.DATE, 1);
    viewStart.setValue(cal.getTime());
    // 表示終了日時
    cal.add(Calendar.MONTH, 1);
    viewEndCrt.setValue(cal.getTime());

    // 次の月、前の月
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(viewMonth.getValue());
    cal2.add(Calendar.MONTH, 1);
    nextMonth.setValue(cal2.getTime());
    cal2.add(Calendar.MONTH, -2);
    prevMonth.setValue(cal2.getTime());

    // ブログカレンダーをロードする．
    loadMonthCalendar();

    // ポートレット AccountPerson のへのリンクを取得する．
    userAccountURI =
      BlogUtils.getPortletURIinPersonalConfigPane(rundata, "AccountPerson");

    super.init(action, rundata, context);

    EipTBlogEntry record = BlogUtils.getEipTBlogEntry(rundata, context);
    if (record != null) {
      view_uid = record.getOwnerId();
    } else {
      if (rundata.getParameters().containsKey("view_uid")) {
        view_uid =
          Integer.parseInt(rundata.getParameters().getString("view_uid"));
      } else {
        view_uid = uid;
      }
    }
    ALEipUtils.setTemp(rundata, context, "view_uid", String.valueOf(view_uid));

    // 顔写真の取得
    ALBaseUser user = BlogUtils.getBaseUser(view_uid);
    if (user.getPhoto() != null) {
      has_photo = true;
    } else {
      has_photo = false;
    }

    ALEipUser view_user = ALEipUtils.getALEipUser(view_uid);
    view_uname = view_user.getAliasName().getValue();

    // アクセス権
    if (view_uid == uid) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_SELF;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadThemaList(RunData rundata, Context context) {
    // テーマ一覧
    themaList = BlogUtils.getThemaList(rundata, context);
  }

  private void loadMonthCalendar() {
    MonthCalendar c = new MonthCalendar();
    month =
      c.createCalendar(Integer.parseInt(viewMonth.getYear()), Integer
        .parseInt(viewMonth.getMonth()));
  }

  private void loadFootmark(EipTBlog blog) throws Exception {
    footmarkList = new ArrayList<BlogFootmarkResultData>();

    SelectQuery<EipTBlogFootmarkMap> query =
      Database.query(EipTBlogFootmarkMap.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTBlogFootmarkMap.BLOG_ID_PROPERTY, blog
        .getBlogId());
    query.setQualifier(exp);
    query.orderDesending(EipTBlogFootmarkMap.UPDATE_DATE_PROPERTY);
    query.limit(10);
    List<EipTBlogFootmarkMap> list = query.fetchList();

    if (list != null && list.size() > 0) {
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTBlogFootmarkMap record = list.get(i);
        BlogFootmarkResultData footmark = new BlogFootmarkResultData();
        footmark.initField();
        footmark.setUserId(record.getUserId().longValue());
        footmark.setUserName(BlogUtils.getUserFullName(record
          .getUserId()
          .intValue()));

        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
        footmark.setUpdateDate(format.format(record.getUpdateDate()));
        footmarkList.add(footmark);
      }

    }
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
      SelectQuery<EipTBlogEntry> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      query.orderDesending(EipTBlogEntry.CREATE_DATE_PROPERTY);

      ResultList<EipTBlogEntry> list = query.getResultList();
      // エントリーの総数をセットする．
      entrySum = list.getTotalCount();

      EipTBlog blog = null;
      if (list != null && list.size() > 0) {
        EipTBlogEntry record = list.get(0);
        blog = record.getEipTBlog();
      } else {
        blog = getBlog(view_uid);
      }

      if (uid != view_uid) {
        // 他ユーザのブログにあしあとを残す
        footmark(rundata, blog);
      } else {
        // 自分のブログのあしあとの一覧を取得する
        loadFootmark(blog);
      }

      // 左メニューカレンダーの構築
      setupDetailCalendar(rundata, context);

      return list;
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
  private SelectQuery<EipTBlogEntry> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTBlogEntry> query = Database.query(EipTBlogEntry.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTBlogEntry.OWNER_ID_PROPERTY, Integer
        .valueOf(view_uid));
    query.setQualifier(exp1);

    // 月毎の記事表示
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTBlogEntry.CREATE_DATE_PROPERTY,
        viewStart.getValue());
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(
        EipTBlogEntry.CREATE_DATE_PROPERTY,
        viewEndCrt.getValue());
    query.andQualifier(exp11.andExp(exp12));

    if (viewDay != null) {
      // 選択された日の記事表示
      ALDateTimeField tmpViewDay = new ALDateTimeField("yyyy-MM-dd");
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, Integer.parseInt(viewMonth.getYear()));
      cal.set(Calendar.MONTH, Integer.parseInt(viewMonth.getMonth()) - 1);
      cal.set(Calendar.DATE, Integer.parseInt(viewDay));
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      tmpViewDay.setValue(cal.getTime());

      Expression exp21 =
        ExpressionFactory.greaterOrEqualExp(
          EipTBlogEntry.CREATE_DATE_PROPERTY,
          tmpViewDay.getValue());

      cal.set(Calendar.DATE, Integer.valueOf(viewDay) + 1);
      tmpViewDay.setValue(cal.getTime());
      Expression exp22 =
        ExpressionFactory.lessExp(
          EipTBlogEntry.CREATE_DATE_PROPERTY,
          tmpViewDay.getValue());

      query.andQualifier(exp21.andExp(exp22));
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTBlogEntry> getSelectQueryForCalendar(RunData rundata,
      Context context) {
    SelectQuery<EipTBlogEntry> query =
      Database.query(EipTBlogEntry.class).select(
        EipTBlogEntry.CREATE_DATE_COLUMN);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTBlogEntry.OWNER_ID_PROPERTY, Integer
        .valueOf(view_uid));
    query.setQualifier(exp1);

    // 月毎の記事表示
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTBlogEntry.CREATE_DATE_PROPERTY,
        viewStart.getValue());
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(
        EipTBlogEntry.CREATE_DATE_PROPERTY,
        viewEndCrt.getValue());
    query.andQualifier(exp11.andExp(exp12));

    return query;
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
      rd.setNote(BlogUtils.compressString(record.getNote(), 100));
      rd.setBlogId(record.getEipTBlog().getBlogId().longValue());
      rd.setThemaId(record.getEipTBlogThema().getThemaId().longValue());
      rd.setThemaName(ALCommonUtils.compressString(record
        .getEipTBlogThema()
        .getThemaName(), getStrLength()));
      rd.setAllowComments("T".equals(record.getAllowComments()));

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日（EE）");
      rd.setTitleDate(sdf.format(record.getCreateDate()));
      SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
      rd.setDay(Integer.parseInt((sdf2.format(record.getCreateDate()))));

      SelectQuery<EipTBlogComment> query =
        Database.query(EipTBlogComment.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTBlogComment.EIP_TBLOG_ENTRY_PROPERTY
          + "."
          + EipTBlogEntry.ENTRY_ID_PK_COLUMN, record.getEntryId());
      query.setQualifier(exp);
      List<EipTBlogComment> list = query.fetchList();
      if (list != null && list.size() > 0) {
        rd.setCommentsNum(list.size());
      }

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  @SuppressWarnings("unused")
  private boolean containsSelectedView(String viewDay, int day) {
    String dayStr = "" + day;
    if (viewDay != null && !"".equals(viewDay)) {
      if (dayStr.equals(viewDay)) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  private void setBlogEntryToMonthCalendar(Date date) {
    CalendarElement element = null;
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    if ((cal.get(Calendar.MONTH) + 1) != month.getMonth()) {
      return;
    }

    String d_day = "" + cal.get(Calendar.DATE);
    int size = month.size();
    for (int i = 0; i < size; i++) {
      element = month.get(i);
      if (d_day.equals(element.getLabel())) {
        ((Day) element).setBlogEntry(true);
      }
    }
  }

  /**
   * ブログIDを取得する
   * 
   * @param view_uid
   */
  private EipTBlog getBlog(int view_uid) throws Exception {
    SelectQuery<EipTBlog> query = Database.query(EipTBlog.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTBlog.OWNER_ID_PROPERTY, Integer
        .valueOf(view_uid));
    query.setQualifier(exp);
    List<EipTBlog> list = query.fetchList();
    if (list == null || list.size() <= 0) {
      // 新規オブジェクトモデル
      EipTBlog blog = Database.create(EipTBlog.class);
      // ユーザーID
      blog.setOwnerId(Integer.valueOf(view_uid));
      // 作成日
      blog.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      blog.setUpdateDate(Calendar.getInstance().getTime());
      // ブログを登録
      Database.commit();
      return blog;
    } else {
      EipTBlog blog = list.get(0);
      return blog;
    }
  }

  /**
   * あしあと機能
   * 
   */
  private void footmark(RunData rundata, EipTBlog blog) throws Exception {
    if (blog.getOwnerId().intValue() == ALEipUtils.getUserId(rundata)) {
      // ログインユーザーのブログには足跡を残さない
      return;
    }

    ALDateTimeField today = new ALDateTimeField("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    today.setValue(cal.getTime());

    SelectQuery<EipTBlogFootmarkMap> query =
      Database.query(EipTBlogFootmarkMap.class);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTBlogFootmarkMap.BLOG_ID_PROPERTY, blog
        .getBlogId());
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTBlogFootmarkMap.USER_ID_PROPERTY, Integer
        .valueOf(uid));
    query.andQualifier(exp2);
    Expression exp3 =
      ExpressionFactory.matchExp(
        EipTBlogFootmarkMap.CREATE_DATE_PROPERTY,
        today.getValue());
    query.andQualifier(exp3);

    List<EipTBlogFootmarkMap> list = query.fetchList();
    if (list == null || list.size() <= 0) {
      // あしあとを登録する
      EipTBlogFootmarkMap footmark = Database.create(EipTBlogFootmarkMap.class);
      footmark.setEipTBlog(blog);
      footmark.setUserId(Integer.valueOf(uid));
      footmark.setCreateDate(Calendar.getInstance().getTime());
      footmark.setUpdateDate(Calendar.getInstance().getTime());
      Database.commit();
    } else {
      // あしあとを更新する
      EipTBlogFootmarkMap footmark = list.get(0);
      footmark.setUpdateDate(Calendar.getInstance().getTime());
      Database.commit();
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

    try {
      setupDetailCalendar(rundata, context);

      EipTBlogEntry obj = BlogUtils.getEipTBlogEntry(rundata, context);

      if (obj != null) {
        EipTBlog blog = null;
        EipTBlogEntry record = obj;

        blog = record.getEipTBlog();

        if (uid != view_uid) {
          try {
            footmark(rundata, blog);
          } catch (Exception e) {
          }
        } else {
          // 自分のブログのあしあとの一覧を取得する
          loadFootmark(record.getEipTBlog());
        }
      }

      return obj;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  private void setupDetailCalendar(RunData rundata, Context context) {
    try {
      SelectQuery<EipTBlogEntry> query =
        getSelectQueryForCalendar(rundata, context);
      query.orderDesending(EipTBlogEntry.UPDATE_DATE_PROPERTY);

      List<EipTBlogEntry> list = query.fetchList();

      for (EipTBlogEntry entry : list) {
        setBlogEntryToMonthCalendar(entry.getCreateDate());
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTBlogEntry record) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日（EE） HH時mm分");
      BlogEntryResultData rd = new BlogEntryResultData();
      rd.initField();
      rd.setEntryId(record.getEntryId().longValue());
      rd.setOwnerId(record.getOwnerId().longValue());
      rd
        .setOwnerName(BlogUtils.getUserFullName(record.getOwnerId().intValue()));
      rd.setTitle(record.getTitle());
      rd.setNote(record.getNote());
      rd.setBlogId(record.getEipTBlog().getBlogId().longValue());
      rd.setThemaId(record.getEipTBlogThema().getThemaId().intValue());
      rd.setThemaName(record.getEipTBlogThema().getThemaName());
      rd.setAllowComments("T".equals(record.getAllowComments()));
      rd.setCreateDate(sdf.format(record.getCreateDate()));
      rd.setCreateDateAlternative(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());

      commentList = new ArrayList<BlogCommentResultData>();

      SelectQuery<EipTBlogComment> query =
        Database.query(EipTBlogComment.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTBlogComment.EIP_TBLOG_ENTRY_PROPERTY
          + "."
          + EipTBlogEntry.ENTRY_ID_PK_COLUMN, record.getEntryId());
      query.orderAscending(EipTBlogComment.UPDATE_DATE_PROPERTY);
      query.setQualifier(exp);
      List<EipTBlogComment> comments = query.fetchList();

      if (comments != null && comments.size() > 0) {
        int size = comments.size();
        for (int i = 0; i < size; i++) {
          EipTBlogComment blogcomment = comments.get(i);
          BlogCommentResultData comment = new BlogCommentResultData();
          comment.initField();
          comment.setCommentId(blogcomment.getCommentId().longValue());
          comment.setOwnerId(blogcomment.getOwnerId().longValue());
          comment.setOwnerName(BlogUtils.getUserFullName(blogcomment
            .getOwnerId()
            .intValue()));
          comment.setComment(blogcomment.getComment());
          comment.setEntryId(blogcomment
            .getEipTBlogEntry()
            .getEntryId()
            .longValue());
          comment.setUpdateDate(sdf.format(blogcomment.getUpdateDate()));
          comment.setUpdateDateAlternative(blogcomment.getUpdateDate());

          commentList.add(comment);
        }
      }

      SelectQuery<EipTBlogFile> filequery = Database.query(EipTBlogFile.class);
      Expression fileexp =
        ExpressionFactory.matchDbExp(EipTBlogFile.EIP_TBLOG_ENTRY_PROPERTY
          + "."
          + EipTBlogEntry.ENTRY_ID_PK_COLUMN, record.getEntryId());
      filequery.setQualifier(fileexp);
      List<EipTBlogFile> files = filequery.fetchList();

      if (files != null && files.size() > 0) {
        List<FileuploadBean> attachmentFileList =
          new ArrayList<FileuploadBean>();
        FileuploadBean filebean = null;
        int size = files.size();
        for (int i = 0; i < size; i++) {
          EipTBlogFile file = files.get(i);

          String realname = file.getTitle();
          javax.activation.DataHandler hData =
            new javax.activation.DataHandler(
              new javax.activation.FileDataSource(realname));

          filebean = new FileuploadBean();
          filebean.setFileId(file.getFileId());
          filebean.setFileName(realname);
          if (hData != null) {
            filebean.setContentType(hData.getContentType());
          }
          filebean.setIsImage(FileuploadUtils.isImage(realname));
          attachmentFileList.add(filebean);
        }
        rd.setAttachmentFiles(attachmentFileList);
      }

      if (record.getOwnerId().intValue() == uid) {
        record.setUpdateDate(Calendar.getInstance().getTime());
        Database.commit();
      }
      return rd;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 
   * @return
   */
  public List<BlogThemaResultData> getThemaList() {
    return themaList;
  }

  public int getLoginUid() {
    return uid;
  }

  public int getViewUid() {
    return view_uid;
  }

  public String getViewUname() {
    return view_uname;
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
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("thema", EipTBlogThema.THEMA_ID_PK_COLUMN);
    return map;
  }

  public List<BlogCommentResultData> getCommentList() {
    return commentList;
  }

  public List<BlogFootmarkResultData> getFootmarkList() {
    return footmarkList;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public Month getMonth() {
    return month;
  }

  /**
   * 現在の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getViewMonth() {
    return viewMonth;
  }

  /**
   * 前の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getPrevMonth() {
    return prevMonth;
  }

  /**
   * 次の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getNextMonth() {
    return nextMonth;
  }

  public int getUserId() {
    return uid;
  }

  public boolean hasPhoto() {
    return has_photo;
  }

  public String getUserAccountURI() {
    return userAccountURI;
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
