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

package org.apache.jetspeed.xml;

import java.io.*;
//Jetspeed cache
import org.apache.jetspeed.cache.disk.*;

import org.xml.sax.*;


/**
 * an entity resolver which tries to lookup DTD files
 * through the URL Manager.
 *
 * TODO: Should be a singleton.
 *
 *@author <A HREF="mailto:christian.sell@netcologne.de">Christian Sell</A>
 *@author <A HREF="mailto:sgala@hisitech.com">Santiago Gala</A>
 *@version $Id: JetspeedXMLEntityResolver.java,v 1.8 2004/02/23 03:14:43 jford Exp $ 
 *
 **/
public class JetspeedXMLEntityResolver implements EntityResolver
{
    public InputSource resolveEntity (String publicId, String systemId)
    {
        try {
            //access Jetspeed cache and get a java.io.Reader
            Reader rdr = JetspeedDiskCache.getInstance().
                getEntry(systemId).getReader();
            InputSource is = new InputSource(rdr);
            is.setPublicId( publicId );
            is.setSystemId( systemId );
            return is;
        } catch(IOException x)
        {
            System.err.println("JER: ( " + publicId  + 
                               " Taking " + systemId + " from cache throwed Exception: " + x);
            
        }
        return null;
    }
}
