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

import java.util.Vector;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.security.RoleException;

/**
 *
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 *
 * @version $Id: LDAPRole.java,v 1.6 2004/02/23 03:12:13 jford Exp $ 
 * 
 */
public class LDAPRole extends BaseLDAPObject implements Role {

    // ---------------------------- Constants ----------------------------

    protected static final String OBJECT_CLASS          = "jetspeedrole";
    protected static final String ORGANIZATIONAL_UNIT   = "ou=roles";

    protected static final String ATTR_ROLE_PERMISSIONS = "rolepermissions";
    protected static final String ATTR_ROLE_NAME        = "rolename";
    protected static final String ATTR_ROLE_ID          = "uid";

    // ------------------------- Member variables ------------------------

    protected String name                               = null;
    protected String id                                 = null;
    protected Vector rolePermissions                    = null;
    protected boolean isNew                             = true;

    // --------------------------- Constructors --------------------------

    public LDAPRole()
    {
        rolePermissions = new Vector();
        isNew = true;
    }

    public LDAPRole(String id)
    {
        this.setId(id);
        isNew = true;
        rolePermissions = new Vector();
    }

    public LDAPRole(String name, boolean isNew)

    {
		name = super.createId(name);
        super.ldapurl = JetspeedLDAP.buildURL(ATTR_ROLE_ID + "=" + name + "," + ORGANIZATIONAL_UNIT);
        this.isNew = isNew;

        if (isNew)
        {
            rolePermissions = new Vector();
            this.setName(name);
            super.myAttrs = new BasicAttributes();
            super.myAttrs.put(ATTR_ROLE_ID, this.id);
            super.myAttrs.put(ATTR_ROLE_NAME, this.name);
			super.setObjectClass(OBJECT_CLASS);
        }
        else
        {
            super.myAttrs = JetspeedLDAP.read(super.ldapurl);
            this.id = getutil(ATTR_ROLE_ID);
            this.name = getutil(ATTR_ROLE_NAME);
            this.rolePermissions = getutil(ATTR_ROLE_PERMISSIONS, true);
        }
    }

    public LDAPRole(LDAPURL ldapurl)
    {
        super.ldapurl = ldapurl;
        super.myAttrs =  JetspeedLDAP.read(ldapurl);
        this.id = getutil(ATTR_ROLE_ID);
        this.name = getutil(ATTR_ROLE_NAME);
        this.rolePermissions = getutil(ATTR_ROLE_PERMISSIONS, true);
    }

    // --------------------- Persistence operations ----------------------

    public void update(boolean create)
	throws RoleException
    {
        removeutil("createTimeStamp", false);
        removeutil("modifyTimeStamp", false);       

		setutil(ATTR_ROLE_PERMISSIONS, rolePermissions, create);
		
        if (create)
        {
            if (JetspeedLDAP.addEntry(super.ldapurl, super.myAttrs) == false) throw new RoleException("Failed to insert role in LDAP!");
        }
        else if (JetspeedLDAP.exists(super.ldapurl))
        {
            JetspeedLDAP.deleteAttrs(super.ldapurl, super.rmAttrs);
			// These two method calls shouldn't be needed anymore.
			// If you face some problems with role permissions, 
			// you can remove the comments from below and try again. 
            //  removePreviousPermissionsFromLDAP();
            //  super.myAttrs.put(toAttribute(ATTR_ROLE_PERMISSIONS, rolePermissions));
            if (JetspeedLDAP.updateEntry(super.ldapurl, super.myAttrs) == false) throw new RoleException("Failed to update role in LDAP!");
        }
    }

    public void removePreviousPermissionsFromLDAP()
    {
        Vector previousPermissions = getutil(ATTR_ROLE_PERMISSIONS, true);
        BasicAttributes attrs = new BasicAttributes();
 
        for (int i=0; i < previousPermissions.size(); i++)
        {
            String uid = (String)previousPermissions.get(i);
            //Log.debug("Deleting value '" + uid + "' from rolepermissions.");
            attrs.put(ATTR_ROLE_PERMISSIONS, uid);
        }

        JetspeedLDAP.deleteAttrs(super.ldapurl, attrs);
    }

    public Attribute toAttribute(String id, Vector values)
    {
        Attribute attr = new BasicAttribute(id);
 
        for (int i=0; i < values.size(); i++)
        {
            String uid = (String)values.get(i);
            //Log.debug("Adding value '" + uid + "' to rolepermissions.");
            attr.add(uid);
        }

        return attr;
    }

    // ------------------------ Accessor methods -------------------------

    /**
     * Get the name of the Role
     *
     * @return the name of the role.
     */
    public String getName()
    {
        return name;
    }
 
    /**
     * Set the name of the Role
     *
     * @param roleName the name of the Role.
     */
    public void setName(String roleName)
    {
    	setId(roleName);
        name = super.createId(roleName);
    }

    /**
     * Get the id of the Role
     *
     * @return the id of the role.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set the id of the Role
     *
     * @param id the new id for the role
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

    public Vector getRolePermissions()
    {
        return rolePermissions;
    }

    public void addRolePermissions(String rolePermission)
    {
        if (!(permissionExists(rolePermission)))
        {
            rolePermissions.add(rolePermission);
        }
    }

    public void removeRolePermissions(String rolePermission)
    {
        rolePermissions.remove(rolePermission);
    }

    public boolean permissionExists(String rolePermission)
    {
        if (rolePermissions.indexOf(rolePermission) != -1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
            

}

