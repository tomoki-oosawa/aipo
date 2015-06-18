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

import org.apache.turbine.services.TurbineServices;
import java.util.List;

/**
 * <p>This is a static wrapper around the URLManagerService for easing 
 * access to its functionalities</p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: URLManager.java,v 1.7 2004/02/23 03:30:47 jford Exp $
 */
public class URLManager {

    /** 
     * @see URLManagerService#STATUS_ANY
     */
    public static int STATUS_ANY = URLManagerService.STATUS_ANY;

    /** 
     * @see URLManagerService#STATUS_UNKNOWN
     */
    public static int STATUS_UNKNOWN = URLManagerService.STATUS_UNKNOWN;

    /** 
     * @see URLManagerService#STATUS_OK
     */
    public static int STATUS_OK = URLManagerService.STATUS_OK;

    /** 
     * @see URLManagerService#STATUS_UNREACHABLE
     */
    public static int STATUS_UNREACHABLE = URLManagerService.STATUS_UNREACHABLE;

    /** 
     * @see URLManagerService#STATUS_TEMPORARY_UNAVAILABLE
     */
    public static int STATUS_TEMPORARY_UNAVAILABLE = URLManagerService.STATUS_TEMPORARY_UNAVAILABLE;

    /** 
     * @see URLManagerService#STATUS_CONTENT_ERROR
     */
    public static int STATUS_CONTENT_ERROR = URLManagerService.STATUS_CONTENT_ERROR;
    
    /** 
     * @see URLManagerService#STATUS_BAD
     */
    public static int STATUS_BAD = URLManagerService.STATUS_BAD;

    /** 
     * @see URLManagerService#register
     */
    public static void register( String url ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        ums.register( url );
    }

    /** 
     * @see URLManagerService#register
     */
    public static void register( String url, int status ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        ums.register( url, status );
    }

    /** 
     * @see URLManagerService#register
     */
    public static void register( String url, int status, String message ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        ums.register( url, status, message );
    }

    /** 
     * @see URLManagerService#register
     */
    public static void register( URLInfo info ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        ums.register( info );
    }

    /** 
     * @see URLManagerService#unregister
     */
    public static void unregister( String url ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        ums.unregister( url );
    }

    /** 
     * @see URLManagerService#getInfo
     */
    public static URLInfo getInfo( String url ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        return ums.getInfo( url );
    }
    
    /** 
     * @see URLManagerService#isOK
     */
    public static boolean isOK( String url ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        return ums.isOK( url );
    }
    
    /** 
     * @see URLManagerService#list
     */
    public static List list() {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        return ums.list();
    }
        
    /** 
     * @see URLManagerService#list
     */
    public static List list( int status ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        return ums.list( status );
    }

    /** 
     * @see URLManagerService#getProxyPort
     */
    public static int getProxyPort( String protocol ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        return ums.getProxyPort( protocol );
    }
    
    /** 
     * @see URLManagerService#getProxyHost
     */
    public static String getProxyHost( String protocol ) {
        URLManagerService ums = (URLManagerService)TurbineServices
            .getInstance()
            .getService( URLManagerService.SERVICE_NAME );
        
        return ums.getProxyHost( protocol );
    }
}
