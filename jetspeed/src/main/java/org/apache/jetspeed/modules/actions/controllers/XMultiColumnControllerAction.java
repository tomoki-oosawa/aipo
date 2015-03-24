/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.modules.actions.controllers;

import java.util.List;

import javax.servlet.ServletRequest;

import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.StringUtils;
import org.apache.jetspeed.util.template.JspTemplate;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * XMultiColumnControllerAction
 * 
 * @author <a href="mailto:junyang@cisco.com">Jun Yang</a>
 */
public class XMultiColumnControllerAction extends MultiColumnControllerAction {
  protected void buildCustomizeContext(PortletController controller,
      Context context, RunData rundata) {
    super.buildCustomizeContext(controller, context, rundata);
    context.put("template", "x-multicolumn-customize.vm");
    context.put("action", "controllers.XMultiColumnControllerAction");

    context.put("includedContent", new JspTemplate(rundata,
        "/controllers/html/x-multicolumn-customize.jsp"));
    ServletRequest request = rundata.getRequest();
    request.setAttribute("jspContext", context);

    // debug
    /*
     * Object[] columns = (Object[]) context.get("portlets"); Map
     * portletTitleMap = (Map) context.get("titles"); for (int i = 0; i <
     * columns.length; i++) { List portletList = (List) columns[i]; for (int j =
     * 0; j < portletList.size(); j++) { PsmlEntry entry = (PsmlEntry)
     * portletList.get(j); String portletTitle = (String)
     * portletTitleMap.get(entry.getId());
     * 
     * 
     * String portletSkinName = entry.getSkin() == null ? "-- Default --" :
     * entry.getSkin().getName(); String portletSecurityId =
     * entry.getSecurityRef() == null ? "-- Default --" :
     * entry.getSecurityRef().getParent(); String controlListBox =
     * (entry.getControl() != null && entry.getControl().getName() != null) ?
     * JetspeedTool.getPortletParameter(rundata, (Portlet)
     * rundata.getUser().getTemp("customizer"), "control",
     * entry.getControl().getName()) : JetspeedTool.getPortletParameter(rundata,
     * (Portlet) rundata.getUser().getTemp("customizer"), "control");
     * controlListBox = controlListBox.substring(12); controlListBox =
     * controlListBox.replace('\n', ' '); controlListBox =
     * controlListBox.replace('\r', ' '); int z = 0; } }
     */
  }

  public void doSave(RunData data, Context context) {
    applyModelChanges(data, context);
    super.doSave(data, context);
  }

  public void doDelete(RunData data, Context context) {
    applyModelChanges(data, context);
    super.doDelete(data, context);
  }

  public void doControl(RunData data, Context context) {
    applyModelChanges(data, context);
    super.doControl(data, context);
  }

  protected void applyModelChanges(RunData data, Context context) {
    ServletRequest request = data.getRequest();
    String[] modelChangeList = request.getParameterValues("modelChangeList");

    try {
      if (modelChangeList != null && modelChangeList.length > 0) {
        if (!modelChangeList[0].equals("")) {
          String[] moves = StringUtils.stringToArray(modelChangeList[0], ";");
          SessionState customizationState = ((JetspeedRunData) data)
              .getPageSessionState();
          List[] columns = (List[]) customizationState
              .getAttribute("customize-columns");

          for (int i = 0; i < moves.length; i++) {
            String[] values = StringUtils.stringToArray(moves[i], ",");
            int originCol = Integer.parseInt(values[0]);
            int originRow = Integer.parseInt(values[1]);
            int destCol = Integer.parseInt(values[2]);
            int destRow = Integer.parseInt(values[3]);
            if (columns[originCol].size() <= originRow)
              throw new IndexOutOfBoundsException();
            Object portletEntry = columns[originCol].get(originRow);
            if (columns[originCol].contains(portletEntry)) {
              columns[originCol].remove(originRow);
            }
            columns[destCol].add(destRow, portletEntry);
          }
        }
      }
    } catch (IndexOutOfBoundsException e) {

    }
  }
}
