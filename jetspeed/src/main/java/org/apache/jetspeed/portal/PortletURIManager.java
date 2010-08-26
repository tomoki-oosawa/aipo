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

package org.apache.jetspeed.portal;


//jetspeed support
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.Registry;

//turbine
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;

/**
<p>
Handles providing URIs to Portlet interface providers.
</p>

<p>
The URIs are based on the individual actions such as Edit/Max.  Editing a
portlet allows you to preview it and perform certain actions such as subscribing
to it or adding it to your Mozilla sidebar.  Maximizing a portlet allows you to
view this portlet at full screen.
</p>

<p>
The following HTTP parameters are used to allow Jetspeed to figure out what to
render:

    <pre>
        name: fetches the name from the portlet registry and then adds some more info:
            - url: the URL on which this portlet is based
            - parameter-{NAME]:  allows the client to add params to a Portlet
            - metainfo-[NAME}: allows the client to supply the title or description
              of the Portlet
    </pre>

</p>

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
@version $Id: PortletURIManager.java,v 1.40 2004/02/23 04:05:35 jford Exp $
*/

public class PortletURIManager {

    //Utility methods

    /**
    Get a PortletURI by it's name and rundata

    @see #getPortletMaxURI( RegistryEntry, RunData)
    */
    public static DynamicURI getPortletMaxURI( String name, RunData data ) {
        return getPortletMaxURI( 
            Registry.getEntry(Registry.PORTLET, name ),
            data );
    }
        
    /**
    Get a URI for viewing this portlet by itself.  This is the only method that
    will work if the user has disabled cookies.
    */

    public static DynamicURI getPortletEditURI( Portlet portlet,
                                                RunData data) {

        DynamicURI uri = new DynamicURI( data, "Info" );
        uri.addPathInfo( "portlet", portlet.getName() );

        return uri;

    }

    
    /**
    Get a URL for viewing this URL full screen. This is the only method that
    will work if the user has disabled cookies.
    */
    public static DynamicURI getPortletMaxURI( RegistryEntry entry, RunData data ) {

        DynamicURI uri = new DynamicURI( data );

        uri.addPathInfo( "portlet", entry.getName() );

        return uri;

    }

    /**
    <p>
    Given a ParameterParser, get a PortletEntry.  This is used so that when you have a
    URI created from PortletURIManager you can get back the PortletEntry that created
    it originally.
    </p>

    <p>
    Return null if we aren't able to figure out the PortletEntry
    </p>
    */
    public static final PortletEntry getEntry( ParameterParser params )
    {
        String name = params.getString( "portlet" );
        return (PortletEntry)Registry.getEntry(Registry.PORTLET, name );
    }

}
