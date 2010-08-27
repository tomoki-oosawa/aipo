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
package com.aimluck.eip.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローの承認／否認のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class WorkflowConfirmFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowConfirmFormData.class.getName());

  /** コメント */
  private ALStringField comment;

  /** 承認フラグ（承認:true、否認:false） */
  private boolean accept_flg;

  /** 差し戻し先の順位 */
  private ALNumberField passback_order;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  public void initField() {
    // コメント
    comment = new ALStringField();
    comment.setTrim(true);

    // 承認フラグ
    accept_flg = false;

    // 差し戻し先の順位
    passback_order = new ALNumberField();
  }

  /**
   * リクエストの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // コメントの文字数制限
    comment.limitMaxLength(1000);

    if (!accept_flg) {
      passback_order.setNotNull(true);
      passback_order.limitMinValue(0);
    }
  }

  /**
   * リクエストのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // コメント
    comment.validate(msgList);

    if (!accept_flg) {
      // 差し戻し先の順位
      passback_order.validate(msgList);
    }
    return (msgList.size() == 0);
  }

  /**
   * リクエストをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * リクエストをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * リクエストをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * データベースに格納されているリクエストを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTWorkflowRequest request =
        WorkflowUtils.getEipTWorkflowRequest(rundata, context, true);
      if (request == null) {
        return false;
      }

      int login_user_id = ALEipUtils.getUserId(rundata);
      EipTWorkflowRequestMap map = null;
      int order = 0;
      List<EipTWorkflowRequestMap> maps =
        WorkflowUtils.getEipTWorkflowRequestMap(request);
      int size = maps.size();
      for (int i = 0; i < size; i++) {
        map = maps.get(i);
        if (WorkflowUtils.DB_STATUS_CONFIRM.equals(map.getStatus())) {
          order = i + 1;
          break;
        }
      }

      // メールを送信する対象のMap
      EipTWorkflowRequestMap sendMailMap = null;

      Date now = Calendar.getInstance().getTime();
      if (accept_flg) {
        // 現在の申請者は承認
        map.setStatus(WorkflowUtils.DB_STATUS_ACCEPT);
        // コメント追記
        map.setNote(comment.getValue());
        map.setUpdateDate(now);

        for (; order < size; order++) {
          // まだ申請先が残っている場合
          // 次の申請先を指定
          map = maps.get(order);

          // 次のユーザーが削除済みだった場合は自動的に承認させる
          if (WorkflowUtils.getUserIsDisabledOrDeleted(map
            .getUserId()
            .toString())) {
            // 自動承認
            map.setStatus(WorkflowUtils.DB_STATUS_THROUGH);
          } else {
            map.setStatus(WorkflowUtils.DB_STATUS_CONFIRM);
            break;
          }
        }
        if (order == size) {
          // すべての申請先で承認を得られた場合
          request.setProgress(WorkflowUtils.DB_PROGRESS_ACCEPT);
          // 申請者のMapを取得
          map = getEipTWorkflowRequestMapWithRequester(maps);
        }
        sendMailMap = map;
      } else {
        // 差し戻しの処理
        int passback_user_order = (int) passback_order.getValue();

        // 自分自身は否認
        map.setStatus(WorkflowUtils.DB_STATUS_DENIAL);
        // コメント追記
        map.setNote(comment.getValue());
        map.setUpdateDate(now);

        // 差し戻し先が申請者であり、有効である場合
        EipTWorkflowRequestMap applicantMap = maps.get(0);
        if (passback_user_order == 0
          && !WorkflowUtils.getUserIsDisabledOrDeleted(applicantMap
            .getUserId()
            .toString())) {
          // 申請者に差し戻す
          request.setProgress(WorkflowUtils.DB_PROGRESS_DENAIL);

          // 申請者のMapを取得
          map = getEipTWorkflowRequestMapWithRequester(maps);
          sendMailMap = map;
        } else {
          // 差し戻し先が申請者以外か、申請者が無効、もしくは削除されていた場合

          // ワークフローを最後の人まで見ていく
          EipTWorkflowRequestMap passbackMap = null;
          int i;
          for (i = passback_user_order; i < size; i++) {
            // 差し戻し先のmap
            passbackMap = maps.get(i);
            int user_id = passbackMap.getUserId().intValue();

            // 差し戻し先が自分自身だ
            if (user_id == login_user_id) {
              // C（確認）に戻して終了
              map.setStatus(WorkflowUtils.DB_STATUS_CONFIRM);
              sendMailMap = map;
              break;
            }

            // 差し戻し先が無効化、もしくは削除されていた場合
            if (WorkflowUtils.getUserIsDisabledOrDeleted(passbackMap
              .getUserId()
              .toString())) {
              // 自動承認した上で次の人を見に行く
              passbackMap.setStatus(WorkflowUtils.DB_STATUS_THROUGH);
            } else {
              // C（確認）に戻して終了
              passbackMap.setStatus(WorkflowUtils.DB_STATUS_CONFIRM);
              sendMailMap = passbackMap;
              break;
            }
          }

          // 差し戻し先以降全ての人が無効化、もしくは削除されていた場合
          if (i == size) {
            // すべて承認扱いにする
            request.setProgress(WorkflowUtils.DB_PROGRESS_ACCEPT);
          }
        }
      }

      // 更新日
      request.setUpdateDate(now);

      // リクエストを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        request.getRequestId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW,
        request.getEipTWorkflowCategory().getCategoryName()
          + " "
          + request.getRequestName());

      if (sendMailMap != null) {
        /* 次の申請先に新着ポートレット登録 */
        ALEipUser nextUser =
          ALEipUtils.getALEipUser(sendMailMap.getUserId().intValue());

        ALAccessControlFactoryService aclservice =
          (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
            .getInstance())
            .getService(ALAccessControlFactoryService.SERVICE_NAME);
        ALAccessControlHandler aclhandler =
          aclservice.getAccessControlHandler();

        if (aclhandler.hasAuthority(
          (int) nextUser.getUserId().getValue(),
          ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_SELF,
          ALAccessControlConstants.VALUE_ACL_DETAIL)) {
          WhatsNewUtils.insertWhatsNew(
            WhatsNewUtils.WHATS_NEW_TYPE_WORKFLOW_REQUEST,
            request.getRequestId().intValue(),
            (int) nextUser.getUserId().getValue());
        }

        // 次の申請先にメール送信
        WorkflowUtils.sendMail(
          rundata,
          request,
          ALEipUtils.getALEipUser(sendMailMap.getUserId().intValue()),
          new ArrayList<String>());
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  private EipTWorkflowRequestMap getEipTWorkflowRequestMapWithRequester(
      List<EipTWorkflowRequestMap> maps) {
    // 申請者のMapを取得
    EipTWorkflowRequestMap map = null;
    int size = maps.size();
    for (int i = 0; i < size; i++) {
      map = maps.get(i);
      if (WorkflowUtils.DB_STATUS_REQUEST.equals(map.getStatus())) {
        break;
      }
    }
    return map;
  }

  public void setAcceptFlg(boolean bool) {
    accept_flg = bool;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_SELF;
  }

}
