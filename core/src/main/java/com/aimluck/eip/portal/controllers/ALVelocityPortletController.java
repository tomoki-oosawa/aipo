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
package com.aimluck.eip.portal.controllers;

// Turbine stuff
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;
import org.apache.jetspeed.portal.controllers.AbstractPortletController;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * A Velocity based portlet controller implementation
 *
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 *
 * @version $Id: VelocityPortletController.java,v 1.12 2004/02/23 03:25:06 jford
 *          Exp $
 */
public class ALVelocityPortletController extends AbstractPortletController {

  private static final long serialVersionUID = 6546798486414045048L;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALVelocityPortletController.class.getName());

  @Override
  public ConcreteElement getContent(RunData rundata) {
    // create a blank context and with all the global application
    // Pull Tools inside
    Context context = TurbineVelocity.getContext();
    context.put("data", rundata);
    context.put("controller", this);
    context.put("portlets", this.getPortlets().toArray());
    context.put("config", this.getConfig());
    context.put("skin", this.getPortlets().getPortletConfig().getPortletSkin());
    context.put("template", getConfig().getInitParameter("template"));

    context.put("js_peid", rundata.getParameters().get("js_peid"));

    // アクセス権限がなかった場合の削除表示フラグ
    boolean hasAuthority =
      ALEipUtils.getHasAuthority(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_DELETE);
    String showDelete = "false";
    if (hasAuthority) {
      showDelete = "true";
    }
    context.put("accessControl", showDelete);

    ALOrgUtilsService.assignCommonContext(context);

    if (Boolean.parseBoolean((String) rundata.getSession().getAttribute(
      "changeToPc"))) { // PC表示切り替え用
      context.put("client", ALEipUtils.getClient(rundata));
    }

    // Put the request and session based contexts
    TurbinePull.populateContext(context, rundata);

    // allow subclass to insert specific objects in the context
    buildContext(rundata, context);

    String actionName = getConfig().getInitParameter("action");

    if (actionName != null) {
      // store the context so that the action can retrieve it
      rundata.getTemplateInfo().setTemplateContext(
        "VelocityControllerContext",
        context);

      // if there is an action with the same name in modules/actions/portlets
      // exec it
      try {
        ActionLoader.getInstance().exec(rundata, actionName);
      } catch (Exception e) {
        logger.error("ALVelocityPortletController.getContent", e);
      }
    }

    // either the action selected the template, or use the default template
    // defined in the registry
    String template = (String) context.get("template");

    // generate the content
    String s = "";

    try {
      if (-1 == template.indexOf(".vm")) {
        template = template + ".vm";
      }

      String templatePath =
        TemplateLocator.locateControllerTemplate(rundata, template);
      TurbineVelocity.handleRequest(context, templatePath, rundata.getOut());
    } catch (Exception e) {
      logger.error("Error generating content: ", e);
      s = e.toString();
    }

    TurbineVelocity.requestFinished(context);

    return new StringElement(s);
  }

  public void buildContext(RunData data, Context context) {
    // nothing special
  }
}
