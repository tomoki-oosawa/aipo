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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTSurvey;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyOption;
import com.aimluck.eip.cayenne.om.portlet.EipTSurveyRespondent;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.survey.util.SurveyUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アンケートのFormDataです。 <BR>
 * 
 */
public class SurveyFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SurveyFormData.class.getName());

  /** タイトル */
  private ALStringField name;

  /** 内容 */
  private ALStringField comment;

  /** 回答形式 */
  private ALStringField optionType;

  /** 作成者 */
  private ALEipUser createUser;

  /** 受付開始日 */
  private ALDateTimeField openDate;

  /** 回答期日 */
  private ALDateTimeField closeDate;

  /** 編集フラグ */
  private ALStringField editableFlag;

  /** 回答対象者 */
  private List<ALEipUser> respondents;

  private ALStringField respondentType;

  /** 回答項目(改行区切) */
  private List<String> optionList;

  private ALStringField options;

  /** 匿名回答 */
  private ALStringField anonymousFlag;

  /**
   * 
   */
  @Override
  public void initField() {
    name = new ALStringField();
    name.setFieldName("タイトル");

    comment = new ALStringField();
    comment.setFieldName("内容");

    optionType = new ALStringField();
    optionType.setFieldName("回答形式");

    openDate = new ALDateTimeField();

    closeDate = new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);
    closeDate.setFieldName("回答期日");

    options = new ALStringField();
    options.setFieldName("回答項目");
    optionList = new ArrayList<String>();

    respondentType = new ALStringField();
    respondentType.setFieldName("回答対象者");
    respondents = new ArrayList<ALEipUser>();

    anonymousFlag = new ALStringField();
    anonymousFlag.setFieldName("匿名回答");
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
    name.setNotNull(true);
    name.setTrim(true);
    name.limitMaxLength(50);

    comment.setNotNull(true);
    comment.limitMaxLength(10000);

    optionType.setNotNull(true);
    closeDate.setNotNull(true);

    respondentType.setNotNull(true);
    anonymousFlag.setNotNull(true);
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
    name.validate(msgList);
    comment.validate(msgList);
    optionType.validate(msgList);
    if (closeDate.validate(msgList)) {
      Date now = new Date();
      if (closeDate.getValue().after(now)) {
        msgList
          .add("『 <span class=\"em\">回答期日</span> 』は本日以降の値を選択してください。</span>");
      }
    }

    if (optionList == null || optionList.size() == 0) {
      msgList.add("『 <span class=\"em\">回答項目</span> 』を入力してください。</span>");
    } else {
      for (String option : optionList) {
        ALStringField dummy = new ALStringField();
        dummy.setFieldName("回答項目");
        dummy.setNotNull(false);
        dummy.limitMaxLength(50);
        dummy.setValue(option.trim());
        dummy.validate(msgList);
      }
    }

    if (respondentType.validate(msgList)) {
      if (SurveyUtils.RESPONDENT_TYPE_MEMBER.equals(respondentType.getValue())) {
        if (respondents.size() == 0) {
          msgList.add("『 <span class=\"em\">回答対象者</span> 』を選択してください。</span>");
        }
      }
    }

    return msgList.size() == 0;
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      if (optionType.toString().equals("")) {
        optionType.setValue(SurveyUtils.OPTION_TYPE_SINGLE);
      }
      if (respondentType.toString().equals("")) {
        respondentType.setValue(SurveyUtils.RESPONDENT_TYPE_ALL);
      }
      if (openDate.toString().equals("")) {
        Calendar cal = Calendar.getInstance();
        openDate.setValue(cal.getTime());
      }

      if (closeDate.toString().equals("")) {
        Calendar cal = Calendar.getInstance();
        closeDate.setValue(cal.getTime());
      }

      String _options[] = rundata.getParameters().getStrings("options[]");
      if (_options != null) {
        for (String line : _options) {
          if (line.trim().length() > 0) {
            optionList.add(line.trim());
          }
        }
      }

      String memberNames[] = rundata.getParameters().getStrings("members");
      if (memberNames != null && memberNames.length > 0) {
        SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
        Expression exp =
          ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, memberNames);
        query.setQualifier(exp);
        respondents.addAll(ALEipUtils.getUsersFromSelectQuery(query));
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
      EipTSurvey Survey = SurveyUtils.getEipTSurvey(rundata, context);
      if (Survey == null) {
        return false;
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
    EipTSurvey survey = null;
    try {
      Date nowDate = Calendar.getInstance().getTime();
      // 新規オブジェクトモデル
      survey = Database.create(EipTSurvey.class);

      survey.setName(name.getValue());
      survey.setComment(comment.getValue());

      for (String option : optionList) {
        EipTSurveyOption _option = Database.create(EipTSurveyOption.class);
        _option.setEipTSurvey(survey);
        _option.setName(option);
      }

      if (SurveyUtils.OPTION_TYPE_MULTIPLE.equals(optionType.getValue())) {
        survey.setOptionType(SurveyUtils.OPTION_TYPE_MULTIPLE);
      } else {
        survey.setOptionType(SurveyUtils.OPTION_TYPE_SINGLE);
      }

      if (SurveyUtils.RESPONDENT_TYPE_MEMBER.equals(respondentType.getValue())) {
        survey.setRespondentType(SurveyUtils.RESPONDENT_TYPE_MEMBER);
        for (ALEipUser user : respondents) {
          EipTSurveyRespondent respondent =
            Database.create(EipTSurveyRespondent.class);
          respondent.setEipTSurvey(survey);
          respondent.setResponseFlag("F");
          respondent.setUserId((int) user.getUserId().getValue());
        }
      } else {
        EipTSurveyRespondent respondent =
          Database.create(EipTSurveyRespondent.class);
        respondent.setEipTSurvey(survey);
        respondent.setUserId(SurveyUtils.RESPONDENT_DUMMY);
        respondent.setResponseFlag("F");
        survey.setRespondentType(SurveyUtils.RESPONDENT_TYPE_ALL);
      }

      survey.setOpenDate(nowDate);
      survey.setOpenFlag(SurveyUtils.OPEN_FLAG_AUTO_OPENED);

      survey.setCloseFlag(SurveyUtils.CLOSE_FLAG_NOT_CLOSED);
      survey.setCloseDate(closeDate.getValue());

      survey.setEditableFlag("T");
      survey.setCreateUserId(ALEipUtils.getUserId(rundata));
      survey.setAnonymousFlag(anonymousFlag.getValue());

      survey.setCreateDate(nowDate);
      survey.setUpdateDate(nowDate);

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
  protected boolean updateFormData(RunData rundata, Context context,
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
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * @return name
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * @param name
   *          セットする name
   */
  public void setName(String str) {
    this.name.setValue(str);
  }

  /**
   * @return createUser
   */
  public ALEipUser getCreateUser() {
    return createUser;
  }

  /**
   * @param createUser
   *          セットする createUser
   */
  public void setCreateUser(ALEipUser user) {
    this.createUser = user;
  }

  /**
   * @return openDate
   */
  public ALDateTimeField getOpenDate() {
    return openDate;
  }

  /**
   * @param openDate
   *          セットする openDate
   */
  public void setOpenDate(Date date) {
    this.openDate.setValue(date);
  }

  /**
   * @return closeDate
   */
  public ALDateTimeField getCloseDate() {
    return closeDate;
  }

  /**
   * @param closeDate
   *          セットする closeDate
   */
  public void setCloseDate(Date date) {
    this.closeDate.setValue(date);
  }

  /**
   * @return editableFlag
   */
  public ALStringField getEditableFlag() {
    return editableFlag;
  }

  /**
   * @param editableFlag
   *          セットする editableFlag
   */
  public void setEditableFlag(String str) {
    this.editableFlag.setValue(str);
  }

  /**
   * @return options
   */
  public ALStringField getOptions() {
    return options;
  }

  /**
   * @param options
   *          セットする options
   */
  public void setOptions(String str) {
    this.options.setValue(str);
  }

  /**
   * @return respondents
   */
  public List<ALEipUser> getRespondents() {
    return respondents;
  }

  /**
   * @param respondents
   *          セットする respondents
   */
  public void setRespondents(List<ALEipUser> respondents) {
    this.respondents = respondents;
  }

  /**
   * @param anonymousFlag
   *          セットする anonymousFlag
   */
  public void setAnonymousFlag(String str) {
    this.anonymousFlag.setValue(str);
  }

  /**
   * @return comment
   */
  public ALStringField getComment() {
    return comment;
  }

  /**
   * @param comment
   *          セットする comment
   */
  public void setComment(String str) {
    this.comment.setValue(str);
  }

  /**
   * @return respondentType
   */
  public ALStringField getRespondentType() {
    return respondentType;
  }

  /**
   * @param respondentType
   *          セットする respondentType
   */
  public void setRespondentType(String str) {
    this.respondentType.setValue(str);
  }

  /**
   * @return optionType
   */
  public ALStringField getOptionType() {
    return optionType;
  }

  /**
   * @param optionType
   *          セットする optionType
   */
  public void setOptionType(String str) {
    this.optionType.setValue(str);
  }

}
