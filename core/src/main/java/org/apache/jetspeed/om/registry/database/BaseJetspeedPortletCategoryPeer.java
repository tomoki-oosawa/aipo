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
import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.om.registry.base.BaseCategory;
import org.apache.jetspeed.om.registry.base.BasePortletEntry;
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
 * Base Peer for Portlet Category Registry entries.
 * 
 * @author <a href="mailto:susinha@cisco.com">Suchisubhra Sinha</a>
 * @version $Id: BaseJetspeedPortletCategoryPeer.java,v 1.3 2004/04/06 23:00:16 morciuch Exp $
 */
public class BaseJetspeedPortletCategoryPeer extends BasePeer
{
	
	/**
	 * Static initialization of the logger for this class
	 */    
	protected static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BaseJetspeedPortletCategoryPeer.class.getName());      
	
    /** the default database name for this class */
    public static final String DATABASE_NAME = "default";
    /** the table name for this class */
    //public static final String TABLE_NAME = "PORTLET_PARAMETER";
    /** the column name for the NAME field */
    public static final String PORTLET_ID;
    /** the column name for the NAME field */
    public static final String NAME;
    /** the column name for the GROUP field */
    public static final String GROUP;
    static {
        PORTLET_ID = "PORTLET_CATEGORY.ID";
        NAME = "PORTLET_CATEGORY.NAME";
        GROUP = "PORTLET_CATEGORY.CATEGORY_GROUP";
        if (Torque.isInit())
        {
            try
            {
                getMapBuilder();
            }
            catch (Exception e)
            {
                logger.error("Could not initialize Peer", e);
            }
        }
    }
    /** number of columns for this peer */
    public static final int numColumns = 1;
    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT =
        "org.apache.jetspeed.om.registry.base.BaseCategory";
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
        criteria.addSelectColumn(NAME);
        criteria.addSelectColumn(GROUP);
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
    public static BaseCategory row2Object(Record row, int offset, Class cls)
        throws TorqueException
    {
        try
        {
            BaseCategory obj = (BaseCategory) cls.newInstance();
            populateObject(row, offset, obj);
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
    public static void populateObject(Record row, int offset, BaseCategory obj)
        throws TorqueException
    {
        try
        {
            obj.setName(row.getValue(offset + 0).asString());
            if (row.getValue(offset + 1).asString() != null)
                obj.setGroup(row.getValue(offset + 1).asString());
        }
        catch (DataSetException e)
        {
            throw new TorqueException(e);
        }
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
        return BaseJetspeedPortletCategoryPeer.doSelectVillageRecords(
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
                BaseJetspeedPortletCategoryPeer.row2Object(
                    row,
                    1,
                    BaseJetspeedPortletCategoryPeer.getOMClass()));
        }
        return results;
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
     * Method to do selects
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List doSelect(BasePortletEntry obj) throws TorqueException
    {
        return doSelect(buildCriteria(obj));
    }
    /** Build a Criteria object from an ObjectKey */
    public static Criteria buildCriteria(ObjectKey pk)
    {
        Criteria criteria = new Criteria();
        criteria.add(PORTLET_ID, pk);
        return criteria;
    }
    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria(BasePortletEntry obj)
    {
        Criteria criteria = new Criteria(DATABASE_NAME);
        //TODO match  the values here
        return criteria;
    }
    /**
        * Retrieve a single object by pk
        *
        * @param pk the primary key
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public static BaseCategory retrieveByPK(int pk) throws TorqueException
    {
        return retrieveByPK(SimpleKey.keyFor(pk));
    }
    /**
     * Retrieve a single object by pk
     *
     * @param pk the primary key
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static BaseCategory retrieveByPK(ObjectKey pk)
        throws TorqueException
    {
        Connection db = null;
        BaseCategory retVal = null;
        try
        {
            db = Torque.getConnection(DATABASE_NAME);
            retVal = retrieveByPK(pk, db);
        }
        finally
        {
            Torque.closeConnection(db);
        }
        return (retVal);
    }
    /**
     * Retrieve a single object by pk
     *
     * @param pk the primary key
     * @param con the connection to use
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static BaseCategory retrieveByPK(ObjectKey pk, Connection con)
        throws TorqueException
    {
        Criteria criteria = buildCriteria(pk);
        List v = doSelect(criteria, con);
        if (v.size() != 1)
        {
            throw new TorqueException("Failed to select one and only one row.");
        }
        else
        {
            return (BaseCategory) v.get(0);
        }
    }
    /**
     * Retrieve a multiple objects by pk
     *
     * @param pks List of primary keys
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List retrieveByPKs(List pks) throws TorqueException
    {
        Connection db = null;
        List retVal = null;
        try
        {
            db = Torque.getConnection(DATABASE_NAME);
            retVal = retrieveByPKs(pks, db);
        }
        finally
        {
            Torque.closeConnection(db);
        }
        return (retVal);
    }
    /**
        * Retrieve a multiple objects by pk
        *
        * @param pks List of primary keys
        * @param dbcon the connection to use
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public static List retrieveByPKs(List pks, Connection dbcon)
        throws TorqueException
    {
        List objs = null;
        if (pks == null || pks.size() == 0)
        {
            objs = new LinkedList();
        }
        else
        {
            Criteria criteria = new Criteria();
            criteria.addIn(PORTLET_ID, pks);
            objs = doSelect(criteria, dbcon);
        }
        return objs;
    }
    /**
     * Retrieve a listt by portlet id
     *
     * @param id the portlet id
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List retrieveById(int pk) throws TorqueException
    {
        return retrieveById(SimpleKey.keyFor(pk));
    }
    /**
         * Retrieve a list of objects by id
         *
         * @param pk the portlet id
         * @throws TorqueException Any exceptions caught during processing will be
         *         rethrown wrapped into a TorqueException.
         */
    public static List retrieveById(ObjectKey pk) throws TorqueException
    {
        Connection db = null;
        List retVal = null;
        try
        {
            db = Torque.getConnection(DATABASE_NAME);
            retVal = retrieveById(pk, db);
        }
        finally
        {
            Torque.closeConnection(db);
        }
        return (retVal);
    }

    /**
     * Retrieve a single object by pk
     *
     * @param pk the primary key
     * @param con the connection to use
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static List retrieveById(ObjectKey pk, Connection con)
        throws TorqueException
    {
        Criteria criteria = buildCriteria(pk);
        return doSelect(criteria, con);
    }
}
