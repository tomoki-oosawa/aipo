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
package com.aimluck.eip.system;

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
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFootmarkMap;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineFile;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineLike;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineUrl;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Query;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * サンプルデータ削除のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class SystemDeleteSampleFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemDeleteSampleFormData.class.getName());

  protected List<String> fpaths = null;

  /** 掲示板,ブログの添付ファイルを保管するディレクトリの指定 */
  protected static final String FOLDER_FILEDIR = JetspeedResources.getString(
    "aipo.filedir",
    "");

  protected static final String FOLDER_MAILDIR = JetspeedResources.getString(
    "aipo.mail.home",
    "");

  private String orgId;

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
    orgId = Database.getDomainName();
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
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
      updateCabinet(ids);

      // メール処理
      updateMail(ids);

      if (ids != null && ids.size() > 0) {
        int size = ids.size();
        for (int i = 0; i < size; i++) {
          fpaths.add(FOLDER_MAILDIR
            + File.separator
            + orgId
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

      // タイムライン処理
      updateTimeline(ids);

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

      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();
      ALEipManager.getInstance().reloadCompany();
      ALEipManager.getInstance().reloadPosition();

      // debug
      updateFlag(rundata);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("system", ex);
      return false;
    }

    return true;
  }

  /**
   * @param ids
   */
  private void updateCabinet(List<Integer> ids) {

    SelectQuery<EipTCabinetFolder> query =
      Database.query(EipTCabinetFolder.class);
    Expression exp1 =
      ExpressionFactory.inExp(EipTCabinetFolder.CREATE_USER_ID_PROPERTY, ids);
    query.setQualifier(exp1);

    List<EipTCabinetFolder> list = query.fetchList();

    for (EipTCabinetFolder folder : list) {
      Integer folderId = folder.getFolderId();
      SelectQuery<EipTCabinetFolder> query1 =
        Database.query(EipTCabinetFolder.class);
      exp1 =
        ExpressionFactory.matchDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
          Integer.valueOf(folderId));
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTCabinetFolder.PARENT_ID_PROPERTY,
          Integer.valueOf(folderId));
      query1.setQualifier(exp1.orExp(exp2));
      List<EipTCabinetFolder> list1 = query1.fetchList();

      ArrayList<Integer> folderids = new ArrayList<Integer>();
      for (EipTCabinetFolder folder2 : list1) {
        folderids.add(folder2.getFolderId());
      }

      // データベースのフォルダ情報を削除する．
      SelectQuery<EipTCabinetFolder> delfolderquery =
        Database.query(EipTCabinetFolder.class);
      Expression delfolderexp =
        ExpressionFactory.inDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
          folderids);
      delfolderquery.setQualifier(delfolderexp);
      List<EipTCabinetFolder> delFolderList = delfolderquery.fetchList();

      // フォルダ情報を削除
      for (EipTCabinetFolder delfolder : delFolderList) {
        List<EipTCabinetFile> cabinetfiles =
          CabinetUtils.getEipTCabinetFileList(delfolder
            .getFolderId()
            .intValue());
        if ((cabinetfiles != null) && (cabinetfiles.size() > 0)) {
          int tsize = cabinetfiles.size();
          for (int k = 0; k < tsize; k++) {
            fpaths.add((cabinetfiles.get(k)).getFilePath());
          }
        }
        // フォルダ情報を削除
        Database.delete(delfolder);
      }
    }
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
      for (EipTBlogFile file : Bloglist3) {
        fpaths.add(getSaveDirPath(
          orgId,
          file.getOwnerId().intValue(),
          FOLDER_FILEDIR,
          "blog")
          + file.getFilePath());
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

    List<EipTBlogThema> deleteThemeList = new ArrayList<EipTBlogThema>();

    SelectQuery<EipTBlogEntry> Blogquery5 = Database.query(EipTBlogEntry.class);
    Expression blogexp5 =
      ExpressionFactory.inExp(EipTBlogEntry.OWNER_ID_PROPERTY, ids);
    Blogquery5.setQualifier(blogexp5);
    List<EipTBlogEntry> bloglist5 = Blogquery5.fetchList();
    if (bloglist5 != null && bloglist5.size() > 0) {
      for (EipTBlogEntry entry : bloglist5) {
        EipTBlogThema theme = entry.getEipTBlogThema();
        if (!theme.getThemaId().equals(1)) {
          deleteThemeList.add(theme);
        }
      }
      Database.deleteAll(bloglist5);

      if (deleteThemeList.size() > 0) {
        List<Integer> themeIds = new ArrayList<Integer>();
        for (EipTBlogThema theme : deleteThemeList) {
          themeIds.add(theme.getThemaId());
        }
        Expression themaExp =
          ExpressionFactory.inExp(
            EipTBlogEntry.EIP_TBLOG_THEMA_PROPERTY,
            themeIds);
        Query<EipTBlogEntry> themaQuery =
          Database.query(EipTBlogEntry.class, themaExp);
        List<EipTBlogEntry> themaEntry = themaQuery.fetchList();
        EipTBlogThema defaultThema = BlogUtils.getEipTBlogThema(1L);
        for (EipTBlogEntry entry : themaEntry) {
          entry.setEipTBlogThema(defaultThema);
        }
        Database.deleteAll(deleteThemeList);
      }
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
    Expression fileExp =
      ExpressionFactory.inExp(EipTMsgboardFile.OWNER_ID_PROPERTY, ids);
    SelectQuery<EipTMsgboardFile> fileQuery =
      Database.query(EipTMsgboardFile.class, fileExp);
    List<EipTMsgboardFile> fileList = fileQuery.fetchList();
    if (fileList != null && fileList.size() > 0) {
      for (EipTMsgboardFile file : fileList) {
        fpaths.add(getSaveDirPath(
          orgId,
          file.getOwnerId().intValue(),
          FOLDER_FILEDIR,
          "msgboard")
          + file.getFilePath());
      }
      Database.deleteAll(fileList);
    }

    Expression topicExp =
      ExpressionFactory.inExp(EipTMsgboardTopic.OWNER_ID_PROPERTY, ids);
    SelectQuery<EipTMsgboardTopic> topicQuery =
      Database.query(EipTMsgboardTopic.class, topicExp);
    List<EipTMsgboardTopic> topicList = topicQuery.fetchList();
    if (topicList != null && topicList.size() > 0) {
      Database.deleteAll(topicList);
    }

    Expression defaultCategoryExp =
      ExpressionFactory.matchDbExp(
        EipTMsgboardCategory.CATEGORY_ID_PK_COLUMN,
        Integer.valueOf(1));
    SelectQuery<EipTMsgboardCategory> defaultCategoryQuery =
      Database.query(EipTMsgboardCategory.class, defaultCategoryExp);
    EipTMsgboardCategory defaultCategory = defaultCategoryQuery.fetchSingle();

    SelectQuery<EipTMsgboardCategory> categoryQuery =
      Database.query(EipTMsgboardCategory.class);
    List<EipTMsgboardCategory> categoryList = categoryQuery.fetchList();

    List<Integer> categoryIdList = new ArrayList<Integer>();
    List<EipTMsgboardCategory> deleteCategoryList =
      new ArrayList<EipTMsgboardCategory>();
    if (categoryList != null && categoryList.size() > 0) {
      for (EipTMsgboardCategory category : categoryList) {
        if (ids.contains(category.getTurbineUser().getUserId())) {
          if (!categoryIdList.contains(category.getCategoryId())) {
            categoryIdList.add(category.getCategoryId());
            deleteCategoryList.add(category);
          }
        }
      }
    }

    if (categoryIdList.size() > 0) {
      Expression deletedCategoryTopicExp =
        ExpressionFactory.inExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY,
          categoryIdList);
      SelectQuery<EipTMsgboardTopic> deletedCategoryTopicQuery =
        Database.query(EipTMsgboardTopic.class, deletedCategoryTopicExp);
      List<EipTMsgboardTopic> deletedCategoryTopicList =
        deletedCategoryTopicQuery.fetchList();
      for (EipTMsgboardTopic topic : deletedCategoryTopicList) {
        topic.setEipTMsgboardCategory(defaultCategory);
      }
      Database.deleteAll(deleteCategoryList);
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

  private void updateTimeline(List<Integer> ids) {
    SelectQuery<EipTTimeline> timelineQuery1 =
      Database.query(EipTTimeline.class);
    Expression timelineExp1 =
      ExpressionFactory.inExp(EipTTimeline.OWNER_ID_PROPERTY, ids);
    timelineQuery1.setQualifier(timelineExp1);
    List<EipTTimeline> timelineList1 = timelineQuery1.fetchList();

    List<Integer> timelineIds = new ArrayList<Integer>();
    if (timelineList1 != null && timelineList1.size() > 0) {
      for (EipTTimeline tmp : timelineList1) {
        timelineIds.add(tmp.getTimelineId());
      }

      SelectQuery<EipTTimeline> timelineQuery2 =
        Database.query(EipTTimeline.class);
      Expression timelineExp2 =
        ExpressionFactory.inDbExp(EipTTimeline.PARENT_ID_COLUMN, timelineIds);
      timelineQuery2.setQualifier(timelineExp2);
      List<EipTTimeline> timelineList2 = timelineQuery2.fetchList();
      for (EipTTimeline tmp : timelineList2) {
        timelineIds.add(tmp.getTimelineId());
      }
      timelineList1.addAll(timelineList2);

      if (timelineList1.size() > 0) {

        SelectQuery<EipTTimelineFile> timelineFileQuery1 =
          Database.query(EipTTimelineFile.class);
        Expression timelineFileExp1 =
          ExpressionFactory.inExp(EipTTimelineFile.OWNER_ID_PROPERTY, ids);
        timelineFileQuery1.setQualifier(timelineFileExp1);
        List<EipTTimelineFile> timelineFileList1 =
          timelineFileQuery1.fetchList();
        if (timelineFileList1 != null && timelineFileList1.size() > 0) {
          Database.deleteAll(timelineFileList1);
        }

        SelectQuery<EipTTimelineMap> timelineMapQuery1 =
          Database.query(EipTTimelineMap.class);
        Expression timelineMapExp1 =
          ExpressionFactory.inExp(EipTTimelineMap.EIP_TTIMELINE_PROPERTY, ids);
        timelineMapQuery1.setQualifier(timelineMapExp1);
        List<EipTTimelineMap> timelineMapList1 = timelineMapQuery1.fetchList();
        if (timelineMapList1 != null && timelineMapList1.size() > 0) {
          Database.deleteAll(timelineMapList1);
        }

        SelectQuery<EipTTimelineUrl> timelineUrlQuery1 =
          Database.query(EipTTimelineUrl.class);
        Expression timelineUrlExp1 =
          ExpressionFactory.inExp(
            EipTTimelineUrl.TIMELINE_ID_PROPERTY,
            timelineIds);
        timelineUrlQuery1.setQualifier(timelineUrlExp1);
        List<EipTTimelineUrl> timelineUrlList1 = timelineUrlQuery1.fetchList();
        if (timelineUrlList1 != null && timelineUrlList1.size() > 0) {
          Database.deleteAll(timelineUrlList1);
        }

        SelectQuery<EipTTimelineLike> timelineLikeQuery1 =
          Database.query(EipTTimelineLike.class);
        Expression timelineLikeExp1 =
          ExpressionFactory.inDbExp(EipTTimelineLike.OWNER_ID_COLUMN, ids);
        Expression timelineLikeExp2 =
          ExpressionFactory.inDbExp(
            EipTTimelineLike.TIMELINE_ID_COLUMN,
            timelineIds);
        timelineLikeQuery1.setQualifier(timelineLikeExp1
          .orExp(timelineLikeExp2));
        List<EipTTimelineLike> timelineLikeList1 =
          timelineLikeQuery1.fetchList();
        if (timelineLikeList1 != null && timelineLikeList1.size() > 0) {
          Database.deleteAll(timelineLikeList1);
        }

        Database.deleteAll(timelineList1);
      }
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

  @SuppressWarnings("unchecked")
  private void updateSchedule(List<Integer> ids) {
    List<Integer> deleteFacilityId = new ArrayList<Integer>();

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
      for (EipTScheduleMap map : schedulelist2) {
        List<EipTScheduleMap> maps =
          map.getEipTSchedule().getEipTScheduleMaps();
        for (EipTScheduleMap map2 : maps) {
          if (map2.getType().equals("F")) {
            deleteFacilityId.add(map2.getUserId());
          }
        }
        // 共有カテゴリの削除
        EipTCommonCategory commonCat = map.getEipTCommonCategory();
        if (!commonCat.getCommonCategoryId().equals(1)) {
          CommonCategoryUtils.setDefaultCommonCategoryToSchedule(commonCat);
          Database.delete(commonCat);
        }
      }
      Database.deleteAll(schedulelist2);

      // 設備の削除
      if (deleteFacilityId.size() > 0) {
        Expression fexp =
          ExpressionFactory.inDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            deleteFacilityId);
        SelectQuery<EipMFacility> query =
          Database.query(EipMFacility.class, fexp);
        List<EipMFacility> flist = query.fetchList();
        if (flist.size() > 0) {
          SelectQuery<EipTScheduleMap> query1 =
            Database.query(EipTScheduleMap.class);
          Expression exp1 =
            ExpressionFactory.inExp(
              EipTScheduleMap.USER_ID_PROPERTY,
              deleteFacilityId);
          Expression exp2 =
            ExpressionFactory.matchExp(EipTScheduleMap.TYPE_PROPERTY, "F");
          query1.setQualifier(exp1.andExp(exp2));
          query1.deleteAll();
          Database.deleteAll(flist);
        }
      }
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
      for (String path : fpaths) {
        file = new File(path);
        if (file.exists()) {
          deleteFolder(file);
        }
      }
    }
  }

  protected boolean deleteFolder(File parent_folder) {
    boolean flag = true;
    try {
      if (!parent_folder.exists()) {
        return false;
      }
      if (parent_folder.isFile()) {
        if (!parent_folder.delete()) {
          flag = false;
        }
      }
      String folders_path[] = parent_folder.list();
      if (folders_path.length == 0) {
        return true;
      }
      int length = folders_path.length;
      for (int i = 0; i < length; i++) {
        File folder =
          new File(parent_folder.getAbsolutePath()
            + File.separator
            + folders_path[i]);
        if (folder.isDirectory()) {
          if (!deleteFolder(folder)) {// フォルダの中身が空もしくは全部削除された場合
            flag = false;
          } else if (!folder.delete()) {
            flag = false;
          }
        } else {
          // 一つでも消えないファイルがあればフラグを動かす
          if (!folder.delete()) {
            flag = false;
          }
        }
      }
    } catch (Exception e) {
      logger.error("[SystemDeleteSampleFormData]", e);
      return false;
    }
    return flag;
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

  private void updateUserhidden(List<Integer> ids)
      throws JetspeedSecurityException {

    List<EipMPost> delpost = new ArrayList<EipMPost>();
    List<Integer> delpositionId = new ArrayList<Integer>();

    Expression gexp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, ids);
    SelectQuery<TurbineUserGroupRole> gquery =
      Database.query(TurbineUserGroupRole.class, gexp);
    List<TurbineUserGroupRole> map = gquery.fetchList();

    if (map != null && map.size() > 0) {
      for (TurbineUserGroupRole tugr : map) {
        EipMPost post = tugr.getTurbineGroup().getEipMPost();
        if (post != null && !delpost.contains(post)) {
          delpost.add(post);
        }
      }
      Database.deleteAll(map);
      if (delpost.size() > 0) {
        for (EipMPost post : delpost) {
          // グループからユーザーを削除
          List<ALEipUser> users = ALEipUtils.getUsers(post.getGroupName());
          for (ALEipUser user : users) {
            JetspeedSecurity.unjoinGroup(user.getName().getValue(), post
              .getGroupName());
          }
          JetspeedSecurity.removeGroup(post.getGroupName());
        }
        Database.deleteAll(delpost);
      }
    }

    SelectQuery<TurbineUser> tquery = Database.query(TurbineUser.class);
    Expression texp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, ids);
    tquery.setQualifier(texp);
    List<TurbineUser> tusers = tquery.fetchList();
    if (tusers != null && tusers.size() > 0) {
      Integer positionId;
      for (TurbineUser tuser : tusers) {
        tuser.setDisabled("T");
        positionId = tuser.getPositionId();
        if (positionId != null && positionId > 0) {
          delpositionId.add(positionId);
        }
      }
      Database.commit();
    }

    // 役職削除
    if (delpositionId.size() > 0) {
      Expression exp =
        ExpressionFactory.inDbExp(
          EipMPosition.POSITION_ID_PK_COLUMN,
          delpositionId);
      SelectQuery<EipMPosition> postQuery =
        new SelectQuery<EipMPosition>(EipMPosition.class, exp);
      List<EipMPosition> list = postQuery.fetchList();
      if (list.size() > 0) {
        for (EipMPosition record : list) {
          // 役職IDを取得
          int positionId = record.getPositionId();

          // 役職を削除
          Database.delete(record);

          // この役職に設定されているユーザーの役職IDを0とする
          String sql =
            "UPDATE turbine_user set POSITION_ID = 0 where POSITION_ID = "
              + positionId;
          Database.sql(TurbineUser.class, sql).execute();
        }
      }
    }
  }
}
