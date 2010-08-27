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
package com.aimluck.eip.deletesample;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
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
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * サンプルデータ削除のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class DeleteSampleFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(DeleteSampleFormData.class.getName());

  private DataContext dataContext;

  ArrayList<String> fpaths = null;

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
    dataContext = DatabaseOrmService.getInstance().getDataContext();
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
      DataContext dataContext =
        DatabaseOrmService.getInstance().getDataContext();

      // 削除プログラム開始

      List<Integer> ids = new ArrayList<Integer>();
      ids.add(Integer.valueOf(4));
      ids.add(Integer.valueOf(5));
      ids.add(Integer.valueOf(6));

      fpaths = new ArrayList<String>();

      // アップデート作業
      // update(ids);

      // ブログ処理
      updateBlog(dataContext, ids);

      // アドレス帳処理
      updateAddressbook(dataContext, ids);

      // メールアカウント処理
      updateMailaccount(dataContext, ids);

      // 共有フォルダ処理
      // updateCabinet(dataContext, ids);

      // メール処理
      updateMail(dataContext, ids);

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
      updateMemo(dataContext, ids);

      // メッセージ板処理
      updateMsgboard(dataContext, ids);

      // ノート処理
      updateNote(dataContext, ids);

      // タイムカード処理
      updateTimecard(dataContext, ids);

      // ToDo処理
      updateTodo(dataContext, ids);

      // スケジュール処理
      updateSchedule(dataContext, ids);

      // ワークフロー処理
      updateWorkflow(dataContext, ids);

      dataContext.commitChanges();

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

  private void updateBlog(DataContext dataContext, List<Integer> ids) {
    // SelectQuery blogquery1 = new SelectQuery(EipTBlogThema.class);
    // Expression blogexp1 = ExpressionFactory.inExp(
    // EipTBlogThema.CREATE_USER_ID_PROPERTY, ids);
    // blogquery1.setQualifier(blogexp1);
    // List bloglist1 = dataContext.performQuery(blogquery1);
    // if (bloglist1 != null && bloglist1.size() > 0) {
    // dataContext.deleteObjects(bloglist1);
    // }

    SelectQuery blogquery2 = new SelectQuery(EipTBlogFootmarkMap.class);
    Expression blogexp2 =
      ExpressionFactory.inExp(EipTBlogFootmarkMap.USER_ID_PROPERTY, ids);
    blogquery2.setQualifier(blogexp2);
    List<?> bloglist2 = dataContext.performQuery(blogquery2);
    if (bloglist2 != null && bloglist2.size() > 0) {
      dataContext.deleteObjects(bloglist2);
    }

    SelectQuery blogquery3 = new SelectQuery(EipTBlogFile.class);
    Expression blogexp3 =
      ExpressionFactory.inExp(EipTBlogFile.OWNER_ID_PROPERTY, ids);
    blogquery3.setQualifier(blogexp3);
    List<?> Bloglist3 = dataContext.performQuery(blogquery3);

    if (Bloglist3 != null && Bloglist3.size() > 0) {
      int size = Bloglist3.size();
      for (int i = 0; i < size; i++) {
        EipTBlogFile file = (EipTBlogFile) Bloglist3.get(i);
        fpaths.add(getSaveDirPath(
          org_id,
          file.getOwnerId().intValue(),
          FOLDER_FILEDIR,
          "blog")
          + ((EipTBlogFile) Bloglist3.get(i)).getFilePath());
      }
    }

    if (Bloglist3 != null && Bloglist3.size() > 0) {
      dataContext.deleteObjects(Bloglist3);
    }

    SelectQuery blogquery4 = new SelectQuery(EipTBlogComment.class);
    Expression blogexp4 =
      ExpressionFactory.inExp(EipTBlogComment.OWNER_ID_PROPERTY, ids);
    blogquery4.setQualifier(blogexp4);
    List<?> bloglist4 = dataContext.performQuery(blogquery4);
    if (bloglist4 != null && bloglist4.size() > 0) {
      dataContext.deleteObjects(bloglist4);
    }

    SelectQuery Blogquery5 = new SelectQuery(EipTBlogEntry.class);
    Expression blogexp5 =
      ExpressionFactory.inExp(EipTBlogEntry.OWNER_ID_PROPERTY, ids);
    Blogquery5.setQualifier(blogexp5);
    List<?> bloglist5 = dataContext.performQuery(Blogquery5);
    if (bloglist5 != null && bloglist5.size() > 0) {
      dataContext.deleteObjects(bloglist5);
    }

    SelectQuery blogquery6 = new SelectQuery(EipTBlog.class);
    Expression blogexp6 =
      ExpressionFactory.inExp(EipTBlog.OWNER_ID_PROPERTY, ids);
    blogquery6.setQualifier(blogexp6);
    List<?> bloglist6 = dataContext.performQuery(blogquery6);
    if (bloglist6 != null && bloglist6.size() > 0) {
      dataContext.deleteObjects(bloglist6);
    }

  }

  private void updateAddressbook(DataContext dataContext, List<Integer> ids) {
    SelectQuery addressquery1 = new SelectQuery(EipMAddressbookCompany.class);
    Expression addressexp1 =
      ExpressionFactory.inExp(
        EipMAddressbookCompany.CREATE_USER_ID_PROPERTY,
        ids);
    addressquery1.setQualifier(addressexp1);
    List<?> addresslist1 = dataContext.performQuery(addressquery1);
    if (addresslist1 != null && addresslist1.size() > 0) {
      dataContext.deleteObjects(addresslist1);
    }

    SelectQuery addressquery2 = new SelectQuery(EipMAddressbook.class);
    Expression addressexp2 =
      ExpressionFactory.inExp(EipMAddressbook.OWNER_ID_PROPERTY, ids);
    addressquery2.setQualifier(addressexp2);
    List<?> addresslist2 = dataContext.performQuery(addressquery2);
    if (addresslist2 != null && addresslist2.size() > 0) {
      dataContext.deleteObjects(addresslist2);
    }
  }

  private void updateMailaccount(DataContext dataContext, List<Integer> ids) {
    SelectQuery mailquery1 = new SelectQuery(EipMMailAccount.class);
    Expression mailexp1 =
      ExpressionFactory.inExp(EipMMailAccount.USER_ID_PROPERTY, ids);
    mailquery1.setQualifier(mailexp1);
    List<?> maillist1 = dataContext.performQuery(mailquery1);
    if (maillist1 != null && maillist1.size() > 0) {
      dataContext.deleteObjects(maillist1);
    }
  }

  // private void updateCabinet(DataContext dataContext, List ids) {
  // SelectQuery cabinetquery1 = new SelectQuery(EipTCabinetFile.class);
  // Expression cabinetexp1 = ExpressionFactory.inExp(
  // EipTCabinetFile.CREATE_USER_ID_PROPERTY, ids);
  // cabinetquery1.setQualifier(cabinetexp1);
  // List cabinetlist1 = dataContext.performQuery(cabinetquery1);
  // if (cabinetlist1 != null && cabinetlist1.size() > 0) {
  // int size = cabinetlist1.size();
  // for (int i = 0; i < size; i++) {
  // EipTCabinetFile file = (EipTCabinetFile) cabinetlist1.get(i);
  // fpaths
  // .add(getSaveDirPath(org_id, -1, FOLDER_FILEDIR, "cabinet")
  // + ((EipTCabinetFile) cabinetlist1.get(i)).getFilePath());
  // }
  // }
  // if (cabinetlist1 != null && cabinetlist1.size() > 0) {
  // dataContext.deleteObjects(cabinetlist1);
  // }
  //
  // SelectQuery cabinetquery2 = new SelectQuery(EipTCabinetFolder.class);
  // Expression cabinetexp2 = ExpressionFactory.inExp(
  // EipTCabinetFolder.CREATE_USER_ID_PROPERTY, ids);
  // cabinetquery2.setQualifier(cabinetexp2);
  // List cabinetlist2 = dataContext.performQuery(cabinetquery2);
  // if (cabinetlist2 != null && cabinetlist2.size() > 0) {
  // dataContext.deleteObjects(cabinetlist2);
  // }
  //
  // }

  private void updateMail(DataContext dataContext, List<Integer> ids) {
    SelectQuery mailquery1 = new SelectQuery(EipTMail.class);
    Expression mailexp1 =
      ExpressionFactory.inExp(EipTMail.USER_ID_PROPERTY, ids);
    mailquery1.setQualifier(mailexp1);
    List<?> maillist1 = dataContext.performQuery(mailquery1);
    if (maillist1 != null && maillist1.size() > 0) {
      dataContext.deleteObjects(maillist1);
    }
  }

  private void updateMemo(DataContext dataContext, List<Integer> ids) {
    SelectQuery memoquery1 = new SelectQuery(EipTMemo.class);
    Expression memoexp1 =
      ExpressionFactory.inExp(EipTMemo.OWNER_ID_PROPERTY, ids);
    memoquery1.setQualifier(memoexp1);
    List<?> memolist1 = dataContext.performQuery(memoquery1);
    if (memolist1 != null && memolist1.size() > 0) {
      dataContext.deleteObjects(memolist1);
    }
  }

  private void updateMsgboard(DataContext dataContext, List<Integer> ids) {
    // SelectQuery msgquery1 = new SelectQuery(EipTMsgboardCategory.class);
    // Expression msgexp1 = ExpressionFactory.inDbExp(
    // EipTMsgboardCategory.TURBINE_USER_PROPERTY + "."
    // + TurbineUser.USER_ID_PK_COLUMN, ids);
    // msgquery1.setQualifier(msgexp1);
    // List msglist1 = dataContext.performQuery(msgquery1);
    // if (msglist1 != null && msglist1.size() > 0) {
    // dataContext.deleteObjects(msglist1);
    // }

    // SelectQuery msgquery2 = new SelectQuery(EipTMsgboardCategoryMap.class);
    // Expression msgexp2 = ExpressionFactory.inExp(
    // EipTMsgboardCategoryMap.USER_ID_PROPERTY, ids);
    // msgquery2.setQualifier(msgexp2);
    // List msglist2 = dataContext.performQuery(msgquery2);
    // if (msglist2 != null && msglist2.size() > 0) {
    // dataContext.deleteObjects(msglist2);
    // }

    SelectQuery msgquery3 = new SelectQuery(EipTMsgboardFile.class);
    Expression msgexp3 =
      ExpressionFactory.inExp(EipTMsgboardFile.OWNER_ID_PROPERTY, ids);
    msgquery3.setQualifier(msgexp3);
    List<?> msglist3 = dataContext.performQuery(msgquery3);
    if (msglist3 != null && msglist3.size() > 0) {
      int size = msglist3.size();
      for (int i = 0; i < size; i++) {
        EipTMsgboardFile file = (EipTMsgboardFile) msglist3.get(i);
        fpaths.add(getSaveDirPath(
          org_id,
          file.getOwnerId().intValue(),
          FOLDER_FILEDIR,
          "msgboard")
          + ((EipTMsgboardFile) msglist3.get(i)).getFilePath());
      }
      dataContext.deleteObjects(msglist3);
    }

    SelectQuery msgquery4 = new SelectQuery(EipTMsgboardTopic.class);
    Expression msgexp4 =
      ExpressionFactory.inExp(EipTMsgboardTopic.OWNER_ID_PROPERTY, ids);
    msgquery4.setQualifier(msgexp4);
    List<?> msglist4 = dataContext.performQuery(msgquery4);
    if (msglist4 != null && msglist4.size() > 0) {
      dataContext.deleteObjects(msglist4);
    }

  }

  private void updateNote(DataContext dataContext, List<Integer> ids) {
    SelectQuery notequery1 = new SelectQuery(EipTNote.class);
    Expression noteexp1 =
      ExpressionFactory.inExp(EipTNote.OWNER_ID_PROPERTY, ids);
    notequery1.setQualifier(noteexp1);
    List<?> notelist1 = dataContext.performQuery(notequery1);
    if (notelist1 != null && notelist1.size() > 0) {
      dataContext.deleteObjects(notelist1);
    }

    SelectQuery notequery2 = new SelectQuery(EipTNoteMap.class);
    Expression noteexp2 =
      ExpressionFactory.inExp(EipTNoteMap.USER_ID_PROPERTY, ids);
    notequery2.setQualifier(noteexp2);
    List<?> notelist2 = dataContext.performQuery(notequery2);
    if (notelist2 != null && notelist2.size() > 0) {
      dataContext.deleteObjects(notelist2);
    }
  }

  private void updateTimecard(DataContext dataContext, List<Integer> ids) {
    SelectQuery timecardquery1 = new SelectQuery(EipTTimecard.class);
    Expression timecardexp1 =
      ExpressionFactory.inExp(EipTTimecard.USER_ID_PROPERTY, ids);
    timecardquery1.setQualifier(timecardexp1);
    List<?> timecardlist1 = dataContext.performQuery(timecardquery1);
    if (timecardlist1 != null && timecardlist1.size() > 0) {
      dataContext.deleteObjects(timecardlist1);
    }
  }

  private void updateTodo(DataContext dataContext, List<Integer> ids) {
    SelectQuery todoquery1 = new SelectQuery(EipTTodoCategory.class);
    Expression todoexp1 =
      ExpressionFactory.inExp(EipTTodoCategory.USER_ID_PROPERTY, ids);
    todoquery1.setQualifier(todoexp1);
    List<?> todolist1 = dataContext.performQuery(todoquery1);
    if (todolist1 != null && todolist1.size() > 0) {
      dataContext.deleteObjects(todolist1);
    }

    SelectQuery todoquery2 = new SelectQuery(EipTTodo.class);
    Expression todoexp2 =
      ExpressionFactory.inExp(EipTTodo.USER_ID_PROPERTY, ids);
    todoquery2.setQualifier(todoexp2);
    List<?> todolist2 = dataContext.performQuery(todoquery2);
    if (todolist2 != null && todolist2.size() > 0) {
      dataContext.deleteObjects(todolist2);
    }
  }

  private void updateSchedule(DataContext dataContext, List<Integer> ids) {
    SelectQuery schedulequery1 = new SelectQuery(EipTSchedule.class);
    Expression scheduleexp1 =
      ExpressionFactory.inExp(EipTSchedule.OWNER_ID_PROPERTY, ids);
    schedulequery1.setQualifier(scheduleexp1);
    List<?> schedulelist1 = dataContext.performQuery(schedulequery1);
    if (schedulelist1 != null && schedulelist1.size() > 0) {
      dataContext.deleteObjects(schedulelist1);
    }

    SelectQuery schedulequery2 = new SelectQuery(EipTScheduleMap.class);
    Expression scheduleexp2 =
      ExpressionFactory.inExp(EipTScheduleMap.USER_ID_PROPERTY, ids);
    schedulequery2.setQualifier(scheduleexp2);
    List<?> schedulelist2 = dataContext.performQuery(schedulequery2);
    if (schedulelist2 != null && schedulelist2.size() > 0) {
      dataContext.deleteObjects(schedulelist2);
    }
  }

  private void updateWorkflow(DataContext dataContext, List<Integer> ids) {
    SelectQuery workquery1 = new SelectQuery(EipTWorkflowRequestMap.class);
    Expression workexp1 =
      ExpressionFactory.inExp(EipTWorkflowRequestMap.USER_ID_PROPERTY, ids);
    workquery1.setQualifier(workexp1);
    List<?> worklist1 = dataContext.performQuery(workquery1);
    if (worklist1 != null && worklist1.size() > 0) {
      dataContext.deleteObjects(worklist1);
    }

    SelectQuery workquery2 = new SelectQuery(EipTWorkflowRequest.class);
    Expression workexp2 =
      ExpressionFactory.inExp(EipTWorkflowRequest.USER_ID_PROPERTY, ids);
    workquery2.setQualifier(workexp2);
    List<?> worklist2 = dataContext.performQuery(workquery2);
    if (worklist2 != null && worklist2.size() > 0) {
      dataContext.deleteObjects(worklist2);
    }
  }

  // private void update(List ids) {
  // // ブログのテーマIDを1に変更する。
  // EipTBlogThema blogthema = (EipTBlogThema) DataObjectUtils.objectForPK(
  // dataContext, EipTBlogThema.class, Integer.valueOf(1));
  //
  // SelectQuery updatequery1 = new SelectQuery(EipTBlogEntry.class);
  // Expression updateexp1 = ExpressionFactory.inExp(
  // EipTBlogEntry.OWNER_ID_PROPERTY, ids);
  // updatequery1.setQualifier(updateexp1);
  // List updateentrys1 = dataContext.performQuery(updatequery1);
  // if (updateentrys1 != null && updateentrys1.size() > 0) {
  // int size = updateentrys1.size();
  // for (int i = 0; i < size; i++) {
  // EipTBlogEntry entry = (EipTBlogEntry) updateentrys1.get(i);
  // entry.setEipTBlogThema(blogthema);
  // }
  // dataContext.commitChanges();
  // }
  //
  // // キャビネットファイルのフォルダIDを1に変更する。
  // EipTCabinetFolder cabinetfolder = (EipTCabinetFolder) DataObjectUtils
  // .objectForPK(dataContext, EipTCabinetFile.class, Integer.valueOf(1));
  //
  // SelectQuery updatequery2 = new SelectQuery(EipTCabinetFile.class);
  // Expression updateexp2 = ExpressionFactory.inExp(
  // EipTCabinetFile.FOLDER_ID_PROPERTY, ids);
  // updatequery2.setQualifier(updateexp2);
  // List updateentrys2 = dataContext.performQuery(updatequery2);
  // if (updateentrys2 != null && updateentrys2.size() > 0) {
  // int size = updateentrys2.size();
  // for (int i = 0; i < size; i++) {
  // EipTCabinetFile entry = (EipTCabinetFile) updateentrys2.get(i);
  // entry.setEipTCabinetFolder(cabinetfolder);
  // }
  // dataContext.commitChanges();
  // }
  //
  // // キャビネットフォルダの親IDを1に変更する。
  //
  // SelectQuery updatequery3 = new SelectQuery(EipTCabinetFolder.class);
  // Expression updateexp3 = ExpressionFactory.inExp(
  // EipTCabinetFolder.PARENT_ID_PROPERTY, ids);
  // updatequery3.setQualifier(updateexp3);
  // List updateentrys3 = dataContext.performQuery(updatequery3);
  // if (updateentrys3 != null && updateentrys3.size() > 0) {
  // int size = updateentrys3.size();
  // for (int i = 0; i < size; i++) {
  // EipTCabinetFolder entry = (EipTCabinetFolder) updateentrys3.get(i);
  // entry.setParentId(cabinetfolder.getFolderId());
  // }
  // dataContext.commitChanges();
  // }
  //
  // // メッセージボードのカテゴリーIDを１に変更する
  //
  // EipTMsgboardTopic msgboardtopic = (EipTMsgboardTopic) DataObjectUtils
  // .objectForPK(dataContext, EipTMsgboardTopic.class, Integer.valueOf(1));
  //
  // SelectQuery updatequery4 = new SelectQuery(EipTMsgboardTopic.class);
  // Expression updateexp4 = ExpressionFactory.inExp(
  // EipTMsgboardTopic.OWNER_ID_PROPERTY, ids);
  // updatequery4.setQualifier(updateexp4);
  // List updateentrys4 = dataContext.performQuery(updatequery4);
  // if (updateentrys4 != null && updateentrys4.size() > 0) {
  // int size = updateentrys4.size();
  // for (int i = 0; i < size; i++) {
  // EipTMsgboardTopic entry = (EipTMsgboardTopic) updateentrys4.get(i);
  // entry.setParentId(msgboardtopic.getTopicId());
  // }
  // dataContext.commitChanges();
  // }
  //
  // }

  private void deleteFiles(ArrayList<String> fpaths) {
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
    SelectQuery gquery = new SelectQuery(TurbineUserGroupRole.class, gexp);
    List<?> map = dataContext.performQuery(gquery);
    if (map != null && map.size() > 0) {
      dataContext.deleteObjects(map);
    }

    SelectQuery tquery = new SelectQuery(TurbineUser.class);
    Expression texp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, ids);
    tquery.setQualifier(texp);
    List<?> tusers = dataContext.performQuery(tquery);
    if (tusers != null && tusers.size() > 0) {
      int size = tusers.size();
      for (int i = 0; i < size; i++) {
        TurbineUser tuser = (TurbineUser) tusers.get(i);
        tuser.setDisabled("T");
      }
      dataContext.commitChanges();
    }

  }
}
