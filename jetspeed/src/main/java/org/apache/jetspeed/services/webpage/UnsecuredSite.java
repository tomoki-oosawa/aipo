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

/* 
 * Holds the state of a proxied target. This simple implementation is unsecured.
 *
 */
public class UnsecuredSite
{    
    private long id;
    private String url;
    private String name;
    private int status;
 
    /*
     * Construct a site object given a unique id and ip address.
     *
     * @param sid the unique network element identifier.
     * @param ip the ip address of the network element.
     *
     */
    public UnsecuredSite(String name, 
                         String url)

    {
        this.id = WebPageHelper.generateId();
        this.name = name;
        this.url = url;        
    }

    /* 
     * Get the unique ID of the proxied target.
     *
     * return The unique ID of the target.
     */
    public long getID()
    {
        return this.id;
    }

    /* 
     * Get the URL of the proxied target.
     *
     * return The URL of the proxied target.
     */
    public String getURL()
    {
        return this.url;
    }
 
    
    /* 
     * get the common name for this proxied target.
     *
     * return the string value of the proxied target name.
     */
    public String getName()
    {
        return this.name;
    }

    /* 
     * get the user name used to logon to this proxied target.
     *
     * return the string value of the proxied target user name.
     */
    public String getUserName()
    {
        return null;
    }
        
    /* 
     * get the password used to logon to this proxied target.
     *
     * return the string value of the proxied target password.
     */
    public String getPassword()
    {
        return null;
    }
     
    /* 
     * get the availability status of this proxied target.
     *
     * return the int value of the proxied target availability status.
     */
    public int getStatus()
    {
        return this.status;
    }
    
    /* 
     * sets the status of this proxied target.
     *
     * @param the int value of the proxied target status.
     */
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    /* 
     * Is this proxied target secured.
     *
     * return True if the target is secured.
     */
    public boolean isSecured()
    {
        return false;
    }

}
