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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;

/**
 * ブログエントリーの添付ファイルの一覧を処理するクラスです。 <br />
 */
public class BlogFileRawScreen extends FileuploadRawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogFileRawScreen.class.getName());

  @Override
  protected void init(RunData rundata) throws Exception {
    EipTBlogFile blogfile = BlogUtils.getEipTBlogFile(rundata);
    setFilePath(BlogUtils.getSaveDirPath(Database.getDomainName(), blogfile
      .getOwnerId()
      .intValue())
      + blogfile.getFilePath());
    setFileName(blogfile.getTitle());
  }

  /**
   *
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      doCheckAclPermission(
        rundata,
        ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);
    } catch (ALPermissionException e) {
      try {
        doCheckAclPermission(
          rundata,
          ALAccessControlConstants.POERTLET_FEATURE_BLOG_ENTRY_SELF,
          ALAccessControlConstants.VALUE_ACL_LIST);
      } catch (ALPermissionException ex) {
        throw new Exception();
      }
    }
    try {
      super.doOutput(rundata);
    } catch (Exception e) {
      logger.error("[BlogFileRawScreen]", e);
    }
  }
}
