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

package com.aimluck.eip.survey.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTSurvey;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyOption;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyRespondent;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyResponseMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アンケートのユーティリティクラスです。 <BR>
 * 
 */
public class SurveyUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SurveyUtils.class.getName());

  /** ポートレット名 */
  public static final String PORTLET_NAME = "Survey";

  public static final int RESPONDENT_DUMMY = 0;

  public static final String RESPONDENT_TYPE_ALL = "A";

  public static final String RESPONDENT_TYPE_MEMBER = "M";

  public static final String OPEN_FLAG_MANUAL_OPENED = "M";

  public static final String OPEN_FLAG_AUTO_OPENED = "A";

  public static final String OPEN_FLAG_NOT_OPENED = "F";

  public static final String CLOSE_FLAG_MANUAL_CLOSED = "M";

  public static final String CLOSE_FLAG_AUTO_CLOSED = "A";

  public static final String CLOSE_FLAG_NOT_CLOSED = "F";

  public static final String OPTION_TYPE_SINGLE = "S";

  public static final String OPTION_TYPE_MULTIPLE = "M";

  public static final String LIST_UNRESPONDED = "unresponded";

  public static final String LIST_OPENED = "opened";

  public static final String LIST_CLOSED = "closed";

  public static final String LIST_CREATED = "created";

  public static void clearSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("submenu");
    ALEipUtils.removeTemp(rundata, context, list);
  }

  public static EipTSurvey getEipTSurvey(RunData rundata, Context context)
      throws ALDBErrorException {
    String requestid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (requestid == null || Integer.valueOf(requestid) == null) {
        // Request IDが空の場合
        logger.debug("[SurveyUtils] Empty ID...");
        return null;
      }

      SelectQuery<EipTSurvey> query = Database.query(EipTSurvey.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTSurvey.SURVEY_ID_PK_COLUMN, requestid);
      query.setQualifier(exp1);

      List<EipTSurvey> requests = query.fetchList();

      if (requests == null || requests.size() == 0) {
        // 指定した Survey IDのレコードが見つからない場合
        logger.debug("[SurveyUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return requests.get(0);
    } catch (ALPageNotFoundException ex) {
      ALEipUtils.redirectPageNotFound(rundata);
      return null;
    } catch (Exception ex) {
      logger.error("Survey", ex);
      throw new ALDBErrorException();
    }
  }

  public static LinkedHashMap<Integer, ALStringField> getOptions(
      EipTSurvey _survey) {
    LinkedHashMap<Integer, EipTSurveyOption> eipTSurveyOptions =
      getEipTSurveyOptions(_survey);
    LinkedHashMap<Integer, ALStringField> options =
      new LinkedHashMap<Integer, ALStringField>();

    for (EipTSurveyOption option : eipTSurveyOptions.values()) {
      ALStringField field = new ALStringField();
      field.setValue(option.getName());
      options.put(option.getOptionId(), field);
    }
    return options;
  }

  public static LinkedHashMap<Integer, EipTSurveyOption> getEipTSurveyOptions(
      EipTSurvey _survey) {
    LinkedHashMap<Integer, EipTSurveyOption> options =
      new LinkedHashMap<Integer, EipTSurveyOption>();
    SelectQuery<EipTSurveyOption> query =
      Database.query(EipTSurveyOption.class);
    query.andQualifier(ExpressionFactory.matchExp(
      EipTSurveyOption.EIP_TSURVEY_PROPERTY,
      _survey));
    query.orderAscending(EipTSurveyOption.SORT_NUMBER_PROPERTY);
    ResultList<EipTSurveyOption> list = query.getResultList();
    for (EipTSurveyOption option : list) {
      options.put(option.getOptionId(), option);
    }
    return options;
  }

  public static ResultList<EipTSurveyResponseMap> getResponseMap(
      EipTSurvey _survey, Integer userId) {
    SelectQuery<EipTSurveyResponseMap> query =
      Database.query(EipTSurveyResponseMap.class);
    query.andQualifier(ExpressionFactory.matchExp(
      EipTSurveyResponseMap.EIP_TSURVEY_PROPERTY,
      _survey));

    query.andQualifier(ExpressionFactory.matchExp(
      EipTSurveyResponseMap.USER_ID_PROPERTY,
      userId));
    return query.getResultList();
  }

  public static EipTSurveyRespondent getEipTSurveyRespondent(
      EipTSurvey _survey, Integer userId) {
    SelectQuery<EipTSurveyRespondent> query =
      Database.query(EipTSurveyRespondent.class);
    query.andQualifier(ExpressionFactory.matchExp(
      EipTSurveyRespondent.EIP_TSURVEY_PROPERTY,
      _survey));
    query.andQualifier(ExpressionFactory.matchExp(
      EipTSurveyRespondent.USER_ID_PROPERTY,
      userId));
    return query.fetchSingle();
  }

  public static List<EipTSurveyRespondent> getEipTSurveyRespondentList(
      EipTSurvey _survey) {
    SelectQuery<EipTSurveyRespondent> query =
      Database.query(EipTSurveyRespondent.class);
    query.andQualifier(ExpressionFactory.matchExp(
      EipTSurveyRespondent.EIP_TSURVEY_PROPERTY,
      _survey));
    query.andQualifier(ExpressionFactory.noMatchExp(
      EipTSurveyRespondent.USER_ID_PROPERTY,
      RESPONDENT_DUMMY));
    return query.fetchList();
  }

  public static EipTSurvey refreshResponseRate(EipTSurvey _survey) {
    List<EipTSurveyRespondent> eipTSurveyRespondentList =
      getEipTSurveyRespondentList(_survey);
    int completed = 0;
    for (EipTSurveyRespondent respondent : eipTSurveyRespondentList) {
      if ("T".equals(respondent.getResponseFlag())) {
        completed++;
      }
    }
    _survey.setRespondentCompleteCount(completed);
    if (RESPONDENT_TYPE_MEMBER.equals(_survey.getRespondentType())) {
      _survey.setRespondentCount(eipTSurveyRespondentList.size());
    }
    Database.commit();
    return _survey;
  }

  public static Integer getAllActiveUserCount() {
    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

    Expression exp11 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
    query.setQualifier(exp11);
    Expression exp21 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(1));
    Expression exp22 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(2));
    Expression exp23 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(3));
    query.andQualifier(exp21.andExp(exp22).andExp(exp23));
    return query.getCount();
  }

  public static boolean isOpen(EipTSurvey survey) {
    return !SurveyUtils.OPEN_FLAG_NOT_OPENED.equals(survey.getOpenFlag())
      && SurveyUtils.CLOSE_FLAG_NOT_CLOSED.equals(survey.getCloseFlag());
  }

  public static Date getEndtimeOfDay(Date input) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(input);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    return cal.getTime();
  }

  public static void refreshFlagsByDate() {
    try {
      SelectQuery<EipTSurvey> query1 = Database.query(EipTSurvey.class);
      Date now = new Date();
      Expression exp1_1 =
        ExpressionFactory.matchExp(
          EipTSurvey.OPEN_FLAG_PROPERTY,
          SurveyUtils.OPEN_FLAG_NOT_OPENED);
      Expression exp1_2 =
        ExpressionFactory.lessExp(EipTSurvey.OPEN_DATE_PROPERTY, now);
      Expression exp1 = exp1_1.andExp(exp1_2);
      query1.andQualifier(exp1);
      int count1 = query1.getCount();
      if (count1 > 0) {
        List<EipTSurvey> fetchList = query1.fetchList();
        for (EipTSurvey item : fetchList) {
          item.setOpenFlag(SurveyUtils.OPEN_FLAG_AUTO_OPENED);
        }
      }

      SelectQuery<EipTSurvey> query2 = Database.query(EipTSurvey.class);
      Expression exp2_1 =
        ExpressionFactory.matchExp(
          EipTSurvey.CLOSE_FLAG_PROPERTY,
          SurveyUtils.CLOSE_FLAG_NOT_CLOSED);
      Expression exp2_2 =
        ExpressionFactory.lessExp(EipTSurvey.CLOSE_DATE_PROPERTY, now);
      Expression exp2 = exp2_1.andExp(exp2_2);

      query2.andQualifier(exp2);
      int count2 = query2.getCount();
      if (count2 > 0) {
        List<EipTSurvey> fetchList = query2.fetchList();
        for (EipTSurvey item : fetchList) {
          item.setCloseFlag(SurveyUtils.CLOSE_FLAG_AUTO_CLOSED);
        }
      }
      Database.commit();
    } catch (Exception e) {
    }
  }
}
