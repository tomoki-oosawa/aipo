package com.aimluck.eip.modules.actions;

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
public class ALChangeToSmartPhone extends ActionEvent {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALChangeToSmartPhone.class.getName());

  @Override
  public void doPerform(RunData data) throws Exception {

    data.getSession().removeAttribute("changeToPc");
    JetspeedLink jsLink = null;

    try {
      jsLink = JetspeedLinkFactory.getInstance(data);
    } catch (Exception e) {
      logger.error("Error getting jsLink", e);
    }
    data.setRedirectURI(jsLink.getHomePage().addQueryData(
      "action",
      "controls.Restore").toString());
    data.getResponse().sendRedirect(data.getRedirectURI());
    JetspeedLinkFactory.putInstance(jsLink);
    jsLink = null;

  }
}
