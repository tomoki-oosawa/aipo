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
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ログイン画面を表示するアクションクラスです。
 * 
 */
public class CellAccountLoginAction extends ALBaseAction {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellAccountLoginAction.class.getName());

  /**
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    String action_logout =
      rundata.getParameters().getString("logout", "").trim();
    if ("T".equals(action_logout)) {
      setTemplate(rundata, "accountlogout-info");
    }

    Map<String, String> attribute = ALOrgUtilsService.getParameters();
    for (Map.Entry<String, String> e : attribute.entrySet()) {
      context.put(e.getKey(), e.getValue());
    }

    if (Boolean.parseBoolean((String) rundata.getSession().getAttribute(
      "changeToPc"))) { // PC表示切り替え用
      context.put("client", ALEipUtils.getClient(rundata));
    }

    doAccount_login(rundata, context);
    setResultData(this);
    putData(rundata, context);
  }

  public void doAccount_login(RunData rundata, Context context)
      throws Exception {
    boolean enableEasyLogin = false;
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
        // ALEipUser eipuser = ALEipUtils.getALEipUser(username);

        DataContext dataContext = DataContext.getThreadDataContext();
        Expression exp =
          ExpressionFactory.matchExp(TurbineUser.LOGIN_NAME_PROPERTY, username);
        SelectQuery<TurbineUser> query =
          Database.query(dataContext, TurbineUser.class, exp);
        List<TurbineUser> users = query.fetchList();
        if (users.size() != 0) {
          TurbineUser tuser = users.get(0);

          if (!(ALCellularUtils.getCheckValueForCellLogin(username, tuser
            .getUserId()
            .toString())).equals(base64value)) {
            username = "";
          }

          String uid = tuser.getCellularUid();
          enableEasyLogin = (uid != null && uid.length() > 0);
        } else {
          username = "";
        }
      } else {
        username = "";
      }
    }

    context.put("username", username);
    context.put("key", key);
    context.put("enableEasyLogin", Boolean.valueOf(enableEasyLogin));
  }

  /**
   * 簡易ログイン説明ページを表示する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_info(RunData rundata, Context context) throws Exception {
    doAccount_login(rundata, context);
    setTemplate(rundata, "accountlogin-info");
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
