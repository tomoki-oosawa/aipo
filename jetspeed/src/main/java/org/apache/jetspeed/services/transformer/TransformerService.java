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

package org.apache.jetspeed.services.transformer;

//standard java stuff
import java.io.Reader;

// turbine stuff
import org.apache.turbine.services.Service;

/**
 * This service is responsible for locate and transform HTML content
 * 
 * @author <a href="mailto:mmari@ce.unipr.it">Marco Mari</a>
 * @version $Id: TransformerService.java,v 1.2 2004/02/23 03:39:10 jford Exp $ 
 */
public interface TransformerService extends Service
{

	/** The name of this service */
	public String SERVICE_NAME = "Transformer";

	/**
	 * Finds an element in a web page
	 * 
	 * @param htmlReader Reader for the html rewriter
	 * @param url        page address
	 * @param element    the element to search
	 */
	public String findElement(Reader htmlReader, String url, String element);

	/**
	 * Clips the part of a web page between startElement and stopElement
	 * 
	 * @param htmlReader    Reader for the html rewriter
	 * @param url           page address
	 * @param startElement  the first element to clip
	 * @param stopElement   the last element to clip
	 */
	public String clipElements(
		Reader htmlReader,
		String url,
		String startElement,
		String stopElement);

	/**
	 * Finds in an HTML page the "tagNumber" tag of type "element"
	 * Example: element = "p", tagNumber = "3"
	 * Page content:
	 * <p>..</p>
	 * <p>..</p>
	 * <p>..   <---Finds this    
	 * 
	 * @param htmlReader Reader for the html rewriter
	 * @param url        page address
	 * @param element    the element to search
	 * @param tagNumber  the number of the element to search
	 */
	public String findElementNumber(
		Reader htmlReader,
		String url,
		String element,
		int tagNumber);

	/**
	 * Clips a part of a web page, starting from the "tagNumber" "startElement"
	 * Example: startElement = "p", tagNumber = "3", stopElement = "img"
	 * Page content:
	 * <p>..</p>
	 * <p>..</p>
	 * <p>..   <---Starts here
	 * ........<img>  <---Stops here
	 * 
	 * @param htmlReader    Reader for the html rewriter
	 * @param url           page address
	 * @param startElement  the first element to clip
	 * @param stopElement   the last element to clip
	 * @param tagNumber     the number of the first element to clip
	 */
	public String clipElementsNumber(
		Reader htmlReader,
		String url,
		String startElement,
		String stopElement,
		int tagNumber);
}
