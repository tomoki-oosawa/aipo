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

//java stuff
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

//Trax support
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Templates;
//import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;              

//xpath objects
import org.apache.xpath.objects.XString;

//SAX Suport
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.InputSource;

//DOM Support
import org.w3c.dom.Document;

//Jetspeed stuff
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.xml.JetspeedXMLEntityResolver;




/**
 * Provides a very simple mechanism to transform a document using XSLT using 
 * one XML document and another XSL document.  This implementation uses the TRAX API.
 * It can be used with any TRAX transformer.   This can be used for very 
 * simple XML -> XSL processing to reduce the complexity and possibility of a 
 * runtime failure.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id: SimpleTransform.java,v 1.23 2004/02/23 03:23:42 jford Exp $
 */
public class SimpleTransform
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SimpleTransform.class.getName());
    
    //FIXME: This code should go into the Turbine XSLTservice.
    //Also, it is a temporary hack, as it should be configurable,
    // and done later.
    static
    {
        try 
        {
            if( System.getProperty( "org.xml.sax.driver" ) == null )
            {
                System.setProperty( "org.xml.sax.driver",
                                    "org.apache.xerces.parsers.SAXParser" );
            }
        }
        catch (Throwable t)
        {
            //be very cautious here. We are in class initialization.
            t.printStackTrace();
        }
    }
    
    /**
     * Given a a DOM and a URL to a stylesheet,
     * transform the original document.
     */
    public static String transform( Document doc,
                                    String stylesheet_url)
        throws SAXException
    {
        return transform( doc, stylesheet_url, null );
    }

    
    /**
     * Given a a DOM and a URL to a stylesheet,
     * transform the original document,
     * passing parameters to the stylesheet
     */
    public static String transform( Document doc,
                                    String stylesheet_url,
                                    Map params)
        throws SAXException
    {

        // Instantiate a TransformerFactory.
        TransformerFactory tFactory = TransformerFactory.newInstance();
        // Determine whether the TransformerFactory supports the use of SAXSource 
        // and SAXResult
        if (!tFactory.getFeature(SAXTransformerFactory.FEATURE) )
        {
            logger.error( "SimpleTransform: nobody told you that we need a SAX Transformer?" );
            throw new SAXException( "Invalid SAX Tranformer" );
        }
        try
        {
            // Cast the TransformerFactory.
            SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
            // Create a ContentHandler to handle parsing of the stylesheet.
            TemplatesHandler templatesHandler = saxTFactory.newTemplatesHandler();

            // Create an XMLReader and set its ContentHandler.
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);
            // Set it to solve Entities through Jetspeed URL Manager
            reader.setEntityResolver( new JetspeedXMLEntityResolver() );
    
            // Parse the stylesheet.                       
            final InputSource xstyle = new InputSource( JetspeedDiskCache.getInstance()
                                                        .getEntry( stylesheet_url ).getReader() );
            xstyle.setSystemId( stylesheet_url );
            reader.parse( xstyle );

            //Get the Templates object from the ContentHandler.
            Templates templates = templatesHandler.getTemplates();
            // Create a ContentHandler to handle parsing of the XML source.  
            TransformerHandler handler 
                = saxTFactory.newTransformerHandler(templates);
        
            // Reset the XMLReader's ContentHandler.
            reader.setContentHandler(handler);  

            // Set the ContentHandler to also function as a LexicalHandler, which
            // includes "lexical" events (e.g., comments and CDATA).
            try
            { 
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
            } 
            catch( org.xml.sax.SAXNotRecognizedException e ) {}

            final Transformer processor = handler.getTransformer();

            if( params != null ) {
                Iterator keys = params.keySet().iterator();
                while( keys.hasNext() )
                {
                    String name  = (String) keys.next();
                    String value = (String) params.get(name);
                    processor.setParameter(name, 
                                           value ); //FIXME: maybe we need to quote again...
                    // was processor.createXString( value)
                }
            }
        
            StringWriter pw = new StringWriter();
        
            // Have the XSLTProcessor processor object transform "foo.xml" to
            // System.out, using the XSLT instructions found in "foo.xsl".
            processor.transform( new DOMSource( doc ),
                                 new StreamResult( pw ) );
                      
            try
            {

                pw.flush();
                pw.close();
            
            } 
            catch (IOException e)
            {
                //should never really happen
                logger.error("Exception",  e);
            }
            return pw.toString();
        } 
        catch (Exception e)
        {
            logger.error( "Invalid SAX Transformer: " , e );
            throw new SAXException( "problem in SAX transform: " + e.toString() );
        }    
    }
    
    /**
     * Given a URL to an XML file and a URL to a stylesheet, transform the 
     * original document.
     */
    public static String transform( String url,
                                    String stylesheet_url )
        throws SAXException
    {

        return transform( url, stylesheet_url, null );
    
    }
    
    /**
     * Given a URL to an XML file and a URL to a stylesheet, transform the 
     * original document.
     */
    public static String transform( String url,
                                    String stylesheet_url,
                                    Map params )
        throws SAXException
    {

        //bring these URLs local if they happen to be remote

        InputSource in;
        InputSource style;
        try
        {
            in = new InputSource( JetspeedDiskCache.getInstance().getEntry( url ).getReader() );
            style = new InputSource( JetspeedDiskCache.getInstance().getEntry( stylesheet_url ).getReader() );
        } 
        catch (IOException e)
        {
            logger.error("Exception",  e);
            //at this point we can just use the original url and stylesheet_url so this shouldn't be a problem
            in = new InputSource( url ); 
            style = new InputSource( stylesheet_url );
        }
        
        if ( logger.isInfoEnabled() )
        {
            logger.info( "SimpleTransform:  transforming url: " + 
                  url + 
                  " with stylesheet: " + 
                  stylesheet_url );
        }

        in.setSystemId( url );
        style.setSystemId( stylesheet_url );

        return transform( in,
                          style,
                          params );
    
    }
    
    /**
     * Used internally to handle doing XSLT transformations directly.
     */
    public static String transform( InputSource content, 
                                    InputSource stylesheet,
                                    Map params)
        throws SAXException
    {

        // Instantiate a TransformerFactory.
        TransformerFactory tFactory = TransformerFactory.newInstance();
        // Determine whether the TransformerFactory supports the use of SAXSource 
        // and SAXResult
        if (!tFactory.getFeature(SAXTransformerFactory.FEATURE) )
        {
            logger.error( "SimpleTransform: nobody told you that we need a SAX Transformer?" );
            throw new SAXException( "Invalid SAX Tranformer" );
        }
        try
        {
            // Cast the TransformerFactory.
            SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
            // Create a ContentHandler to handle parsing of the stylesheet.
            TemplatesHandler templatesHandler = saxTFactory.newTemplatesHandler();

            // Create an XMLReader and set its ContentHandler.
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);
    
            // Parse the stylesheet.                       
            reader.parse( stylesheet );

            //Get the Templates object from the ContentHandler.
            Templates templates = templatesHandler.getTemplates();
            // Create a ContentHandler to handle parsing of the XML source.  
            TransformerHandler handler 
                = saxTFactory.newTransformerHandler(templates);
        
            // Reset the XMLReader's ContentHandler.
            reader.setContentHandler(handler);  

            // Set the ContentHandler to also function as a LexicalHandler, which
            // includes "lexical" events (e.g., comments and CDATA). 
            try
            {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
            } 
            catch( org.xml.sax.SAXNotRecognizedException e ) {}
            
            final Transformer processor = handler.getTransformer();

        
            if( params != null )
            {
                Iterator keys = params.keySet().iterator();
                while( keys.hasNext() )
                {
                    String name  = (String) keys.next();
                    String value = (String) params.get(name);
                    processor.setParameter(name, 
                                           new XString( value ) 
                         /*FIXME: was processor.createXString( value) */ );
                }
            }

            StringWriter pw = new StringWriter();
        
            // Have the XSLTProcessor processor object transform "foo.xml" to
            // System.out, using the XSLT instructions found in "foo.xsl".
            processor.transform( new SAXSource( content ),
                                 new StreamResult( pw ) );
                      
            try
            {
                pw.flush();
                pw.close();
            } 
            catch (IOException e)
            {
                //should never really happen
                logger.error("Exception",  e);
            }
            return pw.toString();
        }
        catch (Exception e)
        {
            logger.error( "Invalid SAX Transformer: " , e);
            throw new SAXException( "problem in SAX transform: " + e.toString() );
        }    
    }

    /**
     * Perform a event based parsing of the given content_url, 
     * process it with the XSLT stylesheet stylesheet_url, using the params
     * parameters, and return a Reader that will do the transformation dynamically.
     *
     * @param content_url The url of the xml document
     * @param stylesheet_url The url of the stylesheet
     * @param params A Map containing stylesheet parameters
     * @return a Reader on the transformed document
     *
     */
    public static Reader SAXTransform( String content_url, 
                                       String stylesheet_url,
                                       Map params) throws IOException
    {

        // Instantiate a TransformerFactory.
        TransformerFactory tFactory = TransformerFactory.newInstance();
        // Determine whether the TransformerFactory supports the use of SAXSource 
        // and SAXResult
        if (!tFactory.getFeature(SAXTransformerFactory.FEATURE) )
        {
            logger.error( "SimpleTransform: nobody told you that we need a SAX Transformer?" );
            throw new IOException( "Invalid SAX Tranformer" );
        }
        try
        {
            // Cast the TransformerFactory.
            SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
            // Create a ContentHandler to handle parsing of the stylesheet.
            TemplatesHandler templatesHandler = saxTFactory.newTemplatesHandler();

            // Create an XMLReader and set its ContentHandler.
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);
            // Set it to solve Entities through Jetspeed URL Manager
            reader.setEntityResolver( new JetspeedXMLEntityResolver() );

            // Parse the stylesheet.                       
            InputSource style = new InputSource( JetspeedDiskCache.getInstance()
                                                 .getEntry( stylesheet_url ).getReader() );
            style.setSystemId( stylesheet_url );
            final InputSource xstyle = style;

            reader.parse( xstyle );

            //Get the Templates object from the ContentHandler.
            Templates templates = templatesHandler.getTemplates();
            // Create a ContentHandler to handle parsing of the XML source.  
            TransformerHandler handler 
                = saxTFactory.newTransformerHandler(templates);
        
            // Reset the XMLReader's ContentHandler.
            reader.setContentHandler(handler);  

            // Set the ContentHandler to also function as a LexicalHandler, which
            // includes "lexical" events (e.g., comments and CDATA). 
            try
            {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
            }
            catch( org.xml.sax.SAXNotRecognizedException e ) {}

            final Transformer processor = handler.getTransformer();

            //Set the parameters (if any)
            if( params != null )
            {
                Iterator keys = params.keySet().iterator();
                while( keys.hasNext() )
                {
                    String name  = (String) keys.next();
                    String value = (String) params.get(name);
                    //FIXME: maybe we need to quote again...
                    // was processor.createXString( value)
                    processor.setParameter(name, 
                                           new XString( value ) );
                }
            }

            PipedInputStream pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream( pis );
            try
            {

                final Writer pw = new OutputStreamWriter( pos, "utf-8" );
                InputSource is = new InputSource( JetspeedDiskCache.getInstance()
                                                  .getEntry( content_url ).getReader() );
                is.setSystemId( content_url );

                final SAXSource xinput = new SAXSource( is );
                //Perform the transformation on a new thread, using
                // PipedStreams 
                Thread t = new Thread( new Runnable()
                    {
                        public void run()
                        {
                            // Have the processor object transform 
                            // "foo.xml" to
                            // System.out, using the XSLT instructions 
                            //found in "foo.xsl".
                            logger.debug("Starting SAX thread...");
                            try 
                            {
                                processor.transform( xinput,
                                                     new StreamResult( pw ) );
                                pw.close();
                                logger.debug("...ending SAX thread.");
                            } 
                            catch( Exception se)
                            {
                                logger.debug("Error in SAXTransform" + se.toString(), se );
                            }
                        }
                    } );
                t.start();
            } 
            catch (java.io.UnsupportedEncodingException uee)
            {
                logger.error("Need utf-8 encoding to SAXTransform", uee);
            }
            return new InputStreamReader ( pis, "utf-8" );
        } 
        catch (Exception e)
        {
            logger.error( "Invalid SAX Transformer:" , e);
            throw new IOException( "problem in SAX transform: " + e.toString() );
        }    
    }

}
