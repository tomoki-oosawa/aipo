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
package com.aimluck.eip.message;

import java.io.Serializable;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.common.ALData;

/**
 *
 */
public class MessageUserResultData implements ALData, Serializable {

  private static final long serialVersionUID = 1884204884214262528L;

  private ALNumberField userId;

  private ALStringField lastName;

  private ALStringField firstName;

  private ALStringField lastNameKana;

  private ALStringField firstNameKana;

  private Long photoModified;

  private boolean hasPhoto = false;

  private boolean isOwner = false;

  /**
   *
   */
  @Override
  public void initField() {
    userId = new ALNumberField();
    lastName = new ALStringField();
    firstName = new ALStringField();
    lastNameKana = new ALStringField();
    firstNameKana = new ALStringField();
  }

  /**
   * @param userId
   *          セットする userId
   */
  public void setUserId(long userId) {
    this.userId.setValue(userId);
  }

  /**
   * @return userId
   */
  public ALNumberField getUserId() {
    return userId;
  }

  /**
   * @param lastName
   *          セットする lastName
   */
  public void setLastName(String lastName) {
    this.lastName.setValue(lastName);
  }

  /**
   * @return lastName
   */
  public ALStringField getLastName() {
    return lastName;
  }

  /**
   * @param firstName
   *          セットする firstName
   */
  public void setFirstName(String firstName) {
    this.firstName.setValue(firstName);
  }

  /**
   * @return firstName
   */
  public ALStringField getFirstName() {
    return firstName;
  }

  /**
   * @param lastNameKana
   *          セットする lastNameKana
   */
  public void setLastNameKana(String lastNameKana) {
    this.lastNameKana.setValue(lastNameKana);
  }

  /**
   * @return lastName
   */
  public ALStringField getLastNameKana() {
    return lastNameKana;
  }

  /**
   * @param firstNameKana
   *          セットする firstNameKana
   */
  public void setFirstNameKana(String firstNameKana) {
    this.firstNameKana.setValue(firstNameKana);
  }

  /**
   * @return firstNameKana
   */
  public ALStringField getFirstNameKana() {
    return firstNameKana;
  }

  /**
   * @param hasPhoto
   *          セットする hasPhoto
   */
  public void setHasPhoto(boolean hasPhoto) {
    this.hasPhoto = hasPhoto;
  }

  /**
   * @return hasPhoto
   */
  public boolean isHasPhoto() {
    return hasPhoto;
  }

  /**
   * @param isOwner
   *          セットする isOwner
   */
  public void setOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  /**
   * @return isOwner
   */
  public boolean isOwner() {
    return isOwner;
  }

  public ALStringField getIndex() {
    String value =
      ALStringUtil.convertKatakana2Hiragana(ALStringUtil
        .convertH2ZKana(lastNameKana.getValue()));
    ALStringField field = new ALStringField();
    if (value.length() > 0) {
      field.setValue(new StringBuilder()
        .append(
          ALStringUtil.convertDakuon2Seion(String.valueOf(value.charAt(0))))
        .toString());
    } else {
      field.setValue("#");
    }
    return field;
  }

  /**
   * @param photoModified
   *          セットする photoModified
   */
  public void setPhotoModified(Long photoModified) {
    this.photoModified = photoModified;
  }

  /**
   * @return photoModified
   */
  public Long getPhotoModified() {
    return photoModified;
  }

}
