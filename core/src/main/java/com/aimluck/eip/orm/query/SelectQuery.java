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
import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.Ordering;

import com.aimluck.eip.orm.DatabaseOrmService;

public class SelectQuery<M> extends AbstractQuery<M> {

  private static final long serialVersionUID = 5404111688862773398L;

  protected org.apache.cayenne.query.SelectQuery delegate;

  protected int page = 1;

  protected int totalCount = 0;

  public SelectQuery(Class<M> rootClass) {
    super(rootClass);
    delegate = new org.apache.cayenne.query.SelectQuery(rootClass);
  }

  public SelectQuery(DataContext dataContext, Class<M> rootClass) {
    super(dataContext, rootClass);
    this.rootClass = rootClass;
    delegate = new org.apache.cayenne.query.SelectQuery(rootClass);
    this.dataContext = dataContext;
  }

  public SelectQuery(Class<M> rootClass, Expression qualifier) {
    super(rootClass);
    this.rootClass = rootClass;
    delegate = new org.apache.cayenne.query.SelectQuery(rootClass, qualifier);
    dataContext = DatabaseOrmService.getInstance().getDataContext();
  }

  public SelectQuery(DataContext dataContext, Class<M> rootClass,
      Expression qualifier) {
    super(dataContext, rootClass);
    this.rootClass = rootClass;
    delegate = new org.apache.cayenne.query.SelectQuery(rootClass, qualifier);
    this.dataContext = dataContext;
  }

  @SuppressWarnings("unchecked")
  public List<M> fetchList() {
    if (delegate.isFetchingDataRows()) {
      List<DataRow> dataRows = dataContext.performQuery(delegate);
      totalCount = dataRows.size();
      List<M> results = new ArrayList<M>();
      int pageSize = delegate.getPageSize();
      if (pageSize > 0) {
        int num = ((int) (Math.ceil(totalCount / (double) pageSize)));
        if ((num > 0) && (num < page)) {
          page = num;
        }
        int start = pageSize * (page - 1);
        int end =
          (start + pageSize <= totalCount) ? start + pageSize : totalCount;
        for (int i = start; i < end; i++) {
          M model = newInstanceFromRowData(dataRows.get(i), rootClass);
          if (model != null) {
            results.add(model);
          }
        }
      } else {
        page = 1;
        for (DataRow dataRow : dataRows) {
          M model = newInstanceFromRowData(dataRow, rootClass);
          if (model != null) {
            results.add(model);
          }
        }
      }

      return results;
    } else {
      List<M> list = dataContext.performQuery(delegate);
      totalCount = list.size();
      List<M> results = new ArrayList<M>();
      int pageSize = delegate.getPageSize();
      if (pageSize > 0) {
        int num = ((int) (Math.ceil(totalCount / (double) pageSize)));
        if ((num > 0) && (num < page)) {
          page = num;
        }
        int start = pageSize * (page - 1);
        int end =
          (start + pageSize <= totalCount) ? start + pageSize : totalCount;
        for (int i = start; i < end; i++) {
          M model = list.get(i);
          if (model != null) {
            results.add(model);
          }
        }
        return results;
      } else {
        page = 1;
        return list;
      }
    }
  }

  public ResultList<M> getResultList() {
    List<M> fetchList = fetchList();
    return new ResultList<M>(
      fetchList,
      page,
      delegate.getPageSize(),
      totalCount);
  }

  public SelectQuery<M> where(Where where) {
    delegate.andQualifier(where.exp);
    return this;
  }

  public SelectQuery<M> where(Where... where) {
    List<Where> list = Arrays.asList(where);
    for (Where w : list) {
      delegate.andQualifier(w.exp);
    }
    return this;
  }

  public SelectQuery<M> setQualifier(Expression qualifier) {
    delegate.setQualifier(qualifier);
    return this;
  }

  public SelectQuery<M> andQualifier(Expression qualifier) {
    delegate.andQualifier(qualifier);
    return this;
  }

  public SelectQuery<M> andQualifier(String qualifier) {
    delegate.andQualifier(Expression.fromString(qualifier));
    return this;
  }

  public SelectQuery<M> orQualifier(Expression qualifier) {
    delegate.orQualifier(qualifier);
    return this;
  }

  public SelectQuery<M> orQualifier(String qualifier) {
    delegate.orQualifier(Expression.fromString(qualifier));
    return this;
  }

  public SelectQuery<M> orderAscending(String ordering) {
    delegate.addOrdering(ordering, Ordering.ASC);
    return this;
  }

  public SelectQuery<M> orderDesending(String ordering) {
    delegate.addOrdering(ordering, Ordering.DESC);
    return this;
  }

  public SelectQuery<M> page(int page) {
    this.page = page;
    return this;
  }

  public SelectQuery<M> pageSize(int pageSize) {
    delegate.setPageSize(pageSize);
    return this;
  }

  public SelectQuery<M> limit(int limit) {
    delegate.setFetchLimit(limit);
    return this;
  }

  public SelectQuery<M> select(String column) {
    delegate.addCustomDbAttribute(column);
    return this;
  }

  public SelectQuery<M> select(String... columns) {
    delegate.addCustomDbAttributes(Arrays.asList(columns));
    return this;
  }

  public SelectQuery<M> distinct() {
    delegate.setDistinct(true);
    return this;
  }

  public SelectQuery<M> distinct(boolean isDistinct) {
    delegate.setDistinct(isDistinct);
    return this;
  }

  public SelectQuery<M> prefetch(String column) {
    delegate.addPrefetch(column);
    return this;
  }

  public org.apache.cayenne.query.SelectQuery getQuery() {
    return delegate;
  }

}
