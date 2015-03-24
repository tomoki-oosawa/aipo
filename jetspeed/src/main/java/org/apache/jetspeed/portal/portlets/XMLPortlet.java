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

package org.apache.jetspeed.portal.portlets;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.apache.ecs.html.Comment;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;
import org.apache.ecs.ElementContainer;

import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.SAXPIFilter;

import org.apache.turbine.util.RunData;


/**
Portlet to output an XML well-formed document without any rendering.
Strips all PIs and XML declaration before outputting content to allow
for inclusion in a wider XML document.

@author <A HREF="mailto:raphael@apache.org">Rapha謖 Luta</A>
@version $Id: XMLPortlet.java,v 1.21 2004/02/23 04:03:34 jford Exp $
*/
public class XMLPortlet extends AbstractPortlet 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(XMLPortlet.class.getName());
    
    /**
    Return the PI-stripped XML document wrapped in an ECS ElementContainer
    */
    public ConcreteElement getContent( RunData rundata ) {

        String myContent = null;

        ElementContainer content = new ElementContainer();
        
        // Strip all PIs and XML declaration from the input XML content URL
        try {

            ByteArrayOutputStream bos= new ByteArrayOutputStream();

            String url = JetspeedDiskCache.getInstance().getEntry( getPortletConfig().getURL() ).getURL();

            new SAXPIFilter(new PrintWriter(bos),true).print( url );
            myContent = bos.toString();

        } catch (Exception e) {

            
            logger.error( "Could not parse the following URL:  " + this.getPortletConfig().getURL(), e );
            return content;
        }

        // If parsing is OK, wraps content in ECS element
        if (myContent!=null) {
            content = new ElementContainer();

            content.addElement( new Comment( "BEGIN PORTLET" ) );
            content.addElement( new StringElement( myContent ) );
            content.addElement( new Comment( "END PORTLET" ) );
        }

        return content;
    }

    /**
    Return the PI-stripped XML document wrapped in an ECS ElementContainer.
    Currently same as getContent()
    */
    public ConcreteElement getContent(Object params, RunData rundata) {
        return getContent( rundata );
    }


}
