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
package com.aimluck.eip.modules.actions.todo;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.todo.CellToDoSelectData;

/**
 * ToDoのアクションクラスです。 <BR>
 * 
 */
public class CellToDoAction extends ToDoAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellToDoAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doTodo_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doTodo_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doTodo_list(rundata, context);
      } else if ("category_detail".equals(mode)) {
        doTodo_category_detail(rundata, context);
      }
      if (getMode() == null) {
        doTodo_menu(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("todo", ex);
    }

  }

  /**
   * ToDo のメニューを表示する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_menu(RunData rundata, Context context) throws Exception {
    putData(rundata, context);
    setTemplate(rundata, "todo-menu");
  }

  /**
   * ToDo登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_form(RunData rundata, Context context) throws Exception {

  }

  /**
   * ToDoを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_insert(RunData rundata, Context context) throws Exception {

  }

  /**
   * ToDoを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_update(RunData rundata, Context context) throws Exception {

  }

  /**
   * ToDoを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_detail(RunData rundata, Context context) throws Exception {
    CellToDoSelectData detailData = new CellToDoSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "todo-detail");
    } else {
      doTodo_list(rundata, context);
    }
  }

  /**
   * ToDoを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_delete(RunData rundata, Context context) throws Exception {

  }

  /**
   * ToDoを削除します。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_multi_delete(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * ToDoの状態を完了にします。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_multi_complete(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * カテゴリ登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_category_form(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * カテゴリを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_category_insert(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * カテゴリを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_category_update(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * カテゴリを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_category_delete(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * カテゴリを削除します。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_category_multi_delete(RunData rundata, Context context)
      throws Exception {

  }

  /**
   * ToDoの状態を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doTodo_state_update(RunData rundata, Context context)
      throws Exception {

  }
}
