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

package org.apache.jetspeed.util.template;

import org.apache.turbine.util.template.*;
import org.apache.turbine.util.RunData;
import org.apache.jetspeed.services.TemplateLocator;
/**
 * A Class that produce localized navigations.
 *
 *
 * @author Ignacio J. Ortega
 */
public class JetspeedTemplateNavigation extends TemplateNavigation {


    private RunData data;
/*XXX add no args constructor to turbine's TemplateNavigation
    public JetspeedTemplateNavigation(){
        data=null;
    }
*/    
    public JetspeedTemplateNavigation(RunData data) {
        super(data);
        this.data=data;
    }
    /**
     * This will initialise a JetspeedTemplateNavigation object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data){
        this.data = (RunData)data;
    }


    public TemplateNavigation setTemplate(String template) {
        return super.setTemplate(TemplateLocator.locateNavigationTemplate(data,template));
    }
}

