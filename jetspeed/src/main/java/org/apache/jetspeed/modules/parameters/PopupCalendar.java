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

// ECS support
import org.apache.ecs.html.Input;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.A;
import org.apache.ecs.html.Script;
import org.apache.ecs.ElementContainer;     

// Java classes
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;

// Turbine support
import org.apache.turbine.util.RunData;

/**
 *  Presentation method to show a date input field with a popup calendar. Calendar is accessed by clicking the icon
 *  next to input field. For now, the only date format supported is mm/dd/yyyy. If value <code>$today</code> is passed, the date in the input field will default to today's date.
 *  <p>Options:
 *  <UL>
 *  <LI><code>$<name>.style.formName</code> - form name where the control is displayed; default=DefaultCustomizer</LI>
 *  <LI><code>$<name>.style.format</code> - date format to use for return value; default=mm/dd/yyyy.
 * The date format can have three types of separators: hyphen(-), space( ), or slash(/), but must be consistent in their usage. E.g.
 * d/m/yyyy
 * The acceptable tokens are :
 *   <UL>
 *   <LI>d - day</LI>
 *   <LI>dd - day (padded with 0 if less than 10)</LI>
 *   <LI>m - month (in numbers)</LI>
 *   <LI>mm - month (in numbers, padded with 0 if less than 10)</LI>
 *   <LI>mmm - month (in words)</LI>
 *   <li>yyyy - year</LI>
 *   </UL> 
 *  </LI>
 *  </UL>
 *  </p>
 * 
 * @author <a href="morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: PopupCalendar.java,v 1.3 2004/02/23 03:01:20 jford Exp $
 */
public class PopupCalendar extends ParameterPresentationStyle
{

    public static final String PARM_FORM_NAME   = "formName";
    public static final String PARM_FORMAT      = "format";

    /**
     * Method returning HTML markup for a date list box
     */
    public String getContent(RunData data, String name, String value, Map parms)
    {

        ElementContainer container = new ElementContainer();

        if ( value.equals("$today") )
        {
            Date dt = new Date(System.currentTimeMillis());
            value = new SimpleDateFormat("M/d/yyyy").format(dt);
        }

        container.addElement(new Script().setLanguage("JavaScript").setSrc("javascript/PopupCalendar.js"));

        container.addElement(new Input(Input.TEXT, name, value));

        IMG img = new IMG("images/cal.gif").setAlt("Click here for popup calendar").setBorder(0);
        A a = new A(this.getJavaScript(name), img);

        container.addElement(a);

        return container.toString();
    }

    /**
     * Returns body of the java script event handler
     */
    private String getJavaScript(String fieldName)
    {

        String formName = (String)this.getParm(PARM_FORM_NAME, "DefaultCustomizer");
        String format = (String)this.getParm(PARM_FORMAT, "mm/dd/yyyy");

        Object[] args = {
            formName,
            fieldName,
            format
        };

        String template = "javascript: show_calendar(''{0}.{1}'',{0}.{1}.value,''{2}'');";

        return new MessageFormat(template).format(args);

    }

    /**
     *  Test method
     */
    public static void main(String args[])
    {

        PopupCalendar pc = new PopupCalendar();      
        System.out.println(pc.getContent(null, "test", "08/01/2001", null));
        System.out.println(pc.getContent(null, "test", "$today", null));

    }

}
