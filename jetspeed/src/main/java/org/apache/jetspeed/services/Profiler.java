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

package org.apache.jetspeed.services;

import java.util.Iterator;

import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.services.profiler.ProfilerService;
import org.apache.jetspeed.util.MimeType;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 * <P>
 * This is a commodity static accessor class around the
 * <code>ProfilerService</code> interface
 * </P>
 * 
 * @see org.apache.jetspeed.services.Profiler
 * @see org.apache.jetspeed.services.profiler.ProfilerService
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 */
public class Profiler {
  public final static String PARAM_ORG = "org";

  public final static String PARAM_MEDIA_TYPE = "media-type";

  public final static String PARAM_ROLE = "role";

  public final static String PARAM_GROUP = "group";

  public final static String PARAM_PAGE = "page";

  public final static String PARAM_USER = "user";

  public final static String PARAM_ANON = "anon";

  public final static String PARAM_LANGUAGE = "language";

  public final static String PARAM_COUNTRY = "country";

  public final static String DEFAULT_PROFILE = "default";

  public final static String FULL_DEFAULT_PROFILE = "default.psml";

  public final static String DEFAULT_EXTENSION = ".psml";

  /**
   * Commodity method for getting a reference to the service singleton
   */
  private static ProfilerService getService() {
    return (ProfilerService) TurbineServices.getInstance().getService(
        ProfilerService.SERVICE_NAME);
  }

  /**
   * get the Profile object using the Rundata state and capability map this is
   * the mapping functionality of the profiler
   * 
   * @param rundata
   *            The rundata object for the current request.
   * @param cm
   *            The device capability map for the current request.
   * @return a Profile object if found by the manager or null
   */
  public static Profile getProfile(RunData rundata, CapabilityMap cm)
      throws ProfileException {
    return getService().getProfile(rundata, cm);
  }

  /**
   * @see ProfilerService#getProfile
   */
  public static Profile getProfile(RunData rundata) throws ProfileException {
    return getService().getProfile(rundata);
  }

  /**
   * @see ProfilerService#getProfile
   * @deprecated Do not use a profiler method based on MimeType
   */
  public static Profile getProfile(RunData data, MimeType mt)
      throws ProfileException {
    return getService().getProfile(data, mt);
  }

  /**
   * get the Profile object using a profile locator
   * 
   * @param locator
   *            The locator containing criteria describing the profile.
   * @return a new Profile object
   */
  public static Profile getProfile(ProfileLocator locator)
      throws ProfileException {
    return getService().getProfile(locator);
  }

  /**
   * @see ProfilerService#makeDynamicURI
   */
  public static DynamicURI makeDynamicURI(RunData data, ProfileLocator locator)
      throws ProfileException {
    return getService().makeDynamicURI(data, locator);
  }

  /**
   * @see ProfilerService#createProfile
   */
  public static Profile createProfile() {
    return getService().createProfile();
  }

  /**
   * @see ProfilerService#createProfile
   */
  public static Profile createProfile(ProfileLocator locator) {
    return getService().createProfile(locator);
  }

  /**
   * @see ProfilerService#createProfile
   */
  public static ProfileLocator createLocator() {
    return getService().createLocator();
  }

  /**
   * @see ProfilerService#createProfile
   */
  public static Profile createProfile(RunData data, Profile profile)
      throws ProfileException {
    return getService().createProfile(data, profile);
  }

  /**
   * @see ProfilerService#createProfile
   */
  public static Profile createProfile(RunData data, Profile profile, MimeType mt)
      throws ProfileException {
    return getService().createProfile(data, profile, mt);
  }

  /**
   * @see ProfilerService#createProfile
   */
  public static Profile createProfile(ProfileLocator locator, Portlets portlets)
      throws ProfileException {
    return getService().createProfile(locator, portlets);
  }

  /**
   * @see ProfilerService#removeProfile
   */
  public static void removeProfile(ProfileLocator locator)
      throws ProfileException {
    getService().removeProfile(locator);
  }

  /**
   * @see ProfilerService#query
   */
  public static Iterator query(QueryLocator locator) {
    return getService().query(locator);
  }

  /**
   * @see ProfilerService#useRoleProfileMerging
   */
  public static boolean useRoleProfileMerging() {
    return getService().useRoleProfileMerging();
  }

}
