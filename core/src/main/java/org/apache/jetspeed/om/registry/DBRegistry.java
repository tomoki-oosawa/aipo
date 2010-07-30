/*
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.jetspeed.om.registry;
import java.util.List;
import org.apache.torque.TorqueException;
/**
 * @author susinha
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface DBRegistry {

    /** the default database name for this class */
    public static final String DATABASE_NAME = "default";

    /**
     * Method to get data.
     *
     * @param criteria object used to create the SELECT statement.
     * @return List of selected Objects
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public List getXREGDataFromDb() throws TorqueException;
    
    /**
     * Method to  data.
     *
     * @param timestamp  when database service ran last time.
     * @return boolean if data has been modified 
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public boolean isModified(String lastUpdateDate ) throws TorqueException;

}
