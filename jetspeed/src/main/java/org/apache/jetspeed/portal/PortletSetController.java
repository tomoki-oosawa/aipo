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

package org.apache.jetspeed.portal;

/**
 * The PortletSetController is a marker interface denoting that the controller
 * implementing this interface only expects to manipulates portlet sets and
 * not directly portlets.
 * This interface is mainly used by the customization component to decide
 * whether to add directly a portlet to the controller or to wrap it inside
 * a set.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PortletSetController.java,v 1.2 2004/02/23 04:05:35 jford Exp $
 */
public interface PortletSetController
{
    // simple marker interface
}
