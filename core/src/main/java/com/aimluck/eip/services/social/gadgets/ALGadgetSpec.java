/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://aipostyle.com/
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
import java.util.List;

/**
 * 
 */
public class ALGadgetSpec extends HashMap<String, Object> implements
    Serializable {

  private static final long serialVersionUID = -1189192399765199814L;

  public String getTitle() {
    return (String) get("title");
  }

  public String getUrl() {
    return (String) get("url");
  }

  public String getDescription() {
    return (String) get("description");
  }

  @SuppressWarnings("unchecked")
  public List<ALOAuthService> getOAuthServices() {
    return (List<ALOAuthService>) get("oauthService");
  }
}
