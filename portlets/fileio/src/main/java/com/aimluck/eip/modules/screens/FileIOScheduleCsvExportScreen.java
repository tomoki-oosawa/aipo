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
package com.aimluck.eip.modules.screens;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.schedule.ScheduleExportListContainer;
import com.aimluck.eip.schedule.ScheduleExportResultData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 *
 */
public class FileIOScheduleCsvExportScreen extends ALCSVScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOScheduleCsvExportScreen.class.getName());

  private int userid;

  /** 閲覧権限の有無 */
  private boolean hasAclviewOther;

  private ScheduleExportListContainer con;

  private List<ALEipUser> users;

  private List<FacilityResultData> facilityAllList;

  private String fileNamePrefix;

  private String fileNameSuffix;

  /** 日付の表示フォーマット */
  public static final String DEFAULT_DATE_TIME_FORMAT = "yyyyMMdd";

  /**
   *
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   *
   * @param rundata
   * @return
   * @throws Exception
   */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    fileNamePrefix = "";
    fileNameSuffix = "";
    if (ALEipUtils.isAdmin(rundata)) {
      userid = ALEipUtils.getUserId(rundata);
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      hasAclviewOther =
        aclhandler.hasAuthority(
          userid,
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
          ALAccessControlConstants.VALUE_ACL_LIST);
      Map<Integer, List<ScheduleExportResultData>> map =
        new HashMap<Integer, List<ScheduleExportResultData>>();
      Map<Integer, Map<String, List<ScheduleExportResultData>>> dummyMap =
        new HashMap<Integer, Map<String, List<ScheduleExportResultData>>>();

      // Date viewStart =
      // DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.JAPAN).parse(
      // rundata.getParameters().get("start_day"));
      // Date viewEnd =
      // DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.JAPAN).parse(
      // rundata.getParameters().get("end_day"));

      DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      Date viewStart = format.parse(rundata.getParameters().get("start_day"));
      Date viewEnd = format.parse(rundata.getParameters().get("end_day"));

      int userid = ALEipUtils.getUserId(rundata);

      fileNameSuffix = getFileNameSurffix(viewStart, viewEnd);

      // 有効なユーザーを全て取得する
      users = ALEipUtils.getUsers("LoginUser");
      List<Integer> userIds = new ArrayList<Integer>();
      for (ALEipUser user : users) {
        userIds.add(user.getUserId().getValueWithInt());
      }
      // 有効な設備を全て取得する
      facilityAllList = FacilitiesUtils.getFacilityAllList();
      ArrayList<Integer> facilityList = new ArrayList<Integer>();
      for (FacilityResultData facility : facilityAllList) {
        facilityList.add(facility.getFacilityId().getValueWithInt());
      }

      List<VEipTScheduleList> scheduleList =
        ScheduleUtils.getScheduleList(
          userid,
          viewStart,
          viewEnd,
          userIds,
          facilityList,
          true);
      List<VEipTScheduleList> resultList =
        ScheduleUtils.sortByDummySchedule(scheduleList);

      Calendar cal5 = Calendar.getInstance();
      cal5.setTime(viewStart);
      Calendar cal6 = Calendar.getInstance();
      cal6.setTime(viewEnd);

      // 参加者調整後リスト
      con = new ScheduleExportListContainer();
      con.initField();
      con.setViewStartDate(cal5);
      con.setViewEndDate(cal6);

      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        StringBuffer sb = new StringBuffer();
        sb.append("開始日,開始時刻,終了日,終了時刻,場所,タイトル,内容,参加者,設備,公開,繰り返し,重複");

        // スケジュール全件抽出
        for (ListIterator<VEipTScheduleList> iterator =
          resultList.listIterator(resultList.size()); iterator.hasPrevious();) {
          ScheduleExportResultData resultData =
            getResultData(iterator.previous());
          if (resultData != null) {
            List<ScheduleExportResultData> list =
              new ArrayList<ScheduleExportResultData>();
            if (map.containsKey(resultData.getScheduleId().getValueWithInt())) {
              list = map.get(resultData.getScheduleId().getValueWithInt());
            }
            list.add(resultData);
            map.put(resultData.getScheduleId().getValueWithInt(), list);
            // dummyのリストを作成
            if (resultData.isDummy()) {
              Map<String, List<ScheduleExportResultData>> map2 =
                new HashMap<String, List<ScheduleExportResultData>>();
              List<ScheduleExportResultData> list2 =
                new ArrayList<ScheduleExportResultData>();
              if (dummyMap.containsKey(resultData
                .getParentId()
                .getValueWithInt())) {
                map2 = dummyMap.get(resultData.getParentId().getValueWithInt());
              }
              if (map2.containsKey(resultData.getViewDate())) {
                list2 = map2.get(resultData.getViewDate());
              }
              list2.add(resultData);
              map2.put(resultData.getViewDate(), list2);
              dummyMap.put(resultData.getParentId().getValueWithInt(), map2);
            }
          }
        }
        List<ScheduleExportResultData> arayList =
          new ArrayList<ScheduleExportResultData>();
        // 出力用データのみ抽出
        for (ScheduleExportResultData record : con.getScheduleList()) {
          boolean isContain = false;
          for (ScheduleExportResultData rd : arayList) {
            if (record.getScheduleId().getValue() == rd
              .getScheduleId()
              .getValue()
              && ScheduleUtils.equalsToDate(
                rd.getStartDate().getValue(),
                record.getStartDate().getValue(),
                false)) {
              // リスト登録済
              isContain = true;
              break;
            }
          }
          if (!isContain) {
            // 参加者・設備をリストアップ
            ArrayList<ALEipUser> members = new ArrayList<ALEipUser>();
            ArrayList<FacilityResultData> facilities =
              new ArrayList<FacilityResultData>();
            if (map.containsKey(record.getScheduleId().getValueWithInt())) {
              List<ScheduleExportResultData> list =
                map.get(record.getScheduleId().getValueWithInt());
              for (ScheduleExportResultData tmpRd : list) {
                if ("F".equals(tmpRd.getType())) {
                  for (FacilityResultData facility : facilityAllList) {
                    if (tmpRd.getUserId().getValueWithInt() == facility
                      .getFacilityId()
                      .getValueWithInt()) {
                      facilities.add(facility);
                    }
                  }
                } else if ("U".equals(tmpRd.getType())) {
                  for (ALEipUser user : users) {
                    if (tmpRd.getUserId().getValueWithInt() == user
                      .getUserId()
                      .getValueWithInt()) {
                      members.add(user);
                    }
                  }
                }
              }
            }
            // dummyを除外
            if (dummyMap.containsKey(record.getScheduleId().getValueWithInt())) {
              Map<String, List<ScheduleExportResultData>> map2 =
                dummyMap.get(record.getScheduleId().getValueWithInt());
              if (map2.containsKey(record.getViewDate())) {
                List<ScheduleExportResultData> list =
                  map2.get(record.getViewDate());
                for (ScheduleExportResultData tmpRd : list) {
                  if ("F".equals(tmpRd.getType())) {
                    for (FacilityResultData facility : facilityAllList) {
                      if (tmpRd.getUserId().getValueWithInt() == facility
                        .getFacilityId()
                        .getValueWithInt()) {
                        facilities.remove(facility);
                      }
                    }
                  } else if ("U".equals(tmpRd.getType())) {
                    for (ALEipUser user : users) {
                      if (tmpRd.getUserId().getValueWithInt() == user
                        .getUserId()
                        .getValueWithInt()) {
                        members.remove(user);
                      }
                    }
                  }
                }
              }
            }
            if (!record.isDummy()
              && (members.size() > 0 || facilities.size() > 0)) {
              // dummyでないスケジュールで参加メンバーがいるスケジュール
              record.addAllMember(members);
              record.addAllFacility(facilities);
              arayList.add(record);
            }
          }
        }
        for (ScheduleExportResultData record : arayList) {
          if (record.isPublic()) { // 公開スケジュールのみに限定
            sb.append(LINE_SEPARATOR);
            sb.append("\"");
            sb.append(record.getViewDate());
            sb.append("\",\"");
            sb.append(record.getStartDate());
            sb.append("\",\"");
            sb.append(record.getEndDateExport());
            sb.append("\",\"");
            sb.append(record.getEndDate());
            sb.append("\",\"");
            sb.append(record.getPlaceExport());
            sb.append("\",\"");
            sb.append(record.getNameExport());
            sb.append("\",\"");
            sb.append(record.getNoteExport());
            sb.append("\",\"");
            sb.append(record.getMemberNameExport());
            sb.append("\",\"");
            sb.append(record.getFacilityNameExport());
            // sb.append("\",\"");
            // sb.append(record.get公開());
            // sb.append("\",\"");
            // sb.append(record.get繰り返し());
            // sb.append("\",\"");
            // sb.append(record.get重複());
            sb.append("\"");
          }
        }

        return sb.toString();
      } catch (Exception e) {
        logger.error("FileIOScheduleCsvFileScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  /**
   * @param viewStart
   * @param viewEnd
   * @return
   */
  private String getFileNameSurffix(Date viewStart, Date viewEnd) {

    String viewStartFormat =
      new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT)
        .format(viewStart.getTime());

    Calendar cal = Calendar.getInstance();
    cal.setTime(viewEnd);
    cal.add(Calendar.DATE, -1);

    String viewEndFormat =
      new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(cal.getTime());

    return viewStartFormat + "-" + viewEndFormat;
  }

  /**
   * @param facilityAllList
   * @param users
   * @param previous
   * @return
   */
  private ScheduleExportResultData getResultData(VEipTScheduleList record) {
    ScheduleExportResultData rd = new ScheduleExportResultData();
    rd.initField();
    try {
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        // return rd;
        return null;
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
        return null;
      }
      if ("C".equals(record.getPublicFlag())
        && ("F".equals(record.getType()) || ("U".equals(record.getType()) && userid != record
          .getUserId()
          .intValue()))
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

      if (!hasAclviewOther && !is_member) {// 閲覧権限がなく、グループでもない
        // return rd;
        return null;
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
      rd.setCreateUser(ALEipUtils.getALEipUser(record.getCreateUserId()));
      rd.setNote(record.getNote());
      rd.setPlace(record.getPlace());
      rd.setDescription(record.getNote());

      if (!rd.getPattern().equals("N") && !rd.getPattern().equals("S")) {
        rd.setRepeat(true);
      }
      // 参加者、設備をセット
      rd.setType(record.getType());
      rd.setUserId(record.getUserId());
      con.addResultData(rd);
    } catch (Exception e) {
      logger.error("schedule", e);
      return null;
    }
    return rd;
  }

  @Override
  protected String getFileName() {
    return ALOrgUtilsService.getAlias()
      + fileNamePrefix
      + "_schedules_"
      + fileNameSuffix
      + ".csv";
  }
}
