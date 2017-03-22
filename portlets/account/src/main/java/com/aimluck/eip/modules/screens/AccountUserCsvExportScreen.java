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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.account.AccountResultData;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class AccountUserCsvExportScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserCsvExportScreen.class.getName());

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
      Integer id = Integer.valueOf(record.getUserId());
      AccountResultData rd = new AccountResultData();
      rd.initField();
      rd.setUserId(Integer.valueOf(record.getUserId()).intValue());
      rd.setUserName(record.getLoginName());
      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString());
      rd.setFirstName(record.getFirstName());
      rd.setLastName(record.getLastName());
      rd.setNameKana(new StringBuffer()
        .append(record.getLastNameKana())
        .append(" ")
        .append(record.getFirstNameKana())
        .toString());
      rd.setFirstNameKana(record.getFirstNameKana());
      rd.setLastNameKana(record.getLastNameKana());
      rd.setEmail(record.getEmail());
      rd.setOutTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPostNameList(ALEipUtils.getPostNameList(id.intValue()));
      rd.setPositionName(ALEipUtils.getPositionName(record.getPositionId()));
      rd.setDisabled(record.getDisabled());
      rd.setIsAdmin(ALEipUtils.isAdmin(Integer.valueOf(record.getUserId())));
      rd.setPhotoModified(record.getPhotoModified().getTime());
      rd.setCode(record.getCode());
      return rd;
    } catch (Exception ex) {
      logger.error("AccountUserCsvExportScreen.getResultData", ex);
      return null;
    }
  }

  /**
  *
  */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    if (ALEipUtils.isAdmin(rundata)) {
      SelectQuery<TurbineUser> query = getSelectQuery(rundata);

      ResultList<TurbineUser> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        AccountResultData data;
        StringBuffer sb =
          new StringBuffer("\"ユーザー名\",\"パスワード\",\"名前（姓）\",\"名前（名）\","
            + "\"名前（姓・フリガナ）\",\"名前（名・フリガナ）\",\"メールアドレス\","
            + "\"電話番号（外線）\",\"電話番号（内線）\",\"電話番号（携帯）\","
            + "\"携帯メールアドレス\",\"部署名\",\"役職\",\"社員コード\"");
        for (ListIterator<TurbineUser> iterator =
          list.listIterator(list.size()); iterator.hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getResultData(iterator.previous());
          sb.append("\"");
          sb.append(data.getUserName());
          sb.append("\",\"");
          sb.append("*");// Password
          sb.append("\",\"");
          sb.append(data.getLastName());
          sb.append("\",\"");
          sb.append(data.getFirstName());
          sb.append("\",\"");
          sb.append(data.getLastNameKana());
          sb.append("\",\"");
          sb.append(data.getFirstNameKana());
          sb.append("\",\"");
          sb.append(data.getEmail());
          sb.append("\",\"");
          sb.append(data.getOutTelephone());
          sb.append("\",\"");
          sb.append(data.getInTelephone());
          sb.append("\",\"");
          sb.append(data.getCellularPhone());
          sb.append("\",\"");
          sb.append(data.getCellularMail());
          sb.append("\",\"");
          int i = 0, size = data.getPostNameList().size();
          while (i < size - 1) {
            sb.append(data.getPostNameList().get(i));
            sb.append("/");
            ++i;
          }
          if (i == size - 1) {
            sb.append(data.getPostNameList().get(i));
          }
          sb.append("\",\"");
          sb.append(data.getPositionName());
          sb.append("\",\"");
          sb.append(data.getCode());
          sb.append("\"");
        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("AccountUserCsvExportScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  /**
   * @param rundata
   * @return
   */
  private SelectQuery<TurbineUser> getSelectQuery(RunData rundata) {

    ObjectId oid =
      new ObjectId("TurbineUser", TurbineUser.USER_ID_PK_COLUMN, 3);

    Expression exp_base =
      ExpressionFactory.matchAllDbExp(
        oid.getIdSnapshot(),
        Expression.GREATER_THAN);

    SelectQuery<TurbineUser> query =
      Database.query(TurbineUser.class, exp_base).where(
        Operations.eq(TurbineUser.COMPANY_ID_PROPERTY, Integer.valueOf(1)),
        Operations.ne(TurbineUser.DISABLED_PROPERTY, "T"));

    ALStringField target_keyword = new ALStringField();
    target_keyword.setValue(rundata.getParameters().get("target_keyword"));
    String filter = rundata.getParameters().get("current_post");
    String filter_role = rundata.getParameters().get("current_role");

    if (target_keyword.getValue() != null
      && !target_keyword.getValue().equals("")) {
      String transWord =
        ALStringUtil.convertHiragana2Katakana(ALStringUtil
          .convertH2ZKana(target_keyword.getValue()));
      transWord = transWord.replace("　", "").replace(" ", ""); // 全角/半角スペースを削除
      String[] transWords = transWord.split(""); // 1文字ずつに分解

      for (int i = 0; i < transWords.length; i++) {
        Expression exp11 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp12 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp13 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_KANA_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp14 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_KANA_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp15 =
          ExpressionFactory.likeExp(TurbineUser.EMAIL_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp16 =
          ExpressionFactory.likeExp(
            TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
              + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
              + "."
              + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            "%" + target_keyword.getValue() + "%");
        Expression exp17 =
          ExpressionFactory.likeExp(TurbineUser.LOGIN_NAME_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp21 =
          ExpressionFactory.likeExp(TurbineUser.OUT_TELEPHONE_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp22 =
          ExpressionFactory.likeExp(TurbineUser.IN_TELEPHONE_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp23 =
          ExpressionFactory.likeExp(TurbineUser.CELLULAR_PHONE_PROPERTY, "%"
            + target_keyword.getValue()
            + "%");
        Expression exp31 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp32 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp33 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_KANA_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp34 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_KANA_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp35 =
          ExpressionFactory.likeExp(
            TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
              + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
              + "."
              + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            "%" + transWords[i] + "%");
        Expression exp36 =
          ExpressionFactory.likeExp(TurbineUser.CODE_PROPERTY, "%"
            + transWords[i]
            + "%");

        query.andQualifier(exp11.orExp(exp12).orExp(exp13).orExp(exp14).orExp(
          exp15).orExp(exp16).orExp(exp17).orExp(exp21).orExp(exp22).orExp(
          exp23).orExp(exp31).orExp(exp32).orExp(exp33).orExp(exp34).orExp(
          exp35).orExp(exp36));
      }
    }

    // ユーザーの状態によるフィルターが指定されている場合。
    if (filter_role != null && !filter_role.equals("")) {

      // 管理者かどうか
      if (filter_role.equals(AccountUtils.ROLE_ADMIN.toString())) {
        try {
          Group group = JetspeedSecurity.getGroup("LoginUser");
          Role adminrole = JetspeedSecurity.getRole("admin");
          List<TurbineUserGroupRole> admins =
            Database
              .query(TurbineUserGroupRole.class)
              .where(
                Operations.eq(
                  TurbineUserGroupRole.TURBINE_ROLE_PROPERTY,
                  adminrole.getId()),
                Operations.eq(
                  TurbineUserGroupRole.TURBINE_GROUP_PROPERTY,
                  group.getId()),
                Operations.ne(TurbineUserGroupRole.TURBINE_USER_PROPERTY, 1))
              .distinct(true)
              .fetchList();
          List<Integer> admin_ids = new ArrayList<Integer>();
          admin_ids.add(Integer.valueOf(1));
          for (TurbineUserGroupRole tugr : admins) {
            admin_ids.add(tugr.getTurbineUser().getUserId());
          }
          query.andQualifier(ExpressionFactory.inDbExp(
            TurbineUser.USER_ID_PK_COLUMN,
            admin_ids));

        } catch (Exception ex) {
          logger.error("AccountUserCsvExportScreen.getSelectQuery", ex);
        }

      }

      // 有効ユーザーかどうか
      if (filter_role.equals(AccountUtils.ROLE_ACTIVE.toString())) {
        query.andQualifier(ExpressionFactory.matchExp(
          TurbineUser.DISABLED_PROPERTY,
          "F"));
      }

      // 有効ユーザーかどうか
      if (filter_role.equals(AccountUtils.ROLE_IN_ACTIVE.toString())) {
        query.andQualifier(ExpressionFactory.matchExp(
          TurbineUser.DISABLED_PROPERTY,
          "N"));
      }
    }

    // 部署によるフィルターが指定されている場合。
    if (filter != null && !filter.equals("")) {

      String groupName =
        (ALEipManager.getInstance().getPostMap().get(Integer.valueOf(filter)))
          .getGroupName()
          .getValue();

      query.where(Operations.eq(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.GROUP_NAME_PROPERTY, groupName));
    }

    query.distinct();

    query.orderDesending(TurbineUser.EIP_MUSER_POSITION_PROPERTY
      + "."
      + EipMUserPosition.POSITION_PROPERTY);

    return query;
  }

  @Override
  protected String getFileName() {
    return "Aipo_users.csv";
  }

}