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

/**
 * <p>This is a simple URL information record which can be used
 * to track URL resources status in a persistent way.</p>
 *
 * <p>The url String used to initialize it MUST be interned,
 * to ensure that if two such urls are "equal()", they will be
 * also "==" </p>
 *
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: URLInfo.java,v 1.5 2004/02/23 03:30:47 jford Exp $
 */
public class URLInfo implements java.io.Serializable {
        
    private String url;
    private int status;
    private String message;

    /**
     * Creates a new minimum URLInfo record
     *
     * @param url a non-null url described by this class
     */
    URLInfo( String url ) {
        this( url, URLManagerService.STATUS_UNKNOWN, null );
    }
        
    /**
     * Creates a new URLInfo record
     *
     * @param url a non-null url described by this class
     * @param status the status of this url
     */
    URLInfo( String url, int status ) {
        this( url, status, null );
    }
        
    /**
     * Creates a new URLInfo record
     *
     * @param url a non-null url described by this class
     * @param status the status of this url
     * @param message a message suitable for display describing
     * the status of the url
     */
    URLInfo( String url, int status, String message ) {
        this.url = url.intern();
        this.status = status;
        this.message = message;
    }
        
    /**
     * Get the url.
     *
     * @return the url string described by this object
     */
    public String getURL() {
        return this.url;
    }
        
    /**
     * Get the status
     *
     * @return the status for this url
     */
    public int getStatus() {
        return status;
    }
        
    /**
     * Set the status
     *
     * @param status the status for this url
     */
    public void setStatus( int status ) {
        this.status = status;
    }

    /**
     * Get the message
     *
     * @return the message for this url
     */
    public String getMessage() {
        return message;
    }
        
    /**
     * Set the message
     *
     * @param message the message for this url
     */
    public void setMessage( String message ) {
        this.message = message;
    }

}
