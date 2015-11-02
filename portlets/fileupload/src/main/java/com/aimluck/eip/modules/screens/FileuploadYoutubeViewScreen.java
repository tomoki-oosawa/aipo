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

import java.util.List;

import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTimelineUrl;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 各ポートレットでの添付ファイルを表示させるクラスです。 <br />
 *
 */
public class FileuploadYoutubeViewScreen extends ALVelocityScreen {

  /**
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    String vid = ALEipUtils.getParameter(rundata, context, "vid");
    String id = ALEipUtils.getParameter(rundata, context, "id");
    String title = "";

    boolean isAndroid = ALEipUtils.isAndroidBrowser(rundata);

    SelectQuery<EipTTimelineUrl> query = Database.query(EipTTimelineUrl.class);
    query.where(Operations.in(EipTTimelineUrl.TIMELINE_ID_PROPERTY, id));
    List<EipTTimelineUrl> list = query.fetchList();

    if (list.size() > 0) {
      title = list.get(0).getTitle();
    }

    context.put("jslink", JetspeedLinkFactory.getInstance(rundata));
    context.put("vid", vid);
    context.put("title", title);
    context.put("android", isAndroid);

    putData(rundata, context);

    String scheme = rundata.getServerData().getServerScheme();
    if (scheme == null || scheme.length() == 0) {
      scheme = "http";
    }
    context.put("scheme", scheme);

    String layout_template = "portlets/html/fileupload-youtube.vm";
    setTemplate(rundata, context, layout_template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return null;
  }

}
