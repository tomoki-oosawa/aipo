package com.aimluck.eip.cayenne.om.portlet;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTExtTimecard;

public class EipTExtTimecard extends _EipTExtTimecard {

  /** 外出／復帰時間を記録できる数 */
  public static final int OUTGOING_COMEBACK_PER_DAY = 5;

  /** タイプ「出勤」*/
  public static final String TYPE_WORK = "P";

  /** タイプ「欠勤」*/
  public static final String TYPE_ABSENT = "A";

  /** タイプ「有休」*/
  public static final String TYPE_HOLIDAY = "H";

  /** タイプ「代休」*/
  public static final String TYPE_COMPENSATORY = "C";

  /** タイプ「その他」*/
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
  public void setOutgoingTime(Date date, int num)
  {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return;
    }

    try {
      Class timecard = EipTExtTimecard.class;
      Method setMethod = timecard.getMethod("setOutgoingTime"+num, new Class[]{Date.class});
      setMethod.invoke(this, new Object[] {date});
    } catch (Exception e) {
      return;
    }
  }


  /**
   * 番号を指定して復帰時間を設定
   *
   * @param date
   * @param num
   */
  public void setComebackTime(Date date, int num)
  {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return;
    }

    try {
      Class timecard = EipTExtTimecard.class;
      Method setMethod = timecard.getMethod("setComebackTime"+num, new Class[]{Date.class});
      setMethod.invoke(this, new Object[] {date});
    } catch (Exception e) {
      return;
    }
  }

  /**
   * 番号を指定して外出時間を取得
   *
   * @param date
   * @param num
   */
  public Date getOutgoingTime(int num)
  {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return null;
    }

    try {
      Class timecard = EipTExtTimecard.class;
      Method setMethod = timecard.getMethod("getOutgoingTime"+num);
      Date date = (Date)setMethod.invoke(this);
      return date;
    } catch (Exception e) {
      return null;
    }
  }


  /**
   * 番号を指定して復帰時間を取得
   *
   * @param date
   * @param num
   */
  public Date getComebackTime(int num)
  {
    if (num > OUTGOING_COMEBACK_PER_DAY) {
      return null;
    }

    try {
      Class timecard = EipTExtTimecard.class;
      Method setMethod = timecard.getMethod("getComebackTime"+num);
      Date date = (Date)setMethod.invoke(this);
      return date;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 外出時間を設定します。
   *
   * @param date
   */
  public void setNewOutgoingTime(Date date)
  {

    for (int i = 1 ; i <= OUTGOING_COMEBACK_PER_DAY ; i++)  {
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
  public void setNewComebackTime(Date date)
  {
    int i;
    for (i = 1 ; i < OUTGOING_COMEBACK_PER_DAY ; i++)  {
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
