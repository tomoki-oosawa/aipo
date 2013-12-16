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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;

class SurveyListDateField extends ALDateTimeField {
  /**
   * 
   */
  private static final long serialVersionUID = -4167331118973029695L;

  @Override
  public String toString() {
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日");
      format.setTimeZone(TimeZone.getDefault());
      return format.format(getValue());
    } catch (Exception e) {
      return super.toString();
    }
  }
}

/**
 * WebデータベースのResultDataです。 <BR>
 * 
 */
public class SurveyResultData implements ALData {

  /** ID */
  private ALNumberField surveyId;

  /** タイトル */
  private ALStringField name;

  /** 内容 */
  private ALStringField comment;

  /** 回答形式 */
  private ALStringField optionType;

  /** 作成者 */
  private ALEipUser createUser;

  /** 作成日時 */
  private ALDateTimeField createdDate;

  /** 回答期日 */
  private ALDateTimeField closeDate;

  /** 締切フラグ */
  private ALStringField closeFlag;

  /** 編集フラグ */
  private ALStringField editableFlag;

  /** 回答率 */
  private ALStringField responseRate;

  /** 回答状況 */
  private ALStringField responseFlag;

  /** 回答項目 */
  private List<ALStringField> options;

  /** 回答対象者 */
  private List<ALEipUser> respondents;

  private ALStringField respondentType;

  /** 匿名回答 */
  private ALStringField anonymousFlag;

  /**
   * 
   */
  @Override
  public void initField() {
    surveyId = new ALNumberField();
    name = new ALStringField();
    comment = new ALStringField();
    optionType = new ALStringField();
    createdDate = new SurveyListDateField();
    closeDate = new SurveyListDateField();
    closeFlag = new ALStringField();
    editableFlag = new ALStringField();
    responseRate = new ALStringField();
    responseFlag = new ALStringField();
    options = new ArrayList<ALStringField>();
    respondentType = new ALStringField();
    respondents = new ArrayList<ALEipUser>();
    anonymousFlag = new ALStringField();
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
   * @return createdDate
   */
  public ALDateTimeField getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate
   *          セットする createdDate
   */
  public void setCreatedDate(Date date) {
    this.createdDate.setValue(date);
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
   * @return closeFlag
   */
  public ALStringField getCloseFlag() {
    return closeFlag;
  }

  /**
   * @param closeFlag
   *          セットする closeFlag
   */
  public void setCloseFlag(String str) {
    this.closeFlag.setValue(str);
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
   * @return responseRate
   */
  public ALStringField getResponseRate() {
    return responseRate;
  }

  /**
   * @param responseRate
   *          セットする responseRate
   */
  public void setResponseRate(String str) {
    this.responseRate.setValue(str);
  }

  /**
   * @return responseFlag
   */
  public ALStringField getResponseFlag() {
    return responseFlag;
  }

  /**
   * @param responseFlag
   *          セットする responseFlag
   */
  public void setResponseFlag(String str) {
    this.responseFlag.setValue(str);
  }

  /**
   * @return options
   */
  public List<ALStringField> getOptions() {
    return options;
  }

  /**
   * @param options
   *          セットする options
   */
  public void setOptions(List<ALStringField> options) {
    this.options = options;
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
   * @return anonymousFlag
   */
  public ALStringField getAnonymousFlag() {
    return anonymousFlag;
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

  /**
   * @return surveyId
   */
  public ALNumberField getSurveyId() {
    return surveyId;
  }

  /**
   * @param surveyId
   *          セットする surveyId
   */
  public void setSurveyId(long value) {
    this.surveyId.setValue(value);
  }

}
