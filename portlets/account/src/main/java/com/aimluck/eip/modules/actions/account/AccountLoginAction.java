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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ログイン画面を表示するアクションクラスです。
 * 
 */
public class AccountLoginAction extends ALBaseAction {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountLoginAction.class.getName());

  /**
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    setResultData(this);
    putData(rundata, context);
  }

  public String getOrgId() {
    return Database.getDomainName();
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  private boolean isMacBrowser(RunData rundata, String browserPattern) {
    String agent = rundata.getRequest().getHeader("User-Agent");
    Pattern pattern = Pattern.compile(browserPattern, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(agent);
    boolean result = matcher.matches();
    return result;
  }

  /**
   * マッキントッシュからのアクセスかどうかを判断します。
   * 
   * @param rundata
   * @return
   */
  private boolean isMac(RunData rundata) {
    String agent = rundata.getRequest().getHeader("User-Agent");
    Pattern pattern = Pattern.compile(".*Mac.*", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(agent);
    boolean result = matcher.matches();
    return result;
  }

}
