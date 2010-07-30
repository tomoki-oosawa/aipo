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
package org.apache.jetspeed.portal.portlets.admin;

//ecs stuff
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.A;
import org.apache.ecs.html.B;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.LI;
import org.apache.ecs.html.UL;

//turbine stuff
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;


//jetspeed stuff
import org.apache.jetspeed.portal.portlets.*;
import org.apache.jetspeed.services.urlmanager.*;

import java.util.*; 

/**
Shows the user what URLs are considered bad.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
@version $Id: BadURLManagerPortlet.java,v 1.13 2004/02/23 03:26:19 jford Exp $ 
*/
public class BadURLManagerPortlet extends AbstractPortlet {

    public static final String RETRY_URL = "retry";


    public ConcreteElement getContent( RunData rundata ) {

        String url = rundata.getParameters().getString( RETRY_URL );

        if ( url != null ) {
            URLManager.unregister(url);
            rundata.getParameters().remove( RETRY_URL );
        }


        
        ElementContainer root = new ElementContainer();

        List urls = URLManager.list( URLManagerService.STATUS_BAD );
        
        root.addElement( "The following " + 
                         urls.size() + 
                         " URL(s) are considered bad: " );

        root.addElement( new BR() );
        
        root.addElement( "Click on a url to take it out of the list" + 
                         " and retry it in when requested. " );

        root.addElement( new BR() );
        
        UL ul = new UL();
        //FIXME: the getReason() call has to be escaped from HTML markup, CR&LF
        DynamicURI uri = new DynamicURI( rundata );
        
        ParameterParser params = rundata.getParameters();
        uri.addQueryData( params );
        
        Iterator i = urls.iterator();
        
        while ( i.hasNext() ) {
            URLInfo info = URLManager.getInfo( (String)i.next() );
            /* It can happen that url is no longer in list while
               we are processing */
            if( info != null ) {
                uri.removeQueryData( RETRY_URL );
                uri.addQueryData(RETRY_URL, info.getURL());
                ul.addElement( new LI()
                    .addElement( new A(uri.toString() )
                        .addElement( info.getURL() ) )
                    .addElement( new B( info.getMessage() ) ) );
            }
        }
        
        root.addElement( ul );

        java.util.Hashtable rt = URLFetcher.getRealtimeURLs();

        root.addElement( "The following " + 
                         rt.size() + 
                         " URL(s) are being loaded: " );

        root.addElement( new BR() );
        ul = new UL();
        
        java.util.Enumeration en = rt.keys();
        while (en.hasMoreElements()) {
            String key = (String)en.nextElement();
            LI li = new LI().addElement( key )
                .addElement( " - by " );
            if (rt.get(key) != null )
                li.addElement( String.valueOf(((Vector)rt.get(key)).size()) )
                    .addElement( " threads." );
            ul.addElement( li );
        }
        
        root.addElement( ul );
        

        
        return root;
    }

    public void init() {
        this.setTitle( "BadURLManager" );
        
        this.setDescription( "Shows the admin what URLs are considered bad." );
    }
    
    public boolean getAllowEdit(RunData rundata) {
        return false;
    }

    public boolean getAllowMaximize(RunData rundata) {
        return false;
    }
    
}
