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

package org.apache.jetspeed.portal.expire;

import org.apache.jetspeed.portal.*;

import java.io.Serializable;
/**
Handles content expiration

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
*/
public interface Expire extends Serializable{

    
    /**
    Initialize this Expiration mechanism

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public void init();

    /**
    Return true if this object has expired.  Provide your own implementation of
    this if you want to determine dynamic expiration.

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public boolean isExpired();

    /**
    Set this as expired.

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public void setExpired( boolean expired );

    /**
    Get the time this Expire object was created

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public long getCreationTime();
  
    /**
    Set the time this Expire object was created
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public void setCreationTime( long creationTime );
    
    /**
    Set a property
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public void setProperty( String name, String value );

    /**
    Get a property
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public String getProperty( String name );

    /**
    Get the Portlet on which this is based.
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public Portlet getPortlet();
    
    /**
    Set the Portlet on which this is based.
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: Expire.java,v 1.5 2004/02/23 03:24:40 jford Exp $
    */
    public void setPortlet( Portlet portlet );
    

    
}

