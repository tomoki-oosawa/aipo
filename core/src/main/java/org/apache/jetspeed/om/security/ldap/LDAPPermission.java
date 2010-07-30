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

package org.apache.jetspeed.om.security.ldap;

import javax.naming.directory.BasicAttributes;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.security.PermissionException;

/**
 *
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 *
 * @version $Id: LDAPPermission.java,v 1.5 2004/02/23 03:12:13 jford Exp $ 
 * 
 */
public class LDAPPermission extends BaseLDAPObject implements Permission {

    // ---------------------------- Constants ----------------------------

    protected static final String OBJECT_CLASS          = "jetspeedpermission";
    protected static final String ORGANIZATIONAL_UNIT   = "ou=permissions";

    protected static final String ATTR_PERMISSION_NAME  = "permissionname";
    protected static final String ATTR_PERMISSION_ID    = "uid";

    // ------------------------- Member variables ------------------------

    protected String name                               = null;
    protected String id                                 = null;
    protected boolean isNew                             = true;

    // --------------------------- Constructors --------------------------

    public LDAPPermission()
    {
        isNew = true;
    }

    public LDAPPermission(String id)
    {
        this.setId(id);
        isNew = true;
    }

    public LDAPPermission(String name, boolean isNew)
    {
		name = super.createId(name);
        super.ldapurl = JetspeedLDAP.buildURL(ATTR_PERMISSION_ID + "=" + name + "," + ORGANIZATIONAL_UNIT);
        this.isNew = isNew;

        if (isNew)
        {
            this.setName(name);
            super.myAttrs = new BasicAttributes();
            myAttrs.put(ATTR_PERMISSION_ID, id);
            myAttrs.put(ATTR_PERMISSION_NAME, name);
            super.setObjectClass(OBJECT_CLASS);
        }
        else
        {
            super.myAttrs = JetspeedLDAP.read(ldapurl);
            this.id = super.getutil(ATTR_PERMISSION_ID);
            this.name = super.getutil(ATTR_PERMISSION_NAME);
        }
    }

    public LDAPPermission(LDAPURL ldapurl)
    {
        super.ldapurl = ldapurl;
        super.myAttrs = JetspeedLDAP.read(ldapurl);
        this.id = super.getutil(ATTR_PERMISSION_ID);
        this.name = super.getutil(ATTR_PERMISSION_NAME);
    }

    // --------------------- Persistence operations ----------------------

    public void update(boolean create)
	throws PermissionException
    {
        removeutil("createTimeStamp", false);
        removeutil("modifyTimeStamp", false);

        if (create)
        {
            if (JetspeedLDAP.addEntry(super.ldapurl, super.myAttrs) == false) throw new PermissionException("Could not insert permission in LDAP!");
        }
        else if (JetspeedLDAP.exists(super.ldapurl))
        {
            JetspeedLDAP.deleteAttrs(super.ldapurl, super.rmAttrs);
            if (JetspeedLDAP.updateEntry(super.ldapurl, super.myAttrs) == false) throw new PermissionException("Could not update permission in LDAP!");
        }
    }

    // ------------------------ Accessor methods -------------------------

    /**
     * Get the name of the Permission
     *
     * @return the name of the permission.
     */
    public String getName()
    {
        return name;
    }
 
    /**
     * Set the name of the Permission
     *
     * @param permissionName the name of the Permission.
     */
    public void setName(String permissionName)
    {
    	setId(permissionName);
        name = super.createId(permissionName);
    }

    /**
     * Get the id of the Permission
     *
     * @return the id of the permission.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set the id of the Permission
     *
     * @param id the new id for the permission
     */
    public void setId(String id)
    {      
        if (this.id == null)
        {
            this.id = super.createId(id);
        }
    }

    public boolean isNew()
    {
        return isNew;
    }

    void setNew(boolean isNew)
    {
        this.isNew = isNew;
    }

}