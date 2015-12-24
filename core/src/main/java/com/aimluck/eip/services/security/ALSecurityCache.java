/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.services.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.CachedAcl;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.SecurityCacheService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

import com.aimluck.eip.http.HttpServletRequestLocator;

/**
 *
 */
public class ALSecurityCache extends TurbineBaseService implements
    SecurityCacheService {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALSecurityCache.class.getName());

  private static String ACLS_KEY =
    "com.aimluck.eip.services.security.ALSecurityCache.acls";

  private static String PERMS_KEY =
    "com.aimluck.eip.services.security.ALSecurityCache.perms";

  protected static SecurityCacheService getService() {
    return (SecurityCacheService) TurbineServices.getInstance().getService(
      SecurityCacheService.SERVICE_NAME);
  }

  @Override
  public void load(String username) throws JetspeedSecurityException {
    Map<String, CachedAcl> acls = getAclsFromRequest();
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    CachedAcl acl = new CachedAcl(username);
    acl.setRoles(JetspeedSecurity.getRoles(username));

    if (acls == null) {
      acls = new HashMap<String, CachedAcl>();
    }
    acls.put(username, acl);
    saveAclsToRequest(acls);

    if (perms != null) {
      loadRolePermissions();
    }

  }

  @Override
  public void unload(String username) {
    Map<String, CachedAcl> acls = getAclsFromRequest();

    if (acls != null) {
      acls.remove(username);
      saveAclsToRequest(acls);
    }
  }

  @Override
  public Role getRole(String username, String roleName) {
    Map<String, CachedAcl> acls = getAclsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl == null) {
        return null;
      }
      return acl.getRole(roleName);
    }

    return null;
  }

  @Override
  public Role getRole(String username, String roleName, String groupName) {
    Map<String, CachedAcl> acls = getAclsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl == null) {
        return null;
      }
      return acl.getRole(roleName, groupName);
    }

    return null;
  }

  @Override
  public void addRole(Role role) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      if (!perms.containsKey(role.getName())) {
        perms.put(role.getName(), new HashMap<String, Permission>());
      }
      savePermissionsToRequest(perms);
    }
  }

  @Override
  public void addRole(String username, Role role) {
    Map<String, CachedAcl> acls = getAclsFromRequest();
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl != null) {
        acl.addRole(role);
        acls.put(username, acl);
        saveAclsToRequest(acls);
      }
    }

    if (perms != null) {
      if (!perms.containsKey(role.getName())) {
        perms.put(role.getName(), new HashMap<String, Permission>());
      }
      savePermissionsToRequest(perms);
    }

  }

  @Override
  public void addRole(String username, Role role, Group group) {
    Map<String, CachedAcl> acls = getAclsFromRequest();
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl != null) {
        acl.addRole(role, group);
        acls.put(username, acl);
        saveAclsToRequest(acls);
      }
    }

    if (perms != null) {
      if (!perms.containsKey(role.getName())) {
        perms.put(role.getName(), new HashMap<String, Permission>());
      }
      savePermissionsToRequest(perms);
    }
  }

  @Override
  public boolean hasRole(String username, String roleName) {
    return hasRole(username, roleName, GroupManagement.DEFAULT_GROUP_NAME);
  }

  @Override
  public boolean hasRole(String username, String roleName, String groupName) {
    Map<String, CachedAcl> acls = getAclsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl != null) {
        return acl.hasRole(roleName, groupName);
      }
    }

    return false;
  }

  @Override
  public void removeRole(String username, String roleName) {
    removeRole(username, roleName, GroupManagement.DEFAULT_GROUP_NAME);
  }

  @Override
  public void removeRole(String username, String roleName, String groupName) {
    Map<String, CachedAcl> acls = getAclsFromRequest();
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl != null) {
        acl.removeRole(roleName, groupName);
        acls.put(username, acl);
        saveAclsToRequest(acls);
      }
    }

    if (perms != null) {
      perms.remove(roleName);
      savePermissionsToRequest(perms);
    }

  }

  @Override
  public CachedAcl getAcl(String username) {
    Map<String, CachedAcl> acls = getAclsFromRequest();

    if (acls != null) {
      return acls.get(username);
    }
    return null;
  }

  @Override
  public Iterator<?> getRoles(String username) {
    Map<String, CachedAcl> acls = getAclsFromRequest();

    if (acls != null) {
      CachedAcl acl = acls.get(username);
      if (acl != null) {
        return acl.getRoles();
      }
    }

    return null;
  }

  @Override
  public Permission getPermission(String roleName, String permissionName) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      Map<String, Permission> map = perms.get(roleName);
      if (map != null) {
        return map.get(permissionName);
      }
    }

    return null;
  }

  @Override
  public void addPermission(String roleName, Permission permission) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      Map<String, Permission> map = perms.get(roleName);
      if (map != null) {
        map.put(permission.getName(), permission);
        perms.put(roleName, map);
        savePermissionsToRequest(perms);
      }
    }
  }

  @Override
  public boolean hasPermission(String roleName, String permissionName) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      Map<String, Permission> map = perms.get(roleName);
      if (map != null) {
        return map.containsKey(permissionName);
      }
    }
    return false;
  }

  @Override
  public void removePermission(String roleName, String permissionName) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      Map<String, Permission> map = perms.get(roleName);
      if (map != null) {
        map.remove(permissionName);
        perms.put(roleName, map);
        savePermissionsToRequest(perms);
      }
    }
  }

  @Override
  public Iterator<?> getPermissions(String roleName) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      Map<String, Permission> map = perms.get(roleName);
      if (map != null) {
        return map.values().iterator();
      }
    }

    return null;
  }

  @Override
  public void removeAllRoles(String rolename) {
    Map<String, CachedAcl> acls = getAclsFromRequest();
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();
    if (acls != null) {
      Iterator<CachedAcl> iterator = acls.values().iterator();
      while (iterator.hasNext()) {
        CachedAcl acl = iterator.next();
        acl.removeRole(rolename);
      }
      saveAclsToRequest(acls);
    }
    if (perms != null) {
      perms.remove(rolename);
      savePermissionsToRequest(perms);
    }
  }

  @Override
  public void removeAllPermissions(String permissionName) {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    if (perms != null) {
      Iterator<Map<String, Permission>> iterator = perms.values().iterator();
      while (iterator.hasNext()) {
        Map<String, Permission> map = iterator.next();
        map.remove(permissionName);
      }
      savePermissionsToRequest(perms);
    }
  }

  @Override
  public void loadRolePermissions() {
    Map<String, Map<String, Permission>> perms = getPermissionsFromRequest();

    try {
      if (perms != null) {
        @SuppressWarnings("unchecked")
        Iterator<Role> roles = JetspeedSecurity.getRoles();
        while (roles.hasNext()) {
          Role role = roles.next();
          Map<String, Permission> map = new HashMap<String, Permission>();
          @SuppressWarnings("unchecked")
          Iterator<Permission> prms =
            JetspeedSecurity.getPermissions(role.getName());
          while (prms.hasNext()) {
            Permission perm = prms.next();
            map.put(perm.getName(), perm);
          }
          perms.put(role.getName(), map);
        }
        savePermissionsToRequest(perms);
      }
    } catch (JetspeedSecurityException e) {
      logger.error("ALSecurityCache.loadRolePermissions", e);
    }
  }

  @SuppressWarnings("unchecked")
  protected Map<String, CachedAcl> getAclsFromRequest() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      Map<String, CachedAcl> cache = null;
      try {
        cache = (Map<String, CachedAcl>) request.getAttribute(ACLS_KEY);
      } catch (Throwable ignore) {
        // ignore
      }
      return cache;
    }
    return null;
  }

  protected void saveAclsToRequest(Map<String, CachedAcl> map) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(ACLS_KEY, map);
    }
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Map<String, Permission>> getPermissionsFromRequest() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      Map<String, Map<String, Permission>> cache = null;
      try {
        cache =
          (Map<String, Map<String, Permission>>) request
            .getAttribute(PERMS_KEY);
      } catch (Throwable ignore) {
        // ignore
      }
      return cache;
    }
    return null;
  }

  protected void savePermissionsToRequest(
      Map<String, Map<String, Permission>> map) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(PERMS_KEY, map);
    }
  }

  // /////////////////////////////////////////////////////////////////////////
  // Service Init
  // /////////////////////////////////////////////////////////////////////////

  @Override
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    if (getInit()) {
      return;
    }

    super.init(conf);

    setInit(true);
  }

}
