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
package com.aimluck.eip.webmail;

import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
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
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.beans.WebmailAccountLiteBean;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * フィルタを管理するためのクラスです。 <br />
 */
public class WebMailFilterSelectData extends
    ALAbstractSelectData<EipTMailFilter, EipTMailFilter> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFilterSelectData.class.getName());

  /** フィルタID */
  String filterId = null;

  /** メールアカウント */
  private EipMMailAccount mailAccount;

  /** メールアカウント一覧 */
  private List<WebmailAccountLiteBean> mailAccountList;

  private List<WebMailFolderResultData> mailFolderList;

  private Map<Integer, Integer> unreadMailSumMap;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
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
          mailAccountId =
            Integer.parseInt(rundata.getParameters().get(
              WebMailUtils.ACCOUNT_ID));
        } else {
          mailAccountId =
            Integer.parseInt(ALEipUtils.getTemp(
              rundata,
              context,
              WebMailUtils.ACCOUNT_ID));
        }
      } catch (Exception e) {
      }
    }

    ALEipUser login_user = ALEipUtils.getALEipUser(rundata);

    // 現在操作中のメールアカウントを取得する
    mailAccount =
      ALMailUtils.getMailAccount(
        (int) login_user.getUserId().getValue(),
        mailAccountId);

    // アカウントIDが取得できなかったとき、デフォルトのアカウントIDを取得する
    if (mailAccount == null) {
      try {
        SelectQuery<EipMMailAccount> query =
          Database.query(EipMMailAccount.class);

        Expression exp =
          ExpressionFactory.matchExp(
            EipMMailAccount.USER_ID_PROPERTY,
            login_user.getUserId());
        EipMMailAccount account = query.setQualifier(exp).fetchSingle();

        if (account != null) {
          mailAccount = account;
        } else {
          return;
        }
      } catch (Exception e) {
      }
    }

    // メールアカウントIDをセッションに保存
    if (mailAccount != null) {
      ALEipUtils.setTemp(rundata, context, WebMailUtils.ACCOUNT_ID, mailAccount
        .getAccountId()
        .toString());
    }

    // フォルダリストを取得
    mailFolderList = WebMailUtils.getMailFolderAll(mailAccount);

    // フォルダ未読数マップを取得
    unreadMailSumMap =
      WebMailUtils.getUnreadMailNumberMap(rundata, ALEipUtils
        .getUserId(rundata), mailAccountId);

    super.init(action, rundata, context);
  }

  /**
   * メールアカウント一覧を取得します。
   * 
   * @param rundata
   * @param context
   */
  public void loadMailAccountList(RunData rundata, Context context) {
    mailAccountList = WebMailUtils.getMailAccountList(rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTMailFilter> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      SelectQuery<EipTMailFilter> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("webmail", ex);
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
  private SelectQuery<EipTMailFilter> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);

    Expression exp =
      ExpressionFactory.matchDbExp(
        EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
        mailAccount);
    query.setQualifier(exp);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTMailFilter selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    EipTMailFilter filter =
      WebMailUtils.getEipTMailFilter(mailAccount, filterId);
    return filter;
  }

  /**
   * フィルタのデータを取得します。
   * 
   * 
   */
  @Override
  protected Object getResultData(EipTMailFilter record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      filterId = record.getFilterId().toString();

      WebMailFilterResultData rd = new WebMailFilterResultData();
      Map<String, String> typeMap = ALMailUtils.getMailFilterTypeMap();

      rd.initField();
      rd.setFilterId(record.getFilterId().longValue());
      rd.setSortOrder(record.getSortOrder().longValue());
      rd.setFilterName(record.getFilterName());
      rd.setFilterType(typeMap.get(record.getFilterType()));
      rd.setFilterString(record.getFilterString());
      rd.setDstFolderName(record.getEipTMailFolder().getFolderName());

      return rd;
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return null;
    }
  }

  /**
   * フィルタの詳細データを取得します。
   * 
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTMailFilter record)
      throws ALPageNotFoundException, ALDBErrorException {

    try {
      filterId = record.getFilterId().toString();

      WebMailFilterResultData rd = new WebMailFilterResultData();
      Map<String, String> typeMap = ALMailUtils.getMailFilterTypeMap();

      rd.initField();
      rd.setFilterId(record.getFilterId().longValue());
      rd.setSortOrder(record.getSortOrder().longValue());
      rd.setFilterName(record.getFilterName());
      rd.setFilterType(typeMap.get(record.getFilterType()));
      rd.setFilterString(record.getFilterString());
      rd.setDstFolderName(record.getEipTMailFolder().getFolderName());

      return rd;
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return null;
    }
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("sort_order", EipTMailFilter.SORT_ORDER_PROPERTY);
    map.putValue("filter_name", EipTMailFilter.FILTER_NAME_PROPERTY);
    map.putValue("filter_string", EipTMailFilter.FILTER_TYPE_PROPERTY);
    map.putValue("dst_folder_name", EipTMailFilter.EIP_TMAIL_FOLDER_PROPERTY
      + "."
      + EipTMailFolder.FOLDER_NAME_PROPERTY);
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
    if (mailAccount == null) {
      return 0;
    } else {
      return mailAccount.getAccountId();
    }
  }

  /**
   * メールアカウントの一覧を取得します。
   * 
   * @return
   */
  public List<WebmailAccountLiteBean> getMailAccountList() {
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

  /**
   * 現在のアカウントが持つメールフォルダを取得します。
   * 
   * @return
   */
  public List<WebMailFolderResultData> getFolderList() {
    return mailFolderList;
  }

  /**
   * フォルダ別未読メール数を取得する。
   * 
   * @return
   */
  public int getUnReadMailSumByFolderId(int folder_id) {
    return unreadMailSumMap.get(folder_id);
  }
}
