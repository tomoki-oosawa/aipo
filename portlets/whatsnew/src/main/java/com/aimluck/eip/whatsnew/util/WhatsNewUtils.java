/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.whatsnew.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
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
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
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

  public static final String WHATSNEW_PORTLET_NAME = "WhatsNew";

  /**
   * 新着情報追加(個別新着情報)
   * 
   * @param type
   * @param entityid
   * @param uid
   * @deprecated
   */
  @Deprecated
  public static void insertWhatsNew(int type, int entityid, int uid) {
    EipTWhatsNew entry = null;
    try {
      SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer
          .valueOf(type));
      query.setQualifier(exp);
      Expression exp1 =
        ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY, Integer
          .valueOf(uid));
      query.andQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTWhatsNew.ENTITY_ID_PROPERTY, Integer
          .valueOf(entityid));
      query.andQualifier(exp2);
      Expression exp3 =
        ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
          .valueOf(INDIVIDUAL_WHATS_NEW));
      query.andQualifier(exp3);
      List<EipTWhatsNew> entries = query.fetchList();
      if (entries == null || entries.size() < 1) {
        // 新規オブジェクトモデル
        entry = Database.create(EipTWhatsNew.class);
        entry.setCreateDate(Calendar.getInstance().getTime());
        entry.setEntityId(entityid);
        entry.setPortletType(Integer.valueOf(type));
        entry.setParentId(Integer.valueOf(INDIVIDUAL_WHATS_NEW));
      } else {
        entry = entries.get(0);
      }
      entry.setUpdateDate(Calendar.getInstance().getTime());
      entry.setUserId(Integer.valueOf(uid));
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("whatsnew", e);
    }
  }

  /**
   * 新着情報追加(全員向け新着情報)
   * 
   * @param type
   * @param entityid
   * @param uid
   * @deprecated
   */
  @Deprecated
  public static void insertWhatsNewPublic(int type, int entityid, int uid) {
    EipTWhatsNew entry = null;
    try {
      SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);
      // ポートレットタイプがtypeである かつ parentidが0である かつ エンティティーＩＤがentityidである
      Expression exp =
        ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer
          .valueOf(type));
      query.setQualifier(exp);
      Expression exp1 =
        ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
          .valueOf("0"));
      query.andQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTWhatsNew.ENTITY_ID_PROPERTY, Integer
          .valueOf(entityid));
      query.andQualifier(exp2);
      List<EipTWhatsNew> entries = query.fetchList();
      if (entries != null && entries.size() > 0) {
        // 更新である場合、今までの新着情報は削除する
        List<Integer> parentIds = new ArrayList<Integer>();
        for (EipTWhatsNew _entry : entries) {
          parentIds.add(_entry.getWhatsNewId());
        }
        SelectQuery<EipTWhatsNew> childQuery =
          Database.query(EipTWhatsNew.class);
        Expression childExp =
          ExpressionFactory.inExp(EipTWhatsNew.PARENT_ID_PROPERTY, parentIds);
        childQuery.setQualifier(childExp);
        childQuery.deleteAll();
        Database.deleteAll(entries);
      }
      // 新規オブジェクトモデル
      entry = Database.create(EipTWhatsNew.class);
      entry.setCreateDate(Calendar.getInstance().getTime());
      entry.setEntityId(entityid);
      entry.setPortletType(Integer.valueOf(type));
      entry.setUpdateDate(Calendar.getInstance().getTime());
      entry.setUserId(Integer.valueOf(uid));
      entry.setParentId(Integer.valueOf("0"));
      Database.commit();

      // 自分を閲覧済みにする
      EipTWhatsNew entry2 = Database.create(EipTWhatsNew.class);
      entry2.setCreateDate(Calendar.getInstance().getTime());
      entry2.setEntityId(entityid);
      entry2.setPortletType(Integer.valueOf(type));
      entry2.setUpdateDate(Calendar.getInstance().getTime());
      entry2.setUserId(Integer.valueOf(uid));
      entry2.setParentId(entry.getWhatsNewId());
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("whatsnew", e);
    }
  }

  /**
   * 既読フラグを追加(個別新着用)
   * 
   * @param type
   * @param entityid
   * @param uid
   * @deprecated
   */
  @Deprecated
  public static void shiftWhatsNewReadFlag(int type, int entityid, int uid) {
    shiftWhatsNewReadFlag(type, entityid, uid, true);
  }

  /**
   * 既読フラグを追加(個別新着用)
   * 
   * @param type
   * @param entityid
   * @param uid
   * @param call
   *          全体向け新着の処理を呼び出すかどうか
   * @deprecated
   */
  @Deprecated
  private static void shiftWhatsNewReadFlag(int type, int entityid, int uid,
      boolean call) {
    try {
      SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer
          .valueOf(type));
      query.setQualifier(exp);
      Expression exp1 =
        ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY, Integer
          .valueOf(uid));
      query.andQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTWhatsNew.ENTITY_ID_PROPERTY, Integer
          .valueOf(entityid));
      query.andQualifier(exp2);
      Expression exp3 =
        ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
          .valueOf("-1"));
      query.andQualifier(exp3);

      List<EipTWhatsNew> entries = query.fetchList();
      if (entries != null && entries.size() > 0) {
        Database.deleteAll(entries);
      } else {
        if (type == WHATS_NEW_TYPE_MSGBOARD_TOPIC && call) {
          // 掲示板カテゴリのアクセス権限変更が行われたレコードに関する処理
          shiftWhatsNewReadFlagPublic(type, entityid, uid, false);
        }
      }
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WhatsNewUtils]", t);
    }
  }

  /**
   * 既読フラグを追加(全体向け新着用)
   * 
   * @param type
   * @param entityid
   * @param uid
   * @param call
   * @deprecated
   */
  @Deprecated
  public static void shiftWhatsNewReadFlagPublic(int type, int entityid, int uid) {
    shiftWhatsNewReadFlagPublic(type, entityid, uid, true);
  }

  /**
   * 既読フラグを追加(全体向け新着用)
   * 
   * @param type
   * @param entityid
   * @param uid
   * @param call
   *          個別向け新着の処理の呼び出しを行うかどうか
   */
  private static void shiftWhatsNewReadFlagPublic(int type, int entityid,
      int uid, boolean call) {
    try {
      SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);

      // 全ユーザIDのリスト
      List<Integer> uids = ALEipUtils.getUserIds("LoginUser");

      // その記事に関する新着情報レコードを探す(0番に親が入る(アップデート前のデータは除く))
      Expression exp =
        ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer
          .valueOf(type));
      query.setQualifier(exp);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTWhatsNew.ENTITY_ID_PROPERTY, Integer
          .valueOf(entityid));
      query.andQualifier(exp2);
      query.orderAscending(EipTWhatsNew.PARENT_ID_PROPERTY);
      List<EipTWhatsNew> entries = query.fetchList();

      if (entries != null
        && entries.size() > 0
        && (entries.get(0)).getParentId().intValue() != -1) {

        // 新しいアルゴリズムによる全体向けWhatsNew用の処理

        if (entries.size() == uids.size()) {
          // 全員から新着が消えていたら、全てのレコードを削除する
          Database.deleteAll(entries);
          Database.commit();
          return;
        }

        EipTWhatsNew parent = entries.get(0);
        Integer parentid = parent.getWhatsNewId();
        boolean hasReadFlag = false;
        // 既に自分の既読フラグがあるか調べる
        for (int i = 1; i < entries.size(); i++) {
          if ((entries.get(i)).getUserId().intValue() == uid) {
            hasReadFlag = true;
            break;
          }

          if (!hasReadFlag) {
            // 既読フラグの登録
            EipTWhatsNew entry = null;
            entry = Database.create(EipTWhatsNew.class);
            entry.setCreateDate(Calendar.getInstance().getTime());
            entry.setUpdateDate(Calendar.getInstance().getTime());
            entry.setEntityId(entityid);
            entry.setPortletType(Integer.valueOf(type));
            entry.setUserId(uid);
            entry.setParentId(parentid);
            Database.commit();
          }
        }
      } else {
        if (call) {
          // アップデートされてきた全体向けWhatsNew用の処理
          WhatsNewUtils.shiftWhatsNewReadFlag(type, entityid, uid, false);
        }
      }

      // 1ヶ月以上前のWhatsNewを消す
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MONTH, -1);
      exp =
        ExpressionFactory.lessExp(EipTWhatsNew.UPDATE_DATE_PROPERTY, cal
          .getTime());
      Database.deleteAll(Database.query(EipTWhatsNew.class, exp).fetchList());
      Database.commit();

    } catch (Exception e) {
      Database.rollback();
      logger.error("whatsnew", e);
    }
  }

  public static WhatsNewResultData setupWhatsNewResultData(
      WhatsNewContainer record, int uid, int num, int span) {
    try {
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
            EipTWhatsNew wn = entity_ids.get(i);
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

      if (deids != null) {
        SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);
        Expression exp =
          ExpressionFactory.matchExp(
            EipTWhatsNew.PORTLET_TYPE_PROPERTY,
            Integer.valueOf(type));
        query.setQualifier(exp);
        Expression exp1 =
          ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY, Integer
            .valueOf(uid));
        query.andQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.inExp(EipTWhatsNew.ENTITY_ID_PROPERTY, deids);
        query.andQualifier(exp2);
        List<EipTWhatsNew> entries = query.fetchList();
        if (entries != null && entries.size() > 0) {
          Database.deleteAll(entries);
          Database.commit();
        }
      }

      if (WhatsNewUtils.WHATS_NEW_TYPE_BLOG_ENTRY == type) {

        Expression exp =
          ExpressionFactory.inDbExp(EipTBlogEntry.ENTRY_ID_PK_COLUMN, eids);

        List<EipTBlogEntry> entries =
          Database.query(EipTBlogEntry.class, exp).orderDesending(
            EipTBlogEntry.CREATE_DATE_PROPERTY).select(
            EipTBlogEntry.ENTRY_ID_PK_COLUMN,
            EipTBlogEntry.TITLE_COLUMN,
            EipTBlogEntry.OWNER_ID_COLUMN).fetchList();

        if (entries == null || entries.size() <= 0) {
          return null;
        }
        size = entries.size();
        rd.setCreateDate(new Date());
        rd.setUpdateDate(new Date());
        rd.setPortletName("[ ブログ ]  新着記事");

        for (int i = 0; i < size; i++) {
          EipTBlogEntry entry = entries.get(i);
          WhatsNewBean bean = new WhatsNewBean();
          bean.initField();
          bean.setEntityId(entry.getEntryId());
          bean.addParamMap("template", "BlogDetailScreen");
          bean.setJsFunctionName("aipo.blog.onLoadBlogDetailDialog");
          bean.setPortletName("[ ブログ ] ");

          try {
            ALEipUser owner = ALEipUtils.getALEipUser(entry.getOwnerId());
            bean.setOwnerName(owner.getAliasName().getValue());
          } catch (Exception e) {
            bean.setOwnerName("");
          }
          bean.setName(entry.getTitle());
          bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
          rd.setBean(bean);
        }
      } else if (WhatsNewUtils.WHATS_NEW_TYPE_BLOG_COMMENT == type) {
        Expression exp =
          ExpressionFactory.inDbExp(EipTBlogComment.COMMENT_ID_PK_COLUMN, eids);

        List<EipTBlogComment> entries =
          Database.query(EipTBlogComment.class, exp).orderDesending(
            EipTBlogComment.CREATE_DATE_PROPERTY).fetchList();

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
                StringBuffer sb =
                  new StringBuffer(tmpb.getOwnerName().getValue());
                try {
                  List<String> array = Arrays.asList(sb.toString().split(","));
                  ALEipUser tmpowner =
                    ALEipUtils.getALEipUser(entry.getOwnerId().intValue());
                  if (array.contains(tmpowner.getAliasName().getValue())) {
                    continue;
                  }
                  sb.append(",").append(tmpowner.getAliasName().getValue());
                  tmpb.setOwnerName(sb.toString());
                } catch (Exception e) {
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
            ALEipUser owner =
              ALEipUtils.getALEipUser(entry.getOwnerId().intValue());
            bean.setOwnerName(owner.getAliasName().getValue());
          } catch (Exception e) {
            bean.setOwnerName("");
          }

          bean.setName(entry.getEipTBlogEntry().getTitle());
          bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
          rd.setBean(bean);
        }
      } else if (WhatsNewUtils.WHATS_NEW_TYPE_WORKFLOW_REQUEST == type) {
        Expression exp =
          ExpressionFactory.inDbExp(
            EipTWorkflowRequest.REQUEST_ID_PK_COLUMN,
            eids);

        List<EipTWorkflowRequest> entries =
          Database.query(EipTWorkflowRequest.class, exp).orderDesending(
            EipTWorkflowRequest.UPDATE_DATE_PROPERTY).fetchList();

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
            List<EipTWorkflowRequestMap> maps =
              getEipTWorkflowRequestMap(entry);
            int m_size = maps.size();
            String lastUpdateUser = "";
            EipTWorkflowRequestMap map;
            if ("A".equals(entry.getProgress())) {
              // すべて承認済みの場合、最終承認者をセットする
              map = maps.get(m_size - 1);
              ALEipUser user =
                ALEipUtils.getALEipUser(map.getUserId().intValue());
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
              ALEipUser user =
                ALEipUtils.getALEipUser(map.getUserId().intValue());
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
        Expression exp =
          ExpressionFactory.inDbExp(EipTMsgboardTopic.TOPIC_ID_PK_COLUMN, eids);

        List<EipTMsgboardTopic> entries =
          Database.query(EipTMsgboardTopic.class, exp).orderDesending(
            EipTWorkflowRequest.CREATE_DATE_PROPERTY).select(
            EipTMsgboardTopic.TOPIC_ID_PK_COLUMN,
            EipTMsgboardTopic.TOPIC_NAME_COLUMN,
            EipTMsgboardTopic.OWNER_ID_COLUMN,
            EipTMsgboardTopic.PARENT_ID_COLUMN).fetchList();
        if (entries == null || entries.size() <= 0) {
          return null;
        }
        size = entries.size();
        rd.setCreateDate(new Date());
        rd.setUpdateDate(new Date());
        rd.setPortletName("[ 掲示板 ]  新しい書き込み");
        for (int i = 0; i < size; i++) {
          EipTMsgboardTopic topic = entries.get(i);
          WhatsNewBean bean = new WhatsNewBean();
          bean.initField();
          int parentId = topic.getParentId().intValue();
          if (parentId > 0) {
            bean.setEntityId(parentId);
          } else {
            bean.setEntityId(topic.getTopicId());
          }
          bean.addParamMap("template", "MsgboardTopicDetailScreen");
          bean.setJsFunctionName("aipo.msgboard.onLoadMsgboardDetail");
          bean.setPortletName("[ 掲示板 ] ");

          try {
            ALEipUser owner = ALEipUtils.getALEipUser(topic.getOwnerId());
            bean.setOwnerName(owner.getAliasName().getValue());
          } catch (Exception e) {
            bean.setOwnerName("");
          }
          bean.setName(topic.getTopicName());
          bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
          rd.setBean(bean);
        }
      } else if (WhatsNewUtils.WHATS_NEW_TYPE_NOTE == type) {
        Expression exp =
          ExpressionFactory.inDbExp(EipTNote.NOTE_ID_PK_COLUMN, eids);

        List<EipTNote> entries =
          Database.query(EipTNote.class, exp).orderDesending(
            EipTNote.CREATE_DATE_PROPERTY).select(
            EipTNote.NOTE_ID_PK_COLUMN,
            EipTNote.CLIENT_NAME_COLUMN,
            EipTNote.SUBJECT_TYPE_COLUMN,
            EipTNote.CUSTOM_SUBJECT_COLUMN,
            EipTNote.OWNER_ID_COLUMN).fetchList();
        if (entries == null || entries.size() <= 0) {
          return null;
        }
        size = entries.size();
        rd.setCreateDate(new Date());
        rd.setUpdateDate(new Date());
        rd.setPortletName("[ 伝言メモ ]  新着メモ");
        for (int i = 0; i < size; i++) {
          EipTNote note = entries.get(i);
          WhatsNewBean bean = new WhatsNewBean();
          bean.initField();
          bean.setEntityId(note.getNoteId());
          bean.addParamMap("template", "NoteDetailScreen");
          bean.setJsFunctionName("aipo.note.onLoadDetail");
          bean.setPortletName("[ 伝言メモ ] ");
          try {
            ALEipUser owner =
              ALEipUtils.getALEipUser(Integer.valueOf(note.getOwnerId()));
            bean.setOwnerName(owner.getAliasName().getValue());
          } catch (Exception e) {
            bean.setOwnerName("");
          }

          String clname = note.getClientName();
          String subject = "";
          String stype = note.getSubjectType();

          if ("0".equals(stype)) {
            subject = note.getCustomSubject();
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
        Expression exp =
          ExpressionFactory.inDbExp(EipTSchedule.SCHEDULE_ID_PK_COLUMN, eids);

        List<EipTSchedule> entries =
          Database.query(EipTSchedule.class, exp).orderDesending(
            EipTSchedule.UPDATE_DATE_PROPERTY).select(
            EipTSchedule.SCHEDULE_ID_PK_COLUMN,
            EipTSchedule.START_DATE_COLUMN,
            EipTSchedule.NAME_COLUMN,
            EipTSchedule.OWNER_ID_COLUMN,
            EipTSchedule.UPDATE_USER_ID_COLUMN).fetchList();
        if (entries == null || entries.size() <= 0) {
          return null;
        }
        size = entries.size();
        rd.setCreateDate(new Date());
        rd.setUpdateDate(new Date());
        rd.setPortletName("[ スケジュール ]  新着予定");
        for (int i = 0; i < size; i++) {
          EipTSchedule schedule = entries.get(i);
          WhatsNewBean bean = new WhatsNewBean();
          bean.initField();
          bean.setEntityId(schedule.getScheduleId());
          bean.addParamMap("template", "ScheduleDetailScreen");
          bean.setJsFunctionName("aipo.schedule.onLoadScheduleDetail");
          bean.setPortletName("[ スケジュール ] ");
          try {
            ALEipUser owner = ALEipUtils.getALEipUser(schedule.getOwnerId());
            bean.setOwnerName(owner.getAliasName().getValue());
          } catch (Exception e) {
            bean.setOwnerName("");
          }
          bean.setName(schedule.getName());
          bean.setUpdateDate(ALDateUtil.format(dates[i], "yyyy/MM/dd/"));
          bean.addParamMap("userid", Integer.toString(uid).trim());

          // view_dateの指定
          Date start_date = schedule.getStartDate();
          bean.addParamMap("view_date", ALDateUtil.format(
            start_date,
            "yyyy-MM-dd-00-00"));

          rd.setBean(bean);
        }
      } else {
        rd = null;
      }

      return rd;
    } catch (RuntimeException ex) {
      Database.rollback();
      logger.error("whatsnew", ex);
      return null;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("whatsnew", ex);
      return null;
    }
  }

  private static List<EipTWorkflowRequestMap> getEipTWorkflowRequestMap(
      EipTWorkflowRequest request) {
    try {
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTWorkflowRequestMap.EIP_TWORKFLOW_REQUEST_PROPERTY
            + "."
            + EipTWorkflowRequest.REQUEST_ID_PK_COLUMN,
          request.getRequestId());

      List<EipTWorkflowRequestMap> maps =
        Database.query(EipTWorkflowRequestMap.class, exp).orderAscending(
          EipTWorkflowRequestMap.ORDER_INDEX_PROPERTY).fetchList();

      if (maps == null || maps.size() == 0) {
        // 指定した Request IDのレコードが見つからない場合
        logger.debug("[WorkflowSelectData] Not found ID...");
        return null;
      }
      return maps;
    } catch (Exception ex) {
      logger.error("whatsnew", ex);
      return null;
    }
  }

  public static void removeSpanOverWhatsNew(int uid, int span) {
    if (span > 0) {
      try {
        Calendar cal = Calendar.getInstance();
        if (span == 31) {// 一ヶ月指定の場合は別処理
          cal.add(Calendar.MONTH, -1);
        } else {
          cal.add(Calendar.DAY_OF_MONTH, -1 * span);
        }

        Expression exp1 =
          ExpressionFactory.lessExp(EipTWhatsNew.UPDATE_DATE_PROPERTY, cal
            .getTime());
        List<EipTWhatsNew> entries1 =
          Database.query(EipTWhatsNew.class, exp1).fetchList();

        if (entries1 != null && entries1.size() > 0) {
          Database.deleteAll(entries1);
          Database.commit();
        }
      } catch (Throwable t) {
        Database.rollback();
        logger.error("[WhatsNewUtils]", t);
      }
    }
  }

  /**
   *
   */
  public static void removeMonthOverWhatsNew() {
    try {
      int span = 31;
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, -1 * span);

      Expression exp1 =
        ExpressionFactory.lessExp(EipTWhatsNew.UPDATE_DATE_PROPERTY, cal
          .getTime());
      List<EipTWhatsNew> entries1 =
        Database.query(EipTWhatsNew.class, exp1).fetchList();
      if (entries1 != null && entries1.size() > 0) {
        Database.deleteAll(entries1);
        Database.commit();
      }
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WhatsNewUtils]", t);
    }
  }
}
