package com.aimluck.eip.orm.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.Ordering;

import com.aimluck.eip.orm.DatabaseOrmService;

public class SelectQuery<M> {

  private static final long serialVersionUID = 5404111688862773398L;

  protected DataContext dataContext = DatabaseOrmService.getInstance()
    .getDataContext();

  protected Class<M> rootClass;

  protected org.apache.cayenne.query.SelectQuery delegate;

  public SelectQuery(Class<M> rootClass) {
    this.rootClass = rootClass;
    delegate = new org.apache.cayenne.query.SelectQuery(rootClass);
  }

  public SelectQuery(Class<M> rootClass, Expression qualifier) {
    this.rootClass = rootClass;
    delegate = new org.apache.cayenne.query.SelectQuery(rootClass, qualifier);
  }

  @SuppressWarnings("unchecked")
  public List<M> perform() {
    if (delegate.isFetchingDataRows()) {
      List<DataRow> dataRows = (List<DataRow>) dataContext
        .performQuery(delegate);
      List<String> attrNames = delegate.getCustomDbAttributes();
      List<M> results = new ArrayList<M>(dataRows.size());
      for (DataRow dataRow : dataRows) {
        try {
          M model = rootClass.newInstance();
          DataObject obj = (DataObject) model;
          for (String attrName : attrNames) {
            obj.writeProperty(attrName, dataRow.get("attrName"));
          }
          results.add(model);
        } catch (InstantiationException ignore) {
          // ignore
        } catch (IllegalAccessException ignore) {
          // ignore
        }
      }
      return results;
    } else {
      return (List<M>) dataContext.performQuery(delegate);
    }

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
