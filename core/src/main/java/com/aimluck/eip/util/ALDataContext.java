package com.aimluck.eip.util;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.Query;

import com.aimluck.eip.orm.DatabaseOrmService;

public class ALDataContext {

  @SuppressWarnings("unchecked")
  public static <M> List<M> performQuery(Class<M> clazz, Query query) {
    DataContext dataContext = DatabaseOrmService.getInstance()
    .getDataContext();
    return (List<M>) dataContext.performQuery(query);

  }

}
