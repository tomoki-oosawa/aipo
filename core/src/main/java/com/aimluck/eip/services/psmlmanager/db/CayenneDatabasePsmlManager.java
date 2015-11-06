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
package com.aimluck.eip.services.psmlmanager.db;

// PSML Manager Service interface
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.jetspeed.services.psmlmanager.db.DBOperations;
import org.exolab.castor.mapping.Mapping;

/**
 * This interface defines the Database PSML Manager service
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: DatabasePsmlManager.java,v 1.2 2004/02/23 03:32:19 jford Exp $
 */
public interface CayenneDatabasePsmlManager extends PsmlManagerService,
    DBOperations {
  public Mapping getMapping();

}
