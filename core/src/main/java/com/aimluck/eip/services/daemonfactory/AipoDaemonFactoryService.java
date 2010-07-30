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
package com.aimluck.eip.services.daemonfactory;

import java.util.Hashtable;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonConfig;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.daemon.DaemonException;
import org.apache.jetspeed.daemon.DaemonThread;
import org.apache.jetspeed.services.daemonfactory.JetspeedDaemonFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 *
 */
public class AipoDaemonFactoryService extends JetspeedDaemonFactoryService {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AipoDaemonFactoryService.class.getName());

  private Hashtable<String, Daemon> daemons = new Hashtable<String, Daemon>();

  private Hashtable<String, DaemonThread> threads = new Hashtable<String, DaemonThread>();

  protected ServletConfig config = null;

  /**
   *
   */
  public synchronized void init(ServletConfig config) {
    this.config = config;
    super.init(config);
  }

  /**
   *
   * @param entry
   */
  private void start(DaemonEntry entry) {
    logger.info("DaemonFactory:  start(): starting daemon -> "
        + entry.getName());
    DaemonThread dt = new DaemonThread(entry);
    this.threads.put(entry.getName(), dt);
    dt.start();
  }

  /**
   *
   */
  public Daemon getDaemon(DaemonEntry entry) throws DaemonException {
    Daemon daemon = (Daemon) this.daemons.get(entry.getName());

    if (daemon != null) {
      return daemon;
    } else {
      logger.info("Creating daemon: " + entry.getName());
    }

    try {

      daemon = (Daemon) Class.forName(entry.getClassname()).newInstance();

      DaemonConfig dc = new DaemonConfig();

      daemon.init(dc, entry);

      this.daemons.put(entry.getName(), daemon);

      return daemon;

    } catch (ClassNotFoundException e) {
      logger.error("Exception", e);
      throw new DaemonException("daemon not found: " + e.getMessage());
    } catch (InstantiationException e) {
      logger.error("Exception", e);
      throw new DaemonException("couldn't instantiate daemon: "
          + e.getMessage());
    } catch (IllegalAccessException e) {
      logger.error("Exception", e);
      throw new DaemonException(e.getMessage());
    }

  }

  /**
   *
   */
  public void process(DaemonEntry entry) throws DaemonException {

    DaemonThread dt = (DaemonThread) this.threads.get(entry.getName());

    if (dt == null) {
      start(entry);
      dt = (DaemonThread) this.threads.get(entry.getName());
    }

    int status = this.getStatus(entry);

    if (status != Daemon.STATUS_PROCESSING && status != Daemon.STATUS_UNKNOWN
        && dt != null) {
      synchronized (dt) {
        dt.notify();
      }
    }

    if (dt != null && dt.isAlive() == false) {
      dt.start();
    }
  }

  public ServletConfig getServletConfig() {
    return config;
  }

}
