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
Occurs when anything unexpected happens within Jetspeed and its Registry.  Any 

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: InvalidEntryException.java,v 1.6 2004/02/23 03:11:39 jford Exp $
*/

public class InvalidEntryException extends RegistryException {

    public static final String ENTRY_DOES_NOT_EXIST 
        = "The specified entry does not exist within the Registry";
    
    public static final String ENTRY_ALREADY_PRESENT 
        = "The entry specified already exists within the Registry";

    public static final String ENTRY_UNKNOWN 
        = "The entry type is unknown";
        
        
    public InvalidEntryException() {
        super();
    }

    public InvalidEntryException( String message ) {
        super( message );
    }


}

