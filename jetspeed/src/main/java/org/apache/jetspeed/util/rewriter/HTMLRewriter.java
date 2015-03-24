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
import java.net.URL;

// this makes it dependent on Swing...need an abstraction WTP
import javax.swing.text.html.HTML;
import javax.swing.text.MutableAttributeSet;
 
/**
 *                  
 * Basic Rewriter for rewriting HTML content. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: HTMLRewriter.java,v 1.6 2004/02/23 03:18:59 jford Exp $
 * 
 */

public class HTMLRewriter implements Rewriter
{
    /*
     * Construct a basic HTML Rewriter
     *
     */
    public HTMLRewriter()
    {
    }

    protected String baseURL = null;

    /*    
     * This callback is called by the HTMLParserAdaptor implementation to write
     * back all rewritten URLs to point to the proxy server.
     * Given the targetURL, rewrites the link as a link back to the proxy server.
     *
     * @return the rewritten URL to the proxy server.
     *
     */
    public String generateNewUrl( String targetURL, HTML.Tag tag, HTML.Attribute attribute)
    {
        String fullPath = "";
        try
        {

            if (baseURL != null)
            {
                URL full = new URL(new URL(baseURL), targetURL);
                fullPath = full.toString();
            }
            else
            {
                return targetURL; // leave as is
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        return fullPath;

    }

    
    /*
     * Returns true if all rewritten URLs should be sent back to the proxy server.
     *
     * @return true if all URLs are rewritten back to proxy server.
     */
    public boolean proxyAllTags()
    {
        return true; //false;
    }

    public String rewrite(Reader input, String baseURL)
                               throws MalformedURLException
    {
        String rewrittenHTML = "";
        this.baseURL = baseURL;

        HTMLParserAdaptor parser = new SwingParserAdaptor(this);
        rewrittenHTML = parser.run(input);

        return rewrittenHTML;
    }

    /*
     * Simple Tag Events
     */
    public boolean enterSimpleTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        return true;
    }

    public String exitSimpleTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        return null;
    }

    /*
     * Start Tag Events
     */
    public boolean enterStartTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        return true;
    }

    public String exitStartTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        return null;
    }

    /*
     * Exit Tag Events
     */
    public boolean enterEndTagEvent(HTML.Tag tag)
    {
        return true;
    }

    public String exitEndTagEvent(HTML.Tag tag)
    {
        return null;
    }

    /*
     * Text Event
     */
    public boolean enterText(char[] values, int param)
    {
        return true;
    }

    /*
     * Convert Tag Events
     */
    public void convertTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
    }

}

