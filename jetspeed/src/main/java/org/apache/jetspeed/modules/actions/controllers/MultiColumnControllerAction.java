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

package org.apache.jetspeed.modules.actions.controllers;

// Jetspeed stuff
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.profile.IdentityElement;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.om.profile.psml.PsmlLayout;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.services.PsmlManager;

// Turbine stuff
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.RunData;

// Velocity Stuff
import org.apache.velocity.context.Context;

// Java stuff
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This action builds a context suitable for controllers handling
 * grid positioned layout using PortletSet.Constraints
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: MultiColumnControllerAction.java,v 1.30 2004/02/23 02:49:58 jford Exp $
 */
public class MultiColumnControllerAction extends VelocityControllerAction
{

    private static final String REFERENCES_REMOVED = "references-removed";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(MultiColumnControllerAction.class.getName());    
    
    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    protected void buildNormalContext(PortletController controller,
                                      Context context,
                                      RunData rundata)
    {
        try
        {
        // retrieve the number of columns
        String cols = controller.getConfig().getInitParameter("cols");
        int colNum = 0;
        int rowNum = 0;
        try
        {
            colNum = Integer.parseInt(cols);
        }
        catch (Exception e)
        {
            // not an integer or null, default to standarrd value
            colNum = 3;
        }
        context.put("colNum", String.valueOf(colNum));

        //retrieve the size for each of the columns
        String sizes = controller.getConfig().getInitParameter("sizes");
        context.put("sizes", getCellSizes(sizes));

        //retrieve the class for each of the columns
        String columnClasses = controller.getConfig().getInitParameter("col_classes");
        context.put("col_classes", getCellClasses(columnClasses));

        PortletSet set = controller.getPortlets();
        // normalize the constraints and calculate max num of rows needed
        Enumeration en = set.getPortlets();
        int row = 0;
        int col = 0;
        while (en.hasMoreElements())
        {
            Portlet p = (Portlet) en.nextElement();

            PortletSet.Constraints
                constraints = p.getPortletConfig().getConstraints();

            if ((constraints != null)
                 && (constraints.getColumn() != null)
                 && (constraints.getRow() != null))
            {
                col = constraints.getColumn().intValue();
                if (col > colNum)
                {
                    constraints.setColumn(new Integer(col % colNum));
                }

                row = constraints.getRow().intValue();
                if (row > rowNum)
                {
                    rowNum = row;
                }
            }
        }
        row = (int) Math.ceil(set.size() / colNum);
        if (row > rowNum)
        {
            rowNum = row;
        }

        if ( logger.isDebugEnabled() ) {
            logger.debug("Controller calculated setSize " + set.size() + " row " + row + " colNum: " + colNum +  " rowNum: " + rowNum);
        }
        // initialize the result position table and the work list
        List[] table = new List[colNum];
        List filler = Collections.nCopies(rowNum + 1, null);
        for (int i = 0; i < colNum; i++)
        {
            table[i] = new ArrayList();
            table[i].addAll(filler);
        }

        List work = new ArrayList();

        //position the constrained elements and keep a reference to the
        //others
        for (int i = 0; i < set.size(); i++)
        {
            Portlet p = set.getPortletAt(i);

            PortletSet.Constraints
                constraints = p.getPortletConfig().getConstraints();

            if ((constraints != null)
                 && (constraints.getColumn() != null)
                 && (constraints.getRow() != null)
                 && (constraints.getColumn().intValue() < colNum))
            {
                row = constraints.getRow().intValue();
                col = constraints.getColumn().intValue();
                table[col].set(row, p);
            }
            else
            {
                work.add(p);
            }
        }

        //insert the unconstrained elements in the table
        Iterator i = work.iterator();
        for (row = 0; row < rowNum; row++)
        {
            for (col = 0; i.hasNext() && (col < colNum); col++)
            {
                if (table[col].get(row) == null)
                {
                    table[col].set(row, i.next());
                }
            }
        }

        // now cleanup any remaining null elements
        for (int j = 0; j < table.length; j++)
        {
            i = table[j].iterator();
            while (i.hasNext())
            {
                Object obj = i.next();
                if (obj == null)
                {
                    i.remove();
                }
            }
        }

        if (logger.isDebugEnabled())
        {
            dumpColumns(table);
        }

        context.put("portlets", table);
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    protected void buildCustomizeContext(PortletController controller,
                                         Context context,
                                         RunData rundata)
    {
        JetspeedRunData jdata = (JetspeedRunData) rundata;

        // get the customization state for this page
        SessionState customizationState = jdata.getPageSessionState();

        super.buildCustomizeContext(controller, context, rundata);

        List[] columns = null;

        // retrieve the number of columns
        String cols = controller.getConfig().getInitParameter("cols");
        int colNum = 0;
        try
        {
            colNum = Integer.parseInt(cols);
        }
        catch (Exception e)
        {
            // not an integer or null, default to standarrd value
            colNum = 3;
        }
        context.put("colNum", String.valueOf(colNum));

        //retrieve the size for each of the columns
        String sizes = controller.getConfig().getInitParameter("sizes");
        context.put("sizes", getCellSizes(sizes));

        //retrieve the class for each of the columns
        String columnClasses = controller.getConfig().getInitParameter("col_classes");
        context.put("col_classes", getCellClasses(columnClasses));

        columns = (List[]) customizationState.getAttribute("customize-columns");
        PortletSet customizedSet = (PortletSet) jdata.getCustomized();
        Portlets set = jdata.getCustomizedProfile()
                            .getDocument()
                            .getPortletsById(customizedSet.getID());

        if ( logger.isDebugEnabled() ) {
            logger.debug("MultiCol: columns " + columns + " set " + set);
        }

        if ((columns != null) && (columns.length == colNum))
        {
            int eCount = 0;
            for (int i = 0; i < columns.length; i++)
            {
                eCount += columns[i].size();
            }

            if ( logger.isDebugEnabled() ) {
                logger.debug("MultiCol: eCount " + eCount + " setCount" + set.getEntryCount() + set.getPortletsCount());
            }
            if (eCount != set.getEntryCount() + set.getPortletsCount())
            {
                if ( logger.isDebugEnabled() ) {
                    logger.debug("MultiCol: rebuilding columns ");
                }
                columns = buildColumns(set, colNum);
            }

        }
        else
        {
            if ( logger.isDebugEnabled() ) {
                logger.debug("MultiCol: rebuilding columns ");
            }
            columns = buildColumns(set, colNum);
        }

        customizationState.setAttribute("customize-columns", columns);
        context.put("portlets", columns);

        Map titles = new HashMap();
        for (int col = 0; col < columns.length; col++)
        {
            for (int row = 0; row < columns[col].size(); row++)
            {
                IdentityElement identityElement = (IdentityElement) columns[col].get(row);
                MetaInfo metaInfo = identityElement.getMetaInfo();
                if ((metaInfo != null) && (metaInfo.getTitle() != null))
                {
                    titles.put(identityElement.getId(), metaInfo.getTitle());
                    continue;
                }

                if (identityElement instanceof Entry)
                {
                    Entry entry = (Entry) identityElement;
                    PortletEntry pentry = (PortletEntry) Registry.getEntry(Registry.PORTLET, entry.getParent());
                    if ((pentry != null) && (pentry.getTitle() != null))
                    {
                        titles.put(entry.getId(), pentry.getTitle());
                        continue;
                    }

                    titles.put(entry.getId(), entry.getParent());
                    continue;
                }

                if (identityElement instanceof Reference)
                {
                    titles.put(identityElement.getId(), Localization.getString(rundata, "CUSTOMIZER_REF_DEFAULTTITLE"));
                    continue;
                }

                // Let's make sure their is a title
                titles.put(identityElement.getId(), Localization.getString(rundata, "CUSTOMIZER_NOTITLESET"));
            }
        }

        context.put("titles", titles);
        context.put("action", "controllers.MultiColumnControllerAction");
    }

    /**
     * Cancel the current customizations.  If this was the last customization
     * on the stack, then return the user to the home page.
     */
    public void doCancel(RunData data, Context context)
    {
        // move one level back in customization
        ((JetspeedRunData) data).setCustomized(null);

        // if we are all done customization
        if (((JetspeedRunData) data).getCustomized() == null)
        {
            try
            {
                ActionLoader.getInstance().exec(data, "controls.EndCustomize");
            }
            catch (Exception e)
            {
                logger.error("Unable to load action controls.EndCustomize ", e);
            }
        }
    }

    public void doSave(RunData data, Context context)
    {
        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData) data).getPageSessionState();

        // update the changes made here to the profile being edited
        List[] columns = (List[]) customizationState.getAttribute("customize-columns");
        for (int col = 0; col < columns.length; col++)
        {
            for (int row = 0; row < columns[col].size(); row++)
            {
                setPosition((IdentityElement) columns[col].get(row), col, row);
            }
        }

        // move one level back in customization
        ((JetspeedRunData) data).setCustomized(null);

        // if we are all done customization
        if (((JetspeedRunData) data).getCustomized() == null)
        {
            // save the edit profile and make it current
            try
            {
                ((JetspeedRunData) data).getCustomizedProfile().store();

                // Because of the way references are stored in memory, we have to completely refresh
                // the profile after a references is removed (otherwise it will continue being displayed)
                String referencesRemoved = (String) customizationState.getAttribute(REFERENCES_REMOVED);
                if (referencesRemoved != null && referencesRemoved.equals("true"))
                {
                    PsmlManager.refresh(((JetspeedRunData) data).getCustomizedProfile());
                }                
            }
            catch (Exception e)
            {
                logger.error("Unable to save profile ", e);
            }

            try
            {
                ActionLoader.getInstance().exec(data, "controls.EndCustomize");
            }
            catch (Exception e)
            {
                logger.error("Unable to load action controls.EndCustomize ", e);
            }
        }
    }

    public void doDelete(RunData data, Context context)
    {
        JetspeedRunData jdata = (JetspeedRunData) data;

        // get the customization state for this page
        SessionState customizationState = jdata.getPageSessionState();

        PortletSet customizedSet = (PortletSet) jdata.getCustomized();

        customizationState.setAttribute(REFERENCES_REMOVED, "false");

        int col = data.getParameters().getInt("col", -1);
        int row = data.getParameters().getInt("row", -1);
        List[] columns = (List[]) customizationState.getAttribute("customize-columns");
        if (columns == null)
        {
            return;
        }

        if ((col > -1) && (row > -1))
        {
            try
            {
                IdentityElement identityElement = (IdentityElement) columns[col].get(row);
                columns[col].remove(row);

                Portlets portlets = jdata.getCustomizedProfile()
                                          .getDocument()
                                          .getPortletsById(customizedSet.getID());

                if (portlets != null)
                {
                    if (identityElement instanceof Entry)
                    {
                        for (int i = 0; i < portlets.getEntryCount(); i++)
                        {
                            if (portlets.getEntry(i) == identityElement)
                            {
                                portlets.removeEntry(i);
                            }
                        }
                    }
                    else if (identityElement instanceof Reference)
                    {
                        for (int i = 0; i < portlets.getReferenceCount(); i++)
                        {
                            if (portlets.getReference(i) == identityElement)
                            {
                                customizationState.setAttribute(REFERENCES_REMOVED, "true");
                                portlets.removeReference(i);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                // probably got wrong coordinates
                logger.error("MultiColumnControllerAction: Probably got wrong coordinates", e);
            }
        }
    }

    public void doLeft(RunData data, Context context)
    {
        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData) data).getPageSessionState();

        List[] columns = (List[]) customizationState.getAttribute("customize-columns");
        int col = data.getParameters().getInt("col", -1);
        int row = data.getParameters().getInt("row", -1);
        if (columns == null)
        {
            return;
        }

        if ((col > 0) && (row > -1))
        {
            move(columns, col, row, col - 1, row);
        }
    }

    public void doRight(RunData data, Context context)
    {
        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData) data).getPageSessionState();

        List[] columns = (List[]) customizationState.getAttribute("customize-columns");
        int col = data.getParameters().getInt("col", -1);
        int row = data.getParameters().getInt("row", -1);
        if (columns == null)
        {
            return;
        }

        if ((col > -1) && (row > -1) && (col < columns.length - 1))
        {
            move(columns, col, row, col + 1, row);
        }
    }

    public void doUp(RunData data, Context context)
    {
        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData) data).getPageSessionState();

        List[] columns = (List[]) customizationState.getAttribute("customize-columns");
        int col = data.getParameters().getInt("col", -1);
        int row = data.getParameters().getInt("row", -1);
        if (columns == null)
        {
            return;
        }

        if ((col > -1) && (row > 0))
        {
            move(columns, col, row, col, row - 1);
        }
    }

    public void doDown(RunData data, Context context)
    {
        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData) data).getPageSessionState();

        List[] columns = (List[]) customizationState.getAttribute("customize-columns");
        int col = data.getParameters().getInt("col", -1);
        int row = data.getParameters().getInt("row", -1);
        if (columns == null)
        {
            return;
        }

        if ((col > -1) && (row > -1) && (row < columns[col].size() - 1))
        {
            move(columns, col, row, col, row + 1);
        }
    }

    public void doControl(RunData data, Context context)
    {
        JetspeedRunData jdata = (JetspeedRunData) data;

        String controlName = data.getParameters().getString("control");
        String id = data.getParameters().getString("js_peid");

        try
        {
            Entry entry = jdata.getCustomizedProfile().getDocument().getEntryById(id);

            if (entry != null)
            {
                if (controlName != null && controlName.trim().length() > 0)
                {
                    PsmlControl control = new PsmlControl();
                    control.setName(controlName);
                    if (control != null)
                    {
                        entry.setControl(control);
                    }
                }
                else
                {
                    entry.setControl(null);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    protected static void setPosition(IdentityElement identityElement, int col, int row)
    {
        boolean colFound = false;
        boolean rowFound = false;

        if (identityElement != null)
        {
            Layout layout = identityElement.getLayout();

            if (layout == null)
            {
                layout = new PsmlLayout();
                identityElement.setLayout(layout);
            }

            for (int i = 0; i < layout.getParameterCount(); i++)
            {
                Parameter p = layout.getParameter(i);

                if (p.getName().equals("column"))
                {
                    p.setValue(String.valueOf(col));
                    colFound = true;
                }
                else if (p.getName().equals("row"))
                {
                    p.setValue(String.valueOf(row));
                    rowFound = true;
                }
            }

            if (!colFound)
            {
                Parameter p = new PsmlParameter();
                p.setName("column");
                p.setValue(String.valueOf(col));
                layout.addParameter(p);
            }

            if (!rowFound)
            {
                Parameter p = new PsmlParameter();
                p.setName("row");
                p.setValue(String.valueOf(row));
                layout.addParameter(p);
            }
        }
    }

    protected static void move(List[] cols, int oCol, int oRow, int nCol, int nRow)
    {
        Object obj = null;

        if ((oCol < cols.length) && (oRow < cols[oCol].size()))
        {
            obj = cols[oCol].get(oRow);
            if (obj != null)
            {
                cols[oCol].remove(oRow);
            }
        }

        if (obj != null)
        {
            if (nCol < cols.length)
            {
                if (nRow < cols[nCol].size())
                {
                    cols[nCol].add(nRow, obj);
                }
                else
                {
                    cols[nCol].add(obj);
                }
            }
        }
    }

    protected static List[] buildColumns(Portlets set, int colNum)
    {
        // normalize the constraints and calculate max num of rows needed
        Iterator iterator = set.getEntriesIterator();
        int row = 0;
        int col = 0;
        int rowNum = 0;
        while (iterator.hasNext())
        {
            IdentityElement identityElement = (IdentityElement) iterator.next();

            Layout layout = identityElement.getLayout();

            if (layout != null)
            {
                for (int p = 0; p < layout.getParameterCount(); p++)
                {
                    Parameter prop = layout.getParameter(p);

                    try
                    {
                        if (prop.getName().equals("row"))
                        {
                            row = Integer.parseInt(prop.getValue());
                            if (row > rowNum)
                            {
                                rowNum = row;
                            }
                        }
                        else if (prop.getName().equals("column"))
                        {
                            col = Integer.parseInt(prop.getValue());
                            if (col > colNum)
                            {
                                prop.setValue(String.valueOf(col % colNum));
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        //ignore any malformed layout properties
                    }
                }
            }
        }

        int sCount = set.getEntryCount() + set.getPortletsCount();
        row = (sCount / colNum) + 1;
        if (row > rowNum)
        {
            rowNum = row;
        }

        if ( logger.isDebugEnabled() ) {
            logger.debug("Controller customize colNum: " + colNum + " rowNum: " + rowNum);
        }
        // initialize the result position table and the work list
        List[] table = new List[colNum];
        List filler = Collections.nCopies(rowNum + 1, null);
        for (int i = 0; i < colNum; i++)
        {
            table[i] = new ArrayList();
            table[i].addAll(filler);
        }

        List work = new ArrayList();

        //position the constrained elements and keep a reference to the
        //others
        for (int i = 0; i < set.getEntryCount(); i++)
        {
            addElement(set.getEntry(i), table, work, colNum);
        }

        // Add references
        for (int i = 0; i < set.getReferenceCount(); i++)
        {
            addElement(set.getReference(i), table, work, colNum);
        }

        //insert the unconstrained elements in the table
        Iterator i = work.iterator();
        for (row = 0; row < rowNum; row++)
        {
            for (col = 0; i.hasNext() && (col < colNum); col++)
            {
                if (table[col].get(row) == null)
                {
                    if ( logger.isDebugEnabled() ) {
                        logger.debug("Set portlet at col " + col + " row " + row);
                    }
                    table[col].set(row, i.next());
                }
            }
        }

        // now cleanup any remaining null elements
        for (int j = 0; j < table.length; j++)
        {
            if ( logger.isDebugEnabled() ) {
                logger.debug("Column " + j);
            }
            i = table[j].iterator();
            while (i.hasNext())
            {
                Object obj = i.next();
                if ( logger.isDebugEnabled() ) {
                    logger.debug("Element " + obj);
                }
                if (obj == null)
                {
                    i.remove();
                }

            }
        }

        return table;
    }

    /** Parses the size config info and returns a list of
     *  size values for the current set
     *
     *  @param sizeList java.lang.String a comma separated string a values
     *  @return a List of values
     */
    protected static List getCellSizes(String sizeList)
    {
        List list = new Vector();

        if (sizeList != null)
        {
            StringTokenizer st = new StringTokenizer(sizeList, ",");
            while (st.hasMoreTokens())
            {
                list.add(st.nextToken());
            }
        }

        return list;
    }

    protected static List getCellClasses(String classlist)
    {
        List list = new Vector();

        if (classlist != null)
        {
            StringTokenizer st = new StringTokenizer(classlist, ",");
            while (st.hasMoreTokens())
            {
                list.add(st.nextToken());
            }
        }

        return list;
    }

    /**
     * Add an element to the "table" or "work" objects.  If the element is
     * unconstrained, and the position is within the number of columns, then
     * the element is added to "table".  Othewise the element is added to "work"
     *
     * @param element to add
     * @param table of positioned elements
     * @param work list of un-positioned elements
     * @param columnCount Number of colum
     */
    protected static void addElement(IdentityElement element, List[] table, List work, int columnCount)
    {
            Layout layout = element.getLayout();
            int row = -1;
            int col = -1;

            if (layout != null)
            {
                try
                {
                    for (int p = 0; p < layout.getParameterCount(); p++)
                    {
                        Parameter prop = layout.getParameter(p);

                        if (prop.getName().equals("row"))
                        {
                            row = Integer.parseInt(prop.getValue());
                        }
                        else if (prop.getName().equals("column"))
                        {
                            col = Integer.parseInt(prop.getValue());
                        }
                    }
                }
                catch (Exception e)
                {
                    //ignore any malformed layout properties
                }
            }

            if ( logger.isDebugEnabled() ) {
                logger.debug("Constraints col " + col + " row " + row);
            }
            if ((row >= 0) && (col >= 0) && (col < columnCount))
            {
                table[col].set(row, element);
            }
            else
            {
                if (layout != null)
                {
                    // We got here because the column, as defined in the layout,
                    // is greater then the numner of columns. This usually
                    // happens when the number of column has been decreased.
                    // Delete the offending layout.  It may be recreated with
                    // the correct values.
                    element.setLayout(null);
                    layout = null;
                }
                work.add(element);
            }

    }

    protected void dumpColumns(List[] cols)
    {
        for (int i = 0; i < cols.length; i++)
        {
            logger.debug("Column " + i);
            for (int j = 0; j < cols[i].size(); j++)
            {
                logger.debug("Row " + j + " object: " + cols[i].get(j));
            }
        }
    }
}
