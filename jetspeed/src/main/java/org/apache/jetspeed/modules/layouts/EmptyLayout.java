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
 
package org.apache.jetspeed.modules.layouts;

// Turbine Modules
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.util.RunData;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.HtmlColor;

/**
*/
public class EmptyLayout extends Layout
{
    /**
    */
    public void doBuild( RunData data ) throws Exception
    {

        // Now execute the Screen portion of the page
        ConcreteElement screen = ScreenLoader.getInstance().eval ( data, data.getScreen() );
        if (screen != null)
            data.getPage().getBody().addElement( screen );

        // The screen should have attempted to set a Title 
        // for itself, otherwise, a default title is set
        data.getPage().getTitle()
            .addElement( data.getTitle() );
        // The screen should have attempted to set a Body bgcolor 
        // for itself, otherwise, a default body bgcolor is set
        data.getPage().getBody()
            .setBgColor(HtmlColor.white);
        
    }

}
