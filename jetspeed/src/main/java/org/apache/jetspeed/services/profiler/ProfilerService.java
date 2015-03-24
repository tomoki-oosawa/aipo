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

package org.apache.jetspeed.services.profiler;

import org.apache.jetspeed.om.profile.*;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;
import org.apache.jetspeed.util.MimeType;
import java.util.Iterator;

/**
 * <P>This interface is a facade for all profile related operations</P>
 *
 * @see org.apache.jetspeed.om.profile.Profile
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: ProfilerService.java,v 1.18 2004/02/23 03:35:24 jford Exp $
 */

public interface ProfilerService extends Service
{

    /** The name of this service */
    public String SERVICE_NAME = "Profiler";

    /**
     *  get the Profile object using the Rundata state and capability map
     * this is the mapping functionality of the profiler
     *
     * @param rundata The rundata object for the current request.
     * @param cm  The device capability map.
     * @return A Profile object if found by the manager or null.
     */
    public Profile getProfile(RunData rundata, CapabilityMap cm)
        throws ProfileException;

    /**
     * get the Profile object using the Rundata state and capability map
     * this is the mapping functionality of the profiler
     *
     * @param rundata The rundata object for the current request.
     * @return A new Profile object.
     */
    public Profile getProfile(RunData rundata)
                throws ProfileException;

    /**
     * get the Profile object using the Rundata state and capability map
     * this is the mapping functionality of the profiler
     *
     * @deprecated Do not use a profiler method based on MimeType
     * @param rundata the rundata object for the current request
     * @return a new Profile object
     */
    public Profile getProfile(RunData data, MimeType mt)
        throws ProfileException;

    /**
     *  get the Profile object using a profile locator
     *
     * @param locator The locator containing criteria describing the profile.
     * @return a new Profile object
     */
    public Profile getProfile(ProfileLocator locator)
        throws ProfileException;

    /**
     * Creates a dynamic URI
     *
     * @param rundata the rundata object for the current request
     * @param locator The description of the profile.
     * @return A new dynamic URI representing all profile parameters from the locator.
     */
    public DynamicURI makeDynamicURI( RunData data, ProfileLocator locator )
        throws ProfileException;

    /**
     * Creates a new Profile object that can be successfully managed by
     * the current Profiler implementation
     *
     * @return A new Profile object
     */
    public Profile createProfile();

    /**
     * Creates a new Profile object for a specific locator.
     *
     * @param locator The description of the profile.
     * @return A new Profile object
     */
    public Profile createProfile(ProfileLocator locator);

    /**
     * Creates a new ProfileLocator object that can be successfully managed by
     * the current Profiler implementation
     *
     * @return A new ProfileLocator object
     */
    public ProfileLocator createLocator();

    /**
     * Create a new profile. The profile parameter's document will be cloned.
     *
     * @param rundata The rundata object for the current request.
     * @param profile The description of the new profile to be created.
     * @return The newly created profile.
     */
    public Profile createProfile( RunData data, Profile profile )
        throws ProfileException;

    /** Create a new profile.
     *
     * @param rundata The rundata object for the current request.
     * @param profile The description of the new profile to be created.
     * @param mt The specific mime type, which is converted to a mediatype.
     * @return The newly created profile.
     */
    public Profile createProfile( RunData data, Profile profile, MimeType mt )
        throws ProfileException;

    /** Create a new profile.
     *
     * @param locator The description of the new profile to be created.
     * @param portlets The PSML tree
     */
    public Profile createProfile(ProfileLocator locator, Portlets portlets)
        throws ProfileException;

   /**
     *  Removes a profile.
     *
     * @param locator The profile locator criteria.
     */
    public void removeProfile( ProfileLocator locator )
        throws ProfileException;

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     * @return The list of profiles matching the locator criteria.
     */
    public Iterator query( QueryLocator locator );

    /**
     * Returns status of role profile merging feature
     *
     * @return True if role profile merging is active
     */
    public boolean useRoleProfileMerging();

}
