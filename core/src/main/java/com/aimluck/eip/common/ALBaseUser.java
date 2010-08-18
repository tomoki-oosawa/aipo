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
package com.aimluck.eip.common;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * ユーザー情報を表すクラスです。 <br />
 * 
 */
public class ALBaseUser extends
    org.apache.jetspeed.om.security.BaseJetspeedUser {

  /**
   *
   */
  private static final long serialVersionUID = -5919528447748101476L;

  public static final String IN_TELEPHONE = "IN_TELEPHONE";

  public static final String OUT_TELEPHONE = "OUT_TELEPHONE";

  public static final String CELLULAR_PHONE = "CELLULAR_PHONE";

  public static final String CELLULAR_MAIL = "CELLULAR_MAIL";

  public static final String CELLULAR_UID = "CELLULAR_UID";

  public static final String COMPANY_ID = "COMPANY_ID";

  public static final String POST_ID = "POST_ID";

  public static final String POSITION_ID = "POSITION_ID";

  public static final String FIRST_NAME_KANA = "FIRST_NAME_KANA";

  public static final String LAST_NAME_KANA = "LAST_NAME_KANA";

  public static final String PHOTO = "PHOTO";

  public static final String CREATED_USER_ID = "CREATED_USER_ID";

  public static final String UPDATED_USER_ID = "UPDATED_USER_ID";

  public static final String CREATED = "CREATED";

  public static final String MODIFIED = "MODIFIED";

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALBaseUser.class.getName());

  /**
   *
   *
   */
  public ALBaseUser() {
  }

  /**
   * 
   * @return
   */
  public String getInTelephone() {
    return (String) getPerm(IN_TELEPHONE);
  }

  /**
   * 
   * @param str
   */
  public void setInTelephone(String str) {
    setPerm(IN_TELEPHONE, str);
  }

  /**
   * 
   * @return
   */
  public String getOutTelephone() {
    return (String) getPerm(OUT_TELEPHONE);
  }

  /**
   * 
   * @param str
   */
  public void setOutTelephone(String str) {
    setPerm(OUT_TELEPHONE, str);
  }

  /**
   * 
   * @return
   */
  public String getCellularPhone() {
    return (String) getPerm(CELLULAR_PHONE);
  }

  /**
   * 
   * @param str
   */
  public void setCellularPhone(String str) {
    setPerm(CELLULAR_PHONE, str);
  }

  /**
   * 
   * @return
   */
  public String getCellularMail() {
    return (String) getPerm(CELLULAR_MAIL);
  }

  /**
   * 
   * @param str
   */
  public void setCellularMail(String str) {
    setPerm(CELLULAR_MAIL, str);
  }

  /**
   * 
   * @return
   */
  public int getCompanyId() {
    return ((Integer) (getPerm(COMPANY_ID))).intValue();
  }

  /**
   * 
   * @param str
   */
  public void setCompanyId(int id) {
    setPerm(COMPANY_ID, Integer.valueOf(id));
  }

  /**
   * 
   * @return
   */
  public int getPostId() {
    return ((Integer) (getPerm(POST_ID))).intValue();
  }

  /**
   * 
   * @param str
   */
  public void setPostId(int id) {
    setPerm(POST_ID, Integer.valueOf(id));
  }

  /**
   * 
   * @return
   */
  public int getPositionId() {
    return ((Integer) (getPerm(POSITION_ID))).intValue();
  }

  /**
   * 
   * @param str
   */
  public void setPositionId(int id) {
    setPerm(POSITION_ID, Integer.valueOf(id));
  }

  /**
   * 
   * @return
   */
  public String getFirstNameKana() {
    return (String) getPerm(FIRST_NAME_KANA);
  }

  /**
   * 
   * @param str
   */
  public void setFirstNameKana(String str) {
    setPerm(FIRST_NAME_KANA, str);
  }

  /**
   * 
   * @return
   */
  public String getLastNameKana() {
    return (String) getPerm(LAST_NAME_KANA);
  }

  /**
   * 
   * @param str
   */
  public void setLastNameKana(String str) {
    setPerm(LAST_NAME_KANA, str);
  }

  /**
   * 
   * @return byte[]
   */
  public byte[] getPhoto() {
    Object obj = getPerm(PHOTO);

    if (obj instanceof byte[]) {
      return (byte[]) obj;
    }

    if (obj == null || "".equals(obj)) {
      return null;
    }

    return ((String) obj).getBytes();
  }

  /**
   * 
   * @param v
   */
  public void setPhoto(byte[] b) {
    setPerm(PHOTO, b);
  }

  /**
   * @return
   */
  public int getCreatedUserId() {
    return ((Integer) (getPerm(CREATED_USER_ID))).intValue();
  }

  /**
   * @param id
   */
  public void setCreatedUserId(int id) {
    setPerm(CREATED_USER_ID, Integer.valueOf(id));
  }

  /**
   * @return
   */
  public int getUpdatedUserId() {
    return ((Integer) (getPerm(UPDATED_USER_ID))).intValue();
  }

  /**
   * @param id
   */
  public void setUpdatedUserId(int id) {
    setPerm(UPDATED_USER_ID, Integer.valueOf(id));
  }

  /**
   * @param d
   */
  public void setCreated(Date d) {
    setPerm(CREATED, d);
  }

  /**
   * @return
   */
  public Date getCreated() {
    return (Date) (getPerm(CREATED));
  }

  /**
   * @param d
   */
  public void setModified(Date d) {
    setPerm(MODIFIED, d);
  }

  /**
   * @return
   */
  public Date getModified() {
    return (Date) (getPerm(MODIFIED));
  }

  /**
   * 会社名を取得します。
   * 
   * @param id
   *          会社ID
   * @return 会社名
   */
  public String getCompanyName(int id) {
    Map<Integer, ALEipCompany> companyMap = ALEipManager.getInstance()
      .getCompanyMap();
    ALEipCompany company = companyMap.get(Integer.valueOf(id));
    return company.getCompanyName().toString();
  }

  /**
   * 携帯電話の固有 ID を取得する．
   */
  public String getCelluarUId() {
    return (String) getPerm(CELLULAR_UID);
  }

  /**
   *
   */
  public void setCelluarUId(String str) {
    setPerm(CELLULAR_UID, str);
  }

  /**
   * 最終アクセス時間を取得します。
   * 
   * @return
   */
  public String getLastAccessTime() {
    StringBuffer str = new StringBuffer();
    Calendar cal = Calendar.getInstance();
    cal.setTime(super.getLastAccessDate());
    return str.append(cal.get(Calendar.YEAR)).append("年")
      .append((cal.get(Calendar.MONTH) + 1)).append("月")
      .append(cal.get(Calendar.DATE)).append("日 ")
      .append(cal.get(Calendar.HOUR_OF_DAY)).append("時")
      .append(cal.get(Calendar.MINUTE)).append("分").toString();
  }

}
