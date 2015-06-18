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
package com.aimluck.eip.note;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 伝言メモフォームデータを管理するためのクラスです。
 */
public class NoteFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(NoteFormData.class.getName());

  /**
   * MSG_TYPE 1 : "伝言メモを送信しました。宛先ユーザーのパソコン用メールアドレスが未設定のため，メールを送信できませんでした。"
   * MSG_TYPE 2 : "伝言メモを送信しました。宛先ユーザーの携帯用メールアドレスが未設定のため，メールを送信できませんでした。"
   * MSG_TYPE 3 : "伝言メモを送信しました。宛先ユーザーのパソコン用と携帯用のメールアドレスが未設定のため，メールを送信できませんでした。"
   */

  /** メール送信時のメッセージ種別（伝言メモを送信．宛先ユーザのパソコン用メールアドレスが未設定でメール送信不可） */
  public final int MSG_TYPE_NON_ADDR_PC = 1;

  /** メール送信時のメッセージ種別（伝言メモを送信．宛先ユーザーの携帯用メールアドレスが未設定でメール送信不可） */
  public final int MSG_TYPE_NON_ADDR_CELL = 2;

  /** メール送信時のメッセージ種別（伝言メモを送信．宛先ユーザーのパソコン用と携帯用のメールアドレスが未設定でメール送信不可） */
  public final int MSG_TYPE_NON_ADDR_PC_CELL = 3;

  /** メール送信時のメッセージ種別（伝言メモの送信不可．宛先ユーザーが存在しないため，伝言メモを送信できませんでした） */
  public final int MSG_TYPE_NON_RECIPIENT = 4;

  /** メール送信時のメッセージ種別（伝言メモの送信不可．送信ユーザーのメールアカウントが未設定でメール送信不可） */
  public final int MSG_TYPE_NON_MAILACCOUNT = 5;

  /** 伝言メモ ID */
  private ALNumberField note_id;

  /** 送信元ユーザ ID（アカウント ID） */
  private ALStringField src_user_id;

  /** 宛先ユーザ ID（アカウント ID） */
  private ALStringField dest_user_id;

  /** 宛先ユーザ名 */
  private ALStringField dest_user_name;

  /** 宛先部署 ID */
  private ALNumberField dest_post_id;

  /** 依頼者名 */
  private ALStringField client_name;

  /** 依頼者所属 */
  private ALStringField company_name;

  /** 依頼者電話番号 */
  private ALStringField telephone1;

  /** 依頼者電話番号 */
  private ALStringField telephone2;

  /** 依頼者電話番号 */
  private ALStringField telephone3;

  /** 依頼者メールアドレス */
  private ALStringField email_address;

  /** 追加送信先タイプ（パソコンのメールアドレスに送信） */
  private ALStringField add_dest_type_pc;

  /** 追加送信先タイプ（携帯電話のメールアドレスに送信） */
  private ALStringField add_dest_type_cellphone;

  /** 用件タイプ */
  private ALStringField subject_type;

  /** 用件（カスタム） */
  private ALStringField custom_subject;

  /** 新着／未読／既読フラグ */
  private ALStringField note_stat;

  /** メモ */
  private ALStringField message;

  /** 受付日時 */
  private ALDateTimeField accept_date;

  /** 確認日時 */
  private ALDateTimeField confirm_date;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日時 */
  private ALDateTimeField update_date;

  /** 現在の年 */
  private int currentYear;

  /** 伝言メモの表示タイプ */
  private ALStringField view_type = null;

  private List<ALEipGroup> myGroupList = null;

  /** 表示対象の部署名 */
  private String target_group_name;

  private final int msg_type = 0;

  /** メンバーリスト */
  private List<ALEipUser> memberList;

  private ALEipUser loginUser;

  private String orgId;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    target_group_name = NoteUtils.getTargetGroupName(rundata, context);

    loginUser = ALEipUtils.getALEipUser(rundata);

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    for (ALEipGroup group : myGroups) {
      myGroupList.add(group);

    }

    orgId = Database.getDomainName();

    ParameterParser parser = rundata.getParameters();
    String dest_user_id_string = parser.getString("dest_user_id");
    if (dest_user_id_string != null) {
      dest_user_id.setValue(dest_user_id_string);
    } else {
      dest_user_id.setValue("");
    }
  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    // 宛先ユーザ (アカウント)ID
    dest_user_id.setNotNull(true);
    dest_user_id.limitMaxLength(50);

    // 宛先ユーザ名
    dest_user_name.setNotNull(true);
    dest_user_name.limitMaxLength(50);

    // 依頼者名
    client_name.setNotNull(true);
    client_name.limitMaxLength(50);

    // 依頼者所属
    company_name.setNotNull(false);
    company_name.limitMaxLength(50);

    // 依頼者電話番号
    telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone1.limitMaxLength(5);
    telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone2.limitMaxLength(4);
    telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone3.limitMaxLength(4);

    // 依頼者メールアドレス
    email_address.setNotNull(false);
    email_address.limitMaxLength(50);

    // 追加送信先タイプ
    add_dest_type_pc.setNotNull(false);
    add_dest_type_pc.limitMaxLength(1);
    add_dest_type_cellphone.setNotNull(false);
    add_dest_type_cellphone.limitMaxLength(1);

    // 用件タイプ
    subject_type.setNotNull(true);
    subject_type.limitMaxLength(1);

    // 用件（カスタム）
    custom_subject.setNotNull(true);
    custom_subject.limitMaxLength(50);

    // 新着／未読／既読フラグ
    note_stat.setNotNull(true);
    note_stat.limitMaxLength(1);

    // メモ
    message.setNotNull(false);
    message.limitMaxLength(1000);

    // 受付日時
    accept_date.setNotNull(true);

    // 確認日時
    confirm_date.setNotNull(true);
    confirm_date.setValue(Calendar.getInstance().getTime());
  }

  /**
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();

    dest_post_id.validate(msgList);
    dest_user_id.validate(msgList);

    if (dest_user_id.getValue() != null
      && dest_user_id.getValue().equals("all")) {
      // 選択されたグループにログインユーザ以外のユーザが登録されていない場合，エラーを表示する．
      if (memberList == null || memberList.size() <= 1) {
        msgList.add(ALLocalizationUtils.getl10n("NOTE_ALERT_NO_USER_IN_GROUP"));
      }
    } else {
      if (memberList == null || memberList.size() <= 1) {
        msgList.add(ALLocalizationUtils.getl10n("NOTE_ALERT_NO_COLLECT_USER"));
      }
    }

    // 依頼者名
    client_name.validate(msgList);
    // 依頼者所属
    company_name.validate(msgList);

    // 依頼者電話番号
    boolean emptyTelephone1 = telephone1.getValue().equals("");
    boolean emptyTelephone2 = telephone2.getValue().equals("");
    boolean emptyTelephone3 = telephone3.getValue().equals("");
    if (!emptyTelephone1 || !emptyTelephone2 || !emptyTelephone3) {
      if (!telephone1.validate(dummy)
        || !telephone2.validate(dummy)
        || !telephone3.validate(dummy)
        || emptyTelephone1
        || emptyTelephone2
        || emptyTelephone3) {
        msgList.add(ALLocalizationUtils
          .getl10n("NOTE_ALERT_NO_CLIENT_PHONE_NUMBER"));
      }
    }

    // 依頼者メールアドレス
    email_address.validate(msgList);
    if (email_address.getValue().trim().length() > 0
      && !ALStringUtil.isCellPhoneMailAddress(email_address.getValue())) {
      msgList.add(ALLocalizationUtils.getl10n("NOTE_ALERT_NO_CLIENT_EMEIL"));
    }

    // 追加送信先タイプ
    add_dest_type_pc.validate(msgList);
    add_dest_type_cellphone.validate(msgList);
    // 用件タイプ
    if ("0".equals(subject_type.getValue())) {
      custom_subject.validate(msgList);
    } else {
      subject_type.validate(msgList);
    }
    // メモ
    message.validate(msgList);
    // 受付日時
    accept_date.validate(msgList);

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
  protected boolean loadFormData(RunData rundata, Context context,
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
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {

    EipTNote note;

    // 追加送信先タイプ
    int add_dest_type_int = 0;

    try {
      Date nowDate = Calendar.getInstance().getTime();

      // 新規オブジェクトモデル
      note = Database.create(EipTNote.class);
      // 送信元ユーザ ID（アカウント ID）
      note.setOwnerId(Integer.toString(ALEipUtils.getUserId(rundata)));
      // 依頼者名
      note.setClientName(client_name.getValue());
      // 依頼者所属
      note.setCompanyName(company_name.getValue());
      // 依頼者電話番号
      if (!telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
        note.setTelephone(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
      } else {
        note.setTelephone("");
      }

      // 依頼者メールアドレス
      note.setEmailAddress(email_address.getValue());
      // 追加送信先タイプ
      // int add_dest_type_int = 0;
      String add_dest_type_pc_str = add_dest_type_pc.getValue();
      String add_dest_type_cellphone_str = add_dest_type_cellphone.getValue();
      if (add_dest_type_pc_str != null && !add_dest_type_pc_str.equals("")) {
        add_dest_type_int += 1;
      }
      if (add_dest_type_cellphone_str != null
        && !add_dest_type_cellphone_str.equals("")) {
        add_dest_type_int += 2;
      }
      note.setAddDestType(Integer.toString(add_dest_type_int));
      // 用件タイプ
      note.setSubjectType(subject_type.getValue());
      if ("0".equals(subject_type.getValue())) {
        note.setCustomSubject(custom_subject.getValue());
      }

      // メモ
      note.setMessage(message.getValue());
      // 受付日時
      note.setAcceptDate(accept_date.getValue());
      // 作成日
      note.setCreateDate(nowDate);
      // 更新日時
      note.setUpdateDate(nowDate);

      // MAP の登録
      if (memberList != null) {
        for (ALEipUser user : memberList) {
          saveNoteMap(
            rundata,
            note,
            user.getUserId().toString(),
            NoteUtils.NOTE_STAT_NEW);
        }
      }

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        note.getNoteId(),
        ALEventlogConstants.PORTLET_TYPE_NOTE,
        NoteUtils.getNoteSubject(note));

      /* 送信先に新着ポートレット登録 */
      List<String> recipients = new ArrayList<String>();
      if (memberList != null) {
        for (ALEipUser user : memberList) {
          if (user.getUserId().getValue() != loginUser.getUserId().getValue()) {
            recipients.add(user.getName().getValue());
          }
        }
      }
      // アクティビティ
      ALEipUser user = ALEipUtils.getALEipUser(rundata);
      NoteUtils.sendNoteActivity(note, user.getName().getValue(), recipients);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("note", ex);
      return false;
    }

    if (add_dest_type_int > 0) {
      // 携帯電話やパソコンに伝言メモをメールで送信する場合の処理
      try {
        String subject =
          "["
            + ALOrgUtilsService.getAlias()
            + "]"
            + ALLocalizationUtils.getl10n("NOTE_NOTE");
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(memberList, (int) loginUser
            .getUserId()
            .getValue(), false);

        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          message.setPcSubject(subject);
          message.setCellularSubject(subject);
          message.setPcBody(createMsgForPc(rundata, note, memberList));
          message.setCellularBody(createMsgForCellPhone(
            rundata,
            note,
            memberList));
          messageList.add(message);
        }
        ALMailService.sendAdminMailAsync(new ALAdminMailContext(
          orgId,
          ALEipUtils.getUserId(rundata),
          messageList,
          add_dest_type_int));

        // 重複するメッセージを削除する
        HashSet<String> tempMsgList = new HashSet<String>();
        tempMsgList.addAll(msgList);

        msgList.clear();
        msgList.addAll(tempMsgList);

      } catch (Exception ex) {
        msgList.add(ALLocalizationUtils.getl10n("NOTE_DONOT_SEND_MAIL"));
        logger.error("note", ex);
        return false;
      }
    }
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
  protected boolean updateFormData(RunData rundata, Context context,
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
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {

    try {
      // オブジェクトモデルを取得
      EipTNoteMap map =
        NoteUtils.getEipTNoteMap(rundata, context, getSelectQueryForDelete(
          rundata,
          context));

      if (map == null) {
        return false;
      }

      List<String> delNoteIdList = new ArrayList<String>();
      delNoteIdList.add(map.getEipTNote().getNoteId().toString());

      // 伝言メモを削除
      return NoteUtils.deleteNotes(rundata, context, delNoteIdList, msgList);
    } catch (Exception ex) {
      logger.error("note", ex);
      return false;
    }
  }

  private SelectQuery<EipTNoteMap> getSelectQueryForDelete(RunData rundata,
      Context context) {
    String userId = Integer.toString(ALEipUtils.getUserId(rundata));

    SelectQuery<EipTNoteMap> query = Database.query(EipTNoteMap.class);

    if ("received_notes".equals(NoteUtils.getCurrentTab(rundata, context))) {
      Expression exp1 =
        ExpressionFactory.matchExp(EipTNoteMap.USER_ID_PROPERTY, userId);
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.noMatchExp(EipTNoteMap.EIP_TNOTE_PROPERTY
          + "."
          + EipTNote.OWNER_ID_PROPERTY, userId);
      query.setQualifier(exp2);
    } else {
      Expression exp =
        ExpressionFactory.matchExp(EipTNoteMap.EIP_TNOTE_PROPERTY
          + "."
          + EipTNote.OWNER_ID_PROPERTY, userId);
      query.setQualifier(exp);

    }
    return query;
  }

  /**
   *
   */
  @Override
  public void initField() {
    note_id = new ALNumberField();
    note_id.setFieldName(ALLocalizationUtils.getl10n("NOTE_ID"));

    src_user_id = new ALStringField();
    src_user_id.setFieldName(ALLocalizationUtils.getl10n("NOTE_SRC_USER"));
    src_user_id.setTrim(true);

    dest_user_id = new ALStringField();
    dest_user_id.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_TO"));
    dest_user_id.setTrim(true);

    dest_user_name = new ALStringField();
    dest_user_name.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_SEND_NOTE_TO_NAME"));
    dest_user_name.setTrim(true);

    dest_post_id = new ALNumberField();
    dest_post_id.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_SEND_NOTE_POSITION_ID"));

    client_name = new ALStringField();
    client_name.setFieldName(ALLocalizationUtils.getl10n("NOTE_CLIENT_NAME"));
    client_name.setTrim(true);

    company_name = new ALStringField();
    company_name.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_CLIENT_CAMPANY"));
    company_name.setTrim(true);

    telephone1 = new ALStringField();
    telephone1.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_CLIENT_CELLILAR_PHONE"));
    telephone1.setTrim(true);
    telephone2 = new ALStringField();
    telephone2.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_CLIENT_CELLILAR_PHONE"));
    telephone2.setTrim(true);
    telephone3 = new ALStringField();
    telephone3.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_CLIENT_CELLILAR_PHONE"));
    telephone3.setTrim(true);

    email_address = new ALStringField();
    email_address
      .setFieldName(ALLocalizationUtils.getl10n("NOTE_CLIENT_EMAIL"));
    email_address.setTrim(true);

    add_dest_type_pc = new ALStringField();
    add_dest_type_pc.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_MAILINFORM_POSITION"));
    add_dest_type_pc.setTrim(true);

    add_dest_type_cellphone = new ALStringField();
    add_dest_type_cellphone.setFieldName(ALLocalizationUtils
      .getl10n("NOTE_MAILINFORM_POSITION"));
    add_dest_type_cellphone.setTrim(true);

    subject_type = new ALStringField();
    subject_type.setFieldName(ALLocalizationUtils.getl10n("NOTE_SUBJECT"));
    subject_type.setTrim(true);

    custom_subject = new ALStringField();
    custom_subject.setFieldName(ALLocalizationUtils.getl10n("NOTE_SUBJECT"));
    custom_subject.setTrim(true);

    note_stat = new ALStringField();
    note_stat.setFieldName(ALLocalizationUtils.getl10n("NOTE_STATE"));
    note_stat.setTrim(true);

    message = new ALStringField();
    message.setFieldName(ALLocalizationUtils.getl10n("NOTE_MESSAGE"));
    message.setTrim(false);

    accept_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    accept_date.setFieldName(ALLocalizationUtils.getl10n("NOTE_CLERK_TIME"));

    confirm_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    confirm_date.setFieldName(ALLocalizationUtils.getl10n("NOTE_CHECK_TIME"));

    create_date = new ALDateTimeField(NoteUtils.CREATED_DATE_FORMAT);
    create_date.setFieldName(ALLocalizationUtils.getl10n("COMMON_CREATE_DATE"));

    update_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    update_date.setFieldName(ALLocalizationUtils.getl10n("COMMON_UPDATE_DATE"));

    // 現在の年
    currentYear = Calendar.getInstance().get(Calendar.YEAR);

    view_type = new ALStringField();
    view_type.setFieldName(ALLocalizationUtils.getl10n("NOTE_EXPRESS_TYPE"));
    view_type.setTrim(true);
  }

  /**
   * データに値をセットする．
   * 
   * @param rundata
   * @param context
   * @param msgList
   *          エラーメッセージのリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean success = super.setFormData(rundata, context, msgList);

    src_user_id.setValue(Integer.toString(ALEipUtils.getUserId(rundata)));

    // 受付日時
    if (accept_date.toString().equals("")) {
      accept_date.setValue(Calendar.getInstance().getTime());
    }

    // 用件タイプ
    if (subject_type.getValue() == null) {
      subject_type.setValue("3");
    }

    if (dest_user_id.getValue() == null
      || dest_user_id.getValue().equals("")
      || dest_user_id.getValue().equals("all")) {
      if (target_group_name == null
        || target_group_name.equals("")
        || target_group_name.equals("all")) {
        memberList = getUsers("LoginUser");
      } else {
        memberList = getUsers(target_group_name);
        if (!containtsUser(memberList, loginUser)) {
          // ログインユーザが含まれていない場合は，登録する．
          memberList.add(loginUser);
        }
      }
    } else {
      Integer destid = Integer.valueOf(dest_user_id.getValue());
      memberList = new ArrayList<ALEipUser>();
      memberList.add(ALEipUtils.getALEipUser(destid.intValue()));
      if (!containtsUser(memberList, loginUser)) {
        // ログインユーザが含まれていない場合は，登録する．
        memberList.add(loginUser);
      }
    }

    return success;
  }

  /**
   * パソコンへ送信するメールの内容を作成する．
   * 
   * @return
   */
  private String createMsgForPc(RunData rundata, EipTNote note,
      List<ALEipUser> memberList) throws ALDBErrorException {
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);

    ALEipUser loginUser = null;
    ALBaseUser user = null;
    StringWriter out = null;

    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }

    try {
      VelocityService service =
        (VelocityService) ((TurbineServices) TurbineServices.getInstance())
          .getService(VelocityService.SERVICE_NAME);
      Context context = service.getContext();

      context.put("clientName", note.getClientName());
      context.put("companyName", note.getCompanyName());

      // 受付時間
      ALDateTimeField alDateTimeField = new ALDateTimeField();
      alDateTimeField.setValue(note.getAcceptDate());
      StringBuffer acceptDate = new StringBuffer();
      acceptDate.append(alDateTimeField.getMonth()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_MONTH")).append(
        alDateTimeField.getDay()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_DAY")).append(
        alDateTimeField.getHour()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_HOUR")).append(
        alDateTimeField.getMinute()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_MINUTE"));

      context.put("acceptDate", acceptDate);
      // 用件
      String subjectType = note.getSubjectType();
      String subject = "";
      if ("0".equals(subjectType)) {
        subject = note.getCustomSubject();
      } else if ("1".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_CALL_AGAIN");
      } else if ("2".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_CALL_BACK");
      } else if ("3".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_TELL_ME");
      } else if ("4".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_TAKE_A_MESSAGE");
      }
      context.put("subjectType", subject);

      // 依頼者情報
      context.put("clientName", note.getClientName());
      context.put("companyName", note.getCompanyName());
      // 電話番号
      context.put("telephone", note.getTelephone());
      // メール
      // context.put("hasemailAddress", !note.getEmailAddress().equals(""));
      context.put("emailAddress", note.getEmailAddress());
      // 本文
      context.put("message", note.getMessage());
      // 送信者
      context.put("loginUser", loginUser.getAliasName().toString());
      context.put("hasEmail", !user.getEmail().equals(""));
      context.put("email", user.getEmail());

      // サービス
      context.put("serviceAlias", ALOrgUtilsService.getAlias());
      // サービス（Aipo）へのアクセス
      context.put("enableAsp", enableAsp);
      context.put("globalurl", ALMailUtils.getGlobalurl());
      context.put("localurl", ALMailUtils.getLocalurl());
      CustomLocalizationService locService =
        (CustomLocalizationService) ServiceUtil
          .getServiceByName(LocalizationService.SERVICE_NAME);
      String lang = locService.getLocale(rundata).getLanguage();
      out = new StringWriter();
      if (lang != null && lang.equals("ja")) {
        service.handleRequest(context, "portlets/mail/"
          + lang
          + "/note-notification-mail.vm", out);
        out.flush();
        return out.toString();
      } else {
        service.handleRequest(
          context,
          "portlets/mail/note-notification-mail.vm",
          out);
        out.flush();
        return out.toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
    return null;
  }

  /**
   * 携帯電話へ送信するメールの内容を作成する．
   * 
   * @return
   */
  private String createMsgForCellPhone(RunData rundata, EipTNote note,
      List<ALEipUser> memberList) throws ALDBErrorException {
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);

    ALEipUser loginUser = null;
    ALBaseUser user = null;
    StringWriter out = null;

    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }

    try {
      VelocityService service =
        (VelocityService) ((TurbineServices) TurbineServices.getInstance())
          .getService(VelocityService.SERVICE_NAME);
      Context context = service.getContext();

      context.put("clientName", note.getClientName());
      context.put("companyName", note.getCompanyName());

      // 受付時間
      ALDateTimeField alDateTimeField = new ALDateTimeField();
      alDateTimeField.setValue(note.getAcceptDate());
      StringBuffer acceptDate = new StringBuffer();
      acceptDate.append(alDateTimeField.getMonth()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_MONTH")).append(
        alDateTimeField.getDay()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_DAY")).append(
        alDateTimeField.getHour()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_HOUR")).append(
        alDateTimeField.getMinute()).append(
        ALLocalizationUtils.getl10nFormat("NOTE_MINUTE"));

      context.put("acceptDate", acceptDate);
      // 用件
      String subjectType = note.getSubjectType();
      String subject = "";
      if ("0".equals(subjectType)) {
        subject = note.getCustomSubject();
      } else if ("1".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_CALL_AGAIN");
      } else if ("2".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_CALL_BACK");
      } else if ("3".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_TELL_ME");
      } else if ("4".equals(subjectType)) {
        subject = ALLocalizationUtils.getl10n("NOTE_TAKE_A_MESSAGE");
      }
      context.put("subjectType", subject);

      // 依頼者情報
      context.put("clientName", note.getClientName());
      context.put("companyName", note.getCompanyName());
      // 電話番号
      context.put("telephone", note.getTelephone());
      // メール
      // context.put("hasemailAddress", !note.getEmailAddress().equals(""));
      context.put("emailAddress", note.getEmailAddress());
      // 本文
      context.put("message", note.getMessage());
      // 送信者
      context.put("loginUser", loginUser.getAliasName().toString());
      context.put("hasEmail", !user.getEmail().equals(""));
      context.put("email", user.getEmail());

      // サービス
      context.put("serviceAlias", ALOrgUtilsService.getAlias());
      // サービス（Aipo）へのアクセス
      context.put("enableAsp", enableAsp);
      context.put("globalurl", ALMailUtils.getGlobalurl());
      context.put("localurl", ALMailUtils.getLocalurl());
      CustomLocalizationService locService =
        (CustomLocalizationService) ServiceUtil
          .getServiceByName(LocalizationService.SERVICE_NAME);
      String lang = locService.getLocale(rundata).getLanguage();
      out = new StringWriter();
      if (lang != null && lang.equals("ja")) {
        service.handleRequest(context, "portlets/mail/"
          + lang
          + "/note-notification-mail.vm", out);
        out.flush();
        return out.toString();
      } else {
        service.handleRequest(
          context,
          "portlets/mail/note-notification-mail.vm",
          out);
        out.flush();
        return out.toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
    return null;
  }

  /**
   * 
   * @param rundata
   * @param note
   * @param userId
   * @param stat
   * @throws ALDBErrorException
   */
  private void saveNoteMap(RunData rundata, EipTNote note, String userId,
      String stat) throws ALDBErrorException {
    EipTNoteMap map = Database.create(EipTNoteMap.class);
    // map.setPrimaryKey(noteId, userId);
    map.setEipTNote(note);
    map.setUserId(userId);
    map.setDelFlg("F");
    // 新着／未読／既読フラグ
    map.setNoteStat(stat);
  }

  /**
   * 
   * @param list
   * @param user
   * @return
   */
  private boolean containtsUser(List<ALEipUser> list, ALEipUser user) {
    if (user == null || list == null || list.size() <= 0) {
      return false;
    }

    long userid = user.getUserId().getValue();
    int size = list.size();
    for (int i = 0; i < size; i++) {
      user = list.get(i);
      if (user.getUserId().getValue() == userid) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   */
  public void enableAddDestTypes() {
    // 追加送信先タイプ（パソコンのメールアドレスに送信）
    add_dest_type_pc.setValue("1");
    // 追加送信先タイプ（携帯電話のメールアドレスに送信）
    add_dest_type_cellphone.setValue("1");
  }

  /**
   * 
   * @return
   */
  public int getCurrentYear() {
    return currentYear;
  }

  /**
   * @return
   */
  public ALDateTimeField getAcceptDate() {
    return accept_date;
  }

  /**
   * @return
   */
  public ALStringField getAddDestTypePc() {
    return add_dest_type_pc;
  }

  /**
   * @return
   */
  public ALStringField getAddDestTypeCellphone() {
    return add_dest_type_cellphone;
  }

  /**
   * @return
   */
  public ALStringField getClientName() {
    return client_name;
  }

  /**
   * @return
   */
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * @return
   */
  public ALDateTimeField getConfirmDate() {
    return confirm_date;
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALStringField getDestUserId() {
    return dest_user_id;
  }

  /**
   * @return
   */
  public ALStringField getEmailAddress() {
    return email_address;
  }

  /**
   * @return
   */
  public ALStringField getMessage() {
    return message;
  }

  /**
   * @return
   */
  public ALNumberField getNoteId() {
    return note_id;
  }

  /**
   * @return
   */
  public ALStringField getSrcUserId() {
    return src_user_id;
  }

  /**
   * @return
   */
  public ALStringField getSubjectType() {
    return subject_type;
  }

  public ALStringField getCustomSubject() {
    return custom_subject;
  }

  /**
   * @return
   */
  public ALStringField getTelephone1() {
    return telephone1;
  }

  /**
   * @return
   */
  public ALStringField getTelephone2() {
    return telephone2;
  }

  /**
   * @return
   */
  public ALStringField getTelephone3() {
    return telephone3;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return update_date;
  }

  public void setNoteStat(String value) {
    note_stat.setValue(value);
  }

  public ALStringField getNoteStat() {
    return note_stat;
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * @return
   */
  public ALStringField getViewType() {
    return view_type;
  }

  /**
   * @param string
   */
  public void setViewType(String string) {
    view_type.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getDestUserName() {
    return dest_user_name;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getDestPostId() {
    return dest_post_id;
  }

  /**
   * 
   * @param userName
   * @return
   */
  public String getUserId(String userName) {
    return NoteUtils.getUserId(userName);
  }

  /**
   * 
   * @return
   */
  public String getTargetGroupName() {
    return target_group_name;
  }

  /**
   * 
   * @return
   */
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * 
   * @return
   */
  public int getMsgType() {
    return msg_type;
  }

}
