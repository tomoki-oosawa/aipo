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

package org.apache.jetspeed.modules.actions.portlets;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.SerializationUtils;
import org.apache.jetspeed.modules.actions.portlets.security.SecurityConstants;
import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.registry.MediaTypeRegistry;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.base.BaseParameter;
import org.apache.jetspeed.om.registry.base.BaseSecurity;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.velocity.context.Context;

/**
 * This action enables the creation and editing of portlets
 *
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: PortletUpdateAction.java,v 1.8 2004/02/23 02:56:58 jford Exp $
 */
public class PortletUpdateAction extends RegistryUpdateAction
{
    private static final String PORTLET_UPDATE_PANE = "portlet-form";

    private static final String PORTLET_NAME = "portlet_name";
    private static final String TAB_PARAMETER = "tab";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletUpdateAction.class.getName());     
    
    public PortletUpdateAction()
    {
        registryEntryName = PORTLET_NAME;
        registry = Registry.PORTLET;
        pane = PORTLET_UPDATE_PANE;
    }

    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext(
        VelocityPortlet portlet,
        Context context,
        RunData rundata)
        throws Exception
    {
        super.buildNormalContext(portlet, context, rundata);
        
        String mode =
            rundata.getParameters().getString(SecurityConstants.PARAM_MODE);

        if (mode != null && mode.equals(SecurityConstants.PARAM_MODE_INSERT))
        {
            context.put("parents", PortletFilter.buildParentList(PortletFilter.getAllPortlets()));
            context.put(
                "securitys",
                CustomizeSetAction.buildList(rundata, Registry.SECURITY));
        }

        if (mode != null && mode.equals(SecurityConstants.PARAM_MODE_UPDATE))
        {
            String portletName =
                rundata.getParameters().getString(PORTLET_NAME);
            PortletEntry portletEntry =
                (PortletEntry) Registry.getEntry(Registry.PORTLET, portletName);
                
            context.put("groups", iteratorToCollection(JetspeedSecurity.getGroups()));
            context.put("categories", PortletFilter.buildCategoryList(PortletFilter.getAllPortlets()));

            String tab = rundata.getParameters().getString(TAB_PARAMETER);
            context.put("currentTab", tab);

            context.put(
                "securitys",
                CustomizeSetAction.buildList(rundata, Registry.SECURITY));

            context.put("entry", portletEntry);
            
            MediaTypeRegistry mediaTypeReg = (MediaTypeRegistry)Registry.get(Registry.MEDIA_TYPE);
            context.put("media_types", iteratorToCollection(mediaTypeReg.listEntryNames()));
            
            if(portletEntry.getType() != null && portletEntry.getType().equals("ref"))
            {
                PortletEntry parentEntry = (PortletEntry) Registry.getEntry(Registry.PORTLET, portletEntry.getParent());
                if(parentEntry == null)
                {
                    logger.error(this.getClass().getName() + ": Portlet " + portletName + " of type ref has no parent.  This portlet will not work properly.");
                }
                else
                {
                    Collection parentCategories = iteratorToCollection(parentEntry.listCategories());
                    context.put("parent_categories", parentCategories);
                    
                    Collection parentMediaTypes = iteratorToCollection(parentEntry.listMediaTypes());
                    context.put("parent_mediatypes", parentMediaTypes);
                }
            }
        }

        if (mode != null && mode.equals(SecurityConstants.PARAM_MODE_DELETE))
        {
            String portletName =
                rundata.getParameters().getString(PORTLET_NAME);
            PortletEntry portletEntry =
                (PortletEntry) Registry.getEntry(registry, portletName);

            context.put("entry", portletEntry);
        }
    }
    
    public void doInsert(RunData rundata, Context context) throws Exception
    {
        super.doInsert(rundata, context);
        
        String entryName =
               rundata.getParameters().getString(registryEntryName);
        
        PortletEntry portletEntry =
                        (PortletEntry) Registry.getEntry(registry, entryName);
        if (portletEntry == null)
        {
            String message = "Portlet entry " + entryName + " does not exist.  The portlet was not added to the registry.";
            logger.error(this.getClass().getName() + ": " + message);
            
            throw new IllegalStateException(message);
        }
        else
        {
            if(portletEntry.getType().equals("ref")) {
                PortletEntry parentEntry = (PortletEntry)Registry.getEntry(Registry.PORTLET, portletEntry.getParent());
                
                if(parentEntry == null)
                {
                    logger.error(this.getClass().getName() + ": Portlet " + entryName + " of type ref has no parent.  This portlet will not work properly.");
                }
                else
                {
                    //When we create a portlet entry initially, we need to copy
                    //the parameters from the parent to the child so that
                    //a user does not end up editing his parents parameters
                    Iterator paramIter = portletEntry.getParameterNames();
                    while(paramIter.hasNext())
                    {
                        String paramName = (String)paramIter.next();
                        BaseParameter param = (BaseParameter) portletEntry.getParameter(paramName);
                        BaseParameter clonedParameter = (BaseParameter)SerializationUtils.clone(param);
                        portletEntry.addParameter(clonedParameter);
                    }
    
                    Registry.addEntry(Registry.PORTLET, portletEntry);
                }
            }
        }
    }
    
    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#updateRegistryEntry(org.apache.turbine.util.RunData, org.apache.jetspeed.om.registry.RegistryEntry)
     */
    protected void updateRegistryEntry(RunData rundata, RegistryEntry registryEntry) throws Exception
    {
        super.updateRegistryEntry(rundata, registryEntry);
        setPortletEntryInfo(rundata, (PortletEntry)registryEntry);
    }

    /**
     * Sets the portlet entry's fields
     * @param rundata
     * @param portletEntry
     */
    private void setPortletEntryInfo(
        RunData rundata,
        PortletEntry portletEntry)
    {
        String parent = rundata.getParameters().getString("parent");
        String title = rundata.getParameters().getString("title");
        String description = rundata.getParameters().getString("description");
        String url = rundata.getParameters().getString("url");
        String type = rundata.getParameters().getString("portlet_type");

        String mediaType = rundata.getParameters().getString("media_type");

        //meta info
        String image = rundata.getParameters().getString("image");

        String className = rundata.getParameters().getString("class_name");

        boolean isApplication =
            rundata.getParameters().getBoolean("is_application", false);
        boolean isCachedOnURL =
            rundata.getParameters().getBoolean("is_cached_on_url", false);
        boolean isHidden =
            rundata.getParameters().getBoolean("is_hidden", false);
        boolean isAdmin = rundata.getParameters().getBoolean("is_admin", false);

        String newSecurityParent =
            rundata.getParameters().getString("security_ref");

        String newSecurityRole =
            rundata.getParameters().getString("security_role");

        portletEntry.setTitle(title);
        portletEntry.setDescription(description);
        portletEntry.setURL(url);
        portletEntry.setParent(parent);
        portletEntry.setType(type);
        portletEntry.getMetaInfo().setImage(image);

        //need to build media index before add media
        portletEntry.listMediaTypes();
        portletEntry.addMediaType(mediaType);

        if (className != null && className.length() > 0)
        {
            portletEntry.setClassname(className);
        }

        portletEntry.setApplication(isApplication);
        portletEntry.setCachedOnURL(isCachedOnURL);
        portletEntry.setHidden(isHidden);
        //portletEntry.

        if (newSecurityParent != null && newSecurityParent.length() > 0)
        {
            SecurityReference securityRef = new BaseSecurityReference();
            securityRef.setParent(newSecurityParent);
            portletEntry.setSecurityRef(securityRef);
        }

        if (newSecurityRole != null && newSecurityRole.length() > 0)
        {
            BaseSecurity securityRole = new BaseSecurity();
            securityRole.setRole(newSecurityRole);
            portletEntry.setSecurity(securityRole);
        }
    }

    /**
     * Add a category to a portlet
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doAddcategory(RunData rundata, Context context)
        throws Exception
    {
        try
        {
            String portletName =
                rundata.getParameters().getString(PORTLET_NAME);
            PortletEntry portletEntry =
                (PortletEntry) Registry.getEntry(Registry.PORTLET, portletName);
            if (portletEntry != null)
            {
                String categoryName =
                    rundata.getParameters().getString("category_name");
                if (categoryName != null && categoryName.length() > 0)
                {
                    String categoryGroup =
                        rundata.getParameters().getString(
                            "category_group",
                            "Jetspeed");
                    portletEntry.addCategory(categoryName, categoryGroup);

                    Registry.addEntry(registry, portletEntry);

                    clearUserData(rundata);
                }
                else
                {
                    DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                    rundata.setRedirectURI(duri.toString());

                    resetForm(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);
                
                logger.error("Failed to find registry entry while trying to add category");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);
            
            logger.error("Exception", e);
        }
    }

    /**
     * Remove categories from a portlet
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doRemovecategories(RunData rundata, Context context)
        throws Exception
    {
        try
        {
            String portletName =
                rundata.getParameters().getString(PORTLET_NAME);
            PortletEntry portletEntry =
                (PortletEntry) Registry.getEntry(Registry.PORTLET, portletName);
            if (portletEntry != null)
            {
                String[] categories =
                    rundata.getParameters().getStrings("category_name");
                if (categories != null && categories.length > 0)
                {
                    for (int i = 0; i < categories.length; i++)
                    {
                        String categoryName = categories[i];
                        String categoryGroup =
                            rundata.getParameters().getString(
                                categoryName + ".category_group",
                                "Jetspeed");
                        portletEntry.removeCategory(
                            categoryName,
                            categoryGroup);
                    }

                    Registry.addEntry(registry, portletEntry);
                    clearUserData(rundata);
                }
                else
                {
                    DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                    duri = duri.addQueryData(PORTLET_NAME, portletName);
                    rundata.setRedirectURI(duri.toString());

                    resetForm(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);
                
                logger.error("Failed to find registry entry while trying to remove categories");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);
            
            logger.error("Exception", e);
        }
    }

    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#resetForm(org.apache.turbine.util.RunData)
     */
    /**
     * Populates the user's temp storage with form data
     * @param rundata The turbine rundata context for this request.
     */
    protected void resetForm(RunData rundata)
    {
        super.resetForm(rundata);
        
        String parent = rundata.getParameters().getString("parent");
        String title = rundata.getParameters().getString("title");
        String description = rundata.getParameters().getString("description");
        String url = rundata.getParameters().getString("url");
        String type = rundata.getParameters().getString("portlet_type");

        //meta info
        String image = rundata.getParameters().getString("image");

        String className = rundata.getParameters().getString("class_name");

        String isApplication =
            rundata.getParameters().getString("is_application");
        String isCachedOnURL =
            rundata.getParameters().getString("is_cached_on_url");
        String isHidden = rundata.getParameters().getString("is_hidden");
        String isAdmin = rundata.getParameters().getString("is_admin");

        String newSecurityParent =
            rundata.getParameters().getString("security_ref");

        //String newSecurity = rundata.getParameters().getString("security_role");
        
        rundata.getUser().setTemp("parent", parent);
        rundata.getUser().setTemp("portlet_type", type);
        rundata.getUser().setTemp("class_name", className);
        rundata.getUser().setTemp("url", url);
        rundata.getUser().setTemp("image", image);
        rundata.getUser().setTemp("is_application", isApplication);
        rundata.getUser().setTemp("is_cached_on_url", isCachedOnURL);
        rundata.getUser().setTemp("is_hidden", isHidden);
        rundata.getUser().setTemp("is_admin", isAdmin);
        rundata.getUser().setTemp("security_ref", newSecurityParent);
        //rundata.getUser().setTemp("security_role", newSecurity);
    }

    
    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#clearUserData(org.apache.turbine.util.RunData)
     */
    protected void clearUserData(RunData rundata)
    {
        try
        {
            super.clearUserData(rundata);

            rundata.getUser().removeTemp("parameter_name");
            rundata.getUser().removeTemp("paramter_value");
            rundata.getUser().removeTemp("parent");
            rundata.getUser().removeTemp("portlet_type");
            rundata.getUser().removeTemp("class_name");
            rundata.getUser().removeTemp("url");
            rundata.getUser().removeTemp("image");
            rundata.getUser().removeTemp("is_application");
            rundata.getUser().removeTemp("is_cached_on_url");
            rundata.getUser().removeTemp("is_hidden");
            rundata.getUser().removeTemp("is_admin");
            rundata.getUser().removeTemp("security_ref");
            //rundata.getUser().removeTemp("security_role");
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("PortletUpdateAction: Failed to clear user data");
            }
        }
    }
    
    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#redirect(org.apache.turbine.util.RunData, java.lang.String, int)
     */
    protected DynamicURI redirect(RunData rundata, String mode, int reason)
            throws TurbineException
    {
        DynamicURI duri = super.redirect(rundata, mode, reason);
        
        String tab = rundata.getParameters().getString(TAB_PARAMETER);
        if(tab != null && tab.length() > 0)
        {
            duri.addQueryData(TAB_PARAMETER, tab);
        }

        return duri;
    }
}
