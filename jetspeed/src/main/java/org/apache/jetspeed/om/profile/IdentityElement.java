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

/**
 * ConfigElement is the base interface that objects must implement in order to
 * be used with the Profile service.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: IdentityElement.java,v 1.4 2004/02/23 03:05:01 jford Exp $
 */
public interface IdentityElement extends MetaInfo, ConfigElement {
  /**
   * @return the id of this entry. This value is guaranteed to be unique at
   *         least within the current Document.
   */
  public String getId();

  /**
   * Changes the name of this entry
   * 
   * @param name
   *          the new name for this entry
   */
  public void setId(String id);

  /**
   * set the MetaInfo
   * 
   * @param metaInfo
   *          info for this entry
   */
  public void setMetaInfo(MetaInfo metaInfo);

  /**
   * @return the metaInfo for this element
   */
  public MetaInfo getMetaInfo();

  public Skin getSkin();

  public void setSkin(Skin skin);

  public Layout getLayout();

  public void setLayout(Layout layout);

  public Control getControl();

  public void setControl(Control control);

}