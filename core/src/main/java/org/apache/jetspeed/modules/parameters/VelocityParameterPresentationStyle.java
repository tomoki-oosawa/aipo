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

package org.apache.jetspeed.modules.parameters;

// Turbine support
import java.util.Map;

import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.BaseJetspeedLink;
import org.apache.turbine.services.localization.LocalizationTool;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALCommonUtils;

/**
 * Generic Velocity-based presentation style. The following default objects are
 * put in the context:
 * <UL>
 * <LI>data - rundata object</LI>
 * <LI>name - name of the parameter</LI>
 * <LI>value - current value of the parameter</LI>
 * <LI>parms - map of additional style parameters</LI>
 * </UL>
 * 
 * <P>
 * Supporting Velocity templates should be placed in
 * ${velocity-templates-root}/parameters folder.
 * </p>
 * 
 * <P>
 * It may be used directly with "template" as the only required parameter. This
 * is useful when the no additional objects are needed by the template.
 * </P>
 * 
 * <P>
 * If additional objects need to be put in the context, a new class extending
 * VelocityParameterPresentationStyle should be created. Override buildContext
 * to place custom objects in the Velocity context.
 * </P>
 * 
 * <P>
 * If "template" parameter is not specified, it is assumed that the template
 * name is "classname.vm".
 * </P>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 */

public class VelocityParameterPresentationStyle extends
    ParameterPresentationStyle {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(VelocityParameterPresentationStyle.class.getName());

  /**
   * Returns presentation control
   * 
   * @param data -
   *            rundata object
   * @param name -
   *            parameter name
   * @param value -
   *            current parameter value
   * @param parms -
   *            additional style parameters
   * @return string
   */
  public String getContent(RunData data, String name, String value, Map parms) {
    String result = null;

    // create a blank context
    Context context = TurbineVelocity.getContext();

    // Put basics in the context
    context.put("data", data);
    context.put("name", name);
    context.put("value", value);
    context.put("parms", parms);
    context.put("events", this.getJavascriptEvents());
    LocalizationTool lt = new LocalizationTool();
    lt.init(data);
    context.put("l10n", lt);
    context.put("jslink", new BaseJetspeedLink(data));
    context.put("utils", new ALCommonUtils());

    try {
      // Add custom objects to the context
      this.buildContext(data, name, value, parms, context);

      // Build default template name (classname + .vm)
      String className = this.getClass().getName();
      int pos = className.lastIndexOf(".");
      pos = pos < 0 ? 0 : pos + 1;
      className = className.substring(pos);

      // Render the template
      String template = (String) this.getParm("template", className + ".vm");
      String templatePath = TemplateLocator.locateParameterTemplate(data,
          template);
      result = TurbineVelocity.handleRequest(context, templatePath);
    } catch (Exception e) {
      logger.error("Exception", e);
      // Fallback to input text box presentation style
      result = "<input type=\"text\" name=\"" + name + "\" value=\"" + value
          + "\"";
    }

    TurbineVelocity.requestFinished(context);

    return result;

  }

  /**
   * Override this method to put your own objects in the Velocity context
   * 
   * @param data
   * @param name
   * @param value
   * @param parms
   * @param context
   */
  public void buildContext(RunData data, String name, String value, Map parms,
      Context context) {

  }
}
