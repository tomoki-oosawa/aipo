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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * １日スケジュール（グループ）の検索結果を管理するクラスです。
 * 
 */
public class ScheduleDetailOnedaySelectData extends
    ScheduleOnedayGroupSelectData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleDetailOnedaySelectData.class.getName());

  /** <code>loginuserid</code> ログインユーザーID */
  private int loginuserid;

  /** <code>viewDate</code> 表示する日 */
  private ALDateTimeField view_date = null;

  /** <code>userid</code> ID（ユーザ or 設備） */
  private int userid = 0;

  /** <code>type</code> マップ種別（ユーザ or 設備） */
  private String type;

  private boolean hasAclviewOther = false;

  private ScheduleOnedayContainer con;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    loginuserid = ALEipUtils.getUserId(rundata);
    viewtype = "detail";

    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_date")) {
        view_date = new ALDateTimeField("yyyy-MM-dd");
        String tmpViewDate = rundata.getParameters().getString("view_date");
        view_date.setValue(tmpViewDate);
        if (!view_date.validate(new ArrayList<String>())) {
          view_date = null;
        }
      }
    }

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
    }

    // アクセス権限
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAclviewOther =
      aclhandler.hasAuthority(
        loginuserid,
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    con = new ScheduleOnedayContainer();
    con.initField();
    con.initHour(0, 24);
  }

  @Override
  protected List<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {

    if (view_date == null || userid <= 0) {
      return new ArrayList<VEipTScheduleList>();
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(view_date.getValue());
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MILLISECOND, -1);
    ALDateTimeField field = new ALDateTimeField();
    field.setValue(cal.getTime());

    List<Integer> ulist = new ArrayList<Integer>();
    if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(type)) {
      ulist.add(userid);
    }

    List<Integer> flist = new ArrayList<Integer>();
    if (ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(type)) {
      ulist.add(userid);
    }

    return ScheduleUtils.getScheduleList(
      loginuserid,
      view_date.getValue(),
      field.getValue(),
      ulist,
      flist);
  }

  @Override
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
    rd.initField();
    try {
      if ("R".equals(record.getStatus())) {
        return rd;
      }
      if (!ScheduleUtils.isView(view_date, record.getRepeatPattern(), record
        .getStartDate(), record.getEndDate())) {
        return rd;
      }

      boolean is_member = record.isMember();

      // Dummy スケジュールではない
      // 完全に隠す
      // 自ユーザー以外
      // 共有メンバーではない
      // オーナーではない
      if ((!"D".equals(record.getStatus()))
        && "P".equals(record.getPublicFlag())
        && (userid != record.getUserId().intValue())
        && (userid != record.getOwnerId().intValue())
        && !is_member) {
        return rd;
      } else if (!hasAclviewOther && !is_member) {// 閲覧権限がなく、グループでもない
        return rd;
      }
      if ("C".equals(record.getPublicFlag())
        && (userid != record.getUserId().intValue())
        && (userid != record.getOwnerId().intValue())
        && !is_member) {
        rd.setName(ALLocalizationUtils.getl10n("SCHEDULE_CLOSE_PUBLIC_WORD"));
        // 仮スケジュールかどうか
        rd.setTmpreserve(false);
      } else {
        rd.setName(record.getName());
        // 仮スケジュールかどうか
        rd.setTmpreserve("T".equals(record.getStatus()));
      }
      // ID
      rd.setScheduleId(record.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(record.getParentId().intValue());
      // 開始日時
      rd.setStartDate(record.getStartDate());
      // 終了日時
      rd.setEndDate(record.getEndDate());
      // 公開するかどうか
      rd.setPublic("O".equals(record.getPublicFlag()));
      // 非表示にするかどうか
      rd.setHidden("P".equals(record.getPublicFlag()));
      // ダミーか
      rd.setDummy("D".equals(record.getStatus()));
      // ログインユーザかどうか
      rd.setLoginuser(record.getUserId().intValue() == userid);
      // オーナーかどうか
      rd.setOwner(record.getOwnerId().intValue() == userid);
      // 共有メンバーかどうか
      rd.setMember(is_member);
      // 繰り返しパターン
      rd.setPattern(record.getRepeatPattern());

      // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        return rd;
      }

      if (!rd.getPattern().equals("N")) {
        // 繰り返しスケジュール
        if (!ScheduleUtils.isView(view_date, rd.getPattern(), rd
          .getStartDate()
          .getValue(), rd.getEndDate().getValue())) {
          return rd;
        }
        rd.setRepeat(true);
      }
      con.addResultData(rd, 0, 24, view_date);
    } catch (Exception e) {
      logger.error("schedule", e);
      return null;
    }
    return rd;
  }

  public ScheduleOnedayContainer getScheduleOnedayContainer() {
    return con;
  }
}
