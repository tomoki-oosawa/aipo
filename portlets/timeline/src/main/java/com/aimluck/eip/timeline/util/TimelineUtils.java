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

package com.aimluck.eip.timeline.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineFile;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineLike;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.services.social.model.ALActivityPutRequest;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.timeline.TimelineUserResultData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムラインのユーティリティクラス <BR>
 * 
 */
public class TimelineUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineUtils.class.getName());

  /** 所有者の識別子 */
  public static final String OWNER_ID = "ownerid";

  /** タイムラインの添付ファイルを保管するディレクトリの指定 */
  public static final String FOLDER_FILEDIR_TIMELIME = JetspeedResources
    .getString("aipo.filedir", "");

  /** タイムラインの添付ファイルを保管するディレクトリのカテゴリキーの指定 */
  public static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.timeline.categorykey",
    "");

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  public static final String FILE_ENCODING = JetspeedResources.getString(
    "content.defaultencoding",
    "UTF-8");

  /** 全てのユーザーが閲覧／返信可 */
  public static final int ACCESS_PUBLIC_ALL = 0;

  /** 全てのユーザーが閲覧可。ただし返信できるのは所属メンバーのみ。 */
  public static final int ACCESS_PUBLIC_MEMBER = 1;

  /** 所属メンバーのみ閲覧／閲覧可 */
  public static final int ACCESS_SEACRET_MEMBER = 2;

  /** 自分のみ閲覧／返信可 */
  public static final int ACCESS_SEACRET_SELF = 3;

  /** カテゴリの公開／非公開の値（公開） */
  public static final String PUBLIC_FLG_VALUE_PUBLIC = "T";

  /** カテゴリの公開／非公開の値（非公開） */
  public static final String PUBLIC_FLG_VALUE_NONPUBLIC = "F";

  /** カテゴリの状態値（自分のみのカテゴリ） */
  public static final String STAT_VALUE_OWNER = "O";

  /** カテゴリの状態値（共有カテゴリ） */
  public static final String STAT_VALUE_SHARE = "S";

  /** カテゴリの状態値（公開カテゴリ） */
  public static final String STAT_VALUE_ALL = "A";

  public static final String TIMELIME_PORTLET_NAME = "Timeline";

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  /**
   * トピックに対する返信数を返します
   * 
   * @param timeline_id
   * @return
   */
  public static Integer countReply(Integer timeline_id) {
    SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);

    Expression exp1 =
      ExpressionFactory.matchDbExp(EipTTimeline.PARENT_ID_COLUMN, timeline_id);
    query.setQualifier(exp1);

    return query.getCount();
  }

  /**
   * トピックオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isSuperUser
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static List<EipTTimeline> getEipTTimelineListToDeleteTopic(
      RunData rundata, Context context, boolean isSuperUser)
      throws ALPageNotFoundException, ALDBErrorException {
    String topicid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (topicid == null || Integer.valueOf(topicid) == null) {
        // トピック ID が空の場合
        logger.debug("[Timeline] Empty ID...");
        throw new ALPageNotFoundException();
      }

      int userid = ALEipUtils.getUserId(rundata);

      SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);

      Expression exp01 =
        ExpressionFactory.matchDbExp(EipTTimeline.OWNER_ID_COLUMN, Integer
          .valueOf(userid));
      Expression exp02 =
        ExpressionFactory.matchDbExp(
          EipTTimeline.TIMELINE_ID_PK_COLUMN,
          Integer.valueOf(topicid));
      Expression exp03 =
        ExpressionFactory.matchExp(EipTTimeline.PARENT_ID_PROPERTY, Integer
          .valueOf(topicid));

      if (isSuperUser) {
        query.andQualifier((exp02).orExp(exp03));
      } else {
        query.andQualifier((exp01.andExp(exp02)).orExp(exp03));
      }

      List<EipTTimeline> topics = query.fetchList();
      if (topics == null || topics.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[Timeline] Not found ID...");
        throw new ALPageNotFoundException();
      }

      boolean isdelete = false;
      int size = topics.size();
      for (int i = 0; i < size; i++) {
        EipTTimeline topic = topics.get(i);
        if (topic.getOwnerId().intValue() == userid || isSuperUser) {
          isdelete = true;
          break;
        }
      }
      if (!isdelete) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[Timeline] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return topics;
    } catch (Exception ex) {
      logger.error("[TimelineUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * いいねオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isSuperUser
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static List<EipTTimelineLike> getEipTTimelineLikeListToDeleteTopic(
      RunData rundata, Context context, boolean isSuperUser)
      throws ALPageNotFoundException, ALDBErrorException {
    String topicid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (topicid == null || Integer.valueOf(topicid) == null) {
        // トピック ID が空の場合
        logger.debug("[Timeline] Empty ID...");
        throw new ALPageNotFoundException();
      }

      int userid = ALEipUtils.getUserId(rundata);

      SelectQuery<EipTTimelineLike> query =
        Database.query(EipTTimelineLike.class);

      Expression exp01 =
        ExpressionFactory.matchDbExp(EipTTimelineLike.OWNER_ID_COLUMN, Integer
          .valueOf(userid));
      Expression exp02 =
        ExpressionFactory.matchDbExp(
          EipTTimelineLike.TIMELINE_ID_COLUMN,
          Integer.valueOf(topicid));

      if (isSuperUser) {
        query.andQualifier(exp02);
      } else {
        query.andQualifier(exp01.andExp(exp02));
      }

      List<EipTTimelineLike> topics = query.fetchList();
      if (topics == null || topics.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[Timeline] Not found ID...");
        throw new ALPageNotFoundException();
      }

      boolean isdelete = false;
      int size = topics.size();
      for (int i = 0; i < size; i++) {
        EipTTimelineLike topic = topics.get(i);
        if (topic.getOwnerId().intValue() == userid || isSuperUser) {
          isdelete = true;
          break;
        }
      }
      if (!isdelete) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[Timeline] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return topics;
    } catch (Exception ex) {
      logger.error("[TimelineUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * 顔写真の有無の情報をもつユーザオブジェクトの一覧を取得する．
   * 
   * @param org_id
   * @param groupname
   * @return
   */
  public static List<TimelineUserResultData> getTimelineUserResultDataList(
      String groupname) {
    List<TimelineUserResultData> list = new ArrayList<TimelineUserResultData>();

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
      TimelineUserResultData user;

      // ユーザデータを作成し、返却リストへ格納
      for (int j = 0; j < recNum; j++) {
        dataRow = ulist.get(j);
        user = new TimelineUserResultData();
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
      logger.error("[timelineUtils]", ex);
    }
    return list;
  }

  public static void createNewCommentActivity(EipTTimeline timeline,
      String loginName, String targetLoginName) {
    if (loginName.equals(targetLoginName)) {
      return;
    }
    List<String> recipients = new ArrayList<String>();
    recipients.add(targetLoginName);
    String title = new StringBuilder("あなたの投稿にコメントしました").toString();
    String portletParams =
      new StringBuilder("?template=TimelineDetailScreen")
        .append("&entityid=")
        .append(timeline.getTimelineId())
        .toString();
    ALActivityService.create(new ALActivityPutRequest()
      .withAppId("timeline")
      .withLoginName(loginName)
      .withPortletParams(portletParams)
      .withRecipients(recipients)
      .withTile(title)
      .witchPriority(1f)
      .withExternalId(String.valueOf(timeline.getTimelineId())));
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
  public static EipTTimeline getEipTTimelineParentEntry(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    String entryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (entryid == null || Integer.valueOf(entryid) == null) {
        // トピック ID が空の場合
        logger.debug("[TimelineUtil] Empty ID...");
        throw new ALPageNotFoundException();
      }

      SelectQuery<EipTTimeline> query = Database.query(EipTTimeline.class);
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTTimeline.TIMELINE_ID_PK_COLUMN,
          Integer.valueOf(entryid));
      query.setQualifier(exp);
      List<EipTTimeline> entrys = query.fetchList();
      if (entrys == null || entrys.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[TimelineUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTTimeline entry = entrys.get(0);
      return entry;
    } catch (Exception ex) {
      logger.error("[EntryUtils]", ex);
      throw new ALDBErrorException();

    }
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
              new BufferedReader(new InputStreamReader(ALStorageService
                .getTmpFile(ALEipUtils.getUserId(rundata), folderName, fileid
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
        SelectQuery<EipTTimelineFile> reqquery =
          Database.query(EipTTimelineFile.class);
        Expression reqexp1 =
          ExpressionFactory.inDbExp(
            EipTBlogFile.FILE_ID_PK_COLUMN,
            hadfileidsValue);
        reqquery.setQualifier(reqexp1);
        List<EipTTimelineFile> requests = reqquery.fetchList();
        int requestssize = requests.size();
        for (int i = 0; i < requestssize; i++) {
          EipTTimelineFile file = requests.get(i);
          filebean = new FileuploadBean();
          filebean.initField();
          filebean.setFileId(file.getFileId());
          filebean.setFileName(file.getFileName());
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
  public static EipTTimelineFile getEipTTimelineFile(RunData rundata)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int attachmentIndex =
        rundata.getParameters().getInt("attachmentIndex", -1);
      if (attachmentIndex < 0) {
        // ID が空の場合
        logger.debug("[TimelineUtils] Empty ID...");
        throw new ALPageNotFoundException();

      }

      SelectQuery<EipTTimelineFile> query =
        Database.query(EipTTimelineFile.class);
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTTimelineFile.FILE_ID_PK_COLUMN,
          Integer.valueOf(attachmentIndex));
      query.andQualifier(exp);

      List<EipTTimelineFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        // 指定した ID のレコードが見つからない場合
        logger.debug("[TimelineUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return files.get(0);
    } catch (Exception ex) {
      logger.error("[TimelineUtils]", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId, int uid) {
    return ALStorageService.getDocumentPath(
      FOLDER_FILEDIR_TIMELIME,
      CATEGORY_KEY + ALStorageService.separator() + uid);
  }

  /**
   * ファイル検索のクエリを返します
   * 
   * @param requestid
   *          ファイルを検索するリクエストのid
   * @return query
   */
  public static SelectQuery<EipTTimelineFile> getSelectQueryForFiles(
      int requestid) {
    SelectQuery<EipTTimelineFile> query =
      Database.query(EipTTimelineFile.class);
    Expression exp =
      ExpressionFactory.matchDbExp(EipTTimeline.TIMELINE_ID_PK_COLUMN, Integer
        .valueOf(requestid));
    query.setQualifier(exp);
    return query;
  }

  public static void deleteFiles(int timelineId) {
    SelectQuery<EipTTimelineFile> query =
      Database.query(EipTTimelineFile.class);
    query.andQualifier(ExpressionFactory.matchDbExp(
      EipTTimelineFile.EIP_TTIMELINE_PROPERTY,
      timelineId));
    List<EipTTimelineFile> files = query.fetchList();
    Database.deleteAll(files);
    Database.commit();
  }

  public static void deleteLikes(int timelineId) {
    SelectQuery<EipTTimelineLike> query =
      Database.query(EipTTimelineLike.class);
    query.andQualifier(ExpressionFactory.matchDbExp(
      EipTTimelineLike.EIP_TTIMELINE_PROPERTY,
      timelineId));
    List<EipTTimelineLike> likes = query.fetchList();
    Database.deleteAll(likes);
    Database.commit();
  }

}
