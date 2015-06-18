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


/**

<p>Utility class for declaring MIME types to use for various requests and provide
utility manipulation methods.</p>
<p>Added Content-Encoding capability, with defaults

@author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
@author <a href="mailto:sgala@apache.org">Santiago Gala</a>
@version $Id: MimeType.java,v 1.8 2004/02/23 03:23:42 jford Exp $
*/
public class MimeType {

    public static final MimeType HTML  = new MimeType( "text/html", "UTF-8" ); //FIXME: test
    public static final MimeType XHTML = new MimeType( "text/xhtml" );
    public static final MimeType WML   = new MimeType( "text/vnd.wap.wml" );
    public static final MimeType XML   = new MimeType( "text/xml" );
    public static final MimeType VXML  = new MimeType( "text/vxml" );
    
    /**
     * Standard ContentType String, with no encoding appended.
     */
    private String mimeType = "";
    /**
     * null value means default encoding.
     * Otherwise, charset to be used.
     */
    private String charSet = null;
    
    public MimeType( String mimeType ) {
        if(mimeType == null) {
            throw new NullPointerException();
        }
        this.mimeType = mimeType;
    }
    
    /**
     *
     */
    public MimeType( String mimeType, String charSet ) {
        if(mimeType == null) {
            throw new NullPointerException();
        }
        this.mimeType = mimeType;
        this.charSet = charSet;
    }
    
    /** Extracts from this MimeType a user-friendly identifying code 
      * ie "html" for "text/html" or "wml" for "text/vnd.wap.wml"
      *
      * @param mime the type to simplify
      * @return the simplified type
      */
    public String getCode() {
        String type = this.mimeType;
        // get everything after "/"
        type = type.substring(type.indexOf("/")+1);
        // remove any dot in the name
        int idx = type.lastIndexOf(".");
        if (idx >= 0 ) {
            type = type.substring(idx+1);
        }
        //remove anything before a "-"
        idx = type.lastIndexOf("-");
        if (idx >= 0 ) {
            type = type.substring(idx+1);
        }
        
        return type.toLowerCase();
    }

    /**
    Return the media type associated
    */
    public String getContentType()
    {
        return this.mimeType;
    }

    /**
    Return the character encoding associated, if any
    */
    public String getCharSet()
    {
        return this.charSet;
    }

    /**
    Convert this MimeType to its external String representation
    */
    public String toString()
    {
        if( null == this.charSet )
        {
            return this.mimeType;
        }
        return this.mimeType +
            "; charset=" +
            this.charSet;
    }

    /**
    Compare one MimeType to another
    */
    public boolean equals( Object obj ) {
        if ( this == obj) {
            return true;
        }
        
        if (obj instanceof MimeType) {
            MimeType comp = (MimeType)obj;
            return this.toString().equals( comp.toString() );
        } else {
            return false;
        }
    }
    
}
