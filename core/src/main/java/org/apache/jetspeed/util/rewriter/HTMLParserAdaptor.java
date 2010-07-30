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

package org.apache.jetspeed.util.rewriter;

import java.net.MalformedURLException;
import java.io.Reader;

/*
 * Interface for HTML Parser Adaptors.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: HTMLParserAdaptor.java,v 1.4 2004/02/23 03:18:59 jford Exp $
 */

public interface HTMLParserAdaptor
{
    /*
     * Parses and rewrites a HTML document, rewriting all URLs as either fully proxied
     * URLs or as web-application full URLs, not relative.
     *
     * @param reader to the source to be read
     
     * @throws MalformedURLException If the baseUrl is not a valid URL or if an URL inside
     * the document could not be converted.     
     * @return An HTML-String with rewritten URLs.
     */    
    
    String run(Reader reader)
            throws MalformedURLException;
}

