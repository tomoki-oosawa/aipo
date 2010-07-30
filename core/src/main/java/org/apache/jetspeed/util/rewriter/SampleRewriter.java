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


// java.io
import java.io.Reader;

// java.net
import java.net.MalformedURLException;

// this makes it dependent on Swing...need an abstraction WTP
import javax.swing.text.html.HTML;
import javax.swing.text.MutableAttributeSet;

/**
 *
 * Sample of extending HTML Rewriter for your specific needs
 *
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SampleRewriter.java,v 1.5 2004/02/23 03:18:59 jford Exp $
 */

public class SampleRewriter extends HTMLRewriter
{
    private boolean debug = false;
    private String basePortalURL;
    private String fullPortalURL;
    private String sampleURL;

    private String sessionID = "NONE";
    private String formID = "NONE";
    private boolean sampleEndFlag = false;

    public String getSessionID()
    {
        return sessionID;
    }

    public String getFormID()
    {
        return formID;
    }

    public boolean getSampleEndFlag()
    {
        return sampleEndFlag;
    }

    /*
     * Basic constructor for creating a Sample Rewriter.
     *
     * @param basePortalURL Base Portal URL
     * @param fullPortalURL  Full Portal URL with path parameters and query strings (sessionid)
     * @param sampleURL  The sample URL.
     *
     */
    public SampleRewriter(String basePortalURL, String fullPortalURL, String sampleURL )
    {
        this.basePortalURL = basePortalURL;
        this.fullPortalURL = fullPortalURL;
        this.sampleURL = sampleURL;
    }

    /*
     * Entry point into rewriting HTML content.
     *
     * Reads stream from proxied host, runs configured HTML parser against that stream,
     * rewriting relevant links, and writes the parsed stream back to the client.
     *
     * @param input the HTML input stream.
     * @param baseURL the base URL of the target host.
     * @return the rewritten HTML output stream.
     *
     * @exception MalformedURLException a servlet exception.
     */

    public String rewrite(Reader input, String baseURL)
                               throws MalformedURLException
    {
        String rewrittenHTML = "";
        this.basePortalURL = baseURL;

        HTMLParserAdaptor parser = new SwingParserAdaptor(this);
        rewrittenHTML = parser.run(input);

        return rewrittenHTML;
    }

    /*
     * <p>
     * This callback is called by the HTMLParserAdaptor implementation to write
     * back all rewritten URLs to point to the proxy server. The MMS implementation
     * writes specifically for network element ids and relative paths to MMS
     * resources.
     * </p>
     * <p>
     * Given the targetURL, rewrites the link as a link back to the proxy server.
     * </p>
     *
     * Example format:
     *
     *   http://proxyserver/proxy?neid=id?nepath=path
     *
     * @param targetURL the URL to be rewritten back to the proxy server.
     * @param baseURL the base URL of the target host.
     * @param proxyURL the base URL of the proxy server.
     * @return the rewritten URL to the proxy server.
     *
     * @exception MalformedURLException a servlet exception.
     */
    public String generateNewUrl(String targetURL, HTML.Tag tag, HTML.Attribute attribute)
    {
        if (debug)
            System.out.println("[rewriter] Tag: " + tag.toString() + "  Attribute: " + attribute.toString() + "  targetURL: " + targetURL + "  target = " + fullPortalURL + "]");

        // The only URL we want to re-write is ACTION attribute of the <FORM> tag.
        // Ignore all others
        if (tag == HTML.Tag.FORM && attribute == HTML.Attribute.ACTION) {

            // Strip the session Id value out of the ACTION attribute value
            int sessionLocation = targetURL.indexOf( "?sessionId" );
            if (sessionLocation > -1) {
                int equalsLocation = targetURL.indexOf( "=", sessionLocation );
                if (equalsLocation > -1) {
                    int ampLocation = targetURL.indexOf( "&", equalsLocation );
                    if (ampLocation > -1) {
                        sessionID = targetURL.substring( equalsLocation + 1, ampLocation );
                    } else {
                        sessionID = targetURL.substring( equalsLocation + 1 );
                    }
                }
            }

            if (sampleEndFlag) {
                // The sample session is being terminated, make the form action return to the portal home page
                return basePortalURL;
            } else {
                // Make the form action run the same portal page
                return fullPortalURL;
            }
        }

        // This is a tag that we do not wish to re-write, return it's own value unmodified
        return targetURL;
    }

    /*
     * Returns true if all rewritten URLs should be sent back to the proxy server.
     *
     * @return true if all URLs are rewritten back to proxy server.
     */
    public boolean proxyAllTags()
    {
        return true;
    }

    /*
     * Start Tag Events
     */
    public String exitStartTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        if (tag == HTML.Tag.FORM)
        {
            String inputTag = "<input type='hidden' name='sessionId' value='" + sessionID + "'/>";
            return inputTag;
        }
        return null;
    }

    /*
     * Simple Tag Events
     */

    public boolean enterSimpleTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        if (tag == HTML.Tag.META)
        {
            Object o = attrs.getAttribute(HTML.Attribute.NAME);
            if (o != null)
            {
                String s = o.toString();
                if (s.equalsIgnoreCase("SampleEnd"))
                {
                    sampleEndFlag = true;
                }
            }
        }
        return true;
    }


    /*
     * Convert Tag Events
     */

    public void convertTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
        if (tag == HTML.Tag.FORM) {
           // All forms from sample will have the same form NAME.
           // Jetspeed will add its own FORM depending on the type of portlet
           // being used.  So if you have multiple forms, any Javascript will
           // have to know which form to reference.
           attrs.addAttribute("NAME","SampleForm");
        }

        // INPUT Tag
        if (tag == HTML.Tag.INPUT)
        {
            Object o = attrs.getAttribute(HTML.Attribute.NAME);
            if (o != null)
            {
                String s = o.toString();
                if (s.equalsIgnoreCase("FormID"))
                {
                    o = attrs.getAttribute(HTML.Attribute.VALUE);
                    if (o != null)
                    {
                        formID = o.toString();
                    }
                }
            }
        }

    }

}

