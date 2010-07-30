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

package org.apache.jetspeed.portal.portlets;

//Element Construction Set
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.ecs.ConcreteElement;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.Transformer;
import org.apache.jetspeed.util.Base64;
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.RunData;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * A class that clips parts of one or more web pages.
 *
 * @author <a href="mailto:mmari@ce.unipr.it">Marco Mari</a>
 * @version $Id: WebClippingPortlet.java,v 1.2 2004/02/23 04:03:34 jford Exp $ 
 */

public class WebClippingPortlet extends AbstractInstancePortlet
{

	/**
	 * Static initialization of the logger for this class
	 */
	private static final JetspeedLogger logger =
		JetspeedLogFactoryService.getLogger(WebClippingPortlet.class.getName());

	// Define parameter name for the first tag to clip
	public static final String START = "startTag";
	// Define parameter name for the last tag to clip
	public static final String STOP = "stopTag";
	// Define parameter name for a single tag to clip
	public static final String TAG = "Tag";
	// Define parameter name for the number of the tag to clip
	public static final String TAGNUM = "startTagNumber";
	// Define parameter name for the URL of the page
	public static final String URL = "url";
	// Error message for startTag without stopTag
	private String BAD_PARAM = "<br>Error: startTag without stopTag<br>";
	// Error message for wrong startTagNumber parameter
	private String BAD_NUMBER = "<br>Error: bad integer parameter<br>";

	protected boolean initDone = false;
	protected boolean contentStale = true;
	protected boolean cacheContent = false;
	protected String username = null;
	protected String password = null;

	private Hashtable patterns = null;

	/**
	 * Initialize this portlet
	 * @throws PortletException Initialization failed
	 */
	public void init()
	{
		if (initDone)
			return;

		patterns = new Hashtable();

		try
		{
			loadParams();
		}
		catch (Exception e)
		{
			logger.info("Exception occurred:" + e.toString());
			e.printStackTrace();
		}

		contentStale = true;
		initDone = true;
	}

	/**
	 * took this from FileServerPortlet as it was private 
	 *
	*/

	// FIXME: Currently only the expiration the HTTP Response header is honored. 
	//        Expiration information in <meta> tags are not honored 

	protected Reader getReader(String url) throws IOException
	{
		URL pageUrl = new URL(url);

		URLConnection pageConn = pageUrl.openConnection();
		try
		{
			// set HTTP Basic Authetication header if username and password are set
			if (username != null && password != null)
			{
				pageConn.setRequestProperty(
					"Authorization",
					"Basic "
						+ Base64.encodeAsString(username + ":" + password));
			}

		}
		catch (Exception e)
		{
			logger.info("Exception occurred:" + e.toString());
			e.printStackTrace();
		}

		long pageExpiration = pageConn.getExpiration();
		String encoding = "iso-8859-1";
		String contentType = pageConn.getContentType();
		String tempString = null;
		String noCache = "no-cache";

		if (contentType != null)
		{
			StringTokenizer st = new StringTokenizer(contentType, "; =");
			while (st.hasMoreTokens())
			{
				if (st.nextToken().equalsIgnoreCase("charset"))
				{
					try
					{
						encoding = st.nextToken();
						break;
					}
					catch (Exception e)
					{
						break;
					}
				}
			}
		}

		/*
		 * Determing if content should be cached.
		 */
		cacheContent = true; // Assume content is cached
		if (pageExpiration == 0)
		{
			cacheContent = false;
		}
		// Check header field CacheControl
		tempString = pageConn.getHeaderField("Cache-Control");
		if (tempString != null)
		{
			if (tempString.toLowerCase().indexOf(noCache) >= 0)
			{
				cacheContent = false;
			}
		}
		// Check header field Pragma
		tempString = pageConn.getHeaderField("Pragma");
		if (tempString != null)
		{
			if (tempString.toLowerCase().indexOf(noCache) >= 0)
			{
				cacheContent = false;
			}
		}

		// Assign a reader
		Reader rdr = new InputStreamReader(pageConn.getInputStream(), encoding);

		// Only set the page expiration it the page has not expired
		if (pageExpiration > System.currentTimeMillis()
			&& (cacheContent == true))
		{
			contentStale = false;
			logger.debug(
				"WebPagePortlet caching URL: "
					+ url
					+ " Expiration: "
					+ pageExpiration
					+ ", "
					+ (pageExpiration - System.currentTimeMillis())
					+ " milliseconds into the future");
			setExpirationMillis(pageExpiration);
		}
		else
		{
			contentStale = true;
		}

		return rdr;
	}

	/**
	This methods outputs the content of the portlet for a given 
	request.
	
	@param data the RunData object for the request
	@return the content to be displayed to the user-agent
	*/
	public ConcreteElement getContent(RunData data)
	{
		PortletConfig config = this.getPortletConfig();

		if (contentStale == true)
			return getWebClippedContent(data, config);

		if (null == getExpirationMillis())
			return getContent(data, null, true);

		if (getExpirationMillis().longValue() <= System.currentTimeMillis())
			return getWebClippedContent(data, config);

		return getContent(data, null, true);
	}

	/*
	 * This method returns the clipped part of the Web page
	 */
	private ConcreteElement getWebClippedContent(
		RunData data,
		PortletConfig config)
	{
		String clippedString = ""; // HTML to visualize
		JetspeedClearElement element = null;
		int patternNumber = 1;
		int tagNumber = 0;
		Reader htmlReader;
		String defaultUrl = selectUrl(data, config);

		try
		{
			// Re-load parameters to see immediately the effect of changes
			loadParams();
			Enumeration en = patterns.keys();

			while (en.hasMoreElements())
			{
				String name = (String) en.nextElement();

				// Search for parameters in the right order
				if (name.equals(START + String.valueOf(patternNumber))
					|| name.equals(TAG + String.valueOf(patternNumber)))
				{
					String start =
						(String) patterns.get(
							START + String.valueOf(patternNumber));
					String simpleTag =
						(String) patterns.get(
							TAG + String.valueOf(patternNumber));
					String stop =
						(String) patterns.get(
							STOP + String.valueOf(patternNumber));
					String tagNum =
						(String) patterns.get(
							TAGNUM + String.valueOf(patternNumber));
					// A group of params can have a specific url
					String url =
						(String) patterns.get(
							URL + String.valueOf(patternNumber));
					url = controlUrl(url, defaultUrl);
					htmlReader = getReader(url);

					if ((start != null) && (stop == null))
					{
						element = new JetspeedClearElement(BAD_PARAM);
						return element;
					}

					if (tagNum != null)
					{
						try
						{
							tagNumber = Integer.parseInt(tagNum);
						}
						catch (NumberFormatException e)
						{
							logger.info("Exception occurred:" + e.toString());
							e.printStackTrace();
							element = new JetspeedClearElement(BAD_NUMBER);
							return element;
						}
					}

					if ((simpleTag != null) && (tagNum == null))
						clippedString =
							clippedString
								+ Transformer.findElement(
									htmlReader,
									url,
									simpleTag);
					else if ((simpleTag != null) && (tagNum != null))
						clippedString =
							clippedString
								+ Transformer.findElementNumber(
									htmlReader,
									url,
									simpleTag,
									tagNumber);
					else if (tagNum == null)
						clippedString =
							clippedString
								+ Transformer.clipElements(
									htmlReader,
									url,
									start,
									stop);
					else if (tagNum != null)
						clippedString =
							clippedString
								+ Transformer.clipElementsNumber(
									htmlReader,
									url,
									start,
									stop,
									tagNumber);

					patternNumber = patternNumber + 1;
					//Restart Enumeration, because params could not be in the right order
					en = patterns.keys();
					htmlReader.close();
				}
			}

			element = new JetspeedClearElement(clippedString);

			//FIXME: We should do a clearContent() for the media type, not ALL media types
			this.clearContent();
			// doing this because setContent() is not overwriting current content.
			this.setContent(element);

		}
		catch (Exception e)
		{
			logger.info("Exception occurred:" + e.toString());
			e.printStackTrace();
		}

		return element;
	}

	/**
	 * Usually called by caching system when portlet is marked as expired, but
	 * has not be idle longer then TimeToLive.
	 *
	 * Any cached content that is expired need to be refreshed.
	 */
	public void refresh()
	{
		if (cacheContent == true)
		{
			getWebClippedContent(null, this.getPortletConfig());
		}
	}

	/**
	 * Select the URL to use for this portlet.
	 * @return The URL to use for this portlet
	 */
	protected String selectUrl(RunData data, PortletConfig config)
	{
		String url = config.getURL();
		return url;
	}

	/*
	 * Choose between a specific url and the default url
	 */
	private String controlUrl(String url, String defaultUrl)
	{
		if (url == null)
		{
			return defaultUrl;
		}

		//if the given URL doesn not include a protocol... ie http:// or ftp://
		//then resolve it relative to the current URL context
		if (url.indexOf("://") < 0)
		{
			url = TurbineServlet.getResource(url).toString();
		}

		return url;
	}

	/*
	 * Load portlet parameters
	 */
	private void loadParams() throws PortletException
	{
		Iterator en = this.getPortletConfig().getInitParameterNames();

		try
		{
			while (en.hasNext())
			{
				String name = (String) en.next();

				if (name.equals("username"))
					username =
						this.getPortletConfig().getInitParameter("username");
				else if (name.equals("password"))
					password =
						this.getPortletConfig().getInitParameter("password");
				else
					patterns.put(
						name,
						this.getPortletConfig().getInitParameter(name));

			}
		}
		catch (Exception e)
		{
			logger.info("Exception occurred:" + e.toString());
			e.printStackTrace();
			throw new PortletException(e.toString());
		}
	}

}
