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
package com.aimluck.eip.schedule.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールのユーティリティクラスです。
 * 
 */
public class CellScheduleUtils {

  /** <code>FLAG_EDIT_REPEAT_DEF</code> デフォルト値（繰り返し編集範囲） */
  public static final int FLAG_EDIT_REPEAT_DEF = -1;

  /** <code>FLAG_EDIT_REPEAT_ALL</code> 個別日程を編集（繰り返し編集範囲） */
  public static final int FLAG_EDIT_REPEAT_ALL = 0;

  /** <code>FLAG_EDIT_SCHEDULE_ONE</code> 全日程を編集（繰り返し編集範囲） */
  public static final int FLAG_EDIT_REPEAT_ONE = 1;

  /** <code>FLAG_DEL_MEMBER_ALL</code> [削除フラグ] すべての共有メンバーからこのスケジュールを削除する */
  public static final int FLAG_DEL_MEMBER_ALL = 0;

  /** <code>FLAG_DEL_MEMBER_ONE</code> [削除フラグ] このスケジュールからログインユーザーだけを削除する */
  public static final int FLAG_DEL_MEMBER_ONE = 1;

  /** <code>FLAG_DEL_RANGE_ALL</code> [削除フラグ] この繰り返しスケジュールを削除する */
  public static final int FLAG_DEL_RANGE_ALL = 0;

  /** <code>FLAG_DEL_RANGE_ONE</code> [削除フラグ] この繰り返しスケジュールの特定スケジュールのみを削除する */
  public static final int FLAG_DEL_RANGE_ONE = 1;

  /** スケジュールタイプ（一日） */
  public static final String SCHEDULE_TYPE_ONEDAY = "O";

  /** スケジュールタイプ（繰り返し） */
  public static final String SCHEDULE_TYPE_REPEAT = "R";

  /** スケジュールタイプ（期間） */
  public static final String SCHEDULE_TYPE_SPAN = "S";

  /** <code>logger</code> loger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(CellScheduleUtils.class.getName());

  public static String getScheduleType(EipTSchedule schedule) {
    String ptn = schedule.getRepeatPattern();

    if (ptn.charAt(0) == 'S') {
      return SCHEDULE_TYPE_SPAN;
    } else if (ptn.charAt(0) == 'D'
      || ptn.charAt(0) == 'M'
      || ptn.charAt(0) == 'W') {
      return SCHEDULE_TYPE_REPEAT;
    }
    return SCHEDULE_TYPE_ONEDAY;
  }

  public static List<ALEipUser> getShareUserMemberList(RunData rundata) {
    List<ALEipUser> memberList = new ArrayList<ALEipUser>();
    ALEipUser login_user = ALEipUtils.getALEipUser(rundata);

    memberList.add(login_user);

    ALEipUser user = null;
    String str[] = rundata.getParameters().getStrings("shareuser");
    if (str != null && str.length > 0) {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, str);
      query.setQualifier(exp);
      List<ALEipUser> sharuserlist = ALEipUtils.getUsersFromSelectQuery(query);
      int sharuserSize = sharuserlist.size();
      for (int i = 0; i < sharuserSize; i++) {
        user = sharuserlist.get(i);
        if (!ScheduleUtils.isContains(memberList, user)) {
          memberList.add(user);
        }
      }
    }

    String shareusers = rundata.getParameters().getString("shareusers");
    if (shareusers != null && shareusers.length() > 0) {
      List<String> list = new ArrayList<String>();
      String token = null;
      StringTokenizer st = new StringTokenizer(shareusers, ",");
      while (st.hasMoreTokens()) {
        token = st.nextToken();
        if (token != null) {
          list.add(token.trim());
        }
      }
      str = new String[list.size()];
      str = list.toArray(str);

      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, str);
      query.setQualifier(exp);
      List<ALEipUser> sharuserslist = ALEipUtils.getUsersFromSelectQuery(query);
      int sharusersSize = sharuserslist.size();
      for (int i = 0; i < sharusersSize; i++) {
        user = sharuserslist.get(i);
        if (!ScheduleUtils.isContains(memberList, user)) {
          memberList.add(user);
        }
      }
      // if (!ScheduleUtils.isContains(memberList, login_user)) {
      // memberList.add(login_user);
      // }
    }

    return memberList;
  }

  public static List<FacilityResultData> getShareFacilityMemberList(
      RunData rundata) {
    List<FacilityResultData> facilityMemberList =
      new ArrayList<FacilityResultData>();
    FacilityResultData f_record = null;
    String facstr[] = rundata.getParameters().getStrings("sharefac");
    if (facstr != null && facstr.length > 0) {
      SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
      Expression fexp =
        ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, facstr);
      fquery.setQualifier(fexp);
      List<FacilityResultData> f_list =
        FacilitiesUtils.getFacilitiesFromSelectQuery(fquery);
      int f_list_size = f_list.size();
      for (int i = 0; i < f_list_size; i++) {
        f_record = f_list.get(i);
        if (!FacilitiesUtils.isContains(facilityMemberList, f_record)) {
          facilityMemberList.add(f_record);
        }
      }
    }

    String sharefacs = rundata.getParameters().getString("sharefacs");
    if (sharefacs != null && sharefacs.length() > 0) {
      List<String> list = new ArrayList<String>();
      String token = null;
      StringTokenizer st = new StringTokenizer(sharefacs, ",");
      while (st.hasMoreTokens()) {
        token = st.nextToken();
        if (token != null) {
          list.add(token.trim());
        }
      }
      String[] str = new String[list.size()];
      str = list.toArray(str);

      SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
      Expression fexp =
        ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, str);
      fquery.setQualifier(fexp);
      List<FacilityResultData> f_list =
        FacilitiesUtils.getFacilitiesFromSelectQuery(fquery);
      int fsize = f_list.size();
      for (int i = 0; i < fsize; i++) {
        f_record = f_list.get(i);
        if (!FacilitiesUtils.isContains(facilityMemberList, f_record)) {
          facilityMemberList.add(f_record);
        }
      }
    }

    return facilityMemberList;
  }

}
