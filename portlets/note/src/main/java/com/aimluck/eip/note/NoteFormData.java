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
package com.aimluck.eip.note;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
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
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * 伝言メモフォームデータを管理するためのクラスです。 <br />
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

  /** 作成日時 */
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

  private int msg_type = 0;

  /** メンバーリスト */
  private List<ALEipUser> memberList;

  private ALEipUser loginUser;

  private DataContext dataContext;

  private String org_id;

  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    target_group_name = NoteUtils.getTargetGroupName(rundata, context);

    loginUser = ALEipUtils.getALEipUser(rundata);

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    int length = myGroups.size();
    for (int i = 0; i < length; i++) {
      myGroupList.add(myGroups.get(i));
    }

    dataContext = DatabaseOrmService.getInstance().getDataContext();
    org_id = DatabaseOrmService.getInstance().getOrgId(rundata);

    ParameterParser parser = rundata.getParameters();
    String dest_user_id_string = parser.getString("dest_user_id");
    if (dest_user_id_string != null) {
      dest_user_id.setValue(dest_user_id_string);
    } else {
      dest_user_id.setValue("");
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
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
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();

    dest_post_id.validate(msgList);
    dest_user_id.validate(msgList);

    if (dest_user_id.getValue() == null || dest_user_id.getValue().equals("")
        || dest_user_id.getValue().equals("all")) {
      // 選択されたグループにログインユーザ以外のユーザが登録されていない場合，エラーを表示する．
      if (memberList == null || memberList.size() <= 1) {
        msgList
            .add("ユーザーが登録されている『 <span class='em'> 宛先のグループ </span> 』を選択してください。");
      }
    } else {
      if (memberList == null || memberList.size() <= 1) {
        msgList.add("『 <span class='em'> 宛先のユーザー </span> 』を正しく選択してください。");
      }
    }

    // 依頼者名
    client_name.validate(msgList);
    // 依頼者所属
    company_name.validate(msgList);
    // 依頼者電話番号
    if (!telephone1.getValue().equals("") || !telephone2.getValue().equals("")
        || !telephone3.getValue().equals("")) {
      if (!telephone1.validate(dummy) || !telephone2.validate(dummy)
          || !telephone3.validate(dummy)) {
        msgList.add("『 <span class='em'> 依頼者電話番号 </span> 』を正しく入力してください。");
      }
    }

    // 依頼者メールアドレス
    email_address.validate(msgList);
    if (email_address.getValue().trim().length() > 0
        && !ALStringUtil.isCellPhoneMailAddress(email_address.getValue())) {
      msgList.add("『 <span class='em'> 依頼者メールアドレス </span>』を正しく入力してください。");
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
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {

    // 追加送信先タイプ
    int add_dest_type_int = 0;

    try {
      Date nowDate = Calendar.getInstance().getTime();

      // 新規オブジェクトモデル
      EipTNote note = (EipTNote) dataContext
          .createAndRegisterNewObject(EipTNote.class);
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
        note.setTelephone(new StringBuffer().append(telephone1.getValue())
            .append("-").append(telephone2.getValue()).append("-")
            .append(telephone3.getValue()).toString());
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
      // 作成日時
      note.setCreateDate(nowDate);
      // 更新日時
      note.setUpdateDate(nowDate);

      // MAP の登録
      if (memberList != null) {
        int size = memberList.size();
        for (int i = 0; i < size; i++) {
          ALEipUser user = memberList.get(i);
          saveNoteMap(rundata, note, user.getUserId().toString(),
              NoteUtils.NOTE_STAT_NEW);
        }
      }

      dataContext.commitChanges();

      // イベントログに保存
      ALEventlogFactoryService
          .getInstance()
          .getEventlogHandler()
          .log(note.getNoteId(), ALEventlogConstants.PORTLET_TYPE_NOTE,
              NoteUtils.getNoteSubject(note));

      /* 送信先に新着ポートレット登録 */
      if (memberList != null) {
        int size = memberList.size();
        for (int i = 0; i < size; i++) {
          ALEipUser user = memberList.get(i);
          if (user.getUserId().getValue() != loginUser.getUserId().getValue()) {
            WhatsNewUtils.insertWhatsNew(dataContext,
                WhatsNewUtils.WHATS_NEW_TYPE_NOTE, note.getNoteId().intValue(),
                (int) user.getUserId().getValue());
          }
        }
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    if (add_dest_type_int > 0) {
      // 携帯電話やパソコンに伝言メモをメールで送信する場合の処理
      try {
        String subject = "[" + DatabaseOrmService.getInstance().getAlias()
            + "]伝言メモ";
        List<ALEipUserAddr> destMemberList = ALMailUtils.getALEipUserAddrs(
            memberList, (int) loginUser.getUserId().getValue(), false);

        for (int i = 0; i < destMemberList.size(); i++) {
          List<ALEipUserAddr> destMember = new ArrayList<ALEipUserAddr>();
          destMember.add(destMemberList.get(i));
          ALMailUtils.sendMailDelegate(org_id, ALEipUtils.getUserId(rundata),
              destMember, subject, subject, createMsgForPc(),
              createMsgForCellPhone(destMember.get(0)
                  .getUserId()), add_dest_type_int, msgList);
        }

      } catch (Exception ex) {
        msgList.add("メールを送信できませんでした。");
        logger.error("Exception", ex);
        return false;
      }
    }
    return (msgList.size() == 0);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {

    try {
      // オブジェクトモデルを取得
      EipTNoteMap map = NoteUtils.getEipTNoteMap(rundata, context,
          getSelectQueryForDelete(rundata, context));

      if (map == null)
        return false;

      List<String> delNoteIdList = new ArrayList<String>();
      delNoteIdList.add(map.getEipTNote().getNoteId().toString());

      // 伝言メモを削除
      return NoteUtils.deleteNotes(rundata, context, delNoteIdList, msgList);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
  }

  private SelectQuery getSelectQueryForDelete(RunData rundata, Context context) {
    String userId = Integer.toString(ALEipUtils.getUserId(rundata));

    SelectQuery query = new SelectQuery(EipTNoteMap.class);

    if ("received_notes".equals(NoteUtils.getCurrentTab(rundata, context))) {
      Expression exp1 = ExpressionFactory.matchExp(
          EipTNoteMap.USER_ID_PROPERTY, userId);
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.noMatchExp(
          EipTNoteMap.EIP_TNOTE_PROPERTY + "." + EipTNote.OWNER_ID_PROPERTY,
          userId);
      query.setQualifier(exp2);
    } else {
      Expression exp = ExpressionFactory.matchExp(
          EipTNoteMap.EIP_TNOTE_PROPERTY + "." + EipTNote.OWNER_ID_PROPERTY,
          userId);
      query.setQualifier(exp);

    }
    return query;
  }

  /**
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    note_id = new ALNumberField();
    note_id.setFieldName("伝言メモID");

    src_user_id = new ALStringField();
    src_user_id.setFieldName("送信者");
    src_user_id.setTrim(true);

    dest_user_id = new ALStringField();
    dest_user_id.setFieldName("宛先");
    dest_user_id.setTrim(true);

    dest_user_name = new ALStringField();
    dest_user_name.setFieldName("宛先ユーザ名");
    dest_user_name.setTrim(true);

    dest_post_id = new ALNumberField();
    dest_post_id.setFieldName("宛先部署 ID");

    client_name = new ALStringField();
    client_name.setFieldName("依頼者名");
    client_name.setTrim(true);

    company_name = new ALStringField();
    company_name.setFieldName("依頼者所属");
    company_name.setTrim(true);

    telephone1 = new ALStringField();
    telephone1.setFieldName("依頼者電話番号");
    telephone1.setTrim(true);
    telephone2 = new ALStringField();
    telephone2.setFieldName("依頼者電話番号");
    telephone2.setTrim(true);
    telephone3 = new ALStringField();
    telephone3.setFieldName("依頼者電話番号");
    telephone3.setTrim(true);

    email_address = new ALStringField();
    email_address.setFieldName("依頼者メールアドレス");
    email_address.setTrim(true);

    add_dest_type_pc = new ALStringField();
    add_dest_type_pc.setFieldName("追加送信先");
    add_dest_type_pc.setTrim(true);

    add_dest_type_cellphone = new ALStringField();
    add_dest_type_cellphone.setFieldName("追加送信先");
    add_dest_type_cellphone.setTrim(true);

    subject_type = new ALStringField();
    subject_type.setFieldName("用件");
    subject_type.setTrim(true);

    custom_subject = new ALStringField();
    custom_subject.setFieldName("用件");
    custom_subject.setTrim(true);

    note_stat = new ALStringField();
    note_stat.setFieldName("状態");
    note_stat.setTrim(true);

    message = new ALStringField();
    message.setFieldName("本文");
    message.setTrim(false);

    accept_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    accept_date.setFieldName("受付日時");

    confirm_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    confirm_date.setFieldName("確認日時");

    create_date = new ALDateTimeField(NoteUtils.CREATED_DATE_FORMAT);
    create_date.setFieldName("作成日時");

    update_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    update_date.setFieldName("更新日時");

    // 現在の年
    currentYear = Calendar.getInstance().get(Calendar.YEAR);

    view_type = new ALStringField();
    view_type.setFieldName("表示タイプ");
    view_type.setTrim(true);
  }

  /**
   * データに値をセットする． <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   *          エラーメッセージのリスト
   * @return TRUE 成功 FALSE 失敗
   */
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean success = super.setFormData(rundata, context, msgList);

    src_user_id.setValue(Integer.toString(ALEipUtils.getUserId(rundata)));

    // 受付日時
    if (accept_date.toString().equals("")) {
      accept_date.setValue(Calendar.getInstance().getTime());
    }

    // 用件タイプ
    if (subject_type.getValue() == null)
      subject_type.setValue("3");

    if (dest_user_id.getValue() == null || dest_user_id.getValue().equals("")
        || dest_user_id.getValue().equals("all")) {
      if (target_group_name == null || target_group_name.equals("")
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

  // private boolean sendMail(RunData rundata, int srcUserId, List memberList,
  // int add_dest_type_int, List<String> msgList) throws Exception {
  //
  // if (add_dest_type_int < 0 || add_dest_type_int > 3) {
  // return false;
  // }
  //
  // if (memberList == null || memberList.size() == 0)
  // return true;
  //
  // // メールの送信
  // EipMMailAccount account = WebMailUtils.getEipMMailAccountForAdmin(rundata);
  // if (account == null) {
  // // 伝言メモの登録を完了させるため，true を返す．
  // // msgList.add("メールアカウントが未設定のため，メールを送信できませんでした。");
  // msg_type = MSG_TYPE_NON_MAILACCOUNT;
  // return true;
  // }
  //
  // ArrayList destEmailAddrs = new ArrayList();
  // ArrayList destCellularEMailAddrs = new ArrayList();
  // int size = memberList.size();
  // for (int i = 0; i < size; i++) {
  // ALEipUser eipuser = (ALEipUser) memberList.get(i);
  // ALBaseUser user = (ALBaseUser) JetspeedSecurity.getUser(eipuser.getName()
  // .getValue());
  // String emailAddr = user.getEmail();
  // if (emailAddr != null && !emailAddr.equals("")) {
  // destEmailAddrs.add(emailAddr);
  // }
  // String cellularEmailAddr = user.getCellularMail();
  // if (cellularEmailAddr != null && !cellularEmailAddr.equals("")) {
  // destCellularEMailAddrs.add(cellularEmailAddr);
  // }
  // }
  //
  // // 送信先の情報の有無を検証する
  // boolean validUserEmail = false;
  // boolean validCellularMail = false;
  //
  // int destEmailAddrsSize = destEmailAddrs.size();
  // int destCellularEMailAddrsSize = destCellularEMailAddrs.size();
  // if (add_dest_type_int == 0) {
  // msg_type = this.MSG_TYPE_NON_ADDR_PC_CELL;
  // return true;
  // } else if (add_dest_type_int == 1) {
  // if (destEmailAddrsSize == 0) {
  // msg_type = MSG_TYPE_NON_ADDR_PC;
  // return true;
  // }
  // validUserEmail = true;
  // } else if (add_dest_type_int == 2) {
  // if (destCellularEMailAddrsSize == 0) {
  // msg_type = MSG_TYPE_NON_ADDR_CELL;
  // return true;
  // }
  // validCellularMail = true;
  // } else if (add_dest_type_int == 3) {
  // if ((destEmailAddrsSize == 0) && (destCellularEMailAddrsSize == 0)) {
  // msg_type = MSG_TYPE_NON_ADDR_PC_CELL;
  // return true;
  // } else {
  // if (destEmailAddrsSize == 0) {
  // msg_type = MSG_TYPE_NON_ADDR_PC;
  // } else {
  // validUserEmail = true;
  // }
  // if (destCellularEMailAddrsSize == 0) {
  // msg_type = MSG_TYPE_NON_ADDR_CELL;
  // } else {
  // validCellularMail = true;
  // }
  // }
  // }
  //
  // String authSendUserId = account.getPop3userName();
  // String authSendUserPassword = new String(WebMailUtils
  // .getDecryptedAccountPasswd(account.getPop3password()));
  //
  // String subject = "[Aipo]伝言メモ";
  // // String[] to;
  // boolean success = true;
  //
  // ALMailHandler mailhandler = ALMailFactoryService.getInstance()
  // .getMailHandler();
  // // 送信サーバ情報
  // ALSmtpMailSenderContext scontext = new ALSmtpMailSenderContext();
  // scontext.setOrgId(org_id);
  // scontext.setUserId((int) loginUser.getUserId().getValue());
  // scontext.setAccountId(account.getAccountId().intValue());
  // scontext.setSmtpHost(account.getSmtpserverName());
  // scontext.setSmtpPort(account.getSmtpPort());
  // scontext.setAuthSendFlag(WebMailUtils.stringAsInt(account
  // .getBeforeSmtpFlg()));
  // scontext.setAuthSendUserId(account.getPop3userName());
  // scontext.setAuthSendUserPassword(new String(WebMailUtils
  // .getDecryptedAccountPasswd(account.getPop3password())));
  //
  // int successSendToPc = -1;
  // int successSendToCell = -1;
  // // パソコンへメールを送信
  // if (validUserEmail) {
  // String[] tos = new String[destEmailAddrsSize];
  // tos = (String[]) destEmailAddrs.toArray(tos);
  //
  // // 送信メッセージのコンテキスト
  // ALSmtpMailContext mailcontext = new ALSmtpMailContext();
  // mailcontext.setTo(tos);
  // mailcontext.setCc(null);
  // mailcontext.setBcc(null);
  // mailcontext.seFrom(account.getMailAddress());
  // mailcontext.setName(ALStringUtil.unsanitizing(account.getMailUserName()));
  // mailcontext.setSubject(ALStringUtil.unsanitizing(subject));
  // mailcontext.setMsgText(ALStringUtil.unsanitizing(createMsgForPc()));
  // mailcontext.setFilePaths(null);
  // mailcontext.setAdditionalHeaders(null);
  //
  // successSendToPc = mailhandler.send(scontext, mailcontext);
  // }
  //
  // // 携帯電話へメールを送信
  // if (validCellularMail) {
  // String[] tos = new String[destCellularEMailAddrsSize];
  // tos = (String[]) destCellularEMailAddrs.toArray(tos);
  //
  // ALSmtpMailContext mailcontext = new ALSmtpMailContext();
  // mailcontext.setTo(tos);
  // mailcontext.setCc(null);
  // mailcontext.setBcc(null);
  // mailcontext.seFrom(account.getMailAddress());
  // mailcontext.setName(ALStringUtil.unsanitizing(account.getMailUserName()));
  // mailcontext.setSubject(ALStringUtil.unsanitizing(subject));
  // mailcontext
  // .setMsgText(ALStringUtil.unsanitizing(createMsgForCellPhone()));
  // mailcontext.setFilePaths(null);
  // mailcontext.setAdditionalHeaders(null);
  //
  // successSendToCell = mailhandler.send(scontext, mailcontext);
  // }
  //
  // if (successSendToPc == ALSmtpMailSender.SEND_MSG_SUCCESS
  // && successSendToCell == ALSmtpMailSender.SEND_MSG_SUCCESS) {
  // success = true;
  // } else {
  // success = false;
  // }
  //
  // return success;
  // }

  /**
   * パソコンへ送信するメールの内容を作成する．
   *
   * @return
   */
  private String createMsgForPc() {
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer();
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);

    if (company_name.getValue() != null
        && (!company_name.getValue().equals(""))) {
      body.append(company_name);
    }
    body.append(client_name).append("様から伝言があります。").append(CR).append(CR);

    body.append("[受付時間]").append(CR);
    body.append(accept_date.getMonth()).append("月")
        .append(accept_date.getDay()).append("日").append(accept_date.getHour())
        .append("時").append(accept_date.getMinute()).append("分").append(CR);
    body.append("[用件]").append(CR);
    if ("0".equals(subject_type.getValue())) {
      body.append(custom_subject.getValue()).append(CR);
    } else if ("1".equals(subject_type.getValue())) {
      body.append("再度電話します").append(CR);
    } else if ("2".equals(subject_type.getValue())) {
      body.append("電話をしてください").append(CR);
    } else if ("3".equals(subject_type.getValue())) {
      body.append("電話がありました").append(CR);
    } else if ("4".equals(subject_type.getValue())) {
      body.append("伝言があります").append(CR);
    }

    body.append("[依頼者情報]").append(CR).append("名前：").append(client_name);
    if (company_name.getValue() != null
        && (!company_name.getValue().equals(""))) {
      body.append(CR).append("所属：").append(company_name);
    }
    if (telephone1 != null && telephone2 != null && telephone3 != null
        && !telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
      body.append(CR).append("[電話番号]").append(CR).append(telephone1)
          .append("-").append(telephone2).append("-").append(telephone3);
    }
    if (email_address.getValue() != null
        && (!email_address.getValue().equals(""))) {
      body.append(CR).append("[メール]").append(CR).append(email_address);
    }

    if (message.getValue() != null && (!message.getValue().equals(""))) {
      body.append(CR).append("[本文]").append(CR).append(message.getValue());
    }

    ALBaseUser user = null;
    try {
      user = (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(
          src_user_id.getValue()));
    } catch (Exception e) {
      return "";
    }
    String username = new StringBuffer().append(user.getLastName()).append(" ")
        .append(user.getFirstName()).toString();
    body.append(CR).append("[送信者]").append(CR).append(username);
    String e_mail_addr = user.getEmail();
    if (!e_mail_addr.equals("")) {
      body.append("(").append(e_mail_addr).append(")");
    }
    body.append(CR);
    body.append(CR);
    body.append("[").append(DatabaseOrmService.getInstance().getAlias())
        .append("へのアクセス]").append(CR);
    if (enableAsp) {
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
    } else {
      body.append("・社外").append(CR);
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
      body.append("・社内").append(CR);
      body.append("　").append(ALMailUtils.getLocalurl()).append(CR).append(CR);
    }
    body.append("---------------------").append(CR);
    body.append(DatabaseOrmService.getInstance().getAlias()).append(CR);

    return body.toString();
  }

  /**
   * 携帯電話へ送信するメールの内容を作成する．
   *
   * @return
   */
  private String createMsgForCellPhone(int destUserID) {
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer();

    if (company_name.getValue() != null
        && (!company_name.getValue().equals(""))) {
      body.append(company_name);
    }
    body.append(client_name).append("様から伝言があります。").append(CR).append(CR);

    body.append("[受付時間]").append(CR);
    body.append(accept_date.getMonth()).append("月")
        .append(accept_date.getDay()).append("日").append(accept_date.getHour())
        .append("時").append(accept_date.getMinute()).append("分").append(CR);
    body.append("[用件]").append(CR);
    if ("0".equals(subject_type.getValue())) {
      body.append(custom_subject.getValue()).append(CR);
    } else if ("1".equals(subject_type.getValue())) {
      body.append("再度電話します").append(CR);
    } else if ("2".equals(subject_type.getValue())) {
      body.append("電話をしてください").append(CR);
    } else if ("3".equals(subject_type.getValue())) {
      body.append("電話がありました").append(CR);
    } else if ("4".equals(subject_type.getValue())) {
      body.append("伝言があります").append(CR);
    }

    body.append("[依頼者情報]").append(CR).append("名前：").append(client_name);
    if (company_name.getValue() != null
        && (!company_name.getValue().equals(""))) {
      body.append(CR).append("所属：").append(company_name);
    }
    if (telephone1 != null && telephone2 != null && telephone3 != null
        && !telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
      body.append(CR).append("[電話番号]").append(CR).append(telephone1)
          .append("-").append(telephone2).append("-").append(telephone3);
    }
    if (email_address.getValue() != null
        && (!email_address.getValue().equals(""))) {
      body.append(CR).append("[メール]").append(CR).append(email_address);
    }

    if (message.getValue() != null && (!message.getValue().equals(""))) {
      body.append(CR).append("[本文]").append(CR).append(message.getValue());
    }

    ALBaseUser user = null;
    try {
      user = (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(
          src_user_id.getValue()));
    } catch (Exception e) {
      return "";
    }

    String username = new StringBuffer().append(user.getLastName()).append(" ")
        .append(user.getFirstName()).toString();
    body.append(CR).append("[送信者]").append(CR).append(username);
    String e_mail_addr = user.getEmail();
    if (e_mail_addr != null && !e_mail_addr.equals("")) {
      body.append("(").append(e_mail_addr).append(")");
    }

    ALEipUser destUser;
    try {
      destUser = ALEipUtils.getALEipUser(destUserID);
    } catch (ALDBErrorException ex) {
      logger.error("Exception", ex);
      return "";
    }

    body.append(CR);
    body.append(CR);
    body.append("[").append(DatabaseOrmService.getInstance().getAlias())
        .append("へのアクセス]").append(CR);
    body.append("　").append(ALMailUtils.getGlobalurl()).append("?key=")
        .append(ALCellularUtils.getCellularKey(destUser)).append(CR);
    body.append("---------------------").append(CR);
    body.append(DatabaseOrmService.getInstance().getAlias()).append(CR);
    return body.toString();
  }

  private void saveNoteMap(RunData rundata, EipTNote note, String userId,
      String stat) throws ALDBErrorException {
    EipTNoteMap map = (EipTNoteMap) dataContext
        .createAndRegisterNewObject(EipTNoteMap.class);
    // map.setPrimaryKey(noteId, userId);
    map.setEipTNote(note);
    map.setUserId(userId);
    map.setDelFlg("F");
    // 新着／未読／既読フラグ
    map.setNoteStat(stat);
  }

  private boolean containtsUser(List<ALEipUser> list, ALEipUser user) {
    if (user == null || list == null || list.size() <= 0)
      return false;

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

  public ALNumberField getDestPostId() {
    return dest_post_id;
  }

  public String getUserId(String userName) {
    return NoteUtils.getUserId(userName);
  }

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

  public int getMsgType() {
    return msg_type;
  }

}
