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

package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.eventlog.action.ALActionEventlogConstants;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class ToDoFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoFormData.class.getName());

  /** ToDo名 */
  private ALStringField todo_name;

  /** カテゴリID */
  private ALNumberField category_id;

  /** 優先度 */
  private ALNumberField priority;

  /** 状態 */
  private ALNumberField state;

  /** メモ */
  private ALStringField note;

  /** 開始日 */
  private ALDateField start_date;

  /** 締め切り日 */
  private ALDateField end_date;

  /** 開始日指定フラグ */
  private ALStringField start_date_check;

  /** 締め切り日指定フラグ */
  private ALStringField end_date_check;

  /** カテゴリ一覧 */
  private List<ToDoCategoryResultData> categoryList;

  /** 現在の年 */
  private int currentYear;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** */
  private boolean is_new_category;

  /** 公開/非公開フラグ */
  private ALStringField public_flag;

  /** スケジュール表示フラグ */
  private ALStringField addon_schedule_flg;

  private EipTTodoCategory category;

  /** ログインユーザーのID * */
  private int user_id;

  /** ACL用の変数 * */
  private String aclPortletFeature;

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
    is_new_category = rundata.getParameters().getBoolean("is_new_category");

    user_id = ALEipUtils.getUserId(rundata);

    aclPortletFeature =
      ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // ToDo名
    todo_name = new ALStringField();
    todo_name.setFieldName("ToDo名");
    todo_name.setTrim(true);
    // カテゴリID
    category_id = new ALNumberField();
    category_id.setFieldName("カテゴリ");
    // 優先度
    priority = new ALNumberField(3);
    priority.setFieldName("優先度");
    // 状態
    state = new ALNumberField();
    state.setFieldName("進捗");
    // メモ
    note = new ALStringField();
    note.setFieldName("メモ");
    note.setTrim(false);
    // 開始日
    start_date = new ALDateField();
    start_date.setFieldName("開始日");
    start_date.setValue(new Date());
    // 締め切り日
    end_date = new ALDateField();
    end_date.setFieldName("締め切り日");
    end_date.setValue(new Date());
    // 開始日指定フラグ
    start_date_check = new ALStringField();
    start_date_check.setFieldName("指定しない");
    // 締め切り日指定フラグ
    end_date_check = new ALStringField();
    end_date_check.setFieldName("指定しない");
    // 現在の年
    currentYear = Calendar.getInstance().get(Calendar.YEAR);

    // カテゴリ
    category_name = new ALStringField();
    category_name.setFieldName("カテゴリ名");

    // 公開区分
    public_flag = new ALStringField();
    public_flag.setFieldName("公開区分");
    public_flag.setValue("T");
    public_flag.setTrim(true);

    addon_schedule_flg = new ALStringField();
    addon_schedule_flg.setFieldName("スケジュールへの表示");
    addon_schedule_flg.setValue("T");
    addon_schedule_flg.setTrim(true);
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata, Context context) {
    // カテゴリ一覧
    categoryList = new ArrayList<ToDoCategoryResultData>();
    try {

      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      Expression exp2 =
        ExpressionFactory.matchExp(EipTTodoCategory.USER_ID_PROPERTY, Integer
          .valueOf(0));

      List<EipTTodoCategory> categoryList2 =
        Database
          .query(EipTTodoCategory.class, exp1)
          .orQualifier(exp2)
          .orderAscending(EipTTodoCategory.CATEGORY_NAME_PROPERTY)
          .fetchList();

      for (EipTTodoCategory record : categoryList2) {
        ToDoCategoryResultData rd = new ToDoCategoryResultData();
        rd.initField();
        rd.setCategoryId(record.getCategoryId().longValue());
        rd.setCategoryName(record.getCategoryName());
        categoryList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * ToDoの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // ToDo名必須項目
    todo_name.setNotNull(true);
    // ToDo名の文字数制限
    todo_name.limitMaxLength(50);
    // メモの文字数制限
    note.limitMaxLength(1000);
    if (is_new_category) {
      // カテゴリ名必須項目
      category_name.setNotNull(true);
      // カテゴリ名文字数制限
      category_name.limitMaxLength(50);
    }
  }

  /**
   * ToDoのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {

    try {

      Expression exp =
        ExpressionFactory.matchExp(
          EipTTodoCategory.CATEGORY_NAME_PROPERTY,
          category_name.getValue());

      Expression exp2 =
        ExpressionFactory.matchExp(EipTTodoCategory.USER_ID_PROPERTY, Integer
          .valueOf(0));

      Expression exp3 =
        ExpressionFactory.matchExp(EipTTodoCategory.USER_ID_PROPERTY, Integer
          .valueOf(this.user_id));

      if (Database.query(EipTTodoCategory.class, exp).andQualifier(
        exp2.orExp(exp3)).fetchList().size() != 0) {
        msgList.add("カテゴリ名『 <span class='em'>"
          + category_name.toString()
          + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    boolean isStartDate = false;
    // ToDo名
    todo_name.validate(msgList);
    // 開始日指定フラグが設定されている場合は開始日入力フォームチェックを行いません。
    if (start_date_check.getValue() == null) {
      // 開始日
      isStartDate = start_date.validate(msgList);
    }
    // 締め切り日指定フラグが設定されている場合は締め切り日入力フォームチェックを行いません。
    if (end_date_check.getValue() == null) {
      // 締め切り日
      if (end_date.validate(msgList) && isStartDate) {
        try {
          if (end_date.getValue().getDate().before(
            start_date.getValue().getDate())) {
            msgList
              .add("『 <span class='em'>締め切り日</span> 』は『 <span class='em'>開始日</span> 』以降の日付で指定してください。");
          }
        } catch (Exception e) {
          logger.error("Exception", e);
        }
      }
    }
    // メモ
    note.validate(msgList);
    if (is_new_category) {
      // カテゴリ名
      category_name.validate(msgList);
    }
    return (msgList.size() == 0);

  }

  /**
   * ToDoをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    String date1 = null;
    try {
      // オブジェクトモデルを取得
      EipTTodo todo = ToDoUtils.getEipTTodo(rundata, context, false);
      if (todo == null) {
        return false;
      }
      // ToDo名
      todo_name.setValue(todo.getTodoName());
      // カテゴリID
      category_id.setValue(todo
        .getEipTTodoCategory()
        .getCategoryId()
        .longValue());
      // 開始日
      if (ToDoUtils.isEmptyDate(todo.getStartDate())) {
        start_date_check.setValue("TRUE");
        start_date.setValue(date1);
      } else {
        start_date.setValue(todo.getStartDate());
      }
      // 締め切り日
      if (ToDoUtils.isEmptyDate(todo.getEndDate())) {
        end_date_check.setValue("TRUE");
        end_date.setValue(date1);
      } else {
        end_date.setValue(todo.getEndDate());
      }
      // 状態
      state.setValue(todo.getState().longValue());
      // 優先度
      priority.setValue(todo.getPriority().longValue());
      // メモ
      note.setValue(todo.getNote());
      // 公開区分
      public_flag.setValue(todo.getPublicFlag());

      addon_schedule_flg.setValue(todo.getAddonScheduleFlg());
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * ToDoをデータベースから削除します。 <BR>
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
      EipTTodo todo = ToDoUtils.getEipTTodo(rundata, context, false);
      if (todo == null) {
        return false;
      }

      // entityIdの取得
      int entityId = todo.getTodoId();
      // todo名の取得
      String todoName = todo.getTodoName();

      // Todoを削除
      Database.delete(todo);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_TODO,
        todoName);

    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * ToDoをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      if (is_new_category) {
        // カテゴリの登録処理
        if (!insertCategoryData(rundata, context, msgList)) {
          return false;
        }
      } else {
        category =
          ToDoUtils.getEipTTodoCategory(Long.valueOf(category_id.getValue()));
      }

      // 新規オブジェクトモデル
      EipTTodo todo = Database.create(EipTTodo.class);

      // Todo名
      todo.setTodoName(todo_name.getValue());
      // カテゴリID
      todo.setEipTTodoCategory(category);
      // ユーザーID
      TurbineUser tuser =
        Database.get(TurbineUser.class, Integer.valueOf(user_id));
      todo.setTurbineUser(tuser);
      // 開始日
      if (start_date_check.getValue() == null) {
        todo.setStartDate(start_date.getValue().getDate());
      } else {
        todo.setStartDate(ToDoUtils.getEmptyDate());
      }
      // 締め切り日
      if (end_date_check.getValue() == null) {
        todo.setEndDate(end_date.getValue().getDate());
      } else {
        todo.setEndDate(ToDoUtils.getEmptyDate());
      }
      // 状態
      todo.setState(Short.valueOf((short) state.getValue()));
      // 優先度
      todo.setPriority(Short.valueOf((short) priority.getValue()));
      // メモ
      todo.setNote(note.getValue());
      // 公開区分
      todo.setPublicFlag(public_flag.getValue());

      todo.setAddonScheduleFlg(addon_schedule_flg.getValue());
      // 作成日
      todo.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      todo.setUpdateDate(Calendar.getInstance().getTime());
      // Todoを登録
      Database.commit();

      if (category != null) {
        // カテゴリIDの設定
        category_id.setValue(category.getCategoryId().longValue());
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        todo.getTodoId(),
        ALEventlogConstants.PORTLET_TYPE_TODO,
        todo_name.getValue());

      if (is_new_category) {
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          category.getCategoryId(),
          ALEventlogConstants.PORTLET_TYPE_TODO_CATEGORY,
          category_name.getValue());
      }

    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * ToDoカテゴリをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  private boolean insertCategoryData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      setAclPortletFeature(ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_SELF);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_INSERT);
      setAclPortletFeature(ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF);

      // 新規オブジェクトモデル
      category = Database.create(EipTTodoCategory.class);

      // カテゴリ名
      category.setCategoryName(category_name.getValue());
      // ユーザーID
      category.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // 作成日
      category.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      category.setUpdateDate(Calendar.getInstance().getTime());
    } catch (ALPermissionException e) {
      msgList.add(ALAccessControlConstants.DEF_PERMISSION_ERROR_STR);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      msgList.add("エラーが発生しました。");
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているToDoを更新します。 <BR>
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
      // オブジェクトモデルを取得
      EipTTodo todo = ToDoUtils.getEipTTodo(rundata, context, false);
      if (todo == null) {
        return false;
      }

      if (is_new_category) {
        // カテゴリの登録処理
        if (!insertCategoryData(rundata, context, msgList)) {
          return false;
        }
      } else {
        category =
          ToDoUtils.getEipTTodoCategory(Long.valueOf(category_id.getValue()));
      }

      // Todo名
      todo.setTodoName(todo_name.getValue());
      // カテゴリID
      todo.setEipTTodoCategory(category);
      // ユーザーID
      TurbineUser tuser =
        Database.get(TurbineUser.class, Integer.valueOf(user_id));

      todo.setTurbineUser(tuser);
      // 開始日
      if (start_date_check.getValue() == null) {
        todo.setStartDate(start_date.getValue().getDate());
      } else {
        todo.setStartDate(ToDoUtils.getEmptyDate());
      }
      // 締め切り日
      if (end_date_check.getValue() == null) {
        todo.setEndDate(end_date.getValue().getDate());
      } else {
        todo.setEndDate(ToDoUtils.getEmptyDate());
      }
      // 状態
      todo.setState(Short.valueOf((short) state.getValue()));
      // 優先度
      todo.setPriority(Short.valueOf((short) priority.getValue()));
      // メモ
      todo.setNote(note.getValue());
      // 公開区分
      todo.setPublicFlag(public_flag.getValue());

      todo.setAddonScheduleFlg(addon_schedule_flg.getValue());
      // 更新日
      todo.setUpdateDate(Calendar.getInstance().getTime());

      // Todo を更新
      Database.commit();

      if (category != null) {
        // カテゴリIDの設定
        category_id.setValue(category.getCategoryId().longValue());
      }
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        todo.getTodoId(),
        ALEventlogConstants.PORTLET_TYPE_TODO,
        todo_name.getValue());

      if (is_new_category) {
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          category.getCategoryId(),
          ALEventlogConstants.PORTLET_TYPE_TODO_CATEGORY,
          category_name.getValue(),
          ALActionEventlogConstants.EVENT_MODE_INSERT);
      }

    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * カテゴリIDを取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
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
   * 優先度を取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getPriority() {
    return priority;
  }

  /**
   * 状態を取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getState() {
    return state;
  }

  /**
   * ToDo名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getTodoName() {
    return todo_name;
  }

  /**
   * 締め切り日を取得します。 <BR>
   * 
   * @return
   */
  public ALDateField getEndDate() {
    return end_date;
  }

  /**
   * 開始日を取得します。 <BR>
   * 
   * @return
   */
  public ALDateField getStartDate() {
    return start_date;
  }

  /**
   * カテゴリ一覧を取得します。 <BR>
   * 
   * @return
   */
  public List<ToDoCategoryResultData> getCategoryList() {
    return categoryList;
  }

  /**
   * 締め切り日指定フラグを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getEndDateCheck() {
    return end_date_check;
  }

  /**
   * 開始日指定フラグを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getStartDateCheck() {
    return start_date_check;
  }

  /**
   * 
   * @return
   */
  public int getCurrentYear() {
    return currentYear;
  }

  /**
   * @return
   */
  public boolean isNewCategory() {
    return is_new_category;
  }

  /**
   * カテゴリ名を取得します。
   * 
   * @return
   */
  public ALStringField getCategoryName() {
    return category_name;
  }

  /**
   * 公開/非公開フラグを取得する．
   * 
   * @return
   */
  public ALStringField getPublicFlag() {
    return public_flag;
  }

  public ALStringField getAddonScheduleFlg() {
    return addon_schedule_flg;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  public void setAclPortletFeature(String aclPortletFeature) {
    this.aclPortletFeature = aclPortletFeature;
  }

  public void setCategoryId(long i) {
    category_id.setValue(i);
  }
}
