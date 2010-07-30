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

package org.apache.jetspeed.services;

//standard java stuff
import java.io.Reader;

// jetspeed stuff
import org.apache.jetspeed.services.transformer.TransformerService;

// turbine stuff
import org.apache.turbine.services.TurbineServices;


/**
 * <P>This is a commodity static accessor class around the 
 * <code>TransformerService</code></P>
 * 
 * @see org.apache.jetspeed.services.transformer.TransformerService
 * @author <a href="mailto:mmari@ce.unipr.it">Marco Mari</a>
 * @version $Id: Transformer.java,v 1.2 2004/02/23 04:00:57 jford Exp $ 
 */
public class Transformer {
 
    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static TransformerService getService() 
    {
        return (TransformerService)TurbineServices
                .getInstance()
                .getService(TransformerService.SERVICE_NAME);     
    }
    
    /**
     * @see org.apache.jetspeed.services.transformer.TransformerService#findElement
     */
    public static String findElement(Reader htmlReader, String url, String element)  
    {
        return getService().findElement(htmlReader, url, element);
    }

    /**
     * @see org.apache.jetspeed.services.transformer.TransformerService#clipElements
     */
    public static String clipElements(Reader htmlReader, 
                                       String url, 
                                       String startElement, 
                                       String stopElement)
    {
        return getService().clipElements(htmlReader, url, startElement, stopElement);
    }

    /**
     * @see org.apache.jetspeed.services.transformer.TransformerService#findElementNumber
     */
    public static String findElementNumber(Reader htmlReader, 
                                            String url, 
                                            String element, 
                                            int    tagNumber)  
    {
        return getService().findElementNumber(htmlReader, url, element, tagNumber);
    }

     /**
     * @see org.apache.jetspeed.services.transformer.TransformerService#clipElementsNumber
     */
    public static String clipElementsNumber(Reader htmlReader, 
                                             String url, 
                                             String startElement, 
                                             String stopElement,
                                             int    tagNumber)
    {
        return getService().clipElementsNumber(htmlReader, 
                                                url, 
                                                startElement, 
                                                stopElement, 
                                                tagNumber);
    }
}
