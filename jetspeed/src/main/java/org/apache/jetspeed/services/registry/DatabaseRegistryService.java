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

package org.apache.jetspeed.services.registry;

// Java classes
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.registry.DBRegistry;
import org.apache.jetspeed.om.registry.Registry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.om.registry.base.BaseRegistry;
import org.apache.jetspeed.om.registry.base.LocalRegistry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.ServletService;

/**
 * <p>
 * This is an implementation of the <code>RegistryService</code> based on the
 * Jetspeed Database Persistence Manager
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:susinha@cisco.com">Suchisubhra Sinha</a>
 */
public class DatabaseRegistryService extends TurbineBaseService implements
    RegistryService, FileRegistry {
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CastorRegistryService.class.getName());

  /** The name of this service */
  public static final String SERVICE_NAME = "DatabaseRegistry";

  public static final int DEFAULT_VERBOSE = 1;

  /** regsitry type keyed list of entries */
  private Hashtable registries = new Hashtable();

  /** The list of default fragments stores for newly created objects */
  private Hashtable defaults = new Hashtable();

  /** The Castor generated RegsitryFragment objects */
  private Hashtable fragments = new Hashtable();

  /** Associates entries with their fragments name for quick lookup */
  private Hashtable entryIndex = new Hashtable();

  /** the Watcher object which monitors the regsitry directory */
  private DatabaseRegistryWatcher watcher = null;

  /** Assign the default poolname */
  private final static String POOL_NAME = "database";

  /**
   * controls amount of debug output, the bigger the more output will be
   * generated
   */
  private int verbose = DEFAULT_VERBOSE;

  /** Base class to implement */
  private static Hashtable baseClass = new Hashtable();

  /**
   * Returns a Registry object for further manipulation
   * 
   * @param regName
   *            the name of the registry to fetch
   * @return a Registry object if found by the manager or null
   */
  public Registry get(String regName) {
    return (Registry) registries.get(regName);
  }

  /**
   * List all the registry currently available to this service
   * 
   * @return an Enumeration of registry names.
   */
  public Enumeration getNames() {
    return registries.keys();
  }

  /**
   * Creates a new RegistryEntry instance compatible with the current Registry
   * instance implementation
   * 
   * @param regName
   *            the name of the registry to use
   * @return the newly created RegistryEntry
   */
  public RegistryEntry createEntry(String regName) {
    RegistryEntry entry = null;
    Registry registry = (Registry) registries.get(regName);

    if (registry != null) {
      entry = registry.createEntry();
    }

    return entry;
  }

  /**
   * Returns a RegistryEntry from the named Registry. This is a convenience
   * wrapper around {@link org.apache.jetspeed.om.registry.Registry#getEntry }
   * 
   * @param regName
   *            the name of the registry
   * @param entryName
   *            the name of the entry to retrieve from the registry.
   * @return a RegistryEntry object if the key is found or null
   */
  public RegistryEntry getEntry(String regName, String entryName) {
    try {
      return ((Registry) registries.get(regName)).getEntry(entryName);
    } catch (RegistryException e) {
      if (logger.isInfoEnabled()) {
        logger.info("RegistryService: Failed to retrieve " + entryName
            + " from " + regName);
      }
    } catch (NullPointerException e) {
      logger.error("DatabaseRegistryService: " + regName
          + " registry is not known ");
      logger.error(e);
    }

    return null;
  }

  /**
   * Add a new RegistryEntry in the named Registry. This is a convenience
   * wrapper around {@link org.apache.jetspeed.om.registry.Registry#addEntry }
   * 
   * @param regName
   *            the name of the registry
   * @param entry
   *            the Registry entry to add
   * @exception Sends
   *                a RegistryException if the manager can't add the provided
   *                entry
   */
  public void addEntry(String regName, RegistryEntry entry)
      throws RegistryException {
    if (entry == null) {
      return;
    }

    LocalRegistry registry = (LocalRegistry) registries.get(regName);

    if (registry != null) {
      String fragmentName = (String) entryIndex.get(entry.getName());

      if (fragmentName == null) {
        // either the entry was deleted or it does not exist
        // in both cases, use the default fragment
        fragmentName = (String) defaults.get(regName);
      }

      RegistryFragment fragment = (RegistryFragment) fragments
          .get(fragmentName);

      // Fragment can be (and sometimes is, but should not be) null
      if (fragment == null) {
        fragment = new RegistryFragment();
        fragment.put(regName, new Vector());
        fragments.put(fragmentName, fragment);
      } else {
        Vector vectRegistry = (Vector) fragment.get(regName);
        if (vectRegistry == null) {
          fragment.put(regName, new Vector());
        }
      }

      synchronized (entryIndex) {
        if (registry.hasEntry(entry.getName())) {
          fragment.setEntry(regName, entry);
          registry.setLocalEntry(entry);
        } else {
          fragment.addEntry(regName, entry);
          registry.addLocalEntry(entry);
        }

        entryIndex.put(entry.getName(), fragmentName);
        // mark this fragment so that it's persisted next time
        // the registry watcher is running
        fragment.setDirty(true);
      }
    }
  }

  /**
   * Deletes a RegistryEntry from the named Registry This is a convenience
   * wrapper around
   * {@link org.apache.jetspeed.om.registry.Registry#removeEntry }
   * 
   * @param regName
   *            the name of the registry
   * @param entryName
   *            the name of the entry to remove
   */
  public void removeEntry(String regName, String entryName) {
    if (entryName == null) {
      return;
    }

    LocalRegistry registry = (LocalRegistry) registries.get(regName);

    if (registry != null) {
      String fragmentName = (String) entryIndex.get(entryName);

      if (fragmentName != null) {
        RegistryFragment fragment = (RegistryFragment) fragments
            .get(fragmentName);

        synchronized (entryIndex) {
          fragment.removeEntry(regName, entryName);
          entryIndex.remove(entryName);

          // mark this fragment so that it's persisted next time
          // the registry watcher is running
          fragment.setDirty(true);
        }
      }

      // the entry is physically removed, remove the dangling reference
      registry.removeLocalEntry(entryName);
    }
  }

  /**
   * This is the early initialization method called by the Turbine
   * <code>Service</code> framework
   */
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    int refreshRate = 0;
    Vector names = new Vector();

    // Ensure that the servlet service is initialized
    TurbineServices.getInstance()
        .initService(ServletService.SERVICE_NAME, conf);

    ResourceService serviceConf = ((TurbineServices) TurbineServices
        .getInstance()).getResources(SERVICE_NAME);

    // build the map of default fragments, eahc registry must be associated
    // with at least one fragment
    try {
      refreshRate = serviceConf.getInt("refreshRate", DEFAULT_REFRESH);
      ResourceService defaults = serviceConf.getResources("default");
      Iterator i = defaults.getKeys();

      while (i.hasNext()) {
        String name = (String) i.next();
        // add this name in the list of available registries

        names.add(name);
        try {
          String registryClass = "org.apache.jetspeed.om.registry.database.BaseJetspeed"
              + name + "Peer";

          baseClass.put(name, (DBRegistry) Class.forName(registryClass)
              .newInstance());
        } catch (Exception e) {
          if (logger.isWarnEnabled()) {
            logger
                .warn("DatabaseRegistryService: Class " + name + " not found");
          }

        }

      }
    } catch (Throwable t) {
      throw new InitializationException(
          "Unable to initialize DatabaseRegistryService, missing config keys");
    }

    this.watcher = new DatabaseRegistryWatcher();
    this.watcher.setSubscriber(this);

    if (refreshRate == 0) {
      this.watcher.setDone();
    } else {
      this.watcher.setRefreshRate(refreshRate);
    }
    // changing the base will trigger a synchronous loading of the fragments
    this.watcher.changeBase(names);

    // Mark that we are done
    setInit(true);

    // load the registries
    Enumeration en = names.elements();

    RegistryService localeService = (RegistryService) TurbineServices
        .getInstance().getService(RegistryService.SERVICE_NAME);

    while (en.hasMoreElements()) {
      String name = (String) en.nextElement();
      Registry registry = (Registry) registries.get(name);

      if (registry == null) {
        String registryClass = null;
        try {
          registry = localeService.get(name);
        } catch (Exception e) {
          if (logger.isWarnEnabled()) {
            logger.warn("DatabaseRegistryService: Class " + registryClass
                + " not found, reverting to default Registry");
          }
          registry = new BaseRegistry();
        }
        registries.put(name, registry);
      }

      refresh(name);
    }

    // Start the directory watcher thread and rely on its refresh process
    // to completely load all registries
    if (this.watcher != null) {
      this.watcher.start();
    }

    if (logger.isDebugEnabled()) {
      logger
          .debug("DatabaseRegistryService: early init()....end!, this.getInit()= "
              + getInit());
    }
  }

  /**
   * @return a Map of all fragments keyed by file names
   */
  public Map getFragmentMap() {
    return (Map) fragments.clone();
  }

  /** Late init method from Turbine Service model */
  public void init() throws InitializationException {
    if (logger.isDebugEnabled()) {
      logger.debug("DatabaseRegistryService: Late init called");
    }
    while (!getInit()) {
      // Not yet...
      try {
        Thread.sleep(500);
        if ((verbose > 2) && logger.isDebugEnabled()) {
          logger
              .debug("DatabaseRegistryService: Waiting for init of Registry...");
        }
      } catch (InterruptedException ie) {
        logger.error(ie);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("DatabaseRegistryService: We are done");
    }
  }

  /**
   * This is the shutdown method called by the Turbine <code>Service</code>
   * framework
   */
  public void shutdown() {
    this.watcher.setDone();

    Iterator i = fragments.keySet().iterator();
    while (i.hasNext()) {
      saveFragment((String) i.next());
    }
  }

  /**
   * Scan all the registry fragments for new entries relevant to this registry
   * and update its definition.
   * 
   * @param regName
   *            the name of the Registry to refresh
   */
  protected void refresh(String regName) {
    if (logger.isDebugEnabled()) {
      logger.debug("DatabaseRegistryService: Updating the " + regName
          + " registry");
    }

    int count = 0;
    int counDeleted = 0;
    LocalRegistry registry = (LocalRegistry) get(regName);

    if (registry == null) {
      logger.error("DatabaseRegistryService: Null " + name
          + " registry in refresh");
      return;
    }

    Vector toDelete = new Vector();
    Iterator i = registry.listEntryNames();

    while (i.hasNext()) {
      toDelete.add(i.next());
    }

    // for each fragment...
    Enumeration en = fragments.keys();
    while (en.hasMoreElements()) {
      String location = (String) en.nextElement();
      RegistryFragment fragment = (RegistryFragment) fragments.get(location);
      int fragCount = 0;

      if (!fragment.hasChanged()) {
        if ((verbose > 2) && logger.isDebugEnabled()) {
          logger
              .debug("DatabaseRegistryService: Skipping fragment " + location);
        }

        // remove this fragment entries from the delete list
        Vector entries = fragment.getEntries(regName);
        i = entries.iterator();
        while (i.hasNext()) {
          toDelete.remove(((RegistryEntry) i.next()).getName());
        }
        continue;
      }

      // the fragment has some changes, iterate over its entries...
      Vector entries = fragment.getEntries(regName);
      // ... if it has entries related to this regsistry,
      if (entries != null) {
        // for all these entries
        Enumeration en2 = entries.elements();
        while (en2.hasMoreElements()) {
          RegistryEntry entry = (RegistryEntry) en2.nextElement();
          // update or add the entry in the registry
          try {
            if (registry.hasEntry(entry.getName())) {
              if (registry.getEntry(entry.getName()).equals(entry)) {
                if ((verbose > 2) && logger.isDebugEnabled()) {
                  logger.debug("DatabaseRegistryService: No changes to entry "
                      + entry.getName());
                }
              } else {
                if ((verbose > 1) && logger.isDebugEnabled()) {
                  logger.debug("DatabaseRegistryService: Updating entry "
                      + entry.getName() + " of class " + entry.getClass()
                      + " to registry " + name);
                }
                registry.setLocalEntry(entry);
                // Initialize the entry index
                this.entryIndex.put(entry.getName(), location);
                ++fragCount;
              }
            } else {
              registry.addLocalEntry(entry);
              // Initialize the entry index
              this.entryIndex.put(entry.getName(), location);
              ++fragCount;

              if ((verbose > 1) && logger.isDebugEnabled()) {
                logger.debug("DatabaseRegistryService: Adding entry "
                    + entry.getName() + " of class " + entry.getClass()
                    + " to registry " + name);
              }
            }
          } catch (RegistryException e) {
            logger.error(
                "DatabaseRegistryService: RegistryException while adding "
                    + entry.getName() + "from " + location, e);
          }
          // remove this entry from the delete list
          toDelete.remove(entry.getName());
        }
      }

      count += fragCount;
    }

    // now delete the entries not found in any fragment
    i = toDelete.iterator();
    while (i.hasNext()) {
      String entryName = (String) i.next();

      if ((verbose > 1) && logger.isDebugEnabled()) {
        logger.debug("DatabaseRegistryService: removing entry " + entryName);
      }
      // TODO may be I will do it later
      // it should delete only portlets which is coming from database

      // registry.removeLocalEntry(entryName);
    }

    if ((verbose > 1) && logger.isDebugEnabled()) {
      logger.debug("DatabaseRegistryService: Merged " + count
          + " entries and deleted " + toDelete.size() + " in " + name);
    }
  }

  // FileRegistry interface

  /**
   * Refresh the state of the registry implementation. Should be called whenever
   * the underlying fragments are modified
   */
  public void refresh() {
    synchronized (watcher) {
      Enumeration en = getNames();
      while (en.hasMoreElements()) {
        refresh((String) en.nextElement());
      }
    }
  }

  /**
   * Load and unmarshal a RegistryFragment from the file
   * 
   * @param file
   *            the absolute file path storing this fragment
   */
  public void loadFragment(String file) {
    try {
      RegistryFragment fragment = createFragment(file);
      // mark this fragment as changed
      fragment.setChanged(true);

      // if we get here, we successfully loaded the new fragment
      updateFragment(file, fragment);

    } catch (Throwable t) {
      logger.error("DatabaseRegistryService: Could not unmarshal: " + file, t);
    }
  }

  /**
   * Read and unmarshal a fragment in memory
   * 
   * @param name
   *            the name of this fragment
   * @param persistent
   *            whether this fragment should be persisted on disk in the
   *            registry
   */
  public void createFragment(String name, Reader reader, boolean persistent) {
    String file = null;

    try {
    } catch (Throwable t) {
      logger.error("DatabaseRegistryService: Could not create fragment: "
          + file, t);
    } finally {
      try {
        reader.close();
      } catch (Exception e) {
        logger.error(e); // At least log the exception.
      }
    }
  }

  /**
   * Marshal and save a RegistryFragment to disk
   * 
   * @param file
   *            the absolute file path storing this fragment
   */
  public void saveFragment(String file) {

    /**
     * TODO I will implement this should go to database
     */

  }

  /**
   * Remove a fragment from storage
   * 
   * @param file
   *            the absolute file path storing this fragment
   */
  public void removeFragment(String file) {
    RegistryFragment fragment = (RegistryFragment) fragments.get(file);

    if (fragment != null) {
      synchronized (entryIndex) {
        // clear the entry index
        Iterator i = entryIndex.keySet().iterator();
        while (i.hasNext()) {
          if (file.equals(entryIndex.get(i.next()))) {
            i.remove();
          }
        }

        // make sure the keys & entries are freed for this fragment
        // only the entries not replaced by the next registry refresh will
        // stay in memory
        fragment.clear();
        // remove the actual fragment from memory
        fragments.remove(file);
      }
    }
  }

  /**
   * Updates a fragment in storage and the associated entryIndex
   */
  protected void updateFragment(String name, RegistryFragment fragment) {
    synchronized (entryIndex) {
      // remove the old keys
      Iterator i = entryIndex.keySet().iterator();
      while (i.hasNext()) {
        if (name.equals(entryIndex.get(i.next()))) {
          i.remove();
        }
      }
      // store the new fragment
      fragments.put(name, fragment);

      // recreate the index entries (only this fragment)

      Enumeration enu = fragment.keys();
      while (enu.hasMoreElements()) {
        String strReg = (String) enu.nextElement();
        Vector v = fragment.getEntries(strReg);
        for (int counter = 0; counter < v.size(); counter++) {
          RegistryEntry str = (RegistryEntry) v.elementAt(counter);
          entryIndex.put(str.getName(), name);
        }
      }
    }
  }

  // class specific implementation
  private static List getData(String name) {
    List list = null;
    try {
      DBRegistry BaseClass = (DBRegistry) baseClass.get(name);
      if (BaseClass != null) {
        list = BaseClass.getXREGDataFromDb();
      } else {
        logger.warn("DatabaseRegistryService: Base class  for service " + name
            + " not found");
      }
    } catch (Exception ex) {
      logger.warn("DatabaseRegistryService: Base class  for service " + name
          + " not found");
    }
    return list;
  }

  private RegistryFragment createFragment(String regName) {
    RegistryFragment fragment = (RegistryFragment) fragments.get(regName);

    // Fragment can be (and sometimes is, but should not be) null
    if (fragment == null) {
      fragment = new RegistryFragment();
      fragment.put(regName, new Vector());
    } else {
      Vector vectRegistry = (Vector) fragment.get(regName);
      if (vectRegistry == null) {
        fragment.put(regName, new Vector());
      }
    }
    List entries = getData(regName);
    if (entries != null) {
      for (int i = 0; i < entries.size(); i++) {
        fragment.setEntry(regName, (RegistryEntry) entries.get(i));
        // mark this fragment so that it's persisted next time
        // the registry watcher is running
        fragment.setDirty(true);
      }
    } else {
      logger.warn("DatabaseRegistryService:no data fouund for service " + name);

    }
    return fragment;

  }
}
