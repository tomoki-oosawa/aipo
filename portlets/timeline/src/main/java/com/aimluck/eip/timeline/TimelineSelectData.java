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

package com.aimluck.eip.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineFile;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineUrl;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALTimelineManager;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムライントピックの検索データを管理するクラスです。 <BR>
 * 
 */
public class TimelineSelectData extends
    ALAbstractSelectData<EipTTimeline, EipTTimeline> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineSelectData.class.getName());

  /** トピックの総数 */
  private int topicSum;

  /** トピックの高さ（通常画面） */
  private int contentHeight;

  /** トピックの高さ（最大化画面） */
  private int contentHeightMax;

  /** スクロールの位置 */
  private int scrollTop;

  /** ログインユーザ ID */
  private int uid;

  private ALBaseUser baseuser;

  private ALEipUser user;

  /** 返信フォーム表示の有無（トピック詳細表示） */
  private final boolean showReplyForm = false;

  /** 閲覧権限の有無 */
  private boolean hasAclCategoryList;

  /** 他ユーザーの作成したトピックの編集権限 */
  private boolean hasAclUpdateTopicOthers;

  /** 他ユーザーの作成したトピックの削除権限 */
  private boolean hasAclDeleteTopicOthers;

  private ALStringField target_keyword;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "update_date");
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_TYPE_STR,
      ALEipConstants.LIST_SORT_TYPE_DESC);

    uid = ALEipUtils.getUserId(rundata);
    baseuser = (ALBaseUser) rundata.getUser();
    user = ALEipUtils.getALEipUser(rundata);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTTimeline> selectList(RunData rundata, Context context) {
    try {
      SelectQuery<EipTTimeline> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      // 表示するカラムのみデータベースから取得する．
      ResultList<EipTTimeline> list = query.getResultList();
      // 件数をセットする．
      topicSum = list.getTotalCount();
      // 表示する高さを調節する
      contentHeight =
        Integer.parseInt(ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p1a-rows"));
      context.put("contentHeight", contentHeight);

      // 表示する高さを調節する
      contentHeightMax =
        Integer.parseInt(ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p2a-rows"));
      context.put("contentHeightMax", contentHeightMax);
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

  private SelectQuery<EipTTimeline> getSelectQuery(RunData rundata,
      Context context) {

    SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTTimeline.PARENT_ID_PROPERTY, Integer
        .valueOf(0));

    // 自分の更新情報は表示させない。
    // and !(自分 and 更新) (!自分 || !更新)
    long loginuserid = ALEipUtils.getALEipUser(rundata).getUserId().getValue();

    Expression exp2 =
      ExpressionFactory
        .noMatchExp(EipTTimeline.OWNER_ID_PROPERTY, loginuserid)
        .orExp(
          ExpressionFactory.noMatchExp(
            EipTTimeline.TIMELINE_TYPE_PROPERTY,
            EipTTimeline.TIMELINE_TYPE_ACTIVITY));
    query.setQualifier(exp1.andExp(exp2));

    query.distinct(true);

    List<Integer> resultid = new ArrayList<Integer>();
    List<EipTTimeline> queryList = query.fetchList();
    for (EipTTimeline item : queryList) {
      if (item.getParentId() != 0 && !resultid.contains(item.getParentId())) {
        resultid.add(item.getParentId());
      } else if (!resultid.contains(item.getTimelineId())) {
        resultid.add(item.getTimelineId());
      }
    }
    if (resultid.size() == 0) {
      // 検索結果がないことを示すために-1を代入
      resultid.add(-1);
    }
    Expression ex =
      ExpressionFactory.inDbExp(EipTTimeline.TIMELINE_ID_PK_COLUMN, resultid);
    query.andQualifier(ex);

    return query;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Object getResultData(EipTTimeline record) {
    try {
      TimelineResultData rd = new TimelineResultData();
      rd.initField();
      rd.setTimelineId(record.getTimelineId().longValue());

      rd.setNote(record.getNote());

      rd.setOwnerId(record.getOwnerId().longValue());
      rd.setOwnerName(ALEipUtils
        .getUserFullName(record.getOwnerId().intValue()));

      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());
      rd.setTimelineType(record.getTimelineType());
      rd.setParams(record.getParams());

      rd.setReplyCount(TimelineUtils.countReply(record.getTimelineId()));

      /* 子トピック */
      List<TimelineResultData> coTopicList =
        new ArrayList<TimelineResultData>();

      SelectQuery<EipTTimeline> query =
        getSelectQueryForCotopic(record.getTimelineId().toString());

      query.orderAscending(EipTTimeline.CREATE_DATE_PROPERTY);

      List<EipTTimeline> aList = query.fetchList();
      if (aList != null) {
        for (EipTTimeline coTopic : aList) {
          coTopicList.add((TimelineResultData) getResultData(coTopic));
        }
      }

      rd.setCoTopicList(coTopicList);

      /* いいね */
      TimelineLikeSelectData ls = new TimelineLikeSelectData();
      List<TimelineLikeResultData> likeList =
        ls.getLikeList(record.getTimelineId());

      rd.setLikeList(likeList);

      for (TimelineLikeResultData like : likeList) {
        if ((int) like.getUserId().getValue() == uid) {
          rd.setLike(true);
        }
      }

      /* URL */
      TimelineUrlSelectData us = new TimelineUrlSelectData();
      List<TimelineUrlResultData> urlList =
        us.getUrlList(record.getTimelineId());

      rd.setUrlList(urlList);

      int userId = record.getOwnerId().intValue();
      rd.setHasPhoto(false);

      ALTimelineManager manager = ALTimelineManager.getInstance();
      List<TimelineUserResultData> userDataList =
        (List<TimelineUserResultData>) manager.getUserDataList();

      if (userDataList == null) {
        userDataList = TimelineUtils.getTimelineUserResultDataList("LoginUser");
        manager.setUserDataList(userDataList);
      }
      for (TimelineUserResultData userData : userDataList) {
        if (userId == userData.getUserId().getValue() && userData.hasPhoto()) {
          rd.setHasPhoto(true);
          break;
        }
      }

      // ファイルリスト
      List<EipTTimelineFile> list =
        TimelineUtils
          .getSelectQueryForFiles(record.getTimelineId().intValue())
          .fetchList();
      if (list != null && list.size() > 0) {
        List<FileuploadBean> attachmentFileList =
          new ArrayList<FileuploadBean>();
        FileuploadBean filebean = null;
        for (EipTTimelineFile file : list) {
          String realname = file.getFileName();
          javax.activation.DataHandler hData =
            new javax.activation.DataHandler(
              new javax.activation.FileDataSource(realname));

          filebean = new FileuploadBean();
          filebean.setFileId(file.getFileId().intValue());
          filebean.setFileName(realname);
          if (hData != null) {
            filebean.setContentType(hData.getContentType());
          }
          filebean.setIsImage(FileuploadUtils.isImage(realname));
          attachmentFileList.add(filebean);
        }
        rd.setAttachmentFileList(attachmentFileList);
      }

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTTimeline record)
      throws ALPageNotFoundException, ALDBErrorException {
    return getResultData(record);
  }

  @Override
  public EipTTimeline selectDetail(RunData rundata, Context context)
      throws ALDBErrorException, ALPageNotFoundException {
    try {
      EipTTimeline timeline =
        TimelineUtils.getEipTTimelineParentEntry(rundata, context);
      return timeline;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  private SelectQuery<EipTTimeline> getSelectQueryForCotopic(String topicid) {
    SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTTimeline.PARENT_ID_PROPERTY, Integer
        .valueOf(topicid));
    query.setQualifier(exp);
    query.distinct(true);
    return query;
  }

  private SelectQuery<EipTTimelineUrl> getSelectQueryForUrl(String topicid) {
    SelectQuery<EipTTimelineUrl> query = Database.query(EipTTimelineUrl.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTTimelineUrl.TIMELINE_ID_PROPERTY, Integer
        .valueOf(topicid));
    query.setQualifier(exp);
    query.distinct(true);
    return query;
  }

  /**
   * トピックの総数を返す． <BR>
   * 
   * @return
   */
  public int getTopicSum() {
    return topicSum;
  }

  /**
   * トピックの総数を返す． <BR>
   * 
   * @return
   */
  public int getContentHeight() {
    return contentHeight;
  }

  /**
   * トピックの総数を返す． <BR>
   * 
   * @return
   */
  public int getContentHeightMax() {
    return contentHeightMax;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("update_date", EipTTimeline.UPDATE_DATE_PROPERTY);
    map.putValue("owner_name", EipTTimeline.OWNER_ID_PROPERTY);

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

  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  public int getUserId() {
    return uid;
  }

  public boolean showReplyForm() {
    return showReplyForm;
  }

  /**
   * 他ユーザのトピックを編集する権限があるかどうかを返します。
   * 
   * @return
   */
  public boolean hasAclUpdateTopicOthers() {
    return hasAclUpdateTopicOthers;
  }

  /**
   * 他ユーザのトピックを削除する権限があるかどうかを返します。
   * 
   * @return
   */
  public boolean hasAclDeleteTopicOthers() {
    return hasAclDeleteTopicOthers;
  }

  public String getCurrentSearchWithSanitize() {
    return ALStringUtil.sanitizing(getCurrentSearch());
  }

  /**
   * @return target_keyword
   */
  public ALStringField getTargetKeyword() {
    return target_keyword;
  }

  /**
   * @return scrollTop
   */
  public int getScrollTop() {
    return scrollTop;
  }

  /**
   * @param scrollTop
   *          セットする scrollTop
   */
  public void setScrollTop(int scrollTop) {
    this.scrollTop = scrollTop;
  }

  public boolean hasMyPhoto() {
    if (baseuser == null) {
      return false;
    }
    return baseuser.getPhoto() != null;
  }

  public ALEipUser getMyUser() {
    return user;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isAdmin() {
    return ALEipUtils.isAdmin(uid);
  }

}
