package com.aimluck.eip.modules.actions;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.util.RunData;

/**
 * PC表示切り替え処理用のクラスです。 <br />
 *
 */
public class ALChangeToPC extends ActionEvent {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALChangeToPC.class.getName());

  private boolean isLogin(JetspeedUser loginuser) {
    return (loginuser != null && loginuser.hasLoggedIn());
  }

  @Override
  public void doPerform(RunData data) throws Exception {
    JetspeedUser loginuser = (JetspeedUser) data.getUser();

    if (isLogin(loginuser)) {
      data.getSession().setAttribute("changeToPc", "true");
      JetspeedLink jsLink = null;

      try {
        jsLink = JetspeedLinkFactory.getInstance(data);
      } catch (Exception e) {
        logger.error("Error getting jsLink", e);
      }
      String mypageId = data.getParameters().getString("mypageId");
      data.setRedirectURI(jsLink
        .getHomePage()
        .addQueryData("js_pane", mypageId)
        .addQueryData("action", "controls.Restore")
        .toString());
      data.getResponse().sendRedirect(data.getRedirectURI());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
    }
  }
}
