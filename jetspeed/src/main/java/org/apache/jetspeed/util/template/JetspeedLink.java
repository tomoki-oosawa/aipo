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

// Jetspeed
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;

// Turbine
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 * <p>A customized version of the TemplateLink which can handle portlet
 * references.</p>
 *
 * <p>It is inserted into the template context by Turbine, via request tools.</p>
 *
 * <p>Each portlet must call setPortlet(this) on it before entering the template
 * rendering code. This is done currently in VelocityPortlet.</p>
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: JetspeedLink.java,v 1.15 2004/03/11 03:08:53 paulsp Exp $
 */
public interface JetspeedLink {
    
    public static int CURRENT = 101;
    public static int DEFAULT = 102;
    public static int GROUP = 201;
    public static int ROLE = 202;
    public static int USER = 203;
    public static int PANE_ID = 301;
    public static int PANE_NAME = 302;
    public static int PORTLET_ID = 303;
    public static int PORTLET_NAME = 304;
    public static int PORTLET_ID_QUERY = 305;
    
    
    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value An Object with the value to add.
     * @return DynamicURI that to the desired page
     */
    public DynamicURI addPathInfo(String name, Object value);
    
    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value An Object with the value to add.
     * @return DynamicURI that to the desired page
     */
    public DynamicURI addQueryData(String name, Object value);
    
    /**
     * Return the action key.  Used by velocity templates, i.e. $jlink.ActionKey
     *
     * @return the action parameter name
     */
    public String getActionKey();
    
    /**
     * Return link to the home page without user,
     * page, group, role, template, action, media type, language, or country
     * in link.
     * @return DynamicURI to the home page
     */
    public DynamicURI getHomePage();
    
    /**
     * This will initialise a JetspeedLink object that was
     * constructed with the default constructor
     *
     * @param rundata to be a RunData object
     */
    public void init(RunData rundata);
    
    /**
     * Return a link that includes the template
     * from rundata
     *
     * @return DynamicURI to template
     */
    public DynamicURI getTemplate();
    
    /**
     * Is the PSML for the anonymous user?
     *
     * @return True = PSML is for the anonymous user
     */
    public boolean getAnonymous();
    
    /**
     * Return country of the PSML file
     *
     * @return Country of PSML, or null if no country
     */
    public String getCountry();
    
    /**
     * Return Group name of the PSML file
     *
     * @return Group name of PSML, or null if no Group name
     */
    public String getGroupName();
    
    /**
     * Return Language of the PSML file
     *
     * @return Language of PSML, or null if no Language
     */
    public String getLanguage();
    
    /**
     * Return Media Type of the PSML file
     *
     * @return Media Type of PSML, or null if no Media Type
     */
    public String getMediaType();
    
    /**
     * Return Page name of the PSML file
     *
     * @return Page name of PSML, or null if no Page name
     */
    public String getPageName();
    
    /**
     * Return Role name of the PSML file
     *
     * @return Role name of PSML, or null if no Role name
     */
    public String getRoleName();
    
    /**
     * Return User name of the PSML file
     *
     * @return User name of PSML, or null if no User name
     */
    public String getUserName();
    
    /**
     * Return a link that includes an action
     *
     * @param action action
     * @return DynamicURI that includes the desire action
     */
    public DynamicURI getAction(String action);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by an entry
     *
     * @param action Desired action
     * @param entry to receive the action
     * @return DynamicURI that includes the desire action
     */
    public DynamicURI getAction(String action, Entry entry);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by a portlet
     *
     * @param action Desired action
     * @param portlet to receive the action
     * @return DynamicURI that includes the desire action
     */
    public DynamicURI getAction(String action, Portlet portlet);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by a portlets
     *
     * @param action Desired action
     * @param portlet to receive the action
     * @return DynamicURI that includes the desire action
     */
    public DynamicURI getAction(String action, Portlets portlet);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by a PEID
     *
     * @param action Desired action
     * @param peid Id of portlet to receive the action
     * @return DynamicURI that includes the desire action
     */
    public DynamicURI getAction(String action, String peid);

    /**
     * Return a link to a default page for the group
     *
     * @param group Desired group
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getGroup(String group);
    
    /**
     * Return a link to a desired page for the group
     *
     * @param page Desired page
     * @param group Desired group
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getGroup(String group, String page);
    
    /**
     * Return a link to a default page for the
     * current user, group, or role.
     *
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getPage();
    
    /**
     * Return a link to a desired page for the
     * current user, group, or role.
     *
     * @param page Desired page
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getPage(String page);
    
    /**
     * Return a link to a desired page and pane for the
     * current user, group, or role.
     *
     * @param page Desired page
     * @param paneName Name of desired pane
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getPage(String page, String paneName);
    
    /**
     * Return an link to a specific portlet using the portet's id
     *
     * @param peid of the portlet
     * @return DynamicURI to specific portlet
     *
     */
    public DynamicURI getPortletById(String peid);
    
    /**
     * Add a portlet reference in the link.
     *
     * Note: This must be used with caution, since a portlet may exist may times
     * in a PSML.  setPortletById() is the perfered method.
     *
     * @param portletName the name of the portlet to link to
     * @return a DynamicURI referencing the named portlet for easy link construction in template
     *
     * @deprecated Use getPortletById()     
     */
    public DynamicURI getPortletByName(String portletName);
    
    /**
     * Return a link to a default page for the role
     *
     * @param role Desired role
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getRole(String role);
    
    /**
     * Return a link to a desired page for the role
     *
     * @param role Desired role
     * @param page Desired page
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getRole(String role, String page);
    
    /**
     * Return a link to the template.
     *
     * @param template to add to link
     * @return DynamicURI to specific portlet
     */
    public DynamicURI getTemplate(String template);
    
    /**
     * Return a link to a default page for the user
     *
     * @param user Desired user
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getUser(String user);
    
    /**
     * Return a link to a desired page for the user
     *
     * @param page Desired page
     * @param user Desired user
     * @return DynamicURI that to the desired page
     */
    public DynamicURI getUser(String user, String page);

    /**
     * Return a link that includes an action
     *
     * @param action action
     * @return DynamicURI that includes the desire action
     * @deprecated Use getAction()
     */
    public DynamicURI setAction(String action);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by an entry
     *
     * @param action Desired action
     * @param entry to receive the action
     * @return DynamicURI that includes the desire action
     *
     * @deprecated use getAction()
     */
    public DynamicURI setAction(String action, Entry entry);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by a portlet
     *
     * @param action Desired action
     * @param portlet to receive the action
     * @return DynamicURI that includes the desire action
     *
     * @deprecated use getAction()
     */
    public DynamicURI setAction(String action, Portlet portlet);
    
    /**
     * Return a link that includes an action to a specific portlet, as defined
     * by a portlets
     *
     * @param action Desired action
     * @param portlet to receive the action
     * @return DynamicURI that includes the desire action
     *
     * @deprecated use getAction()
     */
    public DynamicURI setAction(String action, Portlets portlet);
    
    /**
     * Return a link to a default page for the group
     *
     * @param group Desired group
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getGroup()
     */
    public DynamicURI setGroup(String group);
    
    /**
     * Return a link to a desired page for the group
     *
     * @param page Desired page
     * @param group Desired group
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getGroup()
     */
    public DynamicURI setGroup(String group, String page);
    
    /**
     * Return a link to a default page for the
     * current user, group, or role.
     *
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getPage()
     */
    public DynamicURI setPage();
    
    /**
     * Return a link to a desired page for the
     * current user, group, or role.
     *
     * @param page Desired page
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getPage()
     */
    public DynamicURI setPage(String page);
    
    /**
     * Return a link to a desired page and pane for the
     * current user, group, or role.
     *
     * @param page Desired page
     * @param paneName Name of desired pane
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getPage()
     */
    public DynamicURI setPage(String page, String paneName);
    
    public DynamicURI setMediaType(String mediaType);

    /**
     * Return an link to a specific portlet using the portet's id
     *
     * @param peid of the portlet
     * @return DynamicURI to specific portlet
     *
     * @deprecated use getPortletById()
     */
    public DynamicURI setPortletById(String peid);
    
    /**
     * Return an link to a specific portal element
     *
     * @param peid of the portal element
     * @return DynamicURI to specific portal element
     *
     * @deprecated use getPortletById or getPaneById()
     */
    public DynamicURI setPortalElement(String peid);
    
    /**
     * Add a portlet reference in the link.
     *
     * Note: This must be used with caution, since a portlet may exist may times
     * in a PSML.  setPortletById() is the perfered method.
     *
     * @param portletName the name of the portlet to link to
     * @return a DynamicURI referencing the named portlet for easy link construction in template
     *
     * @deprecated use getPortletByName()
     */
    public DynamicURI setPortletByName(String portletName);
    
    /**
     * Return a link to a default page for the role
     *
     * @param role Desired role
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getRole()
     */
    public DynamicURI setRole(String role);
    
    /**
     * Return a link to a desired page for the role
     *
     * @param role Desired role
     * @param page Desired page
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getRole()
     */
    public DynamicURI setRole(String role, String page);
    
    /**
     * Return a link to the template.
     *
     * @param template to add to link
     * @return DynamicURI to specific portlet
     *
     * @deprecated use getTemplate()
     */
    public DynamicURI setTemplate(String template);
    
    /**
     * Return a link to a default page for the user
     *
     * @param user Desired user
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getUser()
     */
    public DynamicURI setUser(String user);
    
    /**
     * Return a link to a desired page for the user
     *
     * @param page Desired page
     * @param user Desired user
     * @return DynamicURI that to the desired page
     *
     * @deprecated use getUser()
     */
    public DynamicURI setUser(String user, String page);
    
    /**
     * Return a URL, as a string, the the root page or pane.
     *
     * @return a URL, as a string, the the root page or pane.
     */
    public String toString();
    
    /**
     * Return a link to a specific pane using the pane's id
     *
     * @param paneId of the Pane
     * @return URI to specific portlet
     * @deprecated Use getPaneById()
     */
    public DynamicURI setPaneById(String paneId);
            
    /**
     * Return a link to a specific pane using the pane's id
     *
     * @param paneId of the Pane
     * @return URI to specific portlet
     */
    public DynamicURI getPaneById(String paneId);
    
    /**
     * Return a link to a specific pane using the pane's id
     *
     * @param paneName Name of the Pane
     * @return URI to specific portlet
     */
    public DynamicURI getPaneByName(String paneName);

    /**
     * depreceted methods from JetspeedTemplateLink.
     */
    
    /**
     * <p> Set the portlet giving context to this Link object.</p>
     *
     * This method is from JetspeedTemplateLink and is only here
     * for backward compatibility. This it should not be used for
     * any new development.  Also any problems with this method will
     * not be fixed
     *
     * @param portlet the name of the active portlet
     * @deprecated Use getLink() or getAction() or getPortletById() or getPortletByName().
     */
    public void setPortlet(Portlet portlet);
    
    /**
     * Return a link to a desired page.  This is allows the inclusion of a Group/Role/User,
     * page, template, action, media type, language, and country.
     *
     * @param rootType Type of root PSML docuument.  The should be one of the following:
     *                 <dl>
     *                   <dt>JetspeedLink.CURRENT</dt><dd>The link will retain the current Group/Role/User referance. rootValue is not used</dd>
     *                   <dt>JetspeedLink.DEFAULT</dt><dd>Default Group, Role, or User. rootValue is not used</dd>
     *                   <dt>JetspeedLink.GROUP</dt><dd>Link will be to a Group PSML.  rootValue is a Group Name</dd>
     *                   <dt>JetspeedLink.ROLE</dt><dd>Link will be to a Role PSML.  rootValue is a Role Name</dd>
     *                   <dt>JetspeedLink.USER</dt><dd>Link will be to a User PSML.  rootValue is a User Name</dd>
     *                 </dl>
     * @param rootValue  See description of rootType
     * @param pageName  Name of page. null = default page
     * @param elementType
     *                 <dl>
     *                   <dt>JetspeedLink.CURRENT</dt><dd>The link will retain the current Pane/Portlet referance. elementValue is not used</dd>
     *                   <dt>JetspeedLink.DEFAULT</dt><dd>The link will NOT referance a pane or portlet. elementValue is not used</dd>
     *                   <dt>JetspeedLink.PANE_ID</dt><dd>Link will be to a Pane using it's ID.  elementValue is a Pane's ID</dd>
     *                   <dt>JetspeedLink.PANE_NAME</dt><dd>Link will be to a Pane using it's Name.  elementValue is a Pane's Name</dd>
     *                   <dt>JetspeedLink.PORTLET_ID</dt><dd>Link will be to a Portlet using it's ID.  elementValue is a Portlet's ID</dd>
     *                   <dt>JetspeedLink.PORTLET_NAME</dt><dd>Link will be to a Portlet using it's Name.  elementValue is a Portlet's Name</dd>
     *                 </dl>
     * @param elementValue  See description of elementType
     * @param actionName Name of action. If no action is desired use JetspeedLink.NO_ACTION.
     * @param templateName Name of template. If no template is desired use JetspeedLink.NO_TEMPLATE.
     * @param mediaType Desired media type. null = default media type
     * @param language Desired language. null = default language
     * @param country Desired country.  null = default language
     * @return  URI to specific portlet
     */    
    public DynamicURI getLink(int rootType, String rootValue, String pageName, int elementType, String elementValue, String actionName, String templateName, String mediaType, String language, String country);
    /**
     * @see # org.apache.jetspeed.util.template.JetspeedLink.getLink( int, String, String, int, String, String, String, String, String, String
     */    
    public DynamicURI getLink(int rootType, String rootValue, String pageName, int elementType, String elementValue, String actionName, String templateName, String mediaType, String language);
    public DynamicURI getLink(int rootType, String rootValue, String pageName, int elementType, String elementValue, String actionName, String templateName, String mediaType);
    public DynamicURI getLink(int rootType, String rootValue, String pageName, int elementType, String elementValue, String actionName, String templateName);
    public DynamicURI getLink(int rootType, String rootValue, String pageName, int elementType, String elementValue, String actionName);
    public DynamicURI getLink(int rootType, String rootValue, String pageName, int elementType, String elementValue);
    
}
