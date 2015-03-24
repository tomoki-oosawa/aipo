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
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;

import org.apache.turbine.util.Log;

/**
 *
 * HTML Rewriter for transformer service
 *
 * @author <a href="mailto:mmari@ce.unipr.it">Marco Mari</a>
 * @version $Id: ClipperRewriter.java,v 1.2 2004/02/23 03:18:59 jford Exp $ 
 */

public class ClipperRewriter extends HTMLRewriter
{
	private String startElement;
	private String stopElement;
	private boolean foundStart = false;
	private boolean foundStop = false;
	private boolean nested = false;
	private int nestedNumber = 0;
	private int startElementNumber;
	private int foundElementNumber = 0;
	private String NOT_FOUND = "<br>Element not found, returning null<br>";
	private String INVALID_START = "<br>Error: received null start element<br>";
	private String INVALID_NUMBER =
		"<br>Error: received tagNumber negative or null<br>";

	/*
	 * Construct the Clipper Rewriter
	 *
	 */
	public ClipperRewriter()
	{
	}

	/*
	 * Rewriting HTML content between startElement and stopElement
	 *
	 * @param input    the HTML input stream.
	 * @param baseURL  the base URL of the target host.
	 * @return         the rewritten HTML output stream.
	 *
	 * @exception      MalformedURLException a servlet exception.
	 */

	public String rewrite(Reader input, String baseURL)
		throws MalformedURLException
	{
		this.baseURL = baseURL;
		String rewrittenHTML = "";
		foundStart = false;
		foundStop = false;
		nestedNumber = 0;
		foundElementNumber = 0;

		// Null startElement is invalid
		if (startElement == null)
		{
			return INVALID_START;
		}

		// StartElementNumber must be positive
		if (startElementNumber <= 0)
		{
			return INVALID_NUMBER;
		}

		nested = controlCoupled(startElement, stopElement);
		HTMLParserAdaptor parser = new SwingParserAdaptor(this);
		rewrittenHTML = parser.run(input);

		if (Log.getLogger().isDebugEnabled())
		{
			Log.debug("Clipper rewriter: start element:" + startElement);
			Log.debug("Clipper rewriter: stop element:" + stopElement);
			Log.debug("Clipper rewriter: foundStart:" + foundStart);
			Log.debug("Clipper rewriter: foundStop:" + foundStop);
			Log.debug("Clipper rewriter: nested:" + nested);
			Log.debug(
				"Clipper rewriter: foundElementNumber:" + foundElementNumber);
			Log.debug("Clipper rewriter: rewrittenHTML:" + rewrittenHTML);
		}

		if ((foundStart == false)
			|| ((foundStop == false) && (stopElement != null)))
			return NOT_FOUND;
		else
			return rewrittenHTML;
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
	 * Simple Tag Events
	 */
	public boolean enterSimpleTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
	{
		String attributes = attrsToString(attrs);
		String tagString = tag.toString();
		return checkTag(tagString, attributes, "simple");
	}

	/*
	 * Start Tag Events
	 */
	public boolean enterStartTagEvent(HTML.Tag tag, MutableAttributeSet attrs)
	{
		String attributes = attrsToString(attrs);
		String tagString = tag.toString();
		return checkTag(tagString, attributes, "start");
	}

	/*
	 * Exit Tag Events
	 */
	public boolean enterEndTagEvent(HTML.Tag tag)
	{
		String tagString = tag.toString();
		return checkTag(tagString, null, "end");
	}

	/*
	 * Text Event
	 */
	public boolean enterText(char[] values, int param)
	{
		if ((foundStart == true)
			&& (foundStop == false)
			&& (stopElement != null))
			return true;
		else
			return false;
	}

	/**
	 * Set the start element
	 *
	 * @param startElement    the new start element
	 */
	public void setStartElement(String startElement)
	{
		this.startElement = startElement;
	}

	/**
	 * Set the stop element
	 *
	 * @param stopElement    the new stop element
	 */
	public void setStopElement(String stopElement)
	{
		this.stopElement = stopElement;
	}

	/**
	 * Set the start element number
	 *
	 * @param startElementNumber    the new start element number
	 */
	public void setStartElementNumber(int startElementNumber)
	{
		this.startElementNumber = startElementNumber;
	}

	/*
	 * Control if searched tags are coupled.
	 * If searched tags are coupled, we must consider nested tags, example:
	 * Searched: <table>   and   </table>
	 * in the page there is:
	 * <table>...
	 *     <table>...
	 *     </table>
	 * </table>
	 * We are searching for the first and fourth tag!
	 */
	private boolean controlCoupled(String start, String stop)
	{
		StringTokenizer startTok = new StringTokenizer(start);
		boolean foundCoupled = false;
		String token;

		if (stop == null)
			return false;

		while (startTok.hasMoreTokens())
		{
			token = startTok.nextToken();

			if (token.equals(stop) == true)
				return true;
		}

		return false;
	}

	/*
	 * Convert the attributes set to a string
	 */
	private String attrsToString(MutableAttributeSet attrs)
	{
		String element = "";

		if (attrs != null)
		{
			Enumeration en = attrs.getAttributeNames();

			while (en.hasMoreElements())
			{
				Object attr = en.nextElement();
				element =
					element
						+ " "
						+ attr.toString()
						+ "="
						+ attrs.getAttribute(attr).toString();
			}

			return element;
		}

		return null;
	}

	/*
	 * Control to include or exclude the tag
	 */
	private boolean checkTag(String tag, String attrs, String position)
	{
		if (foundStart == false)
		{
			// Searching for start element
			if ((compareTag(tag, attrs, startElement) == true)
				&& ((position.equals("end") == false) || (stopElement == null)))
			{
				foundElementNumber = foundElementNumber + 1;

				if (foundElementNumber == startElementNumber)
				{
					foundStart = true;

					if (nested == true)
						nestedNumber = nestedNumber + 1;

					return true;
				}
				else
					return false;
			}
			else
			{
				// It's not start element
				return false;
			}
			// Searching for stop element
		}
		else if ((foundStop == false) && (stopElement != null))
		{
			if (compareTag(tag, attrs, stopElement))
			{
				if (nested == true)
					if (position.equals("start"))
						nestedNumber = nestedNumber + 1;
					else if (position.equals("end"))
						nestedNumber = nestedNumber - 1;

				if ((nestedNumber == 0) && (position.equals("start") == false))
					foundStop = true;

				return true;
			}
			else
			{
				// It's not stop element
				return true;
			}
		}
		else
			// Stop already found, don't include this tag
			return false;
	}

	/*
	 * Control if the current tag is the searched tag with the right attributes
	 */
	private boolean compareTag(String tag, String attrs, String base)
	{
		StringTokenizer baseTok = new StringTokenizer(base);
		String token;
		boolean foundTag = false;

		while (baseTok.hasMoreTokens())
		{
			token = baseTok.nextToken();

			// Exact match for the tag, for the attrs it's simpler to control the index
			if (token.equals(tag))
				foundTag = true;
			else if (attrs == null)
				return false;
			else if (attrs.indexOf(token) == -1)
				return false;
		}

		if (foundTag == false)
			return false;
		else
		{
			if (Log.getLogger().isDebugEnabled())
				Log.debug(
					"Clipper rewriter: match between tag "
						+ tag
						+ ", attrs "
						+ attrs
						+ ", and searched: "
						+ base);

			return true;
		}

	}

}
