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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.AccountPasswdFormData;
import com.aimluck.eip.account.AccountUserFormData;
import com.aimluck.eip.account.AccountUserMultiDelete;
import com.aimluck.eip.account.AccountUserMultiDisable;
import com.aimluck.eip.account.AccountUserMultiEnable;
import com.aimluck.eip.common.ALEipConstants;

/**
 * ユーザーアカウントをJSONデータとして出力するクラスです。 <br />
 * 
 */
public class AccountUserFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {

        AccountUserFormData formData = new AccountUserFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {

        AccountUserFormData formData = new AccountUserFormData();
        formData.initField();
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {

        AccountUserFormData formData = new AccountUserFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if ("disable".equals(mode)) {
        AccountUserFormData formData = new AccountUserFormData();
        formData.initField();
        formData.init(this, rundata, context);
        List<String> msgList = new ArrayList<String>();
        formData.disableFormData(rundata, context, msgList);
        if (!msgList.isEmpty()) {
          JSONArray json = JSONArray.fromObject(msgList);
          result = json.toString();
        }

      } else if ("enable".equals(mode)) {
        AccountUserFormData formData = new AccountUserFormData();
        formData.initField();
        formData.init(this, rundata, context);
        formData.enableFormData(rundata, context, null);

      } else if ("multi_delete".equals(mode)) {

        AccountUserMultiDelete delete = new AccountUserMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("multi_enable".equals(mode)) {

        AccountUserMultiEnable delete = new AccountUserMultiEnable();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if ("multi_disable".equals(mode)) {

        AccountUserMultiDisable delete = new AccountUserMultiDisable();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("updatepasswd".equals(mode)) {
        AccountPasswdFormData formData = new AccountPasswdFormData();
        formData.initField();
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      }
    } catch (RuntimeException e) {
      logger.error("AccountUserFormJSONScreen.getJSONString", e);
    } catch (Exception e) {
      logger.error("AccountUserFormJSONScreen.getJSONString", e);
    }

    return result;
  }
}
