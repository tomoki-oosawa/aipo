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
package com.aimluck.eip.msgboard.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategoryMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.msgboard.MsgboardCategoryResultData;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.orgutils.ALOrgUtilsFactoryService;
import com.aimluck.eip.util.orgutils.ALOrgUtilsHandler;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * 掲示板のユーティリティクラス <BR>
 * 
 */
public class MsgboardUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardUtils.class.getName());

  /** 所有者の識別子 */
  public static final String OWNER_ID = "ownerid";

  /** 掲示板の添付ファイルを保管するディレクトリの指定 */
  private static final String FOLDER_FILEDIR_MSGBOARD = JetspeedResources
    .getString("aipo.filedir", "");

  /** 掲示板の添付ファイルを保管するディレクトリのカテゴリキーの指定 */
  protected static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.msgboard.categorykey",
    "");

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

  /**
   * トピックオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isJoin
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipTMsgboardTopic getEipTMsgboardParentTopic(RunData rundata,
      Context context, boolean isJoin) throws ALPageNotFoundException,
      ALDBErrorException {
    String topicid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (topicid == null || Integer.valueOf(topicid) == null) {
        // トピック ID が空の場合
        logger.debug("[MsgboardTopic] Empty ID...");
        throw new ALPageNotFoundException();
      }

      int userid = ALEipUtils.getUserId(rundata);

      SelectQuery<EipTMsgboardTopic> query =
        Database.query(EipTMsgboardTopic.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
          Integer.valueOf(topicid));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.PARENT_ID_PROPERTY,
          Integer.valueOf(0));
      query.andQualifier(exp2);

      // アクセス制御
      Expression exp11 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
            + "."
            + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          "T");
      Expression exp12 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
            + "."
            + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          "F");
      Expression exp13 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
            + "."
            + EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.USER_ID_PROPERTY,
          Integer.valueOf(userid));
      query.andQualifier(exp11.orExp(exp12.andExp(exp13)));
      query.distinct(true);

      List<EipTMsgboardTopic> topics = query.fetchList();
      if (topics == null || topics.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopic] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTMsgboardTopic topic = topics.get(0);
      return topic;
    } catch (Exception ex) {
      logger.error("[MsgboardUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * 返信記事オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isSuperUser
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipTMsgboardTopic getEipTMsgboardTopicReply(RunData rundata,
      Context context, String topicid, boolean isSuperUser)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      if (topicid == null || Integer.valueOf(topicid) == null) {
        // トピック ID が空の場合
        logger.debug("[MsgboardTopic] Empty ID...");
        throw new ALPageNotFoundException();
      }

      SelectQuery<EipTMsgboardTopic> query =
        Database.query(EipTMsgboardTopic.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
          Integer.valueOf(topicid));
      query.setQualifier(exp1);

      if (!isSuperUser) {
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTMsgboardTopic.OWNER_ID_PROPERTY,
            Integer.valueOf(ALEipUtils.getUserId(rundata)));
        query.andQualifier(exp2);
      }

      List<EipTMsgboardTopic> topics = query.fetchList();
      if (topics == null || topics.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopic] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return topics.get(0);
    } catch (Exception ex) {
      logger.error("[MsgboardUtils]", ex);
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
  public static EipTMsgboardFile getEipTMsgboardFile(RunData rundata)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int attachmentIndex =
        rundata.getParameters().getInt("attachmentIndex", -1);
      if (attachmentIndex < 0) {
        // ID が空の場合
        logger.debug("[MsgboardUtils] Empty ID...");
        throw new ALPageNotFoundException();

      }

      SelectQuery<EipTMsgboardFile> query =
        Database.query(EipTMsgboardFile.class);
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMsgboardFile.FILE_ID_PK_COLUMN,
          Integer.valueOf(attachmentIndex));
      query.andQualifier(exp);

      List<EipTMsgboardFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        // 指定した ID のレコードが見つからない場合
        logger.debug("[MsgboardUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return files.get(0);
    } catch (Exception ex) {
      logger.error("[MsgboardUtils]", ex);
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
  public static List<EipTMsgboardTopic> getEipTMsgboardTopicList(
      RunData rundata, Context context, boolean isJoin)
      throws ALPageNotFoundException, ALDBErrorException {
    String topicid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (topicid == null || Integer.valueOf(topicid) == null) {
        // トピック ID が空の場合
        logger.debug("[MsgboardTopic] Empty ID...");
        throw new ALPageNotFoundException();
      }

      SelectQuery<EipTMsgboardTopic> query =
        Database.query(EipTMsgboardTopic.class);
      Expression exp001 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.PARENT_ID_PROPERTY,
          Integer.valueOf(topicid));

      // アクセス制御
      Expression exp01 =
        ExpressionFactory.matchDbExp(
          EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
          Integer.valueOf(topicid));
      Expression exp11 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
            + "."
            + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          "T");
      Expression exp21 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
            + "."
            + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          "F");
      Expression exp22 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.EIP_TMSGBOARD_CATEGORY_PROPERTY
            + "."
            + EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.USER_ID_PROPERTY,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier((exp01.andExp(exp11.orExp(exp21.andExp(exp22))))
        .orExp(exp001));
      query.distinct(true);

      List<EipTMsgboardTopic> topics = query.fetchList();
      if (topics == null || topics.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopic] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return topics;
    } catch (Exception ex) {
      logger.error("[MsgboardUtils]", ex);
      throw new ALDBErrorException();

    }
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
  public static List<EipTMsgboardTopic> getEipTMsgboardTopicListToDeleteTopic(
      RunData rundata, Context context, boolean isSuperUser)
      throws ALPageNotFoundException, ALDBErrorException {
    String topicid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (topicid == null || Integer.valueOf(topicid) == null) {
        // トピック ID が空の場合
        logger.debug("[MsgboardTopic] Empty ID...");
        throw new ALPageNotFoundException();
      }

      int userid = ALEipUtils.getUserId(rundata);

      SelectQuery<EipTMsgboardTopic> query =
        Database.query(EipTMsgboardTopic.class);

      Expression exp01 =
        ExpressionFactory.matchDbExp(EipTMsgboardTopic.OWNER_ID_COLUMN, Integer
          .valueOf(userid));
      Expression exp02 =
        ExpressionFactory.matchDbExp(
          EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
          Integer.valueOf(topicid));
      Expression exp03 =
        ExpressionFactory.matchExp(
          EipTMsgboardTopic.PARENT_ID_PROPERTY,
          Integer.valueOf(topicid));

      if (isSuperUser) {
        query.andQualifier((exp02).orExp(exp03));
      } else {
        query.andQualifier((exp01.andExp(exp02)).orExp(exp03));
      }

      List<EipTMsgboardTopic> topics = query.fetchList();
      if (topics == null || topics.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopic] Not found ID...");
        throw new ALPageNotFoundException();
      }

      boolean isdelete = false;
      int size = topics.size();
      for (int i = 0; i < size; i++) {
        EipTMsgboardTopic topic = topics.get(i);
        if (topic.getOwnerId().intValue() == userid || isSuperUser) {
          isdelete = true;
          break;
        }
      }
      if (!isdelete) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[MsgboardTopic] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return topics;
    } catch (Exception ex) {
      logger.error("[MsgboardUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * カテゴリオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMsgboardCategory getEipTMsgboardCategory(RunData rundata,
      Context context, boolean ownerOnly) throws ALPageNotFoundException,
      ALDBErrorException {
    String categoryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (categoryid == null || Integer.valueOf(categoryid) == null) {
        // カテゴリ IDが空の場合
        logger.debug("[MsgboardCategory] Empty ID...");
        throw new ALPageNotFoundException();
      }

      SelectQuery<EipTMsgboardCategory> query =
        Database.query(EipTMsgboardCategory.class);

      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTMsgboardCategory.CATEGORY_ID_PK_COLUMN,
          Integer.valueOf(categoryid));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.noMatchDbExp(
          EipTMsgboardCategory.TURBINE_USER_PROPERTY
            + "."
            + TurbineUser.USER_ID_PK_COLUMN,
          Integer.valueOf(0));
      query.andQualifier(exp2);
      if (ownerOnly) {
        /*
         * Expression exp3 = ExpressionFactory.matchDbExp(
         * EipTMsgboardCategory.TURBINE_USER_PROPERTY + "." +
         * TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(ALEipUtils
         * .getUserId(rundata))); query.andQualifier(exp3);
         */
      }

      // アクセス制御

      int loginUserId = ALEipUtils.getUserId(rundata);

      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      boolean hasAclviewOther =
        aclhandler.hasAuthority(
          loginUserId,
          ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY_OTHER,
          ALAccessControlConstants.VALUE_ACL_LIST);

      Expression exp01 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          "T");
      Expression exp02 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.STATUS_PROPERTY,
          "O");
      Expression exp03 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.STATUS_PROPERTY,
          "A");
      Expression exp11 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          "F");
      Expression exp12 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.USER_ID_PROPERTY,
          Integer.valueOf(loginUserId));

      if (!hasAclviewOther) {
        query.andQualifier((exp01.andExp(exp02.orExp(exp03))).orExp(exp11
          .andExp(exp12)));
      } else {
        query.andQualifier((exp01.andExp(exp02.orExp(exp03))).orExp(exp11
          .andExp(exp02.orExp(exp03))));
      }
      query.distinct(true);

      List<EipTMsgboardCategory> categories = query.fetchList();
      if (categories == null || categories.size() == 0) {
        // 指定したカテゴリ IDのレコードが見つからない場合
        logger.debug("[MsgboardUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return categories.get(0);
    } catch (Exception ex) {
      logger.error("[MsgboardUtils]", ex);
      throw new ALDBErrorException();

    }
  }

  /**
   * <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static List<Integer> getWhatsNewInsertList(RunData rundata,
      int categoryid, String is_public) throws ALPageNotFoundException,
      ALDBErrorException {

    int userid = ALEipUtils.getUserId(rundata);
    List<ALEipUser> result = new ArrayList<ALEipUser>();

    if ("F".equals(is_public)) {
      try {

        SelectQuery<EipTMsgboardCategoryMap> query =
          Database.query(EipTMsgboardCategoryMap.class);
        query.select(EipTMsgboardCategoryMap.USER_ID_COLUMN);

        Expression exp1 =
          ExpressionFactory.matchExp(
            EipTMsgboardCategoryMap.CATEGORY_ID_PROPERTY,
            Integer.valueOf(categoryid));
        query.setQualifier(exp1);

        // アクセス制御
        Expression exp11 =
          ExpressionFactory.matchExp(
            EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
              + "."
              + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
            PUBLIC_FLG_VALUE_PUBLIC);
        Expression exp12 =
          ExpressionFactory.matchExp(
            EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
              + "."
              + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
            PUBLIC_FLG_VALUE_NONPUBLIC);
        Expression exp13 =
          ExpressionFactory.matchExp(
            EipTMsgboardCategoryMap.STATUS_PROPERTY,
            STAT_VALUE_SHARE);
        Expression exp14 =
          ExpressionFactory.matchExp(
            EipTMsgboardCategoryMap.STATUS_PROPERTY,
            STAT_VALUE_OWNER);
        query.andQualifier(exp11.orExp(exp12.andExp(exp13)).orExp(
          exp12.andExp(exp14)));
        query.distinct(true);

        List<EipTMsgboardCategoryMap> uids = query.fetchList();

        if (uids != null && uids.size() != 0) {
          int size = uids.size();
          for (int i = 0; i < size; i++) {
            EipTMsgboardCategoryMap uid = uids.get(i);
            Integer id = uid.getUserId();
            if (id.intValue() != userid) {
              result.add(ALEipUtils.getALEipUser(id.intValue()));
            }
          }
        }

        /* メンバー全員に新着ポートレット登録 */
        ALAccessControlFactoryService aclservice =
          (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
            .getInstance())
            .getService(ALAccessControlFactoryService.SERVICE_NAME);
        ALAccessControlHandler aclhandler =
          aclservice.getAccessControlHandler();
        List<Integer> userIds =
          aclhandler.getAcceptUserIdsInListExceptLoginUser(
            ALEipUtils.getUserId(rundata),
            ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC,
            ALAccessControlConstants.VALUE_ACL_DETAIL,
            result);

        return userIds;
      } catch (Exception ex) {
        logger.error("[MsgboardUtils]", ex);
        throw new ALDBErrorException();
      }
    } else {
      /* 自分以外の全員に新着ポートレット登録 */
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      List<Integer> userIds =
        aclhandler.getAcceptUserIdsExceptLoginUser(
          ALEipUtils.getUserId(rundata),
          ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC,
          ALAccessControlConstants.VALUE_ACL_DETAIL);
      return userIds;

    }
  }

  public static List<MsgboardCategoryResultData> loadCategoryList(
      RunData rundata) {
    // カテゴリ一覧
    List<MsgboardCategoryResultData> categoryList =
      new ArrayList<MsgboardCategoryResultData>();
    try {
      SelectQuery<EipTMsgboardCategory> query =
        Database.query(EipTMsgboardCategory.class);

      // アクセス制御
      Expression exp01 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          MsgboardUtils.PUBLIC_FLG_VALUE_PUBLIC);
      Expression exp02 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.STATUS_PROPERTY,
          MsgboardUtils.STAT_VALUE_OWNER);
      Expression exp03 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.STATUS_PROPERTY,
          MsgboardUtils.STAT_VALUE_ALL);
      Expression exp11 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
          MsgboardUtils.PUBLIC_FLG_VALUE_NONPUBLIC);
      Expression exp12 =
        ExpressionFactory.matchExp(
          EipTMsgboardCategory.EIP_TMSGBOARD_CATEGORY_MAPS_PROPERTY
            + "."
            + EipTMsgboardCategoryMap.USER_ID_PROPERTY,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier((exp01.andExp(exp02.orExp(exp03))).orExp(exp11
        .andExp(exp12)));
      query.distinct(true);

      MsgboardCategoryResultData otherRd = null;

      List<EipTMsgboardCategory> aList = query.fetchList();
      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipTMsgboardCategory record = aList.get(i);
        MsgboardCategoryResultData rd = new MsgboardCategoryResultData();
        rd.initField();
        rd.setCategoryId(record.getCategoryId().longValue());
        rd.setCategoryName(record.getCategoryName());
        if (record.getCategoryId().longValue() == 1) {
          // カテゴリ「その他」は最後に追加するため，ここではリストに追加しない．
          otherRd = rd;
        } else {
          categoryList.add(rd);
        }
      }
      if (otherRd != null) {
        categoryList.add(otherRd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return categoryList;
  }

  public static boolean insertFileDataDelegate(RunData rundata,
      Context context, EipTMsgboardTopic topic,
      List<FileuploadLiteBean> fileuploadList, String folderName,
      List<String> msgList) {

    int uid = ALEipUtils.getUserId(rundata);
    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    String[] fileids = rundata.getParameters().getStrings("attachments");

    // fileidsがnullなら、ファイルがアップロードされていないので、trueを返して終了
    if (fileids == null) {
      return true;
    }

    int fileIDsize;
    if (fileids[0].equals("")) {
      fileIDsize = 0;
    } else {
      fileIDsize = fileids.length;
    }
    // 送られてきたFileIDの個数とDB上の当該TopicID中の添付ファイル検索を行った結果の個数が一致したら、
    // 変更が無かったとみなし、trueを返して終了。
    SelectQuery<EipTMsgboardFile> dbquery =
      Database.query(EipTMsgboardFile.class);
    dbquery.andQualifier(ExpressionFactory.matchDbExp(
      EipTMsgboardFile.EIP_TMSGBOARD_TOPIC_PROPERTY,
      topic.getTopicId()));
    for (int i = 0; i < fileIDsize; i++) {
      dbquery.orQualifier(ExpressionFactory.matchDbExp(
        EipTMsgboardFile.FILE_ID_PK_COLUMN,
        fileids[i]));
    }

    List<EipTMsgboardFile> files = dbquery.fetchList();

    if (files.size() == fileIDsize
      && (fileuploadList == null || fileuploadList.size() <= 0)) {
      return true;
    }

    SelectQuery<EipTMsgboardFile> query =
      Database.query(EipTMsgboardFile.class);
    query.andQualifier(ExpressionFactory.matchDbExp(
      EipTMsgboardFile.EIP_TMSGBOARD_TOPIC_PROPERTY,
      topic.getTopicId()));
    for (int i = 0; i < fileIDsize; i++) {
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMsgboardFile.FILE_ID_PK_COLUMN,
          Integer.parseInt(fileids[i]));
      query.andQualifier(exp.notExp());
    }
    // DB上でトピックに属すが、送られてきたFileIDにIDが含まれていないファイルのリスト(削除されたファイルのリスト)

    List<EipTMsgboardFile> delFiles = query.fetchList();

    if (delFiles.size() > 0) {
      // ローカルファイルに保存されているファイルを削除する．
      File file = null;
      int delsize = delFiles.size();
      for (int i = 0; i < delsize; i++) {
        file =
          new File(MsgboardUtils.getSaveDirPath(org_id, uid)
            + (delFiles.get(i)).getFilePath());
        if (file.exists()) {
          file.delete();
        }
      }
      // データベースから添付ファイルのデータ削除
      Database.deleteAll(delFiles);
    }

    // 追加ファイルが無ければtrueを返して終了
    if (fileuploadList == null || fileuploadList.size() <= 0) {
      return true;
    }

    // ファイル追加処理
    try {
      FileuploadLiteBean filebean = null;
      int size = fileuploadList.size();
      for (int i = 0; i < size; i++) {
        filebean = fileuploadList.get(i);

        // サムネイル処理
        String[] acceptExts = ImageIO.getWriterFormatNames();
        byte[] fileThumbnail =
          FileuploadUtils.getBytesShrinkFilebean(
            org_id,
            folderName,
            uid,
            filebean,
            acceptExts,
            FileuploadUtils.DEF_THUMBNAIL_WIDTH,
            FileuploadUtils.DEF_THUMBNAIL_HEIGTH,
            msgList);

        String filename =
          FileuploadUtils.getNewFileName(MsgboardUtils.getSaveDirPath(
            org_id,
            uid));

        // 新規オブジェクトモデル
        EipTMsgboardFile file = Database.create(EipTMsgboardFile.class);
        // 所有者
        file.setOwnerId(Integer.valueOf(uid));
        // トピックID
        file.setEipTMsgboardTopic(topic);
        // ファイル名
        file.setFileName(filebean.getFileName());
        // ファイルパス
        file.setFilePath(MsgboardUtils.getRelativePath(filename));
        // サムネイル画像
        if (fileThumbnail != null) {
          file.setFileThumbnail(fileThumbnail);
        }
        // 作成日
        file.setCreateDate(Calendar.getInstance().getTime());
        // 更新日
        file.setUpdateDate(Calendar.getInstance().getTime());

        // ファイルの移動
        File srcFile =
          FileuploadUtils.getAbsolutePath(org_id, uid, folderName, filebean
            .getFileId());
        File destFile =
          new File(MsgboardUtils.getAbsolutePath(org_id, uid, filename));
        FileuploadUtils.copyFile(srcFile, destFile);

        srcFile = null;
        destFile = null;
      }

      // 添付ファイル保存先のフォルダを削除
      File folder = FileuploadUtils.getFolder(org_id, uid, folderName);
      FileuploadUtils.deleteFolder(folder);
    } catch (Exception e) {
      Database.rollback();
      logger.error("Exception", e);
      return false;
    }
    return true;
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId, int uid) {
    ALOrgUtilsHandler handler =
      ALOrgUtilsFactoryService.getInstance().getOrgUtilsHandler();
    File path =
      new File(handler.getDocumentPath(
        FOLDER_FILEDIR_MSGBOARD,
        orgId,
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
          handler.getDocumentPath(FOLDER_FILEDIR_MSGBOARD, orgId, CATEGORY_KEY))
        .append(File.separator)
        .append(uid);
    File f = new File(sb.toString());
    if (!f.exists()) {
      f.mkdirs();
    }
    return sb.append(File.separator).append(fileName).toString();
  }

  public static void shiftWhatsNewReadFlag(RunData rundata, int entityid) {
    int uid = ALEipUtils.getUserId(rundata);
    boolean isPublic = false;

    SelectQuery<EipTMsgboardTopic> query =
      Database.query(EipTMsgboardTopic.class);
    Expression exp =
      ExpressionFactory
        .matchExp(EipTMsgboardTopic.PARENT_ID_PROPERTY, entityid);
    query.setQualifier(exp);
    query.select(EipTMsgboardTopic.TOPIC_ID_PK_COLUMN);
    query.distinct(true);

    List<EipTMsgboardTopic> topics = query.fetchList();

    query = Database.query(EipTMsgboardTopic.class);
    exp =
      ExpressionFactory.matchDbExp(
        EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
        entityid);
    query.setQualifier(exp);

    List<EipTMsgboardTopic> topic = query.fetchList();
    if (topic != null
      && ((topic.get(0)).getEipTMsgboardCategory().getPublicFlag().equals("T"))) {
      isPublic = true;
    }

    if (topics != null) {

      int size = topics.size();
      Integer _id = null;

      if (isPublic) {
        for (int i = 0; i < size; i++) {
          EipTMsgboardTopic record = topics.get(i);
          _id = record.getTopicId();
          WhatsNewUtils.shiftWhatsNewReadFlagPublic(
            WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC,
            _id.intValue(),
            uid);
        }
      } else {
        for (int i = 0; i < size; i++) {
          EipTMsgboardTopic record = topics.get(i);
          _id = record.getTopicId();
          WhatsNewUtils.shiftWhatsNewReadFlag(
            WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC,
            _id.intValue(),
            uid);
        }
      }
    }
    if (isPublic) {
      WhatsNewUtils.shiftWhatsNewReadFlagPublic(
        WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC,
        entityid,
        uid);
    } else {
      WhatsNewUtils.shiftWhatsNewReadFlag(
        WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC,
        entityid,
        uid);
    }

  }

  /**
   * アクセス権限をチェックします。
   * 
   * @return
   */
  public static boolean checkPermission(RunData rundata, Context context,
      int defineAclType, String pfeature) {

    if (defineAclType == 0) {
      return true;
    }

    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    boolean hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        pfeature,
        defineAclType);

    return hasAuthority;
  }
}
