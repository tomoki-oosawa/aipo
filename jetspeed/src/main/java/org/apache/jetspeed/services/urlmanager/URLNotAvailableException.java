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

import java.io.IOException;

/**
<p>
Thrown if a URL is not available becuase it is in the BadURLManager.
</p>

<p>
Note that this extends java.io.IOException so that it is compatible with 
existing IO code.
</p>

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: URLNotAvailableException.java,v 1.3 2004/02/23 03:30:47 jford Exp $
*/
public class URLNotAvailableException extends IOException {

    public static final String MESSAGE = "The following URL is not available because it is considered invalid: ";
    
    public URLNotAvailableException( String reason, 
                                     String url ) {

        super( MESSAGE + 
               url + 
               " -> " +
               reason );

    }

    public URLNotAvailableException( String url ) {

        super( MESSAGE + 
               url );

    }

    
    
}

