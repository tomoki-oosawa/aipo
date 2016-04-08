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
package com.aimluck.eip.orm;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.CayenneException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.Transaction;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.CustomDBCPDataSourceFactory;
import org.apache.cayenne.conf.DBCPDataSourceFactory;
import org.apache.cayenne.conf.DataSourceFactoryDelegate;
import org.apache.cayenne.dba.AutoAdapter;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.orm.access.CustomTransaction;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * データベース操作ユーティリティ
 *
 */
public class Database {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(Database.class.getName());

  public static final String DEFAULT_ORG = "org001";

  protected static final String SHARED_DOMAIN = "SharedDomain";

  /**
   * 検索用クエリを作成します。
   *
   * @param <M>
   * @param modelClass
   * @return
   */
  public static <M> SelectQuery<M> query(Class<M> modelClass) {
    return new SelectQuery<M>(modelClass);
  }

  /**
   * 検索用クエリを作成します。
   *
   * @param <M>
   * @param dataContext
   * @param modelClass
   * @return
   */
  public static <M> SelectQuery<M> query(DataContext dataContext,
      Class<M> modelClass) {
    return new SelectQuery<M>(dataContext, modelClass);
  }

  /**
   * 検索用クエリを作成します。
   *
   * @param <M>
   * @param modelClass
   * @param exp
   * @return
   */
  public static <M> SelectQuery<M> query(Class<M> modelClass, Expression exp) {
    return new SelectQuery<M>(modelClass, exp);
  }

  /**
   * 検索用クエリを作成します。
   *
   * @param <M>
   * @param dataContext
   * @param modelClass
   * @param exp
   * @return
   */
  public static <M> SelectQuery<M> query(DataContext dataContext,
      Class<M> modelClass, Expression exp) {
    return new SelectQuery<M>(dataContext, modelClass, exp);
  }

  /**
   * SQL検索クエリを作成します。
   *
   * @param <M>
   * @param modelClass
   * @param sql
   * @return
   */
  public static <M> SQLTemplate<M> sql(Class<M> modelClass, String sql) {
    return new SQLTemplate<M>(modelClass, sql);
  }

  /**
   * SQL検索クエリを作成します。
   *
   * @param <M>
   * @param dataContext
   * @param modelClass
   * @param sql
   * @return
   */
  public static <M> SQLTemplate<M> sql(DataContext dataContext,
      Class<M> modelClass, String sql) {
    return new SQLTemplate<M>(dataContext, modelClass, sql);
  }

  /**
   * プライマリキーで指定されたオブジェクトモデルを取得します。
   *
   * @param <M>
   * @param modelClass
   * @param primaryKey
   * @return
   */
  public static <M> M get(Class<M> modelClass, Object primaryKey) {
    return get(DataContext.getThreadDataContext(), modelClass, primaryKey);
  }

  /**
   * 指定されたオブジェクトモデルを取得します。
   *
   * @param <M>
   * @param dataContext
   * @param modelClass
   * @param primaryKey
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M> M get(DataContext dataContext, Class<M> modelClass,
      Object primaryKey) {
    beginTransaction(dataContext);
    return (M) DataObjectUtils.objectForPK(dataContext, modelClass, primaryKey);
  }

  /**
   *
   * @param <M>
   * @param modelClass
   * @param key
   * @param value
   * @return
   */
  public static <M> M get(Class<M> modelClass, String key, Object value) {
    return get(DataContext.getThreadDataContext(), modelClass, key, value);
  }

  /**
   *
   * @param <M>
   * @param dataContext
   * @param modelClass
   * @param key
   * @param value
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M> M get(DataContext dataContext, Class<M> modelClass,
      String key, Object value) {
    beginTransaction(dataContext);
    return (M) dataContext.refetchObject(new ObjectId(modelClass
      .getSimpleName(), key, value));
  }

  /**
   * オブジェクトモデルを新規作成します。
   *
   * @param <M>
   * @param modelClass
   * @return
   */
  public static <M> M create(Class<M> modelClass) {
    return create(DataContext.getThreadDataContext(), modelClass);
  }

  /**
   * オブジェクトモデルを新規作成します。
   *
   * @param <M>
   * @param dataContext
   * @param modelClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M> M create(DataContext dataContext, Class<M> modelClass) {
    return (M) dataContext.createAndRegisterNewObject(modelClass);

  }

  /**
   * オブジェクトモデルを削除します。
   *
   * @param target
   */
  public static void delete(Persistent target) {
    delete(DataContext.getThreadDataContext(), target);
  }

  /**
   * オブジェクトモデルを削除します。
   *
   * @param dataContext
   * @param target
   */
  public static void delete(DataContext dataContext, Persistent target) {
    dataContext.deleteObject(target);
  }

  /**
   * オブジェクトモデルをすべて削除します。
   *
   * @param target
   */
  public static void deleteAll(List<?> target) {
    deleteAll(DataContext.getThreadDataContext(), target);
  }

  /**
   * オブジェクトモデルをすべて削除します。
   *
   * @param dataContext
   * @param target
   */
  public static void deleteAll(DataContext dataContext, List<?> target) {
    dataContext.deleteObjects(target);

  }

  /**
   * オブジェクトモデルをすべて削除します。
   *
   * @param target
   */
  public static void deleteAll(DataObject... target) {
    deleteAll(DataContext.getThreadDataContext(), target);
  }

  /**
   * オブジェクトモデルをすべて削除します。
   *
   * @param dataContext
   * @param target
   */
  public static void deleteAll(DataContext dataContext, DataObject... target) {
    dataContext.deleteObjects(Arrays.asList(target));
  }

  /**
   * 現在までの更新をコミットします。
   *
   */
  public static void commit() {
    commit(DataContext.getThreadDataContext());
  }

  /**
   * 現在までの更新をコミットします。
   *
   * @param dataContext
   */
  public static void commit(DataContext dataContext) {
    beginTransaction(dataContext);
    dataContext.commitChanges();
    Transaction threadTransaction = Transaction.getThreadTransaction();
    if (threadTransaction != null) {
      try {
        threadTransaction.commit();
      } catch (IllegalStateException e) {
        logger.error(e.getMessage(), e);
      } catch (SQLException e) {
        logger.error(e.getMessage(), e);
      } catch (CayenneException e) {
        logger.error(e.getMessage(), e);
      } finally {
        Transaction.bindThreadTransaction(null);
      }
    }
  }

  /**
   * 現在までの更新をロールバックします。
   *
   */
  public static void rollback() {
    rollback(DataContext.getThreadDataContext());
  }

  /**
   * 現在までの更新をロールバックします。
   *
   * @param dataContext
   */
  public static void rollback(DataContext dataContext) {
    try {
      beginTransaction(dataContext);
      dataContext.rollbackChanges();
    } catch (Throwable t) {
      logger.warn("[Database] rollback", t);
    }
  }

  /**
   * DataRow から指定したキーの値を取得します。
   *
   * @param dataRow
   * @param key
   * @return
   */
  public static Object getFromDataRow(DataRow dataRow, Object key) {
    String lowerKey = ((String) key).toLowerCase(Locale.ENGLISH);
    if (dataRow.containsKey(lowerKey)) {
      return dataRow.get(lowerKey);
    } else {
      return dataRow.get(((String) key).toUpperCase(Locale.ENGLISH));
    }
  }

  public static <M> M objectFromRowData(DataRow dataRow, Class<M> rootClass) {
    try {
      M model = rootClass.newInstance();
      CayenneDataObject obj = (CayenneDataObject) model;

      ObjEntity objEntity =
        DataContext.getThreadDataContext().getEntityResolver().lookupObjEntity(
          obj);

      ObjectId objId =
        createObjectId(objEntity.getName(), dataRow, objEntity.getDbEntity());
      if (objId != null) {
        obj.setObjectId(objId);
      }

      @SuppressWarnings("unchecked")
      Iterator<ObjAttribute> iterator = objEntity.getAttributes().iterator();
      while (iterator.hasNext()) {
        ObjAttribute objAttribute = iterator.next();
        DbAttribute dbAttribute = objAttribute.getDbAttribute();
        Object value = getFromDataRow(dataRow, dbAttribute.getName());
        if (value != null) {
          obj.writeProperty(objAttribute.getName(), value);
        }
      }
      return model;
    } catch (InstantiationException ignore) {
      // ignore
    } catch (IllegalAccessException ignore) {
      // ignore
    }
    return null;
  }

  public static ObjectId createObjectId(String entityName, DataRow dataRow,
      DbEntity entity) {

    @SuppressWarnings("unchecked")
    List<DbAttribute> pk = entity.getPrimaryKey();
    if (pk.size() == 1) {
      DbAttribute attribute = pk.get(0);

      String key = attribute.getName();

      Object val = getFromDataRow(dataRow, key);
      if (val == null) {
        return null;
      } else {
        return new ObjectId(entityName, attribute.getName(), val);
      }
    }

    Map<String, Object> idMap = new HashMap<String, Object>(pk.size() * 2);
    DbAttribute attribute;
    Object val;
    for (Iterator<DbAttribute> it = pk.iterator(); it.hasNext(); idMap.put(
      attribute.getName(),
      val)) {
      attribute = it.next();
      String key = attribute.getName();

      val = getFromDataRow(dataRow, key);
      if (val == null) {
        return null;
      }
    }

    return new ObjectId(entityName, idMap);
  }

  public static String getDomainName() {
    try {
      return DataContext.getThreadDataContext().getParentDataDomain().getName();
    } catch (Throwable ignore) {
      return null;
    }
  }

  public static DataContext createDataContext(String orgId) throws Exception {

    DataDomain dataDomain =
      Configuration.getSharedConfiguration().getDomain(SHARED_DOMAIN);

    DataDomain destDataDomain =
      new DataDomain(orgId, dataDomain.getProperties());
    destDataDomain.setEntityResolver(dataDomain.getEntityResolver());
    destDataDomain.setEventManager(dataDomain.getEventManager());
    destDataDomain.setTransactionDelegate(dataDomain.getTransactionDelegate());
    DataNode dataNode = new DataNode(orgId + "domainNode");
    dataNode.setDataMaps(dataDomain.getDataMaps());
    dataSourceFactory.initializeWithParentConfiguration(Configuration
      .getSharedConfiguration());
    DataSource dataSource =
      dataSourceFactory.getDataSource("datasource/dbcp-"
        + orgId
        + ".properties");

    dataNode.setDataSource(dataSource);
    dataNode.setAdapter(new AutoAdapter(dataSource));
    destDataDomain.addNode(dataNode);

    return destDataDomain.createDataContext();
  }

  public static void tearDown() {
    Transaction threadTransaction = Transaction.getThreadTransaction();
    if (threadTransaction != null) {
      try {
        threadTransaction.rollback();
      } catch (IllegalStateException e) {
        logger.error(e.getMessage(), e);
      } catch (SQLException e) {
        logger.error(e.getMessage(), e);
      } catch (CayenneException e) {
        logger.error(e.getMessage(), e);
      } catch (Throwable t) {
        logger.warn(t.getMessage(), t);
      } finally {
        Transaction.bindThreadTransaction(null);
      }
    }
    if (dataSourceFactory instanceof DataSourceFactoryDelegate) {
      ((DataSourceFactoryDelegate) dataSourceFactory).tearDown();
    }
    DataContext.bindThreadDataContext(null);
  }

  public static boolean isJdbcPostgreSQL() {

    DataContext dataContext = DataContext.getThreadDataContext();
    String adapterName = null;
    try {
      adapterName =
        ((AutoAdapter) dataContext.getParentDataDomain().getNode(
          Database.getDomainName() + "domainNode").getAdapter())
          .getAdapter()
          .getClass()
          .getName();
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
    }

    return adapterName != null && adapterName.endsWith("PostgresAdapter");
  }

  public static boolean isJdbcMySQL() {

    DataContext dataContext = DataContext.getThreadDataContext();
    String adapterName = null;
    try {
      adapterName =
        ((AutoAdapter) dataContext.getParentDataDomain().getNode(
          Database.getDomainName() + "domainNode").getAdapter())
          .getAdapter()
          .getClass()
          .getName();
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
    }

    return adapterName != null && adapterName.endsWith("MySQLAdapter");
  }

  protected static DBCPDataSourceFactory createDataSourceFactory() {
    String property =
      System.getProperty("com.aimluck.eip.orm.DataSourceFactory");
    if (property == null || property.isEmpty()) {
      return new CustomDBCPDataSourceFactory();
    } else {
      try {
        Class<?> forName = Class.forName(property);
        DBCPDataSourceFactory instance =
          (DBCPDataSourceFactory) forName.newInstance();
        return instance;
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static DBCPDataSourceFactory dataSourceFactory = null;

  public static void initialize(ServletContext servletContext) {
    String property =
      servletContext.getInitParameter("com.aimluck.eip.orm.DataSourceFactory");
    if (property == null || property.isEmpty()) {
      dataSourceFactory = new CustomDBCPDataSourceFactory();
    } else {
      try {
        Class<?> forName = Class.forName(property);
        dataSourceFactory = (DBCPDataSourceFactory) forName.newInstance();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void beginTransaction(DataContext dataContext) {
    boolean res =
      JetspeedResources.getBoolean("aipo.jdbc.aggregateTransaction");
    if (!res) {
      return;
    }
    Transaction tx = Transaction.getThreadTransaction();
    if (tx == null) {
      CustomTransaction ctx = new CustomTransaction();
      ctx.begin();
      Transaction.bindThreadTransaction(ctx);
    }
  }

  public static String castToIntRawColumn(String column) {
    if (isJdbcMySQL()) {
      return "CAST(" + column + " AS UNSIGNED)";
    } else if (isJdbcPostgreSQL()) {
      return "CAST(" + column + " AS INT)";
    }
    return column;
  }

  public static String castToDateTimeRawColumn(String column) {
    if (isJdbcMySQL()) {
      return "CAST(" + column + " AS DATETIME)";
    } else if (isJdbcPostgreSQL()) {
      return "CAST(" + column + " AS TIMESTAMP)";
    }
    return column;
  }

  public static String castToDateRawColumn(String column) {
    if (isJdbcMySQL()) {
      return "CAST(" + column + " AS DATE)";
    } else if (isJdbcPostgreSQL()) {
      return "CAST(CASE WHEN length("
        + column
        + " ) = 0 THEN NULL ELSE "
        + column
        + "  END AS DATE)";
    }
    return column;
  }

  private Database() {
  }

}
