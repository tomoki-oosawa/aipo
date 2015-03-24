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
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.jsp.JspService;

// Jetsped Classes 
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.URILookup;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.portal.PortletURIManager;

/**
 * Supporting class for the uriLookup tag.
 * Returns the URL for the respective link
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 */
public class URILookupTag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(URILookupTag.class.getName());
    
    /**
     * type parameter defines type of URI that is requested
     */
    private String type;

    /** 
     * The setter for type parameter
     */
    public void setType(String type)
    {
        this.type = type;
    }

    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    

        try
        {           
            String result = null;

            /* HOME */
            if (type.equalsIgnoreCase( "Home" )) {
              result = URILookup.getURI(URILookup.TYPE_HOME, URILookup.SUBTYPE_NONE, data);
            }  

            /* LOGIN */
            if (type.equalsIgnoreCase( "Login" )) {
              result = URILookup.getURI(URILookup.TYPE_LOGIN, URILookup.SUBTYPE_NONE, data);
            }  

            /* ENROLLMENT */
            if (type.equalsIgnoreCase( "Enrollment" )) {
              result = URILookup.getURI(URILookup.TYPE_ENROLLMENT, URILookup.SUBTYPE_NONE, data);
            }  

            /* LOGOUT */
            if (type.equalsIgnoreCase( "Logout" )) {
              result = URILookup.getURI(URILookup.TYPE_HOME, URILookup.SUBTYPE_LOGOUT, data);
            }  

            /* CUSTOMIZE */
            if (type.equalsIgnoreCase( "Customize") ) {
              result = URILookup.getURI(URILookup.TYPE_CUSTOMIZE, URILookup.SUBTYPE_NONE, data);
            }  

            /* EDIT ACCOUNT */
            if (type.equalsIgnoreCase( "EditAccount" )) {
              result = URILookup.getURI(URILookup.TYPE_EDIT_ACCOUNT, URILookup.SUBTYPE_NONE, data);
            }  

            /* APPLICATIONS */
            if (type.equalsIgnoreCase( "Applications" )) {
              PortletEntry entry = null;
              entry = (PortletEntry)Registry.getEntry( Registry.PORTLET, "Applications" );
              result = PortletURIManager.getPortletMaxURI( entry, data ).toString();
            }  

            /* BASE URL */
            if (type.equalsIgnoreCase( "BaseURL" )) {
              result = URILookup.getWebAppBaseDirURI( data );
            }   

            if (result != null) {
              pageContext.getOut().print(result);
            } else {
              throw new Exception( "jetspeed-URILookup tag: Unknown parameter!");
            }
        }
        catch (Exception e)
        {
            String message = "Error processing uriLookup-tag, parameter: "+ type;
            logger.error(message, e);
            try
            {
                data.getOut().print( "Error processing uriLookup-tag, parameter: "+ type);
            }
            catch(java.io.IOException ioe) {}    
        }
        return EVAL_BODY_INCLUDE;
    }

}
