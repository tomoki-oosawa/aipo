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

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class FileIOAccountCsvFileScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountCsvFileScreen.class.getName());

  public static final String DEFAULT_VIEW_PASSWORD = "*";

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
   *
   */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("ユーザー名,パスワード,名前（姓）,名前（名）");
      sb.append(",名前（姓・フリガナ）,名前（名・フリガナ）,メールアドレス");
      sb.append(",電話番号（外線）,電話番号（内線）,電話番号（携帯）,携帯メールアドレス,部署名,役職,社員コード").append(
        LINE_SEPARATOR);

      sb.append("yamada,a,山田,太郎");
      sb.append(",ヤマダ,タロウ,a@a.com");
      sb.append(",99-99-91,1001,111-1111-1111,a@a.ne.jp,営業部/人事部,,").append(
        LINE_SEPARATOR);

      sb.append("suzuki0,b,鈴木,花子");
      sb.append(",スズキ,ハナコ,b@b.com");
      sb.append(",99-99-92,2002,222-2222-2222,b@b.ne.jp,業務部,業務部長,").append(
        LINE_SEPARATOR);

      sb.append("suzuki1,c,鈴木,太郎");
      sb.append(",スズキ,タロウ,c@c.com");
      sb.append(",99-99-93,,,c@c.ne.jp,,,").append(LINE_SEPARATOR);

      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAccountCsvFileScreen.getCSVString", e);
      return null;
    }
  }

  /**
   *
   * @param rundata
   * @return
   * @throws Exception
   */
  protected String getCSVStringDB(RunData rundata) throws Exception {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("ユーザー名,パスワード,名前（姓）,名前（名）");
      sb.append(",名前（姓・フリガナ）,名前（名・フリガナ）,メールアドレス");
      sb.append(",電話番号（外線）,電話番号（内線）,電話番号（携帯）,携帯メールアドレス,部署名,役職,社員コード").append(
        LINE_SEPARATOR);
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp1 =
        ExpressionFactory.matchExp(TurbineUser.COMPANY_ID_PROPERTY, Integer
          .valueOf(1));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
      query.andQualifier(exp2);
      query.orderAscending(TurbineUser.EIP_MUSER_POSITION_PROPERTY
        + "."
        + EipMUserPosition.POSITION_PROPERTY);

      List<TurbineUser> list = query.fetchList();

      String position = "";
      TurbineUser record = null;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        record = list.get(i);
        List<ALStringField> postNames = new ArrayList<ALStringField>();
        try {
          postNames = ALEipUtils.getPostNameList(record.getUserId());
          EipMPosition position_data =
            getEipMPosition(record.getPositionId().intValue());
          if (position_data != null) {
            position = position_data.getPositionName();
          } else {
            position = "";
          }
        } catch (Exception e) {

          position = "";
          logger.error("[FileIOAccountCsvFileScreen]", e);
        }
        sb
          .append(record.getLoginName())
          .append(",")
          .append(DEFAULT_VIEW_PASSWORD)
          .append(",")
          .append(record.getLastName())
          .append(",")
          .append(record.getFirstName())
          .append(",")
          .append(record.getLastNameKana())
          .append(",")
          .append(record.getFirstNameKana())
          .append(",")
          .append(record.getEmail())
          .append(",")
          .append(record.getOutTelephone())
          .append(",")
          .append(record.getInTelephone())
          .append(",")
          .append(record.getCellularPhone())
          .append(",")
          .append(record.getCellularMail())
          .append(",")
          .append(record.getCode())
          .append(",");

        for (int j = 0; j < postNames.size(); j++) {
          if (j != 0) {
            sb.append("/");
          }
          sb.append(postNames.get(j));
        }
        sb.append(",").append(position).append(LINE_SEPARATOR);
      }
      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAccountCsvFileScreen.getCSVStringDB", e);
      return null;
    }
  }

  @Override
  protected String getFileName() {
    return ALOrgUtilsService.getAlias() + "_users.csv";
  }

  /**
   * データベースから指定された番号のオブジェクトモデルを取得 <BR>
   *
   * @param i
   * @return
   */
  @SuppressWarnings("unused")
  private EipMPost getEipMPost(int i) {
    SelectQuery<EipMPost> query = Database.query(EipMPost.class);
    Expression exp =
      ExpressionFactory.matchDbExp(EipMPost.POST_ID_PK_COLUMN, i);
    query.setQualifier(exp);
    List<EipMPost> list = query.fetchList();
    if (list == null || list.size() == 0) {
      return null;
    }
    EipMPost post = list.get(0);
    return post;
  }

  /**
   * データベースから指定された番号のオブジェクトモデルを取得 <BR>
   *
   * @param i
   * @return
   */
  private EipMPosition getEipMPosition(int i) {
    SelectQuery<EipMPosition> query = Database.query(EipMPosition.class);
    Expression exp =
      ExpressionFactory.matchDbExp(EipMPosition.POSITION_ID_PK_COLUMN, i);
    query.setQualifier(exp);
    List<EipMPosition> list = query.fetchList();
    if (list == null || list.size() == 0) {
      return null;
    }
    EipMPosition position = list.get(0);
    return position;
  }
}
