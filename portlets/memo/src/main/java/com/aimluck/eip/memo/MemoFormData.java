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
package com.aimluck.eip.memo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.memo.util.MemoUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メモ帳のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class MemoFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MemoFormData.class.getName());

  /** Memo名 */
  private ALStringField memo_name;

  /** メモ */
  private ALStringField note;

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
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // Memo名
    memo_name = new ALStringField();
    memo_name.setFieldName("タイトル");
    memo_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("内容");
    note.setTrim(false);

  }

  /**
   * Memoの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // Memo名必須項目
    memo_name.setNotNull(true);
    // Memo名の文字数制限
    memo_name.limitMaxLength(50);
    // メモの文字数制限
    note.limitMaxLength(10000);
  }

  /**
   * Memoのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // Memo名
    memo_name.validate(msgList);
    // メモ
    note.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * Memoをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTMemo memo = MemoUtils.getEipTMemo(rundata, context);
      if (memo == null) {
        return false;
      }

      // Memo名
      memo_name.setValue(memo.getMemoName());
      // メモ
      note.setValue(memo.getNote());
    } catch (Exception ex) {
      logger.error("memo", ex);
      return false;
    }
    return true;
  }

  /**
   * Memoをデータベースから削除します。 <BR>
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
      EipTMemo memo = MemoUtils.getEipTMemo(rundata, context);
      if (memo == null) {
        return false;
      }

      String saved_memoid =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p1a-memos")
          .trim();

      // 固定解除
      if (saved_memoid != null
        && memo.getMemoId().toString().matches(saved_memoid)) {
        MemoUtils.saveMemoSelection(rundata, "");
      }

      // Memoを削除
      Database.delete(memo);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        memo.getMemoId(),
        ALEventlogConstants.PORTLET_TYPE_MEMO,
        memo.getMemoName());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("memo", ex);
      return false;
    }
    return true;
  }

  /**
   * Memoをデータベースに格納します。 <BR>
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
      // 新規オブジェクトモデル
      EipTMemo memo = Database.create(EipTMemo.class);

      Date now = Calendar.getInstance().getTime();

      // Memo名
      memo.setMemoName(memo_name.getValue());
      // 作成者ID
      memo.setOwnerId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // メモ
      memo.setNote(note.getValue());
      // 作成日
      memo.setCreateDate(now);
      // 更新日
      memo.setUpdateDate(now);
      // Memoを登録
      Database.commit();

      // 新規フラグを立てる
      ALEipUtils.setTemp(rundata, context, MemoUtils.NEW_FLAG, memo
        .getMemoId()
        .toString());

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        memo.getMemoId(),
        ALEventlogConstants.PORTLET_TYPE_MEMO,
        memo.getMemoName());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("memo", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているmemoを更新します。 <BR>
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
      EipTMemo memo = MemoUtils.getEipTMemo(rundata, context);
      if (memo == null) {
        return false;
      }

      Date now = Calendar.getInstance().getTime();

      // Memo名
      memo.setMemoName(memo_name.getValue());
      // 作成者ID
      memo.setOwnerId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // メモ
      memo.setNote(note.getValue());
      // 更新日
      memo.setUpdateDate(now);
      // Memoを更新
      Database.commit();

      // 新規フラッグを立てる
      ALEipUtils.setTemp(rundata, context, MemoUtils.NEW_FLAG, memo
        .getMemoId()
        .toString());

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        memo.getMemoId(),
        ALEventlogConstants.PORTLET_TYPE_MEMO,
        memo.getMemoName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("memo", ex);
      return false;
    }
    return true;
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
   * Memo名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getMemoName() {
    return memo_name;
  }

}
