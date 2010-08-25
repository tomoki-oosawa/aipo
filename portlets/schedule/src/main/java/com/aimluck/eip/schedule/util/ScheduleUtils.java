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
package com.aimluck.eip.schedule.util;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALAbstractField;
import com.aimluck.commons.field.ALCellDateField;
import com.aimluck.commons.field.ALCellDateTimeField;
import com.aimluck.commons.field.ALCellStringField;
import com.aimluck.commons.field.ALDateContainer;
import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALIllegalDateException;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.AjaxScheduleResultData;
import com.aimluck.eip.schedule.AjaxTermScheduleWeekContainer;
import com.aimluck.eip.schedule.ScheduleResultData;
import com.aimluck.eip.schedule.ScheduleTermWeekContainer;
import com.aimluck.eip.schedule.ScheduleToDoResultData;
import com.aimluck.eip.schedule.ScheduleToDoWeekContainer;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.userfacility.beans.UserFacilityLiteBean;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * スケジュールのユーティリティクラスです。
 * 
 */
public class ScheduleUtils {

  /** <code>logger</code> loger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleUtils.class.getName());

  /** <code>SCHEDULEMAP_TYPE_USER</code> ユーザ */
  public static final String SCHEDULEMAP_TYPE_USER = "U";

  /** <code>SCHEDULEMAP_TYPE_FACILITY</code> 設備 */
  public static final String SCHEDULEMAP_TYPE_FACILITY = "F";

  /** <code>TARGET_FACILITY_ID</code> ユーザによる表示切り替え用変数の識別子 */
  public static final String TARGET_FACILITY_ID = "f";

  /**
   * Scheudle オブジェクトモデルを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTSchedule getEipTSchedule(RunData rundata, Context context,
      boolean isOwner) throws ALPageNotFoundException, ALDBErrorException {
    return getEipTSchedule(rundata, context, isOwner, ALEipUtils
      .getUserId(rundata));

  }

  /**
   * Scheudle オブジェクトモデルを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTSchedule getEipTSchedule(RunData rundata, int scheduleid,
      boolean isOwner, int userid) throws ALPageNotFoundException,
      ALDBErrorException {

    try {
      SelectQuery<EipTSchedule> query = Database.query(EipTSchedule.class);

      // スケジュールID
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTSchedule.SCHEDULE_ID_PK_COLUMN,
          Integer.valueOf(scheduleid));
      query.setQualifier(exp1);

      if (isOwner) {
        // ユーザーID
        Expression exp2 =
          ExpressionFactory.matchExp(EipTSchedule.OWNER_ID_PROPERTY, Integer
            .valueOf(ALEipUtils.getUserId(rundata)));
        query.andQualifier(exp2);
      }

      List<EipTSchedule> schedules = query.fetchList();

      // 指定したSchedule IDのレコードが見つからない場合
      if (schedules == null || schedules.size() == 0) {
        logger.error("[ScheduleUtils] Not found record.");
        throw new ALPageNotFoundException();
      }

      EipTSchedule record = schedules.get(0);

      // 条件が足りないかも（by Komori 2006/06/09）
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, record
          .getScheduleId());
      mapquery.setQualifier(mapexp1);
      Expression mapexp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(userid));
      mapquery.andQualifier(mapexp2);
      Expression mapexp3 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(userid));
      mapquery.andQualifier(mapexp3);

      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();
      boolean is_member =
        (schedulemaps != null && schedulemaps.size() > 0) ? true : false;

      // boolean is_member = orm_map.count(new Criteria().add(
      // EipTScheduleMapConstants.SCHEDULE_ID, record.getScheduleId()).add(
      // EipTScheduleMapConstants.USER_ID, userid).add(
      // EipTScheduleMapConstants.USER_ID, ALEipUtils.getUserId(rundata))) != 0;

      boolean is_public = "O".equals(record.getPublicFlag());

      // アクセス権限がない場合
      if (!is_member && !is_public) {
        logger.error("[ScheduleUtils] Cannnot access this record. ");
        throw new ALPageNotFoundException();
      }

      return schedules.get(0);

    } catch (Exception ex) {
      logger.error("[ScheduleUtils]", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * Scheudle オブジェクトモデルを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTSchedule getEipTSchedule(RunData rundata, Context context,
      boolean isOwner, int userid) throws ALPageNotFoundException,
      ALDBErrorException {

    // String org_id = OrgORMappingMap.getInstance().getOrgId(rundata);

    // スケジュールIDをセッション変数から取得
    String scheduleid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      // IDが空の場合 || IDがintでない場合
      if (scheduleid == null || Integer.valueOf(scheduleid) == null) {
        logger.error("[ScheduleUtils] ENTITYID is empty.");
        throw new ALPageNotFoundException();
      }
    } catch (NumberFormatException ex) {
      logger.error("[ScheduleUtils] NumberFormatException: ENTITYID is wrong.");
      throw new ALPageNotFoundException();

    }

    try {
      SelectQuery<EipTSchedule> query = Database.query(EipTSchedule.class);

      // スケジュールID
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTSchedule.SCHEDULE_ID_PK_COLUMN,
          scheduleid);
      query.setQualifier(exp1);

      if (isOwner) {
        // ユーザーID
        Expression exp2 =
          ExpressionFactory.matchExp(EipTSchedule.OWNER_ID_PROPERTY, Integer
            .valueOf(ALEipUtils.getUserId(rundata)));
        query.andQualifier(exp2);
      }

      List<EipTSchedule> schedules = query.fetchList();

      // 指定したSchedule IDのレコードが見つからない場合
      if (schedules == null || schedules.size() == 0) {
        logger.error("[ScheduleUtils] Not found record.");
        throw new ALPageNotFoundException();
      }

      EipTSchedule record = schedules.get(0);

      // Integer.valueOf(userid)
      // 条件が足りないかも（by Komori 2006/06/09）
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, record
          .getScheduleId());
      mapquery.setQualifier(mapexp1);
      Expression mapexp21 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, ALEipUtils
          .getUserId(rundata));
      Expression mapexp22 =
        ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.CREATE_USER_ID_PROPERTY, Integer.valueOf(ALEipUtils
          .getUserId(rundata)));
      mapquery.andQualifier(mapexp21.orExp(mapexp22));

      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();
      boolean is_member =
        (schedulemaps != null && schedulemaps.size() > 0) ? true : false;

      // boolean is_member = orm_map.count(new Criteria().add(
      // EipTScheduleMapConstants.SCHEDULE_ID, record.getScheduleId()).add(
      // EipTScheduleMapConstants.USER_ID, userid).add(
      // EipTScheduleMapConstants.USER_ID, ALEipUtils.getUserId(rundata))) != 0;

      int loginuser_id = ALEipUtils.getUserId(rundata);
      boolean is_owner = record.getOwnerId().intValue() == loginuser_id;
      boolean is_createuser =
        loginuser_id == record.getCreateUserId().intValue();
      boolean is_public = "O".equals(record.getPublicFlag());

      // アクセス権限がない場合
      if (!is_public && !is_member && (!(is_createuser || is_owner))) {
        ALEipUtils.redirectPermissionError(rundata);
      }

      /*
       * // アクセス権限がない場合 if (!is_member && (!(is_createuser || is_owner) &&
       * !is_public)) { // TODO: エラー処理 logger.error("[ScheduleUtils] Cannnot
       * access this record. "); throw new ALPageNotFoundException(); }
       */

      return schedules.get(0);

    } catch (Exception ex) {
      logger.error("[ScheduleUtils]", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * 詳細表示用の Scheudle オブジェクトモデルを取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTSchedule getEipTScheduleDetail(RunData rundata,
      Context context, boolean isOwner, int id, String type)
      throws ALPageNotFoundException, ALDBErrorException {

    // スケジュールIDをセッション変数から取得
    String scheduleid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      // IDが空の場合 || IDがintでない場合
      if (scheduleid == null || Integer.valueOf(scheduleid) == null) {
        logger.error("[ScheduleUtils] ENTITYID is empty.");
        throw new ALPageNotFoundException();
      }
    } catch (NumberFormatException ex) {
      logger.error(
        "[ScheduleUtils] NumberFormatException: ENTITYID is wrong.",
        ex);
      throw new ALPageNotFoundException();

    }

    /**
     * 新着ポートレット既読処理
     */
    try {
      WhatsNewUtils.shiftWhatsNewReadFlag(
        WhatsNewUtils.WHATS_NEW_TYPE_SCHEDULE,
        Integer.parseInt(scheduleid),
        ALEipUtils.getUserId(rundata));
    } catch (NumberFormatException e) {
      logger.error("[ScheduleUtils]", e);
    }

    try {
      SelectQuery<EipTSchedule> query = Database.query(EipTSchedule.class);
      query.getQuery().setRefreshingObjects(true);

      // スケジュールID
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTSchedule.SCHEDULE_ID_PK_COLUMN,
          scheduleid);
      query.setQualifier(exp1);

      if (isOwner) {
        // ユーザーID
        Expression exp2 =
          ExpressionFactory.matchExp(EipTSchedule.OWNER_ID_PROPERTY, Integer
            .valueOf(ALEipUtils.getUserId(rundata)));
        query.andQualifier(exp2);
      }

      // ユーザのスケジュール
      Expression exp3 =
        ExpressionFactory.matchExp(EipTSchedule.EIP_TSCHEDULE_MAPS_PROPERTY
          + "."
          + EipTScheduleMap.TYPE_PROPERTY, type);
      query.andQualifier(exp3);

      List<EipTSchedule> schedules = query.fetchList();

      // 指定したSchedule IDのレコードが見つからない場合
      if (schedules == null || schedules.size() == 0) {

        // TODO: エラー処理
        logger.error("[ScheduleUtils] Not found record.");
        throw new ALPageNotFoundException();
      }

      EipTSchedule record = schedules.get(0);

      // 条件が足りないかも（by Komori 2006/06/09）
      // Integer.valueOf(id)
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, record
          .getScheduleId());
      mapquery.setQualifier(mapexp1);
      Expression mapexp21 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .toString(ALEipUtils.getUserId(rundata)));
      Expression mapexp22 =
        ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.CREATE_USER_ID_PROPERTY, Integer.valueOf(ALEipUtils
          .getUserId(rundata)));
      mapquery.andQualifier(mapexp21.orExp(mapexp22));
      Expression mapexp3 =
        ExpressionFactory.matchExp(EipTScheduleMap.TYPE_PROPERTY, type);
      mapquery.andQualifier(mapexp3);

      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();
      boolean is_member =
        (schedulemaps != null && schedulemaps.size() > 0) ? true : false;

      // boolean is_member = orm_map.count(new Criteria().add(
      // EipTScheduleMapConstants.SCHEDULE_ID, record.getScheduleId()).add(
      // EipTScheduleMapConstants.USER_ID, id).add(
      // EipTScheduleMapConstants.TYPE, type).add(
      // EipTScheduleMapConstants.USER_ID, ALEipUtils.getUserId(rundata))) != 0;

      int loginuser_id = ALEipUtils.getUserId(rundata);
      boolean is_owner = record.getOwnerId().intValue() == loginuser_id;
      boolean is_createuser =
        loginuser_id == record.getCreateUserId().intValue();
      boolean is_public = "O".equals(record.getPublicFlag());

      // アクセス権限がない場合
      if (type.equals("F") || is_public) {
      } else if (!is_member && (!(is_createuser || is_owner))) {
        ALEipUtils.redirectPermissionError(rundata);
      }

      return schedules.get(0);

    } catch (Exception ex) {
      logger.error("[ScheduleUtils]", ex);
      throw new ALDBErrorException();
    }

  }

  /**
   * ScheudleMap オブジェクトモデルを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTScheduleMap getEipTScheduleMap(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {

    // スケジュールIDをセッション変数から取得
    String scheduleid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      // IDが空の場合 || IDがintでない場合
      if (scheduleid == null || Integer.valueOf(scheduleid) == null) {
        logger.error("[ScheduleUtils] ENTITYID is empty.");
        throw new ALPageNotFoundException();
      }
    } catch (NumberFormatException ex) {
      logger.error(
        "[ScheduleUtils] NumberFormatException: ENTITYID is wrong.",
        ex);
      throw new ALPageNotFoundException();

    }

    try {
      SelectQuery<EipTScheduleMap> query =
        Database.query(EipTScheduleMap.class);

      // スケジュールID
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          scheduleid);
      query.setQualifier(exp1);
      // ユーザーID
      Expression exp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.andQualifier(exp2);

      List<EipTScheduleMap> schedules = query.fetchList();

      // 指定したIDのレコードが見つからない場合
      if (schedules == null || schedules.size() == 0) {

        // TODO: エラー処理
        logger.error("[ScheduleUtils] Not found record.");
        throw new ALPageNotFoundException();
      }

      return schedules.get(0);

    } catch (Exception ex) {
      // TODO: エラー処理
      logger.error("[ScheduleUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  public static List<EipTScheduleMap> getEipTScheduleMaps(EipTSchedule schedule)
      throws ALPageNotFoundException, ALDBErrorException {

    // スケジュールIDを取得
    Integer scheduleid = schedule.getScheduleId();

    try {
      SelectQuery<EipTScheduleMap> query =
        Database.query(EipTScheduleMap.class);

      // スケジュールID
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          scheduleid);
      query.setQualifier(exp1);
      // // ユーザーID
      // Expression exp2 = ExpressionFactory.matchExp(
      // EipTScheduleMap.USER_ID_PROPERTY, new Integer(ALEipUtils
      // .getUserId(rundata)));
      // query.andQualifier(exp2);

      List<EipTScheduleMap> schedules = query.fetchList();

      // 指定したIDのレコードが見つからない場合
      if (schedules == null || schedules.size() == 0) {

        // TODO: エラー処理
        logger.error("[ScheduleUtils] Not found record.");
        throw new ALPageNotFoundException();
      }

      return schedules;

    } catch (Exception ex) {
      logger.error("[ScheduleUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * 共有メンバーを取得します。
   * 
   * @param rundata
   * @param context
   * @param includeLoginUser
   *          ログインユーザーを共有メンバーとして取り扱う場合，true．
   * @return
   */
  public static List<ALEipUser> getUsers(RunData rundata, Context context,
      boolean includeLoginUser) throws ALPageNotFoundException,
      ALDBErrorException {
    List<ALEipUser> list = new ArrayList<ALEipUser>();

    // スケジュールIDをセッション変数から取得
    String scheduleid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      // IDが空の場合 || IDがintでない場合
      if (scheduleid == null || Integer.valueOf(scheduleid) == null) {
        logger.error("[ScheduleUtils] ENTITYID is empty.");
        throw new ALPageNotFoundException();
      }
    } catch (NumberFormatException ex) {
      logger.error(
        "[ScheduleUtils] NumberFormatException: ENTITYID is wrong.",
        ex);
      throw new ALPageNotFoundException();

    }

    try {
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);

      // スケジュールID
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          Integer.valueOf(scheduleid));
      mapquery.setQualifier(exp1);
      if (!includeLoginUser) {
        Expression exp2 =
          ExpressionFactory.noMatchExp(
            EipTScheduleMap.USER_ID_PROPERTY,
            Integer.valueOf(ALEipUtils.getUserId(rundata)));
        mapquery.andQualifier(exp2);
      }
      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();

      List<Integer> uidlist = new ArrayList<Integer>();
      EipTScheduleMap map = null;
      int mapsize = schedulemaps.size();
      for (int i = 0; i < mapsize; i++) {
        map = schedulemaps.get(i);
        if (!uidlist.contains(map.getUserId())) {
          uidlist.add(map.getUserId());
        }
      }

      SelectQuery<TurbineUser> userquery = Database.query(TurbineUser.class);
      Expression userexp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, uidlist);
      userquery.setQualifier(userexp);
      List<Ordering> orders = new ArrayList<Ordering>();
      orders.add(new Ordering(TurbineUser.LAST_NAME_KANA_PROPERTY, true));
      orders.add(new Ordering(TurbineUser.FIRST_NAME_KANA_PROPERTY, true));
      userquery.getQuery().addOrderings(orders);

      List<TurbineUser> ulist = userquery.fetchList();

      TurbineUser tuser;
      ALEipUser user;
      for (int j = 0; j < ulist.size(); j++) {
        tuser = ulist.get(j);
        user = new ALEipUser();
        user.initField();
        user.setUserId(tuser.getUserId().intValue());
        user.setName(tuser.getLoginName());
        user.setAliasName(tuser.getFirstName(), tuser.getLastName());
        list.add(user);
      }

    } catch (Exception e) {
      logger.error("[ScheduleUtils]", e);
      throw new ALDBErrorException();

    }
    return list;
  }

  /**
   * 施設メンバーを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static int[] getFacilityIds(EipTSchedule schedule)
      throws ALPageNotFoundException, ALDBErrorException {
    List<?> list = schedule.getEipTScheduleMaps();

    List<Integer> flist = new ArrayList<Integer>();
    EipTScheduleMap map = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      map = (EipTScheduleMap) list.get(i);
      if (SCHEDULEMAP_TYPE_FACILITY.equals(map.getType())) {
        flist.add(map.getUserId());
      }
    }

    int fsize = flist.size();
    int[] ids = new int[fsize];
    for (int i = 0; i < fsize; i++) {
      ids[i] = (flist.get(i)).intValue();
    }
    return ids;
  }

  /**
   * 指定した繰り返しパターンにマッチするかどうかを返します。
   * 
   * @param date
   * @param ptn
   * @param startDate
   * @param limitDate
   * @return
   */
  public static boolean isView(ALDateTimeField date, String ptn,
      Date startDate, Date limitDate) {
    int count = 0;
    boolean result = false;
    Calendar cal = Calendar.getInstance();
    cal.setTime(date.getValue());
    // 毎日
    if (ptn.charAt(0) == 'D') {
      result = true;
      count = 1;
      // 毎週
    } else if (ptn.charAt(0) == 'W') {

      int dow = cal.get(Calendar.DAY_OF_WEEK);
      switch (dow) {
        // 日
        case Calendar.SUNDAY:
          result = ptn.charAt(1) != '0';
          break;
        // 月
        case Calendar.MONDAY:
          result = ptn.charAt(2) != '0';
          break;
        // 火
        case Calendar.TUESDAY:
          result = ptn.charAt(3) != '0';
          break;
        // 水
        case Calendar.WEDNESDAY:
          result = ptn.charAt(4) != '0';
          break;
        // 木
        case Calendar.THURSDAY:
          result = ptn.charAt(5) != '0';
          break;
        // 金
        case Calendar.FRIDAY:
          result = ptn.charAt(6) != '0';
          break;
        // 土
        case Calendar.SATURDAY:
          result = ptn.charAt(7) != '0';
          break;
        default:
          result = false;
          break;
      }
      count = 8;
      // 毎月
    } else if (ptn.charAt(0) == 'M') {
      int mday = Integer.parseInt(ptn.substring(1, 3));
      result = Integer.parseInt(date.getDay()) == mday;
      count = 3;
    } else {
      return true;
    }

    if (result) {
      if (ptn.charAt(count) == 'L') {
        // 締め切り日がある場合
        if (equalsToDate(startDate, date.getValue(), false)
          || equalsToDate(limitDate, date.getValue(), false)) {
          // 繰り返しの開始日と終了日
          result = true;
        } else {
          // 繰り返しの開始日と終了日の間に指定した日が入っているかを検証．
          result =
            result
              && startDate.before(cal.getTime())
              && limitDate.after(cal.getTime());
        }
      }
    }

    return result;
  }

  /**
   * 指定したエントリー名を持つ個人設定ページに含まれるポートレットへの URI を取得する．
   * 
   * @param rundata
   * @param portletEntryName
   *          PSML ファイルに記述されているタグ entry の要素 parent
   * @return
   */
  public static String getPortletURIinPersonalConfigPane(RunData rundata,
      String portletEntryName) {
    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getParent().equals(portletEntryName)) {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);

            DynamicURI duri =
              jsLink.getLink(
                JetspeedLink.CURRENT,
                null,
                null,
                JetspeedLink.CURRENT,
                null);
            duri =
              duri
                .addPathInfo(
                  JetspeedResources.PATH_PANEID_KEY,
                  portletList[i].getId() + "," + entries[j].getId())
                .addQueryData(
                  JetspeedResources.PATH_ACTION_KEY,
                  "controls.Restore");
            return duri.toString();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  public static String getPortletURItoTodoDetailPane(RunData rundata,
      String portletEntryName, long entityid, String schedulePortletId) {
    return getPortletURItoTodoModePane(
      rundata,
      portletEntryName,
      entityid,
      schedulePortletId,
      "detail");
  }

  public static String getPortletURItoTodoPublicDetailPane(RunData rundata,
      String portletEntryName, long entityid, String schedulePortletId) {
    return getPortletURItoTodoModePane(
      rundata,
      portletEntryName,
      entityid,
      schedulePortletId,
      "public_detail");
  }

  public static String getPortletURItoTodoModePane(RunData rundata,
      String portletEntryName, long entityid, String schedulePortletId,
      String mode) {
    if (mode == null || "".equals(mode)) {
      return null;
    }

    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getParent().equals(portletEntryName)) {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);

            DynamicURI duri =
              jsLink.getLink(
                JetspeedLink.CURRENT,
                null,
                null,
                JetspeedLink.CURRENT,
                null);
            duri =
              duri
                .addPathInfo(
                  JetspeedResources.PATH_PANEID_KEY,
                  portletList[i].getId())
                .addPathInfo(
                  JetspeedResources.PATH_PORTLETID_KEY,
                  entries[j].getId())
                .addQueryData(
                  JetspeedResources.PATH_ACTION_KEY,
                  "controls.Maximize")
                .addQueryData(ALEipConstants.MODE, mode)
                .addQueryData(ALEipConstants.ENTITY_ID, entityid)
                .addQueryData("sch", schedulePortletId);
            int jmode = ((JetspeedRunData) rundata).getMode();
            if (jmode == JetspeedRunData.MAXIMIZE) {
              duri.addQueryData("prev", JetspeedRunData.MAXIMIZE);
            } else {
              duri.addQueryData("prev", JetspeedRunData.NORMAL);
            }
            return duri.toString();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  public static String getPortletURItoTodoDetailPaneForCell(RunData rundata,
      String portletEntryName, long entityid, String schedulePortletId) {
    return getPortletURItoTodoModePaneForCell(
      rundata,
      portletEntryName,
      entityid,
      schedulePortletId,
      "detail");
  }

  public static String getPortletURItoTodoModePaneForCell(RunData rundata,
      String portletEntryName, long entityid, String schedulePortletId,
      String mode) {
    if (mode == null || "".equals(mode)) {
      return null;
    }

    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Entry[] entries = portlets.getEntriesArray();
      if (entries == null || entries.length <= 0) {
        return null;
      }

      int ent_length = entries.length;
      for (int j = 0; j < ent_length; j++) {
        if (entries[j].getParent().equals(portletEntryName)) {
          JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);

          DynamicURI duri =
            jsLink.getLink(
              JetspeedLink.CURRENT,
              null,
              null,
              JetspeedLink.CURRENT,
              null);
          duri =
            duri
              .addPathInfo(
                JetspeedResources.PATH_PORTLETID_KEY,
                entries[j].getId())
              .addQueryData(
                JetspeedResources.PATH_ACTION_KEY,
                "controls.Maximize")
              .addQueryData(ALEipConstants.MODE, mode)
              .addQueryData(ALEipConstants.ENTITY_ID, entityid)
              .addQueryData("sch", schedulePortletId);
          return duri.toString();
        }
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  /**
   * 指定した2つの日付を比較する．
   * 
   * @param date1
   * @param date2
   * @param checkTime
   *          時間まで比較する場合，true．
   * @return 等しい場合，true．
   */
  public static boolean equalsToDate(Date date1, Date date2, boolean checkTime) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date1);
    int date1Year = cal.get(Calendar.YEAR);
    int date1Month = cal.get(Calendar.MONTH) + 1;
    int date1Day = cal.get(Calendar.DATE);
    int date1Hour = cal.get(Calendar.HOUR);
    int date1Minute = cal.get(Calendar.MINUTE);
    cal.setTime(date2);
    int date2Year = cal.get(Calendar.YEAR);
    int date2Month = cal.get(Calendar.MONTH) + 1;
    int date2Day = cal.get(Calendar.DATE);
    int date2Hour = cal.get(Calendar.HOUR);
    int date2Minute = cal.get(Calendar.MINUTE);
    if (checkTime) {
      if (date1Year == date2Year
        && date1Month == date2Month
        && date1Day == date2Day
        && date1Hour == date2Hour
        && date1Minute == date2Minute) {
        return true;
      }
    } else {
      if (date1Year == date2Year
        && date1Month == date2Month
        && date1Day == date2Day) {
        return true;
      }
    }
    return false;
  }

  /**
   * 指定した2つの日付を比較する．
   * 
   * @param date1
   * @param date2
   * @return
   */
  public static int compareToDate(Date date1, Date date2) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date1);
    int date1Year = cal.get(Calendar.YEAR);
    int date1Month = cal.get(Calendar.MONTH) + 1;
    int date1Day = cal.get(Calendar.DATE);
    cal.setTime(date2);
    int date2Year = cal.get(Calendar.YEAR);
    int date2Month = cal.get(Calendar.MONTH) + 1;
    int date2Day = cal.get(Calendar.DATE);

    if (date1Year == date2Year
      && date1Month == date2Month
      && date1Day == date2Day) {
      return 0;
    }

    if (date1Year < date2Year) {
      return 1;
    } else if (date2Year > date1Year) {
      return -1;
    } else {
      if (date1Month < date2Month) {
        return 1;
      } else if (date2Month > date1Month) {
        return -1;
      } else {
        if (date1Day < date2Day) {
          return 1;
        } else {
          return -1;
        }
      }
    }
  }

  /**
   * 指定した2つの時刻のみを比較する．
   * 
   * @param date1
   * @param date2
   * @return
   */
  public static int compareTime(Date date1, Date date2) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date1);
    int date1Hour = cal.get(Calendar.HOUR_OF_DAY);
    int date1Minute = cal.get(Calendar.MINUTE);
    cal.setTime(date2);
    int date2Hour = cal.get(Calendar.HOUR_OF_DAY);
    int date2Minute = cal.get(Calendar.MINUTE);

    if (date1Hour == date2Hour && date1Minute == date2Minute) {
      return 0;
    }

    if (date1Hour < date2Hour) {
      return 1;
    } else if (date1Hour > date2Hour) {
      return -1;
    } else {
      if (date1Minute < date2Minute) {
        return 1;
      } else {
        return -1;
      }
    }

  }

  /**
   * アクセスしてきたユーザが利用するブラウザ名が Windows の MSIE であるかを判定する．
   * 
   * @param rundata
   * @return MSIE の場合は，true．
   */
  public static boolean isMsieBrowser(RunData rundata) {
    // String os = "Win";
    String browserNames = "MSIE";

    // User-Agent の取得
    String userAgent = rundata.getRequest().getHeader("User-Agent");
    if (userAgent == null || userAgent.equals("")) {
      return false;
    }

    if (userAgent.indexOf("Win") < 0) {
      return false;
    }

    if (userAgent.indexOf(browserNames) > 0) {
      return true;
    }
    return false;
  }

  /**
   * ダミースケジュールを登録する．
   * 
   * @param schedule
   * @param ownerid
   * @param startDate
   * @param endDate
   * @param memberIdList
   * @throws ALDBErrorException
   */
  public static void insertDummySchedule(EipTSchedule schedule, int ownerid,
      Date startDate, Date endDate, int[] memberIdList, int[] facilityIdList)
      throws ALDBErrorException {

    // ダミーのスケジュールを登録する．
    EipTSchedule dummySchedule = Database.create(EipTSchedule.class);

    // 親スケジュール ID
    dummySchedule.setParentId(schedule.getScheduleId());
    // 予定
    dummySchedule.setName("dummy");
    // 場所
    dummySchedule.setPlace("");
    // 内容
    dummySchedule.setNote("");
    // 公開フラグ
    dummySchedule.setPublicFlag("P");
    // 共有メンバーによる編集／削除フラグ
    dummySchedule.setEditFlag("F");
    // オーナーID
    dummySchedule.setOwnerId(Integer.valueOf(ownerid));
    // 作成日
    Date now2 = new Date();
    dummySchedule.setCreateDate(now2);
    dummySchedule.setCreateUserId(Integer.valueOf(ownerid));
    // 更新日
    dummySchedule.setUpdateDate(now2);
    dummySchedule.setUpdateUserId(Integer.valueOf(ownerid));
    dummySchedule.setRepeatPattern("N");
    dummySchedule.setStartDate(startDate);
    dummySchedule.setEndDate(endDate);

    EipTCommonCategory category =
      CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

    // orm.doInsert(dummySchedule);
    int size = memberIdList.length;
    for (int i = 0; i < size; i++) {
      EipTScheduleMap map = Database.create(EipTScheduleMap.class);
      int userid = memberIdList[i];
      map.setEipTSchedule(dummySchedule);
      map.setUserId(Integer.valueOf(userid));

      // D: ダミースケジュール
      map.setStatus("D");
      map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_USER);

      map.setCommonCategoryId(Integer.valueOf(1));
      map.setEipTCommonCategory(category);
    }

    if (facilityIdList != null && facilityIdList.length > 0) {
      int fsize = facilityIdList.length;
      for (int i = 0; i < fsize; i++) {
        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
        int fid = facilityIdList[i];
        map.setEipTSchedule(dummySchedule);
        map.setUserId(Integer.valueOf(fid));

        // D: ダミースケジュール
        map.setStatus("D");
        map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);

        map.setCommonCategoryId(Integer.valueOf(1));
        map.setEipTCommonCategory(category);
      }
    }

    // スケジュールを登録
    Database.commit();
  }

  /**
   * ダミースケジュールを登録する（携帯電話対応時には上記のメソッドに変更する）．
   * 
   * @param schedule
   * @param ownerid
   * @param startDate
   * @param endDate
   * @param memberIdList
   * @throws ALDBErrorException
   */
  public static void insertDummySchedule(EipTSchedule schedule, int ownerid,
      Date startDate, Date endDate, int[] memberIdList)
      throws ALDBErrorException {

    // ダミーのスケジュールを登録する．
    EipTSchedule dummySchedule = Database.create(EipTSchedule.class);
    // 親スケジュール ID
    dummySchedule.setParentId(schedule.getScheduleId());
    // 予定
    dummySchedule.setName("dummy");
    // 場所
    dummySchedule.setPlace("");
    // 内容
    dummySchedule.setNote("");
    // 公開フラグ
    dummySchedule.setPublicFlag("P");
    // 共有メンバーによる編集／削除フラグ
    dummySchedule.setEditFlag("F");
    // オーナーID
    dummySchedule.setOwnerId(Integer.valueOf(ownerid));
    // 作成日
    Date now2 = new Date();
    dummySchedule.setCreateDate(now2);
    dummySchedule.setCreateUserId(Integer.valueOf(ownerid));
    // 更新日
    dummySchedule.setUpdateDate(now2);
    dummySchedule.setUpdateUserId(Integer.valueOf(ownerid));
    dummySchedule.setRepeatPattern("N");
    dummySchedule.setStartDate(startDate);
    dummySchedule.setEndDate(endDate);

    EipTCommonCategory category =
      CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

    // // スケジュールを登録
    // orm.doInsert(dummySchedule);
    int size = memberIdList.length;
    for (int i = 0; i < size; i++) {
      EipTScheduleMap map = Database.create(EipTScheduleMap.class);
      int userid = memberIdList[i];
      // map.setPrimaryKey(dummySchedule.getScheduleId(), userid);
      map.setEipTSchedule(dummySchedule);
      map.setUserId(Integer.valueOf(userid));
      // D: ダミースケジュール
      map.setStatus("D");
      map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_USER);

      map.setCommonCategoryId(Integer.valueOf(1));
      map.setEipTCommonCategory(category);
    }

    // スケジュールを登録
    Database.commit();
  }

  public static void insertDummySchedule(EipTSchedule schedule, int ownerid,
      Date startDate, Date endDate, List<Integer> memberIdList,
      List<Integer> facilityIdList) throws ALDBErrorException {

    // ダミーのスケジュールを登録する．
    EipTSchedule dummySchedule = Database.create(EipTSchedule.class);

    // 親スケジュール ID
    dummySchedule.setParentId(schedule.getScheduleId());
    // 予定
    dummySchedule.setName("dummy");
    // 場所
    dummySchedule.setPlace("");
    // 内容
    dummySchedule.setNote("");
    // 公開フラグ
    dummySchedule.setPublicFlag("P");
    // 共有メンバーによる編集／削除フラグ
    dummySchedule.setEditFlag("F");
    // オーナーID
    dummySchedule.setOwnerId(Integer.valueOf(ownerid));
    // 作成日
    Date now2 = new Date();
    dummySchedule.setCreateDate(now2);
    dummySchedule.setCreateUserId(Integer.valueOf(ownerid));
    // 更新日
    dummySchedule.setUpdateDate(now2);
    dummySchedule.setUpdateUserId(Integer.valueOf(ownerid));
    dummySchedule.setRepeatPattern("N");
    dummySchedule.setStartDate(startDate);
    dummySchedule.setEndDate(endDate);

    EipTCommonCategory category =
      CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

    // orm.doInsert(dummySchedule);
    int size = memberIdList.size();
    for (int i = 0; i < size; i++) {
      EipTScheduleMap map = Database.create(EipTScheduleMap.class);
      int userid = (memberIdList.get(i)).intValue();
      map.setEipTSchedule(dummySchedule);
      map.setUserId(Integer.valueOf(userid));

      // D: ダミースケジュール
      map.setStatus("D");
      map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_USER);

      map.setCommonCategoryId(Integer.valueOf(1));
      map.setEipTCommonCategory(category);
    }

    if (facilityIdList != null) {
      int fsize = facilityIdList.size();
      for (int i = 0; i < fsize; i++) {
        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
        int fid = (facilityIdList.get(i)).intValue();
        map.setEipTSchedule(dummySchedule);
        map.setUserId(Integer.valueOf(fid));

        // D: ダミースケジュール
        map.setStatus("D");
        map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);

        map.setCommonCategoryId(Integer.valueOf(1));
        map.setEipTCommonCategory(category);
      }
    }

    // スケジュールを登録
    Database.commit();
  }

  /**
   * ダミースケジュールが上にくるようにソートする．
   * 
   * @param list
   * @return
   */
  public static List<EipTScheduleMap> sortByDummySchedule(
      List<EipTScheduleMap> list) {
    // 重複スケジュールの表示調節のために，
    // ダミースケジュールをリストの始めに寄せる．
    List<EipTScheduleMap> dummyList = new ArrayList<EipTScheduleMap>();
    List<EipTScheduleMap> normalList = new ArrayList<EipTScheduleMap>();
    EipTScheduleMap map = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      map = list.get(i);
      if ("D".equals(map.getStatus())) {
        dummyList.add(map);
      } else {
        normalList.add(map);
      }
    }

    list.clear();
    list.addAll(dummyList);
    list.addAll(normalList);
    return list;
  }

  /**
   * 現在選択しているタブ名を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getCurrentTab(RunData rundata, Context context) {
    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // 現在選択されているタブ
      // oneday : １日表示
      // weekly : 週間表示
      // monthly: 月間表示
      if (rundata.getParameters().containsKey("tab")) {
        ALEipUtils.setTemp(rundata, context, "tab", rundata
          .getParameters()
          .getString("tab"));
      }
    }
    String currentTab;
    String tmpCurrentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tmpCurrentTab == null
      || !(tmpCurrentTab.equals("oneday")
        || tmpCurrentTab.equals("weekly")
        || tmpCurrentTab.equals("monthly")
        || tmpCurrentTab.equals("oneday-group") || tmpCurrentTab
        .equals("weekly-group"))) {
      currentTab = "oneday";
    } else {
      currentTab = tmpCurrentTab;
    }
    return currentTab;
  }

  /**
   * Date のオブジェクトを指定した形式の文字列に変換する．
   * 
   * @param date
   * @param dateFormat
   * @return
   */
  public static String translateDate(Date date, String dateFormat) {
    if (date == null) {
      return "Unknown";
    }

    // 日付を表示形式に変換
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    sdf.setTimeZone(TimeZone.getDefault());
    return sdf.format(date);
  }

  /**
   * 指定した曜日が，選択範囲に入っているかを検証する．
   * 
   * @param selectedWeek
   *          指定曜日
   * @param startWeek
   *          期間開始曜日
   * @param endWeek
   *          期間終了曜日
   * @return 選択範囲に入っている場合，true．
   */
  public static boolean includeWeek(int selectedWeek, int startWeek, int endWeek) {
    if (startWeek <= endWeek) {
      if (startWeek <= selectedWeek && selectedWeek <= endWeek) {
        return true;
      } else {
        return false;
      }
    } else {
      if (selectedWeek < startWeek && endWeek < selectedWeek) {
        return false;
      } else {
        return true;
      }
    }
  }

  /**
   * 第一引数のリストに，第二引数で指定したユーザ ID が含まれているかを検証する．
   * 
   * @param memberIdList
   * @param memberId
   * @return
   */
  public static boolean isContains(List<Integer> memberIdList, int userId) {
    int size = memberIdList.size();
    Integer tmpInt = null;
    for (int i = 0; i < size; i++) {
      tmpInt = memberIdList.get(i);
      if (userId == tmpInt.intValue()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 第一引数のリストに，第二引数で指定したユーザ ID が含まれているかを検証する．
   * 
   * @param memberIdList
   * @param memberId
   * @return
   */
  public static boolean isContains(List<ALEipUser> memberList, ALEipUser user) {
    int size = memberList.size();
    long userid = user.getUserId().getValue();
    ALEipUser member = null;
    for (int i = 0; i < size; i++) {
      member = memberList.get(i);
      if (member.getUserId().getValue() == userid) {
        return true;
      }
    }
    return false;
  }

  public static boolean removeUser(List<ALEipUser> memberList, ALEipUser user) {
    if (memberList == null || memberList.size() == 0 || user == null) {
      return false;
    }

    int index = -1;
    int size = memberList.size();
    long userid = user.getUserId().getValue();
    ALEipUser member = null;
    for (int i = 0; i < size; i++) {
      member = memberList.get(i);
      if (member.getUserId().getValue() == userid) {
        index = i;
        break;
      }
    }

    if (index >= 0) {
      memberList.remove(index);
      return true;
    } else {
      return false;
    }
  }

  public static EipTScheduleMap getScheduleMap(
      List<EipTScheduleMap> scheduleMaps, int userid) {
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

  public static boolean setFormDataDelegate(RunData rundata, Context context,
      ALAbstractFormData formdata, Field[] fields, List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    if (fields == null || fields.length == 0) {
      return false;
    }

    boolean res = false;

    try {
      String FORMAT_DATE = "yyyyMMdd";
      String FORMAT_TIME = "HHmm";
      int FORMAT_DATE_LEN = FORMAT_DATE.length();
      int FORMAT_TIME_LEN = FORMAT_TIME.length();

      int length = fields.length;
      for (int i = 0; i < length; i++) {
        fields[i].setAccessible(true);
        String name = fields[i].getName();
        Object obj = fields[i].get(formdata);
        // フィールドが ALCellDateTimeField の場合
        if (obj instanceof ALCellDateTimeField) {
          String dateString =
            new StringBuffer().append(name).append("_date").toString();
          String timeString =
            new StringBuffer().append(name).append("_time").toString();

          ALCellDateTimeField field = (ALCellDateTimeField) obj;
          String dateStr = null;
          String timeStr = null;
          Calendar cal = Calendar.getInstance();
          if (rundata.getParameters().containsKey(dateString)) {
            dateStr = rundata.getParameters().getString(dateString);
          } else {
            continue;
          }
          if (rundata.getParameters().containsKey(timeString)) {
            timeStr = rundata.getParameters().getString(timeString);
          } else {
            continue;
          }

          if (dateStr.length() != FORMAT_DATE_LEN) {
            // 文字列の長さが正しくない場合
            msgList.add("『日付』で入力された年月日は存在しません");
            continue;
          }

          if (timeStr.length() != FORMAT_TIME_LEN) {
            // 文字列の長さが正しくない場合
            msgList.add("『時間』で入力された時間は存在しません");
            continue;
          }

          List<String> tmpList = new ArrayList<String>();
          ALCellStringField sf = new ALCellStringField(dateStr);
          sf.setTrim(true);
          sf.setCharacterType(ALStringField.TYPE_NUMBER);
          sf.setValue(dateStr);
          sf.validate(tmpList);
          if (tmpList.size() != 0) {
            msgList.add("『日付』で入力された年月日は存在しません");
            continue;
          }

          sf = new ALCellStringField(timeStr);
          sf.setTrim(true);
          sf.setCharacterType(ALStringField.TYPE_NUMBER);
          sf.setValue(dateStr);
          sf.validate(tmpList);
          if (tmpList.size() != 0) {
            msgList.add("『時間』で入力された時間は存在しません");
            continue;
          }

          Date date = null;
          // 日付を表示形式に変換
          SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
          sdf.setLenient(false);
          sdf.setTimeZone(TimeZone.getDefault());
          if (!dateStr.equals("")) {
            try {
              date = sdf.parse(dateStr);
            } catch (Exception e) {
              msgList.add("『日付』で入力された年月日は存在しません");
              continue;
            }
          } else {
            continue;
          }
          Date time = null;
          SimpleDateFormat sdf2 = new SimpleDateFormat(FORMAT_TIME);
          sdf2.setLenient(false);
          sdf2.setTimeZone(TimeZone.getDefault());
          if (!timeStr.equals("")) {
            try {
              time = sdf2.parse(timeStr);
            } catch (Exception e) {
              msgList.add("『時間』で入力された時間は存在しません");
              continue;
            }
          } else {
            continue;
          }

          date.setHours(time.getHours());
          date.setMinutes(time.getMinutes());
          cal.setLenient(false);
          cal.setTime(date);
          cal.set(Calendar.SECOND, 0);
          cal.set(Calendar.MILLISECOND, 0);

          field.setValue(cal.getTime());

          // フィールドが ALCellDateField の場合
        } else if (obj instanceof ALCellDateField) {
          ALCellDateField field = (ALCellDateField) obj;
          Date date = null;
          ALDateContainer con = new ALDateContainer();
          String dateString = new StringBuffer().append(name).toString();
          String dateStr = null;
          if (rundata.getParameters().containsKey(name)) {
            dateStr = rundata.getParameters().getString(dateString);
          } else {
            continue;
          }
          if (dateStr == null || dateStr.equals("")) {
            continue;
          }

          if (dateStr.length() != FORMAT_DATE_LEN) {
            // 文字列の長さが正しくない場合
            msgList.add("『日付』で入力された年月日は存在しません");
            continue;
          }

          List<String> tmpList = new ArrayList<String>();
          ALCellStringField sf = new ALCellStringField(dateStr);
          sf.setTrim(true);
          sf.setCharacterType(ALStringField.TYPE_NUMBER);
          sf.setValue(dateStr);
          sf.validate(tmpList);
          if (tmpList.size() != 0) {
            msgList.add("『日付』で入力された年月日は存在しません");
            continue;
          }

          // 日付を表示形式に変換
          SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
          sdf.setLenient(false);
          sdf.setTimeZone(TimeZone.getDefault());
          try {
            date = sdf.parse(dateStr);
          } catch (Exception e) {
            msgList.add("『日付』で入力された年月日は存在しません");
            continue;
          }

          date.setHours(0);
          date.setMinutes(0);
          Calendar cal = Calendar.getInstance();
          cal.setLenient(false);
          cal.setTime(date);
          cal.set(Calendar.SECOND, 0);
          cal.set(Calendar.MILLISECOND, 0);

          con.setYear(cal.get(Calendar.YEAR));
          con.setMonth(cal.get(Calendar.MONTH) + 1);
          con.setDay(cal.get(Calendar.DATE));
          field.setValue(con);

          // フィールドが ALAbstractField の場合
        } else if (obj instanceof ALAbstractField) {
          ALAbstractField field = (ALAbstractField) obj;
          if (rundata.getParameters().containsKey(name)) {
            field.setValue(rundata.getParameters().getString(name));
          }
        }
      }

      res = true;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  /**
   * パラメータを読み込みます。
   * 
   * @param rundata
   * @param context
   */
  public static void loadParametersDelegate(RunData rundata, Context context,
      String tmpStart, String tmpEnd, String tmpView) {
    ALDateTimeField dummy = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    dummy.setNotNull(true);
    if (ALEipUtils.isMatch(rundata, context)) {
      Calendar cal = Calendar.getInstance();
      int min = cal.get(Calendar.MINUTE);
      if (min <= 15) {
        cal.set(Calendar.MINUTE, 15);
      } else if (min <= 30) {
        cal.set(Calendar.MINUTE, 30);
      } else if (min <= 45) {
        cal.set(Calendar.MINUTE, 45);
      } else {
        cal.set(Calendar.MINUTE, 60);
      }
      Date now = cal.getTime();

      if (rundata.getParameters().containsKey("form_start")) {
        tmpStart = rundata.getParameters().getString("form_start");
        ALEipUtils.setTemp(rundata, context, "tmpStart", tmpStart);
        dummy.setValue(tmpStart);
        if (!dummy.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "form_start");
          ALEipUtils.removeTemp(rundata, context, "form_end");
          logger.debug("[ScheduleFormData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      } else {
        dummy.setValue(now);
        ALEipUtils.setTemp(rundata, context, "tmpStart", dummy.toString());
      }
      if (rundata.getParameters().containsKey("form_end")) {
        tmpEnd = rundata.getParameters().getString("form_end");
        ALEipUtils.setTemp(rundata, context, "tmpEnd", tmpEnd);
        dummy.setValue(tmpEnd);
        if (!dummy.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "form_start");
          ALEipUtils.removeTemp(rundata, context, "form_end");
          logger.debug("[ScheduleFormData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      } else {
        dummy.setValue(now);
        ALEipUtils.setTemp(rundata, context, "tmpEnd", dummy.toString());
      }
    }
    tmpStart = ALEipUtils.getTemp(rundata, context, "tmpStart");
    // tmpEnd = ALEipUtils.getTemp(rundata, context, "tmpEnd");
    loadParametersViewDate(rundata, context, tmpStart, tmpView);
  }

  public static void loadParametersViewDate(RunData rundata, Context context,
      String tmpStart, String tmpView) {
    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_date")) {
        ALDateTimeField dummy = new ALDateTimeField("yyyy-MM-dd");
        tmpView = rundata.getParameters().getString("view_date");
        ALEipUtils.setTemp(rundata, context, "tmpView", tmpView);
        dummy.setValue(tmpView);
        if (!dummy.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "tmpView");
          logger.debug("[ScheduleFormData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      } else {
        if (tmpView == null || tmpView.equals("")) {
          if (tmpStart != null && !tmpStart.equals("")) {
            tmpView = tmpStart;
            ALEipUtils.setTemp(rundata, context, "tmpView", tmpView);
          }
        }
      }
    }
  }

  public static boolean validateDelegate(ALDateTimeField start_date,
      ALDateTimeField end_date, ALStringField repeat_type, boolean is_repeat,
      boolean is_span, ALStringField week_0, ALStringField week_1,
      ALStringField week_2, ALStringField week_3, ALStringField week_4,
      ALStringField week_5, ALStringField week_6, ALStringField limit_flag,
      ALDateField limit_start_date, ALDateField limit_end_date,
      ALNumberField month_day, ALEipUser login_user, String entityid,
      List<String> msgList) throws ALDBErrorException, ALPageNotFoundException {
    int YEAR_FIRST = 2004;
    int YEAR_END = 2016;

    if (!is_repeat && !is_span) {
      // 開始日時
      start_date.validate(msgList);
      int startyear = start_date.getValue().getYear() + 1900;
      if (startyear < YEAR_FIRST || startyear > YEAR_END) {
        msgList.add("『日付』は" + YEAR_FIRST + "年から" + YEAR_END + "年の間で指定してください。");
      }
      if (start_date.getValue().getMinutes() % 15.0 != 0) {
        msgList.add("『時間』は15分単位で指定してください。");
      }

      // 終了日時
      end_date.validate(msgList);
      if (end_date.getValue().getMinutes() % 15.0 != 0) {
        msgList.add("『時間』は15分単位で指定してください。");
      }
    }

    // 開始日時＆終了日時
    if (end_date.getValue().before(start_date.getValue())) {
      msgList.add("『終了日時』は『開始日時』以降の日付を指定してください。");
    }

    if (is_repeat) {
      try {
        if ("W".equals(repeat_type.getValue())) {
          if (week_0.getValue() == null
            && week_1.getValue() == null
            && week_2.getValue() == null
            && week_3.getValue() == null
            && week_4.getValue() == null
            && week_5.getValue() == null
            && week_6.getValue() == null) {
            msgList.add("『毎週』は曜日をひとつ以上指定してください。");
          } else {
            // 期間を指定しているか．
            if ("ON".equals(limit_flag.getValue())) {
              int limitstartyear =
                limit_start_date.getValue().getDate().getYear() + 1900;
              if (limitstartyear < YEAR_FIRST || limitstartyear > YEAR_END) {
                msgList.add("『期限の開始日時』は"
                  + YEAR_FIRST
                  + "年から"
                  + YEAR_END
                  + "年の間で指定してください。");
              }
              int limitendyear =
                limit_end_date.getValue().getDate().getYear() + 1900;
              if (limitendyear < YEAR_FIRST || limitendyear > YEAR_END) {
                msgList.add("『期限の終了日時』は"
                  + YEAR_FIRST
                  + "年から"
                  + YEAR_END
                  + "年の間で指定してください。");
              }

              // 指定期間内に指定した曜日が入るか．
              long deltaDay =
                (limit_end_date.getValue().getDate().getTime() - limit_start_date
                  .getValue()
                  .getDate()
                  .getTime()) / 86400000;
              if (deltaDay < 6) {
                // 指定期間内に，月火水木金土日が必ずしも含まれない．
                Calendar limitStartCal = Calendar.getInstance();
                limitStartCal.setTime(limit_start_date.getValue().getDate());
                Calendar limitEndCal = Calendar.getInstance();
                limitEndCal.setTime(limit_end_date.getValue().getDate());
                int limitStartDayOfWeek =
                  limitStartCal.get(Calendar.DAY_OF_WEEK);
                int limitEndDayOfWeek = limitEndCal.get(Calendar.DAY_OF_WEEK);
                boolean hasWeek = true;
                if (week_0.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.SUNDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_1.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.MONDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_2.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.TUESDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_3.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.WEDNESDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_4.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.THURSDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_5.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.FRIDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_6.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.SATURDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (!hasWeek) {
                  msgList.add("『毎週』で選択する曜日は、『期限』の範囲内で指定してください。");
                }
              }
            }
          }
        } else if ("M".equals(repeat_type.getValue())) {
          if (month_day.getValue() == 0) {
            msgList.add("『毎月』は日にちを指定してください。");
          } else {
            month_day.validate(msgList);
            if ("ON".equals(limit_flag.getValue())) {
              int limitstartyear =
                limit_start_date.getValue().getDate().getYear() + 1900;
              if (limitstartyear < YEAR_FIRST || limitstartyear > YEAR_END) {
                msgList.add("『期限の開始日時』は"
                  + YEAR_FIRST
                  + "年から"
                  + YEAR_END
                  + "年の間で指定してください。");
              }
              int limitendyear =
                limit_end_date.getValue().getDate().getYear() + 1900;
              if (limitendyear < YEAR_FIRST || limitendyear > YEAR_END) {
                msgList.add("『期限の終了日時』は"
                  + YEAR_FIRST
                  + "年から"
                  + YEAR_END
                  + "年の間で指定してください。");
              }

              // 毎月の日付指定
              Calendar limitStartCal = Calendar.getInstance();
              limitStartCal.setTime(limit_start_date.getValue().getDate());
              Calendar limitEndCal = Calendar.getInstance();
              limitEndCal.setTime(limit_end_date.getValue().getDate());
              int limitStartYear = limitStartCal.get(Calendar.YEAR);
              int limitEndYear = limitEndCal.get(Calendar.YEAR);
              int limitStartMonth = limitStartCal.get(Calendar.MONTH);
              int limitEndMonth = limitEndCal.get(Calendar.MONTH);
              if (limitStartYear >= limitEndYear) {
                if (limitStartMonth >= limitEndMonth) {
                  // 同じ月
                  if (limit_start_date.getValue().getDay() > month_day
                    .getValue()
                    || limit_end_date.getValue().getDay() < month_day
                      .getValue()) {
                    msgList.add("『毎月』は『期限』の範囲内の日付を指定してください。");
                  }
                } else {
                  if (limit_start_date.getValue().getDay() > month_day
                    .getValue()
                    && limit_end_date.getValue().getDay() < month_day
                      .getValue()) {
                    msgList.add("『毎月』は『期限』の範囲内の日付を指定してください。");
                  }
                }
              }
            }
          }
        } else {
          // 期間を指定しているか．
          if ("ON".equals(limit_flag.getValue())) {
            int limitstartyear =
              limit_start_date.getValue().getDate().getYear() + 1900;
            if (limitstartyear < YEAR_FIRST || limitstartyear > YEAR_END) {
              msgList.add("『期限の開始日時』は"
                + YEAR_FIRST
                + "年から"
                + YEAR_END
                + "年の間で指定してください。");
            }
            int limitendyear =
              limit_end_date.getValue().getDate().getYear() + 1900;
            if (limitendyear < YEAR_FIRST || limitendyear > YEAR_END) {
              msgList.add("『期限の終了日時』は"
                + YEAR_FIRST
                + "年から"
                + YEAR_END
                + "年の間で指定してください。");
            }
          }
        }

        if ("ON".equals(limit_flag.getValue())) {
          // Date limitEndDate = limit_end_date.getValue().getDate();
          if (!ScheduleUtils.equalsToDate(
            limit_start_date.getValue().getDate(),
            limit_end_date.getValue().getDate(),
            false)
            && limit_start_date.getValue().getDate().after(
              limit_end_date.getValue().getDate())) {
            msgList.add("『期限の終了日時』は『期限の開始日時』以降の日付を指定してください。");
          }
        }
      } catch (NumberFormatException nfe) {
        logger
          .error("[ScheduleFormData] NumberFormatException: Limit Date is wrong.");
        throw new ALPageNotFoundException();
      } catch (ALIllegalDateException ad) {
        logger
          .error("[ScheduleFormData] ALIllegalDateException: Limit Date is wrong.");
        throw new ALPageNotFoundException();
      }
    }

    if (is_span) {
      // 開始日時
      start_date.validate(msgList);
      int startyear = start_date.getValue().getYear() + 1900;
      if (startyear < YEAR_FIRST || startyear > YEAR_END) {
        msgList
          .add("『開始日時』は" + YEAR_FIRST + "年から" + YEAR_END + "年の間で指定してください。");
      }

      // 終了日時
      end_date.validate(msgList);
      int endyear = end_date.getValue().getYear() + 1900;
      if (endyear < YEAR_FIRST || endyear > YEAR_END) {
        msgList
          .add("『終了日時』は" + YEAR_FIRST + "年から" + YEAR_END + "年の間で指定してください。");
      }

      try {
        String tmpScheduleId = entityid;
        boolean is_update = true;
        int id = 0;
        try {
          // IDが空の場合 || IDがintでない場合
          if (tmpScheduleId == null || !ALStringUtil.isNumber(tmpScheduleId)) {
            is_update = false;
          } else {
            is_update = true;
            id = Integer.valueOf(tmpScheduleId).intValue();
          }
        } catch (NumberFormatException ex) {
          // TODO: エラー処理
          logger
            .error("[ScheduleFormData] NumberFormatException: ENTITYID is wrong.");
          throw new ALPageNotFoundException();
        }

        SelectQuery<EipTScheduleMap> query =
          Database.query(EipTScheduleMap.class);

        Expression exp1 =
          ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
            .valueOf((int) login_user.getUserId().getValue()));
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.greaterOrEqualExp(
            EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
              + "."
              + EipTSchedule.END_DATE_PROPERTY,
            start_date.getValue());
        query.andQualifier(exp2);
        Expression exp3 =
          ExpressionFactory.lessOrEqualExp(
            EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
              + "."
              + EipTSchedule.START_DATE_PROPERTY,
            end_date.getValue());
        query.andQualifier(exp3);
        Expression exp4 =
          ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
            + "."
            + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");
        query.andQualifier(exp4);

        if (is_update) {
          Expression mapexp =
            ExpressionFactory.noMatchExp(
              EipTScheduleMap.SCHEDULE_ID_PROPERTY,
              Integer.valueOf(id));
          query.andQualifier(mapexp);
        }
      } catch (Exception e) {
        logger.error("[ScheduleFormData]", e);
        throw new ALDBErrorException();

      }
    }

    return (msgList.size() == 0);
  }

  /**
   * 同一期間内に複数の ToDo を追加する. 第一引数の List を排他制御しないで処理するので注意.
   * 
   * @param weekSpanConList
   *          複数の期間スケジュールを保持するリスト
   * @param viewStartDate
   *          表示開始の年月日
   * @param index
   *          期間スケジュールの追加位置
   * @param rd
   *          期間スケジュール
   */
  public static void addToDo(List<ScheduleToDoWeekContainer> weekConList,
      Date viewStartDate, int index, ScheduleToDoResultData rd) {
    try {
      boolean success = false;
      if (weekConList.size() > 0) {
        ScheduleToDoWeekContainer tmpWeekCon = null;
        int size = weekConList.size();
        for (int i = 0; i < size; i++) {
          tmpWeekCon = weekConList.get(i);
          success = tmpWeekCon.canAddTodo(rd);
          if (success) {
            tmpWeekCon.addToDoResultData(index, rd);
            break;
          }
        }
      }

      if (!success) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(viewStartDate);
        // 週間スケジュールコンテナの初期化
        ScheduleToDoWeekContainer weekCon = new ScheduleToDoWeekContainer();
        weekCon.initField();
        weekCon.setViewStartDate(cal2);
        weekCon.addToDoResultData(index, rd);
        weekConList.add(weekCon);
      }

    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  public static boolean isZeroLength(String[] strs) {
    if (strs == null || strs.length <= 0) {
      return true;
    }

    int len = strs.length;
    for (int i = 0; i < len; i++) {
      if (strs[i] == null || "".equals(strs[i])) {
        return true;
      }
    }
    return false;
  }

  public static boolean isSpanDuplicate(int user_id, Date start_date,
      Date end_date, Integer update_id) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
        .valueOf(user_id));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        start_date);
    query.andQualifier(exp2);
    Expression exp3 =
      ExpressionFactory.lessOrEqualExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, end_date);
    query.andQualifier(exp3);
    Expression exp4 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");
    query.andQualifier(exp4);

    if (update_id != null) {
      Expression mapexp =
        ExpressionFactory.noMatchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          Integer.valueOf(update_id));
      query.andQualifier(mapexp);
    }

    List<EipTScheduleMap> list = query.fetchList();
    int count = (list != null && list.size() > 0) ? list.size() : 0;
    return count > 0;
  }

  public static List<ALEipUser> getALEipUsers(String[] ids)
      throws ALDBErrorException {

    int ids_len = ids.length;
    Integer tmp_id = null;
    List<Integer> tmp_ids = new ArrayList<Integer>();
    for (int i = 0; i < ids_len; i++) {
      try {
        tmp_id = Integer.valueOf(ids[i]);
        if (tmp_id.intValue() > 3) {
          tmp_ids.add(tmp_id);
        }
      } catch (Exception e) {
      }
    }

    Expression exp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, tmp_ids);
    List<TurbineUser> users =
      Database.query(TurbineUser.class, exp).fetchList();
    if (users.size() == 0) {
      return null;
    }

    List<ALEipUser> uList = new ArrayList<ALEipUser>();
    TurbineUser tuser = null;
    ALEipUser user = null;
    int len = users.size();
    for (int i = 0; i < len; i++) {
      tuser = users.get(i);
      user = new ALEipUser();
      user.initField();
      user.setUserId(tuser.getUserId().intValue());
      user.setName(tuser.getLoginName());
      user.setAliasName(tuser.getFirstName(), tuser.getLastName());
      uList.add(user);
    }
    return uList;
  }

  public static List<UserFacilityLiteBean> getALEipUserFacility(String[] ids,
      RunData rundata) throws ALDBErrorException {

    List<UserFacilityLiteBean> ulist = new ArrayList<UserFacilityLiteBean>();

    int ids_len = ids.length;
    Integer tmp_id = null;
    List<Integer> tmp_ids = new ArrayList<Integer>();
    List<String> f_ids = new ArrayList<String>();
    for (int i = 0; i < ids_len; i++) {
      if (ids[i].startsWith("f")) {
        // facilityIDをセット
        f_ids.add(ids[i].replace("f", ""));
      } else {
        // ユーザーID
        try {
          tmp_id = Integer.valueOf(ids[i]);
          if (tmp_id.intValue() > 3) {
            tmp_ids.add(tmp_id);
          }
        } catch (Exception e) {
        }
      }
    }

    UserFacilityLiteBean user = null;
    // tmp_ids と f_ids をつかって
    // ユーザーIDのデータを取得
    if (tmp_ids != null && tmp_ids.size() > 0) {
      StringBuffer tmp_ids_str = new StringBuffer();
      int tmp_ids_len = tmp_ids.size() - 1;
      for (int i = 0; i < tmp_ids_len; i++) {
        tmp_ids_str.append(tmp_ids.get(i)).append(",");
      }
      tmp_ids_str.append(tmp_ids.get(tmp_ids_len));

      // SQLの作成
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT DISTINCT ");
      statement
        .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
      statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
      statement.append("LEFT JOIN TURBINE_USER as B ");
      statement.append("  on A.USER_ID = B.USER_ID ");
      statement.append("LEFT JOIN TURBINE_GROUP as C ");
      statement.append("  on A.GROUP_ID = C.GROUP_ID ");
      statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
      statement.append("  on A.USER_ID = D.USER_ID ");
      statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
      statement
        .append(" AND B.USER_ID IN (")
        .append(tmp_ids_str.toString())
        .append(")");
      statement.append("ORDER BY D.POSITION");
      String query = statement.toString();

      List<TurbineUser> users =
        Database.sql(TurbineUser.class, query).fetchList();

      int recNum = users.size();
      for (int j = 0; j < recNum; j++) {
        TurbineUser tuser = users.get(j);
        user = new UserFacilityLiteBean();
        user.initField();
        user.setUserFacilityId(tuser.getUserId());
        user.setName(tuser.getLoginName());
        user.setAliasName(tuser.getFirstName(), tuser.getLastName());
        user.setUserFacilityType("U");
        if (Integer.toString(ALEipUtils.getUserId(rundata)).equals(
          user.getUserFacilityId())) {
          ulist.add(0, user);
        } else {
          ulist.add(user);
        }
      }
    }

    // facilityIDを元にデータを取得
    if (f_ids != null && f_ids.size() > 0) {
      Expression f_exp =
        ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, f_ids);
      List<EipMFacility> facilities =
        Database.query(EipMFacility.class, f_exp).fetchList();
      if (facilities.size() == 0) {
        return null;
      }
      int f_size = facilities.size();
      for (int i = 0; i < f_size; i++) {
        EipMFacility f_user = facilities.get(i);
        user = new UserFacilityLiteBean();
        user.initField();
        user.setUserFacilityId(f_user.getFacilityId().intValue());
        user.setName("f" + user.getUserFacilityId());
        user.setAliasName(f_user.getFacilityName());
        user.setUserFacilityType("F");
        ulist.add(user);
      }
    }

    return ulist;
  }

  /*
   * 同一期間内に複数の 期間スケジュール を追加する. 第一引数の List を排他制御しないで処理するので注意.
   * 
   * @param weekSpanConList 複数の期間スケジュールを保持するリスト @param viewStartDate 表示開始の年月日
   * 
   * @param index 期間スケジュールの追加位置 @param rd 期間スケジュール
   */
  public static void addTerm(List<AjaxTermScheduleWeekContainer> weekConList,
      Date viewStartDate, int index, AjaxScheduleResultData rd) {
    try {
      boolean success = false;
      if (weekConList.size() > 0) {
        AjaxTermScheduleWeekContainer tmpWeekCon = null;
        int size = weekConList.size();
        for (int i = 0; i < size; i++) {
          tmpWeekCon = weekConList.get(i);
          success = tmpWeekCon.canAddTerm(rd);
          if (success) {
            tmpWeekCon.addTermResultData(index, rd);
            break;
          }
        }
      }

      if (!success) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(viewStartDate);
        // 週間スケジュールコンテナの初期化
        AjaxTermScheduleWeekContainer weekCon =
          new AjaxTermScheduleWeekContainer();
        weekCon.initField();
        weekCon.setViewStartDate(cal2);
        weekCon.addTermResultData(index, rd);
        weekConList.add(weekCon);
      }

    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /*
   * 同一期間内に複数の 期間スケジュール を追加する. 第一引数の List を排他制御しないで処理するので注意.
   * 
   * @param weekConList 複数の期間スケジュールを保持するリスト @param viewStartDate 表示開始の年月日 @param
   * index 期間スケジュールの追加位置 @param rd 期間スケジュール
   */
  public static void addTermSchedule(
      List<ScheduleTermWeekContainer> weekConList, Date viewStartDate,
      int index, ScheduleResultData rd) {
    try {
      boolean success = false;
      if (weekConList.size() > 0) {
        ScheduleTermWeekContainer tmpWeekCon = null;
        int size = weekConList.size();
        for (int i = 0; i < size; i++) {
          tmpWeekCon = weekConList.get(i);
          success = tmpWeekCon.canAddTerm(rd);
          if (success) {
            tmpWeekCon.addTermResultData(index, rd);
            break;
          }
        }
      }

      if (!success) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(viewStartDate);
        // 週間スケジュールコンテナの初期化
        ScheduleTermWeekContainer weekCon = new ScheduleTermWeekContainer();
        weekCon.initField();
        weekCon.setViewStartDate(cal2);
        weekCon.addTermResultData(index, rd);
        weekConList.add(weekCon);
      }

    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /**
   * 
   * @return
   */
  public static Date getEmptyDate() {
    Calendar cal = Calendar.getInstance();
    cal.set(9999, 11, 31);
    return cal.getTime();
  }

  public static String hasAuthOther(RunData rundata) {
    try {
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      if (aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST)) {
        return "T";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "F";
  }

  public static String hasAuthSelf(RunData rundata) {
    try {
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      if (aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
        ALAccessControlConstants.VALUE_ACL_LIST)) {
        return "T";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "F";
  }

  public static String hasAuthOtherUpdate(RunData rundata) {
    try {
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      if (aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE)) {
        return "T";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "F";
  }

  public static String hasAuthSelfUpdate(RunData rundata) {
    try {
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      if (aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
        ALAccessControlConstants.VALUE_ACL_UPDATE)) {
        return "T";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "F";
  }

  public static int getOwnerId(RunData rundata) {
    String scheduleId =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    if (scheduleId == null | "".equals(scheduleId)) {
      return ALEipUtils.getUserId(rundata);
    }
    Expression exp =
      ExpressionFactory.matchDbExp(EipTSchedule.SCHEDULE_ID_PK_COLUMN, Integer
        .valueOf(scheduleId));
    List<EipTSchedule> list =
      Database.query(EipTSchedule.class, exp).fetchList();
    if (list.size() == 0) {
      return ALEipUtils.getUserId(rundata);
    } else {
      return list.get(0).getOwnerId().intValue();
    }
  }

  public static boolean hasRelation(RunData rundata) {
    String scheduleId =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    if (scheduleId == null | "".equals(scheduleId)) {
      return false;
    }
    int userId = ALEipUtils.getUserId(rundata);
    Expression exp11 =
      ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, Integer
        .valueOf(scheduleId));
    Expression exp12 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
        .valueOf(userId));
    List<EipTScheduleMap> list =
      Database
        .query(EipTScheduleMap.class, exp11)
        .andQualifier(exp12)
        .fetchList();
    if (list.size() == 0) {
      return false;
    } else {
      return true;
    }
  }

  public static boolean hasMinimumAuthority(RunData rundata) {
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    boolean hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
        ALAccessControlConstants.VALUE_ACL_LIST);

    if (!hasAuthority) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    }
    return true;
  }

  /**
   * パソコンへ送信するメールの内容を作成する．
   * 
   * @return
   */
  public static String createMsgForPc(RunData rundata, EipTSchedule schedule,
      List<ALEipUser> memberList) {
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);
    ALEipUser loginUser = null;
    ALBaseUser user = null;
    String date_detail = "";

    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
      date_detail = getMsgDate(schedule);
    } catch (Exception e) {
      return "";
    }
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer("");
    body.append(loginUser.getAliasName().toString());
    if (!"".equals(user.getEmail())) {
      body.append("(").append(user.getEmail()).append(")");
    }
    body.append("さんが予定を追加しました。").append(CR).append(CR);
    body
      .append("[予定]")
      .append(CR)
      .append(schedule.getName().toString())
      .append(CR);
    body.append("[日時]").append(CR).append(date_detail).append(CR);

    if (schedule.getPlace().toString().length() > 0) {
      body
        .append("[場所] ")
        .append(CR)
        .append(schedule.getPlace().toString())
        .append(CR);
    }

    if (schedule.getNote().toString().length() > 0) {
      body
        .append("[内容]")
        .append(CR)
        .append(schedule.getNote().toString())
        .append(CR);
    }

    if (memberList != null) {
      int size = memberList.size();
      int i;
      body.append("[参加者]").append(CR);
      for (i = 0; i < size; i++) {
        if (i != 0) {
          body.append(", ");
        }
        ALEipUser member = memberList.get(i);
        body.append(member.getAliasName());
      }
      body.append(CR);
    }
    body.append(CR);
    body
      .append("[")
      .append(DatabaseOrmService.getInstance().getAlias())
      .append("へのアクセス]")
      .append(CR);
    if (enableAsp) {
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
    } else {
      body.append("・社外").append(CR);
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
      body.append("・社内").append(CR);
      body.append("　").append(ALMailUtils.getLocalurl()).append(CR).append(CR);
    }

    body.append("---------------------").append(CR);
    body.append(DatabaseOrmService.getInstance().getAlias()).append(CR);

    return body.toString();
  }

  /**
   * 携帯電話へ送信するメールの内容を作成する．
   * 
   * @return
   */
  public static String createMsgForCellPhone(RunData rundata,
      EipTSchedule schedule, List<ALEipUser> memberList, int destUserID) {
    ALEipUser loginUser = null;
    ALBaseUser user = null;
    String date_detail = "";
    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
      date_detail = getMsgDate(schedule);
    } catch (Exception e) {
      return "";
    }
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer("");
    body.append(loginUser.getAliasName().toString());
    if (!"".equals(user.getEmail())) {
      body.append("(").append(user.getEmail()).append(")");
    }
    body.append("さんが予定を追加しました。").append(CR).append(CR);
    body
      .append("[予定]")
      .append(CR)
      .append(schedule.getName().toString())
      .append(CR);
    body.append("[日時]").append(CR).append(date_detail).append(CR);

    if (memberList != null) {
      int size = memberList.size();
      int i;
      body.append("[参加者]").append(CR);
      for (i = 0; i < size; i++) {
        if (i != 0) {
          body.append(", ");
        }
        ALEipUser member = memberList.get(i);
        body.append(member.getAliasName());
      }
      body.append(CR);
    }
    body.append(CR);

    ALEipUser destUser;
    try {
      destUser = ALEipUtils.getALEipUser(destUserID);
    } catch (ALDBErrorException ex) {
      logger.error("Exception", ex);
      return "";
    }
    body
      .append("[")
      .append(DatabaseOrmService.getInstance().getAlias())
      .append("へのアクセス]")
      .append(CR);
    body.append("　").append(ALMailUtils.getGlobalurl()).append("?key=").append(
      ALCellularUtils.getCellularKey(destUser)).append(CR);
    body.append("---------------------").append(CR);
    body.append(DatabaseOrmService.getInstance().getAlias()).append(CR);
    return body.toString();
  }

  public static String getMsgDate(EipTSchedule schedule) {
    Calendar start_cal = Calendar.getInstance();
    start_cal.setTime(schedule.getStartDate());
    Calendar end_cal = Calendar.getInstance();
    end_cal.setTime(schedule.getEndDate());

    StringBuffer result = new StringBuffer();
    // DN -> 毎日 (A = N -> 期限なし A = L -> 期限あり)
    // WnnnnnnnN W01111110 -> 毎週(月～金用)
    // MnnN M25 -> 毎月25日
    // S -> 期間での指定
    String ptn = schedule.getRepeatPattern();
    int count = 0;
    boolean is_repeat = true;
    boolean is_span = false;
    // 毎日
    if (ptn.charAt(0) == 'D') {
      result.append("毎日");
      count = 1;
      // 毎週
    } else if (ptn.charAt(0) == 'W') {
      result.append(new StringBuffer()
        .append("毎週 ")
        .append(ptn.charAt(1) != '0' ? "日" : "")
        .append(ptn.charAt(2) != '0' ? "月" : "")
        .append(ptn.charAt(3) != '0' ? "火" : "")
        .append(ptn.charAt(4) != '0' ? "水" : "")
        .append(ptn.charAt(5) != '0' ? "木" : "")
        .append(ptn.charAt(6) != '0' ? "金" : "")
        .append(ptn.charAt(7) != '0' ? "土" : "")
        .append(" 曜日")
        .toString());
      count = 8;
      // 毎月
    } else if (ptn.charAt(0) == 'M') {
      result
        .append("毎月 ")
        .append(Integer.parseInt(ptn.substring(1, 3)))
        .append("日")
        .toString();
      count = 3;
      // 期間
    } else if (ptn.charAt(0) == 'S') {
      is_span = true;
      is_repeat = false;
    } else {
      is_repeat = false;
    }

    ALDateTimeField date_field = new ALDateTimeField("yyyy/MM/dd");
    ALDateTimeField time_field = new ALDateTimeField("HH:mm");

    if (!is_span) {
      if (!is_repeat) {
        date_field.setValue(schedule.getStartDate());
        result.append(date_field.toString());
      }
      time_field.setValue(schedule.getStartDate());
      result.append(" ").append(time_field.toString()).append("～");
      time_field.setValue(schedule.getEndDate());
      result.append(time_field.toString()).append(" ");
    } else {
      Date start = schedule.getStartDate();
      Date end = schedule.getEndDate();
      date_field.setValue(start);
      result.append(date_field.toString());
      if (!start.equals(end)) {
        result.append("～");
        date_field.setValue(end);
        result.append(date_field.toString());
      }
    }

    if (is_repeat) {
      if (ptn.charAt(count) == 'N') {
        // schedule.setLimit(false);
      } else {
        // schedule.setLimit(true);
        // 期限
        date_field.setValue(schedule.getStartDate());
        result.append(" （").append(date_field.toString()).append("～");
        date_field.setValue(schedule.getEndDate());
        result.append(date_field.toString()).append("）").toString();
      }
    }
    return result.toString();
  }

  public static boolean isDuplicateFacilitySchedule(EipTSchedule schedule,
      List<Integer> facilityIdList, Integer _old_scheduleid, Date _old_viewDate) {
    /* ダミースケジュール検索用 */
    GregorianCalendar cald = new GregorianCalendar();

    boolean result = false;
    {

      Date start_date;
      Date end_date;
      String repeat_pattern;
      String repeat_type;
      boolean week_0;
      boolean week_1;
      boolean week_2;
      boolean week_3;
      boolean week_4;
      boolean week_5;
      boolean week_6;
      String limit_flag;
      int month_day = -1;
      Integer db_scheduleid = null;
      boolean[] week_array = new boolean[7];
      boolean unlimited_repeat = false;
      try {
        start_date = schedule.getStartDate();

        end_date = schedule.getEndDate();

        repeat_pattern = schedule.getRepeatPattern();

        repeat_type = repeat_pattern.substring(0, 0);

        limit_flag = repeat_pattern.substring(repeat_pattern.length() - 1);

        week_0 = repeat_pattern.matches("W1.......");

        week_1 = repeat_pattern.matches("W.1......");

        week_2 = repeat_pattern.matches("W..1.....");

        week_3 = repeat_pattern.matches("W...1....");

        week_4 = repeat_pattern.matches("W....1...");

        week_5 = repeat_pattern.matches("W.....1..");

        week_6 = repeat_pattern.matches("W......1.");

        if (repeat_pattern.startsWith("M")) {
          month_day = Integer.parseInt(repeat_pattern.substring(1, 3));
        }

        // 単体スケジュールは期限1日のみのスケジュールとして判定
        if (repeat_pattern.startsWith("N")) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(start_date);
          int dow = cal.get(Calendar.DAY_OF_WEEK);
          week_0 = (dow == Calendar.SUNDAY);
          week_1 = (dow == Calendar.MONDAY);
          week_2 = (dow == Calendar.TUESDAY);
          week_3 = (dow == Calendar.WEDNESDAY);
          week_4 = (dow == Calendar.THURSDAY);
          week_5 = (dow == Calendar.FRIDAY);
          week_6 = (dow == Calendar.SATURDAY);
          month_day = cal.get(Calendar.DAY_OF_MONTH);
        } else if (repeat_pattern.endsWith("N")) {
          unlimited_repeat = true;
        }

        week_array[0] = week_0;
        week_array[1] = week_1;
        week_array[2] = week_2;
        week_array[3] = week_3;
        week_array[4] = week_4;
        week_array[5] = week_5;
        week_array[6] = week_6;

      } catch (Exception e) {
        return false;
      }

      // 施設予約状況をチェックする
      if (facilityIdList.size() > 0) {
        List<Integer> fids = facilityIdList;
        SelectQuery<EipTScheduleMap> fquery =
          Database.query(EipTScheduleMap.class);
        Expression fexp1 =
          ExpressionFactory.inExp(EipTScheduleMap.USER_ID_PROPERTY, fids);
        fquery.setQualifier(fexp1);

        Expression fexp2 =
          ExpressionFactory.matchExp(
            EipTScheduleMap.TYPE_PROPERTY,
            ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
        fquery.andQualifier(fexp2);

        Expression oneexp = null;
        Expression rdexp = null;
        Expression rwexp = null;
        // Expression rwlexp = null;
        Expression rmexp = null;

        { // １日スケジュールの検索
          Expression exp100 =
            ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
              + "."
              + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");

          try {
            if (!unlimited_repeat) {
              Expression exp101 =
                ExpressionFactory.lessOrEqualExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.START_DATE_PROPERTY,
                  end_date);
              Expression exp102 =
                ExpressionFactory.greaterExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.END_DATE_PROPERTY,
                  start_date);
              oneexp = exp100.andExp(exp101.andExp(exp102));
            } else {
              oneexp = exp100;
            }
          } catch (Exception e) {

          }
        }

        { // 繰り返しスケジュールの検索
          // char lim = 'N';
          if ("ON".equals(limit_flag)) {
            // lim = 'L';
          }

          { // "D".equals(repeat_type.getValue())
            Expression dexp01 =
              ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                + "."
                + EipTSchedule.REPEAT_PATTERN_PROPERTY, "D_");
            rdexp = dexp01;
          }

          { // "W".equals(repeat_type.getValue())
            Expression wexp = null;
            List<Expression> wexps = new ArrayList<Expression>();
            if (week_0 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W1_______");
              wexps.add(wexp);
            }
            if (week_1 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W_1______");
              wexps.add(wexp);
            }
            if (week_2 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W__1_____");
              wexps.add(wexp);
            }
            if (week_3 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W___1____");
              wexps.add(wexp);
            }
            if (week_4 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W____1___");
              wexps.add(wexp);
            }
            if (week_5 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W_____1__");
              wexps.add(wexp);
            }
            if (week_6 == true) {
              wexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W______1_");
              wexps.add(wexp);
            }
            if (wexps.size() > 0) {
              rwexp = wexps.get(0);
              int wexpssize = wexps.size();
              for (int k = 1; k < wexpssize; k++) {
                rwexp = rwexp.orExp(wexps.get(k));
              }
            } else {
              rwexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "W________");
            }
          }

          { // "M".equals(repeat_type.getValue())
            if (month_day > 0) { // 毎月、もしくは単体の場合
              DecimalFormat exF = new DecimalFormat("00");
              String md_str = exF.format(month_day);
              rmexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "M" + md_str + "_");
            } else {
              rmexp =
                ExpressionFactory.likeExp(
                  EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                    + "."
                    + EipTSchedule.REPEAT_PATTERN_PROPERTY,
                  "M___");
            }
          }

          Expression repeatexp = oneexp;
          if (rdexp != null) {
            repeatexp = repeatexp.orExp(rdexp);
          }
          if (rwexp != null) {
            repeatexp = repeatexp.orExp(rwexp);
          }
          if (rmexp != null) {
            repeatexp = repeatexp.orExp(rmexp);
          }
          fquery.andQualifier(repeatexp);
        }

        db_scheduleid = schedule.getScheduleId();
        if (db_scheduleid != null && db_scheduleid >= 0) {
          Expression exp00 =
            ExpressionFactory.noMatchDbExp(
              EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
                + "."
                + EipTSchedule.SCHEDULE_ID_PK_COLUMN,
              db_scheduleid);
          fquery.andQualifier(exp00);
        }

        fquery.distinct(true);
        List<EipTScheduleMap> f_list = fquery.fetchList();
        if (f_list != null && f_list.size() > 0) {
          // 繰り返しスケジュール同士の時刻幅での比較
          boolean existFacility = false;
          int f_list_size = f_list.size();
          for (int i = 0; i < f_list_size; i++) {
            EipTScheduleMap map = f_list.get(i);

            Date dbStartDate = map.getEipTSchedule().getStartDate();
            Date dbEndDate = map.getEipTSchedule().getEndDate();

            boolean containtsRs = false;
            // 繰り返し期限付きの処理
            String ptn = map.getEipTSchedule().getRepeatPattern();

            if (ptn.charAt(0) == 'N') { // 単体スケジュール
              if ("D".equals(repeat_type) || "N".equals(repeat_type)) { // 毎日 or
                // 単体
                try {
                  if ((dbStartDate.before(end_date) && dbEndDate
                    .after(start_date))
                    || unlimited_repeat) {
                    containtsRs = true;
                  }
                } catch (Exception e) {
                  containtsRs = false;
                }
              } else {
                if ((dbStartDate.before(end_date) && dbEndDate
                  .after(start_date))
                  || unlimited_repeat) {
                  containtsRs = true;
                }
              }

            } else if (ptn.charAt(0) == 'D') {
              if (ptn.charAt(1) == 'L') {
                try {
                  if ((dbStartDate.before(end_date) && dbEndDate
                    .after(start_date))
                    || unlimited_repeat) {
                    containtsRs = true;
                  }
                } catch (Exception e) {
                  containtsRs = false;
                }
              } else {
                containtsRs = true;
              }
            } else if (ptn.charAt(0) == 'W') {
              if (ptn.charAt(8) == 'L') {
                try {
                  if ((dbStartDate.before(end_date) && dbEndDate
                    .after(start_date))
                    || unlimited_repeat) {
                    containtsRs = true;
                  }
                } catch (Exception e) {
                  containtsRs = false;
                }
              } else {
                containtsRs = true;
              }
            } else if (ptn.charAt(0) == 'M') {
              if (ptn.charAt(3) == 'L') {
                try {
                  if ((dbStartDate.before(end_date) && dbEndDate
                    .after(start_date))
                    || unlimited_repeat) {
                    containtsRs = true;
                  }
                } catch (Exception e) {
                  containtsRs = false;
                }
              } else {
                containtsRs = true;
              }
            } else {
              containtsRs = true;
            }

            if (containtsRs) {
              int ss_flg = ScheduleUtils.compareTime(start_date, dbEndDate);
              int se_flg = ScheduleUtils.compareTime(end_date, dbStartDate);
              if (ss_flg > 0 && se_flg < 0) {
                /* 期限無しのスケジュール同士の場合は重複とみなす */
                if (!"N".equals(ptn) && ptn.endsWith("N") && unlimited_repeat) {
                  existFacility = true;
                } else {
                  Date _start_date = null;
                  Date _end_date = null;

                  if (!"N".equals(ptn)
                    && ptn.endsWith("N")
                    && !unlimited_repeat) {
                    _start_date = (Date) start_date.clone();
                    _end_date = (Date) end_date.clone();
                  } else if (("N".equals(ptn) || !ptn.endsWith("N"))
                    && unlimited_repeat) {
                    _start_date = (Date) dbStartDate.clone();
                    _end_date = (Date) dbEndDate.clone();
                  } else if (("N".equals(ptn) || !ptn.endsWith("N"))
                    && !unlimited_repeat) {

                    if (dbStartDate.after(start_date)) {
                      _start_date = (Date) dbStartDate.clone();
                    } else {
                      _start_date = (Date) start_date.clone();
                    }

                    if (dbEndDate.before(end_date)) {
                      _end_date = (Date) dbEndDate.clone();
                    } else {
                      _end_date = (Date) end_date.clone();
                    }

                  }

                  if ((_start_date == null) || (_end_date == null)) {
                    continue;
                  }

                  /* 期限内の日付を全て比較 */
                  Expression dexp1 =
                    ExpressionFactory.matchExp(
                      EipTSchedule.NAME_PROPERTY,
                      "dummy");

                  Expression dexp2 =
                    ExpressionFactory.matchExp(
                      EipTSchedule.PARENT_ID_PROPERTY,
                      map.getScheduleId());

                  if (db_scheduleid != null) {
                    Expression dexp21 =
                      ExpressionFactory.matchExp(
                        EipTSchedule.PARENT_ID_PROPERTY,
                        db_scheduleid);
                    dexp2 = dexp2.orExp(dexp21);
                  }
                  Expression dexp3 = null;

                  cald.setTime(_start_date);
                  cald.set(Calendar.MILLISECOND, 0);
                  cald.set(Calendar.SECOND, 0);
                  cald.set(Calendar.MINUTE, 0);
                  cald.set(Calendar.HOUR_OF_DAY, 0);
                  Date ddate = cald.getTime();
                  List<EipTSchedule> temp = null;

                  if ("N".equals(repeat_pattern)) {
                    /* 繰り返しスケジュールのうちひとつだけを移動した場合の処理 */
                    if ((_old_scheduleid != null) && (_old_viewDate != null)) {
                      if ((_old_scheduleid.intValue() == map
                        .getScheduleId()
                        .intValue())
                        && compareToDate(_start_date, _old_viewDate) == 0) {
                        continue;
                      }
                    }

                    try {
                      dexp3 =
                        ExpressionFactory.matchExp(
                          EipTSchedule.START_DATE_PROPERTY,
                          ddate);
                      temp =
                        Database.query(
                          EipTSchedule.class,
                          dexp1.andExp(dexp2).andExp(dexp3)).fetchList();
                      if (temp == null || temp.size() <= 0) {
                        existFacility = true;
                        break;
                      }
                    } catch (Exception e) {
                      logger.error("[DuplicateFacilityCheck]: ", e);
                      existFacility = true;
                      break;
                    }
                  } else if (repeat_pattern.startsWith("D")) {
                    while (!ddate.after(_end_date)) {
                      if (matchDay(cald, ptn)) {
                        try {
                          dexp3 =
                            ExpressionFactory.matchExp(
                              EipTSchedule.START_DATE_PROPERTY,
                              ddate);
                          temp =
                            Database.query(
                              EipTSchedule.class,
                              dexp1.andExp(dexp2).andExp(dexp3)).fetchList();
                          if (temp == null || temp.size() <= 0) {
                            existFacility = true;
                            break;
                          }
                        } catch (Exception e) {
                          logger.error("[DuplicateFacilityCheck]: ", e);
                          existFacility = true;
                          break;
                        }
                      }
                      cald.add(Calendar.DATE, 1);
                      ddate = cald.getTime();
                    }
                  } else if (repeat_pattern.startsWith("W")) {
                    /* ダミースケジュールを探す */
                    int wlen = week_array.length;
                    if (wlen < 1) {
                      continue;
                    }
                    int k;
                    while (!ddate.after(_end_date)) {
                      k = (cald.get(Calendar.DAY_OF_WEEK) - 1) % wlen;
                      if ((week_array[k] == true) && matchDay(cald, ptn)) {
                        try {
                          dexp3 =
                            ExpressionFactory.matchExp(
                              EipTSchedule.START_DATE_PROPERTY,
                              ddate);
                          temp =
                            Database.query(
                              EipTSchedule.class,
                              dexp1.andExp(dexp2).andExp(dexp3)).fetchList();
                          if (temp == null || temp.size() <= 0) {
                            existFacility = true;
                            break;
                          }
                        } catch (Exception e) {
                          logger.error("[DuplicateFacilityCheck]: ", e);
                          existFacility = true;
                          break;
                        }
                      }
                      cald.add(Calendar.DATE, 1);
                      ddate = cald.getTime();
                    }
                  } else if (repeat_pattern.startsWith("M")) {
                    /* 比較開始日までカレンダー移動 */
                    cald.setTime(dbStartDate);
                    cald.set(Calendar.MILLISECOND, 0);
                    cald.set(Calendar.SECOND, 0);
                    cald.set(Calendar.MINUTE, 0);
                    cald.set(Calendar.HOUR_OF_DAY, 0);

                    if (month_day > 0) {
                      cald.set(Calendar.DAY_OF_MONTH, month_day);
                    } else {
                      continue;
                    }
                    Date tmp_date = cald.getTime();
                    while (tmp_date.before(ddate)) {
                      cald.add(Calendar.MONTH, 1);
                      /* 月によって日にちがないときのための処理 */
                      while (month_day > cald
                        .getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        cald.add(Calendar.MONTH, 1);
                        cald.set(Calendar.DAY_OF_MONTH, month_day);
                        if (tmp_date.before(tmp_date)) {
                          break;
                        }
                      }
                      tmp_date = cald.getTime();
                    }
                    ddate = tmp_date;
                    /* 比較開始 */
                    while (!ddate.after(_end_date)) {
                      if (matchDay(cald, ptn)) {
                        try {
                          dexp3 =
                            ExpressionFactory.matchExp(
                              EipTSchedule.START_DATE_PROPERTY,
                              ddate);
                          temp =
                            Database.query(
                              EipTSchedule.class,
                              dexp1.andExp(dexp2).andExp(dexp3)).fetchList();
                          if (temp == null || temp.size() <= 0) {
                            existFacility = true;
                            break;
                          }
                        } catch (Exception e) {
                          logger.error("[DuplicateFacilityCheck]: ", e);
                          existFacility = true;
                          break;
                        }
                      }
                      cald.add(Calendar.MONTH, 1);
                      /* 月によって日にちがないときのための処理 */
                      while (month_day > cald
                        .getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        cald.add(Calendar.MONTH, 1);
                        cald.set(Calendar.DAY_OF_MONTH, month_day);
                        if (!ddate.after(_end_date)) {
                          break;
                        }
                      }
                      ddate = cald.getTime();
                    }
                  } else {
                    continue;
                  }
                }
                // existFacility = true;
              }
            }
            if (existFacility) {
              break;
            }
          }
          if (existFacility) {
            return existFacility;
          }
        }
      }
    }
    return result;
  }

  private static boolean matchDay(Calendar cal, String repeat_ptn) { // カレンダーの日付が繰り返しパターンと重なっているか判定
    if (repeat_ptn == null || "".equals(repeat_ptn)) {
      return false;
    }
    if (repeat_ptn.startsWith("M")) {
      int month_day = Integer.parseInt(repeat_ptn.substring(1, 3));
      int ptn_day = cal.get(Calendar.DAY_OF_MONTH);
      return (month_day == ptn_day);
    } else if (repeat_ptn.startsWith("W")) {
      int dow = cal.get(Calendar.DAY_OF_WEEK);
      if (dow == Calendar.SUNDAY) {
        return repeat_ptn.matches("W1.......");
      }
      if (dow == Calendar.MONDAY) {
        return repeat_ptn.matches("W.1......");
      }
      if (dow == Calendar.TUESDAY) {
        return repeat_ptn.matches("W..1.....");
      }
      if (dow == Calendar.WEDNESDAY) {
        return repeat_ptn.matches("W...1....");
      }
      if (dow == Calendar.THURSDAY) {
        return repeat_ptn.matches("W....1...");
      }
      if (dow == Calendar.FRIDAY) {
        return repeat_ptn.matches("W.....1..");
      }
      if (dow == Calendar.SATURDAY) {
        return repeat_ptn.matches("W......1.");
      }
      return false;
    } else {
      return true;
    }
  }

}
