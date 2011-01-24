/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.io.File;
import java.util.Calendar;

import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonConfig;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.util.ALEipUtils;

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
  public void run() {

    Calendar cal = Calendar.getInstance();
    int interval = JetspeedResources.getInt("deletetemporaryfile.keepinterval");
    cal.add(Calendar.MINUTE, -interval);

    // tmpフォルダとファイルを削除
    String tmpdir = JetspeedResources.getString("aipo.tmp.directory", "");
    ALEipUtils.deleteOldFolder(new File(tmpdir), cal);
  }

  /**
   *
   */
  public void init(DaemonConfig config, DaemonEntry entry) {
    this.config = config;
    this.entry = entry;
  }

  /**
   *
   */
  public DaemonConfig getDaemonConfig() {
    return this.config;
  }

  /**
   *
   */
  public DaemonEntry getDaemonEntry() {
    return this.entry;
  }

  /**
   *
   */
  public int getStatus() {
    return this.status;
  }

  /**
   *
   */
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * 
   * @return
   */
  public int getResult() {
    return this.result;
  }

  /**
   * 
   * @param result
   */
  public void setResult(int result) {
    this.result = result;
  }

  /**
   * 
   * @return
   */
  public String getMessage() {
    return null;
  }

}
