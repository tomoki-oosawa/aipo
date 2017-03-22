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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュール詳細表示の検索結果を管理するクラスです。
 *
 */
public class ScheduleTooltipSelectData extends
    ALAbstractSelectData<EipTSchedule, EipTSchedule> {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleTooltipSelectData.class.getName());

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  /** <code>statusList</code> メンバーの状態 */
  private Map<Integer, String> statusList;

  /** <code>userid</code> ID（ユーザ or 設備） */
  private int userid;

  /** <code>type</code> マップ種別（ユーザ or 設備） */
  private String type;

  /** <code>loginuserid</code> ログインユーザーID */
  private int loginuserid;

  /** <code>viewDate</code> 表示する日 */
  private ALDateTimeField view_date;

  /** <code>facilities</code> 共有設備 */
  private List<FacilityResultData> facilities;

  /** <code>hasAuthorityOtherEdit</code> アクセス権限 */
  private boolean hasAuthorityOtherEdit = false;

  /** <code>hasAuthorityOtherDelete</code> アクセス権限 */
  private boolean hasAuthorityOtherDelete = false;

  /** <code>hasAuthoritySelfEdit</code> アクセス権限 */
  private boolean hasAuthoritySelfEdit = false;

  /** <code>hasAuthoritySelfDelete</code> アクセス権限 */
  private boolean hasAuthoritySelfDelete = false;

  /** アクセスコントロール用の変数 */
  private String aclPortletFeature;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // 展開されるパラメータは以下の通りです。
    // ・userid 形式 int

    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);
    view_date = new ALDateTimeField("yyyy-MM-dd");
    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_date")) {
        String tmpViewDate = rundata.getParameters().getString("view_date");
        view_date.setValue(tmpViewDate);
        if (!view_date.validate(new ArrayList<String>())) {
          logger.debug("[ScheduleSelectData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      }
    }

    loginuserid = ALEipUtils.getUserId(rundata);
    statusList = new HashMap<Integer, String>();

    if (rundata.getParameters().containsKey("userid")) {
      String tmpid = rundata.getParameters().getString("userid");
      if (tmpid != null && tmpid.startsWith(ScheduleUtils.TARGET_FACILITY_ID)) {
        userid =
          Integer.parseInt(tmpid.substring(ScheduleUtils.TARGET_FACILITY_ID
            .length(), tmpid.length()));
        type = ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY;
      } else {
        userid = rundata.getParameters().getInt("userid");
        type = ScheduleUtils.SCHEDULEMAP_TYPE_USER;
      }
    } else if (rundata.getParameters().containsKey("facilityid")) {
      userid =
        Integer.parseInt(rundata.getParameters().getString("facilityid"));
      type = ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY;
    } else {
      userid = loginuserid;
      type = ScheduleUtils.SCHEDULEMAP_TYPE_USER;
    }

    // 自分に関係のある予定なのかどうか判断する
    int scheduleId =
      Integer.valueOf(rundata.getParameters().getString("scheduleid"));
    if (!ScheduleUtils.hasRelation(rundata, scheduleId)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
    }

    // アクセス権限
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAuthorityOtherEdit =
      aclhandler.hasAuthority(
        loginuserid,
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    hasAuthorityOtherDelete =
      aclhandler.hasAuthority(
        loginuserid,
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);

    hasAuthoritySelfEdit =
      aclhandler.hasAuthority(
        loginuserid,
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    hasAuthoritySelfDelete =
      aclhandler.hasAuthority(
        loginuserid,
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
        ALAccessControlConstants.VALUE_ACL_DELETE);

  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTSchedule> selectList(RunData rundata, Context context) {
    // このメソッドは利用されません。
    return null;
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTSchedule selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return ScheduleUtils
      .getEipTScheduleDetailForTooltip(rundata, context, type);
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTSchedule obj) {
    // このメソッドは利用されません。
    return null;
  }

  /**
   *
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTSchedule record)
      throws ALPageNotFoundException, ALDBErrorException {
    ScheduleDetailResultData rd = new ScheduleDetailResultData();
    rd.initField();
    try {

      // 選択した予定に対するダミースケジュールを検索
      SelectQuery<EipTSchedule> schedulequery =
        Database.query(EipTSchedule.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipTSchedule.PARENT_ID_PROPERTY, record
          .getScheduleId());
      Expression exp2 =
        ExpressionFactory.matchExp(EipTSchedule.START_DATE_PROPERTY, view_date
          .getValue());
      schedulequery.setQualifier(exp1);
      schedulequery.andQualifier(exp2);
      List<Integer> scheduleList = new ArrayList<Integer>();
      List<EipTSchedule> dummyScheduleList = schedulequery.fetchList();

      scheduleList.add(record.getScheduleId());
      for (EipTSchedule dummy : dummyScheduleList) {
        scheduleList.add(dummy.getScheduleId());
      }

      // 元のスケジュール及びダミースケジュールに登録されているマップを検索
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.inExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          scheduleList);
      mapquery.setQualifier(mapexp1);
      mapquery.orderAscending(EipTScheduleMap.SCHEDULE_ID_PROPERTY);

      List<EipTScheduleMap> list = mapquery.fetchList();

      List<Integer> users = new ArrayList<Integer>();
      List<Integer> facilityIds = new ArrayList<Integer>();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTScheduleMap map = list.get(i);
        if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
          statusList.put(map.getUserId(), map.getStatus());
          // 表示するユーザーの場合
          if (map.getUserId().intValue() == userid) {
            // 仮スケジュールかどうか
            rd.setTmpreserve("T".equals(map.getStatus()));
            // 確定スケジュールかどうか
            rd.setConfirm("C".equals(map.getStatus()));
          }
          // 削除したユーザーはツールチップに表示しない
          if (!("R".equals(map.getStatus()) || "D".equals(map.getStatus()))) {
            users.add(map.getUserId());
          }

          if (userid == map.getUserId().intValue()) {
            EipTCommonCategory category = map.getEipTCommonCategory();
            if (category == null) {
              rd.setCommonCategoryName(null);
            } else {
              rd.setCommonCategoryName(category.getName());
            }
          }

        } else {
          facilityIds.add(map.getUserId());
        }
      }

      if (users.isEmpty()) {
        users.add(-1);
      }

      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
      query.setQualifier(exp);
      members = ALEipUtils.getUsersFromSelectQuery(query);
      // members = ALEipUtils.getUsersFromCriteria(rundata, new
      // Criteria().addIn(
      // TurbineUserConstants.USER_ID, users));

      if (facilityIds.size() > 0) {
        SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
        Expression fexp =
          ExpressionFactory.inDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            facilityIds);
        fquery.setQualifier(fexp);
        facilities = FacilitiesUtils.getFacilitiesFromSelectQuery(fquery);
      }
      // facilities = FacilitiesUtils.getFacilitiesFromCriteria(org_id,
      // new Criteria().addIn(EipMFacilityConstants.FACILITY_ID, facilityIds));

      if (ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(type)) {
        // 設備
        ALEipUser facilityuser = new ALEipUser();
        facilityuser.initField();
        facilityuser.setUserId(userid);
        facilityuser.setName("");
        facilityuser.setAliasName("", getFacilityName(facilities, userid));
        rd.setUser(facilityuser);
      } else {
        // ユーザー
        rd.setUser(ALEipUtils.getALEipUser(userid));
      }
      // タイプ
      rd.setType(type);
      // 開始日時
      rd.setStartDate(record.getStartDate());
      // オーナー
      rd.setOwner(record.getOwnerId().intValue() == loginuserid);
      // 終了日時
      rd.setEndDate(record.getEndDate());
      // タイトル
      rd.setName(record.getName());
      // ID
      rd.setScheduleId(record.getScheduleId().intValue());
      // 場所
      rd.setPlace(record.getPlace());
      // 内容
      rd.setNote(record.getNote());
      // 公開するかどうか
      rd.setPublic("O".equals(record.getPublicFlag()));
      // 非表示にするかどうか
      rd.setHidden("P".equals(record.getPublicFlag()));
      // 共有メンバーによる編集／削除フラグ
      rd.setEditFlag("T".equals(record.getEditFlag()));

      // DN -> 毎日 (A = N -> 期限なし A = L -> 期限あり)
      // WnnnnnnnN W01111110 -> 毎週(月～金用)
      // MnnN M25 -> 毎月25日
      // S -> 期間での指定
      String ptn = record.getRepeatPattern();
      int count = 0;
      boolean is_repeat = true;
      rd.setRepeat(true);
      // 毎日
      if (ptn.charAt(0) == 'D') {
        rd.addText(ALLocalizationUtils.getl10n("SCHEDULE_EVERY_DAY"));
        count = 1;
        // 毎週
      } else if (ptn.charAt(0) == 'W') {
        rd
          .addText(new StringBuffer()
            .append(ALLocalizationUtils.getl10n("SCHEDULE_EVERY_WEEK_SPACE"))
            .append(
              ptn.charAt(1) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_SUNDAY") : "")
            .append(
              ptn.charAt(2) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_MONDAY") : "")
            .append(
              ptn.charAt(3) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_TUSEDAY") : "")
            .append(
              ptn.charAt(4) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_WEDNESDAY") : "")
            .append(
              ptn.charAt(5) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_THURSDAY") : "")
            .append(
              ptn.charAt(6) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_FRIDAY") : "")
            .append(
              ptn.charAt(7) != '0' ? ALLocalizationUtils
                .getl10n("SCHEDULE_SATURDAY") : "")
            .append(ALLocalizationUtils.getl10n("SCHEDULE_A_DAY_OF_THE_WEEK"))
            .toString());
        count = 8;
        // 毎月
      } else if (ptn.charAt(0) == 'M') {
        if (ptn.substring(1, 3).equals("XX")) {
          rd.addText(new StringBuffer().append(
            ALLocalizationUtils.getl10n("SCHEDULE_EVERY_MONTH_SPACE")).append(
            ALLocalizationUtils.getl10n("SCHEDULE_END_OF_MONTH")).append(
            ALLocalizationUtils.getl10n("SCHEDULE_DAY")).toString());
        } else {
          rd.addText(new StringBuffer().append(
            ALLocalizationUtils.getl10n("SCHEDULE_EVERY_MONTH_SPACE")).append(
            Integer.parseInt(ptn.substring(1, 3))).append(
            ALLocalizationUtils.getl10n("SCHEDULE_DAY")).toString());
        }
        count = 3;
        // 期間
      } else if (ptn.charAt(0) == 'S') {
        rd.setSpan(true);
        rd.setRepeat(false);
        is_repeat = false;
      } else {
        rd.setRepeat(false);
        is_repeat = false;

      }
      if (is_repeat) {
        if (ptn.charAt(count) == 'N') {
          rd.setLimit(false);
        } else {
          rd.setLimit(true);
          // 期限
          rd.addText(new StringBuffer().append(" （").append(
            rd.getStartDate().getYear()).append(
            ALLocalizationUtils.getl10n("SCHEDULE_YEAR")).append(
            rd.getStartDate().getMonth()).append(
            ALLocalizationUtils.getl10n("SCHEDULE_MONTH")).append(
            rd.getStartDate().getDay()).append(
            ALLocalizationUtils.getl10n("SCHEDULE_UNTIL_DAY")).append(
            rd.getEndDate().getYear()).append(
            ALLocalizationUtils.getl10n("SCHEDULE_YEAR")).append(
            rd.getEndDate().getMonth()).append(
            ALLocalizationUtils.getl10n("SCHEDULE_MONTH")).append(
            rd.getEndDate().getDay()).append(
            ALLocalizationUtils.getl10n("SCHEDULE_FROM_DAY")).toString());
        }
      }
      // 登録者
      rd.setCreateUser(ALEipUtils.getALEipUser(record
        .getCreateUserId()
        .intValue()));
      // 更新者
      rd.setUpdateUser(ALEipUtils.getALEipUser(record
        .getUpdateUserId()
        .intValue()));
      // 作成日
      rd.setCreateDate(record.getCreateDate());
      // 更新日時
      rd.setUpdateDate(record.getUpdateDate());
      // ログインユーザーID
      rd.setLoginuser(loginuserid == userid);
      // Calendar cal = Calendar.getInstance();
      // cal.setTime(record.getStartDate());
      // cal.set(Calendar.HOUR, 0);
      // cal.set(Calendar.MINUTE, 0);
      // view_date.setValue(cal.getTime());
    } catch (RuntimeException e) {
      logger.error("schedule", e);

      return null;
    } catch (Exception e) {
      logger.error("schedule", e);

      return null;
    }
    return rd;
  }

  /*
   *
   */
  @Override
  protected Attributes getColumnMap() {
    // このメソッドは利用されません。
    return null;
  }

  private String getFacilityName(List<FacilityResultData> list, int id) {
    FacilityResultData rd = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      rd = list.get(i);
      if (rd.getFacilityId().getValue() == id) {
        return rd.getFacilityName().getValue();
      }
    }
    return "";
  }

  /**
   * 共有メンバーを取得します。
   *
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return members;
  }

  /**
   * 状態を取得します。
   *
   * @param id
   * @return
   */
  public String getStatus(long id) {
    return statusList.get(Integer.valueOf((int) id));
  }

  /**
   *
   * @return
   */
  public ALDateTimeField getViewDate() {
    return view_date;
  }

  public List<FacilityResultData> getFacilityList() {
    return facilities;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  /**
   * アクセス権限用メソッド。<br />
   * アクセス権限の有無を返します。
   *
   * @return
   */
  public boolean hasAuthorityOtherEdit() {
    return hasAuthorityOtherEdit;
  }

  public boolean hasAuthorityOtherDelete() {
    return hasAuthorityOtherDelete;
  }

  public boolean hasAuthoritySelfEdit() {
    return hasAuthoritySelfEdit;
  }

  public boolean hasAuthoritySelfDelete() {
    return hasAuthoritySelfDelete;
  }

}
