package com.aimluck.eip.util;

import java.util.List;

import org.apache.cayenne.access.DataContext;

import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;

public class ALDataContext {

  @SuppressWarnings("unchecked")
  public static <M> List<M> performQuery(SelectQuery<M> query) {
    DataContext dataContext = DatabaseOrmService.getInstance()
    .getDataContext();
    return (List<M>) dataContext.performQuery(query);

  }

}
