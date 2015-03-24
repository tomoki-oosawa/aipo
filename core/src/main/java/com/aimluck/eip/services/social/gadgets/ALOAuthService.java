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
package com.aimluck.eip.services.social.gadgets;

import java.io.Serializable;
import java.util.HashMap;

import com.aimluck.eip.util.ALCommonUtils;

/**
 * 
 */
public class ALOAuthService extends HashMap<String, Object> implements
    Serializable {

  private static final long serialVersionUID = 3445493234180193806L;

  public String getName() {
    return (String) get("name");
  }

  public String getAccessUrl() {
    return (String) get("accessUrl");
  }

  public String getAccessUrlCR() {
    return ALCommonUtils.replaceToAutoCR(getAccessUrl());
  }

  public String getRequestUrl() {
    return (String) get("requestUrl");
  }

  public String getRequestUrlCR() {
    return ALCommonUtils.replaceToAutoCR(getRequestUrl());
  }

  public String getAuthorizationUrl() {
    return (String) get("authorizationUrl");
  }

  public String getAuthorizationUrlCR() {
    return ALCommonUtils.replaceToAutoCR(getAuthorizationUrl());
  }
}
