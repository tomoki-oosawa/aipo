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
package com.aimluck.eip.orm.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;

import com.aimluck.eip.orm.DatabaseOrmService;

public class SQLTemplate<M> extends AbstractQuery<M> {

  private static final long serialVersionUID = 5404111688862773398L;

  protected org.apache.cayenne.query.SQLTemplate delegate;

  protected String primaryKey;

  protected List<String> attrNames = new ArrayList<String>();

  protected Map<String, Object> parameters = new HashMap<String, Object>();

  public SQLTemplate(Class<M> rootClass, String sql) {
    super(rootClass);
    delegate = new org.apache.cayenne.query.SQLTemplate(rootClass, sql);
    delegate.setFetchingDataRows(true);
    dataContext = DatabaseOrmService.getInstance().getDataContext();
  }

  public SQLTemplate(DataContext dataContext, Class<M> rootClass, String sql) {
    super(dataContext, rootClass);
    delegate = new org.apache.cayenne.query.SQLTemplate(rootClass, sql);
    delegate.setFetchingDataRows(true);
    this.dataContext = dataContext;
  }

  public List<M> fetchList() {
    delegate.setParameters(parameters);
    @SuppressWarnings("unchecked")
    List<DataRow> dataRows = dataContext.performQuery(delegate);
    List<M> results = new ArrayList<M>(dataRows.size());
    for (DataRow dataRow : dataRows) {
      M model = newInstanceFromRowData(dataRow, rootClass);
      if (model != null) {
        results.add(model);
      }
    }
    return results;
  }

  public SQLTemplate<M> pageSize(int pageSize) {
    delegate.setPageSize(pageSize);
    return this;
  }

  public SQLTemplate<M> limit(int limit) {
    delegate.setFetchLimit(limit);
    return this;
  }

  public SQLTemplate<M> param(String key, Object value) {
    parameters.put(key, value);
    return this;
  }

  public org.apache.cayenne.query.SQLTemplate getSQLTemplate() {
    return delegate;
  }
}
