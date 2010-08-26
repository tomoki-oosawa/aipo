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

package org.apache.jetspeed.services.security;

import java.util.Date;

/**
 * A User's statistics for logon attempts. 
 *
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: UserLogonStats.java,v 1.2 2004/02/23 03:58:11 jford Exp $
 */
public class UserLogonStats
{
    private int failures = 0;
    private int total = 0;
    private long firstLogon = 0;
    private final String username;
    private boolean disabled = false;
    private Object sem;

    UserLogonStats(String username)
    {
        this.username = username;
        sem = new Object();
    }

    public int getFailures()
    {
        return failures;
    }

    public int getTotalFailures()
    {
        return total;
    }

    public long getFirstLogon()
    {
        return firstLogon;
    }

    public String getUserName()
    {
        return username;
    }

    public boolean failCheck(int allowed, long secondsAllowed, int max)
    {
        synchronized(sem)
        {
            if (disabled)
                return true;

            failures = failures + 1;
            total = total + 1;
    
            if (total >= max)
            {
                reset();
                disabled = true;
                return true;
            }
        
            long msAllowed = secondsAllowed * 1000;
            long now = new Date().getTime();
           
            if (firstLogon == 0)
                firstLogon = now;
    
            long diff = now - firstLogon;
    
            if (diff > msAllowed)
                reset();
    
            if (failures >= allowed)
            {
                reset();
                disabled = true;
                return true;
            }
            return false;
        }
    }

    public void reset()
    {
        synchronized(sem)
        {
            failures = 0;
            Date now = new Date();
            firstLogon = now.getTime();
            disabled = false;
        }
    }
}
