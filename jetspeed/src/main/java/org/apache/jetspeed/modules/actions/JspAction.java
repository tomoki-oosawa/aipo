package org.apache.jetspeed.modules.actions;

/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

// Turbine Stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.modules.screens.TemplateScreen;
import org.apache.turbine.modules.ActionEvent;

/**
 * This class provides a convenience methods for Jsp Actions
 * to use. Since this class is abstract, it should only be extended
 * and not used directly.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark ORciuch</a>
 * @version $Id: JspAction.java,v 1.3 2004/02/23 02:59:06 jford Exp $
 */
public abstract class JspAction extends ActionEvent
{

    /**
     * Sets up the context and then calls super.perform(); thus,
     * subclasses don't have to worry about getting a context
     * themselves!
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void perform(RunData data)
        throws Exception
    {
        super.perform(data);
    }

    /**
     * This method is used when you want to short circuit an Action
     * and change the template that will be executed next.
     *
     * @param data Turbine information.
     * @param template The template that will be executed next.
     */
    public void setTemplate(RunData data, String template)
    {
        TemplateScreen.setTemplate(data, template);
    }
}
