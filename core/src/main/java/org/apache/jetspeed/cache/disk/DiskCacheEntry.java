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

package org.apache.jetspeed.cache.disk;
 
import java.io.*;

/**
Interface for a DiskCacheEntry.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
*/
public interface DiskCacheEntry {

    /**
    Get the URL that was cached.
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public String getURL();
    
    /**
    Get the original URL this came from
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public String getSourceURL();

    /**
    Get a File which is representative of this item in the cache
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    @deprecated 
    @use We should use getInputStream (preferred) or getData instead
    */
    public File getFile();

    /**
    Get the contents/data of this URL
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public String getData() throws IOException;

    /**
    Get an InputStream for this Entry.

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public InputStream getInputStream() throws IOException;

    /**
    Get a Reader for this Entry.
    It should take into account the character encoding.

    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public Reader getReader() throws IOException;
    
    /**
    Get a Writer to update  this Entry.
    It should take into account the character encoding.
	It will throw an exception if the entry is not writable

    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public Writer getWriter() throws IOException;
    
    /**
    Get  the lastModified date of this  DiskCacheEntry
    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public long getLastModified();

    /**
    Set  the lastModified date of this  DiskCacheEntry
    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public void setLastModified(long time);

    /**
    Get the expiration time of this  DiskCacheEntry
    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public long getExpirationTime();

    /**
    Set the expiration time of this  DiskCacheEntry
    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public void setExpirationTime(long time);

    /**
    Test if the entry is expired
    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public boolean hasExpired();

    /**
    Test if the entry is a Local (fake) disk cache entry
    @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
    @version $Id: DiskCacheEntry.java,v 1.9 2004/02/23 02:45:29 jford Exp $
    */
    public boolean isLocal();

}

