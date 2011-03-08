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
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * ブログエントリーのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class BlogEntryFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogEntryFormData.class.getName());

  /** Title */
  private ALStringField title;

  /** メモ */
  private ALStringField note;

  /** ブログ ID */
  private ALNumberField blog_id;

  /** カテゴリID */
  private ALNumberField thema_id;

  /** コメント付加フラグ */
  private ALStringField allow_comments;

  private List<BlogThemaResultData> themaList;

  /** */
  private boolean is_new_thema;

  /** 添付ファイルリスト */
  private List<FileuploadLiteBean> fileuploadList = null;

  /** 添付フォルダ名 */
  private String folderName = null;

  private int uid;

  private String user_name;

  private boolean has_photo;

  private EipTBlog blog;

  private EipTBlogThema thema;

  private BlogThemaFormData blogthema;

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
    is_new_thema = rundata.getParameters().getBoolean("is_new_thema");

    uid = ALEipUtils.getUserId(rundata);
    orgId = Database.getDomainName();

    folderName = rundata.getParameters().getString("folderName");

    // 顔写真の取得
    ALBaseUser user = BlogUtils.getBaseUser(uid);
    user_name = user.getLastName() + " " + user.getFirstName();
    if (user.getPhoto() != null) {
      has_photo = true;
    } else {
      has_photo = false;
    }
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // Title
    title = new ALStringField();
    title.setFieldName("タイトル");
    title.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("記事");
    note.setTrim(false);
    // カテゴリID
    blog_id = new ALNumberField();
    blog_id.setFieldName("ブログ");
    // カテゴリID
    thema_id = new ALNumberField();
    thema_id.setFieldName("テーマ");
    // 公開区分
    allow_comments = new ALStringField();
    allow_comments.setFieldName("コメント付加フラグ");
    allow_comments.setValue("T");
    allow_comments.setTrim(true);

    fileuploadList = new ArrayList<FileuploadLiteBean>();

    blogthema = new BlogThemaFormData();
    blogthema.initField();
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadThemaList(RunData rundata, Context context) {
    themaList = BlogUtils.getThemaList(rundata, context);
  }

  /**
   * エントリーの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // Title必須項目
    title.setNotNull(true);
    // Titleの文字数制限
    title.limitMaxLength(50);
    // メモの文字数制限
    note.limitMaxLength(10000);

    blogthema.setValidator();
  }

  /**
   * エントリーのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // Title
    title.validate(msgList);
    // メモ
    note.validate(msgList);
    if (is_new_thema) {
      // テーマ
      blogthema.setMode(ALEipConstants.MODE_INSERT);
      blogthema.validate(msgList);
    }

    return (msgList.size() == 0);

  }

  /**
   * エントリーをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTBlogEntry entry = BlogUtils.getEipTBlogEntry(rundata, context);
      if (entry == null) {
        return false;
      }
      // Title
      title.setValue(entry.getTitle());
      // メモ
      note.setValue(entry.getNote());
      // カテゴリID
      thema_id.setValue(entry.getEipTBlogThema().getThemaId().longValue());
      // 状態
      allow_comments.setValue(entry.getAllowComments());

      SelectQuery<EipTBlogFile> filequery = Database.query(EipTBlogFile.class);
      Expression fileexp =
        ExpressionFactory.matchDbExp(EipTBlogFile.EIP_TBLOG_ENTRY_PROPERTY
          + "."
          + EipTBlogEntry.ENTRY_ID_PK_COLUMN, entry.getEntryId());
      filequery.setQualifier(fileexp);
      List<EipTBlogFile> files = filequery.fetchList();
      FileuploadLiteBean filebean = null;
      int size = files.size();
      for (int i = 0; i < size; i++) {
        EipTBlogFile file = files.get(i);
        filebean = new FileuploadLiteBean();
        filebean.initField();
        filebean.setFolderName(BlogUtils.PREFIX_DBFILE
          + Integer.toString(file.getFileId()));
        filebean.setFileName(file.getTitle());
        filebean.setFileId(file.getFileId());
        fileuploadList.add(filebean);
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * エントリーをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTBlogEntry entry = BlogUtils.getEipTBlogEntry(rundata, context);
      if (entry == null) {
        return false;
      }

      // entityIdの取得
      int entityId = entry.getEntryId();
      // タイトルの取得
      String todoName = entry.getTitle();

      List<String> fpaths = new ArrayList<String>();
      List<?> files = entry.getEipTBlogFiles();
      if (files != null && files.size() > 0) {
        int size = files.size();
        for (int i = 0; i < size; i++) {
          fpaths.add(((EipTBlogFile) files.get(i)).getFilePath());
        }
      }

      // エントリーを削除
      Database.delete(entry);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_BLOG_ENTRY,
        todoName);

      if (fpaths.size() > 0) {
        // ローカルファイルに保存されているファイルを削除する．
        int fsize = fpaths.size();
        for (int i = 0; i < fsize; i++) {
          ALStorageService.deleteFile(BlogUtils.getSaveDirPath(orgId, uid)
            + fpaths.get(i));

        }
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * エントリーをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = true;
    try {
      if (is_new_thema) {
        // テーマの登録処理
        res = blogthema.insertFormData(rundata, context, msgList);
        if (res) {
          thema_id.setValue(blogthema.getThemaId());
        }
      }

      if (res) {
        thema =
          Database.get(EipTBlogThema.class, Integer.valueOf((int) thema_id
            .getValue()));

        blog = BlogUtils.getEipTBlog(rundata, context);
        if (blog == null) {
          if (!insertBlogData(rundata, context)) {
          }
        } else {
          blog_id.setValue(blog.getBlogId().longValue());
        }

        // 新規オブジェクトモデル
        EipTBlogEntry entry = Database.create(EipTBlogEntry.class);
        // Owner ID
        entry.setOwnerId(Integer.valueOf(uid));
        // Title
        entry.setTitle(title.getValue());
        // Note
        entry.setNote(note.getValue());
        // ブログID
        entry.setEipTBlog(blog);
        // テーマID
        entry.setEipTBlogThema(thema);
        // コメント付加フラグ
        entry.setAllowComments("T");
        // 作成日
        entry.setCreateDate(Calendar.getInstance().getTime());
        // 更新日
        entry.setUpdateDate(Calendar.getInstance().getTime());

        // 添付ファイルを登録する．
        insertAttachmentFiles(fileuploadList, folderName, uid, entry, msgList);

        Database.commit();

        /* 自分以外の全員に新着ポートレット登録 */
        WhatsNewUtils.insertWhatsNewPublic(
          WhatsNewUtils.WHATS_NEW_TYPE_BLOG_ENTRY,
          entry.getEntryId().intValue(),
          uid);

        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          blog.getBlogId(),
          ALEventlogConstants.PORTLET_TYPE_BLOG_ENTRY,
          title.getValue());

        // アクティビティ
        String loginName = ALEipUtils.getALEipUser(uid).getName().getValue();
        BlogUtils.createNewBlogActivity(entry, loginName);
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  private List<Integer> getRequestedHasFileIdList(
      List<FileuploadLiteBean> attachmentFileNameList) {
    List<Integer> idlist = new ArrayList<Integer>();
    FileuploadLiteBean filebean = null;
    // if (attachmentFileNameList != null && !"".equals(attachmentFileNameList))
    // {
    if (attachmentFileNameList != null) {
      int size = attachmentFileNameList.size();
      for (int i = 0; i < size; i++) {
        filebean = attachmentFileNameList.get(i);
        if (!filebean.isNewFile()) {
          int index = filebean.getFileId();
          idlist.add(Integer.valueOf(index));
        }
      }
    }
    return idlist;
  }

  private boolean insertAttachmentFiles(
      List<FileuploadLiteBean> fileuploadList, String folderName, int uid,
      EipTBlogEntry entry, List<String> msgList) {

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
          EipTBlogFile file = Database.create(EipTBlogFile.class);
          file.setOwnerId(Integer.valueOf(uid));
          file.setTitle(newfilebean.getFileName());
          file.setFilePath(BlogUtils.getRelativePath(filename));
          if (fileThumbnail != null) {
            file.setFileThumbnail(fileThumbnail);
          }
          file.setEipTBlogEntry(entry);
          file.setCreateDate(Calendar.getInstance().getTime());
          file.setUpdateDate(Calendar.getInstance().getTime());

          // ファイルの移動
          ALStorageService.copyTmpFile(
            uid,
            folderName,
            String.valueOf(newfilebean.getFileId()),
            BlogUtils.FOLDER_FILEDIR_BLOG,
            BlogUtils.CATEGORY_KEY + ALStorageService.separator() + uid,
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

  private boolean insertBlogData(RunData rundata, Context context) {
    try {
      // 新規オブジェクトモデル
      blog = Database.create(EipTBlog.class);
      // ユーザーID
      blog.setOwnerId(Integer.valueOf(uid));
      // 作成日
      blog.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      blog.setUpdateDate(Calendar.getInstance().getTime());
      // ブログを登録
      Database.commit();
      // ブログIDの設定
      blog_id.setValue(blog.getBlogId().longValue());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているエントリーを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = false;
    try {
      // オブジェクトモデルを取得
      EipTBlogEntry entry = BlogUtils.getEipTBlogEntry(rundata, context);
      if (entry == null) {
        return false;
      }

      if (is_new_thema) {
        // テーマの登録処理
        res = blogthema.insertFormData(rundata, context, msgList);

        if (res) {
          thema = BlogUtils.getEipTBlogThema((long) blogthema.getThemaId());
          thema_id.setValue(thema.getThemaId().longValue());
        }
      } else {
        thema =
          Database.get(EipTBlogThema.class, Integer.valueOf((int) thema_id
            .getValue()));
        res = true;
      }

      if (res) {
        // Title
        entry.setTitle(title.getValue());
        // メモ
        entry.setNote(note.getValue());
        // カテゴリID
        entry.setEipTBlogThema(thema);
        // ユーザーID
        entry.setOwnerId(Integer.valueOf(uid));
        // コメント付加フラグ
        entry.setAllowComments("T");
        // 更新日
        entry.setUpdateDate(Calendar.getInstance().getTime());

        // サーバーに残すファイルのID
        List<Integer> attIdList = getRequestedHasFileIdList(fileuploadList);
        // 現在選択しているエントリが持っているファイル
        List<EipTBlogFile> files =
          BlogUtils.getEipTBlogFileList(entry.getEntryId());
        if (files != null) {
          int size = files.size();
          for (int i = 0; i < size; i++) {
            EipTBlogFile file = files.get(i);
            if (!attIdList.contains(file.getFileId())) {
              // ファイルシステムから削除
              ALStorageService.deleteFile(BlogUtils.getSaveDirPath(orgId, uid)
                + file.getFilePath());

              // DBから削除
              Database.delete(file);

            }
          }
        }

        // 添付ファイルを登録する．
        insertAttachmentFiles(fileuploadList, folderName, uid, entry, msgList);

        Database.commit();

        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entry.getEntryId(),
          ALEventlogConstants.PORTLET_TYPE_BLOG_ENTRY,
          title.getValue());
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return res;
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

    if (res) {
      try {
        blogthema.setFormData(rundata, context, msgList);
        fileuploadList = BlogUtils.getFileuploadList(rundata);
      } catch (Exception ex) {
        logger.error("Exception", ex);
      }
    }
    return res;
  }

  /**
   * カテゴリIDを取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getThemaId() {
    return thema_id;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * Title を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getTitle() {
    return title;
  }

  /**
   * カテゴリ一覧を取得します。 <BR>
   * 
   * @return
   */
  public List<BlogThemaResultData> getThemaList() {
    return themaList;
  }

  /**
   * @return
   */
  public boolean isNewThema() {
    return is_new_thema;
  }

  /**
   * テーマを取得します。
   * 
   * @return
   */
  public BlogThemaFormData getBlogThema() {
    return blogthema;
  }

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    return fileuploadList;
  }

  public String getFolderName() {
    return folderName;
  }

  public int getUserId() {
    return uid;
  }

  public String getUserName() {
    return user_name;
  }

  public boolean hasPhoto() {
    return has_photo;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_SELF;
  }

  public void setThemaId(long i) {
    thema_id.setValue(i);
  }
}
