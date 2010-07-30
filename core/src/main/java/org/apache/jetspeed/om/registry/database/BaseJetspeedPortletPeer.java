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
package org.apache.jetspeed.om.registry.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.registry.DBRegistry;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.base.BaseCategory;
import org.apache.jetspeed.om.registry.base.BaseContentURL;
import org.apache.jetspeed.om.registry.base.BaseMediaType;
import org.apache.jetspeed.om.registry.base.BaseMetaInfo;
import org.apache.jetspeed.om.registry.base.BasePortletEntry;
import org.apache.jetspeed.om.registry.base.BaseSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.QueryDataSet;
import com.workingdogs.village.Record;

/**
 * Base Peer for Portlet Parameter registry entries.
 * 
 * @author <a href="mailto:susinha@cisco.com">Suchisubhra Sinha</a>
 * @version $Id: BaseJetspeedPortletPeer.java,v 1.3 2004/04/06 23:00:16 morciuch Exp $
 */
public class BaseJetspeedPortletPeer extends BasePeer implements DBRegistry
{
	
	/**
	 * Static initialization of the logger for this class
	 */    
	protected static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BaseJetspeedPortletPeer.class.getName());      
	
    /** the table name for this class */
    public static final String TABLE_NAME = "PORTLET";
    /** the column name for the PORTAL_ID field */
    public static final String PORTAL_ID;
    /** the column name for the NAME field */
    public static final String NAME;
    /** the column name for the HIDDEN field */
    public static final String HIDDEN;
    /** the column name for the CLASSNAME field */
    public static final String CLASSNAME;
    /** the column name for the TYPE field */
    public static final String TYPE;
    /** the column name for the APPLICATION field */
    public static final String APPLICATION;
    /** the column name for the PARENT field */
    public static final String PARENT;
    /** the column name for the URL field */
    public static final String URL;
    /** the column name for the CACHEDONURL field */
    public static final String CACHEDONURL;
    /** the column name for the ROLE field */
    public static final String ROLE;
    /** the column name for the TITLE field */
    public static final String TITLE;
    /** the column name for the DESCRIPTION field */
    public static final String DESCRIPTION;
    /** the column name for the IMAGE field */
    public static final String IMAGE;
    /** the column name for the SECURITY field */
    public static final String SECURITY;
    /** the column name for the  field */
    public static final String LASTUPDATE;
    static {
        PORTAL_ID = "PORTLET.ID";
        NAME = "PORTLET.NAME";
        HIDDEN = "PORTLET.HIDDEN";
        CLASSNAME = "PORTLET.CLASSNAME";
        TYPE = "PORTLET.TYPE";
        APPLICATION = "PORTLET.APPLICATION";
        PARENT = "PORTLET.PARENT";
        URL = "PORTLET.URL";
        CACHEDONURL = "PORTLET.CACHEDONURL";
        ROLE = "PORTLET.ROLE";
        TITLE = "PORTLET.TITLE";
        DESCRIPTION = "PORTLET.DESCRIPTION";
        IMAGE = "PORTLET.IMAGE";
        SECURITY = "PORTLET.SECURITY";
        LASTUPDATE = "PORTLET.LASTUPDATE";
        /*        
           if (Torque.isInit())
           {
               try
               {
                   getMapBuilder();
               }
               catch (Exception e)
               {
                      
                   category.error("Could not initialize Peer", e);
               }
           }
        */
    }
    /** number of columns for this peer */
    public static final int numColumns = 14;
    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT =
        "org.apache.jetspeed.om.registry.base.BasePortletEntry";
    /** A class that can be returned by this peer. */
    protected static final Class CLASS_DEFAULT = initClass(CLASSNAME_DEFAULT);
    /**
     * Class object initialization method.
     *
     * @param className name of the class to initialize
     * @return the initialized class
     */
    private static Class initClass(String className)
    {
        Class c = null;
        try
        {
            c = Class.forName(className);
        }
        catch (Throwable t)
        {
            logger.error(
                "A FATAL ERROR has occurred which should not "
                    + "have happened under any circumstance.  Please notify "
                    + "the Turbine developers <turbine-dev@jakarta.apache.org> "
                    + "and give as many details as possible (including the error "
                    + "stack trace).",
                t);
            // Error objects should always be propogated.
            if (t instanceof Error)
            {
                throw (Error) t.fillInStackTrace();
            }
        }
        return c;
    }
    /**
        * Get the list of objects for a ResultSet.  Please not that your
        * resultset MUST return columns in the right order.  You can use
        * getFieldNames() in BaseObject to get the correct sequence.
        *
        * @param results the ResultSet
        * @return the list of objects
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public static List resultSet2Objects(java.sql.ResultSet results)
        throws TorqueException
    {
        try
        {
            QueryDataSet qds = null;
            List rows = null;
            try
            {
                qds = new QueryDataSet(results);
                rows = getSelectResults(qds);
            }
            finally
            {
                if (qds != null)
                {
                    qds.close();
                }
            }
            return populateObjects(rows);
        }
        catch (SQLException e)
        {
            throw new TorqueException(e);
        }
        catch (DataSetException e)
        {
            throw new TorqueException(e);
        }
    }
    /**
        * Add all the columns needed to create a new object.
        *
        * @param criteria object containing the columns to add.
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public static void addSelectColumns(Criteria criteria)
        throws TorqueException
    {
        criteria.addSelectColumn(PORTAL_ID);
        criteria.addSelectColumn(NAME);
        criteria.addSelectColumn(HIDDEN);
        criteria.addSelectColumn(CLASSNAME);
        criteria.addSelectColumn(TYPE);
        criteria.addSelectColumn(APPLICATION);
        criteria.addSelectColumn(PARENT);
        criteria.addSelectColumn(URL);
        criteria.addSelectColumn(CACHEDONURL);
        criteria.addSelectColumn(ROLE);
        criteria.addSelectColumn(TITLE);
        criteria.addSelectColumn(DESCRIPTION);
        criteria.addSelectColumn(IMAGE);
        criteria.addSelectColumn(SECURITY);
    }
    /**
         * Create a new object of type cls from a resultset row starting
         * from a specified offset.  This is done so that you can select
         * other rows than just those needed for this object.  You may
         * for example want to create two objects from the same row.
         *
         * @throws TorqueException Any exceptions caught during processing will be
         *         rethrown wrapped into a TorqueException.
         */
    public static BasePortletEntry row2Object(
        Record row,
        int offset,
        Class cls)
        throws TorqueException
    {
        try
        {
            BasePortletEntry obj = (BasePortletEntry) cls.newInstance();
            populateObject(row, offset, obj);
            //obj.setModified(false);
            //obj.setNew(false);
            return obj;
        }
        catch (InstantiationException e)
        {
            throw new TorqueException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new TorqueException(e);
        }
    }
    /**
     * Populates an object from a resultset row starting
     * from a specified offset.  This is done so that you can select
     * other rows than just those needed for this object.  You may
     * for example want to create two objects from the same row.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static void populateObject(
        Record row,
        int offset,
        BasePortletEntry obj)
        throws TorqueException
    {
        try
        {
            int id = row.getValue(offset + 0).asInt();
            obj.setName(row.getValue(offset + 1).asString());
            obj.setHidden(row.getValue(offset + 2).asBoolean());
            obj.setClassname(row.getValue(offset + 3).asString());
            obj.setType(row.getValue(offset + 4).asString());
            obj.setApplication(row.getValue(offset + 5).asBoolean());
            obj.setParent(row.getValue(offset + 6).asString());
            obj.setURL(row.getValue(offset + 7).asString());
            obj.setCachedOnURL(row.getValue(offset + 8).asBoolean());
            //set  the security
            BaseSecurity security =
                new BaseSecurity(row.getValue(offset + 9).asString());
            obj.setSecurity(security);
            obj.setBaseSecurity(security);
            //create the content URL
            BaseContentURL baseContentURL = new BaseContentURL();
            baseContentURL.setCachedOnURL(row.getValue(offset + 8).asBoolean());
            baseContentURL.setURL(row.getValue(offset + 7).asString());
            obj.setContentURL(baseContentURL);
            //create meta info
            BaseMetaInfo baseMetaInfo =
                new BaseMetaInfo(
                    row.getValue(offset + 10).asString(),
                    row.getValue(offset + 11).asString(),
                    row.getValue(offset + 12).asString());
            obj.setMetaInfo(baseMetaInfo);
            String securityRef = row.getValue(offset + 13).asString();
            if ((securityRef != null) || ("".equals(securityRef)))
            {
                BaseSecurityReference paramSecurityRef =
                    new BaseSecurityReference();
                paramSecurityRef.setParent(securityRef);
                obj.setSecurityRef(paramSecurityRef);
            }
            buildPortletCategory(id, obj);
            buildPortletMedia(id, obj);
            buildPortletParameters(id, obj);
        }
        catch (DataSetException e)
        {
            throw new TorqueException(e);
        }
    }
    /**
        * Method to get  regsitered data from database.
        *
        * @param criteria object used to create the SELECT statement.
        * @return List of selected Objects
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public List getXREGDataFromDb() throws TorqueException
    {
        Criteria criteria = buildCriteria();
        return doSelect(criteria);
    }
    public boolean isModified(String lastUpdateDate)
    {
        return true;
    }
    /**
     * Method to do selects.
     *
     * @param criteria object used to create the SELECT statement.
     * @return List of selected Objects
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List doSelect(Criteria criteria) throws TorqueException
    {
        return populateObjects(doSelectVillageRecords(criteria));
    }
    /**
        * Method to do selects within a transaction.
        *
        * @param criteria object used to create the SELECT statement.
        * @param con the connection to use
        * @return List of selected Objects
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public static List doSelect(Criteria criteria, Connection con)
        throws TorqueException
    {
        return populateObjects(doSelectVillageRecords(criteria, con));
    }
    /**
       * Grabs the raw Village records to be formed into objects.
       * This method handles connections internally.  The Record objects
       * returned by this method should be considered readonly.  Do not
       * alter the data and call save(), your results may vary, but are
       * certainly likely to result in hard to track MT bugs.
       *
       * @throws TorqueException Any exceptions caught during processing will be
       *         rethrown wrapped into a TorqueException.
       */
    public static List doSelectVillageRecords(Criteria criteria)
        throws TorqueException
    {
        return BaseJetspeedPortletPeer.doSelectVillageRecords(
            criteria,
            (Connection) null);
    }
    /**
    * Grabs the raw Village records to be formed into objects.
     * This method should be used for transactions
     *
     * @param con the connection to use
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List doSelectVillageRecords(
        Criteria criteria,
        Connection con)
        throws TorqueException
    {
        if (criteria.getSelectColumns().size() == 0)
        {
            addSelectColumns(criteria);
        }
        // Set the correct dbName if it has not been overridden
        // criteria.getDbName will return the same object if not set to
        // another value so == check is okay and faster
        if (criteria.getDbName() == Torque.getDefaultDB())
        {
            criteria.setDbName(DATABASE_NAME);
        }
        // BasePeer returns a List of Value (Village) arrays.  The array
        // order follows the order columns were placed in the Select clause.
        if (con == null)
        {
            return BasePeer.doSelect(criteria);
        }
        else
        {
            return BasePeer.doSelect(criteria, con);
        }
    }
    /**
     * The returned List will contain objects of the default type or
     * objects that inherit from the default.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List populateObjects(List records) throws TorqueException
    {
        List results = new ArrayList(records.size());
        // populate the object(s)
        for (int i = 0; i < records.size(); i++)
        {
            Record row = (Record) records.get(i);
            results.add(
                BaseJetspeedPortletPeer.row2Object(
                    row,
                    1,
                    BaseJetspeedPortletPeer.getOMClass()));
        }
        return results;
    }
    /** Build a Criteria object from an ObjectKey */
    public static Criteria buildCriteria(ObjectKey pk)
    {
        Criteria criteria = new Criteria();
        criteria.add(PORTAL_ID, pk);
        return criteria;
    }
    /** Build a Criteria object  */
    public static Criteria buildCriteria()
    {
        Criteria criteria = new Criteria();
        return criteria;
    }
    /**
     * The class that the Peer will make instances of.
     * If the BO is abstract then you must implement this method
     * in the BO.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static Class getOMClass() throws TorqueException
    {
        return CLASS_DEFAULT;
    }
    /**
     * it will make the parameters  for this portlet
     * @param portlet object.
     * 
     */
    public static void buildPortletParameters(int id, BasePortletEntry obj)
        throws TorqueException
    {
        try
        {
            List list =
                BaseJetspeedPortletParameterPeer.retrieveById(
                    SimpleKey.keyFor(id));
            for (int i = 0; i < list.size(); i++)
            {
                Parameter p = (Parameter) list.get(i);
                obj.addParameter(p);
            }
        }
        catch (Exception e)
        {
            throw new TorqueException(e);
        }
    }
    /**
         * it will make the media  types  for this portlet
         * @param portlet object.
         * 
         */
    public static void buildPortletMedia(int id, BasePortletEntry obj)
        throws TorqueException
    {
        try
        {
            List list =
                BaseJetspeedPortletMediaTypePeer.retrieveById(
                    SimpleKey.keyFor(id));
            for (int i = 0; i < list.size(); i++)
            {
                BaseMediaType p = (BaseMediaType) list.get(i);
                String mediaName = p.getRef();
                if (!obj.hasMediaType(mediaName))
                {
                    obj.addMediaType(mediaName);
                }
            }
        }
        catch (Exception e)
        {
            throw new TorqueException(e);
        }
    }
    /**
         * it will make the category  types  for this portlet
         * @param portlet object.
         * 
         */
    public static void buildPortletCategory(int id, BasePortletEntry obj)
        throws TorqueException
    {
        try
        {
            List list =
                BaseJetspeedPortletCategoryPeer.retrieveById(
                    SimpleKey.keyFor(id));
            for (int i = 0; i < list.size(); i++)
            {
                BaseCategory p = (BaseCategory) list.get(i);
                String name = p.getName();
                String group = p.getGroup();
                if (group == null)
                {
                    if (!obj.hasCategory(name))
                        obj.addCategory(name);
                }
                else
                    if (!obj.hasCategory(name, group))
                    {
                        obj.addCategory(name, group);
                    }
            }
        }
        catch (Exception e)
        {
            throw new TorqueException(e);
        }
    }
}
