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

package org.apache.jetspeed.services.ldap;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ReferralException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;

/**
 * 
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 * 
 */
public class LDAPService extends TurbineBaseService {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(LDAPService.class.getName());

  public static final String SERVICE_NAME = "ldap";

  private static final String DEFAULT_ATTR[] = { "objectclass" };

  public static final int BASE = 0;

  public static final int ONE = 1;

  public static final int SUB = 2;

  public static final int DEFAULT_PORT = 389;

  public static final int DEFAULT_SSLPORT = 636;

  public static final int DEFAULT_LIMIT = 0;

  public static final int DEFAULT_TIMEOUT = 0;

  public static final int DEFAULT_VERSION = 3;

  private static String DEFAULT_CTX = "com.sun.jndi.ldap.LdapCtxFactory";

  private Hashtable connections;

  private Connector connector;

  private int limit;

  private int timeout;

  private int version;

  private String host;

  private int port;

  private int sslport;

  private String basedn;

  private String managerdn;

  private String password;

  private String managerlogin;

  private int batchsize;

  private String securityAuthentication;

  private String securityProtocol;

  private String socketFactory;

  private String saslclientpckgs;

  private String jndiprovider;

  private boolean anonymousBind;

  private String listFilter;

  private String attributesList[];

  private NameParser parser;

  private boolean showOpAttributes;

  private boolean useCachedDirContexts;

  private Properties env;

  /**
   * Main Connection Function
   * 
   * Make first connection and store it in connections.
   * 
   * @param url
   *            <code>LDAPURL</code> which locate server to connect.
   * @return boolean true if success else false.
   */
  private boolean mainConnect(LDAPURL url) {
    setDefaultEnv();
    String base = url.getBase();
    env.put("java.naming.provider.url", base);
    try {
      DirContext ctx = new InitialDirContext(env);
      if (useCachedDirContexts) {
        connections.put(basedn, ctx);
      }
      if (parser == null)
        parser = ctx.getNameParser("");
      return true;
    } catch (NamingException e) {
      logger.error("LDAP Service: Failed to connect to " + url.getUrl(), e);
    }
    return false;
  }

  /**
   * Connection Function
   * 
   * tries to connect given <code>LDAPURL</code>.
   * 
   * @param url
   *            <code>LDAPURL</code> which locate server to connect.
   * @return DirContext connection context object.
   */
  public DirContext connect(LDAPURL url) {

    String base = url.getBase();
    DirContext ctx = (DirContext) connections.get(base);
    if (ctx != null) {
      // System.out.println("LDAPService: returning cached context.");
      // System.out.println("LDAPService: DN is " + url.getDN());
      return ctx;
    } else {
      // System.out.println("LDAPService: creating new context for base " +
      // base);
      // System.out.println("LDAPService: DN is " + url.getDN());
    }

    setDefaultEnv();
    env.put("java.naming.provider.url", base);
    do {
      try {
        ctx = new InitialDirContext(env);
        if (useCachedDirContexts)
          connections.put(base, ctx);
        return ctx;
      } catch (AuthenticationException e) {
        logger.error("LDAP Service: Authentication error: " + base, e);
        if (connector == null)
          return null;
        Properties pr = connector.referralConnection(env, url, anonymousBind);
        if (pr != null) {
          env = pr;
          continue;
        }
      } catch (CommunicationException e) {
        logger.error("LDAP Service: Communication error: " + base, e);
        if (connector == null)
          return null;
        if (connector.connectionFailed(url)) {
          resetConnection(url);
          continue;
        }
      } catch (NamingException e) {
        logger.error("LDAP Service:Failed to connect to " + base, e);
      }
      return ctx;
    } while (true);
  }

  /**
   * Reset Given Connection Function
   * 
   * tries to connect given <code>LDAPURL</code>.
   * 
   * @param url
   *            <code>LDAPURL</code> which locate server to connect.
   * 
   */
  private void resetConnection(LDAPURL url) {
    // System.out.println("LDAPService: resetConnection() called.");
    connections.remove(url.getBase());
  }

  /**
   * Set Default Environment
   * 
   * Fill properties necessary to connect.
   * 
   */
  private void setDefaultEnv() {
    showOpAttributes = attributesList != null;
    env.put("java.naming.referral", "ignore");
    env.put("java.naming.batchsize", String.valueOf(batchsize));

    if (anonymousBind) {
      env.remove("java.naming.security.principal");
      env.remove("java.naming.security.credentials");
    } else {
      env.put("java.naming.security.principal", managerdn);
      env.put("java.naming.security.credentials", password);
    }

    env.put("java.naming.security.authentication", securityAuthentication);
    if (saslclientpckgs != null) {
      env.put("javax.security.sasl.client.pkgs", saslclientpckgs);
    } else {
      env.remove("javax.security.sasl.client.pkgs");
    }

    env.put("java.naming.ldap.derefAliases", "never");
    env.put("java.naming.ldap.deleteRDN", "true");
    env.put("java.naming.ldap.version", String.valueOf(version));

    if (securityProtocol != null) {
      env.put("java.naming.security.protocol", securityProtocol);
      if (securityProtocol.equalsIgnoreCase("ssl")) {
        env.put("java.naming.ldap.factory.socket", socketFactory);
      }
    } else {
      env.remove("java.naming.security.protocol");
      env.remove("java.naming.ldap.factory.socket");
    }

    // env.put("com.sun.jndi.ldap.trace.ber", System.err);
    env.put("java.naming.factory.initial", (Object) (jndiprovider));
  }

  /**
   * Disconnection Function
   * 
   * tries to disconnect all connection.
   * 
   * @return boolean true if success else false.
   */

  public boolean disconnect() {
    // System.out.println("LDAPService: disconnect() called.");
    DirContext ctx = null;

    for (Enumeration enu = connections.elements(); enu.hasMoreElements();) {
      try {
        ctx = (DirContext) enu.nextElement();
        ctx.close();
      } catch (NamingException e) {
        logger.error("LDAP Service: Disconnect failed", e);
      }
    }

    connections.clear();
    return true;
  }

  public boolean checkAndCloseContext(Context context) {
    try {
      if (!useCachedDirContexts) {
        context.close();
        // System.out.println("LDAPService: closeContext() called.");
      } else {
        // System.out.println("LDAPService: context left in cache.");
      }
      return true;
    } catch (NamingException e) {
      logger.error("LDAP Service: closeContext() failed", e);
      return false;
    }
  }

  /**
   * Delete Atrribute Function
   * 
   * Delete given attribute for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object affected.
   * @param at
   *            Atribute to delete
   * @return boolean true if success else false.
   */

  public boolean deleteAttribute(LDAPURL url, Attribute at) {
    try {
      ModificationItem mods[] = new ModificationItem[1];
      mods[0] = new ModificationItem(3, at);
      return modifyAttribute(url, mods);
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to delete '" + at.getID()
          + "' attribute for " + url.getUrl(), e);
    }
    return false;
  }

  /**
   * Add Attribute Function
   * 
   * add given attribute to given <code>LDAPURL</code>.
   * 
   * @param url
   *            object affected.
   * @param at
   *            Atribute to add
   * @return boolean true if success else false.
   */
  public boolean addAttribute(LDAPURL url, Attribute at) {
    try {
      ModificationItem mods[] = new ModificationItem[1];
      mods[0] = new ModificationItem(1, at);
      return modifyAttribute(url, mods);
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to add '" + at.getID()
          + "' attribute for " + url.getUrl(), e);
    }
    return false;
  }

  /**
   * Add entry Function
   * 
   * tries to add object with given <code>LDAPURL</code> and with given
   * attributes.
   * 
   * @param url
   *            object to create.
   * @param at
   *            Atributes to add
   * @return boolean true if success else false.
   */
  public boolean addEntry(LDAPURL url, Attributes at) {
    DirContext ctx = connect(url);

    if (ctx == null)
      return false;
    try {
      ctx.createSubcontext(url.getDN(), at);
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      return addEntry(myurl, at);
    } catch (NamingException e) {

      e.printStackTrace();

      logger.error("LDAP Service: Failed to add new entry " + url.getDN(), e);
      return false;
    }
    return true;
  }

  /**
   * Query existense of an Object Function
   * 
   * tries to locate given <code>LDAPURL</code>.
   * 
   * @param url
   *            object affected.
   * @return boolean true if exist else false.
   */
  public boolean exists(LDAPURL url) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      NamingEnumeration results = search(ctx, url.getDN(), "(objectclass=*)",
          DEFAULT_ATTR, 0, false);
      checkAndCloseContext(ctx);
      return true;
    } catch (NameNotFoundException _ex) {
      return false;
    } catch (NamingException _ex) {
      return false;
    }
  }

  /**
   * Compare Function
   * 
   * Compare given <code>LDAPURL</code>s.
   * 
   * @param srcUrl
   *            object affected.
   * @param dstUrl
   *            object affected.
   * @return int 0 same host+DN, 1 same DN,2 child,3 no relation.
   */
  public int compare(LDAPURL srcUrl, LDAPURL dstUrl) {
    if (!srcUrl.sameHosts(dstUrl))
      return 0;
    Name src = parse(srcUrl.getDN());
    Name dst = parse(dstUrl.getDN());
    if (dst.compareTo(src) == 0)
      return 1;
    if (dst.startsWith(src))
      return 2;
    Name prefix = src.getPrefix(src.size() - 1);
    return dst.compareTo(prefix) != 0 ? 0 : 3;
  }

  /**
   * Import Function
   * 
   * Import given <code>LDAPURL</code> to another dn.
   * 
   * @param url
   *            object to import.
   * @param dn
   *            Dn of new object.
   * @param entry
   *            attributes.
   * @param type
   *            0 addnew, 1 update, 2 sync.
   * @return int 1 success, 0 unknown type,-1 failure.
   */
  public int importEntry(LDAPURL url, String dn, Attributes entry, int type) {
    boolean rs = false;
    LDAPURL myurl = new LDAPURL(url.getHost(), url.getPort(), dn);
    if (type == 0)
      rs = addEntry(myurl, entry);
    else

    if (type == 1)
      rs = updateEntry(myurl, entry);
    else if (type == 2)
      rs = synchEntry(myurl, entry);
    else
      return 0;
    return !rs ? -1 : 1;
  }

  /**
   * Modify Function
   * 
   * Modify given <code>LDAPURL</code> with fiven modification items.
   * 
   * @param url
   *            object to modify.
   * @param mods
   *            Modification items.
   * @exception NamingException
   * @return boolean true if success else false.
   */
  private boolean modifyAttribute(LDAPURL url, ModificationItem mods[])
      throws NamingException {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      ctx.modifyAttributes(url.getDN(), mods);
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      return modifyAttribute(myurl, mods);
    }
    return true;
  }

  /**
   * Build LDAPURL Function
   * 
   * Build <code>LDAPURL</code> with given DN.
   * 
   * @param DN
   *            DN value for object.
   * @return LDAPURL build with given DN.
   */
  public LDAPURL buildURL(String DN) {
    return new LDAPURL(host, port, DN + "," + basedn);
  }

  /**
   * Read Attributes Function
   * 
   * Return attributes for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to read attributes.
   * @return Attributes attributes for given url.
   */
  public Attributes read(LDAPURL url) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return null;

    Attributes attrs = null;
    try {
      if (showOpAttributes) {
        attrs = ctx.getAttributes(url.getDN(), attributesList);
      } else {
        attrs = ctx.getAttributes(url.getDN());
      }
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      if (myurl.getDN().length() == 0) {
        myurl.setDN(url.getDN());
      }
      return read(myurl);
    } catch (CommunicationException e) {
      if (connector == null) {
        logger.debug("LDAP Service: Communication error : " + url.getBase(), e);
        return null;
      }
      if (connector.connectionFailed(url)) {
        resetConnection(url);
      }
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to read entry " + url.getDN(), e);
      return null;
    }
    return attrs;
  }

  /**
   * Rename Entry Function
   * 
   * Rename given <code>LDAPURL</code> with given DN.
   * 
   * @param url
   *            object to modify.
   * @param newDN
   *            DN value for new object.
   * @return boolean true if success else false.
   */
  public boolean renameEntry(LDAPURL url, String newDN) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      ctx.rename(url.getDN(), newDN);
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      logger
          .debug(
              "LDAP Service: Failed to rename entry. (not supported for referrals)",
              e);
      return false;
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to rename entry " + url.getDN(), e);
      return false;
    }
    return true;
  }

  /**
   * Sync Entry Function
   * 
   * Sync given <code>LDAPURL</code> with given atrributes.
   * 
   * @param url
   *            object to sync.
   * @param ats
   *            Modification items.
   * @return boolean true if success else false.
   */
  public boolean synchEntry(LDAPURL url, Attributes ats) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      ctx.modifyAttributes(url.getDN(), 2, ats);
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      return synchEntry(url, ats);
    } catch (NameNotFoundException _ex) {
      try {
        ctx.createSubcontext(url.getDN(), ats);
      } catch (NamingException _ex2) {
        return false;
      }
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to synchronize entries", e);
      return false;
    }
    return true;
  }

  /**
   * Delete Attributes Function
   * 
   * Delete Attributes for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to modify.
   * @param ats
   *            Attributes to delete.
   * @return boolean true if success else false.
   */
  public boolean deleteAttrs(LDAPURL url, Attributes ats) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      ctx.modifyAttributes(url.getDN(), DirContext.REMOVE_ATTRIBUTE, ats);
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      return synchEntry(url, ats);
    } catch (NameNotFoundException _ex) {
      try {
        ctx.createSubcontext(url.getDN(), ats);
        checkAndCloseContext(ctx);
      } catch (NamingException _ex2) {
        return false;
      }
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to delete Attributes", e);
      return false;
    }
    return true;
  }

  /**
   * Delete Entry Function
   * 
   * Delete given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to delete.
   * @return boolean true if success else false.
   */
  public boolean deleteEntry(LDAPURL url) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      ctx.destroySubcontext(url.getDN());
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      return deleteEntry(myurl);
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to delete entry " + url.getDN(), e);
      return false;
    }
    return true;
  }

  /**
   * Find Entry Name Function
   * 
   * Return entry name for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to modify.
   * @return LDAPURL real entry DN.
   */
  public LDAPURL findEntryName(LDAPURL url) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return null;

    Name name = parse(url.getDN());
    String base = name.getPrefix(name.size() - 1).toString();
    String dn = url.getDN();
    String rdn = name.get(name.size() - 1);
    int i = 1;
    boolean foundName = true;

    while (foundName) {
      try {
        NamingEnumeration results = search(ctx, dn, "(objectclass=*)",
            DEFAULT_ATTR, 0, false);
        if (i == 1)
          rdn = rdn + " copy";
        else if (i == 2)
          rdn = rdn + " " + i;
        else if (i >= 3)
          rdn = rdn.substring(0, rdn.length() - 1) + i;
        dn = rdn + ", " + base;
        i++;
      } catch (NameNotFoundException _ex) {
        foundName = false;
        return new LDAPURL(url.getHost(), url.getPort(), dn);
      } catch (NamingException _ex) {
        return null;
      }
    }

    checkAndCloseContext(ctx);

    return null;
  }

  /**
   * Delete Tree Function
   * 
   * Delete record with all child node <code>LDAPURL</code>.
   * 
   * @param url
   *            object to modify.
   * @return boolean true if success else false.
   */
  public boolean deleteTree(LDAPURL url) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    String entryDN = null;
    LDAPURL myurl = null;
    String baseDN = url.getDN();

    try {
      for (NamingEnumeration results = search(ctx, baseDN, "(objectclass=*)",
          DEFAULT_ATTR, 1, false); results.hasMore();) {
        SearchResult si = (SearchResult) results.next();
        entryDN = getFixedDN(si.getName(), baseDN);
        myurl = new LDAPURL(url.getHost(), url.getPort(), entryDN);
        if (!deleteTree(myurl)) {
          return false;
        }
      }

      checkAndCloseContext(ctx);
    } catch (NamingException e) {
      logger.debug("LDAP Service: Delete tree failed", e);
      return false;
    }
    return deleteEntry(url);
  }

  /**
   * Transfer Function
   * 
   * Transfer given <code>LDAPURL</code> to other <code>LDAPURL</code>.
   * 
   * @param fromUrl
   *            object to transfer.
   * @param toUrl
   *            target object.
   * @param delete
   *            delete after transfer.
   * @param replace
   *            replace if exist.
   * @param withChildren
   *            transfer with childs.
   * @return boolean true if success else false.
   */
  public boolean transfer(LDAPURL fromUrl, LDAPURL toUrl, boolean delete,
      boolean replace, boolean withChildren) {
    LDAPURL dstUrl = toUrl;
    int rc = compare(fromUrl, toUrl);
    if (rc == 1)
      dstUrl = findEntryName(dstUrl);
    if (withChildren)
      return transferTreeSub(fromUrl, dstUrl, delete, replace);
    else
      return transferEntry(fromUrl, dstUrl, delete, replace);

  }

  /**
   * Transfer with updates Function
   * 
   * Transfer updated <code>LDAPURL</code> with given modification items to
   * other <code>LDAPURL</code>.
   * 
   * @param fromUrl
   *            object to transfer.
   * @param toUrl
   *            target object.
   * @param delete
   *            delete after transfer.
   * @param replace
   *            replace if exist.
   * @param ats
   *            attributes to update.
   * @return boolean true if success else false.
   */
  public boolean transferEntry(LDAPURL fromUrl, Attributes ats, LDAPURL toUrl,
      boolean delete, boolean replace) {
    if (delete && !deleteEntry(fromUrl))
      return false;
    if (updateEntry(toUrl, ats, replace))
      return true;
    if (delete)
      addEntry(fromUrl, ats);
    return false;
  }

  /**
   * Transfer without updates Function
   * 
   * Transfer <code>LDAPURL</code> to other <code>LDAPURL</code>.
   * 
   * @param fromUrl
   *            object to transfer.
   * @param toUrl
   *            target object.
   * @param delete
   *            delete after transfer.
   * @param replace
   *            replace if exist.
   * @return boolean true if success else false.
   */

  public boolean transferEntry(LDAPURL fromUrl, LDAPURL toUrl, boolean delete,
      boolean replace) {
    Attributes ats = read(fromUrl);
    if (ats == null)
      return false;
    else
      return transferEntry(fromUrl, ats, toUrl, delete, replace);
  }

  /**
   * Transfer Tree Function
   * 
   * Transfer <code>LDAPURL</code> with all child to other
   * <code>LDAPURL</code>.
   * 
   * @param fromUrl
   *            object to transfer.
   * @param toUrl
   *            target object.
   * @param delete
   *            delete after transfer.
   * @param replace
   *            replace if exist.
   * @return boolean true if success else false.
   */
  private boolean transferTreeSub(LDAPURL fromUrl, LDAPURL toUrl,
      boolean delete, boolean replace) {
    DirContext ctx = connect(fromUrl);
    if (ctx == null)
      return false;

    Attributes ats = read(fromUrl);
    if (ats == null)
      return false;

    String srcDN = fromUrl.getDN();
    String dstDN = toUrl.getDN();
    boolean createdBase = false;
    boolean rc = false;
    boolean moreReferrals = true;

    while (moreReferrals) {
      try {
        NamingEnumeration results = search(ctx, srcDN, "(objectclass=*)",
            DEFAULT_ATTR, 1, false);
        if (!results.hasMore()) {
          if (!transferEntry(fromUrl, ats, toUrl, delete, replace))
            return false;
        } else {
          String name = null;
          if (!createdBase) {
            if (!updateEntry(toUrl, ats, replace))
              return false;
            createdBase = true;
          }
          LDAPURL srcUrl;
          LDAPURL dstUrl;
          for (; results.hasMore(); transferTreeSub(srcUrl, dstUrl, delete,
              replace)) {
            SearchResult si = (SearchResult) results.next();
            name = fixName(si.getName());
            String tmpSrcDN = getDN(name, srcDN);
            srcUrl = new LDAPURL(fromUrl.getHost(), fromUrl.getPort(), tmpSrcDN);
            String tmpDstDN = getDN(name, dstDN);
            dstUrl = new LDAPURL(toUrl.getHost(), toUrl.getPort(), tmpDstDN);
          }

          if (delete && !deleteEntry(fromUrl))
            return false;
        }
        moreReferrals = false;
      } catch (ReferralException e) {
        if (delete) {
          moreReferrals = false;
        } else {
          if (!createdBase) {
            if (!updateEntry(toUrl, ats, replace))
              return false;
            createdBase = true;
          }

          LDAPURL srcUrl = getReferralUrl(e);
          String tmpDstDN = getName(srcUrl.getDN()) + ", " + dstDN;
          LDAPURL dstUrl = new LDAPURL(toUrl.getHost(), toUrl.getPort(),
              tmpDstDN);
          boolean rs = transferTreeSub(srcUrl, dstUrl, delete, replace);
          if (!rs)
            return false;

          moreReferrals = e.skipReferral();
          try {
            // Close old context
            checkAndCloseContext(ctx);
            ctx = (DirContext) e.getReferralContext();
          } catch (NamingException _ex) {
          }
        }
      } catch (NamingException e) {
        logger.debug("LDAP Service: Transfer Tree failed", e);
        return false;
      }
    }

    checkAndCloseContext(ctx);
    return true;
  }

  /**
   * Update Atribute Function
   * 
   * Update an attribute for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to update.
   * @param at
   *            atrribute to update.
   * @return boolean true if success else false.
   */
  public boolean updateAttribute(LDAPURL url, Attribute at) {
    try {
      ModificationItem mods[] = new ModificationItem[1];
      mods[0] = new ModificationItem(2, at);
      return modifyAttribute(url, mods);
    } catch (NamingException e) {
      logger.debug("LDAP Service: Failed to update '" + at.getID()
          + "' attribute for " + url.getUrl(), e);
    }
    return false;
  }

  /**
   * Update Atributes Function
   * 
   * Update attributes for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to update.
   * @param at
   *            atrributes to update.
   * @return boolean true if success else false.
   */
  public boolean updateEntry(LDAPURL url, Attributes at) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    try {
      ctx.modifyAttributes(url.getDN(), 2, at);
      checkAndCloseContext(ctx);
    } catch (ReferralException e) {
      LDAPURL myurl = getReferralUrl(e);
      return updateEntry(myurl, at);
    } catch (NamingException e) {
      logger.error("LDAP Service: Failed to update entry " + url.getDN(), e);
      return false;
    }
    return true;
  }

  /**
   * Update Entry Function
   * 
   * Update attributes for given <code>LDAPURL</code>.
   * 
   * @param url
   *            object to update.
   * @param ats
   *            atrributes to update.
   * @param replace
   *            replace if exist.
   * @return boolean true if success else false.
   */
  public boolean updateEntry(LDAPURL url, Attributes ats, boolean replace) {
    return replace ? synchEntry(url, ats) : addEntry(url, ats);
  }

  /**
   * Search Function
   * 
   * Search objects for given Base DN and filter.
   * 
   * @param ctx
   *            directory context.
   * @param dn
   *            Base search DN.
   * @param filter
   *            Search filter.
   * @param attribs
   *            attributes to receive.
   * @param type
   *            search scope 1 Subscope, else 0.
   * @exception NamingException
   * @return NamingEnumeration Results.
   */
  public NamingEnumeration search(DirContext ctx, String dn, String filter,
      String attribs[], int type) throws NamingException {
    return search(ctx, dn, filter, attribs, type, true);
  }

  /**
   * Search Function
   * 
   * Search objects for given Base DN and filter.
   * 
   * @param ctx
   *            directory context.
   * @param dn
   *            Base search DN.
   * @param filter
   *            Search filter.
   * @param attribs
   *            attributes to receive.
   * @param type
   *            search scope 2 Subscope, else 1.
   * @param setLimits
   *            enable limits.
   * @exception NamingException
   * @return NamingEnumeration Results.
   */
  private NamingEnumeration search(DirContext ctx, String dn, String filter,
      String attribs[], int type, boolean setLimits) throws NamingException {
    SearchControls constraints = new SearchControls();
    constraints.setSearchScope(type);
    constraints.setReturningAttributes(attribs);
    if (setLimits) {
      constraints.setCountLimit(limit);
      constraints.setTimeLimit(timeout);
    }
    NamingEnumeration results = ctx.search(dn, filter, constraints);
    return results;
  }

  /**
   * Search Function
   * 
   * Search objects for given BaseURL and filter.
   * 
   * @param url
   *            Base URL .
   * @param filter
   *            Search filter.
   * @param attribs
   *            attributes to receive.
   * @param subTreeScope
   *            true subtree else false.
   * @return Vector Results.
   */
  public Vector search(LDAPURL url, String filter, String attribs[],
      boolean subTreeScope) {
    /*
     * System.out.println("===== LDAPService: search");
     * System.out.println("===== LDAPService: " + url);
     * System.out.println("===== LDAPService: " + filter);
     * System.out.println("===== LDAPService: " + attribs);
     * System.out.println("===== LDAPService: " + subTreeScope);
     */

    Vector results = new Vector();
    String attrs[] = new String[attribs.length + 1];
    attrs[0] = "objectclass";
    System.arraycopy(attribs, 0, attrs, 1, attribs.length);
    int scope = subTreeScope ? 2 : 1;
    subSearch(url, filter, attrs, scope, results);

    return results;
  }

  /**
   * Search Function
   * 
   * Search objects for given BaseURL and filter.
   * 
   * @param url
   *            Base URL .
   * @param filter
   *            Search filter.
   * @param attribs
   *            attributes to receive.
   * @param scope
   *            true subtree else false.
   * @param rs
   *            Result
   * @return boolean true if success else false.
   */
  private boolean subSearch(LDAPURL url, String filter, String attribs[],
      int scope, Vector rs) {
    DirContext ctx = connect(url);
    if (ctx == null)
      return false;

    String entryDN = null;
    Attributes at = null;
    Attribute a = null;
    LDAPURL myurl = null;
    int subscope = 0;
    String baseDN = url.getDN();

    boolean moreReferrals = true;
    while (moreReferrals) {
      try {
        Vector vl;
        for (NamingEnumeration results = search(ctx, baseDN, filter, attribs,
            scope); results.hasMore(); rs.addElement(vl)) {
          SearchResult si = (SearchResult) results.next();
          vl = new Vector(attribs.length);
          entryDN = getFixedDN(si.getName(), baseDN);
          myurl = new LDAPURL(url.getHost(), url.getPort(), entryDN);
          vl.addElement(myurl);
          at = si.getAttributes();
          for (int i = 1; i < attribs.length; i++) {
            a = at.get(attribs[i]);
            if (a == null) {
              vl.addElement("N/A");
            } else {
              Object v = a.get();
              if (v instanceof byte[])
                vl.addElement(v);
              else
                vl.addElement(a.get().toString());
            }
          }
        }
        moreReferrals = false;
      }

      catch (ReferralException e) {
        myurl = getReferralUrl(e);
        subscope = scope != 1 ? scope : 0;
        boolean error = subSearch(myurl, filter, attribs, subscope, rs);
        if (!error)
          return error;

        moreReferrals = e.skipReferral();
        try {
          // Close old context
          checkAndCloseContext(ctx);
          ctx = (DirContext) e.getReferralContext();
        } catch (NamingException _ex) {
        }
      } catch (NamingException e) {
        logger.debug("LDAP Service: Search failed", e);
        return false;
      }
    }

    checkAndCloseContext(ctx);
    return true;
  }

  /**
   * Get value Function
   * 
   * Return value for attribute value pair.
   * 
   * @param attrvalue
   *            input.
   * @return String Value.
   */
  public String removeAttrName(String attrvalue) {
    StringTokenizer token = new StringTokenizer(attrvalue, "=");
    if (token.countTokens() == 2) {
      token.nextToken();
      return token.nextToken();
    } else {
      return attrvalue;
    }
  }

  /**
   * Return full DN Function
   * 
   * Add Base DN to given DN.
   * 
   * @param rdn
   *            full DN.
   * @param base
   *            Base DN.
   * @return String DN.
   */
  private String getFixedDN(String rdn, String base) {
    return getDN(fixName(rdn), base);
  }

  /**
   * Return Name Function
   * 
   * Return name for given DN.
   * 
   * @param dn
   *            DN.
   * @return String Name.
   */
  public String getName(String dn) {
    try {
      Name nm = parser.parse(dn);
      return nm.get(nm.size() - 1);
    } catch (NamingException _ex) {
      return null;
    }
  }

  /**
   * Fix Name Function
   * 
   * Fix chars .
   * 
   * @param name
   *            Name to fix.
   * @return String Fixed name.
   */
  private String fixName(String name) {
    if (name.length() > 0 && name.charAt(0) == '"') {
      int size = name.length() - 1;
      StringBuffer buf = new StringBuffer();
      for (int i = 1; i < size; i++) {
        if (name.charAt(i) == '/')
          buf.append("\\");
        buf.append(name.charAt(i));
      }

      return buf.toString();
    } else {
      return name;
    }
  }

  /**
   * Return full DN Function
   * 
   * Add Base DN to given DN.
   * 
   * @param rdn
   *            DN.
   * @param base
   *            Base DN.
   * @return String full DN.
   */
  private String getDN(String rdn, String base) {
    if (rdn.length() == 0)
      return base;
    if (base.length() == 0)
      return rdn;
    else
      return rdn + ", " + base;
  }

  /**
   * Return Name Function
   * 
   * Add Base DN to given DN.
   * 
   * @param dn
   *            full DN.
   * @return Name Name for given DN.
   */
  public Name parse(String dn) {
    try {
      return parser.parse(dn);
    } catch (NamingException _ex) {
      return null;
    }
  }

  /**
   * Get Referral URL Function
   * 
   * Return <code>LDAPURL</code> extracted from exception.
   * 
   * @param e
   *            Exception to extract.
   * @return LDAPURL referrral URL.
   */
  public LDAPURL getReferralUrl(ReferralException e) {
    String url = (String) e.getReferralInfo();
    try {
      return new LDAPURL(url);
    } catch (Exception ex) {
      logger.debug("Invalid url: " + ex.getMessage() + " " + url);
    }
    return null;
  }

  // /////////////////////////////////////////////////////////////////////////
  // Service Init
  // /////////////////////////////////////////////////////////////////////////

  /**
   * This is the early initialization method called by the Turbine
   * <code>Service</code> framework
   * 
   * @param conf
   *            The <code>ServletConfig</code>
   * @exception InitializationException
   *                if the service fails to initialize
   */
  public void init(ServletConfig conf) throws InitializationException {
    connections = new Hashtable();
    connector = null;
    parser = null;
    env = new Properties();
    ResourceService serviceConf = ((TurbineServices) TurbineServices
        .getInstance()).getResources(SERVICE_NAME);
    this.host = serviceConf.getString("host");
    this.port = serviceConf.getInt("port", DEFAULT_PORT);
    this.sslport = serviceConf.getInt("sslport", DEFAULT_SSLPORT);
    this.limit = serviceConf.getInt("limit", DEFAULT_LIMIT);
    this.timeout = serviceConf.getInt("timeout", DEFAULT_TIMEOUT);
    this.version = serviceConf.getInt("version", DEFAULT_VERSION);
    this.listFilter = repair(serviceConf.getString("listfilter",
        "(objectclass=*)"));
    this.basedn = repair(serviceConf.getString("basedn"));
    this.managerdn = repair(serviceConf.getString("managerdn"));
    this.password = serviceConf.getString("password");
    this.attributesList = getList(serviceConf.getString("attributeslist"), " ");
    this.showOpAttributes = serviceConf.getBoolean("showopattributes", false);
    this.anonymousBind = serviceConf.getBoolean("anonymousbind", false);
    this.securityAuthentication = serviceConf.getString(
        "securityauthentication", "simple");
    this.securityProtocol = serviceConf.getString("securityprotocol");
    this.socketFactory = serviceConf.getString("socketfactory");
    this.useCachedDirContexts = serviceConf.getBoolean("contextcache", false);

    this.jndiprovider = serviceConf.getString("jndiprovider", DEFAULT_CTX);
    this.saslclientpckgs = serviceConf.getString("saslclientpckgs");
    mainConnect(new LDAPURL(host, port, basedn));
    setInit(true);
  }

  /**
   * This is the late initialization method called by the Turbine
   * <code>Service</code> framework
   * 
   * @param conf
   *            The <code>ServletConfig</code>
   * @exception InitializationException
   *                if the service fails to initialize
   */
  public void init() throws InitializationException {
    while (!getInit()) {
      // Not yet...
      try {
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        logger.error(ie);
      }
    }
  }

  /**
   * Repair Given Parameter Function
   * 
   * Repair String read from config.
   * 
   * @param value
   *            String to repair.
   * @return String Repaired String.
   */
  private String repair(String value) {
    value = value.replace('/', '=');
    value = value.replace('%', ',');
    return value;
  }

  /**
   * Tokenizer Wrapper Function
   * 
   * Tokenize given string with given parameter.
   * 
   * @param value
   *            String to repair.
   * @param separator
   *            separator
   * @return String Result.
   */
  private String[] getList(String value, String separator) {
    if (value == null)
      return null;

    StringTokenizer tokens = new StringTokenizer(value, separator);
    String at[] = new String[tokens.countTokens()];

    for (int i = 0; tokens.hasMoreTokens(); i++) {
      at[i] = tokens.nextToken();
    }

    return at;
  }

}
