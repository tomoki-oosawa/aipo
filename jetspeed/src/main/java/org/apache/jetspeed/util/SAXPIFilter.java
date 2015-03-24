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

package org.apache.jetspeed.util;

//standard java stuff
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

// SAX classes
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserFactory;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
This class is used to either strip and/or insert PIs in a XML document.
It uses SAX-1 API and outputs text to an
output stream.
WARNING: This behavior will be modified in the future.

@author <A HREF="mailto:raphael@apache.org">Rapha謖 Luta</A>
@version $Id: SAXPIFilter.java,v 1.8 2004/02/23 03:23:42 jford Exp $
*/
public class SAXPIFilter extends HandlerBase 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SAXPIFilter.class.getName());
    
    private static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    protected PrintWriter out=new PrintWriter(System.out);

    private String pi;

    private boolean stripExistingPI;

    private Vector pis = new Vector();
    
    /**
    Creates of simple parser which outputs its document to the PrintWriter passed
    as arguments.
    In this mode the parser may be used as a simple well-formedness checker.

    @param outPW the printWriter where to output parsed data
    */
    public SAXPIFilter(PrintWriter outPW) throws UnsupportedEncodingException {
        this(outPW,false,null);
    }

    /**
    In this mode the parser may be used as a simple well-formedness checker or a PI
    stripper.

    @param outPW the printWriter where to output parsed data
    @param strip configure the parser to strip PIs if strip is true
    */
    public SAXPIFilter(PrintWriter outPW, boolean strip) throws UnsupportedEncodingException {
        this( outPW, strip, null );
    }

    /**
    In this mode the parser can strip existing PIs and insert new ones just after
    the document declaration

    @param outPW the printWriter where to output parsed data
    @param strip configure the parser to strip PIs if strip is true
    @param PI string reprensenting the PI to be output after the document declaration
    */
    public SAXPIFilter(PrintWriter outPW, boolean strip, String PI) throws UnsupportedEncodingException {
        this.out=outPW;
        this.stripExistingPI=strip;
        this.pi=PI;
    }

    /**
    Get all permitted processing instructions
    */
    public String[] getProcessingInstructions() {
        
        String[] ins = new String[ pis.size() ];
        pis.copyInto( ins );
        return ins;
        
    }
    
    public void addProcessingInstruction(String pi) {
        this.pis.addElement( pi );
    }


    /**
    Parse and output the content of the URL given as parameter.

    @param uri URL where to fetch the document to be parsed
    */
    public void print(String uri) {

        try {
            HandlerBase handler = this;

            Parser parser = ParserFactory.makeParser(DEFAULT_PARSER_NAME);
            parser.setDocumentHandler(handler);
            parser.setErrorHandler(handler);
            parser.parse(uri);
        }
        catch (Exception e) {
            logger.error("Exception",  e);
        }

    }

    /**
    Parse and output the content of the stream

    @param in a content InputStream
    */
    public void print(InputStream in) {

        try {
            HandlerBase handler = this;

            Parser parser = ParserFactory.makeParser(DEFAULT_PARSER_NAME);
            parser.setDocumentHandler(handler);
            parser.setErrorHandler(handler);
            parser.parse(new InputSource(in));
        }
        catch (Exception e) {
            logger.error("Exception",  e);
        }

    }

    /**
    Parse and output the content of the reader

    @param in a content Reader
    */
    public void print(Reader in) {

        try {
            HandlerBase handler = this;

            Parser parser = ParserFactory.makeParser(DEFAULT_PARSER_NAME);
            parser.setDocumentHandler(handler);
            parser.setErrorHandler(handler);
            parser.parse(new InputSource(in));
        }
        catch (Exception e) {
            logger.error("Exception",  e);
        }

    }

    /**
    SAX Handler implementation
    */
    public void processingInstruction(String target, String data) {

        
        if ( ! stripExistingPI ) {
            out.print( makeSAXPI( target, data ) );
        } else {
            //cocoon-process            
            //if your original XML document has cocoon tags leave them in.

            //add exceptable processing instructions here.
            if ( target.equals("cocoon-process") ) {
                
                this.addProcessingInstruction( makeSAXPI( target, data ) );

           }
            
        }

    }

    private String makeSAXPI( String target, String data ) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?");
        buffer.append( target );
        if (data != null && data.length() > 0) {
            buffer.append(' ');
            buffer.append(data);
        }
        buffer.append("?>");
        
        return buffer.toString();
    }
    
   

    /**
    SAX Handler implementation
    */
    public void startDocument() {

        if ( pi != null ) {
            out.print( pi );
        }

    }

    /**
    SAX Handler implementation
    */
    public void startElement(String name, AttributeList attrs) {

        out.print('<');
        out.print(name);
        if (attrs != null) {
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                out.print(' ');
                out.print(attrs.getName(i));
                out.print("=\"");
                out.print(normalize(attrs.getValue(i)));
                out.print('"');
            }
        }
        out.print('>');

    }

    /**
    SAX Handler implementation
    */
    public void characters(char ch[], int start, int length) {

        out.print(normalize(new String(ch, start, length)));

    }

    /**
    SAX Handler implementation
    */
    public void ignorableWhitespace(char ch[], int start, int length) {

        characters(ch, start, length);

    }

    /**
    SAX Handler implementation
    */
    public void endElement(String name) {

        out.print("</");
        out.print(name);
        out.print('>');

    }

    /**
    SAX Handler implementation
    */
    public void endDocument() {

        out.flush();

    }

    /**
    SAX Handler implementation
    */
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }

    /**
    SAX Handler implementation
    */
    public void error(SAXParseException ex) {
        System.err.println("[Error] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }

    /**
    SAX Handler implementation
    */
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
        throw ex;
    }

    /**
    Retrieves the error location in the input stream
    */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();

    }

    /**
    Escapes characters data
    */
    protected String normalize(String s) {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    str.append("&lt;");
                    break;
                }
                case '>': {
                    str.append("&gt;");
                    break;
                }
                case '&': {
                    str.append("&amp;");
                    break;
                }
                case '"': {
                    str.append("&quot;");
                    break;
                }
                default: {
                    str.append(ch);
                }
            }
        }

        return str.toString();

    }

}
