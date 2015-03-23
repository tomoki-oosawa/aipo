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
import java.util.List;

import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.turbine.services.Service;

/**
 * This service is responsible for loading and saving PSML documents.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: PsmlManagerService.java,v 1.12 2004/02/23 03:32:51 jford Exp $
 */
public interface PsmlManagerService extends Service {

  /** The name of the service */
  public String SERVICE_NAME = "PsmlManager";

  /**
   * Returns a PSML document of the given name. For this implementation, the
   * name must be the document URL or absolute filepath
   *
   * @deprecated
   * @param name
   *          the name of the document to retrieve
   */
  @Deprecated
  public PSMLDocument getDocument(String name);

  /**
   * Returns a PSML document for the given locator
   *
   * @param locator
   *          The locator descriptor of the document to be retrieved.
   */
  public PSMLDocument getDocument(ProfileLocator locator);

  /**
   * Given a ordered list of locators, find the first document matching a
   * profile locator, starting from the beginning of the list and working to the
   * end.
   *
   * @param locator
   *          The ordered list of profile locators.
   */
  public PSMLDocument getDocument(List locators);

  /**
   * Store the PSML document on disk, using its locator
   *
   * @param profile
   *          the profile locator description.
   * @return true if the operation succeeded
   */
  public boolean store(Profile profile);

  /**
   * Save the PSML document on disk, using its name as filepath
   *
   * @deprecated
   * @param doc
   *          the document to save
   * @return true if the operation succeeded
   */
  @Deprecated
  public boolean saveDocument(PSMLDocument doc);

  /**
   * Save the PSML document on disk to the specififed fileOrUrl
   *
   * @deprecated
   * @param fileOrUrl
   *          a String representing either an absolute URL or an absolute
   *          filepath
   * @param doc
   *          the document to save
   * @return true if the operation succeeded
   */
  @Deprecated
  public boolean saveDocument(String fileOrUrl, PSMLDocument doc);

  /**
   * Create a new document.
   *
   * @param profile
   *          the profile to use
   * @return The newly created document.
   */
  public PSMLDocument createDocument(Profile profile);

  /**
   * Remove a document.
   *
   * @param locator
   *          The description of the profile to be removed.
   */
  public void removeDocument(ProfileLocator locator);

  /**
   * Removes all documents for a given user.
   *
   * @param user
   *          The user object.
   */
  public void removeUserDocuments(JetspeedUser user);

  /**
   * Removes all documents for a given group.
   *
   * @param group
   *          The group object.
   */
  public void removeGroupDocuments(Group group);

  /**
   * Removes all documents for a given role.
   *
   * @param role
   *          The role object.
   */
  public void removeRoleDocuments(Role role);

  /**
   * Query for a collection of profiles given a profile locator criteria.
   *
   * @param locator
   *          The profile locator criteria.
   *
   * @return A collection of profiles that match the criteria specified in the
   *         locator.
   */
  public Iterator query(QueryLocator locator);

  /**
   * Export profiles from this service into another service
   *
   * @param consumer
   *          The PSML consumer service, receives PSML from this service.
   * @param locator
   *          The profile locator criteria.
   *
   * @return The count of profiles exported.
   */
  public int export(PsmlManagerService consumer, QueryLocator locator);

  /**
   * Returns a PSML document for the given locator bypassing the cache (if
   * applicable)
   *
   * @param locator
   *          The locator descriptor of the document to be retrieved.
   */
  public PSMLDocument refresh(ProfileLocator locator);

}
