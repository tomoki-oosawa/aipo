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

package com.aimluck.eip.fileio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileio.util.FileIOScheduleCsvUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 『スケジュール』のフォームデータを管理するクラスです。 <BR>
 * 
 * 
 */
public class FileIOScheduleCsvFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(FileIOScheduleCsvFormData.class
      .getName());

  /** ブラウザに表示するデフォルトのパスワード（ダミーパスワード） */
  public static final String DEFAULT_VIEW_PASSWORD = "*";

  /** ログイン名 */
  private ALStringField username;

  /** ユーザー名 */
  private ALStringField userfirstname;

  private ALStringField userlastname;

  private ALStringField userfullname;

  /** スケジュール名 */
  private ALStringField schedulename;

  /** 場所 */
  private ALStringField place;

  /** 備考 */
  private ALStringField note;

  /** 入力日時 */
  private ALDateTimeField create_date;

  /** 開始日 */
  private ALDateTimeField start_date;

  /** 終了日 */
  private ALDateTimeField end_date;

  /** 開始時刻 */
  private ALDateTimeField start_time;

  /** 終了時刻 */
  private ALDateTimeField end_time;

  /** 開始日時 */
  private ALDateTimeField start_date_time;

  /** 終了日時 */
  private ALDateTimeField end_date_time;

  private boolean is_auto_time;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  public void initField() {
    // ログイン名
    username = new ALStringField();
    username.setFieldName("ログイン名");
    username.setTrim(true);
    // ログインID
    userfullname = new ALStringField();
    userfullname.setFieldName("ログインID");
    userfullname.setTrim(true);
    // ユーザー名
    userfirstname = new ALStringField();
    userfirstname.setFieldName("名前");
    userfirstname.setTrim(true);
    userlastname = new ALStringField();
    userlastname.setFieldName("名字");
    userlastname.setTrim(true);

    // スケジュール
    schedulename = new ALStringField();
    schedulename.setFieldName("スケジュール");
    schedulename.setTrim(true);
    schedulename.setValue("");

    // 場所
    place = new ALStringField();
    place.setFieldName("場所");
    place.setTrim(true);
    place.setValue("");

    // 詳細
    note = new ALStringField();
    note.setFieldName("内容");
    note.setTrim(true);
    note.setValue("");

    // 入力日時
    create_date = new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);
    create_date.setFieldName("入力日時");
    create_date.setValue("");

    // 開始日
    start_date = new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_FORMAT);
    start_date.setFieldName("開始日付");

    // 終了日
    end_date = new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_FORMAT);
    end_date.setFieldName("終了日付");

    // 開始時刻
    start_time =
      new ALDateTimeField(FileIOScheduleCsvUtils.DEFAULT_TIME_FORMAT);
    start_time.setFieldName("開始時刻");

    // 終了時刻
    end_time = new ALDateTimeField(FileIOScheduleCsvUtils.DEFAULT_TIME_FORMAT);
    end_time.setFieldName("終了時刻");

    // 開始日時
    start_date_time =
      new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);
    start_date_time.setFieldName("開始日時");

    // 終了日時
    end_date_time =
      new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);
    end_date_time.setFieldName("終了日時");

    start_date_time.setValue("");
    end_date_time.setValue("");
    start_date.setValue("");
    end_date.setValue("");
    start_time.setValue("");
    end_time.setValue("");
    // 時間を自動補完するか
    is_auto_time = false;
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // ユーザーID
    username.limitMaxLength(16);
    // ユーザー名
    userfullname.limitMaxLength(40);
    // ユーザー氏名
    userfirstname.limitMaxLength(20);
    userlastname.limitMaxLength(20);
    // 予定
    schedulename.setNotNull(true);
    schedulename.limitMaxLength(50);
    // 場所
    place.limitMaxLength(50);
    // 備考
    note.limitMaxLength(1000);

  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    String usernamestr = username.getValue();
    if ("admin".equals(usernamestr)
      || "template".equals(usernamestr)
      || "anon".equals(usernamestr)
      || !username.validate(msgList)) {
      username.setValue(null);
    }
    if (!userfullname.validate(msgList)) {
      userfullname.setValue(null);
    }
    if (!userfirstname.validate(msgList)) {
      userfirstname.setValue(null);
    }
    if (!userlastname.validate(msgList)) {
      userlastname.setValue(null);
    }
    if (!schedulename.validate(msgList)) {
      schedulename.setValue(null);
    }
    if (!place.validate(msgList)) {
      place.setValue(null);
    }
    if (!note.validate(msgList)) {
      note.setValue(null);
    }

    if (!create_date.validate(msgList)) {
      Date now = new Date();
      create_date.setValue(now);
    }

    if (!start_date.validate(msgList)) {
      start_date.setValue("");
      msgList.add("『 <span class='em'>開始日付</span> 』が正しく入力されていません。");
    }
    if (!end_date.validate(msgList)) {
      end_date.setValue("");
      msgList.add("『 <span class='em'>終了日付</span> 』が正しく入力されていません。");
    }

    if (!start_date.toString().equals("") && !end_date.toString().equals("")) {
      if (is_auto_time) {
        if ((start_time.toString().equals(""))
          && (end_time.toString().equals(""))) {
          this.setStartTime("00:00");
          this.setEndTime("00:00");
        }
      } else if ((start_time.toString().equals(""))
        && (end_time.toString().equals(""))) {
        msgList.add("『 <span class='em'>開始,終了時刻</span> 』が正しく入力されていません。");
      }
      if (compareToDate(start_date.getValue(), end_date.getValue()) != 0) {
        msgList.add("日付を跨いでの入力は出来ません。");
      }
    }

    if (!start_time.validate(msgList)) {
      start_time.setValue("");
      msgList.add("『 <span class='em'>開始時刻</span> 』が正しく入力されていません。");
    }
    if (!end_time.validate(msgList)) {
      end_time.setValue("");
      msgList.add("『 <span class='em'>終了時刻</span> 』が正しく入力されていません。");
    }

    try {
      this.getStartDateTime();
      this.getEndDateTime();
    } catch (Exception e) {

    }

    if (!start_date_time.validate(msgList)) {
      start_date_time.setValue("");
      msgList.add("『 <span class='em'>開始日時</span> 』が正しく入力されていません。");
    }
    if (!end_date_time.validate(msgList)) {
      end_date_time.setValue("");
      msgList.add("『 <span class='em'>終了日時</span> 』が正しく入力されていません。");
    }

    if (!end_date_time.toString().equals("")
      && !start_date_time.toString().equals("")) {
      if (end_date_time.getValue().before(start_date_time.getValue())) {
        msgList
          .add("『 <span class='em'>終了日時</span> 』は『 <span class='em'>開始日時</span> 』以降の日付を指定してください。");
        start_date.setValue("");
        end_date.setValue("");
        start_time.setValue("");
        end_time.setValue("");
      }
    }

    return (msgList.size() == 0);
  }

  /**
   * 『ユーザー』を読み込みます。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『ユーザー』を追加します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      List<ALEipUser> memberList = new ArrayList<ALEipUser>();
      memberList.add(ALEipUtils.getALEipUser(username.getValue()));
      Calendar startcal = new GregorianCalendar();
      startcal.setTime(start_date_time.getValue());
      Calendar endcal = Calendar.getInstance();
      endcal.setTime(end_date_time.getValue());
      endcal.set(Calendar.YEAR, startcal.get(Calendar.YEAR));
      endcal.set(Calendar.MONTH, startcal.get(Calendar.MONTH));
      endcal.set(Calendar.DATE, startcal.get(Calendar.DATE));
      end_date_time.setValue(endcal.getTime());

      // 新規オブジェクトモデル
      EipTSchedule schedule = Database.create(EipTSchedule.class);
      // 親スケジュール ID
      schedule.setParentId(Integer.valueOf(1));
      // 予定
      schedule.setName(schedulename.getValue());
      // 場所
      schedule.setPlace(place.getValue());
      // 内容
      schedule.setNote(note.getValue());
      // 公開フラグ
      // schedule.setPublicFlag(public_flag.getValue());
      schedule.setPublicFlag("O");
      // 共有メンバーによる編集／削除フラグ
      schedule.setEditFlag("O");
      // オーナーID
      // schedule.setOwnerId(new Integer(ownerid));
      // 作成日
      Date now = new Date();
      schedule.setCreateDate(now);
      // schedule.setCreateUserId(new Integer(ownerid));
      // 更新日
      schedule.setUpdateDate(now);
      // schedule.setUpdateUserId(new Integer(ownerid));

      schedule.setStartDate(start_date_time.getValue());

      // 終了日時
      schedule.setEndDate(end_date_time.getValue());
      schedule.setRepeatPattern("N");
      schedule.setStartDate(start_date_time.getValue());

      EipTCommonCategory category =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

      // // スケジュールを登録
      // orm.doInsert(schedule);
      int size = memberList.size();
      for (int i = 0; i < size; i++) {
        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
        ALEipUser user = memberList.get(i);
        int userid = (int) user.getUserId().getValue();

        schedule.setOwnerId(Integer.valueOf(userid));
        schedule.setCreateUserId(Integer.valueOf(userid));
        schedule.setUpdateUserId(Integer.valueOf(userid));

        // map.setPrimaryKey(schedule.getScheduleId(), userid);
        map.setEipTSchedule(schedule);
        map.setUserId(Integer.valueOf(userid));

        map.setCommonCategoryId(Integer.valueOf(1));
        map.setEipTCommonCategory(category);

        // // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
        // if (userid == ALEipUtils.getUserId(rundata)) {
        // map.setStatus("O");
        // } else {
        map.setStatus("O");
        // }
        map.setType(FileIOScheduleCsvUtils.SCHEDULEMAP_TYPE_USER);
      }
      // スケジュールを登録
      Database.commit();
      // logger.error("f1");
    } catch (Exception e) {
      Database.rollback();
      logger.error("[FileIOScheduleCsvFormData]", e);
      // throw new ALDBErrorException();
      return false;
    }

    return true;
  }

  /**
   * 『スケジュール』を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『スケジュール』を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * ユーザーの氏名からユーザーIDを取得する。 <BR>
   * 
   * @param userFullName
   * @return
   */
  private TurbineUser getTurbineUser() {

    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
    Expression exp1 =
      ExpressionFactory
        .matchExp(TurbineUser.FIRST_NAME_PROPERTY, userfirstname);
    Expression exp2 =
      ExpressionFactory.matchExp(TurbineUser.LAST_NAME_PROPERTY, userlastname);
    Expression exp3 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");

    query.setQualifier(exp1.andExp(exp2.andExp(exp3)));

    TurbineUser user = query.fetchSingle();

    if (user == null) {
      // 指定したUser IDのレコードが見つからない場合
      logger.debug("[FileIOScheduleCsvFormData] Not found ID...");
      return null;
    }
    return user;
  }

  /**
   * オブジェクトモデルからログイン名を取得する。 <BR>
   * 
   * @param userFullName
   * @return
   */
  public ALStringField getUserName() {

    if ("".equals(username.toString())) {
      try {
        TurbineUser tuser = this.getTurbineUser();
        this.setUserName(tuser.getLoginName());
      } catch (Exception e) {
        setUserName("");
      }
    } else {
      try {
        ALEipUtils.getALEipUser(username.getValue());
      } catch (Exception e) {
        setUserName("");
      }
    }
    return username;
  }

  /**
   * ユーザー名(フルネーム)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getUserFullName() {
    return userfullname;
  }

  /**
   * ユーザー名(名)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getUserFirstName() {
    return userfirstname;
  }

  /**
   * ユーザー名(氏)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getUserLastName() {
    return userlastname;
  }

  /**
   * 予定を取得します <BR>
   * 
   * @return
   */
  public ALStringField getScheduleName() {
    return schedulename;
  }

  /**
   * 備考を取得します <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * 場所を取得します <BR>
   * 
   * @return
   */
  public ALStringField getPlace() {
    return place;
  }

  /**
   * 作成日時を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
  }

  /**
   * 開始日を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getStartDate() {
    return start_date;
  }

  /**
   * 終了日を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getEndDate() {
    return end_date;
  }

  /**
   * 開始時刻を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getStartTime() {
    return start_time;
  }

  /**
   * 終了時刻を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getEndTime() {
    return end_time;
  }

  /**
   * 開始日時を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getStartDateTime() {
    Calendar cal = Calendar.getInstance();
    Date date;
    if ("".equals(start_date_time.toString())) {
      cal.set(Calendar.YEAR, Integer.parseInt(start_date.getYear()));
      cal.set(Calendar.MONTH, Integer.parseInt(start_date.getMonth()) - 1);// 月が０からカウントされる為
      cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start_date.getDay()));
      cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.getHour()));
      cal.set(Calendar.MINUTE, Integer.parseInt(start_time.getMinute()));
      date = cal.getTime();
      start_date_time.setValue(date);
    }
    return start_date_time;
  }

  /**
   * 終了日時を取得します <BR>
   * 
   * @return
   */
  public ALDateTimeField getEndDateTime() {
    Calendar cal = Calendar.getInstance();
    Date date;
    if ("".equals(end_date_time.toString())) {
      cal.set(Calendar.YEAR, Integer.parseInt(end_date.getYear()));
      cal.set(Calendar.MONTH, Integer.parseInt(end_date.getMonth()) - 1);// 月が０からカウントされる為
      cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(end_date.getDay()));
      cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end_time.getHour()));
      cal.set(Calendar.MINUTE, Integer.parseInt(end_time.getMinute()));
      date = cal.getTime();
      end_date_time.setValue(date);
    }
    return end_date_time;
  }

  /**
   * ログイン名を入力します <BR>
   * 
   * @param str
   */
  public void setUserName(String str) {
    username.setValue(str);
  }

  /**
   * ユーザー名(フルネーム)を入力します <BR>
   * 
   * @param str
   */
  public void setUserFullName(String str) {
    userfullname.setValue(str);
  }

  /**
   * ユーザー名(名)を入力します <BR>
   * 
   * @param str
   */
  public void setUserFirstName(String str) {
    userfirstname.setValue(str);
  }

  /**
   * ユーザー名(氏)を入力します <BR>
   * 
   * @param str
   */
  public void setUserLastName(String str) {
    userlastname.setValue(str);
  }

  /**
   * 予定を入力します <BR>
   * 
   * @param str
   */
  public void setScheduleName(String str) {
    schedulename.setValue(str);
  }

  /**
   * 備考を入力します <BR>
   * 
   * @param str
   */
  public void setNote(String str) {
    note.setValue(str);
  }

  /**
   * 場所を入力します <BR>
   * 
   * @param str
   */
  public void setPlace(String str) {
    place.setValue(str);
  }

  /**
   * 入力日時を入力します <BR>
   * 
   * @param str
   */
  public void setCreateDate(String str) {
    create_date.setValue(str);
  }

  /**
   * 開始日を入力します <BR>
   * 
   * @param str
   */
  public void setStartDate(String str) {
    start_date.setValue(str);
  }

  /**
   * 終了日を入力します <BR>
   * 
   * @param str
   */
  public void setEndDate(String str) {
    end_date.setValue(str);
  }

  /**
   * 開始時刻を入力します <BR>
   * 
   * @param str
   */
  public void setStartTime(String str) {
    start_time.setValue(str);
  }

  /**
   * 終了時刻を入力します <BR>
   * 
   * @param str
   */
  public void setEndTime(String str) {
    end_time.setValue(str);
  }

  /**
   * 開始日時を入力します <BR>
   * 
   * @param str
   */
  public void setStartDateTime(String str) {
    start_date_time.setValue(str);
  }

  /**
   * 終了日時を入力します <BR>
   * 
   * @param str
   */
  public void setEndDateTime(String str) {
    end_date_time.setValue(str);
  }

  /**
   * 入力日時を入力します
   * 
   * @param date
   */
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  /**
   * 開始日を入力します <BR>
   * 
   * @param date
   */
  public void setStartDate(Date date) {
    start_date.setValue(date);
  }

  /**
   * 終了日を入力します <BR>
   * 
   * @param date
   */
  public void setEndDate(Date date) {
    end_date.setValue(date);
  }

  /**
   * 開始時刻を入力します <BR>
   * 
   * @param date
   */
  public void setStartTime(Date date) {
    start_time.setValue(date);
  }

  /**
   * 終了時刻を入力します <BR>
   * 
   * @param date
   */
  public void setEndTime(Date date) {
    end_time.setValue(date);
  }

  /**
   * 開始日時を入力します <BR>
   * 
   * @param date
   */
  public void setStartDate_Time(Date date) {
    start_date_time.setValue(date);
  }

  /**
   * 終了日時を入力します <BR>
   * 
   * @param date
   */
  public void setEndDateTime(Date date) {
    end_date_time.setValue(date);
  }

  /**
   * 時間を自動補完する場合は"1"を入力します <BR>
   * 
   * @param flag
   */
  public void setIsAutoTime(String flag) {
    if (Integer.parseInt(flag) == 1) {
      is_auto_time = true;
    } else {
      is_auto_time = false;
    }
  }

  /**
   * 二つの日付を比較します。 <BR>
   * 
   * @param date1
   * @param date2
   * @return
   */
  private int compareToDate(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);

    int date1Year = cal1.get(Calendar.YEAR);
    int date1Month = cal1.get(Calendar.MONTH) + 1;
    int date1Day = cal1.get(Calendar.DATE);
    int date2Year = cal2.get(Calendar.YEAR);
    int date2Month = cal2.get(Calendar.MONTH) + 1;
    int date2Day = cal2.get(Calendar.DATE);

    if (date1Year == date2Year
      && date1Month == date2Month
      && date1Day == date2Day) {
      return 0;
    }
    if (cal1.after(cal2)) {
      return 2;
    } else {
      return 1;
    }
  }

  /**
   * 読み取った単語を指定されたフィールドに格納します。 <BR>
   * 
   * @param token
   * @param i
   */
  public void addItemToken(String token, int i) {
    StringTokenizer st;
    switch (i) {
      case -1:
        break;
      case 0:
        st = new StringTokenizer(token);
        String Fullname = "";
        if (st.hasMoreTokens()) {
          this.setUserLastName(st.nextToken());
          Fullname += this.getUserLastName().toString();
        }
        if (st.hasMoreTokens()) {
          this.setUserFirstName(st.nextToken());
          Fullname += this.getUserFirstName().toString();
        }
        this.setUserFullName(Fullname);
        break;
      case 1:
        this.setScheduleName(token);
        break;
      case 2:
        this.setPlace(token);
        break;
      case 3:
        this.setNote(token);
        break;
      case 4:
        this.setStartDate(token);
        break;
      case 5:
        this.setEndDate(token);
        break;
      case 6:
        this.setStartTime(token);
        break;
      case 7:
        this.setEndTime(token);
        break;
      case 8:
        this.setUserName(token);
        break;
      case 9:
        this.setStartDateTime(token);
        break;
      case 10:
        this.setEndDateTime(token);
        break;
      default:
        break;
    }

  }

}
