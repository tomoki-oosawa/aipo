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

package org.apache.jetspeed.portal.security.portlets;

//jetspeed
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletState;

import org.apache.jetspeed.services.JetspeedSecurity;

//turbine
import org.apache.turbine.util.RunData;
//import org.apache.turbine.util.TurbineRuntimeException;



/**
<p>
This object is used to wrap a Portlet, ensuring that access control rules are enforced.
</p>

@author <A HREF="mailto:sgala@apache.org">Santiago Gala</A>
@author <A HREF="mailto:morciuch@apache.org">Mark Orciuch</A>
@version $Id: StatefulPortletWrapper.java,v 1.5 2004/02/23 03:27:46 jford Exp $
*/
public class StatefulPortletWrapper extends PortletWrapper implements PortletState
{

    /*
     * The portletstate of our portlet
     */
    private PortletState wrappedState = null;

    public StatefulPortletWrapper( Portlet inner )
    {
        super( inner );
        if( inner instanceof PortletState )
        {
            wrappedState = (PortletState) inner;
        }
        else
        {
            //Report error or throw exception
        }
    }

    // PortletState Interface implementation
    
    /**
     * Implements the default close behavior: any authenticated user may
     * remove a portlet from his page
     *
     * @param rundata the RunData object for the current request
     */
    public final boolean allowClose( RunData rundata )
    {
        return checkPermission(rundata, 
                               JetspeedSecurity.PERMISSION_CLOSE );
    }

    /**
     * Returns true if this portlet is currently closed
     */
    public final boolean isClosed(RunData rundata)
    {
        return wrappedState.isClosed( rundata );
    }

    /**
     * Toggles the portlet state between closed and normal
     *
     * @param minimized the new portlet state
     * @param data the RunData for this request
     */
    public final void setClosed(boolean close, RunData rundata)
    {
        if( allowClose( rundata ) )
        {
            wrappedState.setClosed( close, rundata );
        }
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * get information on a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public final boolean allowInfo( RunData rundata )
    {
        return checkPermission(rundata, 
                               JetspeedSecurity.PERMISSION_INFO );
    }

    /**
     * Implements the default customize behavior: any authenticated user may
     * customize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public final boolean allowCustomize( RunData rundata )
    {
        return checkPermission(rundata, 
                               JetspeedSecurity.PERMISSION_CUSTOMIZE );
    }

    /**
     * Implements the default maximize behavior: any authenticated user may
     * maximize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMaximize( RunData rundata )
    {
        return checkPermission(rundata, 
                               JetspeedSecurity.PERMISSION_MAXIMIZE );
    }

    /**
     * Implements the default info behavior: any authenticated user may
     * minimize a portlet
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowMinimize( RunData rundata )
    {
        return checkPermission(rundata, 
                               JetspeedSecurity.PERMISSION_MINIMIZE );
    }

    /**
     * Returns true if this portlet is currently minimized
     */
    public boolean isMinimized(RunData rundata)
    {
        return wrappedState.isMinimized( rundata );
    }

    /**
    Change the portlet visibility state ( minimized <-> normal )

    @param minimize True if the portlet change to minimized
    @param rundata A RunData object
    */
    public void setMinimized( boolean minimize, RunData rundata )
    {
        if( allowMinimize( rundata ) )
        {
            wrappedState.setMinimized(minimize, rundata );
        }
    }
    
    /**
     * Implements the default info behavior: any authenticated user may
     * view portlet in print friendly format
     *
     * @param rundata the RunData object for the current request
     */
    public boolean allowPrintFriendly( RunData rundata )
    {
        return checkPermission(rundata, 
                               JetspeedSecurity.PERMISSION_PRINT_FRIENDLY );
    }

}
