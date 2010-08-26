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

package org.apache.jetspeed.modules.actions.portlets.security;


/**
 * This class contains all the common constants used between data entry forms and browsers and actions
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SecurityConstants.java,v 1.6 2004/02/23 02:53:08 jford Exp $
 */
public class SecurityConstants
{
    // msg id - for error or information messages on data entry forms
    public static final String PARAM_MSGID = "msgid";
    // msg - the informational or form message in a form
    public static final String PARAM_MSG = "msg";
    // unique entity id - parameter passed between browser forms and update forms
    public static final String PARAM_ENTITY_ID = "entityid";
    // username parameter
    public static final String PARAM_USERNAME = "username";

    // mode parameter
    public static final String PARAM_MODE = "mode";
    // update mode parameter
    public static final String PARAM_MODE_UPDATE = "update";
    // delete mode parameter
    public static final String PARAM_MODE_DELETE = "delete";
    // insert mode parameter
    public static final String PARAM_MODE_INSERT = "insert";

    //
    // Context Constants
    //
    public static final String CONTEXT_USER = "user";
    public static final String CONTEXT_USERS = "users";
    public static final String CONTEXT_ROLE = "role";
    public static final String CONTEXT_ROLES = "roles";
    public static final String CONTEXT_PERMISSION = "permission";
    public static final String CONTEXT_PERMISSIONS = "permissions";
    public static final String CONTEXT_GROUP = "group";
    public static final String CONTEXT_GROUPS = "groups";
    public static final String CONTEXT_SELECTED = "selected";
    public static final String CONTEXT_ROLES_SELECTED = "roles_selected";
    public static final String CONTEXT_GROUPS_SELECTED = "groups_selected";
    public static final String CONTEXT_GROUPS_ROLES = "userGroupRoles";

    // user browser pane id
    public static final String PANEID_USER_BROWSER = "UserBrowser";
    // user form pane id
    public static final String PANEID_USER_UPDATE = "UserForm";
    // role browser pane id
    public static final String PANEID_ROLE_BROWSER = "RoleBrowser";
    // role form pane id
    public static final String PANEID_ROLE_UPDATE = "RoleForm";
    // permission browser pane id
    public static final String PANEID_PERMISSION_BROWSER = "PermissionBrowser";
    // permission form pane id
    public static final String PANEID_PERMISSION_UPDATE = "PermissionForm";
    // group browser pane id
    public static final String PANEID_GROUP_BROWSER = "GroupBrowser";
    // group form pane id
    public static final String PANEID_GROUP_UPDATE = "GroupForm";
    // user role form pane id
    public static final String PANEID_USERROLE_UPDATE = "UserRoleForm";
    // role permission form pane id
    public static final String PANEID_ROLEPERMISSION_UPDATE = "PermissionBrowser";

    public static final String PANE_NAME = "js_panename";

    //
    // Informational and Error Messages for Security Forms
    ///
    public static final String MESSAGES[] =
    {
        "Database Update Failure. Please report this error to your Database Administrator.",
        "Database Delete Failure. Please report this error to your Database Administrator.",
        "Invalid Entity Name. Please enter a valid entity name.",
        "Entity Name Already Exists. Please choose another unique, identifying name.",
        "Deletion not allowed. You are trying to delete the currently logged on user.",
        "Missing Parameter. Cannot process Security form."
    };
    //
    // indexes into messages
    //
    public static final int MID_UPDATE_FAILED = 0;
    public static final int MID_DELETE_FAILED = 1;
    public static final int MID_INVALID_ENTITY_NAME = 2;
    public static final int MID_ENTITY_ALREADY_EXISTS = 3;
    public static final int MID_CANT_DELETE_CURRENT = 4;
    public static final int MID_MISSING_PARAMETER = 5;

};
