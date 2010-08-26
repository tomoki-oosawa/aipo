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

//ecs stuff
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.Table;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;

// java stuff
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map;
import java.util.Enumeration;

//turbine support
import org.apache.turbine.util.RunData;

/**
 *  Returns a group of check boxes using the following options:
 *  <UL>
 *  <LI><code>items</code>: list of comma-delimited check box names/values</LI>
 *  <LI><code>layout</code> [$northsouth|<strong>$eastwest</strong>]: presentation layout</LI>
 *  <LI><code>prefix</code>: prefix to use for check box names, default="cb"</LI> 
 * </UL>
 * 
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: CheckBoxGroup.java,v 1.4 2004/02/23 03:01:20 jford Exp $
 */
public class CheckBoxGroup extends ParameterPresentationStyle
{

    public static final String ITEMS = "items";
    public static final String LAYOUT = "layout";
    public static final String LAYOUT_EW = "$eastwest";
    public static final String LAYOUT_NS = "$northsouth";
    public static final String PREFIX = "prefix";
    public static final String PREFIX_DEFAULT = "cb";

    /**
     * Returns presentation control
     */
    public String getContent(RunData data, String name, String value, Map parms)
    {

        ElementContainer result = new ElementContainer();
        String items = (String)parms.get(ITEMS);
        String layout = (String)parms.get(LAYOUT);
        String prefix = (String)parms.get(PREFIX);
        if (prefix == null)
        {
            prefix = PREFIX_DEFAULT;
        }

        StringTokenizer st = new StringTokenizer(items, ",");
        Vector v = new Vector();
        while ( st.hasMoreTokens() )
        {
            String token = st.nextToken().trim();
            if ( !token.equalsIgnoreCase("") )
            {
                v.add(token);
            }
        }

        Table t = new Table();

        for ( Enumeration e = v.elements(); e.hasMoreElements(); )
        {
            String item = ((String)e.nextElement()).trim();
            Input cb = new Input(Input.CHECKBOX, prefix + item, item);
            cb.setChecked(value.indexOf(item) >= 0);
            cb.setOnClick(getJavascript(name, v, prefix));
            ElementContainer temp = new ElementContainer();
            temp.addElement(cb).addElement("&nbsp;").addElement(item);
            if ( layout.equalsIgnoreCase(LAYOUT_NS) )
            {
                t.addElement(new TR().addElement(new TD(temp)));
            } else
            {
                result.addElement(temp);
            }
        }

        if ( layout.equalsIgnoreCase(LAYOUT_NS) )
        {
            result.addElement(t);
        }

        result.addElement(new Input(Input.HIDDEN, name, value));

        return result.toString();

    }

    /**
     * 
     * @param name
     * @param v
     * @return string
     */
    private String getJavascript(String name, Vector v, String prefix)
    {

        StringBuffer result = new StringBuffer();
        result.append(name).append(".value = ");

        for ( Enumeration e = v.elements(); e.hasMoreElements(); )
        {
            String item = prefix + (String)e.nextElement();
            result.append("((");
            result.append(item);
            result.append(".checked) ? ");
            result.append(item);
            result.append(".value : '')");
            if ( e.hasMoreElements() )
            {
                result.append(" + \',\' + ");
            }
        }

        return result.toString();
    }

    /**
     *  Test method
     */
    public static void main(String args[])
    {

        CheckBoxGroup cbg = new CheckBoxGroup();    
        java.util.Hashtable parms = new java.util.Hashtable();
        parms.put(ITEMS, "Tomaszewski,Gorgon,Zmuda,Szymanowski,Musial,Kasperczak,Deyna,Cmikiewicz,Lato,Szarmach,Gadocha");
        System.out.println(cbg.getContent(null, "test", "Deyna,,,,Gorgon,Lato,Szarmach,", parms));

    }


}
