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

package org.apache.jetspeed.modules.actions.controls;

// Turbine stuff
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 * This action must be invoked to clean up the customization state and redirect
 * the user to his portal hompage
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 */
public class EndCustomize extends Action {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(EndCustomize.class.getName());

  /**
   * @param rundata
   *            The RunData object for the current request
   */
  public void doPerform(RunData data) throws Exception {
    ((JetspeedRunData) data).cleanupFromCustomization();

    // bring logged on user to homepage via HTTP redirect
    try {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(data);
      DynamicURI duri = jsLink.getLink(JetspeedLink.CURRENT, null, null,
          JetspeedLink.CURRENT, null);
      String mtype = data.getParameters().getString("mtype");
      if (mtype != null) {
        jsLink.setMediaType(mtype);
        duri = jsLink.addQueryData("mtype", mtype);
        duri = duri.addPathInfo("media-type", mtype);
        String language = data.getParameters().getString("language");
        if (language != null) {
          duri = duri.addQueryData("language", language);
          String country = data.getParameters().getString("country");
          if (country != null) {
            duri = duri.addQueryData("country", country);
          }
        }
      }

      // 理由等 ：デフォルトに戻す際に、ページ遷移を行わない
      // 対処方法：リダイレクトにてカスタマイズページを指定
      String eventDefault = data.getParameters().getString(
          "eventSubmit_doDefault");
      if (eventDefault != null && eventDefault.trim().length() != 0) {
        String peid = data.getParameters().getString("js_peid");
        if (peid != null) {
          duri = duri.addQueryData("js_peid", peid);
          duri = duri.addQueryData("action", "controls.Customize");
        }
      }

      data.setRedirectURI(duri.toString());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
    } catch (Exception e) {
      logger.error("Error while trying to bring user back to home page", e);
    }
  }
}
