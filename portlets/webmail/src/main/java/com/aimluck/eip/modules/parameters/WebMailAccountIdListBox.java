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
package com.aimluck.eip.modules.parameters;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webメールの設定値を処理するクラスです。 <br />
 */
public class WebMailAccountIdListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  private static final String DEF_INITIAL_VALUE = "（メールアカウントの選択）";

  /**
   * Initialize options
   * 
   * @param data
   */
  @Override
  protected void init(RunData data) {
    try {
      SelectQuery<EipMMailAccount> query =
        Database.query(EipMMailAccount.class);

      Expression exp =
        ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(data)));
      List<EipMMailAccount> accounts = query.setQualifier(exp).fetchList();
      if (accounts != null) {
        int length = 1;
        if (accounts.size() > 0) {
          length = accounts.size() + 1;
        }

        String[] keys = new String[length];
        String[] values = new String[length];

        keys[0] = "";
        values[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
        int count = 1;

        for (EipMMailAccount account : accounts) {
          keys[count] = account.getAccountId().toString();
          values[count] = account.getAccountName();
          count++;
        }

        this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
        this.items = keys;
        this.values = values;
        this.size = Integer.toString(length);
        this.multiple =
          Boolean
            .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
            .booleanValue();
      }
    } catch (Exception e) {
      ALEipUtils.redirectPageNotFound(data);
    }

  }
}
