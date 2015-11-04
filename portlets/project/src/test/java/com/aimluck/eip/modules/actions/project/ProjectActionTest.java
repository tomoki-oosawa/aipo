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
package com.aimluck.eip.modules.actions.project;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aimluck.eip.cayenne.om.account.EipMConfig;
import com.aimluck.eip.orm.Database;

public class ProjectActionTest {

  @Before
  public void initialize() {
    ProjectTestInitializer.initialize();
  }

  @After
  public void finalizeTest() {
    ProjectTestInitializer.finalizeTest();
  }

  @Test
  public void doPerform() {

    boolean result = true;
    EipMConfig config = Database.create(EipMConfig.class);
    config.setName("commitTest");
    config.setValue("testValue");
    try {
      Database.commit();
    } catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    assertTrue(result);
  }

}
