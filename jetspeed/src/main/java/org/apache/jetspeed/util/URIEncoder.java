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
Take a URI and encode it so that it can be stored on all filesystems and HTTP
values 

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
@version $Id: URIEncoder.java,v 1.11 2004/02/23 03:23:42 jford Exp $
*/
public class URIEncoder {


    /**
    A list of invalid characters that can't exist within filenames.  If they 
    appear then the DiskCache will escape them.  The current list is in part
    based on Microsoft Knowledge Base article Q177506 (because DOS filesystems
    are more generally limited than UNIX filesystems).

    SGP: Windows NT refuses to take "?", so I add it to the list.
    Additionally, if we encode "?", the jdk runtime logic decodes it twice 
    for "file:" urls, giving a filename with a space in it. I have fixed 
    it in JetspeedDiskCacheEntry.java, avoiding the creation of a new URL when
    getFile() is not null.
    */
    public static final String[] INVALID_CHARACTERS = { "\\", 
                                                        "/", 
                                                        ":", 
                                                        "*", 
                                                        "\"", 
                                                        "<", 
                                                        ">", 
                                                        "|", 
                                                        "+", 
                                                        "?" };
    public static final String[] CODED_CHARACTERS = { "#" + (int)'\\' + ";", 
                                                      "#" + (int)'/' + ";",
                                                      "#" + (int)':' + ";",
                                                      "#" + (int)'*' + ";",
                                                      "#" + (int)'"' + ";",
                                                      "#" + (int)'<' + ";",
                                                      "#" + (int)'>' + ";",
                                                      "#" + (int)'|' + ";",
                                                      "#" + (int)'+' + ";",
                                                      "#" + (int)'?' + ";"
    };
    
    /**
    Encode the given URI
    */
    public static String encode( String uri ) {

        if ( uri == null ) {
            throw new IllegalArgumentException( "URI may not be null. " );
        }
        
        /*
        
        Very basic encoding... should work for most circumstances.
        
        files like http://www.apache.org:80/index.html
        
        will be changed to:
        
        http_www.apache.org___80.index.html
        
        - a "_"         ->  "__"
        - a "://"       ->  "_"    
        - a "/"       ->  "_"    
        - a ":"       ->  "___"
        */
        
        StringBuffer buffer = new StringBuffer( uri );
        StringUtils.replaceAll( buffer, "_", "__" );
        StringUtils.replaceAll( buffer, "://", "_" );
        StringUtils.replaceAll( buffer, "/", "_" );
        StringUtils.replaceAll( buffer, ":", "___" );

        
        //if there are any characters that can't be stored in a filesystem encode
        //them now
        encodeQueryData( buffer );
        
        
        return buffer.toString();
    }


    /**
    Decode the given URI.
    */
    public static String decode( String uri ) {

        if ( uri == null ) {
            throw new IllegalArgumentException( "URI may not be null. " );
        }
        
        String newURI = "";

        int start = uri.indexOf("_");

        String protocol = null;
        
        //SGP: needed if uri does not contain protocol but contains "_"
        if( uri.charAt( start + 1 ) == '_' ) {
            start = -1;
        }

        if ( start > -1 ) {
            protocol = uri.substring( 0, start );
        }
        
        newURI = uri.substring( start + 1, uri.length() );
        StringBuffer buffer = new StringBuffer( newURI );

        StringUtils.replaceAll( buffer, "___", ":" );

        StringUtils.replaceAll( buffer, "_", "/" );
        StringUtils.replaceAll( buffer, "_", "/" );
        
        //now the original "__" should be slashes so replace them with a single "_"
        StringUtils.replaceAll( buffer, "//", "_" );
      
        if ( protocol != null ) {
            buffer.replace( 0, 0, "://" ); //prepend string
            buffer.replace( 0, 0, protocol ); //prepend protocol
        }
        
        decodeQueryData( buffer );
        
        return buffer.toString();
    }
    
    /**
    <p>
    If this data contains any INVALID_CHARACTERS encode the data into a target
    String.
    </p>
    
    <p>
    NOTE: the algorithm between encode and decode is shared, if you modify one
    you should modify the other.
    </p>
    @see decode(String data)
    */
    private static StringBuffer encodeQueryData( StringBuffer data ) {
        
        for (int i = 0; i < INVALID_CHARACTERS.length; ++i ) {
            
            String source = INVALID_CHARACTERS[i];
            
            String coded = CODED_CHARACTERS[i]; 
            
            data = StringUtils.replaceAll( data, source, coded );            
            
        }
        
        return data;
    }
    
    /**
    <p>
    If this data contains any encoded INVALID_CHARACTERS, decode the data back 
    into the source string
    </p>
    
    <p>
    NOTE: the algorithm between encode and decode is shared, if you modify one
    you should modify the other.
    </p>
    @see encode(String data)
    */
    private static StringBuffer decodeQueryData( StringBuffer data ) {
        
        for (int i = 0; i < INVALID_CHARACTERS.length; ++i ) {
            
            String source = INVALID_CHARACTERS[i];
            
            String coded = CODED_CHARACTERS[i]; 
            
            data = StringUtils.replaceAll( data, coded, source );            
            
        }
        
        return data;
    }

    
}
