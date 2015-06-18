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
package com.aimluck.eip.webmail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALLocalMailMessage;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALMailSenderContext;
import com.aimluck.eip.mail.ALSmtpMailContext;
import com.aimluck.eip.mail.ALSmtpMailSender;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.portal.ALPortalApplicationService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.webmail.beans.WebmailAccountLiteBean;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * Webメールフォームデータを管理するためのクラスです。 <br />
 */
public class WebMailFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFormData.class.getName());

  /** 件名の最大文字数 */
  private final int FIELD_SUBJECT_MAX_LEN = 256;

  /** 宛先（To，CC，BCC）の最大文字数 */
  private final int FIELD_RECIPIENT_MAX_LEN = 512;

  /** メール本文の最大文字数 */
  private final int FIELD_BODY_MAX_LEN = 50000;

  /** タイプ：デフォルトメール */
  public static final int TYPE_DEF_MAIL = -1;

  /** タイプ：新規メール */
  public static final int TYPE_NEW_MAIL = 0;

  /** タイプ：返信メール */
  public static final int TYPE_REPLY_MAIL = 1;

  /** タイプ：転送メール */
  public static final int TYPE_FORWARD_MAIL = 2;

  /** タイプ：全員に返信メール */
  public static final int TYPE_REPLY_ALL_MAIL = 3;

  /** メール作成のタイプ */
  private ALNumberField mailType = null;

  /** 宛名 */
  private ALStringField to = null;

  /** CC */
  private ALStringField cc = null;

  /** BCC */
  private ALStringField bcc = null;

  /** 件名 */
  private ALStringField subject = null;

  /** 本文 */
  private ALStringField body = null;

  /** 添付ファイルリスト */
  private List<FileuploadLiteBean> fileuploadList = null;

  private String folderName = null;

  /**  */
  private int userId = -1;

  /**  */
  private int accountId = -1;

  /** メールアカウント一覧 */
  private List<WebmailAccountLiteBean> mailAccountList = null;

  private String orgId;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);
    try {
      accountId =
        Integer.parseInt(ALEipUtils.getTemp(
          rundata,
          context,
          WebMailUtils.ACCOUNT_ID));
    } catch (Exception e) {
      accountId = 0;
    }

    folderName = rundata.getParameters().getString("folderName");

    orgId = Database.getDomainName();

    mailAccountList = WebMailUtils.getMailAccountList(rundata, context);
  }

  /**
   *
   */
  @Override
  public void initField() {
    // メール作成のタイプ
    mailType = new ALNumberField();
    mailType.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_SETFIELDNAME_TYPE"));

    // To
    to = new ALStringField();
    to.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_SETFIELDNAME_TO"));
    to.setTrim(true);

    // CC
    cc = new ALStringField();
    cc.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_SETFIELDNAME_CC"));
    cc.setTrim(true);

    // BCC
    bcc = new ALStringField();
    bcc.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_SETFIELDNAME_BCC"));
    bcc.setTrim(true);

    // Subject
    subject = new ALStringField();
    subject.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_SETFIELDNAME_SUBJECT"));
    subject.setTrim(true);

    // Body
    body = new ALStringField();
    body.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_SETFIELDNAME_BODY"));
    body.setTrim(false);

    fileuploadList = new ArrayList<FileuploadLiteBean>();
  }

  /**
   * 各フィールドに対する制約条件を設定する抽象メソッドです。
   */
  @Override
  protected void setValidator() {
    // 宛先を必須項目にする
    to.setNotNull(true);
    to.limitMaxLength(FIELD_RECIPIENT_MAX_LEN);

    // CC
    cc.limitMaxLength(FIELD_RECIPIENT_MAX_LEN);

    // BCC
    bcc.limitMaxLength(FIELD_RECIPIENT_MAX_LEN);

    // 件名の文字数制限
    subject.limitMaxLength(FIELD_SUBJECT_MAX_LEN);

    // 本文の文字数制限
    body.limitMaxLength(FIELD_BODY_MAX_LEN);

  }

  /**
   * フォームデータの妥当性を検証する．
   * 
   * @param msgList
   *          エラーメッセージのリスト
   */
  @Override
  public boolean validate(List<String> msgList) {
    String delim = ",;";
    if (to.validate(msgList)
      && !WebMailUtils.checkAddress(to.getValue(), delim)) {
      msgList.add("『 <span class='em'>宛先</span> 』を正しく入力してください。");
    }
    if (cc.validate(msgList)
      && cc.getValue().trim().length() > 0
      && !WebMailUtils.checkAddress(cc.getValue(), delim)) {
      msgList.add("『 <span class='em'>CC</span> 』を正しく入力してください。");
    }
    if (bcc.validate(msgList)
      && bcc.getValue().trim().length() > 0
      && !WebMailUtils.checkAddress(bcc.getValue(), delim)) {
      msgList.add("『 <span class='em'>BCC</span> 』を正しく入力してください。");
    }
    subject.validate(msgList);
    body.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      int index;
      try {
        index =
          Integer.parseInt(ALEipUtils.getTemp(
            rundata,
            context,
            ALEipConstants.ENTITY_ID));
      } catch (Exception e) {
        return false;
      }

      String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
      int type_mail =
        (WebMailUtils.TAB_RECEIVE.equals(currentTab))
          ? ALFolder.TYPE_RECEIVE
          : ALFolder.TYPE_SEND;
      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      ALFolder folder =
        handler.getALFolder(type_mail, orgId, userId, Integer
          .valueOf(accountId));
      folder.deleteMail(index);

    } catch (Exception e) {
      logger.error("webmail", e);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {

    String[] attachmentFilepaths = null;
    try {
      FileuploadLiteBean filebean = null;
      boolean hasAttachments =
        (fileuploadList != null && fileuploadList.size() > 0);
      if (hasAttachments) {
        int size = fileuploadList.size();
        attachmentFilepaths = new String[size];
        for (int i = 0; i < size; i++) {
          filebean = fileuploadList.get(i);
          attachmentFilepaths[i] =
            ALStorageService.getDocumentPath(
              FileuploadUtils.FOLDER_TMP_FOR_ATTACHMENT_FILES,
              userId + ALStorageService.separator() + folderName)
              + ALStorageService.separator()
              + filebean.getFileId();
        }
      }

      // 件名の値を検証
      if (subject.getValue() == null || subject.getValue().equals("")) {
        subject.setValue("無題");
      }

      // 返信メールの場合は，ヘッダを追加する．
      Map<String, String> map = null;
      if (getMailType().getValue() == TYPE_REPLY_MAIL
        || getMailType().getValue() == TYPE_REPLY_ALL_MAIL) {
        ALLocalMailMessage msg = null;
        try {
          msg =
            (ALLocalMailMessage) WebMailUtils.getSelectedLocalMailMessage(
              rundata,
              context,
              (int) getMailType().getValue());
          if (msg == null) {
            return false;
          }
        } catch (Exception e) {
          return false;
        }
        String in_reply_tos = msg.getMessageID();
        StringBuffer reference = new StringBuffer();
        String[] references = msg.getHeader("References");
        map = new LinkedHashMap<String, String>();
        if (references != null && references.length > 0) {
          reference.append(ALMailUtils.getOneString(references, " "));
        }
        if (in_reply_tos != null && (!in_reply_tos.equals(""))) {
          map.put("In-Reply-To", in_reply_tos);
          reference.append(" ").append(in_reply_tos);
        }

        map.put("References", reference.toString());

      }

      if (map != null && map.size() == 0) {
        map = null;
      }

      String delim = ",;";

      // オブジェクトモデルを取得
      EipMMailAccount account = ALMailUtils.getMailAccount(userId, accountId);

      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      // 送信サーバ情報
      ALMailSenderContext scontext =
        ALMailUtils.getALSmtpMailSenderContext(orgId, account);

      // 送信メッセージのコンテキスト
      ALSmtpMailContext mailcontext =
        ALMailUtils.getALSmtpMailContext(ALMailUtils.getTokens(ALStringUtil
          .unsanitizing(to.getValue()), delim), ALMailUtils.getTokens(
          ALStringUtil.unsanitizing(cc.getValue()),
          delim), ALMailUtils.getTokens(ALStringUtil.unsanitizing(bcc
          .getValue()), delim), account.getMailAddress(), ALStringUtil
          .unsanitizing(account.getMailUserName()), ALStringUtil
          .unsanitizing(subject.getValue()), ALStringUtil.unsanitizing(body
          .getValue()), attachmentFilepaths, map);

      int success_send = handler.send(scontext, mailcontext);

      if (success_send == ALSmtpMailSender.SEND_MSG_SUCCESS) {
        if (hasAttachments) {
          // 添付ファイル保存先のフォルダを削除
          ALStorageService.deleteTmpFolder(userId, folderName);
        }
      } else {
        if (success_send == ALSmtpMailSender.SEND_MSG_FAIL) {
          msgList.add("メールを送信できませんでした。アカウント設定が間違っている可能性があります。");
        } else if (success_send == ALSmtpMailSender.SEND_MSG_OVER_MAIL_MAX_SIZE) {
          msgList.add(String.valueOf(FileuploadUtils.getMaxFileSize()).concat(
            "MB を超えるサイズのメールは送信できません。"));
        } else if (success_send == ALSmtpMailSender.SEND_MSG_FAIL_SMTP_AUTH) {
          msgList.add("メールを送信できませんでした。SMTP認証の認証に失敗しました。");
        }

        return false;
      }
    } catch (Exception e) {
      logger.error("webmail", e);
      msgList.add("メールを送信できませんでした。アカウント設定が間違っている可能性があります。");
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {

    mailType.setValue(rundata.getParameters().getInt(
      WebMailUtils.MAIL_TYPE,
      TYPE_NEW_MAIL));

    try {
      ALLocalMailMessage msg =
        (ALLocalMailMessage) WebMailUtils.getSelectedLocalMailMessage(
          rundata,
          context,
          (int) getMailType().getValue());
      if (msg == null) {
        return false;
      }

      String tmpSubject = null;
      if (getMailType().getValue() == TYPE_NEW_MAIL) {
        // unreachable flow
        // TO
        Address[] tos = msg.getRecipients(Message.RecipientType.TO);
        this.setTo(ALMailUtils.getAddressString(tos));

        // CC
        Address[] ccs = msg.getRecipients(Message.RecipientType.CC);
        this.setCc(ALMailUtils.getAddressString(ccs));
        // BCC
        Address[] bccs = msg.getRecipients(Message.RecipientType.BCC);
        this.setBcc(ALMailUtils.getAddressString(bccs));

        tmpSubject = msg.getSubject();
      } else if (getMailType().getValue() == TYPE_REPLY_MAIL) {
        // TO
        this.setTo(ALMailUtils.getFromDelegateExtract(msg));
        tmpSubject = "Re: " + msg.getSubject();
      } else if (getMailType().getValue() == TYPE_FORWARD_MAIL) {
        tmpSubject = "Fwd: " + msg.getSubject();
      } else if (getMailType().getValue() == TYPE_REPLY_ALL_MAIL) {
        // TO
        Address[] from = ALMailUtils.getFromDelegateExtractForAddress(msg);
        Address[] to = msg.getRecipients(Message.RecipientType.TO, false);

        EipMMailAccount myaccount =
          ALMailUtils.getMailAccount(userId, accountId);
        String myaddress = myaccount.getMailAddress();

        List<Address> allList = new ArrayList<Address>();
        allList.addAll(Arrays.asList(from));
        allList.addAll(Arrays.asList(to));
        List<Address> replayList = new ArrayList<Address>();
        for (Address address : allList) {
          if (address instanceof InternetAddress) {
            InternetAddress internetAddress = (InternetAddress) address;
            String email = internetAddress.getAddress();
            if (email != null
              && !email.equalsIgnoreCase(myaddress)
              && !email.contains("<" + myaddress + ">")) {
              replayList.add(address);
            }
          }
        }
        this.setTo(ALMailUtils.getAddressString(replayList
          .toArray(new Address[replayList.size()])));

        // CC
        Address[] ccs = msg.getRecipients(Message.RecipientType.CC, false);
        this.setCc(ALMailUtils.getAddressString(ccs));

        // BCC
        Address[] bccs = msg.getRecipients(Message.RecipientType.BCC);
        this.setBcc(ALMailUtils.getAddressString(bccs));

        tmpSubject = "Re: " + msg.getSubject();
      }

      // Subject
      this.setSubject(tmpSubject);

      // 返信の原文付与
      String[] tmp2 = msg.getBodyTextArray();

      StringBuffer replies = new StringBuffer();
      replies.append("\r\n\r\n\r\n" + "------Original Message-------\r\n");
      if (tmp2 != null) {
        for (String factor : tmp2) {
          replies.append("> " + factor + "\r\n");
        }
      }

      // Body
      try {
        // オブジェクトモデルを取得
        EipMMailAccount account = ALMailUtils.getMailAccount(userId, accountId);
        // 署名と返信とを本文に追加
        if (account.getSignature() != null
          && !"".equals(account.getSignature())) {
          body.setValue(replies + "\r\n\r\n\r\n" + account.getSignature());
        } else {
          body.setValue(replies.toString());
        }
      } catch (Exception ex) {
        logger.error("webmail", ex);
      }

      if (getMailType().getValue() == TYPE_FORWARD_MAIL) {
        msg =
          (ALLocalMailMessage) WebMailUtils.getSelectedLocalMailMessage(
            rundata,
            context,
            TYPE_NEW_MAIL);
        if (msg == null) {
          return false;
        }
        String[] filenames = msg.getAttachmentFileNameArray();
        if (filenames != null && filenames.length > 0) {
          /** 添付ファイルを含んだメールを転送する */
          if (folderName == null || folderName.equals("")) {
            folderName = "undefined";
          }

          for (int i = 0; i < filenames.length; i++) {
            /** 各々の添付ファイルを、一度ファイルに書き出して再度添付する */
            int fileId = Long.valueOf(System.nanoTime()).intValue();
            String newAttachmentFileName = String.valueOf(fileId);
            String realfilename = filenames[i];

            if (realfilename == null) {
              continue;
            }

            ALStorageService.createNewTmpFile(
              msg.getInputStream(i),
              userId,
              folderName,
              newAttachmentFileName,
              realfilename);

            FileuploadLiteBean filebean = new FileuploadLiteBean();
            filebean.initField();
            filebean.setFileId(fileId);
            filebean.setFileName(realfilename);
            filebean.setFolderName(folderName);
            fileuploadList.add(filebean);
          }
        }
      }
      return true;
    } catch (Exception e) {
      logger.error("webmail", e);
      return false;
    }
  }

  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);

    if (accountId <= 0 || userId <= 0) {
      return res;
    }
    try {
      fileuploadList = FileuploadUtils.getFileuploadList(rundata);
      // Body
      // オブジェクトモデルを取得
      EipMMailAccount account = ALMailUtils.getMailAccount(userId, accountId);
      // 署名を本文に追加
      if (!ALEipConstants.MODE_INSERT.equals(rundata.getParameters().get(
        ALEipConstants.MODE))
        && account.getSignature() != null
        && !"".equals(account.getSignature())) {
        StringBuffer bodybuf = new StringBuffer();
        if (body.getValue() != null) {
          bodybuf.append(body.getValue());
        }
        bodybuf.append("\r\n\r\n\r\n");
        bodybuf.append(account.getSignature());
        body.setValue(bodybuf.toString());
      }
    } catch (Exception ex) {
      logger.error("webmail", ex);
    }

    return res;
  }

  /**
   * @return
   */
  public ALStringField getBcc() {
    return bcc;
  }

  /**
   * @return
   */
  public ALStringField getBody() {
    return body;
  }

  /**
   * @return
   */
  public ALStringField getCc() {
    return cc;
  }

  /**
   * @return
   */
  public ALStringField getSubject() {
    return subject;
  }

  /**
   * @return
   */
  public ALStringField getTo() {
    return to;
  }

  /**
   * @return
   */
  public ALNumberField getMailType() {
    return mailType;
  }

  public int getMaxFileSize() {
    return FileuploadUtils.getMaxFileSize();
  }

  /**
   * @param field
   */
  public void setMailType(int field) {
    mailType.setValue(field);
  }

  /**
   * @param field
   */
  public void setBcc(String string) {
    bcc.setValue(string);
  }

  /**
   * @param field
   */
  public void setBody(String string) {
    body.setValue(string);
  }

  /**
   * @param field
   */
  public void setCc(String string) {
    cc.setValue(string);
  }

  /**
   * @param field
   */
  public void setSubject(String string) {
    subject.setValue(string);
  }

  /**
   * @param field
   */
  public void setTo(String string) {
    to.setValue(string);
  }

  public String getAccountName() {
    return ALMailUtils.getAccountName(userId, accountId);
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    return fileuploadList;
  }

  public boolean hasAuthorityAddressBook() {
    return ALPortalApplicationService.isActive("AddressBook");
  }

  public String getFolderName() {
    return folderName;
  }

  public int getAccountId() {
    return accountId;
  }

  public String getAddrForCell(ALStringField addrs_filed) {
    if (addrs_filed == null) {
      return "";
    }

    String addrs = addrs_filed.getValue();

    if (addrs == null || addrs.length() == 0) {
      return "";
    }

    StringBuffer addrbuf = new StringBuffer();
    int count = 0;
    String token = null;
    StringTokenizer st = new StringTokenizer(addrs, ",");
    int size = st.countTokens();
    for (int i = 0; i < size; i++) {
      token = st.nextToken();
      if (token.indexOf("<") == -1) {
        addrbuf.append(token);
      } else {
        StringTokenizer tmp_st = new StringTokenizer(token, "<>");
        if (tmp_st.countTokens() == 2) {
          tmp_st.nextToken();
          addrbuf.append(tmp_st.nextToken());
        }
      }
      count = count + 1;
      if (count < size) {
        addrbuf.append(",");
      }
    }
    return addrbuf.toString();
  }

  public boolean isExistsAccount() {
    if (mailAccountList != null && mailAccountList.size() > 0) {
      return true;
    } else {
      return false;
    }
  }
}
