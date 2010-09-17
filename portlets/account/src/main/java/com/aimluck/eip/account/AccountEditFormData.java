/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

package com.aimluck.eip.account;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.datasync.ALDataSyncFactoryService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントのフォームデータを管理するためのクラスです。 <br />
 */
public class AccountEditFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountEditFormData.class.getName());

  /** 名前（名） */
  private ALStringField firstname;

  /** 名前（姓） */
  private ALStringField lastname;

  /** メールアドレス */
  private ALStringField email;

  /** 電話番号（内線） */
  private ALStringField in_telephone;

  /** 電話番号 */
  private ALStringField out_telephone1;

  /** 電話番号 */
  private ALStringField out_telephone2;

  /** 電話番号 */
  private ALStringField out_telephone3;

  /** 電話番号（携帯） */
  private ALStringField cellular_phone1;

  /** 電話番号（携帯） */
  private ALStringField cellular_phone2;

  /** 電話番号（携帯） */
  private ALStringField cellular_phone3;

  /** 携帯メールアドレス */
  private ALStringField cellular_mail;

  /** フリガナ（名） */
  private ALStringField first_name_kana;

  /** フリガナ（姓） */
  private ALStringField last_name_kana;

  /** 顔写真 */
  private ALStringField photo = null;

  /** 添付ファイル */
  private FileuploadLiteBean filebean = null;

  /** 添付フォルダ名 */
  private String folderName = null;

  private String org_id;

  /** 顔写真データ */
  private byte[] facePhoto;

  /**
   * 初期化処理を行います。 <BR>
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    if (ALEipUtils.isMatch(rundata, context)) {
      ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, rundata
        .getUser()
        .getUserName());
    }

    folderName = rundata.getParameters().getString("folderName");

    org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
  }

  /**
   *
   *
   */
  public void initField() {
    // 名
    firstname = new ALStringField();
    firstname.setFieldName("名前（名）");
    firstname.setTrim(true);
    // 姓
    lastname = new ALStringField();
    lastname.setFieldName("名前（姓）");
    lastname.setTrim(true);
    // メールアドレス
    email = new ALStringField();
    email.setFieldName("メールアドレス");
    email.setTrim(true);
    // 内線番号
    in_telephone = new ALStringField();
    in_telephone.setFieldName("電話番号（内線）");
    in_telephone.setTrim(true);
    // 外線番号
    out_telephone1 = new ALStringField();
    out_telephone1.setFieldName("電話番号（外線）");
    out_telephone1.setTrim(true);
    out_telephone2 = new ALStringField();
    out_telephone2.setFieldName("電話番号（外線）");
    out_telephone2.setTrim(true);
    out_telephone3 = new ALStringField();
    out_telephone3.setFieldName("電話番号（外線）");
    out_telephone3.setTrim(true);

    // 携帯番号
    cellular_phone1 = new ALStringField();
    cellular_phone1.setFieldName("電話番号（携帯）");
    cellular_phone1.setTrim(true);
    cellular_phone2 = new ALStringField();
    cellular_phone2.setFieldName("電話番号（携帯）");
    cellular_phone2.setTrim(true);
    cellular_phone3 = new ALStringField();
    cellular_phone3.setFieldName("電話番号（携帯）");
    cellular_phone3.setTrim(true);
    // 携帯アドレス
    cellular_mail = new ALStringField();
    cellular_mail.setFieldName("携帯メールアドレス");
    cellular_mail.setTrim(true);
    // 名（フリガナ）
    first_name_kana = new ALStringField();
    first_name_kana.setFieldName("フリガナ（名）");
    first_name_kana.setTrim(true);
    // 姓（フリガナ）
    last_name_kana = new ALStringField();
    last_name_kana.setFieldName("フリガナ（姓）");
    last_name_kana.setTrim(true);

    // 顔写真
    photo = new ALStringField();
    photo.setFieldName("顔写真");
    photo.setTrim(true);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);
    try {
      if (res) {
        List<FileuploadLiteBean> fileBeanList =
          FileuploadUtils.getFileuploadList(rundata);
        if (fileBeanList != null && fileBeanList.size() > 0) {
          filebean = fileBeanList.get(0);
          if (filebean.getFileId() > 0) {
            // 顔写真をセットする．
            String[] acceptExts = ImageIO.getWriterFormatNames();
            facePhoto =
              FileuploadUtils.getBytesShrinkFilebean(
                org_id,
                folderName,
                ALEipUtils.getUserId(rundata),
                filebean,
                acceptExts,
                FileuploadUtils.DEF_THUMBNAIL_WIDTH,
                FileuploadUtils.DEF_THUMBNAIL_HEIGTH,
                msgList);
          } else {
            facePhoto = null;
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   *
   *
   */
  @Override
  protected void setValidator() {
    // 名
    firstname.setNotNull(true);
    firstname.limitMaxLength(50);
    // 姓
    lastname.setNotNull(true);
    lastname.limitMaxLength(50);
    // 名（フリガナ）
    first_name_kana.setNotNull(true);
    first_name_kana.limitMaxLength(50);
    // 姓（フリガナ）
    last_name_kana.setNotNull(true);
    last_name_kana.limitMaxLength(50);

    // 内線
    in_telephone.setCharacterType(ALStringField.TYPE_ASCII);
    in_telephone.limitMaxLength(13);
    // メールアドレス
    email.setCharacterType(ALStringField.TYPE_ASCII);

    // 外線
    out_telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
    out_telephone1.limitMaxLength(5);
    out_telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
    out_telephone2.limitMaxLength(4);
    out_telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
    out_telephone3.limitMaxLength(4);

    // 携帯
    cellular_phone1.setCharacterType(ALStringField.TYPE_NUMBER);
    cellular_phone1.limitMaxLength(5);
    cellular_phone2.setCharacterType(ALStringField.TYPE_NUMBER);
    cellular_phone2.limitMaxLength(4);
    cellular_phone3.setCharacterType(ALStringField.TYPE_NUMBER);
    cellular_phone3.limitMaxLength(4);
    // 携帯メール
    cellular_mail.setCharacterType(ALStringField.TYPE_ASCII);
    cellular_mail.limitMaxLength(50);
  }

  /**
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();
    firstname.validate(msgList);
    lastname.validate(msgList);

    // 名前(フリガナ)をカタカナへと変換します
    first_name_kana.setValue(ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(first_name_kana.toString())));
    last_name_kana.setValue(ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(last_name_kana.toString())));
    first_name_kana.validate(msgList);
    last_name_kana.validate(msgList);

    // 内線
    in_telephone.validate(msgList);
    // ハイフン以外の記号とアルファベットの入力をはじきます
    Pattern pattern = Pattern.compile(".*[^-0-9]+.*");
    Matcher matcher = pattern.matcher(in_telephone.getValue());
    Boolean ext_validater = matcher.matches();
    if (ext_validater) {
      msgList.add("電話番号（内線）は 15 文字以下でハイフン（-）または半角数字で入力してください。");
    }

    // メールアドレス
    email.validate(msgList);
    if (email.getValue() != null
      && email.getValue().trim().length() > 0
      && !ALStringUtil.isMailAddress(email.getValue())) {
      msgList.add("『 <span class='em'> メールアドレス </span>』を正しく入力してください。");
    }

    // 携帯メールアドレス
    cellular_mail.validate(msgList);
    if (cellular_mail.getValue().trim().length() > 0
      && !ALStringUtil.isCellPhoneMailAddress(cellular_mail.getValue())) {
      msgList.add("『 <span class='em'> 携帯メールアドレス </span>』を正しく入力してください。");
    }

    if (!out_telephone1.getValue().equals("")
      || !out_telephone2.getValue().equals("")
      || !out_telephone3.getValue().equals("")) {
      if (!out_telephone1.validate(dummy)
        || !out_telephone2.validate(dummy)
        || !out_telephone3.validate(dummy)) {
        msgList.add("『 <span class='em'>電話番号（外線）</span> 』を正しく入力してください。");
      }
    }

    if (!cellular_phone1.getValue().equals("")
      || !cellular_phone2.getValue().equals("")
      || !cellular_phone3.getValue().equals("")) {
      if (!cellular_phone1.validate(dummy)
        || !cellular_phone2.validate(dummy)
        || !cellular_phone3.validate(dummy)) {
        msgList.add("『 <span class='em'>電話番号（携帯）</span> 』を正しく入力してください。");
      }
    }

    // 顔写真
    if (filebean != null && filebean.getFileId() > 0 && facePhoto == null) {
      msgList.add("『 <span class='em'>顔写真</span> 』にはJpeg画像を指定してください。");
    }

    return (msgList.size() == 0);
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      ALBaseUser user = (ALBaseUser) rundata.getUser();
      if (user == null) {
        logger
          .debug("Not found. (" + AccountEditFormData.class.getName() + ")");
        return false;
      }
      firstname.setValue(user.getFirstName());
      lastname.setValue(user.getLastName());
      email.setValue(user.getEmail());
      // 電話番号（内線）
      in_telephone.setValue(user.getInTelephone());
      // 電話番号（外線）
      StringTokenizer token;
      if (user.getOutTelephone() != null) {
        token = new StringTokenizer(user.getOutTelephone(), "-");
        if (token.countTokens() == 3) {
          out_telephone1.setValue(token.nextToken());
          out_telephone2.setValue(token.nextToken());
          out_telephone3.setValue(token.nextToken());
        }
      }
      // 電話番号（携帯）
      if (user.getCellularPhone() != null) {
        token = new StringTokenizer(user.getCellularPhone(), "-");
        if (token.countTokens() == 3) {
          cellular_phone1.setValue(token.nextToken());
          cellular_phone2.setValue(token.nextToken());
          cellular_phone3.setValue(token.nextToken());
        }
      }
      cellular_mail.setValue(user.getCellularMail());
      first_name_kana.setValue(user.getFirstNameKana());
      last_name_kana.setValue(user.getLastNameKana());

      if (user.getPhoto() != null) {
        filebean = new FileuploadLiteBean();
        filebean.initField();
        filebean.setFolderName("");
        filebean.setFileId(0);
        filebean.setFileName("以前の写真ファイル");
      }

      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * メールアドレスを設定します。
   * 
   * @return
   */
  public void loadEmail(RunData rundata) {
    try {
      ALBaseUser user = (ALBaseUser) rundata.getUser();
      if (user == null) {
        logger
          .debug("Not found. (" + AccountEditFormData.class.getName() + ")");
        email.setValue("");
      } else {
        email.setValue(user.getEmail());
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /**
   * 未使用。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 編集者自身を示すオブジェクト
      ALBaseUser user = (ALBaseUser) rundata.getUser();
      if (user == null) {
        return false;
      }

      // WebAPIのDBへ接続できるか確認
      if (!ALDataSyncFactoryService
        .getInstance()
        .getDataSyncHandler()
        .checkConnect()) {
        msgList.add("コントロールパネルWebAPIのデータベースの接続に失敗したため、処理は実行されませんでした。");
        return false;
      }

      user.setLastAccessDate();

      user.setFirstName(firstname.getValue());
      user.setLastName(lastname.getValue());
      user.setInTelephone(in_telephone.getValue());
      if (!out_telephone1.getValue().equals("")
        && !out_telephone2.getValue().equals("")
        && !out_telephone3.getValue().equals("")) {
        user.setOutTelephone(new StringBuffer().append(
          out_telephone1.getValue()).append("-").append(
          out_telephone2.getValue()).append("-").append(
          out_telephone3.getValue()).toString());
      } else {
        user.setOutTelephone("");
      }

      if (!cellular_phone1.getValue().equals("")
        && !cellular_phone2.getValue().equals("")
        && !cellular_phone3.getValue().equals("")) {
        user.setCellularPhone(new StringBuffer().append(
          cellular_phone1.getValue()).append("-").append(
          cellular_phone2.getValue()).append("-").append(
          cellular_phone3.getValue()).toString());
      } else {
        user.setCellularPhone("");
      }
      user.setCellularMail(cellular_mail.getValue());
      user.setFirstNameKana(first_name_kana.getValue());
      user.setLastNameKana(last_name_kana.getValue());
      user.setEmail(email.getValue());

      if (filebean != null) {
        if (filebean.getFileId() > 0) {
          // 顔写真を登録する．
          user.setPhoto(facePhoto);
        }
      } else {
        user.setPhoto(null);
      }

      // ユーザーを更新
      JetspeedSecurity.saveUser(user);

      // WebAPIとのDB同期
      if (!ALDataSyncFactoryService
        .getInstance()
        .getDataSyncHandler()
        .updateUser(user)) {
        return false;
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        Integer.valueOf(user.getUserId()),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNTPERSON,
        null);

      // 一時的な添付ファイルの削除
      File folder =
        FileuploadUtils.getFolder(
          org_id,
          ALEipUtils.getUserId(rundata),
          folderName);
      FileuploadUtils.deleteFolder(folder);

      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 添付ファイルを削除する．
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  public boolean doDeleteAttachments(ALAction action, RunData rundata,
      Context context, String mode) {
    try {
      init(action, rundata, context);
      // action.setMode(ALEipConstants.MODE_DELETE);
      action.setMode(mode);
      List<String> msgList = new ArrayList<String>();
      setValidator();
      boolean res =
        (setFormData(rundata, context, msgList) && deleteAttachments(
          rundata,
          context,
          msgList));
      action.setResultData(this);
      action.addErrorMessages(msgList);
      action.putData(rundata, context);
      return res;
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
   * @param msgList
   * @return
   */
  protected boolean deleteAttachments(RunData rundata, Context context,
      List<String> msgList) {
    if (rundata == null || context == null) {
      msgList.add("システム上の問題のため、削除できませんでした。");
      return false;
    }
    int userId = ALEipUtils.getUserId(rundata);

    List<FileuploadLiteBean> fileBeanList = new ArrayList<FileuploadLiteBean>();
    fileBeanList.add(filebean);
    return FileuploadUtils.deleteAttachments(
      org_id,
      userId,
      folderName,
      fileBeanList);
  }

  /**
   * 携帯メールアドレスを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCellularMail() {
    return cellular_mail;
  }

  /**
   * メールアドレスを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getEmail() {
    return email;
  }

  /**
   * フリガナ（名）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFirstNameKana() {
    return first_name_kana;
  }

  /**
   * 名前（名）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFirstName() {
    return firstname;
  }

  /**
   * 電話番号（内線）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getInTelephone() {
    return in_telephone;
  }

  /**
   * フリガナ（姓）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getLastNameKana() {
    return last_name_kana;
  }

  /**
   * 名前（姓）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getLastName() {
    return lastname;
  }

  /**
   * 携帯電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCellularPhone1() {
    return cellular_phone1;
  }

  /**
   * 携帯電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCellularPhone2() {
    return cellular_phone2;
  }

  /**
   * 携帯電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCellularPhone3() {
    return cellular_phone3;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getOutTelephone1() {
    return out_telephone1;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getOutTelephone2() {
    return out_telephone2;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getOutTelephone3() {
    return out_telephone3;
  }

  public String getFolderName() {
    return folderName;
  }

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    if (filebean == null) {
      return null;
    }
    List<FileuploadLiteBean> list = new ArrayList<FileuploadLiteBean>();
    list.add(filebean);
    return list;
  }
}
