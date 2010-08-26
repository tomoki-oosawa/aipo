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

package org.apache.jetspeed.util.rewriter;

// java.io
import java.io.Reader;

// java.net
import java.net.MalformedURLException;

// this makes it dependent on Swing...need an abstraction WTP
import javax.swing.text.html.HTML;
import javax.swing.text.MutableAttributeSet;

/**
 * 
 * Interface for  URL rewriting.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: Rewriter.java,v 1.6 2004/02/23 03:18:59 jford Exp $
 */

public interface Rewriter
{

    /*
     * Entry point into rewriting HTML content.
     *
     * Reads stream from proxied host, runs configured HTML parser against that stream,
     * rewriting relevant links, and writes the parsed stream back to the client.
     *
     * @param input the HTML input stream.
     * @param input the base URL host string used to create full links back to host
     * @return the rewritten HTML output stream.
     *
     * @exception MalformedURLException a servlet exception.
     */
    String rewrite(Reader input, String baseURL)
            throws MalformedURLException;
                               

    /* <p>
     * This callback is called by the HTMLParserAdaptor implementation to write
     * back all rewritten URLs to point to the proxy server. Each implementation
     * writes specifically for their own type of resources.</p>
     * <p>
     * Given the targetURL, rewrites the link as a link back to the proxy server.     
     * </p>
     * @param targetURL the URL to be rewritten back to the proxy server.
     * @param baseURL the base URL of the target host.
     * @param proxyURL the base URL of the proxy server.
     * @return the rewritten URL to the proxy server.
     *
     * @exception MalformedURLException a servlet exception.
     */
    String generateNewUrl(String targetURL, HTML.Tag tag, HTML.Attribute attribute);

    /*
     * Returns true if all rewritten URLs should be sent back to the proxy server.
     *
     * @return true if all URLs are rewritten back to proxy server.
     */        
    boolean proxyAllTags();

    // parser event handling
    boolean enterSimpleTagEvent(HTML.Tag tag, MutableAttributeSet attrs);
    String exitSimpleTagEvent(HTML.Tag tag, MutableAttributeSet attrs);

    boolean enterStartTagEvent(HTML.Tag tag, MutableAttributeSet attrs);
    String exitStartTagEvent(HTML.Tag tag, MutableAttributeSet attrs);

    boolean enterEndTagEvent(HTML.Tag tag);
    String exitEndTagEvent(HTML.Tag tag);

    boolean enterText(char[] values, int param);

    void convertTagEvent(HTML.Tag tag, MutableAttributeSet attrs);
}

