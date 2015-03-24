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
package com.aimluck.eip.modules.actions.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.CellAccountEasyLoginFormData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 個人設定用-ユーザ情報用アクションクラス
 * 
 */
public class CellAccountEditAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellAccountEditAction.class.getName());

  public static final String MODE_CONFIG = "config";

  /**
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    // if (getMode() == null) {
    // doAccountedit_configlogin(rundata, context);
    // }
  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      String username = null;
      String key = rundata.getParameters().getString("key", "").trim();
      if (key.contains("_")) {

        username = key.substring(0, key.lastIndexOf("_"));
        String base64value = key.substring(key.lastIndexOf("_") + 1);

        // 入力されたユーザ名を検証する．
        ALStringField tmpname = new ALStringField();
        tmpname.setTrim(true);
        tmpname.setNotNull(true);
        tmpname.setCharacterType(ALStringField.TYPE_ASCII);
        tmpname.limitMaxLength(16);
        tmpname.setValue(username);
        boolean valid = tmpname.validate(new ArrayList<String>());

        int length = username.length();
        for (int i1 = 0; i1 < length; i1++) {
          if (isSymbol(username.charAt(i1))) {
            // 使用されているのが妥当な記号であるかの確認
            if (!(username.charAt(i1) == "_".charAt(0)
              || username.charAt(i1) == "-".charAt(0) || username.charAt(i1) == "."
              .charAt(0))) {
              valid = false;
              break;
            }
          }
        }

        if (valid) {
          ALEipUser eipuser = ALEipUtils.getALEipUser(username);
          if (eipuser != null) {
            if (!(ALCellularUtils.getCheckValueForCellLogin(username, eipuser
              .getUserId()
              .toString())).equals(base64value)) {
              username = "";
            }
          } else {
            username = "";
          }
        } else {
          username = "";
        }
      }
      context.put("username", username);
      context.put("key", key);

      if (MODE_CONFIG.equals(mode)) {
        doAccountedit_configlogin(rundata, context);
      } else if (ALEipConstants.MODE_INSERT.equals(mode)) {
        doAccountedit_insert(rundata, context);
      }

      if (getMode() == null) {
        doAccountedit_configlogin(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("CellAccountEditAction.buildMaximizedContext", ex);
    }
  }

  /**
   * 簡易ログイン設定用のフォームを表示する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccountedit_configlogin(RunData rundata, Context context)
      throws Exception {
    CellAccountEasyLoginFormData formData = new CellAccountEasyLoginFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "accountlogin-config");
  }

  /**
   * 簡易ログイン用に携帯電話の固有番号を登録する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccountedit_insert(RunData rundata, Context context)
      throws Exception {
    CellAccountEasyLoginFormData formData = new CellAccountEasyLoginFormData();
    formData.initField();
    setTemplate(rundata, "accountlogin-config");
    if (formData.doInsert(this, rundata, context)) {
      doAccountedit_configlogin(rundata, context);
    } else {
      setTemplate(rundata, "accountlogin-config");
    }
  }

  /**
   * 簡易ログイン用に携帯電話の固有番号を削除する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccountedit_delete(RunData rundata, Context context)
      throws Exception {
    CellAccountEasyLoginFormData formData = new CellAccountEasyLoginFormData();
    formData.initField();
    setTemplate(rundata, "accountlogin-config");
    if (formData.doDelete(this, rundata, context)) {
      doAccountedit_configlogin(rundata, context);
    } else {
      setTemplate(rundata, "accountlogin-config");
    }
  }

  /**
   * 
   * 指定したchar型文字が記号であるかを判断します。
   * 
   * @param ch
   * @return
   */
  protected boolean isSymbol(char ch) {
    byte[] chars;

    try {
      chars = (Character.valueOf(ch).toString()).getBytes("shift_jis");
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    if (chars == null
      || chars.length == 2
      || Character.isDigit(ch)
      || Character.isLetter(ch)) {
      return false;
    } else {
      return true;
    }

  }
}