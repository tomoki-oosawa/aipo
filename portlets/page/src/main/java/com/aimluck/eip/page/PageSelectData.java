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
package com.aimluck.eip.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.page.util.PageUtils;

/**
 * ページ設定の検索データを管理するためのクラスです。
 */
public class PageSelectData extends ALAbstractSelectData<Portlets, Portlets> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PageSelectData.class.getName());

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<Portlets> selectList(RunData rundata, Context context) {
    Portlet portlet = (Portlet) context.get("portlet");
    String selectedPortletId = portlet.getID();
    if (selectedPortletId == null || selectedPortletId.equals("")) {
      return null;
    }

    // タブ [個人設定] のIDを取得する。
    String selectedPageId =
      PageUtils.getPortletSetId(rundata, selectedPortletId);

    Portlets portlets =
      ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
    if (portlets == null) {
      return null;
    }

    // ページの位置でソートする．
    Portlets pane = null;
    List<Portlets> portletList = new ArrayList<Portlets>();
    int portletsLen = portlets.getPortletsCount();
    for (int i = 0; i < portletsLen; i++) {
      pane = portlets.getPortlets(i);
      if (selectedPageId.equals(pane.getId())
        || pane.getSecurityRef().getParent().equals("admin-view")) {
        // [個人設定]と[システム管理]は表示しない
        continue;
      }
      portletList.add(pane);
    }
    Collections.sort(portletList, new Comparator<Portlets>() {
      @Override
      public int compare(Portlets pane1, Portlets pane2) {
        Long pos1 = Long.valueOf(pane1.getLayout().getPosition());
        Long pos2 = Long.valueOf(pane2.getLayout().getPosition());
        return pos1.compareTo(pos2);
      }
    });

    int length = portletList.size();
    setPageParam(length);
    int start = getStart();
    int rowsNum = getRowsNum();

    // Portlets pagePortlets = null;
    int count = -1;
    List<Portlets> list = new ArrayList<Portlets>();
    for (int i = 0; i < rowsNum; i++) {
      count = i + start;
      if (count >= length) {
        break;
      }
      list.add(portletList.get(count));
    }

    portletList.clear();

    return new ResultList<Portlets>(list);
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected Portlets selectDetail(RunData rundata, Context context) {
    String portletId =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    if (portletId == null || portletId.equals("")) {
      return null;
    }

    return PageUtils.getPortlets(rundata, portletId);
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(Portlets obj) {
    return getResultDataDetail(obj);
  }

  /**
   *
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(Portlets record) {
    PageResultData rd = new PageResultData();
    rd.initField();
    rd.setPageId(record.getId());
    rd.setPageTitle(record.getTitle());
    rd.setPageDescription(record.getDescription());
    rd.setPortletNum(record.getEntryCount());
    return rd;
  }

  /**
   *
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
