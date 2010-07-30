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

package org.apache.jetspeed.services;

import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import org.apache.jetspeed.services.ldap.LDAPService;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.turbine.services.TurbineServices;

/**
 *
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 *
 * @version $Id: JetspeedLDAP.java,v 1.5 2004/02/23 04:00:57 jford Exp $ 
 * 
 */
public class JetspeedLDAP 
{
    public static LDAPService getService()
    {
        return(LDAPService)TurbineServices
        .getInstance()
        .getService(LDAPService.SERVICE_NAME);
    }

    public static boolean addAttribute(LDAPURL url, Attribute at)
    {
        return getService().addAttribute(url, at);
    }


    public static boolean addEntry(LDAPURL url, Attributes at)
    {
        return getService().addEntry(url, at);
    }

    public static int compare(LDAPURL srcUrl, LDAPURL dstUrl)
    {
        return getService().compare(srcUrl, dstUrl);
    }

    public static boolean deleteAttribute(LDAPURL url, Attribute at)
    {
        return getService().deleteAttribute(url, at);
    }

    public static boolean deleteEntry(LDAPURL url)
    {
        return getService().deleteEntry(url);
    }

    public static LDAPURL buildURL(String DN)
    {
        return getService().buildURL(DN);
    }

    public static boolean deleteTree(LDAPURL url)
    {
        return getService().deleteTree(url);
    }

    public static boolean exists(LDAPURL url)
    {
        return getService().exists(url);
    }

    public static LDAPURL findEntryName(LDAPURL url)
    {
        return getService().findEntryName(url);
    }

    public static String removeAttrName(String attrvalue)
    {
        return getService().removeAttrName(attrvalue);
    }

    public static Attributes read(LDAPURL url)
    {
        return getService().read(url);
    }

    public static boolean renameEntry(LDAPURL url, String newDN)
    {
        return getService().renameEntry(url, newDN);
    }

    public static NamingEnumeration search(DirContext ctx, String dn, String filter, String attribs[], int type)
    {
		try
		{
	        return getService().search(ctx, dn, filter, attribs, type);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
    }

    public static Vector search(LDAPURL url, String filter, String attribs[], boolean subTreeScope)
    {
        return getService().search(url, filter, attribs, subTreeScope);
    }

    public static boolean synchEntry(LDAPURL url, Attributes ats)
    {
        return getService().synchEntry(url, ats);
    }

    public static boolean deleteAttrs(LDAPURL url, Attributes ats)
    {
        return getService().deleteAttrs(url, ats);
    }

    public static boolean transfer(LDAPURL fromUrl, LDAPURL toUrl, boolean delete, boolean replace, boolean withChildren)
    {
        return getService().transfer(fromUrl, toUrl, delete, replace, withChildren);
    }

    public static boolean transferEntry(LDAPURL fromUrl, Attributes ats, LDAPURL toUrl, boolean delete, boolean replace)
    {
        return getService().transferEntry(fromUrl, ats, toUrl, delete, replace);
    }

    public static boolean transferEntry(LDAPURL fromUrl, LDAPURL toUrl, boolean delete, boolean replace)
    {
        return getService().transferEntry(fromUrl, toUrl, delete, replace);
    }

    public static boolean updateAttribute(LDAPURL url, Attribute at)
    {
        return getService().updateAttribute(url, at);
    }

    public static boolean updateEntry(LDAPURL url, Attributes at)
    {
        return getService().updateEntry(url, at);
    }

    public static boolean updateEntry(LDAPURL url, Attributes ats, boolean replace)
    {
        return getService().updateEntry(url, ats, replace);
    }

    public static String getName(String dn)
    {
        return getService().getName(dn);
    }
}
