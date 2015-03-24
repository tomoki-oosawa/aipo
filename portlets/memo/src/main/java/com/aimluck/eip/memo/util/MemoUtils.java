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
package com.aimluck.eip.memo.util;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Memoのユーティリティクラスです。 <BR>
 * 
 */
public class MemoUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MemoUtils.class.getName());

  public static final String MEMO_PORTLET_NAME = "Memo";

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

  /** セッション内の検索キーワード名 */
  public static final String SESSION_KEYWORD = "memo_keyword";

  /** セッション内のメモの新規(追加,更新)キーワード名 */
  public static final String NEW_FLAG = "new_flag";

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  private static boolean isMemoIDEmpty(String memoid) {
    return memoid == null
      || "".equals(memoid)
      || Integer.valueOf(memoid) == null;
  }

  /**
   * Memo オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMemo getEipTMemo(RunData rundata, Context context)
      throws ALPageNotFoundException {
    try {
      String memoid = ALEipUtils.getTemp(rundata, context, NEW_FLAG);
      if (isMemoIDEmpty(memoid)
        || !"MemoScreen".equals(rundata.getScreenTemplate())) {
        memoid = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
        if (isMemoIDEmpty(memoid)) {
          memoid = rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
          if (isMemoIDEmpty(memoid)) {
            // MemoIDが空の場合、更新日が最新のメモを返す
            return getLatestEipTMemo(rundata, context);
          }

        }
      } else {
        // 新規作成されたメモがある場合、新規作成フラッグを解除
        ALEipUtils.setTemp(rundata, context, NEW_FLAG, null);
      }
      EipTMemo memo = getEipTMemo(rundata, context, Integer.valueOf(memoid));
      if (memo == null) {
        // MemoIDで指定されたMemoが見つからない場合、更新日が最新のメモを返す
        return getLatestEipTMemo(rundata, context);
      }
      return memo;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    } catch (Exception ex) {
      logger.error("memo", ex);
      return null;
    }
  }

  /**
   * 初期セレクトメモを設定します
   * 
   * @param rundata
   * @param index
   * @return
   */
  public static boolean saveMemoSelection(RunData rundata, String entityid) {
    String portletEntryId = rundata.getParameters().getString("js_peid", null);
    if (portletEntryId == null || "".equals(portletEntryId)) {
      return false;
    }

    String MEMO_IDX = "p1a-memos";

    try {
      Profile profile = ((JetspeedRunData) rundata).getProfile();
      Portlets portlets = profile.getDocument().getPortlets();
      if (portlets == null) {
        return false;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return false;
      }

      PsmlParameter param = null;
      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            boolean hasParam = false;
            Parameter params[] = entries[j].getParameter();
            int param_len = params.length;
            for (int k = 0; k < param_len; k++) {
              if (params[k].getName().equals(MEMO_IDX)) {
                params[k].setValue(entityid);
                entries[j].setParameter(k, params[k]);
                hasParam = true;
              }
            }

            if (!hasParam) {
              param = new PsmlParameter();
              param.setName(MEMO_IDX);
              param.setValue(entityid);
              entries[j].addParameter(param);
            }

            break;
          }
        }

      }

      profile.store();
      return true;
    } catch (IndexOutOfBoundsException e) {
      logger.error("[MemoUtils]", e);
    } catch (ProfileException e) {
      logger.error("[MemoUtils]", e);
    }
    return false;
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
   * 表示切り替えで指定した検索キーワードを取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetKeyword(RunData rundata, Context context) {
    String target_keyword = null;
    String keywordParam = rundata.getParameters().getString(TARGET_KEYWORD);
    target_keyword = ALEipUtils.getTemp(rundata, context, SESSION_KEYWORD);

    if (keywordParam == null && (target_keyword == null)) {
      ALEipUtils.setTemp(rundata, context, SESSION_KEYWORD, "");
      target_keyword = "";
    } else if (keywordParam != null) {
      ALEipUtils
        .setTemp(rundata, context, SESSION_KEYWORD, keywordParam.trim());
      target_keyword = keywordParam;
    }
    return target_keyword;
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
    ALEipUtils.setTemp(rundata, context, SESSION_KEYWORD, "");
  }

  /**
   * Memo オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMemo getEipTMemo(RunData rundata, Context context,
      Integer memoid) throws ALPageNotFoundException {
    int uid = ALEipUtils.getUserId(rundata);
    try {
      if (memoid == null) {
        // Memo IDが空の場合
        logger.debug("[MemoUtils] Empty ID...");
        return null;
      }

      SelectQuery<EipTMemo> query = Database.query(EipTMemo.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTMemo.MEMO_ID_PK_COLUMN, memoid);
      exp.andExp(ExpressionFactory.matchExp(EipTMemo.OWNER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata))));
      query.setQualifier(exp);
      List<EipTMemo> memos = query.fetchList();
      if (memos == null || memos.size() == 0) {
        // 指定したMemo IDのレコードが見つからない場合
        logger.debug("[MemoUtils] Not found ID...");
        return null;
      }

      // アクセス権の判定
      EipTMemo memo = memos.get(0);
      if (uid != memo.getOwnerId().intValue()) {
        logger.debug("[MemoUtils] Invalid user access...");
        throw new ALPageNotFoundException();
      }

      return memos.get(0);
    } catch (ALPageNotFoundException pageNotFound) {
      // logger.error(pageNotFound);
      throw pageNotFound;
    } catch (Exception ex) {
      logger.error("memo", ex);
      return null;
    }
  }

  /**
   * 更新日が最新の Memo オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMemo getLatestEipTMemo(RunData rundata, Context context)
      throws ALPageNotFoundException {
    int uid = ALEipUtils.getUserId(rundata);
    try {

      SelectQuery<EipTMemo> query = Database.query(EipTMemo.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTMemo.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp);
      List<EipTMemo> memos = query.fetchList();
      if (memos == null || memos.size() == 0) {
        // 指定したUser IDのレコードが見つからない場合
        logger.debug("[MemoUtils] Not found records...");
        return null;
      }
      EipTMemo latest_memo = memos.get(0);
      // 最新のアップデートをもつmemoを選別
      for (EipTMemo memo : memos) {
        if (memo.getUpdateDate().after(latest_memo.getUpdateDate())) {
          // アクセス権の判定
          if (uid != memo.getOwnerId().intValue()) {
            logger.debug("[MemoUtils] Invalid user access...");
            throw new ALPageNotFoundException();
          }
          latest_memo = memo;
        }
      }
      return latest_memo;
    } catch (ALPageNotFoundException pageNotFound) {
      // logger.error(pageNotFound);
      throw pageNotFound;
    } catch (Exception ex) {
      logger.error("memo", ex);
      return null;
    }
  }
}
