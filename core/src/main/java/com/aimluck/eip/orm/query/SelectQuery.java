package com.aimluck.eip.orm.query;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;

import com.aimluck.eip.orm.DatabaseOrmService;

public class SelectQuery<M> extends org.apache.cayenne.query.SelectQuery {

  private static final long serialVersionUID = 5404111688862773398L;

  private DataContext dataContext = DatabaseOrmService.getInstance()
      .getDataContext();

  public SelectQuery(Class<M> rootClass) {
    super(rootClass, null);
  }

  public SelectQuery(Class<M> rootClass, Expression qualifier) {
    super(rootClass, qualifier);
  }

  @SuppressWarnings("unchecked")
  public List<M> perform() {
    return (List<M>) dataContext.performQuery(this);

  }
}
