/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.services.psmlmanager.db;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.DeleteQuery;
import org.apache.cayenne.query.QualifiedQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedGroupFactory;
import org.apache.jetspeed.om.security.JetspeedRoleFactory;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.JetspeedUserFactory;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.psmlmanager.PsmlImporter;
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.jetspeed.services.psmlmanager.db.DBUtils;
import org.apache.jetspeed.services.psmlmanager.db.DatabasePsmlManager;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.ServletService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.exolab.castor.mapping.Mapping;
import org.xml.sax.InputSource;

import com.aimluck.eip.cayenne.om.account.JetspeedGroupProfile;
import com.aimluck.eip.cayenne.om.account.JetspeedRoleProfile;
import com.aimluck.eip.cayenne.om.account.JetspeedUserProfile;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.orm.Database;

/**
 * This service is responsible for loading and saving PSML documents. It uses
 * database to persist the PSML documents.
 * 
 * @author <a href="mailto:adambalk@cisco.com">Atul Dambalkar</a>
 * @author <a href="mailto:mvaidya@cisco.com">Medha Vaidya</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: CayenneDatabasePsmlManagerService.java,v 1.35 2004/02/23
 *          03:32:19 jford Exp $
 */
public class CayenneDatabasePsmlManagerService extends TurbineBaseService
    implements DatabasePsmlManager {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CayenneDatabasePsmlManagerService.class.getName());

  private final Map<String, PSMLDocument> psmlCache =
    new HashMap<String, PSMLDocument>();

  /** The watcher for the document locations */
  private CacheRefresher refresher = null;

  /** the base refresh rate for documents */
  private long refreshRate; // default will be 8 hours

  private final static String REFRESH_RATE = "refresh-rate";

  private final static long DEFAULT_REFRESH_RATE = 60 * 60 * 8 * 1000; // 8hrs

  /** whether caching is allowed */
  private boolean cachingOn; // default will be false

  private final static String CACHING_ON = "caching-on";

  private final static boolean DEFAULT_CACHING_ON = false;

  private final static String POOL_NAME = "database";

  // castor mapping
  public static final String DEFAULT_MAPPING =
    "${webappRoot}/WEB-INF/conf/psml-mapping.xml";

  String mapFile = null;

  /** the Castor mapping file name */
  private Mapping mapping = null;

  /** The pool name to use for database requests. */
  @SuppressWarnings("unused")
  private String poolName = null;

  /**
   * This is the early initialization method called by the Turbine
   * <code>Service</code> framework
   */
  @Override
  public void init(ServletConfig conf) throws InitializationException {
    if (getInit()) {
      return;
    }

    logger.info("Initializing CayenneDatabasePsmlManagerService...");
    initConfiguration(conf);

    logger.info("Done initializing CayenneDatabasePsmlManagerService.");

  }

  /**
   * Loads the configuration parameters for this service from the
   * JetspeedResources.properties file.
   * 
   * @exception throws a <code>InitializationException</code> if the service
   *            fails to initialize
   */
  private void initConfiguration(ServletConfig conf)
      throws InitializationException {

    // Ensure that the servlet service is initialized
    TurbineServices
      .getInstance()
      .initService(ServletService.SERVICE_NAME, conf);

    ResourceService serviceConf =
      ((TurbineServices) TurbineServices.getInstance())
        .getResources(PsmlManagerService.SERVICE_NAME);
    try {
      // get configuration parameters from Turbine Resources
      // we'll use only string accessors so the values can be multiply
      // specified in the properties files (the first one wins).
      String value = serviceConf.getString(REFRESH_RATE);
      refreshRate = DEFAULT_REFRESH_RATE;
      try {
        refreshRate = Long.parseLong(value);
      } catch (Exception e) {
        logger
          .warn("CayenneDatabasePsmlManagerService: error in refresh-rate configuration: using default");
      }

      // get the name of the torque database pool to use
      poolName = serviceConf.getString(POOL_NAME);

      // find out if caching allowed
      value = serviceConf.getString(CACHING_ON);
      cachingOn = DEFAULT_CACHING_ON;
      try {
        cachingOn = value.equals("true");
      } catch (Exception e) {
        logger
          .warn("CayenneDatabasePsmlManagerService: error in caching-on configuration: using default");
      }

      // psml castor mapping file
      mapFile = serviceConf.getString("mapping", DEFAULT_MAPPING);
      mapFile = TurbineServlet.getRealPath(mapFile);
      loadMapping();
    } catch (Throwable t) {
      logger.error(this + ".init:", t);
      throw new InitializationException(
        "Exception initializing CayenneDatabasePsmlManagerService" + t);
    }

    if (cachingOn) {
      this.refresher = new CacheRefresher();
      refresher.start();
    }

  }

  /** Late init method from Turbine Service model */
  @Override
  public void init() throws InitializationException {
    // Mark that we are done
    setInit(true);

    try {

      PsmlManagerService exporterService =
        (PsmlManagerService) TurbineServices.getInstance().getService(
          "PsmlImportManager");

      PsmlImporter importer = new PsmlImporter();
      importer.run(exporterService, this);

    } catch (Exception e) {
      logger.warn(
        "CayenneDatabasePsmlManagerService.init: exception while importing:",
        e);
    }

  }

  protected void loadMapping() throws InitializationException {
    // test the mapping file and create the mapping object

    if (mapFile != null) {
      File map = new File(mapFile);
      if (logger.isDebugEnabled()) {
        logger.debug("Loading psml mapping file " + mapFile);
      }
      if (map.exists() && map.isFile() && map.canRead()) {
        try {
          mapping = new Mapping();
          InputSource is = new InputSource(new FileReader(map));
          is.setSystemId(mapFile);
          mapping.loadMapping(is);
        } catch (Exception e) {
          logger.error("Error in psml mapping creation", e);
          throw new InitializationException("Error in mapping", e);
        }
      } else {
        throw new InitializationException(
          "PSML Mapping not found or not a file or unreadable: " + mapFile);
      }
    }
  }

  /**
   * This is the shutdown method called by the Turbine <code>Service</code>
   * framework
   */
  @Override
  public void shutdown() {
    if (this.refresher != null) {
      this.refresher.setDone(true);
    }
  }

  /**
   * A thread implementation of cache refreshing mechanism for database
   * persisted PSMLs. We have to refresh the cache after specific intervals if
   * someone manually updates the PSML database.
   * 
   * @author <a href="mailto:adambalk@cisco.com">Atul Dambalkar</a>
   */
  class CacheRefresher extends Thread {
    private boolean done = false;

    /**
     * Constructor to to set the priority.
     */
    CacheRefresher() {
      setDaemon(true);
      setPriority(Thread.MIN_PRIORITY + 1);
    }

    /**
     * We are all done, system is shutting down.
     */
    void setDone(boolean done) {
      this.done = done;
    }

    /**
     * Method as needed for a Thread to run
     */
    @Override
    public void run() {
      try {
        while (!done) {
          if (logger.isDebugEnabled()) {
            logger.debug("Cache Refresher thread sleeping now!");
          }
          sleep(refreshRate);
          if (logger.isDebugEnabled()) {
            logger.debug("Cache Refresher thread working now!");
          }

          try {
            synchronized (this) {
              Iterator<String> i = psmlCache.keySet().iterator();

              while (i.hasNext()) {
                String locator = i.next();

                // do refresh for the locator
                PSMLDocument doc = refresh(stringToLocator(locator));

                // over write the existing document in cache
                psmlCache.put(locator, doc);
              }
            }
          } catch (Exception e) {
            logger
              .warn(
                "CayenneDatabasePsmlManagerService.CacheRefresher: Error in cache refresher...",
                e);
          }
        }
      } catch (InterruptedException e) {
        if (logger.isDebugEnabled()) {
          logger
            .debug("CayenneDatabasePsmlManagerService.CacheRefresher: recieved interruption, aborting.");
        }
      }
    }
  }

  /**
   * Return a unique string identifying this object.
   */
  private String locatorToString(ProfileLocator locator) {
    StringBuffer keybuf = new StringBuffer();

    JetspeedUser user = locator.getUser();
    Role role = locator.getRole();
    Group group = locator.getGroup();
    String name = locator.getName();
    String mediaType = locator.getMediaType();
    String country = locator.getCountry();
    String language = locator.getLanguage();

    synchronized (this) {
      if (user != null) {
        keybuf.append("User:").append(user.getUserName());
      } else if (group != null) {
        keybuf.append("Group:").append(group.getName());
      } else if (role != null) {
        keybuf.append("Role:").append(role.getName());
      }

      if (name != null) {
        keybuf.append('$').append("Page:").append(name);
      }

      if (mediaType != null) {
        keybuf.append('$').append("MediaType:").append(mediaType);
      }
      if (country != null && (!country.equals("-1"))) {
        keybuf.append('$').append("Country:").append(country);
      }
      if (language != null && (!language.equals("-1"))) {
        keybuf.append('$').append("Language:").append(language);
      }
    }
    if (logger.isDebugEnabled()) {
      logger
        .debug("CayenneDatabasePsmlManagerService: Returning locator string: "
          + keybuf.toString());
    }

    return keybuf.toString();
  }

  private ProfileLocator stringToLocator(String locstr) throws Exception {
    ProfileLocator locator = Profiler.createLocator();
    String entity = null;

    if (logger.isDebugEnabled()) {
      logger
        .debug("CayenneDatabasePsmlManagerService: Creating locator for string: "
          + locstr);
    }

    StringTokenizer dollarTokens = new StringTokenizer(locstr, "$");
    while (dollarTokens.hasMoreTokens()) {
      String dollarToken = dollarTokens.nextToken().trim();

      StringTokenizer colonTokens = new StringTokenizer(dollarToken, ":");
      String colonToken = colonTokens.nextToken();
      if (colonToken.equals("User")) {
        entity = colonTokens.nextToken().trim();
        locator.setUser(JetspeedSecurity.getUser(entity));
      } else if (colonToken.equals("Group")) {
        entity = colonTokens.nextToken().trim();
        locator.setGroup(JetspeedSecurity.getGroup(entity));
      } else if (colonToken.equals("Role")) {
        entity = colonTokens.nextToken().trim();
        locator.setRole(JetspeedSecurity.getRole(entity));
      } else if (colonToken.equals("Page")) {
        entity = colonTokens.nextToken().trim();
        locator.setName(entity);
      } else if (colonToken.equals("MediaType")) {
        entity = colonTokens.nextToken().trim();
        locator.setMediaType(entity);
      } else if (colonToken.equals("Country")) {
        entity = colonTokens.nextToken().trim();
        locator.setCountry(entity);
      } else if (colonToken.equals("Language")) {
        entity = colonTokens.nextToken().trim();
        locator.setLanguage(entity);
      }
    }
    if (logger.isDebugEnabled()) {
      logger
        .debug("CayenneDatabasePsmlManagerService: Returning locator for string: "
          + locatorToString(locator));
    }

    return locator;

  }

  @Override
  public PSMLDocument getDocument(String name) {
    // do nothing, deprecated
    logger.warn("*** NOT SUPPORTED: GETDOC FROM DATABASE PSML MANAGER!!!");
    return null;
  }

  @Override
  public boolean saveDocument(String fileOrUrl, PSMLDocument doc) {
    // do nothing, deprecated
    logger.warn("*** NOT SUPPORTED: SAVING DOC FROM DATABASE PSML MANAGER!!!");
    return false;
  }

  @Override
  public boolean saveDocument(PSMLDocument doc) {
    // do nothing, will be deprecated
    logger.warn("*** NOT SUPPORTED: SAVING DOC FROM DATABASE PSML MANAGER!!!");
    return false;
  }

  /**
   * Returns a PSML document for the given locator
   * 
   * @param locator
   *          The locator descriptor(ProfileLocator object) of the document to
   *          be retrieved.
   * @return psmldoc The PSMLDocument object
   */
  @Override
  public PSMLDocument getDocument(ProfileLocator locator) {
    // check the cache for the req'e document if not available in cache
    // get the document from database

    if (locator == null) {
      String message = "PSMLManager: Must specify a locator";
      logger.warn("CayenneDatabasePsmlManagerService.getDocument: " + message);
      throw new IllegalArgumentException(message);
    }

    PSMLDocument psmldoc = null;
    String locStr = locatorToString(locator);
    boolean inCache = false;

    if (cachingOn) {
      synchronized (psmlCache) {
        // psmldoc = (PSMLDocument)psmlCache.get(locatorToString(locator));
        // if we have seached and found nothing, this is cached as a null value
        // so check to see if the key is there
        inCache = psmlCache.containsKey(locStr);
        if (inCache) {
          psmldoc = psmlCache.get(locStr);
        }
      }
      // if (Log.getLogger().isDebugEnabled())
      // Log.info("CayenneDatabasePsmlManagerService.getDocument(): psmlcache: "
      // +
      // (inCache ? ((psmldoc == null) ? "null present" : "doc present") :
      // "not in cache") + " : " + locStr);

      // if in the cache, doc or null, return what's in the cache
      if (inCache) {
        return psmldoc;
      }
    }

    try {
      return refresh(locator);
    } catch (Exception e) {
      logger.warn(
        "CayenneDatabasePsmlManagerService.getDocument: exception:",
        e);
      throw new RuntimeException("Could not get profile from DB");
    }
  }

  /**
   * Stores the PSML document in DB for the given profile
   * 
   * @param profile
   *          The profile that holds the PSMLDocument.
   * @return PSMLDocument The PSMLDocument that got created in DB.
   */
  @Override
  public PSMLDocument createDocument(Profile profile) {
    return createOrSaveDocument(profile, INSERT);
  }

  /**
   * Update the PSML document in DB for the given profile
   * 
   * @param profile
   *          The profile that holds the PSMLDocument.
   * @return PSMLDocument The PSMLDocument that got created in DB.
   */
  @Override
  public boolean store(Profile profile) {
    return createOrSaveDocument(profile, UPDATE) != null;
  }

  private PSMLDocument createOrSaveDocument(Profile profile, int operation) {
    // create record in the database for Portlets for the given
    // profile/PSMLDocuemnt,use marsheller to create Portlets
    // object and then put it in database, update the cache
    if (profile == null) {
      String message = "PSMLManager: Must specify a profile";
      logger.warn("CayenneDatabasePsmlManagerService.createOrSaveDocument: "
        + message);
      throw new IllegalArgumentException(message);
    }

    JetspeedUser user = profile.getUser();
    Role role = profile.getRole();
    Group group = profile.getGroup();
    String tableName = null;

    try {
      DataContext dataContext = DataContext.getThreadDataContext();
      if (user != null) {
        tableName = "JETSPEED_USER_PROFILE";
        if (operation == INSERT) {
          insertJetspeedUserProfilePeer(dataContext, profile);
        } else if (operation == UPDATE) {
          updateJetspeedUserProfilePeer(dataContext, profile);
        }
      } else if (role != null) {
        tableName = "JETSPEED_ROLE_PROFILE";
        if (operation == INSERT) {
          insertJetspeedRoleProfilePeer(dataContext, profile);
        } else if (operation == UPDATE) {
          updateJetspeedRoleProfilePeer(dataContext, profile);
        }
      } else if (group != null) {
        tableName = "JETSPEED_GROUP_PROFILE";
        if (operation == INSERT) {
          insertJetspeedGroupProfilePeer(dataContext, profile);
        } else if (operation == UPDATE) {
          updateJetspeedGroupProfilePeer(dataContext, profile);
        }
      }

      if (cachingOn) {
        // insert successful
        synchronized (psmlCache) {
          if (logger.isDebugEnabled()) {
            logger
              .debug("CayenneDatabasePsmlManagerService.createOrSaveDocument: caching document: profile: "
                + locatorToString(profile));
          }
          psmlCache.put(locatorToString(profile), profile.getDocument());
        }
      }

      return profile.getDocument();
    } catch (Exception e) // insert failed
    {
      Database.rollback();
      logger.warn(
        "CayenneDatabasePsmlManagerService.createOrSaveDocument: profile: "
          + profile
          + " tableName: "
          + tableName,
        e);
      throw new RuntimeException("Could not create new profile in DB");
    } finally {
    }

  }

  /**
   * Remove the PSMLDocument/profile for given locator object.
   * 
   * @param locator
   *          The profile locator criteria for profile to be removed.
   */
  @Override
  public void removeDocument(ProfileLocator locator) {
    if (locator == null) {
      String message = "PSMLManager: Must specify a locator";
      logger.warn("CayenneDatabasePsmlManagerService.removeDocument: "
        + message);
      throw new IllegalArgumentException(message);
    }

    JetspeedUser user = locator.getUser();
    Role role = locator.getRole();
    Group group = locator.getGroup();
    String tableName = null;

    try {
      DataContext dataContext = DataContext.getThreadDataContext();
      if (user != null) {
        deleteJetspeedUserProfilePeer(dataContext, locator);
        tableName = "JETSPEED_USER_PROFILE";
      } else if (role != null) {
        deleteJetspeedRoleProfilePeer(dataContext, locator);
        tableName = "JETSPEED_ROLE_PROFILE";
      } else if (group != null) {
        deleteJetspeedGroupProfilePeer(dataContext, locator);
        tableName = "JETSPEED_GROUP_PROFILE";
      }

      if (cachingOn) {
        // Delete successful
        synchronized (psmlCache) {
          psmlCache.remove(locatorToString(locator));
        }
      }
    } catch (Exception e) // insert failed
    {
      logger.warn("CayenneDatabasePsmlManagerService.removeDocument: profile: "
        + locatorToString(locator)
        + " tableName: "
        + tableName, e);
      throw new RuntimeException(
        "Could not delete profile for given locator from DB");
    } finally {
      // make sure to release the database connection
    }
  }

  /**
   * Query for a collection of profiles given a profile locator criteria. Use
   * SQL engine to get the required profiles.
   * 
   * @param locator
   *          The profile locator criteria.
   * @return Iterator object with the PSMLDocuments satisfying query
   */
  @Override
  public Iterator<Profile> query(QueryLocator locator) {
    if (locator == null) {
      String message = "PSMLManager: Must specify a locator";
      logger.warn("CayenneDatabasePsmlManagerService.query: " + message);
      throw new IllegalArgumentException(message);
    }

    try {
      DataContext dataContext = DataContext.getThreadDataContext();
      List<?> userData = null;
      List<?> groupData = null;
      List<?> roleData = null;

      int queryMode = locator.getQueryMode();

      List<Profile> list = new ArrayList<Profile>();

      switch (queryMode) {
        case QueryLocator.QUERY_USER:
          userData = selectJetspeedUserProfilePeer(dataContext, locator, true);
          if (userData != null) {
            list = getProfiles(userData);
          }
          break;

        case QueryLocator.QUERY_GROUP:
          groupData =
            selectJetspeedGroupProfilePeer(dataContext, locator, true);
          if (groupData != null) {
            list = getProfiles(groupData);
          }
          break;

        case QueryLocator.QUERY_ROLE:
          roleData = selectJetspeedRoleProfilePeer(dataContext, locator, true);
          if (roleData != null) {
            list = getProfiles(roleData);
          }
          break;

        default: // QUERY_ALL
          userData = selectJetspeedUserProfilePeer(dataContext, locator, true);
          if (userData != null) {
            list.addAll(getProfiles(userData));
          }

          groupData =
            selectJetspeedGroupProfilePeer(dataContext, locator, true);
          if (groupData != null) {
            list.addAll(getProfiles(groupData));
          }

          roleData = selectJetspeedRoleProfilePeer(dataContext, locator, true);
          if (roleData != null) {
            list.addAll(getProfiles(roleData));
          }

          break;
      }

      return list.iterator();
    } catch (Exception e) {
      logger.warn("CayenneDatabasePsmlManagerService.query: exception", e);
    } finally {
      // make sure to release the databased connection

    }

    return new ArrayList<Profile>().iterator(); // return empty non-null
    // iterator
  }

  /**
   * Get profile iterator from given list of objects.
   * 
   * @param data
   *          List of JetspeedUserProfile, JetspeedGroupProfile,
   *          JetspeedRoleProfile, objects
   * @return List of profiles
   */
  private List<Profile> getProfiles(List<?> data) {
    List<Profile> list = new ArrayList<Profile>();

    for (int i = 0; i < data.size(); i++) {
      Object obj = data.get(i);
      Portlets portlets = null;

      if (obj instanceof JetspeedUserProfile) {
        portlets =
          DBUtils.bytesToPortlets(
            ((JetspeedUserProfile) obj).getProfile(),
            this.mapping);
        list.add(createUserProfile((JetspeedUserProfile) obj, portlets));
      } else if (obj instanceof JetspeedGroupProfile) {
        portlets =
          DBUtils.bytesToPortlets(
            ((JetspeedGroupProfile) obj).getProfile(),
            this.mapping);
        list.add(createGroupProfile((JetspeedGroupProfile) obj, portlets));
      } else if (obj instanceof JetspeedRoleProfile) {
        portlets =
          DBUtils.bytesToPortlets(
            ((JetspeedRoleProfile) obj).getProfile(),
            this.mapping);
        list.add(createRoleProfile((JetspeedRoleProfile) obj, portlets));
      }

    }
    return list;
  }

  /**
   * Get PSMLDocument object for given pagename and portlets.
   * 
   * @param portlets
   *          Portlets for the given page name
   * @param page
   *          page name for this resource
   * @return PSMLDocument object for given page and portlets
   */
  private PSMLDocument getPSMLDocument(String page, Portlets portlets) {
    PSMLDocument psmldoc = new BasePSMLDocument();
    psmldoc.setName(page);
    psmldoc.setPortlets(portlets);
    return psmldoc;
  }

  /**
   * Given ordered list of locators, find the first document matching a profile
   * locator, starting from the beginning of the list and working to the end.
   * 
   * @param locator
   *          The ordered list of profile locators.
   * @return PSMLDocument object for the first document matching a locator
   */
  @Override
  @SuppressWarnings("rawtypes")
  public PSMLDocument getDocument(List locators) {
    if (locators == null) {
      String message = "PSMLManager: Must specify a list of locators";
      logger.warn("CayenneDatabasePsmlManagerService.getDocument: " + message);
      throw new IllegalArgumentException(message);
    }

    // iterate over the list and invoke getDocument(locator) method
    for (int i = 0; i < locators.size(); i++) {
      PSMLDocument psmldoc = getDocument((ProfileLocator) locators.get(i));
      if (psmldoc != null) {
        return psmldoc;
      }
    }
    return null;
  }

  /**
   * Returns a PSML document for the given locator, it is called by the cache
   * refresher
   * 
   * @param locator
   *          The locator descriptor(ProfileLocator object) of the document to
   *          be retrieved.
   * @return psmldoc The PSMLDocument object
   */
  @Override
  public PSMLDocument refresh(ProfileLocator locator) {
    // go to database and get the blob, and marshal the Portlets

    if (locator == null) {
      String message = "PSMLManager: Must specify a locator";
      logger.warn("CayenneDatabasePsmlManagerService.refresh: " + message);
      throw new IllegalArgumentException(message);
    }

    JetspeedUser user = locator.getUser();
    Role role = locator.getRole();
    Group group = locator.getGroup();
    String tableName = null;
    List<?> records = null;
    Portlets portlets = null;
    PSMLDocument psmldoc = null;
    String page = null;

    DataContext dataContext = DataContext.getThreadDataContext();
    if (user != null) {
      tableName = "JETSPEED_USER_PROFILE";
      records = selectJetspeedUserProfilePeer(dataContext, locator, false);
      Iterator<?> iterator = records.iterator();
      while (iterator.hasNext()) {
        JetspeedUserProfile uprofile = (JetspeedUserProfile) iterator.next();
        page = uprofile.getPage();
        portlets = DBUtils.bytesToPortlets(uprofile.getProfile(), this.mapping);
      }
    } else if (role != null) {
      tableName = "JETSPEED_ROLE_PROFILE";
      records = selectJetspeedRoleProfilePeer(dataContext, locator, false);
      Iterator<?> iterator = records.iterator();
      while (iterator.hasNext()) {
        JetspeedRoleProfile rprofile = (JetspeedRoleProfile) iterator.next();
        page = rprofile.getPage();
        portlets = DBUtils.bytesToPortlets(rprofile.getProfile(), this.mapping);
      }
    } else if (group != null) {
      tableName = "JETSPEED_GROUP_PROFILE";
      records = selectJetspeedGroupProfilePeer(dataContext, locator, false);
      Iterator<?> iterator = records.iterator();
      while (iterator.hasNext()) {
        JetspeedGroupProfile gprofile = (JetspeedGroupProfile) iterator.next();
        page = gprofile.getPage();
        portlets = DBUtils.bytesToPortlets(gprofile.getProfile(), this.mapping);
      }
    }

    if (page != null && portlets != null) {
      psmldoc = getPSMLDocument(page, portlets);
      if (cachingOn) {
        synchronized (psmlCache) {
          if (logger.isDebugEnabled()) {
            logger
              .debug("CayenneDatabasePsmlManagerService.refresh: caching document: profile: "
                + locatorToString(locator));
          }
          psmlCache.put(locatorToString(locator), psmldoc);
        }
      }
      return psmldoc;
    } else {
      if (cachingOn) {
        // cache the fact that there is NO document matching this profile
        psmlCache.put(locatorToString(locator), null);
        if (logger.isDebugEnabled()) {
          logger
            .debug("CayenneDatabasePsmlManagerService.refresh: caching 'document not found': profile: "
              + locatorToString(locator));
        }
      }
    }

    if (logger.isDebugEnabled()) {
      logger
        .debug("CayenneDatabasePsmlManagerService.refresh: no document found: profile: "
          + locatorToString(locator));
    }
    return null;
  }

  /**
   * Removes all documents for a given user.
   * 
   * @param user
   *          The user object.
   */
  @Override
  public void removeUserDocuments(JetspeedUser user) {
    try {
      DataContext dataContext = DataContext.getThreadDataContext();
      if (user != null) {
        deleteJetspeedUserProfilePeer(dataContext, user);
      }
    } catch (Exception e) // delete failed
    {
      logger.warn(
        "CayenneDatabasePsmlManagerService.removeUserDocuments: exception:",
        e);
      throw new RuntimeException(
        "Could not delete documents for given user from DB");
    } finally {
      // make sure to release the database connection
    }

  }

  /**
   * Removes all documents for a given role.
   * 
   * @param role
   *          The role object.
   */
  @Override
  public void removeRoleDocuments(Role role) {
    try {
      if (role != null) {
        DataContext dataContext = DataContext.getThreadDataContext();
        deleteJetspeedRoleProfilePeer(dataContext, role);
      }
    } catch (Exception e) // delete failed
    {
      logger.warn(
        "CayenneDatabasePsmlManagerService.removeRoleDocuments: exception:",
        e);
      throw new RuntimeException(
        "Could not delete documents for given role from DB");
    } finally {
      // make sure to release the database connection
    }
  }

  /**
   * Removes all documents for a given group.
   * 
   * @param group
   *          The group object.
   */
  @Override
  public void removeGroupDocuments(Group group) {
    try {
      if (group != null) {
        DataContext dataContext = DataContext.getThreadDataContext();
        deleteJetspeedGroupProfilePeer(dataContext, group);
      }
    } catch (Exception e) // delete failed
    {
      logger.warn(
        "CayenneDatabasePsmlManagerService.removeGroupDocuments: exception:",
        e);
      throw new RuntimeException(
        "Could not delete documents for given group from DB");
    } finally {
      // make sure to release the database connection
    }
  }

  /**
   * Query for a collection of profiles given a profile locator criteria. This
   * method should be used when importing or exporting profiles between
   * services.
   * 
   * @param locator
   *          The profile locator criteria.
   * @return The count of profiles exported.
   */
  @Override
  public int export(PsmlManagerService consumer, QueryLocator locator) {
    Iterator<Profile> profiles = null;
    int count = 0;
    try {
      profiles = query(locator);

      while (profiles.hasNext()) {
        Profile profile = profiles.next();
        // dumpProfile(profile);
        try {
          consumer.createDocument(profile);
          count++;
        } catch (Exception ex) {
          try {
            consumer.store(profile);
            count++;
          } catch (Exception e) {
            logger.warn("CayenneDatabasePsmlManagerService.export: profile: "
              + profile, ex);
          }
        }
      }
    } catch (Exception e) {
      logger.warn("CayenneDatabasePsmlManagerService.export: exception:", e);

    } finally {
    }
    return count;
  }

  @Override
  public Mapping getMapping() {
    return this.mapping;
  }

  /**
   * Creates a user profile from a JetspeedUserProfile database object.
   * 
   * @param entity
   *          The user profile entity in the database.
   * @param portlets
   *          The PSML blob.
   * @return A new profile object representing the locator and PSML blob.
   */
  public Profile createUserProfile(JetspeedUserProfile entity, Portlets portlets) {
    Profile profile = Profiler.createProfile();
    try {
      JetspeedUser user = JetspeedSecurity.getUser(entity.getUserName());
      if (null == user) {
        user = JetspeedUserFactory.getInstance();
        user.setUserName(entity.getUserName());
      }
      profile.setUser(user);

      profile.setMediaType(entity.getMediaType());
      profile.setLanguage(entity.getLanguage());
      profile.setCountry(entity.getCountry());
      profile.setName(entity.getPage());
      profile.setDocument(getPSMLDocument(entity.getPage(), portlets));
    } catch (JetspeedSecurityException e) {
    }
    return profile;
  }

  /**
   * Creates a group profile from a JetspeedGroupProfile database object.
   * 
   * @param entity
   *          The group profile entity in the database.
   * @param portlets
   *          The PSML blob.
   * @return A new profile object representing the locator and PSML blob.
   */
  public Profile createGroupProfile(JetspeedGroupProfile entity,
      Portlets portlets) {
    Profile profile = Profiler.createProfile();
    try {
      Group group = JetspeedSecurity.getGroup(entity.getGroupName());
      if (null == group) {
        group = JetspeedGroupFactory.getInstance();
        group.setName(entity.getGroupName());
      }
      profile.setGroup(group);
      profile.setMediaType(entity.getMediaType());
      profile.setLanguage(entity.getLanguage());
      profile.setCountry(entity.getCountry());
      profile.setName(entity.getPage());
      profile.setDocument(getPSMLDocument(entity.getPage(), portlets));
    } catch (JetspeedSecurityException e) {
    }
    return profile;
  }

  /**
   * Creates a role profile from a JetspeedRoleProfile database object.
   * 
   * @param entity
   *          The group profile entity in the database.
   * @param portlets
   *          The PSML blob.
   * @return A new profile object representing the locator and PSML blob.
   */
  public Profile createRoleProfile(JetspeedRoleProfile entity, Portlets portlets) {
    Profile profile = Profiler.createProfile();
    try {
      Role role = JetspeedSecurity.getRole(entity.getRoleName());
      if (null == role) {
        role = JetspeedRoleFactory.getInstance();
        role.setName(entity.getRoleName());
      }
      profile.setRole(role);
      profile.setMediaType(entity.getMediaType());
      profile.setLanguage(entity.getLanguage());
      profile.setCountry(entity.getCountry());
      profile.setName(entity.getPage());
      profile.setDocument(getPSMLDocument(entity.getPage(), portlets));
    } catch (JetspeedSecurityException e) {
    }
    return profile;
  }

  private void updateJetspeedGroupProfilePeer(DataContext dataContext,
      Profile profile) throws CayenneRuntimeException {
    JetspeedGroupProfile groupProfile =
      this.selectJetspeedGroupProfilePeer(dataContext, profile, false).get(0);

    groupProfile.setGroupName(profile.getGroup().getName());
    groupProfile.setMediaType(profile.getMediaType());

    String language = profile.getLanguage();
    if (language != null && (!language.equals("-1"))) {
      groupProfile.setLanguage(language);
    } else {
      groupProfile.setLanguage(null);
    }

    String country = profile.getCountry();
    if (country != null && (!country.equals("-1"))) {
      groupProfile.setCountry(country);
    } else {
      groupProfile.setCountry(null);
    }
    String name = profile.getName();
    if (name == null || name.equals("")) {
      profile.setName(Profiler.FULL_DEFAULT_PROFILE);
    } else if (!name.endsWith(Profiler.DEFAULT_EXTENSION)) {
      profile.setName(name + Profiler.DEFAULT_EXTENSION);
    }
    groupProfile.setPage(profile.getName());
    groupProfile.setProfile(DBUtils.portletsToBytes(profile
      .getDocument()
      .getPortlets(), this.getMapping()));

    Database.commit(dataContext);
  }

  private void insertJetspeedGroupProfilePeer(DataContext dataContext,
      Profile profile) throws CayenneRuntimeException {
    JetspeedGroupProfile groupProfile =
      (JetspeedGroupProfile) dataContext
        .createAndRegisterNewObject(JetspeedGroupProfile.class);

    groupProfile.setGroupName(profile.getGroup().getName());
    groupProfile.setMediaType(profile.getMediaType());

    String language = profile.getLanguage();
    if (language != null && (!language.equals("-1"))) {
      groupProfile.setLanguage(language);
    } else {
      groupProfile.setLanguage(null);
    }

    String country = profile.getCountry();
    if (country != null && (!country.equals("-1"))) {
      groupProfile.setCountry(country);
    } else {
      groupProfile.setCountry(null);
    }
    String name = profile.getName();
    if (name == null || name.equals("")) {
      profile.setName(Profiler.FULL_DEFAULT_PROFILE);
    } else if (!name.endsWith(Profiler.DEFAULT_EXTENSION)) {
      profile.setName(name + Profiler.DEFAULT_EXTENSION);
    }
    groupProfile.setPage(profile.getName());
    groupProfile.setProfile(DBUtils.portletsToBytes(profile
      .getDocument()
      .getPortlets(), this.getMapping()));

    Database.commit(dataContext);
  }

  private void updateJetspeedRoleProfilePeer(DataContext dataContext,
      Profile profile) throws CayenneRuntimeException {
    JetspeedRoleProfile roleProfile =
      selectJetspeedRoleProfilePeer(dataContext, profile, false).get(0);
    roleProfile.setRoleName(profile.getRole().getName());
    roleProfile.setMediaType(profile.getMediaType());
    String language = profile.getLanguage();
    if (language != null && (!language.equals("-1"))) {
      roleProfile.setLanguage(language);
    } else {
      roleProfile.setLanguage(null);
    }
    String country = profile.getCountry();
    if (country != null && (!country.equals("-1"))) {
      roleProfile.setCountry(country);
    } else {
      roleProfile.setCountry(null);
    }
    String name = profile.getName();
    if (name == null || name.equals("")) {
      profile.setName(Profiler.FULL_DEFAULT_PROFILE);
    } else if (!name.endsWith(Profiler.DEFAULT_EXTENSION)) {
      profile.setName(name + Profiler.DEFAULT_EXTENSION);
    }
    roleProfile.setPage(profile.getName());
    roleProfile.setProfile(DBUtils.portletsToBytes(profile
      .getDocument()
      .getPortlets(), this.getMapping()));

    Database.commit(dataContext);
  }

  private void insertJetspeedRoleProfilePeer(DataContext dataContext,
      Profile profile) throws CayenneRuntimeException {
    JetspeedRoleProfile roleProfile =
      (JetspeedRoleProfile) dataContext
        .createAndRegisterNewObject(JetspeedRoleProfile.class);

    roleProfile.setRoleName(profile.getRole().getName());
    roleProfile.setMediaType(profile.getMediaType());

    String language = profile.getLanguage();
    if (language != null && (!language.equals("-1"))) {
      roleProfile.setLanguage(language);
    } else {
      roleProfile.setLanguage(null);
    }

    String country = profile.getCountry();
    if (country != null && (!country.equals("-1"))) {
      roleProfile.setCountry(country);
    } else {
      roleProfile.setCountry(null);
    }

    String name = profile.getName();
    if (name == null || name.equals("")) {
      profile.setName(Profiler.FULL_DEFAULT_PROFILE);
    } else if (!name.endsWith(Profiler.DEFAULT_EXTENSION)) {
      profile.setName(name + Profiler.DEFAULT_EXTENSION);
    }
    roleProfile.setPage(profile.getName());
    roleProfile.setProfile(DBUtils.portletsToBytes(profile
      .getDocument()
      .getPortlets(), this.getMapping()));

    Database.commit(dataContext);
  }

  private void updateJetspeedUserProfilePeer(DataContext dataContext,
      Profile profile) throws CayenneRuntimeException {

    JetspeedUserProfile userProfile =
      this.selectJetspeedUserProfilePeer(dataContext, profile, false).get(0);
    userProfile.setUserName(profile.getUser().getUserName());
    userProfile.setMediaType(profile.getMediaType());

    String language = profile.getLanguage();
    if (language != null && (!language.equals("-1"))) {
      userProfile.setLanguage(language);
    } else {
      userProfile.setLanguage(null);
    }

    String country = profile.getCountry();
    if (country != null && (!country.equals("-1"))) {
      userProfile.setCountry(country);
    } else {
      userProfile.setCountry(null);
    }

    String name = profile.getName();
    if (name == null || name.equals("")) {
      profile.setName(Profiler.FULL_DEFAULT_PROFILE);
    } else if (!name.endsWith(Profiler.DEFAULT_EXTENSION)) {
      profile.setName(name + Profiler.DEFAULT_EXTENSION);
    }
    userProfile.setPage(profile.getName());
    userProfile.setProfile(DBUtils.portletsToBytes(profile
      .getDocument()
      .getPortlets(), this.getMapping()));

    Database.commit(dataContext);
  }

  private void insertJetspeedUserProfilePeer(DataContext dataContext,
      Profile profile) throws CayenneRuntimeException {
    JetspeedUserProfile userProfile =
      Database.create(dataContext, JetspeedUserProfile.class);

    userProfile.setUserName(profile.getUser().getUserName());
    userProfile.setMediaType(profile.getMediaType());

    String language = profile.getLanguage();
    if (language != null && (!language.equals("-1"))) {
      userProfile.setLanguage(language);
    } else {
      userProfile.setLanguage(null);
    }

    String country = profile.getCountry();
    if (country != null && (!country.equals("-1"))) {
      userProfile.setCountry(country);
    } else {
      userProfile.setCountry(null);
    }

    String name = profile.getName();
    if (name == null || name.equals("")) {
      profile.setName(Profiler.FULL_DEFAULT_PROFILE);
    } else if (!name.endsWith(Profiler.DEFAULT_EXTENSION)) {
      profile.setName(name + Profiler.DEFAULT_EXTENSION);
    }
    userProfile.setPage(profile.getName());
    userProfile.setProfile(DBUtils.portletsToBytes(profile
      .getDocument()
      .getPortlets(), this.getMapping()));

    Database.commit(dataContext);
  }

  private void deleteJetspeedGroupProfilePeer(DataContext dataContext,
      ProfileLocator locator) throws CayenneRuntimeException {
    DeleteQuery deleteQuery = new DeleteQuery(JetspeedGroupProfile.class);
    assignJetSpeedGroupProfileQuery(locator, deleteQuery);
    dataContext.performQuery(deleteQuery);
  }

  private void deleteJetspeedRoleProfilePeer(DataContext dataContext,
      ProfileLocator locator) throws CayenneRuntimeException {
    DeleteQuery deleteQuery = new DeleteQuery(JetspeedRoleProfile.class);
    assignJetSpeedRoleProfileQuery(locator, deleteQuery);
    dataContext.performQuery(deleteQuery);
  }

  private void deleteJetspeedUserProfilePeer(DataContext dataContext,
      ProfileLocator locator) throws CayenneRuntimeException {
    DeleteQuery deleteQuery = new DeleteQuery(JetspeedUserProfile.class);
    assignJetSpeedUserProfileQuery(locator, deleteQuery);
    dataContext.performQuery(deleteQuery);
  }

  private List<JetspeedGroupProfile> selectJetspeedGroupProfilePeer(
      DataContext dataContext, ProfileLocator locator, boolean order)
      throws CayenneRuntimeException {
    SelectQuery query = new SelectQuery(JetspeedGroupProfile.class);
    assignJetSpeedGroupProfileQuery(locator, query);
    if (order) {
      query.addOrdering(JetspeedGroupProfile.GROUP_NAME_PROPERTY, true);
      query.addOrdering(JetspeedGroupProfile.MEDIA_TYPE_PROPERTY, true);
      query.addOrdering(JetspeedGroupProfile.LANGUAGE_PROPERTY, true);
      query.addOrdering(JetspeedGroupProfile.COUNTRY_PROPERTY, true);
      query.addOrdering(JetspeedGroupProfile.PAGE_PROPERTY, true);
    }

    Database.beginTransaction(dataContext);
    @SuppressWarnings("unchecked")
    List<JetspeedGroupProfile> list = dataContext.performQuery(query);
    return list;
  }

  private List<JetspeedRoleProfile> selectJetspeedRoleProfilePeer(
      DataContext dataContext, ProfileLocator locator, boolean order)
      throws CayenneRuntimeException {
    SelectQuery query = new SelectQuery(JetspeedRoleProfile.class);
    assignJetSpeedRoleProfileQuery(locator, query);
    if (order && (query instanceof SelectQuery)) {
      query.addOrdering(JetspeedRoleProfile.ROLE_NAME_PROPERTY, true);
      query.addOrdering(JetspeedRoleProfile.MEDIA_TYPE_PROPERTY, true);
      query.addOrdering(JetspeedRoleProfile.LANGUAGE_PROPERTY, true);
      query.addOrdering(JetspeedRoleProfile.COUNTRY_PROPERTY, true);
      query.addOrdering(JetspeedRoleProfile.PAGE_PROPERTY, true);
    }

    Database.beginTransaction(dataContext);
    @SuppressWarnings("unchecked")
    List<JetspeedRoleProfile> list = dataContext.performQuery(query);
    return list;
  }

  @SuppressWarnings("unchecked")
  private List<JetspeedUserProfile> selectJetspeedUserProfilePeer(
      DataContext dataContext, ProfileLocator locator, boolean order)
      throws CayenneRuntimeException {
    Object obj = ALEipManager.getInstance().getUserProfile(locator);

    if (obj != null) {
      return (List<JetspeedUserProfile>) obj;
    } else {

      SelectQuery query = new SelectQuery(JetspeedUserProfile.class);
      assignJetSpeedUserProfileQuery(locator, query);
      if (order) {
        query.addOrdering(JetspeedUserProfile.USER_NAME_PROPERTY, true);
        query.addOrdering(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, true);
        query.addOrdering(JetspeedUserProfile.LANGUAGE_PROPERTY, true);
        query.addOrdering(JetspeedUserProfile.COUNTRY_PROPERTY, true);
        query.addOrdering(JetspeedUserProfile.PAGE_PROPERTY, true);
      }

      Database.beginTransaction(dataContext);
      List<JetspeedUserProfile> list = dataContext.performQuery(query);
      ALEipManager.getInstance().setUserProfile(locator, list);
      return list;
    }
  }

  private void deleteJetspeedUserProfilePeer(DataContext dataContext,
      JetspeedUser user) throws CayenneRuntimeException {
    DeleteQuery deleteQuery = new DeleteQuery(JetspeedUserProfile.class);
    Expression exp =
      ExpressionFactory.matchExp(JetspeedUserProfile.USER_NAME_PROPERTY, user
        .getUserName());
    deleteQuery.andQualifier(exp);
    dataContext.performQuery(deleteQuery);
  }

  private void deleteJetspeedRoleProfilePeer(DataContext dataContext, Role role)
      throws CayenneRuntimeException {
    DeleteQuery deleteQuery = new DeleteQuery(JetspeedRoleProfile.class);
    Expression exp =
      ExpressionFactory.matchExp(JetspeedRoleProfile.ROLE_NAME_PROPERTY, role
        .getName());
    deleteQuery.andQualifier(exp);
    dataContext.performQuery(deleteQuery);
  }

  private void deleteJetspeedGroupProfilePeer(DataContext dataContext,
      Group group) throws CayenneRuntimeException {
    DeleteQuery deleteQuery = new DeleteQuery(JetspeedGroupProfile.class);
    Expression exp =
      ExpressionFactory.matchExp(
        JetspeedGroupProfile.GROUP_NAME_PROPERTY,
        group.getName());
    deleteQuery.andQualifier(exp);
    dataContext.performQuery(deleteQuery);
  }

  private static QualifiedQuery assignJetSpeedUserProfileQuery(
      ProfileLocator locator, QualifiedQuery query) {
    String mediaType = locator.getMediaType();
    String language = locator.getLanguage();
    String country = locator.getCountry();
    String pageName = locator.getName();
    String userName = null;
    JetspeedUser user = locator.getUser();

    if (user != null) {
      userName = user.getUserName();
    }

    if (userName != null && userName.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedUserProfile.USER_NAME_PROPERTY,
          userName);
      query.andQualifier(exp);
    }

    if (pageName != null && pageName.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedUserProfile.PAGE_PROPERTY, pageName);
      query.andQualifier(exp);
    }

    if (mediaType != null && mediaType.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedUserProfile.MEDIA_TYPE_PROPERTY,
          mediaType);
      query.andQualifier(exp);
    }

    if (language != null && language.length() > 0 && (!language.equals("-1"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedUserProfile.LANGUAGE_PROPERTY,
          language);
      query.andQualifier(exp);
    } else if (language != null && language.equals("-1")) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedUserProfile.LANGUAGE_PROPERTY, null);
      query.andQualifier(exp);
    }

    if (country != null && country.length() > 0 && (!country.equals("-1"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedUserProfile.COUNTRY_PROPERTY,
          country);
      query.andQualifier(exp);
    } else if (country != null && country.equals("-1")) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedUserProfile.COUNTRY_PROPERTY, null);
      query.andQualifier(exp);
    }

    return query;
  }

  private static QualifiedQuery assignJetSpeedRoleProfileQuery(
      ProfileLocator locator, QualifiedQuery query) {
    String mediaType = locator.getMediaType();
    String language = locator.getLanguage();
    String country = locator.getCountry();
    String pageName = locator.getName();
    String roleName = null;

    Role role = locator.getRole();
    if (role != null) {
      roleName = role.getName();
    }
    if (roleName != null && roleName.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedRoleProfile.ROLE_NAME_PROPERTY,
          roleName);
      query.andQualifier(exp);
    }

    if (pageName != null && pageName.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedRoleProfile.PAGE_PROPERTY, pageName);
      query.andQualifier(exp);
    }

    if (mediaType != null && mediaType.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedRoleProfile.MEDIA_TYPE_PROPERTY,
          mediaType);
      query.andQualifier(exp);
    }

    if (language != null && language.length() > 0 && (!language.equals("-1"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedRoleProfile.LANGUAGE_PROPERTY,
          language);
      query.andQualifier(exp);
    } else if (language != null && language.equals("-1")) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedRoleProfile.LANGUAGE_PROPERTY, null);
      query.andQualifier(exp);
    }

    if (country != null && country.length() > 0 && (!country.equals("-1"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedRoleProfile.COUNTRY_PROPERTY,
          country);
      query.andQualifier(exp);
    } else if (country != null && country.equals("-1")) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedRoleProfile.COUNTRY_PROPERTY, null);
      query.andQualifier(exp);
    }

    return query;
  }

  private static QualifiedQuery assignJetSpeedGroupProfileQuery(
      ProfileLocator locator, QualifiedQuery query) {
    String mediaType = locator.getMediaType();
    String language = locator.getLanguage();
    String country = locator.getCountry();
    String pageName = locator.getName();
    String groupName = null;

    Group group = locator.getGroup();
    if (group != null) {
      groupName = group.getName();
    }
    if (groupName != null && groupName.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedGroupProfile.GROUP_NAME_PROPERTY,
          groupName);
      query.andQualifier(exp);
    }

    if (pageName != null && pageName.length() > 0) {
      Expression exp =
        ExpressionFactory
          .matchExp(JetspeedGroupProfile.PAGE_PROPERTY, pageName);
      query.andQualifier(exp);
    }

    if (mediaType != null && mediaType.length() > 0) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedGroupProfile.MEDIA_TYPE_PROPERTY,
          mediaType);
      query.andQualifier(exp);
    }

    if (language != null && language.length() > 0 && (!language.equals("-1"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedGroupProfile.LANGUAGE_PROPERTY,
          language);
      query.andQualifier(exp);
    } else if (language != null && language.equals("-1")) {
      Expression exp =
        ExpressionFactory
          .matchExp(JetspeedGroupProfile.LANGUAGE_PROPERTY, null);
      query.andQualifier(exp);
    }

    if (country != null && country.length() > 0 && (!country.equals("-1"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          JetspeedGroupProfile.COUNTRY_PROPERTY,
          country);
      query.andQualifier(exp);
    } else if (country != null && country.equals("-1")) {
      Expression exp =
        ExpressionFactory.matchExp(JetspeedGroupProfile.COUNTRY_PROPERTY, null);
      query.andQualifier(exp);
    }

    return query;
  }
}
