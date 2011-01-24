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

package com.aimluck.eip.msgboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategoryMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.msgboard.util.MsgboardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板トピックの検索データを管理するクラスです。 <BR>
 * 
 */
public class MsgboardTopicSelectData extends
    ALAbstractSelectData<EipTMsgboardTopic, EipTMsgboardTopic> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService
      .getLogger(MsgboardTopicSelectData.class.getName());

  /** カテゴリ一覧 */
  private List<MsgboardCategoryResultData> categoryList;

  /** トピックの総数 */
  private int topicSum;

  /** 親トピックオブジェクト */
  private Object parentTopic;

  /** 子トピックオブジェクト */
  private List<MsgboardTopicResultData> coTopicList;

  /** ログインユーザ ID */
  private int uid;

  /** 返信フォーム表示の有無（トピック詳細表示） */
  private boolean showReplyForm = false;

  /** 閲覧権限の有無 */
  private boolean hasAclCategoryList;

  /** 他ユーザーの作成したトピックの編集権限 */
  private boolean hasAclUpdateTopicOthers;

  /** 他ユーザーの作成したトピックの削除権限 */
  private boolean hasAclDeleteTopicOthers;

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

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      String sortStr =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p2a-sort");
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, sortStr);
      if ("update_date".equals(sortStr)) {
        ALEipUtils.setTemp(rundata, context, LIST_SORT_TYPE_STR, "desc");
      }

      logger.debug("[MsgboardTopicSelectData] Init Parameter. : "
        + ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p2a-sort"));
    }

    uid = ALEipUtils.getUserId(rundata);

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAclCategoryList =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY,
        ALAccessControlConstants.VALUE_ACL_LIST);

    hasAclDeleteTopicOthers =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);

    hasAclUpdateTopicOthers =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    // カテゴリの初期値を取得する
    try {
      String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
      if (filter == null) {
        VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
        String categoryId =
          portlet.getPortletConfig().getInitParameter("p3a-category");
        if (categoryId != null) {
          ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, categoryId);
          ALEipUtils
            .setTemp(rundata, context, LIST_FILTER_TYPE_STR, "category");
        }
      }
    } catch (Exception ex) {
      logger.debug("Exception", ex);
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata, Context context) {
    // カテゴリ一覧
    categoryList = MsgboardUtils.loadCategoryList(rundata);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTMsgboardTopic> selectList(RunData rundata,
      Context context) {

    try {
      SelectQuery<EipTMsgboardTopic> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      // 表示するカラムのみデータベースから取得する．
      ResultList<EipTMsgboardTopic> list = query.getResultList();
      // 件数をセットする．
      topicSum = list.getTotalCount();
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
  private SelectQuery<EipTMsgboardTopic> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTMsgboardTopic> query =
      Database.query(EipTMsgboardTopic.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTMsgboardTopic.PARENT_ID_PROPERTY, Integer
        .valueOf(0));
    query.setQualifier(exp1);

    // アクセス制御
    Expression exp01 =
      ExpressionFactory.matchExp(
        EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
        "T");

    Expression exp02 =
      ExpressionFactory.matchExp(
        EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
          + "."
          + EipTMsgboardCategoryMap.STATUS_PROPERTY,
        "O");
    Expression exp03 =
      ExpressionFactory.matchExp(
        EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
          + "."
          + EipTMsgboardCategoryMap.STATUS_PROPERTY,
        "A");
    Expression exp11 =
      ExpressionFactory.matchExp(
        EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
        "F");
    Expression exp12 =
      ExpressionFactory.matchExp(
        EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
          + "."
          + EipTMsgboardCategoryMap.USER_ID_PROPERTY,
        Integer.valueOf(ALEipUtils.getUserId(rundata)));
    query.andQualifier((exp01.andExp(exp02.orExp(exp03))).orExp(exp11
      .andExp(exp12)));
    query.distinct(true);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTMsgboardTopic record) {
    try {
      MsgboardTopicResultData rd = new MsgboardTopicResultData();
      rd.initField();
      rd.setTopicId(record.getTopicId().longValue());
      rd.setTopicName(ALCommonUtils.compressString(
        record.getTopicName(),
        getStrLength()));
      rd.setCategoryId(record
        .getEipTMsgboardCategory()
        .getCategoryId()
        .longValue());
      rd.setCategoryName(ALCommonUtils.compressString(record
        .getEipTMsgboardCategory()
        .getCategoryName(), getStrLength()));

      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record
        .getEipTMsgboardCategory()
        .getPublicFlag()));
      rd.setOwnerId(record.getOwnerId().longValue());
      rd.setOwnerName(ALEipUtils
        .getUserFullName(record.getOwnerId().intValue()));
      rd.setUpdateUser(ALEipUtils.getUserFullName(record
        .getUpdateUserId()
        .intValue()));
      rd.setUpdateDate(record.getUpdateDate());

      // 新着を設定する（期限は最終更新日からの 1 日間）．
      Date date = record.getUpdateDate();
      Calendar now = Calendar.getInstance();
      now.add(Calendar.DATE, -1);
      rd.setNewTopicFlag(date.after(now.getTime()));
      rd.setReplyCount(MsgboardUtils.countReply(record.getTopicId()));

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細表示します。
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FASLE 失敗
   */
  @Override
  public boolean doViewDetail(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_DETAIL);
      action.setMode(ALEipConstants.MODE_DETAIL);
      List<EipTMsgboardTopic> aList = selectDetailList(rundata, context);
      if (aList != null) {
        coTopicList = new ArrayList<MsgboardTopicResultData>();
        int size = aList.size();
        for (int i = 0; i < size; i++) {
          coTopicList.add((MsgboardTopicResultData) getResultDataDetail(aList
            .get(i)));
        }
      }

      action.setResultData(this);
      action.putData(rundata, context);
      return true;
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
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public List<EipTMsgboardTopic> selectDetailList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    String topicid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      /**
       * 新着ポートレット既読処理
       */
      MsgboardUtils.shiftWhatsNewReadFlag(rundata, Integer.parseInt(topicid));
      /**
       * 
       */

    } catch (Exception e) {
      logger.error("[MsgboardTopicSelectData]", e);
    }

    if (topicid == null || Integer.valueOf(topicid) == null) {
      // トピック ID が空の場合
      logger.debug("[MsgboardTopic] Empty ID...");
      throw new ALPageNotFoundException();
    }

    String cotopicsort =
      ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2b-sort");

    try {
      parentTopic =
        getResultDataDetail(MsgboardUtils.getEipTMsgboardParentTopic(
          rundata,
          context,
          false));

      SelectQuery<EipTMsgboardTopic> query =
        getSelectQueryForCotopic(rundata, context, topicid, cotopicsort);
      /** 詳細画面は全件表示する */
      // buildSelectQueryForListView(query);
      if ("response_new".equals(cotopicsort)) {
        query.orderDesending(EipTMsgboardTopic.CREATE_DATE_PROPERTY);
      } else {
        query.orderAscending(EipTMsgboardTopic.CREATE_DATE_PROPERTY);
      }

      // 表示するカラムのみデータベースから取得する．
      return query.fetchList();
    } catch (Exception ex) {
      logger.error("[MsgboardTopicSelectData]", ex);
      throw new ALDBErrorException();
    }
  }

  @Override
  public EipTMsgboardTopic selectDetail(RunData rundata, Context context) {
    ALEipUtils.redirectPageNotFound(rundata);
    return null;
  }

  private SelectQuery<EipTMsgboardTopic> getSelectQueryForCotopic(
      RunData rundata, Context context, String topicid, String cotopicsort) {
    SelectQuery<EipTMsgboardTopic> query =
      Database.query(EipTMsgboardTopic.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTMsgboardTopic.PARENT_ID_PROPERTY, Integer
        .valueOf(topicid));
    query.setQualifier(exp);
    query.distinct(true);
    return query;
  }

  private SelectQuery<EipTMsgboardFile> getSelectQueryForFiles(int topicid) {
    SelectQuery<EipTMsgboardFile> query =
      Database.query(EipTMsgboardFile.class);
    Expression exp =
      ExpressionFactory.matchDbExp(
        EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
        Integer.valueOf(topicid));
    query.setQualifier(exp);
    return query;
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTMsgboardTopic record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      // 親トピックのアクセス権限をもとに，返信フォーム表示の有無を決定する．
      if (record.getParentId().intValue() == 0) {
        EipTMsgboardCategory category = record.getEipTMsgboardCategory();
        if ("T".equals(category.getPublicFlag())) {
          List<?> categoryMap = category.getEipTMsgboardCategoryMaps();
          int mapsize = categoryMap.size();
          for (int i = 0; i < mapsize; i++) {
            EipTMsgboardCategoryMap map =
              (EipTMsgboardCategoryMap) categoryMap.get(i);
            if ("A".equals(map.getStatus())) {
              showReplyForm = true;
            } else {
              if (map.getUserId().intValue() == uid) {
                // ログインユーザが所属メンバの場合
                showReplyForm = true;
                break;
              }
            }

          }
        } else {
          // 非公開のトピックの場合は，
          // データベースの検索時にアクセスをフィルタリングしている．
          showReplyForm = true;
        }
      }

      MsgboardTopicResultData rd = new MsgboardTopicResultData();
      rd.initField();
      rd.setTopicId(record.getTopicId().longValue());
      rd.setTopicName(record.getTopicName());
      rd.setParentId(record.getParentId().longValue());
      rd.setCategoryId(record
        .getEipTMsgboardCategory()
        .getCategoryId()
        .longValue());
      rd.setCategoryName(record.getEipTMsgboardCategory().getCategoryName());
      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record
        .getEipTMsgboardCategory()
        .getPublicFlag()));
      rd.setOwnerId(record.getOwnerId().longValue());
      rd.setOwnerName(ALEipUtils
        .getUserFullName(record.getOwnerId().intValue()));
      rd.setNote(record.getNote());
      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());

      List<EipTMsgboardFile> list =
        getSelectQueryForFiles(record.getTopicId().intValue()).fetchList();
      if (list != null && list.size() > 0) {
        List<FileuploadBean> attachmentFileList =
          new ArrayList<FileuploadBean>();
        FileuploadBean filebean = null;
        EipTMsgboardFile file = null;
        int size = list.size();
        for (int i = 0; i < size; i++) {
          file = list.get(i);
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
        rd.setAttachmentFiles(attachmentFileList);
      }

      return rd;
    } catch (Exception e) {
      logger.error("[MsgboardTopicSelectData]", e);
      throw new ALDBErrorException();
    }
  }

  /**
   * 
   * @return
   */
  public List<MsgboardCategoryResultData> getCategoryList() {
    if (hasAclCategoryList) {
      return categoryList;
    } else {
      return new ArrayList<MsgboardCategoryResultData>();
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
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("topic_name", EipTMsgboardTopic.TOPIC_NAME_PROPERTY);
    map.putValue("update_date", EipTMsgboardTopic.UPDATE_DATE_PROPERTY);
    map.putValue("category", EipTMsgboardCategory.CATEGORY_ID_PK_COLUMN);
    map.putValue(
      "category_name",
      EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
        + "."
        + EipTMsgboardCategory.CATEGORY_NAME_PROPERTY);
    map.putValue("owner_name", EipTMsgboardTopic.OWNER_ID_PROPERTY);
    map.putValue("update_user", EipTMsgboardTopic.UPDATE_USER_ID_PROPERTY);

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

  public Object getParentTopic() {
    return parentTopic;
  }

  public List<MsgboardTopicResultData> getCoTopicList() {
    return coTopicList;
  }

  public int getUserId() {
    return uid;
  }

  public boolean showReplyForm() {
    return showReplyForm;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC;
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

}
