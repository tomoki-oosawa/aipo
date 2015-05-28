/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.modules.actions.portlets.PortletFilter;
import org.apache.jetspeed.om.profile.IdentityElement;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.PortletInfoEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.base.BaseCategory;
import org.apache.jetspeed.om.registry.base.BasePortletEntry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.portal.ALPortalApplicationService;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest.Status;

/**
 * 伝言メモの一覧を処理するクラスです。
 *
 */
public class CustomizeUtils {

  public static final String UI_PORTLETS_SELECTED = "portletsSelected";

  private static final String HIDE_EMPTY_CATEGORIES =
    "customizer.hide.empty.categories";

  public static final String FILTER_FIELDS = "filter_fields";

  public static final String FILTER_VALUES = "filter_values";

  public static final String REFERENCES_REMOVED = "references-removed";

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CustomizeUtils.class.getName());

  public static boolean isEditable(RunData data, PortletEntry entry,
      String mediaType) {

    if (JetspeedSecurity.checkPermission(
      (JetspeedUser) data.getUser(),
      new PortalResource(entry),
      JetspeedSecurity.PERMISSION_VIEW)
      && ((!entry.isHidden())
        && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
          .hasMediaType(mediaType))
      && !entry.getSecurityRef().getParent().equals("admin-view")) {
      return true;
    }
    return false;
  }

  // Create a list of all available portlets
  @SuppressWarnings("unchecked")
  public static List<PortletEntry> buildPortletList(RunData data,
      String mediaType, List<PortletEntry> allPortlets) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    List<PortletEntry> list = null;
    if (request != null) {
      try {
        list = (List<PortletEntry>) request.getAttribute("portlets.list");
      } catch (Throwable ignore) {
        //
      }
    }
    if (list != null) {
      return list;
    }
    list = new ArrayList<PortletEntry>();
    Iterator<?> i = Registry.get(Registry.PORTLET).listEntryNames();

    while (i.hasNext()) {
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, (String) i.next());

      // Iterator medias;
      // Make a master portlet list, we will eventually us this to build a
      // category list
      allPortlets.add(entry);
      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_VIEW)
        && ((!entry.isHidden())
          && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
            .hasMediaType(mediaType))
        && ALPortalApplicationService.isActive(entry.getName())
        && isAdminUserView(entry, data)
        && !entry.getSecurityRef().getParent().equals("admin-view")) {
        list.add(entry);
      }
    }

    ResultList<ALApplication> resultList =
      ALApplicationService.getList(new ALApplicationGetRequest()
        .withStatus(ALApplicationGetRequest.Status.ACTIVE));

    for (ALApplication app : resultList) {
      BasePortletEntry entry = new BasePortletEntry();
      entry.setTitle(app.getTitle().getValue());
      entry.setDescription(app.getDescription().getValue());
      entry.setName("GadgetsTemplate::" + app.getAppId().getValue());
      entry.setParent("GadgetsTemplate");
      entry.addParameter("aid", app.getAppId().getValue());
      entry.addParameter("url", app.getUrl().getValue());
      list.add(entry);
    }

    String[] filterFields =
      (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
    String[] filterValues =
      (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
    list = PortletFilter.filterPortlets(list, filterFields, filterValues);

    Collections.sort(list, new Comparator<PortletEntry>() {
      @Override
      public int compare(PortletEntry o1, PortletEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle().toLowerCase() : (o1)
            .getName()
            .toLowerCase();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle().toLowerCase() : (o2)
            .getName()
            .toLowerCase();

        return t1.compareTo(t2);
      }
    });
    // this is used only by maintainUserSelection - which does not need the
    // portlet list to be regenrated
    if (request != null) {
      request.setAttribute("portlets.list", list);
    }
    return list;
  }

  // Create a list of all available portlets and all applications
  @SuppressWarnings("unchecked")
  public static List<PortletEntry> buildPortletListWithStatus(RunData data,
      String mediaType, List<PortletEntry> allPortlets, Status status) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    List<PortletEntry> list = null;
    if (request != null) {
      try {
        list = (List<PortletEntry>) request.getAttribute("portlets.list");
      } catch (Throwable ignore) {
        //
      }
    }
    if (list != null) {
      return list;
    }
    list = new ArrayList<PortletEntry>();
    Iterator<?> i = Registry.get(Registry.PORTLET).listEntryNames();

    while (i.hasNext()) {
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, (String) i.next());
      entry.setType("active");
      // Iterator medias;
      // Make a master portlet list, we will eventually us this to build a
      // category list
      allPortlets.add(entry);
      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_VIEW)
        && ((!entry.isHidden())
          && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
            .hasMediaType(mediaType))
        && ALPortalApplicationService.isActive(entry.getName())
        && isAdminUserView(entry, data)
        && !entry.getSecurityRef().getParent().equals("admin-view")
        && !status.equals(ALApplicationGetRequest.Status.INACTIVE)) {
        list.add(entry);
      }
    }

    ResultList<ALApplication> appList =
      ALApplicationService.getList(new ALApplicationGetRequest()
        .withStatus(status));

    for (ALApplication app : appList) {
      BasePortletEntry entry = new BasePortletEntry();
      entry.setTitle(app.getTitle().getValue());
      entry.setDescription(app.getDescription().getValue());
      entry.setName("GadgetsTemplate::" + app.getAppId().getValue());
      entry.setParent("GadgetsTemplate");
      entry.setType((app.getStatus() == 1) ? "active" : "inactive");
      list.add(entry);
    }

    String[] filterFields =
      (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
    String[] filterValues =
      (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
    list = PortletFilter.filterPortlets(list, filterFields, filterValues);

    Collections.sort(list, new Comparator<PortletEntry>() {
      @Override
      public int compare(PortletEntry o1, PortletEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle().toLowerCase() : (o1)
            .getName()
            .toLowerCase();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle().toLowerCase() : (o2)
            .getName()
            .toLowerCase();

        return t1.compareTo(t2);
      }
    });
    // this is used only by maintainUserSelection - which does not need the
    // portlet list to be regenrated
    if (request != null) {
      request.setAttribute("portlets.list", list);
    }
    return list;
  }

  // Create a list of all portlets
  @SuppressWarnings("unchecked")
  public static List<PortletEntry> buildAllPortletList(RunData data,
      String mediaType, List<PortletEntry> allPortlets) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    List<PortletEntry> list = null;
    if (request != null) {
      try {
        list = (List<PortletEntry>) request.getAttribute("portlets.list");
      } catch (Throwable ignore) {
        //
      }
    }
    if (list != null) {
      return list;
    }
    list = new ArrayList<PortletEntry>();
    Iterator<?> i = Registry.get(Registry.PORTLET).listEntryNames();

    while (i.hasNext()) {
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, (String) i.next());

      // Iterator medias;
      // Make a master portlet list, we will eventually us this to build a
      // category list
      allPortlets.add(entry);
      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_VIEW)
        && ((!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
          .hasMediaType(mediaType))
        && (entry.getSecurityRef() != null && !entry
          .getSecurityRef()
          .getParent()
          .equals("admin-view"))) {
        list.add(entry);
      }
    }

    ResultList<ALApplication> resultList =
      ALApplicationService.getList(new ALApplicationGetRequest()
        .withStatus(ALApplicationGetRequest.Status.ACTIVE));

    for (ALApplication app : resultList) {
      BasePortletEntry entry = new BasePortletEntry();
      entry.setTitle(app.getTitle().getValue());
      entry.setDescription(app.getDescription().getValue());
      entry.setName("GadgetsTemplate::" + app.getAppId().getValue());
      entry.setParent("GadgetsTemplate");
      entry.addParameter("aid", app.getAppId().getValue());
      entry.addParameter("url", app.getUrl().getValue());
      list.add(entry);
    }

    String[] filterFields =
      (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
    String[] filterValues =
      (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
    list = PortletFilter.filterPortlets(list, filterFields, filterValues);

    Collections.sort(list, new Comparator<PortletEntry>() {
      @Override
      public int compare(PortletEntry o1, PortletEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle().toLowerCase() : (o1)
            .getName()
            .toLowerCase();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle().toLowerCase() : (o2)
            .getName()
            .toLowerCase();

        return t1.compareTo(t2);
      }
    });
    // this is used only by maintainUserSelection - which does not need the
    // portlet list to be regenrated
    if (request != null) {
      request.setAttribute("portlets.list", list);
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, PortletEntry> getUserSelections(RunData data) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    Map<String, PortletEntry> userSelections = null;
    if (request != null) {
      userSelections =
        (Map<String, PortletEntry>) request.getAttribute("portlets.selections");
    }
    if (userSelections == null) {
      userSelections = new HashMap<String, PortletEntry>();
    }
    if (request != null) {
      request.setAttribute("portlets.selections", userSelections);
    }
    return userSelections;
  }

  public static List<PortletInfoEntry> buildInfoList(RunData data,
      String regName, String mediaType) {
    List<PortletInfoEntry> list = new ArrayList<PortletInfoEntry>();

    Iterator<?> i = Registry.get(regName).listEntryNames();

    while (i.hasNext()) {
      PortletInfoEntry entry =
        (PortletInfoEntry) Registry.getEntry(regName, (String) i.next());

      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_CUSTOMIZE)
        && ((!entry.isHidden()) && entry.hasMediaType(mediaType))) {
        list.add(entry);
      }
    }

    Collections.sort(list, new Comparator<RegistryEntry>() {
      @Override
      public int compare(RegistryEntry o1, RegistryEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle() : (o1).getName();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle() : (o2).getName();

        return t1.compareTo(t2);
      }
    });

    return list;
  }

  public static List<RegistryEntry> buildList(RunData data, String regName) {
    List<RegistryEntry> list = new ArrayList<RegistryEntry>();

    Iterator<?> i = Registry.get(regName).listEntryNames();
    while (i.hasNext()) {
      RegistryEntry entry = Registry.getEntry(regName, (String) i.next());

      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_CUSTOMIZE)
        && (!entry.isHidden())) {
        list.add(entry);
      }
    }

    Collections.sort(list, new Comparator<RegistryEntry>() {
      @Override
      public int compare(RegistryEntry o1, RegistryEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle() : (o1).getName();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle() : (o2).getName();

        return t1.compareTo(t2);
      }
    });

    return list;
  }

  /**
   * Builds a list of all portlet categories
   *
   * @param RunData
   *          current requests RunData object
   * @param List
   *          portlets All available portlets
   */
  public static List<BaseCategory> buildCategoryList(RunData data,
      String mediaType, List<PortletEntry> portlets) {
    boolean hideEmpties =
      JetspeedResources.getBoolean(HIDE_EMPTY_CATEGORIES, true);
    TreeMap<String, BaseCategory> catMap = new TreeMap<String, BaseCategory>();
    Iterator<PortletEntry> pItr = portlets.iterator();
    while (pItr.hasNext()) {
      PortletEntry entry = pItr.next();
      if (hideEmpties) {
        if (JetspeedSecurity.checkPermission(
          (JetspeedUser) data.getUser(),
          new PortalResource(entry),
          JetspeedSecurity.PERMISSION_VIEW)
          && ((!entry.isHidden())
            && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
              .hasMediaType(mediaType))) {
          Iterator<?> cItr = entry.listCategories();
          while (cItr.hasNext()) {
            BaseCategory cat = (BaseCategory) cItr.next();
            catMap.put(cat.getName(), cat);
          }
        }
      } else {
        Iterator<?> cItr = entry.listCategories();
        while (cItr.hasNext()) {
          BaseCategory cat = (BaseCategory) cItr.next();
          catMap.put(cat.getName(), cat);
        }
      }
    }

    // BaseCategory allCat = new BaseCategory();
    // allCat.setName("All Portlets");
    // catMap.put(allCat.getName(), allCat);
    return new ArrayList<BaseCategory>(catMap.values());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static List<?>[] buildColumns(Portlets set, int colNum) {
    // normalize the constraints and calculate max num of rows needed
    Iterator<?> iterator = set.getEntriesIterator();
    int row = 0;
    int col = 0;
    int rowNum = 0;
    while (iterator.hasNext()) {
      IdentityElement identityElement = (IdentityElement) iterator.next();

      Layout layout = identityElement.getLayout();

      if (layout != null) {
        for (int p = 0; p < layout.getParameterCount(); p++) {
          Parameter prop = layout.getParameter(p);

          try {
            if (prop.getName().equals("row")) {
              row = Integer.parseInt(prop.getValue());
              if (row > rowNum) {
                rowNum = row;
              }
            } else if (prop.getName().equals("column")) {
              col = Integer.parseInt(prop.getValue());
              if (col > colNum) {
                prop.setValue(String.valueOf(col % colNum));
              }
            }
          } catch (Exception e) {
            // ignore any malformed layout properties
          }
        }
      }
    }

    int sCount = set.getEntryCount() + set.getPortletsCount();
    row = (sCount / colNum) + 1;
    if (row > rowNum) {
      rowNum = row;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Controller customize colNum: "
        + colNum
        + " rowNum: "
        + rowNum);
    }
    // initialize the result position table and the work list
    List[] table = new List[colNum];
    List filler = Collections.nCopies(rowNum + 1, null);
    for (int i = 0; i < colNum; i++) {
      table[i] = new ArrayList();
      table[i].addAll(filler);
    }

    List<IdentityElement> work = new ArrayList<IdentityElement>();

    // position the constrained elements and keep a reference to the
    // others
    for (int i = 0; i < set.getEntryCount(); i++) {
      addElement(set.getEntry(i), table, work, colNum);
    }

    // Add references
    for (int i = 0; i < set.getReferenceCount(); i++) {
      addElement(set.getReference(i), table, work, colNum);
    }

    // insert the unconstrained elements in the table
    Iterator<IdentityElement> i = work.iterator();
    for (row = 0; row < rowNum; row++) {
      for (col = 0; i.hasNext() && (col < colNum); col++) {
        if (table[col].get(row) == null) {
          if (logger.isDebugEnabled()) {
            logger.debug("Set portlet at col " + col + " row " + row);
          }
          table[col].set(row, i.next());
        }
      }
    }

    // now cleanup any remaining null elements
    for (int j = 0; j < table.length; j++) {
      if (logger.isDebugEnabled()) {
        logger.debug("Column " + j);
      }
      i = table[j].iterator();
      while (i.hasNext()) {
        Object obj = i.next();
        if (logger.isDebugEnabled()) {
          logger.debug("Element " + obj);
        }
        if (obj == null) {
          i.remove();
        }

      }
    }
    return table;
  }

  /**
   * Add an element to the "table" or "work" objects. If the element is
   * unconstrained, and the position is within the number of columns, then the
   * element is added to "table". Othewise the element is added to "work"
   *
   * @param element
   *          to add
   * @param table
   *          of positioned elements
   * @param work
   *          list of un-positioned elements
   * @param columnCount
   *          Number of colum
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected static void addElement(IdentityElement element, List[] table,
      List<IdentityElement> work, int columnCount) {
    Layout layout = element.getLayout();
    int row = -1;
    int col = -1;

    if (layout != null) {
      try {
        for (int p = 0; p < layout.getParameterCount(); p++) {
          Parameter prop = layout.getParameter(p);

          if (prop.getName().equals("row")) {
            row = Integer.parseInt(prop.getValue());
          } else if (prop.getName().equals("column")) {
            col = Integer.parseInt(prop.getValue());
          }
        }
      } catch (Exception e) {
        // ignore any malformed layout properties
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Constraints col " + col + " row " + row);
    }
    if ((row >= 0) && (col >= 0) && (col < columnCount)) {
      table[col].add(row, element);
    } else {
      if (layout != null) {
        // We got here because the column, as defined in the layout,
        // is greater then the numner of columns. This usually
        // happens when the number of column has been decreased.
        // Delete the offending layout. It may be recreated with
        // the correct values.
        element.setLayout(null);
        layout = null;
      }
      work.add(element);
    }
  }

  /**
   * 管理者ユーザーのみ表示可能アプリ
   *
   * @param entry
   * @param data
   * @return
   */
  public static boolean isAdminUserView(PortletEntry entry, RunData data) {

    if (data != null) {
      JetspeedRunData jdata = (JetspeedRunData) data;
      if (jdata.getUserId() != null
        && entry != null
        && entry.getSecurityRef() != null
        && entry.getSecurityRef().getParent() != null
        && entry.getSecurityRef().getParent().equals("admin-user-view")) {
        return ALEipUtils.isAdmin(data) ? true : false;
      }
    }
    return true;
  }

  /**
   * 管理者ユーザーのみ表示可能アプリ
   *
   * @param entry
   * @param data
   * @return
   */
  public static boolean isAdminUserView(String portletName, RunData data) {
    PortletEntry entry = null;
    try {
      entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, portletName);
    } catch (Exception e) {
      logger.error("CustomizeUtils.isAdminUserView", e);
    }
    return isAdminUserView(entry, data);
  }

  /**
   * 管理者ユーザーのみ表示可能アプリ
   *
   * @param entry
   * @param data
   * @return
   */
  public static boolean isAdminUserView(PortletEntry entry) {
    JetspeedRunData jData = null;
    try {
      JetspeedRunDataService jrds =
        (JetspeedRunDataService) ServiceUtil
          .getServiceByName(RunDataService.SERVICE_NAME);
      jData = jrds.getCurrentRunData();
    } catch (Exception e) {
      logger.error("CustomizeUtils.isAdminUserView", e);
    }
    return isAdminUserView(entry, jData);
  }

  public static List<?>[] buildCustomizeColumns(RunData rundata,
      Context context, Portlets portlets) {
    HttpServletRequest request = HttpServletRequestLocator.get();

    List<?>[] columns = null;
    if (request != null) {
      columns = (List[]) request.getAttribute("customize-columns");
    }
    String controllerName = portlets.getController().getName();
    int colNum = 2;
    if (controllerName.startsWith("One")) {
      colNum = 1;
    } else if (controllerName.startsWith("Three")) {
      colNum = 3;
    }
    Portlets set = portlets;

    if (logger.isDebugEnabled()) {
      logger.debug("MultiCol: columns "
        + Arrays.toString(columns)
        + " set "
        + set);
    }
    if ((columns != null) && (columns.length == colNum)) {
      int eCount = 0;
      for (int i = 0; i < columns.length; i++) {
        eCount += columns[i].size();
      }

      if (logger.isDebugEnabled()) {
        logger.debug("MultiCol: eCount "
          + eCount
          + " setCount"
          + set.getEntryCount()
          + set.getPortletsCount());
      }
      if (eCount != set.getEntryCount() + set.getPortletsCount()) {
        if (logger.isDebugEnabled()) {
          logger.debug("MultiCol: rebuilding columns ");
        }
        columns = CustomizeUtils.buildColumns(set, colNum);
      }

    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("MultiCol: rebuilding columns ");
      }
      columns = CustomizeUtils.buildColumns(set, colNum);
    }
    if (request != null) {
      request.setAttribute("customize-columns", columns);
    }
    return columns;
  }

  public static Portlet getCustomizePortlet(RunData rundata, String peid) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    Portlet found = null;
    Stack<Portlet> sets = new Stack<Portlet>();
    sets.push(jdata.getCustomizedProfile().getRootSet());

    while ((found == null) && (sets.size() > 0)) {
      PortletSet set = (PortletSet) sets.pop();

      if (set.getID().equals(peid)) {
        found = set;
      } else {
        Enumeration<?> en = set.getPortlets();
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
    return found;
  }
}
