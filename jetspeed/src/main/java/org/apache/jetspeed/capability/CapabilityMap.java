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

package org.apache.jetspeed.capability;

import org.apache.jetspeed.util.*;
import java.util.*;

/**
 * This interface provides lookup features on the capabilities supported
 * by a client user agent.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id: CapabilityMap.java,v 1.8 2004/02/23 02:46:39 jford Exp $
 */
public interface CapabilityMap
{

    /** Handle HTML Table */
    public static final int HTML_TABLE = 0;

    /** Handle inline image display */
    public static final int HTML_IMAGE = 1;

    /** Handle form handling */
    public static final int HTML_FORM = 2;

    /** Handle frames */
    public static final int HTML_FRAME = 3;

    /** Handle client-side applet */
    public static final int HTML_JAVA = 17;
    public static final int HTML_JAVA1_0 = 4;
    public static final int HTML_JAVA1_1 = 5;
    public static final int HTML_JAVA1_2 = 6;

    /** Handle client-side javascript */
    public static final int HTML_JSCRIPT = 18;
    public static final int HTML_JSCRIPT1_0 = 7;
    public static final int HTML_JSCRIPT1_1 = 8;
    public static final int HTML_JSCRIPT1_2 = 9;

    /** Handle activex controls */
    public static final int HTML_ACTIVEX = 10;

    /** Handle CSS1 */
    public static final int HTML_CSS1 = 11;

    /** Handle CSS2 */
    public static final int HTML_CSS2 = 12;

    /** Handle CSSP */
    public static final int HTML_CSSP = 13;

    /** Handle XML */
    public static final int HTML_XML = 14;

    /** Handle XSL */
    public static final int HTML_XSL = 15;

    /** Handle DOM */
    public static final int HTML_DOM = 16;

    /**
    Returns the preferred MIME type for the current user-agent
    */
    public MimeType getPreferredType();

    /**
    Returns the preferred media type for the current user-agent
    */
    public String getPreferredMediaType();

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     */
    public Iterator listMediaTypes();

    /**
    Returns the user-agent string
    */
    public String getAgent();

    /**
    Checks to see if the current agent has the specified capability
    */
    public boolean hasCapability( int cap );

    /**
    Checks to see if the current agent has the specified capability
    */
    public boolean hasCapability( String capability );

    /**
    Get the mime types that this CapabilityMap supports.
    */
    public MimeType[] getMimeTypes();

    /**
    Return true if this CapabilityMap supports the given MimeType
    */
    public boolean supportsMimeType( MimeType mimeType );

    /**
     * Return true if this CapabilityMap supports the given media type
     *
     * @param media the name of a media type registered in the
     * MediaType regsitry
     *
     * @return true is the capabilities of this agent at least match those
     * required by the media type
     */
    public boolean supportsMediaType( String media );

    /**
    Create a map -> string representation
    */
    public String toString();

}

