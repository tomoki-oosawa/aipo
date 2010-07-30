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

package org.apache.jetspeed.services.ldap;

import java.net.MalformedURLException;
import java.net.URLDecoder;

/**
 *
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @version $Id: LDAPURL.java,v 1.6 2004/02/23 03:28:31 jford Exp $ 
 * 
 */
public class LDAPURL
{
    private String host;
    private int port;
    private String dn;
    private String base;

    public LDAPURL()
    {
        host = dn = base = null;
        port = 389;
    }

    public LDAPURL(String url)
        throws MalformedURLException
    {
        try
        {
            // this is the correct approach for 1.4, unfortunately its unsupported in 1.3
            // uncomment the line below if using 1.4
            // url = URLDecoder.decode(url,"UTF-8");
            url = URLDecoder.decode(url); // deprecated in 1.4
        }
        catch(Exception e)
        {
            throw new MalformedURLException(e.getMessage());
        }
        int p1 = url.indexOf("://");
        if(p1 == -1)
            throw new MalformedURLException("Missing '[protocol]://'");
        String protocol = url.substring(0, p1);
        p1 += 3;
        int p2 = url.indexOf(47, p1);
        String base = null;
        if(p2 == -1)
        {
            base = url.substring(p1);
            parseHostPort(base);
            dn = "";
        } else
        {
            base = url.substring(p1, p2);
            p2++;
            dn = url.substring(p2);
            int p3 = dn.indexOf(63);
            if(p3 != -1)
                dn = dn.substring(0, p3);
            parseHostPort(base);
        }
    }

    public LDAPURL(String host, int port, String dn)
    {
        this.host = host;
        this.port = port;
        this.dn = dn;
    }

    public static String encode(String toEncode)
    {
        StringBuffer encoded = new StringBuffer(toEncode.length() + 10);
        for(int currPos = 0; currPos < toEncode.length(); currPos++)
        {
            char currChar = toEncode.charAt(currPos);
            if(currChar >= 'a' && currChar <= 'z' || currChar >= 'A' && currChar <= 'Z' || currChar >= '0' && currChar <= '9' || "$-_.+!*'(),".indexOf(currChar) > 0)
            {
                encoded.append(currChar);
            } else
            {
                encoded.append("%");
                encoded.append(hexChar((currChar & 0xf0) >> 4));
                encoded.append(hexChar(currChar & 0xf));
            }
        }

        return encoded.toString();
    }

    public String getBase()
    {
        if(base == null)
            base = "ldap://" + host + ":" + port;
        return base;
    }

    public String getDN()
    {
        return dn;
    }

    public String getEncodedUrl()
    {
        return getBase() + "/" + encode(dn);
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getUrl()
    {
        return getBase() + "/" + dn;
    }

    private static char hexChar(int hexValue)
    {
        if(hexValue < 0 || hexValue > 15)
            return 'x';
        if(hexValue < 10)
            return (char)(hexValue + 48);
        else
            return (char)((hexValue - 10) + 97);
    }

    private void parseHostPort(String str)
        throws MalformedURLException
    {
        int p1 = str.indexOf(58);
        if(p1 == -1)
        {
            host = str;
            port = 389;
        } else
        {
            host = str.substring(0, p1);
            String pp = str.substring(p1 + 1);
            try
            {
                port = Integer.parseInt(pp);
            }
            catch(NumberFormatException _ex)
            {
                throw new MalformedURLException("Invalid port number: " + pp);
            }
        }
    }

    public boolean sameHosts(LDAPURL url)
    {
        return getHost().equalsIgnoreCase(url.getHost()) && getPort() == url.getPort();
    }

    public void setDN(String dn)
    {
        this.dn = dn;
    }

    public void setHost(String host)
    {
        this.host = host;
        base = null;
    }

    public void setPort(int port)
    {
        this.port = port;
        base = null;
    }

    public static String toUrl(String host, int port, String dn, boolean ssl)
    {
        StringBuffer msg = new StringBuffer();
        msg.append(ssl ? "ldaps://" : "ldap://");
        msg.append(host);
        if(ssl && port != 636 || !ssl && port != 389)
        {
            msg.append(":");
            msg.append(String.valueOf(port));
        }
        msg.append("/");
        msg.append(dn);
        return msg.toString();
    }

    public String toString()
    {
        return "LDAPURL: base = " + base + ", url = " + toUrl(host, port, dn, false);
    }

}
