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
package com.aimluck.eip.common;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

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

  public static final String PHOTO_SMARTPHONE = "PHOTO_SMARTPHONE";

  public static final String CREATED_USER_ID = "CREATED_USER_ID";

  public static final String UPDATED_USER_ID = "UPDATED_USER_ID";

  public static final String CREATED = "CREATED";

  public static final String MODIFIED = "MODIFIED";

  public static final String PHOTO_MODIFIED = "PHOTO_MODIFIED";

  public static final String PHOTO_MODIFIED_SMARTPHONE =
    "PHOTO_MODIFIED_SMARTPHONE";

  public static final String HAS_PHOTO = "HAS_PHOTO";

  public static final String PHOTO_TYPE = "PHOTO_TYPE";

  public static final String HAS_PHOTO_SMARTPHONE = "HAS_PHOTO_SMARTPHONE";

  public static final String MIGRATE_VERSION = "MIGRATE_VERSION";

  public static final String CODE = "CODE";

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALBaseUser.class.getName());

  // セッションに保存されないデータ
  transient private Map<String, Object> map = null;

  /**
   *
   *
   */
  public ALBaseUser() {
    super();
    map = new HashMap<String, Object>();
  }

  public Object getImperm(String name) {
    return map.get(name);
  }

  public Map<String, Object> getImpermStorage() {
    if (this.map == null) {
      this.map = new HashMap<String, Object>();
    }
    return this.map;
  }

  public void setImperm(String name, Object value) {
    if (value == null) {
      map.put(name, "");
    } else {
      map.put(name, value);
    }
  }

  /**
   *
   * @return
   */
  public String getInTelephone() {
    return (String) getImperm(IN_TELEPHONE);
  }

  /**
   *
   * @param str
   */
  public void setInTelephone(String str) {
    setImperm(IN_TELEPHONE, str);
  }

  /**
   *
   * @return
   */
  public String getOutTelephone() {
    return (String) getImperm(OUT_TELEPHONE);
  }

  /**
   *
   * @param str
   */
  public void setOutTelephone(String str) {
    setImperm(OUT_TELEPHONE, str);
  }

  /**
   *
   * @return
   */
  public String getCellularPhone() {
    return (String) getImperm(CELLULAR_PHONE);
  }

  /**
   *
   * @param str
   */
  public void setCellularPhone(String str) {
    setImperm(CELLULAR_PHONE, str);
  }

  /**
   *
   * @return
   */
  public String getCellularMail() {
    return (String) getImperm(CELLULAR_MAIL);
  }

  /**
   *
   * @param str
   */
  public void setCellularMail(String str) {
    setImperm(CELLULAR_MAIL, str);
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
    return (String) getImperm(FIRST_NAME_KANA);
  }

  /**
   *
   * @param str
   */
  public void setFirstNameKana(String str) {
    setImperm(FIRST_NAME_KANA, str);
  }

  /**
   *
   * @return
   */
  public String getLastNameKana() {
    return (String) getImperm(LAST_NAME_KANA);
  }

  /**
   *
   * @param str
   */
  public void setLastNameKana(String str) {
    setImperm(LAST_NAME_KANA, str);
  }

  /**
   *
   * @return byte[]
   */
  public byte[] getPhoto() {
    Object obj = getImperm(PHOTO);

    if (obj instanceof byte[]) {
      return (byte[]) obj;
    }

    if (obj == null || "".equals(obj)) {
      return null;
    }

    try {
      return ((String) obj).getBytes(ALEipConstants.DEF_CONTENT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      logger.error("ALBaseUser.getPhoto", e);
      return ((String) obj).getBytes();
    }
  }

  /**
   *
   * @return byte[]
   */
  public byte[] getPhotoSmartphone() {
    Object obj = getImperm(PHOTO_SMARTPHONE);

    if (obj instanceof byte[]) {
      return (byte[]) obj;
    }

    if (obj == null || "".equals(obj)) {
      return null;
    }

    try {
      return ((String) obj).getBytes(ALEipConstants.DEF_CONTENT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      logger.error("ALBaseUser.getPhotoSmartphone", e);
      return ((String) obj).getBytes();
    }
  }

  /**
   *
   * @param v
   */
  public void setPhoto(byte[] v) {
    setImperm(PHOTO, v);
  }

  /**
   *
   * @param b
   */
  public void setPhotoSmartphone(byte[] b) {
    setImperm(PHOTO_SMARTPHONE, b);
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
    String companyName = "";
    Map<Integer, ALEipCompany> companyMap =
      ALEipManager.getInstance().getCompanyMap();
    ALEipCompany company = companyMap.get(Integer.valueOf(id));
    if (company != null) {
      companyName = company.getCompanyName().toString();
    }
    return companyName;
  }

  /**
   * 携帯電話の固有 ID を取得する．
   */
  public String getCelluarUId() {
    return (String) getImperm(CELLULAR_UID);
  }

  /**
   *
   */
  public void setCelluarUId(String str) {
    setImperm(CELLULAR_UID, str);
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
    return str
      .append(cal.get(Calendar.YEAR))
      .append(ALLocalizationUtils.getl10n("COMMON_YEAR"))
      .append((cal.get(Calendar.MONTH) + 1))
      .append(ALLocalizationUtils.getl10n("COMMON_MONTH"))
      .append(cal.get(Calendar.DATE))
      .append(ALLocalizationUtils.getl10n("COMMON_DAY"))
      .append(cal.get(Calendar.HOUR_OF_DAY))
      .append(ALLocalizationUtils.getl10n("COMMON_HOUR"))
      .append(cal.get(Calendar.MINUTE))
      .append(ALLocalizationUtils.getl10n("COMMON_MINUTE"))
      .toString();
  }

  /**
   * 指定されたユーザが管理者権限を持っているかを返します。
   *
   * @return
   */
  public boolean isAdmin() {
    return ALEipUtils.isAdmin(Integer.parseInt(this.getUserId()));
  }

  public boolean hasPhoto() {
    String hasPhoto = (String) getPerm(HAS_PHOTO);
    return "T".equals(hasPhoto) || "N".equals(hasPhoto);
  }

  public String hasPhotoString() {
    return (String) getPerm(HAS_PHOTO);
  }

  public boolean hasPhotoSmartphone() {
    String hasPhotoSmartphone = (String) getPerm(HAS_PHOTO_SMARTPHONE);
    return "T".equals(hasPhotoSmartphone) || "N".equals(hasPhotoSmartphone);
  }

  public String hasPhotoSmartphoneString() {
    return (String) getPerm(HAS_PHOTO_SMARTPHONE);
  }

  /**
   *
   */
  public void setHasPhoto(String hasPhoto) {
    setPerm(HAS_PHOTO, hasPhoto);
  }

  /**
  *
  */
  public void setHasPhotoSmartphone(String hasPhotoSmartphone) {
    setPerm(HAS_PHOTO_SMARTPHONE, hasPhotoSmartphone);
  }

  /**
   * @param d
   */
  public void setPhotoModified(Date d) {
    setPerm(PHOTO_MODIFIED, d);
  }

  /**
   * @param dd
   */
  public void setPhotoModifiedSmartphone(Date dd) {
    setPerm(PHOTO_MODIFIED_SMARTPHONE, dd);
  }

  /**
   * @return
   */
  public Date getPhotoModified() {
    return (Date) (getPerm(PHOTO_MODIFIED));
  }

  /**
   * @return
   */
  public Date getPhotoModifiedSmartphone() {
    if (getPerm(PHOTO_MODIFIED_SMARTPHONE) == null
      || "".equals(getPerm(PHOTO_MODIFIED_SMARTPHONE))) {
      return null;
    }
    return (Date) (getPerm(PHOTO_MODIFIED_SMARTPHONE));
  }

  /**
   *
   */
  public int getMigrateVersion() {
    return ((Integer) (getPerm(MIGRATE_VERSION))).intValue();
  }

  /**
   *
   */
  public void setMigrateVersion(int id) {
    setPerm(MIGRATE_VERSION, Integer.valueOf(id));
  }

  /**
   *
   */
  public String getCode() {
    return (String) getPerm(CODE);
  }

  /**
   *
   */
  public void setCode(String code) {
    setPerm(CODE, code);
  }

  @Override
  public Date getPasswordChanged() {
    try {
      return super.getPasswordChanged();
    } catch (ClassCastException ignore) {
      return null;
    }
  }
}
