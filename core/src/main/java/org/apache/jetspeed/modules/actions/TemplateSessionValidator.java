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
package org.apache.jetspeed.modules.actions;

// Turbine Classes
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.util.RunData;
import org.apache.turbine.modules.actions.sessionvalidator.SessionValidator;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.JetspeedSecurity;

/**
 * SessionValidator for use with the Template Service, the
 * TemplateSessionValidator is virtually identical to the
 * TemplateSecureValidator except that it does not tranfer to the
 * login page when it detects a null user (or a user not logged in).
 *
 * <p>The Template Service requires a different Session Validator
 * because of the way it handles screens.
 *
 * @see TemplateSecureSessionValidator
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id: TemplateSessionValidator.java,v 1.3 2004/02/23 02:59:06 jford Exp $
 */
public class TemplateSessionValidator extends SessionValidator
{
    /**
     * Execute the action.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doPerform( RunData rundata ) throws Exception
    {
        JetspeedRunData data = (JetspeedRunData)rundata;
        /*
         * Pull user from session.
         */
        data.populate();

        // The user may have not logged in, so create a "guest" user.
        if ( data.getUser() == null)
        {
            data.setUser(JetspeedSecurity.getAnonymousUser());
            data.save();
        }

        // make sure we have some way to return a response
        if ( !data.hasScreen() &&
             data.getTemplateInfo().getScreenTemplate() == null )
        {
            String template = JetspeedResources.getString(
                TurbineConstants.TEMPLATE_HOMEPAGE);

            if (template != null)
            {
                data.getTemplateInfo().setScreenTemplate(template);
            }
            else
            {
                data.setScreen(JetspeedResources.getString(
                    TurbineConstants.SCREEN_HOMEPAGE));
            }
        }
        // the session_access_counter can be placed as a hidden field in
        // forms.  This can be used to prevent a user from using the
        // browsers back button and submitting stale data.
        else if ( data.getParameters().containsKey("_session_access_counter") )
        {
            // See comments in screens.error.InvalidState.
            if ( data.getParameters().getInt("_session_access_counter") <
                (((Integer)data.getUser().getTemp("_session_access_counter"))
                .intValue()-1) )
            {
                if (data.getTemplateInfo().getScreenTemplate() != null)
                {
                    data.getUser().setTemp( "prev_template",
                        data.getTemplateInfo().getScreenTemplate()
                        .replace('/', ',') );
                    data.getTemplateInfo().setScreenTemplate(
                        JetspeedResources.getString(
                        TurbineConstants.TEMPLATE_INVALID_STATE) );
                }
                else
                {
                    data.getUser().setTemp( "prev_screen",
                        data.getScreen().replace('/', ',') );
                    data.setScreen( JetspeedResources.getString(
                        TurbineConstants.SCREEN_INVALID_STATE) );
                }
                data.getUser().setTemp("prev_parameters", data.getParameters());
                data.setAction( "" );
            }
        }

        // we do not want to allow both a screen and template parameter.
        // The template parameter is dominant.
        if ( data.getTemplateInfo().getScreenTemplate() != null )
        {
            data.setScreen(null);
        }
    }

    /**
     * By default, this is true. It says that we require a new session
     * in order to allow people to access the system. We accomplish
     * this by doing a redirect and using the HttpSession spec.
     *
     * @param data Turbine information.
     * @return True if we require a new session in order to allow
     * people to access the system.
     */
    public boolean requiresNewSession(RunData data)
    {
        return true;
    }
}

