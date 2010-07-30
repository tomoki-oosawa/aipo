
package org.apache.jetspeed.modules.actions;

// jetspeed stuff
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.psmlmanager.PsmlImporter;
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;

/**
 * Import file psml into database action. This action is useful when populating
 * psml for the first time.
 * 
 * @author <a href="mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: ImportPsml.java,v 1.1 2004/01/29 20:36:54 morciuch Exp $
 */
public class ImportPsml extends org.apache.turbine.modules.Action
{

    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String CHECK_IMPORT = "check-import";

    /**
     * Perform the action
     */
    public void doPerform(RunData data) throws Exception
    {
        try
        {
            ParameterParser parser = data.getParameters();
            //String username = parser.getString(USER, "admin");
            //String password = parser.getString(PASSWORD);
			boolean checkImport = parser.getBoolean(CHECK_IMPORT, false);

            //JetspeedUser admin = JetspeedSecurity.login(username, password);
            String username = data.getUser().getUserName();
            if (!JetspeedSecurity.hasRole(username, "admin"))
            {
            	data.setMessage("Only administrator can perform this action");
                throw new Exception("Only administrator can perform this action");
            }

            //
            // get a handle to the exporter service
            //
            PsmlManagerService exporterService = null;
            PsmlManagerService importerService = null;

            try
            {
                exporterService = (PsmlManagerService)TurbineServices.getInstance().getService("PsmlImportManager");
            }
            catch (org.apache.turbine.services.InstantiationException e)
            {
                String msg = "PSML Importer: error loading Psml Exporter Service";
                data.setMessage(msg);
                Log.error(msg, e);
            }

            //
            // get a handle to the importer service
            //
            try
            {
                importerService = PsmlManager.getService();
            }
            catch (org.apache.turbine.services.InstantiationException e)
            {
                String msg = "PSML Importer: error loading Psml Importer Service";
                data.setMessage(msg);
                Log.error(msg, e);
            }

            if (exporterService.getClass().getName().equals(importerService.getClass().getName()))
            {
                String msg = "PSML Importer Error: Importer Class cannot equal Exporter Class.";
                data.setMessage(msg);
                Log.error(msg);
            }

            PsmlImporter importer = new PsmlImporter();
            importer.setCheck(checkImport);
            boolean ran = importer.run(exporterService, importerService);

            if (ran)
            {
            	String msg = "**** PSML Importer - completed";
                System.out.println(msg);
                data.setMessage(msg);
            }        
            else
            {
				String msg = "**** PSML Importer - did not run";
				System.out.println(msg);
				data.setMessage(msg);            	
            }
        }
        catch (Exception e)
        {
        	data.setMessage(e.getMessage());
            Log.error(e.getMessage());
        }
    }

}