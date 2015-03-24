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

package org.apache.jetspeed.portal.controllers;

// Turbine stuff
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

// Jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PanedPortletController;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * A Velocity based portlet controller implementation that can be used
 * to manage paned content (ie, where a only a subset of all portlets
 * is visible at any given time)
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *
 * @version $Id: VelocityPanedPortletController.java,v 1.13 2004/02/23 03:25:06 jford Exp $
 */

public class VelocityPanedPortletController extends VelocityPortletController
    implements PanedPortletController
{
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(VelocityPanedPortletController.class.getName());
    
    public static final String DEFAULT_PARAMETER = "pane";

    /*
     * @return the pane parameter name
     *
     */
    public String getPaneParameter()    
    {
        return JetspeedResources.PATH_PANEID_KEY;
    }


    /**
     * Test whether the selected portlet is considered selected for the current
     * request.
     *
     * @param p the Portlet to check
     * @param rundata the RunData for the request
     * @return true if the portlet is selected, false otherwise
     */
    public boolean isSelected( Portlet p, RunData rundata )
    {
        String peid = rundata.getParameters().getString( getPaneParameter() );
        String pname = rundata.getParameters().getString(JetspeedResources.PATH_PANENAME_KEY);
        String last = retrievePaneIDFromSession(rundata);
        
        //match by portlet name if appropriate.
        if (pname != null && pname.equals(p.getName())) 
        {
            return true;	
        }

        if (peid == null)
        {
            if (last == null)
            {
                return (getPortlets().getPortletAt( 0 ) == p);
            }
            else
            {
                if (pname == null) 
                {
                    return (p.getID().equals(last));
                }
                else
                {
                    //If the current portlet set has a portlet with the same name as the one we're trying to select
                    //we don't want to select anything else b/c we'll select the one we want AND the last-used portlet.
                    //If the portlet set doesn't have a portlet by this name, we WANT to select the last
                    //used, otherwise nothing will be selected in this set.
	                 return (getPortlets().getPortletByName(pname) == null && p.getID().equals(last));
                }
            }
        }
        else 
        {          
            String subPane = null;
            int index = peid.indexOf(JetspeedResources.PATH_SUBPANE_SEPARATOR);
            if (index > -1)
            {
                subPane = peid.substring(index + 1);
                peid = peid.substring(0, index);
            }
            
            if ( p.getID().equals(peid) ) // && subPane == null )
            {
                return true;
            }

            // is this the sub pane?
            if (subPane!=null && p.getID().equals(subPane))
            {
              // change the currently selected pane in the user session
              // If this is not done, the tab selection works, but the
              // content is picked up from the pane in the session!!
              if(!p.getAttribute("_menustate", "open", rundata).equals("closed")) {
                  SessionState state = ((JetspeedRunData)rundata).getPortletSessionState(getPortlets().getID());
                  state.setAttribute(JetspeedResources.PATH_PANEID_KEY, subPane);   
              }

              return true;
            }

			// is the peid for this tab set?
            if (getPortlets().getPortletByID(peid) != null)
            {
                // its for another tab in this set
                return false;
            }
            if (subPane == null)
            {
                if (last == null)
                {
                    return (getPortlets().getPortletAt( 0 ) == p); 
                }
                else
                {
                    return (p.getID().equals(last));
                }
            }
            else
            {
                if (p.getID().equals( subPane )) 
                {
                    // change the currently selected pane in the user session
                    // If this is not done, the tab selection works, but the
                    // content is picked up from the pane in the session!!
                    if(!p.getAttribute("_menustate", "open", rundata).equals("closed"))
                    {
                        SessionState state = 
                            ((JetspeedRunData)rundata).getPortletSessionState(getPortlets().getID());
                        state.setAttribute(JetspeedResources.PATH_PANEID_KEY, subPane);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  Builds the link to access to a given pane.
     *
     *  @param rundata The request data.
     *  @param portlet The portlet to build the link for by id.
     *  @return DynamicURI A new Dynamic URI with the query parameter
     */
    public DynamicURI getPortletURI( Portlet portlet, RunData rundata )
    {
        JetspeedLink jsLink = null;
        try
        {
            jsLink = JetspeedLinkFactory.getInstance(rundata);
        }
        catch( Exception e)
        {
            logger.error("Exception",  e);
        }
        DynamicURI uri = jsLink.getPaneById(portlet.getID());
        JetspeedLinkFactory.putInstance(jsLink);
        
        return uri;
    }

    /**
     * Returns the pane id of the parameter used for pane selection
     *
     *  @param rundata The request data.
     *  @param byParameter Set to true to look by query parameter first.
     *  @return String The pane id for the selected pane.
     *
     */
    public String retrievePaneID(RunData rundata, boolean byParameter)
    {
        if (false == byParameter)
            return retrievePaneIDFromSession(rundata);

        String pane = rundata.getParameters().getString( getPaneParameter() );
        
        if (pane == null)
        {
            // the parameter is undefined, search for sticky value in session
            String id = getPortlets().getID(); 
            pane = retrievePaneIDFromSession(rundata);
        }
        
        if(pane != null)
        {
            int index = pane.indexOf(JetspeedResources.PATH_SUBPANE_SEPARATOR);
            if (index > -1)
            {
                //return pane.substring(index + 1);
                return pane.substring(0, index);
            }
        }
        
        return pane;
    }

    /**
     * Returns the pane id from the session for pane selection of this portlet set / portal page
     *
     *  @param rundata The request data.
     *  @return String The pane id for the selected pane.
     *
     */
    protected String retrievePaneIDFromSession(RunData rundata)
    {
        // get the state for this portlet (portlet set) in this page in this session
        SessionState state = ((JetspeedRunData)rundata).getPortletSessionState(getPortlets().getID());

        // get the PANE_PARAMETER attribute
        String pane = (String) state.getAttribute(JetspeedResources.PATH_PANEID_KEY);
        
        // if not yet defined, select the first portlet set
        if (pane == null)
        {
            // use default
            if(getPortlets().size() > 0)
            {
	            pane = getPortlets().getPortletAt(0).getID();
            }
        }
       
        return pane;
    }

    /**
     * Saves the pane id to the session to remember selection state of menu or tab for this portlet set / portal page.
     *
     *  @param rundata The request data.
     *  @param id  The tab id to save for this controller
     */
    public void savePaneID( RunData data, String id )
    {
        // get the state for this portlet (portlet set) in this page in this session
        SessionState state = ((JetspeedRunData)data).getPortletSessionState(getPortlets().getID());
        
        // set the PANE_PARAMETER attribute
        state.setAttribute(JetspeedResources.PATH_PANEID_KEY, id);
    }

    /**
     * Sets the name of the parameter that will define which pane should
     * be displayed
     *
     * @deprecated
     *
     * @param name the selection parameter name
     */
    public void setParameterName( String name )
    {
        getConfig().setInitParameter( "parameter", name );          
    }
    
    /**
     * Returns the name of the parameter used for pane selection
     *
     * @deprecated
     */
    public String getParameterName()
    {
        return getConfig().getInitParameter( "parameter", DEFAULT_PARAMETER )
               + getPortlets().getName();
    }

    /**
     * Returns the name of the parameter used for pane selection
     *
     * @deprecated
     *
     */
    public String retrievePaneName(RunData rundata)
    {
        String pane = rundata.getParameters().getString( getParameterName() );
        
        if (pane == null)
        {
            // the parameter is undefined, search for sticky value in session
            pane = (String)rundata.getUser().getTemp( "pane-"+getParameterName() );
            
            if (pane == null)
            {
                // use default
                pane = getConfig().getInitParameter( "defaultpane", "0" );
            }
        }
        
        return pane;
    }

    /**
     * Sets the name of the parameter that will define which pane should
     * be displayed
     *
     * @deprecated
     *
     * @param name the selection parameter name
     */
    public void savePaneName( RunData data, String name )
    {
        data.getUser().setTemp( "pane-"+getParameterName(), name );
    }
    
}

