/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.modules.screens;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTWikiFile;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.wiki.util.WikiFileUtils;

/**
 * WIKIの添付ファイルを処理するクラスです。
 */
public class WikiFileRawScreen extends FileuploadRawScreen {

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    EipTWikiFile file = WikiFileUtils.getEipTWikiFile(rundata);
    if (null == file) {
      throw new ALPageNotFoundException();
    }
    super.setFilePath(WikiFileUtils.getSaveDirPath(
      Database.getDomainName(),
      file.getOwnerId().intValue())
      + file.getFilePath());
    super.setFileName(file.getFileName());
    super.doOutput(rundata);
  }

}
