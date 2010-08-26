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


// java.io
import java.io.IOException;
import java.io.FileOutputStream;

// java.util
import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;

// java.net
import java.net.InetAddress;
import java.net.UnknownHostException;

// javax.servlet
import javax.servlet.http.Cookie;


/**
 * Helper methods for WebPage Service
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: WebPageHelper.java,v 1.3 2004/02/23 03:46:26 jford Exp $
 */
public class WebPageHelper
{
    
    public static final int CT_TEXT  = 0;
    public static final int CT_BINARY = 1;
    public static final int CT_APPLICATION = 2;
    public static final int CT_HTML  = 3;
    public static final int CT_IMAGE = 4;
    public static final int CT_CSS   = 5;
    public static final int CT_JS    = 6;


    /**
     * Given the content-type header string, returns a content type id
     *
     * @param typeString the http content-type header string.
     * @return the integer value of the content-type 
     */

    public static int getContentType(String typeString, String resource)
    {
        int contentType = CT_HTML;

        if (null == typeString) {

            if (null == resource)
            {
                return contentType;
            }

            if (resource.endsWith(".js"))
            {
                return CT_JS;
            }
            else if (resource.endsWith(".gif") ||
                     resource.endsWith(".jpg") ||
                     resource.endsWith(".png"))
            {
                return CT_IMAGE;
            }
            else if (resource.endsWith(".css") )
            {
                return CT_CSS;
            }
            return contentType;
        }
        if (typeString.equalsIgnoreCase("text/html")) 
            contentType = CT_HTML;
        else if (typeString.startsWith("image")) 
            contentType = CT_IMAGE;
        else if (typeString.startsWith("text/css")) 
            contentType = CT_CSS;
        else if (typeString.startsWith("text")) 
            contentType = CT_TEXT;
        else if (typeString.startsWith("binary")) 
            contentType = CT_BINARY;
        else if (typeString.equals("application/x-javascript") )
            contentType = CT_JS;
        else if (typeString.startsWith("application")) 
            contentType = CT_APPLICATION;

        return contentType;
    }


    /**
     * Given a cookie object, build a http-compliant cookie string header
     *
     * @param Cookie the cookie source object.
     * @return the cookie string formatted as a http header.
     * 
     */
    public static String buildCookieString(Cookie cookie)
    {
        StringBuffer buffer = new StringBuffer();

        int version = cookie.getVersion();
        if (version != -1) {
           buffer.append("$Version=\"");          
           buffer.append(cookie.getVersion());
           buffer.append("\"; ");
        }
        // cookie name/value
        buffer.append(cookie.getName());
        //buffer.append("=\"");
        buffer.append("=");
        buffer.append(cookie.getValue());
        //buffer.append("\"; ");

        // cookie path
        String path = cookie.getPath();
        if (path != null) {
            //buffer.append("path=\""); // $Path
            buffer.append("; path=");
            buffer.append(path);
            //buffer.append("\"");        
        }

        String cookieHeader = buffer.toString();

        return cookieHeader;
    }

   /**
     * Parses cookies from the HTTP response header string
     * and stores them into this instance's cookies collection
     *
     * @param cookieHeader the string from the response header with the cookies.
     * @return true when cookies were found, otherwise false
     *
     */

    public static boolean parseCookies(String cookieHeader, SiteSession session)
    {                
        StringTokenizer st = new StringTokenizer(cookieHeader, " =;");
        String token, value;
        boolean firstTime = true; // cookie name/value always comes first
        Cookie cookie = null;

        while (st.hasMoreTokens()) 
        {
            token = st.nextToken(); 
            if (firstTime) {
                value  = st.nextToken(); 
                cookie = new Cookie(token, value);
                cookie.setVersion(-1);
                firstTime = false;
            }
            else if (token.equalsIgnoreCase("path")) {
                cookie.setPath(st.nextToken());
            }
            else if (token.equalsIgnoreCase("version")) {
                cookie.setVersion(Integer.getInteger(st.nextToken()).intValue());                
            }            
            else if (token.equalsIgnoreCase("max-age")) {
                cookie.setMaxAge( Integer.getInteger(st.nextToken()).intValue() );
            }
            else if (token.equalsIgnoreCase("domain")) {
                cookie.setDomain( st.nextToken() );
            }
            else if (token.equalsIgnoreCase("secure")) {
                cookie.setSecure(true);
            }
            else
            {
                if (null != cookie)                
                    session.addCookieToSession(cookie);
                if (!st.hasMoreTokens()) {
                    break;
                }
                value = st.nextToken(); 
                cookie = new Cookie(token, value);
                cookie.setVersion(-1);
           }           
        }
        if (null != cookie) 
           session.addCookieToSession(cookie);

        return (null != cookie); // found a cookie
    }

    /**
     * given a stringbuffer, replaces 'find' with 'replacement'
     *
     * @param buffer the string to be manipulated.
     * @param find the string to be replaced.
     * @param replacement the string that is put in place.
     *
     */
    public static StringBuffer replaceAll(StringBuffer buffer, 
                                          String find, 
                                          String replacement)
    {

        int bufidx = buffer.length() - 1;
        int offset = find.length();
        while( bufidx > -1 ) { 
            int findidx = offset -1;
            while( findidx > -1 ) {
                if( bufidx == -1 ) {
                    //Done
                    return buffer;
                }
                if( buffer.charAt( bufidx ) == find.charAt( findidx ) ) {
                    findidx--; //Look for next char
                    bufidx--; 
                } else {
                    findidx = offset - 1; //Start looking again
                    bufidx--;
                    if( bufidx == -1 ) {
                        //Done
                        return buffer;
                    }
                    continue;
                }
            }
            buffer.replace( bufidx+1, 
                            bufidx+1+offset, 
                            replacement);
            //start looking again
        }
        //No more matches
        return buffer;
            
    }

    /**
     * Given a base string and a path string, concatenates the strings to make a full URL.
     * Handles the concatenation for proper path separators.
     *
     * @param base the base part of a URL, such as http://localhost/
     * @param path the path part of the url, such as /webinterface/controllers/x.php
     * @param the concatenated path, such as http://localhost/webinterface/controllers/x.php
     *
     */
    public static String concatURLs(String base, String path)
    {
        String result = "";
        if (base.endsWith("/")) 
        {
            if (path.startsWith("/"))
            {
                result = base.concat( path.substring(1));
                return result;
            }
            
        }
        else
        {
            if (!path.startsWith("/")) 
            {
                result = base.concat("/").concat(path);
                return result;
            }
        }
        return base.concat(path);
    }

    /*
     * Maps the availability status code to a small string message
     *
     * @param status the integer availability status code.
     * @return the corresponding string message for the status code.
     */
    public static String getAvailabilityStatus(int status)
    {
        switch(status)
        {
        case 0:    return "Not Initialized";
        case 1:    return "Online";
        default:   return "Offline";
        }
    }


    /*
     * Writes the date/time stamp header to the content log.
     *
     * @param fos The file output stream that is written to (the content log).
     *
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String CONTENT_LOG_HEADER = 
        "------------------------------------------------------" ;

    public static void writeHeader(FileOutputStream fos, String resource)
        throws IOException
    {
        fos.write(13);
        fos.write(10);       
        fos.write(CONTENT_LOG_HEADER.getBytes());
        fos.write(13);
        fos.write(10);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        fos.write(sdf.format(new Date()).getBytes());
        fos.write(13);
        fos.write(10);
        fos.write(resource.getBytes());
        fos.write(13);
        fos.write(10);
    }

    /*
     * Gets the IP address for a given hostname.
     *
     * @param  hostname The hostname that we are looking up the IP address for.
     * @return The IP address of the given hostname.
     */
    public static String getIP(String hostname)
    {
        String ip = null;

        try
        {
            InetAddress computer = InetAddress.getByName(hostname);
            ip = computer.getHostAddress();
        }
        catch (UnknownHostException ex)
        {
        }
        return ip;
    }

    private static int id = 0;
    
    public static synchronized long generateId()
    {
        id = id + 1;
        return id;
    }
}
