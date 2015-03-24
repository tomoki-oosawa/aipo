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

// Element Construction Set
import java.util.Enumeration;
import java.util.Properties;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

/**
 * Handles enumerating Portlets that are also applications
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 */
public class JavaRuntimePortlet extends AbstractPortlet {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(JavaRuntimePortlet.class.getName());

  public ConcreteElement getContent(RunData rundata) {

    Table table = new Table().setWidth("100%");

    try {
      // get Runtime status
      Runtime jvm = Runtime.getRuntime();

      table.addElement(new TR().addElement(new TD("Free Memory (in bytes)"))
          .addElement(new TD(Long.toString(jvm.freeMemory()))));

      table.addElement(new TR().addElement(new TD("Total Memory (in bytes)"))
          .addElement(new TD(Long.toString(jvm.totalMemory()))));

      // get the system properties (It can throw a SecurityException)
      Properties props = System.getProperties();

      Enumeration enu = props.propertyNames();
      while (enu.hasMoreElements()) {
        Object key = enu.nextElement();
        if (!(key instanceof String)) {
          continue;
        }

        Object value = props.getProperty(key.toString());
        table.addElement(new TR().addElement(new TD(key.toString()))
            .addElement(new TD(value.toString())));

      }
    } catch (Throwable t) {
      logger.error("Throwable", t);
      table.addElement(new TR().addElement(new TD("Error")).addElement(
          new TD("Could not read system properties")));
    }

    return table;
  }

  /**
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: JavaRuntimePortlet.java,v 1.18 2004/02/23 03:26:19 jford Exp $
   */
  public void init() throws PortletException {

    this.setTitle("Java Runtime");
    this.setDescription("Information about your Java Runtime");

  }

  public boolean getAllowEdit(RunData rundata) {
    return false;
  }

  public boolean getAllowMaximize(RunData rundata) {
    return false;
  }

}
