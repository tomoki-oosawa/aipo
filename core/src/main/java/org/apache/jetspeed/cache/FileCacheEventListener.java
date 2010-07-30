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

package org.apache.jetspeed.cache;

/**
 * FileCacheEventListener on notifications sent when FileCache events occur
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *  @version $Id: FileCacheEventListener.java,v 1.4 2004/02/23 02:46:05 jford Exp $
 */

public interface FileCacheEventListener
{
    /**
     * Refresh event, called when the entry is being refreshed from file system.
     *
     * @param entry the entry being refreshed.
     */
    void refresh(FileCacheEntry entry);

    /**
     * Evict event, called when the entry is being evicted out of the cache
     *
     * @param entry the entry being refreshed.
     */
    void evict(FileCacheEntry entry);

}



