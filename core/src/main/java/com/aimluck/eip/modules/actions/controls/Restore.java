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
package com.aimluck.eip.modules.actions.controls;

// Turbine stuff
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletState;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.util.ALSessionInitializer;

/**
 * Change the internal state of a portlet from minimized to normal
 * 
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco </a>
 * @author <a href="mailto:paulsp@apache">Paul Spencer </a>
 */
public class Restore extends Action {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(Restore.class.getName());

  /**
   * @param rundata
   *          The RunData object for the current request
   */
  @Override
  public void doPerform(RunData rundata) throws Exception {
    // Only logged in users can Restored
    if (rundata.getUser() == null) {
      return;
    }

    // Get jsp_peid parmameter. If it does not exist, then do nothing
    String peid = rundata.getParameters().getString("js_peid");
    if (peid == null) {
      JetspeedRunData jdata = (JetspeedRunData) rundata;
      peid = (String) jdata.getUser().getTemp("js_peid");
      if (peid == null) {

        return;
      }
    }

    // Get the Portlet using the PSML document and the PEID
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    Entry entry = jdata.getProfile().getDocument().getEntryById(peid);
    if (entry == null) {
      logger.warn("Failed to get PEID ("
        + peid
        + ") entry for User ("
        + rundata.getUser().getName()
        + ")");

      // error redirect
      jdata.getUser().removeTemp("js_peid");
      rundata.setRedirectURI("/aipo/portal");

      return;
    }
    Portlet portlet = PortletFactory.getPortlet(entry);

    // Now unset the portlet to minimized
    if ((portlet != null) && (portlet instanceof PortletState)) {
      ((PortletState) portlet).setMinimized(false, rundata);
    }

    // make sure we use the default template
    while (jdata.getCustomized() != null) {
      jdata.setCustomized(null);
    }

    // remove the maximized portlet name - nothing is maximized now
    jdata.getUser().removeTemp("js_peid");

    // 理由等 ：セッションが切れた時に、エラーメッセージの表示に不具合あり
    // 対処方法：ログイン画面以外でユーザがログインしていない場合はエラーページへスクリーンを変更
    JetspeedUser user = (JetspeedUser) jdata.getUser();
    if ((user == null || !user.hasLoggedIn())
      && !JetspeedResources.getBoolean("automatic.logon.enable", false)) {
      String uri = (jdata).getRequest().getRequestURI().trim();
      String contextPath = ServletContextLocator.get().getContextPath();
      if ("/".equals(contextPath)) {
        contextPath = "";
      }
      String portalPath = contextPath + "/portal";
      if (!uri.equals(portalPath + "/") && !uri.equals(portalPath)) {
        jdata.setScreenTemplate("Timeout");
        // セッションの削除
        if (jdata.getSession() != null) {
          try {
            jdata.getSession().invalidate();
          } catch (IllegalStateException ex) {
            logger.debug("セッションは既に削除されています。");
          }
        }
      }
    } else {
      jdata.setScreenTemplate("Home");
    }

    // 日付 : 2004/09/29
    // 理由等 : ポートレット画面での各APのセッション初期化処理を追加
    ALSessionInitializer sinit = new ALSessionInitializer();
    sinit.initializeSession(jdata, peid);
  }
}
