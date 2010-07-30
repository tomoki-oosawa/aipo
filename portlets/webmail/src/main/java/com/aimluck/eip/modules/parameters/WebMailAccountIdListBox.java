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
package com.aimluck.eip.modules.parameters;

import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.modules.parameters.ListBox;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webメールの設定値を処理するクラスです。 <br />
 */
public class WebMailAccountIdListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  private String DEF_INITIAL_VALUE = "（メールアカウントの選択）";

  /**
   * Initialize options
   * 
   * @param data
   */
  protected void init(RunData data) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipMMailAccount.class);
      query.addCustomDbAttribute(EipMMailAccount.ACCOUNT_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipMMailAccount.ACCOUNT_NAME_COLUMN);
      Expression exp = ExpressionFactory.matchExp(
          EipMMailAccount.USER_ID_PROPERTY, Integer.valueOf(ALEipUtils
              .getUserId(data)));
      query.setQualifier(exp);
      List accounts = dataContext.performQuery(query);

      int length = 1;
      if (accounts != null && accounts.size() > 0) {
        length = accounts.size() + 1;
      }

      String[] keys = new String[length];
      String[] values = new String[length];

      keys[0] = "";
      values[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
      int count = 1;

      DataRow dataRow = null;
      Iterator iter = accounts.iterator();
      while (iter.hasNext()) {
        dataRow = (DataRow) iter.next();

        keys[count] = ((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMMailAccount.ACCOUNT_ID_PK_COLUMN)).toString();
        values[count] = (String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMMailAccount.ACCOUNT_NAME_COLUMN);
        count++;
      }

      this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
      this.items = keys;
      this.values = values;
      this.size = Integer.toString(length);
      this.multiple = Boolean.valueOf(
          (String) this.getParm(MULTIPLE_CHOICE, "false")).booleanValue();
    } catch (Exception e) {
      ALEipUtils.redirectPageNotFound(data);
    }

  }
}
