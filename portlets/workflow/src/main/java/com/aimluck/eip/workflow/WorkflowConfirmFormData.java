/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
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
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils.Type;

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
  @Override
  public void initField() {
    // コメント
    comment = new ALStringField();
    comment.setFieldName(ALLocalizationUtils.getl10n("WORKFLOW_COMMENT"));
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
      int login_user_id = ALEipUtils.getUserId(rundata);

      // get object model
      EipTWorkflowRequest request =
        WorkflowUtils.getEipTWorkflowRequest(rundata, context, true);

      if (request == null) {
        return false;
      }

      List<EipTWorkflowRequestMap> maps =
        WorkflowUtils.getEipTWorkflowRequestMap(request);

      WorkflowRequestMapHandler mapHandler =
        new WorkflowRequestMapHandler(maps);
      EipTWorkflowRequestMap currentMap = mapHandler.getCurrentMap();
      Date now = Calendar.getInstance().getTime();

      if (currentMap.getUserId() != login_user_id) {
        return false;
      }

      if (accept_flg) {
        // accept case
        currentMap.setStatus(WorkflowUtils.DB_STATUS_ACCEPT);
        currentMap.setNote(comment.getValue());
        currentMap.setUpdateDate(now);

        mapHandler.changeStatusForNextUser();

        if (mapHandler.isApprovalForAll()) {
          // all accept case
          request.setProgress(WorkflowUtils.DB_PROGRESS_ACCEPT);
          mapHandler.setFlowStatus(Type.ACCEPT);

          List<Integer> excludeUserList = new ArrayList<Integer>();
          excludeUserList.add(login_user_id);

          // mail,activity to all users except last approver
          mapHandler.setSendMailMapsForAll(excludeUserList);
          mapHandler.setRecipientsForAll(excludeUserList);
        } else {
          mapHandler.setFlowStatus(Type.REQUEST);
          // mail,activity to next user
          EipTWorkflowRequestMap nextRequestMap =
            maps.get(mapHandler.getLatestOrderIndex());
          mapHandler.addSendMailMaps(nextRequestMap);
          mapHandler.addRecipients(nextRequestMap);
        }

      } else {
        // pass back case
        currentMap.setStatus(WorkflowUtils.DB_STATUS_DENIAL);
        currentMap.setNote(comment.getValue());
        currentMap.setUpdateDate(now);

        int passback_user_order = (int) passback_order.getValue();

        // pass back user is applicant user case
        if (mapHandler.isApplicantUser(passback_user_order)
          && !WorkflowUtils.isDisabledOrDeleted(mapHandler
            .getApplicantUserMap()
            .getUserId())) {
          request.setProgress(WorkflowUtils.DB_PROGRESS_DENAIL);
          mapHandler.setFlowStatus(Type.DENAIL);

          // mail to applicant user and approver
          mapHandler.addSendMailMapsForApplicantAndApprover(login_user_id);
          // activity to applicant user only
          mapHandler.addRecipientsApplicantOnly();
        } else {
          // pass back user is approver or unable user
          boolean result =
            mapHandler.passbackToApprover(passback_user_order, login_user_id);

          if (!result) {
            // passback user and next users all unable case
            // all accept
            request.setProgress(WorkflowUtils.DB_PROGRESS_ACCEPT);
            mapHandler.setFlowStatus(Type.ACCEPT);

            List<Integer> noList = new ArrayList<Integer>();
            // mail, activity to all user
            mapHandler.setSendMailMapsForAll(noList);
            mapHandler.setRecipientsForAll(noList);
          }
        }
      }

      request.setUpdateDate(now);
      Database.commit();

      // save to eventlog
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        request.getRequestId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW,
        request.getEipTWorkflowCategory().getCategoryName()
          + " "
          + request.getRequestName());

      // activity
      if (mapHandler.isRecipientsNotEmpty()) {
        ALEipUser user = ALEipUtils.getALEipUser(login_user_id);
        WorkflowUtils.createWorkflowRequestActivity(request, user
          .getName()
          .getValue(), mapHandler.getRecipients(), mapHandler.getFlowStatus());
      }
      // send mail
      WorkflowUtils.sendMailForUpdate(
        rundata,
        mapHandler.getSendMailMaps(),
        request,
        comment.getValue(),
        mapHandler.getFlowStatus());

    } catch (ALPageNotFoundException ex) {
      Database.rollback();
      msgList.add(ALLocalizationUtils
        .getl10n("WORKFLOW_WORKFLOW_ALREADY_DELETED"));
      logger.error("[WorkflowConfirmFormData]", ex);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
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

  private static class WorkflowRequestMapHandler {

    EipTWorkflowRequestMap currentMap;

    int orderIndex;

    int latestOrderIndex;

    List<EipTWorkflowRequestMap> requestMaps;

    List<EipTWorkflowRequestMap> sendMailMaps;

    int requestMapSize;

    Type flowStatus;

    List<String> recipients;

    public WorkflowRequestMapHandler(List<EipTWorkflowRequestMap> requestMaps) {
      this.requestMaps = requestMaps;
      this.currentMap = null;
      this.orderIndex = 0;
      this.sendMailMaps = new ArrayList<EipTWorkflowRequestMap>();
      this.requestMapSize = requestMaps.size();
      this.flowStatus = null;
      this.recipients = new ArrayList<String>();
      this.init();
    }

    public void init() {

      for (int i = 0; i < requestMapSize; i++) {
        currentMap = requestMaps.get(i);
        if (WorkflowUtils.DB_STATUS_CONFIRM.equals(currentMap.getStatus())) {
          orderIndex = i;
          break;
        }
      }

      this.latestOrderIndex = orderIndex;
    }

    public EipTWorkflowRequestMap getCurrentMap() {
      return currentMap;
    }

    @SuppressWarnings("unused")
    public int getOrderInex() {
      return orderIndex;
    }

    public List<EipTWorkflowRequestMap> getSendMailMaps() {
      return sendMailMaps;
    }

    @SuppressWarnings("unused")
    public int getRequestMapSize() {
      return requestMapSize;
    }

    public int getLatestOrderIndex() {
      return latestOrderIndex;
    }

    public Type getFlowStatus() {
      return flowStatus;
    }

    public void setFlowStatus(Type str) {
      this.flowStatus = str;
    }

    public List<String> getRecipients() {
      return recipients;
    }

    public void changeStatusForNextUser() {

      EipTWorkflowRequestMap nextRequestMap;
      latestOrderIndex++;

      while (latestOrderIndex < requestMapSize) {

        // まだ申請先が残っている場合、次の申請先を指定
        nextRequestMap = requestMaps.get(latestOrderIndex);

        // 次のユーザーが削除済みだった場合は自動的に承認させる
        if (WorkflowUtils.isDisabledOrDeleted(nextRequestMap.getUserId())) {
          // 自動承認
          nextRequestMap.setStatus(WorkflowUtils.DB_STATUS_THROUGH);
          latestOrderIndex++;
        } else {
          nextRequestMap.setStatus(WorkflowUtils.DB_STATUS_CONFIRM);
          break;
        }
      }
    }

    public boolean isApprovalForAll() {
      return getLatestOrderIndex() == requestMapSize;
    }

    public void setSendMailMapsForAll(List<Integer> excludeUserList) {

      for (EipTWorkflowRequestMap requestMap : requestMaps) {
        int userId = requestMap.getUserId();
        boolean isExclude = false;

        for (Integer excluedUserId : excludeUserList) {
          if (userId == excluedUserId) {
            isExclude = true;
            break;
          }
        }

        if (!isExclude) {
          sendMailMaps.add(requestMap);
        }
      }
    }

    public void addSendMailMaps(EipTWorkflowRequestMap requestMap) {
      sendMailMaps.add(requestMap);
    }

    public boolean isApplicantUser(int request_order) {
      return request_order == 0;
    }

    public EipTWorkflowRequestMap getApplicantUserMap() {
      return requestMaps.get(0);
    }

    public void addSendMailMapsForApplicantAndApprover(int loginUserId) {
      for (EipTWorkflowRequestMap requestMap : requestMaps) {

        if (requestMap.getUserId() == loginUserId) {
          break;
        } else {
          this.addSendMailMaps(requestMap);
        }
      }
    }

    public void addSendMailMapsForApprover(int passbackUserId, int loginUserId) {

      boolean isStart = false;

      for (EipTWorkflowRequestMap requestMap : requestMaps) {

        int userId = requestMap.getUserId();

        if (userId == passbackUserId) {
          isStart = true;
        }

        if (userId == loginUserId) {
          break;
        } else {
          if (isStart) {
            this.addSendMailMaps(requestMap);
          }
        }
      }
    }

    public boolean passbackToApprover(int passbackUserOrderIndex,
        int loginUserId) {

      EipTWorkflowRequestMap passbackMap =
        getRequestMapByOrderIndex(passbackUserOrderIndex);

      if (null == passbackMap) {
        return false;
      }

      int passbackUserId = passbackMap.getUserId();

      // bass back user equals myself
      if (passbackUserId == loginUserId) {
        // status change to C and process done .
        currentMap.setStatus(WorkflowUtils.DB_STATUS_CONFIRM);
        setFlowStatus(Type.DENAIL);

        // mail,activity to myself
        addSendMailMaps(passbackMap);
        addRecipients(passbackMap);
        return true;
      }

      if (WorkflowUtils.isDisabledOrDeleted(passbackUserId)) {
        // pass back user is unable
        // auto approve and process to next user
        passbackMap.setStatus(WorkflowUtils.DB_STATUS_THROUGH);
        return passbackToApprover(passbackUserOrderIndex + 1, loginUserId);
      } else {
        // status change to C and process done .
        passbackMap.setStatus(WorkflowUtils.DB_STATUS_CONFIRM);
        setFlowStatus(Type.DENAIL);

        // mail to passback user and approver
        addSendMailMapsForApprover(passbackUserId, loginUserId);
        // activity to passback user only
        addRecipients(passbackMap);

        return true;
      }

    }

    public EipTWorkflowRequestMap getRequestMapByOrderIndex(int orderIndex) {
      for (EipTWorkflowRequestMap requestMap : requestMaps) {
        if (requestMap.getOrderIndex() == orderIndex) {
          return requestMap;
        }
      }
      return null;
    }

    @SuppressWarnings("unused")
    public boolean isSendMailMapsNotEmpty() {
      return (null != sendMailMaps) && (sendMailMaps.size() > 0);
    }

    public void setRecipientsForAll(List<Integer> excludeUserList) {

      for (EipTWorkflowRequestMap requestMap : requestMaps) {
        int userId = requestMap.getUserId();
        boolean isExclude = false;

        for (Integer excluedUserId : excludeUserList) {
          if (userId == excluedUserId) {
            isExclude = true;
            break;
          }
        }

        if (!isExclude) {
          try {
            ALEipUser user = ALEipUtils.getALEipUser(requestMap.getUserId());
            recipients.add(user.getName().getValue());
          } catch (ALDBErrorException e) {
            logger.error("[WorkflowConfirmFormData]", e);
          }
        }
      }
    }

    public void addRecipients(EipTWorkflowRequestMap requestMap) {
      try {
        ALEipUser user = ALEipUtils.getALEipUser(requestMap.getUserId());
        recipients.add(user.getName().getValue());
      } catch (ALDBErrorException e) {
        logger.error("[WorkflowConfirmFormData]", e);
      }
    }

    public void addRecipientsApplicantOnly() {
      addRecipients(requestMaps.get(0));
    }

    public boolean isRecipientsNotEmpty() {
      return (null != recipients) && (recipients.size() > 0);
    }

  }

}
