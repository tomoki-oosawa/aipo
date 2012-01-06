/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTReport;
import com.aimluck.eip.cayenne.om.portlet.EipTReportFile;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.modules.actions.report.ReportAction;
import com.aimluck.eip.modules.screens.ReportDetailScreen;
import com.aimluck.eip.modules.screens.ReportFormJSONScreen;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.report.util.ReportUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板返信のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class ReportReplyFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ReportReplyFormData.class.getName());

  /** トピック名 */
  private ALStringField report_name;

  /** メモ */
  private ALStringField note;

  /** 添付ファイル */
  private ALStringField attachment = null;

  /** 添付ファイルリスト */
  private List<FileuploadLiteBean> fileuploadList = null;

  /** 添付フォルダ名 */
  private String folderName = null;

  private int uid;

  private String orgId;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /** 閲覧権限の有無 */
  @SuppressWarnings("unused")
  private boolean hasAclCategoryList;

  /** 他ユーザーの作成したトピックの編集権限 */
  private boolean hasAclUpdatereportOthers;

  /** 他ユーザーの作成したトピックの削除権限 */
  private boolean hasAclDeletereportOthers;

  /** <code>login_user</code> ログインユーザー */
  private ALEipUser login_user;

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

    login_user = ALEipUtils.getALEipUser(rundata);
    uid = ALEipUtils.getUserId(rundata);
    orgId = Database.getDomainName();

    folderName = rundata.getParameters().getString("folderName");

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAclDeletereportOthers =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_REPORT_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);

    hasAclUpdatereportOthers =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_REPORT_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // トピック名
    report_name = new ALStringField();
    report_name.setFieldName("返信タイトル");
    report_name.setTrim(true);
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
   * 
   */
  @Override
  protected void setValidator() {
    // メモ必須項目
    note.setNotNull(true);
    // メモの文字数制限
    note.limitMaxLength(10000);
  }

  /**
   * トピックのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param reportList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> reportList) {
    // トピック名
    report_name.validate(reportList);
    // メモ
    note.validate(reportList);
    return (reportList.size() == 0);
  }

  /**
   * トピックをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param reportList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> reportList) {
    return false;
  }

  /**
   * 返信記事をデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param reportList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> reportList) throws ALPageNotFoundException,
      ALDBErrorException {
    try {
      String reportid = rundata.getParameters().getString("report_reply_id");

      // オブジェクトモデルを取得
      EipTReport report;

      if (this.hasAclDeletereportOthers) {
        report =
          ReportUtils.getEipTReportReply(rundata, context, reportid, true);
      } else {
        report =
          ReportUtils.getEipTReportReply(rundata, context, reportid, false);
      }
      if (report == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[ReportreportReplyFormData] Not found ID...");
        throw new ALPageNotFoundException();
      }

      List<String> fpaths = new ArrayList<String>();
      List<?> files = report.getEipTReportFiles();
      if (files != null && files.size() > 0) {
        int fsize = files.size();
        for (int i = 0; i < fsize; i++) {
          fpaths.add(((EipTReportFile) files.get(i)).getFilePath());
        }
      }

      // 返信記事を削除
      // 添付ファイルはカスケードで自動的に削除される．
      Database.delete(report);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        report.getReportId(),
        ALEventlogConstants.PORTLET_TYPE_REPORT,
        report.getReportName());

      if (fpaths.size() > 0) {
        // ローカルファイルに保存されているファイルを削除する．
        int fsize = fpaths.size();
        for (int i = 0; i < fsize; i++) {
          ALStorageService.deleteFile(ReportUtils.getSaveDirPath(orgId, uid)
            + fpaths.get(i));
        }
      }
    } catch (Exception e) {
      Database.rollback();
      logger.error("[ReportreportReplyFormData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * トピックをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param reportList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> reportList) throws ALPageNotFoundException,
      ALDBErrorException {
    try {
      // オブジェクトモデルを取得
      EipTReport parentreport =
        ReportUtils.getEipTReportParentReply(rundata, context, false);
      if (parentreport == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[ReportreportReplyFormData] Not found ID...");
        throw new ALPageNotFoundException();
      }
      Date updateDate = Calendar.getInstance().getTime();

      // 新規オブジェクトモデル
      EipTReport report = Database.create(EipTReport.class);

      // 報告書名
      report.setReportName(report_name.getValue());
      // ユーザーID
      report
        .setUserId(Integer.valueOf((int) login_user.getUserId().getValue()));
      // メモ
      report.setNote(note.getValue());
      // 作成日
      report.setCreateDate(updateDate);
      // 更新日
      report.setUpdateDate(Calendar.getInstance().getTime());
      // ユーザーID
      report.setTurbineUser(ALEipUtils.getTurbineUser(ALEipUtils
        .getUserId(rundata)));
      // 親トピック ID
      report.setParentId(parentreport.getReportId());

      // ファイルをデータベースに登録する．

      // 添付ファイルを登録する．
      insertAttachmentFiles(fileuploadList, folderName, uid, report, reportList);

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        report.getReportId(),
        ALEventlogConstants.PORTLET_TYPE_REPORT,
        report.getReportName());

      // 「更新情報」に表示させる。
      String loginName = login_user.getName().getValue();
      ReportUtils.createNewReportReplyActivity(parentreport, loginName);

      String recipient =
        ALEipUtils.getALEipUser(parentreport.getUserId()).getName().getValue();
      ReportUtils.createReportReplyActivity(parentreport, loginName, recipient);

      try {
        // メール送信
        List<ALEipUser> memberList = new ArrayList<ALEipUser>();
        memberList.add(ALEipUtils.getALEipUser(parentreport.getUserId()));
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(memberList, ALEipUtils
            .getUserId(rundata), false);

        String subject = "[" + ALOrgUtilsService.getAlias() + "]報告書への返信";
        String orgId = Database.getDomainName();

        // パソコン、携帯電話へメールを送信
        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          message.setPcSubject(subject);
          message.setPcBody(ReportUtils.createReplyMsgForPc(
            rundata,
            report,
            parentreport));
          message.setCellularSubject(subject);
          message.setCellularBody(ReportUtils.createReplyMsgForCellPhone(
            rundata,
            report,
            parentreport,
            destMember.getUserId()));
          messageList.add(message);
        }

        ALMailService.sendAdminMail(new ALAdminMailContext(orgId, ALEipUtils
          .getUserId(rundata), messageList, ALMailUtils
          .getSendDestType(ALMailUtils.KEY_MSGTYPE_REPORT)));

      } catch (Exception ex) {
        logger.error("Exception", ex);
        return false;
      }

      // 添付ファイル保存先のフォルダを削除
      ALStorageService.deleteTmpFolder(uid, folderName);
    } catch (Exception e) {
      logger.error("[ReportreportReplyFormData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * データベースに格納されているトピックを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param reportList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> reportList) {
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
  @Override
  public boolean doInsert(ALAction action, RunData rundata, Context context) {
    try {
      if (!doCheckSecurity(rundata, context)) {
        return false;
      }
      init(action, rundata, context);

      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_INSERT);

      action.setMode(ALEipConstants.MODE_INSERT);
      List<String> reportList = new ArrayList<String>();
      setValidator();
      boolean res =
        (setFormData(rundata, context, reportList) && validate(reportList) && insertFormData(
          rundata,
          context,
          reportList));
      if (!res) {
        action.setMode(ALEipConstants.MODE_NEW_FORM);
        setMode(action.getMode());
      }
      if (action instanceof ReportFormJSONScreen) {
        action.setResultData(this);
        action.addErrorMessages(reportList);
        action.putData(rundata, context);
      } else {
        ReportAction reportAction = (ReportAction) action;
        reportAction.setResultDataOnReportDetail(this);
        reportAction.addErrorMessagesOnReportDetail(reportList);
        reportAction.putDataOnReportDetail(rundata, context);
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
  @Override
  public boolean doViewForm(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);

      // doCheckAclPermission(rundata, context,
      // ALAccessControlConstants.VALUE_ACL_INSERT);

      action.setMode("reply");
      // mode = action.getMode();
      List<String> reportList = new ArrayList<String>();
      boolean res = setFormData(rundata, context, reportList);
      if (action instanceof ReportDetailScreen) {
        ReportDetailScreen reportAction = (ReportDetailScreen) action;
        reportAction.setResultDataOnReportDetail(this);
        reportAction.addErrorMessagesOnReportDetail(reportList);
        reportAction.putDataOnReportDetail(rundata, context);
      } else {
        ReportAction reportAction = (ReportAction) action;
        reportAction.setResultDataOnReportDetail(this);
        reportAction.addErrorMessagesOnReportDetail(reportList);
        reportAction.putDataOnReportDetail(rundata, context);
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
   * @param rundata
   * @param context
   * @param reportList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> reportList) throws ALPageNotFoundException,
      ALDBErrorException {

    boolean res = super.setFormData(rundata, context, reportList);

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
  public ALStringField getReportName() {
    return report_name;
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
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    // if (aclPortletFeature == null || "".equals(aclPortletFeature)) {
    // return ALAccessControlConstants.POERTLET_FEATURE_REPORT_REPLY;
    // } else {
    // return aclPortletFeature;
    // }
    return aclPortletFeature;
  }

  /**
   * 他ユーザのトピックを編集する権限があるかどうかを返します。
   * 
   * @return
   */
  private boolean insertAttachmentFiles(
      List<FileuploadLiteBean> fileuploadList, String folderName, int uid,
      EipTReport entry, List<String> msgList) {

    if (fileuploadList == null || fileuploadList.size() <= 0) {
      return true;
    }

    try {
      int length = fileuploadList.size();
      ArrayList<FileuploadLiteBean> newfilebeans =
        new ArrayList<FileuploadLiteBean>();
      FileuploadLiteBean filebean = null;
      for (int i = 0; i < length; i++) {
        filebean = fileuploadList.get(i);
        if (filebean.isNewFile()) {
          newfilebeans.add(filebean);
        }
      }
      int newfilebeansSize = newfilebeans.size();
      if (newfilebeansSize > 0) {
        FileuploadLiteBean newfilebean = null;
        for (int j = 0; j < length; j++) {
          newfilebean = newfilebeans.get(j);
          // サムネイル処理
          String[] acceptExts = ImageIO.getWriterFormatNames();
          byte[] fileThumbnail =
            FileuploadUtils.getBytesShrinkFilebean(
              orgId,
              folderName,
              uid,
              newfilebean,
              acceptExts,
              FileuploadUtils.DEF_THUMBNAIL_WIDTH,
              FileuploadUtils.DEF_THUMBNAIL_HEIGTH,
              msgList);

          String filename = j + "_" + String.valueOf(System.nanoTime());

          // 新規オブジェクトモデル
          EipTReportFile file = Database.create(EipTReportFile.class);
          file.setOwnerId(Integer.valueOf(uid));
          file.setFileName(newfilebean.getFileName());
          file.setFilePath(ReportUtils.getRelativePath(filename));
          if (fileThumbnail != null) {
            file.setFileThumbnail(fileThumbnail);
          }
          file.setEipTReport(entry);
          file.setCreateDate(Calendar.getInstance().getTime());
          file.setUpdateDate(Calendar.getInstance().getTime());

          // ファイルの移動
          ALStorageService.copyTmpFile(
            uid,
            folderName,
            String.valueOf(newfilebean.getFileId()),
            ReportUtils.FOLDER_FILEDIR_REPORT,
            ReportUtils.CATEGORY_KEY + ALStorageService.separator() + uid,
            filename);
        }

        // 添付ファイル保存先のフォルダを削除
        ALStorageService.deleteTmpFolder(uid, folderName);
      }

    } catch (Exception e) {
      logger.error(e);
    }
    return true;
  }

  public boolean hasAclUpdatereportOthers() {
    return hasAclUpdatereportOthers;
  }

  /**
   * 他ユーザのトピックを削除する権限があるかどうかを返します。
   * 
   * @return
   */
  public boolean hasAclDeletereportOthers() {
    return hasAclDeletereportOthers;
  }
}
