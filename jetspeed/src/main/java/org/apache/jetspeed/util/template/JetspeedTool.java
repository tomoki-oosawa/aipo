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

package org.apache.jetspeed.util.template;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;
import org.apache.jetspeed.modules.ParameterLoader;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.ClientRegistry;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.security.portlets.PortletWrapper;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;

/**
 * Utility class for accessing Jetspeed in a "pull" mode
 * 
 * <strong>Since the tool stores a RunData object, it may not be shared between
 * threads and/or requests</strong>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * @version $Id: JetspeedTool.java,v 1.38 2004/03/29 21:38:43 taylor Exp $
 */
public class JetspeedTool implements ApplicationTool {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(JetspeedTool.class.getName());

  /** RunData object for this request */
  protected JetspeedRunData rundata = null;

  /**
   * Empty constructor used by introspection
   */
  public JetspeedTool() {
  }

  /**
   * The Tool constructor
   * 
   * @param data
   *          the RunData object for the current request
   */
  public JetspeedTool(RunData data) {
    this.rundata = (JetspeedRunData) data;
  }

  /**
   * This will initialise a JetspeedTool object that was constructed with the
   * default constructor (ApplicationTool method).
   * 
   * @param data
   *          assumed to be a RunData object
   */
  @Override
  public void init(Object data) {
    this.rundata = (JetspeedRunData) data;
  }

  /**
   * Refresh method - does nothing
   */
  @Override
  public void refresh() {
    // empty
  }

  /**
   * Returns the portlet content customized for the current user. Currently, the
   * layout engine does not handle panes, so the panes are mapped to real PSML
   * files. If the pane name is null or "default", the profiler will
   * automatically chose the PSML content, else the tool will try to load the
   * PSML file with the specified name
   * 
   * @param name
   *          the name of the pane to render
   * @return the rendered content of the pane
   */
  public ConcreteElement getPane(String name) {
    ConcreteElement result = null;
    String msg = "";

    if (null != rundata) {
      Profile profile = rundata.getProfile();
      try {
        if (null == profile) {
          profile = Profiler.getProfile(rundata);
          if (profile == null) {
            throw new ProfileException("Profile not found.");
          }
          rundata.setProfile(profile);
        }

        if ((name == null)
          || Profiler.DEFAULT_PROFILE.equals(name)
          || "".equals(name)) {
          ;
        } else {
          profile.setName(name);
        }

        PSMLDocument doc = profile.getDocument();
        Portlets portlets = doc.getPortlets();

        String useragent = rundata.getUserAgent();
        useragent = useragent.trim();
        ClientRegistry registry =
          (ClientRegistry) Registry.get(Registry.CLIENT);
        ClientEntry entry = registry.findEntry(useragent);

        if ("IPHONE".equals(entry == null ? null : entry.getManufacturer())
          && !Boolean.parseBoolean((String) rundata.getSession().getAttribute(
            "changeToPc"))) {
          for (@SuppressWarnings("unchecked")
          Iterator<Portlets> it = portlets.getPortletsIterator(); it.hasNext();) {
            Portlets subset = it.next();

            {
              // スケジュールがあればスケジュールを先頭にする。
              Entry[] preentrylist = subset.getEntriesArray();
              if (preentrylist.length > 0) {
                int scheduleindex = 0;
                for (int i = 0; i < preentrylist.length; i++) {
                  if ("Schedule".equals(preentrylist[i].getParent())
                    || "AjaxScheduleWeekly".equals(preentrylist[i].getParent())) {
                    scheduleindex = i;
                  }
                  subset.removeEntry(0);
                }
                subset.addEntry(preentrylist[scheduleindex]);
                for (int i = 0; i < preentrylist.length; i++) {
                  if (scheduleindex != i) {
                    subset.addEntry(preentrylist[i]);
                  }
                }
              }
              // タイムラインがあればタイムラインを先頭にする。
              Entry[] entrylist = subset.getEntriesArray();
              if (entrylist.length > 0) {
                int timelineindex = 0;
                for (int i = 0; i < entrylist.length; i++) {
                  if ("Timeline".equals(entrylist[i].getParent())) {
                    timelineindex = i;
                  }
                  subset.removeEntry(0);
                }
                subset.addEntry(entrylist[timelineindex]);
                for (int i = 0; i < entrylist.length; i++) {
                  if (timelineindex != i) {
                    subset.addEntry(entrylist[i]);
                  }
                }
              }
            }

            Controller ctl = subset.getController();
            ctl.setName("MenuController");
            Control c = subset.getControl();
            if (c == null) {
              c = new PsmlControl();
            }
            if (c.getName() != "MenuControl") {
              c.setName("MenuControl");
            }
            subset.setControl(c);
            subset.setController(ctl);
          }
        }

        if (null != doc) {
          result = PortalToolkit.getSet(doc.getPortlets()).getContent(rundata);
        }
      } catch (Exception e) {
        logger.warn("JetspeedTool.getPane: problem getting: "
          + name
          + " from current request's profile: "
          + e.toString());
        msg = e.getMessage();
      }

    }

    if (result == null) {
      result = new StringElement("Error retrieving Portal Page: " + msg);
    }

    return result;
  }

  /**
   * Return the content of a named portlet. This portlet is sought in the
   * current PSML resource.
   * 
   * If a control is attached to the portlet description, returns the defined
   * portlet and control, otherwise use the default control.
   * 
   * Note: This will return the FIRST portlet with a name = name. Use
   * getPortletById().
   * 
   * @param name
   *          the name of the portlet to render
   * @return the rendered content of the portlet
   * 
   * @deprecated Use getPortletById()
   */
  @Deprecated
  public ConcreteElement getPortlet(String name) {
    ConcreteElement result = null;
    Portlet found = null;
    Stack sets = new Stack();
    sets.push(rundata.getProfile().getRootSet());

    while ((sets.size() > 0) && (found == null)) {
      PortletSet set = (PortletSet) sets.pop();

      if (set.getName().equals(name)) {
        found = set;
      } else {
        Enumeration en = set.getPortlets();
        while ((found == null) && en.hasMoreElements()) {
          Portlet p = (Portlet) en.nextElement();

          // unstack the controls to find the real PortletSets
          Portlet real = p;
          while (real instanceof PortletControl) {
            real = ((PortletControl) p).getPortlet();
          }

          if (real instanceof PortletSet) {
            // we'll explore this set afterwards
            sets.push(real);
          } else if (p.getName().equals(name)) {
            found = p;
          }
        }
      }
    }

    if (found != null) {
      result = found.getContent(rundata);
    }

    if (result == null) {
      // the customizer already streamed its content, return a stub
      result = new ConcreteElement();
    }

    return result;
  }

  /**
   * This method retrieves the appropriate customizer portlet for the current
   * portlet
   * 
   * @param p
   *          the portlet to customize
   * @param data
   *          the RunData for this request
   * @return the portlet object of the appropriate customizer
   */
  public static Portlet getCustomizer(Portlet p) {
    Portlet customizer = p;

    while (p instanceof PortletControl) {
      p = ((PortletControl) p).getPortlet();
    }

    // if the portlet cannot customize itself...
    if (!p.providesCustomization()) {

      // look for the customizer name in the portlet
      // config (from Registry definition)

      String name = p.getPortletConfig().getInitParameter("_customizer");

      if (name == null) {
        String key = (p instanceof PortletSet) ? "PortletSet" : "Portlet";

        name =
          JetspeedResources.getString("customizer." + key, key + "Customizer");
      }

      try {
        customizer = PortletFactory.getPortlet(name, p.getID() + "customize");
        customizer.getPortletConfig().setPortletSkin(
          p.getPortletConfig().getPortletSkin());
        PortletControl control = PortalToolkit.getControl((String) null);
        if (control != null) {
          control.setPortlet(customizer);
          control.init();
          return control;
        }
      } catch (Exception e) {
        logger.error("Exception", e);
      }
    }

    return customizer;
  }

  /**
   * This method retrieves the appropriate information portlet for the current
   * portlet
   * 
   * @param p
   *          the portlet to display information about
   * @param data
   *          the RunData for this request
   * @return the portlet object of the appropriate customizer
   */
  public static Portlet getPortletInfoPortlet(RunData data) {
    Portlet info = null;

    String name =
      JetspeedResources.getString(
        "PortletInfoPortlet.name",
        "PortletInfoPortlet");

    try {

      if (null != data) {
        JetspeedRunData jdata = (JetspeedRunData) data;
        Profile profile = jdata.getProfile();

        if (null == profile) {
          logger.warn("JetspeedTool: profile is null");
          profile = Profiler.getProfile(jdata);
          jdata.setProfile(profile);
        }

        Portlet source = findPortlet(data);
        if (source != null) {
          jdata.setPortlet(source.getName());
          info = PortletFactory.getPortlet(name, "PortletInfoPortlet");
          info.getPortletConfig().setPortletSkin(
            source.getPortletConfig().getPortletSkin());
          PortletControl control = PortalToolkit.getControl((String) null);
          if (control != null) {
            control.setPortlet(info);
            control.init();
            return control;
          }
        }
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }

    return info;
  }

  /**
   * Finds portlet identified by js_peid in the current user's profile
   * 
   * @param rundata
   *          for this request
   * @return portlet identified by js_peid
   */
  private static Portlet findPortlet(RunData rundata) {

    Portlet found = null;
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    String peid = jdata.getJs_peid();
    if (peid != null) {
      Stack sets = new Stack();
      sets.push(jdata.getProfile().getRootSet());

      while ((found == null) && (sets.size() > 0)) {
        PortletSet set = (PortletSet) sets.pop();

        if (set.getID().equals(peid)) {
          found = set;
        } else {
          Enumeration en = set.getPortlets();
          while ((found == null) && en.hasMoreElements()) {
            Portlet p = (Portlet) en.nextElement();

            // unstack the controls to find the real PortletSets
            Portlet real = p;
            while (real instanceof PortletControl) {
              real = ((PortletControl) p).getPortlet();
            }

            if (real instanceof PortletSet) {
              if (real.getID().equals(peid)) {
                found = real;
              } else {
                // we'll explore this set afterwards
                sets.push(real);
              }
            } else if (p.getID().equals(peid)) {
              found = p;
            }
          }
        }
      }
    }

    return found;
  }

  /**
   * Return the content of a portal element given the id of the element.
   * 
   * @param id
   *          The portlet id
   * @return the rendered content of the portlet
   */
  public ConcreteElement getPortalElement(String id) {
    ConcreteElement result = null;

    if (null != rundata) {
      Profile profile = rundata.getProfile();
      try {
        if (null == profile) {
          System.out.println("profile is null");
          profile = Profiler.getProfile(rundata);
          rundata.setProfile(profile);
        }

        PSMLDocument doc = profile.getDocument();
        if (null != doc) {
          Entry entry = doc.getEntryById(id);
          if (null == entry) {
            // FIXME: need to write this function
            // Portlets ps = doc.getPortletsById(id);
            result = new StringElement("not implemented - PortletElement");
          } else {
            Portlet p = PortletFactory.getPortlet(entry);
            if (p != null) {
              result = p.getContent(rundata);
            } else {
              result = new StringElement("Error retrieving PortletElement");
            }

          }
        }
      } catch (Exception e) {
        logger.error("Exception", e);
      }
    }

    if (result == null) {
      result = new StringElement("Error fetching pane");
    }

    return result;

  }

  /**
   * Return the content of a portlet using the portlet's id (PEID). This portlet
   * is sought in the current PSML resource.
   * 
   * If a control is attached to the portlet description, returns the defined
   * portlet and control, otherwise use the default control.
   * 
   * @param peid
   *          the peid of the portlet to render
   * @return the rendered content of the portlet
   */
  public ConcreteElement getPortletById(String peid) {
    ConcreteElement result = null;
    Portlet found = null;
    Stack sets = new Stack();
    sets.push(rundata.getProfile().getRootSet());

    while ((sets.size() > 0) && (found == null)) {
      PortletSet set = (PortletSet) sets.pop();

      if (set.getID().equals(peid)) {
        found = set;
      } else {
        Enumeration en = set.getPortlets();
        while ((found == null) && en.hasMoreElements()) {
          Portlet p = (Portlet) en.nextElement();

          // unstack the controls to find the real PortletSets
          Portlet real = p;
          while (real instanceof PortletControl) {
            real = ((PortletControl) p).getPortlet();
          }

          if (real instanceof PortletSet) {
            // we'll explore this set afterwards
            sets.push(real);
          } else if (p.getID().equals(peid)) {
            found = p;
          }
        }
      }
    }

    if (found != null) {
      // Return portlet's content checking the security first
      result = PortletWrapper.wrap(found).getContent(rundata);
    }

    if (result == null) {
      // the customizer already streamed its content, return a stub
      result = new ConcreteElement();
    }

    return result;
  }

  /**
   * Return the content of a portlet using the portlet's name. This portlet is
   * sought in the registry. This is useful when you want to get portlet's
   * content without actually having the portlet in user's profile (for example,
   * to preview a portlet before adding it to the profile).
   * <P>
   * If a control name is specified to the portlet description, returns the
   * defined portlet and control, otherwise use the default control.
   * <P>
   * Issues to resolve:
   * <UL>
   * <LI>is new portlet instance created everytime someone previews the same
   * portlet?</LI>
   * <LI>should use the same skin as the current pane</LI>
   * <LI>if TitlePortletControl is used, the action icons (max, min, etc) are
   * not functional. Also, customize icon should not be present.</LI>
   * <LI>interactive portlets (such as DatabaseBrowser) lose functionality (such
   * as sorting in DatabaseBrowser).</LI>
   * </UL>
   * 
   * @param portletName
   *          Name of the portlet as defined in registry
   * @param controlName
   *          Optional control name to use in displaying the portlet
   * @return the rendered content of the portlet
   */
  public ConcreteElement getPortletFromRegistry(RunData data) {

    ConcreteElement result = null;
    Portlet p = null;
    String portletName = data.getParameters().getString("p");
    String controlName = data.getParameters().getString("c");

    try {

      // Retrieve registry entry
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, portletName);

      // Verify security for the parameter
      boolean canAccess =
        JetspeedSecurity.checkPermission(
          (JetspeedUser) data.getUser(),
          new PortalResource(entry),
          JetspeedSecurity.PERMISSION_CUSTOMIZE);

      if (canAccess) {
        // Always set portlet id to "preview" so each preview request gets it
        // from the cache.
        // At least, I think that's how it works.
        p = PortletFactory.getPortlet(portletName, "preview");
        PortletControl control =
          controlName == null
            ? PortalToolkit.getControl((String) null)
            : PortalToolkit.getControl(controlName);
        if (control != null) {
          JetspeedRunData jdata = rundata;
          // Use the profile's skin
          p.getPortletConfig().setPortletSkin(
            PortalToolkit.getSkin(jdata
              .getProfile()
              .getDocument()
              .getPortlets()
              .getSkin()));
          control.setPortlet(p);
          control.init();
          result = control.getContent(rundata);
        } else if (p != null) {
          result = p.getContent(rundata);
        }
      } else {
        result =
          new JetspeedClearElement(Localization.getString(
            data,
            "SECURITY_NO_ACCESS_TO_PORTLET"));
      }
    } catch (Exception e) {
      logger.error("Exception", e);
      result = new ConcreteElement();
    }

    if (result == null) {
      // the customizer already streamed its content, return a stub
      result = new ConcreteElement();
    }

    return result;
  }

  /**
   * Return the content of a portlet using the portlet's name. This portlet is
   * sought in the registry. This is useful when you want to get portlet's
   * content without actually having the portlet in user's profile (for example,
   * to preview a portlet before adding it to the profile).
   * <P>
   * If a control name is specified to the portlet description, returns the
   * defined portlet and control, otherwise use the default control.
   * <P>
   * Issues to resolve:
   * <UL>
   * <LI>is new portlet instance created everytime someone previews the same
   * portlet?</LI>
   * <LI>should use the same skin as the current pane</LI>
   * <LI>if TitlePortletControl is used, the action icons (max, min, etc) are
   * not functional. Also, customize icon should not be present.</LI>
   * <LI>interactive portlets (such as DatabaseBrowser) lose functionality (such
   * as sorting in DatabaseBrowser).</LI>
   * </UL>
   * 
   * @param portletName
   *          Name of the portlet as defined in registry
   * @param controlName
   *          Optional control name to use in displaying the portlet
   * @return the rendered content of the portlet
   * @deprecated Do not use this method because it's not secure. It will be
   *             removed after Beta 5.
   */
  @Deprecated
  public ConcreteElement getPortletFromRegistry(String portletName,
      String controlName) {

    ConcreteElement result = null;
    Portlet p = null;

    try {
      // Always set portlet id to "preview" so each preview request gets it from
      // the cache.
      // At least, I think that's how it works.
      p = PortletFactory.getPortlet(portletName, "preview");
      PortletControl control =
        controlName == null
          ? PortalToolkit.getControl((String) null)
          : PortalToolkit.getControl(controlName);
      if (control != null) {
        JetspeedRunData jdata = rundata;
        // Use the profile's skin
        p.getPortletConfig().setPortletSkin(
          PortalToolkit.getSkin(jdata
            .getProfile()
            .getDocument()
            .getPortlets()
            .getSkin()));
        control.setPortlet(p);
        control.init();
        result = control.getContent(rundata);
      } else if (p != null) {
        result = p.getContent(rundata);
      }
    } catch (Exception e) {
      logger.error("Exception", e);
      result = new ConcreteElement();
    }

    if (result == null) {
      // the customizer already streamed its content, return a stub
      result = new ConcreteElement();
    }

    return result;
  }

  /**
   * Returns a parameter in its defined parameter style
   * 
   * @param data
   *          for this request
   * @param portlet
   *          portlet instance
   * @param parmName
   *          parameter name
   * @return current parameter value using specified presentation style
   */
  public static String getPortletParameter(RunData data, Portlet portlet,
      String parmName) {

    if (portlet != null && parmName != null) {
      String parmValue =
        portlet.getPortletConfig().getInitParameter(parmName, "");
      return getPortletParameter(data, portlet, parmName, parmValue);
    }

    return "";
  }

  /**
   * Returns a parameter in its defined parameter style
   * 
   * @param data
   *          for this request
   * @param portlet
   *          portlet instance
   * @param parmName
   *          parameter name
   * @param parmValue
   *          current parameter value
   * @return current parameter value using specified presentation style
   */
  public static String getPortletParameter(RunData data, Portlet portlet,
      String parmName, String parmValue) {
    String result = null;
    try {
      if (portlet != null && parmName != null) {
        // Retrieve registry entry and its parameter
        PortletEntry entry =
          (PortletEntry) Registry.getEntry(Registry.PORTLET, portlet.getName());
        Parameter param = entry.getParameter(parmName);

        // Verify security for the parameter
        boolean canAccess =
          JetspeedSecurity.checkPermission(
            (JetspeedUser) data.getUser(),
            new PortalResource(entry, param),
            JetspeedSecurity.PERMISSION_CUSTOMIZE);
        Map portletParms = portlet.getPortletConfig().getInitParameters();
        String parmStyle =
          portlet.getPortletConfig().getInitParameter(parmName + ".style");

        // Add portlet reference
        portletParms.put(parmName.concat(".style.portlet"), portlet);

        if (canAccess) {
          if (parmStyle != null) {
            result =
              ParameterLoader.getInstance().eval(
                data,
                parmStyle,
                parmName,
                parmValue,
                portletParms);
          } else {
            result =
              "<input type=\"text\" name=\""
                + parmName
                + "\" value=\""
                + parmValue
                + "\"";
          }
        } else {
          // If security does not allow access to specific parameter, allow to
          // provide a fallback parameter
          String parmNameNoAccess =
            portlet.getPortletConfig().getInitParameter(
              parmName + ".style.no-access");
          if (parmNameNoAccess != null) {
            if (logger.isDebugEnabled()) {
              logger.debug("JetspeedTool: access to parm ["
                + parmName
                + "] disallowed, redirecting to parm ["
                + parmNameNoAccess
                + "]");
            }
            String parmStyleNoAccess =
              portlet.getPortletConfig().getInitParameter(
                parmNameNoAccess + ".style");
            result =
              ParameterLoader.getInstance().eval(
                data,
                parmStyleNoAccess,
                parmNameNoAccess,
                parmValue,
                portletParms);
          }
        }
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }

    return result;
  }

  /**
   * Returns a parameter rendered in specific parameter style
   * 
   * @param data
   *          for this request
   * @param parmStyle
   *          parameter style
   * @param parmName
   *          parameter name
   * @param parmValue
   *          current parameter value
   * @param options
   *          optional style parameters in delimited format
   *          (option1=value1;option2=value2)
   * @return current parameter value using specified presentation style
   */
  public static String getParameter(RunData data, String parmStyle,
      String parmName, String parmValue, String parmOptions) {
    String result = null;
    try {
      if (parmName != null) {
        if (parmStyle != null) {
          Map options = null;
          if (parmOptions != null && parmOptions.length() > 0) {
            options = new Hashtable();

            StringTokenizer st = new StringTokenizer(parmOptions, ";");
            String prefix = parmName + ".style.";
            while (st.hasMoreTokens()) {
              StringTokenizer pair = new StringTokenizer(st.nextToken(), "=");
              if (pair.countTokens() == 2) {
                options.put(prefix + pair.nextToken().trim(), pair
                  .nextToken()
                  .trim());
              }
            }

          }
          result =
            ParameterLoader.getInstance().eval(
              data,
              parmStyle,
              parmName,
              parmValue,
              options);
        } else {
          result =
            "<input type=\"text\" name=\""
              + parmName
              + "\" value=\""
              + parmValue
              + "\"";
        }
      }
    } catch (Exception e) {
      logger.error("Exception", e);
      result =
        "<input type=\"text\" name=\""
          + parmName
          + "\" value=\""
          + parmValue
          + "\"";
    }

    return result;
  }

  /**
   * Retreives the correct SecurityReference for the portlet based on the
   * current profile and the request.
   */
  public SecurityReference getSecurityReference(Entry entry) {
    return JetspeedSecurity.getSecurityReference(entry, rundata);
  }

  public int getSecuritySource(Entry entry) {
    return JetspeedSecurity.getSecuritySource(entry, rundata);
  }

  /**
   * Retreives the Entry object for current portlet based on the "js_peid"
   * parameter
   */
  public Entry getEntryFromRequest() throws Exception {
    String jsPeid = rundata.getParameters().getString("js_peid");
    Profile profile = Profiler.getProfile(rundata);
    PSMLDocument doc = profile.getDocument();
    return doc.getEntryById(jsPeid);
  }

}
