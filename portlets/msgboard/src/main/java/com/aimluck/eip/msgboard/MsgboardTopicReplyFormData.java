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
package com.aimluck.eip.msgboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.modules.actions.msgboard.MsgboardAction;
import com.aimluck.eip.modules.screens.MsgboardTopicDetailScreen;
import com.aimluck.eip.modules.screens.MsgboardTopicFormJSONScreen;
import com.aimluck.eip.msgboard.util.MsgboardUtils;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * 掲示板返信のフォームデータを管理するクラスです。 <BR>
 *
 */
public class MsgboardTopicReplyFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(MsgboardTopicReplyFormData.class.getName());

  /** トピック名 */
  private ALStringField topic_name;

  /** メモ */
  private ALStringField note;

  /** 添付ファイル */
  private ALStringField attachment = null;

  /** 添付ファイルリスト */
  private List<FileuploadLiteBean> fileuploadList = null;

  /** 添付フォルダ名 */
  private String folderName = null;

  private int uid;

  private String org_id;

  private DataContext dataContext;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /** 閲覧権限の有無 */
  @SuppressWarnings("unused")
  private boolean hasAclCategoryList;

  /** 他ユーザーの作成したトピックの編集権限 */
  private boolean hasAclUpdateTopicOthers;

  /** 他ユーザーの作成したトピックの削除権限 */
  private boolean hasAclDeleteTopicOthers;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractFormData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    uid = ALEipUtils.getUserId(rundata);
    org_id = DatabaseOrmService.getInstance().getOrgId(rundata);

    if (dataContext == null) {
      dataContext = DatabaseOrmService.getInstance().getDataContext();
    }

    folderName = rundata.getParameters().getString("folderName");

    ALAccessControlFactoryService aclservice = (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAclCategoryList = aclhandler.hasAuthority(ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY,
        ALAccessControlConstants.VALUE_ACL_LIST);

    hasAclDeleteTopicOthers = aclhandler.hasAuthority(ALEipUtils
        .getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);

    hasAclUpdateTopicOthers = aclhandler.hasAuthority(ALEipUtils
        .getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   *
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    // トピック名
    topic_name = new ALStringField();
    topic_name.setFieldName("タイトル");
    topic_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("内容");
    note.setTrim(false);
    // Attachment
    attachment = new ALStringField();
    attachment.setFieldName("添付ファイル");
    attachment.setTrim(true);

    fileuploadList = new ArrayList<FileuploadLiteBean>();
  }

  /**
   * 掲示板の各フィールドに対する制約条件を設定します。 <BR>
   *
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  protected void setValidator() {
    // トピック名必須項目
    topic_name.setNotNull(true);
    // トピック名の文字数制限
    topic_name.limitMaxLength(50);
    // メモ必須項目
    note.setNotNull(true);
    // メモの文字数制限
    note.limitMaxLength(10000);
  }

  /**
   * トピックのフォームに入力されたデータの妥当性検証を行います。 <BR>
   *
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  protected boolean validate(List<String> msgList) {
    // トピック名
    topic_name.validate(msgList);
    // メモ
    note.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * トピックをデータベースから読み出します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 返信記事をデータベースから削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      String topicid = rundata.getParameters().getString("topic_reply_id");

      // オブジェクトモデルを取得
      EipTMsgboardTopic topic;

      if (this.hasAclDeleteTopicOthers) {
        topic = MsgboardUtils.getEipTMsgboardTopicReply(rundata, context,
            topicid, true);
      } else {
        topic = MsgboardUtils.getEipTMsgboardTopicReply(rundata, context,
            topicid, false);
      }
      if (topic == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopicReplyFormData] Not found ID...");
        throw new ALPageNotFoundException();
      }

      List<String> fpaths = new ArrayList<String>();
      List<?> files = topic.getEipTMsgboardFileArray();
      if (files != null && files.size() > 0) {
        int fsize = files.size();
        for (int i = 0; i < fsize; i++) {
          fpaths.add(((EipTMsgboardFile) files.get(i)).getFilePath());
        }
      }

      // 返信記事を削除
      // 添付ファイルはカスケードで自動的に削除される．
      dataContext.deleteObject(topic);
      dataContext.commitChanges();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          topic.getTopicId(), ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC,
          topic.getTopicName());

      if (fpaths.size() > 0) {
        // ローカルファイルに保存されているファイルを削除する．
        File file = null;
        int fsize = fpaths.size();
        for (int i = 0; i < fsize; i++) {
          file = new File(MsgboardUtils.getSaveDirPath(org_id, uid)
              + (String) fpaths.get(i));
          if (file.exists()) {
            file.delete();
          }
        }
      }
    } catch (Exception e) {
      // TODO: エラー処理
      logger.error("[MsgboardTopicReplyFormData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * トピックをデータベースに格納します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // オブジェクトモデルを取得
      EipTMsgboardTopic parenttopic = MsgboardUtils.getEipTMsgboardParentTopic(
          rundata, context, false);
      if (parenttopic == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopicReplyFormData] Not found ID...");
        throw new ALPageNotFoundException();
      }

      Date updateDate = Calendar.getInstance().getTime();

      // 新規オブジェクトモデル
      EipTMsgboardTopic topic = (EipTMsgboardTopic) dataContext
          .createAndRegisterNewObject(EipTMsgboardTopic.class);
      // トピック名
      topic.setTopicName(topic_name.getValue());
      // カテゴリID
      topic.setEipTMsgboardCategory(parenttopic.getEipTMsgboardCategory());
      // 親トピック ID
      topic.setParentId(parenttopic.getTopicId());
      // ユーザーID
      topic.setOwnerId(Integer.valueOf(uid));
      // メモ
      topic.setNote(note.getValue());
      // 作成者
      topic.setCreateUserId(Integer.valueOf(uid));
      // 更新者
      topic.setUpdateUserId(Integer.valueOf(uid));
      // 作成日
      topic.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      topic.setUpdateDate(updateDate);

      // 親トピックの更新情報を更新する．
      parenttopic.setUpdateUserId(Integer.valueOf(uid));
      parenttopic.setUpdateDate(updateDate);

      // ファイルをデータベースに登録する．
      if (!MsgboardUtils.insertFileDataDelegate(rundata, context, topic,
          fileuploadList, folderName, msgList)) {
        return false;
      }

      dataContext.commitChanges();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          topic.getTopicId(), ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC,
          topic.getTopicName());

      /* 自分以外の全員に新着ポートレット登録 */
      if ("T".equals(topic.getEipTMsgboardCategory().getPublicFlag())) {
        WhatsNewUtils.insertWhatsNewPublic(dataContext,
            WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC, topic.getTopicId(),
            uid);
      } else {
        List<Integer> userIds = MsgboardUtils.getWhatsNewInsertList(rundata, topic
            .getEipTMsgboardCategory().getCategoryId().intValue(), topic
            .getEipTMsgboardCategory().getPublicFlag());

        int u_size = userIds.size();
        for (int i = 0; i < u_size; i++) {
          Integer _id = (Integer) userIds.get(i);
          WhatsNewUtils.insertWhatsNew(dataContext,
              WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC, topic.getTopicId()
                  .intValue(), _id.intValue());
        }
      }
      File folder = FileuploadUtils.getFolder(org_id, uid, folderName);
      // 添付ファイル保存先のフォルダを削除
      FileuploadUtils.deleteFolder(folder);
    } catch (Exception e) {
      // TODO: エラー処理
      logger.error("[MsgboardTopicReplyFormData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * データベースに格納されているトピックを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * トピック詳細表示ページからデータを新規登録します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  public boolean doInsert(ALAction action, RunData rundata, Context context) {
    try {
      if (!doCheckSecurity(rundata, context)) {
        return false;
      }
      init(action, rundata, context);

      doCheckAclPermission(rundata, context,
          ALAccessControlConstants.VALUE_ACL_INSERT);

      action.setMode(ALEipConstants.MODE_INSERT);
      List<String> msgList = new ArrayList<String>();
      setValidator();
      boolean res = (setFormData(rundata, context, msgList)
          && validate(msgList) && insertFormData(rundata, context, msgList));
      if (!res) {
        action.setMode(ALEipConstants.MODE_NEW_FORM);
        setMode(action.getMode());
      }
      if (action instanceof MsgboardTopicFormJSONScreen) {
        action.setResultData(this);
        action.addErrorMessages(msgList);
        action.putData(rundata, context);
      } else {
        MsgboardAction msgboardAction = (MsgboardAction) action;
        msgboardAction.setResultDataOnTopicDetail(this);
        msgboardAction.addErrorMessagesOnTopicDetail(msgList);
        msgboardAction.putDataOnTopicDetail(rundata, context);
      }
      return res;
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
   * トピック詳細表示ページにフォームを表示します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  public boolean doViewForm(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);

      // doCheckAclPermission(rundata, context,
      // ALAccessControlConstants.VALUE_ACL_INSERT);

      action.setMode("reply");
      // mode = action.getMode();
      List<String> msgList = new ArrayList<String>();
      boolean res = setFormData(rundata, context, msgList);
      if (action instanceof MsgboardTopicDetailScreen) {
        MsgboardTopicDetailScreen msgboardAction = (MsgboardTopicDetailScreen) action;
        msgboardAction.setResultDataOnTopicDetail(this);
        msgboardAction.addErrorMessagesOnTopicDetail(msgList);
        msgboardAction.putDataOnTopicDetail(rundata, context);
      } else {
        MsgboardAction msgboardAction = (MsgboardAction) action;
        msgboardAction.setResultDataOnTopicDetail(this);
        msgboardAction.addErrorMessagesOnTopicDetail(msgList);
        msgboardAction.putDataOnTopicDetail(rundata, context);
      }
      return res;
      // } catch (ALPermissionException e) {
      // ALEipUtils.redirectPermissionError(rundata);
      // return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }
  }

  /**
   *
   * @see com.aimluck.eip.common.ALAbstractFormData#setFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);

    try {
      fileuploadList = FileuploadUtils.getFileuploadList(rundata);
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }

    return res;
  }

  public void setAclPortletFeature(String featureName) {
    aclPortletFeature = featureName;
  }

  /**
   * トピック名を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getTopicName() {
    return topic_name;
  }

  /**
   * メモを取得します。 <BR>
   *
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    return fileuploadList;
  }

  public String getFolderName() {
    return folderName;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    if (aclPortletFeature == null || "".equals(aclPortletFeature)) {
      return ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_REPLY;
    } else {
      return aclPortletFeature;
    }
  }

  /**
   * 他ユーザのトピックを編集する権限があるかどうかを返します。
   *
   * @return
   */

  public boolean hasAclUpdateTopicOthers() {
    return hasAclUpdateTopicOthers;
  }

  /**
   * 他ユーザのトピックを削除する権限があるかどうかを返します。
   *
   * @return
   */
  public boolean hasAclDeleteTopicOthers() {
    return hasAclDeleteTopicOthers;
  }
}
