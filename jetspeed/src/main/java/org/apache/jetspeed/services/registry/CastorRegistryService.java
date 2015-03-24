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

package org.apache.jetspeed.services.registry;

// Jetspeed stuff
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jetspeed.om.registry.Registry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.om.registry.base.BaseRegistry;
import org.apache.jetspeed.om.registry.base.LocalRegistry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.ServletService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * <p>
 * This is an implementation of the <code>RegistryService</code> based on the
 * Castor XML serialization mechanisms
 * </p>
 * <p>
 * This registry aggregates multiple RegistryFragment to store the regsistry
 * entries
 * </p>
 * 
 * <p>
 * This service expects the following properties to be set for correct
 * operation:
 * <dl>
 * <dt>directory</dt>
 * <dd>The directory where the Registry will look for fragment files</dd>
 * <dt>extension</dt>
 * <dd>The extension used for identifying the registry fragment files. Default
 * .xreg</dd>
 * <dt>mapping</dt>
 * <dd>the Castor object mapping file path</dd>
 * <dt>registries</dt>
 * <dd>a comma separated list of registry names to load from this file</dd>
 * <dt>refreshRate</dt>
 * <dd>Optional. The manager will check every refreshRate seconds if the config
 * has changed and if true will refresh all the registries. A value of 0 or
 * negative will disable the automatic refresh operation. Default: 300 (5
 * minutes)</dd>
 * </dl>
 * </p>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 */
public class CastorRegistryService extends TurbineBaseService implements
    RegistryService, FileRegistry {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CastorRegistryService.class.getName());

  public static final int DEFAULT_REFRESH = 300;

  public static final String DEFAULT_EXTENSION = ".xreg";

  public static final String DEFAULT_MAPPING =
    "${webapp}/WEB-INF/conf/mapping.xml";

  /** regsitry type keyed list of entries */
  private final Hashtable registries = new Hashtable();

  /** The Castor generated RegsitryFragment objects */
  private final Hashtable fragments = new Hashtable();

  /** The list of default fragments stores for newly created objects */
  private final Hashtable defaults = new Hashtable();

  /** Associates entries with their fragments name for quick lookup */
  private final Hashtable entryIndex = new Hashtable();

  /** the Watcher object which monitors the regsitry directory */
  private RegistryWatcher watcher = null;

  /** the Castor mapping file name */
  private Mapping mapping = null;

  /** the output format for pretty printing when saving registries */
  private OutputFormat format = null;

  /** the base regsitry directory */
  private String directory = null;

  /** the extension for registry files */
  private String extension = null;

  /**
   * Returns a Registry object for further manipulation
   * 
   * @param regName
   *          the name of the registry to fetch
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
   *          the name of the registry to use
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
   *          the name of the registry
   * @param entryName
   *          the name of the entry to retrieve from the registry
   * @return a RegistryEntry object if the key is found or null
   */
  public RegistryEntry getEntry(String regName, String entryName) {
    try {
      return ((Registry) registries.get(regName)).getEntry(entryName);
    } catch (RegistryException e) {
      if (logger.isInfoEnabled()) {
        logger.info("RegistryService: Failed to retrieve "
          + entryName
          + " from "
          + regName);
      }
    } catch (NullPointerException e) {
      logger
        .error("RegistryService: " + regName + " registry is not known ", e);
    }

    return null;
  }

  /**
   * Add a new RegistryEntry in the named Registry. This is a convenience
   * wrapper around {@link org.apache.jetspeed.om.registry.Registry#addEntry }
   * 
   * @param regName
   *          the name of the registry
   * @param entry
   *          the Registry entry to add
   * @exception Sends
   *              a RegistryException if the manager can't add the provided
   *              entry
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

      RegistryFragment fragment =
        (RegistryFragment) fragments.get(fragmentName);

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
   * wrapper around {@link org.apache.jetspeed.om.registry.Registry#removeEntry }
   * 
   * @param regName
   *          the name of the registry
   * @param entryName
   *          the name of the entry to remove
   */
  public void removeEntry(String regName, String entryName) {
    if (entryName == null) {
      return;
    }

    LocalRegistry registry = (LocalRegistry) registries.get(regName);

    if (registry != null) {
      String fragmentName = (String) entryIndex.get(entryName);

      if (fragmentName != null) {
        RegistryFragment fragment =
          (RegistryFragment) fragments.get(fragmentName);

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
  @Override
  public synchronized void init(ServletConfig conf)
      throws InitializationException {

    // Ensure that the servlet service is initialized
    TurbineServices
      .getInstance()
      .initService(ServletService.SERVICE_NAME, conf);

    ResourceService serviceConf =
      ((TurbineServices) TurbineServices.getInstance())
        .getResources(RegistryService.SERVICE_NAME);
    String mapFile = null;
    Vector names = new Vector();
    int refreshRate = 0;

    // read the configuration keys
    try {
      directory = serviceConf.getString("directory");
      mapFile = serviceConf.getString("mapping", DEFAULT_MAPPING);
      extension = serviceConf.getString("extension", DEFAULT_EXTENSION);
      refreshRate = serviceConf.getInt("refreshRate", DEFAULT_REFRESH);

      mapFile = TurbineServlet.getRealPath(mapFile);
      directory = TurbineServlet.getRealPath(directory);
    } catch (Throwable t) {
      throw new InitializationException(
        "Unable to initialize CastorRegistryService, missing config keys");
    }

    // build the map of default fragments, eahc registry must be associated
    // with at least one fragment
    try {
      ResourceService defaults = serviceConf.getResources("default");
      Iterator i = defaults.getKeys();
      while (i.hasNext()) {
        String name = (String) i.next();
        String fragmentFileName = defaults.getString(name);

        String absFileName =
          new File(directory, fragmentFileName + extension).getCanonicalPath();
        // add this name in the list of available registries
        names.add(name);

        // store the default file mapping
        this.defaults.put(name, absFileName);
      }
    } catch (Exception e) {
      logger.error("RegistryService: Registry init error", e);
      throw new InitializationException(
        "Unable to initialize CastorRegistryService, invalid registries definition");
    }

    // create the serializer output format
    this.format = new OutputFormat();
    format.setIndenting(true);
    format.setIndent(4);
    format.setLineWidth(0);

    // test the mapping file and create the mapping object

    if (mapFile != null) {
      File map = new File(mapFile);
      if (map.exists() && map.isFile() && map.canRead()) {
        try {
          mapping = new Mapping();
          InputSource is = new InputSource(new FileReader(map));
          is.setSystemId(mapFile);
          mapping.loadMapping(is);
        } catch (Exception e) {
          logger.error("RegistryService: Error in mapping creation", e);
          throw new InitializationException("Error in mapping", e);
        }
      } else {
        throw new InitializationException(
          "Mapping not found or not a file or unreadable: " + mapFile);
      }
    }

    // Set directory watcher if directory exists
    File base = new File(directory);

    if (base.exists() && base.isDirectory() && base.canRead()) {
      this.watcher = new RegistryWatcher();
      this.watcher.setSubscriber(this);
      this.watcher.setFilter(new ExtFileFilter(extension));
      if (refreshRate == 0) {
        this.watcher.setDone();
      } else {
        this.watcher.setRefreshRate(refreshRate);
      }
      // changing the base will trigger a synchronous loading of the fragments
      this.watcher.changeBase(base);
    }

    // Mark that we are done
    setInit(true);

    // load the registries
    Enumeration en = names.elements();

    while (en.hasMoreElements()) {
      String name = (String) en.nextElement();
      Registry registry = (Registry) registries.get(name);

      if (registry == null) {
        String registryClass = null;
        try {
          registryClass =
            "org.apache.jetspeed.om.registry.base.Base" + name + "Registry";

          registry = (Registry) Class.forName(registryClass).newInstance();
        } catch (Exception e) {
          if (logger.isWarnEnabled()) {
            logger.warn("RegistryService: Class "
              + registryClass
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
      logger.debug("RegistryService: early init()....end!, this.getInit()= "
        + getInit());
    }

  }

  /** Late init method from Turbine Service model */
  @Override
  public void init() throws InitializationException {
    if (logger.isDebugEnabled()) {
      logger.debug("RegistryService: Late init called");
    }

    while (!getInit()) {
      // Not yet...
      try {
        Thread.sleep(500);
        if (logger.isDebugEnabled()) {
          logger.debug("RegistryService: Waiting for init of Registry...");
        }
      } catch (InterruptedException ie) {
        logger.error("Exception", ie);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("RegistryService: We are done");
    }
  }

  /**
   * This is the shutdown method called by the Turbine <code>Service</code>
   * framework
   */
  @Override
  public void shutdown() {
    this.watcher.setDone();

    Iterator i = fragments.keySet().iterator();
    while (i.hasNext()) {
      saveFragment((String) i.next());
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
   * @return a Map of all fragments keyed by file names
   */
  public Map getFragmentMap() {
    return (Map) fragments.clone();
  }

  /**
   * Load and unmarshal a RegistryFragment from the file
   * 
   * @param file
   *          the absolute file path storing this fragment
   */
  public void loadFragment(String file) {
    try {
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbfactory.newDocumentBuilder();

      Document d = builder.parse(new File(file));

      Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
      RegistryFragment fragment = (RegistryFragment) unmarshaller.unmarshal(d);

      // mark this fragment as changed
      fragment.setChanged(true);

      // if we get here, we successfully loaded the new fragment
      updateFragment(file, fragment);

    } catch (Throwable t) {
      logger.error("RegistryService: Could not unmarshal: " + file, t);
    }

  }

  /**
   * Read and unmarshal a fragment in memory
   * 
   * @param name
   *          the name of this fragment
   * @param reader
   *          the reader to use for creating this fragment
   * @param persistent
   *          whether this fragment should be persisted on disk in the registry
   */
  public void createFragment(String name, Reader reader, boolean persistent) {
    String file = null;

    try {
      synchronized (watcher) {
        file = new File(directory, name + extension).getCanonicalPath();

        Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
        RegistryFragment fragment =
          (RegistryFragment) unmarshaller.unmarshal(reader);

        fragment.setChanged(true);

        updateFragment(file, fragment);

        if (persistent) {
          saveFragment(file);
        }
      }
    } catch (Throwable t) {
      logger.error("RegistryService: Could not create fragment: " + file, t);
    } finally {
      try {
        reader.close();
      } catch (Exception e) {
        logger.error("Exception", e); // At least log the exception.
      }
    }
  }

  /**
   * Marshal and save a RegistryFragment to disk
   * 
   * @param file
   *          the absolute file path storing this fragment
   */
  public void saveFragment(String file) {
    OutputStreamWriter writer = null;
    FileOutputStream fos = null;
    RegistryFragment fragment = (RegistryFragment) fragments.get(file);

    if (fragment != null) {
      try {
        fos = new FileOutputStream(file);
        writer =
          new OutputStreamWriter(fos, JetspeedResources.getString(
            JetspeedResources.CONTENT_ENCODING_KEY,
            "utf-8"));
        format.setEncoding(JetspeedResources.getString(
          JetspeedResources.CONTENT_ENCODING_KEY,
          "utf-8"));
        Serializer serializer = new XMLSerializer(writer, format);
        Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
        marshaller.setMapping(this.mapping);
        marshaller.marshal(fragment);
      } catch (Throwable t) {
        logger.error("RegistryService: Could not marshal: " + file, t);
      } finally {
        try {
          writer.close();
        } catch (Exception e) {
          logger.error("Exception", e); // At least log the exception.
        }

        try {
          fos.close();
        } catch (Exception e) {
          logger.error("Exception", e); // At least log the exception.
        }
      }
    }
  }

  /**
   * Remove a fragment from storage
   * 
   * @param file
   *          the absolute file path storing this fragment
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

  // Implementation specific methods

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

  /**
   * Scan all the registry fragments for new entries relevant to this registry
   * and update its definition.
   * 
   * @param regName
   *          the name of the Registry to refresh
   */
  protected void refresh(String regName) {

    if (logger.isDebugEnabled()) {
      logger.debug("RegistryService: Updating the " + regName + " registry");
    }

    int count = 0;
    int counDeleted = 0;
    LocalRegistry registry = (LocalRegistry) get(regName);

    if (registry == null) {
      logger.error("RegistryService: Null " + name + " registry in refresh");
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
        if (logger.isDebugEnabled()) {
          logger.debug("RegistryService: Skipping fragment " + location);
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
                if (logger.isDebugEnabled()) {
                  logger.debug("RegistryService: No changes to entry "
                    + entry.getName());
                }
              } else {
                if (logger.isDebugEnabled()) {
                  logger.debug("RegistryService: Updating entry "
                    + entry.getName()
                    + " of class "
                    + entry.getClass()
                    + " to registry "
                    + name);
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

              if (logger.isDebugEnabled()) {
                logger.debug("RegistryService: Adding entry "
                  + entry.getName()
                  + " of class "
                  + entry.getClass()
                  + " to registry "
                  + name);
              }
            }
          } catch (RegistryException e) {
            logger.error("RegistryService: RegistryException while adding "
              + entry.getName()
              + "from "
              + location, e);
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

      if (logger.isDebugEnabled()) {
        logger.debug("RegistryService: removing entry " + entryName);
      }

      registry.removeLocalEntry(entryName);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("RegistryService: Merged "
        + count
        + " entries and deleted "
        + toDelete.size()
        + " in "
        + name);
    }
  }

  /** FileFilter implementing a file extension based filter */
  class ExtFileFilter implements FileFilter {
    private String extension = null;

    ExtFileFilter(String extension) {
      this.extension = extension;
    }

    public boolean accept(File f) {
      return f.toString().endsWith(extension);
    }
  }

}
