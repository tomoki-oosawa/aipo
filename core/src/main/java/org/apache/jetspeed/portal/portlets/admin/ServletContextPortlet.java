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
import org.apache.ecs.html.Table;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.ConcreteElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

//turbine
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.servlet.TurbineServlet;

//standard java stuff
import java.util.Enumeration;
import javax.servlet.ServletContext;

/**

@author <a href="mailto:tkuebler@cisco.com">Todd Kuebler</a>
@version $Id: ServletContextPortlet.java,v 1.4 2004/02/23 03:26:19 jford Exp $ 

Based on the Java Runtime Portlet.

*/
public class ServletContextPortlet extends AbstractPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ServletContextPortlet.class.getName());
    
    public ConcreteElement getContent( RunData rundata ) {

        Table table = new Table().setWidth("100%");

        try {

            ServletContext context = TurbineServlet.getServletContext();

            table.addElement( new TR() 
                .addElement( new TH( "Key" ) )
                .addElement( new TH( "String Value" ) ) );

            table.addElement( new TR() 
                .addElement( new TD( "Server Info" ) )
                .addElement( new TD( context.getServerInfo() ) ) );

            table.addElement( new TR() 
                .addElement( new TH( "Attribute" ) )
                .addElement( new TH( "String Value" ) ) );

            Enumeration names = context.getAttributeNames();

            while ( names.hasMoreElements() ) {
                String name = (String) names.nextElement();
                table.addElement( new TR()
                    .addElement( new TD( name ) )
                    .addElement( new TD( context.getAttribute( name ).toString() ) ) );
            }

            table.addElement( new TR() 
                .addElement( new TH( "InitParameter" ) )
                .addElement( new TH( "String Value" ) ) );


            Enumeration ipnames = context.getInitParameterNames();
            while ( ipnames.hasMoreElements() ) {
                String ipname = (String) ipnames.nextElement();
                table.addElement( new TR()
                    .addElement( new TD( ipname ) )
                    .addElement( new TD( context.getInitParameter( ipname ).toString() ) ) );
            }

        } catch (Throwable t) {
            logger.error("Throwable",  t);
            table.addElement( new TR() 
                .addElement( new TD( "Error" ) )
                .addElement( new TD( "Could not read servlet context" ) ) );
        }

        return table;
    }
    
    /**
    @author <a href="mailto:tkuebler@cisco.com">Todd Kuebler</a>
    @version $Id: ServletContextPortlet.java,v 1.4 2004/02/23 03:26:19 jford Exp $ 
    */
    public void init() throws PortletException {

        this.setTitle("Java Servlet Context");
        this.setDescription("Attributes of your Servlet Context");

    }

    public boolean getAllowEdit(RunData rundata) {
        return false;
    }

    public boolean getAllowMaximize(RunData rundata) {
        return false;
    }
    
    
}
