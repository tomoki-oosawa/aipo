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
package com.aimluck.eip.todo.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.services.social.model.ALActivityPutRequest;
import com.aimluck.eip.todo.ToDoCategoryResultData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoのユーティリティクラスです。 <BR>
 * 
 */
public class ToDoUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoUtils.class.getName());

  /** グループによる表示切り替え用変数の識別子 */
  public static final String TARGET_GROUP_NAME = "target_group_name";

  /** ユーザによる表示切り替え用変数の識別子 */
  public static final String TARGET_USER_ID = "target_user_id";

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

  /** パラメータリセットの識別子 */
  private static final String RESET_KEYWORD_FLAG = "reset_keyword_params";

  /** パラメータリセットの識別子 */
  private static final String RESET_TARGET_FLAG = "reset_target_params";

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  /** 期限状態（期限前） */
  public static final int LIMIT_STATE_BEFORE = -1;

  /** 期限状態（期限当日） */
  public static final int LIMIT_STATE_TODAY = 0;

  /** 期限状態（期限後） */
  public static final int LIMIT_STATE_AFTER = 1;

  public static final String TODO_PORTLET_NAME = "ToDo";

  /**
   * Todo オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isJoin
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipTTodo getEipTTodo(RunData rundata, Context context,
      boolean isJoin) throws ALPageNotFoundException {
    String todoid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    int uid = ALEipUtils.getUserId(rundata);
    try {
      if (todoid == null || Integer.valueOf(todoid) == null) {
        // Todo IDが空の場合
        logger.debug("[Todo] Empty ID...");
        throw new ALPageNotFoundException();
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipTTodo.TODO_ID_PK_COLUMN, todoid);
      exp.andExp(ExpressionFactory.matchDbExp(EipTTodo.TURBINE_USER_PROPERTY
        + "."
        + TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(ALEipUtils
        .getUserId(rundata))));

      List<EipTTodo> todoList = Database.query(EipTTodo.class, exp).fetchList();

      if (todoList == null || todoList.size() == 0) {
        // 指定したTodo IDのレコードが見つからない場合
        logger.debug("[Todo] Not found ID...");
        throw new ALPageNotFoundException();
      }

      // アクセス権の判定
      EipTTodo todo = todoList.get(0);
      if ((uid != todo.getUserId().intValue())
        && "F".equals(todo.getPublicFlag())) {
        logger.debug("[Todo] Invalid user access...");
        throw new ALPageNotFoundException();
      }
      return todo;
    } catch (ALPageNotFoundException pageNotFound) {
      // logger.error("[ToDoUtils]", pageNotFound);
      throw pageNotFound;
    } catch (Exception ex) {
      logger.error("todo", ex);
      return null;
    }
  }

  /**
   * 公開 Todo オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param isJoin
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipTTodo getEipTPublicTodo(RunData rundata, Context context,
      boolean isJoin) {
    String todoid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (todoid == null || Integer.valueOf(todoid) == null) {
        // Todo IDが空の場合
        logger.debug("[Todo] Empty ID...");
        return null;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipTTodo.TODO_ID_PK_COLUMN, todoid);
      exp
        .andExp(ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "T"));

      List<EipTTodo> todoList = Database.query(EipTTodo.class, exp).fetchList();

      if (todoList == null || todoList.size() == 0) {
        // 指定したTodo IDのレコードが見つからない場合
        logger.debug("[Todo] Not found ID...");
        return null;
      }
      return todoList.get(0);
    } catch (Exception ex) {
      logger.error("todo", ex);
      return null;
    }
  }

  /**
   * Todoカテゴリ オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTTodoCategory getEipTTodoCategory(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    String categoryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    if (categoryid == null || Integer.valueOf(categoryid) == null) {
      // カテゴリIDが空の場合
      logger.debug("[Todo] Empty ID...");
      throw new ALPageNotFoundException();
    }

    try {
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTTodoCategory.CATEGORY_ID_PK_COLUMN,
          categoryid);

      List<EipTTodoCategory> categoryList =
        Database.query(EipTTodoCategory.class, exp1).fetchList();

      if (categoryList == null || categoryList.size() == 0) {
        // 指定したカテゴリIDのレコードが見つからない場合
        logger.debug("[Todo] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return categoryList.get(0);
    } catch (Exception ex) {
      logger.error("todo", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * Todoカテゴリ オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTTodoCategory getEipTTodoCategory(Long category_id) {
    try {
      return Database.get(EipTTodoCategory.class, category_id);
    } catch (Exception ex) {
      logger.error("todo", ex);
      return null;
    }
  }

  /**
   * 
   * @return
   */
  public static Date getEmptyDate() {
    Calendar cal = Calendar.getInstance();
    cal.set(9999, 11, 31);
    return cal.getTime();
  }

  /**
   * 
   * @param date
   * @return
   */
  public static boolean isEmptyDate(Date date) {
    if (date == null) {
      return false;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR) == 9999;
  }

  /**
   * 優先度を表す画像名を取得します。 <BR>
   * 1 : 高い : priority_high.gif <BR>
   * 2 : やや高い : priority_middle_high.gif <BR>
   * 3 : 普通 : priority_middle.gif <BR>
   * 4 : やや低い : priority_middle_low.gif <BR>
   * 5 : 低い : priority_low.gif <BR>
   * 
   * @param i
   * @return
   */
  public static String getPriorityImage(int i) {
    String[] temp =
      {
        "priority_high.gif",
        "priority_middle_high.gif",
        "priority_middle.gif",
        "priority_middle_low.gif",
        "priority_low.gif" };
    String image = null;
    try {
      image = temp[i - 1];
    } catch (Throwable ignore) {
    }
    return image;
  }

  /**
   * 優先度を表す文字列を取得します。 <BR>
   * 1 : 高い : priority_high.gif <BR>
   * 2 : やや高い : priority_middle_high.gif <BR>
   * 3 : 普通 : priority_middle.gif <BR>
   * 4 : やや低い : priority_middle_low.gif <BR>
   * 5 : 低い : priority_low.gif <BR>
   * 
   * @param i
   * @return
   */
  public static String getPriorityString(int i) {
    String[] temp = { "高い", "やや高い", "普通", "やや低い", "低い" };
    String string = null;
    try {
      string = temp[i - 1];
    } catch (Throwable ignore) {
    }
    return string;
  }

  /**
   * 状態を表す画像名を取得します。 <BR>
   * 0 : 未着手 <BR>
   * 10 : 10% <BR>
   * 20 : 20% <BR>
   * : :<BR>
   * 90 : 90% <BR>
   * 100 : 完了 <BR>
   * 
   * @param i
   * @return
   */
  public static String getStateImage(int i) {
    String[] temp =
      {
        "state_000.gif",
        "state_010.gif",
        "state_020.gif",
        "state_030.gif",
        "state_040.gif",
        "state_050.gif",
        "state_060.gif",
        "state_070.gif",
        "state_080.gif",
        "state_090.gif",
        "state_100.gif" };
    String image = null;
    try {
      image = temp[i / 10];
    } catch (Throwable ignore) {
    }
    return image;
  }

  /**
   * 状態を表す文字列を取得します。 <BR>
   * 0 : 未着手 <BR>
   * 10 : 10% <BR>
   * 20 : 20% <BR>
   * : :<BR>
   * 90 : 90% <BR>
   * 100 : 完了 <BR>
   * 
   * @param i
   * @return
   */
  public static String getStateString(int i) {
    if (i == 0) {
      return "未着手";
    } else if (i == 100) {
      return "完了";
    } else {
      return new StringBuffer().append(i).append("%").toString();
    }
  }

  /**
   * 表示切り替えで指定したグループ ID を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
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
   * 表示切り替えで指定したユーザ ID を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetUserId(RunData rundata, Context context) {
    String target_user_id = null;
    String idParam = rundata.getParameters().getString(TARGET_USER_ID);
    target_user_id = ALEipUtils.getTemp(rundata, context, TARGET_USER_ID);

    if (idParam == null && (target_user_id == null)) {
      ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, "all");
      target_user_id = "all";
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, idParam);
      target_user_id = idParam;
    }
    return target_user_id;
  }

  /**
   * 表示切り替えで指定した検索キーワードを取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetKeyword(RunData rundata, Context context) {
    String target_keyword = null;
    String keywordParam = rundata.getParameters().getString(TARGET_KEYWORD);
    target_keyword = ALEipUtils.getTemp(rundata, context, TARGET_KEYWORD);

    if (keywordParam == null && (target_keyword == null)) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
      target_keyword = "";
    } else if (keywordParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, keywordParam.trim());
      target_keyword = keywordParam;
    }
    return target_keyword;
  }

  public static int getViewId(RunData rundata, Context context, int uid)
      throws ALDBErrorException, ALPageNotFoundException {
    int view_uid = -1;
    EipTTodo record;
    try {
      record = ToDoUtils.getEipTTodo(rundata, context, false);
    } catch (ALPageNotFoundException ex) {
      record = null;
    }
    if (record != null) {
      view_uid = record.getUserId();
    } else {
      if (rundata.getParameters().containsKey("view_uid")) {
        view_uid =
          Integer.parseInt(rundata.getParameters().getString("view_uid"));
      } else {
        view_uid = uid;
      }
    }
    ALEipUtils.setTemp(rundata, context, "view_uid", String.valueOf(view_uid));
    return view_uid;
  }

  /**
   * 表示切り替えのリセットフラグがあるかを返す．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static boolean hasResetKeywordFlag(RunData rundata, Context context) {
    String resetflag = rundata.getParameters().getString(RESET_KEYWORD_FLAG);
    return resetflag != null;
  }

  /**
   * フィルターを初期化する．
   * 
   * @param rundata
   * @param context
   * @param className
   */
  public static void resetKeyword(RunData rundata, Context context,
      String className) {
    ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
  }

  /**
   * 表示切り替えのリセットフラグがあるかを返す．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static boolean hasResetTargetFlag(RunData rundata, Context context) {
    String resetflag = rundata.getParameters().getString(RESET_TARGET_FLAG);
    return resetflag != null;
  }

  /**
   * フィルターを初期化する．
   * 
   * @param rundata
   * @param context
   * @param className
   */
  public static void resetTarget(RunData rundata, Context context,
      String className) {
    ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, "all");
    ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, "all");
  }

  /**
   * 表示切り替えのリセットフラグがあるかを返す．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static boolean hasResetFlag(RunData rundata, Context context) {
    String resetflag = rundata.getParameters().getString(RESET_FLAG);
    return resetflag != null;
  }

  /**
   * フィルターを初期化する．
   * 
   * @param rundata
   * @param context
   * @param className
   */
  public static void resetFilter(RunData rundata, Context context,
      String className) {
    ALEipUtils.removeTemp(rundata, context, new StringBuffer()
      .append(className)
      .append(ALEipConstants.LIST_FILTER)
      .toString());
    ALEipUtils.removeTemp(rundata, context, new StringBuffer()
      .append(className)
      .append(ALEipConstants.LIST_FILTER_TYPE)
      .toString());
  }

  /**
   * 現在日時と指定した日時とを比較する．
   * 
   * @param endDate
   * @return 現在日時 < 指定日時 ：LIMIT_STATE_BEFORE <br>
   *         現在日時 == 指定日時 ：LIMIT_STATE_TODAY <br>
   *         現在日時 > 指定日時 ：LIMIT_STATE_AFTER
   */
  public static int getLimitState(Date endDate) {
    if (endDate == null) {
      return LIMIT_STATE_BEFORE;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    Date nowDate = calendar.getTime();
    calendar.setTime(endDate);
    Date endDate1 = calendar.getTime();

    if (calendar.get(Calendar.YEAR) == 9999
      && calendar.get(Calendar.MONTH) == 11
      && calendar.get(Calendar.DATE) == 31) {
      // 締切日時が未指定の場合
      return LIMIT_STATE_BEFORE;
    }

    int result = nowDate.compareTo(endDate1);
    if (result < 0) {
      result = LIMIT_STATE_BEFORE;
    } else if (result == 0) {
      result = LIMIT_STATE_TODAY;
    } else {
      result = LIMIT_STATE_AFTER;
    }
    return result;
  }

  /**
   * プルダウン用のカテゴリーリストを返します
   * 
   * @param rundata
   * @return
   */
  public static ArrayList<ToDoCategoryResultData> getCategoryList(
      RunData rundata) {
    ArrayList<ToDoCategoryResultData> categoryList =
      new ArrayList<ToDoCategoryResultData>();

    ToDoCategoryResultData rd;

    try {
      // カテゴリ一覧
      List<EipTTodoCategory> categoryList2 =
        Database.query(EipTTodoCategory.class).orderAscending(
          EipTTodoCategory.CATEGORY_NAME_PROPERTY).fetchList();

      StringBuffer title;
      ALEipUser user;
      for (EipTTodoCategory record : categoryList2) {
        user = ALEipUtils.getALEipUser(record.getUserId());
        // exclude 「その他」
        if (user != null) {
          rd = new ToDoCategoryResultData();
          rd.initField();
          rd.setCategoryId(record.getCategoryId().longValue());
          title = new StringBuffer(record.getCategoryName());
          // title.append(" （");
          // title.append(user.getAliasName());
          // title.append("）");
          rd.setCategoryName(title.toString());
          categoryList.add(rd);
        }
      }
    } catch (Exception ex) {
      logger.error("todo", ex);
    }

    // その他追加
    EipTTodoCategory unCategorized =
      Database.query(EipTTodoCategory.class).where(
        Operations.eq(EipTTodoCategory.TURBINE_USER_PROPERTY, 0)).fetchSingle();
    rd = new ToDoCategoryResultData();
    rd.initField();
    rd.setCategoryId(unCategorized.getCategoryId());
    rd.setCategoryName(unCategorized.getCategoryName());
    categoryList.add(rd);

    return categoryList;
  }

  /**
   * ToDo を作成・更新した通知を送信します。
   * 
   * @param schedule
   * @param loginName
   * @param recipients
   * @param isNew
   */
  public static void createToDoActivity(EipTTodo todo, String loginName,
      List<String> recipients, boolean isNew, int operationUserId) {

    ALActivity RecentActivity =
      ALActivity.getRecentActivity("Todo", todo.getTodoId(), 1f);
    boolean isDeletePrev =
      RecentActivity != null && RecentActivity.isReplace(loginName);

    String title =
      new StringBuilder("ToDo「").append(todo.getTodoName()).append(
        isNew ? "」を追加しました。" : "」を編集しました。").toString();
    String portletParams =
      new StringBuilder("?template=ToDoDetailScreen")
        .append("&entityid=")
        .append(todo.getTodoId())
        .toString();
    if (recipients != null && recipients.size() > 0) {
      // 個人向け通知
      ALActivityService.create(new ALActivityPutRequest()
        .withAppId("ToDo")
        .withLoginName(loginName)
        .withUserId(operationUserId)
        .withPortletParams(portletParams)
        .withRecipients(recipients)
        .withTitle(title)
        .withPriority(1f)
        .withExternalId(String.valueOf(todo.getTodoId())));
    }
    if (todo.getPublicFlag().equals("T")) {
      // 全体向けアクティビティー
      ALActivityService.create(new ALActivityPutRequest()
        .withAppId("ToDo")
        .withUserId(operationUserId)
        .withLoginName(loginName)
        .withPortletParams(portletParams)
        .withTitle(title)
        .withPriority(0f)
        .withExternalId(String.valueOf(todo.getTodoId())));
    }

    if (isDeletePrev) {
      RecentActivity.delete();
    }
  }

  /**
   * パソコンへ送信するメールの内容を作成する．
   * 
   * @return
   * @throws ALDBErrorException
   */
  public static String createMsgForPc(RunData rundata, EipTTodo todo,
      List<ALEipUser> memberList, boolean isNew) throws ALDBErrorException {
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);
    ALEipUser loginUser = null;
    ALBaseUser user = null;

    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    String CR = System.getProperty("line.separator");
    StringBuffer body = new StringBuffer("");
    body.append(loginUser.getAliasName().toString());
    if (!"".equals(user.getEmail())) {
      body.append("(").append(user.getEmail()).append(")");
    }
    if (isNew) {
      body.append("さんがToDoを追加しました。").append(CR).append(CR);
    } else {
      body.append("さんがToDoを編集しました。").append(CR).append(CR);
    }
    body
      .append("[ToDo詳細]")
      .append(CR)
      .append(todo.getTodoName().toString())
      .append(CR);

    body
      .append("[担当者]")
      .append(CR)
      .append(
        ALEipUtils
          .getALEipUser(todo.getTurbineUser().getUserId())
          .getAliasName())
      .append(CR);

    if (!isEmptyDate(todo.getStartDate())) {
      body.append("[開始日] ").append(CR).append(
        ALDateUtil.format(todo.getStartDate(), "yyyy年M月d日(E)")).append(CR);
    }
    if (!isEmptyDate(todo.getEndDate())) {
      body.append("[締切日] ").append(CR).append(
        ALDateUtil.format(todo.getEndDate(), "yyyy年M月d日(E)")).append(CR);
    }

    body.append("[進捗]").append(CR).append(todo.getState().toString()).append(
      "%").append(CR);

    body.append("[優先度]").append(CR).append(
      ToDoUtils.getPriorityString(todo.getPriority())).append(CR);

    if (todo.getNote().toString().length() > 0) {
      body.append("[メモ]").append(CR).append(todo.getNote().toString()).append(
        CR);
    }
    body.append(CR);
    body
      .append("[")
      .append(ALOrgUtilsService.getAlias())
      .append("へのアクセス]")
      .append(CR);
    if (enableAsp) {
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
    } else {
      body.append("・社外").append(CR);
      body.append("　").append(ALMailUtils.getGlobalurl()).append(CR);
      body.append("・社内").append(CR);
      body.append("　").append(ALMailUtils.getLocalurl()).append(CR).append(CR);
    }

    body.append("---------------------").append(CR);
    body.append(ALOrgUtilsService.getAlias()).append(CR);

    return body.toString();
  }

  /**
   * todo-notification-mail.vmからパソコンへ送信するメールの内容を作成する．
   * 
   * @return
   * @throws ALDBErrorException
   */
  public static String createMsgForPcTmpl(RunData rundata, EipTTodo todo,
      List<ALEipUser> memberList, boolean isNew) throws ALDBErrorException {
    VelocityContext context = new VelocityContext();
    boolean enableAsp = JetspeedResources.getBoolean("aipo.asp", false);
    ALEipUser loginUser = null;
    ALBaseUser user = null;

    try {
      loginUser = ALEipUtils.getALEipUser(rundata);
      user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(loginUser
          .getUserId()
          .toString()));
    } catch (Exception e) {
      return "";
    }
    context.put("loginUser", loginUser.getAliasName().toString());
    context.put("hasEmail", !user.getEmail().equals(""));
    context.put("email", user.getEmail());
    context.put("isNew", isNew);
    context.put("todoName", todo.getTodoName().toString());
    context.put("turbineUser", ALEipUtils.getALEipUser(
      todo.getTurbineUser().getUserId()).getAliasName());
    context.put("hasStartDate", !isEmptyDate(todo.getStartDate()));
    context.put("startDate", ALDateUtil.format(
      todo.getStartDate(),
      "yyyy年M月d日(E)"));
    context.put("hasEndDate", !isEmptyDate(todo.getEndDate()));
    context
      .put("endDate", ALDateUtil.format(todo.getEndDate(), "yyyy年M月d日(E)"));
    context.put("state", todo.getState().toString());
    context.put("priority", todo.getPriority());
    context.put("hasNote", todo.getNote().toString().length() > 0);
    context.put("note", todo.getNote().toString());
    context.put("serviceAlias", ALOrgUtilsService.getAlias());
    context.put("enableAsp", enableAsp);
    context.put("globalurl", ALMailUtils.getGlobalurl());
    context.put("localurl", ALMailUtils.getLocalurl());

    CustomLocalizationService locService =
      (CustomLocalizationService) ServiceUtil
        .getServiceByName(LocalizationService.SERVICE_NAME);
    String lang = locService.getLocale(rundata).getLanguage();
    StringWriter writer = new StringWriter();
    try {
      if (lang != null && lang.equals("ja")) {
        Template template =
          Velocity.getTemplate("portlets/mail/"
            + lang
            + "/todo-notification-mail.vm", "utf-8");
        template.merge(context, writer);
      } else {
        Template template =
          Velocity.getTemplate(
            "portlets/mail/todo-notification-mail.vm",
            "utf-8");
        template.merge(context, writer);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    writer.flush();
    String ret = writer.getBuffer().toString();
    return ret;
  }

  /**
   * 
   * PSMLに設定されているデータと比較して valueが正しい値ならその値を新しくPSMLに保存。
   * 
   * 
   * @param rundata
   * @param context
   * @param config
   * @return
   */
  public static String passPSML(RunData rundata, Context context, String key,
      String value) {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    PortletConfig config = portlet.getPortletConfig();
    if (value == null || "".equals(value)) {
      value = config != null ? config.getInitParameter(key) : "";
    } else {
      ALEipUtils.setPsmlParameters(rundata, context, key, value);
    }
    return value;
  }
}
