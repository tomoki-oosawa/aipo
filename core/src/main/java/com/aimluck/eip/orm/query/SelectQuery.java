package com.aimluck.eip.orm.query;

import org.apache.cayenne.exp.Expression;

public class SelectQuery<M> extends org.apache.cayenne.query.SelectQuery {

  private static final long serialVersionUID = 5404111688862773398L;

  public SelectQuery(Class<M> rootClass) {
    super(rootClass, null);
  }

  public SelectQuery(Class<M> rootClass, Expression qualifier) {
    super(rootClass, qualifier);
  }

}
