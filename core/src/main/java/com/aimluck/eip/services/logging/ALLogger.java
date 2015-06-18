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
package com.aimluck.eip.services.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.aimluck.eip.orm.Database;

/**
 * 
 */
public class ALLogger extends Logger {

  /**
   * @param name
   */
  protected ALLogger(String name) {
    super(name);
  }

  @Override
  protected void forcedLog(String fqcn, Priority level, Object message,
      Throwable t) {
    super.forcedLog(fqcn, level, getPrefix() + message, t);
  }

  protected String getPrefix() {
    String domainName = null;
    try {
      domainName = Database.getDomainName();
    } catch (Throwable t) {

    }
    if (domainName == null) {
      return "";
    }
    return "[" + domainName + "] ";
  }
}
