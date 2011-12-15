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

package com.aimluck.eip.report.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTReport;
import com.aimluck.eip.cayenne.om.portlet.EipTReportFile;
import com.aimluck.eip.cayenne.om.portlet.EipTReportMap;
import com.aimluck.eip.cayenne.om.portlet.EipTReportMemberMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.services.social.model.ALActivityPutRequest;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.user.beans.UserLiteBean;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のユーティリティクラスです。 <BR>
 * 
 */
public class ReportUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ReportUtils.class.getName());

  /** 未読 */
  public static final String DB_STATUS_UNREAD = "U";

  /** 既読 */
  public static final String DB_STATUS_READ = "R";

  public static final String DATE_TIME_FORMAT =
    ALDateTimeField.DEFAULT_DATE_TIME_FORMAT;

  public static final String REPORT_PORTLET_NAME = "Report";

  /** 報告書の添付ファイルを保管するディレクトリの指定 */
  public static final String FOLDER_FILEDIR_REPORT = JetspeedResources
    .getString("aipo.filedir", "");

  /** 報告書の添付ファイルを保管するディレクトリのカテゴリキーの指定 */
  public static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.report.categorykey",
    "");

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  /**
   * Report オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param mode_update
   * @return
   */
  public static EipTReport getEipTReport(RunData rundata, Context context)
      throws ALDBErrorException {
    String requestid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (requestid == null || Integer.valueOf(requestid) == null) {
        // Request IDが空の場合
        logger.debug("[ReportUtils] Empty ID...");
        return null;
      }

      SelectQuery<EipTReport> query = Database.query(EipTReport.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTReport.REPORT_ID_PK_COLUMN, requestid);
      query.setQualifier(exp1);

      List<EipTReport> requests = query.fetchList();

      if (requests == null || requests.size() == 0) {
        // 指定した Report IDのレコードが見つからない場合
        logger.debug("[ReportUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return requests.get(0);
    } catch (ALPageNotFoundException ex) {
      ALEipUtils.redirectPageNotFound(rundata);
      return null;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * Report オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param mode_update
   * @return
   */
  public static List<EipTReport> getEipTReport(EipTReport report)
      throws ALPageNotFoundException {
    try {

      SelectQuery<EipTReport> query = Database.query(EipTReport.class);

      Expression exp =
        ExpressionFactory.matchExp(EipTReport.REPORT_ID_PK_COLUMN, report);

      query.setQualifier(exp);

      return query.fetchList();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTReportFile getEipTReportFile(RunData rundata)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int attachmentIndex =
        rundata.getParameters().getInt("attachmentIndex", -1);
      if (attachmentIndex < 0) {
        // ID が空の場合
        logger.debug("[ReportUtils] Empty ID...");
        throw new ALPageNotFoundException();

      }

      SelectQuery<EipTReportFile> query = Database.query(EipTReportFile.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTReportFile.FILE_ID_PK_COLUMN, Integer
          .valueOf(attachmentIndex));
      query.andQualifier(exp);
      List<EipTReportFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        // 指定した ID のレコードが見つからない場合
        logger.debug("[ReportUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return files.get(0);
    } catch (Exception ex) {
      logger.error("[ReportUtils]", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * マップオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static List<EipTReportFile> getEipTReportFile(EipTReport report) {
    try {
      SelectQuery<EipTReportFile> query = Database.query(EipTReportFile.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTReportFile.EIP_TREPORT_PROPERTY
          + "."
          + EipTReport.REPORT_ID_PK_COLUMN, report.getReportId());
      query.setQualifier(exp);

      List<EipTReportFile> maps = query.fetchList();

      if (maps == null || maps.size() == 0) {
        // 指定した Report IDのレコードが見つからない場合
        logger.debug("[ReportSelectData] Not found ID...");
        return null;
      }
      return maps;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * マップオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static List<EipTReportMap> getEipTReportMap(EipTReport report) {
    try {
      SelectQuery<EipTReportMap> query = Database.query(EipTReportMap.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTReportMap.EIP_TREPORT_PROPERTY
          + "."
          + EipTReport.REPORT_ID_PK_COLUMN, report.getReportId());
      query.setQualifier(exp);

      List<EipTReportMap> maps = query.fetchList();

      if (maps == null || maps.size() == 0) {
        // 指定した Report IDのレコードが見つからない場合
        logger.debug("[ReportSelectData] Not found ID...");
        return null;
      }
      return maps;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * マップオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static List<EipTReportMemberMap> getEipTReportMemberMap(
      EipTReport report) {
    try {
      SelectQuery<EipTReportMemberMap> query =
        Database.query(EipTReportMemberMap.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTReportMemberMap.EIP_TREPORT_PROPERTY
          + "."
          + EipTReport.REPORT_ID_PK_COLUMN, report.getReportId());
      query.setQualifier(exp);

      List<EipTReportMemberMap> members = query.fetchList();

      if (members == null || members.size() == 0) {
        // 指定した Report IDのレコードが見つからない場合
        logger.debug("[ReportSelectData] Not found ID...");
        return null;
      }
      return members;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId, int uid) {
    return ALStorageService.getDocumentPath(FOLDER_FILEDIR_REPORT, CATEGORY_KEY
      + ALStorageService.separator()
      + uid);
  }

  /**
   * ユーザ毎の保存先（相対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getRelativePath(String fileName) {
    return new StringBuffer().append("/").append(fileName).toString();
  }

  public static List<UserLiteBean> getAuthorityUsers(RunData rundata,
      String groupname, boolean includeLoginuser) {

    try {
      // アクセス権限
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      List<TurbineUser> ulist =
        aclhandler.getAuthorityUsersFromGroup(
          rundata,
          ALAccessControlConstants.POERTLET_FEATURE_REPORT_SELF,
          groupname,
          includeLoginuser);

      List<UserLiteBean> list = new ArrayList<UserLiteBean>();

      UserLiteBean user;
      // ユーザデータを作成し、返却リストへ格納
      for (TurbineUser tuser : ulist) {
        user = new UserLiteBean();
        user.initField();
        user.setUserId(tuser.getUserId());
        user.setName(tuser.getLoginName());
        user.setAliasName(tuser.getFirstName(), tuser.getLastName());
        list.add(user);
      }
      return list;
    } catch (InstantiationException e) {
      return null;
    }

  }

  /**
   * 表示切り替えのリセットフラグがあるかを返す．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static boolean hasResetFlag(RunData rundata, Context context) {
    String resetflag = rundata.getParameters().getString(RESET_FLAG);
    return resetflag != null;
  }

  public static void clearReportSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("submenu");
    list.add("com.aimluck.eip.report.ReportSelectDatasort");
    list.add("com.aimluck.eip.report.ReportSelectDatasorttype");
    list.add("com.aimluck.eip.report.ReportSelectDatafiltertype");
    list.add("com.aimluck.eip.report.ReportSelectDatafilter");
    ALEipUtils.removeTemp(rundata, context, list);
  }

  public static int getViewId(RunData rundata, Context context, int uid)
      throws ALDBErrorException {
    int view_uid = -1;
    EipTReport record = ReportUtils.getEipTReport(rundata, context);
    if (record != null) {
      view_uid = record.getUserId();
    } else {
      if (rundata.getParameters().containsKey("view_uid")) {
        view_uid =
          Integer.parseInt(rundata.getParameters().getString("view_uid"));
      } else {
        view_uid = uid;
      }
    }
    ALEipUtils.setTemp(rundata, context, "view_uid", String.valueOf(view_uid));
    return view_uid;
  }

  /**
   * ファイル検索のクエリを返します
   * 
   * @param requestid
   *          ファイルを検索するリクエストのid
   * @return query
   */
  public static SelectQuery<EipTReportFile> getSelectQueryForFiles(int requestid) {
    SelectQuery<EipTReportFile> query = Database.query(EipTReportFile.class);
    Expression exp =
      ExpressionFactory.matchDbExp(EipTReport.REPORT_ID_PK_COLUMN, Integer
        .valueOf(requestid));
    query.setQualifier(exp);
    return query;
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
   * アクティビティを通知先・社内参加者の「あなた宛のお知らせ」に表示させる
   * 
   * @param report
   * @param loginName
   * @param recipients
   * @param type
   */
  public static void createReportActivity(EipTReport report, String loginName,
      List<String> recipients, Boolean type) {
    if (recipients != null && recipients.size() > 0) {

      StringBuilder b = new StringBuilder("報告書「");

      b.append(report.getReportName()).append("」").append(
        type ? "の確認依頼を出しました。" : "を編集しました。");

      String portletParams =
        new StringBuilder("?template=ReportDetailScreen")
          .append("&entityid=")
          .append(report.getReportId())
          .toString();
      ALActivityService.create(new ALActivityPutRequest()
        .withAppId("Report")
        .withLoginName(loginName)
        .withPortletParams(portletParams)
        .withRecipients(recipients)
        .withTile(b.toString())
        .witchPriority(1f)
        .withExternalId(String.valueOf(report.getReportId())));
    }
  }

  /**
   * アクティビティが公開である場合、「更新情報」に表示させる。
   * 
   * @param report
   * @param loginName
   * @param recipients
   * @param type
   */
  public static void createNewReportActivity(EipTReport report,
      String loginName, Boolean type) {

    StringBuilder b = new StringBuilder("報告書「");

    b.append(report.getReportName()).append("」").append(
      type ? "を追加しました。" : "を編集しました。");

    String portletParams =
      new StringBuilder("?template=ReportDetailScreen")
        .append("&entityid=")
        .append(report.getReportId())
        .toString();
    ALActivityService.create(new ALActivityPutRequest()
      .withAppId("Report")
      .withLoginName(loginName)
      .withPortletParams(portletParams)
      .withTile(b.toString())
      .witchPriority(0f)
      .withExternalId(String.valueOf(report.getReportId())));
  }

  /**
   * パソコンへ送信するメールの内容を作成する．
   * 
   * @return
   */
  public static String createMsgForPc(RunData rundata, EipTReport report,
      List<ALEipUser> memberList, List<ALEipUser> mapList, Boolean type) {
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);
    ALEipUser loginUser = null;
    ALBaseUser user = null;

    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer("");
    body.append(loginUser.getAliasName().toString());
    if (!"".equals(user.getEmail())) {
      body.append("(").append(user.getEmail()).append(")");
    }
    body
      .append("さんが報告書")
      .append(type ? "を追加しました。" : "を編集しました。")
      .append(CR)
      .append(CR);
    body
      .append("[表題]")
      .append(CR)
      .append(report.getReportName().toString())
      .append(CR);
    body.append("[日時]").append(CR).append(
      translateDate(report.getCreateDate(), "yyyy年M月d日H時m分")).append(CR);

    if (report.getNote().toString().length() > 0) {
      body
        .append("[内容]")
        .append(CR)
        .append(report.getNote().toString())
        .append(CR);
    }

    if (memberList != null) {
      int size = memberList.size();
      int i;
      body.append("[社内参加者]").append(CR);
      for (i = 0; i < size; i++) {
        if (i != 0) {
          body.append(", ");
        }
        ALEipUser member = memberList.get(i);
        body.append(member.getAliasName());
      }
      body.append(CR);
    }

    if (mapList != null) {
      int size = mapList.size();
      int i;
      body.append("[通知先]").append(CR);
      for (i = 0; i < size; i++) {
        if (i != 0) {
          body.append(", ");
        }
        ALEipUser member = mapList.get(i);
        body.append(member.getAliasName());
      }
      body.append(CR);
    }
    body.append(CR);
    body
      .append("[")
      .append(ALOrgUtilsService.getAlias())
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
    body.append(ALOrgUtilsService.getAlias()).append(CR);

    return body.toString();
  }

  /**
   * 携帯電話へ送信するメールの内容を作成する．
   * 
   * @return
   */
  public static String createMsgForCellPhone(RunData rundata,
      EipTReport report, List<ALEipUser> memberList, List<ALEipUser> mapList,
      Boolean type, int destUserID) {
    ALEipUser loginUser = null;
    ALBaseUser user = null;
    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer("");
    body.append(loginUser.getAliasName().toString());
    if (!"".equals(user.getEmail())) {
      body.append("(").append(user.getEmail()).append(")");
    }
    body
      .append("さんが報告書")
      .append(type ? "を追加しました。" : "を編集しました。")
      .append(CR)
      .append(CR);
    body
      .append("[表題]")
      .append(CR)
      .append(report.getReportName().toString())
      .append(CR);
    body.append("[日時]").append(CR).append(
      translateDate(report.getCreateDate(), "yyyy年M月d日H時m分")).append(CR);

    if (memberList != null) {
      int size = memberList.size();
      int i;
      body.append("[社内参加者]").append(CR);
      for (i = 0; i < size; i++) {
        if (i != 0) {
          body.append(", ");
        }
        ALEipUser member = memberList.get(i);
        body.append(member.getAliasName());
      }
      body.append(CR);
    }
    if (mapList != null) {
      int size = mapList.size();
      int i;
      body.append("[通知先]").append(CR);
      for (i = 0; i < size; i++) {
        if (i != 0) {
          body.append(", ");
        }
        ALEipUser member = mapList.get(i);
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
      .append(ALOrgUtilsService.getAlias())
      .append("へのアクセス]")
      .append(CR);
    body.append("　").append(ALMailUtils.getGlobalurl()).append("?key=").append(
      ALCellularUtils.getCellularKey(destUser)).append(CR);
    body.append("---------------------").append(CR);
    body.append(ALOrgUtilsService.getAlias()).append(CR);
    return body.toString();
  }
}
