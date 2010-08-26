/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
      String servername = jdata.getServletConfig().getServletName();
      if (!uri.equals("/" + servername + "/portal/")
        && !uri.equals("/" + servername + "/portal")) {
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
