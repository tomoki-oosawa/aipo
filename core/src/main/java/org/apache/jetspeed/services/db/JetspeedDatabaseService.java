/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.services.db;

import org.apache.turbine.services.Service;

/**
 * <P>This interface is a facade for all Jetspeed-DB related operations</P>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedDatabaseService.java,v 1.2 2004/02/23 03:28:10 jford Exp $
 */

public interface JetspeedDatabaseService extends Service
{
 
    /** The name of this service */
    public String SERVICE_NAME = "JetspeedDatabase";

    /**
     *  Returns a PersistenceManager for the the configured database service.
     *  When JDO implementations are available, this method should return a JDO PersistenceManager interface.
     *
     * @return A PersistenceManager instance.
     */
    public Object getPersistenceManager();  // TODO: this should eventually be a JDO PersistenceManager, not an Object

}