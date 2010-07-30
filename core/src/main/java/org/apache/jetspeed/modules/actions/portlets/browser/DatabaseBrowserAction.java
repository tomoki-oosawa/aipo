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

package org.apache.jetspeed.modules.actions.portlets.browser;

// velocity
import org.apache.velocity.context.Context;

// Java Stuff
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.StringTokenizer;

// turbine util
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;

import org.apache.torque.Torque;

// jetspeed services
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

// jetspeed velocity
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;
import org.apache.jetspeed.modules.actions.portlets.browser.ActionParameter;
import org.apache.jetspeed.modules.actions.portlets.browser.BrowserQuery;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.portal.portlets.browser.DatabaseBrowserIterator;
import org.apache.jetspeed.portal.portlets.browser.BrowserIterator;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.PortletConfigState;



/**
 * This action sets up the template context for retrieving paged data from the resultSet
 * according to the quey speciified by the user.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: DatabaseBrowserAction.java,v 1.34 2004/02/23 02:51:19 jford Exp $
 */
public class DatabaseBrowserAction extends VelocityPortletAction implements BrowserQuery
{
    protected static final String SQL = "sql";
    protected static final String POOLNAME = "poolname";
    protected static final String START = "start";
    protected static final String CUSTOMIZE_TEMPLATE = "customizeTemplate";
    protected static final String WINDOW_SIZE = "windowSize";

    protected static final String USER_OBJECT_NAMES = "user-object-names";
    protected static final String USER_OBJECT_TYPES = "user-object-types";
    protected static final String USER_OBJECTS = "user-objects";

    protected static final String SQL_PARAM_PREFIX = "sqlparam";

    protected static final String LINKS_READ = "linksRead";
    protected static final String ROW_LINK = "rowLinks";
    protected static final String TABLE_LINK = "tableLinks";
    protected static final String ROW_LINK_IDS = "row-link-ids";
    protected static final String ROW_LINK_TYPES = "row-link-types";
    protected static final String ROW_LINK_TARGETS = "row-link-targets";
    protected static final String TABLE_LINK_IDS = "table-link-ids";
    protected static final String TABLE_LINK_TYPES = "table-link-types";
    protected static final String TABLE_LINK_TARGETS = "table-link-targets";
    protected static final String BROWSER_TABLE_SIZE = "tableSize";
    protected static final String DATABASE_BROWSER_ACTION_KEY = "database_browser_action_key";
    protected static final String BROWSER_ITERATOR = "table";
    protected static final String BROWSER_TITLE_ITERATOR = "title";
    protected static final String NEXT = "next";
    protected static final String PREVIOUS = "prev";
    protected static final String VELOCITY_NULL_ENTRY = "-";
    // portlet entry Id
    protected static final String PEID = "js_peid";
    protected static final String SORT_COLUMN_NAME = "js_dbcolumn";

    protected List sqlParameters = new Vector();

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DatabaseBrowserAction.class.getName());     
    
    /**
     * Build the maximized state content for this portlet. (Same as normal state).
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildMaximizedContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {
        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Subclasses should override this method if they wish to
     * provide their own customization behavior.
     * Default is to use Portal base customizer action
     */
    protected void buildConfigureContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {
        try
        {
            super.buildConfigureContext( portlet, context, rundata);
        }
        catch (Exception ex)
        {
            logger.error("Exception", ex);
        }
        context.put(SQL, getQueryString(portlet, rundata, context));
        context.put(WINDOW_SIZE, getParameterUsingFallback(portlet, rundata, WINDOW_SIZE, null));
        setTemplate(rundata, getParameterUsingFallback(portlet, rundata, CUSTOMIZE_TEMPLATE, null));
    }

    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext( VelocityPortlet portlet,
                                       Context context,
                                       RunData rundata )
    {
        int resultSetSize, next, prev, windowSize;

        BrowserIterator iterator = getDatabaseBrowserIterator(portlet, rundata);
        String sortColName = getRequestParameter(portlet, rundata, SORT_COLUMN_NAME);
        int start = getStartVariable(portlet, rundata, START, sortColName, iterator);

        windowSize = Integer.parseInt((String)getParameterUsingFallback(portlet, rundata, WINDOW_SIZE, "10"));
        next = start + windowSize;
        prev = start - windowSize;

        try
        {
            if(iterator == null || PortletSessionState.getPortletConfigChanged(portlet, rundata))
            {
                String sql = getQueryString(portlet, rundata, context);
                //System.out.println("buildNormalContext SQL: "+sql);
                if(sql != null )
                {
                    readUserParameters(portlet,rundata,context);
                    getRows(portlet, rundata, sql, windowSize);
                    iterator = getDatabaseBrowserIterator(portlet, rundata);
                }
                else
                {
                    logger.info("The sql query is null, hence not generating the result set.");
                }
            }
            else
            {
                if(sortColName != null)
                {
                    iterator.sort(sortColName);
                }
                iterator.setTop(start);
            }

            readLinkParameters(portlet, rundata, context);

            if(iterator != null)
            {
                resultSetSize = iterator.getResultSetSize();

                if(next < resultSetSize)
                {
                    context.put(NEXT, String.valueOf(next));
                }
                if(prev <= resultSetSize && prev >=0 )
                {
                    context.put(PREVIOUS, String.valueOf(prev));
                }

                context.put(BROWSER_ITERATOR, iterator);
                context.put(BROWSER_TITLE_ITERATOR, iterator.getResultSetTitleList());
                context.put(BROWSER_TABLE_SIZE, new Integer(resultSetSize));

                /*
                System.out.println("buildNormalContext Sort column name= "+sortColName);
                System.out.println("buildNormalContext Iterator: "+iterator);
                System.out.println("buildNormalContext Titles= "+iterator.getResultSetTitleList());
                System.out.println("buildNormalContext windowSize="+windowSize+" prev="+prev+
                                   " next="+next+" start="+start+" resultSetSize="+resultSetSize);
                */
            }

        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Exception", e);

            rundata.setMessage("Error in Jetspeed Database Browser: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

    /**
     * This method is called when the user configures any of the parameters.
     * @param data The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doUpdate(RunData rundata, Context context)
    {
        VelocityPortlet portlet = (VelocityPortlet)context.get("portlet");
        String sql = getRequestParameter(portlet, rundata, SQL);
        String pageSize = getRequestParameter(portlet, rundata, WINDOW_SIZE);

        if (sql!=null)
        {
            setParameterToPSML( portlet, rundata, SQL, sql);
            context.put(SQL, sql);
            clearDatabaseBrowserIterator(portlet, rundata);

        }
        if(pageSize!=null)
        {
            setParameterToPSML( portlet, rundata, WINDOW_SIZE, pageSize);
            context.put(WINDOW_SIZE, pageSize);

        }
        /*
        System.out.println("doUpdate SQL: "+sql+", Window Size: "+pageSize);
        */
        buildNormalContext( portlet, context, rundata);
    }

    /**
     * This method is called when the user hits refresh to refetch the result set.
     * @param data The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doRefresh(RunData rundata, Context context)
    {
        VelocityPortlet portlet = (VelocityPortlet)context.get("portlet");
        if(isMyRequest(portlet,rundata))
        {
            clearDatabaseBrowserIterator(portlet, rundata);
        }
        buildNormalContext(portlet, context, rundata);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.modules.actions.portlets.browser.BrowserQuery#filter(java.util.List, RunData)
     */
    public boolean filter(List row, RunData rundata)
    {
        return false;
    }

    /**
     * Execute the sql statement as specified by the user or the default, and store the
     * resultSet in a vector.
     *
     * @param sql The sql statement to be executed.
     * @param data The turbine rundata context for this request.
     */
    protected void getRows(VelocityPortlet portlet, RunData rundata, String sql,
                           int windowSize) throws Exception
    {
        List resultSetList = new ArrayList();
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        Connection con = null;
        PreparedStatement selectStmt = null;
        ResultSet rs = null;
        try
        {
            String poolname = getParameterUsingFallback(portlet,rundata,POOLNAME,null);
            if (poolname==null || poolname.length()==0)
            {
                con = Torque.getConnection();
            }
            else
            {
                con = Torque.getConnection(poolname);
            }
            selectStmt = con.prepareStatement(sql);

            readSqlParameters(portlet, rundata);
            Iterator it = sqlParameters.iterator();
            int ix = 0;
            while (it.hasNext())
            {
                ix++;
                Object object = it.next();
                selectStmt.setObject(ix, object);
            }
            rs = selectStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNum = rsmd.getColumnCount();
            /*
            get the user object types to be displayed and add them to the
            title list as well as the result set list
            */
            List userObjList = (List)getParameterFromTemp(portlet, rundata, USER_OBJECTS);
            int userObjListSize = 0;
            if (userObjList != null)
            {
                userObjListSize = userObjList.size();
            }
            //System.out.println("User List Size = "+ userObjListSize);
            /*
            the array columnDisplayed maintains a boolean value for each
            column index. Only the columns that are set to true are added to
            the resultSetList, resultSetTitleList and resultSetTypeList.
            */
            boolean[] columnDisplayed = new boolean [columnNum + userObjListSize];

            /*
            this for loop constructs the columnDisplayed array as well as adds
            to the resultSetTitleList and resultSetTypeList
            */
            for(int i = 1; i <= columnNum; i++)
            {
                int type = rsmd.getColumnType(i);
                if( !((type == Types.BLOB) || (type == Types.CLOB) ||
                      (type == Types.BINARY) || (type == Types.LONGVARBINARY) ||
                      (type == Types.VARBINARY)) )
                {
                    resultSetTitleList.add(rsmd.getColumnName(i));
                    resultSetTypeList.add(String.valueOf(type));
                    columnDisplayed[i-1] = true;
                }
                else
                {
                    columnDisplayed[i-1] = false;
                }
            }

            for (int i = columnNum; i < columnNum + userObjListSize; i++)
            {
                ActionParameter usrObj = (ActionParameter)userObjList.get(i - columnNum);
                resultSetTitleList.add(usrObj.getName());
                resultSetTypeList.add(usrObj.getType());
                columnDisplayed[i] = true;
                //System.out.println("User List Name = "+ usrObj.getName()+" Type = "+usrObj.getType());
            }
            /*
            this while loop adds each row to the resultSetList
            */
            int index = 0;
            while(rs.next())
            {
                List row = new ArrayList(columnNum);

                for(int i = 1; i <= columnNum; i++)
                {
                    if( columnDisplayed[i-1] )
                    {
                        Object obj = rs.getObject(i);
                        if (obj == null)
                        {
                            obj = VELOCITY_NULL_ENTRY;
                        }
                        row.add(obj);
                    }
                }
                for (int i = columnNum; i < columnNum + userObjListSize; i++)
                {
                    ActionParameter usrObj = (ActionParameter)userObjList.get(i - columnNum);
                    if( columnDisplayed[i] )
                    {
                        Class c = Class.forName(usrObj.getType());
                        row.add(c.newInstance());
                        populate(index, i, row);
                    }
                }
                
                if (filter(row, rundata))
                {
                    continue;
                }

                resultSetList.add(row);
                index++;
            }
            BrowserIterator iterator =
                new DatabaseBrowserIterator( resultSetList, resultSetTitleList,
                                             resultSetTypeList, windowSize);
            setDatabaseBrowserIterator(portlet, rundata, iterator);

        }
        catch (SQLException e)
        {
            throw e;
        }
        finally
        {
            try
            {
                if (null != selectStmt)
                    selectStmt.close();
                if (null != rs)
                    rs.close();
                if (null != con) //closes con also
                    Torque.closeConnection(con);

            }
            catch (Exception e)
            {
                throw e;
            }
        }

    }

    /**
     * Centralizes the calls to runData.getUser.getTemp() - to retrieve
     * the DatabaseBrowserIterator.
     *
     * @param data The turbine rundata context for this request.
     *
     */
    protected BrowserIterator getDatabaseBrowserIterator(VelocityPortlet portlet,
                                                         RunData rundata)
    {


        BrowserIterator iterator =
            (BrowserIterator)getParameterFromTemp(portlet, rundata, DATABASE_BROWSER_ACTION_KEY);
        return iterator;
    }

    /**
     * Centralizes the calls to runData.getUser.setTemp() - to set
     * the DatabaseBrowserIterator.
     *
     * @param data The turbine rundata context for this request.
     * @param iterator.
     *
     */
    protected void setDatabaseBrowserIterator(VelocityPortlet portlet,
                                              RunData rundata,
                                              BrowserIterator iterator)
    {
        setParameterToTemp(portlet, rundata, DATABASE_BROWSER_ACTION_KEY, iterator);
    }

    /**
     * Centralizes the calls to runData.getUser.removeTemp() - to clear
     * the DatabaseBrowserIterator from the temp storage.
     *
     * @param data The turbine rundata context for this request.
     *
     */
    protected void clearDatabaseBrowserIterator(VelocityPortlet portlet, RunData rundata)
    {
        clearParameterFromTemp(portlet, rundata, DATABASE_BROWSER_ACTION_KEY);
    }

    /**
     * This method returns the query to be executed to get the results which will
     * be opened in the browser.
     *
     */
    public String getQueryString(RunData rundata, Context context)
    {
        return null;
    }
    /**
     * This method returns the sql from the getQuery method which can be
     * overwritten according to the needs of the application. If the getQuery()
     * returns null, then it gets the value from the psml file. If the psml value is null
     * then it returns the value from the xreg file.
     *
     */
    protected String getQueryString(VelocityPortlet portlet, RunData rundata, Context context)
    {
        String sql = getQueryString(rundata, context);
        if( sql==null )
        {
            sql = getParameterUsingFallback(portlet, rundata, SQL, null);
        }
        return sql;
    }
    /**
     *
     */
    protected void clearQueryString(VelocityPortlet portlet, RunData rundata)
    {
        clearParameterFromPSML(portlet, rundata, SQL);
    }
    /**
     * to be used if sorting behavior to be overwritten
     */
    protected int getStartIndex()
    {
        return 0;
    }
    /**
     *
     */
    protected String getParameterUsingFallback(VelocityPortlet portlet, RunData rundata,
                                             String attrName, String attrDefValue)
    {
        return PortletConfigState.getParameter(portlet, rundata, attrName, attrDefValue);
    }
    /**
     *
     */
    protected void clearParameterFromPSML(VelocityPortlet portlet, RunData rundata,
                                        String attributeName)
    {
        PortletConfigState.clearInstanceParameter(portlet, rundata, attributeName);
    }
    /**
     *
     */
    protected void setParameterToPSML(VelocityPortlet portlet, RunData rundata,
                                    String attrName, String attrValue)
    {
        PortletConfigState.setInstanceParameter(portlet, rundata, attrName, attrValue);
    }

    /**
     *
     */
    protected String getParameterFromPSML(VelocityPortlet portlet, RunData rundata,
                                        String attrName, String attrDefValue)
    {
        return PortletConfigState.getInstanceParameter(portlet, rundata, attrName);

    }

    protected String getParameterFromRegistry(VelocityPortlet portlet,
                                            String attrName,
                                            String attrDefValue)
    {
        return PortletConfigState.getConfigParameter(portlet, attrName, attrDefValue);

    }

    /**
     *
     */
    protected Object getParameterFromTemp(VelocityPortlet portlet, RunData rundata, String attrName)
    {
        return PortletSessionState.getAttribute(portlet, rundata, attrName);
    }
    /**
     *
     */
    protected void setParameterToTemp(VelocityPortlet portlet, RunData rundata,
                                      String attrName, Object attrValue)
    {
        PortletSessionState.setAttribute(portlet, rundata, attrName, attrValue);
    }
    /**
     *
     */
    protected void clearParameterFromTemp(VelocityPortlet portlet, RunData rundata,
                                          String attrName)
    {
        PortletSessionState.clearAttribute(portlet, rundata, attrName);
    }

    /**
     *
     */
    protected boolean isMyRequest(VelocityPortlet portlet, RunData rundata)
    {
        String peId = portlet.getID();

        if(peId != null && peId.equals(rundata.getParameters().getString(PEID)))
            return true;
        else
            return false;
    }
    /**
     *
     */
    protected String getRequestParameter(VelocityPortlet portlet, RunData rundata,
                                               String attrName)
    {
        if(isMyRequest(portlet, rundata))
            return rundata.getParameters().getString(attrName);
        else
            return null;
    }
    /**
     *
     */
    protected int getStartVariable(VelocityPortlet portlet, RunData rundata,
                                 String attrName, String sortColName,
                                 BrowserIterator iterator)
    {
        int start = -1;
        // if users want to overwrite how the sorting affects the cursor for
        // the window
        if( sortColName != null ) start = getStartIndex();

        if( start < 0 )
        {
            //fallback routine for start
            String startStr = getRequestParameter(portlet, rundata, attrName);
            if(startStr != null && startStr.length() > 0)
            {
                start = Integer.parseInt(startStr);
            }
            else if( start == -1 && iterator != null )
            {
                start = iterator.getTop();
            }

            if( start < 0 ) start = 0;
        }
        return start;

    }

    public void setSQLParameters(List parameters)
    {
        this.sqlParameters = parameters;
    }

    public List getSQLParameters()
    {
        return sqlParameters;
    }

    protected void readSqlParameters(VelocityPortlet portlet, RunData rundata)
    {
        List sqlParamList = null;

        int i = 1;
        while (true)
        {
            String param = getParameterUsingFallback(portlet, rundata, SQL_PARAM_PREFIX + i, null);
            if (param == null)
            {
                break;
            }
            else
            {
                if (sqlParamList == null)
                {
                    sqlParamList = new ArrayList();
                }
                sqlParamList.add(param);
            }
            i++;
        }

        if (sqlParamList != null)
        {
            setSQLParameters(sqlParamList);
        }

    }

    protected void readUserParameters(VelocityPortlet portlet, RunData rundata, Context context)
    {
        List userObjectList;
        Object userObjRead = getParameterFromTemp(portlet, rundata, USER_OBJECTS);
        if ( userObjRead != null)
        {
            context.put(USER_OBJECTS, (List)userObjRead);
            //System.out.println("userObjectListSize: "+ ((List)userObjRead).size());
        }
        else
        {
            String userObjTypes= getParameterFromRegistry(portlet,USER_OBJECT_TYPES,null);
            String userObjNames= getParameterFromRegistry(portlet,USER_OBJECT_NAMES,null);
            if( userObjTypes != null && userObjTypes.length() > 0 )
            {
                userObjectList = new ArrayList();
                int userObjectIndex = 0;
                StringTokenizer tokenizer1 = new StringTokenizer(userObjNames, ",");
                StringTokenizer tokenizer3 = new StringTokenizer(userObjTypes, ",");
                while(tokenizer1.hasMoreTokens() && tokenizer3.hasMoreTokens())
                {
                    userObjectList.add(userObjectIndex,
                                       new ActionParameter(tokenizer1.nextToken(), null, tokenizer3.nextToken()));
                    userObjectIndex++;
                }
                context.put(USER_OBJECTS, userObjectList);
                setParameterToTemp(portlet, rundata, USER_OBJECTS, userObjectList);
                //System.out.println("readLink: userObjectTypesListSize: "+userObjectList.size());
            }
        }
    }

    protected void readLinkParameters(VelocityPortlet portlet, RunData rundata, Context context)
    {
        List rowList, tableList;
        Object linksRead = getParameterFromTemp(portlet, rundata, LINKS_READ);
        if(linksRead != null && ((String)linksRead).equals(LINKS_READ))
        {
            Object tmp = getParameterFromTemp(portlet, rundata, ROW_LINK);
            if(tmp != null)
            {
                context.put(ROW_LINK, (List)tmp);
                //System.out.println("rowListSize"+ ((List)tmp).size());
            }
            tmp = getParameterFromTemp(portlet, rundata, TABLE_LINK);
            if(tmp != null)
            {
                context.put(TABLE_LINK, (List)tmp);
                //System.out.println("tableListSize: "+((List)tmp).size());
            }
        }
        else
        {
            String rowLinkIds= getParameterFromRegistry(portlet,ROW_LINK_IDS,null);
            String rowLinkClasses= getParameterFromRegistry(portlet,ROW_LINK_TARGETS,null);
            String rowLinkTypes= getParameterFromRegistry(portlet,ROW_LINK_TYPES,null);
            if( rowLinkIds != null && rowLinkIds.length() > 0 )
            {
                rowList = new ArrayList();
                int rowIndex = 0;
                StringTokenizer tokenizer1 = new StringTokenizer(rowLinkIds, ",");
                StringTokenizer tokenizer2 = new StringTokenizer(rowLinkClasses, ",");
                StringTokenizer tokenizer3 = new StringTokenizer(rowLinkTypes, ",");
                while(tokenizer1.hasMoreTokens() && tokenizer2.hasMoreTokens() && tokenizer3.hasMoreTokens())
                {
                    rowList.add(rowIndex,
                                new ActionParameter(tokenizer1.nextToken(), tokenizer2.nextToken(), tokenizer3.nextToken()));
                    rowIndex++;
                }
                context.put(ROW_LINK, rowList);
                setParameterToTemp(portlet, rundata, ROW_LINK, rowList);
                //System.out.println("readLink: rowListSize"+rowList.size());
            }

            String tableLinkIds= getParameterFromRegistry(portlet,TABLE_LINK_IDS,null);
            String tableLinkClasses= getParameterFromRegistry(portlet,TABLE_LINK_TARGETS,null);
            String tableLinkTypes= getParameterFromRegistry(portlet,TABLE_LINK_TYPES,null);
            if( tableLinkIds != null && tableLinkIds.length() > 0 )
            {
                tableList = new ArrayList();
                int tableIndex = 0;
                StringTokenizer tokenizer1 = new StringTokenizer(tableLinkIds, ",");
                StringTokenizer tokenizer2 = new StringTokenizer(tableLinkClasses, ",");
                StringTokenizer tokenizer3 = new StringTokenizer(tableLinkTypes, ",");
                while(tokenizer1.hasMoreTokens() && tokenizer2.hasMoreTokens() && tokenizer3.hasMoreTokens())
                {
                    tableList.add(tableIndex,
                                  new ActionParameter(tokenizer1.nextToken(), tokenizer2.nextToken(), tokenizer3.nextToken()));
                    tableIndex++;
                }
                context.put(TABLE_LINK, tableList);
                setParameterToTemp(portlet, rundata, TABLE_LINK, tableList);
                //System.out.println("readLink: tableListSize: "+tableList.size());
            }
            setParameterToTemp(portlet, rundata, LINKS_READ, LINKS_READ);
        }

    }

    /**
     * This method should be overwritten every time the user object needs to be
     * populated with some user specific constraints. As an example if the user wanted
     * to track the parent of an object based on some calculation per row, it could be
     * done here.
     *
     */
    public void populate(int rowIndex, int columnIndex, List row)
    {
    }


}
