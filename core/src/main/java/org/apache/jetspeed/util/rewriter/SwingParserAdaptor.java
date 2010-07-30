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

// javax.swing.text
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.HTMLEditorKit;

// java.io
import java.io.*;

// java.util
import java.util.*;

// java.net
import java.net.*;
import org.apache.turbine.util.Log;//AAAtogli!


/*
 * HTML Parser Adaptor for the Swing 'HotJava' parser.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SwingParserAdaptor.java,v 1.6 2004/02/23 03:18:59 jford Exp $
 */

public class SwingParserAdaptor implements HTMLParserAdaptor
{

    private SwingParserAdaptor.Callback cb = new SwingParserAdaptor.Callback();
    private String lineSeparator;
    private boolean skippingImplied = false;
    private Rewriter rewriter;
    /*
     * Construct a swing (hot java) parser adaptor
     * Receives a Rewriter parameter, which is used as a callback when rewriting URLs.
     * The rewriter object executes the implementation specific URL rewriting.
     *
     * @param rewriter The rewriter object that is called back during URL rewriting
     */
    public SwingParserAdaptor(Rewriter rewriter)
    {
        this.rewriter = rewriter;
        lineSeparator = System.getProperty("line.separator", "\r\n");         
    }

    /*
     * Parses and an HTML document, rewriting all URLs as determined by the Rewriter callback
     *
     *
     * @param reader The input stream reader 
     *
     * @throws MalformedURLException 
     *
     * @return An HTML-String with rewritten URLs.
     */    
    public String run(Reader reader)
    throws MalformedURLException
    {
        HTMLEditorKit.Parser parser = new SwingParserAdaptor.ParserGetter().getParser();        

        String res ="";
        try
        {
            parser.parse(reader, cb, true);
            res = cb.getResult(); 
        } catch (Exception e)
        {
            e.printStackTrace();
      //Log.info("Exception occurred:" + e.toString());AAAtogli!!!
      //Log.info("Exception occurred:" + e.printStackTrace());
            throw new MalformedURLException(e.toString());
        }
        return res;
    }


    /*
     * This Class is needed, because getParser is protected and therefore 
     *  only accessibly by a subclass
     */
    class ParserGetter extends HTMLEditorKit
    {

        public HTMLEditorKit.Parser getParser(){
            return super.getParser();
        }
    } 


    /*
     *  Swing Parser Callback from the HTMLEditorKit.
     * This class handles all SAX-like events during parsing.
     *
     */
    class Callback extends HTMLEditorKit.ParserCallback
    {


        // either handling of <FORM> is buggy, or I made some weird mistake ... 
        // ... JDK 1.3 sends double "</form>"-tags on closing <form>
        private boolean inForm = false; 
        private boolean inScript = false; 
        private boolean emit = true;
        private boolean simpleTag = false;

        private StringWriter result = new StringWriter();

        private Callback () 
        {
        }

        //
        // -------------- Hot Java event callbacks... --------------------
        //

        /*
         *  Hot Java event callback for text (all data in between tags)
         * 
         * @param values The array of characters containing the text.
         */
        public void handleText(char[] values,int param) 
        {
             if (false == emit)                               
                 return;                                      
             if (values[0] == '>')                            
                 return;     
             if (false == rewriter.enterText(values, param))
                return;                    

            addToResult(values);
        }

        /*
         * Hot Java event callback for handling a simple tag (without begin/end)
         *
         * @param tag The HTML tag being handled.
         * @param attrs The mutable HTML attribute set for the current HTML element.         
         * @param position the position of the tag.         
         *
         */
        public void handleSimpleTag(HTML.Tag tag,MutableAttributeSet attrs,int param) 
        {
            simpleTag = true;
            if (false == rewriter.enterSimpleTagEvent(tag, attrs))
                return;

            if (false == isValidFragmentTag(tag))
                return;

            appendTagToResult(tag,attrs);        
            if (tag.toString().equalsIgnoreCase("param") ||
                tag.toString().equalsIgnoreCase("object") ||
                tag.toString().equalsIgnoreCase("embed"))
            {
                result.write(lineSeparator);
            }
            simpleTag = false;
            String appended = rewriter.exitSimpleTagEvent(tag, attrs);
            if (null != appended)
                result.write(appended);
        }

        /*
         * Hot Java event callback for handling a start tag.
         *
         * @param tag The HTML tag being handled.
         * @param attrs The mutable HTML attribute set for the current HTML element.         
         * @param position the position of the tag.         
         *
         */
        public void handleStartTag(HTML.Tag tag,  MutableAttributeSet attrs, int position) 
        {
            if (false == rewriter.enterStartTagEvent(tag, attrs))
                return;

            if (tag == HTML.Tag.HEAD)
            {
                emit = false;
                return;
            }

           if (false == isValidFragmentTag(tag))
                return;

            appendTagToResult(tag,attrs);
            formatLine(tag);
            String appended = rewriter.exitStartTagEvent(tag, attrs);
            if (null != appended)
                result.write(appended);
        }


        boolean isValidFragmentTag(HTML.Tag tag)
        {                    
            /*
            if (false == emit)
                return false;

            if (tag == HTML.Tag.HTML)  // always strip out HTML tag for fragments
                return false;

            if (tag == HTML.Tag.BODY)
                return false;

            if (tag == HTML.Tag.FRAMESET)  // always strip out FRAMESET tag for fragments
                return false;

            if (tag == HTML.Tag.FRAME)  
                return false;

            if (tag == HTML.Tag.NOFRAMES)  
                return false;
              */
            return true;
        }


        /*
         * Hot Java event callback for handling an end tag.
         *
         * @param tag The HTML tag being handled.
         * @param position the position of the tag.
         *
         */
        public void handleEndTag(HTML.Tag tag, int position) 
        {
            if (false == rewriter.enterEndTagEvent(tag))
                return;

            if (tag == HTML.Tag.HEAD)
            {
                emit = true;
                return;
            }

           if (false == isValidFragmentTag(tag))
                return;

           addToResult("</").addToResult(tag).addToResult(">");

            formatLine(tag);
            String appended = rewriter.exitEndTagEvent(tag);
            if (null != appended)
                result.write(appended);

        }


        /*
         * Hot Java event callback for handling errors.
         *
         * @param str The error message from Swing.
         * @param param A parameter passed to handler.
         *
         */
        public void handleError(java.lang.String str,int param) 
        {
            // ignored
        }

        /*
         * Hot Java event callback for HTML comments.
         *
         * @param values The character array of text comments.
         * @param param A parameter passed to handler.
         *
         */
        public void handleComment(char[] values,int param) 
        {
            // STRIP COMMENTS: addToResult(values);
            // this is questionable, we may need to turn this on for scripts inside comments
        }

        /*
         * Hot Java event callback for end of line strings.
         *
         * @param str The end-of-line string.
         *
         */
        public void handleEndOfLineString(java.lang.String str) 
        {
            addToResult(str);
        }


        /*
         * Prints new lines to make the output a little easier to read when debugging.
         *
         * @param tag The HTML tag being handled.         
         *
         */
        private void formatLine(HTML.Tag tag)
        {
            if (tag.isBlock() || 
                tag.breaksFlow() || 
                tag == HTML.Tag.FRAME ||
                tag == HTML.Tag.FRAMESET ||
                tag == HTML.Tag.SCRIPT)
            {
                result.write(lineSeparator);
            }
        }


        /*
         * Used to write tag and attribute objects to the output stream.
         * Returns a reference to itself so that these calls can be chained.
         *
         * @param txt Any text to be written out to stream with toString method.
         *            The object being written should implement its toString method.
         * @return A handle to the this, the callback, for chaining results.
         *
         */
        private Callback addToResult(Object txt)
        {
            // to allow for implementation using Stringbuffer or StringWriter
            // I don't know yet, which one is better in this case
            //if (ignoreLevel > 0 ) return this;

            try
            {
                result.write(txt.toString());
            } catch (Exception e)
            {
                System.err.println("Error parsing:" + e);
            }
            return this;
        }


        /*
         * Used to write all character content to the output stream.
         * Returns a reference to itself so that these calls can be chained.
         *
         * @param txt Any character text to be written out directly to stream.
         * @return A handle to the this, the callback, for chaining results.
         *
         */
        private Callback addToResult(char[] txt)
        {
            //if (ignoreLevel > 0) return this;

            try
            {

                result.write(txt);

            } catch (Exception e)
            { /* ignore */
            }
            return this;
        }

        /* 
         * Accessor to the Callback's content-String
         *
         * @return Cleaned and rewritten HTML-Content
         */        
        public String getResult() 
        {
            try
            {
                result.flush();
            } catch (Exception e)
            { /* ignore */
            }

            // WARNING: doesn't work, if you remove " " + ... but don't know why
            String res = " " + result.toString(); 

            return res;
        }

        /*
         * Flushes the output stream. NOT IMPLEMENTED
         *
         */
        public void flush() throws javax.swing.text.BadLocationException 
        {
            // nothing to do here ...
        }

        /*
         * Writes output to the final stream for all attributes of a given tag.
         *
         * @param tag The HTML tag being output.
         * @param attrs The mutable HTML attribute set for the current HTML tag.
         *
         */
        private void appendTagToResult(HTML.Tag tag, MutableAttributeSet attrs) 
        {
            convertURLS(tag,attrs);
            Enumeration e = attrs.getAttributeNames();
            addToResult("<").addToResult(tag);
            while (e.hasMoreElements())
            {
                Object attr = e.nextElement();
                String value = attrs.getAttribute(attr).toString();
                addToResult(" ").addToResult(attr).addToResult("=\"").
                addToResult(value).addToResult("\"");
            }        
            if (simpleTag)
                addToResult("/>");
            else             
                addToResult(">");
        }


        /*
         * Determines which HTML Tag/Element is being inspected, and calls the 
         * appropriate converter for that context.  This method contains all the
         * logic for determining how tags are rewritten. 
         *
         * TODO: it would be better to drive this logic off a state table that is not
         * tied to the Hot Java parser.
         *
         * @param tag TAG from the Callback-Interface.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         */

        private void convertURLS( HTML.Tag tag, MutableAttributeSet attrs ) 
        {
            rewriter.convertTagEvent(tag, attrs);
            if ((tag == HTML.Tag.A) && 
                (attrs.getAttribute(HTML.Attribute.HREF) != null))
            {

                // ---- CHECKING <A HREF
                addProxiedConvertedAttribute( tag, HTML.Attribute.HREF, attrs);

            } 
            else if (((tag == HTML.Tag.IMG || 
                         tag == HTML.Tag.INPUT
                        ) && 
                        (attrs.getAttribute(HTML.Attribute.SRC) != null)
                       ))
            {

                // ---- CHECKING <IMG SRC & <INPUT SRC
                addConvertedAttribute( tag,
                                       HTML.Attribute.SRC, 
                                       attrs, 
                                       rewriter.proxyAllTags());    

            } else if (((tag == HTML.Tag.OPTION) ) && 
                       (attrs.getAttribute(HTML.Attribute.VALUE) != null))
            {
                // ---- CHECKING <OPTION 
                addProxiedConvertedAttribute( tag, HTML.Attribute.VALUE, attrs );

            } else if (((tag == HTML.Tag.LINK) ) && 
                       (attrs.getAttribute(HTML.Attribute.HREF) != null))
            {

                // ---- CHECKING <LINK
                addConvertedAttribute( tag,
                                       HTML.Attribute.HREF,
                                       attrs,
                                       rewriter.proxyAllTags());

            } else if ( tag == HTML.Tag.APPLET )
            {

                // ---- CHECKING <APPLET CODEBASE=
                addConvertedAttribute( tag,
                                       HTML.Attribute.CODEBASE,
                                       attrs,
                                       rewriter.proxyAllTags());

            } else if ( tag == HTML.Tag.FRAME )
            {

                // ---- CHECKING <FRAME SRC=
                addProxiedConvertedAttribute( tag, HTML.Attribute.SRC, attrs);

            } else if ( tag == HTML.Tag.SCRIPT )
            {
                // ---- CHECKING <SCRIPT SRC=
                if (attrs.getAttribute(HTML.Attribute.SRC) != null)
                {

                    // script is external
                    String s = attrs.getAttribute(HTML.Attribute.SRC).toString();
                    if (s.indexOf("%3E") == -1)
                    {
                        addConvertedAttribute( tag,
                                               HTML.Attribute.SRC, 
                                               attrs,
                                               rewriter.proxyAllTags());
                    }

                } else
                {
                    // script is inline
                    //parserOff = true;
                }

            } else if (tag == HTML.Tag.FORM)
            {

                // ---- CHECKING <FORM ACTION=
                inForm = true; // buggy <form> handling in jdk 1.3 

                if (attrs.getAttribute(HTML.Attribute.ACTION) == null)
                {
                    // always post
                    attrs.addAttribute(HTML.Attribute.METHOD, "POST");                      
                    //self referencing <FORM>
                    
                    // attrs.addAttribute(HTML.Attribute.ACTION,
                    //                   baseURL);

                } else
                {
                    // always post
                    attrs.addAttribute(HTML.Attribute.METHOD, "POST");                      
                    addProxiedConvertedAttribute( tag, HTML.Attribute.ACTION, attrs);

                }

            } else if (((tag == HTML.Tag.AREA) ) && 
                       (attrs.getAttribute(HTML.Attribute.HREF) != null))
            {

                // ---- CHECKING <AREA
                addProxiedConvertedAttribute( tag, HTML.Attribute.HREF,
                                              attrs );

            } else if (((tag == HTML.Tag.BODY) ) && 
                       (attrs.getAttribute(HTML.Attribute.BACKGROUND) != null))
            {

                // ---- CHECKING <BODY
                addConvertedAttribute( tag,
                                       HTML.Attribute.BACKGROUND,
                                       attrs,
                                       rewriter.proxyAllTags());

            } else if (tag == HTML.Tag.TD)
            {
                // ---- CHECKING <TD BACKGROUND=
                if (! (attrs.getAttribute(HTML.Attribute.BACKGROUND) == null))
                {
                    addConvertedAttribute( tag,
                                           HTML.Attribute.BACKGROUND,
                                           attrs,
                                           rewriter.proxyAllTags());
                }
            }

            /*
              if ( removeScript && (tag == HTML.Tag.SCRIPT)) {
                ignoreLevel ++;
              */
        }

        /*
         * Converts the given attribute's URL compatible element to a proxied URL.
         * Uses the proxy parameter to determine if the URL should be written back as a
         * proxied URL, or as a fullpath to the original host.
         *
         * @param attr The HTML attribute to be proxied.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         * @param proxy If set true, the URL is written back as a proxied URL, otherwise
         * it is written back as a fullpath back to the original host.
         *
         */
        private void addConvertedAttribute( HTML.Tag tag,
                                            HTML.Attribute attr,
                                            MutableAttributeSet attrs,
                                            boolean proxy ) 
        {
            if (proxy)
            {
                addProxiedConvertedAttribute(tag, attr,attrs);
            } else
            {
                if ( attrs.getAttribute( attr ) != null )
                {
                    attrs.addAttribute( attr,
                                        generateNewUrl( tag, attrs, attr, false ) );
                }
            }
        }


        /**
         *
         * Converts the given attribute's URL compatible element to a proxied URL.
         * This method will always add the proxy host prefix to the rewritten URL.
         *
         * @param attr The HTML attribute to be proxied.
         * @param attrs The mutable HTML attribute set for the current HTML element.
         *
         */
        private void addProxiedConvertedAttribute( HTML.Tag tag,
                                                   HTML.Attribute attr,
                                                   MutableAttributeSet attrs ) {



            if ( attrs.getAttribute( attr ) != null )
            {
                String attrSource =  attrs.getAttribute( attr ).toString();

                // special case: mailto should not be sent to the proxy server
                if (attrSource.startsWith("mailto:"))
                {
                    attrs.addAttribute( attr,
                                        generateNewUrl( tag, attrs, attr, true ) );
                } else if (attrSource.startsWith("javascript:"))
                {
                    attrs.addAttribute( attr,
                                        attrSource);
                } else
                {
                    attrs.addAttribute( attr,
                                        generateNewUrl( tag, attrs, attr, true ) );
                }
            }
        }

        /*
         * Calls the rewriter's URL generator callback, which will translate the old url
         * into a new fullpath URL, either relative to the proxy server, or a fullpath
         * to the original web server, depending on the 'proxied' parameter.
         * 
         * @param oldURL The original URL, before it is tranlated.
         * @param proxied Boolean indicator denotes if the URL should be written back
         *        as a proxied URL (true), or as a fully addressable address to the 
         *       original web server.
         * @return The translated new URL.
         *         
         */
        private String generateNewUrl(HTML.Tag tag,
                                      MutableAttributeSet attrs,
                                      HTML.Attribute attr,
                                      boolean proxied)
        {
            String oldURL =  attrs.getAttribute( attr ).toString();
            // System.out.println("Generating new url: " + oldURL);
            return rewriter.generateNewUrl(oldURL, tag, attr);
        }


    }

}


