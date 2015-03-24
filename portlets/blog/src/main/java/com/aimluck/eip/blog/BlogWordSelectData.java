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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogComment;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログエントリー検索ボックス用データです。
 * 
 */
public class BlogWordSelectData extends ALAbstractSelectData<DataRow, DataRow> {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogWordSelectData.class.getName());

  /** 検索ワード */
  private ALStringField searchWord;

  private final List<Integer> users = new ArrayList<Integer>();

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "name_kana");
    }

    super.init(action, rundata, context);
  }

  /**
   * 自分がオーナーのアドレスを取得
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<DataRow> selectList(RunData rundata, Context context) {
    List<DataRow> list;

    // ページャからきた場合に検索ワードをセッションへ格納する
    if (!rundata.getParameters().containsKey(ALEipConstants.LIST_START)
      && !rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)) {
      ALEipUtils.setTemp(rundata, context, "Blogsword", rundata
        .getParameters()
        .getString("sword"));
    }

    // 検索ワードの設定
    searchWord = new ALStringField();
    searchWord.setTrim(true);
    // セッションから値を取得する。
    // 検索ワード未指定時は空文字が入力される
    searchWord.setValue(ALEipUtils.getTemp(rundata, context, "Blogsword"));

    try {
      list = searchList(rundata, context);

      if (list == null) {
        list = new ArrayList<DataRow>();
      }
    } catch (Exception ex) {
      logger.error("blog", ex);
      return null;
    }
    int totalSize = list.size();
    list = buildPaginatedList(list);
    return new ResultList<DataRow>(list, current_page, getRowsNum(), totalSize);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected DataRow selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   *
   */
  @Override
  protected Object getResultData(DataRow obj) {
    try {
      DataRow dataRow = obj;

      Integer entry_id =
        (Integer) Database.getFromDataRow(
          dataRow,
          EipTBlogEntry.ENTRY_ID_PK_COLUMN);
      Integer ower_id =
        (Integer) Database.getFromDataRow(
          dataRow,
          EipTBlogEntry.OWNER_ID_COLUMN);

      BlogEntryResultData rd = new BlogEntryResultData();
      rd.initField();
      rd.setEntryId(entry_id.longValue());
      rd.setOwnerId(ower_id);

      rd.setTitle(ALCommonUtils.compressString((String) Database
        .getFromDataRow(dataRow, EipTBlogEntry.TITLE_COLUMN), getStrLength()));
      rd.setNote(((String) Database.getFromDataRow(
        dataRow,
        EipTBlogEntry.NOTE_COLUMN)).replaceAll("\\r\\n", " ").replaceAll(
        "\\n",
        " ").replaceAll("\\r", " "));

      rd.setTitleDate((Date) Database.getFromDataRow(
        dataRow,
        EipTBlogEntry.CREATE_DATE_COLUMN));

      SelectQuery<EipTBlogComment> query =
        Database.query(EipTBlogComment.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTBlogComment.EIP_TBLOG_ENTRY_PROPERTY
          + "."
          + EipTBlogEntry.ENTRY_ID_PK_COLUMN, entry_id);
      query.setQualifier(exp);
      List<EipTBlogComment> list = query.fetchList();
      if (list != null && list.size() > 0) {
        rd.setCommentsNum(list.size());
      }

      if (!users.contains(ower_id)) {
        users.add(ower_id);
      }

      return rd;
    } catch (Exception ex) {
      logger.error("blog", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(DataRow obj) {
    return null;
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();

    return map;
  }

  private List<DataRow> searchList(RunData rundata, Context context) {
    List<DataRow> list = null;
    try {
      int uid = ALEipUtils.getUserId(rundata);

      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      boolean hasAuthority =
        aclhandler.hasAuthority(
          uid,
          ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER,
          ALAccessControlConstants.VALUE_ACL_LIST);

      String word = searchWord.getValue();

      if (word == null || word.length() == 0) {
        return new ArrayList<DataRow>();
      }

      word = '%' + word + '%';

      // SQLの作成
      StringBuffer statement = new StringBuffer();
      statement
        .append("SELECT DISTINCT t0.entry_id, t0.owner_id, t0.title, t0.note, ");
      statement.append("t0.thema_id, t0.update_date, t0.create_date ");
      statement
        .append("FROM eip_t_blog_entry as t0 left join eip_t_blog_comment as t1 on t1.entry_id = t0.entry_id ");
      statement.append("WHERE ((t0.title LIKE #bind($word))");
      statement.append(" OR (t0.note LIKE #bind($word))");
      statement
        .append(" OR (t0.entry_id = t1.entry_id AND (t1.comment LIKE #bind($word)))) ");
      if (!hasAuthority) {
        statement.append(" AND (t0.owner_id = " + uid + ") ");
      }
      statement.append("ORDER BY t0.create_date DESC");
      String query = statement.toString();

      list =
        Database
          .sql(EipTBlogEntry.class, query)
          .param("word", word)
          .fetchListAsDataRow();

    } catch (Exception e) {
      logger.error(e);
      return new ArrayList<DataRow>();
    }
    return list;
  }

  /**
   * 検索ワードを取得します。
   * 
   * @return
   */
  public ALStringField getSearchWord() {
    return searchWord;
  }

  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    boolean result = super.doViewList(action, rundata, context);
    loadAggregateUsers();
    return result;
  }

  protected void loadAggregateUsers() {
    ALEipManager.getInstance().getUsers(users);
  }

}
