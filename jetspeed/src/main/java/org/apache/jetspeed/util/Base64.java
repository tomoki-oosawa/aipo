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
package org.apache.jetspeed.util;

import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Simple Base64 string decoding function
 * @author Jason Borden <jborden@javasense.com>
 *
 * This class was copied from the jakarta-james project.
 * The only change made, besides comments, is the package name.
 * This class orgininated in org.apache.james.util as version 1.3
 * which was committed by darrel on 2002/01/18 02:48:39
 * 
 * $Id: Base64.java,v 1.5 2004/02/23 03:23:42 jford Exp $
 * Committed on $Date: 2004/02/23 03:23:42 $ by: $Name: JETSPEED-RELEASE-1-5 $ 
 */

public class Base64 {

    public static String decodeAsString(String b64string) throws Exception
    {
        return new String(decodeAsByteArray(b64string));
    }

    public static byte[] decodeAsByteArray(String b64string) throws Exception
    {
        InputStream in = MimeUtility.decode(new ByteArrayInputStream(
                         b64string.getBytes()), "base64");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        while(true)
        {
            int b = in.read();
            if (b == -1) break;
            else out.write(b);
        }
    
        return out.toByteArray();
    }

    public static String encodeAsString(String plaintext) throws Exception
    {
        return encodeAsString(plaintext.getBytes());
    }

    public static String encodeAsString(byte[] plaindata) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream inStream = new ByteArrayOutputStream();

        inStream.write(plaindata, 0, plaindata.length);

        // pad
        if ((plaindata.length % 3 ) == 1)
        {
            inStream.write(0);
            inStream.write(0);
        }
        else if((plaindata.length % 3 ) == 2)
        {
            inStream.write(0);
        }

        inStream.writeTo(MimeUtility.encode(out, "base64"));
        return out.toString();
    }

}

