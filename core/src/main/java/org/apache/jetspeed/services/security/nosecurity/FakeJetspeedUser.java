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

// package
package org.apache.jetspeed.services.security.nosecurity;

// imports
import javax.servlet.http.HttpSessionBindingEvent;
import org.apache.jetspeed.om.security.BaseJetspeedUser;

/**
 * <p> A fake jetspeed user - constructed as needed.</p>
 * 
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: FakeJetspeedUser.java,v 1.2 2004/02/23 03:53:24 jford Exp $
 */
                                                             
public class FakeJetspeedUser
    extends BaseJetspeedUser
{
    public FakeJetspeedUser(String id, boolean loggedIn)
    {
        setUserId(id);
        setUserName(id);
        setHasLoggedIn(new Boolean(loggedIn));
        setConfirmed(CONFIRM_DATA);
        setFirstName("");
        setLastName(id);
    }

    public void valueUnbound(HttpSessionBindingEvent hsbe) {}
    public void save() {}

}   // class FakeJetspeedUser

