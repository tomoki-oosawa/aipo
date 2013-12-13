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
 *
 * General Purpose Database Portlet was developed by Advance,Inc.
 * http://www.a-dvance.co.jp/
 */

package com.aimluck.eip.survey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTSurvey;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyOption;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyRespondent;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyResponseMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.survey.util.SurveyUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アンケートのFormDataです。 <BR>
 * 
 */
public class SurveyResponseFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SurveyResponseFormData.class.getName());

  private Map<Integer, ALStringField> options;

  private List<Integer> oldResponses;

  private List<Integer> newResponses;

  private SurveyResultData survey;

  private int loginUserId = -1;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    loginUserId = ALEipUtils.getUserId(rundata);
  }

  /**
   * 
   */
  @Override
  public void initField() {
    survey = new SurveyResultData();
    survey.initField();
    options = new LinkedHashMap<Integer, ALStringField>();
    oldResponses = new ArrayList<Integer>();
    newResponses = new ArrayList<Integer>();
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
  }

  /**
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    return msgList.size() == 0;
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      String responses[] = rundata.getParameters().getStrings("responses[]");
      if (responses != null) {
        for (String line : responses) {
          if (line.trim().length() > 0) {
            newResponses.add(Integer.parseInt(line));
          }
        }
      }
    }
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
    try {
      // オブジェクトモデルを取得
      EipTSurvey _survey = SurveyUtils.getEipTSurvey(rundata, context);
      if (_survey == null) {
        return false;
      }

      survey.setSurveyId(_survey.getSurveyId());
      survey.setName(_survey.getName());
      survey.setComment(_survey.getName());
      survey.setCreateUser(ALEipUtils.getALEipUser(_survey.getCreateUserId()));
      survey.setCloseDate(_survey.getCloseDate());
      survey.setAnonymousFlag(_survey.getAnonymousFlag());
      survey.setOptionType(_survey.getOptionType());

      this.options = SurveyUtils.getOptions(_survey);
      List<EipTSurveyResponseMap> list =
        SurveyUtils.getResponseMap(_survey, loginUserId);
      for (EipTSurveyResponseMap map : list) {
        oldResponses.add(map.getEipTSurveyOption().getOptionId());
      }

    } catch (Exception ex) {
      logger.error("Survey", ex);
      return false;
    }
    return true;
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
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
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
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipTSurvey _survey = SurveyUtils.getEipTSurvey(rundata, context);
      List<EipTSurveyResponseMap> list =
        SurveyUtils.getResponseMap(_survey, loginUserId);
      for (EipTSurveyResponseMap map : list) {
        Database.delete(map);
      }

      EipTSurveyRespondent respondent =
        SurveyUtils.getEipTSurveyRespondent(_survey, loginUserId);
      if (respondent == null) {
        respondent = Database.create(EipTSurveyRespondent.class);
        respondent.setEipTSurvey(_survey);
        respondent.setUserId(loginUserId);
      }
      respondent.setResponseFlag("T");

      LinkedHashMap<Integer, EipTSurveyOption> _options =
        SurveyUtils.getEipTSurveyOptions(_survey);
      for (Integer responseId : newResponses) {
        if (_options.containsKey(responseId)) {
          EipTSurveyResponseMap responseMap =
            Database.create(EipTSurveyResponseMap.class);
          responseMap.setEipTSurvey(_survey);
          responseMap.setEipTSurveyOption(_options.get(responseId));
          responseMap.setUserId(loginUserId);
        }
      }
      Database.commit();
      return true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("report", ex);
      return false;
    }
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
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  public SurveyResultData getDetail() {
    return survey;
  }

  public Map<Integer, ALStringField> getOptions() {
    return options;
  }

  public List<Integer> getOldResponses() {
    return oldResponses;
  }
}
