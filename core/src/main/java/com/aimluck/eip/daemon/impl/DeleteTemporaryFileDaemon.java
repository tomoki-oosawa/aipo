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
package com.aimluck.eip.daemon.impl;

import java.util.Calendar;

import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonConfig;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.services.storage.ALStorageService;

/**
 * 定期的にテンポラリフォルダに存在するファイルを削除するデーモンクラスです。 <br />
 * 
 */
public class DeleteTemporaryFileDaemon implements Daemon {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(DeleteTemporaryFileDaemon.class.getName());

  private int status = Daemon.STATUS_NOT_PROCESSED;

  private int result = Daemon.RESULT_UNKNOWN;

  private DaemonConfig config = null;

  private DaemonEntry entry = null;

  /**
   * テンポラリファイルを削除し、インターバルの時間をセット
   * 
   */
  @Override
  public void run() {

    Calendar cal = Calendar.getInstance();
    int interval = JetspeedResources.getInt("deletetemporaryfile.keepinterval");
    cal.add(Calendar.MINUTE, -interval);

    // tmpフォルダとファイルを削除
    String tmpdir = JetspeedResources.getString("aipo.tmp.directory", "");
    ALStorageService.deleteOldFolder(tmpdir, cal);
  }

  /**
   *
   */
  @Override
  public void init(DaemonConfig config, DaemonEntry entry) {
    this.config = config;
    this.entry = entry;
  }

  /**
   *
   */
  @Override
  public DaemonConfig getDaemonConfig() {
    return this.config;
  }

  /**
   *
   */
  @Override
  public DaemonEntry getDaemonEntry() {
    return this.entry;
  }

  /**
   *
   */
  @Override
  public int getStatus() {
    return this.status;
  }

  /**
   *
   */
  @Override
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * 
   * @return
   */
  @Override
  public int getResult() {
    return this.result;
  }

  /**
   * 
   * @param result
   */
  @Override
  public void setResult(int result) {
    this.result = result;
  }

  /**
   * 
   * @return
   */
  @Override
  public String getMessage() {
    return null;
  }

}
