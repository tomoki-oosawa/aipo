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

package org.apache.jetspeed.modules.pages;

import org.apache.turbine.util.RunData;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.velocity.context.Context;
import org.apache.jetspeed.util.template.JetspeedTemplateNavigation;

/**
 * Extends JetspeedTemplatePage to set the Velocity template Context.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: JetspeedVelocityPage.java,v 1.9 2004/02/23 02:59:52 jford Exp $
 */
public class JetspeedVelocityPage extends JetspeedTemplatePage
{
    /**
     * Stuffs the Context into the RunData so that it is available to
     * the Action module and the Screen module via getContext().
     *
     * Add a couple of default jetspeed context objects.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildBeforeAction(RunData data) throws Exception
    {
        super.doBuildBeforeAction(data);

        Context context = TurbineVelocity.getContext(data);

        context.put("jnavigation", new JetspeedTemplateNavigation(data));
        data.getTemplateInfo().setTemplateContext(VelocityService.CONTEXT, context);
    }

    /**
     * Allows the VelocityService to peform post-request actions.
     * (releases the (non-global) tools in the context for reuse later)
     */
    protected void doPostBuild(RunData data) throws Exception
    {
        Context context = TurbineVelocity.getContext(data);
        TurbineVelocity.requestFinished(context);
    }

}
