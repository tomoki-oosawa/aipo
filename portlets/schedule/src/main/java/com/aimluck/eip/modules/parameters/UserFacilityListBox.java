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
package com.aimluck.eip.modules.parameters;

import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールポートレット初期選択設備の設定値を処理するクラスです。 <br />
 */
public class UserFacilityListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  private final String DEF_INITIAL_VALUE = ALLocalizationUtils
    .getl10n("SCHEDULE_SELECT_FACILITY_AND_USER");

  /**
   * Initialize options
   * 
   * @param data
   */
  @Override
  protected void init(RunData data) {

    // ログインユーザの取得
    List<ALEipUser> list = ALEipUtils.getUsers("LoginUser");

    List<FacilityResultData> facilityList =
      FacilitiesUtils.getFacilityAllList();

    ALEipUser user = null;
    FacilityResultData facility = null;
    int length = list.size();
    int f_length = facilityList.size();
    String[] groupKeys = new String[length + f_length + 1];
    String[] groupValues = new String[length + f_length + 1];

    groupKeys[0] = "";
    groupValues[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
    for (int i = 0; i < length; i++) {
      user = list.get(i);
      groupKeys[i + 1] = user.getUserId().getValueAsString();
      groupValues[i + 1] = user.getAliasName().toString();
    }

    for (int j = 0; j < f_length; j++) {
      facility = facilityList.get(j);
      groupKeys[length + 1 + j] =
        ScheduleUtils.TARGET_FACILITY_ID
          + facility.getFacilityId().getValueAsString();
      groupValues[length + 1 + j] = facility.getFacilityName().toString();
    }

    this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
    this.items = groupKeys;
    this.values = groupValues;
    this.size = Integer.toString(length + f_length + 1);
    this.multiple =
      Boolean
        .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
        .booleanValue();

  }

}
