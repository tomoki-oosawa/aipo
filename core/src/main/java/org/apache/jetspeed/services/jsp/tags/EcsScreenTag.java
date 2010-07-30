package org.apache.jetspeed.services.jsp.tags;

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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

// Turbine Classes 
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.ecs.ConcreteElement;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Supporting class for the ecsscreen tag.
 * Sends the screens ecs element's content to the output stream.  
 * RunData's screen value is expected to be set!
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @version $Id: EcsScreenTag.java,v 1.12 2004/02/23 03:59:40 jford Exp $
 */
public class EcsScreenTag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(EcsScreenTag.class.getName());
    
    /**
     * Method called when the tag is encountered to send attributes to the
     * output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);
        String screenName = data.getScreen();

        // make sure that not the BaseJspScreen is called 
        // as this would result in an endless loop...
        if ( screenName.equals( "BaseJspScreen" ) )
        {
            screenName = TurbineResources.getString("screen.homepage");
        }

        try
        {  
            pageContext.getOut().flush();

            ConcreteElement screenElement = ScreenLoader.getInstance().eval( data, screenName );

            // Check whether this is an "old" screen (that returns a ConcreteElement)
            // or a "new" one that returns null.
            if ( screenElement != null )
            {
                //The ECS element must serialize in the character encoding
                // of the response
                screenElement.setCodeSet( data.getResponse().getCharacterEncoding() );

                screenElement.output( data.getResponse().getWriter() );
            }

        }
    catch (Exception e)
        {
            String message = "Error processing ecs screen '" + screenName + "'.";
            logger.error(message, e);
            try
            {
                data.getOut().print("Error processing ecs screen '" + screenName + "'. See log for more information.");
            }
            catch(java.io.IOException ioe) {}    
        }
        return SKIP_BODY;
    }
}
