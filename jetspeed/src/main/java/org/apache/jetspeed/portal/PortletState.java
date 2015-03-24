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

package org.apache.jetspeed.portal;

// Turbine stuff
import org.apache.turbine.util.RunData;

/**
 * This interface allows implementing portlets to modify the
 * behavior of their PortletControl manager by specifically allowing or
 * refusing window manipulation actions.
 * If the given PortletControl implements these actions, it must use
 * this information.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 */
public interface PortletState
{

    /**
     * Returns true if the portlet allows the use to remove it from its portlat
     * page
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowClose( RunData rundata );

    /**
     * Returns true if this portlet is currently closed
     */
    public boolean isClosed(RunData data);

    /**
     * Toggles the portlet state between closed and normal
     *
     * @param minimized the new portlet state
     * @param data the RunData for this request
     */
    public void setClosed(boolean closed, RunData data);

    /**
     * Returns true if the portlet allows the manager to link to a information
     * page about this portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowInfo( RunData rundata );

    /**
     * Returns true if the portlet supports customization of its options
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowCustomize( RunData rundata );

    /**
     * Returns true if the portlet allows the user to maximize it, ie use
     * all the display space allowed to portlets in the given pane
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMaximize( RunData rundata );

    /**
     * Returns true if the portlet allows the user to minimize it.
     * The portlet content is not displayed when minimized
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMinimize( RunData rundata );

    /**
     * Returns true if this portlet is currently minimized
     */
    public boolean isMinimized(RunData data);

    /**
     * Toggles the portlet state between minimized and normal
     *
     * @param minimized the new portlet state
     * @param data the RunData for this request
     */
    public void setMinimized(boolean minimized, RunData data);

    /**
     * Returns true if the portlet allows the user to display it in print friendly format.
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowPrintFriendly( RunData rundata );

}
