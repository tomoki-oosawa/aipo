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

package org.apache.jetspeed.util.servlet;

// Java classes
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;

// SAX Classes
import org.xml.sax.InputSource;

// ECS classes
import org.apache.ecs.ConcreteElement;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.SimpleTransform;

/**
 * NOTE: The use of Ecs for aggregating portlet content is deprecated!
 *       This utility class will be removed once we don't have the ecs 
 *       dependency any more.
 *
 * EcsStylesheetElement encapsulates XML data, a stylesheet and the parameters for 
 * processing the XML data within the context of ECS markup. 
 * 
 * This is a workaround to allow invoking stylesheets from JetSpeed Portlets without
 * buffering strings with the transformation results. Transformation is invoked when
 * traversal of an ECS tree during writing reaches the EcsStylesheetElement.
 *
 * @author Thomas Schaeck (schaeck@de.ibm.com) 
 */
public class EcsStylesheetElement extends ConcreteElement 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(EcsStylesheetElement.class.getName());
    
    /**
     * Processes the referenced XML content using the referenced XSLT stylesheet and 
     * parameters.
     *
     * @param out The output stream to which the result shall be written.
     */
     public void output(OutputStream out)
     {
        output(new PrintWriter(out));
     }               

    /**
     * Processes the referenced XML content using the referenced XSLT stylesheet and 
     * parameters.
     *
     * @param out The print writer to be used for writing the result.
     */
    public void output(PrintWriter out)
    {
        try {

            StringReader rdr = new StringReader (SimpleTransform.transform( content_, stylesheet_, params_ ) );
            int count = 0;
            char buff[] = new char[1024];
            while( (count = rdr.read( buff, 0, buff.length ) ) > 0 ) {
                out.write( buff, 0, count );
                }

        /*    // Get a new XSLT Processor 
            XSLTProcessor processor = XSLTProcessorFactory.getProcessor();

            // set the parameters for the stylesheet
            if (params_ != null) 
            {
                Enumeration keys = params_.keys();
                while (keys.hasMoreElements()) 
                {
                    String name = (String) keys.nextElement();
                    processor.setStylesheetParam(name, (String) params_.get(name));
                }
            }

            //  process the stylesheet
            processor.process( content_, stylesheet_, new XSLTResultTarget(out) ); */

            } catch (Exception e) 
            {
                String message = "ECSStylesheetElement.output(PrintWriter): error processing stylesheet" + e.getMessage(); 
                logger.error(message, e);
                out.print(message);
                e.printStackTrace(out);
            }
    }   


    /** XML content to be processed. */
    private InputSource content_;

    /** Parameters to be used by the stylesheet. */
    private Map params_;

    /** XSLT stylesheet to be used for rendering the content. */
    private InputSource stylesheet_;

    /**
     * Construct an ECS element that will render a given XML dicument using a given 
     * stylesheet and parameters when one of its output methods is invoked.
     *
     * @param content    XML content to be processed
     * @param stylesheet XSLT stylesheet to be used for processing the content
     * @param params	 parameters for the stylesheet
     */
    public EcsStylesheetElement( InputSource content, 
                                 InputSource stylesheet,
                                 Map params ) 
    {
        content_ = content;
        stylesheet_ = stylesheet;
        params_ = params;

    }

}
