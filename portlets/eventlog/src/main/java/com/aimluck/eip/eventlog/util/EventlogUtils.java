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
package com.aimluck.eip.eventlog.util;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTEventlog;
import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * イベントログのユーティリティクラスです。 <BR>
 * 
 */
public class EventlogUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(EventlogUtils.class.getName());

  public static final String VIEW_DATE_YEAR = "view_date_year";

  public static final String VIEW_DATE_MONTH = "view_date_month";

  public static final String VIEW_DATE_DAY = "view_date_day";

  public static final String EVENTLOG_PORTLET_NAME = "Eventlog";

  /**
   * @param start_end
   *          true=>start, false =>end
   * @param rundata
   * @param context
   * @return
   */
  public static Calendar getViewCalendar(boolean start_end, RunData rundata,
      Context context) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    if (start_end) {
      cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
    }
    int[] par = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH };
    String[] str = { "_date_year", "_date_month", "_date_day" };
    String head = (start_end) ? "start" : "end";
    for (int i = 0; i < str.length; i++) {
      str[i] = head.concat(str[i]);
      setCalendar(cal, par[i], str[i], rundata, context);
    }
    return cal;
  }

  /**
   * @param cal
   * @param par
   * @param viewdate
   * @param rundata
   * @param context
   */
  private static void setCalendar(Calendar cal, int par, String viewdate,
      RunData rundata, Context context) {
    String temp = ALEipUtils.getTemp(rundata, context, viewdate);
    String idParam = rundata.getParameters().getString(viewdate);
    if (idParam == null && temp == null) {
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, viewdate, idParam);
      int i = Integer.parseInt(idParam);
      if (par == Calendar.MONTH) {
        i--;
      }
      cal.set(par, i);
    } else if (temp != null) {
      ALEipUtils.setTemp(rundata, context, viewdate, temp);
      int i = Integer.parseInt(temp);
      if (par == Calendar.MONTH) {
        i--;
      }
      cal.set(par, i);

    }
  }

  /**
   * イベントログオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTEventlog getEipTEventlog(RunData rundata, Context context) {
    String logid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (logid == null || Integer.valueOf(logid) == null) {
        // eventlog IDが空の場合
        logger.debug("[EventlogUtils] Empty ID...");
        return null;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipTEventlog.EVENTLOG_ID_PK_COLUMN, logid);
      SelectQuery<EipTEventlog> query = Database.query(EipTEventlog.class, exp);
      List<EipTEventlog> logs = query.fetchList();
      if (logs == null || logs.size() == 0) {
        // 指定した Eventlog ID のレコードが見つからない場合
        logger.debug("[EventlogUtils] Not found ID...");
        return null;
      }
      return logs.get(0);
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ポートレットタイプを元に、それぞれのデータ名を取得します。 <BR>
   * 返すデータがない操作では、nullを返します
   * 
   * @param portletType
   * @param entityId
   * @return dataName
   */

  public static String getPortletDataName(int portletType, int entityId) {
    if (portletType == ALEventlogConstants.PORTLET_TYPE_NONE) {
      return null;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_LOGIN) {
      return null;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_LOGOUT) {
      return null;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ACCOUNT) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_AJAXSCHEDULEWEEKLY) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_BLOG_ENTRY) {
      return getBlogEntryName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_BLOG_THEMA) {
      return getBlogEntryThema(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WORKFLOW) {
      return getWorkFlowRequestName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY) {
      return getWorkFlowCategoryName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TODO) {
      return getTodoName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TODO_CATEGORY) {
      return getTodoCategoryName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TIMECARD) {
      return getTimecardName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK) {
      return getAddressBookName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY) {
      return getAddressBookCompanyName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_GROUP) {
      return getAddressBookGroupName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MEMO) {
      return getMemoName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC) {
      return getMsgboardTopicName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MSGBOARD_CATEGORY) {
      return getMsgboardCategoryName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_EXTERNALSEARCH) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MYLINK) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WHATSNEW) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_CABINET_FILE) {
      return getCabinetFileName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_CABINET_FOLDER) {
      return getCabinetFolderName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WEBMAIL) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WEBMAIL_ACCOUNT) {
      return getWebMailAccountName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_SCHEDULE) {
      return getScheduleName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MANHOUR) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ACCOUNTPERSON) {
      return null;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MYGROUP) {
      return getMyGroupName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_PAGE) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_CELLULAR) {
      return "nothing";
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_NOTE) {
      return getNoteName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_COMMON_CATEGORY) {
      return getCommonCategoryName(entityId);
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TIMELINE) {
      return null;
    } else {
      return "nothing";
    }
  }

  /**
   * 社外グループ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getAddressBookGroupName(int entityId) {
    try {
      EipMAddressGroup group =
        Database.get(EipMAddressGroup.class, (long) entityId);
      if (group == null) {
        return null;
      }

      String dataName = group.getGroupName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * 会社名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getAddressBookCompanyName(int entityId) {
    try {
      EipMAddressbookCompany company =
        Database.get(EipMAddressbookCompany.class, (long) entityId);
      if (company == null) {
        return null;
      }

      String dataName = company.getCompanyName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * アドレス帳に登録した名前を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getAddressBookName(int entityId) {
    try {
      EipMAddressbook address =
        Database.get(EipMAddressbook.class, (long) entityId);
      if (address == null) {
        return null;
      }

      String dataName =
        new StringBuffer().append(address.getLastName()).append(" ").append(
          address.getLastName()).toString();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ブログテーマ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getBlogEntryThema(int entityId) {
    try {
      EipTBlogThema thema = Database.get(EipTBlogThema.class, (long) entityId);
      if (thema == null) {
        return null;
      }

      String dataName = thema.getThemaName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ToDoデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getTodoName(int entityId) {
    try {
      EipTTodo todo = Database.get(EipTTodo.class, (long) entityId);
      if (todo == null) {
        return null;
      }

      String dataName = todo.getTodoName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ToDoカテゴリのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getTodoCategoryName(int category_id) {
    try {
      EipTTodoCategory category =
        Database.get(EipTTodoCategory.class, (long) category_id);
      if (category == null) {
        return null;
      }

      String dataName = category.getCategoryName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ブログのエントリ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getBlogEntryName(int entityId) {
    try {
      EipTBlogEntry blog = Database.get(EipTBlogEntry.class, (long) entityId);
      if (blog == null) {
        return null;
      }

      String dataName = blog.getTitle();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ワークフローを取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getWorkFlowRequestName(int entityId) {
    try {
      EipTWorkflowRequest request =
        Database.get(EipTWorkflowRequest.class, (long) entityId);
      if (request == null) {
        return null;
      }

      String dataName =
        request.getEipTWorkflowCategory().getCategoryName()
          + " "
          + request.getRequestName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * ワークフローカテゴリのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getWorkFlowCategoryName(int category_id) {
    try {
      EipTWorkflowCategory category =
        Database.get(EipTWorkflowCategory.class, (long) category_id);
      if (category == null) {
        return null;
      }

      String dataName = category.getCategoryName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * メールアカウントのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getWebMailAccountName(int entityId) {
    try {
      EipMMailAccount account =
        Database.get(EipMMailAccount.class, (long) entityId);
      if (account == null) {
        return null;
      }

      String dataName = account.getAccountName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * タイムカードのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getTimecardName(int entityId) {
    try {
      EipTTimecard timecard = Database.get(EipTTimecard.class, (long) entityId);
      if (timecard == null) {
        return null;
      }

      String dataName = timecard.getReason();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * スケジュールのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getScheduleName(int entityId) {
    try {
      EipTSchedule schedule = Database.get(EipTSchedule.class, (long) entityId);
      if (schedule == null) {
        return null;
      }

      String dataName = schedule.getName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * 伝言メモのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getNoteName(int entityId) {
    try {
      EipTNote note = Database.get(EipTNote.class, (long) entityId);
      if (note == null) {
        return null;
      }

      String subject = "";
      if (note.getSubjectType().equals("0")) {
        subject = note.getCustomSubject();
      } else if (note.getSubjectType().equals("1")) {
        subject = "再度電話します";
      } else if (note.getSubjectType().equals("2")) {
        subject = "電話をしてください";
      } else if (note.getSubjectType().equals("3")) {
        subject = "電話がありました";
      } else if (note.getSubjectType().equals("4")) {
        subject = "伝言があります";
      }
      String dataName = subject + " (" + note.getClientName() + ")";
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * マイグループのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getMyGroupName(int entityId) {
    try {
      TurbineGroup group = Database.get(TurbineGroup.class, (long) entityId);
      if (group == null) {
        return null;
      }

      String dataName = group.getGroupName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * Msgboardデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getMsgboardTopicName(int entityId) {
    try {
      EipTMsgboardTopic topic =
        Database.get(EipTMsgboardTopic.class, (long) entityId);
      if (topic == null) {
        return null;
      }

      String dataName = topic.getTopicName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * Msgboardカテゴリのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getMsgboardCategoryName(int category_id) {
    try {
      EipTMsgboardCategory category =
        Database.get(EipTMsgboardCategory.class, (long) category_id);
      if (category == null) {
        return null;
      }

      String dataName = category.getCategoryName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * 共有フォルダのファイル名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getCabinetFileName(int entityId) {
    try {
      EipTCabinetFile file =
        Database.get(EipTCabinetFile.class, (long) entityId);
      if (file == null) {
        return null;
      }

      String dataName = file.getFileTitle();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * 共有フォルダのフォルダ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getCabinetFolderName(int entityId) {
    try {
      EipTCabinetFolder file =
        Database.get(EipTCabinetFolder.class, (long) entityId);
      if (file == null) {
        return null;
      }

      String dataName = file.getFolderName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * 共有カテゴリのカテゴリ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getCommonCategoryName(int entityId) {
    try {
      EipTCommonCategory category =
        Database.get(EipTCommonCategory.class, (long) entityId);
      if (category == null) {
        return null;
      }

      String dataName = category.getName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * memoのデータ名を取得します。 <BR>
   * 
   * @param entityId
   * @return dataName
   */
  private static String getMemoName(int memo_id) {
    try {
      EipTMemo memo = Database.get(EipTMemo.class, (long) memo_id);
      if (memo == null) {
        return null;
      }

      String dataName = memo.getMemoName();
      return dataName;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

}
