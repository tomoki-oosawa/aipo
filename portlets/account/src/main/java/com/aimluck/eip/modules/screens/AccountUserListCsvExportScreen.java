package com.aimluck.eip.modules.screens;

import java.util.ListIterator;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.account.AccountResultData;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class AccountUserListCsvExportScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserListCsvExportScreen.class.getName());

  /**
   *
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  protected AccountResultData getResultData(TurbineUser record) {
    try {

      AccountResultData rd = new AccountResultData();
      rd.initField();

      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString());
      rd.setUserName(record.getLoginName());
      rd.setEmail(record.getEmail());
      rd.setOutTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularMail(record.getCellularMail());

      rd.setDisabled(record.getDisabled());
      rd.setPositionName(ALEipUtils.getPositionName(record
        .getPositionId()
        .intValue()));

      return rd;
    } catch (Exception ex) {
      logger.error("AccountUserSelectData.getResultData", ex);
      return null;
    }
  }

  /**
  *
  */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    if (ALEipUtils.isAdmin(rundata)) {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

      ResultList<TurbineUser> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        StringBuffer sb =
          new StringBuffer("\"名前\",\"ユーザ名\",\"メールアドレス\","
            + "\"電話番号(外線)\",\"電話番号(内線)\",\"電話番号(携帯)\",\"メールアドレス(携帯)\","
            + "\"部署\",\"役職\"");
        AccountResultData data;
        for (ListIterator<TurbineUser> iterator =
          list.listIterator(list.size()); iterator.hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getResultData(iterator.previous());
          sb.append("\"");
          sb.append(data.getName());
          sb.append("\",\"");
          sb.append(data.getUserName());
          sb.append("\",\"");
          sb.append(data.getEmail());
          sb.append("\",\"");
          sb.append(data.getUserId());
          sb.append("\",\"");
          sb.append(data.getDisabled());
          sb.append("\",\"");
          sb.append(data.getPositionName());
          sb.append("\",\"");
          sb.append(data.getEmail());
          sb.append("\"");

        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("AccountUserListCsvExportScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  @Override
  protected String getFileName() {
    return "userlist.csv";
  }

}