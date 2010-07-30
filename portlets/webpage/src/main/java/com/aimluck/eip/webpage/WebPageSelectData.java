/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.webpage;

import java.util.List;
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * WebPageの検索データを管理するクラスです。 <BR>
 *
 */
public class WebPageSelectData extends ALAbstractSelectData implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebPageSelectData.class.getName());

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractListData#selectData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  public List<?> selectList(RunData rundata, Context context) {
    try {
      return null;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      return null;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  public Object selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    try {
      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
      return portlet;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj) {
    try {
      VelocityPortlet portlet = (VelocityPortlet) obj;
      WebPageResultData rd = new WebPageResultData();
      rd.initField();
      String url = portlet.getPortletConfig().getInitParameter("p1a-url");
      int NormalHeight = Integer.parseInt(portlet.getPortletConfig()
          .getInitParameter("p1b-normalheight"));
      int MaximizedHeight = Integer.parseInt(portlet.getPortletConfig()
          .getInitParameter("p1c-maximizedheight"));

      if (url == null || url.toString() == "") {
        rd.setWebPageFlag(false);
      } else {
        rd.setWebPageFlag(true);
        Pattern pattern = Pattern.compile(".*://.*");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
          url = "http://" + url;
        }
        rd.setLink(url);
      }

      rd.setNormalHeight(NormalHeight);
      rd.setMaximizedHeight(MaximizedHeight);
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    return null;
  }
}
