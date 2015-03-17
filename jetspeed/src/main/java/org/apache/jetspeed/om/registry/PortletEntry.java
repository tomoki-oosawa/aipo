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

package org.apache.jetspeed.om.registry;

import java.util.Iterator;

/**
 * This entry describes all the properties that should be present in a
 * RegistryEntry describing a Portlet.
 * <p>
 * Each PortletEntry must have a type, which may be:
 * <dl>
 * <dt>abstract</dt>
 * <dd>The entry description is unsuitable for instanciating a Portlet</dd>
 * <dt>instance</dt>
 * <dd>This entry may be used to create a Portlet and does not depend on any
 * other portlet entries</dd>
 * <dt>ref</dt>
 * <dd>This entry may be used to instanciate Portlets but depends on another
 * PortletEntry definition whose registry name can be retrieved by getParent()</dd>
 * </dl>
 * </p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: PortletEntry.java,v 1.5 2004/02/23 03:11:39 jford Exp $
 */
public interface PortletEntry extends PortletInfoEntry {

  public static final String TYPE_REF = "ref";

  public static final String TYPE_INSTANCE = "instance";

  public static final String TYPE_ABSTRACT = "abstract";

  public final static String DEFAULT_GROUP = "Jetspeed";

  public final static String DEFAULT_CATEGORY_REF = "General";

  public final static String DEFAULT_CATEGORY_ABSTRACT = "Abstract";

  /** @return the URL associated with this portlet or null */
  public String getURL();

  /**
   * Sets the URL for this PortletEntry
   * 
   * @param url
   *          the new PortletEntry URL
   */
  public void setURL(String url);

  /**
   * Determines whether to use the URL as part of the unique id to the portlet
   * cache. This can be used to control the lifetime of the portlet. The URL is
   * used in combination with the parameter names and values for this portlet to
   * uniquely identify to portlet. Parameters may also be optionally included in
   * the cache key. This value can be set in the portlet registry.
   *
   * @return true if the URL is to be part of the cache key.
   */
  public boolean isCachedOnURL();

  /**
   * Determines whether to use the URL as part of the unique id to the portlet
   * cache. This can be used to control the lifetime of the portlet. The URL is
   * used in combination with the parameter names and values for this portlet to
   * uniquely identify to portlet. Parameters may also be optionally included in
   * the cache key. This value can be set in the portlet registry.
   *
   * @return cached set to true if want this portlet to be cached based on the
   *         url
   */
  public void setCachedOnURL(boolean cached);

  /**
   * Gets the URL entry record for this portlet entry
   *
   * @return ContentURL the URL entry object
   */
  public ContentURL getURLEntry();

  /**
   * helper to get an instance of a cached parameter.
   *
   * @param name
   *          The parameter name.
   * @return The cached parameter entry.
   */
  public CachedParameter getCachedParameter(String name);

  /** @return the entry name from which this one is derived */
  public String getParent();

  /**
   * Sets the ancestor for this PortletEntry.
   * 
   * @param parent
   *          the new ancestor entry name. This name should be defined in the
   *          system registry
   */
  public void setParent(String parent);

  /**
   * @return true is this entry is only accessible by the portal administrators.
   */
  public boolean isAdmin();

  /** @return true is the PortletEntry is marked as an application */
  public boolean isApplication();

  /**
   * Sets the application status of this portlet entry. If an entry is maked as
   * application, the associated portlet will only be displayed in Maximized
   * mode and can be retrieved specifically
   *
   * @param application
   *          the new application status
   */
  public void setApplication(boolean application);

  /** @return the type of this entry */
  public String getType();

  /**
   * Sets the type of this entry. The type specifies whether it is abstract,
   * instance or ref
   *
   * @param type
   *          the new type for the PortletEntry
   */
  public void setType(String type);

  /**
   * Returns a list of the categories
   *
   * @return an iterator on the categories
   */
  public Iterator listCategories();

  /**
   * Test if a given category exists for this entry
   *
   * @param name
   *          the category name
   * @return true is the category exists in the default group
   */
  public boolean hasCategory(String name);

  /**
   * Test if a given category exists for this entry, in the specified group of
   * categories.
   *
   * @param name
   *          the category name
   * @param group
   *          the category group
   * @return true is the category exists in the specified group
   */
  public boolean hasCategory(String name, String group);

  /**
   * Add a new category to this portlet entry in the default group.
   *
   * @param name
   *          the category name
   */
  public void addCategory(String name);

  /**
   * Add a new category to this portlet entry.
   *
   * @param name
   *          the category name
   * @param group
   *          the category group name
   */
  public void addCategory(String name, String group);

  /**
   * Remove a category from this portlet entry in the default group.
   *
   * @param name
   *          the category name
   */
  public void removeCategory(String name);

  /**
   * Remove a category from this portlet entry in the specified group.
   *
   * @param name
   *          the media type name to remove.
   * @param group
   *          the category group name
   */
  public void removeCategory(String name, String group);

}
