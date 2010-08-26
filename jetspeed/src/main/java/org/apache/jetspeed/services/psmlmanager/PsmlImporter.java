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
 
package org.apache.jetspeed.services.psmlmanager;

import java.util.Iterator;

import org.apache.turbine.services.TurbineServices;

// Jetspeed Security service
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// Profile and ProfileLocator interface 
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.om.profile.QueryLocator;

import org.apache.turbine.util.TurbineConfig;

/**
 * Reads all PSML files from the file system and imports them into PSML DB
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlImporter.java,v 1.16 2004/02/23 03:32:51 jford Exp $
 */
public class PsmlImporter
{   
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PsmlImporter.class.getName());
    
    protected boolean check = true;

    public PsmlImporter()
    {
    }

    public static void main(String args[]) 
    {
        System.out.println("***** PSML Importer *****");
        boolean checkImport = true;
        
        //
        // initialize and bootstrap services
        //
        try
        {
            String root = "./webapp";
            String properties = "/WEB-INF/conf/TurbineResources.properties";
            if (args.length > 0)
            {
                if (args[0].equalsIgnoreCase("true"))
                    checkImport = true;
                else
                    checkImport = false;
            }
            if (args.length > 1)
            {
                root = args[1];
            }
            if (args.length > 2)
            {
                properties = args[2];
            }
            TurbineConfig config = new TurbineConfig( root, properties);
            config.init();
        }
        catch (Exception e)
        {
            String msg = "PSML Importer: error initializing Turbine configuration";
            logger.error(msg, e);
            System.out.println(msg);
            e.printStackTrace();
            System.exit(0);
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
            logger.error(msg, e);
            System.out.println(msg);
            e.printStackTrace();
            System.exit(0);
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
            logger.error(msg, e);
            System.out.println(msg);
            e.printStackTrace();
            System.exit(0);
        }

        if (exporterService.getClass().getName().equals(importerService.getClass().getName()))
        {
            String msg = "PSML Importer Error: Importer Class cannot equal Exporter Class.";
            logger.error(msg);
            System.out.println(msg);
            System.exit(0);
        }

        PsmlImporter importer = new PsmlImporter();
        importer.setCheck(checkImport);
        boolean ran = importer.run(exporterService, importerService);

        if (ran)
        {
            System.out.println("**** PSML Importer - completed");
        }        

        System.exit(1);

    }

    public boolean run(PsmlManagerService exporterService,
                    PsmlManagerService importerService)
    {
        String msg;
        int count = 0;
        try
        {
           if (check && alreadyImported())
                return false; 

            msg = "Running with Importer Service: " + importerService.getClass();
            System.out.println(msg);
            logger.info(msg);

            msg = "Running with Exporter Service: " + exporterService.getClass();
            System.out.println(msg);
            logger.info(msg);


            QueryLocator locator = new QueryLocator(QueryLocator.QUERY_ALL);
            count = exporterService.export(importerService, locator);
        }
        catch (Exception e)
        {
            System.out.println("Error importing: " + e.toString());
            logger.error("Error importing: " , e);
            e.printStackTrace();
            return false;
        }             
        msg = "PSMLImporter completed. Exported " + count + " profiles";
        System.out.println(msg);
        logger.info(msg);
        return true;
    }


    /*
     * Check to see if import has already completed.
     * Only considers a "onetime" import, checking for the "admin" user.
     *
     * @return true if import was already ran.
     */
    public boolean alreadyImported() 
    {
        try 
        {
            JetspeedUser user = JetspeedSecurity.getUser("admin");
            QueryLocator ql = new QueryLocator(QueryLocator.QUERY_USER);
            ql.setUser(user);
            Iterator iterator = PsmlManager.query(ql);
            if (iterator.hasNext())
            {                      
                String msg = "PSMLImporter: Detected database is populated. No need to import.";
                System.out.println(msg);
                logger.info(msg);
                return true;    // record found
            }
            return false;   // record not found
        }
        catch (UnknownUserException e)
        {
            return false;  // record not found
        }
        catch (JetspeedSecurityException e)
        {
            String msg = "Failed to run import: Database Access Error detecting database on import: ";
            logger.error(msg, e);    
            System.out.println(msg + e.toString());
            return true;
        }
    }

    public void setCheck(boolean check)
    {
        this.check = check;
    }

    public boolean getCheck()
    {
        return this.check;
    }

}
