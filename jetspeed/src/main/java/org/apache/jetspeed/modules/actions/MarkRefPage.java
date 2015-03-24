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

package org.apache.jetspeed.modules.actions;

// Java Core Classes
import java.util.Enumeration;

import org.apache.jetspeed.util.URILookup;
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.RunData;

/**
 * Marks the referer page to enable buttons as "back"
 * 
 * @author <A HREF="shesmer@raleigh.ibm.com">Stephan Hesmer</A>
 */
public class MarkRefPage extends Action {
  public void doPerform(RunData data) throws Exception {
    Enumeration enu = data.getRequest().getHeaderNames();
    while (enu.hasMoreElements()) {
      String name = (String) enu.nextElement();
      if ("referer".equalsIgnoreCase(name)) {
        // check name case insensitive, because getHeader checks case-sensitive,
        // though it is defined as case-insensitive in the servlet specification
        String uri = data.getRequest().getHeader(name);
        if (uri != null) {
          // remove sessionid, if exists
          if (uri.indexOf(";jsessionid") != -1) {
            uri = uri.substring(0, uri.indexOf(";jsessionid"));
          }
          // adds sessionid if necessary
          uri = data.getResponse().encodeURL(uri);
          URILookup.markPage(uri, data);
        }
        break;
      }
    }
  }

}
