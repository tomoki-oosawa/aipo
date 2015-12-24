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
package com.aimluck.eip.orm.access;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.access.QueryLogger;
import org.apache.cayenne.access.Transaction;

/**
 *
 */
public class CustomTransaction extends Transaction {

  /**
   *
   */
  @Override
  public void begin() {
    QueryLogger.logBeginTransaction("transaction started.");
  }

  /**
   * @throws IllegalStateException
   * @throws SQLException
   * @throws CayenneException
   */
  @Override
  public void commit() throws IllegalStateException, SQLException,
      CayenneException {
    try {
      processCommit();
      if (delegate != null) {
        delegate.didCommit(this);
      }
    } finally {
      close();
    }
  }

  /**
   * @throws IllegalStateException
   * @throws SQLException
   * @throws CayenneException
   */
  @Override
  public void rollback() throws IllegalStateException, SQLException,
      CayenneException {
    try {
      processRollback();
      if (delegate != null) {
        delegate.didRollback(this);
      }
    } finally {
      close();
    }
  }

  @Override
  public boolean addConnection(String name, Connection connection)
      throws SQLException {
    if (super.addConnection(name, connection)) {
      if (connection.getAutoCommit()) {
        try {
          connection.setAutoCommit(false);
        } catch (SQLException ignore) {
          //
        }
      }
      return true;
    } else {
      return false;
    }

  }

  protected void close() {
    if (connections == null || connections.isEmpty()) {
      return;
    }

    @SuppressWarnings("unchecked")
    Iterator<Connection> it = connections.values().iterator();
    while (it.hasNext()) {
      try {
        it.next().close();
      } catch (Throwable ignore) {
        //
      }
    }
  }

  protected void processCommit() throws SQLException, CayenneException {

    if (connections != null && connections.size() > 0) {
      Throwable deferredException = null;
      @SuppressWarnings("unchecked")
      Iterator<Connection> it = connections.values().iterator();
      while (it.hasNext()) {
        Connection connection = it.next();
        try {

          if (deferredException == null) {
            connection.commit();
          } else {
            connection.rollback();
          }

        } catch (Throwable th) {
          deferredException = th;
        }
      }

      if (deferredException != null) {
        QueryLogger.logRollbackTransaction("transaction rolledback.");
        if (deferredException instanceof SQLException) {
          throw (SQLException) deferredException;
        } else {
          throw new CayenneException(deferredException);
        }
      } else {
        QueryLogger.logCommitTransaction("transaction committed.");
      }
    }
  }

  protected void processRollback() throws SQLException, CayenneException {

    if (connections != null && connections.size() > 0) {
      Throwable deferredException = null;

      @SuppressWarnings("unchecked")
      Iterator<Connection> it = connections.values().iterator();
      while (it.hasNext()) {
        Connection connection = it.next();

        try {
          if (!connection.isClosed()) {
            connection.rollback();
          }
        } catch (Throwable th) {
          deferredException = th;
        }
      }

      if (deferredException != null) {
        if (deferredException instanceof SQLException) {
          throw (SQLException) deferredException;
        } else {
          throw new CayenneException(deferredException);
        }
      } else {
        QueryLogger.logRollbackTransaction("transaction rolledback.");
      }
    }
  }
}
