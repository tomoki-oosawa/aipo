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
package org.apache.jetspeed.services.search;

import java.util.Set;

/**
 * Contract for implementing a specific object handler. Implementation
 * should convert the object into a document suitable for placement into
 * search index.
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: ObjectHandler.java,v 1.4 2004/02/23 03:48:47 jford Exp $
 */
public interface ObjectHandler
{
    /**
     * Parses a specific object into a document suitable for index placement
     * 
     * @param o
     * @return 
     */
    public ParsedObject parseObject(Object o);
    
    /**
     * Returns the set of fields used to create the parsed object.
     * @return
     */
    public Set getFields();
    
    /**
     * Returns the set of keywords used to create the parsed object.
     * @return
     */
    public Set getKeywords();
}

