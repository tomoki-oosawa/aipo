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
package org.apache.jetspeed.portal.portlets.viewprocessor;

// Jetspeed apis
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;

// XML stuff
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Portlet which renders RDF Site Summary.</p>
 * <p>This portlet uses XML stylesheet for transforming the RSS
 * content into display markup depending on the MimeType requested
 * by the user-agent</p>
 * <p>It accepts the following parameters :
 * <dl>
 * <dt>itemDisplayed</dt>
 * <dd>The number of items from the RSS file to display on screen. Default 15 for HTML, 5 for WML</dd>
 * <dt>showDescription</dt>
 * <dd>Should the portlet show the item descriptions. Must be true or false. Default: true for HTML, false for WML</dd>
 * <dt>showTitle</dt>
 * <dd>Should the portlet show the channel description. Must be true or false. Default: true for HTML, false for WML</dd>
 * <dt>stylesheet[.<mime>]</dt>
 * <dd>The stylesheet URL. If a mime-type is specified, the stylesheet
 * is only used for this mime-type</dd>
 * </dl>
 * 
 * @author <A HREF="mailto:raphael@apache.org">Rapha謖 Luta</A>
 * @version $Id: $
 * @since 1.4b4
 */
public class RSSViewProcessor extends XSLViewProcessor
{

    /**
     * This method loads the init parameters and
     * parse the document tied to this portlet
     * 
     * @param portlet
     * @exception PortletException
     */
    public void init(Portlet portlet)
    throws PortletException
    {

        super.init(portlet);

        //Determine title and description for this portlet
        String title = null;
        String description = null;

        //now find the channel node.
        Node channel = null;
        NodeList list = this.document.getElementsByTagName("channel");

        if (list.getLength() != 1)
        {
            throw new PortletException(ERROR_NOT_VALID);
        }

        channel = list.item(0);

        Node tn = getNode( channel, "title" );

        if ( tn == null ) {
            throw new PortletException( ERROR_NOT_VALID );
        } 
        else 
        {
            Node fc = tn.getFirstChild();
            if (fc != null)
            {
                title = fc.getNodeValue();
            }
        }

        Node dn = getNode( channel, "description" );

        if ( dn != null ) 
        {
            Node fc = dn.getFirstChild();
            if (fc != null)
            {
                description = fc.getNodeValue();
            }
        }

        portlet.setTitle(title);
        portlet.setDescription(description);
    }

}
