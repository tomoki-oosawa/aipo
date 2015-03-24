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

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;

/**
 * This interface must be implemented by all portlet controllers that don't
 * display their entire content at one time. The methods defined in this
 * interface allows the controls object that interact with this controller
 * to build the correct links for referencing the hidden portlets
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *
 * @version $Id: PanedPortletController.java,v 1.9 2004/02/23 04:05:35 jford Exp $
 */
public interface PanedPortletController extends PortletController, PortletSetController
{
    /*
     * @return the pane parameter name
     *
     */
    public String getPaneParameter();

    /**
     * Test whether the selected portlet is considered selected for the current
     * request.
     *
     * @param p the Portlet to check
     * @param rundata the RunData for the request
     * @return true if the portlet is selected, false otherwise
     */
    public boolean isSelected( Portlet p, RunData rundata );

    /**
     * Builds a link object to access a given pane.
     *
     * @param p the pane portlet object
     * @param rundata the rundata for this request
     * @return a DynamicURI that can be used to reference the specific 
     * portlet
     */
    public DynamicURI getPortletURI( Portlet p, RunData rundata );

    /**
     * Returns the pane id of the parameter used for pane selection
     *
     *  @param rundata The request data.
     *  @param byParameter Set to true to look by query parameter first.
     *  @return String The pane id for the selected pane.
     */
    public String retrievePaneID(RunData rundata, boolean byParameter);

    /**
     * Saves the pane id to the session to remember selection state of menu or tab.
     *
     *  @param rundata The request data.
     *  @param id  The tab id to save for this controller
     */
    public void savePaneID( RunData rundata, String id );

    /**
     * Sets the name of the pane that should
     * be displayed
     *
     * @deprecated
     *
     * @param name the selection parameter name
     */
    public void savePaneName( RunData data, String name );

    /**
     * Returns the name of the pane selector parameter for this controller
     *
     * @deprecated
     */
    public String getParameterName();

    /**
     * Sets the name of the pane selector parameter for this controller
     * @param name the new parameter name
     *
     * @deprecated
     */
    public void setParameterName(String name);

    /**
     * Returns the name of the pane that should be displayed
     * 
     * @deprecated
     *
     * @param byParameter Set to true to look by query parameter first.
     */
    public String retrievePaneName(RunData rundata);

}
