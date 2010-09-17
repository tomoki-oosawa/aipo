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

package com.aimluck.eip.deletesample;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFootmarkMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * サンプルデータ削除のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class DeleteSampleFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(DeleteSampleFormData.class.getName());

  protected List<String> fpaths = null;

  /** 掲示板,ブログの添付ファイルを保管するディレクトリの指定 */
  protected static final String FOLDER_FILEDIR = JetspeedResources.getString(
    "aipo.filedir",
    "");

  protected static final String FOLDER_MAILDIR = JetspeedResources.getString(
    "aipo.mail.home",
    "");

  private String org_id;

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
    org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  public void initField() {

  }

  /**
   * DeleteSampleの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {

  }

  /**
   * DeleteSampleのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return true;
  }

  /**
   * DeleteSampleをデータベースから読み出します。 <BR>
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
   * DeleteSampleをデータベースから削除します。 <BR>
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
   * DeleteSampleをデータベースに格納します。 <BR>
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
   * データベースに格納されているDeleteSampleを更新します。 <BR>
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

      // 削除プログラム開始

      List<Integer> ids = new ArrayList<Integer>();
      ids.add(Integer.valueOf(4));
      ids.add(Integer.valueOf(5));
      ids.add(Integer.valueOf(6));

      fpaths = new ArrayList<String>();

      // アップデート作業
      // update(ids);

      // ブログ処理
      updateBlog(ids);

      // アドレス帳処理
      updateAddressbook(ids);

      // メールアカウント処理
      updateMailaccount(ids);

      // 共有フォルダ処理
      // updateCabinet(ids);

      // メール処理
      updateMail(ids);

      if (ids != null && ids.size() > 0) {
        int size = ids.size();
        for (int i = 0; i < size; i++) {
          fpaths.add(FOLDER_MAILDIR
            + File.separator
            + org_id
            + File.separator
            + (ids.get(i)).toString());
        }
      }

      // メモ処理
      updateMemo(ids);

      // メッセージ板処理
      updateMsgboard(ids);

      // ノート処理
      updateNote(ids);

      // タイムカード処理
      updateTimecard(ids);

      // ToDo処理
      updateTodo(ids);

      // スケジュール処理
      updateSchedule(ids);

      // ワークフロー処理
      updateWorkflow(ids);

      Database.commit();

      // ファイル削除
      deleteFiles(fpaths);

      // サンプルユーザーを非表示にする
      updateUserhidden(ids);
      updateFlag(rundata);

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    return true;
  }

  private void updateFlag(RunData rundata) throws ProfileException {
    String portletEntryId = rundata.getParameters().getString("js_peid", null);

    String FLAG = "desa";

    Profile profile = ((JetspeedRunData) rundata).getProfile();
    Portlets portlets = profile.getDocument().getPortlets();

    Portlets[] portletList = portlets.getPortletsArray();

    PsmlParameter param = null;
    Parameter params[] = null;

    boolean hasParam = false;

    Entry[] entries = portletList[0].getEntriesArray();
    Entry entry = null;
    int ent_length = entries.length;
    for (int j = 0; j < ent_length; j++) {
      entry = entries[j];
      if (entry.getId().equals(portletEntryId)) {
        params = entry.getParameter();
        int param_len = params.length;
        for (int k = 0; k < param_len; k++) {
          if (params[k].getName().equals(FLAG)) {
            params[k].setValue("1");
            entry.setParameter(k, params[k]);
            hasParam = true;
          }
        }
        if (!hasParam) {
          param = new PsmlParameter();
          param.setName(FLAG);
          param.setValue("1");
          entries[j].addParameter(param);
        }
        break;
      }
    }
    profile.store();
  }

  private void updateBlog(List<Integer> ids) {

    SelectQuery<EipTBlogFootmarkMap> blogquery2 =
      Database.query(EipTBlogFootmarkMap.class);
    Expression blogexp2 =
      ExpressionFactory.inExp(EipTBlogFootmarkMap.USER_ID_PROPERTY, ids);
    blogquery2.setQualifier(blogexp2);
    List<EipTBlogFootmarkMap> bloglist2 = blogquery2.fetchList();
    if (bloglist2 != null && bloglist2.size() > 0) {
      Database.deleteAll(bloglist2);
    }

    SelectQuery<EipTBlogFile> blogquery3 = Database.query(EipTBlogFile.class);
    Expression blogexp3 =
      ExpressionFactory.inExp(EipTBlogFile.OWNER_ID_PROPERTY, ids);
    blogquery3.setQualifier(blogexp3);
    List<EipTBlogFile> Bloglist3 = blogquery3.fetchList();

    if (Bloglist3 != null && Bloglist3.size() > 0) {
      int size = Bloglist3.size();
      for (int i = 0; i < size; i++) {
        EipTBlogFile file = Bloglist3.get(i);
        fpaths.add(getSaveDirPath(
          org_id,
          file.getOwnerId().intValue(),
          FOLDER_FILEDIR,
          "blog")
          + (Bloglist3.get(i)).getFilePath());
      }
    }

    if (Bloglist3 != null && Bloglist3.size() > 0) {
      Database.deleteAll(Bloglist3);
    }

    SelectQuery<EipTBlogComment> blogquery4 =
      Database.query(EipTBlogComment.class);
    Expression blogexp4 =
      ExpressionFactory.inExp(EipTBlogComment.OWNER_ID_PROPERTY, ids);
    blogquery4.setQualifier(blogexp4);
    List<EipTBlogComment> bloglist4 = blogquery4.fetchList();
    if (bloglist4 != null && bloglist4.size() > 0) {
      Database.deleteAll(bloglist4);
    }

    SelectQuery<EipTBlogEntry> Blogquery5 = Database.query(EipTBlogEntry.class);
    Expression blogexp5 =
      ExpressionFactory.inExp(EipTBlogEntry.OWNER_ID_PROPERTY, ids);
    Blogquery5.setQualifier(blogexp5);
    List<EipTBlogEntry> bloglist5 = Blogquery5.fetchList();
    if (bloglist5 != null && bloglist5.size() > 0) {
      Database.deleteAll(bloglist5);
    }

    SelectQuery<EipTBlog> blogquery6 = Database.query(EipTBlog.class);
    Expression blogexp6 =
      ExpressionFactory.inExp(EipTBlog.OWNER_ID_PROPERTY, ids);
    blogquery6.setQualifier(blogexp6);
    List<EipTBlog> bloglist6 = blogquery6.fetchList();
    if (bloglist6 != null && bloglist6.size() > 0) {
      Database.deleteAll(bloglist6);
    }

  }

  private void updateAddressbook(List<Integer> ids) {
    SelectQuery<EipMAddressbookCompany> addressquery1 =
      Database.query(EipMAddressbookCompany.class);
    Expression addressexp1 =
      ExpressionFactory.inExp(
        EipMAddressbookCompany.CREATE_USER_ID_PROPERTY,
        ids);
    addressquery1.setQualifier(addressexp1);
    List<EipMAddressbookCompany> addresslist1 = addressquery1.fetchList();
    if (addresslist1 != null && addresslist1.size() > 0) {
      Database.deleteAll(addresslist1);
    }

    SelectQuery<EipMAddressbook> addressquery2 =
      Database.query(EipMAddressbook.class);
    Expression addressexp2 =
      ExpressionFactory.inExp(EipMAddressbook.OWNER_ID_PROPERTY, ids);
    addressquery2.setQualifier(addressexp2);
    List<EipMAddressbook> addresslist2 = addressquery2.fetchList();
    if (addresslist2 != null && addresslist2.size() > 0) {
      Database.deleteAll(addresslist2);
    }
  }

  private void updateMailaccount(List<Integer> ids) {
    SelectQuery<EipMMailAccount> mailquery1 =
      Database.query(EipMMailAccount.class);
    Expression mailexp1 =
      ExpressionFactory.inExp(EipMMailAccount.USER_ID_PROPERTY, ids);
    mailquery1.setQualifier(mailexp1);
    List<EipMMailAccount> maillist1 = mailquery1.fetchList();
    if (maillist1 != null && maillist1.size() > 0) {
      Database.deleteAll(maillist1);
    }
  }

  private void updateMail(List<Integer> ids) {
    SelectQuery<EipTMail> mailquery1 = Database.query(EipTMail.class);
    Expression mailexp1 =
      ExpressionFactory.inExp(EipTMail.USER_ID_PROPERTY, ids);
    mailquery1.setQualifier(mailexp1);
    List<EipTMail> maillist1 = mailquery1.fetchList();
    if (maillist1 != null && maillist1.size() > 0) {
      Database.deleteAll(maillist1);
    }
  }

  private void updateMemo(List<Integer> ids) {
    SelectQuery<EipTMemo> memoquery1 = Database.query(EipTMemo.class);
    Expression memoexp1 =
      ExpressionFactory.inExp(EipTMemo.OWNER_ID_PROPERTY, ids);
    memoquery1.setQualifier(memoexp1);
    List<EipTMemo> memolist1 = memoquery1.fetchList();
    if (memolist1 != null && memolist1.size() > 0) {
      Database.deleteAll(memolist1);
    }
  }

  private void updateMsgboard(List<Integer> ids) {
    SelectQuery<EipTMsgboardFile> msgquery3 =
      Database.query(EipTMsgboardFile.class);
    Expression msgexp3 =
      ExpressionFactory.inExp(EipTMsgboardFile.OWNER_ID_PROPERTY, ids);
    msgquery3.setQualifier(msgexp3);
    List<EipTMsgboardFile> msglist3 = msgquery3.fetchList();
    if (msglist3 != null && msglist3.size() > 0) {
      int size = msglist3.size();
      for (int i = 0; i < size; i++) {
        EipTMsgboardFile file = msglist3.get(i);
        fpaths.add(getSaveDirPath(
          org_id,
          file.getOwnerId().intValue(),
          FOLDER_FILEDIR,
          "msgboard")
          + msglist3.get(i).getFilePath());
      }
      Database.deleteAll(msglist3);
    }

    SelectQuery<EipTMsgboardTopic> msgquery4 =
      Database.query(EipTMsgboardTopic.class);
    Expression msgexp4 =
      ExpressionFactory.inExp(EipTMsgboardTopic.OWNER_ID_PROPERTY, ids);
    msgquery4.setQualifier(msgexp4);
    List<EipTMsgboardTopic> msglist4 = msgquery4.fetchList();
    if (msglist4 != null && msglist4.size() > 0) {
      Database.deleteAll(msglist4);
    }

  }

  private void updateNote(List<Integer> ids) {
    SelectQuery<EipTNote> notequery1 = Database.query(EipTNote.class);
    Expression noteexp1 =
      ExpressionFactory.inExp(EipTNote.OWNER_ID_PROPERTY, ids);
    notequery1.setQualifier(noteexp1);
    List<EipTNote> notelist1 = notequery1.fetchList();
    if (notelist1 != null && notelist1.size() > 0) {
      Database.deleteAll(notelist1);
    }

    SelectQuery<EipTNoteMap> notequery2 = Database.query(EipTNoteMap.class);
    Expression noteexp2 =
      ExpressionFactory.inExp(EipTNoteMap.USER_ID_PROPERTY, ids);
    notequery2.setQualifier(noteexp2);
    List<EipTNoteMap> notelist2 = notequery2.fetchList();
    if (notelist2 != null && notelist2.size() > 0) {
      Database.deleteAll(notelist2);
    }
  }

  private void updateTimecard(List<Integer> ids) {
    SelectQuery<EipTTimecard> timecardquery1 =
      Database.query(EipTTimecard.class);
    Expression timecardexp1 =
      ExpressionFactory.inExp(EipTTimecard.USER_ID_PROPERTY, ids);
    timecardquery1.setQualifier(timecardexp1);
    List<EipTTimecard> timecardlist1 = timecardquery1.fetchList();
    if (timecardlist1 != null && timecardlist1.size() > 0) {
      Database.deleteAll(timecardlist1);
    }
  }

  private void updateTodo(List<Integer> ids) {
    SelectQuery<EipTTodoCategory> todoquery1 =
      Database.query(EipTTodoCategory.class);
    Expression todoexp1 =
      ExpressionFactory.inExp(EipTTodoCategory.USER_ID_PROPERTY, ids);
    todoquery1.setQualifier(todoexp1);
    List<EipTTodoCategory> todolist1 = todoquery1.fetchList();
    if (todolist1 != null && todolist1.size() > 0) {
      Database.deleteAll(todolist1);
    }

    SelectQuery<EipTTodo> todoquery2 = Database.query(EipTTodo.class);
    Expression todoexp2 =
      ExpressionFactory.inExp(EipTTodo.USER_ID_PROPERTY, ids);
    todoquery2.setQualifier(todoexp2);
    List<EipTTodo> todolist2 = todoquery2.fetchList();
    if (todolist2 != null && todolist2.size() > 0) {
      Database.deleteAll(todolist2);
    }
  }

  private void updateSchedule(List<Integer> ids) {
    SelectQuery<EipTSchedule> schedulequery1 =
      Database.query(EipTSchedule.class);
    Expression scheduleexp1 =
      ExpressionFactory.inExp(EipTSchedule.OWNER_ID_PROPERTY, ids);
    schedulequery1.setQualifier(scheduleexp1);
    List<EipTSchedule> schedulelist1 = schedulequery1.fetchList();
    if (schedulelist1 != null && schedulelist1.size() > 0) {
      Database.deleteAll(schedulelist1);
    }

    SelectQuery<EipTScheduleMap> schedulequery2 =
      Database.query(EipTScheduleMap.class);
    Expression scheduleexp2 =
      ExpressionFactory.inExp(EipTScheduleMap.USER_ID_PROPERTY, ids);
    schedulequery2.setQualifier(scheduleexp2);
    List<EipTScheduleMap> schedulelist2 = schedulequery2.fetchList();
    if (schedulelist2 != null && schedulelist2.size() > 0) {
      Database.deleteAll(schedulelist2);
    }
  }

  private void updateWorkflow(List<Integer> ids) {
    SelectQuery<EipTWorkflowRequestMap> workquery1 =
      Database.query(EipTWorkflowRequestMap.class);
    Expression workexp1 =
      ExpressionFactory.inExp(EipTWorkflowRequestMap.USER_ID_PROPERTY, ids);
    workquery1.setQualifier(workexp1);
    List<EipTWorkflowRequestMap> worklist1 = workquery1.fetchList();
    if (worklist1 != null && worklist1.size() > 0) {
      Database.deleteAll(worklist1);
    }

    SelectQuery<EipTWorkflowRequest> workquery2 =
      Database.query(EipTWorkflowRequest.class);
    Expression workexp2 =
      ExpressionFactory.inExp(EipTWorkflowRequest.USER_ID_PROPERTY, ids);
    workquery2.setQualifier(workexp2);
    List<EipTWorkflowRequest> worklist2 = workquery2.fetchList();
    if (worklist2 != null && worklist2.size() > 0) {
      Database.deleteAll(worklist2);
    }
  }

  private void deleteFiles(List<String> fpaths) {
    if (fpaths == null) {
      return;
    }
    if (fpaths.size() > 0) {
      // ローカルファイルに保存されているファイルを削除する．
      File file = null;
      int fsize = fpaths.size();
      for (int i = 0; i < fsize; i++) {
        file = new File(fpaths.get(i));
        if (file.exists()) {
          ALEipUtils.deleteFolder(file);
        }
      }
    }
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId, int uid, String filter,
      String type) {
    if (uid > 0) {
      return filter
        + File.separator
        + orgId
        + File.separator
        + type
        + File.separator
        + Integer.toString(uid);
    } else {
      return filter + File.separator + orgId + File.separator + type;
    }
  }

  private void updateUserhidden(List<Integer> ids) {

    Expression gexp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, ids);
    SelectQuery<TurbineUserGroupRole> gquery =
      Database.query(TurbineUserGroupRole.class, gexp);
    List<TurbineUserGroupRole> map = gquery.fetchList();
    if (map != null && map.size() > 0) {
      Database.deleteAll(map);
    }

    SelectQuery<TurbineUser> tquery = Database.query(TurbineUser.class);
    Expression texp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, ids);
    tquery.setQualifier(texp);
    List<TurbineUser> tusers = tquery.fetchList();
    if (tusers != null && tusers.size() > 0) {
      int size = tusers.size();
      for (int i = 0; i < size; i++) {
        TurbineUser tuser = tusers.get(i);
        tuser.setDisabled("T");
      }
      Database.commit();
    }

  }
}
