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

/*
 *
 *
 *  COMPATIBILITY
 *  
 *      [28.01.2001, RammerI] Tested on W2K, with J2SE, JDK 1.3
 *      [29.01.2001, RammerI] Tested on W2K, with JDK 1.2.2
 *
 *
 *
 *  FEATURES
 *      = Rewriting of <A HREFs, <IMG SRCes, <FORM ACTIONs, <TD BACKGROUNDs,
 *          <INPUT SRCs, <APPLET CODEBASEs
 *      = Removal of <SCRIPT>, <STYLE>, <HEAD>, <EMBED>, <OBJECT>, <APPLET>,
 *          <NOSCRIPT>
 * 
 ****
 * Please include the following section in the WebPagePortlet documentation     
 ****
 * <CODE>
 *
 * The following describes how HTML tags are rewritten
 *
 * <!-- --> (HTML Comments)
 *   o Unless otherwise mentioned, comments are striped.
 * 
 * <A>
 *   o HREF attribute   - URL merged with base URL (See Note 1)
 *   o TARGET attribute - Set to "_BLANK" if it does not exist 
 *                        and openInNewWindow = TRUE
 * <AREA>
 *   o HREF attribute   - URL merged with base URL (See Note 1)
 *   o TARGET attribute - Set to "_BLANK" if it does not exist 
 *                        and openInNewWindow = TRUE
 * <APPLET>
 *   o Optionally included
 *   o CODEBASE attribute - Set to the current path if it does
 *                          not exist.
 * 
 * <BASE>
 *   o <HEAD> does NOT have to be included.
 *   o HREF attribute  - Set the Base URL of the page, but the tag
 *                       not set in resulting HTML. URL merged with
 *                       base URL (See Note 1)
 * 
 * <BODY>
 *   o Background attribute - Always striped.
 * 
 * <EMBED>
 *   o May not work.  Not supported by JDK 1.3/
 * 
 * <FORM>
 *   o ACTION attribute - Set to the current URL if it does
 *                        not exist. URL merged with base
 *                        URL (See Note 1)
 * 
 * <IMG>
 *   o SRC attribute - URL merged with base URL (See Note 1)
 * 
 * <INPUT>
 *   o SRC attribute - URL merged with base URL (See Note 1)
 * 
 * <LINK>
 *   o HREF attribute - URL merged with base URL (See Note 1)
 *
 * <OBJECT>
 *   o Optionally included
 *   o CODEBASE attribute - Set to the current path if it does
 *                          not exist. URL merged with base
 *                          URL (See Note 1)
 * 
 * <SCRIPT>
 *   o Optionally included
 *   o Contents may be striped if this tag appears in the <HEAD>
 *     and the contents are NOT in a comment
 *   o SRC attribute - URL merged with base URL (See Note 1)
 *   o Script code that is NOT enclosed in a comment (<!-- -->)
 *     and in the <HEAD> may NOT be in the resulting HTML.  This
 *     is related to the HTML parser in included in the JDK 
 * 
 * <TD>
 *   o BACKGROUND attribute - URL merged with base URL (See Note 1)
 * 
 * Note 1: URL Merging.
 *   This is done because the source of the page sent to the
 *   user's browser is different then source the current page.
 *   Example:
 *     Base URL........ http://jakarta.apache.org/jetspeed
 *     URL............. logo.gif
 *     Resulting URL... http://jakarta.apache.org/jetspeed/logo.gif
 * 
 * </CODE>
 *  KNOWN PROBLEMS
 *
 *
 *  == Seems to have problems with international characters, when the web-pages
 *     are not downloaded from the original URL but taken from the cache.
 *     (To reproduce do the following
 *      1. create a new portlet from the url http://www.sycom.at/default.htm
 *      2. stop tomcat & restart tomcat
 *      3. login and customize your page to include this portlet
 *      4. everything should appear fine, the webpage will show some german 
 *         umlauts
 *      5. shutdown tomcat and restart it
 *      6. jetspeed is now taking the HTML not from www.sycom.at, but from the
 *         cache. Instead of the umlauts, you will see weird characters. 
 *
 *
 *  == Does not yet work with XHTML-Pages but only plain-old HTMLs. I.e. Closed
 *     single tags like <BR /> screw the output up.
 *      
 *
 *
 */
package org.apache.jetspeed.util;

import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.MutableAttributeSet;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 *
 * @author  Ingo Rammer (rammer@sycom.at)
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version 0.2
 */

public class HTMLRewriter 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(HTMLRewriter.class.getName());
    
    private HTMLRewriter.Callback cb = new HTMLRewriter.Callback();
    
/** Sets the parameters for the HTMLRewriter
 * @param removeScript Shall SCRIPT-Tags and their content be removed
 * @param removeStyle Shall STYLE-Tags and their content be removed
 * @param removeNoScript Shall NOSCRIPT-Tags and their content be removed
 * @param removeMeta Shall META-Tags be removed
 * @param removeApplet Shall APPLET-Tags and their content be removed
 * @param removeObject Shall OBJECT-Tags and their content be removed
 * @param removeHead Shall HEAD-Tags and their content be removed
 * @param removeOnSomething Shall onClick, onBlur, etc. -Attributes be removed
 */    
    public HTMLRewriter(boolean removeScript,
                        boolean removeStyle,
                        boolean removeNoScript,
                        boolean removeMeta,
                        boolean removeApplet,
                        boolean removeObject,
                        boolean removeHead,
                        boolean removeOnSomething) {
        init ( removeScript,
        removeStyle,
        removeNoScript,
        removeMeta,
        removeApplet,
        removeObject,
        removeHead,
        removeOnSomething,
        false);
    }
        
    /**
     * Sets the parameters for the HTMLRewriter
     * @param removeScript Shall SCRIPT-Tags and their content be removed
     * @param removeStyle Shall STYLE-Tags and their content be removed
     * @param removeNoScript Shall NOSCRIPT-Tags and their content be removed
     * @param removeMeta Shall META-Tags be removed
     * @param removeApplet Shall APPLET-Tags and their content be removed
     * @param removeObject Shall OBJECT-Tags and their content be removed
     * @param removeHead Shall HEAD-Tags and their content be removed
     * @param removeOnSomething Shall onClick, onBlur, etc. -Attributes be removed
     */
    public HTMLRewriter(boolean removeScript,
                        boolean removeStyle,
                        boolean removeNoScript,
                        boolean removeMeta,
                        boolean removeApplet,
                        boolean removeObject,
                        boolean removeHead,
                        boolean removeOnSomething,
                        boolean openInNewWindow ) {
        init ( removeScript,
        removeStyle,
        removeNoScript,
        removeMeta,
        removeApplet,
        removeObject,
        removeHead,
        removeOnSomething,
        openInNewWindow ); 
    }

    /**
     * Sets the parameters for the HTMLRewriter
     *
     * @param removeScript Shall SCRIPT-Tags and their content be removed
     * @param removeStyle Shall STYLE-Tags and their content be removed
     * @param removeNoScript Shall NOSCRIPT-Tags and their content be removed
     * @param removeMeta Shall META-Tags be removed
     * @param removeApplet Shall APPLET-Tags and their content be removed
     * @param removeObject Shall OBJECT-Tags and their content be removed
     * @param removeHead Shall HEAD-Tags and their content be removed
     * @param removeOnSomething Shall onClick, onBlur, etc. -Attributes be removed
     * @param openInNewWindow Shall links set Target="_blank"
     */
    private void init (boolean removeScript,
                       boolean removeStyle,
                       boolean removeNoScript,
                       boolean removeMeta,
                       boolean removeApplet,
                       boolean removeObject,
                       boolean removeHead,
                       boolean removeOnSomething,
                       boolean openInNewWindow ) 
    {
        cb.removeScript = removeScript;
        cb.removeStyle = removeStyle; 
        cb.removeNoScript = removeNoScript;
        cb.removeMeta = removeMeta;
        cb.removeApplet = removeApplet;
        cb.removeObject = removeObject;
        cb.removeHead = removeHead;
        cb.removeOnSomething = removeOnSomething;    
        cb.openInNewWindow = openInNewWindow;    
    }
    
    /**
     * Does the conversion of the HTML
     * @param HTMLrdr Reader for HTML to be converted
     * @param BaseUrl URL from which this HTML was taken. We be the base-Url
     * for all URL-rewritings.
     * @throws MalformedURLException If the BaseUrl is not a valid URL or if an URL inside
     * the document could not be converted. Should not happen
     * normally, even in badly formatted HTML.
     * @return HTML-String with rewritten URLs and removed (according
     * to constructor-settings) tags
     */
    public synchronized String convertURLs(Reader HTMLrdr, String BaseUrl) throws MalformedURLException
    {
        HTMLEditorKit.Parser parse = new HTMLRewriter.ParserGetter().getParser();        
        String res ="";
        try {
            if (cb.result != null) {
              cb.result = null;
              cb.result = new StringWriter();
            }
            cb.baseUrl = new URL(BaseUrl);
            parse.parse(HTMLrdr,cb,true);
            res = cb.getResult(); 
        } catch (Exception e)
        {
            logger.error( "Unable to convertURLS", e );
            throw new MalformedURLException(e.toString());
        }
        return res;
    }

    
    /** That Class is needed, because getParser is protected and therefore 
     *  only accessibly by a subclass
     */
    class ParserGetter extends HTMLEditorKit {
    /** This is needed, because getParser is protected
     * @return Html Parser
     */        
      public HTMLEditorKit.Parser getParser(){
        return super.getParser();
      }
    } 

    
    class Callback extends HTMLEditorKit.ParserCallback {

        // the base-url of which the given html comes from.
        private URL baseUrl;

        // either handling of <FORM> is buggy, or I made some weird mistake ... 
        // ... JDK 1.3 sends double "</form>"-tags on closing <form>
        private boolean inForm = false; 

        
        // when in multi-part ignored tags (like <script> foobar </script>, 
        // <style> foobar </style>, a counter for the nesting-level will be
        // kept here
        private int ignoreLevel = 0;
        
        private boolean removeScript = true;
        private boolean removeStyle = true; 
        private boolean removeNoScript = true;
        private boolean removeMeta = true;
        private boolean removeApplet = true;
        private boolean removeObject = true;
        private boolean removeHead = true;
        private boolean openInNewWindow = false;
        
        // remove the onClick=, onBlur=, etc. - Attributes
        private boolean removeOnSomething = true;
        
        private boolean inScript = false;
        private boolean inStyle = false;
        
        private StringWriter result = new StringWriter();
        
        private Callback () {
        }
        
        
        private Callback addToResult(Object txt)
        {
            // to allow for implementation using Stringbuffer or StringWriter
            // I don't know yet, which one is better in this case
            if (ignoreLevel > 0) return this;

            try {
                result.write(txt.toString());
            } catch (Exception e) { /* ignore */ }
            return this;
        }

        private Callback addToResult(char[] txt)
        {
            if (ignoreLevel > 0) return this;

            try {
                result.write(txt);
            } catch (Exception e) { /* ignore */ }
            return this;
        }
        
        /** Accessor to the Callback's content-String
         * @return Cleaned and rewritten HTML-Content
         */        
        public String getResult() {
            try {
                result.flush();
            } catch (Exception e) { /* ignore */ }
            
            // WARNING: doesn't work, if you remove " " + ... but don't know why
            String res = " " + result.toString(); 

            return res;
        }
        
       
        public void flush() throws javax.swing.text.BadLocationException {
            // nothing to do here ...
        }

        /** 
         * Because Scripts and Stlyle sometimes are defined in comments, thoese
         * will be written. Otherwise comments are removed
         */
        public void handleComment(char[] values,int param) {
            if ( !( inStyle || inScript))
                return;

            try {
                result.write("<!--");
                result.write(values);
                result.write("-->");
            } catch (Exception e) { /* ignore */ }
          // we ignore them 
        }

        public void handleEndOfLineString(java.lang.String str) {
            addToResult("\n");
        }

        public void handleError(java.lang.String str,int param) {
            // ignored
        }

        public void handleSimpleTag(HTML.Tag tag,MutableAttributeSet attrs,int param) {
            if (removeMeta && (tag == HTML.Tag.META)) {
                return;
            }            
            appendTagToResult(tag,attrs);        
        }

        public void handleStartTag(HTML.Tag tag,  MutableAttributeSet attrs, int position) {
            appendTagToResult(tag,attrs);
        }

        public void handleEndTag(HTML.Tag tag, int position) {
            if ((tag ==HTML.Tag.FORM) && (inForm)) { 
                // form handling seems to be buggy
                addToResult("</").addToResult(tag).addToResult(">");
                inForm = false;
            } else if (tag == HTML.Tag.FORM) {
                // do nothing! ... i.e. we are now outside of any <FORM>, so a
                // closing </form> is not really needed ... 
            } else {
                addToResult("</").addToResult(tag).addToResult(">");
            }
            
            
            if ( (removeScript == false) && (tag == HTML.Tag.SCRIPT)) {
                inScript = false;
            } else if ( (removeStyle == false) && (tag == HTML.Tag.STYLE)) {
                inStyle = false;
            }

            if ( removeScript && (tag == HTML.Tag.SCRIPT)) {
                ignoreLevel --;
            } else if ( removeStyle && (tag == HTML.Tag.STYLE)) {
                ignoreLevel --;
            } else if ( removeHead && (tag == HTML.Tag.HEAD)) {
                ignoreLevel --;
            } else if ( removeApplet && (tag == HTML.Tag.APPLET)) {
                ignoreLevel --;
            } else if ( removeObject && (tag == HTML.Tag.OBJECT)) {
                ignoreLevel --;
            } else if ( removeNoScript && (tag.toString().equalsIgnoreCase("NOSCRIPT"))) {
                ignoreLevel --;
            }
        }
  
        private void appendTagToResult(HTML.Tag tag, MutableAttributeSet attrs) {

            if (tag.toString().equalsIgnoreCase("__ENDOFLINETAG__")) {
                // jdk 1.2.2 places a tag <__ENDOFLINETAG__> in the result ...
                // we don't want this one
                return;
            }
            
            if (tag.toString().equalsIgnoreCase("__IMPLIED__")) {
                // jdk 1.3 places a tag <__IMPLIED__> in the result ...
                // we don't want this one
                return;
            }
            
            convertURLS(tag,attrs);
            Enumeration e = attrs.getAttributeNames();
            if (tag == HTML.Tag.BASE)
                return;
            
            addToResult("<").addToResult(tag);
            while (e.hasMoreElements()) {
                Object attr = e.nextElement();
                String attrName = attr.toString();
                String value = attrs.getAttribute(attr).toString();

                // include attribute only when Not(RemoveOnSomething = True and starts with "on")
                if (!(removeOnSomething
                && attrName.toLowerCase().startsWith("on")
                && (attrName.length() > 2))) {
                    // Attribute included
                    addToResult(" ").addToResult(attr).addToResult("=\"")
                    .addToResult(value).addToResult("\"");
                }
            }
            addToResult(">");
        }
                   
        /** Here the magic happens.
         *
         * If someone wants new types of URLs to be rewritten, add them here
         * @param tag TAG from the Callback-Interface
         * @param attrs Attribute-Set from the Callback-Interface
         */
        
        private void convertURLS( HTML.Tag tag, MutableAttributeSet attrs ) {

           // first we do an URL-rewrite on different tags
            
            if (tag == HTML.Tag.A) {
                if (attrs.getAttribute(HTML.Attribute.HREF) != null) {
                    // ---- CHECKING <A HREF
                    addConvertedAttribute( HTML.Attribute.HREF,
                    attrs );
                }
                if ((attrs.getAttribute(HTML.Attribute.TARGET) == null) && cb.openInNewWindow) {
                    attrs.addAttribute(HTML.Attribute.TARGET, "_BLANK");
                }
            } else if (tag == HTML.Tag.AREA) {
                if (attrs.getAttribute(HTML.Attribute.HREF) != null) {
                    // ---- CHECKING <A HREF
                    addConvertedAttribute( HTML.Attribute.HREF,
                    attrs );
                }
                if ((attrs.getAttribute(HTML.Attribute.TARGET) == null) && cb.openInNewWindow) {
                    attrs.addAttribute(HTML.Attribute.TARGET, "_BLANK");
                }
            } else if (((tag == HTML.Tag.IMG) || (tag == HTML.Tag.INPUT) || (tag == HTML.Tag.SCRIPT))
                         && (attrs.getAttribute(HTML.Attribute.SRC) != null)) {
                // ---- CHECKING <IMG SRC & <INPUT SRC
                addConvertedAttribute( HTML.Attribute.SRC,
                                       attrs );
            } else if (tag == HTML.Tag.LINK) {
                if (attrs.getAttribute(HTML.Attribute.HREF) != null) {
                    // ---- CHECKING <LINK HREF
                    addConvertedAttribute( HTML.Attribute.HREF,
                    attrs );
                }
            } else if ( tag == HTML.Tag.APPLET ) {
                // ---- CHECKING <APPLET CODEBASE=
                if (attrs.getAttribute(HTML.Attribute.CODEBASE) == null) {
                    int endOfPath = baseUrl.toString().lastIndexOf("/");
                    attrs.addAttribute(HTML.Attribute.CODEBASE, 
                                       baseUrl.toString().substring(0,endOfPath +1));
                } else {
                    addConvertedAttribute( HTML.Attribute.CODEBASE, attrs );
                }
            } else if (tag == HTML.Tag.OBJECT) {
                // ---- CHECKING <OBJECT CODEBASE=
                if (attrs.getAttribute(HTML.Attribute.CODEBASE) == null) {
                    int endOfPath = baseUrl.toString().lastIndexOf("/");
                    attrs.addAttribute(HTML.Attribute.CODEBASE, 
                                       baseUrl.toString().substring(0,endOfPath +1));
                } else {
                    addConvertedAttribute( HTML.Attribute.CODEBASE, attrs );
                }
            } else if (tag == HTML.Tag.BODY) {
                if (attrs.getAttribute(HTML.Attribute.BACKGROUND) != null) {
                    // background images are applied to the ENTIRE page, this remove them!
                    attrs.removeAttribute( HTML.Attribute.BACKGROUND);
                }
            } else if (tag == HTML.Tag.BASE) {
                if (attrs.getAttribute(HTML.Attribute.HREF) != null) {
                    try {
                        baseUrl = new URL(attrs.getAttribute(HTML.Attribute.HREF).toString());
                    } catch (Throwable t) {
                        logger.error( "HTMLRewriter: Setting BASE=" 
                        + attrs.getAttribute(HTML.Attribute.HREF).toString()
                        + t.getMessage());
                    }
                    attrs.removeAttribute(HTML.Attribute.HREF);
                }
            } else if (tag == HTML.Tag.FORM) {
                // ---- CHECKING <FORM ACTION=
                  inForm = true; // buggy <form> handling in jdk 1.3 
                  if (attrs.getAttribute(HTML.Attribute.ACTION) == null) {
                      //self referencing <FORM>
                       attrs.addAttribute(HTML.Attribute.ACTION,
                                          baseUrl.toString());
                  } else {
                        addConvertedAttribute( HTML.Attribute.ACTION,
                                               attrs );
                  }
            } else if (tag == HTML.Tag.TD) {
                // ---- CHECKING <TD BACKGROUND=
                  if (! (attrs.getAttribute(HTML.Attribute.BACKGROUND) == null)) {
                      addConvertedAttribute( HTML.Attribute.BACKGROUND,
                                             attrs );
                  }
            }

            
            // then we check for ignored tags ...
            // btw. I know, that this code could be written in a shorter way, but
            // I think it's more readable like this ...

            // don't forget to add changes to  handleEndTag() as well, else 
            // things will get screwed up!
            
            if ( (removeScript == false) && (tag == HTML.Tag.SCRIPT)) {
                inScript = true;
            } else if ( (removeStyle == false) && (tag == HTML.Tag.STYLE)) {
                inStyle = true;
            }

            if ( removeScript && (tag == HTML.Tag.SCRIPT)) {
                  ignoreLevel ++;
            } else if ( removeStyle && (tag == HTML.Tag.STYLE)) {
                  ignoreLevel ++;
            } else if ( removeHead && (tag == HTML.Tag.HEAD)) {
                  ignoreLevel ++;
            } else if ( removeApplet && (tag == HTML.Tag.APPLET)) {
                  ignoreLevel ++;
            } else if ( removeObject && (tag == HTML.Tag.OBJECT)) {
                  ignoreLevel ++;
            } else if (removeNoScript && (tag.toString().equalsIgnoreCase("NOSCRIPT"))) {
                  ignoreLevel ++;
            }
        }

        /**
         *
         * Converts the given attribute to base URL, if not null
         *
         */
        private void addConvertedAttribute( HTML.Attribute attr,
                                            MutableAttributeSet attrs ) {
            if( attrs.getAttribute( attr ) != null ) {
                String attrSource =  attrs.getAttribute( attr ).toString();
                attrs.addAttribute( attr,
                                    generateNewUrl( attrSource ) );
            }
        }
              
              
        private String generateNewUrl(String oldURL) {
            try {
                URL x = new URL(baseUrl,oldURL);
                return x.toString();
            } catch (Throwable t) {
                if (oldURL.toLowerCase().startsWith("javascript:")) {
                    return oldURL;
                }
                logger.error( "HTMLRewriter: Setting BASE="
                + baseUrl
                + " Old = "
                + oldURL
                + t.getMessage());
                return oldURL; // default behaviour ...
            }
        }

        public void handleText(char[] values,int param) {
            addToResult(values);
        }
    }
}
