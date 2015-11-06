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
package com.aimluck.eip.system;

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.query.ResultList;

/**
 * 管理者メール通知設定の検索データを管理するためのクラスです。 <br />
 */
public class SystemWebMailAdminSettingsSelectData
    extends
    ALAbstractSelectData<SystemWebMailAdminSettingsResultData, SystemWebMailAdminSettingsResultData> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemWebMailAdminSettingsSelectData.class.getName());

  @Override
  protected ResultList<SystemWebMailAdminSettingsResultData> selectList(
      RunData rundata, Context context) throws ALPageNotFoundException,
      ALDBErrorException {
    return null;
  }

  /**
   * ResultDataを取得する抽象メソッドです。（一覧データ）
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(SystemWebMailAdminSettingsResultData obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * 詳細データを取得する抽象メソッドです。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected SystemWebMailAdminSettingsResultData selectDetail(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    SystemWebMailAdminSettingsResultData rd =
      new SystemWebMailAdminSettingsResultData();
    rd.initField();
    rd
      .setMsgTypeBlog(ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_BLOG));
    rd
      .setMsgTypeNote(ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_NOTE));
    rd.setMsgTypeSchedule(ALMailUtils
      .getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE));
    rd.setMsgTypeWorkflow(ALMailUtils
      .getSendDestType(ALMailUtils.KEY_MSGTYPE_WORKFLOW));
    rd.setMsgTypeMsgboard(ALMailUtils
      .getSendDestType(ALMailUtils.KEY_MSGTYPE_MSGBOARD));
    rd.setMsgTypeMsgboard(ALMailUtils
      .getSendDestType(ALMailUtils.KEY_MSGTYPE_REPORT));

    String timestr = ALMailUtils.getNotifyTime();
    rd.setMsgNotifyTimeHour(timestr.charAt(0) == '0'
      ? timestr.substring(1, 2)
      : timestr.substring(0, 2));
    rd.setMsgNotifyTimeMinute(timestr.charAt(3) == '0' ? timestr
      .substring(4, 5) : timestr.substring(3, 5));
    return rd;
  }

  /**
   * ResultDataを取得する抽象メソッドです。（詳細データ）
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(SystemWebMailAdminSettingsResultData obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return obj;
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
