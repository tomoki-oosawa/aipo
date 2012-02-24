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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.timeline.ALTimelineFactoryService;
import com.aimluck.eip.services.timeline.ALTimelineHandler;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムライントピックのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class TimelineFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineFormData.class.getName());

  /** メモ */
  private ALStringField note;

  private int parentId;

  private int uid;

  private String orgId;

  private String aclPortletFeature = null;

  /** 閲覧権限の有無 */
  @SuppressWarnings("unused")
  private boolean hasAclCategoryList;

  /** 他ユーザーの作成したトピックの編集権限 */
  private boolean hasAclUpdateTopicOthers;

  /** 他ユーザーの作成したトピックの削除権限 */
  private boolean hasAclDeleteTopicOthers;

  /** 顔写真の有無 */
  private boolean has_photo;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    uid = ALEipUtils.getUserId(rundata);
    orgId = Database.getDomainName();
    has_photo = false;

  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // メモ
    note = new ALStringField();
    note.setFieldName("内容");
    note.setTrim(false);

  }

  /**
   * タイムラインの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // メモ必須項目
    note.setNotNull(true);
    // メモの文字数制限
    note.limitMaxLength(500);
  }

  /**
   * トピックのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // メモ
    note.validate(msgList);
    return (msgList.size() == 0);

  }

  /**
   * トピックをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {

      // FIX_ME イベントログのために一度IDと名前を取得
      int parentid =
        Integer.parseInt(ALEipUtils.getTemp(
          rundata,
          context,
          ALEipConstants.ENTITY_ID));

      EipTTimeline parent = Database.get(EipTTimeline.class, (long) parentid);

      // オブジェクトモデルを取得
      List<EipTTimeline> list;

      list =
        TimelineUtils.getEipTTimelineListToDeleteTopic(rundata, context, false);

      if (list == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[TimelineFormData] Not found List...");
        throw new ALPageNotFoundException();
      }

      List<Integer> topicIdList = new ArrayList<Integer>();
      EipTTimeline topic;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        topic = list.get(i);
        topicIdList.add(topic.getTimelineId());
      }

      // トピックを削除
      SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);
      Expression exp =
        ExpressionFactory.inDbExp(
          EipTTimeline.TIMELINE_ID_PK_COLUMN,
          topicIdList);
      query.setQualifier(exp);

      List<EipTTimeline> topics = query.fetchList();

      Database.deleteAll(topics);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        parent.getTimelineId(),
        ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC,
        parent.getNote());

    } catch (Exception e) {
      Database.rollback();
      logger.error("[TimelineSelectData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * トピックをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 新規オブジェクトモデル
      EipTTimeline topic = Database.create(EipTTimeline.class);
      // 親トピックID
      topic.setParentId(Integer.valueOf(parentId));
      // ユーザーID
      topic.setOwnerId(Integer.valueOf(uid));
      // メモ
      topic.setNote(note.getValue());
      // 作成日
      topic.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      topic.setUpdateDate(Calendar.getInstance().getTime());

      if (Integer.valueOf(parentId) != 0) {
        EipTTimeline parent =
          Database.get(EipTTimeline.class, Integer.valueOf(parentId));
        parent.setUpdateDate(Calendar.getInstance().getTime());
      }
      // submitURL();

      Database.commit();

      ALTimelineFactoryService tlservice =
        (ALTimelineFactoryService) ((TurbineServices) TurbineServices
          .getInstance()).getService(ALTimelineFactoryService.SERVICE_NAME);
      ALTimelineHandler timelinehandler = tlservice.getTimelineHandler();
      timelinehandler.pushToken(rundata, String.valueOf(parentId));

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        topic.getTimelineId(),
        ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC,
        topic.getCreateDate().toString());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  protected void submitURL() throws IOException {
    try {
      URL urlPathtraq =
        new URL("http://cdn-assist.appspot.com/channel?msg=" + parentId);
      BufferedReader reader =
        new BufferedReader(new InputStreamReader(urlPathtraq.openStream()));
      String line;
      while ((line = reader.readLine()) != null) {
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  /**
   * データベースに格納されているトピックを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);
    return res;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * @return parentId
   */
  public int getParentId() {
    return parentId;
  }

  /**
   * @param parentId
   *          セットする parentId
   */
  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  public void setAclPortletFeature(String aclPortletFeature) {
    this.aclPortletFeature = aclPortletFeature;
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

  /**
   * 
   * @param bool
   */
  public void setHasPhoto(boolean bool) {
    has_photo = bool;
  }

  public boolean hasPhoto() {
    return has_photo;
  }
}
