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
package com.aimluck.eip.services.config.impl;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.account.EipMConfig;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.services.config.ALConfigHandler;

/**
 *
 */
public class ALDefaultConfigHanlder extends ALConfigHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALDefaultConfigHanlder.class.getName());

  private static ALConfigHandler instance;

  public static ALConfigHandler getInstance() {
    if (instance == null) {
      instance = new ALDefaultConfigHanlder();
    }
    return instance;
  }

  /**
   *
   * @param property
   * @return
   */
  @Override
  public String get(Property property) {
    return get(property.toString(), property.defaultValue());
  }

  /**
   *
   * @param property
   * @param value
   */
  @Override
  public void put(Property property, String value) {
    put(property.toString(), value);
  }

  /**
   * @param property
   * @return
   */
  @Override
  public String get(String property, String defaultValue) {
    EipMConfig config = null;
    try {
      Object obj = ALEipManager.getInstance().getConfig(property.toString());
      if (obj != null) {
        return (String) obj;
      } else {
        config =
          Database
            .query(EipMConfig.class)
            .where(Operations.eq(EipMConfig.NAME_PROPERTY, property.toString()))
            .fetchSingle();
        if (config == null) {
          ALEipManager.getInstance().setConfig(
            property.toString(),
            defaultValue);
        } else {
          ALEipManager.getInstance().setConfig(
            property.toString(),
            config.getValue());
        }
      }
    } catch (CayenneRuntimeException e) {
      throw e;
    } catch (Exception ignore) {
    }
    if (config == null) {
      return defaultValue;
    }
    return config.getValue();
  }

  /**
   * @param property
   * @param value
   */
  @Override
  public void put(String property, String value) {
    try {
      EipMConfig config =
        Database.query(EipMConfig.class).where(
          Operations.eq(EipMConfig.NAME_PROPERTY, property)).fetchSingle();
      if (config == null) {
        config = Database.create(EipMConfig.class);
        config.setName(property.toString());
      }
      config.setValue(value);
      Database.commit();
    } catch (Throwable t) {
      logger.error("ALDefaultConfigHanlder", t);
      Database.rollback();
      if (t instanceof CayenneRuntimeException) {
        throw (CayenneRuntimeException) t;
      } else {
        throw new RuntimeException(t);
      }
    }
  }
}
