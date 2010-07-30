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
package com.aimluck.eip.account;

import java.security.SecureRandom;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPosition;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントの検索データを管理するためのクラスです。 <br />
 */
public class AccountEditSelectData extends ALAbstractSelectData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AccountEditSelectData.class.getName());

  /**
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<Object> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context) {
    ALBaseUser baseUser = (ALBaseUser) rundata.getUser();
    return baseUser;
  }

  /**
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    return null;
  }

  /**
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj) {
    try {
      ALBaseUser record = (ALBaseUser) obj;

      List<String> postNames = ALEipUtils.getPostNameList(Integer
          .valueOf(record.getUserId()));

      AccountResultData rd = new AccountResultData();
      rd.initField();
      rd.setUserId(Integer.valueOf(record.getUserId()).intValue());
      rd.setUserName(record.getUserName());
      rd.setName(new StringBuffer().append(record.getLastName()).append(" ")
          .append(record.getFirstName()).toString());
      rd.setNameKana(new StringBuffer().append(record.getLastNameKana())
          .append(" ").append(record.getFirstNameKana()).toString());
      rd.setEmail(record.getEmail());
      rd.setOutTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());

      if (record.getPhoto() != null) {
        rd.setHasPhoto(true);
      } else {
        rd.setHasPhoto(false);
      }

      rd.setPostNameList(postNames);
      rd.setPositionName(getPositionName(record.getPositionId()));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   *
   * @param id
   * @return
   */
  @SuppressWarnings("unused")
  private String getPostName(int id) {
    if (ALEipManager.getInstance().getPostMap()
        .containsKey(Integer.valueOf(id))) {
      return ((ALEipPost) ALEipManager.getInstance().getPostMap()
          .get(Integer.valueOf(id))).getPostName().getValue();
    }
    return null;
  }

  /**
   *
   * @param id
   * @return
   */
  private String getPositionName(int id) {
    if (ALEipManager.getInstance().getPositionMap()
        .containsKey(Integer.valueOf(id))) {
      return ((ALEipPosition) ALEipManager.getInstance().getPositionMap()
          .get(Integer.valueOf(id))).getPositionName().getValue();
    }
    return null;
  }

  public int getRandomNum() {
    SecureRandom random = new SecureRandom();
    return (random.nextInt() * 100);
  }
}
