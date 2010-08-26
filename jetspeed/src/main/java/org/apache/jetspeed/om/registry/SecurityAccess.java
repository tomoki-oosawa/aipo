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

// Java imports
import java.util.Vector;

/**
 * Interface for manipulatin the security allow on the registry entries
 *
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: SecurityAccess.java,v 1.4 2004/02/23 03:11:39 jford Exp $
 */
public interface SecurityAccess {

    /** Getter for property action.
     * @return Value of property action.
     */
    public String getAction();
    
    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(String action);
    
    /** Getter for property allows.
     * @return Value of property allows.
     */
    public Vector getAllows();
    
    /** Setter for property allows.
     * @param allows New value of property allows.
     */
    public void setAllows(Vector allows);
    
    /** Getter for property allAllows.
     * @return Value of property allAllows.
     */
    public Vector getAllAllows();
    
    /** Getter for property ownerAllows.
     * @return Value of property ownerAllows.
     */
    public Vector getOwnerAllows();
    
    /** Setter for property ownerAllows.
     * @param ownerAllows New value of property ownerAllows.
     */
    public void setOwnerAllows(Vector ownerAllows);
    
}
