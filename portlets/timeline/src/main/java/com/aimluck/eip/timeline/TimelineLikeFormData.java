/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineLike;
import com.aimluck.eip.cayenne.om.social.Activity;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * タイムライントピックのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class TimelineLikeFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineFormData.class.getName());

  /** いいね！を押したユーザーID */
  private int user_id;

  /** いいね！が押されたトピック */
  private int timeline_id;

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
    user_id = ALEipUtils.getUserId(rundata);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
  }

  /**
   * いいね！の各フィールドに対する制約条件を設定します。 <BR>
   * →なし
   * 
   */
  @Override
  protected void setValidator() {
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
    return true;
  }

  /**
   * いいねをデータベースから削除します。 <BR>
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
      List<EipTTimelineLike> list;

      list =
        TimelineUtils.getEipTTimelineLikeListToDeleteTopic(
          rundata,
          context,
          false);

      if (list == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[TimelineFormData] Not found List...");
        throw new ALPageNotFoundException();
      }

      List<Integer> topicIdList = new ArrayList<Integer>();
      EipTTimelineLike topic;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        topic = list.get(i);
        topicIdList.add(topic.getTimelineLikeId());
      }

      // トピックを削除
      SelectQuery<EipTTimelineLike> query =
        Database.query(EipTTimelineLike.class);
      Expression exp =
        ExpressionFactory.inDbExp(
          EipTTimelineLike.TIMELINE_LIKE_ID_PK_COLUMN,
          topicIdList);
      query.setQualifier(exp);

      List<EipTTimelineLike> topics = query.fetchList();

      Database.deleteAll(topics);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        parent.getTimelineId(),
        ALEventlogConstants.PORTLET_TYPE_TIMELINE,
        TimelineUtils.compressString(parent.getNote()));

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

      SelectQuery<EipTTimelineLike> query =
        Database.query(EipTTimelineLike.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipTTimelineLike.OWNER_ID_PROPERTY, user_id);
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTTimelineLike.TIMELINE_ID_PROPERTY,
          timeline_id);

      query.setQualifier(exp1);
      query.andQualifier(exp2);
      List<EipTTimelineLike> fetchList = query.fetchList();
      int count = fetchList.size();

      // DBへの格納処理よりもページリロードが先に実行され,格納済みにもかかわらず表示されていないトピックについて
      // 再度「いいね！」が押された時には,格納処理を行わない。
      if (count == 0) {
        // 新規オブジェクトモデル
        EipTTimelineLike like = Database.create(EipTTimelineLike.class);
        like.setOwnerId(user_id);
        like.setTimelineId(timeline_id);
        like.setCreateDate(Calendar.getInstance().getTime());

        Database.commit();

        if (like.getOwnerId() != 0) {
          // オブジェクトモデルを取得
          EipTTimeline parententry =
            TimelineUtils.getEipTTimelineParentEntry(rundata, context);
          // アクティビティ
          TimelineLikeSelectData likelist = new TimelineLikeSelectData();

          String loginName =
            ALEipUtils.getALEipUser(user_id).getName().getValue();
          String targetLoginName =
            ALEipUtils
              .getALEipUser(parententry.getOwnerId())
              .getName()
              .getValue();
          if (likelist.getLikeList(timeline_id).size() > 1
            && !loginName.equals(targetLoginName)) {
            ALEipUser lastuser = ALEipUtils.getALEipUser(loginName);
            String lastuserName = lastuser.getAliasName().getValue();
            loginName =
              ALLocalizationUtils.getl10nFormat(
                "TIMELINE_OTHER",
                lastuserName,
                likelist.getLikeList(timeline_id).size() - 1);

            SelectQuery<Activity> dQuery = Database.query(Activity.class);
            Expression exp3 =
              ExpressionFactory.matchExp(Activity.EXTERNAL_ID_PROPERTY, String
                .valueOf(parententry.getTimelineId()));
            Expression exp4 =
              ExpressionFactory.matchExp(Activity.APP_ID_PROPERTY, "timeline");

            dQuery.setQualifier(exp3);
            dQuery.andQualifier(exp4);
            List<Activity> maps = dQuery.fetchList();
            Database.deleteAll(maps);

            Database.commit();

          }

          TimelineUtils.createNewLikeActivity(
            parententry,
            loginName,
            targetLoginName);
        }
      }
    } catch (RuntimeException ex) {
      // RuntimeException
      Database.rollback();
      logger.error("timeline", ex);
      return false;
    } catch (ALDBErrorException ex) {
      Database.rollback();
      logger.error("timeline", ex);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("timeline", ex);
      return false;
    }
    return true;
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
   * @return user_id
   */
  public int getUser_id() {
    return user_id;
  }

  /**
   * @param user_id
   *          セットする user_id
   */
  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  /**
   * @return timeline_id
   */
  public int getTimeline_id() {
    return timeline_id;
  }

  /**
   * @param timeline_id
   *          セットする timeline_id
   */
  public void setTimeline_id(int timeline_id) {
    this.timeline_id = timeline_id;
  }

}
