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
package com.aimluck.eip.orm;

import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.orm.query.SelectQuery;

/**
 * データベース操作ユーティリティ
 * 
 */
public class Database {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(Database.class.getName());

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
   * @param modelClass
   * @param exp
   * @return
   */
  public static <M> SelectQuery<M> query(Class<M> modelClass, Expression exp) {
    return new SelectQuery<M>(modelClass, exp);
  }

  /**
   * プライマリキーで指定されたオブジェクトモデルを取得します。
   * 
   * @param <M>
   * @param modelClass
   * @param primaryKey
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M> M get(Class<M> modelClass, Object primaryKey) {
    DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
    return (M) DataObjectUtils.objectForPK(databaseOrmService.getDataContext(),
      modelClass, primaryKey);
  }

  /**
   * オブジェクトモデルを新規作成します。
   * 
   * @param <M>
   * @param modelClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M> M create(Class<M> modelClass) {
    DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
    return (M) databaseOrmService.getDataContext().createAndRegisterNewObject(
      modelClass);

  }

  /**
   * オブジェクトモデルを削除します。
   * 
   * @param target
   */
  public static void delete(Persistent target) {
    DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
    databaseOrmService.getDataContext().deleteObject(target);

  }

  /**
   * オブジェクトモデルをすべて削除します。
   * 
   * @param target
   */
  public static void deleteAll(List<Persistent> target) {
    DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
    databaseOrmService.getDataContext().deleteObjects(target);

  }

  /**
   * オブジェクトモデルをすべて削除します。
   * 
   * @param target
   */
  public static void deleteAll(Persistent... target) {
    DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
    databaseOrmService.getDataContext().deleteObjects(Arrays.asList(target));

  }

  /**
   * 現在までの更新をコミットします。
   * 
   */
  public static void commit() {
    DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
    databaseOrmService.getDataContext().commitChanges();
  }

  /**
   * 現在までの更新をロールバックします。
   * 
   */
  public static void rollback() {
    try {
      DatabaseOrmService databaseOrmService = DatabaseOrmService.getInstance();
      databaseOrmService.getDataContext().rollbackChanges();
    } catch (Throwable t) {
      logger.warn(t);
    }
  }

  private Database() {

  }
}
