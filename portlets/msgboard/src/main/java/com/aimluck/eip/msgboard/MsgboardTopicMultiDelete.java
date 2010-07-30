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
package com.aimluck.eip.msgboard;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板トピックを複数削除するためのクラス． <BR>
 *
 */
public class MsgboardTopicMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(MsgboardTopicMultiDelete.class.getName());

  /**
   *
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      int ownerid_int = ALEipUtils.getUserId(rundata);
      Integer ownerid = Integer.valueOf(ownerid_int);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMsgboardTopic.class);

      for (int i = 0; i < values.size(); i++) {
        String id = (String) values.get(i);
        Expression exp01 = ExpressionFactory.matchDbExp(
            EipTMsgboardTopic.OWNER_ID_COLUMN, ownerid);
        Expression exp02 = ExpressionFactory.matchDbExp(
            EipTMsgboardTopic.TOPIC_ID_PK_COLUMN, Integer.valueOf(id));
        Expression exp03 = ExpressionFactory.matchExp(
            EipTMsgboardTopic.PARENT_ID_PROPERTY, Integer.valueOf(id));
        query.orQualifier((exp01.andExp(exp02)).orExp(exp03));
      }

      @SuppressWarnings("unchecked")
      List<EipTMsgboardTopic> list = dataContext.performQuery(query);
      if (list == null || list.size() == 0) {
        // トピックリストが空の場合
        logger.debug("[MsgboardMultiDelete] Empty TopicIDs...");
        return false;
      }

      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTMsgboardTopic topic = list.get(i);
        dataContext.deleteObject(topic);
        // イベントログに保存
        ALEventlogFactoryService
            .getInstance()
            .getEventlogHandler()
            .log(topic.getTopicId(),
                ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC,
                topic.getTopicName());
      }

      dataContext.commitChanges();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限を返します。
   *
   * @return
   */
  protected int getDefineAclType() {
    return ALAccessControlConstants.VALUE_ACL_DELETE;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC;
  }
}
