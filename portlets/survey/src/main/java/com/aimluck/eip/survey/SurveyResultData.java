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
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;

/**
 * WebデータベースのResultDataです。 <BR>
 * 
 */
public class SurveyResultData implements ALData {

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

  /** 受付開始日 */
  private ALDateTimeField openDate;

  /** 回答期日 */
  private ALDateTimeField closeDate;

  /** 受付フラグ */
  private ALStringField openFlag;

  /** 締切フラグ */
  private ALStringField closeFlag;

  /** 編集フラグ */
  private ALStringField editableFlag;

  /** 回答率 */
  private ALStringField responseRate;

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
    name = new ALStringField();
    comment = new ALStringField();
    optionType = new ALStringField();
    createdDate = new ALDateTimeField();
    openDate = new ALDateTimeField();
    closeDate = new ALDateTimeField();
    openFlag = new ALStringField();
    closeFlag = new ALStringField();
    editableFlag = new ALStringField();
    responseRate = new ALStringField();
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
   * @return openFlag
   */
  public ALStringField getOpenFlag() {
    return openFlag;
  }

  /**
   * @param openFlag
   *          セットする openFlag
   */
  public void setOpenFlag(String str) {
    this.openFlag.setValue(str);
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

}
