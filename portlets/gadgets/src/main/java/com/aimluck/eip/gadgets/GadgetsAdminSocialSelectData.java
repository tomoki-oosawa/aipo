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
package com.aimluck.eip.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest.Status;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.CustomizeUtils;

/**
 *
 */
public class GadgetsAdminSocialSelectData extends
    ALAbstractSelectData<ALApplication, ALApplication> {

  /** 一覧データ */
  private List<Object> list;

  /** 表示タイプ */
  private static final String viewtype = "social";

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

  }

  /**
   * 一覧表示します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FASLE 失敗
   */
  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_LIST);
      action.setMode(ALEipConstants.MODE_LIST);

      ResultList<PortletEntry> resultList = selectListPortletEntry(
        rundata,
        context);

      if (resultList != null) {
        list = new ArrayList<Object>();
        for (PortletEntry model : resultList) {
          Object object = model;
          if (object != null) {
            list.add(object);
          }
        }
      }

      action.setResultData(this);
      action.putData(rundata, context);
      ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
      return (list != null);
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }

  }

  /**
   * ページング結果のリストを取得します。
   *
   * @param records
   *          検索結果
   */
  protected List<PortletEntry> buildPaginatedListPortletEntry(
      List<PortletEntry> records) {
    List<PortletEntry> aList = new ArrayList<PortletEntry>();

    setPageParam(records.size());

    int size = records.size();
    int end = (getStart() + getRowsNum() <= size)
      ? getStart() + getRowsNum()
      : size;
    for (int i = getStart(); i < end; i++) {
      aList.add(records.get(i));
    }

    return aList;
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<ALApplication> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    return new ResultList<ALApplication>();
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  protected ResultList<PortletEntry> selectListPortletEntry(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    Status status = Status.ALL;
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    Profile profile = jdata.getProfile();
    String mediaType = profile.getMediaType();

    if ("1".equals(filter)) {
      status = Status.ACTIVE;
      current_filter = filter;
    } else if ("0".equals(filter)) {
      status = Status.INACTIVE;
      current_filter = filter;
    } else if ("all".equals(filter)) {
      status = Status.ALL;
      current_filter = filter;
    }
    List<PortletEntry> allPortlets = new ArrayList<PortletEntry>();
    List<PortletEntry> portlets = CustomizeUtils.buildPortletListWithStatus(
      rundata,
      mediaType,
      allPortlets,
      status);

    // リストから通常アプリを削除
    portlets
      .removeIf(portlet -> !portlet.getParent().equals("GadgetsTemplate"));

    return new ResultList<PortletEntry>(
      buildPaginatedListPortletEntry(portlets),
      current_page,
      getRowsNum(),
      portlets.size());
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ALApplication selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String appId = ALEipUtils.getTemp(
      rundata,
      context,
      ALEipConstants.ENTITY_ID);

    return ALApplicationService.get(new ALApplicationGetRequest().withAppId(
      appId).withStatus(Status.ALL).withIsDetail(true).withIsFetchXml(true));
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(ALApplication model)
      throws ALPageNotFoundException, ALDBErrorException {
    return model;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(ALApplication model)
      throws ALPageNotFoundException, ALDBErrorException {
    return model;
  }

  public String getStringNumber(String str) {
    return str.replace("GadgetsTemplate::", "");
  }

  /**
   * 一覧データを取得します。
   *
   * @return
   */
  @Override
  public List<Object> getList() {
    return list;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   * 表示タイプを取得します
   *
   * @return String
   */
  public String getViewtype() {
    return viewtype;
  }

}
