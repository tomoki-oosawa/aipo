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

package org.apache.jetspeed.services.urlmanager;

import org.apache.turbine.services.Service;
import java.util.List;

/**
 * <p>This service provides a central repository for storing URL
 * informations</p>
 * <strong>It should be extended to also provide access to their contents</strong>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: URLManagerService.java,v 1.6 2004/02/23 03:30:47 jford Exp $
 */
public interface URLManagerService extends Service {

    /**
     * The service name
     */
    public String SERVICE_NAME = "URLManager";

    /**
     * Matches any status in a list() operation
     */
    public int STATUS_ANY = -1;

    /**
     * Current status is unknown
     */
    public int STATUS_UNKNOWN = 0;

    /**
     * The URL can be fetched corretcly
     */
    public int STATUS_OK = 1;

    /**
     * The URL has permanent fatal errors
     */
    public int STATUS_UNREACHABLE = 2;

    /**
     * A possibly transient problem prevented the system to fetch this URL
     */
    public int STATUS_TEMPORARY_UNAVAILABLE = 4;

    /**
     * The content of this URL is corrupted or unparseable
     */
    public int STATUS_CONTENT_ERROR = 8;
    
    /**
     * This URL is not currently available for use
     */
    public int STATUS_BAD = STATUS_UNREACHABLE | STATUS_TEMPORARY_UNAVAILABLE | STATUS_CONTENT_ERROR;

    /**
     * Registers a new URL record
     * 
     * @param url the url to register
     */
    public void register( String url );

    /**
     * Registers a new URL record. If the url is already registered in 
     * the system, updates the status of this URL info record
     * 
     * @param url the url to register
     * @param status the status of this url
     */
    public void register( String url, int status );

    /**
     * Registers a new URL record. If the url is already registered in 
     * the system, updates both the status and the message of this URL 
     * info record
     * 
     * @param url the url to register
     * @param status the status of this url
     * @param message a descriptive message of the status
     */
    public void register( String url, int status, String message );

    /**
     * Register or replace an URL record. All records are keyed to
     * the imutable URL of URLInfo.
     * 
     * @param info the info record to store
     */
    public void register( URLInfo info );

    /**
     * Unregister an URL from the repository
     * 
     * @param url the url to remove
     */
    public void unregister( String url );

    /**
     * Get the information record stored in the database about
     * an URL.
     * 
     * @param url the url whose record is sought
     * @return the description record found in the repository or null.
     */
    public URLInfo getInfo( String url );
    
    /**
     * Test whether the URL is currently believed to be OK by this 
     * repository.
     * 
     * @param url the url to be tested
     * @return false is the url is known by this repository and has
     * a status indicating an error, true otherwise.
     */
    public boolean isOK( String url );
    
    /**
     * List of the current known URLs in the repository
     *
     * @return a List of URL strings known to this repository
     */
    public List list();
        
    /**
     * List of the current known URLs in the repository which have 
     * the given status.
     *
     * @param status the status to be retrieved. May be 
     * {@link URLManagerService#STATUS_ANY} to indicate any status
     * @return a List of URL strings known to this repository with this status
     */
    public List list( int status );

    /**
     * Return the proxy's port for a protocol.
     *
     * @param protocol The protocol that the proxy supports, e.g. 'http'
     * @return The port of the proxy (1-65535), or -1 if no port was specified (= use default)
     */
    public int getProxyPort( String protocol );

    /**
     * Return the proxy's hostname for a protocol.
     *
     * @param protocol The protocol that the proxy supports, e.g. 'http'
     * @return The hostname of the proxy, or <code>null</code> if no proxy is specified for this protocol
     */
    public String getProxyHost( String protocol );

}
