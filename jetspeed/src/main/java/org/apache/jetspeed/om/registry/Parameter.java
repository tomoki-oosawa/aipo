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
 * Interface describing a parameter for a registry entry. 
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: Parameter.java,v 1.2 2004/02/23 03:11:39 jford Exp $
 */
public interface Parameter extends RegistryEntry
{

    /** @return the value for this parameter */
    public String getValue();

    /** Sets the value of this parameter.
     * 
     * @param value the new parameter value
     */
    public void setValue(String value);

    /** @return the parameter's type */
    public String getType();

    /** Sets the type of this parameter.value.
     * 
     * @param type the new parameter type
     */
    public void setType(String type);

}
