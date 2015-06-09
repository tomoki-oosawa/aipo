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


import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.ecs.*;


/**
 * Portlet for rendering HTML links
 * Can also be used for WML but no output will be created so
 * redering for wml is done in by the /wml/column.vm file
 * 
 * @author <a href="mailto:A.Kempf@web.de">Andreas Kempf</a>
 * @version $Id: LinkPortlet.java,v 1.7 2004/02/23 04:03:34 jford Exp $
 */
public class LinkPortlet extends AbstractPortlet
{

  // Define parameter name for a image
  public static final String L_IMAGE = "image";
  // Define parameter name for the link name
  public static final String L_NAME  = "anchor";
  // Define parameter name for the link url
  public static final String L_URL   = "link";
  // Define parameter name for the link description
  public static final String L_DESC  = "description";
  // Define the image for opening the link in an external window
  public static final String EXT_LINK_IMG  = "exlink.gif";


/**
 * Render HTML links like:
 * <bullet> <open_new_window_link+image> <link_image> <link_name> <link_description>.
 * @return org.apache.ecs.ConcreteElement
 * @param data org.apache.turbine.util.RunData
 */
public org.apache.ecs.ConcreteElement getContent(org.apache.turbine.util.RunData data)
{

  CapabilityMap cap = ((JetspeedRunData)data).getCapability();

  // only for HTML mimetype!!!
  if (cap.getPreferredType().getCode().equals(MimeType.HTML.getCode()))
  {
    String link;
    String image;
    String name;
    String desc;
    String res = "";
    String cstr = "";
    int contains = 0;
    int i = 0;

    do
    {
      if (i>0)
        cstr=String.valueOf(i);

      link = getPortletConfig().getInitParameter(L_URL+cstr);

      // Link available?
      if ((link!=null) && (link.length()>0))
      {

        // start Linklist
        if (i==0)
          res = "<ul>";

        image = getPortletConfig().getInitParameter(L_IMAGE+cstr);
        name = getPortletConfig().getInitParameter(L_NAME+cstr);
        desc = getPortletConfig().getInitParameter(L_DESC+cstr);

        // set description
        if ((desc==null) || (desc.length()<1))
          desc = "follow this link";

        // add new entry
        res += "<li>";

        // add open in new window link
        res += "<A HREF=\""+link+"\" TARGET=\"_new\"><IMG SRC=\"images/"+EXT_LINK_IMG+"\" BORDER=\"0\" ALT=\""+name+"\"></A>";

        // add link
        res += "<A HREF=\""+link+"\">";

        // add image
        if ((image != null) && (image.length()>0))
          res += "&nbsp;<IMG SRC=\""+image+"\" HSPACES=\"5\" ALT=\""+name+"\" BORDER=\"0\">&nbsp;&nbsp;";

        // add name and description
        res += name+"</A>&nbsp;&nbsp;&nbsp;&nbsp;<SMALL>"+desc+"</SMALL></li>";
        contains++;
      }
      else
        link = null;

      i++;
    }
    while (link != null);
    {
    }

    // close list
    if (contains > 0)
      res += "</ul>";

    return(new StringElement(res));
  }



    return new org.apache.jetspeed.util.JetspeedClearElement( " " );
}

}
