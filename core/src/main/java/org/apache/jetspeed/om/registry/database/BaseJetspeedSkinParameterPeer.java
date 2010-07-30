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

import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.base.BaseMetaInfo;
import org.apache.jetspeed.om.registry.base.BaseSecurity;
import org.apache.jetspeed.om.registry.base.BaseSkinEntry;
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
 * Base Peer for Skin Parameter registry entries.
 * 
 * @author <a href="mailto:susinha@cisco.com">Suchisubhra Sinha</a>
 * @version $Id: BaseJetspeedSkinParameterPeer.java,v 1.3 2004/04/06 23:00:16 morciuch Exp $
 */
public class BaseJetspeedSkinParameterPeer extends BasePeer
{
	
	/**
	 * Static initialization of the logger for this class
	 */    
	protected static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BaseJetspeedSkinParameterPeer.class.getName());      
	
    /** the default database name for this class */
    public static final String DATABASE_NAME = "default";
    /** the table name for this class */
    public static final String TABLE_NAME = "SKIN_PARAMETER";
    /** the column name for the PORTAL_ID field */
    public static final String ID;
    /** the column name for the NAME field */
    public static final String NAME;
    /** the column name for the NAME field */
    public static final String VALUE;
    /** the column name for the HIDDEN field */
    public static final String HIDDEN;
    /** the column name for the TYPE field */
    public static final String TYPE;
    /** the column name for the role field */
    public static final String ROLE;
    /** the column name for the TITLE field */
    public static final String TITLE;
    /** the column name for the DESCRIPTION field */
    public static final String DESCRIPTION;
    /** the column name for the IMAGE field */
    public static final String IMAGE;
    /** the portlet id for this parameter **/
    public static final String SKIN_ID;
    static {
        ID = "SKIN_PARAMETER.ID";
        NAME = "SKIN_PARAMETER.NAME";
        VALUE = "SKIN_PARAMETER.VALUE";
        HIDDEN = "SKIN_PARAMETER.HIDDEN";
        TYPE = "SKIN_PARAMETER.TYPE";
        ROLE = "SKIN_PARAMETER.ROLE";
        TITLE = "SKIN_PARAMETER.TITLE";
        DESCRIPTION = "SKIN_PARAMETER.DESCRIPTION";
        IMAGE = "SKIN_PARAMETER.IMAGE";
        SKIN_ID = "SKIN_PARAMETER.SKIN_ID";
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
    public static final int numColumns = 12;
    /** A class that can be returned by this peer. */
    protected static final String CLASSNAME_DEFAULT =
        "org.apache.jetspeed.om.registry.base.BaseParameter";
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
        criteria.addSelectColumn(ID);
        criteria.addSelectColumn(NAME);
        criteria.addSelectColumn(VALUE);
        criteria.addSelectColumn(HIDDEN);
        criteria.addSelectColumn(TYPE);
        criteria.addSelectColumn(ROLE);
        criteria.addSelectColumn(TITLE);
        criteria.addSelectColumn(DESCRIPTION);
        criteria.addSelectColumn(IMAGE);
        criteria.addSelectColumn(SKIN_ID);
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
    public static Parameter row2Object(Record row, int offset, Class cls)
        throws TorqueException
    {
        try
        {
            Parameter obj = (Parameter) cls.newInstance();
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
    public static void populateObject(Record row, int offset, Parameter obj)
        throws TorqueException
    {
        try
        {
            obj.setName(row.getValue(offset + 1).asString());
            obj.setValue(row.getValue(offset + 2).asString());
            obj.setHidden(row.getValue(offset + 3).asBoolean());
            obj.setType(row.getValue(offset + 4).asString());
            BaseMetaInfo baseMetaInfo =
                new BaseMetaInfo(
                    row.getValue(offset + 6).asString(),
                    row.getValue(offset + 7).asString(),
                    row.getValue(offset + 8).asString());
            obj.setMetaInfo(baseMetaInfo);
            //set  the security
            BaseSecurity security =
                new BaseSecurity(row.getValue(offset + 5).asString());
            obj.setSecurity(security);
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
        return BaseJetspeedSkinPeer.doSelectVillageRecords(
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
                BaseJetspeedSkinParameterPeer.row2Object(
                    row,
                    1,
                    BaseJetspeedSkinParameterPeer.getOMClass()));
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
    public static List doSelect(BaseSkinEntry obj) throws TorqueException
    {
        return doSelect(buildCriteria(obj));
    }
    /** Build a Criteria object from an ObjectKey */
    public static Criteria buildCriteria(ObjectKey pk)
    {
        Criteria criteria = new Criteria();
        criteria.add(SKIN_ID, pk);
        return criteria;
    }
    /** Build a Criteria object from the data object for this peer */
    public static Criteria buildCriteria(BaseSkinEntry obj)
    {
        Criteria criteria = new Criteria(DATABASE_NAME);
        /*
                            if (!obj.isNew())
                                criteria.add(PSML_ID, obj.getPsmlId());
                                criteria.add(USER_NAME, obj.getUserName());
                                criteria.add(MEDIA_TYPE, obj.getMediaType());
                                criteria.add(LANGUAGE, obj.getLanguage());
                                criteria.add(COUNTRY, obj.getCountry());
                                criteria.add(PAGE, obj.getPage());
                                criteria.add(PROFILE, obj.getProfile());
        */
        return criteria;
    }
    /**
        * Retrieve a single object by pk
        *
        * @param pk the primary key
        * @throws TorqueException Any exceptions caught during processing will be
        *         rethrown wrapped into a TorqueException.
        */
    public static Parameter retrieveByPK(int pk) throws TorqueException
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
    public static Parameter retrieveByPK(ObjectKey pk) throws TorqueException
    {
        Connection db = null;
        Parameter retVal = null;
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
    public static Parameter retrieveByPK(ObjectKey pk, Connection con)
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
            return (Parameter) v.get(0);
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
            criteria.addIn(SKIN_ID, pks);
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
