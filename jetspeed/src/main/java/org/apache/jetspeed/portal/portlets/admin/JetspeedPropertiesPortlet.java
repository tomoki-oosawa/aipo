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
import org.apache.ecs.html.*;
import org.apache.ecs.ElementContainer;

//Jetspeed stuff
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.resources.JetspeedResources;

//turbine
import org.apache.turbine.util.RunData;

//standard java stuff
import java.util.Iterator;

/**
Handles enumerating Portlets that are also applications

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: JetspeedPropertiesPortlet.java,v 1.21 2004/02/23 03:26:19 jford Exp $ 
*/
public class JetspeedPropertiesPortlet extends AbstractPortlet {

    public static final String INPUT_SIZE = "70";
        
    /**
    */
    public void init() throws PortletException {

        this.setTitle("Properties");
        this.setDescription("Jetspeed Properties");


        ElementContainer root = new ElementContainer();
        Table table = new Table().setWidth("100%");

        Iterator i = JetspeedResources.getKeys();

        root.addElement( new B( "Jetspeed properties: " ) );
        
        while ( i.hasNext() ) {
            String key = (String)i.next();
            try {
                Object value = JetspeedResources.getString(key);
                TR row = new TR();
                row.addElement( new TD().addElement( key ) );
                
                if (value == null) {
                    value = "";
                }
                row.addElement( new TD()
                    .addElement( new Input( "text",
                                            "nothing",
                                            value.toString() )
                        .setSize( INPUT_SIZE ) ) );
                
                table.addElement( row );
            } catch (Throwable t) {
                
            }
          
        }

        root.addElement( new Center( table ) );
        
        //wrap it in a basic form so Netscape is smart enough to render the 
        //width.
        this.setContent( new Form( root ) );

    }

    /**
    */
    public boolean getAllowEdit( RunData rundata ) {
        return false;
    }

    /**
    */
    public boolean getAllowMaximize( RunData rundata ) {
        return false;
    }
    
    
}
