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

package org.apache.jetspeed.portal.expire;

//jetspeed stuff

//java stuff
import java.util.Hashtable;

import org.apache.jetspeed.portal.Portlet;

/**
 * Handles content expiration
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
 */
public abstract class BaseExpire implements Expire {

  /**
   *
   */
  private static final long serialVersionUID = 3999313044598494571L;

  private boolean expired = false;

  private long creationTime;

  private final Hashtable<String, String> properties =
    new Hashtable<String, String>();

  private Portlet portlet = null;

  /**
   * @see Expire#init
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public void init() {
    this.setCreationTime(System.currentTimeMillis());
  }

  /**
   * @see Expire#isExpired
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public boolean isExpired() {
    return this.expired;
  }

  /**
   * @see Expire#setExpired
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  /**
   * @see Expire#getCreationTime
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public long getCreationTime() {
    return this.creationTime;
  }

  /**
   * @see Expire#setCreationTime
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public void setCreationTime(long creationTime) {

    this.creationTime = creationTime;
  }

  /**
   * @see Expire#setProperty
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public void setProperty(String name, String value) {
    this.properties.put(name, value);
  }

  /**
   * @see Expire#getProperty
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public String getProperty(String name) {
    return this.properties.get(name);
  }

  /**
   * Get the Portlet on which this is based.
   * 
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public Portlet getPortlet() {
    return this.portlet;
  }

  /**
   * Set the Portlet on which this is based.
   * 
   * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
   * @version $Id: BaseExpire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
   */
  @Override
  public void setPortlet(Portlet portlet) {
    this.portlet = portlet;
  }

}
