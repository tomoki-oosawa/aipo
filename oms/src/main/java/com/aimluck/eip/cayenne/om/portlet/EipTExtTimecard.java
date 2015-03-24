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
package com.aimluck.eip.cayenne.om.portlet;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.cayenne.ObjectId;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTExtTimecard;

public class EipTExtTimecard extends _EipTExtTimecard {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(EipTExtTimecard.class.getName());

  /** 外出／復帰時間を記録できる数 */
  public static final int OUTGOING_COMEBACK_PER_DAY = 5;

  /** タイプ「出勤」 */
  public static final String TYPE_WORK = "P";

  /** タイプ「欠勤」 */
  public static final String TYPE_ABSENT = "A";

  /** タイプ「有休」 */
  public static final String TYPE_HOLIDAY = "H";

  /** タイプ「代休」 */
  public static final String TYPE_COMPENSATORY = "C";

  /** タイプ「その他」 */
  public static final String TYPE_ETC = "E";

  public Integer getExtTimecardId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(TIMECARD_ID_PK_COLUMN);
      if (obj instanceof Long) {
        Long value = (Long) obj;
        return Integer.valueOf(value.intValue());
      } else {
        return (Integer) obj;
      }
    } else {
      return null;
    }
  }

  public void setExtTimecardId(String id) {
    setObjectId(new ObjectId("EipTExtTimecard", TIMECARD_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

  /**
   * 番号を指定して外出時間を設定
   * 
   * @param date
   * @param num
   */
  public void setOutgoingTime(Date date, int num) {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return;
    }

    try {
      Class<EipTExtTimecard> timecard = EipTExtTimecard.class;
      Method setMethod =
        timecard.getMethod("setOutgoingTime" + num, new Class[] { Date.class });
      setMethod.invoke(this, new Object[] { date });
    } catch (Exception e) {
      logger.error(e, e);
      return;
    }
  }

  /**
   * 番号を指定して復帰時間を設定
   * 
   * @param date
   * @param num
   */
  public void setComebackTime(Date date, int num) {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return;
    }

    try {
      Class<EipTExtTimecard> timecard = EipTExtTimecard.class;
      Method setMethod =
        timecard.getMethod("setComebackTime" + num, new Class[] { Date.class });
      setMethod.invoke(this, new Object[] { date });
    } catch (Exception e) {
      logger.error(e, e);
      return;
    }
  }

  /**
   * 番号を指定して外出時間を取得
   * 
   * @param date
   * @param num
   */
  public Date getOutgoingTime(int num) {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return null;
    }

    try {
      Class<EipTExtTimecard> timecard = EipTExtTimecard.class;
      Method setMethod = timecard.getMethod("getOutgoingTime" + num);
      Date date = (Date) setMethod.invoke(this);
      return date;
    } catch (Exception e) {
      logger.error(e, e);
      return null;
    }
  }

  /**
   * 番号を指定して復帰時間を取得
   * 
   * @param date
   * @param num
   */
  public Date getComebackTime(int num) {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return null;
    }

    try {
      Class<EipTExtTimecard> timecard = EipTExtTimecard.class;
      Method setMethod = timecard.getMethod("getComebackTime" + num);
      Date date = (Date) setMethod.invoke(this);
      return date;
    } catch (Exception e) {
      logger.error(e, e);
      return null;
    }
  }

  /**
   * 外出時間を設定します。
   * 
   * @param date
   */
  public void setNewOutgoingTime(Date date) {

    for (int i = 1; i <= OUTGOING_COMEBACK_PER_DAY; i++) {
      if (getComebackTime(i) == null) {
        setOutgoingTime(date, i);
        break;
      }
    }
  }

  /**
   * 復帰時間を設定します。
   * 
   * @param date
   */
  public void setNewComebackTime(Date date) {
    int i;
    for (i = 1; i < OUTGOING_COMEBACK_PER_DAY; i++) {
      if (getOutgoingTime(i + 1) == null) {
        setComebackTime(date, i);
        break;
      }
    }
    if (i == OUTGOING_COMEBACK_PER_DAY) {
      setComebackTime(date, OUTGOING_COMEBACK_PER_DAY);
    }
  }
}
