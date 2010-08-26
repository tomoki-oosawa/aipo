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

// turbine stuff
import java.io.Reader;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.util.rewriter.ClipperRewriter;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.Log;

/**
 * Simple implementation of the TransformerService.
 * @author <a href="mailto:mmari@ce.unipr.it">Marco Mari</a>
 * @version $Id: JetspeedTransformerService.java,v 1.2 2004/02/23 03:39:10 jford Exp $* 
 */
public class JetspeedTransformerService
	extends TurbineBaseService
	implements TransformerService
{
	protected ClipperRewriter rewriter = null;
	String convertedString;

	/**
	 * This is the early initialization method called by the 
	 * Turbine <code>Service</code> framework
	 * @param conf The <code>ServletConfig</code>
	 */
	public void init(ServletConfig conf)
	{
		// controls if already initialized
		if (getInit())
		{
			return;
		}

		rewriter = new ClipperRewriter();
		setInit(true);
	}

	/**
	 * Late init. Don't return control until early init says we're done.
	 */
	public void init()
	{
		while (!getInit())
		{
			try
			{
				Thread.sleep(500);
				Log.info("JetspeedTransformerService: Waiting for init()...");
			}
			catch (InterruptedException ie)
			{
				Log.error(ie);
			}
		}
	}

	/**
	 * Finds an element in a web page
	 * 
	 * @param htmlReader Reader for the html rewriter
	 * @param url        page address
	 * @param element    a part of the element to search
	 */
	public String findElement(Reader htmlReader, String url, String element)
	{
		// If not indicated, assume to find the first element
		return clipElementsNumber(htmlReader, url, element, null, 1);
	}

	/**
	 * Clips the part of a web page between startElement and stopElement
	 * 
	 * @param htmlReader    Reader for the html rewriter
	 * @param url           page address
	 * @param startElement  the first element to clip
	 * @param lastElement   the last element to clip
	 */
	public String clipElements(
		Reader htmlReader,
		String url,
		String startElement,
		String stopElement)
	{
		// If not indicated, assume to find the first startElement
		return clipElementsNumber(
			htmlReader,
			url,
			startElement,
			stopElement,
			1);
	}

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
		int tagNumber)
	{
		return clipElementsNumber(htmlReader, url, element, null, tagNumber);
	}

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
		int tagNumber)
	{
		rewriter.setStartElement(startElement);
		rewriter.setStopElement(stopElement);
		rewriter.setStartElementNumber(tagNumber);

		try
		{
			convertedString = rewriter.rewrite(htmlReader, url);
		}
		catch (Exception e)
		{
			Log.info("Exception occurred:" + e.toString());
			e.printStackTrace();
		}

		return convertedString;
	}

}
