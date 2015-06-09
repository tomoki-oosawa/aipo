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

import org.apache.jetspeed.util.MimeType;

/**
 * <P>
 * The <CODE>MimeTypeMap</CODE> interface represents a list that stores all
 * mimetypes a client supports. The mimetypes are stored in decreasing order of
 * importance. The very first mimetype is the preferred mimetype of the client.
 * </P>
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: MimetypeMap.java,v 1.3 2004/02/23 03:11:39 jford Exp $
 */
public interface MimetypeMap {

  /**
   * Returns an enumeration of all mimetypes the client supports.
   *
   * @return an enumeration of all mimetypes
   */
  public Iterator getMimetypes();

  /**
   * Returns the preferred mimetype of the client.
   *
   * @return the preferred mimetype
   */
  public MimeType getPreferredMimetype();

  /**
   * Adds the given mimetype
   *
   * @param name
   *          the name of the mimetype
   */
  public void addMimetype(String name);

  /**
   * Removes the given mimetype
   *
   * @param name
   *          the mimetype to be removed
   */
  public void removeMimetype(String name);

}
