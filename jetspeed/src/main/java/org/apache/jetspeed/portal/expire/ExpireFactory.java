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

package org.apache.jetspeed.portal.expire;

//jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.JetspeedException;

/**
Handles content expiration

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: ExpireFactory.java,v 1.6 2004/02/23 03:24:40 jford Exp $
*/
public class ExpireFactory 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ExpireFactory.class.getName());
    
    public static final String FILE_WATCH_EXPIRE 
        = "org.apache.jetspeed.portal.expire.FileWatchExpire";

    public static final String NO_EXPIRE 
        = "org.apache.jetspeed.portal.expire.NoExpire";    

    
    /**
    Since this object essentially does nothing.  Only use one instance for
    performance
    */
    public static Expire noExpire = new NoExpire();
        
    
    /**
    Create a new Expire object with the specified Portlet and classname.

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: ExpireFactory.java,v 1.6 2004/02/23 03:24:40 jford Exp $
    */
    public static Expire getExpire( Portlet portlet,
                                    String classname ) throws JetspeedException {
        
        if ( classname.equals( NO_EXPIRE ) ) {
            return noExpire;
        }
        
        try {
            Expire expire = (Expire)Class.forName( classname ).newInstance();
            expire.init();
            expire.setPortlet( portlet );
            expire.setExpired( false );

            return expire;
        } catch ( Throwable t ) {
            logger.error("Throwable",  t );
            throw new JetspeedException( t.getMessage() );
        }
    }
    
}

