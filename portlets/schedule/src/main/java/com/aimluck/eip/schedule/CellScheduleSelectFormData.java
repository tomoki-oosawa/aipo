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
package com.aimluck.eip.schedule;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 * 
 */
public class CellScheduleSelectFormData extends ALAbstractFormData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleSelectFormData.class.getName());

  /** <code>end_date</code> 表示日時 */
  protected ALCellDateField view_date;

  protected String view_date_str;

  /** <code>currentYear</code> 現在の年 */
  protected int currentYear;

  /*
   *
   */
  @Override
  public void initField() {
  }

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    // 指定日時
    view_date = new ALCellDateField();
    view_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_DATE"));
    view_date.setNotNull(true);

    view_date_str = "";

    if (rundata.getParameters().getString("view_date") == null) {
      view_date.setValue(now);
      // 現在の年（年を選択するリストボックスに利用）
      currentYear = Calendar.getInstance().get(Calendar.YEAR);
    } else {
      String str = rundata.getParameters().getString("view_date");
      if (str.indexOf("-") == -1) {
        if (str.length() == 0) {
        } else if (str.length() != 8) {
          view_date_str = "0";
        } else {
          StringBuffer sb = new StringBuffer();
          sb
            .append(str.substring(0, 4))
            .append("-")
            .append(str.substring(4, 6))
            .append("-")
            .append(str.substring(6, 8));
          view_date_str = sb.toString();
        }
      } else {
        ALDateTimeField date = new ALDateTimeField("yyyy-MM-dd");
        date.setValue(str);
        view_date.setValue(date.getValue());
        currentYear = Integer.parseInt(date.getYear());
        rundata.getParameters().remove("view_date");
      }
    }
  }

  /**
   *
   */
  @Override
  protected void setValidator() {

  }

  /**
   * 
   * @param msgList
   * @return
   * @throws ALDBErrorException
   * @throws ALPageNotFoundException
   */
  @Override
  protected boolean validate(List<String> msgList) throws ALDBErrorException,
      ALPageNotFoundException {
    if (view_date_str.length() == 0) {
      msgList.add(ALLocalizationUtils.getl10n("SCHEDULE_TYPE_DATE"));
      return false;
    }

    view_date.setValue(view_date_str);
    List<String> msgList2 = new ArrayList<String>();
    if (!view_date.validate(msgList2)) {
      msgList.addAll(msgList2);
      return false;
    }

    // 表示日時
    // view_date.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * 入力データを検証する．
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  public boolean doCheck(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      action.setMode(ALEipConstants.MODE_NEW_FORM);
      setMode(action.getMode());
      List<String> msgList = new ArrayList<String>();
      setValidator();

      boolean res = (validate(msgList));

      action.setResultData(this);
      action.addErrorMessages(msgList);
      action.putData(rundata, context);
      return res;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }
  }

  /**
   * 
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
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {

    return false;
  }

  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    Field[] fields = this.getClass().getDeclaredFields();
    boolean res =
      ScheduleUtils
        .setFormDataDelegate(rundata, context, this, fields, msgList);
    return res;
  }

  @SuppressWarnings("unused")
  private EipTScheduleMap getScheduleMap(List<EipTScheduleMap> scheduleMaps,
      int userid) {
    EipTScheduleMap map = null;
    int size = scheduleMaps.size();
    for (int i = 0; i < size; i++) {
      map = scheduleMaps.get(i);
      if (map.getUserId().intValue() == userid) {
        return map;
      }
    }
    return null;
  }

  public String getViewDateStr() {
    StringBuffer sb = new StringBuffer();
    sb
      .append(view_date.getYear())
      .append("-")
      .append(view_date.getMonth())
      .append("-")
      .append(view_date.getDay());
    return sb.toString();
  }

  public ALCellDateField getViewDate() {
    return view_date;
  }

  public int getInt(long num) {
    return (int) num;
  }

  /**
   * 
   * 
   * @return
   */
  public int getCurrentYear() {
    return currentYear;
  }

  public String getViewDateDate() {
    try {
      return ScheduleUtils.translateDate(
        view_date.getValue().getDate(),
        "yyyyMMdd");
    } catch (Exception e) {
      return "";
    }
  }
}
