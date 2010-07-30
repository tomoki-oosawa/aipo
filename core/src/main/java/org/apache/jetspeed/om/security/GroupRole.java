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

package org.apache.jetspeed.om.security;

/**
 * Interface for group/role container.
 *
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: GroupRole.java,v 1.2 2004/02/23 03:14:12 jford Exp $
 */
public interface GroupRole
{
    /**
     * Get the Group
     *
     * @return the group.
     */
    public Group getGroup();

    /**
     * Set the Group
     *
     * @param group the Group.
     */
    public void setGroup(Group group);

    /**
     * Get the Role
     *
     * @return the role.
     */
    public Role getRole();

    /**
     * Set the Role
     *
     * @param group the Role.
     */
    public void setRole(Role role);

}
