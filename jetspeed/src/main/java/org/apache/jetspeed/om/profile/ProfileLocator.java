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

package org.apache.jetspeed.om.profile;

import java.io.Serializable;

import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;

/**
 * Interface definition for a Profile Locator. Locators are used by the profiler
 * to describe the parameters used to locate a resource in the persistent
 * configuration store.
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */

public interface ProfileLocator extends Serializable, Cloneable {
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
  public void createFromPath(String path);

  /*
   * Gets the unique profile locator id, which is a combination of the params
   * This ID must follow the one of the 4 sequences below:
   * 
   * <username>/ <mediaType>/ <language>/ <country>/ <page> <group>/
   * <mediaType>/ <language>/ <country>/ <page> <role>/ <mediaType>/ <language>/
   * <country>/ <page>
   * 
   * @return The profile locator id
   */
  public String getId();

  /*
   * Gets the unique profile locator path, which is a combination of the params
   * This Path must follow the one of the 4 sequences below:
   * 
   * user/ <name>/media-type/ <mediaType>/language/ <language> /country/
   * <country>/ <page>/page
   * 
   * group/ "" role/ ""
   * 
   * @return The profile locator path
   */
  public String getPath();

  /*
   * Gets the resource name parameter for this profile.
   * 
   * @return The resource name parameter for this profile.
   */
  String getName();

  /*
   * Sets the resource name parameter for this profile.
   * 
   * @param name The resource name parameter for this profile.
   */
  void setName(String name);

  /*
   * Gets the media type parameter for this profile. Media types are values such
   * as html, wml, xml ...
   * 
   * @return The media type parameter for this profile.
   */
  public String getMediaType();

  /*
   * Sets the media type parameter for this profile. Media types are values such
   * as html, wml, xml ...
   * 
   * @param mediaType The media type parameter for this profile.
   */
  public void setMediaType(String mediaType);

  /*
   * Gets the language parameter for this profile. Language values are ISO-639
   * standard language abbreviations en, fr, de, ...
   * 
   * @return The language parameter for this profile.
   */
  public String getLanguage();

  /*
   * Sets the language parameter for this profile. Language values are ISO-639
   * standard language abbreviations en, fr, de, ...
   * 
   * @param language The language parameter for this profile.
   */
  public void setLanguage(String language);

  /*
   * Gets the country code parameter for this profile. Country code values are
   * ISO-3166 standard country code abbreviations. GB, US, FR, CA, DE, ...
   * 
   * @return The country code parameter for this profile.
   */
  public String getCountry();

  /*
   * Sets the country code parameter for this profile. Country code values are
   * ISO-3166 standard country code abbreviations. GB, US, FR, CA, DE, ...
   * 
   * @param country The country code parameter for this profile.
   */
  public void setCountry(String country);

  /*
   * Gets the user parameter for this profile.
   * 
   * @return The user parameter for this profile.
   */
  public JetspeedUser getUser();

  public String getUserName();

  /*
   * Sets the user parameter for this profile.
   * 
   * @param user The user parameter for this profile.
   */
  public void setUser(JetspeedUser user);

  /*
   * Gets the anonymous user flag for this profile.
   * 
   * @return True if this profile is anonymous.
   */
  public boolean getAnonymous();

  /*
   * Sets the user parameter as the anonymous user
   * 
   * @param anonymous True indicates this is an anonymous user.
   */
  public void setAnonymous(boolean anonymous);

  /*
   * Gets the role parameter for this profile.
   * 
   * @return The role parameter for this profile.
   */
  public Role getRole();

  public String getRoleName();

  /*
   * Sets the role parameter for this profile.
   * 
   * @param role The role parameter for this profile.
   */
  public void setRole(Role role);

  public void setRoleByName(String roleName);

  /*
   * Gets the group parameter for this profile.
   * 
   * @return The group parameter for this profile.
   */
  public Group getGroup();

  public String getGroupName();

  /*
   * Sets the group parameter for this profile.
   * 
   * @param group The group parameter for this profile.
   */
  public void setGroup(Group group);

  public void setGroupByName(String groupName);

  public String getOrgName();

  public void setOrgName(String orgName);

  /**
   * @see Object#clone
   * @return an instance copy of this object
   */
  public Object clone() throws java.lang.CloneNotSupportedException;

}
