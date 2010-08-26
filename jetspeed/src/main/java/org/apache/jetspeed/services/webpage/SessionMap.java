/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.services.webpage;

// java.util
import java.util.HashMap;
import java.util.Iterator;

// javax.servlet
import javax.servlet.http.*;

import org.apache.log4j.Logger;

/**
 *  Stores a map of sessions with other sites for the current portal session.
 *  The map is keyed by the portal session's SessionID (from the servlet api). 
 *  Binds to the servlet's session so that it can be cleaned up on session end.
 *  This session keeps a map of all live sessions per site local session.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SessionMap.java,v 1.3 2004/02/23 03:46:26 jford Exp $ 
 */

public class SessionMap extends HashMap implements HttpSessionBindingListener
{    

    // the name of the user for this session
    private String sessionUser;
    
    // the servlet session id on this server
    private String sessionID;
    
    // the active status of this session
    private boolean active = true;

    // hit count
    private int hitCount = 0;

    // cache hit count 
    private int cacheCount = 0;

    static Logger log = Logger.getLogger(SessionMap.class);

    /**
     * construct a Session Map
     *
     * @param sessionId the sessionID on this host
     * @param sessionUser the user associated with the new session.
     *
     */
    public SessionMap(String sessionID, String sessionUser)
    {
        this.sessionID = sessionID;
        this.sessionUser = sessionUser;
    }


    /**
     * Gets the user associated with this session.
     *
     * @return the user associated with this sessions.
     */
    public String getUser()
    {
        return sessionUser;
    }

    /**
     * Gets the Session ID associated with this session.
     *
     * @return the session ID associated with this sessions.
     */
    public String getSessionID()
    {
        return sessionID;
    }
      
    /**
     * Gets the Session State, for the servlet session.
     *
     * @return the a string describing the session state.
     */
    public String getSessionState()
    {
        return (active) ? "active" : "inactive";
    }

    /**
     * Gets the managed session count for this portal session.
     *
     * @return the managed session count for this session.
     */
    public int getSessionCount()
    {
        return this.size();
    }

    /**
     * Gets the hitcount for this session.
     *
     * @return the hitcount for this session.
     */
    public int getHitCount()
    {
        return hitCount;
    }

    /**
     * Increments the hitcount for this session.
     *
     */
    public void incHitCount()
    {
        hitCount++;
    }

    /**
     * Gets the cache count for this session.
     *
     * @return the cache count for this session.
     */
    public int getCacheCount()
    {
        return cacheCount;
    }

    /**
     * Increments the hitcount for this session.
     *
     */
    public void incCacheCount()
    {
        cacheCount++;
    }


    /**
     * This method is a session binding event callback.
     * It is called when the session is bound
     *
     */
    public void valueBound(HttpSessionBindingEvent event)
    {
    }

    /**
     * This method is a session unbinding event callback.
     * It is called when the session is unbound.
     * We need to clean up all the site sessions associated with this Session.
     * The session is marked as 'not active' and is no longer useable.
     * It session object is then removed from the agent's cache 
     *
     */    
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        log.info("~~~ SessionMap UNBOUND as " + event.getName() + " from " + event.getSession().getId() );

        // Now logout of all sessions
        Iterator it = values().iterator();
        while (it.hasNext())
        {
            SiteSession hps = (SiteSession)it.next();
            try 
            {
                hps.logout(null);

            } catch(Exception e)
            {
                // continue logging out even if one fails
                log.error("Unbound-Logout of Session: " + e);                                
            }            
        }
        active = false;
        clear();
    }


 }
