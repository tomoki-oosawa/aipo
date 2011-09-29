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
 */

package com.aimluck.eip.modules.actions.holiday;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.holiday.HolidayFormData;
import com.aimluck.eip.holiday.HolidayMultiDelete;
import com.aimluck.eip.holiday.HolidaySelectData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Holidayのアクションクラスです。 <BR>
 * 
 */
public class HolidayAction extends ALBaseAction {

  /**
   * 通常表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報のクリア
    clearHolidaySession(rundata, context);
    // 表示年の初期化
    HolidaySelectData.initViewedHolidayYear(rundata, context);

    // 通常表示処理
    HolidaySelectData listData = new HolidaySelectData();
    listData.initField();
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "holiday");
  }

  /**
   * Holiday登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_form(RunData rundata, Context context) throws Exception {
    HolidayFormData formData = new HolidayFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "holiday-form");

  }

  /**
   * Holidayを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_insert(RunData rundata, Context context)
      throws Exception {
    HolidayFormData formData = new HolidayFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      doHoliday_list(rundata, context);
    } else {
      setTemplate(rundata, "holiday-form");
    }
  }

  /**
   * Holidayを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_update(RunData rundata, Context context)
      throws Exception {
    HolidayFormData formData = new HolidayFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      doHoliday_list(rundata, context);
    } else {
      setTemplate(rundata, "holiday-form");
    }
  }

  /**
   * Holidayを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_delete(RunData rundata, Context context)
      throws Exception {
    HolidayFormData formData = new HolidayFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      doHoliday_list(rundata, context);
    }
  }

  /**
   * Holidayを削除します。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_multi_delete(RunData rundata, Context context)
      throws Exception {
    HolidayMultiDelete delete = new HolidayMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doHoliday_list(rundata, context);
  }

  /**
   * Holidayを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_list(RunData rundata, Context context) throws Exception {
    HolidaySelectData listData = new HolidaySelectData();
    listData.initField();
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "holiday-list");
  }

  /**
   * Holidayを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doHoliday_detail(RunData rundata, Context context)
      throws Exception {
    HolidaySelectData detailData = new HolidaySelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "holiday-detail");
    } else {
      doHoliday_list(rundata, context);
    }
  }

  private void clearHolidaySession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    // エンティティIDの初期化
    list.add("entityid");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
