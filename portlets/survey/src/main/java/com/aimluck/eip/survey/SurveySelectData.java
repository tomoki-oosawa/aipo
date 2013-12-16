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

package com.aimluck.eip.survey;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTSurvey;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyRespondent;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.survey.util.SurveyUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アンケート検索データを管理するクラスです。 <BR>
 * 
 */
public class SurveySelectData extends
    ALAbstractSelectData<EipTSurvey, EipTSurvey> implements ALData {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SurveySelectData.class.getName());

  private String listMode = null;

  public String getListMode() {
    return listMode;
  }

  private int loginUserId = -1;

  private Integer allUserCount = null;

  private List<EipTSurveyRespondent> respondentList = null;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    setLoginUserId(ALEipUtils.getUserId(rundata));

    String listModeParam = rundata.getParameters().getString("listmode");
    listMode = ALEipUtils.getTemp(rundata, context, "listMode");
    if (listModeParam == null && listMode == null) {
      ALEipUtils.setTemp(
        rundata,
        context,
        "listMode",
        SurveyUtils.LIST_UNRESPONDED);
      listMode = SurveyUtils.LIST_UNRESPONDED;
    } else if (listModeParam != null) {
      ALEipUtils.setTemp(rundata, context, "listMode", listModeParam);
      listMode = listModeParam;
    } else if (SurveyUtils.LIST_UNRESPONDED.equals(listMode)
      && rundata.getParameters().containsKey("maximized")) {
      ALEipUtils.setTemp(rundata, context, "listMode", SurveyUtils.LIST_OPENED);
      listMode = SurveyUtils.LIST_OPENED;
    }
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipTSurvey> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      SelectQuery<EipTSurvey> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      ResultList<EipTSurvey> list = query.getResultList();

      if (list.size() > 0) {
        SelectQuery<EipTSurveyRespondent> queryRespondent =
          Database.query(EipTSurveyRespondent.class);
        queryRespondent.andQualifier(ExpressionFactory.inExp(
          EipTSurveyRespondent.EIP_TSURVEY_PROPERTY,
          list));
        respondentList = queryRespondent.fetchList();
      }

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
   *          RunData
   * @param context
   *          Context
   * @return SelectQuery
   */
  private SelectQuery<EipTSurvey> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTSurvey> query = Database.query(EipTSurvey.class);
    // ToDo 未回答のみ、作成済みなどのモードわけ
    int loginUserId = ALEipUtils.getUserId(rundata);
    Expression exp1_1 =
      ExpressionFactory.matchExp(
        EipTSurvey.RESPONDENT_TYPE_PROPERTY,
        SurveyUtils.RESPONDENT_TYPE_ALL);
    Expression exp1_2 =
      ExpressionFactory.matchExp(EipTSurvey.EIP_TSURVEY_RESPONDENTS_PROPERTY
        + "."
        + EipTSurveyRespondent.USER_ID_PROPERTY, SurveyUtils.RESPONDENT_DUMMY);
    Expression exp1_3 =
      ExpressionFactory.matchExp(
        EipTSurvey.CREATE_USER_ID_PROPERTY,
        loginUserId);

    Expression exp2_1 =
      ExpressionFactory.matchExp(
        EipTSurvey.RESPONDENT_TYPE_PROPERTY,
        SurveyUtils.RESPONDENT_TYPE_MEMBER);
    Expression exp2_2 =
      ExpressionFactory.matchExp(EipTSurvey.EIP_TSURVEY_RESPONDENTS_PROPERTY
        + "."
        + EipTSurveyRespondent.USER_ID_PROPERTY, loginUserId);

    Expression exp;
    if (SurveyUtils.LIST_CREATED.equals(listMode)) {
      exp = (exp1_3);
    } else {
      exp = (exp1_1.andExp(exp1_2)).orExp(exp2_1.andExp(exp2_2));
    }

    Expression exp3 =
      ExpressionFactory.matchExp(EipTSurvey.EIP_TSURVEY_RESPONDENTS_PROPERTY
        + "."
        + EipTSurveyRespondent.RESPONSE_FLAG_PROPERTY, "F");

    Expression exp4_1 =
      ExpressionFactory.noMatchExp(
        EipTSurvey.OPEN_FLAG_PROPERTY,
        SurveyUtils.OPEN_FLAG_NOT_OPENED);

    Expression exp4_2 =
      ExpressionFactory.matchExp(
        EipTSurvey.CLOSE_FLAG_PROPERTY,
        SurveyUtils.CLOSE_FLAG_NOT_CLOSED);

    Expression exp4_3 =
      ExpressionFactory.matchExp(
        EipTSurvey.OPEN_FLAG_PROPERTY,
        SurveyUtils.OPEN_FLAG_NOT_OPENED);

    Expression exp4_4 =
      ExpressionFactory.noMatchExp(
        EipTSurvey.CLOSE_FLAG_PROPERTY,
        SurveyUtils.CLOSE_FLAG_NOT_CLOSED);

    if (SurveyUtils.LIST_UNRESPONDED.equals(listMode)) {
      exp = exp.andExp(exp3.andExp(exp4_1.andExp(exp4_2)));
    } else if (SurveyUtils.LIST_OPENED.equals(listMode)) {
      exp = exp.andExp(exp4_1.andExp(exp4_2));
    } else if (SurveyUtils.LIST_CLOSED.equals(listMode)) {
      exp = exp.andExp(exp4_3.orExp(exp4_4));
    }

    query.andQualifier(exp);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTSurvey selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(EipTSurvey obj)
      throws ALPageNotFoundException, ALDBErrorException {
    SurveyResultData rd = new SurveyResultData();
    rd.initField();
    rd.setSurveyId(obj.getSurveyId());
    rd.setName(obj.getName());
    rd.setCreateUser(ALEipUtils.getALEipUser(obj.getCreateUserId().intValue()));
    rd.setCreatedDate(obj.getCreateDate());
    rd.setCloseDate(obj.getCloseDate());
    if (SurveyUtils.LIST_CREATED.equals(listMode)) {
      if (obj.getRespondentCompleteCount() == null
        || obj.getRespondentCount() == null) {
        obj = SurveyUtils.refreshResponseRate(obj);
      }

      Integer respondentCompleteCount = obj.getRespondentCompleteCount();
      Integer respondentCount = obj.getRespondentCount();

      if (respondentCount == null) {
        if (allUserCount == null) {
          allUserCount = SurveyUtils.getAllActiveUserCount();
        }
        respondentCount = allUserCount;
      }

      if (respondentCompleteCount != null && respondentCount != null) {
        rd.setResponseRate(respondentCompleteCount + "/" + respondentCount);
      } else {
        rd.setResponseRate("");
      }

      if (SurveyUtils.isOpen(obj)) {
        rd.setCloseFlag("受付中");
      } else {
        rd.setCloseFlag("終了");
      }

    } else {
      boolean responsed = false;
      if (respondentList != null) {
        for (EipTSurveyRespondent respondent : respondentList) {
          if (respondent.getEipTSurvey().getSurveyId() == obj.getSurveyId()
            && respondent.getUserId() == loginUserId
            && "T".equals(respondent.getResponseFlag())) {
            responsed = true;
            break;
          }
        }
      }
      if (responsed) {
        rd.setResponseFlag("回答済");
      } else {
        rd.setResponseFlag("未回答");
      }
    }
    return rd;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTSurvey obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("name", EipTSurvey.NAME_PROPERTY);
    map.putValue("user_id", EipTSurvey.CREATE_USER_ID_PROPERTY);
    map.putValue("create_date", EipTSurvey.CREATE_DATE_PROPERTY);
    map.putValue("close_date", EipTSurvey.CLOSE_DATE_PROPERTY);
    map.putValue("response_flag", EipTSurvey.EIP_TSURVEY_RESPONDENTS_PROPERTY
      + "."
      + EipTSurveyRespondent.RESPONSE_FLAG_PROPERTY);
    return map;
  }

  /**
   * @return loginUserId
   */
  public int getLoginUserId() {
    return loginUserId;
  }

  /**
   * @param loginUserId
   *          セットする loginUserId
   */
  public void setLoginUserId(int loginUserId) {
    this.loginUserId = loginUserId;
  }

}
