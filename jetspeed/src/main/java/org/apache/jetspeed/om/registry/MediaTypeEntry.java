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

/**
 * This entry describes all the properties that should be present in
 * a RegistryEntry describing a MediaType
 *
 * FIXME: we should add some additionnal attrbutes for separating 2 versions
 * of the same mime type
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: MediaTypeEntry.java,v 1.4 2004/02/23 03:11:39 jford Exp $
 */
public interface MediaTypeEntry extends RegistryEntry
{

    /** @return the mime type associated with this MediaType */
    public String getMimeType();

    /** Sets the MimeType associated with this MediaType
     *  @param mimeType the MIME type to associate
     */
    public void setMimeType( String mimeType );

    /** @return the character set associated with this MediaType */
    public String getCharacterSet();

    /** Sets the character set associated with this MediaType */
    public void setCharacterSet( String charSet);

    /**
     * Returns all supported capablities as <CODE>CapabilityMap</CODE>.
     * The <CODE>CapabilityMap</CODE> contains all capabilities in arbitrary
     * order.
     *
     * @return the CapabilityMap
     * @see CapabilityMap
     */
    public CapabilityMap getCapabilityMap();
}
