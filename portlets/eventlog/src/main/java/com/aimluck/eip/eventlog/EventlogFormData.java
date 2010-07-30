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
package com.aimluck.eip.eventlog;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTEventlog;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.eventlog.util.EventlogUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;

/**
 * イベントログのフォームデータを管理するクラスです。 <BR>
 *
 */
public class EventlogFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(EventlogFormData.class.getName());

  private DataContext dataContext;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractFormData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    dataContext = DatabaseOrmService.getInstance().getDataContext();
  }

  /**
   * 各フィールドを初期化します。 <BR>
   *
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
  }

  /**
   * イベントログの各フィールドに対する制約条件を設定します。 <BR>
   *
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  protected void setValidator() {
  }

  /**
   * イベントログのフォームに入力されたデータの妥当性検証を行います。 <BR>
   *
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  protected boolean validate(List<String> msgList) {
    return false;
  }

  /**
   * イベントログをデータベースから読み出します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * イベントログをデータベースから削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTEventlog eventlog = EventlogUtils.getEipTEventlog(rundata, context);
      if (eventlog == null)
        return false;

      // イベントログを削除
      dataContext.deleteObject(eventlog);
      dataContext.commitChanges();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * イベントログをデータベースに格納します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * データベースに格納されているイベントログを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

}
