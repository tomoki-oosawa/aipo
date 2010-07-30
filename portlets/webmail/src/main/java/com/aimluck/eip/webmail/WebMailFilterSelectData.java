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
package com.aimluck.eip.webmail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.beans.WebmailAccountLiteBean;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * フィルタを管理するためのクラスです。 <br />
 */
public class WebMailFilterSelectData extends ALAbstractSelectData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailFilterSelectData.class.getName());

  /** フィルタID */
  String filterId = null;

  /** メールアカウント */
  private EipMMailAccount mailAccount;

  /** メールアカウント一覧 */
  private ArrayList mailAccountList;

  private DataContext dataContext;

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
    int mailAccountId = 0;

    // ソート列が指定されていない場合は処理順の昇順にする
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "sort_order");
    }

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {

      try {
        // フィルタID
        if (rundata.getParameters().containsKey(WebMailUtils.FILTER_ID)) {
          filterId = rundata.getParameters().get(WebMailUtils.FILTER_ID);
        }

        // メールアカウントID
        if (rundata.getParameters().containsKey(WebMailUtils.ACCOUNT_ID)) {
          mailAccountId = Integer.parseInt(rundata.getParameters().get(
              WebMailUtils.ACCOUNT_ID));
        } else {
          mailAccountId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
              WebMailUtils.ACCOUNT_ID));
        }
      } catch (Exception e) {
      }
    }

    dataContext = DatabaseOrmService.getInstance().getDataContext();
    ALEipUser login_user = ALEipUtils.getALEipUser(rundata);
    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);

    // 現在操作中のメールアカウントを取得する
    mailAccount = ALMailUtils.getMailAccount(null, (int) login_user.getUserId()
        .getValue(), mailAccountId);

    // アカウントIDが取得できなかったとき、デフォルトのアカウントIDを取得する
    if (mailAccount == null) {
      try {
        Expression exp = ExpressionFactory.matchExp(
            EipMMailAccount.USER_ID_PROPERTY, login_user.getUserId());
        SelectQuery query = new SelectQuery(EipMMailAccount.class, exp);
        // query.addOrdering(EipMMailAccount.ACCOUNT_ID_PK_COLUMN, true);
        List accounts = dataContext.performQuery(query);
        if (accounts != null && accounts.size() > 0) {
          mailAccount = (EipMMailAccount) accounts.get(0);
        } else {
          // アカウントが一つも見つからなかった
          logger.error("[WebMail Filter] mail account was not found.");
          return;
        }
      } catch (Exception e) {
      }
    }

    // メールアカウントIDをセッションに保存
    ALEipUtils.setTemp(rundata, context, WebMailUtils.ACCOUNT_ID, mailAccount
        .getAccountId().toString());

    super.init(action, rundata, context);
  }

  /**
   * メールアカウント一覧を取得します。
   *
   * @param rundata
   * @param context
   */
  public void loadMailAccountList(RunData rundata, Context context) {
    try {
      // メールアカウント一覧
      mailAccountList = new ArrayList();

      List aList = WebMailUtils.getMailAccountNameList(ALEipUtils
          .getUserId(rundata));

      if (aList == null)
        return;

      WebmailAccountLiteBean bean = null;
      DataRow dataRow = null;
      Iterator iter = aList.iterator();
      while (iter.hasNext()) {
        dataRow = (DataRow) iter.next();
        bean = new WebmailAccountLiteBean();
        bean.initField();
        bean.setAccountId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMMailAccount.ACCOUNT_ID_PK_COLUMN)).intValue());
        bean.setAccountName((String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMMailAccount.ACCOUNT_NAME_COLUMN));
        mailAccountList.add(bean);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
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
  protected List selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List list = dataContext.performQuery(query);
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipTMailFilter.class);

    Expression exp = ExpressionFactory.matchDbExp(
        EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY, mailAccount);
    query.setQualifier(exp);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    EipTMailFilter filter = WebMailUtils.getEipTMailFilter(mailAccount,
        filterId);
    return filter;
  }

  /**
   * フィルタのデータを取得します。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) throws ALPageNotFoundException,
      ALDBErrorException {
    try {
      EipTMailFilter record = (EipTMailFilter) obj;
      filterId = record.getFilterId().toString();

      WebMailFilterResultData rd = new WebMailFilterResultData();
      Map<String,String> typeMap = ALMailUtils.getMailFilterTypeMap();

      rd.initField();
      rd.setFilterId(record.getFilterId().longValue());
      rd.setSortOrder(record.getSortOrder().longValue());
      rd.setFilterName(record.getFilterName());
      rd.setFilterType((String) typeMap.get(record.getFilterType()));
      rd.setFilterString(record.getFilterString());
      rd.setDstFolderName(record.getEipTMailFolder().getFolderName());

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * フィルタの詳細データを取得します。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj)
      throws ALPageNotFoundException, ALDBErrorException {

    try {
      EipTMailFilter record = (EipTMailFilter) obj;
      filterId = record.getFilterId().toString();

      WebMailFilterResultData rd = new WebMailFilterResultData();
      Map<String,String> typeMap = ALMailUtils.getMailFilterTypeMap();

      rd.initField();
      rd.setFilterId(record.getFilterId().longValue());
      rd.setSortOrder(record.getSortOrder().longValue());
      rd.setFilterName(record.getFilterName());
      rd.setFilterType((String) typeMap.get(record.getFilterType()));
      rd.setFilterString(record.getFilterString());
      rd.setDstFolderName(record.getEipTMailFolder().getFolderName());

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("sort_order", EipTMailFilter.SORT_ORDER_PROPERTY);
    map.putValue("filter_name", EipTMailFilter.FILTER_NAME_PROPERTY);
    map.putValue("filter_string", EipTMailFilter.FILTER_TYPE_PROPERTY);
    map.putValue("dst_folder_name", EipTMailFilter.EIP_TMAIL_FOLDER_PROPERTY
        + "." + EipTMailFolder.FOLDER_NAME_PROPERTY);
    return map;
  }

  public String getFilterId() {
    return filterId;
  }

  /**
   * 現在選択中のアカウントIDを取得します。
   *
   * @return
   */
  public int getAccountId() {
    return mailAccount.getAccountId();
  }

  /**
   * メールアカウントの一覧を取得します。
   *
   * @return
   */
  public List getMailAccountList() {
    return mailAccountList;
  }

  /**
   *
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }
}
