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

package org.apache.jetspeed.portal.portlets.admin;

//Element Construction Set
import org.apache.ecs.html.B;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.HR;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;

//Jetspeed stuff
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.services.portletcache.GlobalCache;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.threadpool.ThreadPoolService;
import org.apache.jetspeed.services.threadpool.JetspeedThreadPoolService;
import org.apache.jetspeed.services.urlmanager.URLManager;
import org.apache.jetspeed.services.urlmanager.URLManagerService;
import org.apache.jetspeed.services.urlmanager.URLFetcher;


//turbine
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;

/**
Returns global information about Jetspeed.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: GlobalAdminPortlet.java,v 1.18 2004/02/23 03:26:19 jford Exp $ 
*/
public class GlobalAdminPortlet extends AbstractPortlet {

    
    /**
     * Return general runtime information. This include memory,
     * thread, Registry, Resource, and caching data.
     *
     * @param rundata RunData object for the current request
     * @return ConcreteElement
     */    
    public ConcreteElement getContent( RunData rundata ) {

        
        ElementContainer ec = new ElementContainer();

        this.createCategory( ec, "Memory" );

        ec.addElement( this.getEntry( "Free Memory (K)", 
                                      Long.toString( Runtime.getRuntime().freeMemory()/1024 ) ) );
        ec.addElement( this.getEntry( "Total Memory (K)", 
                                      Long.toString( Runtime.getRuntime().totalMemory()/1024 ) ) );
        
        JetspeedThreadPoolService service =
            ( JetspeedThreadPoolService ) TurbineServices
            .getInstance()
            .getService( ThreadPoolService.SERVICE_NAME );
        
        this.createCategory( ec, "Thread Pool" );

        ec.addElement( this.getEntry( "Available threads: ", 
                            service.getAvailableThreadCount() ) );
        
        ec.addElement( this.getEntry( "Total threads: ", 
                            service.getThreadCount() ) );

        ec.addElement( this.getEntry( "Runnable queue length: ", 
                            service.getQueueLength() ) );

        ec.addElement( this.getEntry( "Processed thread count: ", 
                            service.getThreadProcessedCount() ) );

        this.createCategory( ec, "PortletRegistry" );

        ec.addElement( this.getEntry( "Number of entries: ", 
                            Registry.get(Registry.PORTLET).getEntryCount() ) );

        this.createCategory( ec, "Resource Manager" );

        ec.addElement( this.getEntry( "Entries in Manager", 
                                      URLManager.list( URLManagerService.STATUS_ANY ).size() ) );
        ec.addElement( this.getEntry( "Bad Entries", 
                                      URLManager.list( URLManagerService.STATUS_BAD ).size() ) );
        ec.addElement( this.getEntry( "URLs Loading/Refreshing", 
                                      URLFetcher.getRealtimeURLs().size() ) );

        this.createCategory( ec, "Global Memory Cache" );
        ec.addElement( this.getEntry( "Objects in Cache",
          GlobalCache.getNumberOfObjects()));

        return ec;
        
    }

    /**
    Create a category within this portlet
    */
    private void createCategory( ElementContainer ec, 
                                 String title ) {


        ec.addElement( new HR() );
        ec.addElement( new B().addElement( title + ":" ) );
        ec.addElement( new BR() );
                                     
    }

    private ConcreteElement getEntry( String title,
                                      int value ) {
        return getEntry( title, Integer.toString( value ) );
    }
    
    private ConcreteElement getEntry( String title, 
                                      String value ) {
        
        ElementContainer ec = new ElementContainer();
        
        ec.addElement( title );
        ec.addElement( " -> " );
        ec.addElement( value );
        ec.addElement( new BR() );
        
        return ec;
    }
    
    //requried info
    /**
     * @see AbstractPortlet#init
     */    
    
    public void init() throws PortletException {

        this.setTitle( "Global" );
        this.setDescription( "Global information..." );

    }

    /**
     * @see AbstractPortlet#getAllowEdit
     */    
    public boolean getAllowEdit(RunData rundata) {
        return false;
    }

    /**
     * @see AbstractPortlet#getAllowMaximize
     */    
    public boolean getAllowMaximize(RunData rundata) {
        return false;
    }

}
