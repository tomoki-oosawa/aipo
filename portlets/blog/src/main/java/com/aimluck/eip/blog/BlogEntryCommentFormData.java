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

package com.aimluck.eip.blog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.blog.BlogAction;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.modules.screens.BlogDetailScreen;
import com.aimluck.eip.modules.screens.BlogEntryFormJSONScreen;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログエントリー・コメントのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class BlogEntryCommentFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogEntryCommentFormData.class.getName());

  /** コメント */
  private ALStringField comment;

  private boolean sendEmailToPC = false;

  private boolean sendEmailToCellular = false;

  /** メール送信時のメッセージ種別(ブログ) 3=PC,Celluler 2=Celluer 1=PC 0=送信しない */
  private int MsgTypeBlog = 0;

  /** <code>login_user</code> ログインユーザー */
  private ALEipUser login_user;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

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

    try {

      MsgTypeBlog = ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_BLOG);

      if ((MsgTypeBlog & 1) > 0) {
        sendEmailToPC = true;
      } else {
        sendEmailToPC = false;
      }
      if ((MsgTypeBlog & 2) > 0) {
        sendEmailToCellular = true;
      } else {
        sendEmailToCellular = false;
      }
    } catch (Throwable t) {
      sendEmailToPC = false;
      sendEmailToCellular = false;
    }

    login_user = ALEipUtils.getALEipUser(rundata);

    int uid = ALEipUtils.getUserId(rundata);
    // アクセス権
    if ("commentdel".equals(action.getMode())) {
      String commentid = rundata.getParameters().getString("comment_id");
      int comment_view_uid =
        BlogUtils.getCommentViewId(rundata, context, uid, commentid);
      if (uid == comment_view_uid) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_REPLY;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER_REPLY;
      }
    }
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
    comment.setFieldName("コメント");
    comment.setTrim(false);

  }

  /**
   * 掲示板の各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // メモ必須項目
    comment.setNotNull(true);
    // メモの文字数制限
    comment.limitMaxLength(1000);
  }

  /**
   * トピックのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // メモ
    comment.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * トピックをデータベースから読み出します。 <BR>
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
   * コメントをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      String commentid = rundata.getParameters().getString("comment_id");

      // オブジェクトモデルを取得
      EipTBlogComment comment =
        BlogUtils.getEipTBlogComment(rundata, context, commentid);
      if (comment == null) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[BlogEntryCommentFormData] Not found ID...");
        throw new ALPageNotFoundException();
      }

      Database.delete(comment);
      Database.commit();

    } catch (Exception e) {
      Database.rollback();
      logger.error("[BlogEntryCommentFormData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * コメントをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // オブジェクトモデルを取得
      EipTBlogEntry parententry =
        BlogUtils.getEipTBlogParentEntry(rundata, context);
      if (parententry == null) {
        // 指定した エントリー ID のレコードが見つからない場合
        logger.debug("[BlogEntryCommentFormData] Not found ID...");
        throw new ALPageNotFoundException();
      }

      int uid = ALEipUtils.getUserId(rundata);
      Date updateDate = Calendar.getInstance().getTime();

      EipTBlogEntry entry =
        Database.get(EipTBlogEntry.class, Integer.valueOf(parententry
          .getEntryId()
          .intValue()));

      // 新規オブジェクトモデル
      EipTBlogComment blogcomment = Database.create(EipTBlogComment.class);
      // ユーザーID
      blogcomment.setOwnerId(Integer.valueOf(uid));
      // コメント
      blogcomment.setComment(comment.getValue());
      // エントリーID
      blogcomment.setEipTBlogEntry(entry);
      // 作成日
      blogcomment.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      blogcomment.setUpdateDate(updateDate);

      // トピックを登録
      Database.commit();

      // アクティビティ
      String loginName = ALEipUtils.getALEipUser(uid).getName().getValue();
      String targetLoginName =
        ALEipUtils.getALEipUser(entry.getOwnerId()).getName().getValue();
      BlogUtils.createNewCommentActivity(entry, loginName, targetLoginName);

      // メール送信
      if (sendEmailToPC || sendEmailToCellular) {
        List<ALEipUser> memberList = new ArrayList<ALEipUser>();
        memberList.add(ALEipUtils.getALEipUser(entry.getOwnerId().intValue()));
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(memberList, ALEipUtils
            .getUserId(rundata), false);

        String orgId = Database.getDomainName();
        String subject =
          "[" + JetspeedResources.getString("aipo.alias") + "]ブログコメント";

        // パソコン、携帯電話へメールを送信
        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          if (sendEmailToPC) {
            message.setPcSubject(subject);
            message.setPcBody(createMsgForPc(rundata));
          }
          if (sendEmailToCellular) {
            message.setCellularSubject(subject);
            message.setCellularBody(createMsgForCellPhone(rundata, destMember
              .getUserId()));
          }

          messageList.add(message);
        }
        List<String> errors =
          ALMailService.sendAdminMail(new ALAdminMailContext(
            orgId,
            (int) login_user.getUserId().getValue(),
            messageList,
            ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_BLOG)));
        msgList.addAll(errors);

      }
    } catch (Exception e) {
      Database.rollback();
      logger.error("[BlogEntryCommentFormData]", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * パソコンへ送信するメールの内容を作成する．
   * 
   * @return
   */
  private String createMsgForPc(RunData rundata) {
    String CR = System.getProperty("line.separator");
    ALEipUser user = ALEipUtils.getALEipUser(rundata);
    StringBuffer body = new StringBuffer();
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);

    ALBaseUser user2 = null;
    try {
      user2 =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(user
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    String username =
      new StringBuffer().append(user2.getLastName()).append(" ").append(
        user2.getFirstName()).toString();
    body.append(username);
    String e_mail_addr = user2.getEmail();
    if (!e_mail_addr.equals("")) {
      body.append("(").append(e_mail_addr).append(")");
    }

    body.append("さんから日記にコメントがつきました。");

    body.append(CR).append(CR);

    body.append("[コメント]").append(CR).append(comment.getValue()).append(CR);

    body.append(CR);
    body
      .append("[")
      .append(ALOrgUtilsService.getAlias())
      .append("へのアクセス]")
      .append(CR);
    if (enableAsp) {
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
    } else {
      body.append("・社外").append(CR);
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
      body.append("・社内").append(CR);
      body.append("　").append(ALMailUtils.getLocalurl()).append(CR).append(CR);
    }
    body.append("---------------------").append(CR);
    body.append(ALOrgUtilsService.getAlias()).append(CR);

    return body.toString();
  }

  /**
   * 携帯電話へ送信するメールの内容を作成する．
   * 
   * @return
   */
  private String createMsgForCellPhone(RunData rundata, int destUserID) {
    String CR = System.getProperty("line.separator");
    ALEipUser user = ALEipUtils.getALEipUser(rundata);
    StringBuffer body = new StringBuffer();
    ALBaseUser user2 = null;
    try {
      user2 =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(user
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    String username =
      new StringBuffer().append(user2.getLastName()).append(" ").append(
        user2.getFirstName()).toString();
    body.append(username);
    String e_mail_addr = user2.getEmail();
    if (!e_mail_addr.equals("")) {
      body.append("(").append(e_mail_addr).append(")");
    }

    body.append("さんから日記にコメントがつきました。");
    body.append(CR).append(CR);

    body.append("[コメント]").append(CR).append(comment.getValue()).append(CR);

    body.append(CR);
    ALEipUser destUser;
    try {
      destUser = ALEipUtils.getALEipUser(destUserID);
    } catch (ALDBErrorException ex) {
      logger.error("Exception", ex);
      return "";
    }

    body
      .append("[")
      .append(ALOrgUtilsService.getAlias())
      .append("へのアクセス]")
      .append(CR);
    body.append("　").append(ALMailUtils.getGlobalurl()).append("?key=").append(
      ALCellularUtils.getCellularKey(destUser)).append(CR);
    body.append("---------------------").append(CR);
    body.append(ALOrgUtilsService.getAlias()).append(CR);
    return body.toString();
  }

  /**
   * データベースに格納されているコメントを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * エントリー詳細表示ページからデータを新規登録します。
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

      ArrayList<String> msgList = new ArrayList<String>();
      setValidator();
      boolean res =
        (setFormData(rundata, context, msgList) && validate(msgList) && insertFormData(
          rundata,
          context,
          msgList));
      if (!res) {
        action.setMode(ALEipConstants.MODE_NEW_FORM);
        setMode(action.getMode());
      }
      if (action instanceof BlogEntryFormJSONScreen) {
        action.setResultData(this);
        action.addErrorMessages(msgList);
        action.putData(rundata, context);
      } else {
        BlogAction blogAction = (BlogAction) action;
        blogAction.setResultDataOnCommentDetail(this);
        blogAction.addErrorMessagesOnCommentDetail(msgList);
        blogAction.putDataOnCommentDetail(rundata, context);
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
   * エントリー詳細表示ページにフォームを表示します。
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
      // action.setMode(isedit ? ALEipConstants.MODE_EDIT_FORM
      // : ALEipConstants.MODE_NEW_FORM);
      // mode = action.getMode();
      // doCheckAclPermission(rundata, context,
      // ALAccessControlConstants.VALUE_ACL_DETAIL);
      ArrayList<String> msgList = new ArrayList<String>();
      boolean res = setFormData(rundata, context, msgList);
      if (action instanceof BlogDetailScreen) {
        BlogDetailScreen blogAction = (BlogDetailScreen) action;
        blogAction.setResultDataOnCommentDetail(this);
        blogAction.addErrorMessagesOnCommentDetail(msgList);
        blogAction.putDataOnCommentDetail(rundata, context);
      } else {
        BlogAction blogAction = (BlogAction) action;
        blogAction.setResultDataOnCommentDetail(this);
        blogAction.addErrorMessagesOnCommentDetail(msgList);
        blogAction.putDataOnCommentDetail(rundata, context);
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
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);
    return res;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getComment() {
    return comment;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

}
