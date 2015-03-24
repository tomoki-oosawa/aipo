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
 * Interface for storing tool information for a portlet, control or controller
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: ToolDescriptor.java,v 1.2 2004/02/23 03:11:39 jford Exp $
 */
public interface ToolDescriptor
{
    /** @return the name of the tool */
    public String getName();

    /** Sets the name for this tool
     * @param title the new name of the tool
     */
    public void setName( String name );

    /** @return the scope of this tool */
    public String getScope();

    /** Sets the scope of this tool.
     * The currently recognized scope are "request", "session", "persistent", "global"
     * @param scope the new scope of this tool
     */
    public void setScope( String scope );

    /** @return the clasname of this tool */
    public String getClassname();

    /** Sets the classname of this tool
     * @param classname the new classname of this tool
     */
    public void setClassname( String classname );
}
