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
import java.io.IOException;
import java.io.CharArrayWriter;
import java.io.Reader;

// java.net
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;                 

// this makes it dependent on Swing...need an abstraction WTP
import javax.swing.text.html.HTML;
import javax.swing.text.MutableAttributeSet;
 
/**
 *                  
 * Proxy Rewriter for rewriting HTML content with links back to a proxy host.
 * URLs or as web-application full URLs, not relative.
 * Given a relative URL, such a "/content/images/my.gif" it can be rewritten as either
 * a proxied URL, for example:
 *
 *   "http://proxyserver/proxy?pxpath=/content/images/my.gif"
 * 
 * or a full path to the URL on the web server:
 *
 *   "http://www.webserver.com/content/images/my.gif" 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ProxyRewriter.java,v 1.3 2004/02/23 03:18:59 jford Exp $
 * 
 */

public class ProxyRewriter implements Rewriter
{
    /*
     * Construct a basic HTML Rewriter
     *
     */
    public ProxyRewriter()
    {
    }

    /*
     * Entry point into rewriting HTML content.
     *
     * Reads stream from proxied host, runs configured HTML parser against that stream,
     * rewriting relevant links, and writes the parsed stream back to the client.
     *
     * @param input the HTML input stream.
     * @param proxyRoot the base URL of the proxy server.
     * @param baseURL the base URL of the target host.
     * @return the rewritten HTML output stream.
     *
     * @exception MalformedURLException a servlet exception.
     */
    public String rewrite(Reader input, 
                          String proxyRoot, 
                          String baseURL)
                               throws MalformedURLException
    {
        String rewrittenHTML = "";
        
        this.proxyRoot = proxyRoot;
        this.baseURL = baseURL;
        HTMLParserAdaptor parser = new SwingParserAdaptor(this);
        rewrittenHTML = parser.run(input);

        return rewrittenHTML;
    }

    private String proxyRoot;
    private String baseURL;

    public final static String DEFAULT_PROXY_PARAM = "js_path";

    /*    
     * This callback is called by the HTMLParserAdaptor implementation to write
     * back all rewritten URLs to point to the proxy server.
     * Given the targetURL, rewrites the link as a link back to the proxy server.
     *
     * @param targetURL the URL to be rewritten back to the proxy server.
     * @param baseURL the base URL of the target host.
     * @param proxyURL the base URL of the proxy server.
     * @return the rewritten URL to the proxy server.
     *
     * @exception MalformedURLException a servlet exception.
     */
    public String generateNewUrl( String targetURL, HTML.Tag tag, HTML.Attribute attribute)
                                //  String  targetURL, 
                                // String     baseURL,
                                // String     proxyURL,
                                // boolean proxied)
    {
        try {                

            URL full = new URL(new URL(proxyRoot), targetURL);
            String fullPath = full.toString();

            StringBuffer buffer = new StringBuffer(proxyRoot.toString());
            buffer.append("?");
             buffer.append(DEFAULT_PROXY_PARAM);
            buffer.append("=");
            buffer.append(URLEncoder.encode(fullPath));
            String proxiedPath = buffer.toString().replace('&', '@');
            return proxiedPath;

        } 
        catch (Throwable t)
        {
            //FIXME: transient print to debug...
            System.err.println( "HTMLRewriter: BASE=" + proxyRoot);
            System.err.println( "target=" + targetURL);
            return URLEncoder.encode(targetURL);    
        }

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


    public static byte[] rewriteScript(String script, 
                                       String url,
                                       String proxyHost,
                                       String base)
                  throws IOException 
    {
        int baseLength = base.length();

        int totalScriptLength = script.length();
        CharArrayWriter writer = new CharArrayWriter(totalScriptLength + 100);
        char chars[] = script.toCharArray();
        boolean translating = false;        

        // now rewrite the script stream
        for (int ix=0; ix < chars.length; ix++)         
        {
            if (chars[ix] == '"')
            {
                //int endpos= ix + len + 1;
                if (translating) 
                    translating = false;
                else if (false == translating ) //&& 
                  //       endpos < totalScriptLength)
                {
                        translating = true;
                        writer.write(chars[ix]);

                        if (!findImage(chars, ix + 1))
                            continue;

                        String trans = translate(proxyHost, base);
                        writer.write(trans);
                        if (chars[ix+1] != PATH_SEPARATOR && base.charAt(baseLength - 1) != PATH_SEPARATOR) 
                            writer.write(PATH_SEPARATOR);
                        if (chars[ix+1] == PATH_SEPARATOR && base.charAt(baseLength - 1) == PATH_SEPARATOR)
                            ix++;

                        continue;
                }
            }
            if (translating && chars[ix] == '&') 
                writer.write('@');
            else
                writer.write(chars[ix]);
        }

        return writer.toString().getBytes();
    }


    public static String translate(String proxyURL, String targetURL)
     {
         StringBuffer buffer = new StringBuffer(proxyURL);
         buffer.append("?");
         buffer.append(DEFAULT_PROXY_PARAM);
         buffer.append("=");
         buffer.append(targetURL.replace('&', '@'));
         String proxiedPath = buffer.toString();                

         return proxiedPath;
     }

    /*
     * Finds an image hyperlink in a quoted string.
     * The image hyperlink is found by searching through the script text, searching
     * for references ending in typical image extensions (GIF, PNG, JPG).
     *
     * NOTE: this function is just the start of script-parsing.
     * A much more robust implementation will be necessary.
     *
     * @param chars The character array to search.
     * @param ix The starting index to search from in the character array.
     * @return If the image string is found, returns true otherwise false.
     *
     */
    protected static boolean findImage(char[] chars, int ix)
    {
        for (int iy=ix; iy < chars.length  ; iy++)
        {
            if (chars[iy] == '"')
                return false;
            if (chars[iy] == '.')                
            {
                int iw = 0;
                for (int iz = iy+1; iz < chars.length && iw < 3; iz++, iw++ )
                {
                    if (chars[iz] == GIF[iw] || chars[iz] == PNG[iw] || chars[iz] == JPG[iw])
                    {
                        continue;
                    }
                    else
                        return false;
                }
                if (iw == 3)
                    return true;

                return false;
            }
        }
        return false;
    }
    
    private static final char[] GIF = {'g', 'i', 'f'};
    private static final char[] PNG = {'p', 'n', 'g'};
    private static final char[] JPG = {'j', 'p', 'g'};
    protected static final char PATH_SEPARATOR = '/';

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
     * Convert Tag Events
     */
    public void convertTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
    {
    }

	public boolean enterText(char[] values, int param)
	{
		return false;
	}
}


