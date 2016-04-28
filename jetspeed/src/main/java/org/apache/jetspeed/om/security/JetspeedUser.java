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

package org.apache.jetspeed.om.security;

import java.util.Date;

import org.apache.turbine.om.security.User;

/**
 * A Jetspeed extension of the Turbine User interface.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedUser.java,v 1.4 2004/02/23 03:14:12 jford Exp $
 */
public interface JetspeedUser extends User {
  public static final String DISABLED = "DISABLED";

  public static final String USER_ID = "USER_ID";

  public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";

  /**
   * Returns the disabled status for the user
   * 
   * @return True when the account is disabled
   */
  public String getDisabled();

  public void setDisabled(String disabled);

  /**
   * Returns the primary principle for this User, the user id.
   * 
   * @return the user id.
   */
  public String getUserId();

  public boolean isNew();

  /**
   * Returns the date of last password change
   * 
   * @return date
   */
  public Date getPasswordChanged();

  /**
   * Sets the date of last password change
   * 
   * @param value
   *            Date
   */
  public void setPasswordChanged(Date value);

}
