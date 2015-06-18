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

package org.apache.jetspeed.services;

import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.turbine.services.TurbineServices;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.Group;

import java.util.Iterator;
import java.util.List;

/**
 * Static accessor for the PsmlManagerService
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PsmlManager.java,v 1.10 2004/02/23 04:00:57 jford Exp $
 */
public class PsmlManager
{

    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    public static PsmlManagerService getService()
    {
        return (PsmlManagerService)TurbineServices
                .getInstance()
                .getService(PsmlManagerService.SERVICE_NAME);     
    }

    /**
     * Returns a PSML document of the given name.
     * For this implementation, the name must be the document
     * URL or absolute filepath
     *
     * @deprecated
     * @param name the name of the document to retrieve
     */
    public static PSMLDocument getDocument( String name )
    {
        return getService().getDocument(name);
    }

    /**
     * Returns a PSML document for the given locator
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public static PSMLDocument getDocument( ProfileLocator locator )
    {
        return getService().getDocument(locator);
    }

    /** Given a ordered list of locators, find the first document matching
     *  a profile locator, starting from the beginning of the list and working
     *  to the end.
     *
     * @param locator The ordered list of profile locators.
     */
    public static PSMLDocument getDocument( List locators )
    {
        return getService().getDocument(locators);
    }

    /** Store the PSML document on disk, using its locator
     * 
     * @param profile the profile locator description.
     * @return true if the operation succeeded
     */
    public static boolean store(Profile profile)
    {
        return getService().store(profile);
    }

    /** Save the PSML document on disk, using its name as filepath
     * @deprecated
     * @param doc the document to save
     */
    public static boolean saveDocument(PSMLDocument doc)
    {
        return getService().saveDocument(doc);
    }
    
    /** Save the PSML document on disk to the specififed fileOrUrl
     * @deprecated
     * @param fileOrUrl a String representing either an absolute URL
     * or an absolute filepath
     * @param doc the document to save
     */
    public static boolean saveDocument(String fileOrUrl, PSMLDocument doc)
    {
        return getService().saveDocument(fileOrUrl, doc);
    }

    /** Create a new document.
     *
     * @param profile The description and default value for the new document.
     * @return The newly created document.
     */
    public static PSMLDocument createDocument( Profile profile )
    {
        return getService().createDocument( profile );
    }

    /** Removes a document.
     *
     * @param locator The description of the profile resource to be removed.
     */
    public static void removeDocument( ProfileLocator locator )
    {
        getService().removeDocument( locator );
    }

    /** Removes all documents for a given user.
     *
     * @param user The user object.
     */
    public static void removeUserDocuments( JetspeedUser user )
    {
        getService().removeUserDocuments( user );
    }

    /** Removes all documents for a given group.
     *
     * @param group The group object.
     */
    public static void removeGroupDocuments( Group group )
    {
        getService().removeGroupDocuments( group );
    }


    /** Removes all documents for a given role.
     *
     * @param role The role object.
     */
    public static void removeRoleDocuments( Role role )
    {
        getService().removeRoleDocuments( role );
    }


    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     */
    public static Iterator query( QueryLocator locator )
    {
        return getService().query( locator );
    }

    /** Export profiles from this service into another service
     *
     * @param consumer The PSML consumer service, receives PSML from this service.
     * @param locator The profile locator criteria.
     *
     * @return The count of profiles exported.
     */
    public int export(PsmlManagerService consumer, QueryLocator locator)
    {
        return getService().export(consumer, locator);
    }

    /**
     * Refreshes a PSML document for the given locator
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public static PSMLDocument refresh( ProfileLocator locator )
    {
        return getService().refresh(locator);
    }

}

