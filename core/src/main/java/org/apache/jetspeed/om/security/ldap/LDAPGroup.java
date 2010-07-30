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
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.security.GroupException;

/**
 *
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 *
 * @version $Id: LDAPGroup.java,v 1.6 2004/02/23 03:12:13 jford Exp $ 
 * 
 */
public class LDAPGroup extends BaseLDAPObject implements Group {

    // ---------------------------- Constants ----------------------------

    protected static final String OBJECT_CLASS          = "jetspeedgroup";
    protected static final String ORGANIZATIONAL_UNIT   = "ou=groups";

    protected static final String ATTR_GROUP_NAME       = "groupname";
    protected static final String ATTR_GROUP_ID         = "uid";

    // ------------------------- Member variables ------------------------

    protected String name                               = null;
    protected String id                                 = null;
    protected boolean isNew                             = true;

    // --------------------------- Constructors --------------------------

    public LDAPGroup()
    {
        isNew = true;
    }

    public LDAPGroup(String id)
    {
        this.setId(id);
        isNew = true;
    }

    public LDAPGroup(String name, boolean isNew)
    {
		name = super.createId(name);
        super.ldapurl = JetspeedLDAP.buildURL(ATTR_GROUP_ID + "=" + name + "," + ORGANIZATIONAL_UNIT);
        this.isNew = isNew;

        if (isNew)
        {
            this.setName(name);
            super.myAttrs = new BasicAttributes();
            super.myAttrs.put(ATTR_GROUP_ID, this.getId());
            super.myAttrs.put(ATTR_GROUP_NAME, this.getName());
            super.setObjectClass(OBJECT_CLASS);
        }
        else
        {
            super.myAttrs =  JetspeedLDAP.read(ldapurl);
            this.id = getutil(ATTR_GROUP_ID);
            this.name = getutil(ATTR_GROUP_NAME);
        }
    }

    public LDAPGroup(LDAPURL ldapurl)
    {
        super.ldapurl = ldapurl;
        super.myAttrs =  JetspeedLDAP.read(ldapurl);
        this.id = getutil(ATTR_GROUP_ID);
        this.name = getutil(ATTR_GROUP_NAME);
    }

    // --------------------- Persistence operations ----------------------

    public void update(boolean create)
	throws GroupException
    {
        removeutil("createTimeStamp", false);
        removeutil("modifyTimeStamp", false);       

        if (create)
        {
            if (JetspeedLDAP.addEntry(super.ldapurl, super.myAttrs) == false) throw new GroupException("Failed to insert group in LDAP!");
        }
        else if (JetspeedLDAP.exists(super.ldapurl))
        {
            JetspeedLDAP.deleteAttrs(super.ldapurl, super.rmAttrs);
            if (JetspeedLDAP.updateEntry(super.ldapurl, super.myAttrs) == false) throw new GroupException("Failed to update group in LDAP!");
        }
    }

    // ------------------------ Accessor methods -------------------------

    /**
     * Get the name of the Group
     *
     * @return the name of the group.
     */
    public String getName()
    {
        return name;
    }
 
    /**
     * Set the name of the Group
     *
     * @param groupName the name of the Group.
     */
    public void setName(String groupName)
    {
        setId(groupName);
        name = super.createId(groupName);
    }

    /**
     * Get the id of the Group
     *
     * @return the id of the group.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set the id of the Group
     *
     * @param id the new id for the group
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