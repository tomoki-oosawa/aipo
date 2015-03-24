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

package org.apache.jetspeed.portal.portlets;


//Jetspeed stuff
import org.apache.jetspeed.portal.*;
import org.apache.jetspeed.util.*;
import org.apache.jetspeed.cache.disk.*;

//standard java stuff
import java.io.*;

/**
 * <p>Serve a static URL (typically a HTML fragment)</p>
 *
 @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 @version $Id: FileServerPortlet.java,v 1.27 2004/02/23 04:03:33 jford Exp $ 
*/
public class FileServerPortlet extends FileWatchPortlet {

    /**
    */
    public void init() throws PortletException {

        // first make sure we propagate init
        super.init();
        
        PortletConfig config = this.getPortletConfig();
        
        //fetch the URL as a String...

        try {

            this.setContent( new JetspeedClearElement( this.getURL(  this.getPortletConfig().getURL() ) ) );

        } catch (Exception e) {
            throw new PortletException( e.getMessage() );
        }
            
       
    }

    /**
    */
    private String getURL(String url) throws IOException {

        int CAPACITY = 1024;

        Reader rdr = JetspeedDiskCache.getInstance()
            .getEntry( url ).getReader();
        StringBuffer buffer = new StringBuffer();

        //now process the Reader...
        char[] chars = new char[CAPACITY];

        int readCount = 0;
        while( ( readCount = rdr.read( chars )) > 0 ) {

            buffer.append( chars, 0, readCount);
        }

        rdr.close();


        return buffer.toString();
            

    }

}
