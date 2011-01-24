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

package com.aimluck.eip.gadgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BasicBlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.util.Base32;
import org.apache.shindig.common.util.CharsetUtil;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.services.social.ALSocialApplicationFactoryService;
import com.aimluck.eip.services.social.ALSocialApplicationHandler;
import com.aimluck.eip.services.social.ALSocialApplicationHandler.Property;
import com.google.common.base.Charsets;

/**
 * 
 */
public class GadgetContext {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GadgetContext.class.getName());

  private final String SECURITY_TOKEN_KEY =
    "/WEB-INF/conf/securityTokenKey.txt";

  private final String viewer;

  private String appUrl;

  private String container = "default";

  private String domain = "shindig";

  private long moduleId = 0;

  private final ALSocialApplicationHandler appHandler;

  private final RunData runData;

  /**
   * 
   */
  public GadgetContext(RunData runData, String viewer, String appUrl) {
    this.runData = runData;
    this.viewer = viewer;
    this.appUrl = appUrl;
    appHandler =
      ALSocialApplicationFactoryService
        .getInstance()
        .getSocialApplicationHandler();
  }

  public String getSecureToken() {
    HttpServletRequest request = ((JetspeedRunData) runData).getRequest();
    String activeUrl = request.getRequestURL().toString();
    try {
      BlobCrypterSecurityToken token =
        new BlobCrypterSecurityToken(
          loadCrypterFromFile(runData),
          container,
          domain);
      token.setOwnerId(viewer);
      token.setViewerId(viewer);
      token.setAppUrl(appUrl);
      token.setModuleId(moduleId);
      token.setActiveUrl(activeUrl);
      return token.encrypt();
    } catch (Exception ex) {
      logger.error(ex, ex);
      return "";
    }
  }

  public String getServerBase() {
    HttpServletRequest request = ((JetspeedRunData) runData).getRequest();

    StringBuilder builder = new StringBuilder();
    String scheme = request.getScheme();
    String serverName = request.getServerName();
    int serverPort = request.getServerPort();

    builder.append(scheme).append("://");
    if (isLockedDomainRequired()) {
      byte[] sha1 = DigestUtils.sha(appUrl);
      String hash = new String(Base32.encodeBase32(sha1));
      builder.append(hash).append(getLockedDomainSuffix());
    } else {
      builder.append(serverName);
      if (serverPort != 80 || serverPort != 443) {
        builder.append(":").append(serverPort);
      }
    }
    return builder.append("/gadgets/").toString();
  }

  protected boolean isLockedDomainRequired() {
    String config =
      appHandler.getContainerConfig(Property.LOCKED_DOMAIN_REQUIRED);
    return "true".equalsIgnoreCase(config);
  }

  protected String getLockedDomainSuffix() {
    String config =
      appHandler.getContainerConfig(Property.LOCKED_DOMAIN_SUFFIX);
    return config;
  }

  protected BlobCrypter loadCrypterFromFile(RunData rundata) throws IOException {
    BufferedReader reader = null;
    byte[] keyBytes = null;
    try {
      ServletContext servletContext =
        ((JetspeedRunData) rundata).getServletContext();
      InputStream resourceAsStream =
        servletContext
          .getResourceAsStream("/WEB-INF/conf/securityTokenKey.txt");
      reader =
        new BufferedReader(new InputStreamReader(
          resourceAsStream,
          Charsets.UTF_8));
      String line = reader.readLine();
      if (line == null) {
        throw new IOException("Unexpectedly empty keyfile: "
          + SECURITY_TOKEN_KEY);
      }
      line = line.trim();
      keyBytes = CharsetUtil.getUtf8Bytes(line);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        // oh well.
      }
    }
    return new BasicBlobCrypter(keyBytes);
  }

  /**
   * @return container
   */
  public String getContainer() {
    return container;
  }

  /**
   * @param container
   *          セットする container
   */
  public void setContainer(String container) {
    this.container = container;
  }

  /**
   * @return domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * @param domain
   *          セットする domain
   */
  public void setDomain(String domain) {
    this.domain = domain;
  }

  /**
   * @return moduleId
   */
  public long getModuleId() {
    return moduleId;
  }

  /**
   * @param moduleId
   *          セットする moduleId
   */
  public void setModuleId(long moduleId) {
    this.moduleId = moduleId;
  }

  /**
   * @return appUrl
   */
  public String getAppUrl() {
    return appUrl;
  }

  /**
   * @param appUrl
   *          セットする appUrl
   */
  public void setAppUrl(String appUrl) {
    this.appUrl = appUrl;
  }
}
