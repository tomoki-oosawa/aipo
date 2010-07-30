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
package com.aimluck.eip.modules.actions.note;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.note.NoteClientSelectData;
import com.aimluck.eip.note.NoteFormData;
import com.aimluck.eip.note.NoteMultiDelete;
import com.aimluck.eip.note.NoteMultiStateUpdate;
import com.aimluck.eip.note.NoteSelectData;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 伝言メモの取り扱いに関するアクションクラスです。 <br />
 */
public class NoteAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(NoteAction.class.getName());

  /**
   * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildNormalContext(org.apache.jetspeed.portal.portlets.VelocityPortlet,
   *      org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
   */
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    // セッション情報をクリアする
    clearNoteSession(rundata, context);

    ALEipUtils.setTemp(rundata, context, "tab", "received_notes");
    doNote_normal(rundata, context);
  }

  /**
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);

    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doNote_create_note(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doNote_show_note(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doNote_list(rundata, context);
      }
      if (getMode() == null) {
        doNote_list(rundata, context);
      }
    } catch (Exception e) {
      logger.error(e);
    }
  }

  public void doNote_normal(RunData rundata, Context context) throws Exception {
    // ALEipUtils.setTemp(rundata, context, "tab", "received_notes");
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    NoteClientSelectData listData = new NoteClientSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（通常時）
    listData.setRowsNum(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(rundata,
        context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "note");
  }

  /**
   * 新規に伝言メモを作成するページを表示する．
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_create_note(RunData rundata, Context context)
      throws Exception {
    context.put(NoteUtils.NOTE_VIEW_TYPE, NoteUtils.NOTE_VIEW_TYPE_LIST);
    NoteFormData formData = new NoteFormData();
    formData.initField();
    formData.enableAddDestTypes();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "note-form");
  }

  public void doNote_insert(RunData rundata, Context context) throws Exception {

    NoteFormData formData = new NoteFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      int msgType = formData.getMsgType();
      context.put("msg_type", "" + msgType);
      ALEipUtils.setTemp(rundata, context, "tab", "sent_notes");
      doNote_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(
      // jsLink
      // .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
      // .addQueryData("eventSubmit_doNote_list2", "1")
      // .addQueryData("tab", "sent_notes")
      // .addQueryData("msg_type", msgType)
      // .toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "note-form");
    }
  }

  /**
   * 伝言メモを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_list(RunData rundata, Context context) throws Exception {
    ALEipUtils.removeTemp(rundata, context, NoteUtils.TARGET_USER_ID);
    NoteSelectData listData = new NoteSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
        .getPortlet(rundata, context).getPortletConfig().getInitParameter(
            "p1b-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(rundata,
        context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "note-list");
  }

  /**
   * 伝言メモを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_list2(RunData rundata, Context context) throws Exception {
    String msgType = rundata.getParameters().getString("msg_type");
    context.put("msg_type", msgType);
    doNote_list(rundata, context);
  }

  public void doNote_show_note(RunData rundata, Context context)
      throws Exception {
    NoteSelectData listData = new NoteSelectData();
    listData.initField();
    listData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "note-detail");
  }

  /**
   * 伝言メモを削除する（単数）． <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_delete(RunData rundata, Context context) throws Exception {
    NoteFormData formData = new NoteFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      doNote_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(
      // jsLink
      // .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
      // .addQueryData("eventSubmit_doNote_list", "1")
      // .toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "note-detail");
    }
  }

  /**
   * 伝言メモを削除する（複数）． <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_multi_delete(RunData rundata, Context context)
      throws Exception {
    NoteMultiDelete delete = new NoteMultiDelete();
    if (delete.doMultiAction(this, rundata, context)) {
      doNote_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doNote_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "note-list");
    }
  }

  /**
   * ノーマル画面の伝言メモを既読にする（複数）． <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_multi_read(RunData rundata, Context context)
      throws Exception {
    NoteMultiStateUpdate data = new NoteMultiStateUpdate();
    data.doMultiAction(this, rundata, context);
    doNote_normal(rundata, context);
  }

  /**
   * 最大化画面の伝言メモを既読にする（複数）． <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doNote_multi_read_max(RunData rundata, Context context)
      throws Exception {
    NoteMultiStateUpdate data = new NoteMultiStateUpdate();
    data.doMultiAction(this, rundata, context);
    doNote_list(rundata, context);
  }

  private void clearNoteSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("target_user_id");
    list.add("target_group_name");

    list.add("com.aimluck.eip.note.NoteSelectDatasort");
    list.add("com.aimluck.eip.note.NoteSelectDatasorttype");
    list.add("com.aimluck.eip.note.NoteClientSelectDatasort");

    ALEipUtils.removeTemp(rundata, context, list);
  }

}
