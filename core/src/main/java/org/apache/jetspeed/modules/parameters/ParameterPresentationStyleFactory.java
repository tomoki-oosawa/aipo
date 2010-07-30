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

package org.apache.jetspeed.modules.parameters;

// Turbine Utility Classes
import org.apache.turbine.services.assemblerbroker.util.java.JavaBaseFactory;
import org.apache.turbine.modules.Assembler;

/**
 * A parameter presentation method factory factory that attempts to load a java class from
 * the module packages defined in the TurbineResource.properties.
 * 
 * @author <a href="mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: ParameterPresentationStyleFactory.java,v 1.2 2004/02/23 03:01:20 jford Exp $
 */
public class ParameterPresentationStyleFactory extends JavaBaseFactory
{

    /**
     * Retrieve named assembler instance
     * 
     * @param name   assembler name
     * @return assembler instance
     */
    public Assembler getAssembler(String name)
    {

        return getAssembler("parameters",name);
    }
}
