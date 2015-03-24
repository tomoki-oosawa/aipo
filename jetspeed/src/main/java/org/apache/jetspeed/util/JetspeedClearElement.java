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
 
package org.apache.jetspeed.util;

import org.apache.jetspeed.util.JetspeedNullFilter;

import org.apache.ecs.StringElement;

/**
A basic ECS element that doesn't have a filter.  This allows content developers
to use ECS with legacy applications that want to generate HTML but also be a
use raw HTML.  Don't use this unless you have to do so for compatibilty reasons
*/

public class JetspeedClearElement extends StringElement {
    
    public JetspeedClearElement(String string) {
        super( string );
        
        this.setFilter( new JetspeedNullFilter() );
    }


}


