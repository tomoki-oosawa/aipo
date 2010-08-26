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

package org.apache.jetspeed.services.webpage;

/**
 * Holds the relevant state about a secured site.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SecuredSite.java,v 1.2 2004/02/23 03:46:26 jford Exp $ 
 */
public class SecuredSite implements Site
{    
    //
    // Site state
    //
    private long id;
    private String url;
    private String name;
    private int    status;
    private String username;
    private String password;

    /**
     * Construct site object given a url 
     *
     * @param name the name of the site.
     * @param url the url address of the site.
     *
     */
    public SecuredSite(String name, String url)
    {
        this.id = WebPageHelper.generateId();
        this.name = name;
        this.url = url;
    }

    /**
     * get the URL for this site
     *
     * return the string value of the URL
     */
    public String getURL()
    {
        return this.url;
    }

    /**
     * get the Site ID for this site
     *
     * return the string value of the Site ID
     */
    public long getID()
    {
        return this.id;
    }

    /** 
     * sets the URL address for this site.
     *
     * @param the URL address of the site.
     */
    public void setURL(String url)
    {
        this.url = url;
    }

    /** 
     * get the common name for this site.
     *
     * return the string value of the site name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * sets the common name for this site.
     *
     * @param the name of the site.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * get the user name used to logon to this site.
     *
     * return the string value of the site user name.
     */
    public String getUserName()
    {
        return username;
    }

    /**
     * sets the user name used to logon to this site.
     *
     * @param the string value of the site user name.
     */
    public void setUserName(String username)
    {
        this.username = username;
    }

    /**
     * get the password used to logon to this site.
     *
     * return the string value of the site password.
     */
    public String getPassword()
    {
        return password;
    }

    /** 
     * sets the password used to logon to this site.
     *
     * @param the string value of the site password.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

   /**
     * get the availability status of this site.
     *
     * return the int value of the site availability status.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * sets the availability status of this site.
     *
     * @param the int value of the site availability status.
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

   /* 
     * Is this site secured.
     *
     * return True if the target is secured.
     */
    public boolean isSecured()
    {
        return true;
    }
 
}
