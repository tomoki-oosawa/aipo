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
package com.aimluck.eip.todo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
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

  /** 期限状態（期限前） */
  public static final int LIMIT_STATE_BEFORE = -1;

  /** 期限状態（期限当日） */
  public static final int LIMIT_STATE_TODAY = 0;

  /** 期限状態（期限後） */
  public static final int LIMIT_STATE_AFTER = 1;

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
        return null;
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
        return null;
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
      logger.error(pageNotFound);
      throw pageNotFound;
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
      logger.error("Exception", ex);
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
      Expression exp2 =
        ExpressionFactory.matchExp(EipTTodoCategory.USER_ID_PROPERTY, Long
          .valueOf(ALEipUtils.getUserId(rundata)));

      List<EipTTodoCategory> categoryList =
        Database
          .query(EipTTodoCategory.class, exp1)
          .andQualifier(exp2)
          .fetchList();

      if (categoryList == null || categoryList.size() == 0) {
        // 指定したカテゴリIDのレコードが見つからない場合
        logger.debug("[Todo] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return categoryList.get(0);
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
      logger.error("Exception", ex);
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
      // 締め切り日時が未指定の場合
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

}
