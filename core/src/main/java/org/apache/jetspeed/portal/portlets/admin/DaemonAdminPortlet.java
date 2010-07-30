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

//Element Construction Set
import org.apache.ecs.html.B;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.P;
import org.apache.ecs.html.Table;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.daemon.DaemonNotFoundException;
import org.apache.jetspeed.daemon.impl.FeedDaemon;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.daemonfactory.DaemonFactory;

//turbine
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;

//standard java stuff
import java.util.*;

/**
Handles enumerating Portlets that are also applications

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@author <a href="mailto:raphael@apache.org">Raphael Luta</a>
@author <a href="mailto:sgala@apache.org">Santiago Gala</a>
@version $Id: DaemonAdminPortlet.java,v 1.30 2004/02/23 03:26:19 jford Exp $
*/
public class DaemonAdminPortlet extends AbstractPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DaemonAdminPortlet.class.getName());    
    
    public static final String REFRESH = "Start";

    /**
    Key for starting daemon manually from a form
    */
    private static final String DAEMON = "daemon";


    public ConcreteElement getContent( RunData rundata ) {
        
        try {

            if ( rundata.getParameters().getString( REFRESH ) != null ) {

                String daemon = rundata.getParameters().getString( DAEMON );

                try {
                    rundata.getParameters().remove( REFRESH );
                    rundata.getParameters().remove( DAEMON );

                    DaemonEntry entry = DaemonFactory.getDaemonEntry( daemon );


                    logger.info( "Admin -> asking " + daemon + " to run..." );
                    //now that we have the DaemonEntry kick it off for processing
                    DaemonFactory.process( entry );

                } catch (DaemonNotFoundException e) {
                    logger.error( "Could not process daemon...", e );
                }
            }


            Table table = new Table().setWidth("100%");

            table.addElement( new TR().addElement( new TH() )
                                      .addElement( new TH("Name") )
                                      .addElement( new TH("Status") )
                                      .addElement( new TH("Result") )
                                      .addElement( new TH("On Startup") )
                                      .addElement( new TH("Interval") )
                                      .addElement( new TH("Classname") ) );

            DaemonEntry[] entries = DaemonFactory.getDaemonEntries();

            for (int i = 0; i < entries.length; ++i) {

                table.addElement( new TR()
                                        .addElement( new TD( this.getForm( entries[i].getName(), rundata ) ) )
                                        .addElement( new TD( entries[i].getName() ) )
                                        .addElement( new TD( this.getStatus( DaemonFactory.getStatus( entries[i] ) ) ) )
                                        .addElement( new TD( this.getResult( DaemonFactory.getResult( entries[i] ) ) ) )
                                        .addElement( new TD( new Boolean( entries[i].onStartup() ).toString() ) )
                                        .addElement( new TD( Long.toString( entries[i].getInterval() ) ) )
                                        .addElement( new TD( entries[i].getClassname() ) ) );

                String message = DaemonFactory.getMessage( entries[i] );

                if ( message != null ) {

                    message = entries[i].getName() + ":  " + message;

                    table.addElement( new TR().addElement( new TD().setColSpan( 7 )
                                .addElement( message ) ) );

                }

            }

            ElementContainer content = new ElementContainer();

            //content.addElement( this.getStatus() );
            //content.addElement( this.getFeedCount() );
            content.addElement( table );
            //content.addElement( this.getForm() );

            return content;

        } catch ( Throwable t ) {
            logger.error("Throwable",  t);
            return new StringElement( t.getMessage() );
        }

    }


    /**
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    */
    private String getStatus(int status) {

        String message = "Unknown";

        switch(status) {
            case Daemon.STATUS_NOT_PROCESSED:
                message = "Not processed";
            break;

            case Daemon.STATUS_PROCESSING:
                message = "Processing...";
            break;
            case Daemon.STATUS_PROCESSED:
                message = "Processed";
            break;

        }

        return message;

    }

    /**
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    */
    private String getResult( int result ) {

        String message = "Unknown";

        switch (result) {
            case Daemon.RESULT_SUCCESS:
                message = "Success";
            break;

            case Daemon.RESULT_FAILED:
                message = "Failed";
            break;

            case Daemon.RESULT_PROCESSING:
                message = "Processing...";
            break;
        }

        return message;

    }


    /**
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    */
    private ConcreteElement getFeedCount() {

        return new P().addElement( new B( "Current number of feeds:  ") )
                      .addElement( Integer.toString( FeedDaemon.getCount() ) );

    }


    /**
    Return a form that can refresh the current daemon

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    */
    private Form getForm( String daemon, RunData rundata ) {


        Form form = new Form();

        ParameterParser params = rundata.getParameters();
        Enumeration keys = params.keys();
        while( keys.hasMoreElements() ) {
            String key = (String)keys.nextElement();
            String value = (String)params.getString(key, "");
            form.addElement( new Input( ).setName( key )
                .setType( "hidden" )
                .setValue( value ) );
        }



        form.addElement( new Input().setType( "submit" )
                                    .setName( REFRESH )
                                    .setValue( REFRESH ) );

        form.addElement( new Input().setType( "hidden" )
                                    .setName( DAEMON )
                                    .setValue( daemon ) );

        return form;
    }


    /**
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    */
    public void init() throws PortletException {

        this.setTitle("Daemons");

        this.setDescription("Configure your daemon...");

    }

    public boolean getAllowEdit( RunData rundata ) {
        return false;
    }

    public boolean getAllowMaximize(RunData rundata ) {
        return false;
    }


}
