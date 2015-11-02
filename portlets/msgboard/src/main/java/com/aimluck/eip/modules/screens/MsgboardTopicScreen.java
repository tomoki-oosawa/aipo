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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.msgboard.MsgboardTopicSelectData;
import com.aimluck.eip.msgboard.util.MsgboardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 掲示板トピックの一覧を処理するクラスです。
 * 
 */
public class MsgboardTopicScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardTopicScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {

      if ("update".equals(mode)) {
        updateState(rundata, context, portlet);
      }

      context.put("ajax_onloadimage", "true");

      ParameterParser parser = rundata.getParameters();

      ALEipUtils.passPSML(rundata, context, "p12f-filters", parser
        .getString(ALEipConstants.LIST_FILTER));
      ALEipUtils.passPSML(rundata, context, "p12g-filtertypes", parser
        .getString(ALEipConstants.LIST_FILTER_TYPE));

      MsgboardTopicSelectData listData = new MsgboardTopicSelectData();
      listData.initField();
      listData.loadCategoryList(rundata, context);
      listData.setRowsNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1a-rows")));
      listData.setTableColumNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1e-rows")));
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-msgboard-topic.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[MsgboardTopicScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  protected void updateState(RunData rundata, Context context,
      VelocityPortlet portlet) {
    // エラーメッセージ
    List<String> msgList = new ArrayList<String>();
    ALNumberField state = new ALNumberField();
    state.setFieldName(ALLocalizationUtils.getl10n("MSGBOARD_STATUS"));
    // 0 から 100 まで
    state.limitValue(0, 100);
    // 必須項目
    state.setNotNull(true);
    if (rundata.getParameters().containsKey("state")) {
      state.setValue(rundata.getParameters().getString("state"));
    } else {
      return;
    }

    ALNumberField eid = new ALNumberField();
    state.setFieldName("entity id");
    eid.setNotNull(true);
    if (rundata.getParameters().containsKey("entityid")) {
      eid.setValue(rundata.getParameters().getString("entityid"));
    } else {
      return;
    }

    int value = (int) state.getValue();
    // 0以上100以下で、10の倍数
    boolean isValid =
      (value % 10 == 0 && state.validate(msgList) && eid.validate(msgList));

    if (!isValid) {
      return;
    }

    try {

      SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchDbExp(EipTTodo.TODO_ID_PK_COLUMN, Integer
          .valueOf((int) eid.getValue()));
      query.andQualifier(exp2);

      List<EipTTodo> list = query.fetchList();
      if (list == null || list.size() == 0) {
        return;
      }

      EipTTodo todo = list.get(0);
      todo.setState(Short.valueOf((short) value));
      Database.commit();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("[MsgboardTopicScreen]", ex);
      return;
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return MsgboardUtils.MSGBOARD_PORTLET_NAME;
  }
}