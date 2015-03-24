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

package org.apache.jetspeed.services;

import org.apache.turbine.services.TurbineServices;
import org.apache.jetspeed.services.db.*;

/**
 * <P>This is a commodity static accessor class around the 
 * <code>JetspeedSecurityService</code></P>
 * 
 * @see org.apache.jetspeed.services.db.JetspeedDatabaseService
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedDatabase.java,v 1.2 2004/02/23 04:00:57 jford Exp $
 */

abstract public class JetspeedDatabase 
{
    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static JetspeedDatabaseService getService()
    {
        return (JetspeedDatabaseService)TurbineServices
                .getInstance()
                .getService(JetspeedDatabaseService.SERVICE_NAME);
    }

    /**
     *  Returns a PersistenceManager for the default database service.
     *  When JDO implementations are available, this method should return a JDO PersistenceManager interface.
     *
     * @return A PersistenceManager instance.
     */
    public static Object getPersistenceManager()  // TODO: this should eventually be a JDO PersistenceManager, not an Object
    {
       return ((JetspeedDatabaseService)getService()).getPersistenceManager();
    }

}