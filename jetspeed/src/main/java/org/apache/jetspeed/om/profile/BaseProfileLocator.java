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

package org.apache.jetspeed.om.profile;

import java.util.StringTokenizer;

import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.JetspeedUserFactory;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Interface definition for a Profile Locator. Locators are used by the profiler
 * to describe the parameters used to locate a resource in the persistent
 * configuration store.
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:adambalk@cisco.com">Atul Dambalkar </a>
 */

public class BaseProfileLocator implements ProfileLocator {
  // instance state
  private String name = null;

  private String mediaType = null;

  private String language = null;

  private String country = null;

  private JetspeedUser user = null;

  private Role role = null;

  private Group group = null;

  private boolean anonymous = false;

  private String roleName = null;

  private String userName = null;

  private String groupName = null;

  /** 組織名 */
  private String orgName = null;

  private static final String DELIM = "/";

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(BaseProfileLocator.class.getName());

  /*
   * Gets the unique profile locator id, which is a combination of the params
   * This ID must follow the one of the 4 sequences below:
   * 
   * <username>/ <mediaType>/ <language>/ <country>/ <page> <group>/
   * <mediaType>/ <language>/ <country>/ <page> <role>/ <mediaType>/ <language>/
   * <country>/ <page>
   * 
   * 
   * @return The profile locator id
   */

  public String getId() {
    StringBuffer id = new StringBuffer(128);

    if (!anonymous && user != null) {
      id.append(Profiler.PARAM_USER).append(DELIM);
      id.append(user.getUserName());
    } else if (group != null) {
      id.append(Profiler.PARAM_GROUP).append(DELIM);
      id.append(group.getName());
    } else if (role != null) {
      id.append(Profiler.PARAM_ROLE).append(DELIM);
      id.append(role.getName());
    } else {
      id.append(Profiler.PARAM_ANON);
    }
    if (language != null) {
      id.append(DELIM);
      id.append(language);
    }
    if (country != null) {
      id.append(DELIM);
      id.append(country);
    }
    if (mediaType != null) {
      id.append(DELIM);
      id.append(mediaType);
    }
    // if (orgName != null) {
    // id.append(DELIM);
    // id.append(orgName);
    // }
    if (name != null) {
      id.append(DELIM);
      id.append(name);
    }

    return id.toString();
  }

  /*
   * Gets the unique profile locator path, which is a combination of the name
   * value pairs. This ID must follow the one of the 4 sequences below:
   * 
   * user/ <name>/media-type/ <mediaType>/language/ <language> /country/
   * <country>/ <page>/page
   * 
   * group/ "" role/ ""
   * 
   * 
   * @return The profile locator path
   */

  public String getPath() {
    StringBuffer id = new StringBuffer(128);

    if (!anonymous && user != null) {
      id.append(Profiler.PARAM_USER).append(DELIM);
      id.append(user.getUserName()).append(DELIM);
    } else if (group != null) {
      id.append(Profiler.PARAM_GROUP).append(DELIM);
      id.append(group.getName()).append(DELIM);
    } else if (role != null) {
      id.append(Profiler.PARAM_ROLE).append(DELIM);
      id.append(role.getName()).append(DELIM);
    } else {
      id.append(Profiler.PARAM_USER).append(DELIM);
      id.append(Profiler.PARAM_ANON).append(DELIM);
    }

    if (language != null) {
      id.append(Profiler.PARAM_LANGUAGE).append(DELIM);
      id.append(language).append(DELIM);
    }
    if (country != null) {
      id.append(Profiler.PARAM_COUNTRY).append(DELIM);
      id.append(country).append(DELIM);
    }
    if (mediaType != null) {
      id.append(Profiler.PARAM_MEDIA_TYPE).append(DELIM);
      id.append(mediaType).append(DELIM);
    }
    // if (orgName != null) {
    // id.append(Profiler.PARAM_ORG).append(DELIM);
    // id.append(orgName).append(DELIM);
    // }
    if (name != null) {
      id.append(Profiler.PARAM_PAGE).append(DELIM);
      id.append(name).append(DELIM);
    }
    id.deleteCharAt(id.length() - 1);
    return id.toString();
  }

  /*
   * populates this profile locator from a given path in the format:
   * 
   * user/ <name>/media-type/ <mediaType>/language/ <language> /country/
   * <country>/ <page>/page
   * 
   * group/ "" role/ ""
   * 
   * @param path The formatted profiler path string.
   */
  public void createFromPath(String path) {
    StringTokenizer tok = new StringTokenizer(path, "/");
    while (tok.hasMoreTokens()) {
      String name = (String) tok.nextToken();
      if (name.equals(Profiler.PARAM_USER) && tok.hasMoreTokens()) {
        try {
          // keep profile locator from failing if the user has been removed
          // from security as it may still exist in the PSML structure.
          this.userName = tok.nextToken();
          this.setUser(JetspeedSecurity.getUser(this.userName));
        } catch (Exception e) {
          logger.error("ProfileLocator: Failed to set User: ", e);
        }
      } else if (name.equals(Profiler.PARAM_GROUP) && tok.hasMoreTokens()) {
        try {
          // keep profile locator from failing if the group has been removed
          // from security as it may still exist in the PSML structure.
          this.groupName = tok.nextToken();
          this.setGroup(JetspeedSecurity.getGroup(this.groupName));
        } catch (Exception e) {
          logger.error("ProfileLocator: Failed to set Group: ", e);
        }
      } else if (name.equals(Profiler.PARAM_ROLE) && tok.hasMoreTokens()) {
        try {
          // keep profile locator from failing if the role has been removed
          // from security as it may still exist in the PSML structure.
          this.roleName = tok.nextToken();
          this.setRole(JetspeedSecurity.getRole(this.roleName));
        } catch (Exception e) {
          logger.error("ProfileLocator: Failed to set Role: ", e);
        }
      } else if (name.equals(Profiler.PARAM_PAGE) && tok.hasMoreTokens()) {
        this.setName(tok.nextToken());
      } else if (name.equals(Profiler.PARAM_MEDIA_TYPE) && tok.hasMoreTokens()) {
        this.setMediaType(tok.nextToken());
      } else if (name.equals(Profiler.PARAM_LANGUAGE) && tok.hasMoreTokens()) {
        this.setLanguage(tok.nextToken());
      } else if (name.equals(Profiler.PARAM_COUNTRY) && tok.hasMoreTokens()) {
        this.setCountry(tok.nextToken());
      }
      // } else if (name.equals(Profiler.PARAM_ORG) && tok.hasMoreTokens()) {
      // this.setOrgName(tok.nextToken());
      // }

    }
  }

  /**
   * @see Object#clone
   * @return an instance copy of this object
   */
  public Object clone() throws java.lang.CloneNotSupportedException {
    return super.clone();
  }

  /*
   * Gets the resource name parameter for this profile.
   * 
   * @return The resource name parameter for this profile.
   */
  public String getName() {
    return name;
  }

  /*
   * Sets the resource name parameter for this profile.
   * 
   * @param The resource name parameter for this profile.
   */
  public void setName(String name) {
    this.name = name;
  }

  /*
   * Gets the anonymous user flag for this profile.
   * 
   * @param The user parameter for this profile.
   */
  public boolean getAnonymous() {
    return this.anonymous;
  }

  /*
   * Sets the user parameter as the anonymous user
   * 
   * @param anonymous True indicates this is an anonymous user.
   */
  public void setAnonymous(boolean anonymous) {
    try {
      JetspeedUser user = JetspeedUserFactory.getInstance();
      user.setUserName(JetspeedSecurity.getAnonymousUserName());
      this.setUser(user);
    } catch (Exception e) {
      logger.error("Could not get Anonymous user", e);
    } finally {
      this.anonymous = anonymous;
    }
  }

  /*
   * Gets the media type parameter for this profile. Media types are values such
   * as html, wml, xml ...
   * 
   * @return The media type parameter for this profile.
   */
  public String getMediaType() {
    return mediaType;
  }

  /*
   * Sets the media type parameter for this profile. Media types are values such
   * as html, wml, xml ...
   * 
   * @param The media type parameter for this profile.
   */
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /*
   * Gets the language parameter for this profile. Language values are ISO-639
   * standard language abbreviations en, fr, de, ...
   * 
   * @return The language parameter for this profile.
   */
  public String getLanguage() {
    return language;
  }

  /*
   * Sets the language parameter for this profile. Language values are ISO-639
   * standard language abbreviations en, fr, de, ...
   * 
   * @param The language parameter for this profile.
   */
  public void setLanguage(String language) {
    this.language = language;
  }

  /*
   * Gets the country code parameter for this profile. Country code values are
   * ISO-3166 standard country code abbreviations. GB, US, FR, CA, DE, ...
   * 
   * @return The country code parameter for this profile.
   */
  public String getCountry() {
    return country;
  }

  /*
   * Sets the country code parameter for this profile. Country code values are
   * ISO-3166 standard country code abbreviations. GB, US, FR, CA, DE, ...
   * 
   * @param The country code parameter for this profile.
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /*
   * Gets the user parameter for this profile.
   * 
   * @return The user parameter for this profile.
   */
  public JetspeedUser getUser() {
    return user;
  }

  public String getUserName() {
    if (null == user)
      return userName;

    return user.getUserName();
  }

  /*
   * Sets the user parameter for this profile.
   * 
   * @param The user parameter for this profile.
   */
  public void setUser(JetspeedUser user) {
    this.user = user;
  }

  /*
   * Gets the role parameter for this profile.
   * 
   * @return The role parameter for this profile.
   */
  public Role getRole() {
    return role;
  }

  public String getRoleName() {
    if (null == role)
      return roleName;

    return role.getName();
  }

  /*
   * Sets the role parameter for this profile.
   * 
   * @param The role parameter for this profile.
   */
  public void setRole(Role role) {
    this.role = role;
  }

  public void setRoleByName(String roleName) {
    try {
      Role temp = JetspeedSecurity.getRole(roleName);
      if (null != temp) {
        role = temp;
      }
    } catch (Exception e) {
      logger.error("ProfileLocator: Failed to set Role " + roleName, e);
    }
  }

  /*
   * Gets the group parameter for this profile.
   * 
   * @return The group parameter for this profile.
   */
  public Group getGroup() {
    return group;
  }

  public String getGroupName() {
    if (null == group)
      return groupName;

    return group.getName();
  }

  /*
   * Sets the group parameter for this profile.
   * 
   * @param The group parameter for this profile.
   */
  public void setGroup(Group group) {
    this.group = group;
  }

  public void setGroupByName(String groupName) {
    try {
      Group temp = JetspeedSecurity.getGroup(groupName);
      if (null != temp) {
        group = temp;
      }
    } catch (Exception e) {
      logger.error("ProfileLocator: Failed to set Group: " + e);
    }
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getOrgName() {
    return orgName;
  }

  /*
   * Comparision Functions. Contributed by Atul Dambalkar
   */

  /**
   * Define equality criteria for ProfileLocator objects.
   * 
   * @param obj
   *            ProfileLocator object to be compared with.
   */
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    synchronized (obj) {
      if (!(obj instanceof ProfileLocator)) {
        return false;
      }

      ProfileLocator locator = (ProfileLocator) obj;

      String name = locator.getName();
      String mediaType = locator.getMediaType();
      String language = locator.getLanguage();
      String country = locator.getCountry();
      Group group = locator.getGroup();
      Role role = locator.getRole();
      // String orgName = locator.getOrgName();

      return nameEquals(name)
          // && locator.getId() == id
          // && orgNameEquals(orgName) && mediaTypeEquals(mediaType)
          && mediaTypeEquals(mediaType) && languageEquals(language)
          && countryEquals(country) && userEquals(locator)
          && groupEquals(group) && roleEquals(role);
    }
  }

  /**
   * Check equality for given User object with this ProfileLocator's User
   * object.
   */
  private boolean userEquals(ProfileLocator locator) {
    JetspeedUser user = locator.getUser();
    // if either of reference is null return false.
    if (exclusiveOr(this.user, user)) {
      return false;
    }
    // check if both are non-nulls
    if (assertNotNull(this.user) && assertNotNull(user)) {
      return stringEquals(this.user.getUserName(), user.getUserName());
    }
    // can be anonymous user
    return this.anonymous == locator.getAnonymous();
  }

  /**
   * Check equality for given Group object with this ProfileLocator's Group
   * object.
   */
  private boolean groupEquals(Group group) {
    // if either of reference is null return false.
    if (exclusiveOr(this.group, group)) {
      return false;
    }
    // check if both are non-nulls
    if (assertNotNull(this.group) && assertNotNull(group)) {
      return stringEquals(this.group.getName(), group.getName());
    }
    // both are null
    return true;
  }

  /**
   * Check equality for given Role object with this ProfileLocator's Role
   * object.
   */
  private boolean roleEquals(Role role) {
    // if either of reference is null return false.
    if (exclusiveOr(this.role, role)) {
      return false;
    }
    // check if both are non-nulls
    if (assertNotNull(this.role) && assertNotNull(role)) {
      return stringEquals(this.role.getName(), role.getName());
    }
    // both are null
    return true;
  }

  /**
   * Check equality for language object with this ProfileLocator's language
   * object.
   */
  private boolean languageEquals(String language) {
    return stringEquals(this.language, language);
  }

  /**
   * Check equality for country object with this ProfileLocator's country
   * object.
   */
  private boolean countryEquals(String country) {
    return stringEquals(this.country, country);
  }

  /**
   * Check equality for name object with this ProfileLocator's name object.
   */
  private boolean nameEquals(String name) {
    return stringEquals(this.name, name);
  }

  /**
   * Check equality for name object with this ProfileLocator's name object.
   */
  private boolean orgNameEquals(String orgName) {
    return stringEquals(this.orgName, orgName);
  }

  /**
   * Check equality for name object with this ProfileLocator's name object.
   */
  private boolean mediaTypeEquals(String mediaType) {
    return stringEquals(this.mediaType, mediaType);
  }

  /**
   * AssertNotNull the two String objects and then check the equality.
   */
  private boolean stringEquals(String str1, String str2) {
    if (exclusiveOr(str1, str2)) {
      return false;
    }
    if (assertNotNull(str1) && assertNotNull(str2)) {
      return str1.equals(str2);
    }
    // both are null
    return true;
  }

  /**
   * AssertNotNull the given object.
   */
  private boolean assertNotNull(Object object) {
    return object != null;
  }

  /**
   * Exclusive or the two objects fro their references null-ability.
   */
  private boolean exclusiveOr(Object obj1, Object obj2) {
    return (assertNotNull(obj1) && !assertNotNull(obj2))
        || (!assertNotNull(obj1) && assertNotNull(obj2));
  }
}
