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
package com.aimluck.eip.blog;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
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
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

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
    comment.setFieldName(ALLocalizationUtils.getl10n("BLOG_COMMENT"));
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
      logger.error("BlogEntryCommentFormData.deleteFormData", e);
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

      // 更新情報を送るユーザーを取得する
      List<ALEipUser> recipientList = getRecipientList(rundata, context);

      // アクティビティ
      ALEipUser loginName = ALEipUtils.getALEipUser(uid);
      BlogUtils.createNewCommentActivity(
        entry,
        loginName.getName().getValue(),
        blogcomment);
      // あなた宛のお知らせ
      List<String> recipientNameList = new ArrayList<String>();
      for (ALEipUser recipient : recipientList) {
        recipientNameList.add(recipient.getName().toString());
      }
      BlogUtils.createNewBlogTopicActivity(entry, loginName
        .getName()
        .toString(), recipientNameList, blogcomment);

      // メール送信
      if (sendEmailToPC || sendEmailToCellular) {
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(recipientList, ALEipUtils
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
        ALMailService.sendAdminMailAsync(new ALAdminMailContext(
          orgId,
          (int) login_user.getUserId().getValue(),
          messageList,
          ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_BLOG)));

      }
    } catch (Exception e) {
      Database.rollback();
      logger.error("BlogEntryCommentFormData.insertFormData", e);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * 当該ブログの更新通知ユーザーを習得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALDBErrorException
   * @throws ALPageNotFoundException
   */
  private List<ALEipUser> getRecipientList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    Integer loginUserId = ALEipUtils.getUserId(rundata);

    // 関連トピックをすべて取得する
    EipTBlogEntry parenttopic =
      BlogUtils.getEipTBlogParentEntry(rundata, context);
    SelectQuery<EipTBlogComment> topicQuery =
      Database.query(EipTBlogComment.class);
    Expression topicExp =
      ExpressionFactory.matchDbExp(EipTBlogComment.ENTRY_ID_COLUMN, parenttopic
        .getEntryId());
    topicQuery.setQualifier(topicExp);

    // トピックに関連する全てのユーザーIDを取得する
    List<Integer> userIdList = new ArrayList<Integer>();
    Integer userId = parenttopic.getOwnerId();
    if (!loginUserId.equals(userId)) {
      userIdList.add(userId);
    }
    List<EipTBlogComment> topicList = topicQuery.fetchList();
    for (EipTBlogComment topic : topicList) {
      userId = topic.getOwnerId();
      if (!userId.equals(loginUserId) && !userIdList.contains(userId)) {
        userIdList.add(userId);
      }
    }

    if (userIdList.isEmpty()) {
      return new ArrayList<ALEipUser>(0);
    }

    // ユーザーIDからユーザー情報を取得する。
    SelectQuery<TurbineUser> userQuery = Database.query(TurbineUser.class);
    Expression userExp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, userIdList);
    userQuery.setQualifier(userExp);
    return ALEipUtils.getUsersFromSelectQuery(userQuery);

  }

  /**
   * パソコンへ送信するメールの内容を作成する．
   * 
   * @return
   */
  private String createMsgForPc(RunData rundata) {
    ALEipUser user = ALEipUtils.getALEipUser(rundata);
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
    StringWriter out = null;
    String e_mail_addr = user2.getEmail();

    try {
      VelocityService service =
        (VelocityService) ((TurbineServices) TurbineServices.getInstance())
          .getService(VelocityService.SERVICE_NAME);
      Context context = service.getContext();

      context.put("userLastName", user2.getLastName());
      context.put("userFirstName", user2.getFirstName());

      if (!e_mail_addr.equals("")) {
        context.put("mailAddress", "(" + e_mail_addr + ")");
      }

      context.put("mailNoticeMSG", ALLocalizationUtils
        .getl10n("BLOG_YOU_GOT_MAIL_NOTICE"));

      context.put("comment", ALLocalizationUtils
        .getl10n("BLOG_MAIL_NOTICE_COMMENT"));
      context.put("commentValue", comment.getValue());

      context.put("accessToAlias", "["
        + ALOrgUtilsService.getAlias()
        + ALLocalizationUtils.getl10n("BLOG_MAIL_NOTICE_ACCESS_TO")
        + "]");

      if (enableAsp) {
        context.put("globalUrl1", ALMailUtils.getGlobalurl());
      } else {
        context.put("outsideOffice", ALLocalizationUtils
          .getl10n("BLOG_MAIL_NOTICE_OUTSIDE_OFFICE"));
        context.put("globalurl2", ALMailUtils.getGlobalurl());
        context.put("insideOffice", ALLocalizationUtils
          .getl10n("BLOG_MAIL_NOTICE_INSIDE_OFFICE"));
        context.put("globalUrl3", ALMailUtils.getLocalurl());
      }
      context.put("Alias", ALOrgUtilsService.getAlias());

      out = new StringWriter();
      service.handleRequest(context, "mail/getBlogNotice.vm", out);
      out.flush();
      return out.toString();
    } catch (Exception e) {
      StringBuffer trace = new StringBuffer();
      String message = e.getMessage();
      logger.warn(message, e);
      e.printStackTrace();
      trace.append("\n").append(message).append("\n").append(e.toString());
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
  private String createMsgForCellPhone(RunData rundata, int destUserID) {
    ALEipUser user = ALEipUtils.getALEipUser(rundata);
    ALBaseUser user2 = null;
    try {
      user2 =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(user
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    StringWriter out = null;
    String e_mail_addr = user2.getEmail();

    try {
      VelocityService service =
        (VelocityService) ((TurbineServices) TurbineServices.getInstance())
          .getService(VelocityService.SERVICE_NAME);
      Context context = service.getContext();

      context.put("userLastName", user2.getLastName());
      context.put("userFirstName", user2.getFirstName());

      if (!e_mail_addr.equals("")) {
        context.put("mailAddress", "(" + e_mail_addr + ")");
      }

      context.put("mailNoticeMSG", ALLocalizationUtils
        .getl10n("BLOG_YOU_GOT_MAIL_NOTICE"));

      context.put("comment", ALLocalizationUtils
        .getl10n("BLOG_MAIL_NOTICE_COMMENT"));
      context.put("commentValue", comment.getValue());

      ALEipUser destUser;
      try {
        destUser = ALEipUtils.getALEipUser(destUserID);
      } catch (ALDBErrorException ex) {
        logger.error("BlogEntryCommentFormData.createMsgForCellPhone", ex);
        return "";
      }

      context.put("accessToAlias", "["
        + ALOrgUtilsService.getAlias()
        + ALLocalizationUtils.getl10n("BLOG_MAIL_NOTICE_ACCESS_TO")
        + "]");
      context.put("globalUrl1", ALMailUtils.getGlobalurl()
        + "?key="
        + ALCellularUtils.getCellularKey(destUser));

      context.put("Alias", ALOrgUtilsService.getAlias());

      out = new StringWriter();
      service.handleRequest(context, "mail/getBlogNotice.vm", out);
      out.flush();
      return out.toString();
    } catch (Exception e) {
      StringBuffer trace = new StringBuffer();
      String message = e.getMessage();
      logger.warn(message, e);
      e.printStackTrace();
      trace.append("\n").append(message).append("\n").append(e.toString());
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
      boolean res = false;
      if (isOverQuota()) {
        msgList.add(ALLocalizationUtils
          .getl10n("COMMON_FULL_DISK_DELETE_DETA_OR_CHANGE_PLAN"));
      } else {
        res =
          (setFormData(rundata, context, msgList) && validate(msgList) && insertFormData(
            rundata,
            context,
            msgList));
      }
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
