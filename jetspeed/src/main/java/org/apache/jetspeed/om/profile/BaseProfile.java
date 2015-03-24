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

import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.PsmlManager;

/**
 * Provides base functionality within a Registry.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */

public class BaseProfile extends BaseProfileLocator implements Profile {

  protected PSMLDocument document = null;

  public BaseProfile() {
  }

  public BaseProfile(ProfileLocator locator) {
    init(locator);
  }

  public void init(ProfileLocator locator) {
    this.setAnonymous(locator.getAnonymous());
    this.setCountry(locator.getCountry());
    this.setGroup(locator.getGroup());
    this.setLanguage(locator.getLanguage());
    this.setMediaType(locator.getMediaType());
    this.setName(locator.getName());
    this.setRole(locator.getRole());
    this.setUser(locator.getUser());
    this.setOrgName(locator.getOrgName());
  }

  /**
   * @see Object#clone
   * @return an instance copy of this object
   */
  public Object clone() throws java.lang.CloneNotSupportedException {
    Object cloned = super.clone();

    // clone the document
    ((BaseProfile) cloned).document = ((this.document == null) ? null
        : (PSMLDocument) this.document.clone());

    return cloned;
  }

  /**
   * Gets the root set of portlets for this profile object.
   * 
   * @return The root portlet set for this profile.
   */
  public PortletSet getRootSet() {
    return PortalToolkit.getSet(getDocument().getPortlets());
  }

  /**
   * Gets the root set of portlets for this profile object.
   * 
   * @return The root portlet set for this profile.
   */
  public PSMLDocument getDocument() {
    synchronized (this) {
      if ((this.document == null) || (this.document.getPortlets() == null)) {
        this.document = PsmlManager.getDocument(this);
      }
    }

    return this.document;
  }

  /*
   * Sets the psml document attached to this profile
   * 
   * @param The PSML document for this profile.
   */
  public void setDocument(PSMLDocument document) {
    this.document = document;
  }

  /**
   * stores the resource by merging and rewriting the psml file
   * 
   * @throws ProfileException
   *             if an error occurs storing the profile
   */
  public void store() throws ProfileException {
    if (document != null) {
      PsmlManager.store(this);
    }
  }

  /**
   * provide useful info for ease of debugging
   */
  public String toString() {
    return "BaseProfile[" + getId() + "]"; /*
                                             * getUser().getUserName()+","+
                                             * getGroup().getName()+","+
                                             * getRole().getName()+","+
                                             * (getAnonymous() ? "anon,":"")+
                                             * getMediaType()+","+
                                             * getCountry()+","+
                                             * getLanguage()+","+ getName()+"]";
                                             */
  }

}
