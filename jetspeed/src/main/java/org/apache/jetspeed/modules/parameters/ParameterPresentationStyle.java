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

package org.apache.jetspeed.modules.parameters;

// Turbine support
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.util.RunData;

// Jetspeed logging classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// Java support
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Interface to be implemented by parameter presentation style class
 *
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: ParameterPresentationStyle.java,v 1.4 2004/02/23 03:01:20 jford Exp $ 
 */
public abstract class ParameterPresentationStyle extends Assembler
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ParameterPresentationStyle.class.getName());     
    
    private Map styleparms = null;

    /**
     * Returns presentation method html fragment
     * 
     * @param data   run context info
     * @param name   name for the returned control
     * @param value  default value for the control
     * @param parms  hashtable with presentation parameters
     * @return html for the control
     */
    public abstract String getContent(RunData data, String name, String value, Map parms);

    /**
     * Allows to initialize style parameter hashtable
     * 
     * @param parms
     */
    public void setParms(Map parms)
    {

        this.styleparms = parms;
    }

    /**
     * Allows to retrieve a style parameter with default
     * 
     * @param key
     * @param def
     * @return object
     */
    public Object getParm(String key, Object def)
    {

        Object result = null;

        if ( this.styleparms != null )
        {
            result = this.styleparms.get(key);
        }

        if ( result == null )
        {
            result = def;
        }

        return result;
    }

    /**
     * <P>Returns javascript event definitions as defined by "[name].style.javascript:[event]".</P>
     * <P> For example: <CODE>symbols.style.javascipt:onChange</CODE>
     * 
     * @return map of javascript events
     */
    public Map getJavascriptEvents()
    {

        Hashtable result = null;

        if (this.styleparms != null) 
        {
             Iterator it = this.styleparms.keySet().iterator();
             while (it.hasNext()) 
             {
                 String parmkey = (String) it.next();
                 if (parmkey.startsWith("javascript:")) 
                 {
                     try 
                     {
                         if (result == null)
                         {
                             result = new Hashtable();
                         }
                         String event = parmkey.substring(parmkey.lastIndexOf(":") + 1);
                         result.put(event, this.styleparms.get(parmkey));
                     } 
                     catch (Exception e) 
                     {
                         logger.error("Exception", e);
                     }
                 }
             }
         }

        return result;
    }

}