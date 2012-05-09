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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

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
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineUrl;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.common.ALTimelineManager;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
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
  // private boolean hasAclCategoryList;

  /** 他ユーザーの作成したトピックの編集権限 */
  private boolean hasAclUpdateTopicOthers;

  /** 他ユーザーの作成したトピックの削除権限 */
  private boolean hasAclDeleteTopicOthers;

  private ALStringField target_keyword;

  private List<Object> list;

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

      // 表示するカラムのみデータベースから取得する．
      ResultList<EipTTimeline> list =
        TimelineUtils.getTimelineList(
          uid,
          Arrays.asList(0),
          null,
          current_page,
          getRowsNum());

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
  protected SelectQuery<EipTTimeline> getSelectQuery(RunData rundata,
      Context context) {

    SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);
    query.where(Operations.eq(EipTTimeline.PARENT_ID_PROPERTY, Integer
      .valueOf(0)));

    return query;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
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
      rd.setAppId(record.getAppId());
      rd.setParams(record.getParams());
      rd.setLike(record.isLike());
      rd.setLikeCount(record.getLikeCount());

      int userId = record.getOwnerId().intValue();
      rd.setHasPhoto(false);

      ALTimelineManager manager = ALTimelineManager.getInstance();
      @SuppressWarnings("unchecked")
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

    TimelineResultData rd = (TimelineResultData) getResultData(record);
    TimelineLikeSelectData ls = new TimelineLikeSelectData();
    List<TimelineLikeResultData> likeList =
      ls.getLikeList(record.getTimelineId());
    rd.setLikeList(likeList);

    return rd;
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

  protected Map<Integer, List<TimelineResultData>> getComments(
      List<Integer> parentIds) {
    List<EipTTimeline> list =
      TimelineUtils.getTimelineList(uid, parentIds, "T", -1, -1);
    Map<Integer, List<TimelineResultData>> result =
      new HashMap<Integer, List<TimelineResultData>>(parentIds.size());
    for (EipTTimeline model : list) {
      Integer id = model.getParentId();
      List<TimelineResultData> rdList = result.get(id);
      if (rdList == null) {
        rdList = new ArrayList<TimelineResultData>();
      }
      rdList.add((TimelineResultData) getResultData(model));
      result.put(id, rdList);
    }

    return result;
  }

  protected Map<Integer, List<TimelineResultData>> getActivities(
      List<Integer> parentIds) {
    List<EipTTimeline> list =
      TimelineUtils.getTimelineList(uid, parentIds, "A", -1, -1);

    Map<Integer, List<TimelineResultData>> result =
      new HashMap<Integer, List<TimelineResultData>>(parentIds.size());
    for (EipTTimeline model : list) {
      Integer id = model.getParentId();
      List<TimelineResultData> rdList = result.get(id);
      if (rdList == null) {
        rdList = new ArrayList<TimelineResultData>();
      }
      rdList.add((TimelineResultData) getResultData(model));
      result.put(id, rdList);
    }

    return result;
  }

  protected Map<Integer, List<TimelineUrlResultData>> getUrls(
      List<Integer> parentIds) {
    if (parentIds == null || parentIds.size() == 0) {
      return new HashMap<Integer, List<TimelineUrlResultData>>(parentIds.size());
    }
    SelectQuery<EipTTimelineUrl> query = Database.query(EipTTimelineUrl.class);
    query.where(Operations.in(EipTTimelineUrl.TIMELINE_ID_PROPERTY, parentIds));

    List<EipTTimelineUrl> list = query.fetchList();
    Map<Integer, List<TimelineUrlResultData>> result =
      new HashMap<Integer, List<TimelineUrlResultData>>(parentIds.size());
    for (EipTTimelineUrl model : list) {
      Integer id = model.getTimelineId();
      List<TimelineUrlResultData> rdList = result.get(id);
      if (rdList == null) {
        rdList = new ArrayList<TimelineUrlResultData>();
      }

      TimelineUrlResultData rd = new TimelineUrlResultData();
      rd.initField();
      rd.setTimelineUrlId(model.getTimelineUrlId().longValue());
      rd.setTimelineId(model.getTimelineId().longValue());
      rd.setThumbnail(model.getThumbnail());
      rd.setTitle(model.getTitle());
      rd.setUrl(model.getUrl());
      rd.setBody(model.getBody());
      boolean flag = false;
      if (model.getThumbnail() != null) {
        flag = true;
      }
      rd.setThumbnailFlag(flag);
      rdList.add(rd);

      result.put(id, rdList);
    }

    return result;
  }

  protected Map<Integer, List<FileuploadBean>> getFiles(List<Integer> parentIds) {
    if (parentIds == null || parentIds.size() == 0) {
      return new HashMap<Integer, List<FileuploadBean>>(parentIds.size());
    }
    SelectQuery<EipTTimelineFile> query =
      Database.query(EipTTimelineFile.class);
    query
      .where(Operations.in(EipTTimelineFile.TIMELINE_ID_PROPERTY, parentIds));

    List<EipTTimelineFile> list = query.fetchList();
    Map<Integer, List<FileuploadBean>> result =
      new HashMap<Integer, List<FileuploadBean>>(parentIds.size());
    for (EipTTimelineFile model : list) {
      Integer id = model.getTimelineId();
      List<FileuploadBean> rdList = result.get(id);
      if (rdList == null) {
        rdList = new ArrayList<FileuploadBean>();
      }

      String realname = model.getFileName();
      DataHandler hData = new DataHandler(new FileDataSource(realname));

      FileuploadBean filebean = new FileuploadBean();
      filebean.setFileId(model.getFileId().intValue());
      filebean.setFileName(realname);
      if (hData != null) {
        filebean.setContentType(hData.getContentType());
      }
      filebean.setIsImage(FileuploadUtils.isImage(realname));
      rdList.add(filebean);
      result.put(id, rdList);
    }

    return result;
  }

  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_LIST);
      action.setMode(ALEipConstants.MODE_LIST);

      // 投稿
      ResultList<EipTTimeline> resultList = selectList(rundata, context);
      List<Integer> parentIds = new ArrayList<Integer>(resultList.size());
      for (EipTTimeline model : resultList) {
        parentIds.add(model.getTimelineId());
      }

      // 更新情報
      Map<Integer, List<TimelineResultData>> activitiesMap =
        getActivities(parentIds);

      // コメント
      List<Integer> commentIds = new ArrayList<Integer>();
      commentIds.addAll(parentIds);
      Iterator<List<TimelineResultData>> activitiesIter =
        activitiesMap.values().iterator();
      while (activitiesIter.hasNext()) {
        List<TimelineResultData> next = activitiesIter.next();
        for (TimelineResultData rd : next) {
          commentIds.add(Integer.valueOf((int) rd.getTimelineId().getValue()));
        }
      }

      Map<Integer, List<TimelineResultData>> commentsMap =
        getComments(commentIds);

      // URL
      Map<Integer, List<TimelineUrlResultData>> urlsMap = getUrls(parentIds);

      // ファイル
      Map<Integer, List<FileuploadBean>> filesMap = getFiles(parentIds);

      if (resultList != null) {
        if (resultList.getTotalCount() > 0) {
          setPageParam(resultList.getTotalCount());
        }
        list = new ArrayList<Object>();
        for (EipTTimeline model : resultList) {
          Object object = getResultData(model);
          TimelineResultData rd = (TimelineResultData) object;

          rd.setCoTopicList(commentsMap.get(model.getTimelineId()));
          rd.setCoActivityList(activitiesMap.get(model.getTimelineId()));
          rd.setUrlList(urlsMap.get(model.getTimelineId()));
          rd.setAttachmentFileList(filesMap.get(model.getTimelineId()));
          rd.setReplyCount(rd.getCoTopicList().size());

          List<TimelineResultData> coac = rd.getCoActivityList();

          // 権限のあるアクティビティのみ表示する

          for (Iterator<TimelineResultData> iter = coac.iterator(); iter
            .hasNext();) {
            TimelineResultData coac_item = iter.next();
            coac_item.setCoTopicList(commentsMap.get(Integer
              .valueOf((int) coac_item.getTimelineId().getValue())));
            coac_item.setReplyCount(coac_item.getCoTopicList().size());

            SelectQuery<EipTTimelineMap> query_map =
              Database.query(EipTTimelineMap.class);
            Expression exp1 =
              ExpressionFactory.matchExp(
                EipTTimelineMap.EIP_TTIMELINE_PROPERTY,
                coac_item.getTimelineId().getValue());
            query_map.setQualifier(exp1);
            List<EipTTimelineMap> data_map = query_map.fetchList();

            List<String> userlist = new ArrayList<String>();
            for (int j = 0; j < data_map.size(); j++) {
              userlist.add(data_map.get(j).getLoginName());
            }

            if (!(user.getUserId().toString().equals(
              coac_item.getOwnerId().toString())
              || userlist.contains(user.getName().toString()) || userlist
                .contains("-1"))) {
              iter.remove();
            }
          }

          if (rd != null
            && !(rd.getCoActivityList().size() == 0
              && rd.getCoTopicList().size() == 0 && rd.getNote().equals(""))) {
            list.add(rd);
          }
        }
      }
      action.setResultData(this);
      action.putData(rundata, context);
      ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
      return (list != null);
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }

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

  @Override
  public List<Object> getList() {
    return list;
  }

}
