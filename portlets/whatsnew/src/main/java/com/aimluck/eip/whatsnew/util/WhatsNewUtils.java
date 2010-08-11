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
package com.aimluck.eip.whatsnew.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTWhatsNew;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.WhatsNewContainer;
import com.aimluck.eip.whatsnew.WhatsNewResultData;
import com.aimluck.eip.whatsnew.beans.WhatsNewBean;

/**
 * WhatsNewのユーティリティクラスです。 <BR>
 * 
 */
public class WhatsNewUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WhatsNewUtils.class.getName());

  /** table識別用 */
  public static final int WHATS_NEW_TYPE_BLOG_ENTRY = 1;

  public static final int WHATS_NEW_TYPE_BLOG_COMMENT = 2;

  public static final int WHATS_NEW_TYPE_WORKFLOW_REQUEST = 3;

  public static final int WHATS_NEW_TYPE_MSGBOARD_TOPIC = 4;

  public static final int WHATS_NEW_TYPE_NOTE = 5;

  public static final int WHATS_NEW_TYPE_SCHEDULE = 6;

  /** 個人宛新着情報フラグ */
  public static final int INDIVIDUAL_WHATS_NEW = -1;

  /**
   * 新着情報追加(個別新着情報)
   * 
   * @param dataContext
   * @param type
   * @param entityid
   * @param uid
   */
  public static void insertWhatsNew(DataContext dataContext, int type,
      int entityid, int uid) {
    EipTWhatsNew entry = null;
    SelectQuery query = null;
    try {
      query = new SelectQuery(EipTWhatsNew.class);
      Expression exp = ExpressionFactory.matchExp(
          EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer.valueOf(type));
      query.setQualifier(exp);
      Expression exp1 = ExpressionFactory.matchExp(
          EipTWhatsNew.USER_ID_PROPERTY, Integer.valueOf(uid));
      query.andQualifier(exp1);
      Expression exp2 = ExpressionFactory.matchExp(
          EipTWhatsNew.ENTITY_ID_PROPERTY, Integer.valueOf(entityid));
      query.andQualifier(exp2);
      Expression exp3 = ExpressionFactory.matchExp(
          EipTWhatsNew.PARENT_ID_PROPERTY, Integer
              .valueOf(INDIVIDUAL_WHATS_NEW));
      query.andQualifier(exp3);
      @SuppressWarnings("unchecked")
      List<EipTWhatsNew> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() < 1) {
        // 新規オブジェクトモデル
        entry = (EipTWhatsNew) dataContext
            .createAndRegisterNewObject(EipTWhatsNew.class);
        entry.setCreateDate(Calendar.getInstance().getTime());
        entry.setEntityId(entityid);
        entry.setPortletType(Integer.valueOf(type));
        entry.setParentId(Integer.valueOf(INDIVIDUAL_WHATS_NEW));
      } else {
        entry = entries.get(0);
      }
      entry.setUpdateDate(Calendar.getInstance().getTime());
      entry.setUserId(Integer.valueOf(uid));
      dataContext.commitChanges();
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /**
   * 新着情報追加(全員向け新着情報)
   * 
   * @param dataContext
   * @param type
   * @param entityid
   * @param uid
   */
  public static void insertWhatsNewPublic(DataContext dataContext, int type,
      int entityid, int uid) {
    EipTWhatsNew entry = null;
    SelectQuery query = null;
    try {
      query = new SelectQuery(EipTWhatsNew.class);
      // ポートレットタイプがtypeである かつ parentidが0である かつ エンティティーＩＤがentityidである
      Expression exp = ExpressionFactory.matchExp(
          EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer.valueOf(type));
      query.setQualifier(exp);
      Expression exp1 = ExpressionFactory.matchExp(
          EipTWhatsNew.PARENT_ID_PROPERTY, Integer.valueOf("0"));
      query.andQualifier(exp1);
      Expression exp2 = ExpressionFactory.matchExp(
          EipTWhatsNew.ENTITY_ID_PROPERTY, Integer.valueOf(entityid));
      query.andQualifier(exp2);
      @SuppressWarnings("unchecked")
      List<EipTWhatsNew> entries = dataContext.performQuery(query);
      if (!(entries == null || entries.size() < 1)) {
        // 更新である場合、今までの新着情報は削除する
        dataContext.deleteObjects(entries);
      }
      // 新規オブジェクトモデル
      entry = (EipTWhatsNew) dataContext
          .createAndRegisterNewObject(EipTWhatsNew.class);
      entry.setCreateDate(Calendar.getInstance().getTime());
      entry.setEntityId(entityid);
      entry.setPortletType(Integer.valueOf(type));
      entry.setUpdateDate(Calendar.getInstance().getTime());
      entry.setUserId(Integer.valueOf(uid));
      entry.setParentId(Integer.valueOf("0"));
      dataContext.commitChanges();

      // 自分を閲覧済みにする
      EipTWhatsNew entry2 = (EipTWhatsNew) dataContext
          .createAndRegisterNewObject(EipTWhatsNew.class);
      entry2.setCreateDate(Calendar.getInstance().getTime());
      entry2.setEntityId(entityid);
      entry2.setPortletType(Integer.valueOf(type));
      entry2.setUpdateDate(Calendar.getInstance().getTime());
      entry2.setUserId(Integer.valueOf(uid));
      entry2.setParentId(entry.getWhatsNewId());
      dataContext.commitChanges();
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /**
   * 既読フラグを追加(個別新着用)
   */
  public static void shiftWhatsNewReadFlag(int type, int entityid, int uid) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = null;

    query = new SelectQuery(EipTWhatsNew.class);
    Expression exp = ExpressionFactory.matchExp(
        EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer.valueOf(type));
    query.setQualifier(exp);
    Expression exp1 = ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY,
        Integer.valueOf(uid));
    query.andQualifier(exp1);
    Expression exp2 = ExpressionFactory.matchExp(
        EipTWhatsNew.ENTITY_ID_PROPERTY, Integer.valueOf(entityid));
    query.andQualifier(exp2);
    Expression exp3 = ExpressionFactory.matchExp(
        EipTWhatsNew.PARENT_ID_PROPERTY, Integer.valueOf("-1"));
    query.andQualifier(exp3);

    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> entries = dataContext.performQuery(query);
    if (entries != null && entries.size() > 0) {
      dataContext.deleteObjects(entries);
    }
    dataContext.commitChanges();
  }

  /**
   * 既読フラグを追加(全体向け新着用)
   */
  public static void shiftWhatsNewReadFlagPublic(int type, int entityid, int uid) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = null;
    try {
      query = new SelectQuery(EipTWhatsNew.class);

      // 全ユーザIDのリスト
      List<Integer> uids = ALEipUtils.getUserIds("LoginUser");

      // その記事に関する新着情報レコードを探す(0番に親が入る(アップデート前のデータは除く))
      Expression exp = ExpressionFactory.matchExp(
          EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer.valueOf(type));
      query.setQualifier(exp);
      Expression exp2 = ExpressionFactory.matchExp(
          EipTWhatsNew.ENTITY_ID_PROPERTY, Integer.valueOf(entityid));
      query.andQualifier(exp2);
      query.addOrdering(EipTWhatsNew.PARENT_ID_PROPERTY, true);
      @SuppressWarnings("unchecked")
      List<EipTWhatsNew> entries = dataContext.performQuery(query);

      if (entries != null && entries.size() > 0
          && ((EipTWhatsNew) entries.get(0)).getParentId().intValue() != -1) {

        // 新しいアルゴリズムによる全体向けWhatsNew用の処理

        if (entries.size() == uids.size()) {
          // 全員から新着が消えていたら、全てのレコードを削除する
          dataContext.deleteObjects(entries);
          dataContext.commitChanges();
          return;
        }

        if (entries != null && entries.size() > 0) {
          EipTWhatsNew parent = (EipTWhatsNew) entries.get(0);
          Integer parentid = parent.getWhatsNewId();
          boolean hasReadFlag = false;
          // 既に自分の既読フラグがあるか調べる
          for (int i = 1; i < entries.size(); i++) {
            if (((EipTWhatsNew) entries.get(i)).getUserId().intValue() == uid) {
              hasReadFlag = true;
              break;
            }
          }
          if (!hasReadFlag) {
            // 既読フラグの登録
            EipTWhatsNew entry = null;
            entry = (EipTWhatsNew) dataContext
                .createAndRegisterNewObject(EipTWhatsNew.class);
            entry.setCreateDate(Calendar.getInstance().getTime());
            entry.setUpdateDate(Calendar.getInstance().getTime());
            entry.setEntityId(entityid);
            entry.setPortletType(Integer.valueOf(type));
            entry.setUserId(uid);
            entry.setParentId(parentid);
            dataContext.commitChanges();
          }
        }
      } else {
        // アップデートされてきた全体向けWhatsNew用の処理
        WhatsNewUtils.shiftWhatsNewReadFlag(type, entityid, uid);
      }

      // 1ヶ月以上前のWhatsNewを消す
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MONTH, -1);
      query = new SelectQuery(EipTWhatsNew.class);
      exp = ExpressionFactory.lessExp(EipTWhatsNew.UPDATE_DATE_PROPERTY, cal
          .getTime());
      query.setQualifier(exp);
      dataContext.deleteObjects(dataContext.performQuery(query));
      dataContext.commitChanges();

    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  public static WhatsNewResultData setupWhatsNewResultData(
      WhatsNewContainer record, int uid, int num, int span) {
    WhatsNewResultData rd = new WhatsNewResultData();
    rd.initField();

    int size = 0;
    int type = record.getType();
    Integer[] eids = null;
    Integer[] deids = null;
    Date[] dates = null;
    List<EipTWhatsNew> entity_ids = record.getList();
    List<EipTWhatsNew> deny_whatsnew = new ArrayList<EipTWhatsNew>();

    if ((entity_ids != null) && (size = entity_ids.size()) > 0) {
      if (size > num) {
        eids = new Integer[num];
        dates = new Date[num];
        deids = new Integer[size - num];
      } else {
        eids = new Integer[size];
        dates = new Date[size];
      }

      for (int i = 0; i < size; i++) {
        try {
          EipTWhatsNew wn = (EipTWhatsNew) entity_ids.get(i);
          if (i < num) {
            eids[i] = wn.getEntityId();
            dates[i] = wn.getUpdateDate();
          } else {
            deids[i - num] = wn.getEntityId();
            deny_whatsnew.add(wn);
          }
        } catch (Exception e) {
          return null;
        }
      }
    } else {
      return null;
    }

    // rd.setEntityId(entityid);
    rd.setType(type);
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query;

    if (deids != null) {
      query = new SelectQuery(EipTWhatsNew.class);
      Expression exp = ExpressionFactory.matchExp(
          EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer.valueOf(type));
      query.setQualifier(exp);
      Expression exp1 = ExpressionFactory.matchExp(
          EipTWhatsNew.USER_ID_PROPERTY, Integer.valueOf(uid));
      query.andQualifier(exp1);
      Expression exp2 = ExpressionFactory.inExp(
          EipTWhatsNew.ENTITY_ID_PROPERTY, deids);
      query.andQualifier(exp2);
      @SuppressWarnings("unchecked")
      List<EipTWhatsNew> entries = dataContext.performQuery(query);
      if (entries != null && entries.size() > 0) {
        dataContext.deleteObjects(entries);
        dataContext.commitChanges();
      }
      query = null;
    }

    if (WhatsNewUtils.WHATS_NEW_TYPE_BLOG_ENTRY == type) {

      /** 保持件数以上のレコードに対して既読させる */
      /*
       * int denysize = deny_whatsnew.size(); Date date_object = new Date();
       * 
       * if (denysize > 0) { dataContext =
       * DatabaseOrmService.getInstance().getDataContext(); for (int i = 0; i <
       * denysize; i++) { EipTWhatsNew whatsnew = (EipTWhatsNew) dataContext
       * .createAndRegisterNewObject(EipTWhatsNew.class);
       * whatsnew.setUserId(uid); whatsnew.setPortletType(type);
       * whatsnew.setParentId((deny_whatsnew.get(i)).getWhatsNewId());
       * whatsnew.setEntityId((deny_whatsnew.get(i)).getEntityId());
       * whatsnew.setUpdateDate(date_object);
       * whatsnew.setCreateDate(date_object); } dataContext.commitChanges(); }
       */

      Expression exp = ExpressionFactory.inDbExp(
          EipTBlogEntry.ENTRY_ID_PK_COLUMN, eids);
      query = new SelectQuery(EipTBlogEntry.class, exp);
      /** 投稿日でソート */
      query.addOrdering(EipTBlogEntry.CREATE_DATE_PROPERTY, false);
      query.addCustomDbAttribute(EipTBlogEntry.ENTRY_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipTBlogEntry.TITLE_COLUMN);
      query.addCustomDbAttribute(EipTBlogEntry.OWNER_ID_COLUMN);

      @SuppressWarnings("unchecked")
      List<DataRow> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() <= 0) {
        return null;
      }
      size = entries.size();
      rd.setCreateDate(new Date());
      rd.setUpdateDate(new Date());
      rd.setPortletName("[ ブログ ]  新着記事");

      DataRow dataRow = null;
      for (int i = 0; i < size; i++) {
        dataRow = entries.get(i);
        WhatsNewBean bean = new WhatsNewBean();
        bean.initField();
        bean.setEntityId((Integer) dataRow
            .get(EipTBlogEntry.ENTRY_ID_PK_COLUMN));
        bean.addParamMap("template", "BlogDetailScreen");
        bean.setJsFunctionName("aipo.blog.onLoadBlogDetailDialog");
        bean.setPortletName("[ ブログ ] ");

        try {
          ALEipUser owner = ALEipUtils.getALEipUser((Integer) dataRow
              .get(EipTBlogEntry.OWNER_ID_COLUMN));
          bean.setOwnerName(owner.getAliasName().getValue());
        } catch (Exception e) {
          bean.setOwnerName("");
        }
        bean.setName((String) dataRow.get(EipTBlogEntry.TITLE_COLUMN));
        bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
        rd.setBean(bean);
      }
    } else if (WhatsNewUtils.WHATS_NEW_TYPE_BLOG_COMMENT == type) {
      Expression exp = ExpressionFactory.inDbExp(
          EipTBlogComment.COMMENT_ID_PK_COLUMN, eids);
      query = new SelectQuery(EipTBlogComment.class, exp);
      /** 投稿日でソート */
      query.addOrdering(EipTBlogComment.CREATE_DATE_PROPERTY, false);

      @SuppressWarnings("unchecked")
      List<EipTBlogComment> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() <= 0) {
        return null;
      }
      size = entries.size();
      rd.setCreateDate(new Date());
      rd.setUpdateDate(new Date());
      rd.setPortletName("[ ブログ ]  新着コメント");
      for (int i = 0; i < size; i++) {
        EipTBlogComment entry = entries.get(i);
        int entryId = entry.getEipTBlogEntry().getEntryId().intValue();
        /**
         * 重複判定
         */
        int size2 = 0;
        List<WhatsNewBean> tmp = rd.getBeans();
        boolean is_contain = false;
        if ((tmp != null) && (size2 = tmp.size()) > 0) {
          for (int j = 0; j < size2; j++) {
            WhatsNewBean tmpb = tmp.get(j);
            if (tmpb.getEntityId().getValue() == entryId) {
              StringBuffer sb = new StringBuffer(tmpb.getOwnerName().getValue());
              try {
                List<String> array = Arrays.asList(sb.toString().split(","));
                ALEipUser tmpowner = ALEipUtils.getALEipUser(entry.getOwnerId()
                    .intValue());
                if (array.contains(tmpowner.getAliasName().getValue())) {
                  continue;
                }
                sb.append(",").append(tmpowner.getAliasName().getValue());
                tmpb.setOwnerName(sb.toString());
              } catch (Exception e) {
                // TODO: handle exception
              }
              is_contain = true;
              break;
            }
          }
        }

        if (is_contain) {
          continue;
        }

        WhatsNewBean bean = new WhatsNewBean();
        bean.initField();
        bean.setEntityId(entryId);
        bean.addParamMap("template", "BlogDetailScreen");
        bean.setJsFunctionName("aipo.blog.onLoadBlogDetailDialog");
        bean.setPortletName("[ ブログ ] ");

        try {
          ALEipUser owner = ALEipUtils.getALEipUser(entry.getOwnerId()
              .intValue());
          bean.setOwnerName(owner.getAliasName().getValue());
        } catch (Exception e) {
          bean.setOwnerName("");
        }

        bean.setName(entry.getEipTBlogEntry().getTitle());
        bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
        rd.setBean(bean);
      }
    } else if (WhatsNewUtils.WHATS_NEW_TYPE_WORKFLOW_REQUEST == type) {
      Expression exp = ExpressionFactory.inDbExp(
          EipTWorkflowRequest.REQUEST_ID_PK_COLUMN, eids);
      query = new SelectQuery(EipTWorkflowRequest.class, exp);
      /** 更新日でソート */
      query.addOrdering(EipTWorkflowRequest.UPDATE_DATE_PROPERTY, false);

      @SuppressWarnings("unchecked")
      List<EipTWorkflowRequest> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() <= 0) {
        return null;
      }
      size = entries.size();
      rd.setCreateDate(new Date());
      rd.setUpdateDate(new Date());
      rd.setPortletName("[ ワークフロー ]  新着依頼");

      for (int i = 0; i < size; i++) {
        EipTWorkflowRequest entry = entries.get(i);
        WhatsNewBean bean = new WhatsNewBean();
        bean.initField();
        bean.setEntityId(entry.getRequestId());
        bean.addParamMap("template", "WorkflowDetailScreen");
        bean.setJsFunctionName("aipo.workflow.onLoadWorkflowDetail");
        bean.setPortletName("[ ワークフロー ] ");
        try {
          List<EipTWorkflowRequestMap> maps = getEipTWorkflowRequestMap(entry);
          int m_size = maps.size();
          String lastUpdateUser = "";
          EipTWorkflowRequestMap map;
          if ("A".equals(entry.getProgress())) {
            // すべて承認済みの場合、最終承認者をセットする
            map = maps.get(m_size - 1);
            ALEipUser user = ALEipUtils
                .getALEipUser(map.getUserId().intValue());
            lastUpdateUser = user.getAliasName().getValue();
          } else {
            // 最終閲覧者を取得する
            int unum = 0;
            for (int j = 0; j < m_size; j++) {
              map = maps.get(j);
              if ("C".equals(map.getStatus())) {
                unum = j - 1;
              } else if ("D".equals(map.getStatus())) {
                unum = j;
                break;
              }
            }
            map = maps.get(unum);
            ALEipUser user = ALEipUtils
                .getALEipUser(map.getUserId().intValue());
            lastUpdateUser = user.getAliasName().getValue();
          }

          bean.setOwnerName(lastUpdateUser);
        } catch (Exception e) {
          bean.setOwnerName("");
        }
        String cname = entry.getEipTWorkflowCategory().getCategoryName();
        String rname = entry.getRequestName();

        String title = "【" + cname + "】 " + rname;

        bean.setName(title);
        bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
        bean.addParamMap("mode", "detail");
        bean.addParamMap("prvid", bean.getEntityId().toString());

        rd.setBean(bean);
      }
    } else if (WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC == type) {
      Expression exp = ExpressionFactory.inDbExp(
          EipTMsgboardTopic.TOPIC_ID_PK_COLUMN, eids);
      query = new SelectQuery(EipTMsgboardTopic.class, exp);
      /** 投稿日でソート */
      query.addOrdering(EipTWorkflowRequest.CREATE_DATE_PROPERTY, false);

      query.addCustomDbAttribute(EipTMsgboardTopic.TOPIC_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipTMsgboardTopic.TOPIC_NAME_COLUMN);
      query.addCustomDbAttribute(EipTMsgboardTopic.OWNER_ID_COLUMN);
      query.addCustomDbAttribute(EipTMsgboardTopic.PARENT_ID_COLUMN);

      @SuppressWarnings("unchecked")
      List<DataRow> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() <= 0) {
        return null;
      }
      size = entries.size();
      rd.setCreateDate(new Date());
      rd.setUpdateDate(new Date());
      rd.setPortletName("[ 掲示板 ]  新しい書き込み");
      DataRow dataRow = null;
      for (int i = 0; i < size; i++) {
        dataRow = entries.get(i);
        WhatsNewBean bean = new WhatsNewBean();
        bean.initField();
        int parentId = ((Integer) dataRow
            .get(EipTMsgboardTopic.PARENT_ID_COLUMN)).intValue();
        if (parentId > 0) {
          bean.setEntityId(parentId);
        } else {
          bean.setEntityId((Integer) dataRow
              .get(EipTMsgboardTopic.TOPIC_ID_PK_COLUMN));
        }
        bean.addParamMap("template", "MsgboardTopicDetailScreen");
        bean.setJsFunctionName("aipo.msgboard.onLoadMsgboardDetail");
        bean.setPortletName("[ 掲示板 ] ");

        try {
          ALEipUser owner = ALEipUtils.getALEipUser((Integer) dataRow
              .get(EipTMsgboardTopic.OWNER_ID_COLUMN));
          bean.setOwnerName(owner.getAliasName().getValue());
        } catch (Exception e) {
          bean.setOwnerName("");
        }
        bean.setName((String) dataRow.get(EipTMsgboardTopic.TOPIC_NAME_COLUMN));
        bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
        rd.setBean(bean);
      }
    } else if (WhatsNewUtils.WHATS_NEW_TYPE_NOTE == type) {
      Expression exp = ExpressionFactory.inDbExp(EipTNote.NOTE_ID_PK_COLUMN,
          eids);
      query = new SelectQuery(EipTNote.class, exp);
      /** 投稿日でソート */
      query.addOrdering(EipTNote.CREATE_DATE_PROPERTY, false);
      query.addCustomDbAttribute(EipTNote.NOTE_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipTNote.CLIENT_NAME_COLUMN);
      query.addCustomDbAttribute(EipTNote.SUBJECT_TYPE_COLUMN);
      query.addCustomDbAttribute(EipTNote.CUSTOM_SUBJECT_COLUMN);
      query.addCustomDbAttribute(EipTNote.OWNER_ID_COLUMN);

      @SuppressWarnings("unchecked")
      List<DataRow> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() <= 0) {
        return null;
      }
      size = entries.size();
      rd.setCreateDate(new Date());
      rd.setUpdateDate(new Date());
      rd.setPortletName("[ 伝言メモ ]  新着メモ");
      DataRow dataRow;
      for (int i = 0; i < size; i++) {
        dataRow = entries.get(i);
        WhatsNewBean bean = new WhatsNewBean();
        bean.initField();
        bean.setEntityId((Integer) dataRow.get(EipTNote.NOTE_ID_PK_COLUMN));
        bean.addParamMap("template", "NoteDetailScreen");
        bean.setJsFunctionName("aipo.note.onLoadDetail");
        bean.setPortletName("[ 伝言メモ ] ");
        try {
          ALEipUser owner = ALEipUtils.getALEipUser(Integer
              .parseInt((String) dataRow.get(EipTNote.OWNER_ID_COLUMN)));
          bean.setOwnerName(owner.getAliasName().getValue());
        } catch (Exception e) {
          bean.setOwnerName("");
        }

        String clname = (String) dataRow.get(EipTNote.CLIENT_NAME_COLUMN);
        String subject = "";
        String stype = (String) dataRow.get(EipTNote.SUBJECT_TYPE_COLUMN);

        if ("0".equals(stype)) {
          subject = (String) dataRow.get(EipTNote.CUSTOM_SUBJECT_COLUMN);
        } else if ("1".equals(stype)) {
          subject = "再度電話します。";
        } else if ("2".equals(stype)) {
          subject = "折返しお電話ください。";
        } else if ("3".equals(stype)) {
          subject = "連絡があったことをお伝えください。";
        } else if ("4".equals(stype)) {
          subject = "伝言をお願いします。";
        }

        String title = "【" + clname + "】 " + subject;

        bean.setName(title);

        bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
        rd.setBean(bean);
      }
    } else if (WhatsNewUtils.WHATS_NEW_TYPE_SCHEDULE == type) {
      Expression exp = ExpressionFactory.inDbExp(
          EipTSchedule.SCHEDULE_ID_PK_COLUMN, eids);
      query = new SelectQuery(EipTSchedule.class, exp);
      /** 更新日でソート */
      query.addOrdering(EipTSchedule.UPDATE_DATE_PROPERTY, false);
      query.addCustomDbAttribute(EipTSchedule.SCHEDULE_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipTSchedule.START_DATE_COLUMN);
      query.addCustomDbAttribute(EipTSchedule.NAME_COLUMN);
      query.addCustomDbAttribute(EipTSchedule.UPDATE_USER_ID_COLUMN);

      @SuppressWarnings("unchecked")
      List<DataRow> entries = dataContext.performQuery(query);
      if (entries == null || entries.size() <= 0) {
        return null;
      }
      size = entries.size();
      rd.setCreateDate(new Date());
      rd.setUpdateDate(new Date());
      rd.setPortletName("[ スケジュール ]  新着予定");
      DataRow dataRow = null;
      for (int i = 0; i < size; i++) {
        dataRow = (DataRow) entries.get(i);
        WhatsNewBean bean = new WhatsNewBean();
        bean.initField();
        bean.setEntityId((Integer) dataRow
            .get(EipTSchedule.SCHEDULE_ID_PK_COLUMN));
        bean.addParamMap("template", "ScheduleDetailScreen");
        bean.setJsFunctionName("aipo.schedule.onLoadScheduleDetail");
        bean.setPortletName("[ スケジュール ] ");
        try {
          ALEipUser owner = ALEipUtils.getALEipUser((Integer) ALEipUtils
              .getObjFromDataRow(dataRow, EipTSchedule.UPDATE_USER_ID_COLUMN));
          bean.setOwnerName(owner.getAliasName().getValue());
        } catch (Exception e) {
          bean.setOwnerName("");
        }
        bean.setName((String) dataRow.get(EipTSchedule.NAME_COLUMN));
        bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
        bean.addParamMap("userid", Integer.toString(uid).trim());

        // view_dateの指定
        Date start_date = (Date) ALEipUtils.getObjFromDataRow(dataRow,
            EipTSchedule.START_DATE_COLUMN);
        bean.addParamMap("view_date", ALDateUtil.format(start_date,
            "yyyy-MM-dd-00-00"));

        rd.setBean(bean);
      }
    } else {
      rd = null;
    }

    return rd;
  }

  private static List<EipTWorkflowRequestMap> getEipTWorkflowRequestMap(
      EipTWorkflowRequest request) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTWorkflowRequestMap.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTWorkflowRequestMap.EIP_TWORKFLOW_REQUEST_PROPERTY + "."
              + EipTWorkflowRequest.REQUEST_ID_PK_COLUMN, request
              .getRequestId());
      query.setQualifier(exp);
      query.addOrdering(EipTWorkflowRequestMap.ORDER_INDEX_PROPERTY, true);

      @SuppressWarnings("unchecked")
      List<EipTWorkflowRequestMap> maps = dataContext.performQuery(query);

      if (maps == null || maps.size() == 0) {
        // 指定した Request IDのレコードが見つからない場合
        logger.debug("[WorkflowSelectData] Not found ID...");
        return null;
      }
      return maps;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public static void removeSpanOverWhatsNew(int uid, int span) {
    if (span > 0) {
      SelectQuery query = new SelectQuery(EipTWhatsNew.class);
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      Calendar cal = Calendar.getInstance();
      if (span == 31) {// 一ヶ月指定の場合は別処理
        cal.add(Calendar.MONTH, -1);
      } else {
        cal.add(Calendar.DAY_OF_MONTH, -1 * span);
      }
      Expression exp1 = ExpressionFactory.lessExp(
          EipTWhatsNew.UPDATE_DATE_PROPERTY, cal.getTime());
      query.setQualifier(exp1);

      @SuppressWarnings("unchecked")
      List<EipTWhatsNew> entries1 = dataContext.performQuery(query);
      if (entries1 != null && entries1.size() > 0) {
        dataContext.deleteObjects(entries1);
        dataContext.commitChanges();
      }
    }
  }

  /**
   * 
   */
  public static void removeMonthOverWhatsNew() {
    int span = 31;
    SelectQuery query = new SelectQuery(EipTWhatsNew.class);
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -1 * span);

    Expression exp1 = ExpressionFactory.lessExp(
        EipTWhatsNew.UPDATE_DATE_PROPERTY, cal.getTime());
    query.setQualifier(exp1);
    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> entries1 = dataContext.performQuery(query);
    if (entries1 != null && entries1.size() > 0) {
      dataContext.deleteObjects(entries1);
      dataContext.commitChanges();
    }

  }
}
