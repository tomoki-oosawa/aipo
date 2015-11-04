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

  private final Hashtable<String, Daemon> daemons =
    new Hashtable<String, Daemon>();

  private final Hashtable<String, DaemonThread> threads =
    new Hashtable<String, DaemonThread>();

  protected ServletConfig config = null;

  /**
   *
   */
  @Override
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
  @Override
  public Daemon getDaemon(DaemonEntry entry) throws DaemonException {
    Daemon daemon = this.daemons.get(entry.getName());

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
      logger.error("AipoDaemonFactoryService.getDaemon", e);
      throw new DaemonException("daemon not found: " + e.getMessage());
    } catch (InstantiationException e) {
      logger.error("AipoDaemonFactoryService.getDaemon", e);
      throw new DaemonException("couldn't instantiate daemon: "
        + e.getMessage());
    } catch (IllegalAccessException e) {
      logger.error("AipoDaemonFactoryService.getDaemon", e);
      throw new DaemonException(e.getMessage());
    }

  }

  public ServletConfig getServletConfig() {
    return config;
  }

}
