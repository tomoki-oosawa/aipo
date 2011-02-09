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

package com.aimluck.eip.blog.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.blog.BlogThemaResultData;
import com.aimluck.eip.blog.BlogUserResultData;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.orgutils.ALOrgUtilsFactoryService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsHandler;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログのユーティリティクラスです。 <BR>
 * 
 */
public class BlogUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogUtils.class.getName());

  public static final String TARGET_GROUP_NAME = "target_group_name";

  /** 所有者の識別子 */
  public static final String OWNER_ID = "ownerid";

  /** 一時添付ファイル名 */
  public static final String ATTACHMENT_TEMP_FILENAME = "file";

  /** 一時添付ファイル名を記録するファイル名 */
  public static final String ATTACHMENT_TEMP_FILENAME_REMAIND = "file.txt";

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  public static final String FILE_ENCODING = JetspeedResources.getString(
    "content.defaultencoding",
    "UTF-8");

  /** ブログの添付ファイルを保管するディレクトリの指定 */
  protected static final String FOLDER_FILEDIR_BLOG = JetspeedResources
    .getString("aipo.filedir", "");

  /** ブログの添付ファイルを保管するディレクトリのカテゴリキーの指定 */
  protected static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.blog.categorykey",
    "");

  /** データベースに登録されたファイルを表す識別子 */
  public static final String PREFIX_DBFILE = "DBF";

  /**
   * エントリーオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isJoin
   *          テーマテーブルをJOINするかどうか
   * @return
   */
  public static EipTBlogEntry getEipTBlogEntry(RunData rundata, Context context) {
    String entryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (entryid == null || Integer.valueOf(entryid) == null) {
        // Todo IDが空の場合
        logger.debug("[Blog Entry] Empty ID...");
        return null;
      }

      SelectQuery<EipTBlogEntry> query = Database.query(EipTBlogEntry.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTBlogEntry.ENTRY_ID_PK_COLUMN, Integer
          .valueOf(entryid));
      query.setQualifier(exp);
      List<EipTBlogEntry> entrys = query.fetchList();
      if (entrys == null || entrys.size() == 0) {
        // 指定したエントリーIDのレコードが見つからない場合
        logger.debug("[Blog Entry] Not found ID...");
        return null;
      }
      return entrys.get(0);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ブログカテゴリ オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTBlogThema getEipTBlogThema(RunData rundata, Context context) {
    String themaid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (themaid == null || Integer.valueOf(themaid) == null) {
        // カテゴリIDが空の場合
        logger.debug("[Blog] Empty ID...");
        return null;
      }

      SelectQuery<EipTBlogThema> query = Database.query(EipTBlogThema.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTBlogThema.THEMA_ID_PK_COLUMN, Integer
          .valueOf(themaid));
      query.setQualifier(exp);
      List<EipTBlogThema> themas = query.fetchList();
      if (themas == null || themas.size() == 0) {
        // 指定したテーマIDのレコードが見つからない場合
        logger.debug("[Blog] Not found ID...");
        return null;
      }
      return themas.get(0);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ブログカテゴリ オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTBlogThema getEipTBlogThema(Long thema_id) {
    try {
      EipTBlogThema thema = Database.get(EipTBlogThema.class, thema_id);
      return thema;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public static EipTBlog getEipTBlog(RunData rundata, Context context) {
    try {
      int uid = ALEipUtils.getUserId(rundata);
      SelectQuery<EipTBlog> query = Database.query(EipTBlog.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTBlog.OWNER_ID_PROPERTY, Integer
          .valueOf(uid));
      query.setQualifier(exp);
      List<EipTBlog> blogs = query.fetchList();
      if (blogs == null || blogs.size() == 0) {
        // 指定したブログIDのレコードが見つからない場合
        logger.debug("[Blog Entry] Not found ID...");
        return null;
      }
      return blogs.get(0);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * コメントオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTBlogComment getEipTBlogComment(RunData rundata,
      Context context, String commentid) throws ALPageNotFoundException,
      ALDBErrorException {
    try {
      if (commentid == null || Integer.valueOf(commentid) == null) {
        // トピック ID が空の場合
        logger.debug("[BlogUtils] Empty ID...");
        throw new ALPageNotFoundException();
      }

      int userid = ALEipUtils.getUserId(rundata);
      SelectQuery<EipTBlogComment> query =
        Database.query(EipTBlogComment.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTBlogComment.COMMENT_ID_PK_COLUMN,
          Integer.valueOf(commentid));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTBlogComment.OWNER_ID_PROPERTY, Integer
          .valueOf(userid));
      query.andQualifier(exp2);
      List<EipTBlogComment> comments = query.fetchList();
      if (comments == null || comments.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[BlogUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return comments.get(0);
    } catch (Exception ex) {
      logger.error("[BlogUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTBlogFile getEipTBlogFile(RunData rundata)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int ownerid = rundata.getParameters().getInt(BlogUtils.OWNER_ID, -1);
      int entryid =
        rundata.getParameters().getInt(ALEipConstants.ENTITY_ID, -1);
      int fileid = rundata.getParameters().getInt("attachmentIndex", -1);
      if (ownerid <= 0 || entryid <= 0 || fileid <= 0) {
        // トピック ID が空の場合
        logger.debug("[BlogUtils] Empty ID...");
        throw new ALPageNotFoundException();
      }

      SelectQuery<EipTBlogFile> query = Database.query(EipTBlogFile.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTBlogFile.FILE_ID_PK_COLUMN, Integer
          .valueOf(fileid));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTBlogFile.OWNER_ID_PROPERTY, Integer
          .valueOf(ownerid));
      query.andQualifier(exp2);
      Expression exp3 =
        ExpressionFactory.matchDbExp(EipTBlogEntry.ENTRY_ID_PK_COLUMN, Integer
          .valueOf(entryid));
      query.andQualifier(exp3);

      List<EipTBlogFile> files = query.fetchList();

      if (files == null || files.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[BlogUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return files.get(0);
    } catch (Exception ex) {
      logger.error("[BlogUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * トピックオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isJoin
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipTBlogEntry getEipTBlogParentEntry(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    String entryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (entryid == null || Integer.valueOf(entryid) == null) {
        // トピック ID が空の場合
        logger.debug("[BlogUtil] Empty ID...");
        throw new ALPageNotFoundException();
      }

      SelectQuery<EipTBlogEntry> query = Database.query(EipTBlogEntry.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTBlogEntry.ENTRY_ID_PK_COLUMN, Integer
          .valueOf(entryid));
      query.setQualifier(exp);
      List<EipTBlogEntry> entrys = query.fetchList();
      if (entrys == null || entrys.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[BlogUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTBlogEntry entry = entrys.get(0);
      return entry;
    } catch (Exception ex) {
      logger.error("[EntryUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  public static List<BlogThemaResultData> getThemaList(RunData rundata,
      Context context) {
    // カテゴリ一覧
    ArrayList<BlogThemaResultData> themaList =
      new ArrayList<BlogThemaResultData>();
    try {
      SelectQuery<EipTBlogThema> query = Database.query(EipTBlogThema.class);
      query.orderAscending(EipTBlogThema.THEMA_NAME_PROPERTY);
      List<EipTBlogThema> aList = query.fetchList();
      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipTBlogThema record = aList.get(i);
        BlogThemaResultData rd = new BlogThemaResultData();
        rd.initField();
        rd.setThemaId(record.getThemaId().longValue());
        rd.setThemaName(record.getThemaName());
        themaList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return themaList;
  }

  public static String getTargetGroupName(RunData rundata, Context context) {
    String target_group_name = null;
    String idParam = rundata.getParameters().getString(TARGET_GROUP_NAME);
    target_group_name = ALEipUtils.getTemp(rundata, context, TARGET_GROUP_NAME);
    if (idParam == null && target_group_name == null) {
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, "all");
      target_group_name = "all";
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, idParam);
      target_group_name = idParam;
    }
    return target_group_name;
  }

  /**
   * 顔写真の有無の情報をもつユーザオブジェクトの一覧を取得する．
   * 
   * @param org_id
   * @param groupname
   * @return
   */
  public static List<BlogUserResultData> getBlogUserResultDataList(
      String groupname) {
    List<BlogUserResultData> list = new ArrayList<BlogUserResultData>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement
      .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, B.PHOTO, D.POSITION ");
    statement.append("FROM turbine_user_group_role as A ");
    statement.append("LEFT JOIN turbine_user as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN eip_m_user_position as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = #bind($groupname) ");
    statement.append("ORDER BY D.POSITION");
    String query = statement.toString();

    try {
      // List ulist = BasePeer.executeQuery(query, org_id);

      List<DataRow> ulist =
        Database
          .sql(TurbineUser.class, query)
          .param("groupname", groupname)
          .fetchListAsDataRow();

      int recNum = ulist.size();

      DataRow dataRow;
      BlogUserResultData user;

      // ユーザデータを作成し、返却リストへ格納
      for (int j = 0; j < recNum; j++) {
        dataRow = ulist.get(j);
        user = new BlogUserResultData();
        user.initField();
        user.setUserId((Integer) Database.getFromDataRow(
          dataRow,
          TurbineUser.USER_ID_PK_COLUMN));
        user.setName((String) Database.getFromDataRow(
          dataRow,
          TurbineUser.LOGIN_NAME_COLUMN));
        user.setAliasName((String) Database.getFromDataRow(
          dataRow,
          TurbineUser.FIRST_NAME_COLUMN), (String) Database.getFromDataRow(
          dataRow,
          TurbineUser.LAST_NAME_COLUMN));
        byte[] photo =
          (byte[]) Database.getFromDataRow(dataRow, TurbineUser.PHOTO_COLUMN);

        if (photo != null && photo.length > 0) {
          user.setHasPhoto(true);
        } else {
          user.setHasPhoto(false);
        }
        list.add(user);
      }
    } catch (Exception ex) {
      logger.error("[BlogUtils]", ex);
    }
    return list;
  }

  /**
   * ユーザ情報の取得
   * 
   * @param userid
   *          ユーザID
   * @return
   */
  public static ALBaseUser getBaseUser(int userid) {
    String uid = String.valueOf(userid);
    try {
      if (uid == null) {
        logger.debug("Empty ID...");
        return null;
      }
      return (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(uid));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public static String getUserFullName(int userid) {
    String userName = "";
    ALBaseUser user = getBaseUser(userid);
    if (user != null) {
      userName =
        new StringBuffer().append(user.getLastName()).append(" ").append(
          user.getFirstName()).toString();
    }
    return userName;
  }

  public static String compressString(String src, int length) {
    if (src == null || src.length() == 0 || length <= 0) {
      return src;
    }

    String subject;
    if (src.length() > length) {
      subject = src.substring(0, length);
      subject += "・・・";
    } else {
      subject = src;
    }
    return subject;
  }

  /**
   * 指定したエントリー名を持つ個人設定ページに含まれるポートレットへの URI を取得する．
   * 
   * @param rundata
   * @param portletEntryName
   *          PSML ファイルに記述されているタグ entry の要素 parent
   * @return
   */
  public static String getPortletURIinPersonalConfigPane(RunData rundata,
      String portletEntryName) {
    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getParent().equals(portletEntryName)) {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);

            DynamicURI duri =
              jsLink.getLink(
                JetspeedLink.CURRENT,
                null,
                null,
                JetspeedLink.CURRENT,
                null);
            duri =
              duri
                .addPathInfo(
                  JetspeedResources.PATH_PANEID_KEY,
                  portletList[i].getId() + "," + entries[j].getId())
                .addQueryData(
                  JetspeedResources.PATH_ACTION_KEY,
                  "controls.Restore");
            return duri.toString();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId, int uid) {
    File path =
      new File(ALOrgUtilsService.getDocumentPath(
        FOLDER_FILEDIR_BLOG,
        CATEGORY_KEY)
        + File.separator
        + uid);
    if (!path.exists()) {
      path.mkdirs();
    }
    return path.getAbsolutePath();
  }

  /**
   * ユーザ毎の保存先（相対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getRelativePath(String fileName) {
    return new StringBuffer().append("/").append(fileName).toString();
  }

  /**
   * 添付ファイル保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getAbsolutePath(String orgId, int uid, String fileName) {
    ALOrgUtilsHandler handler =
      ALOrgUtilsFactoryService.getInstance().getOrgUtilsHandler();
    StringBuffer sb =
      new StringBuffer()
        .append(
          handler.getDocumentPath(FOLDER_FILEDIR_BLOG, orgId, CATEGORY_KEY))
        .append(File.separator)
        .append(uid);
    File f = new File(sb.toString());
    if (!f.exists()) {
      f.mkdirs();
    }
    return sb.append(File.separator).append(fileName).toString();
  }

  /**
   * 添付ファイルを取得します。
   * 
   * @param uid
   * @return
   */
  public static ArrayList<FileuploadLiteBean> getFileuploadList(RunData rundata) {
    String[] fileids =
      rundata
        .getParameters()
        .getStrings(FileuploadUtils.KEY_FILEUPLOAD_ID_LIST);
    if (fileids == null) {
      return null;
    }

    ArrayList<String> hadfileids = new ArrayList<String>();
    ArrayList<String> newfileids = new ArrayList<String>();

    for (int j = 0; j < fileids.length; j++) {
      if (fileids[j].trim().startsWith("s")) {
        hadfileids.add(fileids[j].trim().substring(1));
      } else {
        newfileids.add(fileids[j].trim());
      }
    }

    ArrayList<FileuploadLiteBean> fileNameList =
      new ArrayList<FileuploadLiteBean>();
    FileuploadLiteBean filebean = null;
    int fileid = 0;

    // 新規にアップロードされたファイルの処理
    if (newfileids.size() > 0) {
      String folderName =
        rundata.getParameters().getString(
          FileuploadUtils.KEY_FILEUPLOAD_FODLER_NAME);
      if (folderName == null || folderName.equals("")) {
        return null;
      }

      String orgId = Database.getDomainName();
      File folder =
        FileuploadUtils.getFolder(
          orgId,
          ALEipUtils.getUserId(rundata),
          folderName);
      String folderpath = folder.getAbsolutePath();

      int length = newfileids.size();
      for (int i = 0; i < length; i++) {
        if (newfileids.get(i) == null || newfileids.get(i).equals("")) {
          continue;
        }

        try {
          fileid = Integer.parseInt(newfileids.get(i));
        } catch (Exception e) {
          continue;
        }

        if (fileid == 0) {
          filebean = new FileuploadLiteBean();
          filebean.initField();
          filebean.setFolderName("photo");
          filebean.setFileName("以前の写真ファイル");
          fileNameList.add(filebean);
        } else {
          BufferedReader reader = null;
          try {
            reader =
              new BufferedReader(new InputStreamReader(new FileInputStream(
                folderpath
                  + File.separator
                  + fileid
                  + FileuploadUtils.EXT_FILENAME), FILE_ENCODING));
            String line = reader.readLine();
            if (line == null || line.length() <= 0) {
              continue;
            }

            filebean = new FileuploadLiteBean();
            filebean.initField();
            filebean.setFolderName(fileids[i]);
            filebean.setFileId(fileid);
            filebean.setFileName(line);
            fileNameList.add(filebean);
          } catch (Exception e) {
            logger.error("Exception", e);
          } finally {
            try {
              reader.close();
            } catch (Exception e) {
              logger.error("Exception", e);
            }
          }
        }

      }
    }

    if (hadfileids.size() > 0) {
      // すでにあるファイルの処理
      ArrayList<Integer> hadfileidsValue = new ArrayList<Integer>();
      for (int k = 0; k < hadfileids.size(); k++) {
        try {
          fileid = Integer.parseInt(hadfileids.get(k));
          hadfileidsValue.add(fileid);
        } catch (Exception e) {
          continue;
        }
      }

      try {
        SelectQuery<EipTBlogFile> reqquery = Database.query(EipTBlogFile.class);
        Expression reqexp1 =
          ExpressionFactory.inDbExp(
            EipTBlogFile.FILE_ID_PK_COLUMN,
            hadfileidsValue);
        reqquery.setQualifier(reqexp1);
        List<EipTBlogFile> requests = reqquery.fetchList();
        int requestssize = requests.size();
        for (int i = 0; i < requestssize; i++) {
          EipTBlogFile file = requests.get(i);
          filebean = new FileuploadBean();
          filebean.initField();
          filebean.setFileId(file.getFileId());
          filebean.setFileName(file.getTitle());
          filebean.setFlagNewFile(false);
          fileNameList.add(filebean);
        }
      } catch (Exception ex) {
        logger.error("[BlogUtils] Exception.", ex);
      }
    }
    return fileNameList;
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static List<EipTBlogFile> getEipTBlogFileList(int entryId) {
    try {
      SelectQuery<EipTBlogFile> query = Database.query(EipTBlogFile.class);
      Expression exp =
        ExpressionFactory.matchExp(
          EipTBlogFile.EIP_TBLOG_ENTRY_PROPERTY,
          Integer.valueOf(entryId));
      query.setQualifier(exp);
      List<EipTBlogFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        return null;
      }

      return files;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public static boolean hasMinimumAuthority(RunData rundata) {
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    boolean hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_SELF,
        ALAccessControlConstants.VALUE_ACL_LIST);

    if (!hasAuthority) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    }
    return true;
  }

}
