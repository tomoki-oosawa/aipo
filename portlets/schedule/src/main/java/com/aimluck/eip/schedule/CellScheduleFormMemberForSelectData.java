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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 * 
 */
public class CellScheduleFormMemberForSelectData extends ALAbstractFormData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleFormMemberForSelectData.class.getName());

  /** <code>login_user</code> ログインユーザー */
  private ALEipUser login_user;

  /** <code>group</code> 選択グループ */
  private ALCellStringField selectedgroup;

  /** 表示行数 */
  private int rows_num = 10;

  /** 開始位置 */
  private int start;

  /** 総件数 */
  private int count;

  /** 総ページ数 */
  private int pages_num = 1;

  /** 現在のページ */
  private int current_page = 1;

  /** ユーザー名リスト */
  private List<ALEipUser> users;

  /**
   * フォームを表示します。
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  public boolean doViewForm(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      action.setMode(ALEipConstants.MODE_NEW_FORM);
      setMode(action.getMode());
      List<String> msgList = new ArrayList<String>();
      boolean res = setFormData(rundata, context, msgList);
      action.setResultData(this);
      action.addErrorMessages(msgList);
      action.putData(rundata, context);
      return res;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }
  }

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

    login_user = ALEipUtils.getALEipUser(rundata);

    if (ALEipUtils.isMatch(rundata, context)) {
      // ページャ用
      if (rundata.getParameters().containsKey(ALEipConstants.LIST_START)) {
        current_page =
          rundata.getParameters().getInt(ALEipConstants.LIST_START);
      }
    }

    String groupname = rundata.getParameters().getString("selectedgroup");
    if (groupname != null && !"".equals(groupname)) {
      // リクエストパラメータでグループ名が指定された場合
      users =
        getUsersByGroupName(rundata.getParameters().getString("selectedgroup"));

      // 対象グループをセッションに設定
      ALEipUtils.setTemp(rundata, context, "selectedgroup", groupname);
    } else {
      groupname = ALEipUtils.getTemp(rundata, context, "selectedgroup");
    }
    users = getUsersByGroupName(groupname);

    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);
  }

  /**
   * パラメータを読み込みます。
   * 
   * @param rundata
   * @param context
   */
  public void loadParameters(RunData rundata, Context context) {
  }

  /*
   *
   */
  @Override
  public void initField() {
    selectedgroup = new ALCellStringField();
    selectedgroup.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_GROUP"));
  }

  /*
   *
   */
  @Override
  protected void setValidator() {
  }

  /**
   * 
   * @param msgList
   * @return
   * @throws ALDBErrorException
   * @throws ALPageNotFoundException
   */
  @Override
  protected boolean validate(List<String> msgList) throws ALDBErrorException,
      ALPageNotFoundException {
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    Field[] fields = this.getClass().getDeclaredFields();
    boolean res =
      ScheduleUtils
        .setFormDataDelegate(rundata, context, this, fields, msgList);

    if (!res) {
      return res;
    }

    return true;
  }

  /**
   * 指定したグループ名のユーザーを取得します。
   * 
   * @param groupname
   * @return
   */
  private List<ALEipUser> getUsersByGroupName(String groupname) {
    if (groupname == null || groupname.length() == 0) {
      return new ArrayList<ALEipUser>();
    }

    if ("all".equals(groupname)) {
      groupname = "LoginUser";
    }

    // return ALEipUtils.getUsers(groupname);
    List<ALEipUser> userlist = ALEipUtils.getUsers(groupname);

    // リストからログインユーザを削除する
    ScheduleUtils.removeUser(userlist, login_user);

    int size = userlist.size();
    setPageParam(size);
    int start = getStart();
    int rowsNum = this.getRowsNum();

    int count = -1;
    List<ALEipUser> list = new ArrayList<ALEipUser>();
    for (int i = 0; i < rowsNum; i++) {
      count = i + start;
      if (count >= size) {
        break;
      }
      list.add(userlist.get(count));
    }

    return list;
  }

  /**
   * 部署マップを取得します。
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * ログインユーザを取得します。
   * 
   * @return
   */
  public ALEipUser getLoginUser() {
    return login_user;
  }

  public ALCellStringField getSelectedGroup() {
    return selectedgroup;
  }

  /**
   * 
   * @param cnt
   */
  protected void setPageParam(int cnt) {
    // 開始
    start = rows_num * (current_page - 1);
    // 総件数
    count = cnt;
    // 総ページ数
    pages_num = ((int) (Math.ceil(count / (double) rows_num)));
  }

  /**
   * 表示する項目数を設定します。
   * 
   * @param num
   */
  public void setRowsNum(int num) {
    if (num >= 1) {
      rows_num = num;
    }
  }

  /**
   * 表示する項目数を取得します。
   * 
   * @return
   */
  public int getRowsNum() {
    return rows_num;
  }

  /**
   * 総件数を取得します。
   * 
   * @return
   */
  public int getCount() {
    return count;
  }

  /**
   * 総ページ数を取得します。
   * 
   * @return
   */
  public int getPagesNum() {
    return pages_num;
  }

  /**
   * 現在表示されているページを取得します。
   * 
   * @return
   */
  public int getCurrentPage() {
    return current_page;
  }

  /**
   * @return
   */
  public int getStart() {
    return start;
  }

  public List<ALEipUser> getUsers() {
    return users;
  }
}
