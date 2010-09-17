/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

package com.aimluck.eip.schedule.daemon;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletConfig;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonConfig;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.services.daemonfactory.DaemonFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.servlet.TurbineServlet;

import com.aimluck.commons.field.ALCellDateTimeField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.CellScheduleResultData;
import com.aimluck.eip.schedule.ScheduleToDoResultData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.daemonfactory.AipoDaemonFactoryService;
import com.aimluck.eip.system.util.SystemUtils;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * <p>
 * A daemon that parses out Jetspeed content sources. It also handles multiple
 * updating Feeds within PortletFactory. When it encounters any RSS feeds that
 * are remote it will pull them locally into the JetspeedDiskCache class via the
 * bulkdownloader class.
 * </p>
 * 
 * <p>
 * The major goals of this Daemon are:
 * 
 * <ul>
 * <li>Parse out OCS feeds</li>
 * <li>Put the new Entry into the PortletRegistry</li>
 * <li>Get the URL from the Internet if it hasn't been placed in the cache.</li>
 * <li>Instantiate the Portlet if it already isn't in the cache.</li>
 * </ul>
 * 
 * </p>
 * 
 */
public class RemainderScheduleDaemon implements Daemon {

  // private static RemainderScheduleDaemon instance = null;

  private int status = Daemon.STATUS_NOT_PROCESSED;

  private int result = Daemon.RESULT_UNKNOWN;

  private DaemonConfig config = null;

  private DaemonEntry entry = null;

  /**
   * 実行可能かを判定．最初のメール送信時刻になるまでfalse． それ以降はtrueにし，intervalを24時間に変更する．
   */
  private boolean enable_run = false;

  /** メール送信時刻（時、24時間表記） */
  private int send_time_hour = 15;

  /** メール送信時刻（分） */
  private int send_time_minutes = 0;

  /** <code>viewDate</code> 表示する日付 */
  private ALDateTimeField viewDate;

  private String servlet_name = "";

  @SuppressWarnings("unused")
  private String scheme = null;

  @SuppressWarnings("unused")
  private int port = 80;

  @SuppressWarnings("unused")
  private String realpath = "";

  private String localurl = "";

  /** ASP版かのフラグ */
  private boolean enableAsp = false;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(RemainderScheduleDaemon.class.getName());

  /**
   */
  public void run() {
    if (!enable_run) {
      // Aipo起動時のみ実行しないようにする．
      enable_run = true;
      return;
    }
    // if (entry.getInterval() != 60) {
    // this.entry = new DaemonEntry(entry.getName(), 60, entry.getClassname(),
    // entry.onStartup());
    // }

    try {
      this.setResult(Daemon.RESULT_PROCESSING);
      doViewList();
      doCheck();
      this.setResult(Daemon.RESULT_SUCCESS);
    } catch (Exception e) {
      this.setResult(Daemon.RESULT_FAILED);
      logger.error("RemainderScheduleDaemon", e);
    }

  }

  private boolean doCheck() {
    boolean res = false;

    Calendar cal = Calendar.getInstance();
    Calendar cal_now = Calendar.getInstance();
    long now_millis = cal.getTimeInMillis();

    cal.set(Calendar.HOUR_OF_DAY, send_time_hour);
    cal.set(Calendar.MINUTE, send_time_minutes);

    if (cal_now.getTimeInMillis() < cal.getTimeInMillis()) {
    } else {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    long next_millis = cal.getTimeInMillis();

    long interval = (next_millis - now_millis) / 1000;

    this.entry =
      new DaemonEntry(entry.getName(), interval, entry.getClassname(), entry
        .onStartup());

    return res;
  }

  /**
   * 一覧表示します。
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FASLE 失敗
   */
  private void doViewList() throws Exception {
    initList();

    Object obj = null;
    List<ScheduleToDoResultData> todolist = null;
    ALEipUserAddr useraddr = null;
    String[] to = null;
    String msg = "";
    List<ALEipUserAddr> useraddrs = getAllUserAddrList();
    int useraddrs_size = useraddrs.size();
    for (int j = 0; j < useraddrs_size; j++) {
      try {
        useraddr = useraddrs.get(j);

        to = useraddr.getAddrs();
        if (to == null) {
          continue;
        }

        List<EipTScheduleMap> aList = selectList(useraddr.getUserId());

        if (aList == null) {
          continue;
        }

        List<Object> list = new ArrayList<Object>();
        int size = aList.size();
        for (int i = 0; i < size; i++) {
          obj = getResultData(aList.get(i));
          if (obj != null) {
            list.add(obj);
          }
        }

        // ToDo の読み込み
        todolist = getToDoList(useraddr.getUserId());
        msg = getSendMessage(useraddr, list, todolist);

        // メール送信処理
        ArrayList<ALEipUserAddr> destMemberList =
          new ArrayList<ALEipUserAddr>();
        destMemberList.add(useraddr);

        // ASP版のときは正しいorgidを設定すること。
        String org_id = DatabaseOrmService.getInstance().getDefaultOrgId();
        String subject =
          "[" + DatabaseOrmService.getInstance().getAlias() + "]スケジュール";

        // メール送信
        ALMailUtils.sendMailDelegate(
          org_id,
          1,
          destMemberList,
          subject,
          subject,
          msg,
          msg,
          ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE),
          new ArrayList<String>());

      } catch (Exception e) {
        logger.error("RemainderScheduleDaemon", e);
      }
    }
  }

  private void initList() {
    viewDate = new ALDateTimeField("yyyy-MM-dd");
    viewDate.setNotNull(true);
    // 今日の日付を設定する。
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    viewDate.setValue(cal.getTime());
  }

  private List<ALEipUserAddr> getAllUserAddrList() {
    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
    query.select(TurbineUser.USER_ID_PK_COLUMN);
    query.select(TurbineUser.EMAIL_COLUMN);
    query.select(TurbineUser.CELLULAR_MAIL_COLUMN);
    Expression exp1 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
    Expression exp2 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(1));
    Expression exp3 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(2));
    Expression exp4 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(3));

    query.setQualifier(exp1);
    query.andQualifier(exp2);
    query.andQualifier(exp3);
    query.andQualifier(exp4);

    List<TurbineUser> userids = query.fetchList();
    if (userids.size() == 0) {
      return null;
    }

    ALEipUserAddr useraddr = null;
    List<ALEipUserAddr> list = new ArrayList<ALEipUserAddr>();
    int size = userids.size();
    for (int i = 0; i < size; i++) {
      TurbineUser record = userids.get(i);
      useraddr = new ALEipUserAddr();
      useraddr.setUserId(record.getUserId());
      useraddr.setPcMailAddr(record.getEmail());
      useraddr.setCellMailAddr(record.getCellularMail());
      list.add(useraddr);
    }

    return list;
  }

  /**
   * 
   * @param userid
   * @return
   */
  private List<EipTScheduleMap> selectList(Integer userid) {
    List<EipTScheduleMap> resultBaseList = getSelectQuery(userid).fetchList();

    List<EipTScheduleMap> resultList =
      ScheduleUtils.sortByDummySchedule(resultBaseList);

    List<EipTScheduleMap> list = new ArrayList<EipTScheduleMap>();
    List<EipTScheduleMap> delList = new ArrayList<EipTScheduleMap>();
    int delSize = 0;
    int resultSize = resultList.size();
    int size = 0;
    boolean canAdd = true;
    for (int i = 0; i < resultSize; i++) {
      EipTScheduleMap record = resultList.get(i);
      EipTSchedule schedule = (record.getEipTSchedule());
      delList.clear();
      canAdd = true;
      size = list.size();
      for (int j = 0; j < size; j++) {
        EipTScheduleMap record2 = list.get(j);
        EipTSchedule schedule2 = (record2.getEipTSchedule());
        if (!schedule.getRepeatPattern().equals("N")
          && "D".equals(record2.getStatus())
          && schedule.getScheduleId().intValue() == schedule2
            .getParentId()
            .intValue()) {
          canAdd = false;
          break;
        }
        if (!schedule2.getRepeatPattern().equals("N")
          && "D".equals(record.getStatus())
          && schedule2.getScheduleId().intValue() == schedule
            .getParentId()
            .intValue()) {
          // [繰り返しスケジュール] 親の ID を検索
          if (!delList.contains(record2)) {
            delList.add(record2);
          }
          canAdd = true;
        }
      }
      delSize = delList.size();
      for (int k = 0; k < delSize; k++) {
        list.remove(delList.get(k));
      }

      if (canAdd) {
        list.add(record);
      }
    }

    // ダミーを削除する．
    delList.clear();
    size = list.size();
    for (int i = 0; i < size; i++) {
      EipTScheduleMap record = list.get(i);
      if ("D".equals(record.getStatus())) {
        delList.add(record);
      }
    }
    delSize = delList.size();
    for (int i = 0; i < delSize; i++) {
      list.remove(delList.get(i));
    }

    // ソート
    Collections.sort(list, new Comparator<EipTScheduleMap>() {

      public int compare(EipTScheduleMap a, EipTScheduleMap b) {
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        EipTSchedule p1 = null;
        EipTSchedule p2 = null;
        try {
          p1 = (a).getEipTSchedule();
          p2 = (b).getEipTSchedule();

        } catch (Exception e) {
          logger.error("Exception", e);
        }
        cal.setTime(p1.getStartDate());
        cal.set(0, 0, 0);
        cal2.setTime(p2.getStartDate());
        cal2.set(0, 0, 0);
        if ((cal.getTime()).compareTo(cal2.getTime()) != 0) {
          return (cal.getTime()).compareTo(cal2.getTime());
        } else {
          cal.setTime(p1.getEndDate());
          cal.set(0, 0, 0);
          cal2.setTime(p2.getEndDate());
          cal2.set(0, 0, 0);

          return (cal.getTime()).compareTo(cal2.getTime());
        }
      }
    });

    return list;

  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<EipTScheduleMap> getSelectQuery(Integer userid) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, userid);
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(
        EipTScheduleMap.TYPE_PROPERTY,
        ScheduleUtils.SCHEDULEMAP_TYPE_USER);
    query.andQualifier(exp2);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        viewDate.getValue());

    // 日付を1日ずつずらす
    Calendar cal = Calendar.getInstance();
    cal.setTime(viewDate.getValue());
    cal.add(Calendar.DATE, 1);
    ALCellDateTimeField field = new ALCellDateTimeField();
    field.setValue(cal.getTime());
    // 開始日時
    // LESS_EQUALからLESS_THANへ修正、期間スケジュールFIXのため(Haruo Kaneko)
    Expression exp12 =
      ExpressionFactory.lessExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, field.getValue());

    // 通常スケジュール
    Expression exp13 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    // 期間スケジュール
    Expression exp14 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");

    // 繰り返しスケジュール（週間）
    Calendar date = Calendar.getInstance();
    date.setTime(viewDate.getValue());
    int weekindex = date.get(Calendar.DAY_OF_WEEK);
    String token = null;
    StringBuffer sb = new StringBuffer();
    sb.append("W");
    for (int i = 0; i < 7; i++) {
      if (i == weekindex) {
        token = "1";
      } else {
        token = "_";
      }
      sb.append(token);
    }

    Expression exp21 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, (sb.toString() + "L"));
    Expression exp22 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, (sb.toString() + "N"));

    // 繰り返しスケジュール（日）
    Expression exp23 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "DN");
    Expression exp31 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "DL");

    // 繰り返しスケジュール（月）
    SimpleDateFormat sdf = new SimpleDateFormat("dd");
    sdf.setTimeZone(TimeZone.getDefault());
    String dayStr = sdf.format(date);

    Expression exp24 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, ("M" + dayStr + "L"));
    Expression exp25 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, ("M" + dayStr + "N"));

    query.andQualifier((exp11.andExp(exp12).andExp(((exp13).orExp(exp14))
      .orExp(exp21)
      .orExp(exp31)
      .orExp(exp24))).orExp(exp22.orExp(exp23).orExp(exp25)));

    // 開始日時でソート
    List<Ordering> orders = new ArrayList<Ordering>();
    orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.START_DATE_PROPERTY, true));
    orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.END_DATE_PROPERTY, true));
    query.getQuery().addOrderings(orders);

    return query;
  }

  /**
   * 
   * @param obj
   * @return
   */
  protected Object getResultData(Object obj) {
    CellScheduleResultData rd = new CellScheduleResultData();
    CellScheduleResultData rd2 = new CellScheduleResultData();
    rd.initField();
    rd2.setFormat("yyyy-MM-dd-HH-mm");
    rd2.initField();
    try {
      EipTScheduleMap record = (EipTScheduleMap) obj;
      EipTSchedule schedule = record.getEipTSchedule();
      if ("R".equals(record.getStatus())) {
        // return rd;
        return null;
      }
      if (!ScheduleUtils.isView(viewDate, schedule.getRepeatPattern(), schedule
        .getStartDate(), schedule.getEndDate())) {
        // return rd;
        return null;
      }
      // ID
      rd.setScheduleId(schedule.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(schedule.getParentId().intValue());
      // 予定
      rd.setName(schedule.getName());
      // 開始時間
      rd.setStartDate(schedule.getStartDate());
      // 終了時間
      rd.setEndDate(schedule.getEndDate());
      // 仮スケジュールかどうか
      rd.setTmpreserve("T".equals(record.getStatus()));
      // 公開するかどうか
      rd.setPublic("O".equals(schedule.getPublicFlag()));
      // 表示するかどうか
      rd.setHidden("P".equals(schedule.getPublicFlag()));
      // ダミーか
      // rd.setDummy("D".equals(record.getStatus()));
      // 繰り返しパターン
      rd.setPattern(schedule.getRepeatPattern());

      // // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        rd.setSpan(true);
        return rd;
      }

      // 繰り返しスケジュールの場合
      if (!rd.getPattern().equals("N")) {

        if (!ScheduleUtils.isView(viewDate, rd.getPattern(), rd
          .getStartDate()
          .getValue(), rd.getEndDate().getValue())) {
          return rd;
        }
        rd.setRepeat(true);
      }

    } catch (Exception e) {
      logger.error("Exception", e);

      return null;
    }
    return rd;
  }

  private String getSendMessage(ALEipUserAddr useraddr, List<Object> schelist,
      List<ScheduleToDoResultData> todolist) {
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer();
    body
      .append("--- ")
      .append(viewDate.getYear())
      .append("年")
      .append(viewDate.getMonth())
      .append("月")
      .append(viewDate.getDay())
      .append("日")
      .append(" ---")
      .append(CR)
      .append(CR);
    body.append("[予定]").append(CR);

    if (schelist != null && schelist.size() > 0) {
      CellScheduleResultData rd = null;
      int size = schelist.size();
      for (int i = 0; i < size; i++) {
        rd = (CellScheduleResultData) schelist.get(i);
        if (rd.isSpan()) {
          body.append("・期間：");
        } else {
          body.append("・").append(rd.getDate()).append("  ");
        }
        body.append(rd.getName());

        if (!rd.isPublic()) {
          body.append("(非公開)");
        }

        if (rd.isDuplicate()) {
          body.append("(重複)");
        }

        if (rd.isRepeat()) {
          body.append("(繰り返し)");
        }
        if (rd.isTmpreserve()) {
          body.append("(仮)");
        }

        body.append(CR);
      }
    } else {
      body.append("予定はありません。").append(CR);
    }
    body.append(CR);
    body.append("[ToDo]").append(CR);

    if (todolist != null && todolist.size() > 0) {
      ScheduleToDoResultData todord = null;
      int todosize = todolist.size();
      for (int i = 0; i < todosize; i++) {
        todord = todolist.get(i);
        body.append("・").append(todord.getTodoName()).append(CR);
      }
    } else {
      body.append("ToDoはありません。").append(CR);
    }
    body.append(CR);
    body
      .append("[")
      .append(DatabaseOrmService.getInstance().getAlias())
      .append("へのアクセス]")
      .append(CR);

    String globalurl = getCellularUrl(useraddr);
    if (!(globalurl == null || globalurl.equals(""))) {
      body.append("・社外").append(CR);
      body.append("　").append(globalurl).append(CR);
    }
    if (!(localurl == null || "".equals(localurl))) {
      body.append("・社内").append(CR);
      body.append("　").append(localurl).append(CR).append(CR);
      body.append("---------------------");
    }
    return body.toString();
  }

  private void loadAipoUrls() {
    EipMCompany record = ALEipUtils.getEipMCompany("1");

    try {
      String ipaddress = record.getIpaddressInternal();
      if (null == ipaddress || "".equals(ipaddress)) {
        Enumeration<NetworkInterface> enuIfs =
          NetworkInterface.getNetworkInterfaces();
        if (null != enuIfs) {
          while (enuIfs.hasMoreElements()) {
            NetworkInterface ni = enuIfs.nextElement();
            Enumeration<InetAddress> enuAddrs = ni.getInetAddresses();
            while (enuAddrs.hasMoreElements()) {
              InetAddress in4 = enuAddrs.nextElement();
              if (!in4.isLoopbackAddress()) {
                ipaddress = in4.getHostAddress();
              }
            }
          }
        }
      }
      Integer port_internal = record.getPortInternal();
      if (null == port_internal) {
        port_internal = 80;
      }
      localurl = SystemUtils.getUrl(ipaddress, port_internal, servlet_name);
    } catch (SocketException e) {
      logger.error(e);
    }

    // // Aipoサイト情報の取得
    // localurl = "";
    // try {
    // localurl = SystemUtils.getUrl(
    // InetAddress.getLocalHost().getHostAddress(), port, servlet_name);
    // } catch (UnknownHostException e) {
    // }
  }

  private String getCellularUrl(ALEipUserAddr useraddr) {
    String url = null;

    ALEipUser eipUser;
    try {
      // 最新のユーザ情報を取得する．
      eipUser = ALEipUtils.getALEipUser(useraddr.getUserId().intValue());
    } catch (Exception e) {
      logger.error("Exception", e);
      return "";
    }

    String key =
      eipUser.getName().getValue()
        + "_"
        + ALCellularUtils.getCheckValueForCellLogin(eipUser
          .getName()
          .getValue(), eipUser.getUserId().toString());
    EipMCompany record = ALEipUtils.getEipMCompany("1");
    String domain =
      SystemUtils.getUrl(
        record.getIpaddress(),
        record.getPort().intValue(),
        servlet_name);
    if (domain != null && domain.length() > 0) {
      url = domain + "?key=" + key;
    } else {
      url = "";
    }
    return url;
  }

  private List<ScheduleToDoResultData> getToDoList(Integer userid) {
    List<ScheduleToDoResultData> todoList =
      new ArrayList<ScheduleToDoResultData>();

    SelectQuery<EipTTodo> query = getSelectQueryForTodo(userid);
    List<EipTTodo> todos = query.fetchList();

    int todosize = todos.size();
    for (int i = 0; i < todosize; i++) {
      EipTTodo record = todos.get(i);
      ScheduleToDoResultData rd = new ScheduleToDoResultData();
      rd.initField();
      rd.setTodoName(record.getTodoName());
      todoList.add(rd);
    }

    return todoList;
  }

  private SelectQuery<EipTTodo> getSelectQueryForTodo(Integer userid) {
    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);
    query.select(EipTTodo.TODO_NAME_COLUMN);

    Expression exp1 =
      ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
        .valueOf((short) 100));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTTodo.ADDON_SCHEDULE_FLG_PROPERTY, "T");
    query.andQualifier(exp2);
    Expression exp3 =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, userid);
    query.andQualifier(exp3);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(EipTTodo.END_DATE_PROPERTY, viewDate
        .getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTTodo.START_DATE_PROPERTY, viewDate
        .getValue());

    // 開始日時のみ指定されている ToDo を検索
    Expression exp21 =
      ExpressionFactory.lessOrEqualExp(EipTTodo.START_DATE_PROPERTY, viewDate
        .getValue());
    Expression exp22 =
      ExpressionFactory.matchExp(EipTTodo.END_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    // 終了日時のみ指定されている ToDo を検索
    Expression exp31 =
      ExpressionFactory.greaterOrEqualExp(EipTTodo.END_DATE_PROPERTY, viewDate
        .getValue());
    Expression exp32 =
      ExpressionFactory.matchExp(EipTTodo.START_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)).orExp(
      exp31.andExp(exp32)));
    return query;
  }

  /**
   * Logs a message to the logging service but also sets the result for this
   * daemon.
   */
  @SuppressWarnings("unused")
  private void error(Throwable t, String message) {
    this.setResult(Daemon.RESULT_FAILED);
    logger.error(message, t);
  }

  // /**
  // */
  // private void error(String message) {
  // this.error(null, message);
  // }

  /* *** Daemon interface *** */

  /**
   * Init this Daemon from the DaemonFactory
   */
  public void init(DaemonConfig config, DaemonEntry entry) {

    enableAsp = JetspeedResources.getBoolean("aipo.asp", false);

    if (!enableAsp) {
      EipMCompany record = ALEipUtils.getEipMCompany("1");
      Integer port_internal = record.getPortInternal();
      if (null == port_internal) {
        port_internal = 80;
      }
      port = port_internal;
      loadAipoUrls();
    }

    this.config = config;

    // get configuration parameters from Jetspeed Resources
    AipoDaemonFactoryService aipoDaemonService =
      (AipoDaemonFactoryService) TurbineServices.getInstance().getService(
        DaemonFactoryService.SERVICE_NAME);
    ServletConfig servlet_config = aipoDaemonService.getServletConfig();
    servlet_name = servlet_config.getServletName();

    scheme = TurbineServlet.getServerScheme();

    realpath = servlet_config.getServletContext().getRealPath("/");

    Calendar cal = Calendar.getInstance();
    Calendar cal_now = Calendar.getInstance();

    long now_millis = cal.getTimeInMillis();

    @SuppressWarnings("unused")
    int now_hour = cal.get(Calendar.HOUR_OF_DAY);
    @SuppressWarnings("unused")
    int now_minutes = cal.get(Calendar.MINUTE);

    // プロパティファイルから送信時間(this.send_time_hour)の読み込み
    FileInputStream input = null;
    try {
      String timestr = ALMailUtils.getNotifyTime();
      this.send_time_hour = Integer.valueOf(timestr.substring(0, 2));
      this.send_time_minutes = Integer.valueOf(timestr.substring(3, 5));

      /*
       * String propertiesPath = JetspeedResources.getString("aipo.conf", "");
       * input = new FileInputStream(propertiesPath + File.separator +
       * "WebMailAdminSettings.properties"); Properties prop = new Properties();
       * prop.load(input); this.send_time_hour =
       * Integer.parseInt(prop.getProperty("DayMailTime")); //
       * this.send_time_minutes = Integer.parseInt(prop //
       * .getProperty("DayMailTimeMinutes"));
       */
    } catch (Exception ex) {
      logger.error("Exception", ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException iex) {
          logger.error("Exception", iex);
        }
      }
    }
    cal.set(Calendar.HOUR_OF_DAY, send_time_hour);
    cal.set(Calendar.MINUTE, send_time_minutes);

    if (cal_now.getTimeInMillis() < cal.getTimeInMillis()) {
    } else {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    long next_millis = cal.getTimeInMillis();

    long interval = (next_millis - now_millis) / 1000;

    this.entry =
      new DaemonEntry(entry.getName(), interval, entry.getClassname(), entry
        .onStartup());

    enable_run = false;

  }

  /**
   */
  public DaemonConfig getDaemonConfig() {
    return this.config;
  }

  /**
   */
  public DaemonEntry getDaemonEntry() {
    return this.entry;
  }

  /**
   * Return the status for this Daemon
   */
  public int getStatus() {
    return this.status;
  }

  /**
   * Set the status for this Daemon
   */
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   *
   */
  public int getResult() {
    return this.result;
  }

  /**
   *
   */
  public void setResult(int result) {
    this.result = result;
  }

  /**
   *
   */
  public String getMessage() {
    return null;
  }

}
