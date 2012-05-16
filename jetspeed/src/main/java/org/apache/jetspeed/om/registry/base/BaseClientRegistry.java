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

package org.apache.jetspeed.om.registry.base;

import java.util.Enumeration;
import java.util.regex.Pattern;

import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.ClientRegistry;
import org.apache.jetspeed.om.registry.InvalidEntryException;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Simple implementation of the ClientRegistry interface.
 * <p>
 * Extends BaseRegistry implementation to override object creation method and
 * ensure Registry object is synchronized with its persistence backend by
 * delegating actual addition/deletion of objects to the registry service.
 * </p>
 * <p>
 * To avoid loops, a RegistryService implementation using this class nees to
 * call the addLocalEntry/removeLocalEntry methods to modify the in memory state
 * of this Registry
 * </p>
 * 
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: BaseClientRegistry.java,v 1.5 2004/02/23 03:08:26 jford Exp $
 */
public class BaseClientRegistry extends BaseOrderedRegistry implements
    ClientRegistry {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BaseClientRegistry.class.getName());

  /**
   * @see Registry#setEntry
   */
  @Override
  public void setEntry(RegistryEntry entry) throws InvalidEntryException {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    try {
      Registry.addEntry(Registry.CLIENT, entry);
    } catch (RegistryException e) {
      logger.error("Exception", e);
    }
  }

  /**
   * @see Registry#addEntry
   */
  @Override
  public void addEntry(RegistryEntry entry) throws InvalidEntryException {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    try {
      Registry.addEntry(Registry.CLIENT, entry);
    } catch (RegistryException e) {
      logger.error("Exception", e);
    }
  }

  /**
   * @see Registry#removeEntry
   */
  @Override
  public void removeEntry(String name) {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    Registry.removeEntry(Registry.CLIENT, name);
  }

  /**
   * @see Registry#removeEntry
   */
  @Override
  public void removeEntry(RegistryEntry entry) {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    if (entry != null) {
      Registry.removeEntry(Registry.CLIENT, entry.getName());
    }
  }

  /**
   * Returns the client which matches the given useragent string.
   * 
   * @param useragent
   *          the useragent to match
   * @return the found client or null if the user-agent does not match any
   *         defined client
   */
  @Override
  public ClientEntry findEntry(String useragent) {
    ClientEntry clientEntry = null;
    Enumeration clients = getEntries();

    if (logger.isDebugEnabled()) {
      logger.debug("ClientRegistry: Looking for client with useragent :"
        + useragent);
    }

    if (clients != null) {
      while (clients.hasMoreElements()) {
        ClientEntry client = (ClientEntry) clients.nextElement();
        if (client.getUseragentpattern() != null) {
          try {
            // RE r = new RE(client.getUseragentpattern());
            // r.setMatchFlags(RE.MATCH_CASEINDEPENDENT);

            // org.apache.regexp -> java.util.regex
            Pattern r =
              Pattern.compile(
                client.getUseragentpattern(),
                Pattern.CASE_INSENSITIVE);

            if (r.matcher(useragent).matches()) {

              if (logger.isDebugEnabled()) {
                logger.debug("ClientRegistry: "
                  + useragent
                  + " matches "
                  + client.getUseragentpattern());
              }

              return client;
            } else {
              if (logger.isDebugEnabled()) {
                logger.debug("ClientRegistry: "
                  + useragent
                  + " does not match "
                  + client.getUseragentpattern());
              }
            }
          } catch (org.apache.regexp.RESyntaxException e) {
            String message =
              "ClientRegistryService: UserAgentPattern not valid : "
                + client.getUseragentpattern()
                + " : "
                + e.getMessage();
            logger.error(message, e);
          }
        }
      }
    }

    return clientEntry;
  }

  /**
   * Creates a new RegistryEntry instance compatible with the current Registry
   * instance implementation
   * 
   * @return the newly created RegistryEntry
   */
  @Override
  public RegistryEntry createEntry() {
    return new BaseClientEntry();
  }
}
