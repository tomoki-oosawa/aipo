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
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * フィルタの順番情報のフォームデータを管理するためのクラスです。 <br />
 */
public class WebMailFilterOrderFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailFilterOrderFormData.class.getName());

  /** フィルタ名のリスト */
  private ALStringField positions;

  /** フィルタと紐付くメールアカウント */
  private EipMMailAccount mailAccount;

  /** フィルタ情報のリスト */
  private List filterList = null;

  /**
   * 初期化します。
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractFormData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    int mailAccountId = 0;

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      try {
        // パラメータにアカウントIDがあった場合
        if (rundata.getParameters().containsKey(WebMailUtils.ACCOUNT_ID)) {
          mailAccountId = Integer.parseInt(rundata.getParameters().get(
              WebMailUtils.ACCOUNT_ID));
        } else {
          // 無い場合はセッションからアカウントIDを取得する。
          mailAccountId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
              WebMailUtils.ACCOUNT_ID));
        }
      } catch (Exception e) {
        logger.error("[WebMail Filter] mail account was not found.");
        return;
      }
    }

    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    ALEipUser login_user = ALEipUtils.getALEipUser(rundata);

    // メールアカウントを取得する
    mailAccount = ALMailUtils.getMailAccount(null, (int) login_user.getUserId()
        .getValue(), mailAccountId);

    if (mailAccount == null) {
      logger.error("[WebMail Filter] mail account was not found.");
      return;
    }

    filterList = new ArrayList();

    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    // ユーザ名のリスト
    positions = new ALStringField();
    positions.setFieldName("フィルタリスト");
    positions.setTrim(true);
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#setFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = true;
    try {
      res = super.setFormData(rundata, context, msgList);
      if (res) {
        List filters = ALMailUtils.getEipTMailFilters(mailAccount);
        EipTMailFilter filter;
        for (Object rs : filters) {
          filter = (EipTMailFilter) rs;
          WebMailFilterResultData rd = new WebMailFilterResultData();
          rd.initField();
          rd.setFilterId(filter.getFilterId().longValue());
          rd.setFilterName(filter.getFilterName());
          filterList.add(rd);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  protected void setValidator() {
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  protected boolean validate(List<String> msgList) {
    if (positions.getValue() != null && (!positions.getValue().equals(""))) {
      return true;
    }
    return false;
  }

  /**
   * 『フィルタ』を読み込みます。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * 『フィルタ』を追加します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『フィルタ』を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = true;
    try {
      if (positions.getValue() != null && (!positions.getValue().equals(""))) {
        // formから受け取ったfilter_idのリスト。この順番にSortOrderを変更したい。
        String[] orders = positions.getValue().split(",");
        ArrayList order = new ArrayList();
        for (String rs : orders) {
          order.add(rs);
        }

        // 現存するフィルタのリスト。
        DataContext dataContext = DatabaseOrmService.getInstance()
            .getDataContext();
        List filters = ALMailUtils.getEipTMailFilters(mailAccount);
        EipTMailFilter filter;
        for (Object rs : filters) {
          filter = (EipTMailFilter) rs;
          String filter_id = filter.getFilterId().toString();

          // フィルタのIDでorderの中を探し、順番を得る。
          int order_num = order.indexOf(filter_id);
          if (order_num != -1) {
            filter.setSortOrder(order_num + 1);
          } else {
            // 見つからなければ、指定されるべきfilter_idがorderの中に含まれていなかったことになる
            throw new Exception();
          }
        }

        // orderとフィルタのリストの整合性を確認したうえで、データを更新する
        dataContext.commitChanges();
      }
    } catch (Exception e) {
      logger.error("Exception", e);
      res = false;
    }
    return res;
  }

  /**
   * 『フィルタ』を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * フィルタ情報のリストを取得する．
   * 
   * @return
   */
  public List getFilterList() {
    return filterList;
  }

}
