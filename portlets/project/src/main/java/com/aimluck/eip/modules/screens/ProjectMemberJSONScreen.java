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
package com.aimluck.eip.modules.screens;

import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.project.util.ProjectUtils;

/**
 *
 */
public class ProjectMemberJSONScreen extends ALJSONScreen {

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String projectId = rundata.getParameters().getString("projectId");
    if (StringUtils.isEmpty(projectId) || !StringUtils.isNumeric(projectId)) {
      return "";
    }
    Map<String, String> map =
      ProjectUtils.getProjectMemberMap(Integer.valueOf(projectId));
    return JSONArray.fromObject(map).toString();
  }

}
